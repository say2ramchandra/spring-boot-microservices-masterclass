package com.masterclass.annotations.config;

import com.masterclass.annotations.service.DatabaseService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

@Configuration
@PropertySource("classpath:application.properties")
@ComponentScan(basePackages = "com.masterclass.annotations")
public class AppConfig {

    @Value("${database.url}")
    private String databaseUrl;

    @Bean
    @Primary
    public DatabaseService primaryDatabase() {
        System.out.println("   Creating PRIMARY database: " + databaseUrl);
        return new DatabaseService("Primary-DB", databaseUrl);
    }

    @Bean
    @Profile("dev")
    public DatabaseService devDatabase() {
        System.out.println("   Creating DEV database");
        return new DatabaseService("Dev-DB", "jdbc:h2:mem:devdb");
    }

    @Bean
    @Profile("prod")
    public DatabaseService prodDatabase() {
        System.out.println("   Creating PROD database");
        return new DatabaseService("Prod-DB", "jdbc:postgresql://prod-server/db");
    }
}
