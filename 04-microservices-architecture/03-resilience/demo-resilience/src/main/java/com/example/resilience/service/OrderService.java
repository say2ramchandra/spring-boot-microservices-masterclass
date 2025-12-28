package com.example.resilience.service;

import com.example.resilience.dto.OrderRequest;
import com.example.resilience.dto.OrderResponse;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class OrderService {
    
    @Autowired
    private ExternalService externalService;
    
    private Random random = new Random();
    
    // ==================== CIRCUIT BREAKER DEMO ====================
    
    @CircuitBreaker(name = "orderService", fallbackMethod = "createOrderFallback")
    public OrderResponse createOrderWithCircuitBreaker(OrderRequest request) {
        log.info("Creating order with circuit breaker...");
        
        // Call unreliable external service
        String result = externalService.unreliableCall();
        
        return new OrderResponse(
            random.nextLong(1000, 9999),
            "CONFIRMED",
            result,
            System.currentTimeMillis()
        );
    }
    
    public OrderResponse createOrderFallback(OrderRequest request, Exception e) {
        log.error("Circuit breaker fallback activated", e);
        
        return new OrderResponse(
            null,
            "FAILED",
            "Service temporarily unavailable. Please try again later.",
            System.currentTimeMillis()
        );
    }
    
    // ==================== RETRY DEMO ====================
    
    @Retry(name = "orderService", fallbackMethod = "processOrderFallback")
    public String processOrderWithRetry() {
        log.info("Processing order with retry...");
        
        // Reset counter for clean retry demo
        externalService.resetCounter();
        
        // Call service with transient failures
        return externalService.transientFailureCall();
    }
    
    public String processOrderFallback(Exception e) {
        log.error("All retry attempts exhausted", e);
        return "Failed after all retry attempts";
    }
    
    // ==================== TIMEOUT DEMO ====================
    
    @TimeLimiter(name = "orderService", fallbackMethod = "checkInventoryFallback")
    public CompletableFuture<String> checkInventoryWithTimeout() {
        log.info("Checking inventory with timeout...");
        
        return CompletableFuture.supplyAsync(() -> {
            // This will timeout (takes 5s, limit is 3s)
            return externalService.slowCall();
        });
    }
    
    public CompletableFuture<String> checkInventoryFallback(Exception e) {
        log.error("Inventory check timed out", e);
        return CompletableFuture.completedFuture("Inventory unavailable (timeout)");
    }
    
    // ==================== BULKHEAD DEMO ====================
    
    @Bulkhead(name = "orderService", type = Bulkhead.Type.SEMAPHORE, 
              fallbackMethod = "processBulkheadFallback")
    public String processWithBulkhead() {
        log.info("Processing with bulkhead (max 5 concurrent)...");
        
        // Simulate processing
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        return "Processed successfully";
    }
    
    public String processBulkheadFallback(Exception e) {
        log.error("Bulkhead limit reached", e);
        return "Too many concurrent requests. Please try again.";
    }
    
    // ==================== COMBINED PATTERNS DEMO ====================
    
    @CircuitBreaker(name = "orderService", fallbackMethod = "combinedFallback")
    @Retry(name = "orderService")
    @TimeLimiter(name = "orderService")
    @Bulkhead(name = "orderService")
    public CompletableFuture<OrderResponse> createOrderWithAllPatterns(OrderRequest request) {
        log.info("Creating order with all resilience patterns...");
        
        return CompletableFuture.supplyAsync(() -> {
            String result = externalService.variableProcessing();
            
            return new OrderResponse(
                random.nextLong(1000, 9999),
                "CONFIRMED",
                result,
                System.currentTimeMillis()
            );
        });
    }
    
    public CompletableFuture<OrderResponse> combinedFallback(OrderRequest request, Exception e) {
        log.error("Combined patterns fallback activated", e);
        
        return CompletableFuture.completedFuture(new OrderResponse(
            null,
            "FAILED",
            "Service unavailable: " + e.getMessage(),
            System.currentTimeMillis()
        ));
    }
}
