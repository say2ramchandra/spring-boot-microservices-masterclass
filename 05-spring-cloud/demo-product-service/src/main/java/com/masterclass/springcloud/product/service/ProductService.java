package com.masterclass.springcloud.product.service;

import com.masterclass.springcloud.product.model.Product;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final Map<Long, Product> productStore = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public ProductService() {
        // Initialize with sample data
        initializeSampleData();
    }

    private void initializeSampleData() {
        createProduct(new Product(null, "Laptop", "High-performance laptop", 
                new BigDecimal("999.99"), 50, "Electronics"));
        createProduct(new Product(null, "Smartphone", "Latest smartphone model", 
                new BigDecimal("699.99"), 100, "Electronics"));
        createProduct(new Product(null, "Headphones", "Wireless noise-cancelling headphones", 
                new BigDecimal("199.99"), 75, "Electronics"));
        createProduct(new Product(null, "Coffee Maker", "Automatic coffee maker", 
                new BigDecimal("89.99"), 30, "Home Appliances"));
        createProduct(new Product(null, "Desk Chair", "Ergonomic office chair", 
                new BigDecimal("249.99"), 20, "Furniture"));
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(productStore.values());
    }

    public Optional<Product> getProductById(Long id) {
        return Optional.ofNullable(productStore.get(id));
    }

    public List<Product> getProductsByCategory(String category) {
        return productStore.values().stream()
                .filter(p -> p.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    public Product createProduct(Product product) {
        Long id = idGenerator.getAndIncrement();
        product.setId(id);
        productStore.put(id, product);
        return product;
    }

    public Product updateStock(Long id, Integer quantity) {
        Product product = productStore.get(id);
        if (product != null) {
            product.setStockQuantity(quantity);
            productStore.put(id, product);
        }
        return product;
    }

    public boolean checkStock(Long productId, Integer requiredQuantity) {
        Product product = productStore.get(productId);
        return product != null && product.getStockQuantity() >= requiredQuantity;
    }
}
