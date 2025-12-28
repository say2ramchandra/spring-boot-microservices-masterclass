package com.masterclass.controller;

import com.masterclass.service.LoggingDemoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Logging Demo Controller
 * Demonstrates various logging scenarios
 */
@RestController
@RequestMapping("/api/demo")
@RequiredArgsConstructor
@Slf4j
public class LoggingDemoController {

    private final LoggingDemoService service;

    /**
     * Demonstrate all log levels
     * GET /api/demo/all-levels
     */
    @GetMapping("/all-levels")
    public ResponseEntity<Map<String, String>> demonstrateAllLevels() {
        log.trace("TRACE: Most detailed - method entry/exit");
        log.debug("DEBUG: Detailed developer information");
        log.info("INFO: General informational messages");
        log.warn("WARN: Potentially harmful situations");
        log.error("ERROR: Error events");
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Check logs to see all levels");
        response.put("instruction", "Look at console and logs/ directory");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Demonstrate parameterized logging
     * GET /api/demo/with-params?name=John&age=30
     */
    @GetMapping("/with-params")
    public ResponseEntity<Map<String, String>> demonstrateParameterizedLogging(
            @RequestParam(defaultValue = "Guest") String name,
            @RequestParam(defaultValue = "25") Integer age) {
        
        // ✅ Good - Parameterized logging (efficient)
        log.info("Processing request for user: {} with age: {}", name, age);
        log.debug("Request parameters - name: {}, age: {}", name, age);
        
        // ❌ Bad example (commented out - don't do this!)
        // log.info("Processing request for user: " + name + " with age: " + age);
        
        service.processUser(name, age);
        
        Map<String, String> response = new HashMap<>();
        response.put("name", name);
        response.put("age", age.toString());
        response.put("message", "Logged with parameters");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Demonstrate exception logging
     * GET /api/demo/with-exception?shouldFail=true
     */
    @GetMapping("/with-exception")
    public ResponseEntity<Map<String, String>> demonstrateExceptionLogging(
            @RequestParam(defaultValue = "false") boolean shouldFail) {
        
        log.info("Starting operation that might fail: {}", shouldFail);
        
        try {
            if (shouldFail) {
                throw new RuntimeException("Simulated failure for logging demo");
            }
            
            service.riskyOperation();
            
            log.info("Operation completed successfully");
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Operation completed without errors");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            // ✅ Good - Log exception with context
            log.error("Operation failed with error: {}", e.getMessage(), e);
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Check error.log for details");
            
            return ResponseEntity.ok(response);
        }
    }

    /**
     * Demonstrate MDC (Mapped Diagnostic Context)
     * GET /api/demo/with-mdc
     * 
     * Add header: X-User-Id: user123
     */
    @GetMapping("/with-mdc")
    public ResponseEntity<Map<String, String>> demonstrateMdc() {
        log.info("This log includes MDC context (requestId and userId)");
        
        service.performOperationWithMdc();
        
        log.info("MDC context is automatically included in all logs");
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Check logs - requestId and userId are automatically included");
        response.put("tip", "Add header 'X-User-Id: yourUserId' to see userId in logs");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Demonstrate business logic logging
     * POST /api/demo/create-order
     */
    @PostMapping("/create-order")
    public ResponseEntity<Map<String, String>> demonstrateBusinessLogging(
            @RequestBody Map<String, Object> orderData) {
        
        log.info("Creating order with data: {}", orderData);
        
        try {
            String orderId = service.createOrder(orderData);
            
            log.info("Order created successfully with ID: {}", orderId);
            
            Map<String, String> response = new HashMap<>();
            response.put("orderId", orderId);
            response.put("status", "created");
            response.put("message", "Order logged at INFO level");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to create order", e);
            throw e;
        }
    }

    /**
     * Demonstrate conditional logging
     * GET /api/demo/conditional?verbose=true
     */
    @GetMapping("/conditional")
    public ResponseEntity<Map<String, String>> demonstrateConditionalLogging(
            @RequestParam(defaultValue = "false") boolean verbose) {
        
        log.info("Starting conditional logging demo");
        
        // Conditional logging based on verbosity
        if (log.isDebugEnabled() && verbose) {
            log.debug("Verbose mode enabled - showing detailed logs");
            log.debug("Processing with extra details...");
        }
        
        service.performOperation();
        
        if (log.isTraceEnabled()) {
            log.trace("Trace level enabled - very detailed logs");
        }
        
        Map<String, String> response = new HashMap<>();
        response.put("verbose", String.valueOf(verbose));
        response.put("message", "Conditional logging based on level and parameter");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Demonstrate performance logging
     * GET /api/demo/with-timing
     */
    @GetMapping("/with-timing")
    public ResponseEntity<Map<String, Object>> demonstrateTimingLogging() {
        long startTime = System.currentTimeMillis();
        
        log.info("Starting timed operation");
        
        service.slowOperation();
        
        long duration = System.currentTimeMillis() - startTime;
        log.info("Operation completed in {} ms", duration);
        
        if (duration > 1000) {
            log.warn("Operation took longer than expected: {} ms", duration);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("duration", duration);
        response.put("message", "Check logs for timing information");
        
        return ResponseEntity.ok(response);
    }
}
