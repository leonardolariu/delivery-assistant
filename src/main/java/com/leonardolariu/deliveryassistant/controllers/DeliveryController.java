package com.leonardolariu.deliveryassistant.controllers;

import com.leonardolariu.deliveryassistant.payload.errors.ApiException;
import com.leonardolariu.deliveryassistant.payload.errors.BadRequestError;
import com.leonardolariu.deliveryassistant.payload.errors.NotFoundError;
import com.leonardolariu.deliveryassistant.payload.responses.DeliveryDTO;
import com.leonardolariu.deliveryassistant.payload.responses.MessageResponse;
import com.leonardolariu.deliveryassistant.services.DeliveryService;
import com.leonardolariu.deliveryassistant.services.utils.CSVService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
        DeliveryDTO deliveryDTO = deliveryService.getDailyDeliveryData(userDetails);
        return ResponseEntity.ok(deliveryDTO);
    }

    @PostMapping("/daily/reset")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> resetDailyDelivery() {
        log.info("POST request to reset daily delivery data");

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        deliveryService.resetDailyDelivery(userDetails);
        return ResponseEntity.ok(new MessageResponse("Daily Delivery reset."));
    }

    @PostMapping("/upload-csv")
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
                            .body(new NotFoundError(e.getMessage()));

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
}
