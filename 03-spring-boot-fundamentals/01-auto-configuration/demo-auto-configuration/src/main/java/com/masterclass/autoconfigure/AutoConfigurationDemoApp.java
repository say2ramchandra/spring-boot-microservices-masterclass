package com.masterclass.autoconfigure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

/**
 * Main application demonstrating Spring Boot auto-configuration concepts.
 * 
 * This demo shows:
 * 1. How @SpringBootApplication works
 * 2. Built-in conditional annotations
 * 3. Custom auto-configuration
 * 4. Configuration properties
 * 5. Bean overriding
 */
@SpringBootApplication
public class AutoConfigurationDemoApp {

    private static final Logger logger = LoggerFactory.getLogger(AutoConfigurationDemoApp.class);

    public static void main(String[] args) {
        SpringApplication.run(AutoConfigurationDemoApp.class, args);
    }

    @Bean
    public CommandLineRunner demo(ApplicationContext ctx) {
        return args -> {
            logger.info("=".repeat(80));
            logger.info("SPRING BOOT AUTO-CONFIGURATION DEMO");
            logger.info("=".repeat(80));

            // Part 1: Show all beans created by auto-configuration
            showAllBeans(ctx);

            // Part 2: Demonstrate custom auto-configuration
            demonstrateCustomAutoConfiguration(ctx);

            // Part 3: Show configuration properties
            demonstrateConfigurationProperties(ctx);

            // Part 4: Demonstrate conditional beans
            demonstrateConditionalBeans(ctx);

            // Part 5: Show bean overriding
            demonstrateBeanOverriding(ctx);

            logger.info("=".repeat(80));
            logger.info("Demo completed!");
            logger.info("=".repeat(80));
        };
    }

    /**
     * Part 1: Display all beans registered in the ApplicationContext
     */
    private void showAllBeans(ApplicationContext ctx) {
        logger.info("\n--- Part 1: All Beans Registered by Auto-Configuration ---\n");

        String[] beanNames = ctx.getBeanDefinitionNames();
        Arrays.sort(beanNames);

        logger.info("Total beans registered: {}", beanNames.length);
        logger.info("\nShowing first 20 beans:");

        Arrays.stream(beanNames)
                .limit(20)
                .forEach(name -> logger.info("  - {}", name));

        logger.info("\n... and {} more beans", Math.max(0, beanNames.length - 20));

        // Show specific auto-configured beans
        logger.info("\nChecking for specific auto-configured beans:");
        checkBean(ctx, "autoConfigurationDemoApp", "Application bean");
        checkBean(ctx, "myCustomService", "Custom service bean");
        checkBean(ctx, "emailService", "Email service (conditional)");
        checkBean(ctx, "notificationService", "Notification service (conditional)");
    }

    /**
     * Part 2: Demonstrate custom auto-configuration
     */
    private void demonstrateCustomAutoConfiguration(ApplicationContext ctx) {
        logger.info("\n--- Part 2: Custom Auto-Configuration ---\n");

        try {
            MyCustomService service = ctx.getBean(MyCustomService.class);
            logger.info("✓ MyCustomService was auto-configured!");
            
            MyServiceProperties props = ctx.getBean(MyServiceProperties.class);
            logger.info("Configuration properties loaded:");
            logger.info("  - Enabled: {}", props.isEnabled());
            logger.info("  - Endpoint: {}", props.getEndpoint());
            logger.info("  - Timeout: {} ms", props.getTimeout());
            logger.info("  - Retry Count: {}", props.getRetryCount());

            // Test the service
            String result = service.doSomething();
            logger.info("Service execution result: {}", result);

        } catch (Exception e) {
            logger.error("✗ MyCustomService was NOT auto-configured", e);
        }
    }

    /**
     * Part 3: Demonstrate configuration properties binding
     */
    private void demonstrateConfigurationProperties(ApplicationContext ctx) {
        logger.info("\n--- Part 3: Configuration Properties ---\n");

        try {
            MyServiceProperties props = ctx.getBean(MyServiceProperties.class);
            
            logger.info("Properties bound from application.properties:");
            logger.info("  myservice.enabled = {}", props.isEnabled());
            logger.info("  myservice.endpoint = {}", props.getEndpoint());
            logger.info("  myservice.timeout = {}", props.getTimeout());
            logger.info("  myservice.retry-count = {}", props.getRetryCount());

            logger.info("\nThese properties control the auto-configuration behavior.");

        } catch (Exception e) {
            logger.error("Configuration properties not found", e);
        }
    }

    /**
     * Part 4: Demonstrate conditional beans
     */
    private void demonstrateConditionalBeans(ApplicationContext ctx) {
        logger.info("\n--- Part 4: Conditional Beans ---\n");

        // Check EmailService (enabled by property)
        logger.info("Checking EmailService (@ConditionalOnProperty):");
        if (ctx.containsBean("emailService")) {
            logger.info("✓ EmailService is registered (feature.email.enabled=true)");
            try {
                Object emailService = ctx.getBean("emailService");
                logger.info("  EmailService instance: {}", emailService.getClass().getName());
            } catch (Exception e) {
                logger.error("Error getting EmailService", e);
            }
        } else {
            logger.info("✗ EmailService is NOT registered (feature.email.enabled=false)");
        }

        // Check NotificationService (disabled by property)
        logger.info("\nChecking NotificationService (@ConditionalOnProperty):");
        if (ctx.containsBean("notificationService")) {
            logger.info("✓ NotificationService is registered");
        } else {
            logger.info("✗ NotificationService is NOT registered (feature.notification.enabled=false)");
        }

        // Check DatabaseService (conditional on class)
        logger.info("\nChecking DatabaseService (@ConditionalOnClass):");
        if (ctx.containsBean("databaseService")) {
            logger.info("✓ DatabaseService is registered (H2 on classpath)");
        } else {
            logger.info("✗ DatabaseService is NOT registered (H2 not on classpath)");
        }
    }

    /**
     * Part 5: Demonstrate bean overriding
     */
    private void demonstrateBeanOverriding(ApplicationContext ctx) {
        logger.info("\n--- Part 5: Bean Overriding ---\n");

        logger.info("Checking if default service bean was overridden:");
        
        if (ctx.containsBean("defaultService")) {
            try {
                Object service = ctx.getBean("defaultService");
                logger.info("✓ Found defaultService bean");
                logger.info("  Type: {}", service.getClass().getName());
                logger.info("  This demonstrates @ConditionalOnMissingBean behavior");
            } catch (Exception e) {
                logger.error("Error getting defaultService", e);
            }
        } else {
            logger.info("✗ defaultService bean not found");
        }
    }

    /**
     * Helper method to check if a bean exists
     */
    private void checkBean(ApplicationContext ctx, String beanName, String description) {
        if (ctx.containsBean(beanName)) {
            Object bean = ctx.getBean(beanName);
            logger.info("  ✓ {}: {} ({})", 
                description, 
                beanName, 
                bean.getClass().getSimpleName());
        } else {
            logger.info("  ✗ {}: {} not found", description, beanName);
        }
    }
}
