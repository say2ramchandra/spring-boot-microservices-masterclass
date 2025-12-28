package com.masterclass.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Author Entity
 * Demonstrates @OneToMany relationship with Book
 */
@Entity
@Table(name = "authors", indexes = {
    @Index(name = "idx_author_name", columnList = "name")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "books")  // Avoid circular reference in toString
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 50)
    private String country;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(length = 500)
    private String bio;

    // OneToMany: One author has many books
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference  // Prevents infinite recursion in JSON
    private List<Book> books = new ArrayList<>();

    // Helper method to maintain bidirectional relationship
    public void addBook(Book book) {
        books.add(book);
        book.setAuthor(this);
    }

    public void removeBook(Book book) {
        books.remove(book);
        book.setAuthor(null);
    }

    public Author(String name, String country, LocalDate birthDate) {
        this.name = name;
        this.country = country;
        this.birthDate = birthDate;
    }
}
