package com.masterclass.concurrency.demos;

import java.util.concurrent.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Demonstrates ExecutorService and thread pools
 */
public class ThreadPoolDemo {

    public static void demonstrate() throws Exception {
        System.out.println("\n1. Fixed Thread Pool:\n");
        demonstrateFixedThreadPool();

        System.out.println("\n2. Callable and Future:\n");
        demonstrateCallableAndFuture();

        System.out.println("\n3. Scheduled Tasks:\n");
        demonstrateScheduledTasks();

        System.out.println("\n4. invokeAll (Batch Execution):\n");
        demonstrateInvokeAll();
    }

    private static void demonstrateFixedThreadPool() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        
        System.out.println("   Submitting 5 tasks to pool of 3 threads:");
        
        for (int i = 1; i <= 5; i++) {
            final int taskId = i;
            executor.submit(() -> {
                System.out.println("   Task " + taskId + " started in " + 
                    Thread.currentThread().getName());
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                System.out.println("   Task " + taskId + " completed");
            });
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
        System.out.println("\n   ✅ All tasks completed!");
    }

    private static void demonstrateCallableAndFuture() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // Callable returns a value
        Callable<Integer> task1 = () -> {
            Thread.sleep(500);
            return 42;
        };

        Callable<String> task2 = () -> {
            Thread.sleep(300);
            return "Hello from Callable";
        };

        Future<Integer> future1 = executor.submit(task1);
        Future<String> future2 = executor.submit(task2);

        System.out.println("   Tasks submitted, waiting for results...");

        // Get blocks until result is available
        Integer result1 = future1.get();
        String result2 = future2.get();

        System.out.println("   Result 1: " + result1);
        System.out.println("   Result 2: " + result2);

        executor.shutdown();
    }

    private static void demonstrateScheduledTasks() throws InterruptedException {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

        System.out.println("   Scheduling tasks...");

        // Run once after delay
        scheduler.schedule(() -> {
            System.out.println("   ⏰ Task executed after 1 second delay");
        }, 1, TimeUnit.SECONDS);

        // Run periodically
        ScheduledFuture<?> periodicTask = scheduler.scheduleAtFixedRate(() -> {
            System.out.println("   🔄 Periodic task at " + System.currentTimeMillis());
        }, 0, 500, TimeUnit.MILLISECONDS);

        // Let it run for a bit
        Thread.sleep(2500);

        // Cancel periodic task
        periodicTask.cancel(false);
        scheduler.shutdown();
        
        System.out.println("   ✅ Scheduler stopped");
    }

    private static void demonstrateInvokeAll() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(3);

        List<Callable<String>> tasks = List.of(
            () -> { Thread.sleep(300); return "Task 1 result"; },
            () -> { Thread.sleep(200); return "Task 2 result"; },
            () -> { Thread.sleep(100); return "Task 3 result"; }
        );

        System.out.println("   Executing all tasks in parallel:");
        
        List<Future<String>> futures = executor.invokeAll(tasks);

        for (int i = 0; i < futures.size(); i++) {
            System.out.println("   Task " + (i + 1) + ": " + futures.get(i).get());
        }

        executor.shutdown();
    }
}
