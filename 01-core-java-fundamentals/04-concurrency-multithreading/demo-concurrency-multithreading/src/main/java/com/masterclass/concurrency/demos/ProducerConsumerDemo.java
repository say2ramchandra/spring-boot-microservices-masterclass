package com.masterclass.concurrency.demos;

import java.util.concurrent.*;

/**
 * Demonstrates Producer-Consumer pattern using BlockingQueue
 */
public class ProducerConsumerDemo {

    public static void demonstrate() throws InterruptedException {
        BlockingQueue<String> queue = new ArrayBlockingQueue<>(5);

        // Producer thread
        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= 10; i++) {
                    String item = "Item-" + i;
                    System.out.println("   [Producer] Producing: " + item);
                    queue.put(item); // Blocks if queue is full
                    Thread.sleep(100);
                }
                queue.put("DONE"); // Signal completion
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // Consumer thread
        Thread consumer = new Thread(() -> {
            try {
                while (true) {
                    String item = queue.take(); // Blocks if queue is empty
                    if ("DONE".equals(item)) {
                        System.out.println("   [Consumer] Received termination signal");
                        break;
                    }
                    System.out.println("   [Consumer] Consuming: " + item);
                    Thread.sleep(200); // Simulate processing
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        System.out.println("   Starting producer and consumer...\n");
        producer.start();
        consumer.start();

        producer.join();
        consumer.join();

        System.out.println("\n   ✅ Producer-Consumer completed!");
    }
}
