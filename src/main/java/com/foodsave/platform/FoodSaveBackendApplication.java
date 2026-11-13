package com.foodsave.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FoodSaveBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(FoodSaveBackendApplication.class, args);
    }
}
