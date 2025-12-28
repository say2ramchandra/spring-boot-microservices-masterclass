package com.example.resilience.controller;

import com.example.resilience.dto.OrderRequest;
import com.example.resilience.dto.OrderResponse;
import com.example.resilience.service.OrderService;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/orders")
@Slf4j
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;
    
    // ==================== CIRCUIT BREAKER TEST ====================
    
    @GetMapping("/circuit-breaker-test")
    public ResponseEntity<String> testCircuitBreaker() {
        log.info("Testing circuit breaker...");
        
        try {
            OrderResponse response = orderService.createOrderWithCircuitBreaker(
                new OrderRequest(1L, 100L, 5)
            );
            return ResponseEntity.ok("Success: " + response.getMessage());
            
        } catch (Exception e) {
            return ResponseEntity.status(503)
                .body("Circuit breaker test failed: " + e.getMessage());
        }
    }
    
    @GetMapping("/circuit-breaker-state")
    public ResponseEntity<String> getCircuitBreakerState() {
        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker("orderService");
        
        String state = String.format(
            "Circuit Breaker State: %s\n" +
            "Failure Rate: %.2f%%\n" +
            "Slow Call Rate: %.2f%%\n" +
            "Buffered Calls: %d\n" +
            "Failed Calls: %d\n" +
            "Slow Calls: %d",
            cb.getState(),
            cb.getMetrics().getFailureRate(),
            cb.getMetrics().getSlowCallRate(),
            cb.getMetrics().getNumberOfBufferedCalls(),
            cb.getMetrics().getNumberOfFailedCalls(),
            cb.getMetrics().getNumberOfSlowCalls()
        );
        
        return ResponseEntity.ok(state);
    }
    
    // ==================== RETRY TEST ====================
    
    @GetMapping("/retry-test")
    public ResponseEntity<String> testRetry() {
        log.info("Testing retry pattern...");
        
        try {
            String result = orderService.processOrderWithRetry();
            return ResponseEntity.ok("Retry succeeded: " + result);
            
        } catch (Exception e) {
            return ResponseEntity.status(503)
                .body("Retry test failed: " + e.getMessage());
        }
    }
    
    // ==================== TIMEOUT TEST ====================
    
    @GetMapping("/timeout-test")
    public ResponseEntity<String> testTimeout() {
        log.info("Testing timeout pattern...");
        
        try {
            CompletableFuture<String> result = orderService.checkInventoryWithTimeout();
            String response = result.join();
            return ResponseEntity.ok("Timeout test result: " + response);
            
        } catch (Exception e) {
            return ResponseEntity.status(503)
                .body("Timeout test failed: " + e.getMessage());
        }
    }
    
    // ==================== BULKHEAD TEST ====================
    
    @GetMapping("/bulkhead-test")
    public ResponseEntity<String> testBulkhead() {
        log.info("Testing bulkhead pattern...");
        
        try {
            String result = orderService.processWithBulkhead();
            return ResponseEntity.ok("Bulkhead test: " + result);
            
        } catch (Exception e) {
            return ResponseEntity.status(503)
                .body("Bulkhead test failed: " + e.getMessage());
        }
    }
    
    // ==================== COMBINED PATTERNS TEST ====================
    
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest request) {
        log.info("Creating order with all patterns: {}", request);
        
        try {
            CompletableFuture<OrderResponse> futureResponse = 
                orderService.createOrderWithAllPatterns(request);
            
            OrderResponse response = futureResponse.join();
            
            if ("CONFIRMED".equals(response.getStatus())) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(503).body(response);
            }
            
        } catch (Exception e) {
            log.error("Order creation failed", e);
            
            return ResponseEntity.status(503).body(new OrderResponse(
                null,
                "FAILED",
                "Order creation failed: " + e.getMessage(),
                System.currentTimeMillis()
            ));
        }
    }
}
