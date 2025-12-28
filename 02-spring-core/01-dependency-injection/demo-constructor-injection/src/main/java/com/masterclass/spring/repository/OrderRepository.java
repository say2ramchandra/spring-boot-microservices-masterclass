package com.masterclass.spring.repository;

import org.springframework.stereotype.Repository;

/**
 * OrderRepository - Manages order data persistence
 */
@Repository
public class OrderRepository {

    public String save(Long userId, String product, Double amount) {
        System.out.println("  [OrderRepository] Saving order to database...");
        return String.format("Order saved: User %d, Product: %s, Amount: $%.2f", 
            userId, product, amount);
    }

    public String findById(Long orderId) {
        System.out.println("  [OrderRepository] Fetching order...");
        return "Order #" + orderId + " details";
    }
}
