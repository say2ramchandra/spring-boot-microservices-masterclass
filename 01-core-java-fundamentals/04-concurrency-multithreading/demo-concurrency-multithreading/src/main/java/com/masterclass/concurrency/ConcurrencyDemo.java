package com.masterclass.concurrency;

import com.masterclass.concurrency.demos.*;

/**
 * Comprehensive Concurrency & Multithreading Demo
 * 
 * Demonstrates:
 * 1. Thread basics and creation
 * 2. Synchronization problems and solutions
 * 3. ExecutorService and thread pools
 * 4. CompletableFuture async programming
 * 5. Concurrent collections
 * 6. Real-world scenarios
 */
public class ConcurrencyDemo {

    public static void main(String[] args) throws Exception {
        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║  Concurrency & Multithreading Demo          ║");
        System.out.println("╚══════════════════════════════════════════════╝\n");

        // 1. Thread Basics
        System.out.println("\n" + "=".repeat(50));
        System.out.println("PART 1: Thread Basics");
        System.out.println("=".repeat(50));
        ThreadBasicsDemo.demonstrate();

        // 2. Synchronization Problems
        System.out.println("\n" + "=".repeat(50));
        System.out.println("PART 2: Synchronization - Race Condition");
        System.out.println("=".repeat(50));
        RaceConditionDemo.demonstrate();

        // 3. Synchronization Solutions
        System.out.println("\n" + "=".repeat(50));
        System.out.println("PART 3: Synchronization Solutions");
        System.out.println("=".repeat(50));
        SynchronizationDemo.demonstrate();

        // 4. Thread Pools
        System.out.println("\n" + "=".repeat(50));
        System.out.println("PART 4: ExecutorService & Thread Pools");
        System.out.println("=".repeat(50));
        ThreadPoolDemo.demonstrate();

        // 5. CompletableFuture
        System.out.println("\n" + "=".repeat(50));
        System.out.println("PART 5: CompletableFuture");
        System.out.println("=".repeat(50));
        CompletableFutureDemo.demonstrate();

        // 6. Concurrent Collections
        System.out.println("\n" + "=".repeat(50));
        System.out.println("PART 6: Concurrent Collections");
        System.out.println("=".repeat(50));
        ConcurrentCollectionsDemo.demonstrate();

        // 7. Producer-Consumer
        System.out.println("\n" + "=".repeat(50));
        System.out.println("PART 7: Producer-Consumer Pattern");
        System.out.println("=".repeat(50));
        ProducerConsumerDemo.demonstrate();

        // 8. Real-World Scenarios
        System.out.println("\n" + "=".repeat(50));
        System.out.println("PART 8: Real-World Scenarios");
        System.out.println("=".repeat(50));
        RealWorldScenarios.demonstrate();

        System.out.println("\n" + "=".repeat(50));
        System.out.println("Demo completed successfully!");
        System.out.println("=".repeat(50));
    }
}
