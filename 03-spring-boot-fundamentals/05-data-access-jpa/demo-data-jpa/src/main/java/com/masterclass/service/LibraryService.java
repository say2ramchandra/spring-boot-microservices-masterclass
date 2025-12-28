package com.masterclass.service;

import com.masterclass.model.Author;
import com.masterclass.model.Book;
import com.masterclass.model.Category;
import com.masterclass.repository.AuthorRepository;
import com.masterclass.repository.BookRepository;
import com.masterclass.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Library Service
 * Demonstrates transaction management and business logic
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class LibraryService {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;

    // ==================== Author Operations ====================

    public Author saveAuthor(Author author) {
        log.info("Saving author: {}", author.getName());
        return authorRepository.save(author);
    }

    @Transactional(readOnly = true)
    public List<Author> getAllAuthors() {
        return authorRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Author> getAuthorById(Long id) {
        return authorRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Author> getAuthorWithBooks(Long id) {
        return authorRepository.findByIdWithBooks(id);
    }

    @Transactional(readOnly = true)
    public List<Author> getAuthorsByCountry(String country) {
        return authorRepository.findByCountry(country);
    }

    public void deleteAuthor(Long id) {
        log.info("Deleting author with ID: {}", id);
        authorRepository.deleteById(id);
    }

    // ==================== Book Operations ====================

    public Book saveBook(Book book) {
        log.info("Saving book: {}", book.getTitle());
        return bookRepository.save(book);
    }

    @Transactional(readOnly = true)
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Book> getBookWithDetails(Long id) {
        return bookRepository.findByIdWithDetails(id);
    }

    @Transactional(readOnly = true)
    public List<Book> searchBooks(String keyword) {
        return bookRepository.findByTitleContainingIgnoreCase(keyword);
    }

    @Transactional(readOnly = true)
    public List<Book> getBooksByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return bookRepository.findByPriceBetween(minPrice, maxPrice);
    }

    @Transactional(readOnly = true)
    public List<Book> getBooksByAuthor(Long authorId) {
        return bookRepository.findByAuthorId(authorId);
    }

    public void deleteBook(Long id) {
        log.info("Deleting book with ID: {}", id);
        bookRepository.deleteById(id);
    }

    // ==================== Category Operations ====================

    public Category saveCategory(Category category) {
        log.info("Saving category: {}", category.getName());
        return categoryRepository.save(category);
    }

    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    public void deleteCategory(Long id) {
        log.info("Deleting category with ID: {}", id);
        categoryRepository.deleteById(id);
    }

    // ==================== Complex Operations ====================

    /**
     * Create a complete book with author and categories
     * Demonstrates transaction management - all or nothing
     */
    public Book createCompleteBook(Book book, Long authorId, List<Long> categoryIds) {
        log.info("Creating complete book: {}", book.getTitle());
        
        // Find author
        Author author = authorRepository.findById(authorId)
            .orElseThrow(() -> new RuntimeException("Author not found with ID: " + authorId));
        
        // Set author
        book.setAuthor(author);
        
        // Find and add categories
        for (Long categoryId : categoryIds) {
            Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + categoryId));
            book.addCategory(category);
        }
        
        // Save book (cascade will handle relationships)
        Book savedBook = bookRepository.save(book);
        log.info("Book created successfully with ID: {}", savedBook.getId());
        
        return savedBook;
    }

    /**
     * Update book stock
     * Demonstrates @Modifying query
     */
    public void addBookStock(Long bookId, Integer quantity) {
        log.info("Adding {} units to book ID: {}", quantity, bookId);
        int updated = bookRepository.addStock(bookId, quantity);
        if (updated == 0) {
            throw new RuntimeException("Book not found with ID: " + bookId);
        }
    }

    /**
     * Increase prices for all books by an author
     * Demonstrates batch update with transaction
     */
    public int increasePricesForAuthor(Long authorId, BigDecimal percentage) {
        log.info("Increasing prices by {}% for author ID: {}", percentage, authorId);
        BigDecimal factor = BigDecimal.ONE.add(percentage.divide(BigDecimal.valueOf(100)));
        int updated = bookRepository.updatePricesByAuthor(authorId, factor);
        log.info("Updated {} books", updated);
        return updated;
    }

    /**
     * Transfer book to a new author
     * Demonstrates complex transaction with multiple updates
     */
    public Book transferBookToAuthor(Long bookId, Long newAuthorId) {
        log.info("Transferring book ID {} to author ID {}", bookId, newAuthorId);
        
        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new RuntimeException("Book not found"));
        
        Author newAuthor = authorRepository.findById(newAuthorId)
            .orElseThrow(() -> new RuntimeException("Author not found"));
        
        // Remove from old author
        Author oldAuthor = book.getAuthor();
        if (oldAuthor != null) {
            oldAuthor.removeBook(book);
        }
        
        // Add to new author
        newAuthor.addBook(book);
        
        // Save changes
        Book savedBook = bookRepository.save(book);
        log.info("Book transferred successfully");
        
        return savedBook;
    }

    // ==================== Statistics ====================

    @Transactional(readOnly = true)
    public long getTotalAuthors() {
        return authorRepository.count();
    }

    @Transactional(readOnly = true)
    public long getTotalBooks() {
        return bookRepository.count();
    }

    @Transactional(readOnly = true)
    public long getTotalCategories() {
        return categoryRepository.count();
    }

    @Transactional(readOnly = true)
    public BigDecimal getAveragePriceByAuthor(Long authorId) {
        return bookRepository.findAveragePriceByAuthor(authorId);
    }
}
