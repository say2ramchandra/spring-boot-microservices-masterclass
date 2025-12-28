package com.masterclass.restapi.config;

import com.masterclass.restapi.model.Product;
import com.masterclass.restapi.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Initialize sample data for demonstration
 */
@Configuration
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    public CommandLineRunner initData(ProductRepository repository) {
        return args -> {
            logger.info("Initializing sample data...");

            repository.save(new Product("Laptop", "High-performance laptop", 999.99, 10));
            repository.save(new Product("Mouse", "Wireless mouse", 29.99, 50));
            repository.save(new Product("Keyboard", "Mechanical keyboard", 79.99, 30));
            repository.save(new Product("Monitor", "27-inch 4K monitor", 399.99, 15));
            repository.save(new Product("Headphones", "Noise-cancelling headphones", 199.99, 25));

            logger.info("Sample data initialized. Total products: {}", repository.count());
        };
    }
}
