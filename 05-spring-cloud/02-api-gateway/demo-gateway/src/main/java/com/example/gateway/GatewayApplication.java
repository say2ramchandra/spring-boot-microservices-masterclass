package com.example.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Spring Cloud Gateway Application
 * 
 * This gateway provides:
 * - Dynamic routing to microservices
 * - Load balancing via Eureka
 * - Circuit breaker patterns
 * - Request/Response filtering
 * - Rate limiting
 * - JWT authentication
 * - CORS configuration
 * 
 * @author Spring Boot Microservices Masterclass
 */
@SpringBootApplication
@EnableDiscoveryClient
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🚀 API Gateway Started Successfully!");
        System.out.println("=".repeat(60));
        System.out.println("📍 Gateway URL: http://localhost:8080");
        System.out.println("📚 Actuator: http://localhost:8080/actuator");
        System.out.println("🔍 Routes: http://localhost:8080/actuator/gateway/routes");
        System.out.println("=".repeat(60) + "\n");
    }
}
