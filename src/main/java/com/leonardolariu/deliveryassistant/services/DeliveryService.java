package com.leonardolariu.deliveryassistant.services;

import com.leonardolariu.deliveryassistant.models.Delivery;
import com.leonardolariu.deliveryassistant.models.User;
import com.leonardolariu.deliveryassistant.payload.errors.ApiException;
import com.leonardolariu.deliveryassistant.payload.responses.DeliveryDTO;
import com.leonardolariu.deliveryassistant.payload.responses.Route;
import com.leonardolariu.deliveryassistant.repositories.DeliveryRepository;
import com.leonardolariu.deliveryassistant.repositories.UserRepository;
import com.leonardolariu.deliveryassistant.services.utils.*;
import com.leonardolariu.deliveryassistant.services.utils.Package;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.leonardolariu.deliveryassistant.models.EDeliveryStatus.*;

@Service
public class DeliveryService {
    private UserRepository userRepository;

    private DeliveryRepository deliveryRepository;

    private CSVService csvService;

    private ClusteringService clusteringService;

    private TSPService tspService;

    private ApplicationContext context;

    @Value("${deliveryassistant.app.storagePath}")
    private String storagePath;

    private final String routeFileHeader = "order,xCoordinate,yCoordinate,recipientPhoneNumber,additionalInfo\n";

    @Autowired
    public DeliveryService(UserRepository userRepository, DeliveryRepository deliveryRepository, CSVService csvService,
                           ClusteringService clusteringService, TSPService tspService,
                           ApplicationContext applicationContext) {
        this.userRepository = userRepository;
        this.deliveryRepository = deliveryRepository;
        this.csvService = csvService;
        this.clusteringService = clusteringService;
        this.tspService = tspService;
        this.context = applicationContext;
    }



    public DeliveryDTO getDailyDeliveryData(UserDetails userDetails) throws IOException, ApiException {
        User user = getUser(userDetails);

        Delivery dailyDelivery;
        Optional<Delivery> optionalDailyDelivery = user.getDailyDelivery();
        if (optionalDailyDelivery.isPresent()) {
            dailyDelivery = optionalDailyDelivery.get();
        } else {
            dailyDelivery = new Delivery(NOT_STARTED, user);
            deliveryRepository.save(dailyDelivery);

            user.addDelivery(dailyDelivery);
            userRepository.save(user);
        }

        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE, maxX = Double.MIN_VALUE, maxY = Double.MIN_VALUE;
        Set<Route> routes = new HashSet<>();
        if (ROUTES_PROCESSED.equals(dailyDelivery.getStatus())) {
            int actualDriversCount = dailyDelivery.getActualDriversCount();

            for(int i = 1; i <= actualDriversCount; ++i) {
                List<Package> packages = getPackages(buildRouteFilePath(user, i));
                packages.sort(Comparator.comparing(Package::getOrder));
                routes.add(new Route(packages));

                for (Package aPackage : packages) {
                    minX = Double.min(minX, aPackage.getXCoordinate());
                    minY = Double.min(minY, aPackage.getYCoordinate());
                    maxX = Double.max(maxX, aPackage.getXCoordinate());
                    maxY = Double.max(maxY, aPackage.getYCoordinate());
                }
            }
        }

        return deliveryMapper(
                dailyDelivery,
                new Centroid((minX + maxX) / 2, (minY + maxY) / 2),
                routes
        );
    }

    public void uploadCSV(UserDetails userDetails, MultipartFile file) throws ApiException, IOException {
        User user = getUser(userDetails);

        Delivery dailyDelivery;
        Optional<Delivery> optionalDailyDelivery = user.getDailyDelivery();
        if (optionalDailyDelivery.isPresent()) {
            dailyDelivery = optionalDailyDelivery.get();

            if (!NOT_STARTED.equals(dailyDelivery.getStatus())) {
                throw new ApiException(403, "Action forbidden in this delivery state.");
            }
        } else {
            throw new ApiException(403, "Action forbidden in this delivery state.");
        }


        Resource gcsFile = context.getResource(storagePath + buildPackagesFilePath(user));
        byte[] fileBytes = StreamUtils.copyToByteArray(file.getInputStream());
        try (OutputStream os = ((WritableResource) gcsFile).getOutputStream()) {
            os.write(fileBytes);
        }

        dailyDelivery.setStatus(FILE_UPLOADED);
        dailyDelivery.setPackagesCount(csvService.getPackagesCount(file));
        deliveryRepository.save(dailyDelivery);
    }

    public int estimateDriversCount(UserDetails userDetails) throws ApiException, IOException {
        User user = getUser(userDetails);

        Delivery dailyDelivery;
        Optional<Delivery> optionalDailyDelivery = user.getDailyDelivery();
        if (optionalDailyDelivery.isPresent()) {
            dailyDelivery = optionalDailyDelivery.get();

            if (NOT_STARTED.equals(dailyDelivery.getStatus()) || COMPLETED.equals(dailyDelivery.getStatus()) ||
                    dailyDelivery.getEstimatedDriversCount() != 0) {
                throw new ApiException(403, "Action forbidden in this delivery state.");
            }
        } else {
            throw new ApiException(403, "Action forbidden in this delivery state.");
        }

        List<Package> packages = getPackages(buildPackagesFilePath(user));
        int estimatedDriversCount = clusteringService.estimateDriversCount(packages, user.getDrivers().size());

        dailyDelivery.setEstimatedDriversCount(estimatedDriversCount);
        deliveryRepository.save(dailyDelivery);

        return estimatedDriversCount;
    }

    public void processRoutes(UserDetails userDetails, int driversCount) throws ApiException, IOException {
        User user = getUser(userDetails);

        Delivery dailyDelivery;
        Optional<Delivery> optionalDailyDelivery = user.getDailyDelivery();
        if (optionalDailyDelivery.isPresent()) {
            dailyDelivery = optionalDailyDelivery.get();

            if (!FILE_UPLOADED.equals(dailyDelivery.getStatus())) {
                throw new ApiException(403, "Action forbidden in this delivery state.");
            }
        } else {
            throw new ApiException(403, "Action forbidden in this delivery state.");
        }

        List<Package> packages = getPackages(buildPackagesFilePath(user));
        Map<Centroid, List<Package>> clusters =
                clusteringService.kMeansPlusPlus(packages, Integer.min(dailyDelivery.getPackagesCount(), driversCount));

        double minimumDistanceToCover = 0;
        int routeCounter = 0;
        for (List<Package> clusterPackages : clusters.values()) {
            StringBuilder routeFilecontent = new StringBuilder(routeFileHeader);

            clusterPackages = tspService.solve(clusterPackages);
            int clusterSize = clusterPackages.size();
            for (int i = 0; i < clusterSize; ++i) {
                if (i != clusterSize - 1) {
                    Package curr = clusterPackages.get(i);
                    Package next = clusterPackages.get(i+1);

                    minimumDistanceToCover += DistanceService.geoDistance(curr.getXCoordinate(), curr.getYCoordinate(),
                            next.getXCoordinate(), next.getYCoordinate());
                }

                routeFilecontent.append(clusterPackages.get(i).toString());
            }

            byte[] bytes = routeFilecontent.toString().getBytes();
            Resource gcsRouteFile = context.getResource(storagePath + buildRouteFilePath(user, ++routeCounter));
            try (OutputStream os = ((WritableResource) gcsRouteFile).getOutputStream()) {
                os.write(bytes);
            }
        }

        dailyDelivery.setStatus(ROUTES_PROCESSED);
        dailyDelivery.setActualDriversCount(driversCount);
        dailyDelivery.setMinimumDistanceToCover(minimumDistanceToCover);
        deliveryRepository.save(dailyDelivery);
    }

    public void resetDailyDelivery(UserDetails userDetails) {
        User user = getUser(userDetails);

        Optional<Delivery> optionalDailyDelivery = user.getDailyDelivery();
        if (optionalDailyDelivery.isPresent()) {
            Delivery dailyDelivery = optionalDailyDelivery.get();

            dailyDelivery.setStatus(NOT_STARTED);
            dailyDelivery.setEstimatedDriversCount(0);
            dailyDelivery.setActualDriversCount(0);
            dailyDelivery.setPackagesCount(0);
            dailyDelivery.setMinimumDistanceToCover(0);

            deliveryRepository.save(dailyDelivery);
        }
    }



    private User getUser(UserDetails userDetails) {
        String username = userDetails.getUsername();
        Optional<User> optionalUser = userRepository.findByUsername(username);
        return optionalUser.get();
    }

    private List<Package> getPackages(String filePath) throws ApiException {
        Resource gcsFile = context.getResource(storagePath + filePath);
        return csvService.mapResourceToPackageList(gcsFile);
    }

    private DeliveryDTO deliveryMapper(Delivery delivery, Centroid mapCenter, Set<Route> routes) {
        return DeliveryDTO.builder()
                .date(delivery.getDeliveryShortDateString())
                .status(delivery.getStatus())
                .estimatedDriversCount(delivery.getEstimatedDriversCount())
                .actualDriversCount(delivery.getActualDriversCount())
                .packagesCount(delivery.getPackagesCount())
                .minimumDistanceToCover(delivery.getMinimumDistanceToCover())
                .mapCenter(mapCenter)
                .routes(routes)
                .build();
    }

    private String buildPackagesFilePath(User user) {
        String usernameWithoutWhitespaces = user.getUsername().replaceAll("\\s+", "");
        Calendar today = new GregorianCalendar();
        String todayString = (new SimpleDateFormat("ddMMyyyy")).format(today.getTime());

        return todayString + "_" + usernameWithoutWhitespaces + "_" + "packages.csv";
    }

    private String buildRouteFilePath(User user, int i) {
        String usernameWithoutWhitespaces = user.getUsername().replaceAll("\\s+", "");
        Calendar today = new GregorianCalendar();
        String todayString = (new SimpleDateFormat("ddMMyyyy")).format(today.getTime());

        return todayString + "_" + usernameWithoutWhitespaces + "_" + "route" + i + ".csv";
    }
}
