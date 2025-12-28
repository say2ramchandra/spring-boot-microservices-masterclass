package com.ecommerce.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Eureka Service Discovery Server
 * 
 * Dashboard: http://localhost:8761
 */
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
        
        System.out.println("\n" +
            "==============================================\n" +
            "🚀 Eureka Server Started!\n" +
            "==============================================\n" +
            "📊 Dashboard: http://localhost:8761\n" +
            "📡 Services will register here\n" +
            "==============================================\n");
    }
}
