package com.masterclass.config;

import com.masterclass.entity.User;
import com.masterclass.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Data Initializer
 * Seeds the database with sample users
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    @Override
    public void run(String... args) {
        log.info("Initializing sample data...");

        List<User> users = Arrays.asList(
            new User("John Doe", "john.doe@example.com", 30, "+12025550101"),
            new User("Jane Smith", "jane.smith@example.com", 25, "+12025550102"),
            new User("Bob Johnson", "bob.johnson@example.com", 35, "+12025550103"),
            new User("Alice Williams", "alice.williams@example.com", 28, "+12025550104"),
            new User("Charlie Brown", "charlie.brown@example.com", 32, "+12025550105")
        );

        userRepository.saveAll(users);

        log.info("\n" +
            "========================================\n" +
            "✅ Sample Data Initialized!\n" +
            "========================================\n" +
            "Created {} users\n" +
            "========================================",
            userRepository.count()
        );

        log.info("\n=== Sample Users ===");
        userRepository.findAll().forEach(user ->
            log.info("ID: {}, Name: {}, Email: {}", user.getId(), user.getName(), user.getEmail())
        );
    }
}
