package com.masterclass.aop.service;

import org.springframework.stereotype.Service;

@Service
public class UserService {

    public String createUser(String username) {
        System.out.println("   [Business Logic] Creating user: " + username);
        return "User-" + username;
    }

    public void updateUser(Long id, String username) {
        System.out.println("   [Business Logic] Updating user " + id + ": " + username);
    }

    public String getUser(Long id) {
        System.out.println("   [Business Logic] Getting user: " + id);
        return "User-" + id;
    }

    public void deleteUser(Long id) {
        System.out.println("   [Business Logic] Deleting user: " + id);
        if (id == 999L) {
            throw new RuntimeException("Cannot delete user 999");
        }
    }

    public int calculateDiscount(int amount) {
        System.out.println("   [Business Logic] Calculating discount for amount: " + amount);
        try {
            Thread.sleep(500); // Simulate slow operation
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return amount / 10;
    }
}
