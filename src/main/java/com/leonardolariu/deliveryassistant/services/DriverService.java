package com.leonardolariu.deliveryassistant.services;

import com.leonardolariu.deliveryassistant.models.Driver;
import com.leonardolariu.deliveryassistant.models.User;
import com.leonardolariu.deliveryassistant.payload.errors.ApiException;
import com.leonardolariu.deliveryassistant.payload.requests.AddDriverRequest;
import com.leonardolariu.deliveryassistant.payload.responses.DriverDTO;
import com.leonardolariu.deliveryassistant.payload.responses.DriversData;
import com.leonardolariu.deliveryassistant.repositories.DriverRepository;
import com.leonardolariu.deliveryassistant.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DriverService {
    private UserRepository userRepository;

    private DriverRepository driverRepository;

    @Autowired
    public DriverService(UserRepository userRepository, DriverRepository driverRepository) {
        this.userRepository = userRepository;
        this.driverRepository = driverRepository;
    }



    public DriverDTO addDriver(UserDetails userDetails, AddDriverRequest addDriverRequest) throws ApiException {
        // TODO: check daily delivery status to be NOT_STARTED or COMPLETED

        User user = getUser(userDetails);
        Driver driver = new Driver(addDriverRequest.getName(), addDriverRequest.getEmail(), user);

        try {
            driverRepository.save(driver);
        } catch (Exception e) {
            throw new ApiException(400, "Email address already exists in the system.");
        }

        user.addDriver(driver);
        userRepository.save(user);

        return driverMapper(driver);
    }

    public void removeDriver(UserDetails userDetails, Long driverId) throws ApiException {
        // TODO: check daily delivery status to be NOT_STARTED or COMPLETED

        Optional<Driver> optionalDriver = driverRepository.findById(driverId);
        optionalDriver.ifPresent(driver -> removeDriverFromDB(getUser(userDetails), driver));
        optionalDriver.orElseThrow(() -> new ApiException(404, "Driver not found."));
    }

    public DriversData getDriversData(UserDetails userDetails) {
        User user = getUser(userDetails);

        Set<DriverDTO> drivers = user.getDrivers().stream()
                .map(this::driverMapper)
                .collect(Collectors.toSet());

        return DriversData.builder()
                .driversCount(drivers.size())
                .drivers(drivers)
                .build();
    }



    private User getUser(UserDetails userDetails) {
        String username = userDetails.getUsername();
        Optional<User> optionalUser = userRepository.findByUsername(username);
        return optionalUser.get();
    }

    private void removeDriverFromDB(User user, Driver driver) {
        user.removeDriver(driver);
        userRepository.save(user);

        driverRepository.delete(driver);
    }

    private DriverDTO driverMapper(Driver driver) {
        Calendar since = driver.getSince();
        String sinceString = (new SimpleDateFormat("dd MMM yyyy")).format(since.getTime());

        return DriverDTO.builder()
                .id(driver.getId())
                .name(driver.getName())
                .email(driver.getEmail())
                .since(sinceString)
                .build();
    }
}
