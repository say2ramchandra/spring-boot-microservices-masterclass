# Module 04: Microservices Architecture

> **Design and build scalable microservices systems**

## 📚 Module Overview

Transition from monolithic applications to **microservices architecture**. Learn to design, build, and deploy distributed systems with Spring Boot and Spring Cloud.

---

## 🎯 Learning Objectives

By the end of this module, you will:

- ✅ Understand microservices principles and patterns
- ✅ Design service boundaries using Domain-Driven Design
- ✅ Implement inter-service communication (REST, messaging)
- ✅ Set up service discovery with Eureka
- ✅ Configure API Gateway with Spring Cloud Gateway
- ✅ Implement circuit breakers with Resilience4j
- ✅ Handle distributed transactions
- ✅ Apply microservices best practices

---

## 📂 Module Structure

```
04-microservices-architecture/
├── README.md
├── 01-microservices-basics/
│   ├── README.md
│   └── demo-ecommerce-microservices/    ← Complete e-commerce system
│       ├── eureka-server/               ← Service Discovery
│       ├── api-gateway/                 ← API Gateway
│       ├── product-service/             ← Product microservice
│       ├── order-service/               ← Order microservice
│       └── user-service/                ← User microservice
├── 02-service-communication/
│   ├── README.md
│   ├── demo-rest-communication/         ← REST APIs
│   └── demo-messaging/                  ← Message queues
└── 03-resilience/
    ├── README.md
    └── demo-circuit-breaker/            ← Fault tolerance
```

---

## 🏗️ Microservices Architecture

### Monolith vs Microservices

```
MONOLITHIC ARCHITECTURE:
┌────────────────────────────────┐
│      Single Application        │
├────────────────────────────────┤
│  ┌──────┐  ┌──────┐  ┌──────┐│
│  │ User │  │Order │  │Product││
│  │Module│  │Module│  │Module ││
│  └──────┘  └──────┘  └──────┘│
├────────────────────────────────┤
│      Single Database           │
└────────────────────────────────┘
    ↓
- One deployment unit
- Tightly coupled
- Scales as whole
- Technology lock-in


MICROSERVICES ARCHITECTURE:
┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│ User Service │  │Order Service │  │Product Service│
├──────────────┤  ├──────────────┤  ├──────────────┤
│  Business    │  │  Business    │  │  Business    │
│   Logic      │  │   Logic      │  │   Logic      │
├──────────────┤  ├──────────────┤  ├──────────────┤
│  User DB     │  │  Order DB    │  │  Product DB  │
└──────────────┘  └──────────────┘  └──────────────┘
    ↓                 ↓                  ↓
- Independent deployment
- Loosely coupled
- Individual scaling
- Technology freedom
```

---

## 🔑 Key Patterns

### 1. Service Discovery

**Problem**: How do services find each other in a dynamic environment?

**Solution**: Service Registry (Eureka)

```
┌────────────────────────────────────────┐
│         Eureka Server                   │
│      (Service Registry)                 │
└────────────────────────────────────────┘
         ↑            ↑           ↑
    Register    Register    Register
         │            │           │
   ┌─────────┐  ┌─────────┐  ┌─────────┐
   │ Service │  │ Service │  │ Service │
   │    A    │  │    B    │  │    C    │
   └─────────┘  └─────────┘  └─────────┘
```

### 2. API Gateway

**Problem**: How to handle cross-cutting concerns (auth, routing, rate limiting)?

**Solution**: API Gateway Pattern

```
                   ┌─────────────┐
Client ──────────→ │ API Gateway │
                   └─────────────┘
                          │
          ┌───────────────┼───────────────┐
          ↓               ↓               ↓
    ┌─────────┐     ┌─────────┐    ┌─────────┐
    │ Service │     │ Service │    │ Service │
    │    A    │     │    B    │    │    C    │
    └─────────┘     └─────────┘    └─────────┘
```

### 3. Circuit Breaker

**Problem**: How to handle service failures gracefully?

**Solution**: Circuit Breaker Pattern

```
States:
┌────────┐  Success  ┌────────┐
│ CLOSED │ ─────────→│  OPEN  │
└────────┘           └────────┘
    ↑                    │
    │      Timeout       │
    │    ←───────────────┘
    │   ┌────────────┐
    └───│ HALF_OPEN  │
        └────────────┘
```

---

## 📖 E-Commerce Microservices Demo

We'll build a complete e-commerce system with:

### Services

1. **Eureka Server** (Port 8761)
   - Service discovery and registration

2. **API Gateway** (Port 8080)
   - Single entry point
   - Routing to services
   - Load balancing

3. **User Service** (Port 8081)
   - User management
   - Authentication

4. **Product Service** (Port 8082)
   - Product catalog
   - Inventory management

5. **Order Service** (Port 8083)
   - Order processing
   - Calls User & Product services

### Communication Flow

```
Client Request
    │
    ↓
API Gateway (8080)
    │
    ├─→ User Service (8081)
    │   - GET /api/users/{id}
    │
    ├─→ Product Service (8082)
    │   - GET /api/products
    │   - GET /api/products/{id}
    │
    └─→ Order Service (8083)
        - POST /api/orders
        - Calls User Service
        - Calls Product Service
```

---

## 🚀 Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+
- Postman (for testing)

### Quick Start

**Step 1**: Start Eureka Server

```bash
cd demo-ecommerce-microservices/eureka-server
mvn spring-boot:run
```

Access: http://localhost:8761

**Step 2**: Start API Gateway

```bash
cd demo-ecommerce-microservices/api-gateway
mvn spring-boot:run
```

**Step 3**: Start All Services

```bash
# Terminal 1
cd demo-ecommerce-microservices/user-service
mvn spring-boot:run

# Terminal 2
cd demo-ecommerce-microservices/product-service
mvn spring-boot:run

# Terminal 3
cd demo-ecommerce-microservices/order-service
mvn spring-boot:run
```

**Step 4**: Test the System

```bash
# Get all products (via Gateway)
curl http://localhost:8080/api/products

# Create an order
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "productId": 1,
    "quantity": 2
  }'
```

---

## 💡 Microservices Best Practices

### 1. Design Principles

#### Single Responsibility
Each service has one business capability.

```
✅ GOOD: 
- User Service (manages users only)
- Product Service (manages products only)
- Order Service (manages orders only)

❌ BAD:
- All-in-one Service (users, products, orders)
```

#### Database per Service
Each service owns its database.

```
User Service    →  User Database
Product Service →  Product Database
Order Service   →  Order Database
```

#### API First
Design APIs before implementation.

```yaml
openapi: 3.0.0
info:
  title: Product Service API
paths:
  /api/products:
    get:
      summary: Get all products
```

---

### 2. Communication Patterns

#### Synchronous (REST)
For immediate responses.

```java
// OrderService calls ProductService
@Autowired
private RestTemplate restTemplate;

public Product getProduct(Long productId) {
    return restTemplate.getForObject(
        "http://product-service/api/products/" + productId,
        Product.class
    );
}
```

#### Asynchronous (Messaging)
For eventual consistency.

```java
// Order created → Send message
rabbitTemplate.convertAndSend(
    "order.exchange",
    "order.created",
    orderEvent
);

// Product service listens
@RabbitListener(queues = "order.queue")
public void handleOrderCreated(OrderEvent event) {
    // Update inventory
}
```

---

### 3. Fault Tolerance

#### Circuit Breaker

```java
@CircuitBreaker(name = "productService", fallbackMethod = "getDefaultProduct")
public Product getProduct(Long id) {
    return restTemplate.getForObject(
        "http://product-service/api/products/" + id,
        Product.class
    );
}

public Product getDefaultProduct(Long id, Exception ex) {
    return new Product(id, "Default Product", BigDecimal.ZERO);
}
```

#### Retry Pattern

```java
@Retry(name = "productService", fallbackMethod = "getDefaultProduct")
public Product getProduct(Long id) {
    return productServiceClient.getProductById(id);
}
```

#### Timeout

```java
@TimeLimiter(name = "productService")
public CompletableFuture<Product> getProductAsync(Long id) {
    return CompletableFuture.supplyAsync(() -> 
        productServiceClient.getProductById(id)
    );
}
```

---

### 4. Security

#### API Gateway Authentication

```java
@Configuration
public class SecurityConfig {
    
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(
            ServerHttpSecurity http) {
        return http
            .authorizeExchange()
                .pathMatchers("/api/public/**").permitAll()
                .anyExchange().authenticated()
            .and()
            .oauth2ResourceServer()
                .jwt()
            .and().build();
    }
}
```

---

### 5. Observability

#### Distributed Tracing (Sleuth + Zipkin)

```
Request Flow with Trace ID:

Client → [trace-id: abc123]
    ↓
API Gateway [trace-id: abc123]
    ↓
Order Service [trace-id: abc123, span-id: 001]
    ↓
Product Service [trace-id: abc123, span-id: 002]
```

#### Centralized Logging (ELK Stack)

```
All Services → Logstash → Elasticsearch → Kibana
```

#### Metrics (Prometheus + Grafana)

```properties
# application.properties
management.endpoints.web.exposure.include=*
management.metrics.export.prometheus.enabled=true
```

---

## 🎯 Common Challenges & Solutions

### 1. Data Consistency

**Challenge**: Maintaining consistency across services

**Solutions**:
- **Saga Pattern**: Coordinate transactions across services
- **Event Sourcing**: Store state changes as events
- **CQRS**: Separate read and write models

### 2. Service Discovery

**Challenge**: Services need to find each other

**Solution**: Use Eureka/Consul for service registry

### 3. Configuration Management

**Challenge**: Managing config across services

**Solution**: Spring Cloud Config Server

```
Config Server (8888)
    ↓
Git Repository (configurations)
    ↓
All Services read config from Config Server
```

### 4. Network Latency

**Challenge**: Multiple network calls slow down requests

**Solutions**:
- Caching
- Asynchronous communication
- API composition at gateway

---

## 📊 Monitoring Dashboard

### Eureka Dashboard
```
http://localhost:8761
```
Shows all registered services and their status.

### Spring Boot Admin
```
http://localhost:9090
```
Monitors all microservices health, metrics, logs.

---

## 🎓 Interview Questions

### Q1: What are the main differences between monolith and microservices?

**A:**
- **Monolith**: Single deployment, shared database, tight coupling
- **Microservices**: Independent deployment, database per service, loose coupling

### Q2: What is the purpose of an API Gateway?

**A:** 
- Single entry point for clients
- Cross-cutting concerns (auth, rate limiting, logging)
- Request routing and composition
- Protocol translation

### Q3: How do you handle distributed transactions?

**A:**
- **Saga Pattern**: Choreography or orchestration
- **Two-Phase Commit** (2PC): For strong consistency (but avoid in microservices)
- **Event Sourcing**: Track all state changes

### Q4: What is circuit breaker pattern?

**A:** 
Prevents cascading failures by:
- CLOSED: Normal operation
- OPEN: Fail fast when threshold reached
- HALF_OPEN: Test if service recovered

---

## 📚 Technology Stack

- **Spring Boot 3.2+**: Microservice framework
- **Spring Cloud**: Microservices patterns
  - Eureka: Service discovery
  - Gateway: API gateway
  - Config: Configuration management
  - Sleuth: Distributed tracing
- **Resilience4j**: Fault tolerance
- **Kafka/RabbitMQ**: Async messaging
- **Docker**: Containerization
- **Kubernetes**: Orchestration

---

## ⏭️ Next Steps

1. Complete the e-commerce demo
2. Move to **[Module 05: Spring Cloud](../05-spring-cloud/)**
3. Learn **[Module 06: Message Driven Architecture](../06-messaging/)**

---

_Build scalable distributed systems! 🚀_
