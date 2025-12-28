# Section 04: Circuit Breaker Patterns

## Overview

This section covers Circuit Breaker patterns for building resilient microservices. For a comprehensive guide on Resilience4j patterns (Circuit Breaker, Retry, Rate Limiter, Bulkhead, Time Limiter), please refer to:

**📚 [Module 04 - Section 03: Resilience Patterns](../../04-microservices-architecture/03-resilience/README.md)**

That section provides:
- Complete Circuit Breaker implementation with Resilience4j
- Retry patterns with exponential backoff
- Rate limiting strategies
- Bulkhead pattern for resource isolation
- Time limiter configuration
- Working demo with all patterns
- 8 comprehensive interview questions

---

## Quick Reference for Spring Cloud

### Spring Cloud Circuit Breaker

Spring Cloud provides an abstraction layer over different circuit breaker implementations (Resilience4j, Hystrix, Sentinel, Spring Retry).

### Dependencies

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-circuitbreaker-resilience4j</artifactId>
</dependency>

<!-- For reactive applications -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-circuitbreaker-reactor-resilience4j</artifactId>
</dependency>
```

### Basic Usage

#### Imperative Style

```java
@Service
public class ProductService {
    
    @Autowired
    private CircuitBreakerFactory circuitBreakerFactory;
    
    @Autowired
    private RestTemplate restTemplate;
    
    public Product getProduct(Long id) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("product-service");
        
        return circuitBreaker.run(
            // Primary call
            () -> restTemplate.getForObject(
                "http://product-service/api/products/" + id,
                Product.class
            ),
            // Fallback
            throwable -> getFallbackProduct(id)
        );
    }
    
    private Product getFallbackProduct(Long id) {
        Product product = new Product();
        product.setId(id);
        product.setName("Product Unavailable");
        return product;
    }
}
```

#### Reactive Style

```java
@Service
public class ProductService {
    
    @Autowired
    private ReactiveCircuitBreakerFactory circuitBreakerFactory;
    
    @Autowired
    private WebClient webClient;
    
    public Mono<Product> getProduct(Long id) {
        return circuitBreakerFactory.create("product-service")
            .run(
                webClient.get()
                    .uri("http://product-service/api/products/{id}", id)
                    .retrieve()
                    .bodyToMono(Product.class),
                throwable -> Mono.just(getFallbackProduct(id))
            );
    }
}
```

### Configuration

```yaml
resilience4j:
  circuitbreaker:
    instances:
      product-service:
        # Circuit Breaker Configuration
        sliding-window-size: 10
        failure-rate-threshold: 50
        wait-duration-in-open-state: 10s
        permitted-number-of-calls-in-half-open-state: 3
        minimum-number-of-calls: 5
        
        # Record specific exceptions as failures
        record-exceptions:
          - org.springframework.web.client.HttpServerErrorException
          - java.io.IOException
        
        # Ignore specific exceptions
        ignore-exceptions:
          - com.example.BusinessException
  
  retry:
    instances:
      product-service:
        max-attempts: 3
        wait-duration: 1s
        retry-exceptions:
          - org.springframework.web.client.HttpServerErrorException
  
  timelimiter:
    instances:
      product-service:
        timeout-duration: 5s
```

### Integration with Spring Cloud Gateway

Spring Cloud Gateway provides excellent circuit breaker integration for protecting gateway-level routing. This is the **first line of defense** in your microservices architecture.

#### Complete Gateway Circuit Breaker Configuration

**Step 1: Add Dependencies (pom.xml)**

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-gateway</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-circuitbreaker-reactor-resilience4j</artifactId>
</dependency>
```

**Step 2: Configure Routes with Circuit Breaker (application.yml)**

```yaml
spring:
  cloud:
    gateway:
      routes:
        # Product Service with Circuit Breaker
        - id: product-route
          uri: lb://product-service
          predicates:
            - Path=/api/products/**
          filters:
            - name: CircuitBreaker
              args:
                name: productCircuitBreaker
                fallbackUri: forward:/fallback/product
            - AddRequestHeader=X-Gateway-Request, API-Gateway
            
            # Optional: Retry filter
            - name: Retry
              args:
                retries: 3
                statuses: BAD_GATEWAY,SERVICE_UNAVAILABLE
                methods: GET,POST
                backoff:
                  firstBackoff: 50ms
                  maxBackoff: 500ms
                  factor: 2

        # Order Service with Circuit Breaker
        - id: order-route
          uri: lb://order-service
          predicates:
            - Path=/api/orders/**
          filters:
            - name: CircuitBreaker
              args:
                name: orderCircuitBreaker
                fallbackUri: forward:/fallback/order

# Circuit Breaker Configuration
resilience4j:
  circuitbreaker:
    instances:
      productCircuitBreaker:
        sliding-window-size: 10
        minimum-number-of-calls: 5
        failure-rate-threshold: 50
        wait-duration-in-open-state: 10s
        permitted-number-of-calls-in-half-open-state: 3
        automatic-transition-from-open-to-half-open-enabled: true
        sliding-window-type: COUNT_BASED
      
      orderCircuitBreaker:
        sliding-window-size: 10
        minimum-number-of-calls: 5
        failure-rate-threshold: 50
        wait-duration-in-open-state: 10s

  # Retry Configuration
  retry:
    instances:
      default:
        max-attempts: 3
        wait-duration: 1s
        enable-exponential-backoff: true
        exponential-backoff-multiplier: 2
```

**Step 3: Implement Fallback Controller**

```java
@RestController
@RequestMapping("/fallback")
public class FallbackController {
    
    private static final Logger log = LoggerFactory.getLogger(FallbackController.class);
    
    @GetMapping("/product")
    public ResponseEntity<Map<String, Object>> productFallback() {
        log.warn("⚠️ Product Service fallback triggered");
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Product Service is temporarily unavailable");
        response.put("status", "SERVICE_UNAVAILABLE");
        response.put("timestamp", LocalDateTime.now());
        response.put("recommendation", "Please try again in a few moments");
        response.put("supportContact", "support@example.com");
        
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(response);
    }
    
    @GetMapping("/order")
    public ResponseEntity<Map<String, Object>> orderFallback() {
        log.warn("⚠️ Order Service fallback triggered");
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Order Service is temporarily unavailable");
        response.put("status", "SERVICE_UNAVAILABLE");
        response.put("timestamp", LocalDateTime.now());
        response.put("recommendation", "Your order has been queued and will be processed shortly");
        
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(response);
    }
    
    @GetMapping("/general")
    public ResponseEntity<Map<String, Object>> generalFallback() {
        log.warn("⚠️ General fallback triggered");
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Service temporarily unavailable");
        response.put("status", "SERVICE_UNAVAILABLE");
        response.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(response);
    }
}
```

**Step 4: Enable Monitoring**

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,gateway,circuitbreakers,circuitbreakerevents
  endpoint:
    gateway:
      enabled: true
    health:
      show-details: always
  health:
    circuitbreakers:
      enabled: true
```

#### Testing Gateway Circuit Breaker

**Test 1: Normal Operation**
```bash
# Product service is running
curl http://localhost:8080/api/products
# Returns normal response
```

**Test 2: Trigger Fallback**
```bash
# Stop product service
# Make request through gateway
curl http://localhost:8080/api/products

# Expected: Fallback response
{
  "message": "Product Service is temporarily unavailable",
  "status": "SERVICE_UNAVAILABLE",
  "timestamp": "2025-12-19T10:30:00"
}
```

**Test 3: Circuit Breaker Opens**
```bash
# Make multiple failed requests
for i in {1..10}; do
  curl http://localhost:8080/api/products
  sleep 0.5
done

# Check circuit breaker status
curl http://localhost:8080/actuator/circuitbreakers

# Expected: Circuit breaker in OPEN state
{
  "circuitBreakers": {
    "productCircuitBreaker": {
      "state": "OPEN",
      "failureRate": "100.0%"
    }
  }
}
```

**Test 4: Monitor Events**
```bash
# View circuit breaker events
curl http://localhost:8080/actuator/circuitbreakerevents/productCircuitBreaker
```

#### Programmatic Gateway Configuration

For more control, use Java-based configuration:

```java
@Configuration
public class GatewayCircuitBreakerConfig {

    @Bean
    public RouteLocator customRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("product-service-cb", r -> r
                .path("/api/products/**")
                .filters(f -> f
                    .circuitBreaker(config -> config
                        .setName("productCircuitBreaker")
                        .setFallbackUri("forward:/fallback/product")
                        .setStatusCodes("500", "503"))
                    .retry(retryConfig -> retryConfig
                        .setRetries(3)
                        .setStatuses(HttpStatus.BAD_GATEWAY, HttpStatus.SERVICE_UNAVAILABLE)
                        .setBackoff(Duration.ofMillis(50), Duration.ofMillis(500), 2, false)))
                .uri("lb://product-service"))
            .build();
    }
    
    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer() {
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
            .circuitBreakerConfig(CircuitBreakerConfig.ofDefaults())
            .timeLimiterConfig(TimeLimiterConfig.custom()
                .timeoutDuration(Duration.ofSeconds(5))
                .build())
            .build());
    }
}
```

#### Gateway-Specific Best Practices

1. **Use Different Circuit Breakers per Service**
   ```yaml
   # Each service gets its own circuit breaker
   productCircuitBreaker:
     failure-rate-threshold: 50
   
   orderCircuitBreaker:
     failure-rate-threshold: 40  # More strict
   ```

2. **Set Appropriate Timeouts**
   ```yaml
   spring:
     cloud:
       gateway:
         httpclient:
           connect-timeout: 1000
           response-timeout: 5s
   ```

3. **Combine with Retry**
   ```yaml
   filters:
     - CircuitBreaker
     - Retry  # Try 3 times before circuit breaker records failure
   ```

4. **Monitor Circuit Breaker Health**
   ```java
   @Component
   public class CircuitBreakerHealthIndicator implements HealthIndicator {
       
       @Autowired
       private CircuitBreakerRegistry circuitBreakerRegistry;
       
       @Override
       public Health health() {
           boolean allClosed = circuitBreakerRegistry.getAllCircuitBreakers()
               .stream()
               .allMatch(cb -> cb.getState() == CircuitBreaker.State.CLOSED);
           
           if (allClosed) {
               return Health.up().withDetail("circuitBreakers", "All closed").build();
           } else {
               return Health.down().withDetail("circuitBreakers", "Some open").build();
           }
       }
   }
   ```

#### Working Demo

**🎯 Complete working implementation available at:**
- **[Section 02 - API Gateway Demo](../02-api-gateway/demo-gateway/)**

This demo includes:
- ✅ Circuit breaker configured for multiple services
- ✅ Comprehensive fallback controllers
- ✅ Monitoring with Actuator
- ✅ Testing scenarios
- ✅ Production-ready configuration
- ✅ Complete README with setup instructions

### Integration with Feign Client

```java
@FeignClient(
    name = "product-service",
    fallback = ProductClientFallback.class
)
public interface ProductClient {
    
    @GetMapping("/api/products/{id}")
    Product getProductById(@PathVariable Long id);
}

@Component
public class ProductClientFallback implements ProductClient {
    
    @Override
    public Product getProductById(Long id) {
        // Fallback implementation
        Product product = new Product();
        product.setId(id);
        product.setName("Product Unavailable");
        return product;
    }
}
```

```yaml
feign:
  circuitbreaker:
    enabled: true

resilience4j:
  circuitbreaker:
    instances:
      product-service:
        sliding-window-size: 10
        failure-rate-threshold: 50
        wait-duration-in-open-state: 10s
```

### Monitoring Circuit Breaker

#### Actuator Endpoints

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,circuitbreakers,circuitbreakerevents
  health:
    circuitbreakers:
      enabled: true
```

**Endpoints:**

```bash
# View circuit breaker status
GET /actuator/health

# View all circuit breakers
GET /actuator/circuitbreakers

# View circuit breaker events
GET /actuator/circuitbreakerevents

# View specific circuit breaker events
GET /actuator/circuitbreakerevents/product-service
```

#### Metrics

```yaml
management:
  metrics:
    export:
      prometheus:
        enabled: true
```

**Available Metrics:**

```
resilience4j_circuitbreaker_calls_seconds_count
resilience4j_circuitbreaker_calls_seconds_sum
resilience4j_circuitbreaker_state
resilience4j_circuitbreaker_failure_rate
resilience4j_circuitbreaker_buffered_calls
```

### Programmatic Configuration

```java
@Configuration
public class Resilience4JConfig {
    
    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> defaultCustomizer() {
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
            .circuitBreakerConfig(CircuitBreakerConfig.custom()
                .slidingWindowSize(10)
                .failureRateThreshold(50.0f)
                .waitDurationInOpenState(Duration.ofSeconds(10))
                .permittedNumberOfCallsInHalfOpenState(3)
                .minimumNumberOfCalls(5)
                .build())
            .timeLimiterConfig(TimeLimiterConfig.custom()
                .timeoutDuration(Duration.ofSeconds(5))
                .build())
            .build());
    }
    
    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> specificCustomizer() {
        return factory -> factory.configure(builder -> builder
            .circuitBreakerConfig(CircuitBreakerConfig.custom()
                .slidingWindowSize(20)
                .failureRateThreshold(70.0f)
                .build())
            .timeLimiterConfig(TimeLimiterConfig.custom()
                .timeoutDuration(Duration.ofSeconds(10))
                .build()), "slow-service");
    }
}
```

### Event Listeners

```java
@Component
public class CircuitBreakerEventListener {
    
    private static final Logger log = LoggerFactory.getLogger(CircuitBreakerEventListener.class);
    
    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;
    
    @PostConstruct
    public void registerEventListeners() {
        circuitBreakerRegistry.getAllCircuitBreakers().forEach(circuitBreaker -> {
            circuitBreaker.getEventPublisher()
                .onStateTransition(event -> 
                    log.info("Circuit Breaker {} transitioned from {} to {}",
                        event.getCircuitBreakerName(),
                        event.getStateTransition().getFromState(),
                        event.getStateTransition().getToState()))
                
                .onFailureRateExceeded(event ->
                    log.warn("Circuit Breaker {} failure rate exceeded: {}%",
                        event.getCircuitBreakerName(),
                        event.getFailureRate()))
                
                .onError(event ->
                    log.error("Circuit Breaker {} error: {}",
                        event.getCircuitBreakerName(),
                        event.getThrowable().getMessage()));
        });
    }
}
```

---

## Complete Implementation Guide

For a complete, production-ready implementation with:
- ✅ Circuit Breaker pattern
- ✅ Retry with exponential backoff
- ✅ Rate limiting
- ✅ Bulkhead for resource isolation
- ✅ Time limiter
- ✅ Working demo application
- ✅ Testing strategies
- ✅ Monitoring and metrics
- ✅ Interview questions

**Please refer to:**

### 📚 [Module 04 - Section 03: Resilience Patterns](../../04-microservices-architecture/03-resilience/README.md)

That section provides:
- **800+ lines of comprehensive documentation**
- **Complete working demo** with all resilience patterns
- **Production-ready code** examples
- **8 interview questions** with detailed answers
- **Testing strategies** and examples
- **Monitoring and observability** setup
- **Best practices** for production

---

## Quick Comparison: Spring Cloud vs Direct Resilience4j

| Aspect | Spring Cloud Circuit Breaker | Direct Resilience4j |
|--------|----------------------------|---------------------|
| **Abstraction** | Generic interface | Specific implementation |
| **Flexibility** | Can switch implementations | Locked to Resilience4j |
| **Features** | Basic circuit breaker | Full feature set |
| **Configuration** | Simplified | More control |
| **Integration** | Better with Spring Cloud | Better standalone |

**Recommendation:**
- Use **Spring Cloud Circuit Breaker** for gateway-level protection
- Use **Direct Resilience4j** (as in Module 04) for service-level resilience patterns

---

## Additional Resources

### Spring Cloud Documentation
- [Spring Cloud Circuit Breaker](https://spring.io/projects/spring-cloud-circuitbreaker)
- [Resilience4j Spring Boot](https://resilience4j.readme.io/docs/getting-started-3)

### Related Sections
- [Module 04 - Resilience Patterns](../../04-microservices-architecture/03-resilience/) - Comprehensive guide
- [Section 02 - API Gateway](../02-api-gateway/) - Gateway patterns and routing
- [Section 05 - Feign Client](../05-feign-client/) - Declarative REST clients

### Demo Applications
- [Module 04 - Resilience Demo](../../04-microservices-architecture/03-resilience/demo-resilience/) - All resilience patterns
- [Section 02 - API Gateway Demo](../02-api-gateway/demo-gateway/) - **Gateway with Circuit Breaker** ✅
- [Section 05 - Feign Demo](../05-feign-client/demo-feign-order-service/) - **Feign with Circuit Breaker** ✅

---

## Summary

Circuit Breaker is a critical pattern for building resilient microservices. Spring Cloud provides a convenient abstraction layer, while Resilience4j offers comprehensive resilience patterns.

**For detailed learning:**
1. Start with [Module 04 - Section 03](../../04-microservices-architecture/03-resilience/README.md) for complete theory and patterns
2. Review the working demo in Module 04
3. Apply circuit breaker in Gateway - **[Complete Demo Available](../02-api-gateway/demo-gateway/)**
4. Integrate with Feign clients - **[Complete Demo Available](../05-feign-client/demo-feign-order-service/)**

**Key Patterns:**
- ✅ Circuit Breaker (prevent cascading failures)
- ✅ Retry (handle transient failures)
- ✅ Rate Limiter (protect resources)
- ✅ Bulkhead (isolate resources)
- ✅ Time Limiter (prevent long waits)

All patterns are thoroughly covered in Module 04 with working examples and production-ready code.
