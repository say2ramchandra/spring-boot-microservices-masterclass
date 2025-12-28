package com.masterclass.context;

import com.masterclass.context.config.AppConfig;
import com.masterclass.context.service.DatabaseService;
import com.masterclass.context.service.UserService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;

import java.io.IOException;

/**
 * ApplicationContext Demo
 * 
 * Demonstrates:
 * 1. Creating ApplicationContext
 * 2. Bean retrieval methods
 * 3. Context lifecycle events
 * 4. Custom event publishing
 * 5. Profiles
 * 6. Environment abstraction
 * 7. Resource loading
 */
public class ApplicationContextDemo {

    public static void main(String[] args) throws IOException {
        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║      ApplicationContext Demo                 ║");
        System.out.println("╚══════════════════════════════════════════════╝\n");

        demonstrateContextCreation();
        demonstrateBeanRetrieval();
        demonstrateProfiles();
        demonstrateEvents();
        demonstrateEnvironment();
        demonstrateResourceLoading();
    }

    private static void demonstrateContextCreation() {
        System.out.println("=".repeat(50));
        System.out.println("PART 1: Creating ApplicationContext");
        System.out.println("=".repeat(50) + "\n");

        System.out.println("1. Creating context with configuration class:");
        try (AnnotationConfigApplicationContext context = 
                new AnnotationConfigApplicationContext(AppConfig.class)) {
            
            System.out.println("   ✅ Context created successfully");
            System.out.println("   Context ID: " + context.getId());
            System.out.println("   Display name: " + context.getDisplayName());
            System.out.println("   Startup date: " + context.getStartupDate());
        }
        System.out.println("   ✅ Context closed automatically\n");
    }

    private static void demonstrateBeanRetrieval() {
        System.out.println("=".repeat(50));
        System.out.println("PART 2: Bean Retrieval Methods");
        System.out.println("=".repeat(50) + "\n");

        try (AnnotationConfigApplicationContext context = 
                new AnnotationConfigApplicationContext()) {
            context.getEnvironment().setActiveProfiles("dev");
            context.register(AppConfig.class);
            context.refresh();

            System.out.println("1. Get bean by type:");
            UserService userService = context.getBean(UserService.class);
            System.out.println("   Retrieved: " + userService.getClass().getSimpleName());

            System.out.println("\n2. Get bean by name and type:");
            DatabaseService dbService = context.getBean("devDatabaseService", DatabaseService.class);
            System.out.println("   Retrieved: " + dbService.getClass().getSimpleName());
            System.out.println("   Database: " + dbService.getDatabaseName());

            System.out.println("\n3. Check if bean exists:");
            boolean exists = context.containsBean("userService");
            System.out.println("   'userService' exists: " + exists);

            System.out.println("\n4. Get all bean names:");
            String[] beanNames = context.getBeanDefinitionNames();
            System.out.println("   Total beans: " + beanNames.length);
            System.out.println("   First 5 beans:");
            for (int i = 0; i < Math.min(5, beanNames.length); i++) {
                System.out.println("   - " + beanNames[i]);
            }

            System.out.println("\n5. Get bean count:");
            int count = context.getBeanDefinitionCount();
            System.out.println("   Total bean definitions: " + count);
        }
        System.out.println();
    }

    private static void demonstrateProfiles() {
        System.out.println("=".repeat(50));
        System.out.println("PART 3: Profiles");
        System.out.println("=".repeat(50) + "\n");

        System.out.println("1. Development Profile:");
        try (AnnotationConfigApplicationContext devContext = 
                new AnnotationConfigApplicationContext()) {
            devContext.getEnvironment().setActiveProfiles("dev");
            devContext.register(AppConfig.class);
            devContext.refresh();

            DatabaseService dbService = devContext.getBean(DatabaseService.class);
            dbService.connect();
        }

        System.out.println("\n2. Production Profile:");
        try (AnnotationConfigApplicationContext prodContext = 
                new AnnotationConfigApplicationContext()) {
            prodContext.getEnvironment().setActiveProfiles("prod");
            prodContext.register(AppConfig.class);
            prodContext.refresh();

            DatabaseService dbService = prodContext.getBean(DatabaseService.class);
            dbService.connect();
        }
        System.out.println();
    }

    private static void demonstrateEvents() {
        System.out.println("=".repeat(50));
        System.out.println("PART 4: Event Publishing and Listening");
        System.out.println("=".repeat(50) + "\n");

        try (AnnotationConfigApplicationContext context = 
                new AnnotationConfigApplicationContext()) {
            context.getEnvironment().setActiveProfiles("dev");
            context.register(AppConfig.class);
            context.refresh();

            System.out.println("1. Publishing custom event:");
            UserService userService = context.getBean(UserService.class);
            userService.createUser("john.doe");

            System.out.println("\n2. Lifecycle events:");
            System.out.println("   Starting context...");
            context.start();
            
            System.out.println("   Stopping context...");
            context.stop();
        }
        System.out.println();
    }

    private static void demonstrateEnvironment() {
        System.out.println("=".repeat(50));
        System.out.println("PART 5: Environment Abstraction");
        System.out.println("=".repeat(50) + "\n");

        try (AnnotationConfigApplicationContext context = 
                new AnnotationConfigApplicationContext()) {
            context.getEnvironment().setActiveProfiles("dev", "debug");
            context.register(AppConfig.class);
            context.refresh();

            Environment env = context.getEnvironment();

            System.out.println("1. Active Profiles:");
            String[] profiles = env.getActiveProfiles();
            for (String profile : profiles) {
                System.out.println("   - " + profile);
            }

            System.out.println("\n2. System Properties:");
            String javaVersion = env.getProperty("java.version");
            String osName = env.getProperty("os.name");
            System.out.println("   Java Version: " + javaVersion);
            System.out.println("   OS Name: " + osName);

            System.out.println("\n3. Property with default:");
            String customProp = env.getProperty("custom.property", "default-value");
            System.out.println("   Custom property: " + customProp);

            System.out.println("\n4. Check property existence:");
            boolean hasJavaHome = env.containsProperty("java.home");
            System.out.println("   Has 'java.home': " + hasJavaHome);
        }
        System.out.println();
    }

    private static void demonstrateResourceLoading() throws IOException {
        System.out.println("=".repeat(50));
        System.out.println("PART 6: Resource Loading");
        System.out.println("=".repeat(50) + "\n");

        try (AnnotationConfigApplicationContext context = 
                new AnnotationConfigApplicationContext(AppConfig.class)) {

            System.out.println("1. Load resource:");
            try {
                Resource resource = context.getResource("classpath:application.properties");
                if (resource.exists()) {
                    System.out.println("   Resource found: " + resource.getFilename());
                    System.out.println("   Resource URL: " + resource.getURL());
                } else {
                    System.out.println("   Resource not found (this is expected in demo)");
                }
            } catch (Exception e) {
                System.out.println("   Resource not found (this is expected in demo)");
            }

            System.out.println("\n2. Resource loader capabilities:");
            System.out.println("   Can load from:");
            System.out.println("   - classpath: (from classpath)");
            System.out.println("   - file: (from file system)");
            System.out.println("   - http:// (from URL)");
            System.out.println("   - classpath*: (from all matching resources)");
        }
        System.out.println();
    }
}
