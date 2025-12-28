package com.masterclass.springcloud.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Eureka Server Application
 * 
 * Service Discovery Server that allows microservices to register themselves
 * and discover other services dynamically.
 * 
 * Key Features:
 * - Service Registration
 * - Service Discovery
 * - Health Monitoring
 * - Load Balancing Information
 * 
 * Access Eureka Dashboard: http://localhost:8761
 */
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
        System.out.println("\n===========================================");
        System.out.println("🚀 Eureka Server Started Successfully!");
        System.out.println("📊 Dashboard: http://localhost:8761");
        System.out.println("===========================================\n");
    }
}
