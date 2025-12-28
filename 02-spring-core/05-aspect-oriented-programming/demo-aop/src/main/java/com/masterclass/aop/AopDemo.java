package com.masterclass.aop;

import com.masterclass.aop.config.AopConfig;
import com.masterclass.aop.service.UserService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * AOP Demo
 * 
 * Demonstrates:
 * 1. @Before advice
 * 2. @After advice
 * 3. @AfterReturning advice
 * 4. @AfterThrowing advice
 * 5. @Around advice (performance monitoring)
 * 6. Pointcut expressions
 * 7. Combining pointcuts
 * 8. Real-world aspects (logging, security, performance)
 */
public class AopDemo {

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║      Aspect-Oriented Programming Demo       ║");
        System.out.println("╚══════════════════════════════════════════════╝\n");

        try (AnnotationConfigApplicationContext context = 
                new AnnotationConfigApplicationContext(AopConfig.class)) {
            
            UserService userService = context.getBean(UserService.class);

            demonstrateBeforeAdvice(userService);
            demonstrateAfterReturningAdvice(userService);
            demonstrateAfterAdvice(userService);
            demonstrateAfterThrowingAdvice(userService);
            demonstrateAroundAdvice(userService);
            demonstrateCombinedPointcuts(userService);
        }

        System.out.println("\n" + "=".repeat(50));
        System.out.println("Demo completed successfully!");
        System.out.println("=".repeat(50));
    }

    private static void demonstrateBeforeAdvice(UserService userService) {
        System.out.println("=".repeat(50));
        System.out.println("PART 1: @Before Advice");
        System.out.println("=".repeat(50));
        System.out.println("Aspect executes BEFORE the method\n");

        userService.createUser("alice");
        
        System.out.println();
    }

    private static void demonstrateAfterReturningAdvice(UserService userService) {
        System.out.println("=".repeat(50));
        System.out.println("PART 2: @AfterReturning Advice");
        System.out.println("=".repeat(50));
        System.out.println("Aspect executes AFTER successful return\n");

        String result = userService.getUser(123L);
        System.out.println("   [Main] Result received: " + result);
        
        System.out.println();
    }

    private static void demonstrateAfterAdvice(UserService userService) {
        System.out.println("=".repeat(50));
        System.out.println("PART 3: @After Advice");
        System.out.println("=".repeat(50));
        System.out.println("Aspect executes AFTER method (always)\n");

        userService.updateUser(456L, "bob");
        
        System.out.println();
    }

    private static void demonstrateAfterThrowingAdvice(UserService userService) {
        System.out.println("=".repeat(50));
        System.out.println("PART 4: @AfterThrowing Advice");
        System.out.println("=".repeat(50));
        System.out.println("Aspect executes when exception is thrown\n");

        try {
            userService.deleteUser(999L);
        } catch (Exception e) {
            System.out.println("   [Main] Exception caught: " + e.getMessage());
        }
        
        System.out.println();
    }

    private static void demonstrateAroundAdvice(UserService userService) {
        System.out.println("=".repeat(50));
        System.out.println("PART 5: @Around Advice (Performance Monitoring)");
        System.out.println("=".repeat(50));
        System.out.println("Aspect wraps method execution\n");

        int discount = userService.calculateDiscount(100);
        System.out.println("   [Main] Discount calculated: " + discount);
        
        System.out.println();
    }

    private static void demonstrateCombinedPointcuts(UserService userService) {
        System.out.println("=".repeat(50));
        System.out.println("PART 6: Combined Pointcuts (Security)");
        System.out.println("=".repeat(50));
        System.out.println("Security aspect for update and delete methods\n");

        System.out.println("1. Update operation:");
        userService.updateUser(789L, "charlie");

        System.out.println("\n2. Delete operation:");
        userService.deleteUser(100L);
        
        System.out.println();
    }
}
