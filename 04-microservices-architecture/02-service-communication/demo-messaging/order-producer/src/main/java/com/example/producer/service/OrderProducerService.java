package com.example.producer.service;

import com.example.producer.config.RabbitMQConfig;
import com.example.producer.dto.OrderMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderProducerService {
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    public void sendOrder(OrderMessage orderMessage) {
        
        System.out.println("📤 Sending order to RabbitMQ: " + orderMessage.getOrderId());
        
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.EXCHANGE_NAME,
            RabbitMQConfig.ROUTING_KEY,
            orderMessage
        );
        
        System.out.println("✅ Order sent successfully: " + orderMessage.getOrderId());
    }
}
