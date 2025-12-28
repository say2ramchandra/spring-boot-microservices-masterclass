package com.example.configclient.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Application configuration properties
 * 
 * This class binds configuration properties from Config Server
 * with type safety and validation.
 * 
 * @RefreshScope enables dynamic refresh without restart.
 * @ConfigurationProperties binds properties with prefix "app".
 * 
 * Configuration Example (product-service-dev.yml):
 * 
 * app:
 *   message: "Product Service - Development"
 *   timeout: 5000
 *   max-retries: 3
 *   features:
 *     feature-x: true
 *     feature-y: false
 */
@Data
@Component
@RefreshScope  // Enable dynamic refresh
@ConfigurationProperties(prefix = "app")
public class AppConfig {

    /**
     * Application message
     * Example: "Product Service - Development Environment"
     */
    private String message;

    /**
     * Timeout in milliseconds
     * Example: 5000
     */
    private int timeout;

    /**
     * Maximum retry attempts
     * Example: 3
     */
    private int maxRetries;

    /**
     * Feature flags
     * Example:
     * features:
     *   feature-x-enabled: true
     *   feature-y-enabled: false
     */
    private Map<String, Object> features;

    /**
     * Environment name (from spring.profiles.active)
     */
    private String environment;
}
