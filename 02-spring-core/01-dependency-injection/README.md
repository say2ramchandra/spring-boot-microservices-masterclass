# Dependency Injection

> **Master the core principle that powers Spring Framework**

## 📚 Table of Contents

1. [What is Dependency Injection?](#what-is-dependency-injection)
2. [Why Dependency Injection?](#why-dependency-injection)
3. [Types of Dependency Injection](#types-of-dependency-injection)
4. [Spring DI Annotations](#spring-di-annotations)
5. [Best Practices](#best-practices)
6. [Real-World Scenarios](#real-world-scenarios)

---

## What is Dependency Injection?

### Definition

**Dependency Injection (DI)** is a design pattern where objects receive their dependencies from external sources rather than creating them internally.

### The Problem Without DI

```java
// ❌ TIGHT COUPLING - BAD
public class UserService {
    private UserRepository repository = new UserRepositoryImpl();
    // UserService is tightly coupled to UserRepositoryImpl
    // Hard to test, hard to change implementation
}
```

### The Solution With DI

```java
// ✅ LOOSE COUPLING - GOOD
public class UserService {
    private final UserRepository repository;
    
    public UserService(UserRepository repository) {
        this.repository = repository;  // Injected from outside
    }
}
```

### Visualizing Dependency Injection

```
Without DI:
    UserService
       |
       | creates/owns
       ↓
    UserRepository (concrete)


With DI (Inversion of Control):
    Spring Container
       |
       | provides
       ↓
    UserRepository → UserService
    
    UserService doesn't create UserRepository
    Spring injects it!
```

---

## Why Dependency Injection?

### Benefits

#### 1. **Loose Coupling**
Components depend on interfaces, not concrete implementations.

```java
// Service depends on interface, not implementation
public class OrderService {
    private final PaymentGateway gateway;  // Interface
    
    public OrderService(PaymentGateway gateway) {
        this.gateway = gateway;
    }
}

// Can easily swap implementations:
// - StripePaymentGateway
// - PayPalPaymentGateway
// - MockPaymentGateway (for testing)
```

#### 2. **Testability**
Easy to inject mock dependencies for unit testing.

```java
@Test
void testOrderService() {
    // Easy to test with mock
    PaymentGateway mockGateway = new MockPaymentGateway();
    OrderService service = new OrderService(mockGateway);
    
    // Test service without real payment processing
}
```

#### 3. **Maintainability**
Changes to dependencies don't affect dependent classes.

#### 4. **Reusability**
Components can be reused with different dependencies.

#### 5. **Single Responsibility**
Objects focus on their core responsibility, not dependency creation.

---

## Types of Dependency Injection

Spring supports three types of dependency injection:

### 1. Constructor Injection ⭐ (RECOMMENDED)

**Definition**: Dependencies are provided through class constructor.

```java
@Service
public class UserService {
    private final UserRepository userRepository;
    private final EmailService emailService;
    
    // Constructor injection
    @Autowired  // Optional in Spring 4.3+ if only one constructor
    public UserService(UserRepository userRepository, 
                      EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }
}
```

**Advantages**:
- ✅ Immutable dependencies (final fields)
- ✅ Required dependencies guaranteed (NullPointerException impossible)
- ✅ Easy to test (plain Java instantiation)
- ✅ Circular dependencies detected at startup
- ✅ Spring team recommendation

**When to use**: **ALWAYS** (unless you have a specific reason not to)

---

### 2. Setter Injection

**Definition**: Dependencies are provided through setter methods.

```java
@Service
public class ProductService {
    private ProductRepository productRepository;
    
    // Setter injection
    @Autowired
    public void setProductRepository(ProductRepository repository) {
        this.productRepository = repository;
    }
}
```

**Advantages**:
- ✅ Optional dependencies
- ✅ Can change dependencies at runtime
- ✅ Useful for circular dependencies (not recommended to have them though)

**Disadvantages**:
- ❌ Dependencies not final (mutable)
- ❌ Possible NullPointerException if setter not called
- ❌ Less clear which dependencies are required

**When to use**: For optional dependencies only

---

### 3. Field Injection

**Definition**: Dependencies injected directly into fields.

```java
@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;  // Field injection
    
    @Autowired
    private EmailService emailService;
}
```

**Advantages**:
- ✅ Less boilerplate code
- ✅ Clean looking

**Disadvantages**:
- ❌ Hard to test (requires reflection or Spring context)
- ❌ Violates immutability (not final)
- ❌ Hides dependencies
- ❌ Can't detect circular dependencies
- ❌ Not recommended by Spring team

**When to use**: Avoid if possible (use constructor injection instead)

---

## Spring DI Annotations

### @Autowired

Marks a dependency to be injected by Spring.

```java
@Service
public class BookService {
    private final BookRepository repository;
    
    @Autowired  // Can be omitted if single constructor
    public BookService(BookRepository repository) {
        this.repository = repository;
    }
}
```

**Optional Dependencies**:
```java
@Autowired(required = false)
private OptionalService optionalService;
```

---

### @Qualifier

Used when multiple beans of the same type exist.

```java
// Two implementations of PaymentGateway
@Component("stripe")
public class StripePaymentGateway implements PaymentGateway { }

@Component("paypal")
public class PayPalPaymentGateway implements PaymentGateway { }

// Specify which one to inject
@Service
public class OrderService {
    private final PaymentGateway gateway;
    
    public OrderService(@Qualifier("stripe") PaymentGateway gateway) {
        this.gateway = gateway;
    }
}
```

---

### @Primary

Marks a bean as the default when multiple candidates exist.

```java
@Primary
@Component
public class StripePaymentGateway implements PaymentGateway { }

@Component
public class PayPalPaymentGateway implements PaymentGateway { }

// Will inject StripePaymentGateway (marked as @Primary)
@Service
public class OrderService {
    public OrderService(PaymentGateway gateway) {
        this.gateway = gateway;  // Gets StripePaymentGateway
    }
}
```

---

### @Value

Injects values from properties files.

```java
@Service
public class ConfigService {
    @Value("${app.name}")
    private String appName;
    
    @Value("${app.version:1.0}")  // With default value
    private String appVersion;
    
    @Value("${app.max.connections:100}")
    private int maxConnections;
}
```

---

### @Configuration and @Bean

Java-based configuration for creating beans.

```java
@Configuration
public class AppConfig {
    
    @Bean
    public UserService userService(UserRepository repository) {
        return new UserService(repository);
    }
    
    @Bean
    public UserRepository userRepository() {
        return new UserRepositoryImpl();
    }
}
```

---

## Best Practices

### ✅ DO:

#### 1. **Use Constructor Injection**
```java
@Service
public class OrderService {
    private final OrderRepository repository;  // final = immutable
    
    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }
}
```

#### 2. **Depend on Interfaces**
```java
// Good - depends on interface
private final UserRepository repository;

// Bad - depends on concrete class
private final JpaUserRepository repository;
```

#### 3. **Make Dependencies Final**
```java
private final UserService userService;  // Immutable
```

#### 4. **Use @Qualifier When Needed**
```java
public OrderService(@Qualifier("stripe") PaymentGateway gateway) {
    this.gateway = gateway;
}
```

#### 5. **Keep Constructors Simple**
```java
// Just assign dependencies, no logic
public UserService(UserRepository repo, EmailService email) {
    this.repository = repo;
    this.emailService = email;
}
```

---

### ❌ DON'T:

#### 1. **Avoid Field Injection**
```java
// Avoid this
@Autowired
private UserRepository repository;

// Use this instead
private final UserRepository repository;

public UserService(UserRepository repository) {
    this.repository = repository;
}
```

#### 2. **Don't Create Circular Dependencies**
```java
// BAD - circular dependency
@Service
public class A {
    @Autowired
    private B b;
}

@Service
public class B {
    @Autowired
    private A a;  // A needs B, B needs A - circular!
}
```

#### 3. **Don't Inject Too Many Dependencies**
```java
// Bad - too many dependencies (code smell)
public OrderService(Dep1 d1, Dep2 d2, Dep3 d3, Dep4 d4, 
                    Dep5 d5, Dep6 d6, Dep7 d7) {
    // If you need this many, refactor your class!
}
```

**Guideline**: More than 5-7 dependencies suggests the class has too many responsibilities.

---

## Real-World Scenarios

### Scenario 1: E-Commerce Order Processing

```java
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final PaymentGateway paymentGateway;
    private final EmailService emailService;
    private final InventoryService inventoryService;
    
    public OrderService(OrderRepository orderRepository,
                       PaymentGateway paymentGateway,
                       EmailService emailService,
                       InventoryService inventoryService) {
        this.orderRepository = orderRepository;
        this.paymentGateway = paymentGateway;
        this.emailService = emailService;
        this.inventoryService = inventoryService;
    }
    
    public Order processOrder(Order order) {
        // Check inventory
        inventoryService.reserve(order.getItems());
        
        // Process payment
        paymentGateway.charge(order.getTotal());
        
        // Save order
        order = orderRepository.save(order);
        
        // Send confirmation
        emailService.sendOrderConfirmation(order);
        
        return order;
    }
}
```

**Benefits**:
- Easy to test with mocks
- Easy to swap payment gateway (Stripe → PayPal)
- Clear dependencies
- Immutable service

### Scenario 2: Multiple Database Implementations

```java
// Interface
public interface UserRepository {
    User findById(Long id);
    User save(User user);
}

// SQL implementation
@Repository
@Qualifier("sql")
public class SqlUserRepository implements UserRepository {
    // JDBC/JPA implementation
}

// NoSQL implementation
@Repository
@Qualifier("mongo")
public class MongoUserRepository implements UserRepository {
    // MongoDB implementation
}

// Service can use either
@Service
public class UserService {
    public UserService(@Qualifier("sql") UserRepository repository) {
        this.repository = repository;
    }
}
```

### Scenario 3: Feature Flags

```java
@Configuration
public class FeatureConfig {
    
    @Bean
    @ConditionalOnProperty(name = "feature.new-payment", havingValue = "true")
    public PaymentGateway newPaymentGateway() {
        return new StripePaymentGateway();
    }
    
    @Bean
    @ConditionalOnProperty(name = "feature.new-payment", havingValue = "false")
    public PaymentGateway oldPaymentGateway() {
        return new LegacyPaymentGateway();
    }
}
```

---

## Comparison Table

| Feature | Constructor | Setter | Field |
|---------|-------------|--------|-------|
| **Immutability** | ✅ Yes (final) | ❌ No | ❌ No |
| **Required deps** | ✅ Guaranteed | ❌ Optional | ❌ Optional |
| **Testability** | ✅ Excellent | ⚠️ Good | ❌ Poor |
| **Boilerplate** | ⚠️ More code | ⚠️ More code | ✅ Less code |
| **Circular deps** | ✅ Detected | ⚠️ Hidden | ⚠️ Hidden |
| **Spring recommendation** | ✅ Yes | ⚠️ Conditional | ❌ No |

---

## Demo Projects

Explore these hands-on demos:

1. **[demo-constructor-injection](demo-constructor-injection/)** - Constructor injection examples
2. **[demo-setter-injection](demo-setter-injection/)** - Setter injection use cases
3. **[demo-field-injection](demo-field-injection/)** - Field injection (anti-pattern)
4. **[demo-qualifier-primary](demo-qualifier-primary/)** - Multiple bean resolution
5. **[demo-real-world-ecommerce](demo-real-world-ecommerce/)** - Complete e-commerce service

---

## Interview Questions

### Q1: What is Dependency Injection?
**A:** DI is a design pattern where objects receive their dependencies from external sources (Spring container) rather than creating them. It promotes loose coupling and testability.

### Q2: What's the difference between @Autowired and @Inject?
**A:** Both work similarly. @Autowired is Spring-specific; @Inject is from JSR-330 (Java standard). @Autowired has more features (required=false). Use @Autowired in Spring applications.

### Q3: Which injection type is recommended?
**A:** Constructor injection. It ensures immutability, makes required dependencies explicit, and is easier to test.

### Q4: How to resolve "no qualifying bean" error?
**A:** 
- Use @Qualifier to specify which bean
- Mark one bean as @Primary
- Ensure component scanning is enabled
- Check bean name spelling

### Q5: What is Inversion of Control (IoC)?
**A:** IoC is the principle where control of object creation and lifecycle is transferred from application code to a framework/container (Spring). DI is one way to implement IoC.

---

## Next Steps

Continue to:
- **[Bean Lifecycle →](../02-bean-lifecycle/)** - Understand how Spring manages bean creation and destruction

---

_Constructor injection is your friend. Use it! 🚀_
