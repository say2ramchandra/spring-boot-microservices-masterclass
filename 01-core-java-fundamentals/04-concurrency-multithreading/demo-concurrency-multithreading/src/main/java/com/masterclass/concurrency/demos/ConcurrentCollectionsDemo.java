package com.masterclass.concurrency.demos;

import java.util.concurrent.*;
import java.util.Map;

/**
 * Demonstrates thread-safe concurrent collections
 */
public class ConcurrentCollectionsDemo {

    public static void demonstrate() throws InterruptedException {
        System.out.println("\n1. ConcurrentHashMap:\n");
        demonstrateConcurrentHashMap();

        System.out.println("\n2. CopyOnWriteArrayList:\n");
        demonstrateCopyOnWriteArrayList();

        System.out.println("\n3. ConcurrentLinkedQueue:\n");
        demonstrateConcurrentQueue();
    }

    private static void demonstrateConcurrentHashMap() {
        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();

        // Atomic operations
        map.put("key1", 1);
        map.putIfAbsent("key1", 2); // Won't overwrite
        System.out.println("   key1: " + map.get("key1"));

        map.computeIfAbsent("key2", k -> 10);
        System.out.println("   key2: " + map.get("key2"));

        map.computeIfPresent("key1", (k, v) -> v + 1);
        System.out.println("   key1 incremented: " + map.get("key1"));

        // Atomic increment
        map.merge("key1", 1, Integer::sum);
        System.out.println("   key1 after merge: " + map.get("key1"));

        System.out.println("   ✅ All operations are thread-safe!");
    }

    private static void demonstrateCopyOnWriteArrayList() {
        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>();
        
        list.add("Item 1");
        list.add("Item 2");

        System.out.println("   Original list: " + list);

        // Safe iteration even during modification
        for (String item : list) {
            System.out.println("   Reading: " + item);
            list.add("Item 3"); // Safe to modify during iteration!
        }

        System.out.println("   After iteration: " + list);
        System.out.println("   ✅ No ConcurrentModificationException!");
    }

    private static void demonstrateConcurrentQueue() {
        ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();

        // Thread-safe operations
        queue.offer("Element 1");
        queue.offer("Element 2");
        queue.offer("Element 3");

        System.out.println("   Queue size: " + queue.size());
        System.out.println("   Peek: " + queue.peek());
        System.out.println("   Poll: " + queue.poll());
        System.out.println("   Queue after poll: " + queue);
        System.out.println("   ✅ Lock-free, thread-safe queue!");
    }
}
