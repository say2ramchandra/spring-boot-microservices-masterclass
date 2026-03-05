# Capstone Project - Implementation Guide

> **Update (Feb 2026):** All six business services now have implemented code in `services/` and compile successfully. This guide remains as the reference blueprint and extension handbook.

## ✅ What Has Been Created

### 1. Core Documentation ✅ COMPLETE
- **README.md**: Complete project overview, architecture diagram, quick start guide
- **ARCHITECTURE.md**: Detailed technical architecture, design decisions, communication patterns
- **SETUP.md**: Step-by-step setup instructions for Docker, Kubernetes, and local development
- **This file**: Implementation guide for building all services

### 2. Infrastructure Services ✅ COMPLETE

All three infrastructure services are **fully implemented and ready to run**:

#### Eureka Server (Port 8761)
**Location**: `infrastructure/eureka-server/`

**Files Created**:
- ✅ `pom.xml` - Maven dependencies
- ✅ `EurekaServerApplication.java` - Main application
- ✅ `application.yml` - Configuration

**Status**: Fully working, can be started with `mvn spring-boot:run`

#### Config Server (Port 8888)
**Location**: `infrastructure/config-server/`

**Files Created**:
- ✅ `pom.xml` - Maven dependencies  
- ✅ `ConfigServerApplication.java` - Main application
- ✅ `application.yml` - Git repository configuration

**Status**: Fully working, requires config-repo to be initialized

#### API Gateway (Port 8080)
**Location**: `infrastructure/api-gateway/`

**Files Created**:
- ✅ `pom.xml` - Maven dependencies with JWT, Circuit Breaker, Redis
- ✅ `ApiGatewayApplication.java` - Main application
- ✅ `GatewayConfig.java` - Route configuration for all services
- ✅ `AuthenticationFilter.java` - JWT validation filter
- ✅ `FallbackController.java` - Circuit breaker fallbacks
- ✅ `application.yml` - Gateway routes, circuit breaker, CORS

**Status**: Production-ready with authentication, routing, circuit breakers

---

## 🚀 Quick Start (What's Already Working)

### Start Infrastructure Services

```bash
# Terminal 1 - Eureka
cd infrastructure/eureka-server
mvn spring-boot:run
# Access: http://localhost:8761

# Terminal 2 - Config Server  
cd infrastructure/config-server
# First initialize config repo
cd ../../config-repo
git init && git add . && git commit -m "Initial config"
cd ../infrastructure/config-server
mvn spring-boot:run

# Terminal 3 - API Gateway
cd infrastructure/api-gateway
mvn spring-boot:run
# Access: http://localhost:8080
```

All three services will start successfully and Eureka/Config/Gateway will be operational!

---

## 📋 Reference: Service Implementation Pattern

### Service Implementation Pattern

Each microservice follows this structure:

```
services/<service-name>/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/com/ecommerce/<service>/
│   │   │   ├── <Service>Application.java
│   │   │   ├── entity/
│   │   │   │   └── <Entity>.java
│   │   │   ├── repository/
│   │   │   │   └── <Entity>Repository.java
│   │   │   ├── dto/
│   │   │   │   ├── <Entity>Request.java
│   │   │   │   └── <Entity>Response.java
│   │   │   ├── service/
│   │   │   │   ├── <Entity>Service.java
│   │   │   │   └── <Entity>ServiceImpl.java
│   │   │   ├── controller/
│   │   │   │   └── <Entity>Controller.java
│   │   │   ├── event/
│   │   │   │   ├── <Event>Event.java
│   │   │   │   └── <Event>Publisher.java
│   │   │   ├── exception/
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   └── <Custom>Exception.java
│   │   │   └── config/
│   │   │       └── KafkaConfig.java
│   │   └── resources/
│   │       ├── application.yml
│   │       └── bootstrap.yml
│   └── test/
│       └── java/com/ecommerce/<service>/
│           ├── <Entity>ControllerTest.java
│           └── <Entity>ServiceTest.java
└── Dockerfile
```

---

## 🛠️ Service Templates

### Template 1: Product Service (Partially Created)

**Status**: POM created ✅, Application class created ✅

**Remaining Files Needed**:

1. **Entity** (`entity/Product.java`):
```java
@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    private String name;
    
    private String description;
    
    @NotNull
    @DecimalMin("0.00")
    private BigDecimal price;
    
    private String sku;
    private String category;
    private String imageUrl;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
```

2. **Repository** (`repository/ProductRepository.java`):
```java
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(String category);
    List<Product> findByNameContainingIgnoreCase(String keyword);
    Optional<Product> findBySku(String sku);
}
```

3. **DTO** (`dto/ProductRequest.java`, `dto/ProductResponse.java`):
```java
@Data
public class ProductRequest {
    @NotBlank
    private String name;
    private String description;
    @NotNull
    @DecimalMin("0.00")
    private BigDecimal price;
    @NotBlank
    private String sku;
    private String category;
    private String imageUrl;
}

@Data
@Builder
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String sku;
    private String category;
    private String imageUrl;
    private LocalDateTime createdAt;
}
```

4. **Service** (`service/ProductService.java`):
```java
@Service
@Slf4j
@Transactional
public class ProductService {
    private final ProductRepository repository;
    private final ProductEventPublisher eventPublisher;
    
    public ProductResponse createProduct(ProductRequest request) {
        Product product = new Product();
        // Map fields
        product = repository.save(product);
        eventPublisher.publishProductCreated(product);
        return mapToResponse(product);
    }
    
    public List<ProductResponse> getAllProducts() {
        return repository.findAll().stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    public ProductResponse getProductById(Long id) {
        return repository.findById(id)
            .map(this::mapToResponse)
            .orElseThrow(() -> new ProductNotFoundException(id));
    }
    
    // Additional methods...
}
```

5. **Controller** (`controller/ProductController.java`):
```java
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {
    private final ProductService productService;
    
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }
    
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(productService.createProduct(request));
    }
    
    // More endpoints...
}
```

6. **Event Publisher** (`event/ProductEventPublisher.java`):
```java
@Component
@RequiredArgsConstructor
@Slf4j
public class ProductEventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    private static final String TOPIC = "product.events";
    
    public void publishProductCreated(Product product) {
        ProductCreatedEvent event = ProductCreatedEvent.builder()
            .productId(product.getId())
            .name(product.getName())
            .price(product.getPrice())
            .timestamp(LocalDateTime.now())
            .build();
        
        kafkaTemplate.send(TOPIC, event);
        log.info("Published ProductCreatedEvent for product: {}", product.getId());
    }
}
```

7. **Configuration** (`application.yml`):
```yaml
server:
  port: 8081

spring:
  application:
    name: product-service
  datasource:
    url: jdbc:postgresql://localhost:5432/product_db
    username: postgres
    password: postgres
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
  kafka:
    bootstrap-servers: localhost:9092
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

---

### Template 2: User Service

**Purpose**: Authentication, User Management, JWT Generation

**Key Components**:
- Entity: `User` with email, password (BCrypt), roles
- JWT Token generation and validation
- Login/Register endpoints
- Password encryption
- Role-based access control

**Special Dependencies**:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.3</version>
</dependency>
```

**Key Classes**:
- `JwtTokenProvider` - Generate/validate tokens
- `SecurityConfig` - Spring Security configuration
- `AuthController` - Login/register endpoints
- `UserService` - User management logic

---

### Template 3: Order Service

**Purpose**: Order Processing, Saga Orchestration

**Key Components**:
- Entity: `Order`, `OrderItem`
- Saga pattern implementation
- Integration with Inventory & Payment via Kafka
- Order status tracking

**Key Events**:
- Publishes: `order.created`, `order.confirmed`, `order.cancelled`
- Consumes: `inventory.reserved`, `payment.completed`, `payment.failed`

**Saga Flow**:
```java
@Service
public class OrderSagaOrchestrator {
    public void startOrderSaga(Order order) {
        // 1. Create order (PENDING)
        order.setStatus(OrderStatus.PENDING);
        orderRepository.save(order);
        
        // 2. Publish order.created event
        eventPublisher.publishOrderCreated(order);
        
        // 3. Wait for inventory.reserved
        // 4. Wait for payment.completed
        // 5. Update order to CONFIRMED
        // 6. Publish order.confirmed event
    }
    
    @KafkaListener(topics = "inventory.reserved")
    public void onInventoryReserved(InventoryReservedEvent event) {
        // Continue saga...
    }
    
    @KafkaListener(topics = "payment.failed")
    public void onPaymentFailed(PaymentFailedEvent event) {
        // Compensate: release inventory, cancel order
    }
}
```

---

### Template 4: Inventory Service

**Purpose**: Stock Management, Reservation/Release

**Key Components**:
- Entity: `Inventory` (productId, available_quantity, reserved_quantity)
- Reserve/Release operations
- Low stock alerts

**Key Operations**:
```java
@Service
@Transactional
public class InventoryService {
    public void reserveInventory(Long productId, Integer quantity) {
        Inventory inventory = findByProductId(productId);
        if (inventory.getAvailableQuantity() >= quantity) {
            inventory.setAvailableQuantity(inventory.getAvailableQuantity() - quantity);
            inventory.setReservedQuantity(inventory.getReservedQuantity() + quantity);
            repository.save(inventory);
            eventPublisher.publishInventoryReserved(productId, quantity);
        } else {
            eventPublisher.publishInventoryReservationFailed(productId);
        }
    }
}
```

---

### Template 5: Payment Service

**Purpose**: Payment Processing

**Key Components**:
- Entity: `Payment` with status tracking
- Payment gateway integration (mock for demo)
- Refund handling

**Payment Flow**:
```java
@Service
public class PaymentService {
    public Payment processPayment(PaymentRequest request) {
        Payment payment = new Payment();
        payment.setStatus(PaymentStatus.PROCESSING);
        payment = repository.save(payment);
        
        try {
            // Call payment gateway (Stripe/PayPal)
            boolean success = paymentGateway.charge(request);
            
            if (success) {
                payment.setStatus(PaymentStatus.COMPLETED);
                eventPublisher.publishPaymentCompleted(payment);
            } else {
                payment.setStatus(PaymentStatus.FAILED);
                eventPublisher.publishPaymentFailed(payment);
            }
        } catch (Exception e) {
            payment.setStatus(PaymentStatus.FAILED);
            eventPublisher.publishPaymentFailed(payment);
        }
        
        return repository.save(payment);
    }
}
```

---

### Template 6: Notification Service

**Purpose**: Email/SMS Notifications

**Key Components**:
- MongoDB for flexible notification storage
- Email service (JavaMailSender)
- Template engine (Thymeleaf)
- Event consumers for all notification triggers

**Dependencies**:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>
```

---

## 🐳 Docker & Kubernetes Setup

### Docker Compose Structure

Create these files in `docker/`:

1. **docker-compose.yml** - Full stack
2. **docker-compose-infra.yml** - Infrastructure only
3. **docker-compose-services.yml** - Business services only
4. **docker-compose-monitoring.yml** - Prometheus, Grafana, Zipkin

**Sample docker-compose.yml**:
```yaml
version: '3.8'

services:
  postgres:
    image: postgres:15-alpine
    environment:
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"
    volumes:
      - postgres-data:/var/lib/postgresql/data
      - ./init-databases.sql:/docker-entrypoint-initdb.d/init.sql

  mongodb:
    image: mongo:7
    ports:
      - "27017:27017"
    environment:
      MONGO_INITDB_ROOT_USERNAME: admin
      MONGO_INITDB_ROOT_PASSWORD: admin

  kafka:
    image: confluentinc/cp-kafka:7.5.0
    ports:
      - "9092:9092"
    environment:
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181

  zookeeper:
    image: confluentinc/cp-zookeeper:7.5.0
    ports:
      - "2181:2181"

  eureka-server:
    build: ../infrastructure/eureka-server
    ports:
      - "8761:8761"
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8761/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3

  # Add all other services...
```

### Kubernetes Manifests

Create in `kubernetes/deployments/`:

**eureka-server-deployment.yml**:
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: eureka-server
  namespace: ecommerce
spec:
  replicas: 2
  selector:
    matchLabels:
      app: eureka-server
  template:
    metadata:
      labels:
        app: eureka-server
    spec:
      containers:
      - name: eureka-server
        image: ecommerce/eureka-server:1.0.0
        ports:
        - containerPort: 8761
        resources:
          requests:
            memory: "512Mi"
            cpu: "500m"
          limits:
            memory: "1Gi"
            cpu: "1000m"
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8761
          initialDelaySeconds: 60
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health
            port: 8761
          initialDelaySeconds: 30
          periodSeconds: 5
---
apiVersion: v1
kind: Service
metadata:
  name: eureka-server
  namespace: ecommerce
spec:
  selector:
    app: eureka-server
  ports:
  - port: 8761
    targetPort: 8761
  type: LoadBalancer
```

---

## 🧪 Testing Strategy

### Unit Tests
```java
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock
    private ProductRepository repository;
    
    @InjectMocks
    private ProductService service;
    
    @Test
    void shouldCreateProduct() {
        // Given
        ProductRequest request = new ProductRequest();
        Product product = new Product();
        when(repository.save(any())).thenReturn(product);
        
        // When
        ProductResponse response = service.createProduct(request);
        
        // Then
        assertNotNull(response);
        verify(repository).save(any(Product.class));
    }
}
```

### Integration Tests
```java
@SpringBootTest
@AutoConfigureTestDatabase
@Testcontainers
class ProductIntegrationTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");
    
    @Autowired
    private ProductRepository repository;
    
    @Test
    void shouldSaveProduct() {
        Product product = new Product();
        product.setName("Test Product");
        
        Product saved = repository.save(product);
        
        assertNotNull(saved.getId());
    }
}
```

---

## 🚀 Build & Run Instructions

### Build All Services
```bash
# From project root
cd 12-capstone-project

# Build infrastructure
cd infrastructure/eureka-server && mvn clean install && cd ../..
cd infrastructure/config-server && mvn clean install && cd ../..
cd infrastructure/api-gateway && mvn clean install && cd ../..

# Build services
cd services/product-service && mvn clean install && cd ../..
# Repeat for other services...
```

### Run with Docker Compose
```bash
cd docker
docker-compose up -d
docker-compose logs -f
```

### Deploy to Kubernetes
```bash
cd kubernetes
kubectl create namespace ecommerce
kubectl apply -f configmaps/
kubectl apply -f secrets/
kubectl apply -f deployments/
kubectl apply -f services/
kubectl apply -f ingress/
```

---

## 📊 Project Completion Checklist

### Infrastructure ✅
- [x] Eureka Server
- [x] Config Server  
- [x] API Gateway

### Services (To Be Completed)
- [~] Product Service (80% - needs full implementation)
- [ ] User Service
- [ ] Order Service
- [ ] Inventory Service
- [ ] Payment Service
- [ ] Notification Service

### DevOps (To Be Created)
- [ ] Docker Compose files
- [ ] Kubernetes manifests
- [ ] CI/CD pipelines
- [ ] Monitoring stack (Prometheus, Grafana)

### Documentation ✅
- [x] README.md
- [x] ARCHITECTURE.md
- [x] SETUP.md
- [x] Implementation Guide (this file)

---

## 💡 Implementation Tips

1. **Start Small**: Get Product  Service fully working first
2. **Test Incrementally**: Test each service with Postman before moving to next
3. **Use Eureka Dashboard**: Verify service registration at http://localhost:8761
4. **Check Logs**: Use correlation IDs to trace requests
5. **Database First**: Ensure PostgreSQL/MongoDB are running
6. **Kafka Topics**: Create topics manually if needed
7. **Port Conflicts**: Ensure ports 808x, 5432, 27017, 9092 are available

---

## 🎓 Learning Outcomes

By completing this capstone, you will have:

✅ Built a production-ready microservices system  
✅ Implemented service discovery and API Gateway  
✅ Created event-driven communication with Kafka  
✅ Applied Saga pattern for distributed transactions  
✅ Secured services with JWT authentication  
✅ Containerized with Docker  
✅ Deployed to Kubernetes  
✅ Added comprehensive monitoring  
✅ Written extensive tests  

---

**Next Steps**: Use the templates above to complete the remaining services. Follow the same pattern for each, and you'll have a fully functional e-commerce platform!

**Last Updated**: February 2026
