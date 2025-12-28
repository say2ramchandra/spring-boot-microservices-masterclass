# Microservices Basics

> **Building your first microservices system**

## 📚 Table of Contents

- [What are Microservices?](#what-are-microservices)
- [Microservices vs Monolith](#microservices-vs-monolith)
- [Key Characteristics](#key-characteristics)
- [Service Discovery](#service-discovery)
- [API Gateway](#api-gateway)
- [Design Principles](#design-principles)
- [Demo Project](#demo-project)
- [Interview Questions](#interview-questions)

---

## What are Microservices?

**Microservices** is an architectural style where an application is composed of small, independent services that communicate over a network.

### Core Concept

Instead of building one large application (monolith), you build multiple small services that work together.

```
Traditional Monolith:
┌────────────────────────────────────────┐
│          Single Application            │
│  ┌──────┐  ┌──────┐  ┌───────┐       │
│  │Users │  │Orders│  │Products│       │
│  └──────┘  └──────┘  └───────┘       │
│         Single Database                │
└────────────────────────────────────────┘

Microservices:
┌──────────┐    ┌──────────┐    ┌──────────┐
│  User    │    │  Order   │    │ Product  │
│ Service  │    │ Service  │    │ Service  │
├──────────┤    ├──────────┤    ├──────────┤
│  User DB │    │ Order DB │    │Product DB│
└──────────┘    └──────────┘    └──────────┘
```

---

## Microservices vs Monolith

| Aspect | Monolith | Microservices |
|--------|----------|---------------|
| **Structure** | Single codebase | Multiple services |
| **Deployment** | Deploy entire app | Deploy individual services |
| **Scaling** | Scale entire app | Scale specific services |
| **Technology** | One tech stack | Multiple tech stacks |
| **Database** | Shared database | Database per service |
| **Team** | One team | Multiple teams |
| **Failure** | One failure = entire app down | Isolated failures |
| **Complexity** | Simple initially | Complex from start |

### When to Use Microservices?

**✅ Use Microservices when:**
- Large, complex applications
- Multiple teams working independently
- Need to scale specific parts differently
- Different technologies for different services
- Frequent deployments required

**❌ Avoid Microservices when:**
- Small applications
- Small team
- Simple domain
- Starting a new project (start with modular monolith)

---

## Key Characteristics

### 1. Independently Deployable

Each service can be deployed without affecting others.

```bash
# Deploy only Order Service
cd order-service
mvn clean package
docker build -t order-service .
docker run -p 8083:8083 order-service
```

### 2. Loosely Coupled

Services don't share code or databases.

```java
// ❌ Bad - Tight coupling
class OrderService {
    @Autowired
    private UserRepository userRepository;  // Direct DB access
}

// ✅ Good - Loose coupling
class OrderService {
    @Autowired
    private UserServiceClient userServiceClient;  // API call
}
```

### 3. Business Capability Focused

Each service handles one business domain.

```
User Service:     User management, authentication
Product Service:  Product catalog, inventory
Order Service:    Order processing, cart
Payment Service:  Payments, transactions
```

### 4. Own Their Data

Each service has its own database.

```
User Service    → MySQL (users)
Product Service → PostgreSQL (products)
Order Service   → MongoDB (orders)
```

### 5. Resilient

Failures in one service don't crash the system.

```java
@CircuitBreaker(name = "productService", fallbackMethod = "fallback")
public Product getProduct(Long id) {
    return productClient.getProductById(id);
}

public Product fallback(Long id, Exception ex) {
    return new Product(id, "Default Product", 0.0);
}
```

---

## Service Discovery

**Problem**: In a dynamic environment, how do services find each other?

### Solution: Service Registry (Eureka)

```
┌────────────────────────────────────────┐
│         Eureka Server (8761)           │
│         Service Registry                │
└────────────────────────────────────────┘
         ↑            ↑           ↑
    Register    Register    Register
         │            │           │
   ┌─────────┐  ┌─────────┐  ┌─────────┐
   │  User   │  │ Product │  │  Order  │
   │ Service │  │ Service │  │ Service │
   │  8081   │  │  8082   │  │  8083   │
   └─────────┘  └─────────┘  └─────────┘
```

### How It Works

1. **Services register** with Eureka on startup
2. **Services send heartbeats** every 30 seconds
3. **Clients query Eureka** to find service instances
4. **Load balancing** across multiple instances

### Example: Eureka Server

```java
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
```

```yaml
# application.yml
server:
  port: 8761

eureka:
  client:
    register-with-eureka: false  # Don't register itself
    fetch-registry: false
```

### Example: Eureka Client

```java
@SpringBootApplication
@EnableDiscoveryClient
public class UserServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
```

```yaml
# application.yml
spring:
  application:
    name: user-service

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

---

## API Gateway

**Problem**: Multiple services = multiple endpoints for clients

### Solution: Single Entry Point

```
            ┌─────────────────┐
Client ────→│   API Gateway   │
            │   (Port 8080)   │
            └─────────────────┘
                     │
        ┌────────────┼────────────┐
        ↓            ↓            ↓
   ┌────────┐  ┌─────────┐  ┌────────┐
   │  User  │  │ Product │  │ Order  │
   │Service │  │ Service │  │Service │
   │  8081  │  │  8082   │  │  8083  │
   └────────┘  └─────────┘  └────────┘
```

### Benefits

1. **Single Entry Point**: One URL for clients
2. **Routing**: Route requests to appropriate services
3. **Load Balancing**: Distribute load across instances
4. **Security**: Centralized authentication
5. **Rate Limiting**: Prevent abuse
6. **Request/Response Transformation**: Modify requests/responses
7. **Monitoring**: Centralized logging and metrics

### Example: Spring Cloud Gateway

```java
@SpringBootApplication
public class ApiGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
```

```yaml
# application.yml
spring:
  cloud:
    gateway:
      routes:
        - id: user-service
          uri: lb://user-service  # Load balanced
          predicates:
            - Path=/api/users/**
            
        - id: product-service
          uri: lb://product-service
          predicates:
            - Path=/api/products/**
            
        - id: order-service
          uri: lb://order-service
          predicates:
            - Path=/api/orders/**
```

---

## Design Principles

### 1. Single Responsibility

Each service does ONE thing well.

```
✅ GOOD:
- UserService: Manages users
- ProductService: Manages products
- OrderService: Manages orders

❌ BAD:
- AllInOneService: Users + Products + Orders
```

### 2. Database per Service

Each service owns its data.

```
✅ GOOD:
UserService    → users_db
ProductService → products_db
OrderService   → orders_db

❌ BAD:
All services  → shared_db
```

### 3. Asynchronous Communication

Use events for non-critical operations.

```java
// Synchronous (for critical operations)
Product product = productClient.getProduct(productId);

// Asynchronous (for notifications)
eventPublisher.publish(new OrderCreatedEvent(order));
```

### 4. Fail Fast

Don't wait forever for unresponsive services.

```java
@RestTemplate
@LoadBalanced
public RestTemplate restTemplate(RestTemplateBuilder builder) {
    return builder
        .setConnectTimeout(Duration.ofSeconds(3))
        .setReadTimeout(Duration.ofSeconds(3))
        .build();
}
```

### 5. Design for Failure

Assume services will fail.

```java
@CircuitBreaker(name = "productService", fallbackMethod = "getDefaultProduct")
public Product getProduct(Long id) {
    return productClient.getProductById(id);
}

public Product getDefaultProduct(Long id, Throwable ex) {
    log.warn("Product service unavailable, returning default", ex);
    return new Product(id, "Default Product", BigDecimal.ZERO);
}
```

### 6. API Versioning

Support multiple API versions.

```java
// v1 API
@GetMapping("/api/v1/users/{id}")
public UserV1 getUserV1(@PathVariable Long id) { }

// v2 API (new fields added)
@GetMapping("/api/v2/users/{id}")
public UserV2 getUserV2(@PathVariable Long id) { }
```

### 7. Idempotency

Same request = Same result.

```java
// POST /api/orders with idempotency key
@PostMapping("/orders")
public Order createOrder(
        @RequestBody OrderRequest request,
        @RequestHeader("Idempotency-Key") String key) {
    
    // Check if already processed
    Order existing = orderRepository.findByIdempotencyKey(key);
    if (existing != null) {
        return existing;  // Return existing order
    }
    
    // Create new order
    Order order = new Order();
    order.setIdempotencyKey(key);
    return orderRepository.save(order);
}
```

---

## Demo Project

See [demo-ecommerce-microservices](demo-ecommerce-microservices/) for a complete example with:

- **Eureka Server** (Service Discovery)
- **API Gateway** (Single Entry Point)
- **User Service** (User Management)
- **Product Service** (Product Catalog)
- **Order Service** (Order Processing)

### Architecture

```
                    Client
                      │
                      ↓
              ┌───────────────┐
              │  API Gateway  │ Port 8080
              │  (Routing)    │
              └───────────────┘
                      │
        ┌─────────────┼─────────────┐
        ↓             ↓             ↓
   ┌────────┐    ┌─────────┐   ┌────────┐
   │  User  │    │ Product │   │ Order  │
   │Service │    │ Service │   │Service │
   │  8081  │    │  8082   │   │  8083  │
   └────────┘    └─────────┘   └────────┘
        │             │             │
        ↓             ↓             ↓
   ┌────────┐    ┌─────────┐   ┌────────┐
   │User DB │    │Product  │   │Order DB│
   │  H2    │    │  DB H2  │   │  H2    │
   └────────┘    └─────────┘   └────────┘
        
        All services register with:
        ┌────────────────────┐
        │  Eureka Server     │ Port 8761
        └────────────────────┘
```

---

## Interview Questions

### Q1: What is the difference between monolith and microservices?

**Answer:**

**Monolith:**
- Single codebase and deployment unit
- Shared database
- Scales as a whole
- One technology stack
- Simple initially, complex as it grows

**Microservices:**
- Multiple independent services
- Database per service
- Individual scaling
- Multiple technology stacks
- Complex from start, manageable as it grows

### Q2: What are the main challenges of microservices?

**Answer:**

1. **Distributed System Complexity**: Network latency, failures
2. **Data Consistency**: No ACID transactions across services
3. **Distributed Transactions**: Saga pattern needed
4. **Testing**: Integration testing is complex
5. **Deployment**: Multiple services to deploy
6. **Monitoring**: Need centralized logging and tracing
7. **Security**: More attack surfaces

### Q3: What is Service Discovery and why do we need it?

**Answer:**

**Service Discovery** is a mechanism for services to find each other in a dynamic environment.

**Why needed:**
- Service instances can start/stop dynamically
- IP addresses change in cloud environments
- Multiple instances of same service
- Auto-scaling adds/removes instances

**Example:**
Order Service needs to call Product Service, but doesn't know the IP address. It queries Eureka which returns available Product Service instances.

### Q4: What is an API Gateway and its benefits?

**Answer:**

**API Gateway** is a single entry point for all client requests.

**Benefits:**
1. **Single Entry Point**: One URL for clients
2. **Routing**: Routes to appropriate services
3. **Load Balancing**: Distributes load
4. **Security**: Centralized authentication/authorization
5. **Rate Limiting**: Prevents abuse
6. **Protocol Translation**: HTTP to gRPC, REST to GraphQL
7. **Request Aggregation**: Combine multiple service calls
8. **Monitoring**: Centralized logging

### Q5: How do microservices communicate?

**Answer:**

**Synchronous:**
1. **REST APIs** (most common)
   - HTTP/HTTPS
   - JSON/XML
   - Request-Response

2. **gRPC**
   - Protocol Buffers
   - Faster than REST
   - Binary format

**Asynchronous:**
1. **Message Queues** (RabbitMQ, Kafka)
   - Publish-Subscribe
   - Event-driven
   - Eventual consistency

2. **Event Streaming** (Kafka)
   - Real-time data streams
   - Event sourcing

### Q6: What is the Database per Service pattern?

**Answer:**

Each microservice has its own database that no other service can access directly.

**Benefits:**
- **Loose Coupling**: Services are independent
- **Technology Freedom**: Each service can use different DB
- **Independent Scaling**: Scale database per service needs
- **Fault Isolation**: DB failure affects only one service

**Challenges:**
- **Data Consistency**: No ACID across services
- **Joins**: No cross-service SQL joins
- **Distributed Transactions**: Need Saga pattern

**Example:**
```
User Service    → MySQL (users table)
Product Service → PostgreSQL (products table)
Order Service   → MongoDB (orders collection)
```

### Q7: How do you handle failures in microservices?

**Answer:**

1. **Circuit Breaker**: Stop calling failed services
2. **Retry**: Retry failed requests with exponential backoff
3. **Timeout**: Don't wait forever
4. **Fallback**: Return default value on failure
5. **Bulkhead**: Isolate failures
6. **Health Checks**: Monitor service health
7. **Graceful Degradation**: Partial functionality instead of complete failure

```java
@CircuitBreaker(name = "productService", fallbackMethod = "fallback")
@Retry(name = "productService")
@Timeout(duration = 2, durationUnit = ChronoUnit.SECONDS)
public Product getProduct(Long id) {
    return productClient.getProductById(id);
}

public Product fallback(Long id, Exception ex) {
    return new Product(id, "Default", BigDecimal.ZERO);
}
```

### Q8: What is the Saga pattern?

**Answer:**

**Saga** is a pattern for managing distributed transactions across microservices.

**Two Types:**

1. **Choreography**: Services publish events, others listen
```
Order Service → OrderCreated event
Payment Service → listens → PaymentProcessed event
Inventory Service → listens → InventoryReserved event
```

2. **Orchestration**: Central coordinator controls flow
```
Order Orchestrator:
1. Create Order
2. Process Payment
3. Reserve Inventory
4. Ship Order
(Compensate if any step fails)
```

---

## Summary

| Concept | Description |
|---------|-------------|
| **Microservices** | Architectural style with small, independent services |
| **Service Discovery** | Eureka for finding services dynamically |
| **API Gateway** | Single entry point for all requests |
| **Database per Service** | Each service owns its data |
| **Loose Coupling** | Services communicate via APIs, not direct DB access |
| **Fault Tolerance** | Circuit breaker, retry, timeout patterns |
| **Independent Deployment** | Deploy services independently |

Microservices enable scalability and team independence but add complexity. Start simple, move to microservices when needed.

---

**Next**: [Service Communication](../02-service-communication/)
