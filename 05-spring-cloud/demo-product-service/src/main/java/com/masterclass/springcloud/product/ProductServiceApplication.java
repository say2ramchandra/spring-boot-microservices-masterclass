package com.masterclass.springcloud.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Product Service Application
 * 
 * A microservice that manages product catalog and registers itself with Eureka.
 * 
 * Features Demonstrated:
 * - Service Registration with Eureka
 * - REST API endpoints
 * - Circuit Breaker pattern
 * - Feign Client for inter-service communication
 * - Health checks with Actuator
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class ProductServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
        System.out.println("\n===========================================");
        System.out.println("🚀 Product Service Started!");
        System.out.println("📍 Port: 8081");
        System.out.println("📊 Health: http://localhost:8081/actuator/health");
        System.out.println("📦 Products: http://localhost:8081/api/products");
        System.out.println("===========================================\n");
    }
}
