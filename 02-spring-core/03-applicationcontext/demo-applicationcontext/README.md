# ApplicationContext Demo

## Overview
Comprehensive demonstration of Spring's ApplicationContext - the IoC container that manages beans, handles events, and provides enterprise features.

## Running the Demo

```bash
cd 03-applicationcontext/demo-applicationcontext
mvn clean compile exec:java
```

## What You'll Learn

### Part 1: Creating ApplicationContext
- ✅ AnnotationConfigApplicationContext creation
- ✅ Context properties (ID, display name, startup date)
- ✅ Automatic context closure with try-with-resources

### Part 2: Bean Retrieval Methods
- ✅ Get bean by type
- ✅ Get bean by name and type
- ✅ Check if bean exists
- ✅ Get all bean names
- ✅ Get bean count

### Part 3: Profiles
- ✅ Development profile with H2 database
- ✅ Production profile with PostgreSQL
- ✅ Profile-specific bean creation
- ✅ Activating profiles programmatically

### Part 4: Event Publishing and Listening
- ✅ Custom events (UserCreatedEvent)
- ✅ Event publishing with ApplicationEventPublisher
- ✅ Event listening with @EventListener
- ✅ Built-in lifecycle events (refresh, start, stop, close)

### Part 5: Environment Abstraction
- ✅ Active profiles retrieval
- ✅ System properties access
- ✅ Property with default values
- ✅ Property existence checking

### Part 6: Resource Loading
- ✅ Loading resources from classpath
- ✅ Resource loader capabilities
- ✅ Multiple resource location strategies

## Key Concepts Demonstrated

### 1. ApplicationContext vs BeanFactory

```
BeanFactory (Basic)
    ↓
ApplicationContext (Advanced)
    ├── All BeanFactory features
    ├── Event publishing
    ├── Internationalization
    ├── Resource loading
    ├── Environment abstraction
    └── Eager initialization
```

### 2. Bean Retrieval Patterns

```java
// By type (recommended)
UserService service = context.getBean(UserService.class);

// By name and type (type-safe)
DatabaseService db = context.getBean("devDatabaseService", DatabaseService.class);

// Check existence
if (context.containsBean("userService")) {
    // Use bean
}
```

### 3. Event-Driven Architecture

```
UserService.createUser()
    ↓
Publishes UserCreatedEvent
    ↓
NotificationService listens → Sends email
AuditService listens → Logs action
```

### 4. Profiles for Environment-Specific Configuration

```
dev profile  → H2 in-memory database
prod profile → PostgreSQL production database
test profile → Test database
```

## Project Structure

```
demo-applicationcontext/
├── pom.xml
└── src/main/java/com/masterclass/context/
    ├── ApplicationContextDemo.java (Main demo)
    ├── config/
    │   └── AppConfig.java (Configuration with profiles)
    ├── service/
    │   ├── UserService.java (Event publisher)
    │   ├── DatabaseService.java (Profile-specific)
    │   └── NotificationService.java (Event listener)
    ├── event/
    │   └── UserCreatedEvent.java (Custom event)
    └── listener/
        └── ContextEventListener.java (Lifecycle events)
```

## Expected Output

```
╔══════════════════════════════════════════════╗
║      ApplicationContext Demo                 ║
╚══════════════════════════════════════════════╝

==================================================
PART 1: Creating ApplicationContext
==================================================

1. Creating context with configuration class:
   DatabaseService initialized: H2 In-Memory Database
   ✅ Context created successfully
   Context ID: org.springframework...
   Display name: org.springframework...
   Startup date: 1234567890
   [Event] Context refreshed
   [Event] Context closed
   ✅ Context closed automatically

==================================================
PART 2: Bean Retrieval Methods
==================================================

1. Get bean by type:
   Retrieved: UserService

2. Get bean by name and type:
   Creating DEV database service
   DatabaseService initialized: H2 In-Memory Database
   Retrieved: DatabaseService
   Database: H2 In-Memory Database
...
```

## Real-World Applications

### 1. Event-Driven Microservices
```java
// Service publishes events
@Service
public class OrderService {
    @Autowired
    private ApplicationEventPublisher publisher;
    
    public void createOrder(Order order) {
        // Save order
        publisher.publishEvent(new OrderCreatedEvent(this, order));
    }
}

// Multiple listeners respond
@Component
public class EmailService {
    @EventListener
    public void sendOrderConfirmation(OrderCreatedEvent event) {
        // Send email
    }
}

@Component
public class InventoryService {
    @EventListener
    public void updateInventory(OrderCreatedEvent event) {
        // Update stock
    }
}
```

### 2. Environment-Specific Configuration
```java
@Configuration
public class DatabaseConfig {
    
    @Bean
    @Profile("dev")
    public DataSource devDataSource() {
        return new H2DataSource();
    }
    
    @Bean
    @Profile("prod")
    public DataSource prodDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(env.getProperty("db.url"));
        return new HikariDataSource(config);
    }
}
```

### 3. Resource Loading
```java
@Component
public class ConfigLoader {
    
    @Autowired
    private ApplicationContext context;
    
    public void loadConfiguration() {
        Resource resource = context.getResource("classpath:config.json");
        // Load and parse
    }
}
```

## Best Practices Shown

1. ✅ **Use try-with-resources for context**
   ```java
   try (AnnotationConfigApplicationContext context = ...) {
       // Use context
   } // Automatically closed
   ```

2. ✅ **Prefer constructor injection over context.getBean()**
   ```java
   // Good
   @Service
   public class MyService {
       private final MyRepository repo;
       
       public MyService(MyRepository repo) {
           this.repo = repo;
       }
   }
   
   // Avoid
   MyRepository repo = context.getBean(MyRepository.class);
   ```

3. ✅ **Use @EventListener over ApplicationListener interface**
   ```java
   // Modern
   @EventListener
   public void handleEvent(MyEvent event) { }
   
   // Old way
   public class MyListener implements ApplicationListener<MyEvent> { }
   ```

4. ✅ **Leverage profiles for environment configuration**

5. ✅ **Use Environment for property access**

## Common Interview Questions

**Q: What's the difference between BeanFactory and ApplicationContext?**
A: ApplicationContext extends BeanFactory and adds:
- Event publishing
- Internationalization (i18n)
- Resource loading
- Environment abstraction
- AOP support
- Eager bean initialization

**Q: When should you inject ApplicationContext?**
A: Rarely. Only for:
- Plugin systems
- Dynamic bean creation
- Framework development
Prefer constructor injection of specific beans.

**Q: Can you have multiple ApplicationContexts?**
A: Yes, Spring supports context hierarchy. Child contexts can access parent beans, but not vice versa.

**Q: How do custom events work in Spring?**
A: 1) Create event extending ApplicationEvent
2) Publish with ApplicationEventPublisher
3) Listen with @EventListener

**Q: What are profiles used for?**
A: Environment-specific configuration (dev, test, prod) - different databases, services, or settings per environment.

## Integration with Spring Boot

Spring Boot automatically:
- Creates ApplicationContext
- Activates profiles from properties
- Configures event publishing
- Enables resource loading

You rarely need to manually create ApplicationContext in Spring Boot.

## Next Steps

1. Run the demo and study the output
2. Modify profiles and see different beans created
3. Create your own custom events
4. Try resource loading with actual files
5. Move to [Spring Annotations](../../04-spring-annotations/)

---

**💡 Tip**: Understanding ApplicationContext is crucial - it's the heart of Spring that manages everything!
