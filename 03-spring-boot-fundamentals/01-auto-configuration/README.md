# Auto-Configuration in Spring Boot

> **Understanding how Spring Boot magically configures your application**

## 📚 Table of Contents

- [What is Auto-Configuration?](#what-is-auto-configuration)
- [The Magic Behind @SpringBootApplication](#the-magic-behind-springbootapplication)
- [How Auto-Configuration Works](#how-auto-configuration-works)
- [Conditional Annotations](#conditional-annotations)
- [Creating Custom Auto-Configuration](#creating-custom-auto-configuration)
- [Excluding Auto-Configuration](#excluding-auto-configuration)
- [Debugging Auto-Configuration](#debugging-auto-configuration)
- [Best Practices](#best-practices)
- [Demo Project](#demo-project)
- [Interview Questions](#interview-questions)

---

## What is Auto-Configuration?

Auto-configuration is Spring Boot's killer feature that automatically configures your Spring application based on:
- **Dependencies** on the classpath
- **Properties** you've defined
- **Beans** you've created

### The Problem Without Auto-Configuration

Traditional Spring requires extensive XML or Java configuration:

```java
@Configuration
public class TraditionalConfig {
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        ds.setUrl("jdbc:mysql://localhost:3306/mydb");
        ds.setUsername("root");
        ds.setPassword("password");
        return ds;
    }
    
    @Bean
    public EntityManagerFactory entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean factory = 
            new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(dataSource());
        factory.setPackagesToScan("com.example");
        factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        // ... 20 more lines of configuration
        return factory.getObject();
    }
    
    @Bean
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(entityManagerFactory());
        return txManager;
    }
}
```

### The Solution: Spring Boot Auto-Configuration

With Spring Boot, just add dependency and properties:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/mydb
spring.datasource.username=root
spring.datasource.password=password
```

That's it! DataSource, EntityManagerFactory, TransactionManager all configured automatically.

---

## The Magic Behind @SpringBootApplication

The `@SpringBootApplication` annotation is a composition of three powerful annotations:

```java
@SpringBootApplication
public class MyApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
}
```

### Breaking Down @SpringBootApplication

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@SpringBootConfiguration  // 1. Configuration class
@EnableAutoConfiguration  // 2. Enable auto-configuration
@ComponentScan(           // 3. Component scanning
    excludeFilters = {
        @Filter(type = FilterType.CUSTOM, 
                classes = TypeExcludeFilter.class),
        @Filter(type = FilterType.CUSTOM, 
                classes = AutoConfigurationExcludeFilter.class)
    }
)
public @interface SpringBootApplication {
    // ... attributes
}
```

#### 1. @SpringBootConfiguration

- Indicates that the class provides Spring Boot configuration
- Equivalent to `@Configuration`
- Allows the class to define `@Bean` methods

#### 2. @EnableAutoConfiguration

- **This is the heart of Spring Boot**
- Tells Spring Boot to "guess" how you want to configure Spring
- Based on jar dependencies you have added

#### 3. @ComponentScan

- Scans the package and sub-packages for components
- Looks for `@Component`, `@Service`, `@Repository`, `@Controller`
- Base package is the package containing the `@SpringBootApplication` class

---

## How Auto-Configuration Works

### Step-by-Step Process

1. **Application Startup**
   - `SpringApplication.run()` starts the Spring Boot application
   
2. **Load Auto-Configuration Classes**
   - Spring Boot looks for `META-INF/spring.factories` in all JARs
   - Loads all classes listed under `EnableAutoConfiguration` key
   
3. **Evaluate Conditions**
   - Each auto-configuration class has `@Conditional` annotations
   - Conditions check: classpath, beans, properties, resources
   
4. **Register Beans**
   - If conditions are met, beans are registered in the ApplicationContext
   - If conditions fail, configuration is skipped

### Example: DataSourceAutoConfiguration

```java
@Configuration
@ConditionalOnClass({ DataSource.class, EmbeddedDatabaseType.class })
@EnableConfigurationProperties(DataSourceProperties.class)
@Import({ 
    DataSourcePoolMetadataProvidersConfiguration.class,
    DataSourceInitializationConfiguration.class 
})
public class DataSourceAutoConfiguration {
    
    @Configuration
    @Conditional(EmbeddedDatabaseCondition.class)
    @ConditionalOnMissingBean(DataSource.class)
    protected static class EmbeddedDatabaseConfiguration {
        
        @Bean
        public DataSource dataSource(DataSourceProperties properties) {
            return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .build();
        }
    }
    
    // More configurations...
}
```

**What happens here?**

1. `@ConditionalOnClass` - Only if DataSource is on classpath
2. `@ConditionalOnMissingBean` - Only if user hasn't defined DataSource
3. If conditions pass, creates embedded H2 DataSource automatically

---

## Conditional Annotations

Spring Boot provides many conditional annotations to control when configuration should apply.

### @ConditionalOnClass

Applies configuration only if specified classes are present on the classpath.

```java
@Configuration
@ConditionalOnClass(DataSource.class)
public class DatabaseConfig {
    
    @Bean
    public DatabaseService databaseService() {
        return new DatabaseService();
    }
}
```

**Use Case**: Only configure database-related beans when JDBC driver is present.

### @ConditionalOnMissingBean

Applies configuration only if no bean of specified type exists.

```java
@Configuration
public class DefaultConfig {
    
    @Bean
    @ConditionalOnMissingBean(UserService.class)
    public UserService userService() {
        return new DefaultUserService();
    }
}
```

**Use Case**: Provide default implementation that can be overridden by user.

### @ConditionalOnProperty

Applies configuration based on property value.

```java
@Configuration
@ConditionalOnProperty(
    name = "feature.email.enabled",
    havingValue = "true",
    matchIfMissing = false
)
public class EmailConfig {
    
    @Bean
    public EmailService emailService() {
        return new EmailService();
    }
}
```

**Use Case**: Enable/disable features via configuration.

### @ConditionalOnBean

Applies configuration only if specified bean exists.

```java
@Configuration
@ConditionalOnBean(DataSource.class)
public class JpaConfig {
    
    @Bean
    public JpaRepositoryFactory jpaRepositoryFactory() {
        return new JpaRepositoryFactory();
    }
}
```

### @ConditionalOnMissingClass

Opposite of `@ConditionalOnClass`.

```java
@Configuration
@ConditionalOnMissingClass("com.mongodb.client.MongoClient")
public class SqlOnlyConfig {
    // SQL-specific configuration
}
```

### @ConditionalOnResource

Applies configuration if specified resource is present.

```java
@Configuration
@ConditionalOnResource(resources = "classpath:custom-config.xml")
public class CustomConfig {
    // Load custom configuration
}
```

### @ConditionalOnWebApplication

Applies only in web application context.

```java
@Configuration
@ConditionalOnWebApplication(type = Type.SERVLET)
public class WebConfig {
    
    @Bean
    public FilterRegistrationBean loggingFilter() {
        // Web filter configuration
    }
}
```

### Summary Table

| Annotation | Condition | Example Use Case |
|------------|-----------|------------------|
| `@ConditionalOnClass` | Class present on classpath | Configure Redis if Redis client present |
| `@ConditionalOnMissingClass` | Class absent from classpath | Use in-memory cache if Redis absent |
| `@ConditionalOnBean` | Bean exists in context | Configure secondary bean depending on primary |
| `@ConditionalOnMissingBean` | Bean doesn't exist | Provide default implementation |
| `@ConditionalOnProperty` | Property has specific value | Feature flags |
| `@ConditionalOnResource` | Resource exists | Load config from file if present |
| `@ConditionalOnWebApplication` | Is web application | Web-specific beans |
| `@ConditionalOnNotWebApplication` | Is not web application | CLI-specific beans |

---

## Creating Custom Auto-Configuration

### Step 1: Create Auto-Configuration Class

```java
package com.example.autoconfigure;

import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(MyService.class)
@ConditionalOnProperty(
    prefix = "myservice",
    name = "enabled",
    havingValue = "true",
    matchIfMissing = true
)
@EnableConfigurationProperties(MyServiceProperties.class)
public class MyServiceAutoConfiguration {
    
    @Bean
    @ConditionalOnMissingBean
    public MyService myService(MyServiceProperties properties) {
        return new MyService(properties);
    }
}
```

### Step 2: Create Properties Class

```java
package com.example.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "myservice")
public class MyServiceProperties {
    
    private boolean enabled = true;
    private String endpoint = "http://localhost:8080";
    private int timeout = 5000;
    
    // Getters and setters...
}
```

### Step 3: Register Auto-Configuration

Create `src/main/resources/META-INF/spring.factories`:

```properties
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
com.example.autoconfigure.MyServiceAutoConfiguration
```

### Step 4: Use in Application

```properties
# application.properties
myservice.enabled=true
myservice.endpoint=http://api.example.com
myservice.timeout=10000
```

```java
@SpringBootApplication
public class Application {
    
    @Autowired
    private MyService myService; // Automatically configured!
    
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

---

## Excluding Auto-Configuration

### Method 1: Using @SpringBootApplication

```java
@SpringBootApplication(exclude = {
    DataSourceAutoConfiguration.class,
    HibernateJpaAutoConfiguration.class
})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

### Method 2: Using application.properties

```properties
spring.autoconfigure.exclude=\
org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,\
org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration
```

### Method 3: Using @EnableAutoConfiguration

```java
@Configuration
@EnableAutoConfiguration(exclude = {
    DataSourceAutoConfiguration.class
})
public class CustomConfiguration {
    // Custom beans
}
```

### When to Exclude Auto-Configuration?

1. **Using custom implementation**: You want full control over configuration
2. **Classpath pollution**: A dependency brings unwanted auto-configuration
3. **Performance**: Reduce startup time by disabling unused features
4. **Testing**: Exclude certain configurations in test context

---

## Debugging Auto-Configuration

### Enable Debug Logging

```properties
# application.properties
debug=true
```

Or start with:
```bash
java -jar myapp.jar --debug
```

### Auto-Configuration Report

When debug mode is enabled, Spring Boot prints an auto-configuration report:

```
============================
CONDITIONS EVALUATION REPORT
============================

Positive matches:
-----------------
DataSourceAutoConfiguration matched:
   - @ConditionalOnClass found required classes 'DataSource', 'EmbeddedDatabaseType'
   
JpaRepositoriesAutoConfiguration matched:
   - @ConditionalOnClass found required class 'JpaRepository'

Negative matches:
-----------------
MongoAutoConfiguration:
   Did not match:
      - @ConditionalOnClass did not find required class 'MongoClient'
      
RedisAutoConfiguration:
   Did not match:
      - @ConditionalOnClass did not find required class 'RedisConnectionFactory'

Exclusions:
-----------
None

Unconditional classes:
----------------------
org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration
org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration
```

### Actuator Endpoint

Add actuator dependency and check conditions:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

```properties
management.endpoints.web.exposure.include=conditions
```

Access: `http://localhost:8080/actuator/conditions`

---

## Best Practices

### 1. Order Your Auto-Configuration

Use `@AutoConfigureAfter` and `@AutoConfigureBefore`:

```java
@Configuration
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
public class MyDatabaseConfig {
    // Runs after DataSource is configured
}
```

### 2. Use @ConditionalOnMissingBean for Defaults

Allow users to override your configuration:

```java
@Bean
@ConditionalOnMissingBean
public MyService myService() {
    return new DefaultMyService();
}
```

### 3. Provide Configuration Properties

Make your auto-configuration customizable:

```java
@ConfigurationProperties(prefix = "myapp")
public class MyAppProperties {
    private String feature = "default";
    // getters/setters
}
```

### 4. Document Your Auto-Configuration

Create `META-INF/spring-configuration-metadata.json`:

```json
{
  "properties": [
    {
      "name": "myapp.feature",
      "type": "java.lang.String",
      "description": "Description of the feature.",
      "defaultValue": "default"
    }
  ]
}
```

### 5. Test Your Auto-Configuration

```java
@SpringBootTest
class MyServiceAutoConfigurationTest {
    
    @Autowired(required = false)
    private MyService myService;
    
    @Test
    void shouldAutoConfigureWhenEnabled() {
        assertThat(myService).isNotNull();
    }
}
```

### 6. Fail Fast with Validation

```java
@ConfigurationProperties(prefix = "myapp")
@Validated
public class MyAppProperties {
    
    @NotNull
    @Pattern(regexp = "^https?://.*")
    private String endpoint;
    
    // getters/setters
}
```

---

## Demo Project

See [demo-auto-configuration](demo-auto-configuration/) for a complete working example that demonstrates:

1. **Understanding @SpringBootApplication components**
2. **Built-in conditional annotations**
3. **Creating custom auto-configuration**
4. **Configuration properties**
5. **Debugging auto-configuration**
6. **Excluding unwanted configurations**

### Running the Demo

```bash
cd demo-auto-configuration
mvn clean install
mvn spring-boot:run
```

---

## Interview Questions

### Q1: What is the difference between @Configuration and @SpringBootConfiguration?

**Answer**: `@SpringBootConfiguration` is a specialized form of `@Configuration` used by Spring Boot. It serves the same purpose but indicates that a class provides Spring Boot-specific configuration. Spring Boot uses it to find configuration classes during testing.

```java
// Both are functionally equivalent
@Configuration
public class MyConfig { }

@SpringBootConfiguration
public class MyBootConfig { }
```

Key differences:
- Only one `@SpringBootConfiguration` should exist per application
- Used by `@SpringBootTest` to find configuration
- `@SpringBootApplication` includes `@SpringBootConfiguration`

### Q2: How does Spring Boot determine which auto-configurations to apply?

**Answer**: Spring Boot uses a multi-step process:

1. **Loads candidates**: Reads `META-INF/spring.factories` from all JARs
2. **Evaluates conditions**: Checks `@Conditional*` annotations
   - Classpath conditions (`@ConditionalOnClass`)
   - Bean conditions (`@ConditionalOnBean`, `@ConditionalOnMissingBean`)
   - Property conditions (`@ConditionalOnProperty`)
3. **Applies configurations**: Registers beans if all conditions pass
4. **Respects order**: Uses `@AutoConfigureAfter`/`@AutoConfigureBefore`

### Q3: What happens if you define your own DataSource bean?

**Answer**: Spring Boot's auto-configuration respects user-defined beans due to `@ConditionalOnMissingBean`:

```java
@Configuration
public class MyDataSourceConfig {
    
    @Bean
    public DataSource dataSource() {
        // Your custom DataSource
        return new HikariDataSource();
    }
}
```

Spring Boot's `DataSourceAutoConfiguration` has:
```java
@Bean
@ConditionalOnMissingBean(DataSource.class)
public DataSource defaultDataSource() {
    // This won't be created!
}
```

**Result**: Your custom DataSource is used; auto-configured one is skipped.

### Q4: How do you disable a specific auto-configuration?

**Answer**: Three ways:

1. **Annotation**:
```java
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
```

2. **Properties**:
```properties
spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
```

3. **Programmatic**:
```java
SpringApplication app = new SpringApplication(MyApp.class);
app.setAutoConfiguration(Collections.emptyList());
```

### Q5: What is the purpose of spring.factories?

**Answer**: `spring.factories` is Spring Boot's mechanism for registering auto-configurations and other components. Located in `META-INF/spring.factories`, it maps interface names to implementation classes:

```properties
# Example spring.factories
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
com.example.MyAutoConfiguration,\
com.example.AnotherAutoConfiguration

org.springframework.context.ApplicationListener=\
com.example.MyApplicationListener
```

Spring Boot scans all JARs for this file and loads the specified classes.

### Q6: Explain @ConditionalOnProperty with matchIfMissing

**Answer**:

```java
@ConditionalOnProperty(
    name = "feature.enabled",
    havingValue = "true",
    matchIfMissing = true  // ← Key parameter
)
```

- `matchIfMissing = true`: Condition passes if property is **not defined**
- `matchIfMissing = false`: Condition fails if property is **not defined**

**Example**:
```java
// This configuration is enabled by default
@ConditionalOnProperty(
    name = "email.service.enabled",
    havingValue = "true",
    matchIfMissing = true
)
public class EmailConfig {
    // Applies unless explicitly disabled
}
```

To disable: `email.service.enabled=false`

### Q7: How would you create a custom starter?

**Answer**: Steps to create a custom starter:

1. **Create auto-configuration module** (`my-service-autoconfigure`)
2. **Create starter module** (`my-service-spring-boot-starter`)
3. **Register auto-configuration** in `spring.factories`

Structure:
```
my-service-autoconfigure/
├── src/main/java/
│   └── com/example/autoconfigure/
│       ├── MyServiceAutoConfiguration.java
│       └── MyServiceProperties.java
└── src/main/resources/
    └── META-INF/spring.factories

my-service-spring-boot-starter/
└── pom.xml (depends on autoconfigure module)
```

Users add only:
```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>my-service-spring-boot-starter</artifactId>
</dependency>
```

### Q8: What is the order of precedence for configuration properties?

**Answer**: From highest to lowest priority:

1. Command line arguments: `--server.port=8081`
2. `SPRING_APPLICATION_JSON` properties
3. Java System properties: `-Dserver.port=8081`
4. OS environment variables: `SERVER_PORT=8081`
5. `application-{profile}.properties` outside of jar
6. `application-{profile}.properties` inside jar
7. `application.properties` outside of jar
8. `application.properties` inside jar
9. `@PropertySource` annotations
10. Default properties: `SpringApplication.setDefaultProperties()`

**Example**:
```bash
# application.properties
server.port=8080

# Run with override
java -jar app.jar --server.port=9090
# Result: Uses port 9090
```

---

## Summary

| Concept | Key Points |
|---------|------------|
| **Auto-Configuration** | Automatic configuration based on classpath, beans, and properties |
| **@SpringBootApplication** | Combines `@Configuration`, `@EnableAutoConfiguration`, `@ComponentScan` |
| **Conditional Annotations** | Control when configuration applies (`@ConditionalOnClass`, etc.) |
| **Custom Auto-Configuration** | Create reusable configuration with `spring.factories` |
| **Debugging** | Use `--debug` or actuator `/conditions` endpoint |
| **Best Practices** | Allow overrides with `@ConditionalOnMissingBean`, provide properties |

Auto-configuration is what makes Spring Boot "opinionated but flexible" – it provides smart defaults but respects your custom configuration.

---

**Next**: [Configuration Management](../02-configuration-management/)
