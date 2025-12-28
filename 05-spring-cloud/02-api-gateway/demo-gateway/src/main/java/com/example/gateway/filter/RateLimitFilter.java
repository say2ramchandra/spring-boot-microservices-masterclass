package com.example.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simple In-Memory Rate Limiting Filter
 * 
 * Limits requests per IP address.
 * In production, use Redis-based rate limiting.
 */
@Slf4j
@Component
public class RateLimitFilter implements GlobalFilter, Ordered {

    private static final int MAX_REQUESTS = 10;
    private static final long TIME_WINDOW = 60000; // 1 minute

    private final Map<String, RequestCounter> requestCounts = new ConcurrentHashMap<>();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String clientIp = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
        
        RequestCounter counter = requestCounts.computeIfAbsent(clientIp, k -> new RequestCounter());
        
        // Clean up old entries
        if (System.currentTimeMillis() - counter.timestamp > TIME_WINDOW) {
            counter.count.set(0);
            counter.timestamp = System.currentTimeMillis();
        }

        int currentCount = counter.count.incrementAndGet();

        if (currentCount > MAX_REQUESTS) {
            log.warn("⚠️ Rate limit exceeded for IP: {} (Requests: {})", clientIp, currentCount);
            exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
            exchange.getResponse().getHeaders().add("X-Rate-Limit-Retry-After", "60");
            return exchange.getResponse().setComplete();
        }

        log.debug("✅ Request allowed for IP: {} (Count: {}/{})", clientIp, currentCount, MAX_REQUESTS);
        
        exchange.getResponse().getHeaders().add("X-Rate-Limit-Remaining", 
                String.valueOf(MAX_REQUESTS - currentCount));

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0; // Execute after logging filter
    }

    private static class RequestCounter {
        AtomicInteger count = new AtomicInteger(0);
        long timestamp = System.currentTimeMillis();
    }
}
