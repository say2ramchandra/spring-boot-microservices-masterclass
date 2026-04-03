# Demo: Distributed Tracing with Zipkin

> **Trace requests across multiple microservices**

## 🎯 What This Demo Teaches

This demo demonstrates distributed tracing:

✅ **Spring Cloud Sleuth** - Automatic tracing instrumentation  
✅ **Zipkin** - Distributed tracing visualization  
✅ **Trace IDs** - Track requests across services  
✅ **Span IDs** - Track individual operations  
✅ **Service Dependencies** - Visualize service interactions  
✅ **Performance Analysis** - Identify bottlenecks  
✅ **Custom Spans** - Add business context  

---

## 🏗️ Architecture

```
Client Request
      │
      ↓
┌──────────────┐      HTTP       ┌──────────────┐
│  Service A   │ ───────────────→│  Service B   │
│  (Port 8087) │                 │  (Port 8088) │
└──────────────┘                 └──────────────┘
      │                                 │
      │                                 │
      └─────────────┬───────────────────┘
                    ↓
            ┌──────────────┐
            │    Zipkin    │
            │  (Port 9411) │
            └──────────────┘
```

**Flow**:
1. Client calls Service A
2. Service A calls Service B
3. Both services send traces to Zipkin
4. Zipkin shows complete request flow

---

## 📂 Project Structure

```
demo-distributed-tracing/
├── service-a/
│   ├── src/main/java/.../
│   ├── src/main/resources/
│   ├── pom.xml
│   └── README.md
├── service-b/
│   ├── src/main/java/.../
│   ├── src/main/resources/
│   ├── pom.xml
│   └── README.md
├── docker-compose.yml
└── README.md
```

---

## 🚀 Quick Start

### Step 1: Start Zipkin

```bash
cd 09-observability/demo-distributed-tracing
docker-compose up -d
```

Zipkin UI: **http://localhost:9411**

### Step 2: Start Service B

```bash
cd service-b
mvn spring-boot:run
```

Service B runs on **http://localhost:8088**

### Step 3: Start Service A

```bash
# Open new terminal
cd service-a
mvn spring-boot:run
```

Service A runs on **http://localhost:8087**

---

## 🧪 Test Distributed Tracing

### 1. Simple Request

```bash
curl http://localhost:8087/api/hello
```

**Response**:
```json
{
  "message": "Hello from Service A",
  "serviceB_response": "Hello from Service B",
  "traceId": "abc123..."
}
```

### 2. User Info Request (Calls Service B)

```bash
curl http://localhost:8087/api/user/john
```

### 3. Slow Request (Demonstrates Performance Analysis)

```bash
curl http://localhost:8087/api/slow
```

### 4. Error Request (Demonstrates Error Tracing)

```bash
curl http://localhost:8087/api/error
```

### 5. View Traces in Zipkin

1. Go to **http://localhost:9411**
2. Click **Run Query** (or wait for auto-refresh)
3. Click on a trace to see details
4. See the complete request flow across services

---

## 📊 Trace Visualization

### What You'll See in Zipkin:

```
Trace: abc123-def456-ghi789
Total Duration: 245ms

service-a: GET /api/user/john (245ms)
  │
  ├─ service-a: processRequest (150ms)
  │
  └─ service-b: GET /api/user-details (80ms)
       │
       └─ service-b: fetchUserFromDB (30ms)
```

### Trace Details:

- **Trace ID**: Unique ID for entire request (e.g., `abc123...`)
- **Span ID**: Unique ID for each operation (e.g., `def456...`)
- **Service Name**: Which service handled the span
- **Duration**: Time taken for the operation
- **Tags**: Metadata (HTTP method, status, error info)
- **Annotations**: Events within a span

---

## 🔍 Understanding Traces

### Trace Components

1. **Trace**: Complete journey of a request
2. **Span**: A single operation within a trace
3. **Parent-Child**: Spans form a tree structure

### Example Trace:

```
[Service A: /api/order] ────────────────────────────────┐ (500ms)
  │                                                       │
  ├─ [Service A: validate-order] ──────┐ (50ms)        │
  │                                      │               │
  ├─ [Service B: GET /inventory] ──────┼──────┐ (150ms)│
  │                                      │      │        │
  └─ [Service C: POST /payment] ───────┼──────┼────┐   │
                                        │      │    │   │
                                        └──────┴────┴───┘
```

### Log Output with Tracing:

```
[service-a,abc123,def456,true] Processing request
[service-a,abc123,ghi789,true] Calling service B
[service-b,abc123,jkl012,true] Received request from service A
[service-b,abc123,jkl012,true] Fetching user details
```

Format: `[application-name, trace-id, span-id, exportable]`

---

## 🎨 Custom Spans and Tags

Service A demonstrates adding custom spans:

```java
// Create custom span
Span span = tracer.nextSpan().name("custom-operation");

try (Tracer.SpanInScope ws = tracer.withSpan(span.start())) {
    // Add tags for context
    span.tag("user.id", "12345");
    span.tag("order.total", "99.99");
    span.tag("custom.key", "value");
    
    // Your business logic
    doWork();
    
    // Add event/annotation
    span.event("validation.completed");
    
} catch (Exception e) {
    span.tag("error", e.getMessage());
    throw e;
} finally {
    span.end();
}
```

---

## 📊 Use Cases for Distributed Tracing

### 1. Performance Optimization

**Question**: Why is `/api/checkout` slow?

**Trace Shows**:
```
checkout (2s total)
  ├─ validate-user (50ms)
  ├─ check-inventory (1.8s) ← BOTTLENECK!
  ├─ process-payment (100ms)
  └─ send-email (50ms)
```

**Action**: Optimize `check-inventory` service

### 2. Error Investigation

**Question**: Why did order #12345 fail?

**Trace Shows**:
```
create-order (FAILED)
  ├─ validate-order (OK)
  ├─ deduct-inventory (OK)
  └─ charge-payment (ERROR: Insufficient funds)
```

**Action**: Handle payment errors gracefully

### 3. Service Dependencies

**Question**: Which services does checkout depend on?

**Trace Shows**:
```
checkout
  ├─ user-service
  ├─ inventory-service
  ├─ payment-service
  ├─ email-service
  └─ analytics-service
```

**Action**: Identify critical dependencies

### 4. Latency Analysis

View percentiles of request duration:
- p50 (median): 200ms
- p95: 500ms
- p99: 2s ← Why?

---

## 🔔 Best Practices

### ✅ DO:

1. **Sample Appropriately**
   ```yaml
   spring:
     sleuth:
       sampler:
         probability: 0.1  # Sample 10% in production
   ```

2. **Add Business Context**
   ```java
   span.tag("userId", userId);
   span.tag("orderAmount", amount);
   span.tag("paymentMethod", method);
   ```

3. **Tag Errors**
   ```java
   span.tag("error", "true");
   span.tag("error.message", exception.getMessage());
   ```

4. **Use Meaningful Span Names**
   ```java
   // Good
   span.name("payment-processing");
   
   // Bad
   span.name("process");
   ```

5. **Propagate Trace Context**
   - Sleuth does this automatically for:
     - HTTP (RestTemplate, WebClient)
     - Messaging (Kafka, RabbitMQ)
     - Async operations

### ❌ DON'T:

1. **Sample 100% in Production**
   - High overhead
   - Large storage requirements

2. **Add Sensitive Data to Tags**
   ```java
   // NEVER do this
   span.tag("password", password);
   span.tag("credit_card", card);
   ```

3. **Create Too Many Custom Spans**
   - Each span has overhead
   - Use wisely

4. **Forget to End Spans**
   ```java
   // Always use try-finally
   Span span = tracer.nextSpan().start();
   try {
       // work
   } finally {
       span.end(); // Important!
   }
   ```

---

## 🔍 Zipkin Features

### 1. Search Traces

- By service name
- By annotation
- By tag value
- By duration
- By time range

### 2. Service Dependencies

Click **Dependencies** tab to see:
```
┌──────────┐       ┌──────────┐       ┌──────────┐
│Service A │ ────→ │Service B │ ────→ │Service C │
└──────────┘       └──────────┘       └──────────┘
     │                                      ↑
     └──────────────────────────────────────┘
```

### 3. Analyze Performance

- View duration histogram
- Identify slow services
- Compare traces

---

## 🛠️ Configuration

### application.yml (Both Services)

```yaml
spring:
  application:
    name: service-a  # or service-b
  
  sleuth:
    sampler:
      probability: 1.0  # Sample 100% for demo
    
  zipkin:
    base-url: http://localhost:9411
    enabled: true
```

### Sampling Strategies

```yaml
# Sample everything (dev/demo only)
probability: 1.0

# Sample 10% (production)
probability: 0.1

# Sample 1% (high-traffic production)
probability: 0.01
```

---

## 📊 Metrics from Traces

Zipkin can derive metrics:

1. **Request Rate**: Traces per second
2. **Error Rate**: Failed traces percentage
3. **Duration**: p50, p95, p99 latencies
4. **Service Dependencies**: Call counts

---

## 🎓 Key Takeaways

1. **Trace ID** links all operations for a single request
2. **Spans** represent individual operations
3. **Tags** add business context
4. **Sampling** reduces overhead in production
5. **Zipkin** provides powerful visualization
6. **Spring Cloud Sleuth** handles instrumentation automatically

---

## 🔧 Troubleshooting

### Traces Not Showing in Zipkin

1. **Check Zipkin is running**:
   ```bash
   curl http://localhost:9411/api/v2/services
   ```

2. **Check application can reach Zipkin**:
   ```bash
   curl http://localhost:9411/api/v2/spans -X POST
   ```

3. **Check logs for errors**:
   ```bash
   # Look for Zipkin-related errors
   grep -i zipkin logs/application.log
   ```

4. **Verify configuration**:
   ```yaml
   spring:
     zipkin:
       enabled: true
       base-url: http://localhost:9411
   ```

### Service B Not Receiving Trace Context

1. **Check RestTemplate/WebClient** is configured:
   ```java
   // Spring Boot auto-configures this
   @Bean
   public RestTemplate restTemplate() {
       return new RestTemplate();
   }
   ```

2. **Verify headers** are propagated:
   - `X-B3-TraceId`
   - `X-B3-SpanId`
   - `X-B3-Sampled`

---

## 📚 Next Steps

1. ✅ Run both services and Zipkin
2. ✅ Make requests and view traces
3. ✅ Analyze slow requests
4. ✅ View service dependencies
5. ✅ Add custom spans and tags

Then explore:
- Combine with **logging** for complete observability
- Add **metrics** for quantitative analysis  
- Use all three pillars together!

---

## 🛑 Stop the Demo

```bash
# Stop Service A
Ctrl+C

# Stop Service B  
Ctrl+C

# Stop Zipkin
docker-compose down
```

---

**Happy Tracing! 🔗✨**
