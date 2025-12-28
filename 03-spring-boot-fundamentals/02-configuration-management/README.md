# Configuration Management in Spring Boot

> **Master externalized configuration for flexible, environment-specific applications**

## 📚 Table of Contents

- [What is Configuration Management?](#what-is-configuration-management)
- [application.properties vs application.yml](#applicationproperties-vs-applicationyml)
- [Property Binding with @Value](#property-binding-with-value)
- [Type-Safe Properties with @ConfigurationProperties](#type-safe-properties-with-configurationproperties)
- [Profile-Specific Configuration](#profile-specific-configuration)
- [Externalized Configuration](#externalized-configuration)
- [Configuration Precedence](#configuration-precedence)
- [Property Validation](#property-validation)
- [Best Practices](#best-practices)
- [Demo Project](#demo-project)
- [Interview Questions](#interview-questions)

---

## What is Configuration Management?

Configuration management in Spring Boot allows you to externalize configuration so you can work with the same application code in different environments (development, testing, production).

### Why External Configuration?

1. **Environment-specific settings**: Different database URLs, credentials per environment
2. **Feature toggles**: Enable/disable features without redeployment
3. **No code changes**: Modify behavior through configuration
4. **Security**: Keep sensitive data outside source control
5. **Flexibility**: Easy to change without recompilation

### Configuration Sources

Spring Boot can load properties from multiple sources:

- `application.properties` or `application.yml`
- Environment variables
- Command-line arguments
- Java system properties
- Profile-specific files
- External configuration files

---

## application.properties vs application.yml

### application.properties Format

```properties
# Server configuration
server.port=8080
server.servlet.context-path=/api

# Database configuration
spring.datasource.url=jdbc:mysql://localhost:3306/mydb
spring.datasource.username=root
spring.datasource.password=secret

# Application configuration
app.name=My Application
app.description=A Spring Boot application
app.version=1.0.0
```

### application.yml Format

```yaml
# Server configuration
server:
  port: 8080
  servlet:
    context-path: /api

# Database configuration
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mydb
    username: root
    password: secret

# Application configuration
app:
  name: My Application
  description: A Spring Boot application
  version: 1.0.0
```

### Comparison

| Feature | .properties | .yml |
|---------|-------------|------|
| **Readability** | Good | Excellent (hierarchical) |
| **Conciseness** | Verbose | Compact |
| **Lists/Arrays** | Verbose | Natural syntax |
| **Comments** | `#` only | `#` only |
| **IDE Support** | Excellent | Good |
| **Popularity** | High | Increasing |

### YAML Advantages

**1. Hierarchical Structure**

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/mydb
    username: root
    password: secret
    hikari:
      connection-timeout: 30000
      maximum-pool-size: 10
```

vs properties:

```properties
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/mydb
spring.datasource.username=root
spring.datasource.password=secret
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.maximum-pool-size=10
```

**2. Lists and Arrays**

```yaml
app:
  servers:
    - name: server1
      ip: 192.168.1.1
    - name: server2
      ip: 192.168.1.2
```

vs properties:

```properties
app.servers[0].name=server1
app.servers[0].ip=192.168.1.1
app.servers[1].name=server2
app.servers[1].ip=192.168.1.2
```

### When to Use Which?

- **Use .properties**: If team is more familiar, simpler configuration
- **Use .yml**: Complex hierarchies, lists, better readability

---

## Property Binding with @Value

`@Value` injects property values directly into Spring beans.

### Basic Usage

```java
@Component
public class AppConfig {
    
    @Value("${app.name}")
    private String appName;
    
    @Value("${server.port}")
    private int serverPort;
    
    @Value("${app.enabled:true}")  // Default value: true
    private boolean enabled;
}
```

### SpEL (Spring Expression Language)

```java
@Component
public class AdvancedConfig {
    
    // Property with default
    @Value("${app.timeout:5000}")
    private int timeout;
    
    // Expression
    @Value("#{systemProperties['user.name']}")
    private String username;
    
    // Mathematical expression
    @Value("#{${app.max-connections} * 2}")
    private int calculatedValue;
    
    // Conditional
    @Value("#{${app.cache-enabled} ? 'Redis' : 'InMemory'}")
    private String cacheType;
    
    // List
    @Value("${app.allowed-origins}")
    private List<String> allowedOrigins;
}
```

### Limitations of @Value

1. **No type safety**: Compile-time checks limited
2. **Scattered configuration**: Properties spread across classes
3. **No validation**: No built-in validation support
4. **No IDE support**: Limited autocomplete
5. **No relaxed binding**: Must match property name exactly

---

## Type-Safe Properties with @ConfigurationProperties

`@ConfigurationProperties` binds properties to a POJO with full type safety.

### Basic Example

**application.yml:**

```yaml
app:
  name: My Application
  version: 1.0.0
  max-connections: 100
  timeout: 5000
  features:
    - authentication
    - caching
    - monitoring
```

**Configuration Class:**

```java
@Configuration
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    
    private String name;
    private String version;
    private int maxConnections;
    private int timeout;
    private List<String> features;
    
    // Getters and setters...
}
```

**Using the Properties:**

```java
@Service
public class AppService {
    
    private final AppProperties appProperties;
    
    public AppService(AppProperties appProperties) {
        this.appProperties = appProperties;
    }
    
    public void printConfig() {
        System.out.println("App: " + appProperties.getName());
        System.out.println("Version: " + appProperties.getVersion());
    }
}
```

### Nested Properties

```yaml
database:
  host: localhost
  port: 3306
  credentials:
    username: admin
    password: secret
  pool:
    min-size: 5
    max-size: 20
    timeout: 30000
```

```java
@ConfigurationProperties(prefix = "database")
public class DatabaseProperties {
    
    private String host;
    private int port;
    private Credentials credentials;
    private Pool pool;
    
    public static class Credentials {
        private String username;
        private String password;
        // getters/setters
    }
    
    public static class Pool {
        private int minSize;
        private int maxSize;
        private long timeout;
        // getters/setters
    }
    
    // getters/setters
}
```

### Relaxed Binding

Spring Boot supports multiple naming conventions:

```yaml
# All of these bind to the same property:
my-property: value1
my_property: value2
myProperty: value3
MY_PROPERTY: value4
```

```java
@ConfigurationProperties(prefix = "app")
public class AppConfig {
    private String myProperty;  // Binds to any of the above
}
```

### @ConfigurationProperties vs @Value

| Feature | @Value | @ConfigurationProperties |
|---------|--------|-------------------------|
| **Type Safety** | Limited | Strong |
| **Validation** | Manual | Built-in (@Validated) |
| **IDE Support** | Basic | Excellent (with processor) |
| **Relaxed Binding** | No | Yes |
| **Grouping** | No | Yes (logical grouping) |
| **Use Case** | Simple values | Complex configuration |

---

## Profile-Specific Configuration

Profiles allow different configurations for different environments.

### Defining Profiles

**application.yml** (common config):

```yaml
app:
  name: My Application
  
spring:
  profiles:
    active: dev  # Default profile
```

**application-dev.yml** (development):

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:h2:mem:devdb
    username: sa
    password:

logging:
  level:
    root: DEBUG
```

**application-test.yml** (testing):

```yaml
server:
  port: 8081

spring:
  datasource:
    url: jdbc:h2:mem:testdb
    username: sa
    password:

logging:
  level:
    root: INFO
```

**application-prod.yml** (production):

```yaml
server:
  port: 80

spring:
  datasource:
    url: jdbc:mysql://prod-server:3306/proddb
    username: ${DB_USERNAME}  # From environment variable
    password: ${DB_PASSWORD}

logging:
  level:
    root: WARN
```

### Activating Profiles

**Method 1: application.properties**

```properties
spring.profiles.active=prod
```

**Method 2: Command Line**

```bash
java -jar myapp.jar --spring.profiles.active=prod
```

**Method 3: Environment Variable**

```bash
export SPRING_PROFILES_ACTIVE=prod
java -jar myapp.jar
```

**Method 4: Maven**

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

**Method 5: IDE (IntelliJ IDEA)**

Run Configuration → Environment Variables → `SPRING_PROFILES_ACTIVE=prod`

### Multiple Active Profiles

```properties
spring.profiles.active=prod,monitoring,logging
```

Config files loaded in order:
1. `application.yml`
2. `application-prod.yml`
3. `application-monitoring.yml`
4. `application-logging.yml`

Later profiles override earlier ones.

### Profile-Specific Beans

```java
@Configuration
@Profile("dev")
public class DevConfig {
    
    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .build();
    }
}

@Configuration
@Profile("prod")
public class ProdConfig {
    
    @Bean
    public DataSource dataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:mysql://prod-server:3306/proddb");
        return ds;
    }
}
```

### Conditional Profile Activation

```java
@Configuration
@Profile("!prod")  // All except prod
public class NonProdConfig {
    // Development and test configuration
}

@Configuration
@Profile({"dev", "test"})  // dev OR test
public class DevTestConfig {
    // Configuration for both dev and test
}
```

---

## Externalized Configuration

Load configuration from outside the application JAR.

### External application.properties

**Directory structure:**

```
/app
  ├── myapp.jar
  └── config/
      └── application.properties
```

Spring Boot searches in this order:

1. `/config` subdirectory in current directory
2. Current directory
3. `classpath:/config` package
4. Classpath root

**Running:**

```bash
java -jar myapp.jar
# Automatically loads ./config/application.properties
```

### Custom Config Location

```bash
java -jar myapp.jar --spring.config.location=file:/etc/myapp/config/
```

### Environment Variables

Map properties to environment variables:

```yaml
# application.yml
spring:
  datasource:
    url: ${DATABASE_URL}
    username: ${DATABASE_USERNAME}
    password: ${DATABASE_PASSWORD}
```

```bash
export DATABASE_URL=jdbc:mysql://localhost:3306/mydb
export DATABASE_USERNAME=admin
export DATABASE_PASSWORD=secret
java -jar myapp.jar
```

### Command-Line Arguments

```bash
java -jar myapp.jar \
  --server.port=9090 \
  --spring.datasource.url=jdbc:mysql://localhost:3306/mydb \
  --app.feature.enabled=true
```

---

## Configuration Precedence

Spring Boot loads properties from multiple sources. **Higher numbers override lower numbers:**

1. **Default properties** (SpringApplication.setDefaultProperties)
2. **@PropertySource** on @Configuration classes
3. **Config data** (application.properties/yml)
4. **Config data for profile** (application-{profile}.properties/yml)
5. **OS environment variables**
6. **Java System properties** (System.getProperties())
7. **JNDI attributes** from java:comp/env
8. **ServletConfig init parameters**
9. **ServletContext init parameters**
10. **Command line arguments**

### Example

**application.yml:**

```yaml
server:
  port: 8080
```

**application-prod.yml:**

```yaml
server:
  port: 80
```

**Command line:**

```bash
java -jar myapp.jar --spring.profiles.active=prod --server.port=9090
```

**Result:** Port = `9090` (command line overrides profile config)

### Precedence Table

| Source | Priority | Use Case |
|--------|----------|----------|
| Command Line | Highest | Quick overrides, CI/CD |
| Environment Variables | High | Docker, K8s secrets |
| Profile Files | Medium | Environment-specific |
| Default Files | Low | Application defaults |

---

## Property Validation

Validate configuration properties at startup.

### Using Bean Validation

**Add dependency:**

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

**Configuration class:**

```java
@ConfigurationProperties(prefix = "app")
@Validated
public class AppProperties {
    
    @NotBlank(message = "Application name is required")
    private String name;
    
    @Min(value = 1024, message = "Port must be >= 1024")
    @Max(value = 65535, message = "Port must be <= 65535")
    private int port;
    
    @Email(message = "Invalid email address")
    private String adminEmail;
    
    @Pattern(regexp = "^https?://.*", message = "Must be valid URL")
    private String apiEndpoint;
    
    @NotEmpty(message = "At least one feature required")
    private List<String> features;
    
    @Valid  // Validate nested object
    private Database database;
    
    public static class Database {
        @NotBlank
        private String host;
        
        @Min(1)
        @Max(65535)
        private int port;
        
        // getters/setters
    }
    
    // getters/setters
}
```

**Validation on startup:**

```
***************************
APPLICATION FAILED TO START
***************************

Description:

Binding to target org.springframework.boot.context.properties.bind.BindException: 
Failed to bind properties under 'app' to com.example.AppProperties:

    Property: app.port
    Value: 100
    Reason: must be greater than or equal to 1024
```

### Custom Validation

```java
@ConfigurationProperties(prefix = "app")
@Validated
public class AppProperties {
    
    @ValidUrl  // Custom validator
    private String endpoint;
    
    // getters/setters
}

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UrlValidator.class)
public @interface ValidUrl {
    String message() default "Invalid URL format";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

public class UrlValidator implements ConstraintValidator<ValidUrl, String> {
    
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;  // Use @NotBlank for required check
        }
        try {
            new URL(value);
            return value.startsWith("http://") || value.startsWith("https://");
        } catch (MalformedURLException e) {
            return false;
        }
    }
}
```

---

## Best Practices

### 1. Use @ConfigurationProperties for Complex Config

```java
// ❌ Don't scatter @Value annotations
@Component
public class BadExample {
    @Value("${app.name}") private String name;
    @Value("${app.version}") private String version;
    @Value("${app.timeout}") private int timeout;
}

// ✅ Group related properties
@ConfigurationProperties(prefix = "app")
public class GoodExample {
    private String name;
    private String version;
    private int timeout;
}
```

### 2. Provide Sensible Defaults

```yaml
app:
  timeout: ${APP_TIMEOUT:5000}  # Default: 5000
  max-retries: ${APP_MAX_RETRIES:3}  # Default: 3
```

### 3. Use Profiles Wisely

```
application.yml          # Common config
application-dev.yml      # Development overrides
application-test.yml     # Test overrides
application-prod.yml     # Production overrides
```

### 4. Never Commit Secrets

```yaml
# ❌ Never do this
spring:
  datasource:
    password: mySecretPassword123

# ✅ Use environment variables
spring:
  datasource:
    password: ${DB_PASSWORD}
```

**Add to .gitignore:**

```
application-local.yml
application-*.properties
*.env
```

### 5. Validate Configuration

```java
@ConfigurationProperties(prefix = "app")
@Validated  // Always validate
public class AppProperties {
    
    @NotBlank
    private String name;
    
    @Min(1000)
    private int timeout;
}
```

### 6. Use Configuration Processor

**Add dependency for IDE autocomplete:**

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-configuration-processor</artifactId>
    <optional>true</optional>
</dependency>
```

**Creates metadata:**

```json
{
  "properties": [
    {
      "name": "app.name",
      "type": "java.lang.String",
      "description": "Application name"
    }
  ]
}
```

### 7. Use Meaningful Property Names

```yaml
# ❌ Bad
app.x: 100
app.val: true

# ✅ Good
app.max-connections: 100
app.cache-enabled: true
```

### 8. Document Your Properties

```java
/**
 * Application configuration properties.
 */
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    
    /**
     * Maximum number of concurrent connections.
     * Default: 100
     */
    private int maxConnections = 100;
    
    /**
     * Connection timeout in milliseconds.
     * Must be between 1000 and 60000.
     */
    @Min(1000)
    @Max(60000)
    private int timeout = 5000;
}
```

---

## Demo Project

See [demo-configuration-management](demo-configuration-management/) for a complete working example that demonstrates:

1. **Properties vs YAML comparison**
2. **@Value and @ConfigurationProperties**
3. **Multiple profiles (dev, test, prod)**
4. **Externalized configuration**
5. **Property validation**
6. **Configuration precedence**

### Running the Demo

```bash
cd demo-configuration-management
mvn clean install

# Run with dev profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Run with prod profile
mvn spring-boot:run -Dspring-boot.run.profiles=prod

# Override properties
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=9090"
```

---

## Interview Questions

### Q1: What is the order of precedence for configuration in Spring Boot?

**Answer**: From lowest to highest priority:

1. Default properties
2. @PropertySource
3. application.properties/yml (in JAR)
4. application-{profile}.properties (in JAR)
5. application.properties (outside JAR)
6. application-{profile}.properties (outside JAR)
7. OS environment variables
8. Java system properties
9. Command-line arguments

**Command-line has highest priority and overrides everything else.**

### Q2: What's the difference between @Value and @ConfigurationProperties?

**Answer**:

| Aspect | @Value | @ConfigurationProperties |
|--------|--------|-------------------------|
| **Usage** | Individual property injection | Bulk property binding |
| **Type Safety** | Limited | Strong (POJO-based) |
| **Validation** | No built-in support | @Validated support |
| **Relaxed Binding** | No | Yes (kebab-case, etc.) |
| **IDE Support** | Basic | Excellent (with processor) |
| **Best For** | Simple, scattered values | Complex, grouped config |

### Q3: How do you activate a Spring profile?

**Answer**: Multiple ways:

1. **application.properties**: `spring.profiles.active=prod`
2. **Command line**: `java -jar app.jar --spring.profiles.active=prod`
3. **Environment variable**: `SPRING_PROFILES_ACTIVE=prod`
4. **Maven**: `mvn spring-boot:run -Dspring-boot.run.profiles=prod`
5. **Programmatically**:
   ```java
   SpringApplication app = new SpringApplication(MyApp.class);
   app.setAdditionalProfiles("prod");
   ```

### Q4: How would you externalize sensitive configuration like passwords?

**Answer**: Several secure approaches:

1. **Environment Variables**:
   ```yaml
   spring:
     datasource:
       password: ${DB_PASSWORD}
   ```

2. **External Config Files** (outside JAR):
   ```bash
   java -jar app.jar --spring.config.location=file:/secure/config/
   ```

3. **Spring Cloud Config Server**: Centralized configuration
4. **Vault Integration**: HashiCorp Vault for secrets
5. **Kubernetes Secrets**: For K8s deployments
6. **AWS Secrets Manager / Azure Key Vault**: Cloud-native solutions

**Never commit secrets to source control!**

### Q5: What is relaxed binding in Spring Boot?

**Answer**: Relaxed binding allows different property naming conventions to bind to the same field:

```java
@ConfigurationProperties(prefix = "app")
public class AppConfig {
    private String myProperty;
}
```

All of these bind to `myProperty`:
- `app.my-property` (kebab-case - recommended)
- `app.my_property` (underscore)
- `app.myProperty` (camelCase)
- `APP_MY_PROPERTY` (uppercase - for env vars)

**Best practice**: Use kebab-case in property files, uppercase with underscores for environment variables.

### Q6: How do you validate configuration properties?

**Answer**: Use `@Validated` with JSR-303 annotations:

```java
@ConfigurationProperties(prefix = "app")
@Validated
public class AppProperties {
    
    @NotBlank(message = "Name is required")
    private String name;
    
    @Min(1024)
    @Max(65535)
    private int port;
    
    @Email
    private String adminEmail;
    
    @Valid  // Validate nested
    private DatabaseConfig database;
}
```

If validation fails, application won't start with clear error message.

### Q7: Can you override profile-specific properties?

**Answer**: Yes, using higher precedence sources:

**application-prod.yml**: `server.port=80`

**Override with environment variable**:
```bash
export SERVER_PORT=8080
java -jar app.jar --spring.profiles.active=prod
# Result: Uses port 8080 (env var overrides profile)
```

**Override with command line**:
```bash
java -jar app.jar --spring.profiles.active=prod --server.port=9090
# Result: Uses port 9090 (command line has highest priority)
```

### Q8: What is @PropertySource and when should you use it?

**Answer**: `@PropertySource` loads properties from a specific file:

```java
@Configuration
@PropertySource("classpath:custom.properties")
public class CustomConfig {
    
    @Value("${custom.property}")
    private String customProperty;
}
```

**When to use**:
- Loading properties from non-standard files
- Modularizing configuration
- Loading external configuration files

**Limitation**: Doesn't support YAML files (only .properties).

---

## Summary

| Concept | Key Points |
|---------|------------|
| **Property Files** | application.properties (flat) vs application.yml (hierarchical) |
| **@Value** | Simple property injection with SpEL support |
| **@ConfigurationProperties** | Type-safe, bulk property binding with validation |
| **Profiles** | Environment-specific configuration (dev, test, prod) |
| **Externalization** | Load config from environment, files, command line |
| **Precedence** | Command line > Env vars > Profile files > Default files |
| **Validation** | Use @Validated with JSR-303 constraints |

Configuration management is crucial for building flexible, environment-agnostic applications that can be deployed anywhere without code changes.

---

**Next**: [Starter Dependencies](../03-starter-dependencies/)
