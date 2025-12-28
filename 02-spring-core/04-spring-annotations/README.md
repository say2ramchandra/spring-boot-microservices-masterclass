# Spring Annotations

## Overview
Master Spring's stereotype annotations, configuration annotations, and dependency injection annotations to write clean, declarative code.

## Duration: 0.5 Day

---

## Table of Contents
1. [Stereotype Annotations](#stereotype-annotations)
2. [Configuration Annotations](#configuration-annotations)
3. [Dependency Injection Annotations](#dependency-injection-annotations)
4. [Component Scanning](#component-scanning)
5. [Property and Value Annotations](#property-and-value-annotations)
6. [Bean Lifecycle Annotations](#bean-lifecycle-annotations)
7. [Conditional Annotations](#conditional-annotations)
8. [Best Practices](#best-practices)

---

## Stereotype Annotations

Stereotype annotations mark classes for Spring's component scanning.

### @Component

**Generic stereotype** for any Spring-managed component:

```java
@Component
public class EmailValidator {
    public boolean isValid(String email) {
        return email.contains("@");
    }
}
```

**Use when**: Class doesn't fit other stereotypes.

### @Service

**Business logic layer** - contains business rules:

```java
@Service
public class UserService {
    private final UserRepository repository;
    
    public UserService(UserRepository repository) {
        this.repository = repository;
    }
    
    public User createUser(String name) {
        // Business logic
        return repository.save(new User(name));
    }
}
```

**Use for**: Service layer classes with business logic.

### @Repository

**Data access layer** - interacts with database:

```java
@Repository
public class UserRepository {
    private final JdbcTemplate jdbcTemplate;
    
    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    public User findById(Long id) {
        return jdbcTemplate.queryForObject(
            "SELECT * FROM users WHERE id = ?",
            new UserRowMapper(),
            id
        );
    }
}
```

**Special feature**: Translates persistence exceptions to Spring's DataAccessException.

**Use for**: DAO classes, database repositories.

### @Controller

**Web layer** - handles HTTP requests:

```java
@Controller
public class UserController {
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping("/users/{id}")
    @ResponseBody
    public User getUser(@PathVariable Long id) {
        return userService.findById(id);
    }
}
```

**Use for**: MVC controllers (Spring MVC).

### @RestController

**REST API layer** - combines @Controller + @ResponseBody:

```java
@RestController
@RequestMapping("/api/users")
public class UserRestController {
    private final UserService userService;
    
    public UserRestController(UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.findById(id);
    }
}
```

**Use for**: REST API controllers (automatic JSON serialization).

### Stereotype Hierarchy

```
@Component (base)
    ├── @Service (business logic)
    ├── @Repository (data access)
    └── @Controller (web layer)
            └── @RestController (REST APIs)
```

---

## Configuration Annotations

### @Configuration

**Marks a class as configuration** - replaces XML:

```java
@Configuration
public class AppConfig {
    
    @Bean
    public UserService userService() {
        return new UserService(userRepository());
    }
    
    @Bean
    public UserRepository userRepository() {
        return new UserRepository();
    }
}
```

**Creates**: A configuration class that Spring processes for bean definitions.

### @Bean

**Defines a bean** in a @Configuration class:

```java
@Configuration
public class DatabaseConfig {
    
    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2:mem:testdb");
        return new HikariDataSource(config);
    }
    
    @Bean(initMethod = "init", destroyMethod = "cleanup")
    public CacheManager cacheManager() {
        return new CacheManager();
    }
}
```

**Bean naming**:
- Default: method name
- Custom: `@Bean(name = "myCustomName")`
- Multiple names: `@Bean(name = {"name1", "name2"})`

### @Import

**Import other configuration classes**:

```java
@Configuration
@Import({DatabaseConfig.class, SecurityConfig.class})
public class AppConfig {
    // Combines multiple configurations
}
```

### @ImportResource

**Import XML configuration**:

```java
@Configuration
@ImportResource("classpath:legacy-config.xml")
public class AppConfig {
    // Mix Java and XML config
}
```

---

## Dependency Injection Annotations

### @Autowired

**Automatic dependency injection** (by type):

```java
// Constructor injection (recommended)
@Service
public class UserService {
    private final UserRepository repository;
    
    @Autowired // Optional in Spring 4.3+ if only one constructor
    public UserService(UserRepository repository) {
        this.repository = repository;
    }
}

// Setter injection
@Service
public class OrderService {
    private EmailService emailService;
    
    @Autowired
    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }
}

// Field injection (not recommended)
@Service
public class ProductService {
    @Autowired
    private ProductRepository repository;
}
```

**Required vs Optional**:
```java
@Autowired(required = false) // Won't fail if bean not found
private OptionalService optionalService;
```

### @Qualifier

**Select specific bean** when multiple candidates exist:

```java
@Configuration
public class DatabaseConfig {
    
    @Bean
    @Qualifier("mysqlDataSource")
    public DataSource mysqlDataSource() {
        return new MysqlDataSource();
    }
    
    @Bean
    @Qualifier("h2DataSource")
    public DataSource h2DataSource() {
        return new H2DataSource();
    }
}

@Service
public class UserService {
    
    @Autowired
    @Qualifier("mysqlDataSource")
    private DataSource dataSource;
}
```

### @Primary

**Mark preferred bean** when multiple candidates:

```java
@Configuration
public class DatabaseConfig {
    
    @Bean
    @Primary // This one is chosen by default
    public DataSource primaryDataSource() {
        return new MysqlDataSource();
    }
    
    @Bean
    public DataSource secondaryDataSource() {
        return new H2DataSource();
    }
}

@Service
public class UserService {
    
    @Autowired // Gets primaryDataSource
    private DataSource dataSource;
}
```

### @Lazy

**Lazy initialization** - create bean only when needed:

```java
@Component
@Lazy
public class ExpensiveService {
    public ExpensiveService() {
        System.out.println("ExpensiveService created");
    }
}

// Or on injection point
@Service
public class MyService {
    
    @Autowired
    @Lazy
    private ExpensiveService expensiveService; // Created when first accessed
}
```

---

## Component Scanning

### @ComponentScan

**Scan packages** for components:

```java
@Configuration
@ComponentScan(basePackages = "com.example")
public class AppConfig {
}

// Multiple packages
@ComponentScan(basePackages = {"com.example.service", "com.example.repository"})

// Type-safe with classes
@ComponentScan(basePackageClasses = {UserService.class})

// Exclude filters
@ComponentScan(
    basePackages = "com.example",
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ANNOTATION,
        classes = Configuration.class
    )
)

// Include filters
@ComponentScan(
    basePackages = "com.example",
    includeFilters = @ComponentScan.Filter(
        type = FilterType.REGEX,
        pattern = ".*Service"
    ),
    useDefaultFilters = false
)
```

**Filter Types**:
- `ANNOTATION` - by annotation
- `ASSIGNABLE_TYPE` - by class/interface
- `REGEX` - by regex pattern
- `ASPECTJ` - by AspectJ expression
- `CUSTOM` - custom filter

---

## Property and Value Annotations

### @Value

**Inject values** from properties:

```java
@Component
public class AppConfig {
    
    @Value("${app.name}")
    private String appName;
    
    @Value("${app.timeout:30}") // Default value
    private int timeout;
    
    @Value("#{systemProperties['user.home']}")
    private String userHome;
    
    @Value("#{T(java.lang.Math).random() * 100}")
    private double randomNumber;
}
```

### @PropertySource

**Load properties file**:

```java
@Configuration
@PropertySource("classpath:application.properties")
public class AppConfig {
    
    @Value("${database.url}")
    private String dbUrl;
}

// Multiple files
@PropertySources({
    @PropertySource("classpath:app.properties"),
    @PropertySource("classpath:db.properties")
})
```

### Environment

**Access properties programmatically**:

```java
@Component
public class ConfigService {
    
    @Autowired
    private Environment env;
    
    public void printConfig() {
        String dbUrl = env.getProperty("database.url");
        String dbUrl2 = env.getProperty("database.url", "default");
        
        boolean hasProperty = env.containsProperty("database.url");
    }
}
```

---

## Bean Lifecycle Annotations

### @PostConstruct

**Initialization callback** - runs after dependency injection:

```java
@Component
public class DataLoader {
    
    @Autowired
    private DataSource dataSource;
    
    @PostConstruct
    public void init() {
        System.out.println("Initializing data...");
        // Load initial data
    }
}
```

### @PreDestroy

**Destruction callback** - runs before bean destruction:

```java
@Component
public class ConnectionManager {
    
    private Connection connection;
    
    @PreDestroy
    public void cleanup() {
        System.out.println("Cleaning up resources...");
        if (connection != null) {
            connection.close();
        }
    }
}
```

---

## Conditional Annotations

### @Profile

**Activate beans for specific profiles**:

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
        return new PostgresDataSource();
    }
}
```

### @Conditional

**Custom conditions** for bean creation:

```java
public class WindowsCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return context.getEnvironment().getProperty("os.name").contains("Windows");
    }
}

@Configuration
public class OsConfig {
    
    @Bean
    @Conditional(WindowsCondition.class)
    public FileService windowsFileService() {
        return new WindowsFileService();
    }
}
```

### Spring Boot Conditional Annotations

```java
@ConditionalOnClass(DataSource.class) // If class on classpath
@ConditionalOnMissingBean(DataSource.class) // If bean doesn't exist
@ConditionalOnProperty(name = "feature.enabled", havingValue = "true")
@ConditionalOnExpression("${feature.enabled:false}")
```

---

## Best Practices

### 1. Choose Right Stereotype

```java
// ✅ Clear intent
@Service
public class UserService { }

@Repository
public class UserRepository { }

@Controller
public class UserController { }

// ❌ Generic everywhere
@Component
public class UserService { }
```

### 2. Prefer Constructor Injection

```java
// ✅ Recommended
@Service
public class UserService {
    private final UserRepository repository;
    
    public UserService(UserRepository repository) {
        this.repository = repository;
    }
}

// ❌ Avoid field injection
@Service
public class UserService {
    @Autowired
    private UserRepository repository;
}
```

### 3. Use @Primary for Default Bean

```java
@Bean
@Primary
public DataSource mainDataSource() { }

@Bean
public DataSource secondaryDataSource() { }
```

### 4. Component Scan Base Packages

```java
// ✅ Specific
@ComponentScan(basePackages = "com.example.myapp")

// ❌ Too broad
@ComponentScan(basePackages = "com")
```

### 5. Property Defaults

```java
@Value("${app.timeout:30}") // Always provide defaults
private int timeout;
```

---

## Common Interview Questions

**Q: What's the difference between @Component, @Service, @Repository?**
A: Semantically different layers. @Repository adds exception translation. All are @Component specializations.

**Q: Why prefer constructor injection?**
A: Immutability, testability, clear dependencies, IDE support, fails-fast if dependencies missing.

**Q: @Autowired vs @Inject?**
A: @Inject is JSR-330 standard. @Autowired is Spring-specific with more features (required attribute).

**Q: When to use @Qualifier vs @Primary?**
A: @Primary sets default. @Qualifier selects specific bean.

**Q: What's @Configuration vs @Component?**
A: @Configuration classes are CGLIB-proxied to ensure singleton @Bean methods. @Component doesn't proxy.

---

## See Demo

Check out the [demo project](demo-spring-annotations/) for working examples!

---

**Next**: [Aspect-Oriented Programming →](../05-aspect-oriented-programming/)
