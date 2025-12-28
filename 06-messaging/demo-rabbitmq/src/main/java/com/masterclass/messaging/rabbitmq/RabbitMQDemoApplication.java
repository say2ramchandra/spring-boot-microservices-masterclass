package com.masterclass.messaging.rabbitmq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * RabbitMQ Demo Application
 * 
 * Demonstrates:
 * - Direct Exchange (one-to-one)
 * - Fanout Exchange (broadcast)
 * - Topic Exchange (pattern matching)
 * - Producer and Consumer patterns
 * 
 * Prerequisites: RabbitMQ Server running on localhost:5672
 * Management UI: http://localhost:15672 (guest/guest)
 */
@SpringBootApplication
public class RabbitMQDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(RabbitMQDemoApplication.class, args);
        System.out.println("\n===========================================");
        System.out.println("🐰 RabbitMQ Demo Started!");
        System.out.println("📍 Port: 8084");
        System.out.println("📮 Management UI: http://localhost:15672");
        System.out.println("📤 Send Message: POST http://localhost:8084/api/messages/send");
        System.out.println("===========================================\n");
    }
}
