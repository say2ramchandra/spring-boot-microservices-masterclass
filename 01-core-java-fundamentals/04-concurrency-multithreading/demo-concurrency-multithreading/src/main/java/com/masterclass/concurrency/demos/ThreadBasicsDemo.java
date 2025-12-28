package com.masterclass.concurrency.demos;

/**
 * Demonstrates basic thread creation methods
 */
public class ThreadBasicsDemo {

    public static void demonstrate() throws InterruptedException {
        System.out.println("\n1. Creating Threads - Three Methods:\n");

        // Method 1: Extending Thread class
        Thread thread1 = new MyThread("Thread-1");
        thread1.start();
        thread1.join();

        // Method 2: Implementing Runnable
        Thread thread2 = new Thread(new MyRunnable("Thread-2"));
        thread2.start();
        thread2.join();

        // Method 3: Lambda expression (Modern)
        Thread thread3 = new Thread(() -> {
            System.out.println("   [Lambda] Running in: " + 
                Thread.currentThread().getName());
        }, "Thread-3");
        thread3.start();
        thread3.join();

        System.out.println("\n2. Thread Methods:\n");
        demonstrateThreadMethods();

        System.out.println("\n3. Thread States:\n");
        demonstrateThreadStates();
    }

    private static void demonstrateThreadMethods() throws InterruptedException {
        Thread worker = new Thread(() -> {
            System.out.println("   Name: " + Thread.currentThread().getName());
            System.out.println("   Priority: " + Thread.currentThread().getPriority());
            System.out.println("   Is daemon: " + Thread.currentThread().isDaemon());
            
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        worker.setName("WorkerThread");
        worker.setPriority(Thread.MAX_PRIORITY);
        worker.setDaemon(false);

        System.out.println("   Thread created: " + worker.getName());
        System.out.println("   Is alive before start: " + worker.isAlive());
        
        worker.start();
        System.out.println("   Is alive after start: " + worker.isAlive());
        
        worker.join();
        System.out.println("   Is alive after join: " + worker.isAlive());
    }

    private static void demonstrateThreadStates() throws InterruptedException {
        Thread thread = new Thread(() -> {
            synchronized(ThreadBasicsDemo.class) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        System.out.println("   State after creation: " + thread.getState()); // NEW
        thread.start();
        Thread.sleep(10);
        System.out.println("   State after start: " + thread.getState()); // RUNNABLE or TIMED_WAITING
        thread.join();
        System.out.println("   State after completion: " + thread.getState()); // TERMINATED
    }

    static class MyThread extends Thread {
        public MyThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            System.out.println("   [Extends Thread] Running in: " + getName());
        }
    }

    static class MyRunnable implements Runnable {
        private final String name;

        public MyRunnable(String name) {
            this.name = name;
        }

        @Override
        public void run() {
            System.out.println("   [Implements Runnable] Running in: " + 
                Thread.currentThread().getName() + " (task: " + name + ")");
        }
    }
}
