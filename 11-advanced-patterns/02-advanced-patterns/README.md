# Advanced Microservices Patterns

> **Enterprise-grade patterns for production microservices architectures**

This section covers advanced patterns specifically designed for distributed microservices systems. These patterns address real-world challenges in building scalable, maintainable, and resilient microservices.

---

## 📚 Patterns Overview

### ✅ Implemented Patterns (With Working Demos)

#### 1. Backend for Frontend (BFF) Pattern
**Status:** ✅ Complete Demo  
**Location:** [demo-bff-pattern/](demo-bff-pattern/)  
**Ports:** 8100 (Shared), 8101 (Web), 8102 (Mobile)

**Problem:** Different client types (web, mobile, desktop, IoT) have vastly different requirements:
- Web browsers need rich, formatted data
- Mobile apps need lightweight payloads to save bandwidth/battery
- Different authentication, caching, and optimization strategies

**Solution:** Create separate backend services for each client type, optimized specifically for that frontend.

**Real-World Examples:**
- Netflix (TV vs Mobile vs Web)
- Spotify (Desktop vs Mobile)
- Amazon (Different UIs per platform)

**Demo Includes:**
- 3 microservices showing complete BFF architecture
- Shared backend service with product data
- Web BFF with rich, desktop-optimized responses
- Mobile BFF with lightweight, mobile-optimized responses
- Complete testing guide with curl examples

[→ Explore BFF Pattern Demo](demo-bff-pattern/)

---

### 📝 Documented Patterns (Theory + Best Practices)

#### 2. Strangler Fig Pattern
**Purpose:** Incrementally migrate from monolith to microservices

**Key Concepts:**
- Gradually replace legacy system components
- Run old and new systems side by side
- Route traffic selectively during migration
- Zero downtime migration strategy

**When to Use:**
- Migrating large monolithic applications
- Risk-averse migration strategy needed
- Need to maintain business continuity

**Covered in:** [Main Module README](../README.md#strangler-fig-pattern)

---

#### 3. API Composition Pattern
**Purpose:** Aggregate data from multiple microservices

**Key Concepts:**
- API Gateway queries multiple services
- Compose results into single response
- Handle partial failures gracefully

**When to Use:**
- Need data from multiple services in one call
- Want to reduce client-side complexity
- Mobile apps need aggregated data

**Covered in:** [Main Module README](../README.md#api-composition-pattern)

---

#### 4. Reactive Programming (Spring WebFlux)
**Purpose:** Non-blocking, asynchronous I/O for high throughput

**Key Concepts:**
- Reactive Streams (Mono, Flux)
- Backpressure handling
- Event-driven, non-blocking
- Scales with fewer threads

**When to Use:**
- High-concurrency requirements
- Streaming data scenarios
- Need to handle many concurrent connections

**Covered in:** [Main Module README](../README.md#reactive-programming)

---

#### 5. API Versioning
**Purpose:** Maintain backward compatibility while evolving APIs

**Strategies:**
- **URI Versioning:** `/api/v1/products`, `/api/v2/products`
- **Header Versioning:** Custom header `X-API-Version: 1`
- **Media Type Versioning:** `Accept: application/vnd.myapi.v1+json`
- **Query Parameter:** `/api/products?version=1`

**Best Practice:** URI versioning (most explicit and discoverable)

**Covered in:** [Main Module README](../README.md#api-versioning)

---

#### 6. GraphQL Pattern
**Purpose:** Flexible API queries - clients request exactly what they need

**Key Concepts:**
- Single endpoint for all queries
- Client specifies required fields
- Eliminates over-fetching/under-fetching
- Strong typing and introspection

**When to Use:**
- Multiple client types with different needs
- Mobile apps with limited bandwidth
- Need flexible, ad-hoc queries

**Covered in:** [Main Module README](../README.md#graphql)

---

#### 7. gRPC Pattern
**Purpose:** High-performance RPC with Protocol Buffers

**Key Concepts:**
- Binary protocol (faster than JSON)
- HTTP/2-based (multiplexing, streaming)
- Strong typing with .proto files
- Code generation for multiple languages

**When to Use:**
- Internal microservice communication
- Performance-critical services
- Need streaming (bidirectional, server-side, client-side)

**Covered in:** [Main Module README](../README.md#grpc)

---

## 🏗️ Section Structure

```
02-advanced-patterns/
│
├── README.md                       # ✅ This file
│
└── demo-bff-pattern/               # ✅ Complete 3-service demo
    ├── README.md                   # Architecture & testing guide
    │
    ├── shared-service/             # Backend (Port 8100)
    │   ├── pom.xml
    │   └── src/main/java/.../
    │       ├── SharedServiceApplication.java
    │       ├── controller/ProductController.java
    │       ├── model/Product.java
    │       ├── repository/ProductRepository.java
    │       └── config/DataInitializer.java (5 sample products)
    │
    ├── web-bff/                    # Web BFF (Port 8101)
    │   ├── pom.xml
    │   └── src/main/java/.../
    │       ├── WebBffApplication.java
    │       ├── controller/WebProductController.java
    │       ├── service/WebProductService.java
    │       ├── client/SharedServiceClient.java
    │       └── dto/WebProductResponse.java (20 fields)
    │
    └── mobile-bff/                 # Mobile BFF (Port 8102)
        ├── pom.xml
        └── src/main/java/.../
            ├── MobileBffApplication.java
            ├── controller/MobileProductController.java
            ├── service/MobileProductService.java
            ├── client/SharedServiceClient.java
            └── dto/ (List + Detail responses)
```

---

## 🚀 Quick Start

### Running the BFF Pattern Demo

**Prerequisites:**
- Java 17+
- Maven 3.8+
- 3 available ports: 8100, 8101, 8102

**Step 1: Start Shared Service**
```bash
cd demo-bff-pattern/shared-service
mvn spring-boot:run
```

**Step 2: Start Web BFF**
```bash
cd demo-bff-pattern/web-bff
mvn spring-boot:run
```

**Step 3: Start Mobile BFF**
```bash
cd demo-bff-pattern/mobile-bff
mvn spring-boot:run
```

**Step 4: Test & Compare**
```bash
# Web BFF - Rich desktop response (1200 bytes)
curl http://localhost:8101/api/web/products/1

# Mobile BFF - Lightweight mobile response (300 bytes)
curl http://localhost:8102/api/mobile/products/1
```

---

## 🎯 Pattern Comparison

| Pattern | Use Case | Complexity | Performance | When to Use |
|---------|----------|------------|-------------|-------------|
| **BFF** | Client-specific backends | Medium | Good | Multiple platform types (web, mobile, IoT) |
| **Strangler Fig** | Legacy migration | High | N/A | Migrating from monolith to microservices |
| **API Composition** | Data aggregation | Medium | Medium | Need data from multiple services |
| **Reactive** | High concurrency | High | Excellent | Many concurrent connections, streaming |
| **GraphQL** | Flexible queries | Medium | Good | Variable client data needs |
| **gRPC** | High-performance RPC | Medium | Excellent | Internal service communication |
| **API Versioning** | Backward compatibility | Low | N/A | Evolving public APIs |

---

## 💡 Design Principles

### 1. **Client-First Design**
Design your API around client needs, not database schema.

### 2. **Evolutionary Architecture**
Build systems that can evolve without breaking existing clients.

### 3. **Performance Optimization**
- Minimize payload size (especially for mobile)
- Use caching strategically
- Consider non-blocking I/O for high throughput

### 4. **Separation of Concerns**
- BFFs handle client-specific logic
- Backend services remain generic
- Clear boundaries between services

### 5. **Resilience**
- Handle partial failures gracefully
- Implement circuit breakers
- Provide fallback responses

---

## 🔍 Related Patterns (Other Modules)

### From Module 05: Spring Cloud
- **Service Discovery** (Eureka) - Dynamic service registration
- **API Gateway** (Spring Cloud Gateway) - Single entry point
- **Circuit Breaker** (Resilience4j) - Fault tolerance
- **Config Server** - Centralized configuration

### From Module 06: Messaging
- **Saga Pattern** - Distributed transactions
- **Event Sourcing** - Event-driven state
- **CQRS** - Read/Write separation

### From Module 07: Security
- **OAuth2/JWT** - Authentication & authorization
- **mTLS** - Service-to-service security

---

## 📚 Learning Path

1. **Start Here:** Study the [BFF Pattern Demo](demo-bff-pattern/)
   - Run all 3 services
   - Compare Web vs Mobile responses
   - Understand transformation logic

2. **Deep Dive:** Read the [Main Module README](../README.md)
   - Comprehensive coverage of all patterns
   - UML diagrams and code examples
   - Real-world use cases

3. **Apply:** Use patterns in [Module 12 Capstone Project](../../12-capstone-project/)
   - E-commerce microservices system
   - Apply BFF for web and mobile clients
   - Implement reactive endpoints if needed

---

## 🎓 Key Takeaways

### BFF Pattern (Implemented)
- ✅ One backend per client type
- ✅ Optimize responses for each platform
- ✅ Independent evolution of client APIs
- ✅ Loose coupling between frontend and backend

### Other Patterns (Documented)
- ✅ Strangler Fig for safe migrations
- ✅ Reactive for high concurrency
- ✅ GraphQL/gRPC for specialized APIs
- ✅ API versioning for backward compatibility

---

## 📊 Statistics

- **Implemented Demos:** 1 (BFF Pattern with 3 services)
- **Total Services:** 3 (Shared, Web BFF, Mobile BFF)
- **Documented Patterns:** 6 additional patterns
- **Lines of Code:** 1,800+
- **Documentation:** 2,000+ lines

---

## 🔗 Additional Resources

### Official Documentation
- [Spring Cloud Gateway](https://spring.io/projects/spring-cloud-gateway)
- [Spring WebFlux](https://docs.spring.io/spring-framework/reference/web/webflux.html)
- [GraphQL Java](https://www.graphql-java.com/)
- [gRPC](https://grpc.io/docs/languages/java/)

### Pattern References
- [Microservices Patterns - Chris Richardson](https://microservices.io/patterns/)
- [BFF Pattern - Sam Newman](https://samnewman.io/patterns/architectural/bff/)
- [Building Microservices - Sam Newman](https://www.oreilly.com/library/view/building-microservices-2nd/9781492034018/)

---

## ❓ FAQ

**Q: Why not just use one API for all clients?**  
A: Different clients have different constraints. Mobile needs lightweight payloads, web can handle rich data. One-size-fits-all becomes complex and suboptimal.

**Q: Isn't maintaining multiple BFFs overhead?**  
A: Yes, but the benefits outweigh costs when you have distinct client types. Each team can optimize for their platform independently.

**Q: When should I use GraphQL instead of BFF?**  
A: GraphQL is great when you have many client types making ad-hoc queries. BFF is better when you have 2-3 distinct platforms with predictable needs.

**Q: Can I combine patterns?**  
A: Absolutely! API Gateway + BFF + Reactive is a common combination. Or BFF + GraphQL for maximum flexibility.

**Q: Should BFFs share code?**  
A: Minimize sharing. Each BFF should be independently deployable. Use shared libraries sparingly (common DTOs, utilities only).

---

**Next Steps:**  
→ [Explore BFF Pattern Demo](demo-bff-pattern/)  
→ [Back to Module 11](../README.md)  
→ [Module Status](../MODULE-STATUS.md)

---

**Section Status:** ✅ Complete (1 demo + 6 documented patterns)  
**Last Updated:** February 2026