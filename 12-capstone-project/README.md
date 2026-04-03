# Module 12: Capstone Project - E-Commerce Microservices System

> **Build a production-ready e-commerce platform using everything learned in this masterclass**

## 🎯 Project Overview

This capstone project is the culmination of your Spring Boot & Microservices journey. You'll build a complete, production-ready e-commerce system that demonstrates all patterns, practices, and technologies covered in Modules 1-11.

### What You'll Build

A fully functional e-commerce platform with:
- **9 Microservices** working together
- **Event-driven architecture** with Kafka
- **Security** with JWT and OAuth2
- **Observability** with distributed tracing
- **Containerization** with Docker
- **Orchestration** with Kubernetes
- **CI/CD pipeline** with GitHub Actions
- **Production-grade** error handling & resilience

---

## 🏗️ System Architecture

### Microservices

| Service | Port | Purpose | Database |
|---------|------|---------|----------|
| **Eureka Server** | 8761 | Service Discovery | - |
| **Config Server** | 8888 | Centralized Configuration | - |
| **API Gateway** | 8080 | Single Entry Point, Auth | - |
| **Product Service** | 8081 | Product Catalog Management | PostgreSQL |
| **User Service** | 8082 | Authentication & User Management | PostgreSQL |
| **Order Service** | 8083 | Order Processing | PostgreSQL |
| **Inventory Service** | 8084 | Stock Level Management | PostgreSQL |
| **Payment Service** | 8085 | Payment Processing | PostgreSQL |
| **Notification Service** | 8086 | Email/SMS Notifications | MongoDB |

### Architecture Diagram

```
                              ┌─────────────────────┐
                              │   Eureka Server     │
                              │   (Discovery)       │
                              │   Port: 8761        │
                              └─────────────────────┘
                                        ▲
                                        │ Register
                                        │
                    ┌───────────────────┼───────────────────┐
                    │                   │                   │
         ┌──────────┴─────────┐  ┌─────┴──────┐  ┌────────┴────────┐
         │   Config Server    │  │ API Gateway │  │   All Services  │
         │   (Configuration)  │  │  (8080)     │  │   Register Here │
         │   Port: 8888       │  │             │  └─────────────────┘
         └────────────────────┘  └─────┬───────┘
                                       │
                          ┌────────────┼────────────┐
                          │            │            │
                    ┌─────▼─────┐ ┌───▼────┐ ┌────▼─────┐
                    │  Product   │ │  User  │ │  Order   │
                    │  Service   │ │ Service│ │  Service │
                    │  (8081)    │ │ (8082) │ │  (8083)  │
                    └─────┬──────┘ └───┬────┘ └────┬─────┘
                          │            │            │
                    ┌─────▼─────┐ ┌───▼────┐ ┌────▼─────┐
                    │ Inventory  │ │ Payment│ │Notification│
                    │  Service   │ │ Service│ │  Service │
                    │  (8084)    │ │ (8085) │ │  (8086)  │
                    └────────────┘ └────────┘ └──────────┘
                          │            │            │
                          └────────────┼────────────┘
                                       │
                          ┌────────────▼────────────┐
                          │   Kafka Message Broker  │
                          │   Event-Driven Comms    │
                          └─────────────────────────┘
```

---

## 🎓 Learning Objectives

By completing this capstone project, you will:

✅ **Architecture & Design**
- Design a complete microservices architecture
- Apply domain-driven design principles
- Implement inter-service communication patterns
- Handle distributed transactions with Saga pattern

✅ **Spring Boot & Spring Cloud**
- Build production-ready Spring Boot applications
- Configure service discovery with Eureka
- Implement API Gateway with routing and filters
- Use centralized configuration with Config Server
- Apply circuit breakers and resilience patterns

✅ **Security**
- Implement JWT-based authentication
- Secure service-to-service communication
- Apply OAuth2 for authorization
- Handle security at gateway level

✅ **Data Management**
- Database-per-service pattern
- Spring Data JPA with PostgreSQL
- MongoDB for document storage
- Transaction management across services

✅ **Event-Driven Architecture**
- Implement event-driven communication with Kafka
- Apply Saga pattern for distributed transactions
- Handle event ordering and idempotency
- Implement event sourcing for audit trails

✅ **Observability**
- Structured logging with correlation IDs
- Distributed tracing with Zipkin
- Metrics collection with Prometheus
- Monitoring dashboards with Grafana
- Health checks and actuator endpoints

✅ **Testing**
- Unit tests with JUnit 5 and Mockito
- Integration tests with TestContainers
- Contract testing between services
- E2E testing with REST Assured

✅ **DevOps & Deployment**
- Containerize services with Docker
- Multi-container orchestration with Docker Compose
- Kubernetes deployment manifests
- CI/CD pipeline with GitHub Actions
- Production deployment strategies

---

## 📁 Project Structure

```
12-capstone-project/
│
├── README.md                           # This file - Project overview
├── ARCHITECTURE.md                     # Detailed architecture documentation
├── SETUP.md                            # Setup and running instructions
├── API-DOCUMENTATION.md                # Complete API documentation
│
├── config-repo/                        # Git repository for Config Server
│   ├── application.yml                 # Common configuration
│   ├── eureka-server.yml
│   ├── api-gateway.yml
│   ├── product-service.yml
│   ├── order-service.yml
│   ├── payment-service.yml
│   ├── inventory-service.yml
│   ├── user-service.yml
│   └── notification-service.yml
│
├── infrastructure/                     # Core infrastructure services
│   ├── eureka-server/                 # Service Discovery
│   ├── config-server/                 # Centralized Configuration
│   └── api-gateway/                   # API Gateway
│
├── services/                           # Business microservices
│   ├── product-service/               # Product catalog management
│   ├── user-service/                  # User & authentication
│   ├── order-service/                 # Order processing
│   ├── inventory-service/             # Stock management
│   ├── payment-service/               # Payment processing
│   └── notification-service/          # Notifications
│
├── shared/                             # Shared libraries
│   ├── common-lib/                    # Common utilities
│   └── event-schemas/                 # Kafka event schemas
│
├── docker/
│   ├── docker-compose.yml             # Full stack local development
│   ├── docker-compose-infra.yml       # Infrastructure only
│   └── docker-compose-services.yml    # Services only
│
├── kubernetes/                         # Kubernetes deployment
│   ├── namespaces/
│   ├── configmaps/
│   ├── secrets/
│   ├── deployments/
│   ├── services/
│   ├── ingress/
│   └── monitoring/
│
├── monitoring/                         # Monitoring stack
│   ├── prometheus/
│   ├── grafana/
│   └── zipkin/
│
└── .github/
    └── workflows/
        ├── build.yml                   # CI pipeline
        └── deploy.yml                  # CD pipeline
```

---

## 🚀 Quick Start

### Prerequisites

- **Java 17 or higher**
- **Maven 3.8+**
- **Docker Desktop**
- **Kubernetes** (Minikube or Docker Desktop)
- **Kafka** (via Docker)
- **PostgreSQL** (via Docker)
- **MongoDB** (via Docker)

### Running the Complete System

#### Option 1: Docker Compose (Recommended for Development)

```bash
# Start infrastructure services (Eureka, Config, Gateway)
docker-compose -f docker/docker-compose-infra.yml up -d

# Wait for infrastructure to be ready (30 seconds)
sleep 30

# Start business services
docker-compose -f docker/docker-compose-services.yml up -d

# Start monitoring stack
docker-compose -f docker/docker-compose-monitoring.yml up -d

# Or start everything at once
docker-compose -f docker/docker-compose.yml up -d
```

#### Option 2: Kubernetes (Production-like)

```bash
# Apply all Kubernetes manifests
kubectl apply -f kubernetes/

# Wait for all pods to be ready
kubectl wait --for=condition=ready pod --all -n ecommerce --timeout=300s

# Access services via ingress
# Configure /etc/hosts: 127.0.0.1 ecommerce.local
```

#### Option 3: Run Locally (Development)

```bash
# Terminal 1 - Eureka Server
cd infrastructure/eureka-server
mvn spring-boot:run

# Terminal 2 - Config Server
cd infrastructure/config-server
mvn spring-boot:run

# Terminal 3 - API Gateway
cd infrastructure/api-gateway
mvn spring-boot:run

# Terminal 4-9 - Microservices
cd services/product-service && mvn spring-boot:run
cd services/user-service && mvn spring-boot:run
cd services/order-service && mvn spring-boot:run
cd services/inventory-service && mvn spring-boot:run
cd services/payment-service && mvn spring-boot:run
cd services/notification-service && mvn spring-boot:run
```

---

## 🧪 Testing the System

### 1. Check Service Health

```bash
# All services health
curl http://localhost:8080/actuator/health

# Eureka Dashboard
open http://localhost:8761
```

### 2. User Registration & Authentication

```bash
# Register a new user
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com",
    "password": "password123",
    "firstName": "John",
    "lastName": "Doe"
  }'

# Login to get JWT token
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com",
    "password": "password123"
  }'

# Response: {"token": "eyJhbGc..."}
export TOKEN=<your-token-here>
```

### 3. Browse Products

```bash
# Get all products
curl http://localhost:8080/api/products

# Search products
curl "http://localhost:8080/api/products/search?keyword=laptop"

# Get product details
curl http://localhost:8080/api/products/1
```

### 4. Place an Order

```bash
# Create order (requires authentication)
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "items": [
      {
        "productId": 1,
        "quantity": 2
      },
      {
        "productId": 2,
        "quantity": 1
      }
    ],
    "shippingAddress": {
      "street": "123 Main St",
      "city": "San Francisco",
      "state": "CA",
      "zipCode": "94102",
      "country": "USA"
    }
  }'
```

### 5. Check Order Status

```bash
# Get user's orders
curl http://localhost:8080/api/orders \
  -H "Authorization: Bearer $TOKEN"

# Get specific order
curl http://localhost:8080/api/orders/ORDER123 \
  -H "Authorization: Bearer $TOKEN"
```

---

## 🔄 Key Workflows

### Order Processing Flow (Saga Pattern)

```
1. Order Service: Create order (status: PENDING)
2. Inventory Service: Reserve inventory
   ├─ Success: Continue
   └─ Failure: Cancel order
3. Payment Service: Process payment
   ├─ Success: Continue
   └─ Failure: Release inventory, Cancel order
4. Notification Service: Send confirmation email
5. Order Service: Update order (status: CONFIRMED)
```

### Event Flow

```
Order Created Event
  ├─→ Inventory Service (Reserve Stock)
  ├─→ Payment Service (Process Payment)
  └─→ Notification Service (Send Email)

Payment Completed Event
  ├─→ Order Service (Update Status)
  └─→ Notification Service (Send Receipt)

Inventory Reserved Event
  └─→ Order Service (Update Status)
```

---

## 📊 Monitoring & Observability

### Access Monitoring Tools

- **Eureka Dashboard**: http://localhost:8761
- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3000 (admin/admin)
- **Zipkin**: http://localhost:9411
- **Kafka UI**: http://localhost:8090

### Key Metrics to Monitor

1. **Request Rate**: Total requests per second
2. **Error Rate**: Failed requests percentage
3. **Response Time**: P50, P95, P99 latencies
4. **Service Health**: Up/down status
5. **Database Connections**: Active connections
6. **Kafka Lag**: Consumer lag per topic

### Viewing Distributed Traces

1. Open Zipkin: http://localhost:9411
2. Search for traces by service name
3. View full request flow across services
4. Identify bottlenecks and errors

---

## 🛡️ Security Features

### Authentication & Authorization
- **JWT tokens** for user authentication
- **OAuth2** for third-party integrations
- **Spring Security** for endpoint protection
- **Password encryption** with BCrypt

### API Gateway Security
- Centralized authentication
- Rate limiting per user
- CORS configuration
- Request/response filtering

### Service-to-Service Communication
- Service account tokens
- Mutual TLS (mTLS) in production
- API key validation

---

## 🧩 Technologies Used

### Core Framework
- **Spring Boot 3.2.x**
- **Spring Cloud 2023.x**
- **Java 17**

### Microservices Patterns
- **Eureka** - Service Discovery
- **Spring Cloud Gateway** - API Gateway
- **Spring Cloud Config** - Configuration Management
- **Resilience4j** - Circuit Breaker, Retry, Rate Limiter
- **OpenFeign** - Declarative REST Clients

### Data & Messaging
- **PostgreSQL** - Relational database
- **MongoDB** - Document database
- **Apache Kafka** - Event streaming
- **Spring Data JPA** - Data access
- **Flyway** - Database migrations

### Security
- **Spring Security** - Authentication/Authorization
- **JWT** - Token-based auth
- **OAuth2** - Third-party auth

### Observability
- **Spring Cloud Sleuth** - Distributed tracing
- **Zipkin** - Trace visualization
- **Micrometer** - Metrics collection
- **Prometheus** - Metrics storage
- **Grafana** - Metrics visualization
- **Logback** - Structured logging

### Testing
- **JUnit 5** - Unit testing
- **Mockito** - Mocking
- **TestContainers** - Integration testing
- **REST Assured** - API testing
- **Spring Cloud Contract** - Contract testing

### DevOps
- **Docker** - Containerization
- **Kubernetes** - Orchestration
- **GitHub Actions** - CI/CD
- **Helm** - Kubernetes package manager

---

## 📚 Learning Resources

### Related Modules

This project uses concepts from:
- **Module 01**: Core Java Fundamentals
- **Module 02**: Spring Core & DI
- **Module 03**: Spring Boot Fundamentals
- **Module 04**: Microservices Architecture
- **Module 05**: Spring Cloud
- **Module 06**: Messaging & Event-Driven
- **Module 07**: Security
- **Module 08**: Testing
- **Module 09**: Observability
- **Module 10**: DevOps & Deployment
- **Module 11**: Advanced Patterns

### Additional Documentation

- [ARCHITECTURE.md](./ARCHITECTURE.md) - Detailed architecture
- [SETUP.md](./SETUP.md) - Setup instructions
- [API-DOCUMENTATION.md](./API-DOCUMENTATION.md) - API reference
- Individual service READMEs in each service folder

---

## 🎯 Project Milestones

### Phase 1: Infrastructure (Week 1)
- ✅ Eureka Server
- ✅ Config Server
- ✅ API Gateway
- ✅ Docker setup

### Phase 2: Core Services (Week 2)
- ✅ Product Service
- ✅ User Service
- ✅ Inventory Service

### Phase 3: Business Logic (Week 3)
- ✅ Order Service
- ✅ Payment Service
- ✅ Notification Service
- ✅ Event-driven flow

### Phase 4: Production Ready (Week 4)
- ✅ Security implementation
- ✅ Comprehensive testing
- ✅ Monitoring & observability
- ✅ Kubernetes deployment
- ✅ CI/CD pipeline

---

## 🏆 Success Criteria

You've successfully completed the capstone when:

✅ All 9 services start successfully  
✅ Services register with Eureka  
✅ API Gateway routes requests correctly  
✅ Users can register and login  
✅ Products can be browsed and searched  
✅ Orders can be placed and processed  
✅ Payments are processed successfully  
✅ Inventory is updated in real-time  
✅ Notifications are sent  
✅ Distributed tracing works across services  
✅ Metrics are collected and visible in Grafana  
✅ All tests pass (unit, integration, E2E)  
✅ System runs on Kubernetes  
✅ CI/CD pipeline deploys successfully  

---

## 🐛 Troubleshooting

### Services Not Registering with Eureka
```bash
# Check Eureka is running
curl http://localhost:8761

# Check service logs
docker logs <service-name>

# Verify eureka.client.serviceUrl.defaultZone in config
```

### Database Connection Issues
```bash
# Check PostgreSQL is running
docker ps | grep postgres

# Check connection string in application.yml
spring.datasource.url: jdbc:postgresql://localhost:5432/dbname
```

### Kafka Connection Issues
```bash
# Check Kafka is running
docker ps | grep kafka

# Check Kafka topics
docker exec -it kafka kafka-topics.sh --list --bootstrap-server localhost:9092
```

---

## 📝 Next Steps

After completing this capstone:

1. **Deploy to Cloud**
   - AWS EKS, Azure AKS, or Google GKE
   - Configure production databases
   - Set up cloud monitoring

2. **Add Advanced Features**
   - Service mesh with Istio
   - GraphQL API
   - Reactive endpoints with WebFlux
   - gRPC for internal communication

3. **Enhance Security**
   - HashiCorp Vault for secrets
   - OAuth2 with external providers
   - API key management
   - DDoS protection

4. **Scale & Optimize**
   - Horizontal pod autoscaling
   - Database read replicas
   - Redis caching
   - CDN for static assets

5. **Build Your Own**
   - Adapt this for your domain
   - Add custom business logic
   - Experiment with different patterns
   - Share your project on GitHub

---

## 🙏 Acknowledgments

This capstone project represents the culmination of everything taught in the Spring Boot & Microservices Masterclass. It demonstrates production-ready patterns and best practices used by companies building microservices at scale.

---

## 📧 Support

For questions or issues:
- Review the detailed documentation in each service
- Check the troubleshooting section
- Review logs with correlation IDs
- Use distributed tracing to debug issues

---

**🎉 Congratulations on reaching the capstone project! You've learned everything needed to build production-ready microservices. Now it's time to bring it all together!**

---

**Last Updated**: February 2026  
**Version**: 1.0.0  
**Status**: ✅ Ready to Build
