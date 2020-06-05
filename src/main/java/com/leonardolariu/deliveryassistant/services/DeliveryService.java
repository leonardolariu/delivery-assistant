package com.leonardolariu.deliveryassistant.services;

import com.leonardolariu.deliveryassistant.models.Delivery;
import com.leonardolariu.deliveryassistant.models.User;
import com.leonardolariu.deliveryassistant.payload.errors.ApiException;
import com.leonardolariu.deliveryassistant.payload.responses.DeliveryDTO;
import com.leonardolariu.deliveryassistant.repositories.DeliveryRepository;
import com.leonardolariu.deliveryassistant.repositories.UserRepository;
import com.leonardolariu.deliveryassistant.services.utils.CSVService;
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
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Optional;

import static com.leonardolariu.deliveryassistant.models.EDeliveryStatus.FILE_UPLOADED;
import static com.leonardolariu.deliveryassistant.models.EDeliveryStatus.NOT_STARTED;

@Service
public class DeliveryService {
    private UserRepository userRepository;

    private DeliveryRepository deliveryRepository;

    private CSVService csvService;

    private ApplicationContext context;

    @Value("${deliveryassistant.app.storagePath}")
    private String storagePath;

    @Autowired
    public DeliveryService(UserRepository userRepository, DeliveryRepository deliveryRepository, CSVService csvService, ApplicationContext applicationContext) {
        this.userRepository = userRepository;
        this.deliveryRepository = deliveryRepository;
        this.csvService = csvService;
        this.context = applicationContext;
    }



    public DeliveryDTO getDailyDeliveryData(UserDetails userDetails) {
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

        return deliveryMapper(dailyDelivery);
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

    private DeliveryDTO deliveryMapper(Delivery delivery) {
        return DeliveryDTO.builder()
                .status(delivery.getStatus())
                .estimatedDriversCount(delivery.getEstimatedDriversCount())
                .actualDriversCount(delivery.getActualDriversCount())
                .routes(new HashSet<>())
                .build();
    }

    private String buildPackagesFilePath(User user) {
        String usernameWithoutWhitespaces = user.getUsername().replaceAll("\\s+", "");
        Calendar today = new GregorianCalendar();
        String todayString = (new SimpleDateFormat("ddMMyyyy")).format(today.getTime());

        return todayString + "_" + usernameWithoutWhitespaces + "_" + "packages.csv";
    }
}
