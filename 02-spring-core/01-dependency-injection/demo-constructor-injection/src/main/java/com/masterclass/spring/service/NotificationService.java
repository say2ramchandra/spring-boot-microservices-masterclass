package com.masterclass.spring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * NotificationService - Demonstrates EXPLICIT @Autowired
 * 
 * While @Autowired is optional for single constructor,
 * you can still use it explicitly for clarity.
 * 
 * Note: If you have MULTIPLE constructors, you MUST annotate
 * the one you want Spring to use with @Autowired.
 */
@Service
public class NotificationService {

    private final EmailService emailService;

    /**
     * Constructor with EXPLICIT @Autowired annotation
     * 
     * This is optional but can make intent clearer.
     */
    @Autowired  // Optional, but explicit
    public NotificationService(EmailService emailService) {
        this.emailService = emailService;
        System.out.println("  ✓ NotificationService created (explicit @Autowired)");
    }

    public void sendNotification(String recipient, String message) {
        System.out.println("\n[NotificationService] Sending notification...");
        emailService.sendEmail(recipient, "Notification", message);
    }
}
