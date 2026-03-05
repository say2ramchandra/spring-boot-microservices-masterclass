# Module 09: Observability & Monitoring

> **Monitor, trace, and debug microservices in production**

## 📚 Module Overview

Learn how to make your microservices **observable** through comprehensive logging, metrics collection, distributed tracing, and monitoring. Essential for maintaining healthy production systems.

---

## 🎯 Learning Objectives

By the end of this module, you will:

- ✅ Implement structured logging with Logback and SLF4J
- ✅ Collect and expose metrics with Micrometer
- ✅ Set up Prometheus for metrics scraping
- ✅ Create dashboards with Grafana
- ✅ Implement distributed tracing with Zipkin/Jaeger
- ✅ Configure health checks and readiness probes
- ✅ Monitor microservices in production
- ✅ Set up alerts and notifications

---

## 📂 Module Structure

```
09-observability/
├── README.md                           # This file
├── DEMOS-README.md                     # How to run all demos
├── demo-logging/                       # Structured logging demo
│   ├── pom.xml
│   ├── src/
│   └── README.md
├── demo-metrics-prometheus/            # Metrics with Prometheus
│   ├── pom.xml
│   ├── docker-compose.yml
│   ├── src/
│   └── README.md
└── demo-distributed-tracing/           # Distributed tracing
    ├── pom.xml
    ├── docker-compose.yml
    ├── service-a/
    ├── service-b/
    └── README.md
```

---

## 🔍 The Three Pillars of Observability

### 1. **Logs** 📝
**What happened?** - Discrete events in your application

### 2. **Metrics** 📊
**How much/many?** - Numerical measurements over time

### 3. **Traces** 🔗
**Where is the time spent?** - Request flow across services

```
┌─────────────────────────────────────────────────────┐
│                  Observability                      │
├─────────────────┬────────────────┬──────────────────┤
│      LOGS       │    METRICS     │     TRACES       │
├─────────────────┼────────────────┼──────────────────┤
│ "Order failed"  │ CPU: 75%       │ API → Service A  │
│ "User login"    │ Memory: 2GB    │  ↓               │
│ "Payment done"  │ Requests: 1000 │ Service A → DB   │
│ ERROR/WARN/INFO │ Latency: 200ms │  ↓               │
│                 │                │ Service A → B    │
└─────────────────┴────────────────┴──────────────────┘
```

---

## 📝 Part 1: Logging

### Why Logging Matters

Logs help you:
- Debug issues in production
- Audit user actions
- Track application behavior
- Investigate security incidents

### Log Levels

| Level | Purpose | Example |
|-------|---------|---------|
| **ERROR** | Critical issues | Database connection failed |
| **WARN** | Potential problems | Disk space low (80%) |
| **INFO** | Important events | User logged in, Order placed |
| **DEBUG** | Detailed information | Method parameters, SQL queries |
| **TRACE** | Very detailed | Request/response bodies |

### Logging Best Practices

#### ✅ DO:
```java
// Use SLF4J with parameterized logging
log.info("User {} placed order {} with total ${}", 
    userId, orderId, total);

// Include contextual information
log.error("Failed to process payment for order {}", orderId, exception);

// Use structured logging
log.info("event=order_placed user_id={} order_id={} amount={}", 
    userId, orderId, amount);
```

#### ❌ DON'T:
```java
// String concatenation (slow, not safe)
log.info("User " + userId + " placed order " + orderId);

// Logging sensitive data
log.info("User password: " + password); // NEVER!

// Generic error messages
log.error("Error occurred"); // Not helpful!

// Excessive logging in loops
for (int i = 0; i < 1000000; i++) {
    log.debug("Processing item " + i); // Performance killer!
}
```

### Logback Configuration

**logback-spring.xml**:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    
    <!-- Console Appender for Development -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <!-- File Appender for Production -->
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/application.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <!-- Daily rollover -->
            <fileNamePattern>logs/application-%d{yyyy-MM-dd}.log</fileNamePattern>
            <!-- Keep 30 days of history -->
            <maxHistory>30</maxHistory>
            <!-- Maximum total size: 10GB -->
            <totalSizeCap>10GB</totalSizeCap>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <!-- JSON Appender for Log Aggregation (ELK Stack) -->
    <appender name="JSON" class="ch.qos.logback.core.ConsoleAppender">
        <encoder class="net.logstash.logback.encoder.LogstashEncoder">
            <includeMdc>true</includeMdc>
        </encoder>
    </appender>
    
    <!-- Root Logger -->
    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="FILE"/>
    </root>
    
    <!-- Package-specific logging -->
    <logger name="com.masterclass" level="DEBUG"/>
    <logger name="org.springframework.web" level="DEBUG"/>
    <logger name="org.hibernate.SQL" level="DEBUG"/>
    
</configuration>
```

### Structured Logging Example

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

@Service
public class OrderService {
    
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    
    public Order createOrder(OrderRequest request) {
        // Add context to MDC (Mapped Diagnostic Context)
        MDC.put("userId", request.getUserId().toString());
        MDC.put("orderType", request.getType());
        
        try {
            log.info("Creating order for user: {}", request.getUserId());
            
            Order order = new Order();
            order.setUserId(request.getUserId());
            order.setItems(request.getItems());
            
            // Business logic
            validateOrder(order);
            calculateTotal(order);
            Order savedOrder = orderRepository.save(order);
            
            log.info("Order created successfully. orderId={}, total={}", 
                savedOrder.getId(), savedOrder.getTotal());
            
            return savedOrder;
            
        } catch (ValidationException e) {
            log.warn("Order validation failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to create order", e);
            throw new OrderCreationException("Failed to create order", e);
        } finally {
            // Clean up MDC
            MDC.clear();
        }
    }
}
```

---

## 📊 Part 2: Metrics

### Why Metrics Matter

Metrics help you:
- Understand system performance
- Detect anomalies and trends
- Set up alerts
- Make capacity planning decisions
- Track SLA/SLO compliance

### Types of Metrics

| Type | Description | Example |
|------|-------------|---------|
| **Counter** | Cumulative value (only increases) | Total requests, Total errors |
| **Gauge** | Current value (can go up/down) | CPU usage, Active connections |
| **Timer** | Duration distribution | Request latency, DB query time |
| **Distribution Summary** | Value distribution | Response sizes, Payload sizes |

### Micrometer - Metrics Facade

Micrometer is to metrics what SLF4J is to logging - a facade that works with multiple monitoring systems.

**Supported Systems**:
- Prometheus
- Grafana
- Datadog
- New Relic
- CloudWatch
- AppDynamics
- And many more

### Spring Boot Actuator

Add to `pom.xml`:

```xml
<dependencies>
    <!-- Spring Boot Actuator -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
    
    <!-- Micrometer Prometheus Registry -->
    <dependency>
        <groupId>io.micrometer</groupId>
        <artifactId>micrometer-registry-prometheus</artifactId>
    </dependency>
</dependencies>
```

**application.yml**:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
      base-path: /actuator
  endpoint:
    health:
      show-details: always
    metrics:
      enabled: true
    prometheus:
      enabled: true
  metrics:
    tags:
      application: ${spring.application.name}
      environment: ${ENVIRONMENT:dev}
```

### Actuator Endpoints

| Endpoint | Purpose |
|----------|---------|
| `/actuator/health` | Application health status |
| `/actuator/info` | Application information |
| `/actuator/metrics` | All available metrics |
| `/actuator/metrics/{name}` | Specific metric |
| `/actuator/prometheus` | Prometheus format metrics |

### Custom Metrics Example

```java
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

@Service
public class ProductService {
    
    private final Counter productCreatedCounter;
    private final Counter productCreationFailedCounter;
    private final Timer productCreationTimer;
    private final MeterRegistry meterRegistry;
    
    public ProductService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        
        // Create custom metrics
        this.productCreatedCounter = Counter.builder("products.created")
            .description("Total products created")
            .tag("service", "product-service")
            .register(meterRegistry);
            
        this.productCreationFailedCounter = Counter.builder("products.creation.failed")
            .description("Failed product creations")
            .tag("service", "product-service")
            .register(meterRegistry);
            
        this.productCreationTimer = Timer.builder("products.creation.time")
            .description("Time taken to create a product")
            .tag("service", "product-service")
            .register(meterRegistry);
    }
    
    public Product createProduct(ProductRequest request) {
        return productCreationTimer.record(() -> {
            try {
                Product product = new Product();
                product.setName(request.getName());
                product.setPrice(request.getPrice());
                
                Product saved = productRepository.save(product);
                
                // Increment success counter
                productCreatedCounter.increment();
                
                return saved;
                
            } catch (Exception e) {
                // Increment failure counter
                productCreationFailedCounter.increment();
                throw e;
            }
        });
    }
    
    // Gauge example - current inventory count
    @PostConstruct
    public void setupGauges() {
        meterRegistry.gauge("products.inventory.total", this, 
            service -> productRepository.count());
    }
}
```

### Common Metrics to Track

#### Application Metrics
```java
// HTTP Request metrics (automatic with Spring Boot)
http.server.requests
  - Total requests
  - Request duration
  - Status codes

// JVM Metrics
jvm.memory.used
jvm.memory.max
jvm.threads.live
jvm.gc.pause

// Database Metrics
hikaricp.connections.active
hikaricp.connections.pending
jdbc.connections.active

// Custom Business Metrics
orders.placed
orders.cancelled
payment.success
payment.failed
```

### Prometheus Configuration

**prometheus.yml**:

```yaml
global:
  scrape_interval: 15s
  evaluation_interval: 15s

scrape_configs:
  - job_name: 'spring-boot-app'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['localhost:8080']
        labels:
          application: 'product-service'
          environment: 'production'
```

---

## 🔗 Part 3: Distributed Tracing

### Why Distributed Tracing?

In microservices, a single user request can span multiple services:

```
User Request → API Gateway → Order Service → Product Service → Database
                           → User Service → Database
                           → Payment Service → External API
```

**Without tracing**: Hard to debug where time is spent or where failures occur.

**With tracing**: Complete visibility into the entire request flow.

### Trace Concepts

| Term | Description |
|------|-------------|
| **Trace** | Complete journey of a request across services |
| **Span** | A single operation within a trace |
| **Trace ID** | Unique identifier for the entire trace |
| **Span ID** | Unique identifier for a single span |
| **Parent Span** | The span that initiated the current span |

### Trace Visualization

```
Trace: e7b8c9d2-4f3a-4b2c-9e1d-5f6a7b8c9d0e
Duration: 245ms

API Gateway (50ms)
  │
  ├─→ Order Service (150ms)
  │     │
  │     ├─→ Product Service (80ms)
  │     │     └─→ Database Query (30ms)
  │     │
  │     └─→ User Service (40ms)
  │           └─→ Database Query (15ms)
  │
  └─→ Payment Service (45ms)
        └─→ External API Call (35ms)
```

### Spring Cloud Sleuth

**Add dependencies**:

```xml
<dependencies>
    <!-- Spring Cloud Sleuth for distributed tracing -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-sleuth</artifactId>
    </dependency>
    
    <!-- Zipkin for visualization -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-sleuth-zipkin</artifactId>
    </dependency>
</dependencies>
```

**application.yml**:

```yaml
spring:
  application:
    name: order-service
  sleuth:
    sampler:
      probability: 1.0  # Sample 100% of requests (use 0.1 for 10% in prod)
  zipkin:
    base-url: http://localhost:9411
    enabled: true
```

### Automatic Tracing

Sleuth automatically adds tracing to:
- HTTP requests (RestTemplate, WebClient)
- Messaging (RabbitMQ, Kafka)
- Scheduled tasks
- Async operations

**Log output with tracing**:

```
2026-02-11 10:30:45 [order-service,e7b8c9d2,4f3a4b2c,true] INFO  OrderService - Creating order
2026-02-11 10:30:45 [order-service,e7b8c9d2,9e1d5f6a,true] INFO  ProductClient - Fetching product
```

Format: `[application-name, trace-id, span-id, exportable]`

### Custom Spans

```java
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;

@Service
public class OrderService {
    
    private final Tracer tracer;
    
    public Order processOrder(OrderRequest request) {
        // Create custom span
        Span span = tracer.nextSpan().name("process-order");
        
        try (Tracer.SpanInScope ws = tracer.withSpan(span.start())) {
            // Add custom tags
            span.tag("order.id", request.getId().toString());
            span.tag("order.total", request.getTotal().toString());
            span.tag("user.id", request.getUserId().toString());
            
            // Your business logic
            Order order = createOrder(request);
            
            // Add event
            span.event("order.validated");
            
            processPayment(order);
            span.event("payment.completed");
            
            return order;
            
        } catch (Exception e) {
            span.tag("error", e.getMessage());
            throw e;
        } finally {
            span.end();
        }
    }
}
```

### Zipkin Setup

**docker-compose.yml**:

```yaml
version: '3.8'

services:
  zipkin:
    image: openzipkin/zipkin:latest
    container_name: zipkin
    ports:
      - "9411:9411"
    environment:
      - STORAGE_TYPE=mem
```

Start Zipkin:
```bash
docker-compose up -d
```

Access UI: http://localhost:9411

---

## 🏥 Part 4: Health Checks

### Why Health Checks?

- Kubernetes liveness/readiness probes
- Load balancer health verification
- Monitoring alerts
- Service mesh integration

### Health Check Types

#### 1. Liveness Probe
**Question**: Is the application running?
**Action if fails**: Restart the container

#### 2. Readiness Probe
**Question**: Is the application ready to serve traffic?
**Action if fails**: Remove from load balancer

### Spring Boot Health Indicators

**Built-in health indicators**:

| Indicator | Checks |
|-----------|--------|
| `DiskSpaceHealthIndicator` | Available disk space |
| `DataSourceHealthIndicator` | Database connectivity |
| `RedisHealthIndicator` | Redis connectivity |
| `RabbitHealthIndicator` | RabbitMQ connectivity |
| `MongoHealthIndicator` | MongoDB connectivity |

### Custom Health Indicator

```java
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class ExternalServiceHealthIndicator implements HealthIndicator {
    
    private final RestTemplate restTemplate;
    
    @Override
    public Health health() {
        try {
            // Check external service
            ResponseEntity<String> response = restTemplate.getForEntity(
                "https://api.external-service.com/health", 
                String.class
            );
            
            if (response.getStatusCode().is2xxSuccessful()) {
                return Health.up()
                    .withDetail("service", "external-api")
                    .withDetail("status", "reachable")
                    .withDetail("responseTime", "50ms")
                    .build();
            } else {
                return Health.down()
                    .withDetail("service", "external-api")
                    .withDetail("status", response.getStatusCode())
                    .build();
            }
            
        } catch (Exception e) {
            return Health.down()
                .withDetail("service", "external-api")
                .withDetail("error", e.getMessage())
                .build();
        }
    }
}
```

### Health Response Example

```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "PostgreSQL",
        "validationQuery": "isValid()"
      }
    },
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 499963174912,
        "free": 336389386240,
        "threshold": 10485760
      }
    },
    "externalService": {
      "status": "UP",
      "details": {
        "service": "external-api",
        "status": "reachable"
      }
    },
    "ping": {
      "status": "UP"
    }
  }
}
```

### Kubernetes Integration

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: order-service
spec:
  containers:
  - name: order-service
    image: order-service:1.0.0
    ports:
    - containerPort: 8080
    livenessProbe:
      httpGet:
        path: /actuator/health/liveness
        port: 8080
      initialDelaySeconds: 30
      periodSeconds: 10
      timeoutSeconds: 5
      failureThreshold: 3
    readinessProbe:
      httpGet:
        path: /actuator/health/readiness
        port: 8080
      initialDelaySeconds: 10
      periodSeconds: 5
      timeoutSeconds: 3
      failureThreshold: 3
```

---

## 📈 Part 5: Monitoring with Grafana

### Grafana Dashboard

Grafana provides beautiful, interactive dashboards for your metrics.

### Grafana Setup

**docker-compose.yml**:

```yaml
version: '3.8'

services:
  prometheus:
    image: prom/prometheus:latest
    container_name: prometheus
    ports:
      - "9090:9090"
    volumes:
      - ./prometheus.yml:/etc/prometheus/prometheus.yml
    command:
      - '--config.file=/etc/prometheus/prometheus.yml'

  grafana:
    image: grafana/grafana:latest
    container_name: grafana
    ports:
      - "3000:3000"
    environment:
      - GF_SECURITY_ADMIN_PASSWORD=admin
    depends_on:
      - prometheus
```

### Common Dashboard Panels

#### 1. Request Rate
```promql
rate(http_server_requests_seconds_count[5m])
```

#### 2. Error Rate
```promql
rate(http_server_requests_seconds_count{status=~"5.."}[5m])
```

#### 3. Latency (95th percentile)
```promql
histogram_quantile(0.95, rate(http_server_requests_seconds_bucket[5m]))
```

#### 4. JVM Memory Usage
```promql
jvm_memory_used_bytes{area="heap"} / jvm_memory_max_bytes{area="heap"} * 100
```

#### 5. Active Database Connections
```promql
hikaricp_connections_active
```

### RED Method (Recommended)

Monitor:
- **R**ate - Requests per second
- **E**rrors - Error rate
- **D**uration - Latency distribution

### USE Method (for resources)

Monitor:
- **U**tilization - % time resource is busy
- **S**aturation - Queue length
- **E**rrors - Error count

---

## 🚨 Part 6: Alerting

### Alert Rules (Prometheus)

**alert.rules.yml**:

```yaml
groups:
  - name: application_alerts
    interval: 30s
    rules:
      # High error rate
      - alert: HighErrorRate
        expr: rate(http_server_requests_seconds_count{status=~"5.."}[5m]) > 0.05
        for: 5m
        labels:
          severity: critical
        annotations:
          summary: "High error rate detected"
          description: "Error rate is {{ $value }} req/sec"

      # High latency
      - alert: HighLatency
        expr: histogram_quantile(0.95, rate(http_server_requests_seconds_bucket[5m])) > 1
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "High latency detected"
          description: "95th percentile latency is {{ $value }}s"

      # Service down
      - alert: ServiceDown
        expr: up{job="spring-boot-app"} == 0
        for: 2m
        labels:
          severity: critical
        annotations:
          summary: "Service is down"
          description: "{{ $labels.instance }} is down"

      # High memory usage
      - alert: HighMemoryUsage
        expr: (jvm_memory_used_bytes{area="heap"} / jvm_memory_max_bytes{area="heap"}) > 0.9
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "High memory usage"
          description: "Memory usage is {{ $value | humanizePercentage }}"
```

---

## 🎯 Best Practices

### 1. Logging Best Practices

✅ **DO**:
- Use structured logging
- Include correlation IDs
- Log at appropriate levels
- Use parameterized logging
- Include contextual information

❌ **DON'T**:
- Log sensitive data (passwords, tokens, PII)
- Use string concatenation
- Over-log in loops
- Log entire objects without filtering

### 2. Metrics Best Practices

✅ **DO**:
- Use consistent naming conventions
- Add relevant tags/labels
- Track SLIs (Service Level Indicators)
- Monitor business metrics
- Set up alerts based on trends

❌ **DON'T**:
- Create too many custom metrics
- Use high-cardinality tags
- Forget to clean up unused metrics
- Only monitor technical metrics

### 3. Tracing Best Practices

✅ **DO**:
- Sample appropriately (not 100% in prod)
- Add custom tags for business context
- Trace external dependencies
- Use baggage for important data

❌ **DON'T**:
- Trace everything (performance impact)
- Add sensitive data to spans
- Forget to propagate trace context

### 4. Health Check Best Practices

✅ **DO**:
- Check critical dependencies
- Implement separate liveness/readiness
- Return quickly (< 5 seconds)
- Include meaningful details

❌ **DON'T**:
- Make expensive operations
- Fail liveness for non-critical issues
- Forget external dependencies

---

## 🔧 Complete Observability Stack

### Recommended Architecture

```
┌───────────────────────────────────────────────────┐
│              Your Application                     │
│  (Spring Boot with Actuator + Sleuth)            │
└───────┬────────────┬────────────┬─────────────────┘
        │            │            │
   Logs │       Metrics │     Traces │
        ↓            ↓            ↓
┌───────────┐  ┌──────────┐  ┌──────────┐
│  Logback  │  │Prometheus│  │  Zipkin  │
│  Loki/ELK │  │          │  │  Jaeger  │
└─────┬─────┘  └────┬─────┘  └────┬─────┘
      │             │             │
      └─────────────┼─────────────┘
                    ↓
            ┌──────────────┐
            │   Grafana    │
            │  (Dashboards)│
            └──────────────┘
```

### Technology Stack

| Component | Technology | Purpose |
|-----------|------------|---------|
| **Logging** | SLF4J + Logback | Application logging |
| **Log Aggregation** | ELK Stack or Loki | Centralized log storage |
| **Metrics** | Micrometer | Metrics collection |
| **Metrics Storage** | Prometheus | Time-series database |
| **Tracing** | Spring Cloud Sleuth | Distributed tracing |
| **Trace Storage** | Zipkin or Jaeger | Trace visualization |
| **Dashboards** | Grafana | Unified visualization |
| **Alerting** | Prometheus Alertmanager | Alert notifications |

---

## 📊 Key Metrics to Monitor

### Application Metrics
- Request rate (requests/second)
- Error rate (errors/second)
- Response time (p50, p95, p99)
- Active threads
- Database connection pool

### JVM Metrics
- Heap memory usage
- Non-heap memory usage
- Garbage collection time
- Thread count
- CPU usage

### Business Metrics
- Orders placed
- Revenue generated
- Active users
- Cart abandonment rate
- Checkout success rate

---

## 🚀 Quick Start

### Step 1: Add Dependencies

```xml
<dependencies>
    <!-- Actuator for health & metrics -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
    
    <!-- Prometheus metrics -->
    <dependency>
        <groupId>io.micrometer</groupId>
        <artifactId>micrometer-registry-prometheus</artifactId>
    </dependency>
    
    <!-- Distributed tracing -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-sleuth</artifactId>
    </dependency>
    
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-sleuth-zipkin</artifactId>
    </dependency>
</dependencies>
```

### Step 2: Configure Application

```yaml
spring:
  application:
    name: my-service

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: always
  metrics:
    tags:
      application: ${spring.application.name}
```

### Step 3: Run Monitoring Stack

```bash
# Start Prometheus and Grafana
docker-compose up -d

# Access Prometheus: http://localhost:9090
# Access Grafana: http://localhost:3000
```

---

## 📚 Additional Resources

### Official Documentation
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
- [Micrometer Documentation](https://micrometer.io/docs)
- [Prometheus Documentation](https://prometheus.io/docs/)
- [Zipkin Documentation](https://zipkin.io/)
- [Spring Cloud Sleuth](https://spring.io/projects/spring-cloud-sleuth)

### Recommended Reading
- "Distributed Systems Observability" by Cindy Sridharan
- "Site Reliability Engineering" by Google
- "Observability Engineering" by Charity Majors

### Tools & Platforms
- Grafana: https://grafana.com/
- Datadog: https://www.datadoghq.com/
- New Relic: https://newrelic.com/
- Elastic APM: https://www.elastic.co/apm

---

## ✅ Module Checklist

Track your progress:

- [ ] Understand the three pillars of observability
- [ ] Configure structured logging with Logback
- [ ] Export metrics with Spring Boot Actuator
- [ ] Set up Prometheus for metrics collection
- [ ] Create Grafana dashboards
- [ ] Implement distributed tracing with Sleuth
- [ ] Visualize traces in Zipkin
- [ ] Create custom health indicators
- [ ] Set up alerting rules
- [ ] Monitor a complete microservices system

---

## 🎓 Interview Questions

### Basic
1. What are the three pillars of observability?
2. Difference between logs, metrics, and traces?
3. What is Spring Boot Actuator?
4. What are common Actuator endpoints?
5. What is Micrometer?

### Intermediate
6. Explain the difference between Counter and Gauge metrics
7. What is distributed tracing and why is it important?
8. How does Spring Cloud Sleuth work?
9. What is the difference between liveness and readiness probes?
10. How do you implement custom health indicators?

### Advanced
11. How would you design an observability strategy for 50+ microservices?
12. What sampling strategy would you use for traces in production?
13. How do you handle log aggregation at scale?
14. Explain the RED and USE methods for monitoring
15. How would you implement correlation IDs across services?

---

## 🎯 Next Steps

1. **Run the demos** in this module
2. **Set up monitoring** for your existing services
3. **Create dashboards** in Grafana
4. **Configure alerts** for critical metrics
5. Move to **Module 10: DevOps & Deployment**

---

**Last Updated**: February 2026
**Difficulty**: ⭐⭐⭐ Intermediate to Advanced
**Prerequisites**: Modules 01-08
**Estimated Time**: 3-4 days

🎉 **Master observability to build production-ready microservices!** 🚀
