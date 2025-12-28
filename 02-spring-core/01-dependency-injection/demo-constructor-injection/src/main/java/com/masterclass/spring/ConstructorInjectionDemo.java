package com.masterclass.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import com.masterclass.spring.config.AppConfig;
import com.masterclass.spring.service.*;

/**
 * Comprehensive demonstration of Constructor Injection in Spring.
 * 
 * Constructor Injection is the RECOMMENDED approach for dependency injection.
 * 
 * Why Constructor Injection?
 * 1. Immutability - dependencies can be final
 * 2. Required dependencies - NPE impossible
 * 3. Testability - easy to instantiate and test
 * 4. Circular dependency detection
 * 5. Clear contract - dependencies visible in constructor
 * 
 * @author Spring Boot Microservices Masterclass
 */
public class ConstructorInjectionDemo {

    public static void main(String[] args) {
        System.out.println("=".repeat(70));
        System.out.println("     Spring Constructor Injection Demonstration");
        System.out.println("=".repeat(70));
        System.out.println();

        // Create Spring ApplicationContext
        System.out.println("🔧 Initializing Spring Container...");
        ApplicationContext context = 
            new AnnotationConfigApplicationContext(AppConfig.class);
        System.out.println("✅ Spring Container initialized!");
        System.out.println();

        System.out.println("=".repeat(70));
        System.out.println();

        // Example 1: Simple Constructor Injection
        simpleConstructorInjection(context);

        System.out.println("\n" + "=".repeat(70) + "\n");

        // Example 2: Multiple Dependencies
        multipleDependencies(context);

        System.out.println("\n" + "=".repeat(70) + "\n");

        // Example 3: Constructor with @Autowired
        constructorWithAutowired(context);

        System.out.println("\n" + "=".repeat(70) + "\n");

        // Example 4: Real-World E-Commerce Example
        realWorldEcommerceExample(context);

        System.out.println("\n" + "=".repeat(70) + "\n");

        // Example 5: Testing Constructor Injection
        testingConstructorInjection();

        System.out.println("\n" + "=".repeat(70));
        System.out.println("           Demo Completed Successfully!");
        System.out.println("=".repeat(70));

        // Close the context
        ((AnnotationConfigApplicationContext) context).close();
    }

    /**
     * Example 1: Simple constructor injection with single dependency
     */
    private static void simpleConstructorInjection(ApplicationContext context) {
        System.out.println("📚 Example 1: Simple Constructor Injection");
        System.out.println("-".repeat(50));

        // Get UserService from Spring container
        // Spring automatically injects UserRepository
        UserService userService = context.getBean(UserService.class);

        // Use the service
        String result = userService.createUser("john@example.com", "John Doe");
        System.out.println(result);

        String user = userService.findUser(1L);
        System.out.println(user);

        System.out.println("\n💡 Spring injected UserRepository into UserService!");
        System.out.println("💡 Dependencies are FINAL - immutable and thread-safe!");
    }

    /**
     * Example 2: Constructor injection with multiple dependencies
     */
    private static void multipleDependencies(ApplicationContext context) {
        System.out.println("📚 Example 2: Multiple Dependencies");
        System.out.println("-".repeat(50));

        OrderService orderService = context.getBean(OrderService.class);

        String result = orderService.placeOrder(1L, "LAPTOP-001", 999.99);
        System.out.println(result);

        System.out.println("\n💡 OrderService has 3 dependencies:");
        System.out.println("   1. OrderRepository");
        System.out.println("   2. EmailService");
        System.out.println("   3. PaymentService");
        System.out.println("💡 All injected automatically by Spring!");
    }

    /**
     * Example 3: Explicit @Autowired on constructor
     */
    private static void constructorWithAutowired(ApplicationContext context) {
        System.out.println("📚 Example 3: Constructor with @Autowired");
        System.out.println("-".repeat(50));

        NotificationService notificationService = 
            context.getBean(NotificationService.class);

        notificationService.sendNotification(
            "user@example.com", 
            "Welcome to our platform!"
        );

        System.out.println("\n💡 @Autowired is OPTIONAL for single constructor!");
        System.out.println("💡 Spring 4.3+ automatically wires single constructor!");
    }

    /**
     * Example 4: Real-world e-commerce order processing
     */
    private static void realWorldEcommerceExample(ApplicationContext context) {
        System.out.println("📚 Example 4: Real-World E-Commerce");
        System.out.println("-".repeat(50));

        OrderProcessingService orderProcessor = 
            context.getBean(OrderProcessingService.class);

        // Process a complete order
        boolean success = orderProcessor.processCompleteOrder(
            1001L,           // orderId
            500.00,          // amount
            "user@shop.com"  // email
        );

        if (success) {
            System.out.println("\n✅ Order processed successfully!");
        } else {
            System.out.println("\n❌ Order processing failed!");
        }

        System.out.println("\n💡 OrderProcessingService demonstrates:");
        System.out.println("   • Multiple service dependencies");
        System.out.println("   • Transaction coordination");
        System.out.println("   • Business logic orchestration");
        System.out.println("   • All made possible by Constructor Injection!");
    }

    /**
     * Example 5: Demonstrating testability of constructor injection
     */
    private static void testingConstructorInjection() {
        System.out.println("📚 Example 5: Testing Constructor Injection");
        System.out.println("-".repeat(50));

        System.out.println("Constructor Injection makes testing EASY:");
        System.out.println();

        // No Spring needed for testing!
        MockUserRepository mockRepo = new MockUserRepository();
        UserService userService = new UserService(mockRepo);

        String result = userService.createUser("test@example.com", "Test User");
        System.out.println(result);

        System.out.println("\n💡 No Spring container needed for testing!");
        System.out.println("💡 Just create mocks and pass to constructor!");
        System.out.println("💡 This is why Constructor Injection is superior!");
    }

    /**
     * Mock repository for testing example
     */
    static class MockUserRepository {
        public String save(String email, String name) {
            return "Mock: Saved user " + name;
        }

        public String findById(Long id) {
            return "Mock: Found user with ID " + id;
        }
    }
}
