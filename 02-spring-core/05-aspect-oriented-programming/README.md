# Aspect-Oriented Programming (AOP)

## Overview
Master AOP to handle cross-cutting concerns like logging, security, transactions, and monitoring in a clean, modular way.

## Duration: 1 Day

---

## Table of Contents
1. [What is AOP?](#what-is-aop)
2. [AOP Terminology](#aop-terminology)
3. [AOP in Spring](#aop-in-spring)
4. [Advice Types](#advice-types)
5. [Pointcut Expressions](#pointcut-expressions)
6. [Real-World Use Cases](#real-world-use-cases)
7. [Best Practices](#best-practices)

---

## What is AOP?

**Aspect-Oriented Programming (AOP)** is a programming paradigm that increases modularity by allowing separation of cross-cutting concerns.

### The Problem

Without AOP, cross-cutting concerns (logging, security, transactions) are scattered across your codebase:

```java
public class UserService {
    public User createUser(String name) {
        // Logging (duplicated in every method)
        log.info("Creating user: " + name);
        
        // Security check (duplicated)
        if (!securityContext.isAuthorized()) {
            throw new SecurityException();
        }
        
        // Transaction management (duplicated)
        transactionManager.begin();
        try {
            User user = new User(name);
            repository.save(user);
            transactionManager.commit();
            
            // Logging (duplicated)
            log.info("User created: " + user.getId());
            
            return user;
        } catch (Exception e) {
            transactionManager.rollback();
            throw e;
        }
    }
    
    // Every method has similar boilerplate!
}
```

### The Solution with AOP

AOP extracts cross-cutting concerns into separate aspects:

```java
@Service
public class UserService {
    public User createUser(String name) {
        // Only business logic!
        User user = new User(name);
        repository.save(user);
        return user;
    }
}

@Aspect
@Component
public class LoggingAspect {
    @Before("execution(* com.example.service.*.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        log.info("Calling: " + joinPoint.getSignature().getName());
    }
}
```

**Benefits**:
- ✅ **DRY** - Don't Repeat Yourself
- ✅ **Single Responsibility** - Each class focuses on its core concern
- ✅ **Maintainability** - Change logging in one place
- ✅ **Modularity** - Aspects can be added/removed easily

---

## AOP Terminology

### 1. Aspect
**What**: A module that encapsulates cross-cutting concerns.

```java
@Aspect
@Component
public class LoggingAspect {
    // Logging concern in one place
}
```

### 2. Join Point
**What**: A point during program execution (method call, exception thrown, etc.).

In Spring AOP: **Always a method execution**.

```java
// Every method call is a potential join point
userService.createUser("John");  // Join point
userService.deleteUser(123);     // Join point
```

### 3. Advice
**What**: Action taken by an aspect at a join point.

**Types**: @Before, @After, @AfterReturning, @AfterThrowing, @Around

```java
@Before("execution(* com.example.service.*.*(..))")
public void logBefore(JoinPoint joinPoint) {
    // This is the advice
    log.info("Method called: " + joinPoint.getSignature().getName());
}
```

### 4. Pointcut
**What**: Expression that selects join points.

```java
@Pointcut("execution(* com.example.service.*.*(..))")
public void serviceMethods() {}

@Before("serviceMethods()")
public void logBefore() {
    // Executes before all service methods
}
```

### 5. Weaving
**What**: Process of applying aspects to target objects.

**Types**:
- **Compile-time**: AspectJ compiler
- **Load-time**: Class loader
- **Runtime**: Spring AOP (uses proxies)

Spring AOP uses **runtime weaving** with JDK dynamic proxies or CGLIB.

---

## AOP in Spring

### Setup

**Maven Dependency**:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
```

**Enable AOP**:
```java
@Configuration
@EnableAspectJAutoProxy
public class AopConfig {
}
```

In Spring Boot, AOP is auto-configured if the dependency is present.

### Creating an Aspect

```java
@Aspect
@Component
public class LoggingAspect {
    
    @Before("execution(* com.example.service.*.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        log.info("Calling {}() with args: {}", methodName, Arrays.toString(args));
    }
}
```

---

## Advice Types

### 1. @Before

**Executes before** the method:

```java
@Before("execution(* com.example.service.UserService.createUser(..))")
public void beforeCreateUser(JoinPoint joinPoint) {
    log.info("About to create user");
    // Runs before createUser()
}
```

**Use cases**: Validation, logging, security checks

### 2. @After

**Executes after** the method (always - even if exception):

```java
@After("execution(* com.example.service.*.*(..))")
public void afterMethod(JoinPoint joinPoint) {
    log.info("Method completed: " + joinPoint.getSignature().getName());
    // Runs after method, regardless of outcome
}
```

**Use cases**: Cleanup, audit logging

### 3. @AfterReturning

**Executes after successful** return:

```java
@AfterReturning(
    pointcut = "execution(* com.example.service.UserService.createUser(..))",
    returning = "result"
)
public void afterReturningUser(JoinPoint joinPoint, User result) {
    log.info("User created successfully: " + result.getId());
    // Only runs if method returns normally (no exception)
}
```

**Use cases**: Success logging, result transformation, caching

### 4. @AfterThrowing

**Executes when exception** is thrown:

```java
@AfterThrowing(
    pointcut = "execution(* com.example.service.*.*(..))",
    throwing = "exception"
)
public void afterThrowingException(JoinPoint joinPoint, Exception exception) {
    log.error("Exception in {}: {}", 
        joinPoint.getSignature().getName(), 
        exception.getMessage());
    // Send alert, log to monitoring system
}
```

**Use cases**: Exception logging, alerting, error reporting

### 5. @Around

**Most powerful** - wraps method execution:

```java
@Around("execution(* com.example.service.*.*(..))")
public Object aroundMethod(ProceedingJoinPoint joinPoint) throws Throwable {
    long startTime = System.currentTimeMillis();
    
    log.info("Method starting: " + joinPoint.getSignature().getName());
    
    try {
        Object result = joinPoint.proceed(); // Call actual method
        
        long endTime = System.currentTimeMillis();
        log.info("Method completed in {}ms", (endTime - startTime));
        
        return result;
    } catch (Exception e) {
        log.error("Method failed: " + e.getMessage());
        throw e;
    }
}
```

**Use cases**: Performance monitoring, transactions, caching, retry logic

**Important**: Must call `joinPoint.proceed()` to execute the method!

---

## Pointcut Expressions

### Execution Pointcut

**Syntax**: `execution(modifiers? return-type declaring-type? method-name(params) throws?)`

```java
// All methods in UserService
execution(* com.example.service.UserService.*(..))

// All methods returning User
execution(com.example.model.User com.example..*(..))

// All public methods
execution(public * com.example..*(..))

// All methods in service package and subpackages
execution(* com.example.service..*(..))

// Methods starting with 'create'
execution(* com.example..*create*(..))

// Methods with exactly one String parameter
execution(* com.example..*(String))

// Methods with any number of parameters
execution(* com.example..*(..))

// Methods with no parameters
execution(* com.example..*())
```

### Within Pointcut

**Matches** types within certain packages:

```java
// All methods in service package
@Before("within(com.example.service.*)")

// All methods in service package and subpackages
@Before("within(com.example.service..*)")
```

### Annotation Pointcut

**Matches** methods/classes with annotations:

```java
// Methods annotated with @Transactional
@Around("@annotation(org.springframework.transaction.annotation.Transactional)")

// Methods in classes annotated with @Service
@Before("@within(org.springframework.stereotype.Service)")

// Method with @Loggable annotation
@Before("@annotation(com.example.Loggable)")
```

### Bean Pointcut

**Matches** specific beans:

```java
// Methods in beans named 'userService'
@Before("bean(userService)")

// Methods in beans ending with 'Service'
@Before("bean(*Service)")
```

### Combining Pointcuts

```java
@Aspect
@Component
public class SecurityAspect {
    
    // Reusable pointcut
    @Pointcut("execution(* com.example.service.*.*(..))")
    public void serviceMethods() {}
    
    @Pointcut("@annotation(com.example.Secured)")
    public void securedMethods() {}
    
    // AND
    @Before("serviceMethods() && securedMethods()")
    public void checkSecurity() {
        // Applies to service methods that are @Secured
    }
    
    // OR
    @Before("serviceMethods() || securedMethods()")
    public void logAccess() {
        // Applies to either service methods OR @Secured methods
    }
    
    // NOT
    @Before("serviceMethods() && !within(com.example.service.internal.*)")
    public void publicServiceMethods() {
        // Service methods NOT in internal package
    }
}
```

---

## Real-World Use Cases

### 1. Logging Aspect

```java
@Aspect
@Component
@Slf4j
public class LoggingAspect {
    
    @Before("execution(* com.example.service.*.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        log.info("→ {}() called with args: {}", 
            joinPoint.getSignature().getName(),
            Arrays.toString(joinPoint.getArgs()));
    }
    
    @AfterReturning(pointcut = "execution(* com.example.service.*.*(..))", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        log.info("← {}() returned: {}", 
            joinPoint.getSignature().getName(), result);
    }
}
```

### 2. Performance Monitoring

```java
@Aspect
@Component
@Slf4j
public class PerformanceAspect {
    
    @Around("execution(* com.example.service.*.*(..))")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        
        Object result = joinPoint.proceed();
        
        long executionTime = System.currentTimeMillis() - start;
        log.info("⏱️  {}() took {}ms", 
            joinPoint.getSignature().getName(), executionTime);
        
        if (executionTime > 1000) {
            log.warn("⚠️  Slow method detected: {}ms", executionTime);
        }
        
        return result;
    }
}
```

### 3. Exception Handling

```java
@Aspect
@Component
@Slf4j
public class ExceptionAspect {
    
    @AfterThrowing(
        pointcut = "execution(* com.example.service.*.*(..))",
        throwing = "exception"
    )
    public void handleException(JoinPoint joinPoint, Exception exception) {
        log.error("❌ Exception in {}(): {}", 
            joinPoint.getSignature().getName(),
            exception.getMessage());
        
        // Send to monitoring system
        monitoringService.reportError(
            joinPoint.getSignature().toString(),
            exception
        );
        
        // Send alert if critical
        if (exception instanceof CriticalException) {
            alertService.sendAlert("Critical error in " + 
                joinPoint.getSignature().getName());
        }
    }
}
```

### 4. Security Aspect

```java
@Aspect
@Component
public class SecurityAspect {
    
    @Autowired
    private SecurityContext securityContext;
    
    @Before("@annotation(com.example.Secured)")
    public void checkSecurity(JoinPoint joinPoint) {
        if (!securityContext.isAuthenticated()) {
            throw new SecurityException("User not authenticated");
        }
        
        Secured secured = getSecuredAnnotation(joinPoint);
        String requiredRole = secured.role();
        
        if (!securityContext.hasRole(requiredRole)) {
            throw new SecurityException("Insufficient permissions: requires " + requiredRole);
        }
    }
}

// Usage
@Service
public class AdminService {
    
    @Secured(role = "ADMIN")
    public void deleteAllUsers() {
        // Only admins can execute this
    }
}
```

### 5. Caching Aspect

```java
@Aspect
@Component
public class CachingAspect {
    
    private final Map<String, Object> cache = new ConcurrentHashMap<>();
    
    @Around("@annotation(com.example.Cacheable)")
    public Object cacheResult(ProceedingJoinPoint joinPoint) throws Throwable {
        String key = generateKey(joinPoint);
        
        if (cache.containsKey(key)) {
            log.info("Cache hit for: " + key);
            return cache.get(key);
        }
        
        log.info("Cache miss for: " + key);
        Object result = joinPoint.proceed();
        cache.put(key, result);
        
        return result;
    }
    
    private String generateKey(ProceedingJoinPoint joinPoint) {
        return joinPoint.getSignature().toString() + 
               Arrays.toString(joinPoint.getArgs());
    }
}
```

### 6. Retry Logic

```java
@Aspect
@Component
public class RetryAspect {
    
    @Around("@annotation(com.example.Retry)")
    public Object retry(ProceedingJoinPoint joinPoint) throws Throwable {
        Retry retry = getRetryAnnotation(joinPoint);
        int maxAttempts = retry.maxAttempts();
        long delay = retry.delay();
        
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                return joinPoint.proceed();
            } catch (Exception e) {
                if (attempt == maxAttempts) {
                    throw e;
                }
                log.warn("Attempt {} failed, retrying in {}ms", attempt, delay);
                Thread.sleep(delay);
            }
        }
        throw new RuntimeException("Should not reach here");
    }
}

// Usage
@Service
public class ExternalApiService {
    
    @Retry(maxAttempts = 3, delay = 1000)
    public String callExternalApi() {
        // May fail, will retry 3 times
        return restTemplate.getForObject(url, String.class);
    }
}
```

---

## Best Practices

### 1. Keep Aspects Focused

```java
// ✅ Good - Single responsibility
@Aspect
@Component
public class LoggingAspect {
    // Only logging
}

@Aspect
@Component
public class SecurityAspect {
    // Only security
}

// ❌ Bad - Multiple concerns
@Aspect
@Component
public class EverythingAspect {
    // Logging, security, performance, transactions...
}
```

### 2. Use Specific Pointcuts

```java
// ✅ Good - Specific
@Before("execution(* com.example.service.UserService.createUser(..))")

// ❌ Bad - Too broad
@Before("execution(* *(..))")
```

### 3. Order Your Aspects

```java
@Aspect
@Component
@Order(1) // Executes first
public class SecurityAspect { }

@Aspect
@Component
@Order(2) // Executes second
public class LoggingAspect { }
```

### 4. Don't Overuse AOP

```java
// ✅ Good use: Cross-cutting concerns
@Aspect - Logging, security, transactions

// ❌ Bad use: Business logic
@Aspect - Calculating prices, validating business rules
```

### 5. Handle @Around Carefully

```java
// ✅ Always call proceed()
@Around("...")
public Object around(ProceedingJoinPoint pjp) throws Throwable {
    Object result = pjp.proceed(); // Call actual method
    return result;
}

// ❌ Forgot to call proceed()
@Around("...")
public Object around(ProceedingJoinPoint pjp) {
    return null; // Method never executes!
}
```

### 6. Test Your Aspects

```java
@SpringBootTest
public class LoggingAspectTest {
    
    @Autowired
    private UserService userService;
    
    @Test
    public void testLoggingAspect() {
        // Verify aspect executes
        userService.createUser("test");
        // Check logs
    }
}
```

---

## Spring Boot Integration

### Auto-Configuration

Spring Boot automatically configures AOP if dependency is present:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
```

### Built-in AOP Features

Spring Boot uses AOP for:
- `@Transactional` - Transaction management
- `@Cacheable` - Method-level caching
- `@Async` - Asynchronous execution
- `@Scheduled` - Scheduled tasks
- `@PreAuthorize` - Security

---

## Common Interview Questions

**Q: What is AOP and why use it?**
A: AOP separates cross-cutting concerns (logging, security) from business logic, improving modularity and maintainability.

**Q: Difference between @Before and @Around?**
A: @Before runs before method, can't prevent execution. @Around wraps method, can prevent execution and modify result.

**Q: What are join points in Spring AOP?**
A: Method executions only. AspectJ supports more (field access, constructor calls).

**Q: How does Spring implement AOP?**
A: Runtime proxies (JDK dynamic proxy or CGLIB). Creates proxy around target object.

**Q: When to use AOP vs inheritance?**
A: AOP for cross-cutting concerns affecting multiple unrelated classes. Inheritance for shared behavior in class hierarchy.

**Q: What's weaving?**
A: Process of applying aspects. Spring uses runtime weaving via proxies.

**Q: Can aspects be applied to private methods?**
A: No in Spring AOP (proxy-based). Yes in AspectJ (bytecode weaving).

---

## See Demo

Check out the [demo project](demo-aop/) for complete working examples!

---

**🎉 Congratulations!** You've completed Module 02: Spring Core

**Next**: [Module 03: Spring Boot Fundamentals →](../../03-spring-boot-fundamentals/)
