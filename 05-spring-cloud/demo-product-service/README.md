# Product Service Demo

## Overview
A microservice that manages product catalog. It demonstrates:
- ✅ Service registration with Eureka
- ✅ REST API endpoints
- ✅ Circuit breaker pattern with Resilience4j
- ✅ Health checks with Actuator

## Running the Application

### Prerequisites
1. **Eureka Server must be running** on port 8761
2. Java 17+
3. Maven 3.8+

### Steps
```bash
# First, start Eureka Server (in another terminal)
cd 05-spring-cloud/demo-eureka-server
mvn spring-boot:run

# Then start Product Service
cd 05-spring-cloud/demo-product-service
mvn spring-boot:run
```

## API Endpoints

### Base URL
`http://localhost:8081/api/products`

### Available Endpoints

#### 1. Get All Products
```bash
GET http://localhost:8081/api/products
```

#### 2. Get Product by ID
```bash
GET http://localhost:8081/api/products/1
```

#### 3. Get Products by Category
```bash
GET http://localhost:8081/api/products/category/Electronics
```

#### 4. Create New Product
```bash
POST http://localhost:8081/api/products
Content-Type: application/json

{
  "name": "Tablet",
  "description": "10-inch tablet",
  "price": 399.99,
  "stockQuantity": 40,
  "category": "Electronics"
}
```

#### 5. Update Stock (with Circuit Breaker)
```bash
PUT http://localhost:8081/api/products/1/stock?quantity=75
```

#### 6. Service Info
```bash
GET http://localhost:8081/api/products/info
```

## Testing with cURL

```bash
# Get all products
curl http://localhost:8081/api/products

# Get product by ID
curl http://localhost:8081/api/products/1

# Create product
curl -X POST http://localhost:8081/api/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Mouse","description":"Wireless mouse","price":29.99,"stockQuantity":100,"category":"Electronics"}'

# Update stock
curl -X PUT "http://localhost:8081/api/products/1/stock?quantity=60"
```

## Observing Service Registration

1. **Start Eureka Server** (http://localhost:8761)
2. **Start Product Service**
3. **Open Eureka Dashboard** - You'll see `PRODUCT-SERVICE` registered
4. **Check Health**: http://localhost:8081/actuator/health

## Circuit Breaker Demo

The `updateStock` endpoint uses Resilience4j circuit breaker:

**Configuration**:
- Sliding window: 10 calls
- Failure threshold: 50%
- Wait duration in open state: 10 seconds

**Testing Circuit Breaker**:
1. Make successful calls to update stock
2. Simulate failures (you can modify code to throw exceptions)
3. Observe fallback method returning cached data
4. Check circuit breaker metrics

## Sample Data

The service initializes with 5 products:
1. Laptop - $999.99 (Electronics)
2. Smartphone - $699.99 (Electronics)
3. Headphones - $199.99 (Electronics)
4. Coffee Maker - $89.99 (Home Appliances)
5. Desk Chair - $249.99 (Furniture)

## Key Features Demonstrated

### 1. Service Discovery
- Automatic registration with Eureka
- Instance metadata (IP, port, health)
- Lease renewal every 30 seconds

### 2. Circuit Breaker
- Protects against cascading failures
- Fallback responses
- Automatic recovery (half-open state)

### 3. Health Monitoring
- Actuator endpoints
- Custom health indicators
- Integration with Eureka

## Next Steps
1. Run demo-order-service to see inter-service communication
2. Run demo-api-gateway to route through gateway
3. Test load balancing with multiple instances:
   ```bash
   # Run on different port
   mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8082
   ```

## Troubleshooting

**Not registering with Eureka?**
- Ensure Eureka Server is running on port 8761
- Check `eureka.client.service-url.defaultZone` configuration
- Verify network connectivity

**Port already in use?**
- Change port: `mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8082`
- Or stop the process using port 8081

**Circuit breaker not working?**
- Check Resilience4j dependency
- Verify `@CircuitBreaker` annotation
- Review configuration in application.yml
