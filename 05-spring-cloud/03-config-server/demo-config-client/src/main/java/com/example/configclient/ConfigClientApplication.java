package com.example.configclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * Spring Cloud Config Client Demo
 * 
 * This application fetches configuration from Config Server on startup.
 * Configuration can be refreshed dynamically without restart using @RefreshScope.
 * 
 * Key Features:
 * - Fetches configuration from Config Server
 * - Environment-specific configurations
 * - Dynamic refresh with /actuator/refresh
 * - Type-safe configuration properties
 * 
 * Configuration Files (in Config Server Git repo):
 * - application.yml (shared)
 * - product-service.yml (this service - all environments)
 * - product-service-dev.yml (development overrides)
 * - product-service-prod.yml (production overrides)
 * 
 * Endpoints:
 * - GET /api/config - View current configuration
 * - POST /actuator/refresh - Refresh configuration
 */
@SpringBootApplication
public class ConfigClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigClientApplication.class, args);
        
        System.out.println("\n" + "=".repeat(70));
        System.out.println("  Config Client Started Successfully!");
        System.out.println("=".repeat(70));
        System.out.println("  Application: http://localhost:8081");
        System.out.println("  Config Endpoint: http://localhost:8081/api/config");
        System.out.println("  Refresh: POST http://localhost:8081/actuator/refresh");
        System.out.println("  Health: http://localhost:8081/actuator/health");
        System.out.println("=".repeat(70) + "\n");
    }
}
