package com.example.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Global Logging Filter
 * 
 * Logs all incoming requests and outgoing responses.
 * Demonstrates custom GlobalFilter implementation.
 */
@Slf4j
@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        // Log request details
        log.info("📥 Incoming Request - Method: {}, URI: {}, Headers: {}", 
                request.getMethod(), 
                request.getURI(),
                request.getHeaders().keySet());

        long startTime = System.currentTimeMillis();

        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            ServerHttpResponse response = exchange.getResponse();
            long duration = System.currentTimeMillis() - startTime;
            
            // Log response details
            log.info("📤 Outgoing Response - Status: {}, Duration: {}ms, Path: {}", 
                    response.getStatusCode(),
                    duration,
                    request.getPath());
        }));
    }

    @Override
    public int getOrder() {
        return -1; // Execute first among global filters
    }
}
