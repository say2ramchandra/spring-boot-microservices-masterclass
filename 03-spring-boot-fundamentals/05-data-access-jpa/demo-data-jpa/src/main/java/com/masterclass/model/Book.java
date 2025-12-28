package com.masterclass.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * Book Entity
 * Demonstrates @ManyToOne relationship with Author
 * and @ManyToMany relationship with Category
 */
@Entity
@Table(name = "books", indexes = {
    @Index(name = "idx_book_title", columnList = "title"),
    @Index(name = "idx_book_isbn", columnList = "isbn", unique = true)
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"author", "categories"})  // Avoid circular reference
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 200, message = "Title must be between 1 and 200 characters")
    @Column(nullable = false, length = 200)
    private String title;

    @NotBlank(message = "ISBN is required")
    @Pattern(regexp = "^(?:ISBN(?:-1[03])?:? )?(?=[0-9X]{10}$|(?=(?:[0-9]+[- ]){3})[- 0-9X]{13}$|97[89][0-9]{10}$|(?=(?:[0-9]+[- ]){4})[- 0-9]{17}$)(?:97[89][- ]?)?[0-9]{1,5}[- ]?[0-9]+[- ]?[0-9]+[- ]?[0-9X]$", 
             message = "Invalid ISBN format")
    @Column(unique = true, nullable = false, length = 20)
    private String isbn;

    @Column(length = 1000)
    private String description;

    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @Min(value = 1900, message = "Published year must be after 1900")
    @Max(value = 2100, message = "Published year cannot be in the future")
    @Column(name = "published_year")
    private Integer publishedYear;

    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock = 0;

    // ManyToOne: Many books belong to one author
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    @JsonBackReference  // Prevents infinite recursion in JSON
    private Author author;

    // ManyToMany: Many books belong to many categories
    @ManyToMany
    @JoinTable(
        name = "book_category",
        joinColumns = @JoinColumn(name = "book_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>();

    // Helper methods for bidirectional relationships
    public void addCategory(Category category) {
        categories.add(category);
        category.getBooks().add(this);
    }

    public void removeCategory(Category category) {
        categories.remove(category);
        category.getBooks().remove(this);
    }

    public Book(String title, String isbn, BigDecimal price, Integer publishedYear) {
        this.title = title;
        this.isbn = isbn;
        this.price = price;
        this.publishedYear = publishedYear;
    }
}
