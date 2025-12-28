package com.ecommerce.order.service;

import com.ecommerce.order.client.ProductServiceClient;
import com.ecommerce.order.client.UserServiceClient;
import com.ecommerce.order.dto.CreateOrderRequest;
import com.ecommerce.order.dto.ProductDTO;
import com.ecommerce.order.dto.UserDTO;
import com.ecommerce.order.entity.Order;
import com.ecommerce.order.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private UserServiceClient userServiceClient;
    
    @Autowired
    private ProductServiceClient productServiceClient;
    
    public List<Order> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        orders.forEach(this::enrichOrderWithExternalData);
        return orders;
    }
    
    public Optional<Order> getOrderById(Long id) {
        Optional<Order> order = orderRepository.findById(id);
        order.ifPresent(this::enrichOrderWithExternalData);
        return order;
    }
    
    public List<Order> getOrdersByUserId(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        orders.forEach(this::enrichOrderWithExternalData);
        return orders;
    }
    
    public List<Order> getOrdersByStatus(Order.OrderStatus status) {
        return orderRepository.findByStatus(status);
    }
    
    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        // 1. Validate user exists
        UserDTO user = userServiceClient.getUserById(request.getUserId());
        if (user == null) {
            throw new RuntimeException("User not found with id: " + request.getUserId());
        }
        
        // 2. Validate product exists and has sufficient stock
        ProductDTO product = productServiceClient.getProductById(request.getProductId());
        if (product == null) {
            throw new RuntimeException("Product not found with id: " + request.getProductId());
        }
        
        Boolean isAvailable = productServiceClient.checkProductAvailability(
            request.getProductId(), request.getQuantity());
        
        if (!isAvailable) {
            throw new RuntimeException("Insufficient stock for product: " + product.getName());
        }
        
        // 3. Calculate total price
        BigDecimal totalPrice = product.getPrice()
            .multiply(BigDecimal.valueOf(request.getQuantity()));
        
        // 4. Create order
        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setProductId(request.getProductId());
        order.setQuantity(request.getQuantity());
        order.setTotalPrice(totalPrice);
        order.setStatus(Order.OrderStatus.PENDING);
        order.setOrderDate(LocalDateTime.now());
        
        // 5. Reduce product stock
        productServiceClient.reduceProductStock(request.getProductId(), request.getQuantity());
        
        // 6. Save order
        Order savedOrder = orderRepository.save(order);
        
        // 7. Enrich with external data before returning
        enrichOrderWithExternalData(savedOrder);
        
        return savedOrder;
    }
    
    public Order updateOrderStatus(Long id, Order.OrderStatus status) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
        
        order.setStatus(status);
        return orderRepository.save(order);
    }
    
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }
    
    /**
     * Enrich order with user and product information from other services
     */
    private void enrichOrderWithExternalData(Order order) {
        try {
            UserDTO user = userServiceClient.getUserById(order.getUserId());
            if (user != null) {
                order.setUserName(user.getName());
            }
        } catch (Exception e) {
            order.setUserName("Unknown");
        }
        
        try {
            ProductDTO product = productServiceClient.getProductById(order.getProductId());
            if (product != null) {
                order.setProductName(product.getName());
            }
        } catch (Exception e) {
            order.setProductName("Unknown");
        }
    }
}
