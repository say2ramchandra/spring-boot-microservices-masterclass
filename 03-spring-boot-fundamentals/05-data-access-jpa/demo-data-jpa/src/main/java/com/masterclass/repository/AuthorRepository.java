package com.masterclass.repository;

import com.masterclass.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Author Repository
 * Demonstrates various query methods and custom queries
 */
@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

    // Query Methods - Derived from method name
    
    Optional<Author> findByName(String name);
    
    List<Author> findByCountry(String country);
    
    List<Author> findByBirthDateAfter(LocalDate date);
    
    List<Author> findByNameContainingIgnoreCase(String keyword);
    
    // Custom JPQL Queries
    
    @Query("SELECT a FROM Author a WHERE a.books.size > :minBooks")
    List<Author> findAuthorsWithMinimumBooks(@Param("minBooks") int minBooks);
    
    @Query("SELECT a FROM Author a JOIN FETCH a.books WHERE a.id = :id")
    Optional<Author> findByIdWithBooks(@Param("id") Long id);
    
    @Query("SELECT DISTINCT a FROM Author a JOIN FETCH a.books")
    List<Author> findAllWithBooks();
    
    // Native SQL Query
    
    @Query(value = "SELECT a.* FROM authors a " +
                   "JOIN books b ON a.id = b.author_id " +
                   "GROUP BY a.id " +
                   "HAVING COUNT(b.id) >= :minBooks", 
           nativeQuery = true)
    List<Author> findAuthorsWithMinimumBooksNative(@Param("minBooks") int minBooks);
    
    // Count queries
    
    @Query("SELECT COUNT(a) FROM Author a WHERE a.country = :country")
    long countByCountry(@Param("country") String country);
}
