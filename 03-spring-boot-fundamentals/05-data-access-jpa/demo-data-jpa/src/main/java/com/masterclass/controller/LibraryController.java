package com.masterclass.controller;

import com.masterclass.model.Author;
import com.masterclass.model.Book;
import com.masterclass.model.Category;
import com.masterclass.service.LibraryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Library REST Controller
 * Demonstrates CRUD operations with JPA entities
 */
@RestController
@RequestMapping("/api/library")
@RequiredArgsConstructor
public class LibraryController {

    private final LibraryService libraryService;

    // ==================== Author Endpoints ====================

    @GetMapping("/authors")
    public ResponseEntity<List<Author>> getAllAuthors() {
        return ResponseEntity.ok(libraryService.getAllAuthors());
    }

    @GetMapping("/authors/{id}")
    public ResponseEntity<Author> getAuthor(@PathVariable Long id) {
        return libraryService.getAuthorById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/authors/{id}/with-books")
    public ResponseEntity<Author> getAuthorWithBooks(@PathVariable Long id) {
        return libraryService.getAuthorWithBooks(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/authors/country/{country}")
    public ResponseEntity<List<Author>> getAuthorsByCountry(@PathVariable String country) {
        return ResponseEntity.ok(libraryService.getAuthorsByCountry(country));
    }

    @PostMapping("/authors")
    public ResponseEntity<Author> createAuthor(@RequestBody Author author) {
        Author saved = libraryService.saveAuthor(author);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @DeleteMapping("/authors/{id}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
        libraryService.deleteAuthor(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== Book Endpoints ====================

    @GetMapping("/books")
    public ResponseEntity<List<Book>> getAllBooks() {
        return ResponseEntity.ok(libraryService.getAllBooks());
    }

    @GetMapping("/books/{id}")
    public ResponseEntity<Book> getBook(@PathVariable Long id) {
        return libraryService.getBookById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/books/{id}/details")
    public ResponseEntity<Book> getBookWithDetails(@PathVariable Long id) {
        return libraryService.getBookWithDetails(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/books/search")
    public ResponseEntity<List<Book>> searchBooks(@RequestParam String keyword) {
        return ResponseEntity.ok(libraryService.searchBooks(keyword));
    }

    @GetMapping("/books/price-range")
    public ResponseEntity<List<Book>> getBooksByPriceRange(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice) {
        return ResponseEntity.ok(libraryService.getBooksByPriceRange(minPrice, maxPrice));
    }

    @GetMapping("/books/by-author/{authorId}")
    public ResponseEntity<List<Book>> getBooksByAuthor(@PathVariable Long authorId) {
        return ResponseEntity.ok(libraryService.getBooksByAuthor(authorId));
    }

    @PostMapping("/books")
    public ResponseEntity<Book> createBook(@RequestBody Book book) {
        Book saved = libraryService.saveBook(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @DeleteMapping("/books/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        libraryService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== Category Endpoints ====================

    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(libraryService.getAllCategories());
    }

    @GetMapping("/categories/{id}")
    public ResponseEntity<Category> getCategory(@PathVariable Long id) {
        return libraryService.getCategoryById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/categories")
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        Category saved = libraryService.saveCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        libraryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== Complex Operations ====================

    @PutMapping("/books/{bookId}/stock")
    public ResponseEntity<String> addBookStock(
            @PathVariable Long bookId,
            @RequestParam Integer quantity) {
        libraryService.addBookStock(bookId, quantity);
        return ResponseEntity.ok("Stock updated successfully");
    }

    @PutMapping("/authors/{authorId}/increase-prices")
    public ResponseEntity<String> increasePrices(
            @PathVariable Long authorId,
            @RequestParam BigDecimal percentage) {
        int updated = libraryService.increasePricesForAuthor(authorId, percentage);
        return ResponseEntity.ok(updated + " book(s) updated");
    }

    @PutMapping("/books/{bookId}/transfer-author/{newAuthorId}")
    public ResponseEntity<Book> transferBook(
            @PathVariable Long bookId,
            @PathVariable Long newAuthorId) {
        Book updated = libraryService.transferBookToAuthor(bookId, newAuthorId);
        return ResponseEntity.ok(updated);
    }

    // ==================== Statistics ====================

    @GetMapping("/statistics")
    public ResponseEntity<String> getStatistics() {
        long authors = libraryService.getTotalAuthors();
        long books = libraryService.getTotalBooks();
        long categories = libraryService.getTotalCategories();
        
        String stats = String.format(
            "Library Statistics:\n" +
            "- Total Authors: %d\n" +
            "- Total Books: %d\n" +
            "- Total Categories: %d",
            authors, books, categories
        );
        
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/authors/{authorId}/average-price")
    public ResponseEntity<BigDecimal> getAveragePrice(@PathVariable Long authorId) {
        BigDecimal avgPrice = libraryService.getAveragePriceByAuthor(authorId);
        return ResponseEntity.ok(avgPrice);
    }
}
