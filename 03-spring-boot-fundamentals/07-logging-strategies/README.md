# Logging Strategies in Spring Boot

> **Mastering application logging with SLF4J and Logback**

## 📚 Table of Contents

- [Why Logging?](#why-logging)
- [Logging Frameworks](#logging-frameworks)
- [SLF4J](#slf4j)
- [Log Levels](#log-levels)
- [Logback Configuration](#logback-configuration)
- [Logging Best Practices](#logging-best-practices)
- [MDC (Mapped Diagnostic Context)](#mdc-mapped-diagnostic-context)
- [Structured Logging](#structured-logging)
- [Demo Project](#demo-project)
- [Interview Questions](#interview-questions)

---

## Why Logging?

Logging is essential for:
- **Debugging** - Track down issues in production
- **Monitoring** - Understand application behavior
- **Auditing** - Track user actions and system events
- **Performance** - Identify bottlenecks
- **Security** - Detect suspicious activities

### Bad Logging
```java
System.out.println("User logged in");  // ❌ No timestamp, level, or context
```

### Good Logging
```java
log.info("User '{}' logged in successfully from IP: {}", username, ipAddress);
// ✅ Timestamp, level, context, structured
```

---

## Logging Frameworks

### Evolution

```
Log4j (2001) → SLF4J (2006) → Logback (2007) → Log4j2 (2014)
```

### Spring Boot Default Stack

```
Application Code
     ↓
SLF4J (API - Simple Logging Facade for Java)
     ↓
Logback (Implementation)
```

### Why SLF4J?

- **Abstraction** - Switch implementations without code changes
- **Parameterized logging** - Better performance
- **Industry standard** - Used by most Java frameworks

---

## SLF4J

### Basic Usage

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyService {
    
    private static final Logger log = LoggerFactory.getLogger(MyService.class);
    
    public void doSomething() {
        log.info("Doing something");
    }
}
```

### With Lombok

```java
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyService {
    
    public void doSomething() {
        log.info("Doing something");  // log is automatically available
    }
}
```

### Parameterized Logging

```java
// ✅ Good - Efficient (no string concatenation if not logged)
log.debug("User {} performed action {} at {}", userId, action, timestamp);

// ❌ Bad - Inefficient (always creates string even if not logged)
log.debug("User " + userId + " performed action " + action + " at " + timestamp);
```

---

## Log Levels

### Hierarchy

```
TRACE < DEBUG < INFO < WARN < ERROR
```

### When to Use Each Level

| Level | Usage | Example |
|-------|-------|---------|
| **TRACE** | Very detailed, rarely used | Method entry/exit with all parameters |
| **DEBUG** | Detailed developer information | SQL queries, cache hits/misses |
| **INFO** | General informational messages | Application startup, user login |
| **WARN** | Potentially harmful situations | Deprecated API usage, fallback values |
| **ERROR** | Error events | Exceptions, failures |

### Examples

```java
@Slf4j
@Service
public class UserService {
    
    public User createUser(User user) {
        log.trace("createUser() called with: {}", user);  // Very detailed
        
        log.debug("Validating user: {}", user.getEmail());  // Developer info
        
        log.info("Creating new user: {}", user.getEmail());  // Important event
        
        if (userExists(user.getEmail())) {
            log.warn("User already exists: {}", user.getEmail());  // Warning
            throw new DuplicateUserException();
        }
        
        try {
            User saved = userRepository.save(user);
            log.info("User created successfully with ID: {}", saved.getId());
            return saved;
        } catch (Exception e) {
            log.error("Failed to create user: {}", user.getEmail(), e);  // Error with exception
            throw e;
        }
    }
}
```

### Setting Log Levels

**application.properties:**
```properties
# Root level (applies to all loggers)
logging.level.root=INFO

# Package level
logging.level.com.masterclass=DEBUG
logging.level.com.masterclass.controller=TRACE

# Framework logs
logging.level.org.springframework.web=DEBUG
logging.level.org.hibernate.SQL=DEBUG
```

---

## Logback Configuration

Logback is configured via `logback-spring.xml` in `src/main/resources/`.

### Basic Configuration

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    
    <!-- Console Appender -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <!-- Root Logger -->
    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
    </root>
    
</configuration>
```

### Pattern Elements

| Element | Description | Example |
|---------|-------------|---------|
| `%d` | Date/time | 2024-01-20 10:15:30 |
| `%thread` | Thread name | main |
| `%level` | Log level | INFO |
| `%logger` | Logger name | c.m.UserService |
| `%msg` | Log message | User created |
| `%n` | Line break | \n |
| `%class` | Full class name | com.masterclass.UserService |
| `%method` | Method name | createUser |
| `%line` | Line number | 42 |

### Professional Pattern

```xml
<pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
```

Output:
```
2024-01-20 10:15:30.123 [main] INFO  c.m.service.UserService - User created successfully
```

### File Appender

```xml
<appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
    <file>logs/application.log</file>
    
    <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
        <fileNamePattern>logs/application-%d{yyyy-MM-dd}.%i.log</fileNamePattern>
        <maxFileSize>10MB</maxFileSize>
        <maxHistory>30</maxHistory>
        <totalSizeCap>1GB</totalSizeCap>
    </rollingPolicy>
    
    <encoder>
        <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
    </encoder>
</appender>
```

### Separate Error Log

```xml
<appender name="ERROR_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
    <file>logs/error.log</file>
    
    <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
        <level>ERROR</level>
    </filter>
    
    <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
        <fileNamePattern>logs/error-%d{yyyy-MM-dd}.log</fileNamePattern>
        <maxHistory>60</maxHistory>
    </rollingPolicy>
    
    <encoder>
        <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
    </encoder>
</appender>
```

### Complete Configuration

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    
    <property name="LOG_PATTERN" value="%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"/>
    
    <!-- Console Appender -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>${LOG_PATTERN}</pattern>
        </encoder>
    </appender>
    
    <!-- File Appender -->
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/application.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <fileNamePattern>logs/application-%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <maxFileSize>10MB</maxFileSize>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>${LOG_PATTERN}</pattern>
        </encoder>
    </appender>
    
    <!-- Error File Appender -->
    <appender name="ERROR_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/error.log</file>
        <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
            <level>ERROR</level>
        </filter>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/error-%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>60</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>${LOG_PATTERN}</pattern>
        </encoder>
    </appender>
    
    <!-- Package-level loggers -->
    <logger name="com.masterclass" level="DEBUG"/>
    <logger name="org.springframework.web" level="DEBUG"/>
    <logger name="org.hibernate.SQL" level="DEBUG"/>
    
    <!-- Root logger -->
    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="FILE"/>
        <appender-ref ref="ERROR_FILE"/>
    </root>
    
</configuration>
```

---

## Logging Best Practices

### 1. Use Appropriate Log Levels

```java
// ✅ Good
log.info("User logged in: {}", username);      // Important event
log.debug("Cache miss for key: {}", key);      // Developer info
log.error("Payment failed for order: {}", id, exception);  // Error

// ❌ Bad
log.info("Method started");                     // Too verbose for INFO
log.error("User not found");                    // Not an error, use WARN
```

### 2. Use Parameterized Logging

```java
// ✅ Good - Efficient
log.debug("Processing user {} with role {}", userId, role);

// ❌ Bad - Inefficient
log.debug("Processing user " + userId + " with role " + role);
```

### 3. Don't Log Sensitive Data

```java
// ❌ Bad
log.info("User logged in with password: {}", password);
log.debug("Credit card: {}", creditCard);

// ✅ Good
log.info("User logged in: {}", username);
log.debug("Payment processed for order: {}", orderId);
```

### 4. Always Log Exceptions

```java
// ✅ Good - Include exception
try {
    processPayment();
} catch (PaymentException e) {
    log.error("Payment failed for order: {}", orderId, e);
    throw e;
}

// ❌ Bad - Only message
catch (PaymentException e) {
    log.error("Payment failed: {}", e.getMessage());
}
```

### 5. Use Meaningful Messages

```java
// ✅ Good - Context and details
log.info("User '{}' created successfully with ID: {} in {} ms", 
    user.getEmail(), user.getId(), duration);

// ❌ Bad - Vague
log.info("Success");
```

### 6. Don't Over-Log

```java
// ❌ Bad - Too much
log.debug("Entering method createUser");
log.debug("Validating user");
log.debug("Checking email");
log.debug("Saving user");
log.debug("User saved");
log.debug("Exiting method createUser");

// ✅ Good - Meaningful events only
log.info("Creating user: {}", email);
// business logic
log.info("User created successfully with ID: {}", id);
```

### 7. Use Logger per Class

```java
// ✅ Good
@Slf4j
public class UserService {
    // log is available for UserService
}

// ❌ Bad - Shared logger
public class UserService {
    private static final Logger log = LoggerFactory.getLogger("Application");
}
```

---

## MDC (Mapped Diagnostic Context)

MDC allows you to add contextual information to logs (e.g., user ID, request ID) that appears in all log statements.

### Basic Usage

```java
import org.slf4j.MDC;

MDC.put("userId", "12345");
MDC.put("requestId", UUID.randomUUID().toString());

log.info("Processing request");  // Will include userId and requestId

MDC.clear();  // Clean up
```

### Pattern Configuration

```xml
<pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] [%X{requestId}] [%X{userId}] %-5level %logger{36} - %msg%n</pattern>
```

Output:
```
2024-01-20 10:15:30.123 [http-nio-8080-exec-1] [abc-123] [user-456] INFO  c.m.UserService - Processing request
```

### Filter for Automatic MDC

```java
@Component
public class MdcFilter implements Filter {
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        try {
            // Add request ID
            MDC.put("requestId", UUID.randomUUID().toString());
            
            // Add user ID if authenticated
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()) {
                MDC.put("userId", auth.getName());
            }
            
            chain.doFilter(request, response);
            
        } finally {
            MDC.clear();
        }
    }
}
```

---

## Structured Logging

### JSON Logging

For better log parsing by tools like ELK, Splunk:

**pom.xml:**
```xml
<dependency>
    <groupId>net.logstash.logback</groupId>
    <artifactId>logstash-logback-encoder</artifactId>
    <version>7.4</version>
</dependency>
```

**logback-spring.xml:**
```xml
<appender name="JSON_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
    <file>logs/application.json</file>
    <encoder class="net.logstash.logback.encoder.LogstashEncoder"/>
    <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
        <fileNamePattern>logs/application-%d{yyyy-MM-dd}.json</fileNamePattern>
        <maxHistory>30</maxHistory>
    </rollingPolicy>
</appender>
```

Output:
```json
{
  "@timestamp": "2024-01-20T10:15:30.123Z",
  "level": "INFO",
  "thread_name": "main",
  "logger_name": "c.m.UserService",
  "message": "User created successfully",
  "userId": "12345",
  "requestId": "abc-123"
}
```

---

## Demo Project

See [demo-logging](demo-logging/) for a complete example with:
- SLF4J logging in all layers
- Custom logback configuration
- MDC for request tracking
- Separate error logs
- JSON structured logging
- Best practices demonstrated

---

## Interview Questions

### Q1: What is SLF4J and why use it?

**Answer:**
**SLF4J** (Simple Logging Facade for Java) is an abstraction layer for various logging frameworks.

**Benefits:**
- **Decoupling** - Change logging implementation without code changes
- **Parameterized logging** - Better performance
- **Industry standard** - Used by Spring, Hibernate, etc.

```java
// SLF4J API (doesn't change)
log.info("User: {}", userId);

// Implementation can be Logback, Log4j2, etc. (configured in pom.xml)
```

### Q2: What's the difference between log levels?

**Answer:**

| Level | Use Case | Production? |
|-------|----------|-------------|
| **TRACE** | Method entry/exit with parameters | ❌ No |
| **DEBUG** | Detailed developer information | ❌ No |
| **INFO** | Important business events | ✅ Yes |
| **WARN** | Potentially harmful situations | ✅ Yes |
| **ERROR** | Error events | ✅ Yes |

**Rule of thumb:**
- Development: DEBUG or TRACE
- Production: INFO (WARN and ERROR always visible)

### Q3: Why use parameterized logging?

**Answer:**

**Performance:**
```java
// ✅ Good - String only created if level is enabled
log.debug("User {} has {} items", userId, count);

// ❌ Bad - String always created even if DEBUG is disabled
log.debug("User " + userId + " has " + count + " items");
```

If DEBUG is disabled (common in production), the good version never creates the string, saving CPU and memory.

### Q4: How to log exceptions properly?

**Answer:**

```java
// ✅ Good - Include exception as last parameter
try {
    processPayment();
} catch (PaymentException e) {
    log.error("Payment failed for order: {}", orderId, e);
    throw e;
}

// ❌ Bad - Only message (loses stack trace)
catch (PaymentException e) {
    log.error("Payment failed: {}", e.getMessage());
}

// ❌ Bad - Exception in message (not formatted properly)
catch (PaymentException e) {
    log.error("Payment failed: " + e);
}
```

### Q5: What is MDC and when to use it?

**Answer:**
**MDC** (Mapped Diagnostic Context) adds contextual information to all log statements within a thread.

**Use cases:**
- **Request tracing** - Add request ID to all logs for that request
- **User tracking** - Add user ID to logs
- **Transaction IDs** - Track distributed transactions

```java
// Add context
MDC.put("requestId", "abc-123");
MDC.put("userId", "user-456");

log.info("Processing");  // Includes requestId and userId automatically

// Clean up (important!)
MDC.clear();
```

**Pattern:**
```xml
<pattern>%d [%X{requestId}] [%X{userId}] %-5level %logger - %msg%n</pattern>
```

### Q6: What are Logback appenders?

**Answer:**
**Appenders** define where logs are written.

| Appender | Description |
|----------|-------------|
| **ConsoleAppender** | Console/stdout |
| **FileAppender** | Single file |
| **RollingFileAppender** | Multiple files with rotation |
| **SyslogAppender** | Syslog server |
| **SMTPAppender** | Email |

**Example:**
```xml
<!-- Console -->
<appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
    <encoder><pattern>%msg%n</pattern></encoder>
</appender>

<!-- Rolling File -->
<appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
    <file>logs/app.log</file>
    <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
        <fileNamePattern>logs/app-%d{yyyy-MM-dd}.%i.log</fileNamePattern>
        <maxFileSize>10MB</maxFileSize>
        <maxHistory>30</maxHistory>
    </rollingPolicy>
</appender>
```

### Q7: How to configure different log levels for different packages?

**Answer:**

**application.properties:**
```properties
# Root level (default for all)
logging.level.root=INFO

# Your application
logging.level.com.masterclass=DEBUG
logging.level.com.masterclass.controller=TRACE

# Frameworks
logging.level.org.springframework=WARN
logging.level.org.hibernate.SQL=DEBUG
```

**logback-spring.xml:**
```xml
<logger name="com.masterclass" level="DEBUG"/>
<logger name="org.springframework.web" level="DEBUG"/>

<root level="INFO">
    <appender-ref ref="CONSOLE"/>
</root>
```

---

## Summary

| Concept | Key Points |
|---------|------------|
| **SLF4J** | Logging facade/abstraction |
| **Logback** | Default implementation in Spring Boot |
| **Log Levels** | TRACE, DEBUG, INFO, WARN, ERROR |
| **Parameterized Logging** | `log.info("User: {}", id)` for performance |
| **logback-spring.xml** | Configuration file for appenders and patterns |
| **MDC** | Contextual information across logs |
| **Appenders** | Console, File, Rolling File |
| **Best Practices** | Appropriate levels, meaningful messages, include exceptions |

Proper logging is crucial for maintaining production applications and debugging issues efficiently.

---

**Module Complete!** 🎉 You've mastered Spring Boot Fundamentals!
