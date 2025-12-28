package com.masterclass.spring.service;

import com.masterclass.spring.repository.OrderRepository;
import org.springframework.stereotype.Service;

/**
 * OrderService - Demonstrates MULTIPLE DEPENDENCIES
 * 
 * This service requires:
 * 1. OrderRepository - for data access
 * 2. EmailService - for notifications
 * 3. PaymentService - for payment processing
 * 
 * All three are injected via constructor - clear and explicit!
 */
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final EmailService emailService;
    private final PaymentService paymentService;

    /**
     * Constructor with MULTIPLE dependencies
     * 
     * All dependencies injected by Spring automatically!
     */
    public OrderService(OrderRepository orderRepository,
                       EmailService emailService,
                       PaymentService paymentService) {
        this.orderRepository = orderRepository;
        this.emailService = emailService;
        this.paymentService = paymentService;
        System.out.println("  ✓ OrderService created with 3 dependencies injected");
    }

    /**
     * Business method using multiple injected services
     */
    public String placeOrder(Long userId, String product, Double amount) {
        System.out.println("\n[OrderService] Placing order...");
        
        // Use all injected dependencies
        String orderResult = orderRepository.save(userId, product, amount);
        paymentService.processPayment(amount);
        emailService.sendOrderConfirmation(userId, "user@example.com");
        
        return "✅ " + orderResult;
    }
}
