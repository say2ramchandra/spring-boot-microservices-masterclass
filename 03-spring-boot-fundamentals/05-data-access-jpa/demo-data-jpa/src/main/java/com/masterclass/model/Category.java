package com.masterclass.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

/**
 * Category Entity
 * Demonstrates @ManyToMany relationship with Book
 */
@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "books")  // Avoid circular reference
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(length = 200)
    private String description;

    // ManyToMany: Many categories have many books
    @ManyToMany(mappedBy = "categories")
    private Set<Book> books = new HashSet<>();

    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
