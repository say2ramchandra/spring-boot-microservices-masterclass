package com.masterclass.autoconfigure.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Notification service that is conditionally created based on configuration.
 * 
 * This bean is created only when feature.notification.enabled=true.
 */
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    public NotificationService() {
        logger.info("NotificationService initialized");
    }

    public void sendNotification(String userId, String message) {
        logger.info("Sending notification:");
        logger.info("  User ID: {}", userId);
        logger.info("  Message: {}", message);
    }
}
