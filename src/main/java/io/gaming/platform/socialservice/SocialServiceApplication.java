package io.gaming.platform.socialservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Gaming Platform Social Service.
 * This service is responsible for managing social events and publishing them to Kafka.
 */
@SpringBootApplication
public class SocialServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SocialServiceApplication.class, args);
    }
} 