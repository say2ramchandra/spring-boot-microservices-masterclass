package com.masterclass.context.config;

import com.masterclass.context.service.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@ComponentScan("com.masterclass.context")
public class AppConfig {

    @Bean
    public UserService userService() {
        return new UserService();
    }

    @Bean
    @Profile("dev")
    public DatabaseService devDatabaseService() {
        System.out.println("   Creating DEV database service");
        return new DatabaseService("H2 In-Memory Database");
    }

    @Bean
    @Profile("prod")
    public DatabaseService prodDatabaseService() {
        System.out.println("   Creating PROD database service");
        return new DatabaseService("PostgreSQL Production Database");
    }

    @Bean
    public NotificationService notificationService() {
        return new NotificationService();
    }
}
