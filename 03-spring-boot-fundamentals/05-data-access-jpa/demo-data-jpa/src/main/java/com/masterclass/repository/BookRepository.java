package com.masterclass.repository;

import com.masterclass.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Book Repository
 * Demonstrates query methods, custom queries, and modifying queries
 */
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    // Query Methods - Simple
    
    Optional<Book> findByIsbn(String isbn);
    
    List<Book> findByTitleContainingIgnoreCase(String keyword);
    
    List<Book> findByAuthorId(Long authorId);
    
    List<Book> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    List<Book> findByPublishedYearGreaterThanEqual(Integer year);
    
    List<Book> findTop10ByOrderByPriceDesc();
    
    // Query Methods - Complex
    
    List<Book> findByAuthorIdAndPriceGreaterThan(Long authorId, BigDecimal price);
    
    List<Book> findByTitleContainingIgnoreCaseOrderByPublishedYearDesc(String keyword);
    
    @Query("SELECT b FROM Book b WHERE b.stock < :threshold")
    List<Book> findLowStockBooks(@Param("threshold") Integer threshold);
    
    // Custom JPQL Queries
    
    @Query("SELECT b FROM Book b JOIN FETCH b.author WHERE b.id = :id")
    Optional<Book> findByIdWithAuthor(@Param("id") Long id);
    
    @Query("SELECT b FROM Book b JOIN FETCH b.author JOIN FETCH b.categories WHERE b.id = :id")
    Optional<Book> findByIdWithDetails(@Param("id") Long id);
    
    @Query("SELECT b FROM Book b JOIN b.categories c WHERE c.name = :categoryName")
    List<Book> findByCategoryName(@Param("categoryName") String categoryName);
    
    @Query("SELECT b FROM Book b WHERE b.author.name = :authorName AND b.publishedYear BETWEEN :startYear AND :endYear")
    List<Book> findByAuthorNameAndYearRange(
        @Param("authorName") String authorName,
        @Param("startYear") Integer startYear,
        @Param("endYear") Integer endYear
    );
    
    // Aggregate Queries
    
    @Query("SELECT AVG(b.price) FROM Book b WHERE b.author.id = :authorId")
    BigDecimal findAveragePriceByAuthor(@Param("authorId") Long authorId);
    
    @Query("SELECT b.publishedYear, COUNT(b) FROM Book b GROUP BY b.publishedYear ORDER BY b.publishedYear DESC")
    List<Object[]> countBooksByYear();
    
    // Modifying Queries
    
    @Modifying
    @Query("UPDATE Book b SET b.price = b.price * :factor WHERE b.author.id = :authorId")
    int updatePricesByAuthor(@Param("authorId") Long authorId, @Param("factor") BigDecimal factor);
    
    @Modifying
    @Query("UPDATE Book b SET b.stock = b.stock + :quantity WHERE b.id = :id")
    int addStock(@Param("id") Long id, @Param("quantity") Integer quantity);
    
    @Modifying
    @Query("DELETE FROM Book b WHERE b.publishedYear < :year")
    int deleteOldBooks(@Param("year") Integer year);
    
    // Native SQL Queries
    
    @Query(value = "SELECT b.* FROM books b " +
                   "JOIN authors a ON b.author_id = a.id " +
                   "WHERE a.country = :country " +
                   "ORDER BY b.published_year DESC",
           nativeQuery = true)
    List<Book> findBooksByAuthorCountry(@Param("country") String country);
    
    @Query(value = "SELECT b.published_year, COUNT(*) as count, AVG(b.price) as avg_price " +
                   "FROM books b " +
                   "GROUP BY b.published_year " +
                   "HAVING COUNT(*) > :minCount",
           nativeQuery = true)
    List<Object[]> getBookStatistics(@Param("minCount") int minCount);
}
