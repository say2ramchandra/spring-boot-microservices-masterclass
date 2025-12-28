package com.example.configclient.controller;

import com.example.configclient.config.AppConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuration Controller
 * 
 * Exposes endpoints to view current configuration values.
 * Demonstrates both @Value and @ConfigurationProperties approaches.
 * 
 * @RefreshScope enables this bean to be refreshed when
 * POST /actuator/refresh is called.
 */
@RestController
@RequestMapping("/api/config")
@RefreshScope  // Enable refresh for this controller
@RequiredArgsConstructor
public class ConfigController {

    private final AppConfig appConfig;
    private final Environment environment;

    /**
     * Injected using @Value annotation
     * Updated when /actuator/refresh is called
     */
    @Value("${app.message:Default Message}")
    private String message;

    @Value("${app.timeout:5000}")
    private int timeout;

    @Value("${spring.application.name:unknown}")
    private String applicationName;

    /**
     * Get all configuration values
     * 
     * GET http://localhost:8081/api/config
     * 
     * Shows configuration from:
     * 1. @Value injections
     * 2. @ConfigurationProperties (AppConfig)
     * 3. Environment object
     */
    @GetMapping
    public Map<String, Object> getConfiguration() {
        Map<String, Object> config = new HashMap<>();

        // Configuration from @Value
        Map<String, Object> valueConfig = new HashMap<>();
        valueConfig.put("message", message);
        valueConfig.put("timeout", timeout);
        valueConfig.put("applicationName", applicationName);

        // Configuration from @ConfigurationProperties
        Map<String, Object> propsConfig = new HashMap<>();
        propsConfig.put("message", appConfig.getMessage());
        propsConfig.put("timeout", appConfig.getTimeout());
        propsConfig.put("maxRetries", appConfig.getMaxRetries());
        propsConfig.put("features", appConfig.getFeatures());
        propsConfig.put("environment", appConfig.getEnvironment());

        // Environment information
        Map<String, Object> envConfig = new HashMap<>();
        envConfig.put("activeProfiles", environment.getActiveProfiles());
        envConfig.put("defaultProfiles", environment.getDefaultProfiles());

        config.put("valueAnnotation", valueConfig);
        config.put("configurationProperties", propsConfig);
        config.put("environment", envConfig);
        config.put("timestamp", LocalDateTime.now());

        return config;
    }

    /**
     * Get message only
     * 
     * GET http://localhost:8081/api/config/message
     */
    @GetMapping("/message")
    public Map<String, String> getMessage() {
        return Map.of(
            "message", message,
            "source", "Config Server",
            "profile", String.join(",", environment.getActiveProfiles())
        );
    }

    /**
     * Get timeout value
     * 
     * GET http://localhost:8081/api/config/timeout
     */
    @GetMapping("/timeout")
    public Map<String, Object> getTimeout() {
        return Map.of(
            "timeout", timeout,
            "timeoutFromConfig", appConfig.getTimeout(),
            "unit", "milliseconds"
        );
    }

    /**
     * Get feature flags
     * 
     * GET http://localhost:8081/api/config/features
     */
    @GetMapping("/features")
    public Map<String, Object> getFeatures() {
        return Map.of(
            "features", appConfig.getFeatures() != null ? appConfig.getFeatures() : Map.of(),
            "environment", String.join(",", environment.getActiveProfiles())
        );
    }

    /**
     * Health check endpoint
     * 
     * GET http://localhost:8081/api/config/health
     */
    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
            "status", "UP",
            "configServer", "Connected",
            "profile", String.join(",", environment.getActiveProfiles()),
            "timestamp", LocalDateTime.now()
        );
    }
}
