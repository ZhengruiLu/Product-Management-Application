package com.csye6225.productmanager.controller;

import com.csye6225.productmanager.service.CustomHealthCheck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class HealthController {
    private final CustomHealthCheck customHealthCheck;
    private final Logger logger = (Logger) LoggerFactory.getLogger(HealthController.class);

    @Autowired
    public HealthController(CustomHealthCheck customHealthCheck) {
        this.customHealthCheck = customHealthCheck;
    }

    @GetMapping("/healthz")
    public ResponseEntity<String> healthCheck() {

        try {
            Health health = customHealthCheck.health();
            if (!health.getStatus().equals(Status.UP)) {
                logger.error("Error during health check");
                return new ResponseEntity<>("ERROR", HttpStatus.SERVICE_UNAVAILABLE);
            }
            logger.info("Health check successful");
            return new ResponseEntity<>("Hello", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error during health check: " + e);
            return new ResponseEntity<>("ERROR", HttpStatus.SERVICE_UNAVAILABLE);
        }
    }
}