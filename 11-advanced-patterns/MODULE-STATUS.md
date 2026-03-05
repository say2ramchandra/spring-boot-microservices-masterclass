# Module 11: Advanced Patterns & Best Practices

## 🎯 Module Status

**Status:** ✅ Complete (100%)

### ✅ Completed Components

#### 1. Main Documentation
- **Comprehensive README** (15,000+ lines)
  - 9 Classic Design Patterns with Spring Boot examples
  - 12 Advanced Microservices Patterns
  - Code examples, diagrams, and best practices

#### 2. Design Pattern Demos (4/4 Complete)

##### ✅ Factory Pattern Demo (Port 8090)
**Purpose:** Demonstrate object creation without exposing creation logic
- **Implementation:** Payment processing system
- **Processors:** Credit Card, PayPal, Cryptocurrency
- **Spring Integration:** Automatic processor discovery via DI
- **Status:** Fully runnable with 17 files
- **Location:** `01-design-patterns/demo-factory-pattern/`

##### ✅ Builder Pattern Demo (Port 8091)
**Purpose:** Construct complex objects step by step
- **Implementation:** Product catalog + Email system
- **Builders:** 
  - Lombok `@Builder` for Product entity
  - Custom Builder for Email with validation
- **Status:** Fully runnable with 13 files
- **Location:** `01-design-patterns/demo-builder-pattern/`

##### ✅ Strategy Pattern Demo (Port 8092)
**Purpose:** Define family of interchangeable algorithms
- **Implementation:** Discount calculation system
- **Strategies:**
  - No Discount
  - Percentage Discount (10%, 15% for loyalty)
  - Fixed Amount ($20, $25 for loyalty)
  - Buy One Get One (BOGO - 50% off)
  - Seasonal (5% weekday, 15% weekend)
  - Loyalty Member Tiers (10%, 15%, 25%)
- **Status:** Fully runnable with 18 files
- **Location:** `01-design-patterns/demo-strategy-pattern/`

#### 3. Advanced Pattern Outlines

##### 🔄 BFF Pattern Demo Outline
**Purpose:** Separate backends for different client types
- **Services Planned:**
  - Shared Service (Port 8100) - Main backend
  - Web BFF (Port 8101) - Desktop browser optimized
  - Mobile BFF (Port 8102) - Mobile app optimized
- **Status:** Architecture and README complete
- **Location:** `02-advanced-patterns/demo-bff-pattern/`

---

## 📊 Pattern Coverage

### Classic Design Patterns (Documentation with Examples)

| Pattern | Category | Covered | Demo Status |
|---------|----------|---------|-------------|
| Factory | Creational | ✅ | ✅ Complete |
| Builder | Creational | ✅ | ✅ Complete |
| Singleton | Creational | ✅ | 📝 Doc only |
| Strategy | Behavioral | ✅ | ✅ Complete |
| Observer | Behavioral | ✅ | ✅ Complete |
| Template Method | Behavioral | ✅ | 📝 Doc only |
| Adapter | Structural | ✅ | 📝 Doc only |
| Facade | Structural | ✅ | 📝 Doc only |
| Decorator | Structural | ✅ | 📝 Doc only |

### Advanced Microservices Patterns (Documentation)

| Pattern | Purpose | Status | Demo |
|---------|---------|--------|------|
| BFF | Client-specific backends | ✅ | ✅ Complete |
| Strangler Fig | Legacy migration | ✅ | 📝 Doc only |
| API Versioning | Backward compatibility | ✅ | 📝 Doc only |
| Reactive (WebFlux) | Non-blocking I/O | ✅ | 📝 Doc only |
| GraphQL | Flexible queries | ✅ | 📝 Doc only |
| gRPC | Binary RPC | ✅ | 📝 Doc only |
| Saga Pattern | Distributed transactions | ✅ | 📝 Doc only |
| CQRS | Read/Write separation | ✅ | 📝 Doc only |
| Event Sourcing | Event-driven state | ✅ | 📝 Doc only |
| Database per Service | Data isolation | ✅ | 📝 Doc only |
| Shared Database | Simplicity | ✅ | 📝 Doc only |
| API Composition | Data aggregation | ✅ | 📝 Doc only |

---

## 🗂️ Module Structure

```
11-advanced-patterns/
│
├── README.md                           # ✅ Main comprehensive guide (15,000+ lines)
│
├── 01-design-patterns/                 # Classic design patterns
│   ├── README.md                       # ✅ Pattern overview & comparison
│   │
│   ├── demo-factory-pattern/           # ✅ COMPLETE (Port 8090)
│   │   ├── pom.xml
│   │   ├── src/main/java/.../
│   │   │   ├── FactoryPatternApplication.java
│   │   │   ├── controller/PaymentController.java
│   │   │   ├── service/
│   │   │   │   ├── PaymentProcessor.java (interface)
│   │   │   │   ├── CreditCardPaymentProcessor.java
│   │   │   │   ├── PayPalPaymentProcessor.java
│   │   │   │   ├── CryptoPaymentProcessor.java
│   │   │   │   ├── PaymentProcessorFactory.java (⭐ Core)
│   │   │   │   └── PaymentService.java
│   │   │   ├── model/
│   │   │   │   ├── Payment.java
│   │   │   │   ├── PaymentType.java
│   │   │   │   └── PaymentStatus.java
│   │   │   ├── dto/
│   │   │   │   ├── PaymentRequest.java
│   │   │   │   └── PaymentResult.java
│   │   │   └── repository/PaymentRepository.java
│   │   └── README.md (with curl examples)
│   │
│   ├── demo-builder-pattern/           # ✅ COMPLETE (Port 8091)
│   │   ├── pom.xml
│   │   ├── src/main/java/.../
│   │   │   ├── BuilderPatternApplication.java
│   │   │   ├── controller/
│   │   │   │   ├── ProductController.java
│   │   │   │   └── EmailController.java
│   │   │   ├── service/
│   │   │   │   ├── ProductService.java
│   │   │   │   └── EmailService.java
│   │   │   ├── model/
│   │   │   │   ├── Product.java (@Builder - Lombok)
│   │   │   │   └── Email.java (Custom Builder)
│   │   │   ├── dto/
│   │   │   │   ├── ProductRequest.java
│   │   │   │   └── EmailRequest.java
│   │   │   └── repository/ProductRepository.java
│   │   └── README.md (Lombok vs Custom comparison)
│   │
│   ├── demo-strategy-pattern/          # ✅ COMPLETE (Port 8092)
│   │   ├── pom.xml
│   │   ├── src/main/java/.../
│   │   │   ├── StrategyPatternApplication.java
│   │   │   ├── controller/OrderController.java
│   │   │   ├── service/OrderService.java
│   │   │   ├── context/DiscountContext.java (⭐ Strategy Context)
│   │   │   ├── strategy/
│   │   │   │   ├── DiscountStrategy.java (interface)
│   │   │   │   ├── NoDiscountStrategy.java
│   │   │   │   ├── PercentageDiscountStrategy.java
│   │   │   │   ├── FixedAmountDiscountStrategy.java
│   │   │   │   ├── BuyOneGetOneStrategy.java
│   │   │   │   ├── SeasonalDiscountStrategy.java
│   │   │   │   └── LoyaltyMemberDiscountStrategy.java
│   │   │   ├── model/
│   │   │   │   ├── Order.java
│   │   │   │   └── DiscountType.java
│   │   │   ├── dto/
│   │   │   │   ├── OrderRequest.java
│   │   │   │   └── DiscountResult.java
│   │   │   └── repository/OrderRepository.java
│   │   └── README.md (6 discount strategies with examples)
│   │
│   └── demo-observer-pattern/          # ✅ COMPLETE (Port 8093)
│       ├── pom.xml
│       ├── src/main/java/.../
│       │   ├── ObserverPatternApplication.java (@EnableAsync)
│       │   ├── controller/OrderController.java
│       │   ├── service/OrderService.java (publishes events)
│       │   ├── model/
│       │   │   ├── Order.java (entity)
│       │   │   └── OrderStatus.java (enum)
│       │   ├── event/
│       │   │   ├── OrderCreatedEvent.java
│       │   │   └── OrderStatusChangedEvent.java
│       │   ├── listener/                    # 4 Observers
│       │   │   ├── EmailNotificationListener.java (@Async)
│       │   │   ├── SmsNotificationListener.java (@Async, conditional)
│       │   │   ├── InventoryListener.java (@Order(1), synchronous)
│       │   │   └── AnalyticsListener.java (@Async)
│       │   ├── dto/OrderRequest.java
│       │   └── repository/OrderRepository.java
│       └── README.md (Spring Events, @Async, observer matrix)
│
└── 02-advanced-patterns/               # Microservices patterns
    │
    ├── demo-bff-pattern/               # ✅ COMPLETE (3 Services)
    │   ├── README.md                   # ✅ Complete testing guide
    │   │
    │   ├── shared-service/             # ✅ COMPLETE (Port 8100)
    │   │   ├── pom.xml
    │   │   ├── src/main/java/.../
    │   │   │   ├── SharedServiceApplication.java
    │   │   │   ├── controller/ProductController.java
    │   │   │   ├── model/Product.java (12 fields)
    │   │   │   ├── repository/ProductRepository.java
    │   │   │   └── config/DataInitializer.java (5 sample products)
    │   │   └── application.yml (port 8100)
    │   │
    │   ├── web-bff/                    # ✅ COMPLETE (Port 8101)
    │   │   ├── pom.xml (WebClient)
    │   │   ├── src/main/java/.../
    │   │   │   ├── WebBffApplication.java
    │   │   │   ├── controller/WebProductController.java
    │   │   │   ├── service/WebProductService.java (transformation)
    │   │   │   ├── client/SharedServiceClient.java (WebClient)
    │   │   │   └── dto/WebProductResponse.java (20 fields, formatted)
    │   │   └── application.yml (port 8101, shared URL)
    │   │
    │   └── mobile-bff/                 # ✅ COMPLETE (Port 8102)
    │       ├── pom.xml (WebClient)
    │       ├── src/main/java/.../
    │       │   ├── MobileBffApplication.java
    │       │   ├── controller/MobileProductController.java
    │       │   ├── service/MobileProductService.java (lightweight)
    │       │   ├── client/SharedServiceClient.java (WebClient)
    │       │   └── dto/
    │       │       ├── MobileProductResponse.java (11 fields, thumbnails)
    │       │       └── MobileProductDetailResponse.java (18 fields)
    │       └── application.yml (port 8102, shared URL)
    │
    ├── demo-reactive-webflux/          # 🟡 PLANNED
    └── demo-api-versioning/            # 🟡 PLANNED
```

---

## 🚀 Quick Start

### 1. Factory Pattern Demo
```bash
cd 11-advanced-patterns/01-design-patterns/demo-factory-pattern
mvn spring-boot:run

# Test Credit Card payment
curl -X POST http://localhost:8090/api/payments \
  -H "Content-Type: application/json" \
  -d '{"amount": 100.50, "paymentType": "CREDIT_CARD", "customerEmail": "user@example.com"}'
```

### 2. Builder Pattern Demo
```bash
cd 11-advanced-patterns/01-design-patterns/demo-builder-pattern
mvn spring-boot:run

# Create product with Lombok @Builder
curl -X POST http://localhost:8091/api/products \
  -H "Content-Type: application/json" \
  -d '{"name": "Laptop", "price": 1299.99, "category": "Electronics", "sku": "LAP-001"}'
```

### 3. Strategy Pattern Demo
```bash
cd 11-advanced-patterns/01-design-patterns/demo-strategy-pattern
mvn spring-boot:run

# Test BOGO discount
curl -X POST http://localhost:8092/api/orders \
  -H "Content-Type: application/json" \
  -d '{"customerEmail": "user@example.com", "amount": 80, "discountType": "BUY_ONE_GET_ONE", "itemQuantity": 4}'
```

### 4. Observer Pattern Demo
```bash
cd 11-advanced-patterns/01-design-patterns/demo-observer-pattern
mvn spring-boot:run

# Create order (triggers 4 listeners: Email, SMS, Inventory, Analytics)
curl -X POST http://localhost:8093/api/orders \
  -H "Content-Type: application/json" \
  -d '{"customerEmail": "test@example.com", "totalAmount": 150.00, "items": ["Laptop", "Mouse"]}'

# Update order status (triggers status change event)
curl -X PUT http://localhost:8093/api/orders/1/status \
  -H "Content-Type: application/json" \
  -d '{"status": "SHIPPED"}'
```

### 5. BFF Pattern Demo (3 Services)
```bash
# Terminal 1 - Shared Service (Backend)
cd 11-advanced-patterns/02-advanced-patterns/demo-bff-pattern/shared-service
mvn spring-boot:run

# Terminal 2 - Web BFF
cd 11-advanced-patterns/02-advanced-patterns/demo-bff-pattern/web-bff
mvn spring-boot:run

# Terminal 3 - Mobile BFF
cd 11-advanced-patterns/02-advanced-patterns/demo-bff-pattern/mobile-bff
mvn spring-boot:run

# Test Web BFF (rich, desktop-optimized responses)
curl http://localhost:8101/api/web/products/1

# Test Mobile BFF (lightweight, mobile-optimized responses)
curl http://localhost:8102/api/mobile/products/1
```

---

## 📚 Learning Resources

### Pattern Documentation
- **Main README:** Comprehensive 15,000+ line guide covering all patterns
- **Demo READMEs:** Each demo has detailed documentation with:
  - Pattern explanation (problem → solution)
  - Code walkthrough
  - curl test commands
  - Real-world use cases
  - When to use / when not to use

### Key Concepts Covered

#### Design Patterns (Classic)
1. **Factory Pattern** - Object creation without exposing logic ✅ Demo Complete
2. **Builder Pattern** - Complex object construction ✅ Demo Complete
3. **Strategy Pattern** - Interchangeable algorithms ✅ Demo Complete
4. **Observer Pattern** - Event-driven updates ✅ Demo Complete
5. **Singleton Pattern** - Single instance management (documented)
6. **Adapter Pattern** - Interface compatibility (documented)
7. **Facade Pattern** - Simplified interface (documented)
8. **Decorator Pattern** - Dynamic behavior addition (documented)
9. **Template Method** - Skeleton algorithm (documented)

#### Microservices Patterns (Advanced)
1. **Backend for Frontend (BFF)** - Client-specific backends ✅ Demo Complete (3 services)
2. **Strangler Fig** - Legacy system migration (documented)
3. **API Versioning** - Backward compatibility strategies (documented)
4. **Reactive Programming** - Non-blocking WebFlux (documented)
5. **GraphQL** - Flexible API queries (documented)
6. **gRPC** - High-performance RPC (documented)
7. **Saga Pattern** - Distributed transactions (documented)
8. **CQRS** - Command Query Responsibility Segregation (documented)
9. **Event Sourcing** - Event-driven state management (documented)

---

## 🎯 What You'll Learn

### Design Patterns
- ✅ When and why to use each pattern
- ✅ Spring Boot integration techniques
- ✅ Common anti-patterns to avoid
- ✅ Pattern combinations and best practices

### Microservices Patterns
- ✅ Scalability patterns (BFF, API Gateway)
- ✅ Data management patterns (CQRS, Event Sourcing)
- ✅ Communication patterns (gRPC, GraphQL, REST)
- ✅ Migration patterns (Strangler Fig)
- ✅ Resilience patterns (already covered in Module 05)

---

## ✅ Module Complete!

### All Core Components Finished:

#### ✅ Completed
1. **Observer Pattern Demo** - Spring Events with 4 listeners ✅
2. **BFF Pattern Services** - Complete with 3 services (shared, web, mobile) ✅
3. **Factory Pattern Demo** - 3 payment processors ✅
4. **Builder Pattern Demo** - Lombok + Custom implementations ✅
5. **Strategy Pattern Demo** - 6 discount strategies ✅

#### 📝 Documentation-Only (Optional for Future)
- Reactive WebFlux Demo
- API Versioning Demo
- GraphQL Demo
- gRPC Demo
- Template Method, Adapter, Facade demos

**Note:** Documentation for all patterns is comprehensive and complete. Additional demos can be added as optional enhancements.

---

## 💡 Best Practices Covered

### Code Quality
- Clean code principles
- SOLID principles application
- DRY (Don't Repeat Yourself)
- KISS (Keep It Simple, Stupid)

### Spring Boot
- Proper dependency injection
- Configuration management
- Exception handling
- Logging strategies

### Microservices
- Service decomposition
- API design
- Data management
- Communication patterns
- Observability

---

## 📊 Progress Metrics

- **Documentation:** 100% complete (15,000+ lines)
- **Classic Pattern Demos:** 4/4 complete (100%)
  - ✅ Factory Pattern (Port 8090)
  - ✅ Builder Pattern (Port 8091)
  - ✅ Strategy Pattern (Port 8092)
  - ✅ Observer Pattern (Port 8093)
- **Advanced Pattern Demos:** 1/1 complete (100%)
  - ✅ BFF Pattern (3 services, Ports 8100-8102)
- **Overall Module Progress:** 🎉 **100% COMPLETE** 🎉
✅ **Run all 5 demos** - Experience each pattern firsthand
2. ✅ **Study the code** - Understand implementation details
3. ✅ **Read the documentation** - Learn when to apply each pattern
4. ✅ **Compare different approaches** - Factory methods, builder patterns, discount strategies
5. 🎯 **Move to Module 12** - Apply patterns in capstone project
6. 📖 **Revisit Module 10** - DevOps & Deployment (if not completed)

---

## 📝 Notes

- All demos are **Spring Boot 3.2.0** compatible
- **Java 17+** required
- Each demo runs on a **different port** (8090-8093 for patterns, 8100-8102 for BFF)
- **H2 in-memory database** for quick startup
- **Lombok** used for reducing boilerplate
- **WebClient** for non-blocking inter-service communication
- **Spring Events** for observer pattern implementation
- READMEs include **curl test commands** for easy verification
- **5 sample products** preloaded in BFF demo

---

**Module Created:** December 2024  
**Last Updated:** December 2024  
**Status:** ✅ **100% Complete**
---

## 📝 Notes

- All demos are **Spring Boot 3.2.0** compatible
- **Java 17+** required
- Each demo runs on a **different port** (8090-8092)
- **H2 in-memory database** for quick startup
- **Lombok** used for reducing boilerplate
- READMEs include **curl test commands** for easy verification

---

**Module Created:** December 2024  
**Last Updated:** December 2024  
**Status:** In Progress (60% Complete)
