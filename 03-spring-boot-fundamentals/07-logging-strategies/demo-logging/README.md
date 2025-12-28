# Logging Strategies Demo

This demo showcases comprehensive logging strategies in Spring Boot with SLF4J, Logback, MDC, and structured JSON logging.

## 📋 Features Demonstrated

- **SLF4J Logging** - Abstraction layer for logging
- **Logback Configuration** - Custom logback-spring.xml
- **Log Levels** - TRACE, DEBUG, INFO, WARN, ERROR
- **Parameterized Logging** - Efficient log statements
- **MDC (Mapped Diagnostic Context)** - Request/user tracking
- **Multiple Appenders** - Console, File, Error File, JSON File
- **Rolling File Policy** - Log rotation by size and time
- **Async Logging** - Better performance
- **Structured JSON Logging** - Machine-readable logs
- **Best Practices** - Real-world examples

## 🚀 Running the Demo

### Prerequisites
- Java 17 or higher
- Maven 3.6+

### Steps

1. **Navigate to demo directory:**
   ```bash
   cd 03-spring-boot-fundamentals/07-logging-strategies/demo-logging
   ```

2. **Run the application:**
   ```bash
   mvn spring-boot:run
   ```

3. **Application starts on port 8080**

## 📁 Log Files

After starting the application, check the `logs/` directory:

- **application.log** - All logs (INFO and above)
- **error.log** - Only ERROR level logs
- **application.json** - Structured JSON format

## 🎯 Testing Logging Scenarios

### 1. All Log Levels

```bash
curl http://localhost:8080/api/demo/all-levels

# Check logs/application.log to see:
# TRACE, DEBUG, INFO, WARN, ERROR levels
```

### 2. Parameterized Logging

```bash
curl "http://localhost:8080/api/demo/with-params?name=John&age=30"

# Logs show efficient parameterized logging:
# INFO - Processing request for user: John with age: 30
```

### 3. Exception Logging

```bash
# Success case
curl "http://localhost:8080/api/demo/with-exception?shouldFail=false"

# Failure case (check logs/error.log)
curl "http://localhost:8080/api/demo/with-exception?shouldFail=true"
```

### 4. MDC Context Logging

```bash
# Without user ID
curl http://localhost:8080/api/demo/with-mdc

# With user ID (check logs - userId will appear)
curl -H "X-User-Id: user123" http://localhost:8080/api/demo/with-mdc

# All logs will include:
# - requestId (UUID)
# - userId (from header or "anonymous")
```

### 5. Business Logic Logging

```bash
curl -X POST http://localhost:8080/api/demo/create-order \
  -H "Content-Type: application/json" \
  -d '{
    "product": "Laptop",
    "quantity": 2,
    "price": 1200.00
  }'

# Check logs for:
# - Order creation with ID
# - Business logic logging
# - Success confirmation
```

### 6. Conditional Logging

```bash
# Normal mode
curl "http://localhost:8080/api/demo/conditional?verbose=false"

# Verbose mode (more DEBUG logs)
curl "http://localhost:8080/api/demo/conditional?verbose=true"
```

### 7. Performance Timing

```bash
curl http://localhost:8080/api/demo/with-timing

# Logs show:
# - Operation start time
# - Operation duration
# - Warning if too slow
```

## 📊 Log File Examples

### application.log (Standard Format)

```
2024-01-20 10:15:30.123 [http-nio-8080-exec-1] [abc-123-def] [user123] INFO  c.m.controller.LoggingDemoController - Processing request for user: John with age: 30
2024-01-20 10:15:30.456 [http-nio-8080-exec-1] [abc-123-def] [user123] DEBUG c.m.service.LoggingDemoService - Processing user in service layer: John
2024-01-20 10:15:30.789 [http-nio-8080-exec-1] [abc-123-def] [user123] INFO  c.m.service.LoggingDemoService - Processing adult user: John
```

### error.log (Errors Only)

```
2024-01-20 10:20:15.123 [http-nio-8080-exec-2] [xyz-789-ghi] [user456] ERROR c.m.controller.LoggingDemoController - Operation failed with error: Simulated failure
java.lang.RuntimeException: Simulated failure for logging demo
    at com.masterclass.controller.LoggingDemoController.demonstrateExceptionLogging(LoggingDemoController.java:87)
    ...
```

### application.json (Structured Format)

```json
{
  "@timestamp": "2024-01-20T10:15:30.123Z",
  "level": "INFO",
  "thread_name": "http-nio-8080-exec-1",
  "logger_name": "com.masterclass.controller.LoggingDemoController",
  "message": "Processing request for user: John with age: 30",
  "requestId": "abc-123-def",
  "userId": "user123"
}
```

## 🔍 Logback Configuration Highlights

### Multiple Appenders

1. **CONSOLE** - Outputs to console with MDC context
2. **FILE** - Rolling file appender (10MB, 30 days)
3. **ERROR_FILE** - Only ERROR level (60 days retention)
4. **JSON_FILE** - Structured JSON format
5. **ASYNC_*** - Async wrappers for better performance

### Log Pattern

```
%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] [%X{requestId}] [%X{userId}] %-5level %logger{36} - %msg%n
```

- `%d` - Timestamp
- `%thread` - Thread name
- `%X{requestId}` - MDC requestId
- `%X{userId}` - MDC userId
- `%-5level` - Log level (padded)
- `%logger{36}` - Logger name (max 36 chars)
- `%msg` - Log message
- `%n` - Newline

### Rolling Policy

- **Size-based**: New file when 10MB
- **Time-based**: New file daily
- **Retention**: 30 days for application.log, 60 days for error.log
- **Total size cap**: 1GB

## 💡 Key Learnings

### 1. SLF4J with Lombok

```java
@Slf4j
public class MyService {
    public void doSomething() {
        log.info("Doing something");  // log is auto-injected
    }
}
```

### 2. Parameterized Logging

```java
// ✅ Good - Efficient
log.info("User {} performed action {}", userId, action);

// ❌ Bad - Inefficient
log.info("User " + userId + " performed action " + action);
```

### 3. Exception Logging

```java
// ✅ Good - Include exception as last parameter
try {
    riskyOperation();
} catch (Exception e) {
    log.error("Operation failed for user: {}", userId, e);
}
```

### 4. MDC for Context

```java
// Add context
MDC.put("requestId", UUID.randomUUID().toString());
MDC.put("userId", "user123");

// All logs include requestId and userId automatically

// Clean up
MDC.clear();
```

### 5. Log Levels in Production

- **TRACE/DEBUG**: Disabled (too verbose)
- **INFO**: Enabled (business events)
- **WARN**: Enabled (potential issues)
- **ERROR**: Enabled (failures)

### 6. Async Logging

```xml
<appender name="ASYNC_FILE" class="ch.qos.logback.classic.AsyncAppender">
    <queueSize>512</queueSize>
    <appender-ref ref="FILE"/>
</appender>
```

Better performance by logging in background thread.

## 🎓 Interview Preparation

After running this demo, you should understand:

1. **SLF4J vs Logback** - API vs Implementation
2. **Log Levels** - TRACE, DEBUG, INFO, WARN, ERROR and when to use each
3. **Logback Configuration** - logback-spring.xml with appenders
4. **Parameterized Logging** - Performance benefits
5. **MDC** - Request/user tracking across logs
6. **Rolling Policies** - Size and time-based rotation
7. **Structured Logging** - JSON format for log aggregation
8. **Best Practices** - What to log, what not to log, security

## 🧪 Experiments to Try

1. **Change log levels** in application.properties:
   ```properties
   logging.level.com.masterclass=TRACE
   ```

2. **Watch logs grow** in `logs/` directory

3. **Send multiple requests** with different User IDs:
   ```bash
   curl -H "X-User-Id: alice" http://localhost:8080/api/demo/with-mdc
   curl -H "X-User-Id: bob" http://localhost:8080/api/demo/with-mdc
   ```

4. **Trigger errors** and check error.log:
   ```bash
   curl "http://localhost:8080/api/demo/with-exception?shouldFail=true"
   ```

5. **Parse JSON logs** with jq:
   ```bash
   cat logs/application.json | jq '.message'
   ```

## 📚 References

- [SLF4J Documentation](http://www.slf4j.org/)
- [Logback Manual](https://logback.qos.ch/manual/)
- [Spring Boot Logging](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.logging)
- [Logstash Logback Encoder](https://github.com/logfess/logstash-logback-encoder)

---

**🎉 Module 03 Complete!** You've mastered all 7 sections of Spring Boot Fundamentals!
