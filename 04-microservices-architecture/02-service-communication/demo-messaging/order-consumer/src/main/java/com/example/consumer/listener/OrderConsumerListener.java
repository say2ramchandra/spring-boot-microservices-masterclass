package com.example.consumer.listener;

import com.example.consumer.config.RabbitMQConfig;
import com.example.consumer.dto.OrderMessage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class OrderConsumerListener {
    
    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void receiveOrder(OrderMessage orderMessage) {
        
        System.out.println("\n" + "=".repeat(50));
        System.out.println("📥 Received Order from RabbitMQ");
        System.out.println("=".repeat(50));
        System.out.println("Order ID: " + orderMessage.getOrderId());
        System.out.println("User ID: " + orderMessage.getUserId());
        System.out.println("Product ID: " + orderMessage.getProductId());
        System.out.println("Quantity: " + orderMessage.getQuantity());
        System.out.println("Amount: $" + orderMessage.getAmount());
        System.out.println("=".repeat(50));
        
        try {
            // Simulate order processing
            processOrder(orderMessage);
            
            System.out.println("✅ Order processed successfully: " + orderMessage.getOrderId());
            
        } catch (Exception e) {
            System.err.println("❌ Error processing order: " + orderMessage.getOrderId());
            e.printStackTrace();
            // In real scenarios, might send to DLQ or retry
        }
        
        System.out.println();
    }
    
    private void processOrder(OrderMessage orderMessage) throws InterruptedException {
        // Simulate processing time
        System.out.println("🔄 Processing order " + orderMessage.getOrderId() + "...");
        Thread.sleep(2000);
        
        // Simulate payment processing
        System.out.println("💳 Processing payment for $" + orderMessage.getAmount());
        Thread.sleep(1000);
        
        // Simulate inventory update
        System.out.println("📦 Updating inventory for product " + orderMessage.getProductId());
        Thread.sleep(1000);
        
        // Simulate shipping
        System.out.println("🚚 Arranging shipping for order " + orderMessage.getOrderId());
        Thread.sleep(1000);
    }
}
