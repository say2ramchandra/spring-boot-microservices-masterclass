package com.masterclass;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Exception Handling Demo Application
 * 
 * Demonstrates:
 * - @ControllerAdvice for global exception handling
 * - @ExceptionHandler for specific exceptions
 * - Custom exception classes
 * - Validation error handling
 * - Proper HTTP status codes
 */
@SpringBootApplication
public class ExceptionHandlingDemoApp {

    public static void main(String[] args) {
        SpringApplication.run(ExceptionHandlingDemoApp.class, args);
        
        System.out.println("\n==============================================");
        System.out.println("🚀 Exception Handling Demo Started!");
        System.out.println("==============================================");
        System.out.println("📡 API Base URL: http://localhost:8080/api/users");
        System.out.println("\n🎯 Try these endpoints to see error handling:");
        System.out.println("  GET  /api/users/999      → 404 Not Found");
        System.out.println("  POST /api/users (empty)  → 400 Validation Error");
        System.out.println("  POST /api/users (dup)    → 409 Conflict");
        System.out.println("==============================================\n");
    }
}
