package com.leonardolariu.deliveryassistant.controllers;

import com.leonardolariu.deliveryassistant.payload.errors.ApiException;
import com.leonardolariu.deliveryassistant.payload.errors.BadRequestError;
import com.leonardolariu.deliveryassistant.payload.errors.ForbiddenError;
import com.leonardolariu.deliveryassistant.payload.responses.DeliveryDTO;
import com.leonardolariu.deliveryassistant.payload.responses.EstimationResponse;
import com.leonardolariu.deliveryassistant.payload.responses.MessageResponse;
import com.leonardolariu.deliveryassistant.services.CSVService;
import com.leonardolariu.deliveryassistant.services.DeliveryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/delivery")
public class DeliveryController {
    private DeliveryService deliveryService;

    private CSVService csvService;

    @Autowired
    public DeliveryController(DeliveryService deliveryService, CSVService csvService) {
        this.deliveryService = deliveryService;
        this.csvService = csvService;
    }



    @GetMapping("/daily")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getDailyDeliveryData() {
        log.info("GET request to get daily delivery data");

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            DeliveryDTO deliveryDTO = deliveryService.getDailyDeliveryData(userDetails);
            return ResponseEntity.ok(deliveryDTO);
        } catch (ApiException e) {
            switch (e.getStatus()) {
                case 403:
                    return ResponseEntity
                            .status(HttpStatus.FORBIDDEN)
                            .body(new ForbiddenError(e.getMessage()));

                default:
                    return ResponseEntity
                            .badRequest()
                            .body(new BadRequestError(e.getMessage()));
            }
        } catch (IOException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new BadRequestError("File could not be read."));
        }
    }

    @PostMapping("daily/upload-csv")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> uploadCSV(@RequestParam("file") MultipartFile file) {
        log.info("POST request to upload csv");

        try {
            csvService.mapToPackageList(file);
        } catch (ApiException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new BadRequestError(e.getMessage()));
        }

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            deliveryService.uploadCSV(userDetails, file);
            return ResponseEntity.ok(new MessageResponse("File uploaded successfully."));
        } catch (ApiException e) {
            switch (e.getStatus()) {
                case 403:
                    return ResponseEntity
                            .status(HttpStatus.FORBIDDEN)
                            .body(new ForbiddenError(e.getMessage()));

                default:
                    return ResponseEntity
                            .badRequest()
                            .body(new BadRequestError(e.getMessage()));
            }
        } catch (IOException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new BadRequestError("File could not be read."));
        }
    }

    @GetMapping("/daily/estimate-drivers-count")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getDailyDriversCountEstimation() {
        log.info("GET request to get daily driversCount estimation");

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            int estimatedDriversCount = deliveryService.estimateDriversCount(userDetails);
            return ResponseEntity.ok(new EstimationResponse(estimatedDriversCount));
        } catch (ApiException e) {
            switch (e.getStatus()) {
                case 403:
                    return ResponseEntity
                            .status(HttpStatus.FORBIDDEN)
                            .body(new ForbiddenError(e.getMessage()));

                default:
                    return ResponseEntity
                            .badRequest()
                            .body(new BadRequestError(e.getMessage()));
            }
        } catch (IOException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new BadRequestError("File could not be read."));
        }
    }

    @PostMapping("/daily/process-routes/{driversCount}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getDailyDriversCountEstimation(@PathVariable int driversCount) {
        log.info("POST request to process routes");

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            deliveryService.processRoutes(userDetails, driversCount);
            return ResponseEntity.ok(new MessageResponse("Routes processed successfully."));
        } catch (ApiException e) {
            switch (e.getStatus()) {
                case 403:
                    return ResponseEntity
                            .status(HttpStatus.FORBIDDEN)
                            .body(new ForbiddenError(e.getMessage()));

                default:
                    return ResponseEntity
                            .badRequest()
                            .body(new BadRequestError(e.getMessage()));
            }
        } catch (IOException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new BadRequestError("File could not be read."));
        }
    }

    @PostMapping("/daily/inform-drivers")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> informDriversViaEmail(@RequestBody List<String> emails) {
        log.info("POST request to inform drivers via Email");

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            deliveryService.informDriversViaEmail(userDetails, emails);
            return ResponseEntity.ok(new MessageResponse("Drivers informed successfully."));
        } catch (ApiException e) {
            switch (e.getStatus()) {
                case 403:
                    return ResponseEntity
                            .status(HttpStatus.FORBIDDEN)
                            .body(new ForbiddenError(e.getMessage()));

                default:
                    return ResponseEntity
                            .badRequest()
                            .body(new BadRequestError(e.getMessage()));
            }
        } catch (MessagingException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new BadRequestError("Email could not be sent."));
        }
    }

    @PostMapping("/daily/reset")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> resetDailyDelivery() {
        log.info("POST request to reset daily delivery data");

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            deliveryService.resetDailyDelivery(userDetails);
            return ResponseEntity.ok(new MessageResponse("Daily Delivery reset."));
        } catch (ApiException e) {
            switch (e.getStatus()) {
                case 403:
                    return ResponseEntity
                            .status(HttpStatus.FORBIDDEN)
                            .body(new ForbiddenError(e.getMessage()));

                default:
                    return ResponseEntity
                            .badRequest()
                            .body(new BadRequestError(e.getMessage()));
            }
        }
    }
}
