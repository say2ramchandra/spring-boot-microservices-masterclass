package com.example.gateway.config;

import com.example.gateway.filter.AuthenticationFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Gateway Route Configuration
 * 
 * Defines routes programmatically (alternative to YAML configuration).
 * This demonstrates Java-based route configuration with filters.
 */
@Configuration
public class GatewayConfig {

    private final AuthenticationFilter authFilter;

    public GatewayConfig(AuthenticationFilter authFilter) {
        this.authFilter = authFilter;
    }

    /**
     * Programmatic route configuration
     * Uncomment to use instead of YAML routes
     */
    // @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Product Service Route
                .route("product-service", r -> r
                        .path("/api/products/**")
                        .filters(f -> f
                                .stripPrefix(0)
                                .addRequestHeader("X-Gateway-Request", "API-Gateway")
                                .circuitBreaker(config -> config
                                        .setName("productCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/product"))
                                .retry(retryConfig -> retryConfig.setRetries(3)))
                        .uri("lb://product-service"))
                
                // Order Service Route
                .route("order-service", r -> r
                        .path("/api/orders/**")
                        .filters(f -> f
                                .stripPrefix(0)
                                .addRequestHeader("X-Gateway-Request", "API-Gateway")
                                .circuitBreaker(config -> config
                                        .setName("orderCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/order"))
                                .retry(retryConfig -> retryConfig.setRetries(3)))
                        .uri("lb://order-service"))
                
                // User Service Route (Authenticated)
                .route("user-service", r -> r
                        .path("/api/users/**")
                        .filters(f -> f
                                .filter(authFilter.apply(new AuthenticationFilter.Config()))
                                .addRequestHeader("X-Gateway-Request", "API-Gateway"))
                        .uri("lb://user-service"))
                
                .build();
    }
}
