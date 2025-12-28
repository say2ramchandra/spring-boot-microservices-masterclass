# 🎯 Runnable Demos - Complete Overview

## 📊 Demo Status Summary

| Module | Demo Project | Status | Description |
|--------|--------------|--------|-------------|
| **01** | demo-stream-operations | ✅ Complete | Java Streams & Lambdas |
| **02** | demo-bean-lifecycle | ✅ Complete | Spring Bean Lifecycle |
| **03** | demo-complete-rest-api | ✅ Complete | REST API with CRUD |
| **05** | demo-eureka-server | ✅ **NEW!** | Service Discovery |
| **05** | demo-product-service | ✅ **NEW!** | Product Microservice |
| **05** | demo-order-service | ✅ **NEW!** | Order Service with Feign |
| **05** | demo-api-gateway | ✅ **NEW!** | API Gateway |
| **06** | demo-rabbitmq | ✅ **NEW!** | RabbitMQ Messaging |
| **06** | demo-kafka | 🟡 Planned | Kafka Event Streaming |
| **07** | demo-jwt-auth | 🟡 Planned | JWT Security |
| **08** | demo-testing-complete | 🟡 Planned | Comprehensive Testing |

**Total Demos**: 8 complete, 3 planned = **11 total**

---

## 🚀 Quick Start Guide

### Module 01: Java Streams Demo
```bash
cd 01-core-java-fundamentals/02-streams-and-lambdas/demo-stream-operations
mvn clean compile exec:java
```
**Learn**: Streams, lambdas, functional programming

---

### Module 02: Bean Lifecycle Demo
```bash
cd 02-spring-core/02-bean-lifecycle/demo-bean-lifecycle
mvn spring-boot:run
```
**Learn**: Spring bean lifecycle, post-construct, pre-destroy

---

### Module 03: REST API Demo
```bash
cd 03-spring-boot-fundamentals/02-rest-api/demo-complete-rest-api
mvn spring-boot:run
# Visit: http://localhost:8080/swagger-ui.html
```
**Learn**: REST endpoints, CRUD, validation, Swagger

---

### Module 05: Complete Microservices Ecosystem ⭐ NEW!

**Run all 4 services** (in separate terminals):

**Terminal 1 - Eureka Server**:
```bash
cd 05-spring-cloud/demo-eureka-server
mvn spring-boot:run
# Dashboard: http://localhost:8761
```

**Terminal 2 - Product Service**:
```bash
cd 05-spring-cloud/demo-product-service
mvn spring-boot:run
# API: http://localhost:8081/api/products
```

**Terminal 3 - Order Service**:
```bash
cd 05-spring-cloud/demo-order-service
mvn spring-boot:run
# API: http://localhost:8083/api/orders
```

**Terminal 4 - API Gateway**:
```bash
cd 05-spring-cloud/demo-api-gateway
mvn spring-boot:run
# Gateway: http://localhost:8080
```

**Test the ecosystem**:
```bash
# Get products through gateway
curl http://localhost:8080/api/products

# Create order (tests Feign Client!)
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"customerName":"John","customerEmail":"john@test.com","items":[{"productId":1,"quantity":2}]}'
```

**Learn**: Eureka, API Gateway, Feign Client, Circuit Breaker, Service Discovery

---

### Module 06: RabbitMQ Messaging ⭐ NEW!

**Step 1 - Start RabbitMQ**:
```bash
docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management
# Management UI: http://localhost:15672 (guest/guest)
```

**Step 2 - Run Demo**:
```bash
cd 06-messaging/demo-rabbitmq
mvn spring-boot:run
```

**Step 3 - Test Exchanges**:

**Direct Exchange** (one-to-one):
```bash
curl -X POST http://localhost:8084/api/messages/order \
  -H "Content-Type: application/json" \
  -d '{"customerId":"C123","amount":99.99}'
```

**Fanout Exchange** (broadcast):
```bash
curl -X POST http://localhost:8084/api/messages/broadcast \
  -H "Content-Type: application/json" \
  -d '{"message":"System update"}'
```

**Topic Exchange** (pattern):
```bash
curl -X POST http://localhost:8084/api/messages/notification/email \
  -H "Content-Type: application/json" \
  -d '{"message":"Welcome!"}'
```

**Learn**: Message queues, exchanges, routing, pub-sub patterns

---

## 🎓 Learning Paths

### Path 1: Java & Spring Basics (Week 1)
1. Module 01 Demo - Java Streams
2. Module 02 Demo - Bean Lifecycle
3. Module 03 Demo - REST API

**Output**: Solid foundation in modern Java and Spring Boot

### Path 2: Microservices Architecture (Week 2-3)
1. Module 05 - Eureka Server (Service Discovery)
2. Module 05 - Product & Order Services (Microservices)
3. Module 05 - API Gateway (Routing)
4. Test the complete ecosystem

**Output**: Understanding of microservices patterns

### Path 3: Async Communication (Week 3-4)
1. Module 06 - RabbitMQ Demo
2. Module 06 - Kafka Demo (when available)
3. Integrate with Module 05 services

**Output**: Event-driven architecture skills

---

## 💡 Demo Integration Scenarios

### Scenario 1: Complete Order Flow
**Services Involved**: All Module 05 services + RabbitMQ

1. Client sends order to API Gateway
2. Gateway routes to Order Service
3. Order Service calls Product Service via Feign
4. Order created and event published to RabbitMQ
5. Email/SMS consumers process notifications

**How to Set Up**:
1. Run all Module 05 services
2. Run RabbitMQ demo
3. Modify Order Service to publish events (exercise!)

### Scenario 2: Service Resilience Testing
**Objective**: Test circuit breakers and fallbacks

1. Start all Module 05 services
2. Create successful orders
3. Stop Product Service
4. Create order → observe fallback
5. Restart Product Service → circuit breaker recovers

### Scenario 3: Load Balancing
**Objective**: See Eureka load balancing in action

1. Start Eureka Server
2. Start Product Service on port 8081
3. Start Product Service on port 8082
4. Make multiple requests through gateway
5. Observe round-robin distribution in logs

---

## 📦 All Demo Features

### Module 01 Demo (Streams)
- ✅ Stream creation (8 methods)
- ✅ Intermediate operations (filter, map, flatMap)
- ✅ Terminal operations (collect, reduce, forEach)
- ✅ Real-world queries (employee management)
- ✅ Parallel streams

### Module 02 Demo (Bean Lifecycle)
- ✅ All 12 lifecycle stages
- ✅ @PostConstruct and @PreDestroy
- ✅ BeanPostProcessor
- ✅ Bean scopes (singleton, prototype)
- ✅ Initialization and destruction callbacks

### Module 03 Demo (REST API)
- ✅ Complete CRUD operations
- ✅ Input validation
- ✅ Exception handling
- ✅ Custom error responses
- ✅ Swagger/OpenAPI documentation
- ✅ H2 in-memory database
- ✅ JPA entities and repositories

### Module 05 Demos (Microservices) ⭐
- ✅ Eureka Server with dashboard
- ✅ Service registration and discovery
- ✅ Feign declarative REST client
- ✅ Circuit breaker with fallbacks
- ✅ API Gateway routing
- ✅ Load balancing
- ✅ Health checks
- ✅ Custom gateway filters

### Module 06 Demo (RabbitMQ) ⭐
- ✅ Direct Exchange (one-to-one)
- ✅ Fanout Exchange (broadcast)
- ✅ Topic Exchange (pattern matching)
- ✅ Multiple consumers
- ✅ Message acknowledgment
- ✅ JSON serialization
- ✅ Retry logic

---

## 🛠️ Prerequisites by Module

| Module | Java | Maven | Spring Boot | External Services |
|--------|------|-------|-------------|-------------------|
| 01 | ✅ 17+ | ✅ 3.8+ | ❌ | None |
| 02 | ✅ 17+ | ✅ 3.8+ | ✅ 3.2+ | None |
| 03 | ✅ 17+ | ✅ 3.8+ | ✅ 3.2+ | None |
| 05 | ✅ 17+ | ✅ 3.8+ | ✅ 3.2+ | None (all included) |
| 06 | ✅ 17+ | ✅ 3.8+ | ✅ 3.2+ | RabbitMQ (Docker) |

---

## 🎯 Ports Reference

| Service | Port | URL |
|---------|------|-----|
| REST API Demo | 8080 | http://localhost:8080/swagger-ui.html |
| Product Service | 8081 | http://localhost:8081/api/products |
| Order Service | 8083 | http://localhost:8083/api/orders |
| RabbitMQ Demo | 8084 | http://localhost:8084/api/messages |
| Eureka Dashboard | 8761 | http://localhost:8761 |
| API Gateway | 8080 | http://localhost:8080 |
| RabbitMQ Management | 15672 | http://localhost:15672 |

---

## 🔥 Testing Commands Reference

### Module 03 - REST API
```bash
# Get all users
curl http://localhost:8080/api/users

# Create user
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"firstName":"John","lastName":"Doe","email":"john@example.com","age":30}'
```

### Module 05 - Microservices
```bash
# Get products
curl http://localhost:8080/api/products

# Create order
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"customerName":"Alice","customerEmail":"alice@test.com","items":[{"productId":1,"quantity":2}]}'

# Check Eureka
curl http://localhost:8761/eureka/apps
```

### Module 06 - RabbitMQ
```bash
# Send order event
curl -X POST http://localhost:8084/api/messages/order \
  -H "Content-Type: application/json" \
  -d '{"customerId":"C123","amount":50}'

# Broadcast message
curl -X POST http://localhost:8084/api/messages/broadcast \
  -H "Content-Type: application/json" \
  -d '{"message":"Important announcement"}'
```

---

## 📚 Next Demos (Coming Soon)

### Module 07: JWT Security Demo
**Planned Features**:
- User registration and login
- JWT token generation
- Token validation filter
- Protected endpoints
- Refresh token mechanism

### Module 08: Testing Demo
**Planned Features**:
- Unit tests with JUnit 5
- Integration tests with TestContainers
- Contract tests with Spring Cloud Contract
- E2E tests with REST Assured
- Test coverage with JaCoCo

---

## ✅ Verification Checklist

After running each demo, verify:

**Module 01**:
- [ ] Console shows employee data
- [ ] All 5 parts execute successfully
- [ ] Stream operations produce correct results

**Module 02**:
- [ ] All 12 lifecycle stages logged
- [ ] Application starts and stops cleanly
- [ ] BeanPostProcessor executes

**Module 03**:
- [ ] Swagger UI accessible
- [ ] Can create users
- [ ] Validation works
- [ ] H2 console accessible

**Module 05**:
- [ ] All 3 services visible in Eureka
- [ ] Products returned through gateway
- [ ] Orders created with product details
- [ ] Circuit breaker fallback works

**Module 06**:
- [ ] RabbitMQ management UI accessible
- [ ] All queues created
- [ ] Messages consumed by listeners
- [ ] Logs show producer/consumer activity

---

## 🎉 Achievement Milestones

- 🏅 **Bronze**: Run all Module 01-03 demos
- 🥈 **Silver**: Run complete Module 05 ecosystem
- 🥇 **Gold**: Integrate Module 05 + Module 06
- 💎 **Platinum**: Build your own microservice!

---

**Last Updated**: After Module 06 RabbitMQ demo creation
**Total Lines of Demo Code**: 5,000+
**Ready-to-Run Demos**: 8

🚀 **Start building and learning!**
