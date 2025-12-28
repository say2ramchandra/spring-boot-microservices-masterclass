package com.masterclass.spring.service;

import com.masterclass.spring.repository.OrderRepository;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

/**
 * OrderProcessingService - Real-world complex example
 * 
 * Demonstrates:
 * - Multiple service dependencies
 * - Business logic orchestration
 * - Transaction coordination
 * - Initialization with @PostConstruct
 * 
 * This is a typical real-world service structure!
 */
@Service
public class OrderProcessingService {

    private final OrderRepository orderRepository;
    private final PaymentService paymentService;
    private final EmailService emailService;
    private final NotificationService notificationService;

    /**
     * Constructor with FOUR dependencies
     * 
     * In real applications, services often need multiple dependencies
     * to orchestrate complex business logic.
     */
    public OrderProcessingService(OrderRepository orderRepository,
                                 PaymentService paymentService,
                                 EmailService emailService,
                                 NotificationService notificationService) {
        this.orderRepository = orderRepository;
        this.paymentService = paymentService;
        this.emailService = emailService;
        this.notificationService = notificationService;
        System.out.println("  ✓ OrderProcessingService created with 4 dependencies");
    }

    /**
     * Initialization method called after dependency injection
     * 
     * @PostConstruct runs after all dependencies are injected
     * Perfect for initialization logic that needs dependencies
     */
    @PostConstruct
    public void init() {
        System.out.println("  ✓ OrderProcessingService initialized!");
    }

    /**
     * Complex business method using all dependencies
     */
    public boolean processCompleteOrder(Long orderId, Double amount, String email) {
        System.out.println("\n[OrderProcessingService] Processing complete order #" + orderId);
        
        try {
            // Step 1: Save order
            System.out.println("\n  Step 1: Saving order...");
            orderRepository.save(orderId, "Product", amount);
            
            // Step 2: Process payment
            System.out.println("\n  Step 2: Processing payment...");
            boolean paymentSuccess = paymentService.processPayment(amount);
            if (!paymentSuccess) {
                throw new RuntimeException("Payment failed!");
            }
            
            // Step 3: Send confirmation email
            System.out.println("\n  Step 3: Sending confirmation...");
            emailService.sendOrderConfirmation(orderId, email);
            
            // Step 4: Send notification
            System.out.println("\n  Step 4: Sending notification...");
            notificationService.sendNotification(email, 
                "Your order #" + orderId + " is being processed!");
            
            return true;
            
        } catch (Exception e) {
            System.err.println("  ❌ Error processing order: " + e.getMessage());
            return false;
        }
    }
}
