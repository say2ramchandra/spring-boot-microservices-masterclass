package com.masterclass.annotations.repository;

import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public class UserRepository {
    
    private final Map<Long, String> users = new HashMap<>();
    private Long idCounter = 1L;

    public UserRepository() {
        System.out.println("   UserRepository created");
    }

    public Long save(String username) {
        Long id = idCounter++;
        users.put(id, username);
        return id;
    }

    public Optional<String> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    public List<String> findAll() {
        return new ArrayList<>(users.values());
    }
}
