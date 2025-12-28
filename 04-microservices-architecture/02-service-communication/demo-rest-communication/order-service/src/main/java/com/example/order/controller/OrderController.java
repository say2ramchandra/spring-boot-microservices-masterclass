package com.example.order.controller;

import com.example.order.dto.OrderRequest;
import com.example.order.dto.OrderResponse;
import com.example.order.service.OrderServiceRestTemplate;
import com.example.order.service.OrderServiceWebClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    @Autowired
    private OrderServiceRestTemplate orderServiceRestTemplate;
    
    @Autowired
    private OrderServiceWebClient orderServiceWebClient;
    
    @PostMapping
    public ResponseEntity<OrderResponse> createOrderWithRestTemplate(@RequestBody OrderRequest request) {
        OrderResponse response = orderServiceRestTemplate.createOrder(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/webclient")
    public ResponseEntity<OrderResponse> createOrderWithWebClient(@RequestBody OrderRequest request) {
        OrderResponse response = orderServiceWebClient.createOrder(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/async")
    public Mono<OrderResponse> createOrderAsync(@RequestBody OrderRequest request) {
        return orderServiceWebClient.createOrderAsync(request);
    }
}
