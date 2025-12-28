package com.masterclass.messaging.rabbitmq.consumer;

import com.masterclass.messaging.rabbitmq.config.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Message Consumers
 * 
 * Listen to different queues and process messages.
 */
@Component
public class MessageConsumer {

    private static final Logger logger = LoggerFactory.getLogger(MessageConsumer.class);

    /**
     * Listen to Order Queue (Direct Exchange)
     */
    @RabbitListener(queues = RabbitMQConfig.ORDER_QUEUE)
    public void handleOrderCreated(String message) {
        logger.info("📥 [ORDER CONSUMER] Received: {}", message);
        // Process order - update database, send confirmation, etc.
        logger.info("✅ [ORDER CONSUMER] Order processed successfully");
    }

    /**
     * Listen to Email Queue (Fanout Exchange)
     */
    @RabbitListener(queues = RabbitMQConfig.EMAIL_QUEUE)
    public void handleEmailNotification(String message) {
        logger.info("📧 [EMAIL CONSUMER] Received: {}", message);
        // Send email
        logger.info("✅ [EMAIL CONSUMER] Email sent");
    }

    /**
     * Listen to SMS Queue (Fanout Exchange)
     */
    @RabbitListener(queues = RabbitMQConfig.SMS_QUEUE)
    public void handleSmsNotification(String message) {
        logger.info("📱 [SMS CONSUMER] Received: {}", message);
        // Send SMS
        logger.info("✅ [SMS CONSUMER] SMS sent");
    }

    /**
     * Listen to Notification Queue (Topic Exchange)
     */
    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE)
    public void handleNotification(String message) {
        logger.info("🔔 [NOTIFICATION CONSUMER] Received: {}", message);
        // Process notification
        logger.info("✅ [NOTIFICATION CONSUMER] Notification processed");
    }
}
