package com.example.order.controller;

import com.example.order.dto.CreateOrderRequest;
import com.example.order.entity.Order;
import com.example.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Order Controller
 * 
 * REST API for order management.
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    /**
     * Create new order
     * 
     * POST /api/orders
     */
    @PostMapping
    public ResponseEntity<Order> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        Order order = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }
    
    /**
     * Get all orders
     * 
     * GET /api/orders
     */
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }
    
    /**
     * Get order by ID
     * 
     * GET /api/orders/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }
    
    /**
     * Get orders by customer email
     * 
     * GET /api/orders/customer?email=john@example.com
     */
    @GetMapping("/customer")
    public ResponseEntity<List<Order>> getOrdersByCustomer(@RequestParam String email) {
        List<Order> orders = orderService.getOrdersByCustomer(email);
        return ResponseEntity.ok(orders);
    }
    
    /**
     * Get orders by status
     * 
     * GET /api/orders/status?status=CONFIRMED
     */
    @GetMapping("/status")
    public ResponseEntity<List<Order>> getOrdersByStatus(@RequestParam Order.OrderStatus status) {
        List<Order> orders = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(orders);
    }
    
    /**
     * Cancel order
     * 
     * PUT /api/orders/{id}/cancel
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Order> cancelOrder(@PathVariable Long id) {
        Order order = orderService.cancelOrder(id);
        return ResponseEntity.ok(order);
    }
}
