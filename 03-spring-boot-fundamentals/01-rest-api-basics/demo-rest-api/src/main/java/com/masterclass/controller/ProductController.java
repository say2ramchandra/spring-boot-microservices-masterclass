package com.masterclass.controller;

import com.masterclass.dto.ProductDTO;
import com.masterclass.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * REST Controller for Product API.
 * 
 * @RestController - Combines @Controller + @ResponseBody
 * @RequestMapping - Base path for all endpoints
 * @CrossOrigin - Enable CORS for frontend access
 * 
 * HTTP Methods:
 * - GET: Retrieve resources
 * - POST: Create new resource
 * - PUT: Update entire resource
 * - PATCH: Partial update
 * - DELETE: Remove resource
 * 
 * HTTP Status Codes:
 * - 200 OK: Successful GET/PUT/PATCH
 * - 201 CREATED: Successful POST
 * - 204 NO_CONTENT: Successful DELETE
 * - 400 BAD_REQUEST: Validation error
 * - 404 NOT_FOUND: Resource not found
 * - 500 INTERNAL_SERVER_ERROR: Server error
 * 
 * @author Spring Boot Microservices Masterclass
 */
@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {
    
    private final ProductService productService;
    
    // Constructor injection
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    
    /**
     * GET /api/products
     * Get all products.
     * 
     * @return List of all products
     * 
     * Example: curl http://localhost:8080/api/products
     */
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<ProductDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }
    
    /**
     * GET /api/products/{id}
     * Get product by ID.
     * 
     * @param id Product ID (path variable)
     * @return Product with the specified ID
     * 
     * Example: curl http://localhost:8080/api/products/1
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        ProductDTO product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }
    
    /**
     * POST /api/products
     * Create a new product.
     * 
     * @param productDTO Product data (request body)
     * @return Created product with 201 status
     * 
     * Example:
     * curl -X POST http://localhost:8080/api/products \
     *   -H "Content-Type: application/json" \
     *   -d '{
     *     "name": "New Product",
     *     "description": "Product description",
     *     "price": 99.99,
     *     "quantity": 10
     *   }'
     */
    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody ProductDTO productDTO) {
        ProductDTO createdProduct = productService.createProduct(productDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }
    
    /**
     * PUT /api/products/{id}
     * Update an existing product.
     * 
     * @param id Product ID
     * @param productDTO Updated product data
     * @return Updated product
     * 
     * Example:
     * curl -X PUT http://localhost:8080/api/products/1 \
     *   -H "Content-Type: application/json" \
     *   -d '{
     *     "name": "Updated Product",
     *     "description": "Updated description",
     *     "price": 149.99,
     *     "quantity": 20
     *   }'
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductDTO productDTO) {
        ProductDTO updatedProduct = productService.updateProduct(id, productDTO);
        return ResponseEntity.ok(updatedProduct);
    }
    
    /**
     * DELETE /api/products/{id}
     * Delete a product.
     * 
     * @param id Product ID
     * @return 204 No Content
     * 
     * Example: curl -X DELETE http://localhost:8080/api/products/1
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * GET /api/products/search?name=keyword
     * Search products by name.
     * 
     * @param name Search keyword (query parameter)
     * @return List of matching products
     * 
     * Example: curl "http://localhost:8080/api/products/search?name=laptop"
     */
    @GetMapping("/search")
    public ResponseEntity<List<ProductDTO>> searchProducts(@RequestParam String name) {
        List<ProductDTO> products = productService.searchProductsByName(name);
        return ResponseEntity.ok(products);
    }
    
    /**
     * GET /api/products/price-range?min=10&max=100
     * Get products in a price range.
     * 
     * @param min Minimum price
     * @param max Maximum price
     * @return List of products in the range
     * 
     * Example: curl "http://localhost:8080/api/products/price-range?min=20&max=100"
     */
    @GetMapping("/price-range")
    public ResponseEntity<List<ProductDTO>> getProductsByPriceRange(
            @RequestParam BigDecimal min,
            @RequestParam BigDecimal max) {
        List<ProductDTO> products = productService.getProductsByPriceRange(min, max);
        return ResponseEntity.ok(products);
    }
    
    /**
     * GET /api/products/low-stock?threshold=10
     * Get products with low stock.
     * 
     * @param threshold Quantity threshold (default: 10)
     * @return List of low-stock products
     * 
     * Example: curl "http://localhost:8080/api/products/low-stock?threshold=15"
     */
    @GetMapping("/low-stock")
    public ResponseEntity<List<ProductDTO>> getLowStockProducts(
            @RequestParam(defaultValue = "10") Integer threshold) {
        List<ProductDTO> products = productService.getLowStockProducts(threshold);
        return ResponseEntity.ok(products);
    }
}
