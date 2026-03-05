# Module 09: Observability & Monitoring - Demo Guide

> **Complete guide to running all observabilityدemos**

## 📚 Overview

This module contains **3 comprehensive demos** demonstrating the three pillars of observability:

1. **demo-logging** - Structured logging with Logback and MDC
2. **demo-metrics-prometheus** - Metrics with Micrometer, Prometheus, and Grafana
3. **demo-distributed-tracing** - Distributed tracing with Zipkin

---

## 🎯 What You'll Learn

After completing these demos, you'll understand:

✅ How to implement structured logging with correlation IDs  
✅ How to collect and visualize metrics with Prometheus and Grafana  
✅ How to trace requests across multiple microservices  
✅ How to identify performance bottlenecks  
✅ How to set up production-ready monitoring  

---

## 📂 Demo Structure

```
09-observability/
├── README.md                          # Theory and concepts
├── DEMOS-README.md                    # This file
│
├── demo-logging/                      # Demo 1: Logging
│   ├── src/
│   ├── logs/                          # Generated at runtime
│   ├── pom.xml
│   └── README.md
│
├── demo-metrics-prometheus/           # Demo 2: Metrics
│   ├── src/
│   ├── docker-compose.yml             # Prometheus + Grafana
│   ├── prometheus.yml
│   ├── alert.rules.yml
│   ├── pom.xml
│   └── README.md
│
└── demo-distributed-tracing/          # Demo 3: Tracing
    ├── service-a/                     # First service
    │   ├── src/
    │   └── pom.xml
    ├── service-b/                     # Second service
    │   ├── src/
    │   └── pom.xml
    ├── docker-compose.yml             # Zipkin
    └── README.md
```

---

## 🚀 Quick Start - Run All Demos

### Prerequisites

- Java 17+
- Maven 3.8+
- Docker Desktop (for Prometheus, Grafana, Zipkin)
- Ports available: 8085, 8086, 8087, 8088, 3000, 9090, 9411

---

## 📝 Demo 1: Structured Logging

### What It Demonstrates
- SLF4J with parameterized logging
- MDC (Mapped Diagnostic Context)
- Correlation IDs
- Log rolling policies
- JSON logging for ELK Stack
- Multiple log appenders

### Run the Demo

```bash
# Navigate to demo directory
cd demo-logging

# Start application
mvn spring-boot:run
```

Application:http://localhost:8085

### Test It

```bash
# Demo all log levels
curl http://localhost:8085/api/demo-logs

# Create an order (success)
curl -X POST http://localhost:8085/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "John Doe",
    "productName": "Laptop",
    "quantity": 2,
    "pricePerUnit": 999.99
  }'

# Get all orders
curl http://localhost:8085/api/orders

# View logs
cat logs/application.log
cat logs/application-json.log
```

### What to Look For

1. **Correlation IDs** in logs:
   ```
   [correlationId=abc-123] Incoming request - POST /api/orders
   [correlationId=abc-123] Starting order creation
   [correlationId=abc-123] Order created successfully
   ```

2. **Different log levels**:
   - TRACE: Very detailed
   - DEBUG: Method parameters
   - INFO: Important events
   - WARN: Potential issues
   - ERROR: Critical problems

3. **MDC context**:
   ```
   MDC: {correlationId=abc-123, userId=john, orderId=1}
   ```

---

## 📊 Demo 2: Metrics with Prometheus & Grafana

### What It Demonstrates
- Spring Boot Actuator endpoints
- Micrometer custom metrics
- Prometheus metrics collection
- Grafana dashboards
- Alert rules
- Business metrics tracking

### Run the Demo

#### Step 1: Start Application

```bash
cd demo-metrics-prometheus
mvn spring-boot:run
```

Application: http://localhost:8086

#### Step 2: Start Monitoring Stack

```bash
# In same directory
docker-compose up -d
```

This starts:
- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3000 (admin/admin)

### Test It

```bash
# View metrics info
curl http://localhost:8086/api/metrics-info

# Create some products
curl -X POST http://localhost:8086/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop",
    "description": "High-performance laptop",
    "price": 1299.99,
    "stockQuantity": 15,
    "category": "Electronics"
  }'

# Simulate load (creates 10 products)
curl -X POST http://localhost:8086/api/products/simulate-load

# View actuator endpoints
curl http://localhost:8086/actuator/health
curl http://localhost:8086/actuator/metrics
curl http://localhost:8086/actuator/prometheus
```

### Explore Metrics

#### 1. Prometheus (http://localhost:9090)

Try these queries:

```promql
# Request rate
rate(http_server_requests_seconds_count[5m])

# Error rate
rate(http_server_requests_seconds_count{status=~"5.."}[5m])

# 95th percentile latency
histogram_quantile(0.95, rate(http_server_requests_seconds_bucket[5m]))

# Custom metric: products created
products_created_total

# JVM memory usage
jvm_memory_used_bytes{area="heap"}
```

#### 2. Grafana (http://localhost:3000)

1. Login: admin / admin
2. Add Prometheus data source:
   - URL: `http://prometheus:9090`
3. Import dashboard ID: **4701** (JVM Micrometer)
4. Create custom panels with queries above

### What to Look For

1. **Actuator Endpoints**:
   - `/actuator/health` - Health status
   - `/actuator/metrics` - All metrics
   - `/actuator/prometheus` - Prometheus format

2. **Custom Metrics**:
   - `products.created` - Counter
   - `products.inventory.total` - Gauge
   - `products.creation.time` - Timer

3. **Automatic Metrics**:
   - HTTP request metrics
   - JVM metrics
   - Database connection pool
   - GC metrics

---

## 🔗 Demo 3: Distributed Tracing with Zipkin

### What It Demonstrates
- Spring Cloud Sleuth automatic instrumentation
- Trace IDs and Span IDs
- Cross-service tracing
- Custom spans and tags
- Performance analysis
- Service dependency visualization

### Run the Demo

#### Step 1: Start Zipkin

```bash
cd demo-distributed-tracing
docker-compose up -d
```

Zipkin UI: http://localhost:9411

#### Step 2: Start Service B

```bash
cd service-b
mvn spring-boot:run
```

Service B: http://localhost:8088

#### Step 3: Start Service A (New Terminal)

```bash
cd service-a
mvn spring-boot:run
```

Service A: http://localhost:8087

### Test It

```bash
# Simple request (A calls B)
curl http://localhost:8087/api/hello

# User info request (with custom spans)
curl http://localhost:8087/api/user/john

# Slow request (2 second delay)
curl http://localhost:8087/api/slow

# Error request (demonstrates error tracing)
curl http://localhost:8087/api/error
```

### Explore Traces in Zipkin

1. Go to `http://localhost:9411`
2. Click **Run Query**
3. Click on a trace to see details

### What to Look For

1. **Complete Request Flow**:
   ```
   service-a: GET /api/hello (100ms)
     └─ service-b: GET /api/hello (50ms)
   ```

2. **Trace ID** in logs:
   ```
   [abc123/def456] Processing request
   ```
   Same `abc123` across both services!

3. **Custom Spans**:
   - `getUserInfo`
   - `fetchUserFromDB`
   - `slow-operation`

4. **Tags and Annotations**:
   - Tags: `username=john`, `operation=slow`
   - Events: `query.executed`, `processing.completed`

5. **Service Dependencies**:
   - Click **Dependencies** tab
   - See: service-a → service-b

---

## 🎯 Running All Three Demos Together

For the complete observability experience:

### Terminal 1: Logging Demo
```bash
cd demo-logging
mvn spring-boot:run
# http://localhost:8085
```

### Terminal 2: Metrics Demo
```bash
cd demo-metrics-prometheus
mvn spring-boot:run
# http://localhost:8086

# Start monitoring
docker-compose up -d
# Prometheus: http://localhost:9090
# Grafana: http://localhost:3000
```

### Terminal 3: Tracing - Zipkin
```bash
cd demo-distributed-tracing
docker-compose up -d
# Zipkin: http://localhost:9411
```

### Terminal 4: Tracing - Service B
```bash
cd demo-distributed-tracing/service-b
mvn spring-boot:run
# http://localhost:8088
```

### Terminal 5: Tracing - Service A
```bash
cd demo-distributed-tracing/service-a
mvn spring-boot:run
# http://localhost:8087
```

---

## 📊 Complete Observability Dashboard

| Service | Port | Purpose |
|---------|------|---------|
| **Logging Demo** | 8085 | Structured logging |
| **Metrics Demo** | 8086 | Metrics collection |
| **Service A** | 8087 | Tracing service A |
| **Service B** | 8088 | Tracing service B |
| **Prometheus** | 9090 | Metrics storage |
| **Grafana** | 3000 | Dashboards |
| **Zipkin** | 9411 | Trace visualization |

---

## 🧪 Test Scenarios

### Scenario 1: Track a Single Request

1. Make request to Logging Demo:
   ```bash
   curl -X POST http://localhost:8085/api/orders \
     -H "Content-Type: application/json" \
     -H "X-Correlation-ID: test-123" \
     -d '{"customerName":"John","productName":"Laptop","quantity":1,"pricePerUnit":999}'
   ```

2. Find in logs:
   ```bash
   grep "test-123" demo-logging/logs/application.log
   ```

3. See all logs for this single request!

### Scenario 2: Monitor System Health

1. Check Actuator health:
   ```bash
   curl http://localhost:8086/actuator/health | jq
   ```

2. View metrics in Prometheus:
   - Go to http://localhost:9090
   - Query: `up{job="spring-boot-app"}`

3. Create alert if service is down!

### Scenario 3: Find Performance Bottleneck

1. Make slow request:
   ```bash
   curl http://localhost:8087/api/slow
   ```

2. View in Zipkin:
   - See exactly which operation took 2 seconds
   - Identify: `slow-operation` span

3. Optimize accordingly!

### Scenario 4: Debug Distributed Error

1. Trigger error:
   ```bash
   curl http://localhost:8087/api/error
   ```

2. See in Zipkin:
   - Trace shows error tag
   - Error message captured
   - Which service failed

3. Check logs for stack trace:
   ```bash
   # Logs have same trace ID!
   grep "abc123" service-a/logs/*.log
   ```

---

## 🎓 Learning Path

### Beginner Level
1. ✅ Run demo-logging
2. ✅ Understand log levels
3. ✅ See correlation IDs in action

### Intermediate Level
4. ✅ Run demo-metrics-prometheus
5. ✅ Create custom metrics
6. ✅ Build Grafana dashboard

### Advanced Level
7. ✅ Run demo-distributed-tracing
8. ✅ Analyze complex traces
9. ✅ Add custom spans
10. ✅ Combine all three pillars

---

## 🔧 Troubleshooting

### Application Won't Start

**Error**: Port already in use

**Solution**:
```bash
# Check what's using the port
netstat -ano | findstr :8085

# Kill process or use different port
```

### Docker Containers Won't Start

**Error**: Port conflict

**Solution**:
```bash
# Check running containers
docker ps

# Stop conflicting containers
docker stop <container_name>

# Or stop all
docker-compose down
```

### Prometheus Can't Scrape Metrics

**Error**: Target down in Prometheus

**Solution**:
1. Check app is running: `curl http://localhost:8086/actuator/prometheus`
2. On Linux, change `host.docker.internal` to `172.17.0.1` in `prometheus.yml`
3. Restart: `docker-compose restart prometheus`

### Traces Not Showing in Zipkin

**Error**: No traces visible

**Solution**:
1. Check Zipkin: `curl http://localhost:9411/api/v2/services`
2. Make requests to generate traces
3. Check sampling rate in `application.yml` (should be 1.0 for demo)

### Logs Not Created

**Error**: `logs/` directory doesn't exist

**Solution**:
- Directory is created automatically on first log write
- Make a request to generate logs
- Check file permissions

---

## 🎯 Best Practices

### For Logging
- ✅ Use correlation IDs
- ✅ Parameterized logging
- ✅ Appropriate log levels
- ✅ Structured logging
- ❌ Don't log sensitive data

### For Metrics
- ✅ Track business metrics
- ✅ Use appropriate metric types
- ✅ Add meaningful tags
- ✅ Monitor RED method (Rate, Errors, Duration)
- ❌ Avoid high-cardinality tags

### For Tracing
- ✅ Sample appropriately (10% in production)
- ✅ Add business context to spans
- ✅ Tag errors
- ✅ Use meaningful span names
- ❌ Don't trace everything (overhead)

---

## 📚 Next Steps

After mastering these demos:

1. **Integrate** logging, metrics, and tracing in your projects
2. **Set up** ELK Stack for log aggregation
3. **Configure** alerts in Prometheus
4. **Build** comprehensive Grafana dashboards
5. **Deploy** to production with proper sampling rates

Then move to:
- **Module 10**: DevOps & Deployment
- **Module 11**: Advanced Patterns
- **Module 12**: Capstone Project

---

## 🛑 Stop All Demos

```bash
# Stop Spring Boot applications
Ctrl+C in each terminal

# Stop Docker containers
cd demo-metrics-prometheus
docker-compose down

cd ../demo-distributed-tracing
docker-compose down
```

---

## 📖 Additional Resources

- [Spring Boot Actuator Docs](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
- [Micrometer Documentation](https://micrometer.io/docs)
- [Prometheus Query Guide](https://prometheus.io/docs/prometheus/latest/querying/basics/)
- [Zipkin Documentation](https://zipkin.io/)
- [Grafana Dashboards](https://grafana.com/grafana/dashboards/)

---

## 🎉 Summary

You now have **production-ready observability**:

| Pillar | Technology | Purpose |
|---------|-----------|---------|
| **Logs** | SLF4J + Logback | What happened |
| **Metrics** | Micrometer + Prometheus | How much/many |
| **Traces** | Sleuth + Zipkin | Where is time spent |

**Together**: Complete visibility into your microservices! 🚀

---

**Happy Monitoring! 📊📝🔗**
