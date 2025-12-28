package com.masterclass.springcloud.order.client;

import com.masterclass.springcloud.order.model.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign Client for Product Service
 * 
 * Declarative REST client that:
 * - Automatically discovers service via Eureka
 * - Provides load balancing
 * - Integrates with circuit breaker
 * - Handles serialization/deserialization
 * 
 * No manual HTTP client code needed!
 */
@FeignClient(
    name = "product-service",           // Service name in Eureka
    fallback = ProductClientFallback.class  // Fallback implementation
)
public interface ProductClient {

    /**
     * Get product by ID
     * Feign automatically:
     * - Discovers product-service location from Eureka
     * - Makes HTTP GET request
     * - Deserializes JSON response to Product object
     * - Applies load balancing if multiple instances exist
     */
    @GetMapping("/api/products/{id}")
    Product getProductById(@PathVariable("id") Long id);
}
