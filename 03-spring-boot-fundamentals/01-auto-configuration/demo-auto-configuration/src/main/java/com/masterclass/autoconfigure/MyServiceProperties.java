package com.masterclass.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * Configuration properties for MyCustomService.
 * 
 * These properties can be configured in application.properties:
 *   myservice.enabled=true
 *   myservice.endpoint=http://api.example.com
 *   myservice.timeout=5000
 *   myservice.retry-count=3
 */
@ConfigurationProperties(prefix = "myservice")
@Validated
public class MyServiceProperties {

    /**
     * Enable or disable the service
     */
    private boolean enabled = true;

    /**
     * Service endpoint URL
     */
    @NotBlank(message = "Endpoint must not be blank")
    private String endpoint = "http://localhost:8080";

    /**
     * Connection timeout in milliseconds
     */
    @Min(value = 1000, message = "Timeout must be at least 1000ms")
    @Max(value = 60000, message = "Timeout must not exceed 60000ms")
    private int timeout = 5000;

    /**
     * Number of retry attempts
     */
    @Min(value = 0, message = "Retry count must be non-negative")
    @Max(value = 10, message = "Retry count must not exceed 10")
    private int retryCount = 3;

    // Getters and Setters

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    @Override
    public String toString() {
        return "MyServiceProperties{" +
                "enabled=" + enabled +
                ", endpoint='" + endpoint + '\'' +
                ", timeout=" + timeout +
                ", retryCount=" + retryCount +
                '}';
    }
}
