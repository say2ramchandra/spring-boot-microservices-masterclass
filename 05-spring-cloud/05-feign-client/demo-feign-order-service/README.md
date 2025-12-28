# Feign Client Demo - Order Service

This demo demonstrates **OpenFeign** for making declarative REST calls to Product Service.

## Overview

The Order Service uses **OpenFeign** to communicate with Product Service. It showcases:
- Declarative REST clients
- Service discovery and load balancing
- Circuit breaker with fallbacks
- Error handling
- Retry logic

## Architecture

```
┌────────────────────────────────────────────────────────┐
│             Order Service (8083)                        │
│                                                         │
│  ┌─────────────────────────────────────────┐          │
│  │   @FeignClient(name="product-service")  │          │
│  │   ProductClient interface               │          │
│  └─────────────────────────────────────────┘          │
│                     ↓                                   │
│       Feign generates implementation                    │
│                     ↓                                   │
│  ┌─────────────────────────────────────────┐          │
│  │  - Service discovery via Eureka          │          │
│  │  - Load balancing                        │          │
│  │  - Circuit breaker                       │          │
│  │  - Retry logic                           │          │
│  └─────────────────────────────────────────┘          │
└────────────────────────────────────────────────────────┘
                         ↓
              HTTP REST Call (Feign)
                         ↓
┌────────────────────────────────────────────────────────┐
│           Product Service (8082)                        │
│                                                         │
│  REST API:                                             │
│   GET  /api/products/{id}                              │
│   GET  /api/products                                   │
│   GET  /api/products/search?name=iPhone                │
│   GET  /api/products/{id}/availability?quantity=5      │
│   PUT  /api/products/{id}/stock?quantity=10            │
└────────────────────────────────────────────────────────┘
```

## Features Demonstrated

### 1. Declarative REST Client

**Interface definition:**
```java
@FeignClient(name = "product-service", fallback = ProductClientFallback.class)
public interface ProductClient {
    
    @GetMapping("/api/products/{id}")
    ProductDTO getProductById(@PathVariable("id") Long id);
    
    @GetMapping("/api/products")
    List<ProductDTO> getAllProducts();
}
```

**Usage:**
```java
@Autowired
private ProductClient productClient;

ProductDTO product = productClient.getProductById(1L);
```

### 2. Fallback Pattern

When Product Service is unavailable:
```java
@Component
public class ProductClientFallback implements ProductClient {
    
    @Override
    public ProductDTO getProductById(Long id) {
        // Return default product
        return new ProductDTO(id, "Product Unavailable", 0.0);
    }
}
```

### 3. Circuit Breaker

```yaml
resilience4j:
  circuitbreaker:
    instances:
      product-service:
        sliding-window-size: 10
        failure-rate-threshold: 50
        wait-duration-in-open-state: 10s
```

### 4. Request/Response Examples

**GET with Path Variable:**
```java
@GetMapping("/api/products/{id}")
ProductDTO getProductById(@PathVariable("id") Long id);
```

**GET with Query Parameter:**
```java
@GetMapping("/api/products/search")
List<ProductDTO> searchProducts(@RequestParam("name") String name);
```

**PUT with Path Variable and Query Parameter:**
```java
@PutMapping("/api/products/{id}/stock")
void updateStock(@PathVariable("id") Long id, @RequestParam("quantity") Integer quantity);
```

## Prerequisites

1. **Java 17+**
2. **Maven 3.6+**
3. **Product Service running on port 8082** (use existing demo-product-service)

## Running the Demo

### Step 1: Start Product Service

Product Service should already exist in Module 05. If not, create a simple REST API or use:

```bash
cd ../../demo-product-service
mvn spring-boot:run
```

### Step 2: Start Order Service

```bash
cd demo-feign-order-service
mvn clean package
java -jar target/demo-feign-order-service-1.0.0.jar

# Or use Maven
mvn spring-boot:run
```

**Order Service will start on port 8083**

### Step 3: Test the APIs

**Create Order:**
```bash
curl -X POST http://localhost:8083/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "quantity": 2,
    "customerName": "John Doe",
    "customerEmail": "john@example.com"
  }'
```

**Get All Orders:**
```bash
curl http://localhost:8083/api/orders
```

**Get Order by ID:**
```bash
curl http://localhost:8083/api/orders/1
```

**Get Orders by Customer:**
```bash
curl http://localhost:8083/api/orders/customer?email=john@example.com
```

**Cancel Order:**
```bash
curl -X PUT http://localhost:8083/api/orders/1/cancel
```

## Testing Feign Features

### Test 1: Normal Operation

1. **Ensure Product Service is running**
2. **Create an order:**
```bash
curl -X POST http://localhost:8083/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "quantity": 2,
    "customerName": "Alice",
    "customerEmail": "alice@example.com"
  }'
```

**Expected:** Order created successfully

### Test 2: Fallback Behavior

1. **Stop Product Service** (simulate service down)
2. **Try to create an order:**
```bash
curl -X POST http://localhost:8083/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "quantity": 2,
    "customerName": "Bob",
    "customerEmail": "bob@example.com"
  }'
```

**Expected:** Fallback triggered, error message about Product Service unavailable

**Check logs:**
```
WARN  ProductClientFallback - Fallback triggered for getProductById(1)
```

### Test 3: Circuit Breaker

1. **Stop Product Service**
2. **Make multiple requests (11+) to trigger circuit breaker:**
```bash
for i in {1..15}; do
  curl -X POST http://localhost:8083/api/orders \
    -H "Content-Type: application/json" \
    -d '{
      "productId": 1,
      "quantity": 1,
      "customerName": "Test",
      "customerEmail": "test@example.com"
    }'
done
```

3. **Check circuit breaker status:**
```bash
curl http://localhost:8083/actuator/circuitbreakers
```

**Expected:** Circuit breaker in OPEN state

4. **Start Product Service**
5. **Wait 10 seconds** (wait-duration-in-open-state)
6. **Circuit breaker transitions to HALF_OPEN**
7. **After successful calls, transitions to CLOSED**

### Test 4: Insufficient Stock

1. **Ensure Product Service is running**
2. **Create order with large quantity:**
```bash
curl -X POST http://localhost:8083/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "quantity": 1000,
    "customerName": "Charlie",
    "customerEmail": "charlie@example.com"
  }'
```

**Expected:** 400 Bad Request - Insufficient stock

### Test 5: Product Not Found

```bash
curl -X POST http://localhost:8083/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 999,
    "quantity": 1,
    "customerName": "Dave",
    "customerEmail": "dave@example.com"
  }'
```

**Expected:** 404 Not Found - Product not found

## API Endpoints

### Order Service

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/orders` | POST | Create new order |
| `/api/orders` | GET | Get all orders |
| `/api/orders/{id}` | GET | Get order by ID |
| `/api/orders/customer?email={email}` | GET | Get orders by customer |
| `/api/orders/status?status={status}` | GET | Get orders by status |
| `/api/orders/{id}/cancel` | PUT | Cancel order |

### Required Product Service Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/products/{id}` | GET | Get product by ID |
| `/api/products` | GET | Get all products |
| `/api/products/search?name={name}` | GET | Search products |
| `/api/products/{id}/availability?quantity={quantity}` | GET | Check availability |
| `/api/products/{id}/stock?quantity={quantity}` | PUT | Update stock |

## Feign Client Configuration

### application.yml

```yaml
feign:
  client:
    config:
      default:
        connectTimeout: 5000
        readTimeout: 5000
        loggerLevel: full
      
      product-service:
        connectTimeout: 3000
        readTimeout: 10000
  
  circuitbreaker:
    enabled: true

resilience4j:
  circuitbreaker:
    instances:
      product-service:
        sliding-window-size: 10
        failure-rate-threshold: 50
        wait-duration-in-open-state: 10s
```

### Configuration Explained

**connectTimeout:** Time to establish connection (3000ms = 3 seconds)
**readTimeout:** Time to wait for response (10000ms = 10 seconds)
**loggerLevel:** NONE, BASIC, HEADERS, FULL

**Circuit Breaker:**
- **sliding-window-size:** Last 10 calls tracked
- **failure-rate-threshold:** 50% failure rate opens circuit
- **wait-duration-in-open-state:** Wait 10s before trying HALF_OPEN

## Monitoring

### Health Check

```bash
curl http://localhost:8083/actuator/health
```

### Circuit Breaker Status

```bash
curl http://localhost:8083/actuator/circuitbreakers
```

### Circuit Breaker Events

```bash
curl http://localhost:8083/actuator/circuitbreakerevents
curl http://localhost:8083/actuator/circuitbreakerevents/product-service
```

### Metrics

```bash
curl http://localhost:8083/actuator/metrics
curl http://localhost:8083/actuator/metrics/resilience4j.circuitbreaker.calls
```

## Database

**H2 Console:** http://localhost:8083/h2-console

**Connection Details:**
- JDBC URL: `jdbc:h2:mem:orderdb`
- Username: `sa`
- Password: _(empty)_

**Tables:**
- `orders` - Order records

## Error Handling

### Exception Types

**ProductNotFoundException (404):**
```json
{
  "message": "Product not found: 999",
  "errorCode": "PRODUCT_NOT_FOUND",
  "status": 404,
  "timestamp": "2025-12-19T10:30:00"
}
```

**InsufficientStockException (400):**
```json
{
  "message": "Insufficient stock for product: iPhone",
  "errorCode": "INSUFFICIENT_STOCK",
  "status": 400,
  "timestamp": "2025-12-19T10:30:00"
}
```

**FeignException (503):**
```json
{
  "message": "Service communication error: ...",
  "errorCode": "SERVICE_ERROR",
  "status": 503,
  "timestamp": "2025-12-19T10:30:00"
}
```

**ValidationException (400):**
```json
{
  "message": "Validation failed",
  "errorCode": "VALIDATION_ERROR",
  "status": 400,
  "timestamp": "2025-12-19T10:30:00",
  "fieldErrors": {
    "quantity": "Quantity must be at least 1",
    "customerEmail": "Invalid email format"
  }
}
```

## Troubleshooting

### Issue: Connection refused

**Error:** `java.net.ConnectException: Connection refused`

**Solution:**
- Ensure Product Service is running on port 8082
- Check firewall settings

### Issue: Fallback always triggered

**Problem:** Every call uses fallback even when service is up

**Solution:**
- Check Product Service URL in configuration
- Verify Product Service endpoints match Feign client
- Check network connectivity

### Issue: Circuit breaker stuck OPEN

**Problem:** Circuit breaker won't transition to CLOSED

**Solution:**
- Wait for `wait-duration-in-open-state` (10 seconds)
- Ensure Product Service is healthy
- Check circuit breaker metrics:
```bash
curl http://localhost:8083/actuator/circuitbreakerevents/product-service
```

## Production Considerations

### 1. Use Service Discovery

```yaml
eureka:
  client:
    register-with-eureka: true
    fetch-registry: true

feign:
  client:
    config:
      product-service:
        url: lb://product-service  # Load balanced via Eureka
```

### 2. Configure Timeouts Appropriately

```yaml
feign:
  client:
    config:
      fast-service:
        connectTimeout: 2000
        readTimeout: 5000
      
      slow-service:
        connectTimeout: 5000
        readTimeout: 30000
```

### 3. Implement Comprehensive Fallbacks

```java
@Component
public class ProductClientFallback implements ProductClient {
    
    @Autowired
    private ProductCache cache;  // Cache for fallback data
    
    @Override
    public ProductDTO getProductById(Long id) {
        // Try cache first
        return cache.get(id).orElse(getDefaultProduct(id));
    }
}
```

### 4. Add Request Interceptors

```java
@Configuration
public class FeignConfig {
    
    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            // Add authentication
            template.header("Authorization", "Bearer " + getToken());
            
            // Add correlation ID
            template.header("X-Correlation-ID", MDC.get("correlationId"));
        };
    }
}
```

### 5. Monitor Metrics

```yaml
management:
  metrics:
    export:
      prometheus:
        enabled: true
```

## Next Steps

1. **Integrate with Eureka** for service discovery
2. **Add API Gateway** to route requests
3. **Implement authentication** with JWT
4. **Add distributed tracing** (Spring Cloud Sleuth + Zipkin)
5. **Set up monitoring** (Prometheus + Grafana)

## References

- [OpenFeign Documentation](https://github.com/OpenFeign/feign)
- [Spring Cloud OpenFeign](https://spring.io/projects/spring-cloud-openfeign)
- [Resilience4j Integration](https://resilience4j.readme.io/docs/feign)
- [Section 05 README](../README.md) - Comprehensive Feign guide
