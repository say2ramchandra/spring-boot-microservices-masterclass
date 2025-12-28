package com.ecommerce.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * API Gateway - Single Entry Point for all Microservices
 * 
 * Routes:
 * - /api/users/** -> USER-SERVICE
 * - /api/products/** -> PRODUCT-SERVICE
 * - /api/orders/** -> ORDER-SERVICE
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
        
        System.out.println("\n" +
            "==============================================\n" +
            "🚪 API Gateway Started!\n" +
            "==============================================\n" +
            "🌐 Gateway URL: http://localhost:8080\n" +
            "📍 Routes:\n" +
            "   • /api/users/** -> USER-SERVICE\n" +
            "   • /api/products/** -> PRODUCT-SERVICE\n" +
            "   • /api/orders/** -> ORDER-SERVICE\n" +
            "==============================================\n");
    }
}
