# ApplicationContext

## Overview
Deep dive into Spring's IoC container - the ApplicationContext that manages your beans, handles events, and provides enterprise features.

## Duration: 0.5 Day

---

## Table of Contents
1. [What is ApplicationContext?](#what-is-applicationcontext)
2. [BeanFactory vs ApplicationContext](#beanfactory-vs-applicationcontext)
3. [ApplicationContext Implementations](#applicationcontext-implementations)
4. [Bean Retrieval](#bean-retrieval)
5. [Context Hierarchy](#context-hierarchy)
6. [Event Handling](#event-handling)
7. [Resource Loading](#resource-loading)
8. [Environment and Profiles](#environment-and-profiles)
9. [Best Practices](#best-practices)

---

## What is ApplicationContext?

**ApplicationContext** is the central interface to the Spring IoC container. It:
- Creates and manages beans
- Resolves dependencies
- Handles the complete bean lifecycle
- Provides enterprise features (events, i18n, resource loading)

```java
// Create context
ApplicationContext context = 
    new AnnotationConfigApplicationContext(AppConfig.class);

// Get bean
UserService userService = context.getBean(UserService.class);

// Use bean
userService.createUser("John");
```

### Key Responsibilities

1. **Bean Management**
   - Instantiate beans
   - Wire dependencies
   - Manage lifecycle

2. **Configuration**
   - Load configuration
   - Process annotations
   - Handle profiles

3. **Enterprise Features**
   - Event publishing
   - Resource loading
   - Internationalization (i18n)
   - Environment abstraction

---

## BeanFactory vs ApplicationContext

### BeanFactory (Legacy)

The simplest container - basic IoC functionality:

```java
BeanFactory factory = new XmlBeanFactory(new ClassPathResource("beans.xml"));
MyBean bean = factory.getBean(MyBean.class);
```

**Features**:
- ✅ Basic DI
- ✅ Lazy bean initialization
- ❌ No events
- ❌ No AOP
- ❌ No i18n

### ApplicationContext (Recommended)

Advanced container extending BeanFactory:

```java
ApplicationContext context = 
    new AnnotationConfigApplicationContext(AppConfig.class);
MyBean bean = context.getBean(MyBean.class);
```

**Features**:
- ✅ All BeanFactory features
- ✅ Eager bean initialization (by default)
- ✅ Event publishing
- ✅ AOP support
- ✅ Internationalization
- ✅ Resource loading
- ✅ Environment abstraction

### Comparison Table

| Feature | BeanFactory | ApplicationContext |
|---------|-------------|-------------------|
| Bean instantiation | ✅ | ✅ |
| Dependency injection | ✅ | ✅ |
| Bean lifecycle callbacks | ✅ | ✅ |
| BeanPostProcessor | Manual | Automatic |
| Event publishing | ❌ | ✅ |
| Internationalization | ❌ | ✅ |
| AOP | ❌ | ✅ |
| Resource loading | ❌ | ✅ |
| Initialization | Lazy | Eager (default) |
| Use case | Rarely used | Always recommended |

**Bottom line**: Always use ApplicationContext!

---

## ApplicationContext Implementations

### 1. AnnotationConfigApplicationContext

**Use**: Java-based configuration (most common in modern Spring)

```java
@Configuration
public class AppConfig {
    @Bean
    public UserService userService() {
        return new UserService();
    }
}

// Create context
ApplicationContext context = 
    new AnnotationConfigApplicationContext(AppConfig.class);

// Can also scan packages
ApplicationContext context2 = 
    new AnnotationConfigApplicationContext();
context2.scan("com.example");
context2.refresh();
```

**When to use**: Modern Spring applications with annotation-based config.

### 2. ClassPathXmlApplicationContext

**Use**: XML-based configuration (legacy)

```java
ApplicationContext context = 
    new ClassPathXmlApplicationContext("applicationContext.xml");
```

**XML Config**:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans">
    <bean id="userService" class="com.example.UserService"/>
</beans>
```

**When to use**: Legacy applications, or when XML is preferred.

### 3. FileSystemXmlApplicationContext

**Use**: Load XML from file system path

```java
ApplicationContext context = 
    new FileSystemXmlApplicationContext("/config/applicationContext.xml");
```

**When to use**: Config files outside classpath.

### 4. WebApplicationContext

**Use**: Web applications (Spring MVC, Spring Boot)

```java
// Spring Boot creates this automatically
WebApplicationContext context = 
    (WebApplicationContext) request.getServletContext()
        .getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
```

**When to use**: Automatically used in web applications.

### 5. GenericApplicationContext

**Use**: Programmatic bean registration

```java
GenericApplicationContext context = new GenericApplicationContext();
context.registerBean(UserService.class);
context.registerBean("myBean", MyBean.class, () -> new MyBean());
context.refresh();
```

**When to use**: Dynamic bean registration at runtime.

---

## Bean Retrieval

### 1. By Type
```java
UserService service = context.getBean(UserService.class);
```
**Best practice**: Type-safe, refactoring-friendly.

### 2. By Name
```java
UserService service = (UserService) context.getBean("userService");
```
**Use case**: When multiple beans of same type exist.

### 3. By Name and Type
```java
UserService service = context.getBean("userService", UserService.class);
```
**Best practice**: Type-safe with specific bean selection.

### 4. Check if Bean Exists
```java
boolean exists = context.containsBean("userService");
if (exists) {
    UserService service = context.getBean(UserService.class);
}
```

### 5. Get Bean Names
```java
String[] names = context.getBeanDefinitionNames();
for (String name : names) {
    System.out.println("Bean: " + name);
}

// By type
String[] userServiceBeans = context.getBeanNamesForType(UserService.class);
```

### 6. Get Beans by Type
```java
Map<String, UserService> beans = context.getBeansOfType(UserService.class);
```

---

## Context Hierarchy

Spring supports parent-child context relationships:

```
Root Context (Parent)
    ├── Services
    ├── Repositories
    └── Configuration
        ↓
Web Context (Child)
    ├── Controllers
    ├── Filters
    └── Can access parent beans
```

### Creating Hierarchy

```java
// Parent context
ApplicationContext parentContext = 
    new AnnotationConfigApplicationContext(RootConfig.class);

// Child context
AnnotationConfigApplicationContext childContext = 
    new AnnotationConfigApplicationContext();
childContext.setParent(parentContext);
childContext.register(WebConfig.class);
childContext.refresh();
```

### Rules

1. **Child can see parent beans** ✅
2. **Parent cannot see child beans** ❌
3. **Child beans override parent beans** (same name)

### Use Cases

- **Web Applications**: Root context for services, web context for controllers
- **Multi-tenant**: Shared config in parent, tenant-specific in child
- **Testing**: Test config inherits from production config

---

## Event Handling

Spring provides an event-driven architecture within the ApplicationContext.

### Built-in Events

```java
@Component
public class ContextListener {
    
    @EventListener
    public void handleContextRefreshed(ContextRefreshedEvent event) {
        System.out.println("Context initialized: " + event.getApplicationContext());
    }
    
    @EventListener
    public void handleContextStarted(ContextStartedEvent event) {
        System.out.println("Context started");
    }
    
    @EventListener
    public void handleContextStopped(ContextStoppedEvent event) {
        System.out.println("Context stopped");
    }
    
    @EventListener
    public void handleContextClosed(ContextClosedEvent event) {
        System.out.println("Context closed");
    }
}
```

### Custom Events

**1. Create Event:**
```java
public class UserCreatedEvent extends ApplicationEvent {
    private final String username;
    
    public UserCreatedEvent(Object source, String username) {
        super(source);
        this.username = username;
    }
    
    public String getUsername() {
        return username;
    }
}
```

**2. Publish Event:**
```java
@Service
public class UserService {
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    
    public void createUser(String username) {
        // Create user logic...
        
        // Publish event
        eventPublisher.publishEvent(new UserCreatedEvent(this, username));
    }
}
```

**3. Listen to Event:**
```java
@Component
public class EmailService {
    
    @EventListener
    public void handleUserCreated(UserCreatedEvent event) {
        System.out.println("Sending welcome email to: " + event.getUsername());
        // Send email logic...
    }
}

@Component
public class AuditService {
    
    @EventListener
    @Async // Process asynchronously
    public void auditUserCreated(UserCreatedEvent event) {
        System.out.println("Auditing: User created - " + event.getUsername());
    }
}
```

### Async Events

```java
@Configuration
@EnableAsync
public class AsyncConfig {
    
    @Bean
    public Executor taskExecutor() {
        return Executors.newFixedThreadPool(5);
    }
}

@Component
public class NotificationService {
    
    @EventListener
    @Async
    public void handleUserCreated(UserCreatedEvent event) {
        // Runs asynchronously
        sendNotification(event.getUsername());
    }
}
```

### Conditional Listeners

```java
@EventListener(condition = "#event.username.length() > 5")
public void handleLongUsernames(UserCreatedEvent event) {
    // Only triggered for usernames longer than 5 chars
}
```

---

## Resource Loading

ApplicationContext implements `ResourceLoader` interface:

### Load Resources

```java
// From classpath
Resource resource = context.getResource("classpath:config/app.properties");

// From file system
Resource resource2 = context.getResource("file:/config/app.properties");

// From URL
Resource resource3 = context.getResource("https://example.com/config.properties");

// Read resource
try (InputStream is = resource.getInputStream()) {
    Properties props = new Properties();
    props.load(is);
}
```

### Resource Injection

```java
@Component
public class ConfigLoader {
    
    @Value("classpath:data.json")
    private Resource dataFile;
    
    public void loadData() throws IOException {
        try (InputStream is = dataFile.getInputStream()) {
            // Process file
        }
    }
}
```

### Resource Patterns

```java
// Load all properties files
Resource[] resources = context.getResources("classpath*:config/*.properties");

for (Resource resource : resources) {
    System.out.println(resource.getFilename());
}
```

---

## Environment and Profiles

### Environment Abstraction

```java
Environment env = context.getEnvironment();

// Get property
String dbUrl = env.getProperty("database.url");
String dbUrl2 = env.getProperty("database.url", "default-url");

// Check property
boolean hasProperty = env.containsProperty("database.url");

// Active profiles
String[] profiles = env.getActiveProfiles();

// Check if profile active
boolean isDev = env.acceptsProfiles(Profiles.of("dev"));
```

### Profiles

**Define beans for specific profiles:**

```java
@Configuration
public class DataSourceConfig {
    
    @Bean
    @Profile("dev")
    public DataSource devDataSource() {
        return new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .build();
    }
    
    @Bean
    @Profile("prod")
    public DataSource prodDataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:postgresql://prod-server/db");
        return ds;
    }
}
```

**Activate profile:**

```java
// Programmatically
AnnotationConfigApplicationContext context = 
    new AnnotationConfigApplicationContext();
context.getEnvironment().setActiveProfiles("dev");
context.register(DataSourceConfig.class);
context.refresh();

// Via JVM argument
// -Dspring.profiles.active=dev

// Via environment variable
// SPRING_PROFILES_ACTIVE=dev

// In application.properties
// spring.profiles.active=dev
```

**Multiple profiles:**
```java
@Profile({"dev", "test"})
public DataSource testDataSource() { ... }

// Activate multiple
context.getEnvironment().setActiveProfiles("dev", "debug");
```

**Profile expressions:**
```java
@Profile("!prod") // Not production
public DataSource nonProdDataSource() { ... }

@Profile("dev & debug") // Dev AND debug
public DataSource devDebugBean() { ... }

@Profile("dev | test") // Dev OR test
public DataSource devOrTestBean() { ... }
```

---

## Best Practices

### 1. Prefer Constructor Injection
```java
// ✅ Good
@Service
public class UserService {
    private final UserRepository repository;
    
    public UserService(UserRepository repository) {
        this.repository = repository;
    }
}

// ❌ Avoid
@Autowired
private ApplicationContext context;

public void someMethod() {
    UserRepository repo = context.getBean(UserRepository.class);
}
```

### 2. Don't Inject ApplicationContext
Only inject it if you really need it (rare cases):
- Plugin systems
- Dynamic bean creation
- Framework code

### 3. Use @EventListener over ApplicationListener
```java
// ✅ Modern
@EventListener
public void handleEvent(MyEvent event) { }

// ❌ Old way
public class MyListener implements ApplicationListener<MyEvent> {
    public void onApplicationEvent(MyEvent event) { }
}
```

### 4. Close Context Properly
```java
try (AnnotationConfigApplicationContext context = 
        new AnnotationConfigApplicationContext(AppConfig.class)) {
    // Use context
} // Automatically closed
```

### 5. Use Profiles for Environment-Specific Beans
```java
@Profile("dev")
@Bean
public DataSource devDataSource() { ... }
```

### 6. Leverage Environment for Configuration
```java
@Value("${app.name}")
private String appName;

// Better than hardcoding
```

---

## Spring Boot and ApplicationContext

### Spring Boot Creates ApplicationContext Automatically

```java
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        // Spring Boot creates ApplicationContext
        ApplicationContext context = SpringApplication.run(Application.class, args);
        
        // Can access it if needed
        UserService service = context.getBean(UserService.class);
    }
}
```

### Accessing ApplicationContext in Spring Boot

```java
@Component
public class MyComponent implements ApplicationContextAware {
    
    private ApplicationContext context;
    
    @Override
    public void setApplicationContext(ApplicationContext context) {
        this.context = context;
    }
}

// Or inject it
@Component
public class AnotherComponent {
    
    private final ApplicationContext context;
    
    public AnotherComponent(ApplicationContext context) {
        this.context = context;
    }
}
```

---

## Common Interview Questions

**Q: What is the difference between BeanFactory and ApplicationContext?**
A: ApplicationContext extends BeanFactory and adds enterprise features like event publishing, i18n, AOP, and eager initialization.

**Q: When are beans initialized in ApplicationContext?**
A: Singleton beans are eagerly initialized at context startup (unless marked lazy). Prototype beans are created on each request.

**Q: Can a parent context access beans from child context?**
A: No. Only child can access parent's beans, not vice versa.

**Q: How to publish custom events in Spring?**
A: Inject ApplicationEventPublisher and call publishEvent(). Listen with @EventListener.

**Q: What is the default ApplicationContext in Spring Boot?**
A: AnnotationConfigServletWebServerApplicationContext (for web apps) or AnnotationConfigApplicationContext (for non-web).

---

## See Demo

Check out the [demo project](demo-applicationcontext/) for working examples!

---

**Next**: [Spring Annotations →](../04-spring-annotations/)
