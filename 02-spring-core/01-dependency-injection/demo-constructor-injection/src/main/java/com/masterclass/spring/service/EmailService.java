package com.masterclass.spring.service;

import org.springframework.stereotype.Service;

/**
 * EmailService - Handles email notifications
 */
@Service
public class EmailService {

    public EmailService() {
        System.out.println("  ✓ EmailService created");
    }

    public void sendEmail(String to, String subject, String body) {
        System.out.println("  [EmailService] Sending email...");
        System.out.println("    To: " + to);
        System.out.println("    Subject: " + subject);
        System.out.println("    ✅ Email sent successfully!");
    }

    public void sendOrderConfirmation(Long orderId, String email) {
        System.out.println("  [EmailService] Sending order confirmation...");
        sendEmail(email, 
                 "Order Confirmation #" + orderId, 
                 "Your order has been confirmed!");
    }
}
