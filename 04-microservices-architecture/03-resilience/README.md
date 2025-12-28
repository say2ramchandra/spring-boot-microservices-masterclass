# Section 03: Resilience Patterns

## Table of Contents
1. [Introduction](#introduction)
2. [What is Resilience](#what-is-resilience)
3. [Resilience4j Framework](#resilience4j-framework)
4. [Circuit Breaker Pattern](#circuit-breaker-pattern)
5. [Retry Pattern](#retry-pattern)
6. [Timeout Pattern](#timeout-pattern)
7. [Bulkhead Pattern](#bulkhead-pattern)
8. [Rate Limiter Pattern](#rate-limiter-pattern)
9. [Fallback Strategies](#fallback-strategies)
10. [Best Practices](#best-practices)
11. [Interview Questions](#interview-questions)

---

## Introduction

In microservices architecture, services depend on each other. When one service fails or becomes slow, it can cascade and affect the entire system. **Resilience patterns** help services handle failures gracefully and prevent cascading failures.

### Why Resilience is Critical

- **Network Failures**: Communication over network is unreliable
- **Service Failures**: Dependent services may crash or become unavailable
- **Performance Issues**: Slow services can impact overall system
- **Resource Exhaustion**: Memory leaks, connection pool exhaustion
- **Cascading Failures**: One failure triggers others

---

## What is Resilience

**Resilience** is the ability of a system to handle and recover from failures gracefully.

### Key Principles

1. **Fail Fast**: Detect failures quickly
2. **Fail Gracefully**: Provide fallback responses
3. **Recover Quickly**: Return to normal operation
4. **Isolate Failures**: Prevent cascade effects
5. **Monitor Everything**: Track health and metrics

### Resilience vs Fault Tolerance

| Aspect | Resilience | Fault Tolerance |
|--------|-----------|-----------------|
| **Goal** | Handle and recover from failures | Prevent failures |
| **Approach** | Graceful degradation | Redundancy |
| **Cost** | Lower | Higher |
| **Complexity** | Moderate | High |
| **Example** | Circuit breaker, retry | Replication, backup |

---

## Resilience4j Framework

**Resilience4j** is a lightweight fault tolerance library for Java 8+, inspired by Netflix Hystrix.

### Why Resilience4j?

- ✅ Lightweight (no external dependencies)
- ✅ Java 8+ functional programming
- ✅ Modular (use only what you need)
- ✅ Spring Boot integration
- ✅ Metrics and monitoring
- ✅ Active development (Hystrix is deprecated)

### Core Modules

1. **resilience4j-circuitbreaker**: Circuit breaker implementation
2. **resilience4j-retry**: Retry mechanism
3. **resilience4j-timelimiter**: Timeout handling
4. **resilience4j-bulkhead**: Concurrency limiting
5. **resilience4j-ratelimiter**: Rate limiting

### Maven Dependency

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-circuitbreaker-resilience4j</artifactId>
</dependency>
```

---

## Circuit Breaker Pattern

### Problem

When a service is down, repeatedly calling it:
- Wastes resources
- Increases latency
- Can crash the caller
- Prevents the failing service from recovering

### Solution

**Circuit Breaker** monitors failures and "opens" when threshold is reached, preventing further calls.

### States

```
                ┌──────────────┐
                │    CLOSED    │ ◄─── Normal operation
                │ (Calls pass) │
                └──────┬───────┘
                       │
                Failure threshold
                  reached
                       │
                       ▼
                ┌──────────────┐
                │     OPEN     │ ◄─── Service unavailable
                │ (Calls fail  │      Return fallback
                │  immediately)│
                └──────┬───────┘
                       │
                  After wait
                  duration
                       │
                       ▼
                ┌──────────────┐
                │  HALF_OPEN   │ ◄─── Testing
                │ (Limited     │      Allow few calls
                │  calls pass) │
                └──┬───────┬───┘
                   │       │
              Success  Failure
                   │       │
                   ▼       ▼
              CLOSED    OPEN
```

### Configuration

```yaml
resilience4j:
  circuitbreaker:
    instances:
      userService:
        # Sliding window config
        sliding-window-type: COUNT_BASED
        sliding-window-size: 10
        
        # Failure threshold
        failure-rate-threshold: 50
        slow-call-rate-threshold: 50
        slow-call-duration-threshold: 2s
        
        # Minimum calls before calculating failure rate
        minimum-number-of-calls: 5
        
        # Wait duration in OPEN state
        wait-duration-in-open-state: 10s
        
        # Calls allowed in HALF_OPEN state
        permitted-number-of-calls-in-half-open-state: 3
        
        # Automatic transition
        automatic-transition-from-open-to-half-open-enabled: true
```

### Implementation

```java
@Service
public class OrderService {
    
    @Autowired
    private RestTemplate restTemplate;
    
    @CircuitBreaker(name = "userService", fallbackMethod = "getUserFallback")
    public User getUser(Long userId) {
        String url = "http://USER-SERVICE/api/users/" + userId;
        return restTemplate.getForObject(url, User.class);
    }
    
    // Fallback method - same parameters + Exception
    public User getUserFallback(Long userId, Exception e) {
        log.error("Circuit breaker activated for user: " + userId, e);
        
        // Return default/cached user
        return new User(userId, "Unknown User", "N/A");
    }
}
```

### When to Use

- ✅ External service calls (REST APIs)
- ✅ Database connections
- ✅ Third-party integrations
- ✅ Services with known instability

---

## Retry Pattern

### Problem

Transient failures (network glitches, temporary unavailability) can often be resolved by simply retrying.

### Solution

**Retry** automatically retries failed operations with configurable delays.

### Configuration

```yaml
resilience4j:
  retry:
    instances:
      paymentService:
        # Maximum retry attempts
        max-attempts: 3
        
        # Wait duration between retries
        wait-duration: 1s
        
        # Exponential backoff
        enable-exponential-backoff: true
        exponential-backoff-multiplier: 2
        
        # Retry only on specific exceptions
        retry-exceptions:
          - java.net.ConnectException
          - org.springframework.web.client.ResourceAccessException
        
        # Don't retry on these
        ignore-exceptions:
          - java.lang.IllegalArgumentException
```

### Implementation

```java
@Service
public class PaymentService {
    
    @Retry(name = "paymentService", fallbackMethod = "processPaymentFallback")
    public PaymentResponse processPayment(PaymentRequest request) {
        log.info("Attempting payment processing...");
        
        String url = "http://PAYMENT-SERVICE/api/payments/process";
        return restTemplate.postForObject(url, request, PaymentResponse.class);
    }
    
    public PaymentResponse processPaymentFallback(PaymentRequest request, Exception e) {
        log.error("All retry attempts failed", e);
        
        return new PaymentResponse(
            null,
            "FAILED",
            request.getAmount(),
            "Payment service unavailable. Please try again later."
        );
    }
}
```

### Retry Strategies

#### 1. Fixed Delay
```yaml
wait-duration: 1s
enable-exponential-backoff: false
```
Retries: 1s → 1s → 1s

#### 2. Exponential Backoff
```yaml
wait-duration: 1s
enable-exponential-backoff: true
exponential-backoff-multiplier: 2
```
Retries: 1s → 2s → 4s

#### 3. Random Delay
```yaml
enable-randomized-wait: true
randomized-wait-factor: 0.5
```
Adds randomness to prevent thundering herd

### When to Use

- ✅ Transient network failures
- ✅ Rate-limited APIs
- ✅ Temporary service unavailability
- ❌ Client errors (4xx)
- ❌ Invalid requests

---

## Timeout Pattern

### Problem

Slow services can tie up resources and cascade delays throughout the system.

### Solution

**Timeout** limits how long to wait for a response, failing fast if exceeded.

### Configuration

```yaml
resilience4j:
  timelimiter:
    instances:
      inventoryService:
        timeout-duration: 3s
        cancel-running-future: true
```

### Implementation

```java
@Service
public class InventoryService {
    
    @TimeLimiter(name = "inventoryService", fallbackMethod = "checkStockFallback")
    @Async
    public CompletableFuture<Boolean> checkStock(Long productId, int quantity) {
        return CompletableFuture.supplyAsync(() -> {
            // This call must complete within timeout
            String url = "http://INVENTORY-SERVICE/api/inventory/" + productId;
            Inventory inventory = restTemplate.getForObject(url, Inventory.class);
            return inventory.getQuantity() >= quantity;
        });
    }
    
    public CompletableFuture<Boolean> checkStockFallback(Long productId, int quantity, Exception e) {
        log.error("Inventory check timed out", e);
        
        // Assume stock not available
        return CompletableFuture.completedFuture(false);
    }
}
```

### RestTemplate Timeout

```java
@Bean
public RestTemplate restTemplate() {
    SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
    factory.setConnectTimeout(5000);  // 5 seconds
    factory.setReadTimeout(5000);     // 5 seconds
    return new RestTemplate(factory);
}
```

### When to Use

- ✅ All external service calls
- ✅ Database queries
- ✅ File I/O operations
- ✅ Any potentially slow operation

---

## Bulkhead Pattern

### Problem

One slow or failing service can exhaust all threads, affecting other services.

### Solution

**Bulkhead** isolates resources (threads, connections) for each service, preventing resource exhaustion.

### Analogy

Like bulkheads in a ship that prevent water from flooding the entire vessel.

```
┌─────────────────────────────────┐
│         Thread Pool             │
├──────────┬──────────┬───────────┤
│ Service A│ Service B│ Service C │
│ 10 threads│ 10 threads│ 10 threads│
└──────────┴──────────┴───────────┘
```

### Types

#### 1. Semaphore-Based (Thread Isolation)

```yaml
resilience4j:
  bulkhead:
    instances:
      orderService:
        max-concurrent-calls: 10
        max-wait-duration: 100ms
```

```java
@Bulkhead(name = "orderService", type = Bulkhead.Type.SEMAPHORE)
public Order createOrder(OrderRequest request) {
    // Only 10 concurrent calls allowed
    return processOrder(request);
}
```

#### 2. Thread Pool-Based

```yaml
resilience4j:
  thread-pool-bulkhead:
    instances:
      paymentService:
        max-thread-pool-size: 4
        core-thread-pool-size: 2
        queue-capacity: 20
        keep-alive-duration: 20ms
```

```java
@Bulkhead(name = "paymentService", type = Bulkhead.Type.THREADPOOL)
public CompletableFuture<PaymentResponse> processPayment(PaymentRequest request) {
    // Executed in separate thread pool
    return CompletableFuture.supplyAsync(() -> {
        return callPaymentService(request);
    });
}
```

### When to Use

- ✅ Protecting against resource exhaustion
- ✅ Isolating critical services
- ✅ Preventing cascading failures
- ✅ Multi-tenant applications

---

## Rate Limiter Pattern

### Problem

Too many requests can overwhelm a service, causing it to crash or respond slowly.

### Solution

**Rate Limiter** controls the number of requests allowed within a time period.

### Configuration

```yaml
resilience4j:
  ratelimiter:
    instances:
      apiService:
        # Time period
        limit-refresh-period: 1s
        
        # Number of calls allowed per period
        limit-for-period: 10
        
        # Wait time for permission
        timeout-duration: 0s
```

### Implementation

```java
@Service
public class ApiService {
    
    @RateLimiter(name = "apiService")
    public String callApi() {
        // Only 10 calls per second allowed
        return restTemplate.getForObject("http://API-SERVICE/data", String.class);
    }
}
```

### Use Cases

1. **Protecting APIs**: Prevent abuse
2. **Third-Party APIs**: Respect rate limits
3. **Database Protection**: Limit query rate
4. **Cost Control**: Limit expensive operations

---

## Fallback Strategies

### 1. Default Value

```java
@CircuitBreaker(name = "userService", fallbackMethod = "getDefaultUser")
public User getUser(Long id) {
    return userServiceClient.getUser(id);
}

public User getDefaultUser(Long id, Exception e) {
    return new User(id, "Guest User", "guest@example.com");
}
```

### 2. Cached Data

```java
@Cacheable("users")
@CircuitBreaker(name = "userService", fallbackMethod = "getCachedUser")
public User getUser(Long id) {
    return userServiceClient.getUser(id);
}

public User getCachedUser(Long id, Exception e) {
    // Return cached data
    return cacheManager.getCache("users").get(id, User.class);
}
```

### 3. Alternative Service

```java
@CircuitBreaker(name = "primaryService", fallbackMethod = "useBackupService")
public Data getData() {
    return primaryService.fetchData();
}

public Data useBackupService(Exception e) {
    log.warn("Primary service failed, using backup");
    return backupService.fetchData();
}
```

### 4. Graceful Degradation

```java
@CircuitBreaker(name = "recommendationService", fallbackMethod = "getBasicRecommendations")
public List<Product> getRecommendations(Long userId) {
    return recommendationService.getPersonalizedRecommendations(userId);
}

public List<Product> getBasicRecommendations(Long userId, Exception e) {
    // Return popular products instead of personalized ones
    return productService.getPopularProducts();
}
```

### 5. Empty Response

```java
@CircuitBreaker(name = "commentService", fallbackMethod = "getEmptyComments")
public List<Comment> getComments(Long postId) {
    return commentService.getComments(postId);
}

public List<Comment> getEmptyComments(Long postId, Exception e) {
    log.error("Failed to fetch comments for post: " + postId, e);
    return Collections.emptyList();
}
```

---

## Best Practices

### 1. Combine Patterns

Use multiple patterns together for better resilience:

```java
@CircuitBreaker(name = "paymentService")
@Retry(name = "paymentService")
@TimeLimiter(name = "paymentService")
@Bulkhead(name = "paymentService")
public PaymentResponse processPayment(PaymentRequest request) {
    return paymentServiceClient.process(request);
}
```

**Execution Order**:
1. Bulkhead (Resource isolation)
2. TimeLimiter (Timeout)
3. CircuitBreaker (Fast fail if open)
4. Retry (Retry on failure)

### 2. Configure Appropriately

```yaml
# Fast-failing service
resilience4j:
  circuitbreaker:
    instances:
      criticalService:
        failure-rate-threshold: 30  # Low threshold
        wait-duration-in-open-state: 60s  # Long wait
        
# Tolerant service
resilience4j:
  circuitbreaker:
    instances:
      nonCriticalService:
        failure-rate-threshold: 70  # High threshold
        wait-duration-in-open-state: 5s  # Short wait
```

### 3. Monitor Metrics

```java
@Bean
public CircuitBreakerConfig circuitBreakerConfig() {
    return CircuitBreakerConfig.custom()
        .recordExceptions(IOException.class, TimeoutException.class)
        .ignoreExceptions(BusinessException.class)
        .build();
}
```

Enable metrics:
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,metrics,circuitbreakers
  health:
    circuitbreakers:
      enabled: true
  metrics:
    tags:
      application: ${spring.application.name}
```

### 4. Meaningful Fallbacks

```java
// ❌ Bad - Hides errors
public User getUserFallback(Long id, Exception e) {
    return null;
}

// ✅ Good - Provides context
public User getUserFallback(Long id, Exception e) {
    log.error("Failed to fetch user: " + id, e);
    return new User(id, "Service Unavailable", "N/A");
}
```

### 5. Health Checks

```java
@Component
public class ServiceHealthIndicator implements HealthIndicator {
    
    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;
    
    @Override
    public Health health() {
        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker("userService");
        
        if (cb.getState() == CircuitBreaker.State.OPEN) {
            return Health.down()
                .withDetail("circuitBreaker", "OPEN")
                .withDetail("reason", "Too many failures")
                .build();
        }
        
        return Health.up().build();
    }
}
```

### 6. Testing Resilience

```java
@Test
public void testCircuitBreaker() {
    // Cause failures to open circuit breaker
    for (int i = 0; i < 10; i++) {
        try {
            service.callExternalService();
        } catch (Exception e) {
            // Expected
        }
    }
    
    // Verify circuit breaker is open
    CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker("externalService");
    assertThat(cb.getState()).isEqualTo(CircuitBreaker.State.OPEN);
    
    // Verify fallback is called
    String result = service.callExternalService();
    assertThat(result).isEqualTo("Fallback response");
}
```

---

## Interview Questions

### Q1: What is a Circuit Breaker pattern and why is it important in microservices?

**Answer**:

Circuit Breaker is a design pattern that prevents an application from repeatedly trying to execute an operation that's likely to fail, similar to an electrical circuit breaker.

**States**:
1. **CLOSED**: Normal operation, requests pass through
2. **OPEN**: Too many failures, requests fail immediately with fallback
3. **HALF_OPEN**: Testing recovery, limited requests allowed

**Benefits**:
- Prevents cascading failures
- Fails fast instead of waiting for timeout
- Gives failing service time to recover
- Improves system stability

**Example**:
```java
@CircuitBreaker(name = "userService", fallbackMethod = "getUserFallback")
public User getUser(Long id) {
    return restTemplate.getForObject(url, User.class);
}

public User getUserFallback(Long id, Exception e) {
    return new User(id, "Default User");
}
```

**When to Use**:
- External service calls
- Database connections
- Third-party API integrations

---

### Q2: What is the difference between Retry and Circuit Breaker patterns?

**Answer**:

| Aspect | Retry | Circuit Breaker |
|--------|-------|----------------|
| **Purpose** | Recover from transient failures | Prevent cascading failures |
| **Action** | Retry failed requests | Stop requests when threshold reached |
| **Use Case** | Network glitches | Service unavailability |
| **Duration** | Short-term (seconds) | Medium-term (minutes) |
| **Cost** | Can increase load | Reduces load |

**Retry**:
```java
@Retry(name = "paymentService", maxAttempts = 3)
public Payment processPayment(PaymentRequest request) {
    return paymentService.process(request);
}
```
- Tries 3 times before giving up
- Good for temporary network issues

**Circuit Breaker**:
```java
@CircuitBreaker(name = "paymentService")
public Payment processPayment(PaymentRequest request) {
    return paymentService.process(request);
}
```
- Opens after threshold failures
- Stops trying for a period
- Good for service downtime

**Best Practice**: Use both together
```java
@CircuitBreaker(name = "paymentService")
@Retry(name = "paymentService")
public Payment processPayment(PaymentRequest request) {
    return paymentService.process(request);
}
```

---

### Q3: Explain the Bulkhead pattern with an example

**Answer**:

Bulkhead pattern isolates resources (threads, connections) for different services to prevent resource exhaustion from affecting the entire system.

**Analogy**: Like compartments in a ship that prevent water from flooding the entire vessel.

**Without Bulkhead**:
```
[Service A][Service B][Service C]
       ↓
All sharing 100 threads
       ↓
If Service A hangs, all threads occupied
       ↓
Service B and C can't execute
```

**With Bulkhead**:
```
[Service A: 30 threads]
[Service B: 30 threads]
[Service C: 40 threads]
       ↓
If Service A hangs, only its 30 threads occupied
       ↓
Service B and C continue working
```

**Implementation**:
```java
@Bulkhead(name = "orderService", type = Bulkhead.Type.SEMAPHORE)
public Order createOrder(OrderRequest request) {
    // Only configured number of concurrent calls allowed
    return processOrder(request);
}
```

**Configuration**:
```yaml
resilience4j:
  bulkhead:
    instances:
      orderService:
        max-concurrent-calls: 10
        max-wait-duration: 100ms
```

**Types**:
1. **Semaphore**: Limits concurrent calls
2. **Thread Pool**: Separate thread pool per service

**When to Use**:
- Protecting against slow services
- Multi-tenant applications
- Resource-intensive operations

---

### Q4: What is the purpose of a fallback method?

**Answer**:

Fallback method provides an alternative response when the primary operation fails, enabling graceful degradation.

**Purpose**:
1. Prevent complete failure
2. Provide default/cached data
3. Improve user experience
4. Maintain partial functionality

**Requirements**:
- Same return type as primary method
- Same parameters + Exception parameter

**Example**:
```java
@CircuitBreaker(name = "productService", fallbackMethod = "getProductFallback")
public Product getProduct(Long id) {
    return productService.findById(id);
}

// Fallback method
public Product getProductFallback(Long id, Exception e) {
    log.error("Failed to fetch product: " + id, e);
    
    // Return cached product or default
    return cacheService.get(id)
        .orElse(new Product(id, "Product Unavailable"));
}
```

**Fallback Strategies**:

1. **Cached Data**:
```java
return cacheManager.get(id);
```

2. **Default Value**:
```java
return new Product(id, "Default Product");
```

3. **Alternative Service**:
```java
return backupProductService.getProduct(id);
```

4. **Degraded Functionality**:
```java
// Instead of personalized recommendations
return getPopularProducts();
```

5. **Empty Response**:
```java
return Collections.emptyList();
```

**Best Practices**:
- Always log the error
- Return meaningful defaults
- Don't throw exceptions in fallback
- Consider caching for fallbacks

---

### Q5: How do you configure timeout in Spring Boot microservices?

**Answer**:

**1. RestTemplate Timeout**:
```java
@Bean
public RestTemplate restTemplate() {
    SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
    factory.setConnectTimeout(5000);  // Connection timeout
    factory.setReadTimeout(5000);     // Read timeout
    return new RestTemplate(factory);
}
```

**2. WebClient Timeout**:
```java
@Bean
public WebClient webClient() {
    return WebClient.builder()
        .baseUrl("http://service-url")
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
}

// Usage with timeout
webClient.get()
    .uri("/api/data")
    .retrieve()
    .bodyToMono(Data.class)
    .timeout(Duration.ofSeconds(5))
    .block();
```

**3. Resilience4j TimeLimiter**:
```yaml
resilience4j:
  timelimiter:
    instances:
      userService:
        timeout-duration: 3s
        cancel-running-future: true
```

```java
@TimeLimiter(name = "userService")
@Async
public CompletableFuture<User> getUser(Long id) {
    return CompletableFuture.supplyAsync(() -> {
        return userService.findById(id);
    });
}
```

**4. Feign Client Timeout**:
```yaml
feign:
  client:
    config:
      default:
        connectTimeout: 5000
        readTimeout: 5000
```

**Best Practices**:
- Set realistic timeouts based on SLA
- Different timeouts for different services
- Always have fallback for timeouts
- Monitor timeout metrics

---

### Q6: What metrics should you monitor for circuit breakers?

**Answer**:

**Key Metrics**:

1. **Circuit Breaker State**:
   - CLOSED, OPEN, HALF_OPEN
   - Time in each state
   - State transitions

2. **Failure Rate**:
   - Percentage of failed calls
   - Number of failures
   - Failure threshold

3. **Call Statistics**:
   - Total calls
   - Successful calls
   - Failed calls
   - Slow calls

4. **Response Time**:
   - Average latency
   - P95, P99 percentiles
   - Slow call threshold

**Resilience4j Actuator Endpoints**:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,metrics,circuitbreakers
  health:
    circuitbreakers:
      enabled: true
```

**Endpoints**:
- `/actuator/health` - Overall health
- `/actuator/circuitbreakers` - Circuit breaker states
- `/actuator/metrics/resilience4j.circuitbreaker.calls` - Call metrics

**Custom Metrics**:
```java
@Component
public class CircuitBreakerMetrics {
    
    @Autowired
    private CircuitBreakerRegistry registry;
    
    @Autowired
    private MeterRegistry meterRegistry;
    
    @PostConstruct
    public void init() {
        CircuitBreaker cb = registry.circuitBreaker("userService");
        
        Gauge.builder("circuit.breaker.state", cb,
            circuitBreaker -> {
                switch (circuitBreaker.getState()) {
                    case CLOSED: return 0;
                    case OPEN: return 1;
                    case HALF_OPEN: return 0.5;
                    default: return -1;
                }
            })
            .register(meterRegistry);
    }
}
```

**Alerting**:
- Alert when circuit breaker opens
- Alert on high failure rates
- Alert on increased latency

---

### Q7: How does Resilience4j differ from Netflix Hystrix?

**Answer**:

| Feature | Hystrix | Resilience4j |
|---------|---------|--------------|
| **Status** | Maintenance mode | Actively developed |
| **Java Version** | Java 6+ | Java 8+ |
| **Dependencies** | Many (RxJava, Archaius) | Zero |
| **Design** | Annotation-based | Functional |
| **Modularity** | Monolithic | Modular |
| **Thread Model** | Thread isolation | Semaphore/Thread |
| **Spring Boot** | Integration available | Native support |
| **Metrics** | Custom | Micrometer |

**Resilience4j Advantages**:
1. **Lightweight**: No external dependencies
2. **Modern**: Java 8 functional programming
3. **Flexible**: Use only modules you need
4. **Active**: Regular updates and bug fixes

**Migration**:
```java
// Hystrix
@HystrixCommand(fallbackMethod = "fallback")
public String getData() {
    return service.fetch();
}

// Resilience4j
@CircuitBreaker(name = "service", fallbackMethod = "fallback")
public String getData() {
    return service.fetch();
}
```

**Recommendation**: Use Resilience4j for new projects

---

### Q8: Explain the Saga pattern for distributed transactions

**Answer**:

Saga pattern manages distributed transactions by breaking them into a sequence of local transactions, each with a compensating transaction for rollback.

**Problem**:
- No ACID transactions across microservices
- 2PC (Two-Phase Commit) doesn't scale well
- Need to maintain consistency

**Solution**:
Saga coordinates a series of transactions, with compensating actions if any fails.

**Types**:

**1. Choreography (Event-Driven)**:
```
Order Service → [OrderCreated] → Payment Service
                                      ↓
                                [PaymentProcessed]
                                      ↓
                                Inventory Service
                                      ↓
                                [InventoryReserved]
                                      ↓
                                Shipping Service
```

Each service listens to events and publishes new events.

**Advantages**:
- Loose coupling
- No central orchestrator
- Scalable

**Disadvantages**:
- Complex to understand
- Hard to debug
- No central monitoring

**2. Orchestration (Centralized)**:
```java
@Service
public class OrderSagaOrchestrator {
    
    public void executeOrderSaga(Order order) {
        try {
            // Step 1: Create order
            orderService.createOrder(order);
            
            // Step 2: Process payment
            paymentService.processPayment(order);
            
            // Step 3: Reserve inventory
            inventoryService.reserveInventory(order);
            
            // Step 4: Arrange shipping
            shippingService.arrangeShipping(order);
            
            // Success - confirm order
            orderService.confirmOrder(order);
            
        } catch (PaymentException e) {
            // Compensate: Cancel order
            orderService.cancelOrder(order);
            
        } catch (InventoryException e) {
            // Compensate: Refund payment, cancel order
            paymentService.refundPayment(order);
            orderService.cancelOrder(order);
            
        } catch (ShippingException e) {
            // Compensate: Release inventory, refund payment
            inventoryService.releaseInventory(order);
            paymentService.refundPayment(order);
            orderService.cancelOrder(order);
        }
    }
}
```

**Advantages**:
- Easy to understand
- Centralized logic
- Easy to monitor

**Disadvantages**:
- Single point of failure
- Tight coupling
- Orchestrator complexity

**Best Practices**:
1. Keep sagas short
2. Use idempotency
3. Monitor saga execution
4. Implement timeouts
5. Handle partial failures

---

## Summary

**Resilience Patterns Overview**:

| Pattern | Purpose | When to Use |
|---------|---------|-------------|
| **Circuit Breaker** | Prevent cascading failures | Service unavailability |
| **Retry** | Recover from transient failures | Network glitches |
| **Timeout** | Fail fast on slow services | All external calls |
| **Bulkhead** | Resource isolation | Prevent resource exhaustion |
| **Rate Limiter** | Control request rate | API protection |

**Key Takeaways**:

1. ✅ **Use Multiple Patterns**: Combine for better resilience
2. ✅ **Configure Appropriately**: Based on service characteristics
3. ✅ **Implement Fallbacks**: Provide graceful degradation
4. ✅ **Monitor Metrics**: Track circuit breaker states
5. ✅ **Test Resilience**: Chaos engineering
6. ✅ **Log Everything**: For debugging and analysis

**Resilience Formula**:
```
Circuit Breaker + Retry + Timeout + Bulkhead + Fallback = Resilient Service
```

**Next Steps**:
- Implement circuit breakers in all services
- Add monitoring and alerting
- Practice chaos engineering
- Test failure scenarios
