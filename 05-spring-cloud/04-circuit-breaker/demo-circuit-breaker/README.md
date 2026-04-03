# Circuit Breaker Demo

Interactive demonstration of Resilience4j Circuit Breaker patterns with Spring Cloud.

## Overview

This demo shows how to implement resilient microservices using:
- **Circuit Breaker**: Fail fast when service is unavailable
- **Retry**: Automatically retry failed operations
- **Rate Limiter**: Throttle requests to prevent overload
- **Bulkhead**: Isolate resources to prevent cascading failures
- **Time Limiter**: Fail if operation takes too long

## Prerequisites

- Java 17+
- Maven 3.6+

## Quick Start

```bash
# Run the application
mvn spring-boot:run

# Application runs on port 8087
```

## Circuit Breaker States

```
     ┌─────────────────────────────────────────────────────────┐
     │                                                         │
     │         success    ┌──────────────┐                     │
     │      ┌────────────►│    CLOSED    │◄─────────┐          │
     │      │             │ (Normal ops) │          │          │
     │      │             └──────┬───────┘          │          │
     │      │                    │                  │          │
     │      │     failure_rate > threshold         │          │
     │      │                    │         success_rate > threshold
     │      │                    ▼                  │          │
     │      │             ┌──────────────┐          │          │
     │      │             │     OPEN     │          │          │
     │      │             │ (Fail fast)  │          │          │
     │      │             └──────┬───────┘          │          │
     │      │                    │                  │          │
     │      │        wait_duration elapsed          │          │
     │      │                    ▼                  │          │
     │      │             ┌──────────────┐          │          │
     │      └─────────────│  HALF_OPEN   │──────────┘          │
     │        failure     │(Test recovery)│                    │
     │                    └──────────────┘                     │
     │                                                         │
     └─────────────────────────────────────────────────────────┘
```

## API Endpoints

### Product Service (Circuit Breaker + Retry + Rate Limiter)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/products/{id}` | Get product (protected by CB) |
| POST | `/api/products/simulate?fail=true&failurePercentage=50` | Enable failure simulation |
| GET | `/api/products/stats` | View call statistics |
| POST | `/api/products/reset` | Reset statistics |

### Payment Service (Circuit Breaker + Retry)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/payments/{id}` | Get payment (retry + CB) |
| POST | `/api/payments/simulate?fail=true` | Enable failure simulation |

### Inventory Service (Circuit Breaker + Bulkhead + Time Limiter)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/inventory/{id}` | Check inventory (bulkhead) |
| GET | `/api/inventory/{id}/async` | Async check (time limiter) |
| POST | `/api/inventory/simulate?slow=true` | Enable slow response |

### Circuit Breaker Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/circuit-breaker/status` | View all CB states |
| POST | `/api/circuit-breaker/{name}/transition?state=OPEN` | Force state transition |
| POST | `/api/circuit-breaker/{name}/reset` | Reset circuit breaker |

### Actuator Endpoints

| Endpoint | Description |
|----------|-------------|
| `/actuator/health` | Health with CB status |
| `/actuator/circuitbreakers` | Circuit breaker details |
| `/actuator/circuitbreakerevents` | Event history |
| `/actuator/metrics/resilience4j.circuitbreaker.state` | CB metrics |

## Testing Scenarios

### 1. Normal Operation

```bash
# Get product (should return LIVE data)
curl http://localhost:8087/api/products/PROD-001

# Response:
{
  "id": "PROD-001",
  "name": "Laptop",
  "source": "LIVE"
}
```

### 2. Trigger Circuit Breaker

```bash
# Enable failure simulation (50% failure rate)
curl -X POST "http://localhost:8087/api/products/simulate?fail=true&failurePercentage=50"

# Make multiple calls to trigger circuit breaker
for i in {1..10}; do curl -s http://localhost:8087/api/products/PROD-001 | jq '.source'; done

# Check circuit breaker status
curl http://localhost:8087/api/circuit-breaker/status | jq '.productService'

# After 5+ failures, circuit opens -> fallback data returned
{
  "id": "PROD-001",
  "name": "Laptop",
  "description": "[CACHED] MacBook Pro 14",
  "source": "FALLBACK"
}
```

### 3. Watch Circuit Recovery

```bash
# Disable failure simulation
curl -X POST "http://localhost:8087/api/products/simulate?fail=false"

# Wait for waitDurationInOpenState (5s for productService)
# Circuit transitions to HALF_OPEN

# Make calls - if successful, circuit closes
curl http://localhost:8087/api/products/PROD-001

# Check status - should be CLOSED
curl http://localhost:8087/api/circuit-breaker/status | jq '.productService.state'
```

### 4. Test Retry Pattern

```bash
# Payment service has 40% base failure rate
# Retry is configured for 3 attempts
curl http://localhost:8087/api/payments/PAY-12345

# Watch logs to see retry attempts
```

### 5. Test Bulkhead (Thread Isolation)

```bash
# Enable slow responses for inventory
curl -X POST "http://localhost:8087/api/inventory/simulate?slow=true"

# Make concurrent calls (bulkhead limits to 5)
for i in {1..10}; do curl http://localhost:8087/api/inventory/PROD-001 & done
wait

# Some calls will be rejected when bulkhead is full
```

### 6. Force Circuit State

```bash
# Force circuit to OPEN
curl -X POST "http://localhost:8087/api/circuit-breaker/productService/transition?state=OPEN"

# All calls now return fallback
curl http://localhost:8087/api/products/PROD-001

# Reset circuit
curl -X POST "http://localhost:8087/api/circuit-breaker/productService/reset"
```

## Configuration Reference

Key configurations in `application.yml`:

```yaml
resilience4j:
  circuitbreaker:
    instances:
      productService:
        failureRateThreshold: 50       # Open at 50% failure
        waitDurationInOpenState: 5s    # Wait before HALF_OPEN
        slidingWindowSize: 10          # Evaluate last 10 calls
        minimumNumberOfCalls: 5        # Min calls before evaluation
        permittedNumberOfCallsInHalfOpenState: 3  # Test calls

  retry:
    instances:
      productService:
        maxAttempts: 3               # 3 retry attempts
        waitDuration: 500ms          # Wait between retries
        enableExponentialBackoff: true

  ratelimiter:
    instances:
      productService:
        limitForPeriod: 5            # 5 calls per second
        limitRefreshPeriod: 1s

  bulkhead:
    instances:
      productService:
        maxConcurrentCalls: 5        # Max 5 concurrent calls
```

## Key Concepts

### Decorator Order
```
Retry → CircuitBreaker → RateLimiter → TimeLimiter → Bulkhead → Function
```

### When to Use Each Pattern

| Pattern | Use Case |
|---------|----------|
| **Circuit Breaker** | Prevent cascading failures, fail fast |
| **Retry** | Transient failures (network blips) |
| **Rate Limiter** | Protect from overload |
| **Bulkhead** | Resource isolation, prevent thread exhaustion |
| **Time Limiter** | Prevent slow calls from blocking |

### Fallback Strategies

1. **Cache/Stale Data**: Return cached response
2. **Default Response**: Return sensible defaults
3. **Degraded Service**: Offer reduced functionality
4. **Queue for Later**: Accept request, process when healthy

## Monitoring

View circuit breaker metrics:
```bash
curl http://localhost:8087/actuator/circuitbreakers | jq

# View specific events
curl http://localhost:8087/actuator/circuitbreakerevents/productService | jq
```

## Cleanup

```bash
# Reset all simulations
curl -X POST "http://localhost:8087/api/products/simulate?fail=false"
curl -X POST "http://localhost:8087/api/payments/simulate?fail=false"
curl -X POST "http://localhost:8087/api/inventory/simulate?slow=false"

# Reset all circuit breakers
curl -X POST http://localhost:8087/api/circuit-breaker/productService/reset
curl -X POST http://localhost:8087/api/circuit-breaker/paymentService/reset
curl -X POST http://localhost:8087/api/circuit-breaker/inventoryService/reset
```

## Further Reading

- [Resilience4j Documentation](https://resilience4j.readme.io/)
- [Spring Cloud Circuit Breaker](https://docs.spring.io/spring-cloud-circuitbreaker/docs/current/reference/html/)
- [Resilience Patterns](../../../04-microservices-architecture/03-resilience/README.md)
