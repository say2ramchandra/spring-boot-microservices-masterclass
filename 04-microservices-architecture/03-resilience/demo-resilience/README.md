# Resilience Patterns Demo with Resilience4j

Demonstrates **Circuit Breaker**, **Retry**, **Timeout**, and **Bulkhead** patterns using Resilience4j.

## Architecture

```
┌────────────────┐         ┌─────────────────┐
│  Order Service │ ──────→ │ External Service│
│  Port: 8095    │ Resilience4j Patterns:
└────────────────┘ • Circuit Breaker
                   • Retry
                   • Timeout
                   • Bulkhead
```

## Features Demonstrated

1. **Circuit Breaker**: Prevents cascading failures
2. **Retry**: Automatic retry with exponential backoff
3. **Timeout**: Fail fast on slow operations
4. **Bulkhead**: Resource isolation
5. **Fallback**: Graceful degradation
6. **Metrics**: Actuator endpoints for monitoring

## Running the Demo

```bash
cd resilience-demo
mvn spring-boot:run
```

## Testing Scenarios

### 1. Circuit Breaker Demo

**Trigger Circuit Breaker (cause failures)**:
```bash
# Cause 10 failures to open circuit breaker
for i in {1..10}; do
  curl http://localhost:8095/api/orders/circuit-breaker-test
done
```

**Check Circuit Breaker State**:
```bash
curl http://localhost:8095/actuator/circuitbreakers
```

### 2. Retry Demo

**Test Retry with Transient Failures**:
```bash
curl http://localhost:8095/api/orders/retry-test
```

Watch logs to see retry attempts.

### 3. Timeout Demo

**Test Timeout (slow operation)**:
```bash
curl http://localhost:8095/api/orders/timeout-test
```

### 4. Bulkhead Demo

**Test Concurrent Request Limiting**:
```bash
# Send multiple concurrent requests
for i in {1..20}; do
  curl http://localhost:8095/api/orders/bulkhead-test &
done
wait
```

### 5. Combined Patterns

**Test All Patterns Together**:
```bash
curl -X POST http://localhost:8095/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "productId": 100,
    "quantity": 5
  }'
```

## Monitoring

### Health Check
```bash
curl http://localhost:8095/actuator/health
```

### Circuit Breaker Metrics
```bash
curl http://localhost:8095/actuator/metrics/resilience4j.circuitbreaker.calls
```

### Retry Metrics
```bash
curl http://localhost:8095/actuator/metrics/resilience4j.retry.calls
```

## Configuration

All resilience patterns are configured in `application.yml`:
- Failure thresholds
- Retry attempts
- Timeout durations
- Bulkhead limits

Check the configuration file for customization options.
