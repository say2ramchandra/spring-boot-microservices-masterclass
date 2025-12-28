package com.masterclass.concurrency.demos;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Demonstrates solutions to race conditions
 */
public class SynchronizationDemo {

    public static void demonstrate() throws InterruptedException {
        System.out.println("\n1. Solution 1: Synchronized Method\n");
        demonstrateSynchronizedMethod();

        System.out.println("\n2. Solution 2: Synchronized Block\n");
        demonstrateSynchronizedBlock();

        System.out.println("\n3. Solution 3: ReentrantLock\n");
        demonstrateReentrantLock();

        System.out.println("\n4. Solution 4: AtomicInteger (Best for counters)\n");
        demonstrateAtomic();
    }

    private static void demonstrateSynchronizedMethod() throws InterruptedException {
        SynchronizedCounter counter = new SynchronizedCounter();
        
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) counter.increment();
        });
        
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) counter.increment();
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println("   Result: " + counter.getCount());
        System.out.println("   ✅ Thread-safe!");
    }

    private static void demonstrateSynchronizedBlock() throws InterruptedException {
        BlockSynchronizedCounter counter = new BlockSynchronizedCounter();
        
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) counter.increment();
        });
        
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) counter.increment();
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println("   Result: " + counter.getCount());
        System.out.println("   ✅ Thread-safe with finer-grained locking!");
    }

    private static void demonstrateReentrantLock() throws InterruptedException {
        LockCounter counter = new LockCounter();
        
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) counter.increment();
        });
        
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) counter.increment();
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println("   Result: " + counter.getCount());
        System.out.println("   ✅ Thread-safe with explicit locking!");
    }

    private static void demonstrateAtomic() throws InterruptedException {
        AtomicCounter counter = new AtomicCounter();
        
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) counter.increment();
        });
        
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) counter.increment();
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println("   Result: " + counter.getCount());
        System.out.println("   ✅ Lock-free, thread-safe, best performance!");
    }

    // Solution 1: Synchronized method
    static class SynchronizedCounter {
        private int count = 0;

        public synchronized void increment() {
            count++;
        }

        public synchronized int getCount() {
            return count;
        }
    }

    // Solution 2: Synchronized block
    static class BlockSynchronizedCounter {
        private int count = 0;
        private final Object lock = new Object();

        public void increment() {
            synchronized(lock) {
                count++;
            }
        }

        public int getCount() {
            synchronized(lock) {
                return count;
            }
        }
    }

    // Solution 3: ReentrantLock
    static class LockCounter {
        private int count = 0;
        private final Lock lock = new ReentrantLock();

        public void increment() {
            lock.lock();
            try {
                count++;
            } finally {
                lock.unlock();
            }
        }

        public int getCount() {
            lock.lock();
            try {
                return count;
            } finally {
                lock.unlock();
            }
        }
    }

    // Solution 4: Atomic
    static class AtomicCounter {
        private AtomicInteger count = new AtomicInteger(0);

        public void increment() {
            count.incrementAndGet();
        }

        public int getCount() {
            return count.get();
        }
    }
}
