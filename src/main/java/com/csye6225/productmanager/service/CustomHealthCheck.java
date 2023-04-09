package com.csye6225.productmanager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

@Component
public class CustomHealthCheck implements HealthIndicator {

    @Autowired
    private DataSource dataSource;

    @Override
    public Health health() {
        try {
            // check if a connection can be obtained from the datasource
            Connection connection = dataSource.getConnection();
            connection.close();
            return Health.up().build(); // application is healthy
        } catch (Exception ex) {
            return Health.down().withDetail("Error", ex.getMessage()).build(); // application is not healthy
        }
    }
}

