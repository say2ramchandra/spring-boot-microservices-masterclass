# Order Service Demo

## Overview
Demonstrates **Feign Client** for declarative inter-service communication with automatic service discovery and load balancing.

## Features Demonstrated
- ✅ Feign declarative REST client
- ✅ Service discovery via Eureka
- ✅ Automatic load balancing
- ✅ Circuit breaker integration
- ✅ Fallback responses
- ✅ Inter-service communication

## Running the Application

### Prerequisites
1. **Eureka Server** running on port 8761
2. **Product Service** running on port 8081
3. Java 17+
4. Maven 3.8+

### Steps
```bash
# Start in order:
# 1. Eureka Server
cd 05-spring-cloud/demo-eureka-server
mvn spring-boot:run

# 2. Product Service
cd 05-spring-cloud/demo-product-service
mvn spring-boot:run

# 3. Order Service
cd 05-spring-cloud/demo-order-service
mvn spring-boot:run
```

## API Endpoints

### Base URL
`http://localhost:8083/api/orders`

### Available Endpoints

#### 1. Create Order (Uses Feign Client!)
```bash
POST http://localhost:8083/api/orders
Content-Type: application/json

{
  "customerName": "John Doe",
  "customerEmail": "john@example.com",
  "items": [
    {
      "productId": 1,
      "quantity": 2
    },
    {
      "productId": 2,
      "quantity": 1
    }
  ]
}
```

**What happens internally**:
1. Order Service receives request
2. For each product ID, calls Product Service via Feign Client
3. Feign automatically discovers Product Service location from Eureka
4. Applies load balancing if multiple instances exist
5. Retrieves product details (name, price)
6. Calculates order total
7. Creates and returns order

#### 2. Get All Orders
```bash
GET http://localhost:8083/api/orders
```

#### 3. Get Order by ID
```bash
GET http://localhost:8083/api/orders/1
```

#### 4. Get Orders by Customer
```bash
GET http://localhost:8083/api/orders/customer/John%20Doe
```

#### 5. Update Order Status
```bash
PATCH http://localhost:8083/api/orders/1/status?status=SHIPPED
```

## Testing with cURL

### Create Order
```bash
curl -X POST http://localhost:8083/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "Alice Smith",
    "customerEmail": "alice@example.com",
    "items": [
      {"productId": 1, "quantity": 1},
      {"productId": 3, "quantity": 2}
    ]
  }'
```

### Get All Orders
```bash
curl http://localhost:8083/api/orders
```

### Update Order Status
```bash
curl -X PATCH "http://localhost:8083/api/orders/1/status?status=DELIVERED"
```

## Feign Client Deep Dive

### Feign Client Interface
Located in: `client/ProductClient.java`

```java
@FeignClient(
    name = "product-service",           // Service name in Eureka
    fallback = ProductClientFallback.class  // Fallback when service down
)
public interface ProductClient {
    @GetMapping("/api/products/{id}")
    Product getProductById(@PathVariable("id") Long id);
}
```

**Magic of Feign**:
- ✅ No manual HTTP client code
- ✅ Automatic service discovery
- ✅ Load balancing included
- ✅ Circuit breaker integration
- ✅ JSON serialization/deserialization
- ✅ Request/response logging

### Feign vs Manual REST Template

**Without Feign (Manual)**:
```java
RestTemplate restTemplate = new RestTemplate();
String serviceUrl = "http://localhost:8081"; // Hardcoded!
Product product = restTemplate.getForObject(
    serviceUrl + "/api/products/" + id,
    Product.class
);
```

**With Feign (Declarative)**:
```java
Product product = productClient.getProductById(id);
```

**Benefits**:
- No hardcoded URLs
- Automatic service discovery
- Cleaner code
- Built-in resilience

## Testing Circuit Breaker

### Scenario 1: Product Service Running
```bash
# Create order - should work perfectly
curl -X POST http://localhost:8083/api/orders \
  -H "Content-Type: application/json" \
  -d '{"customerName":"Bob","customerEmail":"bob@example.com","items":[{"productId":1,"quantity":1}]}'
```

**Result**: Order created with actual product details

### Scenario 2: Product Service Down
```bash
# Stop Product Service, then create order
curl -X POST http://localhost:8083/api/orders \
  -H "Content-Type: application/json" \
  -d '{"customerName":"Bob","customerEmail":"bob@example.com","items":[{"productId":1,"quantity":1}]}'
```

**Result**: Order created with fallback product data:
- Product name: "Product Temporarily Unavailable"
- Price: $0.00
- Service doesn't crash!

### Observing Fallback
Check logs when Product Service is down:
```
⚠️ Product Service unavailable. Returning fallback product for ID: 1
```

## Load Balancing Demo

Run multiple Product Service instances:

```bash
# Terminal 1: Product Service Instance 1
cd 05-spring-cloud/demo-product-service
mvn spring-boot:run

# Terminal 2: Product Service Instance 2
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8082

# Create orders - Feign will round-robin between instances!
curl -X POST http://localhost:8083/api/orders ... (repeat multiple times)
```

Check Product Service logs - you'll see requests distributed across both instances!

## Configuration Explained

### Feign Configuration (application.yml)
```yaml
feign:
  circuitbreaker:
    enabled: true              # Enable circuit breaker
  client:
    config:
      default:
        connect-timeout: 5000  # Connection timeout
        read-timeout: 5000     # Read timeout
        logger-level: FULL     # Log requests/responses
```

### Logging Feign Requests
Set log level to DEBUG to see Feign requests:
```yaml
logging:
  level:
    org.springframework.cloud.openfeign: DEBUG
```

You'll see:
```
[ProductClient#getProductById] ---> GET http://product-service/api/products/1
[ProductClient#getProductById] <--- 200 (123ms)
```

## Production Considerations

1. **Timeouts**: Configure appropriate timeouts
   ```yaml
   feign:
     client:
       config:
         product-service:
           connect-timeout: 5000
           read-timeout: 5000
   ```

2. **Retry Logic**: Add retry for transient failures
   ```yaml
   feign:
     client:
       config:
         default:
           retryer: feign.Retryer.Default
   ```

3. **Request/Response Compression**:
   ```yaml
   feign:
     compression:
       request:
         enabled: true
       response:
         enabled: true
   ```

4. **Authentication**: Add interceptors for auth headers
5. **Error Handling**: Implement custom error decoders
6. **Metrics**: Monitor Feign client metrics

## Common Issues

**Feign Client not working**:
- Ensure `@EnableFeignClients` is present
- Check service name matches Eureka registration
- Verify Product Service is registered in Eureka

**Circuit breaker not triggering**:
- Enable circuit breaker: `feign.circuitbreaker.enabled=true`
- Ensure fallback class is annotated with `@Component`

**Timeout errors**:
- Increase connect-timeout and read-timeout
- Check network latency between services

## Next Steps
1. Run through API Gateway to see full routing
2. Test with multiple Product Service instances
3. Implement custom Feign interceptor for authentication
4. Add request/response logging interceptor
5. Configure retry logic
