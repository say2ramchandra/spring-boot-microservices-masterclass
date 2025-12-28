package com.ecommerce.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Product Service - Manages product catalog
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ProductServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
        
        System.out.println("\n" +
            "==============================================\n" +
            "📦 Product Service Started!\n" +
            "==============================================\n" +
            "🌐 Service URL: http://localhost:8082\n" +
            "📊 H2 Console: http://localhost:8082/h2-console\n" +
            "📡 Registered with Eureka\n" +
            "==============================================\n");
    }
}
