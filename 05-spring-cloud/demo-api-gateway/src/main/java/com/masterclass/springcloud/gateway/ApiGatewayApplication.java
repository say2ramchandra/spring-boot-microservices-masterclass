package com.masterclass.springcloud.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * API Gateway Application
 * 
 * Central entry point for all microservices.
 * Provides routing, load balancing, and cross-cutting concerns.
 * 
 * Features:
 * - Dynamic routing based on service discovery
 * - Load balancing across service instances
 * - Circuit breaker integration
 * - Request/Response filtering
 * - Rate limiting
 * 
 * Access Gateway: http://localhost:8080
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
        System.out.println("\n===========================================");
        System.out.println("🚀 API Gateway Started Successfully!");
        System.out.println("📍 Gateway: http://localhost:8080");
        System.out.println("📦 Product Service: http://localhost:8080/api/products");
        System.out.println("📋 Order Service: http://localhost:8080/api/orders");
        System.out.println("===========================================\n");
    }
}
