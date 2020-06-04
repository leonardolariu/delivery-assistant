package com.leonardolariu.deliveryassistant.controllers;

import com.leonardolariu.deliveryassistant.payload.errors.ApiException;
import com.leonardolariu.deliveryassistant.payload.errors.BadRequestError;
import com.leonardolariu.deliveryassistant.payload.errors.NotFoundError;
import com.leonardolariu.deliveryassistant.payload.requests.AddDriverRequest;
import com.leonardolariu.deliveryassistant.payload.responses.DriverDTO;
import com.leonardolariu.deliveryassistant.payload.responses.DriversData;
import com.leonardolariu.deliveryassistant.payload.responses.MessageResponse;
import com.leonardolariu.deliveryassistant.services.DriverService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/drivers")
public class DriverController {
    private DriverService driverService;

    @Autowired
    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }



    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> addDriver(@Valid @RequestBody AddDriverRequest addDriverRequest, BindingResult result) {
        log.info("POST request to add driver: {}", addDriverRequest.getName());

        ResponseEntity<?> responseEntity = checkValidation(result);
        if (responseEntity != null) return responseEntity;

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            DriverDTO driverDTO = driverService.addDriver(userDetails, addDriverRequest);
            return ResponseEntity.ok(driverDTO);
        } catch (ApiException e) {
            switch (e.getStatus()) {
                default:
                    return ResponseEntity
                            .badRequest()
                            .body(new BadRequestError(e.getMessage()));
            }
        }
    }

    @DeleteMapping("/{driverId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> removeDriver(@PathVariable Long driverId) {
        log.info("DELETE request to remove driver with id: {}", driverId);

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            driverService.removeDriver(userDetails, driverId);
            return ResponseEntity.ok(new MessageResponse("Removed driver."));
        } catch (ApiException e) {
            switch (e.getStatus()) {
                case 404:
                    return ResponseEntity
                            .status(HttpStatus.NOT_FOUND)
                            .body(new NotFoundError(e.getMessage()));

                default:
                    return ResponseEntity
                            .badRequest()
                            .body(new BadRequestError(e.getMessage()));
            }
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getDriversData() {
        log.info("GET request to get drivers data");

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        DriversData driversData = driverService.getDriversData(userDetails);
        return ResponseEntity.ok(driversData);
    }



    private ResponseEntity<?> checkValidation(BindingResult result) {
        if (result.hasErrors()) {
            List<String> validationErrors = new ArrayList<>();
            result.getAllErrors().forEach(e -> {
                log.error(e.getDefaultMessage());
                validationErrors.add(e.getDefaultMessage());
            });

            if(!validationErrors.isEmpty()) {
                return ResponseEntity
                        .badRequest()
                        .body(new BadRequestError(validationErrors.get(0)));
            }
        }

        return null;
    }
}
