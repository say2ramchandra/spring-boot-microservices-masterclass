package com.masterclass.annotations.service;

import lombok.Getter;

@Getter
public class DatabaseService {
    private final String name;
    private final String url;

    public DatabaseService(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public void connect() {
        System.out.println("   Connected to " + name + ": " + url);
    }
}
