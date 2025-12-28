package com.example.configserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * Spring Cloud Config Server
 * 
 * Provides centralized external configuration management for microservices.
 * Configuration is stored in a Git repository and served via REST API.
 * 
 * Key Features:
 * - Git-backed configuration storage
 * - Environment-specific configurations (dev/test/prod)
 * - Encryption for sensitive values
 * - Version control via Git
 * - REST API for configuration access
 * 
 * Endpoints:
 * - /{application}/{profile}[/{label}]
 * - /{application}-{profile}.yml
 * - /{label}/{application}-{profile}.yml
 * 
 * Example:
 * GET http://localhost:8888/product-service/dev
 * Returns configuration for product-service in dev profile
 */
@SpringBootApplication
@EnableConfigServer  // Enables Spring Cloud Config Server
public class ConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
        
        System.out.println("\n" + "=".repeat(70));
        System.out.println("  Config Server Started Successfully!");
        System.out.println("=".repeat(70));
        System.out.println("  Dashboard: http://localhost:8888");
        System.out.println("  Example: http://localhost:8888/product-service/dev");
        System.out.println("  Health: http://localhost:8888/actuator/health");
        System.out.println("=".repeat(70) + "\n");
    }
}
