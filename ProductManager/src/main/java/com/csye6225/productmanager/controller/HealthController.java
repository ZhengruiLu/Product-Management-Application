package com.csye6225.productmanager.controller;

import com.csye6225.productmanager.service.CustomHealthCheck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    private CustomHealthCheck customHealthCheck;

    @Autowired
    public HealthController(CustomHealthCheck customHealthCheck) {
        this.customHealthCheck = customHealthCheck;
    }

    @GetMapping("/healthz")
    public ResponseEntity<String> healthCheck() {
        Health health = customHealthCheck.health();
        if (health.getStatus().equals(Status.UP)) {
            return new ResponseEntity<>("OK", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}