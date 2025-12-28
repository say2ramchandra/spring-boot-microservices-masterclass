package com.masterclass.starter.repository;

import com.masterclass.starter.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface demonstrating spring-boot-starter-data-jpa.
 * 
 * Spring Data JPA automatically provides implementations for:
 * - findAll(), findById(), save(), delete()
 * - Custom query methods (findByName, etc.)
 * 
 * All thanks to spring-boot-starter-data-jpa!
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // Spring Data JPA generates implementation automatically
    Product findByName(String name);
}
