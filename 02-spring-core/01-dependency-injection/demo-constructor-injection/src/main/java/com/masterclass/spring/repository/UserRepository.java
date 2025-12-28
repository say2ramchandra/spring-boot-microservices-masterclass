package com.masterclass.spring.repository;

import org.springframework.stereotype.Repository;

/**
 * UserRepository - Data access layer
 * 
 * @Repository annotation:
 * - Marks this as a Spring-managed bean
 * - Indicates it's a DAO (Data Access Object)
 * - Enables exception translation for data access exceptions
 * 
 * In real applications, this would interact with a database.
 * Here we simulate database operations.
 */
@Repository
public class UserRepository {

    /**
     * Simulate saving a user to database
     */
    public String save(String email, String name) {
        System.out.println("  [Repository] Saving user to database...");
        // In real app: INSERT INTO users (email, name) VALUES (?, ?)
        return "✅ User '" + name + "' saved with email: " + email;
    }

    /**
     * Simulate finding a user by ID
     */
    public String findById(Long id) {
        System.out.println("  [Repository] Fetching user from database...");
        // In real app: SELECT * FROM users WHERE id = ?
        return "✅ Found user with ID: " + id;
    }

    /**
     * Simulate finding a user by email
     */
    public String findByEmail(String email) {
        System.out.println("  [Repository] Searching user by email...");
        return "✅ Found user: " + email;
    }
}
