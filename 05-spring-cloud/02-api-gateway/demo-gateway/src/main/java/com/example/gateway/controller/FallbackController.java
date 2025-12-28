package com.example.gateway.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Fallback Controller
 * 
 * Provides fallback responses when downstream services are unavailable.
 * Triggered by Circuit Breaker.
 */
@Slf4j
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/product")
    public ResponseEntity<Map<String, Object>> productFallback() {
        log.warn("⚠️ Product Service fallback triggered");
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Product Service is temporarily unavailable");
        response.put("status", "SERVICE_UNAVAILABLE");
        response.put("timestamp", LocalDateTime.now());
        response.put("recommendation", "Please try again in a few moments");
        response.put("supportContact", "support@example.com");
        
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(response);
    }

    @GetMapping("/order")
    public ResponseEntity<Map<String, Object>> orderFallback() {
        log.warn("⚠️ Order Service fallback triggered");
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Order Service is temporarily unavailable");
        response.put("status", "SERVICE_UNAVAILABLE");
        response.put("timestamp", LocalDateTime.now());
        response.put("recommendation", "Your order has been queued and will be processed shortly");
        response.put("supportContact", "support@example.com");
        
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(response);
    }

    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> userFallback() {
        log.warn("⚠️ User Service fallback triggered");
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "User Service is temporarily unavailable");
        response.put("status", "SERVICE_UNAVAILABLE");
        response.put("timestamp", LocalDateTime.now());
        response.put("recommendation", "Authentication services will be restored shortly");
        
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(response);
    }

    @GetMapping("/general")
    public ResponseEntity<Map<String, Object>> generalFallback() {
        log.warn("⚠️ General fallback triggered");
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Service temporarily unavailable");
        response.put("status", "SERVICE_UNAVAILABLE");
        response.put("timestamp", LocalDateTime.now());
        response.put("recommendation", "Please try again later");
        
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(response);
    }
}
