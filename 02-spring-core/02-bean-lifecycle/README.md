# Bean Lifecycle

> **Understand how Spring creates, manages, and destroys beans**

## 📚 Table of Contents

1. [What is Bean Lifecycle?](#what-is-bean-lifecycle)
2. [Bean Scopes](#bean-scopes)
3. [Lifecycle Callbacks](#lifecycle-callbacks)
4. [Bean Post Processors](#bean-post-processors)
5. [Real-World Use Cases](#real-world-use-cases)

---

## What is Bean Lifecycle?

### Definition

The **Bean Lifecycle** refers to the series of stages a Spring bean goes through from creation to destruction. Understanding this is crucial for proper resource management.

### Lifecycle Stages

```
1. Instantiation
   ↓
2. Populate Properties
   ↓
3. BeanNameAware.setBeanName()
   ↓
4. BeanFactoryAware.setBeanFactory()
   ↓
5. ApplicationContextAware.setApplicationContext()
   ↓
6. Pre-Initialization (BeanPostProcessor)
   ↓
7. @PostConstruct / InitializingBean.afterPropertiesSet()
   ↓
8. Custom init-method
   ↓
9. Post-Initialization (BeanPostProcessor)
   ↓
10. Bean Ready to Use
   ↓
11. @PreDestroy / DisposableBean.destroy()
   ↓
12. Custom destroy-method
```

---

## Bean Scopes

### 1. Singleton (Default)

**Definition**: One instance per Spring container.

```java
@Component
@Scope("singleton")  // Default, can omit
public class DatabaseConnection {
    public DatabaseConnection() {
        System.out.println("Creating DatabaseConnection");
    }
}

// Usage
ApplicationContext context = ...;
DatabaseConnection conn1 = context.getBean(DatabaseConnection.class);
DatabaseConnection conn2 = context.getBean(DatabaseConnection.class);

// conn1 == conn2 (same instance!)
```

**Use Case**: Stateless services, repositories, utilities

**Thread Safety**: Must be thread-safe!

---

### 2. Prototype

**Definition**: New instance every time requested.

```java
@Component
@Scope("prototype")
public class Order {
    private Long id;
    private String customer;
    
    public Order() {
        System.out.println("Creating new Order instance");
    }
}

// Usage
Order order1 = context.getBean(Order.class);
Order order2 = context.getBean(Order.class);

// order1 != order2 (different instances!)
```

**Use Case**: Stateful objects, per-request data

**Note**: Spring doesn't manage destruction for prototype beans!

---

### 3. Request (Web Applications)

**Definition**: One instance per HTTP request.

```java
@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, 
       proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ShoppingCart {
    private List<Item> items = new ArrayList<>();
    
    // New instance for each HTTP request
}
```

**Use Case**: Request-specific data in web apps

---

### 4. Session (Web Applications)

**Definition**: One instance per HTTP session.

```java
@Component
@Scope(value = WebApplicationContext.SCOPE_SESSION,
       proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserPreferences {
    private String theme;
    private String language;
    
    // Same instance throughout user session
}
```

**Use Case**: User session data

---

### 5. Application (Web Applications)

**Definition**: One instance per ServletContext.

```java
@Component
@Scope(value = WebApplicationContext.SCOPE_APPLICATION)
public class ApplicationCache {
    // Shared across entire web application
}
```

---

### 6. WebSocket

**Definition**: One instance per WebSocket session.

---

## Lifecycle Callbacks

### Method 1: @PostConstruct and @PreDestroy

**Most Common and Recommended**

```java
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Component
public class DatabaseService {
    
    private Connection connection;
    
    @PostConstruct
    public void init() {
        System.out.println("Initializing database connection...");
        connection = createConnection();
        System.out.println("Database ready!");
    }
    
    @PreDestroy
    public void cleanup() {
        System.out.println("Closing database connection...");
        if (connection != null) {
            connection.close();
        }
        System.out.println("Cleanup complete!");
    }
}
```

**When called**:
- `@PostConstruct`: After dependency injection
- `@PreDestroy`: Before bean destruction

**Best for**: Initialization and cleanup logic

---

### Method 2: InitializingBean and DisposableBean

**Interface-based approach**

```java
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.DisposableBean;

@Component
public class CacheService implements InitializingBean, DisposableBean {
    
    private Map<String, Object> cache;
    
    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("Initializing cache...");
        cache = new HashMap<>();
        loadCache();
    }
    
    @Override
    public void destroy() throws Exception {
        System.out.println("Clearing cache...");
        cache.clear();
    }
}
```

**Drawback**: Couples code to Spring interfaces

---

### Method 3: Custom init and destroy methods

**Configuration-based**

```java
@Configuration
public class AppConfig {
    
    @Bean(initMethod = "startup", destroyMethod = "shutdown")
    public MyService myService() {
        return new MyService();
    }
}

public class MyService {
    
    public void startup() {
        System.out.println("Custom initialization");
    }
    
    public void shutdown() {
        System.out.println("Custom cleanup");
    }
}
```

---

### Comparison of Callback Methods

| Method | Pros | Cons | Use When |
|--------|------|------|----------|
| **@PostConstruct/@PreDestroy** | ✅ Standard Java<br>✅ Clear intent<br>✅ No coupling | ⚠️ Requires dependency | Preferred choice |
| **InitializingBean/DisposableBean** | ✅ Type-safe | ❌ Couples to Spring | Legacy code |
| **Custom methods** | ✅ No annotations<br>✅ Flexible names | ⚠️ External config | Bean definition |

---

## Bean Post Processors

### What are BeanPostProcessors?

Special beans that intercept bean creation to perform custom processing.

### Creating a BeanPostProcessor

```java
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class LoggingBeanPostProcessor implements BeanPostProcessor {
    
    @Override
    public Object postProcessBeforeInitialization(
            Object bean, String beanName) throws BeansException {
        
        System.out.println("Before Initialization: " + beanName);
        // Can modify bean or return wrapper
        return bean;
    }
    
    @Override
    public Object postProcessAfterInitialization(
            Object bean, String beanName) throws BeansException {
        
        System.out.println("After Initialization: " + beanName);
        // Can modify bean or return proxy
        return bean;
    }
}
```

### Real-World Example: Performance Monitoring

```java
@Component
public class PerformanceMonitoringPostProcessor 
        implements BeanPostProcessor {
    
    @Override
    public Object postProcessAfterInitialization(
            Object bean, String beanName) {
        
        if (bean.getClass().isAnnotationPresent(Monitored.class)) {
            return Proxy.newProxyInstance(
                bean.getClass().getClassLoader(),
                bean.getClass().getInterfaces(),
                (proxy, method, args) -> {
                    long start = System.currentTimeMillis();
                    Object result = method.invoke(bean, args);
                    long end = System.currentTimeMillis();
                    
                    System.out.println(
                        method.getName() + " took " + 
                        (end - start) + "ms"
                    );
                    return result;
                }
            );
        }
        return bean;
    }
}

@Retention(RetentionPolicy.RUNTIME)
@interface Monitored {}
```

---

## Real-World Use Cases

### Use Case 1: Database Connection Pool

```java
@Component
public class ConnectionPoolManager {
    
    private HikariDataSource dataSource;
    
    @PostConstruct
    public void initializePool() {
        System.out.println("Initializing connection pool...");
        
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://localhost/mydb");
        config.setUsername("user");
        config.setPassword("password");
        config.setMaximumPoolSize(10);
        
        dataSource = new HikariDataSource(config);
        
        System.out.println("Connection pool ready!");
    }
    
    @PreDestroy
    public void closePool() {
        System.out.println("Closing connection pool...");
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
        System.out.println("Connection pool closed!");
    }
    
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
```

### Use Case 2: Cache Warming

```java
@Component
public class ProductCacheService {
    
    @Autowired
    private ProductRepository productRepository;
    
    private Map<Long, Product> cache = new ConcurrentHashMap<>();
    
    @PostConstruct
    public void warmUpCache() {
        System.out.println("Warming up product cache...");
        
        List<Product> products = productRepository.findAll();
        products.forEach(p -> cache.put(p.getId(), p));
        
        System.out.println(
            "Cache warmed up with " + cache.size() + " products"
        );
    }
    
    public Product getProduct(Long id) {
        return cache.get(id);
    }
    
    @PreDestroy
    public void clearCache() {
        System.out.println("Clearing product cache...");
        cache.clear();
    }
}
```

### Use Case 3: Scheduled Task Initialization

```java
@Component
public class ReportGenerationService {
    
    @Autowired
    private TaskScheduler taskScheduler;
    
    private ScheduledFuture<?> scheduledTask;
    
    @PostConstruct
    public void startScheduledReportGeneration() {
        System.out.println("Starting scheduled report generation...");
        
        scheduledTask = taskScheduler.scheduleAtFixedRate(
            this::generateDailyReport,
            Duration.ofHours(24)
        );
        
        System.out.println("Scheduled task started!");
    }
    
    @PreDestroy
    public void stopScheduledTask() {
        System.out.println("Stopping scheduled task...");
        if (scheduledTask != null) {
            scheduledTask.cancel(false);
        }
        System.out.println("Scheduled task stopped!");
    }
    
    private void generateDailyReport() {
        // Report generation logic
    }
}
```

### Use Case 4: External Service Connection

```java
@Component
public class PaymentGatewayClient {
    
    @Value("${payment.gateway.url}")
    private String gatewayUrl;
    
    @Value("${payment.gateway.api-key}")
    private String apiKey;
    
    private HttpClient httpClient;
    
    @PostConstruct
    public void connect() {
        System.out.println("Connecting to payment gateway...");
        
        httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
        
        // Test connection
        boolean connected = testConnection();
        if (!connected) {
            throw new RuntimeException("Failed to connect to payment gateway");
        }
        
        System.out.println("Payment gateway connection established!");
    }
    
    @PreDestroy
    public void disconnect() {
        System.out.println("Disconnecting from payment gateway...");
        // Cleanup resources
        System.out.println("Disconnected!");
    }
    
    private boolean testConnection() {
        // Test connection logic
        return true;
    }
}
```

---

## Best Practices

### ✅ DO:

1. **Use @PostConstruct for initialization**
   ```java
   @PostConstruct
   public void init() {
       // Initialization logic
   }
   ```

2. **Clean up resources in @PreDestroy**
   ```java
   @PreDestroy
   public void cleanup() {
       // Close connections, release resources
   }
   ```

3. **Keep singleton beans thread-safe**
   ```java
   @Component
   public class ThreadSafeService {
       private final AtomicInteger counter = new AtomicInteger(0);
   }
   ```

4. **Log lifecycle events during development**
   ```java
   @PostConstruct
   public void init() {
       log.info("Initializing {}", this.getClass().getSimpleName());
   }
   ```

---

### ❌ DON'T:

1. **Don't perform heavy operations in constructor**
   ```java
   // BAD
   public MyService() {
       // Heavy operation in constructor
       loadHugeDataset();
   }
   
   // GOOD
   @PostConstruct
   public void init() {
       loadHugeDataset();
   }
   ```

2. **Don't depend on bean order**
   ```java
   // Rely on @DependsOn if order matters
   @Component
   @DependsOn("databaseService")
   public class DataMigrationService {
       // ...
   }
   ```

3. **Don't forget to clean up prototype beans**
   ```java
   // Prototype beans don't call @PreDestroy
   // You must manually clean them up
   ```

---

## Demo Projects

1. **[demo-bean-scopes](demo-bean-scopes/)** - All scope types
2. **[demo-lifecycle-callbacks](demo-lifecycle-callbacks/)** - Initialization and cleanup
3. **[demo-bean-post-processor](demo-bean-post-processor/)** - Custom processing

---

_Master the lifecycle, master Spring! 🌱_
