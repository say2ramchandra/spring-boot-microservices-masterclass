package com.masterclass.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Simple example of using @Value annotation for property injection.
 * 
 * Demonstrates:
 * - Basic property injection
 * - Default values
 * - SpEL expressions
 */
@Component
public class SimplePropertiesExample {

    @Value("${app.name}")
    private String appName;

    @Value("${app.version}")
    private String appVersion;

    @Value("${server.port}")
    private int serverPort;

    @Value("${app.environment}")
    private String environment;

    @Value("${app.debug-enabled:false}")  // Default: false
    private boolean debugEnabled;

    // Getters

    public String getAppName() {
        return appName;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public int getServerPort() {
        return serverPort;
    }

    public String getEnvironment() {
        return environment;
    }

    public boolean isDebugEnabled() {
        return debugEnabled;
    }
}
