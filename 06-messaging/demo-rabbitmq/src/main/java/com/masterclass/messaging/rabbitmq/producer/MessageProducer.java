package com.masterclass.messaging.rabbitmq.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.masterclass.messaging.rabbitmq.config.RabbitMQConfig;
import com.masterclass.messaging.rabbitmq.model.OrderEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Message Producer
 * 
 * Sends messages to different exchanges demonstrating various patterns.
 */
@Service
public class MessageProducer {

    private static final Logger logger = LoggerFactory.getLogger(MessageProducer.class);
    
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public MessageProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.findAndRegisterModules();
    }

    /**
     * Send to Direct Exchange (One-to-One)
     * Message goes to specific queue based on routing key
     */
    public void sendOrderCreated(String orderId, String customerId, Double amount) {
        try {
            OrderEvent event = new OrderEvent(
                orderId, customerId, amount, "CREATED", LocalDateTime.now()
            );
            
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.DIRECT_EXCHANGE,
                RabbitMQConfig.ORDER_ROUTING_KEY,
                objectMapper.writeValueAsString(event)
            );
            
            logger.info("📤 [DIRECT] Sent Order Created: {}", orderId);
        } catch (Exception e) {
            logger.error("❌ Error sending order event", e);
        }
    }

    /**
     * Send to Fanout Exchange (Broadcast)
     * Message goes to ALL bound queues
     */
    public void sendBroadcastNotification(String message) {
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.FANOUT_EXCHANGE,
            "",  // Routing key ignored in fanout
            message
        );
        logger.info("📤 [FANOUT] Broadcast: {}", message);
    }

    /**
     * Send to Topic Exchange (Pattern Matching)
     * Message routed based on pattern
     */
    public void sendNotification(String type, String message) {
        String routingKey = "notification." + type; // notification.email, notification.sms, etc.
        
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.TOPIC_EXCHANGE,
            routingKey,
            message
        );
        logger.info("📤 [TOPIC] Sent to {}: {}", routingKey, message);
    }
}
