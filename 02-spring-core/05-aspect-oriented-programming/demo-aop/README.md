# AOP Demo

## Overview
Comprehensive demonstration of Aspect-Oriented Programming with Spring AOP, showing all advice types and real-world use cases.

## Running the Demo

```bash
cd 05-aspect-oriented-programming/demo-aop
mvn clean compile exec:java
```

## What You'll Learn

### Part 1: @Before Advice
- ✅ Executes before method execution
- ✅ Access method name and arguments
- ✅ Use case: Logging, validation, security checks

### Part 2: @AfterReturning Advice
- ✅ Executes after successful return
- ✅ Access return value
- ✅ Use case: Success logging, result processing

### Part 3: @After Advice
- ✅ Executes after method (always - even with exceptions)
- ✅ Similar to `finally` block
- ✅ Use case: Cleanup, audit logging

### Part 4: @AfterThrowing Advice
- ✅ Executes when exception is thrown
- ✅ Access exception object
- ✅ Use case: Error logging, alerting, monitoring

### Part 5: @Around Advice
- ✅ Wraps method execution
- ✅ Most powerful - can prevent execution
- ✅ Performance monitoring example
- ✅ Use case: Transactions, caching, retry logic

### Part 6: Combined Pointcuts
- ✅ Reusable pointcut definitions
- ✅ Combining with AND/OR/NOT
- ✅ Security aspect example

## Key Concepts Demonstrated

### 1. Advice Execution Order

```
@Around (before proceed())
    ↓
@Before
    ↓
Method Execution
    ↓
@Around (after proceed())
    ↓
@After
    ↓
@AfterReturning (if success) OR @AfterThrowing (if exception)
```

### 2. Pointcut Expressions

```java
// All methods in UserService
execution(* com.masterclass.aop.service.UserService.*(..))

// Methods starting with 'get'
execution(* com.masterclass.aop.service.UserService.get*(..))

// Methods starting with 'delete'
execution(* com.masterclass.aop.service.UserService.delete*(..))

// Combine pointcuts
deleteMethods() || updateMethods()
```

### 3. Accessing Join Point Information

```java
@Before("...")
public void logBefore(JoinPoint joinPoint) {
    String methodName = joinPoint.getSignature().getName();
    Object[] args = joinPoint.getArgs();
    String className = joinPoint.getTarget().getClass().getName();
}
```

### 4. Performance Monitoring Pattern

```java
@Around("...")
public Object measureTime(ProceedingJoinPoint pjp) throws Throwable {
    long start = System.currentTimeMillis();
    Object result = pjp.proceed(); // Execute method
    long time = System.currentTimeMillis() - start;
    log.info("Execution time: {}ms", time);
    return result;
}
```

## Expected Output

```
╔══════════════════════════════════════════════╗
║      Aspect-Oriented Programming Demo       ║
╚══════════════════════════════════════════════╝

==================================================
PART 1: @Before Advice
==================================================
Aspect executes BEFORE the method

   [Performance] ⏱️  Starting timer for: createUser()
   [Logging] → Method called: createUser() with args: [alice]
   [Business Logic] Creating user: alice
   [Logging] ✓ Method completed: createUser()
   [Performance] ⏱️  Execution time: 5ms

==================================================
PART 2: @AfterReturning Advice
==================================================
Aspect executes AFTER successful return

   [Performance] ⏱️  Starting timer for: getUser()
   [Logging] → Method called: getUser() with args: [123]
   [Business Logic] Getting user: 123
   [Logging] ← Method returned: getUser() => User-123
   [Logging] ✓ Method completed: getUser()
   [Performance] ⏱️  Execution time: 3ms
   [Main] Result received: User-123
...
```

## Aspects Implemented

### 1. LoggingAspect
- **@Before**: Logs method entry with arguments
- **@After**: Logs method completion
- **@AfterReturning**: Logs return values
- **@AfterThrowing**: Logs exceptions

### 2. PerformanceAspect
- **@Around**: Measures execution time
- Detects slow methods (>100ms)
- Logs performance warnings

### 3. SecurityAspect
- **@Pointcut**: Defines reusable pointcuts
- **@Before**: Security checks for sensitive operations
- Combines pointcuts with OR operator

## Real-World Applications

### 1. Method Logging
```java
@Aspect
@Component
public class LoggingAspect {
    @Before("execution(* com.example..*(..))")
    public void logMethodCall(JoinPoint jp) {
        log.info("Calling: {}", jp.getSignature());
    }
}
```

### 2. Performance Monitoring
```java
@Aspect
@Component
public class PerformanceAspect {
    @Around("@annotation(com.example.Monitored)")
    public Object monitor(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = pjp.proceed();
        long time = System.currentTimeMillis() - start;
        metricsService.recordTime(pjp.getSignature(), time);
        return result;
    }
}
```

### 3. Transaction Management (Spring does this)
```java
@Around("@annotation(org.springframework.transaction.annotation.Transactional)")
public Object manageTransaction(ProceedingJoinPoint pjp) throws Throwable {
    transactionManager.begin();
    try {
        Object result = pjp.proceed();
        transactionManager.commit();
        return result;
    } catch (Exception e) {
        transactionManager.rollback();
        throw e;
    }
}
```

### 4. Security Checks
```java
@Before("@annotation(com.example.Secured)")
public void checkSecurity(JoinPoint jp) {
    if (!securityContext.isAuthorized()) {
        throw new SecurityException("Unauthorized");
    }
}
```

## Best Practices Shown

1. ✅ **Focused aspects** - Each aspect has single responsibility
2. ✅ **Specific pointcuts** - Target specific methods
3. ✅ **Reusable pointcuts** - Define once, use multiple times
4. ✅ **@Around best practices** - Always call proceed()
5. ✅ **Clear logging** - Shows aspect execution flow

## Common Interview Questions

**Q: What's the difference between @Before and @Around?**
A: @Before runs before method, can't prevent execution. @Around can control if/when method executes.

**Q: Must @Around call proceed()?**
A: Yes, unless you intentionally want to skip method execution (e.g., returning cached value).

**Q: Order of advice execution?**
A: @Around(before) → @Before → Method → @Around(after) → @After → @AfterReturning/@AfterThrowing

**Q: Can aspects have side effects?**
A: Yes, but keep them focused on cross-cutting concerns. Don't put business logic in aspects.

**Q: How to order multiple aspects?**
A: Use `@Order` annotation. Lower number = higher priority.

**Q: What's a JoinPoint vs ProceedingJoinPoint?**
A: ProceedingJoinPoint (only for @Around) has proceed() method to execute target method.

## Performance Considerations

- ✅ AOP adds minimal overhead (proxy creation)
- ✅ Use specific pointcuts (avoid `execution(* *(..))`)
- ✅ @Around is slightly more expensive than @Before/@After
- ✅ Spring caches proxies for singleton beans

## Integration with Spring Boot

In Spring Boot, many features use AOP:
- `@Transactional` - Transaction management
- `@Cacheable` - Caching
- `@Async` - Asynchronous execution
- `@PreAuthorize` - Security
- `@Scheduled` - Scheduled tasks

## Next Steps

1. Run the demo and observe aspect execution
2. Modify pointcut expressions
3. Create your own custom aspects
4. Add exception handling aspects
5. Complete [Module 03: Spring Boot Fundamentals](../../03-spring-boot-fundamentals/)

---

**🎉 Module 02 Complete!** You've mastered Spring Core fundamentals. Ready for Spring Boot!
