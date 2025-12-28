package com.masterclass.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

/**
 * Main application demonstrating Spring Boot configuration management.
 * 
 * This demo shows:
 * 1. Property binding with @Value
 * 2. Type-safe configuration with @ConfigurationProperties
 * 3. Profile-specific configuration
 * 4. Property precedence
 * 5. Validation
 */
@SpringBootApplication
@EnableConfigurationProperties({
    AppProperties.class,
    DatabaseProperties.class
})
public class ConfigurationDemoApp {

    private static final Logger logger = LoggerFactory.getLogger(ConfigurationDemoApp.class);

    public static void main(String[] args) {
        SpringApplication.run(ConfigurationDemoApp.class, args);
    }

    @Bean
    public CommandLineRunner demo(
            Environment env,
            AppProperties appProperties,
            DatabaseProperties databaseProperties,
            SimplePropertiesExample simpleProps) {
        
        return args -> {
            logger.info("=".repeat(80));
            logger.info("SPRING BOOT CONFIGURATION MANAGEMENT DEMO");
            logger.info("=".repeat(80));

            // Part 1: Environment and active profiles
            showEnvironmentInfo(env);

            // Part 2: Simple @Value properties
            showSimpleProperties(simpleProps);

            // Part 3: @ConfigurationProperties - AppProperties
            showAppProperties(appProperties);

            // Part 4: @ConfigurationProperties - DatabaseProperties (nested)
            showDatabaseProperties(databaseProperties);

            // Part 5: Property precedence demonstration
            showPropertyPrecedence(env);

            logger.info("=".repeat(80));
            logger.info("Demo completed!");
            logger.info("=".repeat(80));
        };
    }

    /**
     * Part 1: Show environment and profile information
     */
    private void showEnvironmentInfo(Environment env) {
        logger.info("\n--- Part 1: Environment Information ---\n");

        String[] activeProfiles = env.getActiveProfiles();
        String[] defaultProfiles = env.getDefaultProfiles();

        logger.info("Active Profiles: {}", 
            activeProfiles.length > 0 ? String.join(", ", activeProfiles) : "none");
        logger.info("Default Profiles: {}", String.join(", ", defaultProfiles));

        // Show some environment properties
        logger.info("\nEnvironment Properties:");
        logger.info("  java.version: {}", env.getProperty("java.version"));
        logger.info("  os.name: {}", env.getProperty("os.name"));
        logger.info("  user.name: {}", env.getProperty("user.name"));
    }

    /**
     * Part 2: Show simple @Value properties
     */
    private void showSimpleProperties(SimplePropertiesExample simpleProps) {
        logger.info("\n--- Part 2: Simple @Value Properties ---\n");

        logger.info("Application Name: {}", simpleProps.getAppName());
        logger.info("Application Version: {}", simpleProps.getAppVersion());
        logger.info("Server Port: {}", simpleProps.getServerPort());
        logger.info("Environment: {}", simpleProps.getEnvironment());
        logger.info("Debug Enabled: {}", simpleProps.isDebugEnabled());
    }

    /**
     * Part 3: Show @ConfigurationProperties - AppProperties
     */
    private void showAppProperties(AppProperties appProperties) {
        logger.info("\n--- Part 3: @ConfigurationProperties - AppProperties ---\n");

        logger.info("Name: {}", appProperties.getName());
        logger.info("Version: {}", appProperties.getVersion());
        logger.info("Description: {}", appProperties.getDescription());
        logger.info("Environment: {}", appProperties.getEnvironment());
        logger.info("Debug Enabled: {}", appProperties.isDebugEnabled());

        logger.info("\nFeatures Enabled:");
        appProperties.getFeatures().getEnabled().forEach(feature -> 
            logger.info("  - {}", feature));

        logger.info("\nConnection Settings:");
        logger.info("  Max Pool Size: {}", appProperties.getConnection().getMaxPoolSize());
        logger.info("  Min Pool Size: {}", appProperties.getConnection().getMinPoolSize());
        logger.info("  Timeout: {} ms", appProperties.getConnection().getTimeout());

        logger.info("\nAPI Configuration:");
        logger.info("  Endpoint: {}", appProperties.getApi().getEndpoint());
        logger.info("  Timeout: {} ms", appProperties.getApi().getTimeout());
        logger.info("  Retry Count: {}", appProperties.getApi().getRetryCount());
    }

    /**
     * Part 4: Show @ConfigurationProperties - DatabaseProperties
     */
    private void showDatabaseProperties(DatabaseProperties databaseProperties) {
        logger.info("\n--- Part 4: @ConfigurationProperties - DatabaseProperties ---\n");

        logger.info("Database Configuration:");
        logger.info("  Host: {}", databaseProperties.getHost());
        logger.info("  Port: {}", databaseProperties.getPort());
        logger.info("  Database Name: {}", databaseProperties.getName());
        logger.info("  Username: {}", databaseProperties.getUsername());
        logger.info("  Password: {}", maskPassword(databaseProperties.getPassword()));

        // Build connection URL
        String url = String.format("jdbc:mysql://%s:%d/%s",
            databaseProperties.getHost(),
            databaseProperties.getPort(),
            databaseProperties.getName());
        logger.info("  Connection URL: {}", url);
    }

    /**
     * Part 5: Demonstrate property precedence
     */
    private void showPropertyPrecedence(Environment env) {
        logger.info("\n--- Part 5: Property Precedence ---\n");

        logger.info("Property precedence (highest to lowest):");
        logger.info("1. Command line arguments");
        logger.info("2. Environment variables");
        logger.info("3. Profile-specific files (application-{profile}.yml)");
        logger.info("4. Default application.yml");

        logger.info("\nExample: server.port property");
        String port = env.getProperty("server.port");
        logger.info("  Current value: {}", port);
        logger.info("  Source: Profile-specific configuration");
        logger.info("  Can be overridden with: --server.port=9090");
    }

    /**
     * Helper to mask passwords
     */
    private String maskPassword(String password) {
        if (password == null || password.isEmpty()) {
            return "[empty]";
        }
        return "*".repeat(password.length());
    }
}
