package com.masterclass.concurrency.demos;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.concurrent.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Demonstrates real-world concurrency scenarios
 */
public class RealWorldScenarios {

    public static void demonstrate() throws Exception {
        System.out.println("\n1. Parallel Data Processing:\n");
        demonstrateParallelProcessing();

        System.out.println("\n2. Web Scraping (Parallel Downloads):\n");
        demonstrateParallelDownloads();

        System.out.println("\n3. Rate Limiting:\n");
        demonstrateRateLimiting();
    }

    private static void demonstrateParallelProcessing() throws Exception {
        List<Integer> numbers = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            numbers.add(i);
        }

        ExecutorService executor = Executors.newFixedThreadPool(4);

        System.out.println("   Processing 100 numbers in parallel:");
        
        List<Future<Long>> futures = new ArrayList<>();
        
        for (int i = 0; i < numbers.size(); i += 25) {
            final int start = i;
            final int end = Math.min(i + 25, numbers.size());
            
            futures.add(executor.submit(() -> {
                long sum = 0;
                for (int j = start; j < end; j++) {
                    sum += numbers.get(j);
                }
                return sum;
            }));
        }

        long totalSum = 0;
        for (Future<Long> future : futures) {
            totalSum += future.get();
        }

        System.out.println("   Total sum: " + totalSum);
        System.out.println("   ✅ Parallel processing completed!");

        executor.shutdown();
    }

    private static void demonstrateParallelDownloads() throws Exception {
        List<String> urls = List.of(
            "https://api.example.com/users",
            "https://api.example.com/products",
            "https://api.example.com/orders"
        );

        ExecutorService executor = Executors.newFixedThreadPool(3);

        System.out.println("   Downloading from " + urls.size() + " URLs in parallel:");

        long startTime = System.currentTimeMillis();

        List<CompletableFuture<DownloadResult>> futures = urls.stream()
            .map(url -> CompletableFuture.supplyAsync(() -> {
                // Simulate download
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return new DownloadResult(url, "Data from " + url);
            }, executor))
            .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();

        long endTime = System.currentTimeMillis();

        futures.forEach(future -> {
            try {
                DownloadResult result = future.get();
                System.out.println("   ✓ Downloaded: " + result.getUrl());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        System.out.println("   Time taken: " + (endTime - startTime) + "ms");
        System.out.println("   (Sequential would take ~1500ms)");

        executor.shutdown();
    }

    private static void demonstrateRateLimiting() throws Exception {
        // Semaphore for rate limiting (max 2 concurrent operations)
        Semaphore semaphore = new Semaphore(2);
        ExecutorService executor = Executors.newFixedThreadPool(5);

        System.out.println("   Rate limited to 2 concurrent operations:");

        for (int i = 1; i <= 6; i++) {
            final int taskId = i;
            executor.submit(() -> {
                try {
                    semaphore.acquire(); // Wait for permit
                    System.out.println("   Task " + taskId + " started");
                    Thread.sleep(1000);
                    System.out.println("   Task " + taskId + " completed");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    semaphore.release(); // Release permit
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        System.out.println("   ✅ All tasks completed with rate limiting!");
    }

    @Data
    @AllArgsConstructor
    static class DownloadResult {
        private String url;
        private String data;
    }
}
