package com.example.resilience.service;

import org.springframework.stereotype.Service;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simulates an external service with configurable failure rates
 */
@Service
public class ExternalService {
    
    private Random random = new Random();
    private AtomicInteger callCounter = new AtomicInteger(0);
    
    /**
     * Simulates a call that fails 70% of the time
     */
    public String unreliableCall() {
        int callNumber = callCounter.incrementAndGet();
        System.out.println("📞 External service call #" + callNumber);
        
        if (random.nextInt(100) < 70) {
            System.out.println("❌ External service failed (call #" + callNumber + ")");
            throw new RuntimeException("External service unavailable");
        }
        
        System.out.println("✅ External service succeeded (call #" + callNumber + ")");
        return "Success from external service";
    }
    
    /**
     * Simulates a slow call (takes 5 seconds)
     */
    public String slowCall() {
        System.out.println("🐌 Slow external service call started...");
        
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted");
        }
        
        System.out.println("✅ Slow external service call completed");
        return "Success from slow service";
    }
    
    /**
     * Simulates a call that fails initially but succeeds after retries
     */
    public String transientFailureCall() {
        int callNumber = callCounter.incrementAndGet();
        System.out.println("📞 Transient failure service call #" + callNumber);
        
        // Fail first 2 attempts, succeed on 3rd
        if (callNumber % 3 != 0) {
            System.out.println("❌ Transient failure (call #" + callNumber + ")");
            throw new RuntimeException("Transient failure");
        }
        
        System.out.println("✅ Service recovered (call #" + callNumber + ")");
        return "Success after transient failure";
    }
    
    /**
     * Simulates processing that takes variable time
     */
    public String variableProcessing() {
        int duration = random.nextInt(4) + 1;
        System.out.println("⏱️  Processing for " + duration + " seconds...");
        
        try {
            Thread.sleep(duration * 1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted");
        }
        
        return "Processed successfully";
    }
    
    public void resetCounter() {
        callCounter.set(0);
    }
}
