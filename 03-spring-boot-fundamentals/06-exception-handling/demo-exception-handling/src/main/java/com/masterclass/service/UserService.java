package com.masterclass.service;

import com.masterclass.entity.User;
import com.masterclass.exception.DuplicateResourceException;
import com.masterclass.exception.ResourceNotFoundException;
import com.masterclass.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * User Service
 * Contains business logic with exception handling
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    /**
     * Get all users
     */
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        log.info("Fetching all users");
        return userRepository.findAll();
    }

    /**
     * Get user by ID
     * Throws ResourceNotFoundException if not found
     */
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        log.info("Fetching user with ID: {}", id);
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    /**
     * Get user by email
     * Throws ResourceNotFoundException if not found
     */
    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        log.info("Fetching user with email: {}", email);
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    /**
     * Create new user
     * Throws DuplicateResourceException if email already exists
     */
    public User createUser(User user) {
        log.info("Creating user: {}", user.getEmail());
        
        // Check for duplicate email
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicateResourceException("User", "email", user.getEmail());
        }
        
        return userRepository.save(user);
    }

    /**
     * Update user
     * Throws ResourceNotFoundException if user not found
     * Throws DuplicateResourceException if email already exists for another user
     */
    public User updateUser(Long id, User userDetails) {
        log.info("Updating user with ID: {}", id);
        
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        
        // Check if email is being changed and if it already exists
        if (!user.getEmail().equals(userDetails.getEmail()) && 
            userRepository.existsByEmail(userDetails.getEmail())) {
            throw new DuplicateResourceException("User", "email", userDetails.getEmail());
        }
        
        // Update fields
        user.setName(userDetails.getName());
        user.setEmail(userDetails.getEmail());
        user.setAge(userDetails.getAge());
        user.setPhone(userDetails.getPhone());
        
        return userRepository.save(user);
    }

    /**
     * Delete user
     * Throws ResourceNotFoundException if not found
     */
    public void deleteUser(Long id) {
        log.info("Deleting user with ID: {}", id);
        
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", "id", id);
        }
        
        userRepository.deleteById(id);
    }

    /**
     * Check if user exists
     */
    @Transactional(readOnly = true)
    public boolean userExists(Long id) {
        return userRepository.existsById(id);
    }

    /**
     * Get user count
     */
    @Transactional(readOnly = true)
    public long getUserCount() {
        return userRepository.count();
    }
}
