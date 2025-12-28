package com.masterclass.autoconfigure.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Email service that is conditionally created based on configuration.
 * 
 * This bean is created only when feature.email.enabled=true in application.properties.
 */
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    public EmailService() {
        logger.info("EmailService initialized");
    }

    public void sendEmail(String to, String subject, String body) {
        logger.info("Sending email:");
        logger.info("  To: {}", to);
        logger.info("  Subject: {}", subject);
        logger.info("  Body: {}", body);
    }
}
