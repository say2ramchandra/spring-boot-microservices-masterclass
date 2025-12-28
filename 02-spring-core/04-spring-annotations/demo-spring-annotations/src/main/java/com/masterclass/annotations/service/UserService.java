package com.masterclass.annotations.service;

import com.masterclass.annotations.repository.UserRepository;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Service
public class UserService {
    
    private final UserRepository repository;

    // Constructor injection (recommended)
    public UserService(UserRepository repository) {
        this.repository = repository;
        System.out.println("   UserService created with repository");
    }

    @PostConstruct
    public void init() {
        System.out.println("   UserService initialized (@PostConstruct)");
    }

    @PreDestroy
    public void cleanup() {
        System.out.println("   UserService cleanup (@PreDestroy)");
    }

    public Long createUser(String username) {
        return repository.save(username);
    }

    public String getUser(Long id) {
        return repository.findById(id).orElse("Not found");
    }
}
