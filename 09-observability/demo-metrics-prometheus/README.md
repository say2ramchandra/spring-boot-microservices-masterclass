# Demo: Metrics with Prometheus & Grafana

> **Complete metrics collection, monitoring, and visualization**

## 🎯 What This Demo Teaches

This demo demonstrates professional metrics and monitoring practices:

✅ **Spring Boot Actuator** - Health checks and metrics endpoints  
✅ **Micrometer** - Metrics facade supporting multiple backends  
✅ **Custom Metrics** - Counters, Gauges, Timers, Distribution Summaries  
✅ **Prometheus** - Time-series metrics collection and storage  
✅ **Grafana** - Beautiful dashboards and visualization  
✅ **Alert Rules** - Automated alerting on metric thresholds  
✅ **Business Metrics** - Track custom application metrics  

---

## 📂 Project Structure

```
demo-metrics-prometheus/
├── src/main/
│   ├── java/.../metrics/
│   │   ├── MetricsDemoApplication.java   # Main application
│   │   ├── controller/
│   │   │   ├── ProductController.java    # REST API with metrics
│   │   │   └── MetricsInfoController.java # Metrics info endpoint
│   │   ├── service/
│   │   │   └── ProductService.java       # Business logic with custom metrics
│   │   ├── model/
│   │   │   └── Product.java              # Entity
│   │   └── repository/
│   │       └── ProductRepository.java    # Data access
│   └── resources/
│       └── application.yml                # Spring Boot config
├── docker-compose.yml                     # Prometheus + Grafana
├── prometheus.yml                         # Prometheus configuration
├── alert.rules.yml                        # Alert rules
├── pom.xml
└── README.md
```

---

## 🚀 Quick Start

### Step 1: Start the Application

```bash
cd 09-observability/demo-metrics-prometheus
mvn spring-boot:run
```

Application starts on **http://localhost:8086**

### Step 2: Start Prometheus & Grafana

```bash
# In the same directory
docker-compose up -d
```

This starts:
- **Prometheus** on http://localhost:9090
- **Grafana** on http://localhost:3000

---

## 🧪 Test the Metrics

### 1. Get Metrics Info

```bash
curl http://localhost:8086/api/metrics-info
```

### 2. Create Some Products

```bash
curl -X POST http://localhost:8086/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop",
    "description": "High-performance laptop",
    "price": 1299.99,
    "stockQuantity": 15,
    "category": "Electronics"
  }'
```

### 3. Simulate Load (Create 10 Products)

```bash
curl -X POST http://localhost:8086/api/products/simulate-load
```

### 4. Get All Products

```bash
curl http://localhost:8086/api/products
```

---

## 📊 View Metrics

### Actuator Endpoints

#### 1. Health Check
```bash
curl http://localhost:8086/actuator/health
```

Response:
```json
{
  "status": "UP",
  "components": {
    "db": { "status": "UP" },
    "diskSpace": { "status": "UP" },
    "ping": { "status": "UP" }
  }
}
```

#### 2. All Available Metrics
```bash
curl http://localhost:8086/actuator/metrics
```

#### 3. Specific Metric
```bash
# JVM memory
curl http://localhost:8086/actuator/metrics/jvm.memory.used

# HTTP requests
curl http://localhost:8086/actuator/metrics/http.server.requests

# Custom metric
curl http://localhost:8086/actuator/metrics/products.created
```

#### 4. Prometheus Format
```bash
curl http://localhost:8086/actuator/prometheus
```

This endpoint provides all metrics in Prometheus format:
```
# HELP products_created_total Total number of products created
# TYPE products_created_total counter
products_created_total{service="product-service",type="business"} 10.0

# HELP products_inventory_total Total number of products in inventory
# TYPE products_inventory_total gauge
products_inventory_total{type="inventory"} 10.0
```

---

## 📈 Prometheus Queries

Access Prometheus: **http://localhost:9090**

### Common Queries

#### 1. Request Rate (requests per second)
```promql
rate(http_server_requests_seconds_count[5m])
```

#### 2. Request Rate by Status Code
```promql
rate(http_server_requests_seconds_count{status="200"}[5m])
```

#### 3. Error Rate
```promql
rate(http_server_requests_seconds_count{status=~"5.."}[5m])
```

#### 4. 95th Percentile Response Time
```promql
histogram_quantile(0.95, rate(http_server_requests_seconds_bucket[5m]))
```

#### 5. Average Response Time
```promql
rate(http_server_requests_seconds_sum[5m]) / rate(http_server_requests_seconds_count[5m])
```

#### 6. JVM Memory Usage
```promql
jvm_memory_used_bytes{area="heap"}
```

#### 7. JVM Memory Usage Percentage
```promql
(jvm_memory_used_bytes{area="heap"} / jvm_memory_max_bytes{area="heap"}) * 100
```

#### 8. CPU Usage
```promql
process_cpu_usage
```

#### 9. Custom Business Metrics

**Total Products Created:**
```promql
products_created_total
```

**Product Creation Rate:**
```promql
rate(products_created_total[5m])
```

**Products in Inventory:**
```promql
products_inventory_total
```

**Low Stock Products:**
```promql
products_inventory_low_stock
```

**Product Creation Duration (95th percentile):**
```promql
histogram_quantile(0.95, rate(products_creation_time_seconds_bucket[5m]))
```

#### 10. Alert Queries

**High Error Rate:**
```promql
rate(http_server_requests_seconds_count{status=~"5.."}[5m]) > 0.05
```

**Service Down:**
```promql
up{job="spring-boot-app"} == 0
```

**High Memory:**
```promql
(jvm_memory_used_bytes{area="heap"} / jvm_memory_max_bytes{area="heap"}) > 0.9
```

---

## 📊 Grafana Dashboards

Access Grafana: **http://localhost:3000**
- Username: `admin`
- Password: `admin`

### Step 1: Add Prometheus Data Source

1. Click **Configuration** (gear icon) → **Data Sources**
2. Click **Add data source**
3. Select **Prometheus**
4. Set URL: `http://prometheus:9090`
5. Click **Save & Test**

### Step 2: Create Dashboard

#### Panel 1: Request Rate
- **Query**: `rate(http_server_requests_seconds_count[5m])`
- **Type**: Graph
- **Title**: "Requests per Second"

#### Panel 2: Error Rate
- **Query**: `rate(http_server_requests_seconds_count{status=~"5.."}[5m])`
- **Type**: Graph
- **Title**: "Errors per Second"

#### Panel 3: Response Time (p95)
- **Query**: `histogram_quantile(0.95, rate(http_server_requests_seconds_bucket[5m]))`
- **Type**: Graph
- **Title**: "95th Percentile Response Time"

#### Panel 4: JVM Memory
- **Query**: `jvm_memory_used_bytes{area="heap"}`
- **Type**: Graph
- **Title**: "JVM Heap Memory"

#### Panel 5: Products Created
- **Query**: `products_created_total`
- **Type**: Stat
- **Title**: "Total Products Created"

#### Panel 6: Inventory Status
- **Query**: `products_inventory_total`
- **Type**: Gauge
- **Title**: "Inventory Total"

### Step 3: Import Pre-Built Dashboards

Grafana has community dashboards for Spring Boot:

1. Click **+** → **Import**
2. Enter Dashboard ID: **4701** (JVM Micrometer)
3. Select Prometheus data source
4. Click **Import**

Popular Spring Boot Dashboards:
- **4701** - JVM (Micrometer)
- **11378** - Spring Boot Statistics
- **12900** - Spring Boot APM Dashboard

---

## 🔔 Alert Rules

Alerts are defined in `alert.rules.yml`:

### 1. High Error Rate
Triggers when error rate > 5% for 5 minutes

### 2. High Response Time
Triggers when p95 latency > 1 second for 5 minutes

### 3. Service Down
Triggers when application is unreachable for 2 minutes

### 4. High Memory Usage
Triggers when heap usage > 90% for 5 minutes

### 5. High CPU Usage
Triggers when CPU usage > 80% for 5 minutes

### View Alerts in Prometheus

Go to: http://localhost:9090/alerts

---

## 📊 Metric Types Explained

### 1. Counter
**Definition**: Cumulative value that only increases (or resets to zero)

**Use Cases**:
- Total requests
- Total errors
- Total products created
- Total orders placed

**Example**:
```java
Counter counter = Counter.builder("products.created")
    .description("Total products created")
    .register(meterRegistry);

counter.increment();
```

### 2. Gauge
**Definition**: Current value that can go up and down

**Use Cases**:
- Current memory usage
- Active connections
- Queue size
- Inventory count

**Example**:
```java
Gauge.builder("products.inventory.total", this,
        service -> productRepository.count())
    .register(meterRegistry);
```

### 3. Timer
**Definition**: Measures duration and frequency of events

**Use Cases**:
- Request latency
- Method execution time
- Database query duration

**Example**:
```java
Timer timer = Timer.builder("products.creation.time")
    .description("Time to create product")
    .register(meterRegistry);

timer.record(() -> {
    // Your code here
});
```

### 4. Distribution Summary
**Definition**: Distribution of values (not time-based)

**Use Cases**:
- Request payload size
- Response body size
- Order amounts

---

## 🎯 Metrics Best Practices

### ✅ DO:

1. **Use Appropriate Metric Types**
   - Counter for cumulative values
   - Gauge for current values
   - Timer for durations

2. **Add Meaningful Tags**
   ```java
   Counter.builder("orders.placed")
       .tag("payment_method", "credit_card")
       .tag("region", "us-east")
       .register(registry);
   ```

3. **Track Business Metrics**
   - Revenue
   - User signups
   - Feature usage
   - Conversion rate

4. **Monitor the RED Method**
   - **R**ate - Requests per second
   - **E**rrors - Error rate
   - **D**uration - Latency

5. **Use Histograms for Percentiles**
   ```yaml
   management:
     metrics:
       distribution:
         percentiles-histogram:
           http.server.requests: true
   ```

### ❌ DON'T:

1. **High Cardinality Tags**
   ```java
   // BAD - userId has millions of values
   .tag("user_id", userId)
   
   // GOOD - user_type has few values
   .tag("user_type", userType)
   ```

2. **Too Many Custom Metrics**
   - Creates storage overhead
   - Makes dashboard crowded

3. **Ignore Default Metrics**
   - Spring Boot provides excellent defaults
   - Use them!

---

## 📊 Common Metrics to Monitor

### Application Metrics

| Metric | Type | Description |
|--------|------|-------------|
| `http.server.requests` | Timer | HTTP request metrics |
| `jvm.memory.used` | Gauge | JVM memory usage |
| `jvm.gc.pause` | Timer | GC pause duration |
| `jvm.threads.live` | Gauge | Active threads |
| `hikaricp.connections.active` | Gauge | DB connections |

### Custom Business Metrics

| Metric | Type | Description |
|--------|------|-------------|
| `products.created` | Counter | Products created |
| `orders.placed` | Counter | Orders placed |
| `revenue.total` | Counter | Total revenue |
| `cart.abandonment` | Counter | Abandoned carts |
| `inventory.total` | Gauge | Current inventory |

---

## 🔍 Troubleshooting

### Prometheus Can't Scrape Metrics

1. **Check application is running**:
   ```bash
   curl http://localhost:8086/actuator/prometheus
   ```

2. **Check Docker network**:
   - On Windows/Mac, use `host.docker.internal` in `prometheus.yml`
   - On Linux, use `172.17.0.1` or `localhost`

3. **Check Prometheus targets**:
   - Go to http://localhost:9090/targets
   - Should show `spring-boot-app` as UP

### Grafana Can't Connect to Prometheus

1. **Use container name**: `http://prometheus:9090`
2. **Not**: `http://localhost:9090` (won't work inside container)

### Metrics Not Showing

1. **Generate some load**:
   ```bash
   curl -X POST http://localhost:8086/api/products/simulate-load
   ```

2. **Check metric exists**:
   ```bash
   curl http://localhost:8086/actuator/metrics/products.created
   ```

---

## 🎓 Key Takeaways

1. **Micrometer** is the metrics facade (like SLF4J for logging)
2. **Actuator** provides production-ready endpoints
3. **Prometheus** collects and stores time-series data
4. **Grafana** visualizes metrics beautifully
5. **Custom metrics** track business-specific data
6. **Alert rules** enable proactive monitoring
7. **Percentiles** (p50, p95, p99) more useful than averages

---

## 📚 Next Steps

1. ✅ Run this demo and explore the metrics
2. ✅ View metrics in Prometheus (http://localhost:9090)
3. ✅ Create dashboards in Grafana (http://localhost:3000)
4. ✅ Trigger alerts by simulating errors
5. ✅ Add your own custom metrics

Then move to:
- **demo-distributed-tracing** - Trace requests across services

---

## 🛑 Stop the Demo

```bash
# Stop Spring Boot app
Ctrl+C

# Stop Docker containers
docker-compose down
```

---

**Happy Monitoring! 📊✨**
