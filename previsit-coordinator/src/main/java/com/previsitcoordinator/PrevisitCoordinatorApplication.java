package com.previsitcoordinator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Starts the PreVisit Coordinator backend.
 *
 * <p>{@code @SpringBootApplication} tells Spring Boot to configure the application
 * automatically and to search this package and its subpackages for components such
 * as controllers. Keeping this class in the root package lets it discover
 * {@link HealthController} without extra configuration.</p>
 */
@SpringBootApplication
public class PrevisitCoordinatorApplication {

    /**
     * The Java Virtual Machine calls this method when the application starts.
     * SpringApplication then creates the Spring application context and starts the
     * embedded web server.
     *
     * @param args command-line arguments passed to the program
     */
    public static void main(String[] args) {
        SpringApplication.run(PrevisitCoordinatorApplication.class, args);
    }
}
