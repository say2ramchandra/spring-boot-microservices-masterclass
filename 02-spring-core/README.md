# Module 02: Spring Core

> **Master the foundations of Spring Framework - Dependency Injection, Bean Management, and AOP**

## 📚 Module Overview

Spring Core is the heart of the Spring Framework. Understanding these concepts is absolutely critical for Spring Boot and Microservices development. This module dives deep into the IoC container, dependency injection patterns, bean lifecycle, and aspect-oriented programming.

## 🎯 Learning Objectives

By the end of this module, you will:

- ✅ Understand Inversion of Control (IoC) and Dependency Injection (DI)
- ✅ Master different types of dependency injection
- ✅ Understand bean lifecycle and scopes
- ✅ Work with ApplicationContext and BeanFactory
- ✅ Use Spring annotations effectively
- ✅ Implement cross-cutting concerns with AOP

## 📖 Module Contents

### 1. [Dependency Injection](01-dependency-injection/)
- What is Dependency Injection?
- Constructor Injection (Recommended)
- Setter Injection
- Field Injection
- @Autowired, @Qualifier, @Primary
- When to use which type

### 2. [Bean Lifecycle](02-bean-lifecycle/)
- Bean Scopes (Singleton, Prototype, Request, Session)
- Initialization and destruction callbacks
- @PostConstruct and @PreDestroy
- BeanPostProcessor
- Bean creation process

### 3. [ApplicationContext](03-applicationcontext/)
- BeanFactory vs ApplicationContext
- Different ApplicationContext implementations
- Context hierarchy
- Event handling
- Resource loading

### 4. [Spring Annotations](04-spring-annotations/)
- @Component, @Service, @Repository, @Controller
- @Configuration and @Bean
- @ComponentScan
- @Value and @PropertySource
- Stereotype annotations

### 5. [Aspect-Oriented Programming](05-aspect-oriented-programming/)
- What is AOP and why use it?
- AOP terminology (Aspect, Advice, Pointcut, Join Point)
- @Aspect, @Before, @After, @Around
- Real-world uses: Logging, Security, Transactions

## ⏱️ Estimated Time

**Total: 4-5 days** (with hands-on practice)

- Dependency Injection: 1.5 days
- Bean Lifecycle: 1 day
- ApplicationContext: 0.5 day
- Spring Annotations: 0.5 day
- Aspect-Oriented Programming: 1 day

## 🚀 Getting Started

### Prerequisites
- Completed Module 01 (Core Java Fundamentals)
- Java 17+
- Maven 3.8+
- Understanding of OOP principles

### Quick Start
```bash
cd 02-spring-core
cd 01-dependency-injection
# Read the README.md, then run demos
```

## 🎓 Learning Path

```
Start Here
    ↓
Dependency Injection (Core concept - master this!)
    ↓
Bean Lifecycle (Understand how Spring manages beans)
    ↓
ApplicationContext (Spring's container)
    ↓
Spring Annotations (Write less boilerplate)
    ↓
AOP (Cross-cutting concerns)
    ↓
Ready for Spring Boot! →
```

## 💡 Why These Topics?

### Dependency Injection
**The foundation of Spring!** Everything in Spring revolves around DI. You'll use it in:
- Service layer (@Service with injected repositories)
- REST controllers (@RestController with injected services)
- Configuration classes (@Configuration with @Bean methods)

### Bean Lifecycle
**Critical for understanding:**
- When beans are created
- Resource initialization (database connections, caches)
- Cleanup operations (closing connections, releasing resources)
- @PostConstruct for initialization logic

### ApplicationContext
**The Spring container that:**
- Creates and manages beans
- Resolves dependencies
- Handles events
- Spring Boot uses this under the hood

### Spring Annotations
**Reduce boilerplate and:**
- Enable component scanning
- Configure beans declaratively
- Inject dependencies cleanly
- Express intent clearly

### AOP
**Handle cross-cutting concerns:**
- Logging method entry/exit
- Security checks (@PreAuthorize)
- Transaction management (@Transactional)
- Performance monitoring
- Exception handling

## 🔗 Connection to Spring Boot

Spring Boot builds on these concepts:

| Spring Core Concept | Spring Boot Usage |
|---------------------|-------------------|
| Dependency Injection | Auto-wiring services, repositories |
| @Component, @Service | Still used everywhere |
| Bean Lifecycle | @PostConstruct for startup tasks |
| ApplicationContext | Spring Boot creates and configures it |
| @Configuration | Java-based configuration |
| AOP | @Transactional, @Async, @Cacheable |

**Important**: Spring Boot doesn't replace Spring Core - it builds on top of it with sensible defaults and auto-configuration!

## 📝 Practice Tips

1. **Start with XML config, then move to annotations** - Understanding XML helps you appreciate annotations
2. **Experiment with different injection types** - See pros/cons firsthand
3. **Debug bean creation** - Use breakpoints to watch Spring's magic
4. **Read stack traces carefully** - Spring exceptions are verbose but helpful
5. **Use Spring Boot only after mastering Core** - Don't skip foundations!

## ✅ Self-Assessment

After completing this module, you should be able to:

- [ ] Explain what Inversion of Control means
- [ ] Choose the right type of dependency injection
- [ ] Configure beans using annotations and Java config
- [ ] Understand bean scopes and lifecycle
- [ ] Create custom AOP aspects for logging
- [ ] Debug Spring context initialization issues
- [ ] Explain difference between @Component and @Bean

## 🔗 Next Steps

Once you complete this module, proceed to:
- **[Module 03: Spring Boot Fundamentals](../03-spring-boot-fundamentals/)** - Build REST APIs with Spring Boot

---

## 🎯 Key Concepts Summary

### Dependency Injection
**What**: Objects receive their dependencies from external sources rather than creating them.

**Why**: Loose coupling, testability, maintainability.

**How in Spring**:
```java
@Service
public class UserService {
    private final UserRepository repository;
    
    // Constructor injection (recommended)
    public UserService(UserRepository repository) {
        this.repository = repository;
    }
}
```

### Bean
**What**: An object managed by Spring IoC container.

**Why**: Spring handles creation, configuration, and lifecycle.

**How to define**:
```java
// Method 1: Component scanning
@Component
public class MyBean { }

// Method 2: Configuration class
@Configuration
public class AppConfig {
    @Bean
    public MyBean myBean() {
        return new MyBean();
    }
}
```

### ApplicationContext
**What**: The Spring IoC container that manages beans.

**Why**: Central orchestrator for your application.

**How to access**:
```java
ApplicationContext context = 
    new AnnotationConfigApplicationContext(AppConfig.class);
MyBean bean = context.getBean(MyBean.class);
```

---

**Ready to begin? Start with [Dependency Injection →](01-dependency-injection/)**

_Master Spring Core, and Spring Boot becomes easy!_
