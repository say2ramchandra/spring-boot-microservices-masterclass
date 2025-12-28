package com.example.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Order Service using OpenFeign
 * 
 * This service demonstrates how to use OpenFeign to make declarative REST calls
 * to other microservices (Product Service in this case).
 * 
 * Key Features:
 * - Declarative REST clients with @FeignClient
 * - Integration with Eureka for service discovery
 * - Circuit breaker pattern with fallbacks
 * - Load balancing across service instances
 * - Error handling and retry logic
 * 
 * Demonstrates:
 * 1. Simple GET requests with path variables
 * 2. POST requests with request body
 * 3. Query parameters and headers
 * 4. Fallback methods for resilience
 * 5. Custom error handling
 */
@SpringBootApplication
@EnableFeignClients  // Enable Feign client scanning
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
        
        System.out.println("\n" + "=".repeat(70));
        System.out.println("  Order Service Started Successfully!");
        System.out.println("=".repeat(70));
        System.out.println("  Application: http://localhost:8083");
        System.out.println("  API Docs: http://localhost:8083/api/orders");
        System.out.println("  Health: http://localhost:8083/actuator/health");
        System.out.println("  ");
        System.out.println("  Using Feign to call Product Service on port 8082");
        System.out.println("=".repeat(70) + "\n");
    }
}
