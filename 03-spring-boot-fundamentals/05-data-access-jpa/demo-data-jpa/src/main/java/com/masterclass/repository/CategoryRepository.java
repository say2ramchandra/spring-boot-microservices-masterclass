package com.masterclass.repository;

import com.masterclass.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Category Repository
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByName(String name);
    
    List<Category> findByNameContainingIgnoreCase(String keyword);
    
    @Query("SELECT c FROM Category c JOIN FETCH c.books WHERE c.id = :id")
    Optional<Category> findByIdWithBooks(@Param("id") Long id);
    
    @Query("SELECT c FROM Category c WHERE c.books.size >= :minBooks")
    List<Category> findCategoriesWithMinimumBooks(@Param("minBooks") int minBooks);
}
