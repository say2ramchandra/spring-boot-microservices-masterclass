package com.masterclass.annotations.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppConfiguration {
    
    @Value("${app.name}")
    private String appName;
    
    @Value("${app.version}")
    private String appVersion;
    
    @Value("${app.timeout:60}") // Default value
    private int timeout;

    public void printConfig() {
        System.out.println("   App Name: " + appName);
        System.out.println("   App Version: " + appVersion);
        System.out.println("   Timeout: " + timeout + " seconds");
    }
}
