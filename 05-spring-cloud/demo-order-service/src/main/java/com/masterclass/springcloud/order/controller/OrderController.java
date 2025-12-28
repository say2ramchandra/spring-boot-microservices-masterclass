package com.masterclass.springcloud.order.controller;

import com.masterclass.springcloud.order.model.Order;
import com.masterclass.springcloud.order.model.OrderRequest;
import com.masterclass.springcloud.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * Create new order
     * Calls Product Service via Feign Client to get product details
     */
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody OrderRequest orderRequest) {
        Order order = orderService.createOrder(orderRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    /**
     * Get all orders
     */
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    /**
     * Get order by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get orders by customer name
     */
    @GetMapping("/customer/{customerName}")
    public ResponseEntity<List<Order>> getOrdersByCustomer(@PathVariable String customerName) {
        return ResponseEntity.ok(orderService.getOrdersByCustomer(customerName));
    }

    /**
     * Update order status
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        Order updated = orderService.updateOrderStatus(id, status);
        return updated != null ? 
            ResponseEntity.ok(updated) : 
            ResponseEntity.notFound().build();
    }

    /**
     * Service info
     */
    @GetMapping("/info")
    public ResponseEntity<String> getServiceInfo() {
        return ResponseEntity.ok("Order Service - Running on port 8083 - Using Feign Client");
    }
}
