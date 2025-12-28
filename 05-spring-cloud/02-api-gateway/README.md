# Section 02: API Gateway with Spring Cloud Gateway

## Table of Contents
1. [Introduction](#introduction)
2. [What is an API Gateway](#what-is-an-api-gateway)
3. [Spring Cloud Gateway Architecture](#spring-cloud-gateway-architecture)
4. [Setting Up Gateway](#setting-up-gateway)
5. [Routing Configuration](#routing-configuration)
6. [Filters and Predicates](#filters-and-predicates)
7. [Security and Authentication](#security-and-authentication)
8. [Rate Limiting and Throttling](#rate-limiting-and-throttling)
9. [Best Practices](#best-practices)
10. [Interview Questions](#interview-questions)

---

## Introduction

In a microservices architecture, clients need to communicate with multiple services. Without an API Gateway, clients must know the location of each service and call them directly. This creates several problems.

**API Gateway** acts as a single entry point for all client requests, routing them to appropriate microservices.

### The Problem Without Gateway

```
Mobile App ──┬──> User Service (http://user-service:8081)
             ├──> Product Service (http://product-service:8082)
             ├──> Order Service (http://order-service:8083)
             └──> Payment Service (http://payment-service:8084)

Problems:
❌ Clients must know all service URLs
❌ CORS configuration on every service
❌ Authentication logic duplicated
❌ No centralized logging/monitoring
❌ Multiple round trips from client
❌ Service URLs exposed to clients
```

### Solution: API Gateway

```
                    ┌─────────────────┐
Mobile App ────────>│   API Gateway   │
Web App ───────────>│   (Port 8080)   │
                    └─────────────────┘
                            │
            ┌───────────────┼───────────────┐
            ↓               ↓               ↓
    ┌─────────────┐ ┌─────────────┐ ┌─────────────┐
    │User Service │ │Product Svc  │ │Order Service│
    │   (8081)    │ │   (8082)    │ │   (8083)    │
    └─────────────┘ └─────────────┘ └─────────────┘

Benefits:
✅ Single entry point
✅ Centralized authentication
✅ Request routing
✅ Load balancing
✅ Monitoring and logging
✅ Rate limiting
```

---

## What is an API Gateway

**API Gateway** is a server that acts as an intermediary between clients and microservices.

### Core Responsibilities

#### 1. Request Routing
```
GET /api/users/1       → User Service
GET /api/products/1    → Product Service
POST /api/orders       → Order Service
```

#### 2. Load Balancing
```
Request → Gateway → [Service Instance 1]
                    [Service Instance 2]  ← Choose one
                    [Service Instance 3]
```

#### 3. Authentication & Authorization
```
Request → Gateway → Verify JWT token → Route to service
                 ↓
              Unauthorized (401)
```

#### 4. Request/Response Transformation
```
Request:  Legacy XML format
Gateway:  Transform to JSON
Service:  Receives JSON
```

#### 5. Rate Limiting
```
Client X: 100 requests/minute allowed
Request 101: ❌ Rejected (429 Too Many Requests)
```

### Gateway Patterns

#### Pattern 1: Backend for Frontend (BFF)

```
┌─────────────┐
│ Mobile App  │──> Mobile API Gateway ──> Mobile-optimized responses
└─────────────┘

┌─────────────┐
│   Web App   │──> Web API Gateway ───> Web-optimized responses
└─────────────┘

┌─────────────┐
│  Admin App  │──> Admin API Gateway ──> Full data access
└─────────────┘
```

#### Pattern 2: Aggregation

```
Client requests: /api/order/123

Gateway:
  1. Calls Order Service → Order details
  2. Calls Product Service → Product info
  3. Calls User Service → User info
  4. Aggregates all data
  5. Returns combined response

Single API call → Multiple backend calls
```

---

## Spring Cloud Gateway Architecture

### Components

```
┌───────────────────────────────────────────────────────┐
│              Spring Cloud Gateway                      │
│                                                        │
│  ┌──────────────────────────────────────────────┐    │
│  │            Route Locator                      │    │
│  │  (Defines routes to services)                 │    │
│  └──────────────────────────────────────────────┘    │
│                      ↓                                 │
│  ┌──────────────────────────────────────────────┐    │
│  │         Gateway Handler Mapping               │    │
│  │  (Matches incoming requests to routes)        │    │
│  └──────────────────────────────────────────────┘    │
│                      ↓                                 │
│  ┌──────────────────────────────────────────────┐    │
│  │         Gateway Web Handler                   │    │
│  │  (Executes filter chain)                      │    │
│  └──────────────────────────────────────────────┘    │
│                      ↓                                 │
│  ┌──────────────────────────────────────────────┐    │
│  │           Filter Chain                        │    │
│  │  PreFilters → Route → PostFilters            │    │
│  └──────────────────────────────────────────────┘    │
└───────────────────────────────────────────────────────┘
                       ↓
            Proxied to Microservice
```

### Request Flow

```
1. Client sends request to Gateway
   GET http://gateway:8080/api/products/1

2. Gateway Handler Mapping finds matching route
   Route: /api/products/** → product-service

3. PreFilters execute (Request modification)
   - Add headers
   - Authenticate
   - Log request

4. Gateway proxies request to service
   GET http://product-service:8082/api/products/1

5. Service processes and responds

6. PostFilters execute (Response modification)
   - Add headers
   - Transform response
   - Log response

7. Gateway returns response to client
```

### Key Concepts

#### 1. Route
Defines how to match and route requests.

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: product-route
          uri: lb://product-service
          predicates:
            - Path=/api/products/**
```

#### 2. Predicate
Conditions to match requests.

```yaml
predicates:
  - Path=/api/products/**     # Match path
  - Method=GET,POST           # Match HTTP method
  - Header=X-Request-Id       # Match header
  - Query=userId              # Match query param
```

#### 3. Filter
Modify request/response.

```yaml
filters:
  - AddRequestHeader=X-Request-Source, Gateway
  - AddResponseHeader=X-Response-Time, 100ms
  - RewritePath=/api/(?<segment>.*), /${segment}
```

---

## Setting Up Gateway

### Step 1: Add Dependencies

```xml
<dependencies>
    <!-- Spring Cloud Gateway -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-gateway</artifactId>
    </dependency>
    
    <!-- Service Discovery -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
    
    <!-- Circuit Breaker -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-circuitbreaker-reactor-resilience4j</artifactId>
    </dependency>
    
    <!-- Actuator for monitoring -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
</dependencies>
```

### Step 2: Main Application

```java
package com.example.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class GatewayApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
```

### Step 3: Basic Configuration

**application.yml:**

```yaml
server:
  port: 8080

spring:
  application:
    name: api-gateway
  
  cloud:
    gateway:
      # Enable discovery locator
      discovery:
        locator:
          enabled: true
          lower-case-service-id: true
      
      # Define routes
      routes:
        # Product Service Route
        - id: product-service
          uri: lb://product-service
          predicates:
            - Path=/api/products/**
          filters:
            - StripPrefix=1
        
        # User Service Route
        - id: user-service
          uri: lb://user-service
          predicates:
            - Path=/api/users/**
          filters:
            - StripPrefix=1
        
        # Order Service Route
        - id: order-service
          uri: lb://order-service
          predicates:
            - Path=/api/orders/**
          filters:
            - StripPrefix=1

# Eureka Configuration
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
  instance:
    prefer-ip-address: true

# Logging
logging:
  level:
    org.springframework.cloud.gateway: DEBUG
    reactor.netty: DEBUG
```

---

## Routing Configuration

### Configuration Methods

#### Method 1: YAML Configuration (Recommended)

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: product-route
          uri: lb://product-service
          predicates:
            - Path=/api/products/**
          filters:
            - AddRequestHeader=X-Request-Source, Gateway
```

#### Method 2: Java Configuration

```java
@Configuration
public class GatewayConfig {
    
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("product-route", r -> r
                .path("/api/products/**")
                .filters(f -> f
                    .addRequestHeader("X-Request-Source", "Gateway")
                    .stripPrefix(1))
                .uri("lb://product-service"))
            
            .route("user-route", r -> r
                .path("/api/users/**")
                .filters(f -> f.stripPrefix(1))
                .uri("lb://user-service"))
            
            .build();
    }
}
```

### Advanced Routing

#### Path Rewriting

```yaml
routes:
  - id: rewrite-route
    uri: lb://product-service
    predicates:
      - Path=/api/v1/products/**
    filters:
      # Rewrite /api/v1/products/123 to /products/123
      - RewritePath=/api/v1/(?<segment>.*), /${segment}
```

#### Multiple Predicates (AND condition)

```yaml
routes:
  - id: admin-route
    uri: lb://admin-service
    predicates:
      - Path=/admin/**
      - Header=X-Admin-Token, ^[a-zA-Z0-9]+$
      - Method=GET,POST
```

#### Query Parameter Routing

```yaml
routes:
  - id: query-route
    uri: lb://product-service
    predicates:
      - Path=/api/products
      - Query=version, v2
    # Matches: /api/products?version=v2
```

#### Host-Based Routing

```yaml
routes:
  - id: host-route
    uri: lb://product-service
    predicates:
      - Host=**.example.com
    # Matches: api.example.com, app.example.com
```

#### Weight-Based Routing (Canary Deployment)

```yaml
routes:
  # 80% traffic to v1
  - id: product-v1
    uri: lb://product-service-v1
    predicates:
      - Path=/api/products/**
      - Weight=group1, 8
  
  # 20% traffic to v2
  - id: product-v2
    uri: lb://product-service-v2
    predicates:
      - Path=/api/products/**
      - Weight=group1, 2
```

---

## Filters and Predicates

### Built-in Predicates

#### 1. Path Predicate
```yaml
predicates:
  - Path=/api/products/**
```

#### 2. Method Predicate
```yaml
predicates:
  - Method=GET,POST
```

#### 3. Header Predicate
```yaml
predicates:
  - Header=X-Request-Id, \d+  # Header must match regex
```

#### 4. Query Predicate
```yaml
predicates:
  - Query=userId  # Query param must exist
  - Query=page, \d+  # Query param must match regex
```

#### 5. Cookie Predicate
```yaml
predicates:
  - Cookie=sessionId, ^[a-zA-Z0-9]+$
```

#### 6. RemoteAddr Predicate
```yaml
predicates:
  - RemoteAddr=192.168.1.0/24  # IP range
```

#### 7. Before/After/Between Predicates
```yaml
predicates:
  - Before=2024-12-31T23:59:59+00:00
  - After=2024-01-01T00:00:00+00:00
  - Between=2024-01-01T00:00:00+00:00, 2024-12-31T23:59:59+00:00
```

### Built-in Filters

#### 1. AddRequestHeader
```yaml
filters:
  - AddRequestHeader=X-Request-Source, Gateway
```

#### 2. AddResponseHeader
```yaml
filters:
  - AddResponseHeader=X-Response-Time, 100ms
```

#### 3. RemoveRequestHeader
```yaml
filters:
  - RemoveRequestHeader=X-Internal-Header
```

#### 4. SetPath
```yaml
filters:
  - SetPath=/api/{segment}
```

#### 5. RewritePath
```yaml
filters:
  - RewritePath=/api/(?<segment>.*), /${segment}
```

#### 6. StripPrefix
```yaml
# Request: /api/products/1
# After filter: /products/1
filters:
  - StripPrefix=1
```

#### 7. PrefixPath
```yaml
# Request: /products/1
# After filter: /api/v1/products/1
filters:
  - PrefixPath=/api/v1
```

#### 8. RedirectTo
```yaml
filters:
  - RedirectTo=302, https://new-domain.com
```

#### 9. Retry
```yaml
filters:
  - name: Retry
    args:
      retries: 3
      statuses: SERVICE_UNAVAILABLE
      methods: GET
      backoff:
        firstBackoff: 50ms
        maxBackoff: 500ms
```

#### 10. CircuitBreaker
```yaml
filters:
  - name: CircuitBreaker
    args:
      name: productServiceCB
      fallbackUri: forward:/fallback/products
```

### Custom Filters

#### Global Filter

```java
@Component
public class LoggingFilter implements GlobalFilter, Ordered {
    
    private static final Logger log = LoggerFactory.getLogger(LoggingFilter.class);
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().toString();
        String method = exchange.getRequest().getMethod().toString();
        
        log.info("Request: {} {}", method, path);
        
        return chain.filter(exchange)
            .then(Mono.fromRunnable(() -> {
                int statusCode = exchange.getResponse().getStatusCode().value();
                log.info("Response: {} {} - {}", method, path, statusCode);
            }));
    }
    
    @Override
    public int getOrder() {
        return -1;  // Execute early
    }
}
```

#### Gateway Filter Factory

```java
@Component
public class AddCorrelationIdFilterFactory extends AbstractGatewayFilterFactory<AddCorrelationIdFilterFactory.Config> {
    
    public AddCorrelationIdFilterFactory() {
        super(Config.class);
    }
    
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String correlationId = UUID.randomUUID().toString();
            
            ServerHttpRequest request = exchange.getRequest().mutate()
                .header("X-Correlation-ID", correlationId)
                .build();
            
            return chain.filter(exchange.mutate().request(request).build());
        };
    }
    
    public static class Config {
        // Configuration properties
    }
}
```

**Usage:**
```yaml
filters:
  - AddCorrelationId
```

---

## Security and Authentication

### JWT Authentication Filter

```java
@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        // Skip authentication for public endpoints
        if (isPublicEndpoint(request.getPath().toString())) {
            return chain.filter(exchange);
        }
        
        // Extract token from header
        String authHeader = request.getHeaders().getFirst("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        
        String token = authHeader.substring(7);
        
        // Validate token
        if (!jwtTokenProvider.validateToken(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        
        // Extract user info and add to request
        String userId = jwtTokenProvider.getUserIdFromToken(token);
        ServerHttpRequest modifiedRequest = request.mutate()
            .header("X-User-Id", userId)
            .build();
        
        return chain.filter(exchange.mutate().request(modifiedRequest).build());
    }
    
    private boolean isPublicEndpoint(String path) {
        return path.startsWith("/auth/") || 
               path.equals("/actuator/health");
    }
    
    @Override
    public int getOrder() {
        return -100;  // Execute before other filters
    }
}
```

### CORS Configuration

```java
@Configuration
public class CorsConfig {
    
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOrigins(Arrays.asList("http://localhost:3000", "https://app.example.com"));
        corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        corsConfig.setAllowedHeaders(Arrays.asList("*"));
        corsConfig.setAllowCredentials(true);
        corsConfig.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        
        return new CorsWebFilter(source);
    }
}
```

**Or in YAML:**

```yaml
spring:
  cloud:
    gateway:
      globalcors:
        corsConfigurations:
          '[/**]':
            allowedOrigins: 
              - "http://localhost:3000"
              - "https://app.example.com"
            allowedMethods:
              - GET
              - POST
              - PUT
              - DELETE
            allowedHeaders: "*"
            allowCredentials: true
            maxAge: 3600
```

---

## Rate Limiting and Throttling

### Redis-Based Rate Limiting

**Add Dependency:**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis-reactive</artifactId>
</dependency>
```

**Configuration:**

```yaml
spring:
  redis:
    host: localhost
    port: 6379
  
  cloud:
    gateway:
      routes:
        - id: product-route
          uri: lb://product-service
          predicates:
            - Path=/api/products/**
          filters:
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 10  # Tokens per second
                redis-rate-limiter.burstCapacity: 20  # Max tokens
                redis-rate-limiter.requestedTokens: 1  # Tokens per request
                key-resolver: "#{@userKeyResolver}"
```

**Key Resolver (Identify User):**

```java
@Configuration
public class RateLimitConfig {
    
    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> {
            // Rate limit by user ID
            String userId = exchange.getRequest().getHeaders().getFirst("X-User-Id");
            return Mono.just(userId != null ? userId : "anonymous");
        };
    }
    
    @Bean
    public KeyResolver ipKeyResolver() {
        return exchange -> {
            // Rate limit by IP address
            String ip = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
            return Mono.just(ip);
        };
    }
    
    @Bean
    public KeyResolver apiKeyResolver() {
        return exchange -> {
            // Rate limit by API key
            String apiKey = exchange.getRequest().getHeaders().getFirst("X-API-Key");
            return Mono.just(apiKey != null ? apiKey : "no-key");
        };
    }
}
```

### Custom Rate Limiting

```java
@Component
public class CustomRateLimitFilter implements GlobalFilter, Ordered {
    
    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String apiKey = exchange.getRequest().getHeaders().getFirst("X-API-Key");
        
        if (apiKey == null) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        
        Bucket bucket = cache.computeIfAbsent(apiKey, k -> createBucket());
        
        if (bucket.tryConsume(1)) {
            return chain.filter(exchange);
        } else {
            exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
            return exchange.getResponse().setComplete();
        }
    }
    
    private Bucket createBucket() {
        Bandwidth limit = Bandwidth.classic(100, Refill.greedy(100, Duration.ofMinutes(1)));
        return Bucket.builder().addLimit(limit).build();
    }
    
    @Override
    public int getOrder() {
        return -50;
    }
}
```

---

## Best Practices

### 1. Use Service Discovery

```yaml
# ✅ Good: Service discovery
spring:
  cloud:
    gateway:
      routes:
        - id: product-route
          uri: lb://product-service  # Load balanced via Eureka
          predicates:
            - Path=/api/products/**

# ❌ Bad: Hard-coded URL
spring:
  cloud:
    gateway:
      routes:
        - id: product-route
          uri: http://localhost:8081  # Brittle, no load balancing
          predicates:
            - Path=/api/products/**
```

### 2. Implement Circuit Breaker

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: product-route
          uri: lb://product-service
          predicates:
            - Path=/api/products/**
          filters:
            - name: CircuitBreaker
              args:
                name: productServiceCB
                fallbackUri: forward:/fallback/products

resilience4j:
  circuitbreaker:
    instances:
      productServiceCB:
        sliding-window-size: 10
        failure-rate-threshold: 50
        wait-duration-in-open-state: 10s
```

### 3. Enable Request/Response Logging

```java
@Component
public class RequestResponseLoggingFilter implements GlobalFilter, Ordered {
    
    private static final Logger log = LoggerFactory.getLogger(RequestResponseLoggingFilter.class);
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        long startTime = System.currentTimeMillis();
        
        ServerHttpRequest request = exchange.getRequest();
        log.info("Request: {} {} from {}", 
            request.getMethod(), 
            request.getPath(),
            request.getRemoteAddress());
        
        return chain.filter(exchange)
            .then(Mono.fromRunnable(() -> {
                long duration = System.currentTimeMillis() - startTime;
                log.info("Response: {} {} - {} ({}ms)",
                    request.getMethod(),
                    request.getPath(),
                    exchange.getResponse().getStatusCode(),
                    duration);
            }));
    }
    
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
```

### 4. Add Correlation ID

```java
@Component
public class CorrelationIdFilter implements GlobalFilter, Ordered {
    
    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String correlationId = exchange.getRequest().getHeaders().getFirst(CORRELATION_ID_HEADER);
        
        if (correlationId == null) {
            correlationId = UUID.randomUUID().toString();
        }
        
        ServerHttpRequest request = exchange.getRequest().mutate()
            .header(CORRELATION_ID_HEADER, correlationId)
            .build();
        
        return chain.filter(exchange.mutate().request(request).build());
    }
    
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
```

### 5. Configure Timeouts

```yaml
spring:
  cloud:
    gateway:
      httpclient:
        connect-timeout: 5000  # 5 seconds
        response-timeout: 30s  # 30 seconds
      
      routes:
        - id: slow-service
          uri: lb://slow-service
          predicates:
            - Path=/api/reports/**
          metadata:
            response-timeout: 60000  # 60 seconds for this route
            connect-timeout: 10000
```

### 6. Monitor Gateway Metrics

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  metrics:
    tags:
      application: ${spring.application.name}
  endpoint:
    health:
      show-details: always
```

**Access Metrics:**
```
http://localhost:8080/actuator/metrics/gateway.requests
http://localhost:8080/actuator/prometheus
```

### 7. Secure Admin Endpoints

```java
@Configuration
public class SecurityConfig {
    
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
            .authorizeExchange(exchanges -> exchanges
                .pathMatchers("/actuator/**").hasRole("ADMIN")
                .anyExchange().permitAll()
            )
            .httpBasic()
            .and()
            .build();
    }
}
```

---

## Interview Questions

### Q1: What is an API Gateway and why do we need it?

**Answer:**

**API Gateway** is a single entry point for all client requests in a microservices architecture.

**Without Gateway:**
```
Client must know all service locations:
- User Service: http://user-service:8081
- Product Service: http://product-service:8082
- Order Service: http://order-service:8083
- Payment Service: http://payment-service:8084

Problems:
❌ Client must track all service URLs
❌ Authentication logic in every service
❌ CORS configuration duplicated
❌ No centralized rate limiting
❌ Service URLs exposed to clients
```

**With Gateway:**
```
Client → API Gateway (http://gateway:8080)
         ↓
Gateway routes to appropriate service
```

**Benefits:**
1. **Single Entry Point**: Clients call one URL
2. **Routing**: Route requests based on path, headers, etc.
3. **Security**: Centralized authentication/authorization
4. **Rate Limiting**: Control request rates
5. **Monitoring**: Centralized logging and metrics
6. **Load Balancing**: Distribute requests across instances
7. **Protocol Translation**: REST to gRPC, etc.
8. **Response Aggregation**: Combine multiple service calls

---

### Q2: What is the difference between Spring Cloud Gateway and Zuul?

**Answer:**

| Feature | Spring Cloud Gateway | Zuul 1.x |
|---------|---------------------|----------|
| **Architecture** | Reactive (WebFlux) | Blocking (Servlet) |
| **Performance** | Better (async I/O) | Lower (thread per request) |
| **Thread Model** | Event loop | Thread pool |
| **Spring Boot** | 2.x+ | 1.x, 2.x |
| **Maintenance** | Active | Legacy |
| **Features** | More predicates/filters | Basic routing |

**Spring Cloud Gateway:**
```java
// Reactive, non-blocking
public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    return chain.filter(exchange);  // Async
}
```

**Zuul:**
```java
// Blocking, synchronous
public Object run() {
    // Thread blocked here
    return null;
}
```

**Recommendation**: Use **Spring Cloud Gateway** for new projects. Zuul is no longer actively developed.

---

### Q3: How does Spring Cloud Gateway handle routing?

**Answer:**

**Routing Process:**

```
1. Client sends request: GET /api/products/1

2. Gateway Handler Mapping matches route:
   - Checks all route predicates
   - Finds matching route (Path=/api/products/**)

3. Gateway Web Handler applies filters:
   - Pre-filters (modify request)
   - Route to service
   - Post-filters (modify response)

4. Load balancer selects instance:
   - Discovers service via Eureka
   - Applies load balancing algorithm
   - Selects: product-service-1 (192.168.1.10:8082)

5. Gateway proxies request:
   - Forward request to selected instance
   - Wait for response

6. Gateway returns response to client
```

**Configuration Example:**

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: product-route
          uri: lb://product-service  # Load balanced
          predicates:
            - Path=/api/products/**  # Match condition
          filters:
            - StripPrefix=1  # Remove /api
            - AddRequestHeader=X-Gateway, true
```

**Java Configuration:**
```java
@Bean
public RouteLocator routes(RouteLocatorBuilder builder) {
    return builder.routes()
        .route("product-route", r -> r
            .path("/api/products/**")
            .filters(f -> f
                .stripPrefix(1)
                .addRequestHeader("X-Gateway", "true"))
            .uri("lb://product-service"))
        .build();
}
```

---

### Q4: What are Gateway Filters and how do they work?

**Answer:**

**Gateway Filters** modify requests and responses passing through the gateway.

**Types:**

**1. GatewayFilter** (Route-specific):
```java
public interface GatewayFilter {
    Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain);
}
```

**2. GlobalFilter** (Applied to all routes):
```java
public interface GlobalFilter {
    Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain);
}
```

**Filter Execution Order:**

```
Request Flow:
  → Global PreFilters (ordered)
  → Route PreFilters
  → Proxy to Service
  → Route PostFilters
  → Global PostFilters (ordered)
  → Response to Client
```

**Example - Logging Filter:**

```java
@Component
public class LoggingFilter implements GlobalFilter, Ordered {
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // PRE-processing
        log.info("Request: {}", exchange.getRequest().getPath());
        
        return chain.filter(exchange)
            .then(Mono.fromRunnable(() -> {
                // POST-processing
                log.info("Response: {}", exchange.getResponse().getStatusCode());
            }));
    }
    
    @Override
    public int getOrder() {
        return -1;  // Execute early
    }
}
```

**Built-in Filters:**

- `AddRequestHeader`: Add header to request
- `AddResponseHeader`: Add header to response
- `RewritePath`: Rewrite request path
- `StripPrefix`: Remove path prefix
- `Retry`: Retry failed requests
- `CircuitBreaker`: Fault tolerance
- `RequestRateLimiter`: Rate limiting
- `RedirectTo`: Redirect requests

---

### Q5: How do you implement authentication in Spring Cloud Gateway?

**Answer:**

**Approach: JWT Authentication Filter**

**Step 1: Create Authentication Filter**

```java
@Component
public class AuthenticationFilter implements GlobalFilter, Ordered {
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        // Skip public endpoints
        if (isPublicEndpoint(request.getPath().toString())) {
            return chain.filter(exchange);
        }
        
        // Extract JWT token
        String authHeader = request.getHeaders().getFirst("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return onError(exchange, "Missing or invalid token", HttpStatus.UNAUTHORIZED);
        }
        
        String token = authHeader.substring(7);
        
        // Validate token
        if (!jwtTokenProvider.validateToken(token)) {
            return onError(exchange, "Invalid token", HttpStatus.UNAUTHORIZED);
        }
        
        // Extract user info and add to request headers
        Claims claims = jwtTokenProvider.getClaims(token);
        ServerHttpRequest modifiedRequest = request.mutate()
            .header("X-User-Id", claims.getSubject())
            .header("X-User-Roles", claims.get("roles", String.class))
            .build();
        
        return chain.filter(exchange.mutate().request(modifiedRequest).build());
    }
    
    private boolean isPublicEndpoint(String path) {
        return path.startsWith("/auth/") || 
               path.equals("/actuator/health");
    }
    
    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        
        String body = String.format("{\"error\":\"%s\"}", message);
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes());
        
        return response.writeWith(Mono.just(buffer));
    }
    
    @Override
    public int getOrder() {
        return -100;  // Execute early
    }
}
```

**Step 2: JWT Token Provider**

```java
@Component
public class JwtTokenProvider {
    
    @Value("${jwt.secret}")
    private String secret;
    
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
    
    public Claims getClaims(String token) {
        return Jwts.parser()
            .setSigningKey(secret)
            .parseClaimsJws(token)
            .getBody();
    }
}
```

**Step 3: Configuration**

```yaml
jwt:
  secret: ${JWT_SECRET:mySecretKey}

# Public endpoints (no auth required)
public:
  endpoints:
    - /auth/login
    - /auth/register
    - /actuator/health
```

**Request Flow:**

```
1. Client sends request with JWT:
   GET /api/products
   Authorization: Bearer eyJhbGciOiJ...

2. Gateway extracts token

3. Gateway validates token

4. If valid:
   - Add user info to headers
   - Forward to service
   
   If invalid:
   - Return 401 Unauthorized

5. Service receives request with user info:
   X-User-Id: user123
   X-User-Roles: ROLE_USER,ROLE_ADMIN
```

---

### Q6: How do you implement rate limiting in Spring Cloud Gateway?

**Answer:**

**Method 1: Redis-Based Rate Limiting**

**Configuration:**

```yaml
spring:
  redis:
    host: localhost
    port: 6379
  
  cloud:
    gateway:
      routes:
        - id: product-route
          uri: lb://product-service
          predicates:
            - Path=/api/products/**
          filters:
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 10  # 10 requests per second
                redis-rate-limiter.burstCapacity: 20  # Max 20 requests in burst
                redis-rate-limiter.requestedTokens: 1
                key-resolver: "#{@userKeyResolver}"
```

**Key Resolver:**

```java
@Configuration
public class RateLimitConfig {
    
    // Rate limit by user ID
    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> {
            String userId = exchange.getRequest()
                .getHeaders().getFirst("X-User-Id");
            return Mono.just(userId != null ? userId : "anonymous");
        };
    }
    
    // Rate limit by IP address
    @Bean
    public KeyResolver ipKeyResolver() {
        return exchange -> {
            String ip = exchange.getRequest()
                .getRemoteAddress()
                .getAddress()
                .getHostAddress();
            return Mono.just(ip);
        };
    }
    
    // Rate limit by API key
    @Bean
    public KeyResolver apiKeyResolver() {
        return exchange -> {
            String apiKey = exchange.getRequest()
                .getHeaders().getFirst("X-API-Key");
            return Mono.just(apiKey != null ? apiKey : "no-key");
        };
    }
}
```

**How It Works:**

```
Token Bucket Algorithm:

Capacity: 20 tokens
Refill Rate: 10 tokens/second

Request 1-20: ✅ Allowed (consume tokens)
Request 21: ❌ Rejected (429 Too Many Requests)

After 1 second: 10 tokens replenished
Request 22-31: ✅ Allowed
```

**Response Headers:**

```
X-RateLimit-Remaining: 19
X-RateLimit-Requested-Tokens: 1
X-RateLimit-Replenish-Rate: 10
X-RateLimit-Burst-Capacity: 20
```

**When rate limit exceeded:**
```
HTTP/1.1 429 Too Many Requests
X-RateLimit-Remaining: 0
```

---

### Q7: How do you handle errors and implement fallback in Gateway?

**Answer:**

**Method 1: Circuit Breaker with Fallback**

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: product-route
          uri: lb://product-service
          predicates:
            - Path=/api/products/**
          filters:
            - name: CircuitBreaker
              args:
                name: productServiceCB
                fallbackUri: forward:/fallback/products

resilience4j:
  circuitbreaker:
    instances:
      productServiceCB:
        sliding-window-size: 10
        failure-rate-threshold: 50
        wait-duration-in-open-state: 10s
```

**Fallback Controller:**

```java
@RestController
@RequestMapping("/fallback")
public class FallbackController {
    
    @GetMapping("/products")
    public Mono<ResponseEntity<ErrorResponse>> productFallback() {
        ErrorResponse error = new ErrorResponse(
            "Product service is temporarily unavailable",
            "PRODUCT_SERVICE_UNAVAILABLE",
            HttpStatus.SERVICE_UNAVAILABLE.value()
        );
        
        return Mono.just(ResponseEntity
            .status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(error));
    }
}
```

**Method 2: Global Error Handler**

```java
@Component
public class GlobalErrorWebExceptionHandler extends DefaultErrorWebExceptionHandler {
    
    public GlobalErrorWebExceptionHandler(ErrorAttributes errorAttributes,
                                         ResourceProperties resourceProperties,
                                         ApplicationContext applicationContext,
                                         ServerCodecConfigurer serverCodecConfigurer) {
        super(errorAttributes, resourceProperties, applicationContext);
        setMessageWriters(serverCodecConfigurer.getWriters());
    }
    
    @Override
    protected Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        Throwable error = super.getError(request);
        
        Map<String, Object> errorAttributes = new HashMap<>();
        errorAttributes.put("message", error.getMessage());
        errorAttributes.put("path", request.path());
        errorAttributes.put("timestamp", LocalDateTime.now());
        
        if (error instanceof ResponseStatusException) {
            ResponseStatusException rse = (ResponseStatusException) error;
            errorAttributes.put("status", rse.getStatus().value());
            errorAttributes.put("error", rse.getStatus().getReasonPhrase());
        }
        
        return errorAttributes;
    }
}
```

---

### Q8: How do you monitor and troubleshoot Spring Cloud Gateway?

**Answer:**

**1. Enable Actuator Endpoints**

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,gateway,httptrace
  endpoint:
    health:
      show-details: always
    gateway:
      enabled: true
```

**2. Gateway Actuator Endpoints**

```bash
# View all routes
GET /actuator/gateway/routes

# View specific route
GET /actuator/gateway/routes/{id}

# Refresh routes
POST /actuator/gateway/refresh

# View global filters
GET /actuator/gateway/globalfilters

# View route filters
GET /actuator/gateway/routefilters
```

**3. Metrics**

```bash
# Gateway-specific metrics
GET /actuator/metrics/gateway.requests

# Filter by route
GET /actuator/metrics/gateway.requests?tag=routeId:product-route

# Response time
GET /actuator/metrics/gateway.requests?tag=status:200
```

**4. Distributed Tracing (Sleuth + Zipkin)**

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-sleuth</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-sleuth-zipkin</artifactId>
</dependency>
```

```yaml
spring:
  sleuth:
    sampler:
      probability: 1.0  # Sample 100% of requests
  zipkin:
    base-url: http://localhost:9411
```

**5. Logging**

```yaml
logging:
  level:
    org.springframework.cloud.gateway: DEBUG
    reactor.netty: DEBUG
    org.springframework.web.reactive: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%X{traceId}/%X{spanId}] %-5level %logger{36} - %msg%n"
```

**6. Custom Metrics Filter**

```java
@Component
public class MetricsFilter implements GlobalFilter, Ordered {
    
    private final MeterRegistry meterRegistry;
    
    public MetricsFilter(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        long startTime = System.currentTimeMillis();
        String path = exchange.getRequest().getPath().toString();
        
        return chain.filter(exchange)
            .then(Mono.fromRunnable(() -> {
                long duration = System.currentTimeMillis() - startTime;
                
                meterRegistry.counter("gateway.requests",
                    "path", path,
                    "status", String.valueOf(exchange.getResponse().getStatusCode().value())
                ).increment();
                
                meterRegistry.timer("gateway.response.time",
                    "path", path
                ).record(duration, TimeUnit.MILLISECONDS);
            }));
    }
    
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
```

---

## Summary

**Key Takeaways:**

1. ✅ **Single Entry Point**: Gateway routes all client requests
2. ✅ **Routing**: Path, method, header-based routing
3. ✅ **Filters**: Modify requests/responses
4. ✅ **Security**: Centralized authentication/authorization
5. ✅ **Rate Limiting**: Control request rates per user/IP
6. ✅ **Circuit Breaker**: Fault tolerance with fallbacks
7. ✅ **Monitoring**: Actuator endpoints and metrics
8. ✅ **CORS**: Centralized CORS configuration

**Production Checklist:**
- [ ] Configure timeouts appropriately
- [ ] Implement authentication filter
- [ ] Add rate limiting
- [ ] Enable circuit breaker with fallbacks
- [ ] Configure CORS
- [ ] Add correlation ID for tracing
- [ ] Enable metrics and monitoring
- [ ] Secure actuator endpoints
- [ ] Implement error handling
- [ ] Add logging filter

**Next Steps:**
- Integrate with Config Server for dynamic routing
- Add distributed tracing (Sleuth + Zipkin)
- Implement API versioning
- Set up canary deployments
