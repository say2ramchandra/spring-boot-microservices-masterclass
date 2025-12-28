package com.masterclass;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Data JPA Demo Application
 * 
 * Demonstrates:
 * - Entity relationships (OneToMany, ManyToOne, ManyToMany)
 * - Repository methods and custom queries
 * - Transaction management
 * - CRUD operations with JPA
 */
@SpringBootApplication
public class DataJpaDemoApp {

    public static void main(String[] args) {
        SpringApplication.run(DataJpaDemoApp.class, args);
        
        System.out.println("\n==============================================");
        System.out.println("🚀 Spring Data JPA Demo Application Started!");
        System.out.println("==============================================");
        System.out.println("📊 H2 Console: http://localhost:8080/h2-console");
        System.out.println("🔗 JDBC URL: jdbc:h2:mem:testdb");
        System.out.println("👤 Username: sa");
        System.out.println("🔑 Password: (leave empty)");
        System.out.println("==============================================\n");
    }
}
