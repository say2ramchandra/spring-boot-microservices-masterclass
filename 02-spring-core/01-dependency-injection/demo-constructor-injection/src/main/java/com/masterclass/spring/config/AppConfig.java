package com.masterclass.spring.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Configuration class using Java-based configuration.
 * 
 * @Configuration - Indicates this class provides bean definitions
 * @ComponentScan - Tells Spring where to find components (@Service, @Repository, etc.)
 * 
 * This replaces XML configuration with clean Java code!
 */
@Configuration
@ComponentScan(basePackages = "com.masterclass.spring")
public class AppConfig {
    
    // Spring will automatically scan and register all components
    // in the specified package and its sub-packages
    
    // @Bean methods can be added here for manual bean creation
    // But component scanning handles most cases automatically
}
