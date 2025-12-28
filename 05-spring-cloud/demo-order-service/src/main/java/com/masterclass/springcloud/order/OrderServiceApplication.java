package com.masterclass.springcloud.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Order Service Application
 * 
 * Demonstrates inter-service communication using Feign Client.
 * 
 * Features:
 * - Service registration with Eureka
 * - Feign declarative REST client
 * - Circuit breaker integration
 * - Load balancing through Eureka
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
        System.out.println("\n===========================================");
        System.out.println("🚀 Order Service Started!");
        System.out.println("📍 Port: 8083");
        System.out.println("📋 Orders: http://localhost:8083/api/orders");
        System.out.println("🔗 Using Feign Client to call Product Service");
        System.out.println("===========================================\n");
    }
}
