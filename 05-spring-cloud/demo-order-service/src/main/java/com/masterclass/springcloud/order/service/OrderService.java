package com.masterclass.springcloud.order.service;

import com.masterclass.springcloud.order.client.ProductClient;
import com.masterclass.springcloud.order.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final ProductClient productClient;
    private final Map<Long, Order> orderStore = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public OrderService(ProductClient productClient) {
        this.productClient = productClient;
    }

    /**
     * Create new order
     * 
     * Demonstrates Feign Client usage:
     * 1. For each order item, calls Product Service via Feign
     * 2. Feign handles service discovery, load balancing, and HTTP communication
     * 3. If Product Service is down, fallback is used
     */
    public Order createOrder(OrderRequest orderRequest) {
        logger.info("📝 Creating order for customer: {}", orderRequest.getCustomerName());

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        // Process each item - calling Product Service via Feign Client
        for (OrderItemRequest itemRequest : orderRequest.getItems()) {
            logger.info("🔍 Fetching product details for ID: {} using Feign Client", 
                       itemRequest.getProductId());
            
            // Feign Client call - automatic service discovery and load balancing!
            Product product = productClient.getProductById(itemRequest.getProductId());
            
            if (product != null) {
                BigDecimal subtotal = product.getPrice()
                        .multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
                
                OrderItem orderItem = new OrderItem(
                    product.getId(),
                    product.getName(),
                    itemRequest.getQuantity(),
                    product.getPrice(),
                    subtotal
                );
                
                orderItems.add(orderItem);
                totalAmount = totalAmount.add(subtotal);
                
                logger.info("✅ Added product: {} x{} = ${}", 
                           product.getName(), itemRequest.getQuantity(), subtotal);
            } else {
                logger.warn("⚠️ Product not found: {}", itemRequest.getProductId());
            }
        }

        // Create order
        Long orderId = idGenerator.getAndIncrement();
        Order order = new Order(
            orderId,
            orderRequest.getCustomerName(),
            orderRequest.getCustomerEmail(),
            orderItems,
            totalAmount,
            "PENDING",
            LocalDateTime.now()
        );

        orderStore.put(orderId, order);
        logger.info("✅ Order created successfully. Order ID: {}, Total: ${}", orderId, totalAmount);
        
        return order;
    }

    public List<Order> getAllOrders() {
        return new ArrayList<>(orderStore.values());
    }

    public Optional<Order> getOrderById(Long id) {
        return Optional.ofNullable(orderStore.get(id));
    }

    public List<Order> getOrdersByCustomer(String customerName) {
        return orderStore.values().stream()
                .filter(order -> order.getCustomerName().equalsIgnoreCase(customerName))
                .collect(Collectors.toList());
    }

    public Order updateOrderStatus(Long orderId, String newStatus) {
        Order order = orderStore.get(orderId);
        if (order != null) {
            order.setStatus(newStatus);
            orderStore.put(orderId, order);
            logger.info("📦 Order {} status updated to: {}", orderId, newStatus);
        }
        return order;
    }
}
