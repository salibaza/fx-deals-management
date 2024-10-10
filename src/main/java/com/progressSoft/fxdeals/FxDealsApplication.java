package com.progressSoft.fxdeals;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "com.progressSoft.fxdeals.model")  // Specify the package where your entity is located
public class FxDealsApplication {

    public static void main(String[] args) {
        // Run the Spring Boot application
        SpringApplication.run(FxDealsApplication.class, args);
    }
}