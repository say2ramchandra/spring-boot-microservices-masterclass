# Connection Pool Demo (HikariCP)

> **Master database connection pooling for high-performance applications**

## 📖 Overview

This demo explores HikariCP, the fastest JDBC connection pool. Learn:
- Pool configuration and tuning
- Monitoring pool metrics
- Handling pool exhaustion
- Detecting connection leaks
- Performance optimization

## 🚀 Running the Demo

```bash
mvn spring-boot:run
```

## 📊 API Endpoints

### Pool Statistics

```bash
# Get current pool stats
curl http://localhost:8081/pool/stats
```

Response:
```json
{
  "poolName": "MasterclassPool",
  "maximumPoolSize": 10,
  "minimumIdle": 5,
  "activeConnections": 0,
  "idleConnections": 5,
  "totalConnections": 5
}
```

### Concurrent Query Demo

```bash
# Run 50 concurrent queries
curl http://localhost:8081/pool/demo/concurrent/50
```

This demonstrates how the pool handles concurrent requests when the request count exceeds pool size.

### Pool Exhaustion Demo

```bash
# Demonstrate pool exhaustion
curl http://localhost:8081/pool/demo/exhaustion
```

Shows what happens when all connections are in use.

### Connection Leak Detection

```bash
# Create a connection leak (for demo only!)
curl "http://localhost:8081/pool/demo/leak?create=true"
```

Demonstrates HikariCP's leak detection feature.

### Actuator Metrics

```bash
# Pool metrics via Actuator
curl http://localhost:8081/actuator/metrics/hikaricp.connections.active
curl http://localhost:8081/actuator/metrics/hikaricp.connections.idle
curl http://localhost:8081/actuator/metrics/hikaricp.connections.pending
```

## ⚙️ Key Configuration Properties

```properties
# Pool Size
spring.datasource.hikari.maximum-pool-size=10    # Max connections
spring.datasource.hikari.minimum-idle=5          # Min idle connections

# Timeouts
spring.datasource.hikari.connection-timeout=30000  # Wait for connection (ms)
spring.datasource.hikari.idle-timeout=600000       # Idle connection timeout (ms)
spring.datasource.hikari.max-lifetime=1800000      # Max connection lifetime (ms)

# Leak Detection
spring.datasource.hikari.leak-detection-threshold=60000  # Leak warning (ms)
```

## 📐 Pool Sizing Formula

**For Most Applications:**
```
connections = (core_count * 2) + effective_spindle_count
```

**For SSDs:**
```
connections = core_count * 2
```

**Example:** 4-core server with SSD = 8-10 connections

### Why Small Pools Are Better

| Pool Size | Throughput | Why |
|-----------|------------|-----|
| 5-10 | High | Less contention, faster connections |
| 20-30 | Medium | More overhead, connection management |
| 50+ | Lower | Lock contention, memory overhead |

> "Bigger is NOT better for connection pools!"

## 🔍 Monitoring Best Practices

### Key Metrics to Watch

1. **Active Connections** - Should be < maximumPoolSize
2. **Pending Threads** - Should be 0 (indicates pool too small if > 0)
3. **Connection Wait Time** - Should be minimal
4. **Connection Timeout Rate** - Should be 0

### Alerts to Configure

```
# Alert if pool is often exhausted
hikaricp.connections.active >= maximum-pool-size for 1 minute

# Alert if threads waiting for connections
hikaricp.connections.pending > 0 for 30 seconds

# Alert if connection acquisition is slow
hikaricp.connections.acquire > 1 second
```

## 🐛 Common Issues

### Connection Timeout

**Symptom:** `SQLTransientConnectionException: Connection is not available`

**Causes:**
- Pool too small
- Connections not being returned (leak)
- Slow queries holding connections

**Solutions:**
- Increase pool size (carefully)
- Enable leak detection
- Optimize slow queries

### Connection Leaks

**Symptom:** Pool slowly exhausts over time

**Detection:**
```properties
spring.datasource.hikari.leak-detection-threshold=60000
```

**Fix:** Always use try-with-resources:
```java
try (Connection conn = dataSource.getConnection()) {
    // Use connection
} // Auto-closed
```

## 📚 Configuration Reference

See [application.properties](src/main/resources/application.properties) for fully documented configuration.

## 🎯 Exercises

1. **Tune the pool** for a 4-core machine
2. **Create a leak** and observe detection logs
3. **Exhaust the pool** and handle the timeout gracefully
4. **Monitor with Actuator** and interpret metrics

## 📖 Further Reading

- [HikariCP GitHub](https://github.com/brettwooldridge/HikariCP)
- [About Pool Sizing](https://github.com/brettwooldridge/HikariCP/wiki/About-Pool-Sizing)
- [MySQL Configuration](https://github.com/brettwooldridge/HikariCP/wiki/MySQL-Configuration)
