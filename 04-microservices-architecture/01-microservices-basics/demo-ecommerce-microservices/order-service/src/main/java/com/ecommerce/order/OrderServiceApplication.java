package com.ecommerce.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * Order Service - Processes orders and communicates with User and Product services
 */
@SpringBootApplication
@EnableDiscoveryClient
public class OrderServiceApplication {

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
        
        System.out.println("\n" +
            "==============================================\n" +
            "🛒 Order Service Started!\n" +
            "==============================================\n" +
            "🌐 Service URL: http://localhost:8083\n" +
            "📊 H2 Console: http://localhost:8083/h2-console\n" +
            "📡 Registered with Eureka\n" +
            "🔗 Communicates with:\n" +
            "   • USER-SERVICE\n" +
            "   • PRODUCT-SERVICE\n" +
            "==============================================\n");
    }
}
