package com.masterclass.repository;

import com.masterclass.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repository interface for Product entity.
 * 
 * Spring Data JPA provides the implementation automatically!
 * 
 * Common methods inherited from JpaRepository:
 * - save(entity) - Save or update
 * - findById(id) - Find by ID
 * - findAll() - Get all entities
 * - deleteById(id) - Delete by ID
 * - count() - Count all entities
 * 
 * Query Methods:
 * Spring Data JPA generates queries from method names!
 * Example: findByName -> SELECT * FROM products WHERE name = ?
 * 
 * @author Spring Boot Microservices Masterclass
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // Query method - finds products by name containing the search term
    // Generated query: SELECT * FROM products WHERE name LIKE %:name%
    List<Product> findByNameContainingIgnoreCase(String name);
    
    // Query method - finds products within a price range
    // Generated query: SELECT * FROM products WHERE price BETWEEN :min AND :max
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    // Query method - finds products with quantity less than specified
    // Generated query: SELECT * FROM products WHERE quantity < :quantity
    List<Product> findByQuantityLessThan(Integer quantity);
    
    // Custom query using JPQL
    @Query("SELECT p FROM Product p WHERE p.price > :price ORDER BY p.price DESC")
    List<Product> findExpensiveProducts(@Param("price") BigDecimal price);
    
    // Custom query - count products in price range
    @Query("SELECT COUNT(p) FROM Product p WHERE p.price BETWEEN :min AND :max")
    Long countProductsInPriceRange(@Param("min") BigDecimal min, @Param("max") BigDecimal max);
    
    // Native SQL query
    @Query(value = "SELECT * FROM products WHERE quantity = 0", nativeQuery = true)
    List<Product> findOutOfStockProducts();
}
