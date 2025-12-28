package com.masterclass.autoconfigure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A custom service that will be auto-configured by Spring Boot.
 * 
 * This service demonstrates how auto-configuration creates and configures beans
 * based on properties and conditions.
 */
public class MyCustomService {

    private static final Logger logger = LoggerFactory.getLogger(MyCustomService.class);

    private final MyServiceProperties properties;

    public MyCustomService(MyServiceProperties properties) {
        this.properties = properties;
        logger.info("MyCustomService created with properties: {}", properties);
    }

    /**
     * Perform some operation using configured properties
     */
    public String doSomething() {
        logger.info("Executing doSomething() method...");
        logger.info("  Using endpoint: {}", properties.getEndpoint());
        logger.info("  With timeout: {} ms", properties.getTimeout());
        logger.info("  Retry count: {}", properties.getRetryCount());

        // Simulate some work
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return "Operation completed successfully with endpoint: " + properties.getEndpoint();
    }

    /**
     * Get configuration details
     */
    public MyServiceProperties getProperties() {
        return properties;
    }

    /**
     * Check if service is enabled
     */
    public boolean isEnabled() {
        return properties.isEnabled();
    }
}
