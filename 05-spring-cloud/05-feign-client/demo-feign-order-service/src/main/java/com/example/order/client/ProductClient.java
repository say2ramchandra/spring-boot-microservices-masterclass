package com.example.order.client;

import com.example.order.dto.ProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Feign Client for Product Service
 * 
 * This interface defines the contract for calling Product Service APIs.
 * Feign automatically generates the implementation at runtime.
 * 
 * Features demonstrated:
 * - Service discovery via Eureka (name = "product-service")
 * - Automatic load balancing across instances
 * - Circuit breaker with fallback
 * - Type-safe method signatures
 * 
 * @FeignClient annotation parameters:
 * - name: Service name registered in Eureka
 * - fallback: Fallback implementation class
 * - path: Base path for all endpoints (optional)
 */
@FeignClient(
    name = "product-service",
    fallback = ProductClientFallback.class
)
public interface ProductClient {
    
    /**
     * Get product by ID
     * 
     * Example: GET http://product-service/api/products/1
     * 
     * @param id Product ID
     * @return Product details
     */
    @GetMapping("/api/products/{id}")
    ProductDTO getProductById(@PathVariable("id") Long id);
    
    /**
     * Get all products
     * 
     * Example: GET http://product-service/api/products
     * 
     * @return List of all products
     */
    @GetMapping("/api/products")
    List<ProductDTO> getAllProducts();
    
    /**
     * Search products by name
     * 
     * Example: GET http://product-service/api/products/search?name=iPhone
     * 
     * @param name Product name to search
     * @return List of matching products
     */
    @GetMapping("/api/products/search")
    List<ProductDTO> searchProducts(@RequestParam("name") String name);
    
    /**
     * Check product availability
     * 
     * Example: GET http://product-service/api/products/1/availability?quantity=5
     * 
     * @param id Product ID
     * @param quantity Requested quantity
     * @return true if available, false otherwise
     */
    @GetMapping("/api/products/{id}/availability")
    Boolean checkAvailability(
        @PathVariable("id") Long id,
        @RequestParam("quantity") Integer quantity
    );
    
    /**
     * Update product stock
     * 
     * Example: PUT http://product-service/api/products/1/stock?quantity=10
     * 
     * @param id Product ID
     * @param quantity New stock quantity
     */
    @PutMapping("/api/products/{id}/stock")
    void updateStock(
        @PathVariable("id") Long id,
        @RequestParam("quantity") Integer quantity
    );
}
