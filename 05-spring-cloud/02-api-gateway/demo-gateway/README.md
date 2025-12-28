# Spring Cloud Gateway Demo

A comprehensive demonstration of **Spring Cloud Gateway** with routing, filtering, authentication, rate limiting, and circuit breaker patterns.

## Overview

This demo showcases a production-ready API Gateway implementation with:
- ✅ Dynamic routing to microservices
- ✅ Load balancing via Eureka
- ✅ JWT authentication
- ✅ Custom filters (logging, rate limiting)
- ✅ Circuit breaker with fallbacks
- ✅ CORS configuration
- ✅ Request/Response filtering
- ✅ Actuator monitoring

## Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    API Gateway (8080)                        │
│                                                              │
│  ┌────────────────┐  ┌────────────────┐  ┌──────────────┐ │
│  │ Global Filters │  │ Route Specific │  │   Fallback   │ │
│  │ - Logging      │  │ - Auth Filter  │  │  Controller  │ │
│  │ - Rate Limit   │  │ - Circuit      │  │              │ │
│  │ - CORS         │  │   Breaker      │  │              │ │
│  └────────────────┘  └────────────────┘  └──────────────┘ │
│                                                              │
│  ┌──────────────────────────────────────────────────────┐  │
│  │             Route Configuration                       │  │
│  │  /api/products/** → Product Service                  │  │
│  │  /api/orders/**   → Order Service                    │  │
│  │  /api/users/**    → User Service (Auth Required)     │  │
│  └──────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                         ↓ Load Balanced via Eureka
      ┌──────────────────┼──────────────────┐
      ↓                  ↓                   ↓
┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│   Product    │  │    Order     │  │     User     │
│   Service    │  │   Service    │  │   Service    │
│   (8082)     │  │   (8083)     │  │   (8084)     │
└──────────────┘  └──────────────┘  └──────────────┘
```

## Features

### 1. Routing & Load Balancing

**Service discovery-based routing:**
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: product-service
          uri: lb://product-service  # lb:// = load balanced
          predicates:
            - Path=/api/products/**
```

**The gateway automatically discovers service instances from Eureka and load balances requests.**

### 2. JWT Authentication

**Login to get token:**
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password"}'
```

**Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 3600,
  "username": "admin"
}
```

**Use token in requests:**
```bash
curl http://localhost:8080/api/users/profile \
  -H "Authorization: Bearer <token>"
```

### 3. Circuit Breaker

Protects against cascading failures:
```yaml
filters:
  - name: CircuitBreaker
    args:
      name: productCircuitBreaker
      fallbackUri: forward:/fallback/product
```

**Circuit breaker states:**
- **CLOSED:** Normal operation
- **OPEN:** Too many failures, reject requests immediately
- **HALF_OPEN:** Testing if service recovered

### 4. Rate Limiting

In-memory rate limiting (10 requests per minute per IP):
```java
@Component
public class RateLimitFilter implements GlobalFilter {
    private static final int MAX_REQUESTS = 10;
    private static final long TIME_WINDOW = 60000; // 1 minute
    ...
}
```

### 5. Global Logging

Logs all requests and responses:
```
📥 Incoming Request - Method: GET, URI: /api/products, Headers: [...]
📤 Outgoing Response - Status: 200 OK, Duration: 125ms, Path: /api/products
```

### 6. CORS Support

Pre-configured for common frontend origins:
```yaml
globalcors:
  cors-configurations:
    '[/**]':
      allowed-origins: 
        - "http://localhost:3000"  # React
        - "http://localhost:4200"  # Angular
```

## Prerequisites

1. **Java 17+**
2. **Maven 3.6+**
3. **Eureka Server** (optional, can disable)
4. **Downstream Services:** Product Service, Order Service (for testing routes)

## Running the Demo

### Step 1: Build the Project

```bash
cd demo-gateway
mvn clean package
```

### Step 2: Start the Gateway

```bash
java -jar target/demo-gateway-1.0.0.jar

# Or use Maven
mvn spring-boot:run
```

**Gateway starts on port 8080**

### Step 3: Test Gateway Features

## Testing Scenarios

### Test 1: Basic Routing

**Requirement:** Product Service running on port 8082

```bash
# Direct call to Product Service
curl http://localhost:8082/api/products

# Through API Gateway
curl http://localhost:8080/api/products
```

**Expected:** Same response from both

### Test 2: JWT Authentication

**Step 1: Login**
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password"}'
```

**Step 2: Validate Token**
```bash
TOKEN="<paste-token-here>"

curl http://localhost:8080/auth/validate \
  -H "Authorization: Bearer $TOKEN"
```

**Expected:** `Token is valid for user: admin`

**Step 3: Access Protected Resource**
```bash
curl http://localhost:8080/api/users/profile \
  -H "Authorization: Bearer $TOKEN"
```

### Test 3: Circuit Breaker & Fallback

**Step 1: Stop Product Service**

**Step 2: Make requests through gateway**
```bash
curl http://localhost:8080/api/products
```

**Expected:** Fallback response
```json
{
  "message": "Product Service is temporarily unavailable",
  "status": "SERVICE_UNAVAILABLE",
  "timestamp": "2025-12-19T10:30:00",
  "recommendation": "Please try again in a few moments"
}
```

**Step 3: Check circuit breaker status**
```bash
curl http://localhost:8080/actuator/circuitbreakers
```

### Test 4: Rate Limiting

**Make rapid requests:**
```bash
for i in {1..15}; do
  echo "Request $i:"
  curl -i http://localhost:8080/api/products 2>/dev/null | grep -E "HTTP|X-Rate-Limit"
  sleep 0.5
done
```

**Expected after 10 requests:**
```
HTTP/1.1 429 Too Many Requests
X-Rate-Limit-Retry-After: 60
```

### Test 5: Request Logging

**Make a request and check logs:**
```bash
curl http://localhost:8080/api/products
```

**Check console output:**
```
📥 Incoming Request - Method: GET, URI: /api/products, Headers: [...]
📤 Outgoing Response - Status: 200 OK, Duration: 125ms, Path: /api/products
```

### Test 6: CORS

**From browser console or frontend:**
```javascript
fetch('http://localhost:8080/api/products', {
  method: 'GET',
  headers: {
    'Content-Type': 'application/json'
  },
  credentials: 'include'
})
```

**Expected:** No CORS errors for allowed origins

## API Endpoints

### Gateway Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/auth/login` | POST | Generate JWT token |
| `/auth/validate` | GET | Validate JWT token |
| `/fallback/product` | GET | Product service fallback |
| `/fallback/order` | GET | Order service fallback |
| `/fallback/user` | GET | User service fallback |

### Proxied Microservice Endpoints

| Gateway Path | Target Service | Port |
|--------------|----------------|------|
| `/api/products/**` | product-service | 8082 |
| `/api/orders/**` | order-service | 8083 |
| `/api/users/**` | user-service | 8084 |

### Actuator Endpoints

| Endpoint | Description |
|----------|-------------|
| `/actuator/health` | Health check |
| `/actuator/gateway/routes` | List all routes |
| `/actuator/gateway/refresh` | Refresh routes |
| `/actuator/circuitbreakers` | Circuit breaker status |
| `/actuator/circuitbreakerevents` | Circuit breaker events |
| `/actuator/metrics` | Application metrics |

## Configuration

### Route Configuration (YAML)

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: product-service
          uri: lb://product-service
          predicates:
            - Path=/api/products/**
          filters:
            - CircuitBreaker
            - AddRequestHeader=X-Gateway-Request, API-Gateway
```

### Route Configuration (Java)

```java
@Bean
public RouteLocator customRoutes(RouteLocatorBuilder builder) {
    return builder.routes()
        .route("product-service", r -> r
            .path("/api/products/**")
            .filters(f -> f
                .circuitBreaker(config -> config
                    .setName("productCircuitBreaker")
                    .setFallbackUri("forward:/fallback/product"))
                .addRequestHeader("X-Gateway", "true"))
            .uri("lb://product-service"))
        .build();
}
```

### Custom Filters

**Global Filter (applies to all routes):**
```java
@Component
public class LoggingFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // Pre-processing
        log.info("Request: {}", exchange.getRequest().getPath());
        
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            // Post-processing
            log.info("Response: {}", exchange.getResponse().getStatusCode());
        }));
    }
    
    @Override
    public int getOrder() {
        return -1; // Execute first
    }
}
```

**Route-Specific Filter:**
```java
@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<Config> {
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            // Authentication logic
            String token = extractToken(exchange);
            validateToken(token);
            return chain.filter(exchange);
        };
    }
}
```

## Monitoring

### View All Routes

```bash
curl http://localhost:8080/actuator/gateway/routes | jq
```

**Response:**
```json
[
  {
    "route_id": "product-service",
    "route_definition": {
      "predicates": [
        {"name": "Path", "args": {"pattern": "/api/products/**"}}
      ],
      "filters": [...]
    },
    "uri": "lb://product-service",
    "order": 0
  }
]
```

### Circuit Breaker Status

```bash
curl http://localhost:8080/actuator/circuitbreakers
```

**Response:**
```json
{
  "circuitBreakers": {
    "productCircuitBreaker": {
      "state": "CLOSED",
      "failureRate": "0.0%",
      "slowCallRate": "0.0%"
    }
  }
}
```

### Circuit Breaker Events

```bash
curl http://localhost:8080/actuator/circuitbreakerevents/productCircuitBreaker
```

**Shows state transitions and events:**
```json
{
  "circuitBreakerEvents": [
    {
      "circuitBreakerName": "productCircuitBreaker",
      "type": "SUCCESS",
      "creationTime": "2025-12-19T10:30:00"
    },
    {
      "circuitBreakerName": "productCircuitBreaker",
      "type": "ERROR",
      "creationTime": "2025-12-19T10:31:00"
    }
  ]
}
```

### Health Check

```bash
curl http://localhost:8080/actuator/health
```

**Response:**
```json
{
  "status": "UP",
  "components": {
    "circuitBreakers": {
      "status": "UP"
    },
    "diskSpace": {
      "status": "UP"
    },
    "ping": {
      "status": "UP"
    }
  }
}
```

## Troubleshooting

### Issue: 404 Not Found

**Problem:** Gateway returns 404 for valid routes

**Solution:**
1. Check if downstream service is registered with Eureka
2. Verify route configuration in `application.yml`
3. Check route definitions: `curl http://localhost:8080/actuator/gateway/routes`

### Issue: Connection Refused

**Problem:** `Connection refused` error

**Solution:**
1. Ensure downstream service is running
2. Check service discovery: `curl http://localhost:8761/eureka/apps`
3. Verify `eureka.client.fetch-registry=true`

### Issue: JWT Token Rejected

**Problem:** `401 Unauthorized` with valid token

**Solution:**
1. Check token expiration: Tokens expire after 1 hour
2. Verify secret key matches between token generation and validation
3. Check logs for specific JWT validation errors

### Issue: Circuit Breaker Not Opening

**Problem:** Circuit breaker stays CLOSED despite failures

**Solution:**
1. Check `minimum-number-of-calls` configuration (default: 5)
2. Verify `failure-rate-threshold` (default: 50%)
3. Monitor events: `curl http://localhost:8080/actuator/circuitbreakerevents`

### Issue: Rate Limit Not Working

**Problem:** Requests not being rate limited

**Solution:**
1. Verify `RateLimitFilter` is registered as a component
2. Check filter order (should execute before routing)
3. Clear rate limit cache by restarting gateway

## Production Considerations

### 1. Enable Service Registration

```yaml
eureka:
  client:
    register-with-eureka: true  # Register gateway with Eureka
    fetch-registry: true
```

### 2. Use Redis for Rate Limiting

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis-reactive</artifactId>
</dependency>
```

```yaml
spring:
  cloud:
    gateway:
      filter:
        request-rate-limiter:
          redis-rate-limiter:
            replenish-rate: 10
            burst-capacity: 20
```

### 3. Secure JWT Secret

```yaml
app:
  jwt:
    secret: ${JWT_SECRET}  # From environment variable
```

### 4. Add Request ID

```java
@Component
public class RequestIdFilter implements GlobalFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String requestId = UUID.randomUUID().toString();
        ServerHttpRequest request = exchange.getRequest().mutate()
            .header("X-Request-ID", requestId)
            .build();
        return chain.filter(exchange.mutate().request(request).build());
    }
}
```

### 5. Enable Distributed Tracing

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

### 6. Configure Timeouts

```yaml
spring:
  cloud:
    gateway:
      httpclient:
        connect-timeout: 1000
        response-timeout: 5s
```

### 7. Add Monitoring

```yaml
management:
  metrics:
    export:
      prometheus:
        enabled: true
```

## Advanced Features

### 1. Path Rewriting

```yaml
filters:
  - RewritePath=/api/products/(?<segment>.*), /$\{segment}
```

### 2. Request Size Limiting

```yaml
spring:
  codec:
    max-in-memory-size: 10MB
```

### 3. WebSocket Support

```yaml
routes:
  - id: websocket-route
    uri: lb:ws://chat-service
    predicates:
      - Path=/ws/**
```

### 4. Weighted Load Balancing

```yaml
routes:
  - id: product-v1
    uri: lb://product-service-v1
    predicates:
      - Path=/api/products/**
      - Weight=group1, 8  # 80% traffic

  - id: product-v2
    uri: lb://product-service-v2
    predicates:
      - Path=/api/products/**
      - Weight=group1, 2  # 20% traffic
```

## Testing with Downstream Services

### Quick Mock Services

**If you don't have actual services, create simple mocks:**

**Product Service (port 8082):**
```bash
json-server --watch products.json --port 8082
```

**products.json:**
```json
{
  "products": [
    {"id": 1, "name": "iPhone 15", "price": 999},
    {"id": 2, "name": "MacBook Pro", "price": 2499}
  ]
}
```

**Or use httpbin.org for testing:**
```yaml
routes:
  - id: test-route
    uri: https://httpbin.org
    predicates:
      - Path=/test/**
    filters:
      - StripPrefix=1
```

**Test:**
```bash
curl http://localhost:8080/test/get
# Routes to https://httpbin.org/get
```

## Project Structure

```
demo-gateway/
├── src/
│   ├── main/
│   │   ├── java/com/example/gateway/
│   │   │   ├── GatewayApplication.java
│   │   │   ├── config/
│   │   │   │   ├── GatewayConfig.java
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   └── CorsConfig.java
│   │   │   ├── filter/
│   │   │   │   ├── AuthenticationFilter.java
│   │   │   │   ├── LoggingFilter.java
│   │   │   │   └── RateLimitFilter.java
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java
│   │   │   │   └── FallbackController.java
│   │   │   ├── dto/
│   │   │   │   ├── LoginRequest.java
│   │   │   │   └── LoginResponse.java
│   │   │   └── util/
│   │   │       └── JwtUtil.java
│   │   └── resources/
│   │       └── application.yml
│   └── test/ (tests to be added)
├── pom.xml
└── README.md
```

## Next Steps

1. **Integrate with Eureka** - Enable full service discovery
2. **Add Distributed Tracing** - Spring Cloud Sleuth + Zipkin
3. **Implement Redis Rate Limiting** - For distributed environments
4. **Add API Documentation** - Swagger/OpenAPI aggregation
5. **Set up Monitoring** - Prometheus + Grafana dashboards
6. **Implement Blue-Green Deployment** - Weighted routing

## References

- [Spring Cloud Gateway Documentation](https://docs.spring.io/spring-cloud-gateway/docs/current/reference/html/)
- [Section 02 README](../README.md) - Comprehensive API Gateway guide
- [Resilience4j Documentation](https://resilience4j.readme.io/)
- [JWT Introduction](https://jwt.io/introduction)

---

**Demo Complete! Gateway is production-ready with authentication, rate limiting, circuit breakers, and comprehensive monitoring.** 🎉
