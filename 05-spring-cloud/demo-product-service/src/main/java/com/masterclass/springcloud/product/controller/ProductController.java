package com.masterclass.springcloud.product.controller;

import com.masterclass.springcloud.product.model.Product;
import com.masterclass.springcloud.product.service.ProductService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * Get all products
     * Demonstrates basic REST endpoint registered with Eureka
     */
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    /**
     * Get product by ID
     * Demonstrates path variable handling
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get products by category
     * Demonstrates query parameter handling
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable String category) {
        return ResponseEntity.ok(productService.getProductsByCategory(category));
    }

    /**
     * Create new product
     * Demonstrates POST request handling
     */
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product created = productService.createProduct(product);
        return ResponseEntity.ok(created);
    }

    /**
     * Update product stock
     * Demonstrates circuit breaker pattern
     */
    @PutMapping("/{id}/stock")
    @CircuitBreaker(name = "productService", fallbackMethod = "updateStockFallback")
    public ResponseEntity<Product> updateStock(
            @PathVariable Long id,
            @RequestParam Integer quantity) {
        Product updated = productService.updateStock(id, quantity);
        return ResponseEntity.ok(updated);
    }

    /**
     * Fallback method for circuit breaker
     * Returns cached or default response when service is unavailable
     */
    public ResponseEntity<Product> updateStockFallback(Long id, Integer quantity, Exception ex) {
        Product fallbackProduct = new Product();
        fallbackProduct.setId(id);
        fallbackProduct.setName("Service Temporarily Unavailable");
        fallbackProduct.setStockQuantity(0);
        fallbackProduct.setPrice(BigDecimal.ZERO);
        return ResponseEntity.ok(fallbackProduct);
    }

    /**
     * Check service health and instance info
     */
    @GetMapping("/info")
    public ResponseEntity<String> getServiceInfo() {
        return ResponseEntity.ok("Product Service - Instance running on port 8081");
    }
}
