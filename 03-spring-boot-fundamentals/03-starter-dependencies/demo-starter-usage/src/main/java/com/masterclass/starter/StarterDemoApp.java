package com.masterclass.starter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

/**
 * Demo application showing Spring Boot starter dependencies in action.
 * 
 * This demo demonstrates:
 * 1. Common starters and their auto-configured beans
 * 2. Dependencies brought by each starter
 * 3. Actuator endpoints for monitoring
 * 4. How different starters work together
 */
@SpringBootApplication
public class StarterDemoApp {

    private static final Logger logger = LoggerFactory.getLogger(StarterDemoApp.class);

    public static void main(String[] args) {
        SpringApplication.run(StarterDemoApp.class, args);
    }

    @Bean
    public CommandLineRunner demo(ApplicationContext ctx) {
        return args -> {
            logger.info("=".repeat(80));
            logger.info("SPRING BOOT STARTER DEPENDENCIES DEMO");
            logger.info("=".repeat(80));

            // Part 1: Show which starters are being used
            showStartersInUse();

            // Part 2: Show beans configured by starters
            showStarterBeans(ctx);

            // Part 3: Show actuator endpoints
            showActuatorInfo();

            // Part 4: Show database auto-configuration
            showDatabaseInfo(ctx);

            logger.info("=".repeat(80));
            logger.info("Demo completed!");
            logger.info("=".repeat(80));
            logger.info("\nYou can explore:");
            logger.info("  - REST API: http://localhost:8080/api/demo");
            logger.info("  - H2 Console: http://localhost:8080/h2-console");
            logger.info("  - Actuator Health: http://localhost:8080/actuator/health");
            logger.info("  - Actuator Beans: http://localhost:8080/actuator/beans");
            logger.info("=".repeat(80));
        };
    }

    /**
     * Part 1: Show which starters are in use
     */
    private void showStartersInUse() {
        logger.info("\n--- Part 1: Starters in Use ---\n");

        logger.info("This application uses the following Spring Boot starters:");
        logger.info("  1. spring-boot-starter");
        logger.info("     → Core Spring Boot features, logging, YAML support");
        logger.info("  2. spring-boot-starter-web");
        logger.info("     → Spring MVC, embedded Tomcat, JSON support (Jackson)");
        logger.info("  3. spring-boot-starter-data-jpa");
        logger.info("     → Hibernate, Spring Data JPA, transaction management");
        logger.info("  4. spring-boot-starter-validation");
        logger.info("     → Hibernate Validator for bean validation");
        logger.info("  5. spring-boot-starter-actuator");
        logger.info("     → Production-ready monitoring and management endpoints");

        logger.info("\nRuntime dependencies:");
        logger.info("  - H2 Database (in-memory database for testing)");

        logger.info("\nDevelopment dependencies:");
        logger.info("  - spring-boot-devtools (optional, development only)");
    }

    /**
     * Part 2: Show key beans auto-configured by starters
     */
    private void showStarterBeans(ApplicationContext ctx) {
        logger.info("\n--- Part 2: Auto-Configured Beans from Starters ---\n");

        // From spring-boot-starter-web
        logger.info("From spring-boot-starter-web:");
        checkBean(ctx, "dispatcherServlet", "DispatcherServlet for handling HTTP requests");
        checkBean(ctx, "requestMappingHandlerMapping", "Maps @RequestMapping to handlers");
        checkBean(ctx, "jacksonObjectMapper", "JSON serialization/deserialization");

        // From spring-boot-starter-data-jpa
        logger.info("\nFrom spring-boot-starter-data-jpa:");
        checkBean(ctx, "dataSource", "Database connection pool (HikariCP)");
        checkBean(ctx, "entityManagerFactory", "JPA EntityManager factory");
        checkBean(ctx, "transactionManager", "Transaction management");

        // From spring-boot-starter-validation
        logger.info("\nFrom spring-boot-starter-validation:");
        checkBean(ctx, "validator", "Bean validation (Hibernate Validator)");

        // From spring-boot-starter-actuator
        logger.info("\nFrom spring-boot-starter-actuator:");
        checkBean(ctx, "healthEndpoint", "Health check endpoint");
        checkBean(ctx, "metricsEndpoint", "Metrics collection endpoint");
    }

    /**
     * Part 3: Show actuator endpoints information
     */
    private void showActuatorInfo() {
        logger.info("\n--- Part 3: Actuator Endpoints ---\n");

        logger.info("Spring Boot Actuator provides production-ready features:");
        logger.info("\nAvailable endpoints:");
        logger.info("  GET  /actuator/health   - Application health status");
        logger.info("  GET  /actuator/info     - Application information");
        logger.info("  GET  /actuator/metrics  - Application metrics");
        logger.info("  GET  /actuator/beans    - All Spring beans");
        logger.info("  GET  /actuator/env      - Environment properties");

        logger.info("\nTry accessing these endpoints in your browser or with curl!");
    }

    /**
     * Part 4: Show database configuration from starter
     */
    private void showDatabaseInfo(ApplicationContext ctx) {
        logger.info("\n--- Part 4: Database Auto-Configuration ---\n");

        logger.info("spring-boot-starter-data-jpa automatically configured:");
        logger.info("  ✓ DataSource (HikariCP connection pool)");
        logger.info("  ✓ EntityManagerFactory (Hibernate)");
        logger.info("  ✓ TransactionManager");
        logger.info("  ✓ JPA Repositories");

        logger.info("\nDatabase details:");
        logger.info("  Type: H2 (in-memory)");
        logger.info("  URL: jdbc:h2:mem:testdb");
        logger.info("  Console: http://localhost:8080/h2-console");
    }

    /**
     * Helper method to check if bean exists
     */
    private void checkBean(ApplicationContext ctx, String beanName, String description) {
        if (ctx.containsBean(beanName)) {
            logger.info("  ✓ {}: {}", beanName, description);
        } else {
            logger.info("  ✗ {} not found", beanName);
        }
    }
}
