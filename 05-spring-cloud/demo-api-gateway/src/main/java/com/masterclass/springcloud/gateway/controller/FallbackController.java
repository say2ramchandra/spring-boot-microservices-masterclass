package com.masterclass.springcloud.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Fallback Controller
 * 
 * Provides fallback responses when downstream services are unavailable.
 */
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/product")
    public ResponseEntity<Map<String, String>> productFallback() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Product Service is temporarily unavailable");
        response.put("status", "SERVICE_UNAVAILABLE");
        response.put("recommendation", "Please try again later");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    @GetMapping("/order")
    public ResponseEntity<Map<String, String>> orderFallback() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Order Service is temporarily unavailable");
        response.put("status", "SERVICE_UNAVAILABLE");
        response.put("recommendation", "Please try again later");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
}
