package com.masterclass.restapi.controller;

import com.masterclass.restapi.model.Product;
import com.masterclass.restapi.service.ProductService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * REST Controller for Product CRUD operations.
 * 
 * Demonstrates:
 * - All HTTP methods (GET, POST, PUT, DELETE)
 * - Path variables and request parameters
 * - Request validation
 * - Proper status codes
 * - ResponseEntity usage
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService productService;

    /**
     * CREATE - POST /api/products
     * Creates a new product
     * Status: 201 Created with Location header
     */
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody @Valid Product product) {
        logger.info("Creating new product: {}", product.getName());
        
        Product savedProduct = productService.save(product);
        
        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(savedProduct.getId())
            .toUri();
        
        return ResponseEntity.created(location).body(savedProduct);
    }

    /**
     * READ ALL - GET /api/products
     * Retrieves all products or search by name
     * Status: 200 OK
     */
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts(
        @RequestParam(required = false) String name
    ) {
        logger.info("Fetching products. Search name: {}", name);
        
        List<Product> products;
        if (name != null && !name.isEmpty()) {
            products = productService.searchByName(name);
        } else {
            products = productService.findAll();
        }
        
        return ResponseEntity.ok(products);
    }

    /**
     * READ ONE - GET /api/products/{id}
     * Retrieves a single product by ID
     * Status: 200 OK or 404 Not Found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        logger.info("Fetching product with id: {}", id);
        
        return productService.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * SEARCH by price range - GET /api/products/search
     * Example: /api/products/search?minPrice=10&maxPrice=100
     * Status: 200 OK
     */
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchByPriceRange(
        @RequestParam Double minPrice,
        @RequestParam Double maxPrice
    ) {
        logger.info("Searching products between {} and {}", minPrice, maxPrice);
        
        List<Product> products = productService.findByPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(products);
    }

    /**
     * UPDATE - PUT /api/products/{id}
     * Updates an existing product
     * Status: 200 OK or 404 Not Found
     */
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
        @PathVariable Long id,
        @RequestBody @Valid Product productDetails
    ) {
        logger.info("Updating product with id: {}", id);
        
        return productService.update(id, productDetails)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * DELETE - DELETE /api/products/{id}
     * Deletes a product
     * Status: 204 No Content or 404 Not Found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        logger.info("Deleting product with id: {}", id);
        
        if (productService.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Product API is running");
    }
}
