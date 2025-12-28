package com.example.resilience;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ResilienceDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(ResilienceDemoApplication.class, args);
        
        System.out.println("\n" +
            "=".repeat(50) + "\n" +
            "🛡️  Resilience4j Demo Started!\n" +
            "=".repeat(50) + "\n" +
            "📊 Actuator: http://localhost:8095/actuator\n" +
            "🔍 Health: http://localhost:8095/actuator/health\n" +
            "⚡ Circuit Breakers: http://localhost:8095/actuator/circuitbreakers\n" +
            "=".repeat(50) + "\n");
    }
}
