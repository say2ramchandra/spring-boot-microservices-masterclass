package com.masterclass.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Type-safe configuration properties demonstrating @ConfigurationProperties.
 * 
 * Binds all properties under "app" prefix to this POJO.
 * Provides validation, IDE autocomplete, and type safety.
 */
@ConfigurationProperties(prefix = "app")
@Validated
public class AppProperties {

    @NotBlank(message = "Application name is required")
    private String name;

    @NotBlank(message = "Version is required")
    @Pattern(regexp = "\\d+\\.\\d+\\.\\d+", message = "Version must follow semantic versioning (x.y.z)")
    private String version;

    private String description;

    @NotBlank(message = "Environment is required")
    private String environment;

    private boolean debugEnabled = false;

    @Valid
    @NotNull(message = "Features configuration is required")
    private Features features = new Features();

    @Valid
    @NotNull(message = "Connection configuration is required")
    private Connection connection = new Connection();

    @Valid
    @NotNull(message = "API configuration is required")
    private Api api = new Api();

    // Nested class for features
    public static class Features {
        @NotEmpty(message = "At least one feature must be enabled")
        private List<String> enabled = new ArrayList<>();

        public List<String> getEnabled() {
            return enabled;
        }

        public void setEnabled(List<String> enabled) {
            this.enabled = enabled;
        }
    }

    // Nested class for connection settings
    public static class Connection {
        @Min(value = 1, message = "Max pool size must be at least 1")
        @Max(value = 100, message = "Max pool size cannot exceed 100")
        private int maxPoolSize = 20;

        @Min(value = 1, message = "Min pool size must be at least 1")
        private int minPoolSize = 5;

        @Min(value = 1000, message = "Timeout must be at least 1000ms")
        @Max(value = 60000, message = "Timeout cannot exceed 60000ms")
        private long timeout = 30000;

        public int getMaxPoolSize() {
            return maxPoolSize;
        }

        public void setMaxPoolSize(int maxPoolSize) {
            this.maxPoolSize = maxPoolSize;
        }

        public int getMinPoolSize() {
            return minPoolSize;
        }

        public void setMinPoolSize(int minPoolSize) {
            this.minPoolSize = minPoolSize;
        }

        public long getTimeout() {
            return timeout;
        }

        public void setTimeout(long timeout) {
            this.timeout = timeout;
        }
    }

    // Nested class for API configuration
    public static class Api {
        @NotBlank(message = "API endpoint is required")
        @Pattern(regexp = "^https?://.*", message = "API endpoint must be a valid HTTP(S) URL")
        private String endpoint;

        @Min(value = 1000, message = "API timeout must be at least 1000ms")
        private int timeout = 5000;

        @Min(value = 0, message = "Retry count cannot be negative")
        @Max(value = 10, message = "Retry count cannot exceed 10")
        private int retryCount = 3;

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        public int getTimeout() {
            return timeout;
        }

        public void setTimeout(int timeout) {
            this.timeout = timeout;
        }

        public int getRetryCount() {
            return retryCount;
        }

        public void setRetryCount(int retryCount) {
            this.retryCount = retryCount;
        }
    }

    // Getters and Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public boolean isDebugEnabled() {
        return debugEnabled;
    }

    public void setDebugEnabled(boolean debugEnabled) {
        this.debugEnabled = debugEnabled;
    }

    public Features getFeatures() {
        return features;
    }

    public void setFeatures(Features features) {
        this.features = features;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public Api getApi() {
        return api;
    }

    public void setApi(Api api) {
        this.api = api;
    }
}
