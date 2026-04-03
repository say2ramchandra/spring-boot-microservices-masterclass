# E-Commerce Microservices - System Architecture

> **Detailed technical architecture and design decisions**

## 📐 Architecture Overview

This document describes the complete architecture of the e-commerce microservices system, including service boundaries, communication patterns, data management, and deployment topology.

---

## 🎯 Architectural Principles

### 1. Domain-Driven Design (DDD)
- Services organized around business capabilities
- Bounded contexts clearly defined
- Ubiquitous language within each context

### 2. Microservices Patterns
- **Database per Service**: Each service owns its data
- **API Gateway**: Single entry point for clients
- **Service Discovery**: Dynamic service registration
- **Circuit Breaker**: Fault tolerance and resilience
- **Event-Driven**: Asynchronous communication via events

### 3. 12-Factor App Methodology
- Codebase tracked in version control
- Dependencies explicitly declared
- Configuration externalized
- Backing services as attached resources
- Stateless processes
- Disposability (fast startup/shutdown)
- Logs as event streams

---

## 🏛️ System Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              CLIENT APPLICATIONS                             │
│                    (Web Browser, Mobile App, External APIs)                 │
└────────────────────────────────────┬─────────────────────────────────────────┘
                                     │
                                     │ HTTPS
                                     ▼
                    ┌────────────────────────────────┐
                    │       API Gateway (8080)       │
                    │   - Routing                    │
                    │   - Authentication             │
                    │   - Rate Limiting              │
                    │   - Load Balancing             │
                    └───────────────┬────────────────┘
                                    │
                    ┌───────────────┼────────────────┐
                    │               │                │
         ┌──────────▼─────┐  ┌──────▼──────┐  ┌────▼──────┐
         │ Eureka Server  │  │   Config    │  │  Zipkin   │
         │   (8761)       │  │  Server     │  │  (9411)   │
         │                │  │  (8888)     │  │           │
         └────────────────┘  └─────────────┘  └───────────┘
                                    │
         ┌──────────────────────────┼──────────────────────────┐
         │                          │                          │
    ┌────▼──────┐           ┌───────▼────┐           ┌────────▼────┐
    │  Product  │           │    User    │           │    Order    │
    │  Service  │           │  Service   │           │   Service   │
    │  (8081)   │           │   (8082)   │           │   (8083)    │
    └────┬──────┘           └─────┬──────┘           └──────┬──────┘
         │                        │                          │
    ┌────▼──────┐           ┌─────▼──────┐           ┌──────▼──────┐
    │Inventory  │           │  Payment   │           │Notification │
    │ Service   │           │  Service   │           │  Service    │
    │ (8084)    │           │  (8085)    │           │  (8086)     │
    └────┬──────┘           └─────┬──────┘           └──────┬──────┘
         │                        │                          │
         └────────────────────────┼──────────────────────────┘
                                  │
                    ┌─────────────▼──────────────┐
                    │   Apache Kafka Cluster     │
                    │   Topics:                  │
                    │   - order.created          │
                    │   - order.confirmed        │
                    │   - payment.processed      │
                    │   - inventory.reserved     │
                    │   - notification.sent      │
                    └────────────────────────────┘
                                  │
         ┌────────────────────────┼────────────────────────┐
         │                        │                        │
    ┌────▼──────┐           ┌─────▼──────┐         ┌──────▼──────┐
    │PostgreSQL │           │PostgreSQL  │         │  MongoDB    │
    │ (Product) │           │  (User)    │         │(Notification)│
    │ (Order)   │           │ (Payment)  │         │             │
    │(Inventory)│           │            │         │             │
    └───────────┘           └────────────┘         └─────────────┘
```

---

## 🔧 Service Architecture

### Infrastructure Services

#### 1. Eureka Server (Service Discovery)
**Purpose**: Service registration and discovery

**Key Features**:
- Self-preservation mode for network partitions
- Service health checks
- Client-side load balancing
- Dynamic service registration

**Technology Stack**:
- Spring Cloud Netflix Eureka Server

**Configuration**:
```yaml
server:
  port: 8761

eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
  server:
    enable-self-preservation: true
```

#### 2. Config Server (Centralized Configuration)
**Purpose**: External configuration management

**Key Features**:
- Git-backed configuration
- Environment-specific profiles
- Encryption/decryption of secrets
- Hot reload with @RefreshScope

**Technology Stack**:
- Spring Cloud Config Server

**Configuration Sources**:
```
config-repo/
├── application.yml              # Common config
├── application-dev.yml          # Development
├── application-prod.yml         # Production
├── product-service.yml          # Service-specific
└── [service-name].yml
```

#### 3. API Gateway
**Purpose**: Single entry point for all client requests

**Key Responsibilities**:
- Request routing to appropriate services
- Authentication and authorization
- Rate limiting
- Request/response transformation
- Circuit breaker integration
- CORS handling

**Technology Stack**:
- Spring Cloud Gateway
- Spring Security
- Resilience4j

**Route Configuration**:
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: product-service
          uri: lb://PRODUCT-SERVICE
          predicates:
            - Path=/api/products/**
          filters:
            - RewritePath=/api/products(?<segment>/?.*), /products$\{segment}
            - name: CircuitBreaker
              args:
                name: productService
                fallbackUri: forward:/fallback/products
```

---

### Business Services

#### 1. Product Service (Port 8081)
**Domain**: Product Catalog Management

**Responsibilities**:
- Manage product information (CRUD)
- Product search and filtering
- Category management
- Product availability check
- Product reviews and ratings

**Database**: PostgreSQL
```sql
Tables:
- products (id, name, description, price, sku, category_id)
- categories (id, name, description)
- product_images (id, product_id, image_url)
- product_reviews (id, product_id, user_id, rating, comment)
```

**API Endpoints**:
```
GET    /products              # List all products (paginated)
GET    /products/{id}         # Get product details
POST   /products              # Create product (admin)
PUT    /products/{id}         # Update product (admin)
DELETE /products/{id}         # Delete product (admin)
GET    /products/search       # Search products
GET    /products/category/{id} # Products by category
```

**Events Published**:
- `product.created`
- `product.updated`
- `product.deleted`

#### 2. User Service (Port 8082)
**Domain**: User Management & Authentication

**Responsibilities**:
- User registration and profile management
- Authentication (login/logout)
- JWT token generation and validation
- Role-based access control (RBAC)
- Password reset functionality

**Database**: PostgreSQL
```sql
Tables:
- users (id, email, password_hash, first_name, last_name, role)
- roles (id, name)
- user_roles (user_id, role_id)
- refresh_tokens (id, user_id, token, expiry_date)
```

**API Endpoints**:
```
POST   /users/register        # Register new user
POST   /users/login           # Login and get JWT
POST   /users/refresh-token   # Refresh JWT
POST   /users/logout          # Logout
GET    /users/me              # Get current user
PUT    /users/me              # Update profile
POST   /users/change-password # Change password
```

**Security**:
- Password encryption with BCrypt
- JWT with RS256 algorithm
- Token expiration: 1 hour (access), 7 days (refresh)

#### 3. Order Service (Port 8083)
**Domain**: Order Management

**Responsibilities**:
- Create and manage orders
- Order status tracking
- Order history
- Saga orchestration for order processing
- Integration with inventory and payment

**Database**: PostgreSQL
```sql
Tables:
- orders (id, user_id, status, total_amount, created_at)
- order_items (id, order_id, product_id, quantity, price)
- order_status_history (id, order_id, status, timestamp)
```

**Order Status Flow**:
```
PENDING → INVENTORY_RESERVED → PAYMENT_PROCESSING → 
PAYMENT_COMPLETED → CONFIRMED → SHIPPED → DELIVERED

Failure paths:
PENDING → CANCELLED (if inventory fails)
INVENTORY_RESERVED → CANCELLED (if payment fails)
```

**API Endpoints**:
```
POST   /orders                # Create order
GET    /orders                # Get user orders
GET    /orders/{id}           # Get order details
PUT    /orders/{id}/cancel    # Cancel order
GET    /orders/{id}/status    # Track order status
```

**Events Published**:
- `order.created`
- `order.confirmed`
- `order.cancelled`
- `order.shipped`
- `order.delivered`

**Events Consumed**:
- `inventory.reserved`
- `inventory.reservation.failed`
- `payment.completed`
- `payment.failed`

#### 4. Inventory Service (Port 8084)
**Domain**: Stock Management

**Responsibilities**:
- Track product inventory levels
- Reserve inventory for orders
- Release inventory on order cancellation
- Low stock alerts
- Inventory replenishment

**Database**: PostgreSQL
```sql
Tables:
- inventory (product_id, available_quantity, reserved_quantity)
- inventory_transactions (id, product_id, transaction_type, quantity, timestamp)
- reservations (id, order_id, product_id, quantity, expiry_time)
```

**API Endpoints**:
```
GET    /inventory/{productId}        # Check stock
POST   /inventory/reserve            # Reserve stock
POST   /inventory/release            # Release reservation
PUT    /inventory/{productId}/adjust # Adjust inventory (admin)
```

**Events Published**:
- `inventory.reserved`
- `inventory.reservation.failed`
- `inventory.released`
- `inventory.low.stock`

**Events Consumed**:
- `order.created`
- `order.cancelled`

#### 5. Payment Service (Port 8085)
**Domain**: Payment Processing

**Responsibilities**:
- Process payments via payment gateways
- Payment status tracking
- Refund processing
- Payment method management
- Integration with Stripe/PayPal

**Database**: PostgreSQL
```sql
Tables:
- payments (id, order_id, amount, status, payment_method, transaction_id)
- payment_methods (id, user_id, type, last_four, expiry)
- refunds (id, payment_id, amount, reason, status)
```

**Payment Status**:
```
INITIATED → PROCESSING → COMPLETED
                       → FAILED → REFUNDED
```

**API Endpoints**:
```
POST   /payments              # Process payment
GET    /payments/{id}         # Get payment status
POST   /payments/{id}/refund  # Refund payment
GET    /payments/methods      # Get user payment methods
POST   /payments/methods      # Add payment method
```

**Events Published**:
- `payment.initiated`
- `payment.completed`
- `payment.failed`
- `payment.refunded`

**Events Consumed**:
- `inventory.reserved`

#### 6. Notification Service (Port 8086)
**Domain**: Notifications

**Responsibilities**:
- Send email notifications
- Send SMS notifications (optional)
- Push notifications (optional)
- Notification templates
- Notification history

**Database**: MongoDB (document-based for flexibility)
```javascript
Collections:
- notifications {
    _id, userId, type, channel, subject, 
    body, status, sentAt, metadata
  }
- templates {
    _id, name, channel, subject,
    bodyTemplate, variables
  }
```

**Notification Types**:
- Order confirmation
- Payment receipt
- Shipping updates
- Order delivered
- Password reset
- Welcome email

**API Endpoints**:
```
GET    /notifications         # Get user notifications
POST   /notifications/send    # Send notification (internal)
PUT    /notifications/{id}/read # Mark as read
```

**Events Consumed**:
- `order.confirmed`
- `payment.completed`
- `order.shipped`
- `order.delivered`
- `user.registered`

---

## 🔄 Communication Patterns

### Synchronous Communication (REST via OpenFeign)

**When to Use**:
- Real-time data validation
- Immediate response required
- Read operations

**Implementation**:
```java
@FeignClient(name = "product-service")
public interface ProductClient {
    @GetMapping("/products/{id}")
    ProductDTO getProduct(@PathVariable Long id);
}
```

**Circuit Breaker Configuration**:
```java
@CircuitBreaker(name = "productService", fallbackMethod = "getProductFallback")
public ProductDTO getProduct(Long id) {
    return productClient.getProduct(id);
}
```

### Asynchronous Communication (Events via Kafka)

**When to Use**:
- Fire-and-forget operations
- Event notification
- Saga choreography
- System decoupling

**Event Schema Example**:
```java
@Data
public class OrderCreatedEvent {
    private String orderId;
    private Long userId;
    private List<OrderItem> items;
    private BigDecimal totalAmount;
    private LocalDateTime timestamp;
    private String correlationId;
}
```

**Kafka Topics**:
```yaml
Topics:
- order.created          # Published by Order Service
- inventory.reserved     # Published by Inventory Service
- payment.completed      # Published by Payment Service
- notification.sent      # Published by Notification Service
- order.confirmed        # Published by Order Service
```

---

## 🔐 Security Architecture

### Authentication Flow

```
1. User sends credentials to API Gateway
2. Gateway forwards to User Service
3. User Service validates credentials
4. User Service generates JWT token
5. JWT returned to client via Gateway
6. Client includes JWT in Authorization header
7. Gateway validates JWT for subsequent requests
8. Gateway forwards request with user context
```

### JWT Structure

```json
{
  "header": {
    "alg": "RS256",
    "typ": "JWT"
  },
  "payload": {
    "sub": "user@example.com",
    "userId": "123",
    "roles": ["USER"],
    "iat": 1234567890,
    "exp": 1234571490
  }
}
```

### Service-to-Service Authentication

**Option 1: Service Accounts**
- Each service has its own credentials
- Services authenticate with User Service
- Short-lived tokens

**Option 2: Mutual TLS**
- Certificate-based authentication
- Used in production Kubernetes
- No token validation overhead

---

## 📊 Data Management

### Database Per Service Pattern

**Benefits**:
- Service independence
- Technology flexibility
- Scalability
- Fault isolation

**Challenges**:
- Data consistency (handled by Saga)
- Joins across services (handled by API Composition)
- Data duplication (acceptable trade-off)

### Data Consistency - Saga Pattern

**Order Processing Saga** (Choreography):

```
┌──────────────┐    order.created    ┌──────────────┐
│Order Service │ ──────────────────→ │Inventory Svc │
└──────────────┘                     └──────────────┘
                                           │
                                           │ inventory.reserved
                                           ▼
┌──────────────┐    payment.completed┌──────────────┐
│Payment Svc   │ ←────────────────── │  Kafka       │
└──────────────┘                     └──────────────┘
      │
      │ payment.completed
      ▼
┌──────────────┐
│Order Service │ → Update Status: CONFIRMED
└──────────────┘
```

**Compensation Logic** (Rollback):

If payment fails:
1. Payment Service publishes `payment.failed`
2. Inventory Service listens and releases reservation
3. Order Service updates status to `CANCELLED`
4. Notification Service sends cancellation email

---

## 📈 Scalability & Performance

### Horizontal Scaling

**Stateless Services**: All services are stateless
- Enable horizontal scaling via Kubernetes
- No session affinity required
- Scale based on CPU/memory/request metrics

**Kubernetes HPA Configuration**:
```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: product-service-hpa
spec:
  minReplicas: 2
  maxReplicas: 10
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 70
```

### Caching Strategy

**Application-Level Cache**:
```java
@Cacheable(value = "products", key = "#id")
public Product getProduct(Long id) {
    return productRepository.findById(id);
}
```

**Redis Cache** (future enhancement):
- Product catalog (TTL: 1 hour)
- User sessions
- Rate limiting counters

### Database Optimization

**Indexes**:
```sql
CREATE INDEX idx_products_category ON products(category_id);
CREATE INDEX idx_orders_user ON orders(user_id);
CREATE INDEX idx_orders_status ON orders(status);
```

**Read Replicas**: For read-heavy services (Product Service)

**Connection Pooling**:
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      connection-timeout: 30000
```

---

## 🔍 Observability Architecture

### Three Pillars

#### 1. Logging (ELK Stack Ready)
- Structured JSON logging
- Correlation IDs across services
- Log levels per service
- Log aggregation with Logstash (optional)

**Log Format**:
```json
{
  "timestamp": "2026-02-17T10:30:00Z",
  "level": "INFO",
  "service": "order-service",
  "traceId": "abc123",
  "spanId": "def456",
  "message": "Order created",
  "userId": "123",
  "orderId": "ORDER-001"
}
```

#### 2. Metrics (Prometheus + Grafana)
- JVM metrics (heap, threads, GC)
- HTTP metrics (request rate, latency, errors)
- Business metrics (orders/minute, revenue)
- Database connection pool metrics
- Kafka consumer lag

**Custom Metrics**:
```java
@Timed(value = "order.creation.time")
public Order createOrder(OrderRequest request) {
    orderCounter.increment();
    return orderRepository.save(order);
}
```

#### 3. Distributed Tracing (Zipkin)
- Request flow across services
- Latency breakdown per service
- Dependency graph
- Error tracking

**Trace Example**:
```
TraceId: abc123
├─ Gateway [100ms]
├─ Order Service [200ms]
│  ├─ Inventory Service [50ms] (via Feign)
│  └─ Payment Service [150ms] (via Feign)
└─ Notification Service [80ms] (via Kafka)
Total: 580ms
```

---

## 🚀 Deployment Architecture

### Local Development (Docker Compose)
```yaml
services:
  - eureka-server
  - config-server
  - api-gateway
  - product-service
  - user-service
  - order-service
  - inventory-service
  - payment-service
  - notification-service
  - postgresql
  - mongodb
  - kafka
  - zookeeper
  - zipkin
```

### Production (Kubernetes)
```
Namespace: ecommerce
├─ Infrastructure (2 replicas each)
│  ├─ eureka-server
│  ├─ config-server
│  └─ api-gateway (LoadBalancer)
├─ Services (3-10 replicas, auto-scaled)
│  ├─ product-service
│  ├─ user-service
│  ├─ order-service
│  ├─ inventory-service
│  ├─ payment-service
│  └─ notification-service
├─ Databases (StatefulSets)
│  ├─ postgresql (with persistent volumes)
│  └─ mongodb (with persistent volumes)
└─ Messaging
   └─ kafka (3 broker cluster)
```

---

## 🔄 CI/CD Pipeline

### Build Pipeline (GitHub Actions)

```yaml
Trigger: Push to main branch
Steps:
1. Checkout code
2. Setup Java 17
3. Run unit tests
4. Run integration tests
5. Code coverage check (80% threshold)
6. Build Docker images
7. Push to Docker Hub
8. Security scan (Trivy)
9. Deploy to Dev environment
```

### Deployment Pipeline

```yaml
Trigger: Manual approval
Steps:
1. Pull latest images
2. Apply Kubernetes manifests
3. Rolling update (zero downtime)
4. Health check validation
5. Smoke tests
6. Rollback on failure
```

---

## 📋 Design Decisions

### Why PostgreSQL for Most Services?
- ACID compliance for transactional data
- Complex queries support
- Mature ecosystem
- Strong consistency

### Why MongoDB for Notifications?
- Flexible schema for various notification types
- Document-based model fits notification data
- High write throughput

### Why Kafka Instead of RabbitMQ?
- Event streaming capability
- Event log retention
- High throughput
- Replay capability for debugging

### Why Spring Cloud Gateway?
- Reactive (non-blocking)
- Native Spring Boot integration
- Circuit breaker support
- Better performance than Zuul

### Why Docker Compose + Kubernetes?
- Docker Compose: Fast local development
- Kubernetes: Production-grade orchestration
- Same container images for both

---

## 🔮 Future Enhancements

1. **Service Mesh (Istio)**
   - Advanced traffic management
   - mTLS by default
   - Better observability

2. **Event Sourcing**
   - Complete audit trail
   - Time travel debugging
   - Event replay

3. **CQRS**
   - Separate read/write models
   - Optimized read queries
   - Scalability improvements

4. **GraphQL API**
   - Client-specified queries
   - Reduce over-fetching
   - Better for mobile apps

5. **Reactive Microservices**
   - WebFlux for non-blocking I/O
   - Better resource utilization
   - Higher throughput

---

## 📚 References

- **Microservices Patterns**: Chris Richardson
- **Building Microservices**: Sam Newman
- **Domain-Driven Design**: Eric Evans
- **Spring Microservices in Action**: John Carnell

---

**Last Updated**: February 2026  
**Version**: 1.0.0
