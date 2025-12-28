# Module 05: Spring Cloud - Running Demos

## 🎯 Complete Microservices Ecosystem

This module contains 4 working Spring Boot applications that demonstrate a complete microservices architecture with service discovery, API Gateway, and inter-service communication.

## 📦 Demo Projects

| Project | Port | Description |
|---------|------|-------------|
| **demo-eureka-server** | 8761 | Service Discovery Server |
| **demo-product-service** | 8081 | Product catalog microservice |
| **demo-order-service** | 8083 | Order management with Feign Client |
| **demo-api-gateway** | 8080 | API Gateway for routing |

## 🚀 Quick Start - Run All Services

### Prerequisites
- Java 17+
- Maven 3.8+

### Step-by-Step Startup

**Terminal 1: Start Eureka Server**
```bash
cd 05-spring-cloud/demo-eureka-server
mvn spring-boot:run
```
Wait for: `🚀 Eureka Server Started Successfully!`
Dashboard: http://localhost:8761

**Terminal 2: Start Product Service**
```bash
cd 05-spring-cloud/demo-product-service
mvn spring-boot:run
```
Wait for: `🚀 Product Service Started!`

**Terminal 3: Start Order Service**
```bash
cd 05-spring-cloud/demo-order-service
mvn spring-boot:run
```
Wait for: `🚀 Order Service Started!`

**Terminal 4: Start API Gateway**
```bash
cd 05-spring-cloud/demo-api-gateway
mvn spring-boot:run
```
Wait for: `🚀 API Gateway Started Successfully!`

## ✅ Verification

### 1. Check Eureka Dashboard
Visit: http://localhost:8761

You should see 3 registered services:
- PRODUCT-SERVICE
- ORDER-SERVICE
- API-GATEWAY

### 2. Test Product Service Direct
```bash
curl http://localhost:8081/api/products
```

### 3. Test Through API Gateway
```bash
curl http://localhost:8080/api/products
```

### 4. Create Order (Tests Feign Client!)
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "John Doe",
    "customerEmail": "john@example.com",
    "items": [
      {"productId": 1, "quantity": 2},
      {"productId": 2, "quantity": 1}
    ]
  }'
```

This tests the complete flow:
1. Request → API Gateway (port 8080)
2. Gateway routes to Order Service (via Eureka)
3. Order Service calls Product Service using **Feign Client**
4. Feign discovers Product Service from Eureka
5. Product details fetched and order created!

## 📚 What Each Demo Teaches

### demo-eureka-server
**Concepts**:
- Service registry
- Service discovery
- Health monitoring
- Dashboard UI

**Key Files**:
- `@EnableEurekaServer`
- `application.yml` - Eureka server configuration

### demo-product-service
**Concepts**:
- Eureka client registration
- REST API endpoints
- Circuit breaker with Resilience4j
- Health checks

**Key Files**:
- `@EnableDiscoveryClient`
- `ProductController` - REST endpoints
- `@CircuitBreaker` annotation
- Fallback methods

### demo-order-service
**Concepts**:
- **Feign Client** for inter-service communication
- Service discovery integration
- Automatic load balancing
- Fallback handling

**Key Files**:
- `@EnableFeignClients`
- `ProductClient` interface - Feign declaration
- `ProductClientFallback` - Fallback implementation
- Order creation with service calls

### demo-api-gateway
**Concepts**:
- Central routing
- Load balancing
- Circuit breaker at gateway level
- Request/response filtering
- Fallback responses

**Key Files**:
- Spring Cloud Gateway configuration
- Route definitions in `application.yml`
- `LoggingFilter` - Custom filter
- `FallbackController` - Fallback endpoints

## 🧪 Testing Scenarios

### Scenario 1: Service Discovery & Load Balancing

**Run multiple Product Service instances**:
```bash
# Terminal 1: Instance 1 on port 8081
cd demo-product-service
mvn spring-boot:run

# Terminal 2: Instance 2 on port 8082
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8082
```

**Test load balancing**:
```bash
# Call multiple times through gateway
for i in {1..10}; do
  curl http://localhost:8080/api/products/info
done
```

Check logs - requests distributed across both instances!

### Scenario 2: Circuit Breaker

**Stop Product Service**:
```bash
# Stop the Product Service terminal (Ctrl+C)
```

**Create order - see fallback in action**:
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "Jane Doe",
    "customerEmail": "jane@example.com",
    "items": [{"productId": 1, "quantity": 1}]
  }'
```

**Expected**: Order created with fallback product data:
- Product Name: "Product Temporarily Unavailable"
- Price: $0.00
- Service doesn't crash!

**Check logs**:
```
⚠️ Product Service unavailable. Returning fallback product
```

### Scenario 3: Gateway Routing

**Access services through gateway**:
```bash
# Product Service through gateway
curl http://localhost:8080/api/products

# Order Service through gateway
curl http://localhost:8080/api/orders

# Gateway automatically routes based on path!
```

### Scenario 4: Gateway Circuit Breaker

**Stop all backend services, then**:
```bash
curl http://localhost:8080/api/products
```

**Expected fallback response**:
```json
{
  "message": "Product Service is temporarily unavailable",
  "status": "SERVICE_UNAVAILABLE",
  "recommendation": "Please try again later"
}
```

## 📊 Architecture Diagram

```
┌─────────────────┐
│   Client        │
└────────┬────────┘
         │
         ▼
┌─────────────────────┐
│   API Gateway       │ (Port 8080)
│   - Routing         │
│   - Load Balancing  │
│   - Circuit Breaker │
└────────┬────────────┘
         │
         ├────────────────────┐
         │                    │
         ▼                    ▼
┌─────────────────┐  ┌─────────────────┐
│ Product Service │  │  Order Service  │
│  (Port 8081)    │◄─│  (Port 8083)    │
│                 │  │  [Feign Client] │
└────────┬────────┘  └────────┬────────┘
         │                    │
         │   Service          │
         │   Discovery        │
         │                    │
         └────────┬───────────┘
                  ▼
         ┌─────────────────┐
         │ Eureka Server   │
         │  (Port 8761)    │
         └─────────────────┘
```

## 🔍 Monitoring & Debugging

### Eureka Dashboard
http://localhost:8761
- View all registered services
- Check instance status
- See renewal statistics

### Actuator Endpoints

**Product Service**:
```bash
curl http://localhost:8081/actuator/health
curl http://localhost:8081/actuator/info
```

**Gateway**:
```bash
curl http://localhost:8080/actuator/health
curl http://localhost:8080/actuator/gateway/routes
```

### Log Files
Each service logs:
- Service registration events
- Request routing
- Circuit breaker state changes
- Feign client calls

## 🎓 Learning Path

1. **Start with Eureka Server**
   - Understand service registry concept
   - Explore dashboard

2. **Add Product Service**
   - See service registration
   - Test REST endpoints
   - Understand health checks

3. **Add Order Service**
   - Learn Feign Client usage
   - See inter-service communication
   - Test fallback mechanisms

4. **Add API Gateway**
   - Central entry point
   - Routing configuration
   - Gateway-level circuit breaker

5. **Experiment**
   - Stop/start services
   - Run multiple instances
   - Observe load balancing
   - Test circuit breakers

## 🛠️ Troubleshooting

**Service not registering with Eureka**:
- Check Eureka server is running first
- Verify `eureka.client.service-url.defaultZone` in application.yml
- Check network connectivity
- Look for errors in console logs

**Feign Client not working**:
- Ensure Product Service is registered in Eureka
- Check `@EnableFeignClients` annotation
- Verify service name matches Eureka registration
- Check logs for discovery errors

**Gateway not routing**:
- Verify path predicates in gateway config
- Check service is registered in Eureka
- Test direct service access first
- Review gateway logs for routing decisions

**Circuit breaker not triggering**:
- Ensure enough failures occurred (check `minimum-number-of-calls`)
- Verify resilience4j dependency
- Check circuit breaker configuration
- Monitor logs for state transitions

## 📖 Additional Resources

- [Spring Cloud Documentation](https://spring.io/projects/spring-cloud)
- [Netflix Eureka](https://github.com/Netflix/eureka/wiki)
- [Spring Cloud Gateway](https://spring.io/projects/spring-cloud-gateway)
- [OpenFeign](https://github.com/OpenFeign/feign)
- [Resilience4j](https://resilience4j.readme.io/)

## 🎉 Success Criteria

You've successfully completed this module when you can:
- ✅ Start all 4 services in correct order
- ✅ See all services registered in Eureka
- ✅ Access services through API Gateway
- ✅ Create orders that call Product Service via Feign
- ✅ Observe circuit breaker fallbacks
- ✅ Run multiple instances and see load balancing
- ✅ Understand service discovery flow

## 🚀 Next Steps

After mastering these demos:
1. **Module 06**: Add RabbitMQ/Kafka messaging
2. **Module 07**: Secure with JWT authentication
3. **Module 08**: Add comprehensive testing
4. Build your own microservices using these patterns!

---

**💡 Tip**: Keep all services running while testing. The magic happens when they all communicate together!
