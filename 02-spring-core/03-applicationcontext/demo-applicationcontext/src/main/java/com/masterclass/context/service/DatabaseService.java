package com.masterclass.context.service;

import lombok.Data;

@Data
public class DatabaseService {
    private final String databaseName;

    public DatabaseService(String databaseName) {
        this.databaseName = databaseName;
        System.out.println("   DatabaseService initialized: " + databaseName);
    }

    public void connect() {
        System.out.println("   Connected to: " + databaseName);
    }
}
