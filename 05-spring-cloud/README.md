# Module 05: Spring Cloud

> **Build cloud-native microservices with Spring Cloud**

## 📚 Module Overview

Spring Cloud provides tools for building distributed systems and microservices. Learn service discovery, API gateways, configuration management, and resilience patterns.

---

## 🎯 Learning Objectives

By the end of this module, you will:

- ✅ Implement Service Discovery with Eureka
- ✅ Build API Gateway with Spring Cloud Gateway
- ✅ Centralize configuration with Config Server
- ✅ Implement Circuit Breaker with Resilience4j
- ✅ Add distributed tracing with Sleuth
- ✅ Use Feign for declarative REST clients
- ✅ Implement load balancing

---

## 📂 Module Structure

```
05-spring-cloud/
├── README.md
├── 01-service-discovery/
│   ├── README.md
│   ├── demo-eureka-server/           ← Service Registry
│   └── demo-eureka-client/           ← Client registration
├── 02-api-gateway/
│   ├── README.md
│   └── demo-cloud-gateway/           ← Gateway with routing
├── 03-config-server/
│   ├── README.md
│   └── demo-config-management/       ← Centralized config
├── 04-circuit-breaker/
│   ├── README.md
│   └── demo-resilience4j/            ← Fault tolerance
└── 05-feign-client/
    ├── README.md
    └── demo-feign-rest/              ← Declarative REST
```

---

## 🔑 Spring Cloud Components

### 1. Service Discovery (Eureka)

**Problem**: In microservices, services need to find each other dynamically.

**Solution**: Service Registry pattern with Eureka

```
┌─────────────────────────────────────┐
│       Eureka Server (8761)          │
│     Service Registry                │
└─────────────────────────────────────┘
         ↑                    ↑
    Register              Register
         │                    │
   ┌──────────┐        ┌──────────┐
   │ Service  │        │ Service  │
   │    A     │        │    B     │
   │  (8081)  │        │  (8082)  │
   └──────────┘        └──────────┘
         │                    
    Discover Service B
         │
    Call Service B
```

**Configuration**:

*Eureka Server*:
```java
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
```

```properties
server.port=8761
eureka.client.register-with-eureka=false
eureka.client.fetch-registry=false
```

*Eureka Client*:
```java
@SpringBootApplication
@EnableDiscoveryClient
public class ClientApplication {
    // Application code
}
```

```properties
spring.application.name=product-service
server.port=8081
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
```

---

### 2. API Gateway (Spring Cloud Gateway)

**Problem**: Need single entry point for all microservices.

**Solution**: API Gateway pattern

```
                  ┌──────────────────┐
   Client ───────→│   API Gateway    │
                  │    (Port 8080)   │
                  └──────────────────┘
                          │
        ┌─────────────────┼─────────────────┐
        ↓                 ↓                 ↓
  ┌──────────┐      ┌──────────┐     ┌──────────┐
  │ Product  │      │  Order   │     │   User   │
  │ Service  │      │ Service  │     │ Service  │
  │  (8081)  │      │  (8082)  │     │  (8083)  │
  └──────────┘      └──────────┘     └──────────┘
```

**Features**:
- Request routing
- Load balancing
- Security (authentication/authorization)
- Rate limiting
- Request/response transformation

**Configuration**:

```java
@Configuration
public class GatewayConfig {
    
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            // Route to Product Service
            .route("product_route", r -> r.path("/api/products/**")
                .uri("lb://PRODUCT-SERVICE"))
            
            // Route to Order Service
            .route("order_route", r -> r.path("/api/orders/**")
                .uri("lb://ORDER-SERVICE"))
            
            // Route to User Service
            .route("user_route", r -> r.path("/api/users/**")
                .uri("lb://USER-SERVICE"))
            
            .build();
    }
}
```

**YAML Configuration**:
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: product_route
          uri: lb://PRODUCT-SERVICE
          predicates:
            - Path=/api/products/**
          filters:
            - StripPrefix=1
            
        - id: order_route
          uri: lb://ORDER-SERVICE
          predicates:
            - Path=/api/orders/**
          filters:
            - AddRequestHeader=X-Request-Source, gateway
            - RewritePath=/api/(?<segment>.*), /${segment}
```

---

### 3. Config Server

**Problem**: Managing configuration across multiple services is challenging.

**Solution**: Externalized configuration with Config Server

```
┌────────────────────────────────────┐
│      Git Repository                │
│  (Configuration Files)             │
└────────────────────────────────────┘
              ↑
              │ Read configs
              │
┌────────────────────────────────────┐
│     Config Server (8888)           │
└────────────────────────────────────┘
              ↓
    ┌─────────┴──────────┬──────────┐
    ↓                    ↓          ↓
┌─────────┐        ┌─────────┐  ┌─────────┐
│Service A│        │Service B│  │Service C│
└─────────┘        └─────────┘  └─────────┘
```

**Benefits**:
- ✅ Centralized configuration
- ✅ Environment-specific configs
- ✅ Dynamic refresh without restart
- ✅ Version control for configs
- ✅ Encryption/decryption support

**Config Server Setup**:
```java
@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}
```

```properties
server.port=8888
spring.cloud.config.server.git.uri=https://github.com/your-repo/config-repo
spring.cloud.config.server.git.default-label=main
```

**Client Configuration**:
```properties
spring.application.name=product-service
spring.config.import=optional:configserver:http://localhost:8888
```

---

### 4. Circuit Breaker (Resilience4j)

**Problem**: Cascading failures when a service is down.

**Solution**: Circuit Breaker pattern

```
Circuit Breaker States:

┌─────────┐
│ CLOSED  │ ──→ Normal operation
└─────────┘     All requests pass through
     │
     │ Failure threshold reached
     ↓
┌─────────┐
│  OPEN   │ ──→ Fail fast
└─────────┘     Return fallback immediately
     │
     │ Timeout period expires
     ↓
┌─────────┐
│ HALF    │ ──→ Test if recovered
│  OPEN   │     Allow limited requests
└─────────┘
     │
     ├──→ Success: back to CLOSED
     └──→ Failure: back to OPEN
```

**Implementation**:

```java
@Service
public class ProductService {
    
    @Autowired
    private RestTemplate restTemplate;
    
    @CircuitBreaker(name = "productService", fallbackMethod = "getDefaultProduct")
    @Retry(name = "productService")
    @TimeLimiter(name = "productService")
    public Product getProduct(Long id) {
        return restTemplate.getForObject(
            "http://product-service/api/products/" + id,
            Product.class
        );
    }
    
    // Fallback method - same signature + exception parameter
    public Product getDefaultProduct(Long id, Exception ex) {
        log.error("Circuit breaker fallback triggered for product: {}", id, ex);
        return new Product(id, "Default Product", BigDecimal.ZERO);
    }
}
```

**Configuration** (application.yml):
```yaml
resilience4j:
  circuitbreaker:
    instances:
      productService:
        register-health-indicator: true
        sliding-window-size: 10
        minimum-number-of-calls: 5
        permitted-number-of-calls-in-half-open-state: 3
        automatic-transition-from-open-to-half-open-enabled: true
        wait-duration-in-open-state: 10s
        failure-rate-threshold: 50
        
  retry:
    instances:
      productService:
        max-attempts: 3
        wait-duration: 1s
        
  timelimiter:
    instances:
      productService:
        timeout-duration: 2s
```

---

### 5. Feign Client

**Problem**: Writing REST client code is repetitive.

**Solution**: Declarative REST client with Feign

**Without Feign** (Manual):
```java
@Service
public class OrderService {
    
    @Autowired
    private RestTemplate restTemplate;
    
    public Product getProduct(Long productId) {
        String url = "http://product-service/api/products/" + productId;
        return restTemplate.getForObject(url, Product.class);
    }
    
    public User getUser(Long userId) {
        String url = "http://user-service/api/users/" + userId;
        return restTemplate.getForObject(url, User.class);
    }
}
```

**With Feign** (Declarative):
```java
@FeignClient(name = "product-service")
public interface ProductClient {
    
    @GetMapping("/api/products/{id}")
    Product getProductById(@PathVariable Long id);
    
    @GetMapping("/api/products")
    List<Product> getAllProducts();
    
    @PostMapping("/api/products")
    Product createProduct(@RequestBody ProductDTO productDTO);
}

@FeignClient(name = "user-service")
public interface UserClient {
    
    @GetMapping("/api/users/{id}")
    User getUserById(@PathVariable Long id);
}

// Usage in service
@Service
public class OrderService {
    
    @Autowired
    private ProductClient productClient;
    
    @Autowired
    private UserClient userClient;
    
    public OrderDTO createOrder(CreateOrderRequest request) {
        // Simple, clean calls!
        Product product = productClient.getProductById(request.getProductId());
        User user = userClient.getUserById(request.getUserId());
        
        // Create order logic...
    }
}
```

---

## 🏗️ Complete E-Commerce System Architecture

```
                         ┌──────────────────┐
Client (Browser/Mobile) ─┤   API Gateway    │
                         │   (Port 8080)    │
                         └──────────────────┘
                                  │
                    ┌─────────────┼─────────────┐
                    │             │             │
              ┌───────────┐ ┌───────────┐ ┌───────────┐
              │  Eureka   │ │  Config   │ │  Zipkin   │
              │  Server   │ │  Server   │ │  (Trace)  │
              │  (8761)   │ │  (8888)   │ │  (9411)   │
              └───────────┘ └───────────┘ └───────────┘
                    ↑             ↑             ↑
            Register│    Read     │    Send     │
                    │    Config   │    Traces   │
        ┌───────────┼─────────────┼─────────────┼──────────┐
        │           │             │             │          │
   ┌────────┐  ┌────────┐    ┌────────┐   ┌────────┐ ┌────────┐
   │ User   │  │Product │    │ Order  │   │Payment │ │Notif.  │
   │Service │  │Service │    │Service │   │Service │ │Service │
   │ (8081) │  │ (8082) │    │ (8083) │   │ (8084) │ │ (8085) │
   └────────┘  └────────┘    └────────┘   └────────┘ └────────┘
       │           │              │             │          │
   ┌────────┐  ┌────────┐    ┌────────┐   ┌────────┐ ┌────────┐
   │User DB │  │Product │    │Order DB│   │Payment │ │ Queue  │
   │        │  │   DB   │    │        │   │   DB   │ │        │
   └────────┘  └────────┘    └────────┘   └────────┘ └────────┘
```

---

## 💡 Best Practices

### 1. Service Naming

```yaml
# Use consistent naming
spring:
  application:
    name: product-service  # kebab-case, descriptive

# Eureka will register as: PRODUCT-SERVICE
```

### 2. Health Checks

```java
@RestController
public class HealthController {
    
    @GetMapping("/actuator/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("UP");
    }
}
```

### 3. Load Balancing

```java
@Configuration
public class LoadBalancerConfig {
    
    @Bean
    @LoadBalanced  // Enable client-side load balancing
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
```

### 4. Graceful Shutdown

```properties
server.shutdown=graceful
spring.lifecycle.timeout-per-shutdown-phase=30s
```

---

## 🚀 Quick Start Guide

### Step 1: Start Infrastructure Services

```bash
# Terminal 1 - Eureka Server
cd 05-spring-cloud/01-service-discovery/demo-eureka-server
mvn spring-boot:run

# Terminal 2 - Config Server
cd 05-spring-cloud/03-config-server/demo-config-management
mvn spring-boot:run

# Terminal 3 - API Gateway
cd 05-spring-cloud/02-api-gateway/demo-cloud-gateway
mvn spring-boot:run
```

### Step 2: Start Microservices

```bash
# Terminal 4 - Product Service
cd demo-product-service
mvn spring-boot:run

# Terminal 5 - Order Service
cd demo-order-service
mvn spring-boot:run
```

### Step 3: Test the System

```bash
# Via Gateway
curl http://localhost:8080/api/products

# Check Eureka Dashboard
http://localhost:8761
```

---

## 🎓 Interview Questions

### Q1: What is Service Discovery?

**A:** Mechanism for services to find and communicate with each other dynamically without hard-coding URLs. Eureka provides client-side discovery where services register themselves and query registry for other services.

### Q2: API Gateway vs Reverse Proxy?

**A:** 
- **API Gateway**: Application-level routing, authentication, rate limiting, transformation
- **Reverse Proxy**: Network-level routing, load balancing, SSL termination

### Q3: When to use Circuit Breaker?

**A:** When calling external services that may fail. Prevents cascading failures by:
- Failing fast when service is down
- Providing fallback responses
- Giving service time to recover

---

## 📚 Next Steps

- Complete all demos in this module
- Move to **[Module 06: Messaging](../06-messaging/)** for async communication
- Learn **[Module 07: Observability](../07-observability/)** for monitoring

---

_Build resilient cloud-native microservices! ☁️_
