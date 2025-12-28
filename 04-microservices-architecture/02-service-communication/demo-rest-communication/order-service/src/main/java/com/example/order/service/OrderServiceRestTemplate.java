package com.example.order.service;

import com.example.order.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;
import java.util.Random;

@Service
public class OrderServiceRestTemplate {
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Value("${payment.service.url}")
    private String paymentServiceUrl;
    
    private Random random = new Random();
    
    public OrderResponse createOrder(OrderRequest orderRequest) {
        
        Long orderId = random.nextLong(1000, 9999);
        
        System.out.println("Creating order: " + orderId);
        
        // Prepare payment request
        PaymentRequest paymentRequest = new PaymentRequest(
            orderId,
            orderRequest.getUserId(),
            orderRequest.getAmount(),
            "CREDIT_CARD"
        );
        
        try {
            // Call Payment Service using RestTemplate
            String url = paymentServiceUrl + "/api/payments/process";
            
            System.out.println("Calling Payment Service: " + url);
            
            PaymentResponse paymentResponse = restTemplate.postForObject(
                url,
                paymentRequest,
                PaymentResponse.class
            );
            
            if (paymentResponse != null && "SUCCESS".equals(paymentResponse.getStatus())) {
                return new OrderResponse(
                    orderId,
                    "CONFIRMED",
                    paymentResponse.getTransactionId(),
                    orderRequest.getAmount(),
                    "Order created and payment processed successfully"
                );
            } else {
                return new OrderResponse(
                    orderId,
                    "FAILED",
                    null,
                    orderRequest.getAmount(),
                    "Payment failed: " + (paymentResponse != null ? paymentResponse.getMessage() : "Unknown error")
                );
            }
            
        } catch (RestClientException e) {
            System.err.println("Error calling Payment Service: " + e.getMessage());
            
            return new OrderResponse(
                orderId,
                "FAILED",
                null,
                orderRequest.getAmount(),
                "Payment service unavailable: " + e.getMessage()
            );
        }
    }
}
