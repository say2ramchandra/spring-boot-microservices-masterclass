package com.example.order.client;

import com.example.order.dto.ProductDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Fallback implementation for ProductClient
 * 
 * This class provides fallback behavior when Product Service is unavailable.
 * It implements the ProductClient interface and returns safe default values.
 * 
 * When is fallback triggered?
 * - Product Service is down
 * - Network timeout
 * - Circuit breaker is OPEN
 * - Any exception during Feign call
 * 
 * Best Practices:
 * - Log the fallback event
 * - Return safe default values
 * - Don't throw exceptions
 * - Consider returning cached data
 */
@Component
public class ProductClientFallback implements ProductClient {
    
    private static final Logger log = LoggerFactory.getLogger(ProductClientFallback.class);
    
    @Override
    public ProductDTO getProductById(Long id) {
        log.warn("Fallback triggered for getProductById({})", id);
        
        // Return default product
        ProductDTO product = new ProductDTO();
        product.setId(id);
        product.setName("Product Unavailable");
        product.setDescription("Product service is temporarily unavailable");
        product.setPrice(0.0);
        product.setStock(0);
        product.setAvailable(false);
        
        return product;
    }
    
    @Override
    public List<ProductDTO> getAllProducts() {
        log.warn("Fallback triggered for getAllProducts()");
        
        // Return empty list
        return Collections.emptyList();
    }
    
    @Override
    public List<ProductDTO> searchProducts(String name) {
        log.warn("Fallback triggered for searchProducts({})", name);
        
        // Return empty list
        return Collections.emptyList();
    }
    
    @Override
    public Boolean checkAvailability(Long id, Integer quantity) {
        log.warn("Fallback triggered for checkAvailability({}, {})", id, quantity);
        
        // Default to not available
        return false;
    }
    
    @Override
    public void updateStock(Long id, Integer quantity) {
        log.warn("Fallback triggered for updateStock({}, {})", id, quantity);
        
        // Do nothing in fallback
        // In production, might queue for later or send alert
    }
}
