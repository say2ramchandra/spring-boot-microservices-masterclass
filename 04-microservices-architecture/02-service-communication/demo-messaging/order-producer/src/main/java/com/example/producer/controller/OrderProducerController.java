package com.example.producer.controller;

import com.example.producer.dto.OrderMessage;
import com.example.producer.dto.OrderRequest;
import com.example.producer.service.OrderProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/orders")
public class OrderProducerController {
    
    @Autowired
    private OrderProducerService orderProducerService;
    
    private Random random = new Random();
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody OrderRequest request) {
        
        Long orderId = random.nextLong(1000, 9999);
        
        OrderMessage orderMessage = new OrderMessage(
            orderId,
            request.getUserId(),
            request.getProductId(),
            request.getQuantity(),
            request.getAmount()
        );
        
        // Send to RabbitMQ (fire-and-forget)
        orderProducerService.sendOrder(orderMessage);
        
        // Return immediately
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Order submitted successfully");
        response.put("orderId", orderId);
        response.put("status", "PROCESSING");
        
        return ResponseEntity.accepted().body(response);
    }
    
    @PostMapping("/bulk")
    public ResponseEntity<Map<String, Object>> createBulkOrders(@RequestBody Map<String, Object> request) {
        
        int count = (int) request.get("count");
        Long userId = Long.valueOf(request.get("userId").toString());
        
        for (int i = 0; i < count; i++) {
            Long orderId = random.nextLong(1000, 9999);
            
            OrderMessage orderMessage = new OrderMessage(
                orderId,
                userId,
                (long) random.nextInt(1, 100),
                random.nextInt(1, 10),
                java.math.BigDecimal.valueOf(random.nextDouble(10, 1000))
            );
            
            orderProducerService.sendOrder(orderMessage);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", count + " orders submitted successfully");
        
        return ResponseEntity.accepted().body(response);
    }
}
