package com.example.order.service;

import com.example.order.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.time.Duration;
import java.util.Random;

@Service
public class OrderServiceWebClient {
    
    @Autowired
    private WebClient webClient;
    
    private Random random = new Random();
    
    public OrderResponse createOrder(OrderRequest orderRequest) {
        
        Long orderId = random.nextLong(1000, 9999);
        
        System.out.println("Creating order with WebClient: " + orderId);
        
        // Prepare payment request
        PaymentRequest paymentRequest = new PaymentRequest(
            orderId,
            orderRequest.getUserId(),
            orderRequest.getAmount(),
            "CREDIT_CARD"
        );
        
        try {
            // Call Payment Service using WebClient
            PaymentResponse paymentResponse = webClient.post()
                .uri("/api/payments/process")
                .bodyValue(paymentRequest)
                .retrieve()
                .bodyToMono(PaymentResponse.class)
                .timeout(Duration.ofSeconds(5))
                .block(); // Blocking for simplicity
            
            if (paymentResponse != null && "SUCCESS".equals(paymentResponse.getStatus())) {
                return new OrderResponse(
                    orderId,
                    "CONFIRMED",
                    paymentResponse.getTransactionId(),
                    orderRequest.getAmount(),
                    "Order created and payment processed successfully (WebClient)"
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
            
        } catch (Exception e) {
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
    
    // Non-blocking reactive version
    public Mono<OrderResponse> createOrderAsync(OrderRequest orderRequest) {
        
        Long orderId = random.nextLong(1000, 9999);
        
        PaymentRequest paymentRequest = new PaymentRequest(
            orderId,
            orderRequest.getUserId(),
            orderRequest.getAmount(),
            "CREDIT_CARD"
        );
        
        return webClient.post()
            .uri("/api/payments/process")
            .bodyValue(paymentRequest)
            .retrieve()
            .bodyToMono(PaymentResponse.class)
            .timeout(Duration.ofSeconds(5))
            .map(paymentResponse -> {
                if ("SUCCESS".equals(paymentResponse.getStatus())) {
                    return new OrderResponse(
                        orderId,
                        "CONFIRMED",
                        paymentResponse.getTransactionId(),
                        orderRequest.getAmount(),
                        "Order created successfully (Async)"
                    );
                } else {
                    return new OrderResponse(
                        orderId,
                        "FAILED",
                        null,
                        orderRequest.getAmount(),
                        "Payment failed"
                    );
                }
            })
            .onErrorResume(e -> Mono.just(new OrderResponse(
                orderId,
                "FAILED",
                null,
                orderRequest.getAmount(),
                "Service unavailable"
            )));
    }
}
