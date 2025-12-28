package com.masterclass.concurrency.demos;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Demonstrates CompletableFuture for async programming
 */
public class CompletableFutureDemo {

    public static void demonstrate() throws Exception {
        System.out.println("\n1. Basic Async Execution:\n");
        demonstrateBasicAsync();

        System.out.println("\n2. Chaining Operations:\n");
        demonstrateChaining();

        System.out.println("\n3. Combining Multiple Futures:\n");
        demonstrateCombining();

        System.out.println("\n4. Error Handling:\n");
        demonstrateErrorHandling();

        System.out.println("\n5. Real-World Scenario:\n");
        demonstrateRealWorld();
    }

    private static void demonstrateBasicAsync() throws Exception {
        // supplyAsync - returns a value
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
            System.out.println("   Task running in: " + Thread.currentThread().getName());
            return "Hello from async task";
        });

        String result = future1.get();
        System.out.println("   Result: " + result);

        // runAsync - no return value
        CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> {
            System.out.println("   Side effect task completed");
        });

        future2.get();
    }

    private static void demonstrateChaining() throws Exception {
        String result = CompletableFuture
            .supplyAsync(() -> {
                System.out.println("   Step 1: Fetch data");
                return "Data";
            })
            .thenApply(data -> {
                System.out.println("   Step 2: Transform - " + data);
                return data.toUpperCase();
            })
            .thenApply(data -> {
                System.out.println("   Step 3: Process - " + data);
                return data + " processed";
            })
            .thenApply(data -> {
                System.out.println("   Step 4: Finalize - " + data);
                return data + "!";
            })
            .get();

        System.out.println("   Final result: " + result);
    }

    private static void demonstrateCombining() throws Exception {
        // Two independent tasks
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
            sleep(500);
            System.out.println("   Task 1 completed");
            return "Hello";
        });

        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
            sleep(300);
            System.out.println("   Task 2 completed");
            return "World";
        });

        // Combine results
        CompletableFuture<String> combined = future1.thenCombine(future2, 
            (s1, s2) -> s1 + " " + s2);

        System.out.println("   Combined: " + combined.get());

        // Wait for all
        CompletableFuture<Void> allOf = CompletableFuture.allOf(future1, future2);
        allOf.get();
        System.out.println("   ✅ All tasks completed");
    }

    private static void demonstrateErrorHandling() throws Exception {
        // Successful case
        String result1 = CompletableFuture
            .supplyAsync(() -> "Success")
            .exceptionally(ex -> "Fallback value")
            .get();
        System.out.println("   Result 1: " + result1);

        // Error case
        String result2 = CompletableFuture
            .supplyAsync(() -> {
                if (true) throw new RuntimeException("Error!");
                return "Won't reach here";
            })
            .exceptionally(ex -> {
                System.out.println("   Caught: " + ex.getMessage());
                return "Recovered from error";
            })
            .get();
        System.out.println("   Result 2: " + result2);

        // Handle (works for both success and error)
        String result3 = CompletableFuture
            .supplyAsync(() -> "Data")
            .handle((result, ex) -> {
                if (ex != null) {
                    return "Error handled";
                }
                return result + " processed";
            })
            .get();
        System.out.println("   Result 3: " + result3);
    }

    private static void demonstrateRealWorld() throws Exception {
        System.out.println("   Simulating parallel API calls:");

        long startTime = System.currentTimeMillis();

        // Three independent API calls
        CompletableFuture<String> userFuture = CompletableFuture.supplyAsync(() -> {
            sleep(500);
            return "User{id=1, name=John}";
        });

        CompletableFuture<String> ordersFuture = CompletableFuture.supplyAsync(() -> {
            sleep(700);
            return "Orders[Order1, Order2]";
        });

        CompletableFuture<String> recommendationsFuture = CompletableFuture.supplyAsync(() -> {
            sleep(300);
            return "Recommendations[Prod1, Prod2]";
        });

        // Combine all results
        CompletableFuture<String> dashboardFuture = userFuture
            .thenCombine(ordersFuture, (user, orders) -> user + ", " + orders)
            .thenCombine(recommendationsFuture, (combined, recs) -> 
                "Dashboard{" + combined + ", " + recs + "}");

        String dashboard = dashboardFuture.get();
        
        long endTime = System.currentTimeMillis();
        
        System.out.println("   Result: " + dashboard);
        System.out.println("   Time taken: " + (endTime - startTime) + "ms");
        System.out.println("   (Sequential would take ~1500ms, parallel took ~700ms)");
    }

    private static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
