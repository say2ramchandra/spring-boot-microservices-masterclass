# Section 05: Declarative REST Clients with OpenFeign

## Table of Contents
1. [Introduction](#introduction)
2. [What is OpenFeign](#what-is-openfeign)
3. [Setting Up Feign Client](#setting-up-feign-client)
4. [Basic Usage](#basic-usage)
5. [Advanced Features](#advanced-features)
6. [Error Handling](#error-handling)
7. [Performance and Timeouts](#performance-and-timeouts)
8. [Best Practices](#best-practices)
9. [Production Enhancements](#production-enhancements)
10. [Interview Questions](#interview-questions)
11. [Working Demo](#working-demo)

---

## Working Demo

**🎯 Complete working implementation available at:**
- **[Feign Order Service Demo](demo-feign-order-service/)**

This demo includes:
- ✅ Complete Feign client implementation with 5 methods
- ✅ Fallback pattern with ProductClientFallback
- ✅ Circuit breaker integration with Resilience4j
- ✅ Service discovery with Eureka
- ✅ Complete order workflow using Feign
- ✅ Error handling and validation
- ✅ JPA persistence layer
- ✅ REST API with 6 endpoints
- ✅ Comprehensive README with testing scenarios
- ✅ Production-ready configuration

**Key Features Demonstrated:**
- Declarative REST client with @FeignClient
- Multiple HTTP methods (GET, POST, PUT)
- Path variables and query parameters
- Fallback behavior when service unavailable
- Circuit breaker state management
- Retry logic with exponential backoff
- Complete business workflow example

---

## Introduction

In microservices architecture, services frequently need to communicate with each other via REST APIs. Writing RestTemplate or WebClient code for every HTTP call becomes repetitive and error-prone.

**OpenFeign** provides a declarative way to write HTTP clients - you just define an interface, and Feign generates the implementation automatically.

### The Problem

**Traditional RestTemplate Approach:**

```java
@Service
public class OrderService {
    
    @Autowired
    private RestTemplate restTemplate;
    
    public Product getProduct(Long id) {
        String url = "http://product-service/api/products/" + id;
        return restTemplate.getForObject(url, Product.class);
    }
    
    public List<Product> getAllProducts() {
        String url = "http://product-service/api/products";
        Product[] products = restTemplate.getForObject(url, Product[].class);
        return Arrays.asList(products);
    }
    
    public Product createProduct(Product product) {
        String url = "http://product-service/api/products";
        return restTemplate.postForObject(url, product, Product.class);
    }
    
    public void updateProduct(Long id, Product product) {
        String url = "http://product-service/api/products/" + id;
        restTemplate.put(url, product);
    }
    
    public void deleteProduct(Long id) {
        String url = "http://product-service/api/products/" + id;
        restTemplate.delete(url);
    }
}
```

**Issues:**
- ❌ Repetitive URL construction
- ❌ Manual serialization/deserialization
- ❌ Error handling scattered everywhere
- ❌ No type safety
- ❌ Hard to test

---

## What is OpenFeign

**OpenFeign** is a declarative HTTP client developed by Netflix (now part of Spring Cloud).

### Key Features

```
✅ Declarative API: Define interface, Feign implements it
✅ Integration: Works with Spring MVC annotations
✅ Load Balancing: Automatic with Eureka/Service Discovery
✅ Circuit Breaker: Integration with Resilience4j
✅ Customizable: Custom encoders, decoders, interceptors
✅ Testable: Easy to mock for unit tests
```

### How It Works

```
Step 1: You define interface with @FeignClient

    @FeignClient(name = "product-service")
    public interface ProductClient {
        @GetMapping("/api/products/{id}")
        Product getProductById(@PathVariable Long id);
    }

Step 2: Feign generates proxy implementation

    - Discovers service via Eureka
    - Constructs HTTP request
    - Serializes/deserializes JSON
    - Handles errors
    - Load balances across instances

Step 3: You use it like any Spring bean

    @Autowired
    private ProductClient productClient;
    
    Product product = productClient.getProductById(1L);
```

---

## Setting Up Feign Client

### Step 1: Add Dependencies

```xml
<dependencies>
    <!-- OpenFeign -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-openfeign</artifactId>
    </dependency>
    
    <!-- Service Discovery (for load balancing) -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
    
    <!-- Circuit Breaker (optional but recommended) -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-circuitbreaker-resilience4j</artifactId>
    </dependency>
</dependencies>
```

### Step 2: Enable Feign Clients

```java
package com.example.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients  // Enable Feign client scanning
public class OrderServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
```

### Step 3: Configure Feign

**application.yml:**

```yaml
spring:
  application:
    name: order-service

# Feign Configuration
feign:
  client:
    config:
      default:  # Global configuration
        connectTimeout: 5000
        readTimeout: 5000
        loggerLevel: full
      
      product-service:  # Service-specific configuration
        connectTimeout: 3000
        readTimeout: 10000
  
  # Circuit Breaker Integration
  circuitbreaker:
    enabled: true
    alphanumeric-ids:
      enabled: true

# Logging for Feign
logging:
  level:
    com.example.order.client: DEBUG
```

---

## Basic Usage

### Example 1: Simple GET Request

**Product Client:**

```java
package com.example.order.client;

import com.example.order.dto.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service")  // Service name from Eureka
public interface ProductClient {
    
    @GetMapping("/api/products/{id}")
    Product getProductById(@PathVariable("id") Long id);
}
```

**Usage in Service:**

```java
@Service
public class OrderService {
    
    @Autowired
    private ProductClient productClient;
    
    public Order createOrder(OrderRequest request) {
        // Call product-service via Feign
        Product product = productClient.getProductById(request.getProductId());
        
        if (product == null) {
            throw new ProductNotFoundException("Product not found");
        }
        
        // Create order
        Order order = new Order();
        order.setProductId(product.getId());
        order.setProductName(product.getName());
        order.setPrice(product.getPrice());
        
        return orderRepository.save(order);
    }
}
```

### Example 2: Request Parameters

```java
@FeignClient(name = "product-service")
public interface ProductClient {
    
    // Query parameters
    @GetMapping("/api/products")
    List<Product> getAllProducts(
        @RequestParam("page") int page,
        @RequestParam("size") int size,
        @RequestParam(value = "sort", required = false) String sort
    );
    
    // Example usage:
    // GET /api/products?page=0&size=10&sort=name
}
```

### Example 3: POST Request

```java
@FeignClient(name = "product-service")
public interface ProductClient {
    
    @PostMapping("/api/products")
    Product createProduct(@RequestBody Product product);
}
```

**Usage:**

```java
Product newProduct = new Product();
newProduct.setName("iPhone 15");
newProduct.setPrice(999.99);

Product created = productClient.createProduct(newProduct);
```

### Example 4: PUT and DELETE

```java
@FeignClient(name = "product-service")
public interface ProductClient {
    
    @PutMapping("/api/products/{id}")
    Product updateProduct(
        @PathVariable("id") Long id,
        @RequestBody Product product
    );
    
    @DeleteMapping("/api/products/{id}")
    void deleteProduct(@PathVariable("id") Long id);
}
```

### Example 5: Headers

```java
@FeignClient(name = "product-service")
public interface ProductClient {
    
    // Static header
    @GetMapping(value = "/api/products/{id}",
                headers = "X-API-Version=v1")
    Product getProductById(@PathVariable Long id);
    
    // Dynamic header
    @GetMapping("/api/products/{id}")
    Product getProductWithAuth(
        @PathVariable Long id,
        @RequestHeader("Authorization") String token
    );
    
    // Multiple headers
    @GetMapping("/api/products/{id}")
    Product getProduct(
        @PathVariable Long id,
        @RequestHeader Map<String, String> headers
    );
}
```

---

## Advanced Features

### Feature 1: Fallback Methods (Circuit Breaker)

**Define Fallback:**

```java
@FeignClient(
    name = "product-service",
    fallback = ProductClientFallback.class  // Fallback implementation
)
public interface ProductClient {
    
    @GetMapping("/api/products/{id}")
    Product getProductById(@PathVariable Long id);
    
    @GetMapping("/api/products")
    List<Product> getAllProducts();
}
```

**Implement Fallback:**

```java
@Component
public class ProductClientFallback implements ProductClient {
    
    private static final Logger log = LoggerFactory.getLogger(ProductClientFallback.class);
    
    @Override
    public Product getProductById(Long id) {
        log.warn("Fallback: getProductById({})", id);
        
        // Return default product
        Product product = new Product();
        product.setId(id);
        product.setName("Product Unavailable");
        product.setPrice(0.0);
        return product;
    }
    
    @Override
    public List<Product> getAllProducts() {
        log.warn("Fallback: getAllProducts()");
        return Collections.emptyList();
    }
}
```

### Feature 2: Fallback Factory (Access Exception)

```java
@FeignClient(
    name = "product-service",
    fallbackFactory = ProductClientFallbackFactory.class
)
public interface ProductClient {
    @GetMapping("/api/products/{id}")
    Product getProductById(@PathVariable Long id);
}
```

**Fallback Factory:**

```java
@Component
public class ProductClientFallbackFactory implements FallbackFactory<ProductClient> {
    
    @Override
    public ProductClient create(Throwable cause) {
        return new ProductClient() {
            @Override
            public Product getProductById(Long id) {
                log.error("Error calling product-service: {}", cause.getMessage());
                
                // Different fallback based on exception
                if (cause instanceof FeignException.NotFound) {
                    throw new ProductNotFoundException("Product not found: " + id);
                } else if (cause instanceof FeignException.ServiceUnavailable) {
                    return getCachedProduct(id);
                } else {
                    return getDefaultProduct(id);
                }
            }
        };
    }
}
```

### Feature 3: Request Interceptors

**Global Request Interceptor:**

```java
@Configuration
public class FeignConfig {
    
    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            // Add authentication header
            String token = SecurityContextHolder.getContext()
                .getAuthentication().getCredentials().toString();
            requestTemplate.header("Authorization", "Bearer " + token);
            
            // Add correlation ID
            String correlationId = MDC.get("correlationId");
            if (correlationId != null) {
                requestTemplate.header("X-Correlation-ID", correlationId);
            }
            
            // Add custom headers
            requestTemplate.header("X-Client", "order-service");
        };
    }
}
```

### Feature 4: Custom Error Decoder

```java
@Configuration
public class FeignConfig {
    
    @Bean
    public ErrorDecoder errorDecoder() {
        return (methodKey, response) -> {
            switch (response.status()) {
                case 400:
                    return new BadRequestException("Bad request");
                case 404:
                    return new ProductNotFoundException("Product not found");
                case 503:
                    return new ServiceUnavailableException("Service unavailable");
                default:
                    return new FeignException.FeignClientException(
                        response.status(),
                        "Error calling " + methodKey,
                        response.request(),
                        null,
                        null
                    );
            }
        };
    }
}
```

### Feature 5: Retry Configuration

```java
@Configuration
public class FeignConfig {
    
    @Bean
    public Retryer retryer() {
        // Retry 3 times with increasing delay
        return new Retryer.Default(
            100,    // Initial interval (ms)
            1000,   // Max interval (ms)
            3       // Max attempts
        );
    }
}
```

### Feature 6: Custom Encoder/Decoder

```java
@Configuration
public class FeignConfig {
    
    @Bean
    public Encoder feignEncoder() {
        return new GsonEncoder();  // Use Gson instead of Jackson
    }
    
    @Bean
    public Decoder feignDecoder() {
        return new GsonDecoder();
    }
}
```

### Feature 7: Contract (Custom Annotations)

```java
@FeignClient(
    name = "product-service",
    configuration = JaxRsConfiguration.class  // Use JAX-RS annotations
)
public interface ProductClient {
    
    @GET
    @Path("/api/products/{id}")
    Product getProductById(@PathParam("id") Long id);
}

@Configuration
public class JaxRsConfiguration {
    
    @Bean
    public Contract feignContract() {
        return new JAXRSContract();  // Use JAX-RS annotations
    }
}
```

---

## Error Handling

### HTTP Status Codes

**Feign handles different status codes:**

```java
try {
    Product product = productClient.getProductById(id);
} catch (FeignException.NotFound e) {
    // 404 - Product not found
    throw new ProductNotFoundException("Product " + id + " not found");
    
} catch (FeignException.BadRequest e) {
    // 400 - Bad request
    throw new InvalidRequestException("Invalid product ID");
    
} catch (FeignException.ServiceUnavailable e) {
    // 503 - Service unavailable
    log.error("Product service is down");
    return getCachedProduct(id);
    
} catch (FeignException e) {
    // Other Feign exceptions
    log.error("Error calling product service: {}", e.getMessage());
    throw new ServiceException("Failed to get product");
}
```

### Global Exception Handling

```java
@ControllerAdvice
public class FeignExceptionHandler {
    
    @ExceptionHandler(FeignException.NotFound.class)
    public ResponseEntity<ErrorResponse> handleNotFound(FeignException.NotFound ex) {
        ErrorResponse error = new ErrorResponse("Resource not found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    @ExceptionHandler(FeignException.ServiceUnavailable.class)
    public ResponseEntity<ErrorResponse> handleServiceUnavailable(FeignException.ServiceUnavailable ex) {
        ErrorResponse error = new ErrorResponse("Service temporarily unavailable");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
    }
    
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorResponse> handleFeignException(FeignException ex) {
        ErrorResponse error = new ErrorResponse("External service error");
        return ResponseEntity.status(ex.status()).body(error);
    }
}
```

---

## Performance and Timeouts

### Connection Timeout vs Read Timeout

```yaml
feign:
  client:
    config:
      default:
        # Time to establish connection
        connectTimeout: 5000  # 5 seconds
        
        # Time to read response
        readTimeout: 10000    # 10 seconds
```

**Connection Timeout:** How long to wait while connecting to server
**Read Timeout:** How long to wait for response after connection

### Connection Pooling

**Use Apache HttpClient (Better Performance):**

```xml
<dependency>
    <groupId>io.github.openfeign</groupId>
    <artifactId>feign-httpclient</artifactId>
</dependency>
```

```yaml
feign:
  httpclient:
    enabled: true
    max-connections: 200
    max-connections-per-route: 50
    time-to-live: 900
    time-to-live-unit: seconds
```

### Compression

```yaml
feign:
  compression:
    request:
      enabled: true
      mime-types: text/xml,application/xml,application/json
      min-request-size: 2048
    response:
      enabled: true
```

---

## Best Practices

### 1. Use Specific Service Configuration

```yaml
feign:
  client:
    config:
      # Service-specific config
      product-service:
        connectTimeout: 3000
        readTimeout: 5000
      
      payment-service:
        connectTimeout: 5000
        readTimeout: 15000  # Payment takes longer
```

### 2. Always Implement Fallbacks

```java
@FeignClient(
    name = "product-service",
    fallback = ProductClientFallback.class  // Always provide fallback
)
public interface ProductClient {
    // ...
}
```

### 3. Use DTOs, Not Entities

```java
// ❌ Bad: Using JPA Entity
@FeignClient(name = "product-service")
public interface ProductClient {
    @GetMapping("/api/products/{id}")
    ProductEntity getProduct(@PathVariable Long id);  // Don't use entities
}

// ✅ Good: Using DTO
@FeignClient(name = "product-service")
public interface ProductClient {
    @GetMapping("/api/products/{id}")
    ProductDTO getProduct(@PathVariable Long id);  // Use DTOs
}
```

### 4. Enable Logging for Debugging

```yaml
logging:
  level:
    com.example.order.client: DEBUG  # Log Feign calls

feign:
  client:
    config:
      default:
        loggerLevel: full  # Log request/response details
```

**Logger Levels:**
- `NONE`: No logging
- `BASIC`: Log request method, URL, response status, execution time
- `HEADERS`: Log request/response headers
- `FULL`: Log headers, body, metadata

### 5. Package Clients Separately

```
src/main/java/com/example/order/
├── client/
│   ├── ProductClient.java
│   ├── ProductClientFallback.java
│   ├── UserClient.java
│   └── UserClientFallback.java
├── controller/
├── service/
└── OrderServiceApplication.java
```

### 6. Create Base Interfaces for Common Operations

```java
public interface CrudClient<T, ID> {
    
    @GetMapping("/{id}")
    T getById(@PathVariable("id") ID id);
    
    @GetMapping
    List<T> getAll();
    
    @PostMapping
    T create(@RequestBody T entity);
    
    @PutMapping("/{id}")
    T update(@PathVariable("id") ID id, @RequestBody T entity);
    
    @DeleteMapping("/{id}")
    void delete(@PathVariable("id") ID id);
}

@FeignClient(name = "product-service", path = "/api/products")
public interface ProductClient extends CrudClient<Product, Long> {
    // Inherits all CRUD operations
    
    // Add custom methods
    @GetMapping("/search")
    List<Product> search(@RequestParam("name") String name);
}
```

### 7. Use Circuit Breaker Patterns

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
        wait-duration-in-open-state: 10000
```

---

## Production Enhancements

### 1. Distributed Tracing with Spring Cloud Sleuth

Distributed tracing helps track requests across multiple microservices.

**Add Dependencies:**

```xml
<dependencies>
    <!-- Spring Cloud Sleuth -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-sleuth</artifactId>
    </dependency>
    
    <!-- Zipkin for visualization (optional) -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-sleuth-zipkin</artifactId>
    </dependency>
</dependencies>
```

**Configuration:**

```yaml
spring:
  application:
    name: order-service
  
  # Zipkin Configuration
  zipkin:
    base-url: http://localhost:9411
    sender:
      type: web
  
  # Sleuth Configuration
  sleuth:
    sampler:
      probability: 1.0  # Sample 100% of requests (use 0.1 for 10% in production)
    
    # Propagate trace context to Feign calls
    feign:
      enabled: true
    
    # Add trace IDs to logs
    log:
      slf4j:
        enabled: true
```

**Automatic Trace Propagation:**

Sleuth automatically adds trace headers to Feign calls:

```
X-B3-TraceId: 5c4e8f8e5d3a1b2c
X-B3-SpanId: 1a2b3c4d5e6f7g8h
X-B3-ParentSpanId: 9i8h7g6f5e4d3c2b
X-B3-Sampled: 1
```

**Custom Span for Feign Call:**

```java
@Service
public class OrderService {
    
    @Autowired
    private ProductClient productClient;
    
    @Autowired
    private Tracer tracer;
    
    public Order createOrder(OrderRequest request) {
        Span span = tracer.nextSpan().name("fetch-product").start();
        
        try (Tracer.SpanInScope ws = tracer.withSpan(span)) {
            span.tag("product.id", String.valueOf(request.getProductId()));
            
            Product product = productClient.getProductById(request.getProductId());
            
            span.tag("product.name", product.getName());
            return createOrderFromProduct(product, request);
            
        } catch (Exception e) {
            span.error(e);
            throw e;
        } finally {
            span.end();
        }
    }
}
```

**View Traces in Zipkin:**

```bash
# Start Zipkin
docker run -d -p 9411:9411 openzipkin/zipkin

# Access UI
http://localhost:9411
```

**Log Output with Trace IDs:**

```
2025-12-19 10:30:00 [order-service,5c4e8f8e5d3a1b2c,1a2b3c4d5e6f7g8h] INFO  OrderService - Creating order
2025-12-19 10:30:01 [order-service,5c4e8f8e5d3a1b2c,2b3c4d5e6f7g8h9i] DEBUG ProductClient - Calling product-service
2025-12-19 10:30:02 [order-service,5c4e8f8e5d3a1b2c,1a2b3c4d5e6f7g8h] INFO  OrderService - Order created successfully
```

**Trace ID Format:** `[service-name, trace-id, span-id]`

---

### 2. Security Integration (OAuth2 & JWT)

#### JWT Token Propagation

**Add JWT to Feign Requests:**

```java
@Configuration
public class FeignSecurityConfig {
    
    @Bean
    public RequestInterceptor jwtRequestInterceptor() {
        return requestTemplate -> {
            // Get JWT from SecurityContext
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication != null && authentication.getCredentials() != null) {
                String token = authentication.getCredentials().toString();
                requestTemplate.header("Authorization", "Bearer " + token);
            }
        };
    }
}
```

**Alternative: Get JWT from Request:**

```java
@Configuration
public class FeignSecurityConfig {
    
    @Bean
    public RequestInterceptor jwtTokenRelayInterceptor() {
        return requestTemplate -> {
            ServletRequestAttributes requestAttributes = 
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            
            if (requestAttributes != null) {
                HttpServletRequest request = requestAttributes.getRequest();
                String authHeader = request.getHeader("Authorization");
                
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    requestTemplate.header("Authorization", authHeader);
                }
            }
        };
    }
}
```

#### OAuth2 Client Integration

**Dependencies:**

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-client</artifactId>
</dependency>
```

**Configuration:**

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          product-service-client:
            provider: keycloak
            client-id: order-service
            client-secret: ${CLIENT_SECRET}
            authorization-grant-type: client_credentials
            scope: read,write
        
        provider:
          keycloak:
            token-uri: http://localhost:8080/auth/realms/microservices/protocol/openid-connect/token
```

**OAuth2 Feign Interceptor:**

```java
@Configuration
public class FeignOAuth2Config {
    
    @Autowired
    private OAuth2AuthorizedClientManager authorizedClientManager;
    
    @Bean
    public RequestInterceptor oauth2FeignRequestInterceptor() {
        return requestTemplate -> {
            OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest
                .withClientRegistrationId("product-service-client")
                .principal("order-service")
                .build();
            
            OAuth2AuthorizedClient authorizedClient = 
                authorizedClientManager.authorize(authorizeRequest);
            
            if (authorizedClient != null) {
                String accessToken = authorizedClient.getAccessToken().getTokenValue();
                requestTemplate.header("Authorization", "Bearer " + accessToken);
            }
        };
    }
    
    @Bean
    public OAuth2AuthorizedClientManager authorizedClientManager(
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientRepository authorizedClientRepository) {
        
        OAuth2AuthorizedClientProvider authorizedClientProvider =
            OAuth2AuthorizedClientProviderBuilder.builder()
                .clientCredentials()
                .build();
        
        DefaultOAuth2AuthorizedClientManager authorizedClientManager =
            new DefaultOAuth2AuthorizedClientManager(
                clientRegistrationRepository, 
                authorizedClientRepository);
        
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);
        
        return authorizedClientManager;
    }
}
```

#### Service-to-Service Authentication

**Mutual TLS (mTLS):**

```yaml
feign:
  client:
    config:
      product-service:
        # Client certificate for mTLS
        key-store: classpath:client-keystore.p12
        key-store-password: ${KEYSTORE_PASSWORD}
        key-store-type: PKCS12
        
        # Server certificate validation
        trust-store: classpath:client-truststore.p12
        trust-store-password: ${TRUSTSTORE_PASSWORD}
        trust-store-type: PKCS12
```

---

### 3. Monitoring with Prometheus & Grafana

#### Enable Metrics

**Dependencies:**

```xml
<dependencies>
    <!-- Actuator for metrics -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
    
    <!-- Micrometer Prometheus -->
    <dependency>
        <groupId>io.micrometer</groupId>
        <artifactId>micrometer-registry-prometheus</artifactId>
    </dependency>
</dependencies>
```

**Configuration:**

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  
  # Prometheus endpoint
  metrics:
    export:
      prometheus:
        enabled: true
    
    # Enable Feign metrics
    tags:
      application: ${spring.application.name}
  
  endpoint:
    metrics:
      enabled: true
    prometheus:
      enabled: true
```

#### Feign Metrics Available

**Default Metrics:**

```
# Total HTTP calls
http_client_requests_seconds_count{client="product-service",method="GET",uri="/api/products/{id}"}

# Success rate
http_client_requests_seconds_count{client="product-service",status="200"}

# Error rate
http_client_requests_seconds_count{client="product-service",status="500"}

# Response time
http_client_requests_seconds_sum{client="product-service"}

# Circuit breaker metrics
resilience4j_circuitbreaker_state{name="productCircuitBreaker"}
```

#### Custom Metrics

**Add Custom Metrics:**

```java
@Service
public class OrderService {
    
    @Autowired
    private ProductClient productClient;
    
    @Autowired
    private MeterRegistry meterRegistry;
    
    public Order createOrder(OrderRequest request) {
        Timer.Sample sample = Timer.start(meterRegistry);
        
        try {
            Product product = productClient.getProductById(request.getProductId());
            Order order = createOrderFromProduct(product, request);
            
            // Record success metric
            meterRegistry.counter("orders.created", 
                "product_id", String.valueOf(product.getId()),
                "status", "success"
            ).increment();
            
            return order;
            
        } catch (Exception e) {
            // Record failure metric
            meterRegistry.counter("orders.created",
                "status", "failure",
                "error", e.getClass().getSimpleName()
            ).increment();
            - **[See Production Enhancements](#production-enhancements)**
- Implement security - **[See Production Enhancements](#production-enhancements)**
- Monitor with Prometheus/Grafana - **[See Production Enhancements](#production-enhancements)**
        } finally {
            sample.stop(Timer.builder("orders.creation.time")
                .tag("service", "order-service")
                .register(meterRegistry));
        }
    }
}
```

#### Prometheus Configuration

**prometheus.yml:**

```yaml
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'order-service'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['localhost:8083']
  
  - job_name: 'product-service'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['localhost:8082']
```

**Start Prometheus:**

```bash
docker run -d -p 9090:9090 \
  -v $(pwd)/prometheus.yml:/etc/prometheus/prometheus.yml \
  prom/prometheus
```

#### Grafana Dashboard

**Key Metrics to Monitor:**

1. **Request Rate:**
   ```promql
   rate(http_client_requests_seconds_count{client="product-service"}[5m])
   ```

2. **Error Rate:**
   ```promql
   rate(http_client_requests_seconds_count{client="product-service",status=~"5.."}[5m])
   ```

3. **Success Rate:**
   ```promql
   rate(http_client_requests_seconds_count{client="product-service",status="200"}[5m])
   / rate(http_client_requests_seconds_count{client="product-service"}[5m])
   ```

4. **Average Response Time:**
   ```promql
   rate(http_client_requests_seconds_sum{client="product-service"}[5m])
   / rate(http_client_requests_seconds_count{client="product-service"}[5m])
   ```

5. **95th Percentile Response Time:**
   ```promql
   histogram_quantile(0.95, 
     rate(http_client_requests_seconds_bucket{client="product-service"}[5m]))
   ```

6. **Circuit Breaker State:**
   ```promql
   resilience4j_circuitbreaker_state{name="productCircuitBreaker"}
   ```

**Import Grafana Dashboard:**

```bash
# Start Grafana
docker run -d -p 3000:3000 grafana/grafana

# Access: http://localhost:3000
# Default credentials: admin/admin

# Add Prometheus as data source
# Import dashboard ID: 11892 (Spring Boot 2.1+ dashboard)
```

**Sample Grafana Dashboard Panels:**

1. **Feign Client Success Rate**
2. **Feign Client Response Time (p50, p95, p99)**
3. **Feign Client Request Rate**
4. **Feign Client Error Rate by Status Code**
5. **Circuit Breaker States**
6. **Top 5 Slowest Endpoints**

#### Alerting Rules

**prometheus-alerts.yml:**

```yaml
groups:
  - name: feign_client_alerts
    interval: 30s
    rules:
      # High error rate
      - alert: HighFeignErrorRate
        expr: |
          rate(http_client_requests_seconds_count{status=~"5.."}[5m])
          / rate(http_client_requests_seconds_count[5m])
          > 0.05
        for: 5m
        annotations:
          summary: "High error rate for Feign client"
          description: "Error rate is {{ $value }}%"
      
      # Slow response time
      - alert: SlowFeignResponse
        expr: |
          histogram_quantile(0.95,
            rate(http_client_requests_seconds_bucket[5m]))
          > 5
        for: 5m
        annotations:
          summary: "Slow Feign client response time"
          description: "P95 response time is {{ $value }}s"
      
      # Circuit breaker open
      - alert: CircuitBreakerOpen
        expr: resilience4j_circuitbreaker_state{state="open"} == 1
        for: 2m
        annotations:
          summary: "Circuit breaker {{ $labels.name }} is OPEN"
          description: "Service {{ $labels.name }} circuit breaker has been open for 2 minutes"
```

---

### Production Deployment Checklist

#### Security
- [ ] Enable HTTPS for all Feign calls
- [ ] Implement JWT token propagation
- [ ] Use OAuth2 for service-to-service auth
- [ ] Enable mTLS for sensitive services
- [ ] Rotate secrets regularly
- [ ] Use vault for credential management

#### Monitoring
- [ ] Enable Prometheus metrics export
- [ ] Set up Grafana dashboards
- [ ] Configure alerting rules
- [ ] Monitor circuit breaker states
- [ ] Track error rates and response times
- [ ] Set up log aggregation (ELK/Loki)

#### Tracing
- [ ] Enable Spring Cloud Sleuth
- [ ] Configure Zipkin/Jaeger
- [ ] Set appropriate sampling rate
- [ ] Add custom spans for critical operations
- [ ] Include trace IDs in logs
- [ ] Correlate traces with metrics

#### Performance
- [ ] Configure connection pooling
- [ ] Set appropriate timeouts
- [ ] Enable request/response compression
- [ ] Implement caching where appropriate
- [ ] Use async/reactive for heavy loads
- [ ] Load test Feign clients

#### Resilience
- [ ] Implement fallback methods
- [ ] Configure circuit breakers
- [ ] Set up retry policies
- [ ] Define timeout strategies
- [ ] Test failure scenarios
- [ ] Document degradation modes

---

## Interview Questions

### Q1: What is OpenFeign and why use it?

**Answer:**

**OpenFeign** is a declarative HTTP client that simplifies REST API calls in microservices.

**Without Feign:**
```java
@Service
public class OrderService {
    @Autowired
    private RestTemplate restTemplate;
    
    public Product getProduct(Long id) {
        String url = "http://product-service/api/products/" + id;
        return restTemplate.getForObject(url, Product.class);
    }
}
```

**With Feign:**
```java
@FeignClient(name = "product-service")
public interface ProductClient {
    @GetMapping("/api/products/{id}")
    Product getProductById(@PathVariable Long id);
}

@Service
public class OrderService {
    @Autowired
    private ProductClient productClient;
    
    public Product getProduct(Long id) {
        return productClient.getProductById(id);
    }
}
```

**Benefits:**
- ✅ Less boilerplate code
- ✅ Type-safe interface
- ✅ Automatic load balancing
- ✅ Built-in circuit breaker support
- ✅ Easy to test (mock interface)
- ✅ Declarative error handling

---

### Q2: How does Feign integrate with Eureka for load balancing?

**Answer:**

Feign automatically integrates with Eureka and Spring Cloud LoadBalancer for client-side load balancing.

**Configuration:**

```java
@FeignClient(name = "product-service")  // Service name from Eureka
public interface ProductClient {
    @GetMapping("/api/products/{id}")
    Product getProductById(@PathVariable Long id);
}
```

**How It Works:**

```
1. Feign receives call: productClient.getProductById(1L)
2. Resolves "product-service" via Eureka
3. Eureka returns list of instances:
   - product-service-1: 192.168.1.10:8081
   - product-service-2: 192.168.1.11:8081
   - product-service-3: 192.168.1.12:8081
4. LoadBalancer picks one instance (Round Robin)
5. Feign makes HTTP call to selected instance
6. Returns result
```

**Load Balancing Algorithm:**

Default: **Round Robin**

Custom:
```java
@Configuration
public class LoadBalancerConfig {
    
    @Bean
    public ReactorLoadBalancer<ServiceInstance> randomLoadBalancer(
            Environment environment,
            LoadBalancerClientFactory loadBalancerClientFactory) {
        String name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
        return new RandomLoadBalancer(
            loadBalancerClientFactory.getLazyProvider(name, ServiceInstanceListSupplier.class),
            name
        );
    }
}
```

---

### Q3: What is the difference between Feign fallback and fallbackFactory?

**Answer:**

| Feature | Fallback | FallbackFactory |
|---------|----------|-----------------|
| **Access to Exception** | ❌ No | ✅ Yes |
| **Definition** | Static implementation | Dynamic based on exception |
| **Use Case** | Simple default response | Complex error handling |

**Fallback (Simple):**

```java
@FeignClient(name = "product-service", fallback = ProductClientFallback.class)
public interface ProductClient {
    @GetMapping("/api/products/{id}")
    Product getProductById(@PathVariable Long id);
}

@Component
public class ProductClientFallback implements ProductClient {
    @Override
    public Product getProductById(Long id) {
        // No access to exception
        return getDefaultProduct();
    }
}
```

**FallbackFactory (Advanced):**

```java
@FeignClient(name = "product-service", fallbackFactory = ProductClientFallbackFactory.class)
public interface ProductClient {
    @GetMapping("/api/products/{id}")
    Product getProductById(@PathVariable Long id);
}

@Component
public class ProductClientFallbackFactory implements FallbackFactory<ProductClient> {
    
    @Override
    public ProductClient create(Throwable cause) {
        return new ProductClient() {
            @Override
            public Product getProductById(Long id) {
                // Access to exception
                if (cause instanceof FeignException.NotFound) {
                    throw new ProductNotFoundException();
                } else if (cause instanceof FeignException.ServiceUnavailable) {
                    return getCachedProduct(id);
                } else {
                    return getDefaultProduct();
                }
            }
        };
    }
}
```

**When to Use:**
- **Fallback**: Simple scenarios, same fallback for all errors
- **FallbackFactory**: Need to inspect exception, different handling per error type

---

### Q4: How do you configure timeouts in Feign?

**Answer:**

**Two Types of Timeouts:**

1. **Connection Timeout**: Time to establish connection
2. **Read Timeout**: Time to wait for response

**Configuration:**

```yaml
feign:
  client:
    config:
      default:  # Global
        connectTimeout: 5000  # 5 seconds
        readTimeout: 10000    # 10 seconds
      
      product-service:  # Service-specific
        connectTimeout: 3000
        readTimeout: 15000
      
      payment-service:
        connectTimeout: 5000
        readTimeout: 30000  # Payment takes longer
```

**Programmatic Configuration:**

```java
@Configuration
public class FeignConfig {
    
    @Bean
    public Request.Options requestOptions() {
        return new Request.Options(
            5000,  // connect timeout
            TimeUnit.MILLISECONDS,
            10000, // read timeout
            TimeUnit.MILLISECONDS,
            true   // follow redirects
        );
    }
}
```

**Best Practices:**

```
Fast Services (Lookups):
- connectTimeout: 2-3 seconds
- readTimeout: 5 seconds

Medium Services (Business Logic):
- connectTimeout: 3-5 seconds
- readTimeout: 10-15 seconds

Slow Services (Reports, Payments):
- connectTimeout: 5 seconds
- readTimeout: 30-60 seconds
```

---

### Q5: How do you test Feign clients?

**Answer:**

**Method 1: Mock Feign Client**

```java
@SpringBootTest
public class OrderServiceTest {
    
    @MockBean
    private ProductClient productClient;
    
    @Autowired
    private OrderService orderService;
    
    @Test
    public void testCreateOrder() {
        // Given
        Product product = new Product(1L, "iPhone", 999.99);
        when(productClient.getProductById(1L)).thenReturn(product);
        
        // When
        Order order = orderService.createOrder(new OrderRequest(1L, 2));
        
        // Then
        assertNotNull(order);
        assertEquals("iPhone", order.getProductName());
        verify(productClient).getProductById(1L);
    }
}
```

**Method 2: WireMock (Integration Testing)**

```java
@SpringBootTest
@AutoConfigureWireMock(port = 0)
public class ProductClientIntegrationTest {
    
    @Autowired
    private ProductClient productClient;
    
    @Test
    public void testGetProductById() {
        // Setup WireMock stub
        stubFor(get(urlEqualTo("/api/products/1"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"id\":1,\"name\":\"iPhone\",\"price\":999.99}")));
        
        // Call Feign client
        Product product = productClient.getProductById(1L);
        
        // Assert
        assertEquals(1L, product.getId());
        assertEquals("iPhone", product.getName());
    }
}
```

**Method 3: TestRestTemplate (End-to-End)**

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrderControllerE2ETest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @MockBean
    private ProductClient productClient;
    
    @Test
    public void testCreateOrderEndpoint() {
        // Given
        Product product = new Product(1L, "iPhone", 999.99);
        when(productClient.getProductById(1L)).thenReturn(product);
        
        OrderRequest request = new OrderRequest(1L, 2);
        
        // When
        ResponseEntity<Order> response = restTemplate.postForEntity(
            "/api/orders",
            request,
            Order.class
        );
        
        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}
```

---

## Summary

**Key Takeaways:**

1. ✅ **Declarative API**: Define interface, Feign implements it
2. ✅ **Spring MVC Annotations**: Use familiar @GetMapping, @PostMapping
3. ✅ **Service Discovery**: Automatic integration with Eureka
4. ✅ **Load Balancing**: Client-side load balancing built-in
5. ✅ **Circuit Breaker**: Easy integration with Resilience4j
6. ✅ **Fallbacks**: Graceful degradation with fallback methods
7. ✅ **Customizable**: Custom encoders, decoders, interceptors
8. ✅ **Testable**: Easy to mock for unit tests

**When to Use Feign:**
- ✅ Microservices communication
- ✅ Need declarative REST client
- ✅ Want automatic load balancing
- ✅ Need circuit breaker integration

**When NOT to Use Feign:**
- ❌ Non-REST protocols (gRPC, WebSocket)
- ❌ Need reactive programming (use WebClient)
- ❌ External APIs with complex authentication

**Production Checklist:**
- [ ] Configure appropriate timeouts
- [ ] Implement fallbacks for resilience
- [ ] Enable circuit breaker
- [ ] Use connection pooling (Apache HttpClient)
- [ ] Add request interceptors for auth/tracing
- [ ] Configure logging appropriately
- [ ] Monitor Feign metrics

**Complete Working Example:**
- 🎯 **[Feign Order Service Demo](demo-feign-order-service/)** - Production-ready implementation
- Features: Fallbacks, Circuit Breaker, Service Discovery, Error Handling
- Includes: Complete setup guide, API documentation, testing scenarios

**Next Steps:**
- Integrate with API Gateway - **[Gateway Demo Available](../02-api-gateway/demo-gateway/)**
- Add distributed tracing (Spring Cloud Sleuth)
- Implement security (OAuth2, JWT)
- Monitor with Prometheus/Grafana

---

## Additional Resources

### Related Documentation
- [Spring Cloud OpenFeign Documentation](https://spring.io/projects/spring-cloud-openfeign)
- [Section 02 - API Gateway](../02-api-gateway/) - Gateway patterns and routing
- [Section 04 - Circuit Breaker](../04-circuit-breaker/) - Resilience patterns
- [Module 04 - Resilience Patterns](../../04-microservices-architecture/03-resilience/)

### Working Demos
- **[Feign Order Service](demo-feign-order-service/)** - Complete Feign client demo ✅
- [API Gateway Demo](../02-api-gateway/demo-gateway/) - Gateway with circuit breaker
- [Config Server Demo](../03-config-server/demo-config-server/) - Centralized configuration
