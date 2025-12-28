# API Gateway Demo

## Overview
Central entry point for all microservices using **Spring Cloud Gateway**.

## Features Demonstrated
- ✅ Dynamic routing based on service discovery
- ✅ Load balancing with Eureka integration
- ✅ Circuit breaker for fault tolerance
- ✅ Custom request/response filters
- ✅ Fallback responses
- ✅ Request logging

## Running the Application

### Prerequisites
1. **Eureka Server** running on port 8761
2. **Product Service** running on port 8081
3. **Order Service** running on port 8083 (optional)
4. Java 17+
5. Maven 3.8+

### Steps
```bash
# Start in order:
# 1. Eureka Server
cd 05-spring-cloud/demo-eureka-server
mvn spring-boot:run

# 2. Product Service
cd 05-spring-cloud/demo-product-service
mvn spring-boot:run

# 3. API Gateway
cd 05-spring-cloud/demo-api-gateway
mvn spring-boot:run
```

## Accessing Services Through Gateway

### Base URL
`http://localhost:8080`

### Route Configuration

#### Product Service Routes
```bash
# Original: http://localhost:8081/api/products
# Through Gateway: http://localhost:8080/api/products

GET http://localhost:8080/api/products
GET http://localhost:8080/api/products/1
GET http://localhost:8080/api/products/category/Electronics
```

#### Order Service Routes
```bash
# Original: http://localhost:8083/api/orders
# Through Gateway: http://localhost:8080/api/orders

GET http://localhost:8080/api/orders
POST http://localhost:8080/api/orders
```

## Testing Gateway Features

### 1. Service Discovery & Load Balancing
```bash
# Access product service through gateway
curl http://localhost:8080/api/products

# Gateway automatically discovers service location via Eureka
# No hardcoded URLs needed!
```

### 2. Request Filtering
Every request through gateway adds custom header:
- Header: `X-Gateway-Request: API-Gateway`

Check logs to see the custom logging filter in action.

### 3. Circuit Breaker
```bash
# Stop Product Service to test circuit breaker
# Then try:
curl http://localhost:8080/api/products

# Response will be fallback message:
{
  "message": "Product Service is temporarily unavailable",
  "status": "SERVICE_UNAVAILABLE",
  "recommendation": "Please try again later"
}
```

### 4. Gateway Actuator Endpoints
```bash
# View all configured routes
curl http://localhost:8080/actuator/gateway/routes

# Gateway health
curl http://localhost:8080/actuator/health

# Gateway metrics
curl http://localhost:8080/actuator/gateway/routes/product-service
```

## Route Configuration Explained

```yaml
routes:
  - id: product-service              # Unique route ID
    uri: lb://product-service        # Load-balanced URI (lb = LoadBalancer)
    predicates:
      - Path=/api/products/**        # Match incoming requests
    filters:
      - CircuitBreaker               # Add circuit breaker
      - AddRequestHeader             # Add custom header
```

**Key Points**:
- `lb://` prefix enables client-side load balancing
- Service name must match the one registered in Eureka
- Predicates determine which requests match this route
- Filters modify requests/responses

## Custom Filters

### Global Logging Filter
Located in: `filter/LoggingFilter.java`

Logs every request and response:
```
📥 Incoming Request: GET /api/products
📤 Response Status: 200 for GET /api/products
```

## Load Balancing Demo

Run multiple instances of Product Service:

```bash
# Terminal 1: Product Service Instance 1
cd 05-spring-cloud/demo-product-service
mvn spring-boot:run

# Terminal 2: Product Service Instance 2
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8082

# Now make requests through gateway
curl http://localhost:8080/api/products/info

# Gateway will round-robin between instances!
```

## Circuit Breaker States

### CLOSED (Normal)
- All requests pass through
- Failures are counted

### OPEN (Service Down)
- Requests fail fast
- Fallback response returned
- After 10 seconds, transitions to HALF_OPEN

### HALF_OPEN (Testing)
- Limited requests allowed
- If successful, returns to CLOSED
- If failures continue, returns to OPEN

## Monitoring Gateway

### View All Routes
```bash
curl http://localhost:8080/actuator/gateway/routes | json_pp
```

### Refresh Routes Dynamically
```bash
curl -X POST http://localhost:8080/actuator/gateway/refresh
```

### Check Gateway Health
```bash
curl http://localhost:8080/actuator/health
```

## Production Considerations

1. **Security**: Add authentication/authorization filters
2. **Rate Limiting**: Implement request rate limiting
3. **CORS**: Configure Cross-Origin Resource Sharing
4. **Timeouts**: Set appropriate connection and response timeouts
5. **Retry Logic**: Add retry filters for transient failures
6. **Monitoring**: Integrate with Prometheus/Grafana
7. **SSL/TLS**: Enable HTTPS for secure communication

## Common Issues

**Gateway can't find services**:
- Ensure Eureka Server is running
- Check service registration in Eureka Dashboard
- Verify `eureka.client.service-url.defaultZone`

**Route not working**:
- Check path predicates match your URL pattern
- View routes: `http://localhost:8080/actuator/gateway/routes`
- Check logs for routing decisions

**Circuit breaker not triggering**:
- Verify resilience4j dependency
- Check circuit breaker configuration
- Ensure fallback endpoint exists

## Next Steps
1. Add authentication filter
2. Implement rate limiting
3. Add request/response transformation
4. Configure CORS
5. Set up distributed tracing
