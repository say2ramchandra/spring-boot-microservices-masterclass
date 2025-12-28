# Spring Annotations Demo

## Overview
Comprehensive demonstration of Spring's annotations for configuration, dependency injection, and component management.

## Running the Demo

```bash
cd 04-spring-annotations/demo-spring-annotations
mvn clean compile exec:java
```

## What You'll Learn

### Part 1: Stereotype Annotations
- ✅ @Service for business logic layer
- ✅ @Repository for data access layer
- ✅ @Component for generic components
- ✅ Automatic bean creation via component scanning

### Part 2: @Configuration and @Bean
- ✅ Java-based configuration (replaces XML)
- ✅ @Bean method definitions
- ✅ Bean naming and discovery
- ✅ Multiple beans of same type

### Part 3: @Value and @PropertySource
- ✅ Injecting properties from files
- ✅ Default values with `:` operator
- ✅ Loading properties with @PropertySource
- ✅ Type conversion (String to int)

### Part 4: @PostConstruct and @PreDestroy
- ✅ Initialization callbacks after dependency injection
- ✅ Cleanup callbacks before bean destruction
- ✅ Resource management (connections, caches)

### Part 5: @Primary Annotation
- ✅ Marking default bean when multiple candidates exist
- ✅ Automatic selection without @Qualifier

### Part 6: @Profile Annotation
- ✅ Development profile with H2 database
- ✅ Production profile with PostgreSQL
- ✅ Environment-specific bean creation

## Key Concepts Demonstrated

### 1. Stereotype Hierarchy

```
@Component (generic)
    ├── @Service (business logic)
    ├── @Repository (data access)
    └── @Controller (web layer)
```

### 2. Dependency Injection

```java
// Constructor injection (recommended)
@Service
public class UserService {
    private final UserRepository repository;
    
    public UserService(UserRepository repository) {
        this.repository = repository;
    }
}
```

### 3. Configuration Class

```java
@Configuration
@ComponentScan("com.example")
@PropertySource("classpath:app.properties")
public class AppConfig {
    
    @Bean
    public MyService myService() {
        return new MyService();
    }
}
```

### 4. Property Injection

```java
@Value("${app.name}")
private String appName;

@Value("${app.timeout:30}") // Default: 30
private int timeout;
```

## Expected Output

```
╔══════════════════════════════════════════════╗
║      Spring Annotations Demo                ║
╚══════════════════════════════════════════════╝

==================================================
PART 1: Stereotype Annotations
==================================================

1. @Service, @Repository, @Component:
   UserRepository created
   UserService created with repository
   UserService initialized (@PostConstruct)
   User created with ID: 1
   Retrieved user: john.doe
   UserService cleanup (@PreDestroy)
...
```

## Annotation Reference

| Annotation | Purpose | Layer |
|------------|---------|-------|
| @Component | Generic component | Any |
| @Service | Business logic | Service |
| @Repository | Data access | Persistence |
| @Controller | Web requests | Web |
| @RestController | REST APIs | Web |
| @Configuration | Configuration class | Config |
| @Bean | Bean definition | Config |
| @Autowired | Dependency injection | Any |
| @Value | Property injection | Any |
| @Qualifier | Select specific bean | Any |
| @Primary | Default bean | Any |
| @Profile | Environment-specific | Any |
| @PostConstruct | Initialization | Any |
| @PreDestroy | Cleanup | Any |

## Best Practices Shown

1. ✅ **Constructor injection** (UserService)
2. ✅ **Property defaults** (@Value with `:`)
3. ✅ **Clear stereotypes** (@Service, @Repository)
4. ✅ **@Primary for default** (primaryDatabase)
5. ✅ **@Profile for environments** (dev/prod)
6. ✅ **Lifecycle management** (@PostConstruct/@PreDestroy)

## Real-World Applications

### 1. Layered Architecture
```java
@RestController
public class UserController {
    private final UserService service;
    
    public UserController(UserService service) {
        this.service = service;
    }
}

@Service
public class UserService {
    private final UserRepository repository;
    
    public UserService(UserRepository repository) {
        this.repository = repository;
    }
}

@Repository
public class UserRepository {
    // Data access
}
```

### 2. Environment-Specific Configuration
```java
@Configuration
public class DataSourceConfig {
    
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

### 3. Property-Driven Configuration
```java
@Component
public class EmailService {
    
    @Value("${email.host}")
    private String host;
    
    @Value("${email.port}")
    private int port;
    
    @Value("${email.from}")
    private String from;
}
```

## Common Interview Questions

**Q: What's the difference between @Component and @Bean?**
A: @Component is class-level for auto-scanning. @Bean is method-level in @Configuration for programmatic bean creation.

**Q: Why prefer constructor injection?**
A: Immutability, testability, clear dependencies, fails-fast, no reflection needed.

**Q: What does @Repository add over @Component?**
A: Exception translation - converts database exceptions to Spring's DataAccessException hierarchy.

**Q: @Primary vs @Qualifier?**
A: @Primary marks default bean. @Qualifier explicitly selects specific bean.

**Q: When are @PostConstruct and @PreDestroy called?**
A: @PostConstruct after dependency injection. @PreDestroy before bean destruction.

## Integration with Spring Boot

Spring Boot uses these annotations extensively:
- `@SpringBootApplication` includes @Configuration and @ComponentScan
- `@RestController` = @Controller + @ResponseBody
- Auto-configuration uses @Conditional annotations
- application.properties automatically loaded

## Next Steps

1. Run the demo and study each part
2. Modify properties and see changes
3. Add new stereotype-annotated classes
4. Experiment with profiles
5. Move to [Aspect-Oriented Programming](../../05-aspect-oriented-programming/)

---

**💡 Tip**: Master these annotations - they're the foundation of Spring development!
