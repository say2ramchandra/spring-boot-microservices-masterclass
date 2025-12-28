package com.masterclass.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.*;

/**
 * Database configuration properties demonstrating nested configuration.
 * 
 * Shows how to bind complex, hierarchical properties from YAML/properties files.
 */
@ConfigurationProperties(prefix = "app.database")
@Validated
public class DatabaseProperties {

    @NotBlank(message = "Database host is required")
    private String host;

    @Min(value = 1, message = "Port must be at least 1")
    @Max(value = 65535, message = "Port must be at most 65535")
    private int port = 3306;

    @NotBlank(message = "Database name is required")
    private String name;

    @NotBlank(message = "Database username is required")
    private String username;

    @NotBlank(message = "Database password is required")
    private String password;

    // Getters and Setters

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Build JDBC connection URL
     */
    public String getJdbcUrl() {
        return String.format("jdbc:mysql://%s:%d/%s", host, port, name);
    }
}
