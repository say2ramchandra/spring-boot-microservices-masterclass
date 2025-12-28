package com.example.order.service;

import com.example.order.client.ProductClient;
import com.example.order.dto.CreateOrderRequest;
import com.example.order.dto.ProductDTO;
import com.example.order.entity.Order;
import com.example.order.exception.InsufficientStockException;
import com.example.order.exception.ProductNotFoundException;
import com.example.order.repository.OrderRepository;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Order Service
 * 
 * Business logic for order management.
 * Uses Feign client to communicate with Product Service.
 */
@Service
public class OrderService {
    
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private ProductClient productClient;
    
    /**
     * Create new order
     * 
     * Process:
     * 1. Call Product Service to get product details (via Feign)
     * 2. Check product availability
     * 3. Calculate total price
     * 4. Create order
     * 5. Update product stock (via Feign)
     * 
     * @param request Order request
     * @return Created order
     */
    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        log.info("Creating order for product {} with quantity {}", 
            request.getProductId(), request.getQuantity());
        
        // Step 1: Get product details from Product Service (via Feign)
        ProductDTO product;
        try {
            product = productClient.getProductById(request.getProductId());
            log.info("Received product details: {}", product.getName());
        } catch (FeignException.NotFound e) {
            log.error("Product not found: {}", request.getProductId());
            throw new ProductNotFoundException("Product not found: " + request.getProductId());
        } catch (FeignException e) {
            log.error("Error calling Product Service: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch product details");
        }
        
        // Step 2: Check if product is available
        if (!product.getAvailable()) {
            throw new ProductNotFoundException("Product is not available");
        }
        
        // Step 3: Check stock availability (via Feign)
        Boolean available;
        try {
            available = productClient.checkAvailability(
                request.getProductId(), 
                request.getQuantity()
            );
        } catch (FeignException e) {
            log.error("Error checking availability: {}", e.getMessage());
            // Assume not available if service call fails
            available = false;
        }
        
        if (!available) {
            throw new InsufficientStockException(
                "Insufficient stock for product: " + product.getName()
            );
        }
        
        // Step 4: Calculate total price and create order
        double totalPrice = product.getPrice() * request.getQuantity();
        
        Order order = new Order();
        order.setProductId(product.getId());
        order.setProductName(product.getName());
        order.setProductPrice(product.getPrice());
        order.setQuantity(request.getQuantity());
        order.setTotalPrice(totalPrice);
        order.setCustomerName(request.getCustomerName());
        order.setCustomerEmail(request.getCustomerEmail());
        order.setStatus(Order.OrderStatus.CONFIRMED);
        
        Order savedOrder = orderRepository.save(order);
        log.info("Order created successfully: {}", savedOrder.getId());
        
        // Step 5: Update product stock (via Feign)
        try {
            int newStock = product.getStock() - request.getQuantity();
            productClient.updateStock(request.getProductId(), newStock);
            log.info("Product stock updated for product: {}", request.getProductId());
        } catch (FeignException e) {
            log.error("Error updating product stock: {}", e.getMessage());
            // In production, might want to implement compensation logic
        }
        
        return savedOrder;
    }
    
    /**
     * Get all orders
     */
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
    
    /**
     * Get order by ID
     */
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Order not found: " + id));
    }
    
    /**
     * Get orders by customer email
     */
    public List<Order> getOrdersByCustomer(String email) {
        return orderRepository.findByCustomerEmail(email);
    }
    
    /**
     * Get orders by status
     */
    public List<Order> getOrdersByStatus(Order.OrderStatus status) {
        return orderRepository.findByStatus(status);
    }
    
    /**
     * Cancel order
     */
    @Transactional
    public Order cancelOrder(Long id) {
        Order order = getOrderById(id);
        
        if (order.getStatus() == Order.OrderStatus.DELIVERED) {
            throw new RuntimeException("Cannot cancel delivered order");
        }
        
        order.setStatus(Order.OrderStatus.CANCELLED);
        
        // Restore product stock (via Feign)
        try {
            ProductDTO product = productClient.getProductById(order.getProductId());
            int newStock = product.getStock() + order.getQuantity();
            productClient.updateStock(order.getProductId(), newStock);
        } catch (FeignException e) {
            log.error("Error restoring product stock: {}", e.getMessage());
        }
        
        return orderRepository.save(order);
    }
}
