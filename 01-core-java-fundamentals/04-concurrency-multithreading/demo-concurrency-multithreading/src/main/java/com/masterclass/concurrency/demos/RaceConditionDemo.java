package com.masterclass.concurrency.demos;

/**
 * Demonstrates the race condition problem
 */
public class RaceConditionDemo {

    private static int counter = 0;

    public static void demonstrate() throws InterruptedException {
        System.out.println("\n1. Race Condition Problem:\n");
        
        counter = 0;
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                counter++; // NOT thread-safe!
            }
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                counter++; // NOT thread-safe!
            }
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println("   Expected: 2000");
        System.out.println("   Actual: " + counter);
        System.out.println("   ⚠️  Lost updates due to race condition!");
        
        System.out.println("\n   Why? counter++ is actually 3 operations:");
        System.out.println("   1. Read value from memory");
        System.out.println("   2. Increment value");
        System.out.println("   3. Write value back to memory");
        System.out.println("   Multiple threads can interleave these operations!");
    }
}
