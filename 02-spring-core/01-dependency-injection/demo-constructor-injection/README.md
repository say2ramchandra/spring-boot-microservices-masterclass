# Constructor Injection Demo

> **Master Constructor Injection - The recommended approach for Spring dependency injection**

## 📚 What This Demo Demonstrates

This comprehensive demo shows:
- ✅ Simple constructor injection with single dependency
- ✅ Constructor injection with multiple dependencies
- ✅ Optional vs required @Autowired annotation
- ✅ Real-world e-commerce service architecture
- ✅ Why constructor injection is superior for testing
- ✅ Bean initialization with @PostConstruct

## 🎯 Learning Objectives

After running this demo, you will understand:
- How Spring injects dependencies through constructors
- Why constructor injection is the recommended approach
- How to create immutable, thread-safe services
- Real-world service layer architecture
- How to test constructor-injected classes

## 📋 Prerequisites

- Java 17+
- Maven 3.8+
- Basic understanding of Spring concepts

## 🚀 How to Run

### Option 1: Using Maven (Recommended)
```bash
# Navigate to demo directory
cd demo-constructor-injection

# Clean and compile
mvn clean compile

# Run the application
mvn exec:java
```

### Option 2: Using IDE
1. Import as Maven project
2. Navigate to `ConstructorInjectionDemo.java`
3. Right-click and select "Run"

### Option 3: Command Line
```bash
# Compile
mvn clean package

# Run
java -jar target/demo-constructor-injection-1.0.0.jar
```

## 📊 Expected Output

```
======================================================================
     Spring Constructor Injection Demonstration
======================================================================

🔧 Initializing Spring Container...
  ✓ UserRepository created
  ✓ OrderRepository created
  ✓ EmailService created
  ✓ PaymentService created
  ✓ UserService created with UserRepository injected
  ✓ OrderService created with 3 dependencies injected
  ✓ NotificationService created (explicit @Autowired)
  ✓ OrderProcessingService created with 4 dependencies
  ✓ OrderProcessingService initialized!
✅ Spring Container initialized!

======================================================================

📚 Example 1: Simple Constructor Injection
--------------------------------------------------

[UserService] Creating user: John Doe
  [Repository] Saving user to database...
✅ User 'John Doe' saved with email: john@example.com

[UserService] Finding user by ID: 1
  [Repository] Fetching user from database...
✅ Found user with ID: 1

💡 Spring injected UserRepository into UserService!
💡 Dependencies are FINAL - immutable and thread-safe!

... (more examples follow)
```

## 🔑 Key Concepts Demonstrated

### 1. Simple Constructor Injection

**File**: [UserService.java](src/main/java/com/masterclass/spring/service/UserService.java)

```java
@Service
public class UserService {
    private final UserRepository userRepository;  // FINAL = immutable
    
    // @Autowired optional for single constructor
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
```

**Why it's great**:
- ✅ Dependency is `final` - immutable and thread-safe
- ✅ No NullPointerException possible
- ✅ Clear what this class needs
- ✅ Easy to test without Spring

### 2. Multiple Dependencies

**File**: [OrderService.java](src/main/java/com/masterclass/spring/service/OrderService.java)

```java
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final EmailService emailService;
    private final PaymentService paymentService;
    
    public OrderService(OrderRepository orderRepository,
                       EmailService emailService,
                       PaymentService paymentService) {
        // All three injected automatically!
    }
}
```

**Real-world pattern**: Services often need multiple dependencies to orchestrate business logic.

### 3. Explicit @Autowired

**File**: [NotificationService.java](src/main/java/com/masterclass/spring/service/NotificationService.java)

```java
@Service
public class NotificationService {
    private final EmailService emailService;
    
    @Autowired  // Optional but can be explicit
    public NotificationService(EmailService emailService) {
        this.emailService = emailService;
    }
}
```

**When to use @Autowired explicitly**:
- Multiple constructors exist
- You want to make intent crystal clear
- Team prefers explicit annotations

### 4. Complex Real-World Service

**File**: [OrderProcessingService.java](src/main/java/com/masterclass/spring/service/OrderProcessingService.java)

```java
@Service
public class OrderProcessingService {
    private final OrderRepository orderRepository;
    private final PaymentService paymentService;
    private final EmailService emailService;
    private final NotificationService notificationService;
    
    public OrderProcessingService(/* 4 dependencies */) {
        // All injected!
    }
    
    @PostConstruct
    public void init() {
        // Runs AFTER dependency injection
    }
    
    public boolean processCompleteOrder(Long orderId, Double amount, String email) {
        // Orchestrates multiple services
    }
}
```

**Demonstrates**:
- Real-world complexity
- Transaction coordination
- Business logic orchestration
- Initialization after DI with @PostConstruct

## 🏗️ Project Structure

```
demo-constructor-injection/
├── pom.xml
├── README.md
└── src/main/java/com/masterclass/spring/
    ├── ConstructorInjectionDemo.java    # Main application
    ├── config/
    │   └── AppConfig.java               # Spring configuration
    ├── repository/
    │   ├── UserRepository.java          # Data access
    │   └── OrderRepository.java
    └── service/
        ├── UserService.java             # Single dependency
        ├── OrderService.java            # Multiple dependencies
        ├── NotificationService.java     # Explicit @Autowired
        ├── EmailService.java            # Support service
        ├── PaymentService.java          # Support service
        └── OrderProcessingService.java  # Complex real-world
```

## 📈 Why Constructor Injection?

### ✅ Advantages

| Feature | Constructor Injection | Setter Injection | Field Injection |
|---------|----------------------|------------------|-----------------|
| **Immutability** | ✅ Yes (final) | ❌ No | ❌ No |
| **Required deps** | ✅ Guaranteed | ❌ Optional | ❌ Optional |
| **Testability** | ✅ Excellent | ⚠️ Good | ❌ Poor |
| **Circular deps** | ✅ Detected early | ⚠️ Hidden | ⚠️ Hidden |
| **Thread safety** | ✅ Yes | ⚠️ Depends | ⚠️ Depends |
| **Spring recommendation** | ✅ Yes | ⚠️ Conditional | ❌ No |

### Testing Without Spring

```java
// No Spring container needed!
@Test
void testUserService() {
    UserRepository mockRepo = new MockUserRepository();
    UserService service = new UserService(mockRepo);  // Simple!
    
    String result = service.createUser("test@test.com", "Test");
    assertNotNull(result);
}
```

## 🎓 Exercises

### Exercise 1: Add New Service
Create a `ProductService` that depends on:
- `ProductRepository`
- `InventoryService`

Use constructor injection to wire them together.

### Exercise 2: Create Test Cases
Write JUnit tests for `UserService`:
- Test with a mock repository
- No Spring context required!

### Exercise 3: Add Initialization Logic
Add a `@PostConstruct` method to `UserService` that:
- Logs initialization
- Performs startup checks

### Exercise 4: Circular Dependency
Try creating circular dependency:
- Service A depends on Service B
- Service B depends on Service A
- See how Spring detects it at startup!

### Exercise 5: Optional Dependency
Modify a service to have an optional dependency using constructor:
```java
public MyService(RequiredDep required, 
                @Autowired(required=false) OptionalDep optional) {
    this.required = required;
    this.optional = optional;  // Might be null
}
```

## 🐛 Common Mistakes to Avoid

### ❌ Mistake 1: Using Field Injection

```java
// DON'T DO THIS
@Service
public class BadService {
    @Autowired
    private UserRepository repository;  // Hard to test!
}
```

**Fix**: Use constructor injection

### ❌ Mistake 2: Not Making Dependencies Final

```java
// DON'T DO THIS
@Service
public class BadService {
    private UserRepository repository;  // Mutable!
    
    public BadService(UserRepository repository) {
        this.repository = repository;
    }
}
```

**Fix**: Always use `final` for injected dependencies

### ❌ Mistake 3: Business Logic in Constructor

```java
// DON'T DO THIS
public UserService(UserRepository repository) {
    this.repository = repository;
    this.cache = repository.loadAllUsers();  // NO! Complex logic!
}
```

**Fix**: Use `@PostConstruct` for initialization logic

### ❌ Mistake 4: Too Many Dependencies

```java
// CODE SMELL - Too many dependencies!
public OrderService(Dep1 d1, Dep2 d2, Dep3 d3, Dep4 d4, 
                    Dep5 d5, Dep6 d6, Dep7 d7, Dep8 d8) {
    // If you need this many, refactor your class!
}
```

**Fix**: Refactor into smaller, focused services

## 💡 Best Practices

✅ **Always use constructor injection**
✅ **Make dependencies final**
✅ **Omit @Autowired for single constructor**
✅ **Keep constructors simple** - just assignment
✅ **Use @PostConstruct** for initialization logic
✅ **Limit dependencies** - max 5-7 is a good guideline
✅ **Depend on interfaces**, not concrete classes
✅ **Test without Spring** - just pass mocks to constructor

## 🔗 Related Topics

- **[Setter Injection](../demo-setter-injection/)** - When to use setter injection
- **[Field Injection](../demo-field-injection/)** - Why to avoid field injection
- **[@Qualifier & @Primary](../demo-qualifier-primary/)** - Resolving multiple beans
- **[Bean Lifecycle](../../02-bean-lifecycle/)** - Understanding bean creation

## 📚 Additional Resources

- [Spring Framework Reference - Constructor Injection](https://docs.spring.io/spring-framework/reference/core/beans/dependencies/factory-collaborators.html)
- [Spring Team Recommendation](https://spring.io/blog/2007/07/11/setter-injection-versus-constructor-injection-and-the-use-of-required)
- [Effective Java - Item 1: Consider static factory methods](https://www.oreilly.com/library/view/effective-java/9780134686097/)

## ❓ Quiz Yourself

1. Why is constructor injection preferred over field injection?
2. When is @Autowired required on a constructor?
3. Can constructor-injected dependencies be null?
4. How do you test a service with constructor injection?
5. What happens if you create circular dependencies?

<details>
<summary>Click for answers</summary>

1. Immutability (final), testability, required dependencies guaranteed
2. Only when you have multiple constructors
3. No! Constructor injection guarantees non-null dependencies
4. Just instantiate the service and pass mock dependencies to constructor
5. Spring detects circular dependencies at startup and throws BeanCurrentlyInCreationException

</details>

---

**Next Step**: Explore [Setter Injection →](../demo-setter-injection/) to understand when it's appropriate

_Constructor injection: Simple, safe, and testable! 🚀_
