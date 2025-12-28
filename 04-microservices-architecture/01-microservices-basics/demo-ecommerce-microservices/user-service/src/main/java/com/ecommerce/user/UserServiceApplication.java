package com.ecommerce.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * User Service - Manages user information
 */
@SpringBootApplication
@EnableDiscoveryClient
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
        
        System.out.println("\n" +
            "==============================================\n" +
            "👤 User Service Started!\n" +
            "==============================================\n" +
            "🌐 Service URL: http://localhost:8081\n" +
            "📊 H2 Console: http://localhost:8081/h2-console\n" +
            "📡 Registered with Eureka\n" +
            "==============================================\n");
    }
}
