package com.masterclass.spring.service;

import com.masterclass.spring.repository.UserRepository;
import org.springframework.stereotype.Service;

/**
 * UserService - Business logic layer
 * 
 * Demonstrates SIMPLE CONSTRUCTOR INJECTION with single dependency.
 * 
 * Key Points:
 * 1. Dependency is FINAL - immutable
 * 2. @Autowired is OPTIONAL (Spring 4.3+)
 * 3. Clear contract - you see exactly what this service needs
 * 4. Easy to test - just pass a mock to constructor
 * 
 * @Service annotation:
 * - Marks this as a Spring-managed bean
 * - Indicates it contains business logic
 * - Specialized form of @Component
 */
@Service
public class UserService {

    // FINAL = immutable = thread-safe = good practice
    private final UserRepository userRepository;

    /**
     * Constructor Injection
     * 
     * @Autowired is OPTIONAL here because there's only one constructor.
     * Spring automatically injects dependencies for single constructor.
     * 
     * @param userRepository injected by Spring container
     */
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        System.out.println("  ✓ UserService created with UserRepository injected");
    }

    /**
     * Business method that uses the injected repository
     */
    public String createUser(String email, String name) {
        System.out.println("\n[UserService] Creating user: " + name);
        
        // Use the injected repository
        return userRepository.save(email, name);
    }

    /**
     * Another business method
     */
    public String findUser(Long id) {
        System.out.println("\n[UserService] Finding user by ID: " + id);
        return userRepository.findById(id);
    }
}
