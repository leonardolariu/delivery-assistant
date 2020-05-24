package com.leonardolariu.deliveryassistant.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/name")
public class DummyController {

    @GetMapping("/{name}")
    public ResponseEntity<?> getName(@PathVariable String name) {
        log.info("Get request to display name: {}", name);
        return new ResponseEntity<>(name,  HttpStatus.OK);
    }

}
