package com.masterclass.controller;

import com.masterclass.entity.User;
import com.masterclass.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User REST Controller
 * Demonstrates exception handling in action
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Get all users
     * GET /api/users
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Get user by ID
     * GET /api/users/{id}
     * 
     * Try: GET /api/users/999 → 404 Not Found
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * Get user by email
     * GET /api/users/email/{email}
     * 
     * Try: GET /api/users/email/nonexistent@test.com → 404 Not Found
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        User user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    /**
     * Create new user
     * POST /api/users
     * 
     * Try with invalid data → 400 Validation Error
     * Try with duplicate email → 409 Conflict
     */
    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        User created = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Update user
     * PUT /api/users/{id}
     * 
     * Try with non-existent ID → 404 Not Found
     * Try with duplicate email → 409 Conflict
     * Try with invalid data → 400 Validation Error
     */
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody User user) {
        User updated = userService.updateUser(id, user);
        return ResponseEntity.ok(updated);
    }

    /**
     * Delete user
     * DELETE /api/users/{id}
     * 
     * Try with non-existent ID → 404 Not Found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "User deleted successfully");
        response.put("id", id.toString());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Check if user exists
     * GET /api/users/{id}/exists
     */
    @GetMapping("/{id}/exists")
    public ResponseEntity<Map<String, Boolean>> checkUserExists(@PathVariable Long id) {
        boolean exists = userService.userExists(id);
        
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get user count
     * GET /api/users/count
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getUserCount() {
        long count = userService.getUserCount();
        
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Test endpoint that throws an exception
     * GET /api/users/test/error
     * 
     * Demonstrates generic exception handling → 500 Internal Server Error
     */
    @GetMapping("/test/error")
    public ResponseEntity<String> testError() {
        throw new RuntimeException("This is a test exception!");
    }
}
