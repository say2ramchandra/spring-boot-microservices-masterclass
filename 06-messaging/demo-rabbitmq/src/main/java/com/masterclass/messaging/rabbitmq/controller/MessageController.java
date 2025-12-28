package com.masterclass.messaging.rabbitmq.controller;

import com.masterclass.messaging.rabbitmq.producer.MessageProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

/**
 * REST Controller to trigger message sending
 */
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageProducer messageProducer;

    /**
     * Send order created event (Direct Exchange)
     */
    @PostMapping("/order")
    public ResponseEntity<Map<String, String>> sendOrderEvent(@RequestBody Map<String, Object> request) {
        String orderId = UUID.randomUUID().toString();
        String customerId = (String) request.get("customerId");
        Double amount = Double.valueOf(request.get("amount").toString());
        
        messageProducer.sendOrderCreated(orderId, customerId, amount);
        
        return ResponseEntity.ok(Map.of(
            "message", "Order event sent",
            "orderId", orderId,
            "exchange", "direct.exchange"
        ));
    }

    /**
     * Send broadcast notification (Fanout Exchange)
     */
    @PostMapping("/broadcast")
    public ResponseEntity<Map<String, String>> sendBroadcast(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        
        messageProducer.sendBroadcastNotification(message);
        
        return ResponseEntity.ok(Map.of(
            "message", "Broadcast sent to all consumers",
            "exchange", "fanout.exchange"
        ));
    }

    /**
     * Send notification (Topic Exchange)
     */
    @PostMapping("/notification/{type}")
    public ResponseEntity<Map<String, String>> sendNotification(
            @PathVariable String type,
            @RequestBody Map<String, String> request) {
        String message = request.get("message");
        
        messageProducer.sendNotification(type, message);
        
        return ResponseEntity.ok(Map.of(
            "message", "Notification sent",
            "type", type,
            "exchange", "topic.exchange",
            "routingKey", "notification." + type
        ));
    }
}
