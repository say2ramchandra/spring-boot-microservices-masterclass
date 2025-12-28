package com.masterclass;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for Product REST API.
 * 
 * @SpringBootApplication is a convenience annotation that combines:
 * - @Configuration: Tags the class as a source of bean definitions
 * - @EnableAutoConfiguration: Enable Spring Boot's auto-configuration
 * - @ComponentScan: Scan for components in this package and sub-packages
 * 
 * @author Spring Boot Microservices Masterclass
 */
@SpringBootApplication
public class ProductRestApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductRestApiApplication.class, args);
        
        System.out.println("\n=".repeat(80));
        System.out.println("✅ Product REST API is running!");
        System.out.println("=".repeat(80));
        System.out.println("📍 API Base URL: http://localhost:8080/api/products");
        System.out.println("📊 H2 Console: http://localhost:8080/h2-console");
        System.out.println("   JDBC URL: jdbc:h2:mem:productdb");
        System.out.println("   Username: sa");
        System.out.println("   Password: (empty)");
        System.out.println("=".repeat(80));
        System.out.println("\n🧪 Try these commands:");
        System.out.println("   curl http://localhost:8080/api/products");
        System.out.println("   curl http://localhost:8080/api/products/1");
        System.out.println("=".repeat(80) + "\n");
    }
}
