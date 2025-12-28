package com.masterclass.annotations;

import com.masterclass.annotations.component.AppConfiguration;
import com.masterclass.annotations.config.AppConfig;
import com.masterclass.annotations.service.DatabaseService;
import com.masterclass.annotations.service.UserService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Spring Annotations Demo
 * 
 * Demonstrates:
 * 1. Stereotype annotations (@Component, @Service, @Repository)
 * 2. Configuration annotations (@Configuration, @Bean)
 * 3. Dependency injection (@Autowired)
 * 4. Component scanning (@ComponentScan)
 * 5. Property injection (@Value, @PropertySource)
 * 6. Lifecycle annotations (@PostConstruct, @PreDestroy)
 * 7. Qualifiers (@Primary, @Qualifier)
 * 8. Profiles (@Profile)
 */
public class SpringAnnotationsDemo {

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║      Spring Annotations Demo                ║");
        System.out.println("╚══════════════════════════════════════════════╝\n");

        demonstrateStereotypeAnnotations();
        demonstrateConfigurationAndBeans();
        demonstratePropertyInjection();
        demonstrateLifecycleCallbacks();
        demonstratePrimaryAnnotation();
        demonstrateProfiles();
    }

    private static void demonstrateStereotypeAnnotations() {
        System.out.println("=".repeat(50));
        System.out.println("PART 1: Stereotype Annotations");
        System.out.println("=".repeat(50) + "\n");

        System.out.println("1. @Service, @Repository, @Component:");
        try (AnnotationConfigApplicationContext context = 
                new AnnotationConfigApplicationContext(AppConfig.class)) {
            
            UserService userService = context.getBean(UserService.class);
            
            Long userId = userService.createUser("john.doe");
            System.out.println("   User created with ID: " + userId);
            
            String username = userService.getUser(userId);
            System.out.println("   Retrieved user: " + username);
        }
        System.out.println();
    }

    private static void demonstrateConfigurationAndBeans() {
        System.out.println("=".repeat(50));
        System.out.println("PART 2: @Configuration and @Bean");
        System.out.println("=".repeat(50) + "\n");

        System.out.println("1. Bean defined in @Configuration class:");
        try (AnnotationConfigApplicationContext context = 
                new AnnotationConfigApplicationContext(AppConfig.class)) {
            
            DatabaseService dbService = context.getBean("primaryDatabase", DatabaseService.class);
            dbService.connect();
            
            System.out.println("\n2. Counting beans of type DatabaseService:");
            String[] beanNames = context.getBeanNamesForType(DatabaseService.class);
            System.out.println("   Found " + beanNames.length + " DatabaseService bean(s):");
            for (String name : beanNames) {
                System.out.println("   - " + name);
            }
        }
        System.out.println();
    }

    private static void demonstratePropertyInjection() {
        System.out.println("=".repeat(50));
        System.out.println("PART 3: @Value and @PropertySource");
        System.out.println("=".repeat(50) + "\n");

        System.out.println("1. Injecting values from properties file:");
        try (AnnotationConfigApplicationContext context = 
                new AnnotationConfigApplicationContext(AppConfig.class)) {
            
            AppConfiguration appConfig = context.getBean(AppConfiguration.class);
            appConfig.printConfig();
        }
        System.out.println();
    }

    private static void demonstrateLifecycleCallbacks() {
        System.out.println("=".repeat(50));
        System.out.println("PART 4: @PostConstruct and @PreDestroy");
        System.out.println("=".repeat(50) + "\n");

        System.out.println("1. Lifecycle callbacks:");
        try (AnnotationConfigApplicationContext context = 
                new AnnotationConfigApplicationContext(AppConfig.class)) {
            
            System.out.println("   Context created, @PostConstruct called above");
            UserService userService = context.getBean(UserService.class);
            userService.createUser("lifecycle.user");
        }
        System.out.println("   Context closed, @PreDestroy called above\n");
    }

    private static void demonstratePrimaryAnnotation() {
        System.out.println("=".repeat(50));
        System.out.println("PART 5: @Primary Annotation");
        System.out.println("=".repeat(50) + "\n");

        System.out.println("1. @Primary selects default bean:");
        try (AnnotationConfigApplicationContext context = 
                new AnnotationConfigApplicationContext(AppConfig.class)) {
            
            // Gets the @Primary bean
            DatabaseService dbService = context.getBean(DatabaseService.class);
            System.out.println("   Default bean: " + dbService.getName());
            dbService.connect();
        }
        System.out.println();
    }

    private static void demonstrateProfiles() {
        System.out.println("=".repeat(50));
        System.out.println("PART 6: @Profile Annotation");
        System.out.println("=".repeat(50) + "\n");

        System.out.println("1. Development Profile:");
        try (AnnotationConfigApplicationContext context = 
                new AnnotationConfigApplicationContext()) {
            context.getEnvironment().setActiveProfiles("dev");
            context.register(AppConfig.class);
            context.refresh();

            String[] beanNames = context.getBeanNamesForType(DatabaseService.class);
            System.out.println("   Active DatabaseService beans:");
            for (String name : beanNames) {
                DatabaseService db = context.getBean(name, DatabaseService.class);
                System.out.println("   - " + db.getName());
            }
        }

        System.out.println("\n2. Production Profile:");
        try (AnnotationConfigApplicationContext context = 
                new AnnotationConfigApplicationContext()) {
            context.getEnvironment().setActiveProfiles("prod");
            context.register(AppConfig.class);
            context.refresh();

            String[] beanNames = context.getBeanNamesForType(DatabaseService.class);
            System.out.println("   Active DatabaseService beans:");
            for (String name : beanNames) {
                DatabaseService db = context.getBean(name, DatabaseService.class);
                System.out.println("   - " + db.getName());
            }
        }
        System.out.println();
    }
}
