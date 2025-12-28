package com.masterclass.autoconfigure.config;

import com.masterclass.autoconfigure.MyCustomService;
import com.masterclass.autoconfigure.MyServiceProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Auto-configuration class for MyCustomService.
 * 
 * This class demonstrates:
 * 1. @AutoConfiguration - Marks this as an auto-configuration class
 * 2. @ConditionalOnClass - Only applies if MyCustomService is on classpath
 * 3. @ConditionalOnProperty - Only applies if property is enabled
 * 4. @EnableConfigurationProperties - Binds properties from application.properties
 * 5. @ConditionalOnMissingBean - Allows users to override default configuration
 * 
 * In a real auto-configuration module, this would be registered in:
 * META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
 */
@AutoConfiguration
@ConditionalOnClass(MyCustomService.class)
@ConditionalOnProperty(
    prefix = "myservice",
    name = "enabled",
    havingValue = "true",
    matchIfMissing = true  // Enable by default even if property is missing
)
@EnableConfigurationProperties(MyServiceProperties.class)
public class MyServiceAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(MyServiceAutoConfiguration.class);

    public MyServiceAutoConfiguration() {
        logger.info("MyServiceAutoConfiguration initialized");
        logger.info("  - Conditions passed: MyCustomService class found, property enabled");
    }

    /**
     * Create MyCustomService bean if not already defined.
     * 
     * @ConditionalOnMissingBean allows users to provide their own implementation
     * by defining a MyCustomService bean in their configuration.
     */
    @Bean
    @ConditionalOnMissingBean
    public MyCustomService myCustomService(MyServiceProperties properties) {
        logger.info("Creating MyCustomService bean with auto-configuration");
        logger.info("  Properties: {}", properties);
        return new MyCustomService(properties);
    }
}
