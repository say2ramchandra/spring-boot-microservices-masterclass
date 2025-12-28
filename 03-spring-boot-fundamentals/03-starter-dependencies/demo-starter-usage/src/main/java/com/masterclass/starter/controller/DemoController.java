package com.masterclass.starter.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Demo REST controller to show spring-boot-starter-web in action.
 * 
 * This controller is automatically discovered and configured by Spring MVC
 * thanks to the spring-boot-starter-web dependency.
 */
@RestController
@RequestMapping("/api/demo")
public class DemoController {

    /**
     * Simple endpoint returning JSON response.
     * Jackson (from spring-boot-starter-web) automatically serializes to JSON.
     */
    @GetMapping
    public Map<String, Object> demo() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Spring Boot Starter Demo");
        response.put("timestamp", LocalDateTime.now());
        response.put("starters", new String[]{
            "spring-boot-starter-web",
            "spring-boot-starter-data-jpa",
            "spring-boot-starter-validation",
            "spring-boot-starter-actuator"
        });
        response.put("features", new String[]{
            "RESTful API (Spring MVC)",
            "JSON serialization (Jackson)",
            "JPA data access (Hibernate)",
            "Bean validation",
            "Production monitoring (Actuator)"
        });
        return response;
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "UP");
        status.put("timestamp", LocalDateTime.now().toString());
        return status;
    }
}
