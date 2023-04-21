package com.csye6225.productmanager;

import com.csye6225.productmanager.controller.HealthController;
import com.csye6225.productmanager.service.CustomHealthCheck;
import com.timgroup.statsd.StatsDClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class HealthControllerTest {

    private HealthController healthController;
    private CustomHealthCheck customHealthCheck;
    private StatsDClient statsDClient;

    @BeforeEach
    void setUp() {
        customHealthCheck = Mockito.mock(CustomHealthCheck.class);
        statsDClient = Mockito.mock(StatsDClient.class);
        healthController = new HealthController(customHealthCheck);
        healthController.setStatsDClient(statsDClient);
    }

    @Test
    void testHealthCheckSuccess() {
        Health health = new Health.Builder().status(Status.UP).build();
        Mockito.when(customHealthCheck.health()).thenReturn(health);
        ResponseEntity<String> responseEntity = healthController.healthCheck();
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertEquals("Pass Health Test!", responseEntity.getBody());
    }

    @Test
    void testHealthCheckFailure() {
        Health health = new Health.Builder().status(Status.DOWN).build();
        Mockito.when(customHealthCheck.health()).thenReturn(health);
        ResponseEntity<String> responseEntity = healthController.healthCheck();
        Assertions.assertEquals(HttpStatus.SERVICE_UNAVAILABLE, responseEntity.getStatusCode());
        Assertions.assertEquals("ERROR", responseEntity.getBody());
    }
}