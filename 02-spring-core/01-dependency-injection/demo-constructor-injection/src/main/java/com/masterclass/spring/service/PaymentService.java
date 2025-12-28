package com.masterclass.spring.service;

import org.springframework.stereotype.Service;

/**
 * PaymentService - Handles payment processing
 */
@Service
public class PaymentService {

    public PaymentService() {
        System.out.println("  ✓ PaymentService created");
    }

    public boolean processPayment(Double amount) {
        System.out.println("  [PaymentService] Processing payment...");
        System.out.println("    Amount: $" + String.format("%.2f", amount));
        
        // Simulate payment processing
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println("    ✅ Payment successful!");
        return true;
    }
}
