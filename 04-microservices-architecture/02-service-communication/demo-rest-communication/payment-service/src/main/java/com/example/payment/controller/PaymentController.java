package com.example.payment.controller;

import com.example.payment.dto.PaymentRequest;
import com.example.payment.dto.PaymentResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    
    @PostMapping("/process")
    public ResponseEntity<PaymentResponse> processPayment(@RequestBody PaymentRequest request) {
        
        System.out.println("Processing payment: " + request);
        
        // Simulate payment processing
        try {
            Thread.sleep(1000); // Simulate processing time
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Validate payment
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            PaymentResponse response = new PaymentResponse(
                null,
                "FAILED",
                request.getAmount(),
                LocalDateTime.now(),
                "Invalid amount"
            );
            return ResponseEntity.badRequest().body(response);
        }
        
        if (request.getAmount().compareTo(BigDecimal.valueOf(10000)) > 0) {
            PaymentResponse response = new PaymentResponse(
                null,
                "FAILED",
                request.getAmount(),
                LocalDateTime.now(),
                "Amount exceeds limit"
            );
            return ResponseEntity.badRequest().body(response);
        }
        
        // Success
        PaymentResponse response = new PaymentResponse(
            UUID.randomUUID().toString(),
            "SUCCESS",
            request.getAmount(),
            LocalDateTime.now(),
            "Payment processed successfully"
        );
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/status/{transactionId}")
    public ResponseEntity<String> getPaymentStatus(@PathVariable String transactionId) {
        return ResponseEntity.ok("Payment status: COMPLETED for " + transactionId);
    }
}
