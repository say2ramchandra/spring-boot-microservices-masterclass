package com.ecommerce.order.client;

import com.ecommerce.order.dto.ProductDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Client to communicate with Product Service
 */
@Component
public class ProductServiceClient {
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Value("${product-service.url}")
    private String productServiceUrl;
    
    public ProductDTO getProductById(Long productId) {
        String url = productServiceUrl + "/api/products/" + productId;
        try {
            return restTemplate.getForObject(url, ProductDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch product with id: " + productId, e);
        }
    }
    
    public Boolean checkProductAvailability(Long productId, Integer quantity) {
        String url = productServiceUrl + "/api/products/" + productId + 
                     "/available?quantity=" + quantity;
        try {
            return restTemplate.getForObject(url, Boolean.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to check product availability", e);
        }
    }
    
    public void reduceProductStock(Long productId, Integer quantity) {
        String url = productServiceUrl + "/api/products/" + productId + 
                     "/reduce-stock?quantity=" + quantity;
        try {
            restTemplate.put(url, null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to reduce product stock", e);
        }
    }
}
