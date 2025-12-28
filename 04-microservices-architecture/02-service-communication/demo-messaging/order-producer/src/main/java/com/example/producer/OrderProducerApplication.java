package com.example.producer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OrderProducerApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderProducerApplication.class, args);
        System.out.println("\n📤 Order Producer started on port 8093\n");
    }
}
