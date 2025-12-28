package com.masterclass.springcloud.order.client;

import com.masterclass.springcloud.order.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Fallback implementation for ProductClient
 * 
 * Provides graceful degradation when Product Service is unavailable.
 * Returns cached or default data instead of failing completely.
 */
@Component
public class ProductClientFallback implements ProductClient {

    private static final Logger logger = LoggerFactory.getLogger(ProductClientFallback.class);

    @Override
    public Product getProductById(Long id) {
        logger.warn("⚠️ Product Service unavailable. Returning fallback product for ID: {}", id);
        
        // Return fallback product with default values
        Product fallbackProduct = new Product();
        fallbackProduct.setId(id);
        fallbackProduct.setName("Product Temporarily Unavailable");
        fallbackProduct.setDescription("Service is down. Please try again later.");
        fallbackProduct.setPrice(BigDecimal.ZERO);
        fallbackProduct.setStockQuantity(0);
        fallbackProduct.setCategory("UNAVAILABLE");
        
        return fallbackProduct;
    }
}
