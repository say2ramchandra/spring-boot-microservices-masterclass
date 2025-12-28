package com.masterclass.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

/**
 * Logging Demo Service
 * Demonstrates logging in service layer
 */
@Service
@Slf4j
public class LoggingDemoService {

    public void processUser(String name, Integer age) {
        log.debug("Processing user in service layer: {}", name);
        
        // Simulate business logic
        if (age < 18) {
            log.warn("User {} is underage: {}", name, age);
        } else {
            log.info("Processing adult user: {}", name);
        }
        
        log.debug("User processing completed");
    }

    public void riskyOperation() {
        log.debug("Executing risky operation");
        
        try {
            // Simulate some work
            Thread.sleep(100);
            log.debug("Risky operation completed successfully");
            
        } catch (InterruptedException e) {
            log.error("Risky operation interrupted", e);
            Thread.currentThread().interrupt();
        }
    }

    public void performOperationWithMdc() {
        log.info("Service layer operation - MDC context is preserved");
        log.debug("Performing some business logic");
        
        // Call another method
        helperMethod();
        
        log.info("Operation completed");
    }

    private void helperMethod() {
        log.debug("Helper method called - MDC still available");
    }

    public String createOrder(Map<String, Object> orderData) {
        String orderId = UUID.randomUUID().toString();
        
        log.info("Creating order with ID: {}", orderId);
        log.debug("Order data: {}", orderData);
        
        // Simulate validation
        if (orderData.isEmpty()) {
            log.warn("Empty order data received");
        }
        
        // Simulate processing
        try {
            Thread.sleep(50);
            log.info("Order {} processed successfully", orderId);
        } catch (InterruptedException e) {
            log.error("Order processing interrupted for order: {}", orderId, e);
            Thread.currentThread().interrupt();
        }
        
        return orderId;
    }

    public void performOperation() {
        log.info("Performing standard operation");
        log.debug("Operation details...");
        
        // Simulate work
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            log.error("Operation interrupted", e);
            Thread.currentThread().interrupt();
        }
        
        log.info("Operation completed");
    }

    public void slowOperation() {
        log.debug("Starting slow operation");
        
        try {
            // Simulate slow operation
            Thread.sleep(500);
            log.debug("Slow operation in progress...");
            Thread.sleep(500);
            
        } catch (InterruptedException e) {
            log.error("Slow operation interrupted", e);
            Thread.currentThread().interrupt();
        }
        
        log.debug("Slow operation completed");
    }
}
