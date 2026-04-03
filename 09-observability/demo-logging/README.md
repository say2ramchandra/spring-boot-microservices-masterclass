# Demo: Structured Logging with Logback

> **Comprehensive logging patterns for production microservices**

## 🎯 What This Demo Teaches

This demo demonstrates professional logging practices including:

✅ **Structured Logging** with SLF4J and Logback  
✅ **MDC (Mapped Diagnostic Context)** for request correlation  
✅ **All Log Levels** - TRACE, DEBUG, INFO, WARN, ERROR  
✅ **JSON Logging** for log aggregation (ELK Stack)  
✅ **Log Rolling** policies for file management  
✅ **Async Logging** for performance  
✅ **Request/Response Logging** with correlation IDs  

---

## 📂 Project Structure

```
demo-logging/
├── src/main/
│   ├── java/.../logging/
│   │   ├── LoggingDemoApplication.java    # Main application
│   │   ├── controller/
│   │   │   └── OrderController.java       # REST endpoints with logging
│   │   ├── service/
│   │   │   └── OrderService.java          # Business logic with comprehensive logging
│   │   ├── model/
│   │   │   └── Order.java                 # Entity
│   │   ├── dto/
│   │   │   └── OrderRequest.java          # DTO
│   │   ├── repository/
│   │   │   └── OrderRepository.java       # Data access
│   │   └── config/
│   │       └── LoggingFilter.java         # Correlation ID filter
│   └── resources/
│       ├── application.yml                 # Application config
│       └── logback-spring.xml             # Logging configuration
├── logs/                                   # Log files (created at runtime)
│   ├── application.log                    # Human-readable logs
│   └── application-json.log               # JSON logs for ELK
├── pom.xml
└── README.md
```

---

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Maven 3.8+

### Run the Application

```bash
# Navigate to the demo directory
cd 09-observability/demo-logging

# Run with Maven
mvn spring-boot:run
```

The application will start on **http://localhost:8085**

---

## 🧪 Test the Logging

### 1. Demonstrate All Log Levels

```bash
curl http://localhost:8085/api/demo-logs
```

Check `logs/application.log` to see all log levels in action.

### 2. Create an Order (Success Case)

```bash
curl -X POST http://localhost:8085/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "John Doe",
    "productName": "Laptop",
    "quantity": 2,
    "pricePerUnit": 999.99
  }'
```

**Expected Logs:**
```
2026-02-11 10:30:45 [http-nio-8085-exec-1] INFO  LoggingFilter - Incoming request - POST /api/orders
2026-02-11 10:30:45 [http-nio-8085-exec-1] INFO  OrderController - Received order creation request
2026-02-11 10:30:45 [http-nio-8085-exec-1] INFO  OrderService - Starting order creation for customer: John Doe
2026-02-11 10:30:45 [http-nio-8085-exec-1] DEBUG OrderService - Order request details - product: Laptop, quantity: 2, price: 999.99
2026-02-11 10:30:45 [http-nio-8085-exec-1] DEBUG OrderService - Validating order request
2026-02-11 10:30:45 [http-nio-8085-exec-1] DEBUG OrderService - Order request validation passed
2026-02-11 10:30:45 [http-nio-8085-exec-1] DEBUG OrderService - Calculated total amount: 1999.98
2026-02-11 10:30:45 [http-nio-8085-exec-1] TRACE OrderService - Attempting to save order to database
2026-02-11 10:30:45 [http-nio-8085-exec-1] INFO  OrderService - Order created successfully - orderId: 1, total: 1999.98, status: PENDING
2026-02-11 10:30:45 [http-nio-8085-exec-1] INFO  OrderService - Processing order: 1
2026-02-11 10:30:45 [http-nio-8085-exec-1] DEBUG OrderService - Step 1: Validating payment details
2026-02-11 10:30:45 [http-nio-8085-exec-1] DEBUG OrderService - Step 2: Checking inventory for product: Laptop
2026-02-11 10:30:45 [http-nio-8085-exec-1] DEBUG OrderService - Step 3: Preparing shipment
2026-02-11 10:30:45 [http-nio-8085-exec-1] INFO  OrderService - Order processed successfully - orderId: 1
2026-02-11 10:30:45 [http-nio-8085-exec-1] INFO  OrderController - Order creation completed successfully - orderId: 1
2026-02-11 10:30:45 [http-nio-8085-exec-1] INFO  LoggingFilter - Request completed - status: 201, duration: 125ms
```

### 3. Create an Order (Validation Failure)

```bash
curl -X POST http://localhost:8085/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "",
    "productName": "Laptop",
    "quantity": 0,
    "pricePerUnit": -10.00
  }'
```

**Expected Logs (WARN level):**
```
2026-02-11 10:32:15 [http-nio-8085-exec-2] WARN  OrderService - Validation failed: Customer name is required
2026-02-11 10:32:15 [http-nio-8085-exec-2] WARN  OrderService - Order validation failed for customer:  - Customer name is required
2026-02-11 10:32:15 [http-nio-8085-exec-2] WARN  OrderController - Invalid request: Customer name is required
```

### 4. Get Order by ID

```bash
curl http://localhost:8085/api/orders/1
```

### 5. Get All Orders

```bash
curl http://localhost:8085/api/orders
```

---

## 📝 Logging Features Demonstrated

### 1. Parameterized Logging (Best Practice)

**✅ GOOD** (Efficient, Safe):
```java
log.info("User {} placed order {} with total ${}", userId, orderId, total);
```

**❌ BAD** (Slow, Not Safe):
```java
log.info("User " + userId + " placed order " + orderId);
```

### 2. MDC (Mapped Diagnostic Context)

MDC adds contextual information to every log line:

```java
MDC.put("correlationId", UUID.randomUUID().toString());
MDC.put("userId", "12345");
MDC.put("orderId", "67890");

log.info("Processing payment");
// Output: [correlationId=abc-123, userId=12345, orderId=67890] Processing payment

MDC.clear(); // Always clean up!
```

### 3. Correlation IDs

Every request gets a unique correlation ID:
- Automatically added by `LoggingFilter`
- Passed in response header: `X-Correlation-ID`
- Included in all logs for that request
- Enables tracing across services

### 4. Log Levels

| Level | When to Use | Example |
|-------|-------------|---------|
| **TRACE** | Very detailed info | Method entry/exit, loop iterations |
| **DEBUG** | Debugging info | SQL queries, calculations, method params |
| **INFO** | Important events | User login, order placed, service started |
| **WARN** | Potential problems | Deprecated API, low disk space, retry attempt |
| **ERROR** | Critical issues | Database connection failed, null pointer |

### 5. Log File Rotation

Logs are automatically rotated:
- **Daily rotation**: One file per day
- **Maximum history**: 30 days
- **Maximum size**: 1GB total
- Old logs are automatically deleted

Files:
```
logs/
├── application.log              # Current day
├── application-2026-02-10.log  # Yesterday
├── application-2026-02-09.log  # 2 days ago
└── ...
```

### 6. JSON Logging for ELK Stack

```json
{
  "@timestamp": "2026-02-11T10:30:45.123Z",
  "level": "INFO",
  "logger_name": "com.masterclass.observability.logging.service.OrderService",
  "message": "Order created successfully",
  "correlationId": "abc-123-def-456",
  "orderId": "12345",
  "method": "POST",
  "uri": "/api/orders",
  "thread_name": "http-nio-8085-exec-1"
}
```

Perfect for:
- Elasticsearch
- Logstash
- Kibana (ELK Stack)
- Splunk
- Datadog

---

## 🎨 Logback Configuration Highlights

### Multiple Appenders

1. **CONSOLE** - Colored output for development
2. **FILE** - Rolling file appender with daily rotation
3. **JSON_FILE** - JSON format for log aggregation
4. **ASYNC_FILE** - Async appender for performance

### Configuration Example

```xml
<appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
    <file>logs/application.log</file>
    <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
        <fileNamePattern>logs/application-%d{yyyy-MM-dd}.log</fileNamePattern>
        <maxHistory>30</maxHistory>
        <totalSizeCap>1GB</totalSizeCap>
    </rollingPolicy>
</appender>
```

---

## 💡 Best Practices Demonstrated

### ✅ DO:
1. **Use SLF4J** - Logging facade (not implementation)
2. **Parameterized logging** - Efficient and safe
3. **Add correlation IDs** - Trace requests across services
4. **Use appropriate log levels** - Don't log everything as INFO
5. **Include context** - User ID, order ID, transaction ID
6. **Clean up MDC** - Use try-finally blocks
7. **Structure your logs** - Consistent format
8. **Rotate log files** - Prevent disk space issues

### ❌ DON'T:
1. **Log sensitive data** - Passwords, tokens, credit cards
2. **Use string concatenation** - Slow and creates garbage
3. **Over-log** - Not every method call needs a log
4. **Generic messages** - "Error occurred" is not helpful
5. **Log in loops** - Performance killer
6. **Ignore exceptions** - Always log the stack trace

---

## 📊 Log Analysis

### Find All Logs for a Request

If you have correlation ID `abc-123`:

```bash
grep "abc-123" logs/application.log
```

### Find All Errors

```bash
grep "ERROR" logs/application.log
```

### Find Slow Requests (> 500ms)

```bash
grep "duration: [5-9][0-9][0-9]ms" logs/application.log
```

---

## 🔍 Observability in Action

### What You Can See:

1. **Request Flow**: Follow a request from controller → service → repository
2. **Performance**: See duration of each request
3. **Errors**: Identify where and why failures occur
4. **User Actions**: Track what users are doing
5. **System Behavior**: Understand application patterns

### Example Trace:

```
[correlationId=abc-123] Incoming request - POST /api/orders
[correlationId=abc-123] Received order creation request
[correlationId=abc-123] Starting order creation for customer: John Doe
[correlationId=abc-123] Validating order request
[correlationId=abc-123] Order created successfully - orderId: 1
[correlationId=abc-123] Processing order: 1
[correlationId=abc-123] Order processed successfully
[correlationId=abc-123] Request completed - status: 201, duration: 125ms
```

All logs linked by `correlationId`!

---

## 🎓 Key Takeaways

1. **Structured logging** makes logs searchable and parseable
2. **MDC** provides context to every log line
3. **Correlation IDs** enable request tracing
4. **Appropriate log levels** make debugging faster
5. **JSON logging** enables log aggregation
6. **Log rotation** prevents disk space issues

---

## 📚 Next Steps

1. ✅ Run this demo and examine the logs
2. ✅ Try different scenarios (success, validation errors)
3. ✅ Check both `application.log` and `application-json.log`
4. ✅ Modify log levels in `application.yml`
5. ✅ Add your own logging statements

Then move to:
- **demo-metrics-prometheus** - Metrics collection
- **demo-distributed-tracing** - Trace requests across services

---

**Happy Logging! 📝✨**
