# Demo: Spring Boot Auto-Configuration

This demo demonstrates Spring Boot's auto-configuration mechanism and conditional bean creation.

## What This Demo Shows

1. **@SpringBootApplication Breakdown**
   - How it combines @Configuration, @EnableAutoConfiguration, and @ComponentScan
   - Bean registration and discovery

2. **Custom Auto-Configuration**
   - Creating auto-configured beans (`MyCustomService`)
   - Using `@AutoConfiguration` annotation
   - Binding configuration properties with `@ConfigurationProperties`

3. **Conditional Bean Creation**
   - `@ConditionalOnProperty`: EmailService, NotificationService
   - `@ConditionalOnClass`: DatabaseService (when H2 is present)
   - `@ConditionalOnMissingBean`: DefaultService (fallback implementation)

4. **Configuration Properties**
   - Property binding with `@ConfigurationProperties`
   - Validation with Jakarta Bean Validation
   - Type-safe configuration

5. **Bean Inspection**
   - Listing all auto-configured beans
   - Checking conditional bean creation
   - Understanding ApplicationContext

## Project Structure

```
demo-auto-configuration/
├── src/main/java/com/masterclass/autoconfigure/
│   ├── AutoConfigurationDemoApp.java          # Main application with demos
│   ├── MyCustomService.java                    # Service to be auto-configured
│   ├── MyServiceProperties.java                # Configuration properties
│   ├── config/
│   │   ├── MyServiceAutoConfiguration.java     # Custom auto-configuration
│   │   └── ConditionalBeansConfig.java         # Conditional beans examples
│   └── service/
│       ├── EmailService.java                   # @ConditionalOnProperty demo
│       ├── NotificationService.java            # Feature toggle demo
│       ├── DatabaseService.java                # @ConditionalOnClass demo
│       └── DefaultService.java                 # @ConditionalOnMissingBean demo
└── src/main/resources/
    └── application.properties                   # Configuration
```

## Running the Demo

### Prerequisites
- Java 17 or higher
- Maven 3.6+

### Option 1: Using Maven

```bash
cd demo-auto-configuration
mvn clean install
mvn spring-boot:run
```

### Option 2: Using Java

```bash
mvn clean package
java -jar target/demo-auto-configuration-1.0.0.jar
```

### Option 3: With Debug Mode

To see the full auto-configuration report:

```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--debug
```

Or modify `application.properties`:
```properties
debug=true
```

## Expected Output

### Part 1: All Beans Registered

```
--- Part 1: All Beans Registered by Auto-Configuration ---

Total beans registered: 87
Showing first 20 beans:
  - autoConfigurationDemoApp
  - conditionalBeansConfig
  - myServiceAutoConfiguration
  - org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration
  - org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration
  ... (and more)

Checking for specific auto-configured beans:
  ✓ Application bean: autoConfigurationDemoApp
  ✓ Custom service bean: myCustomService
  ✓ Email service: emailService (conditional)
  ✗ Notification service: notificationService not found
```

### Part 2: Custom Auto-Configuration

```
--- Part 2: Custom Auto-Configuration ---

✓ MyCustomService was auto-configured!
Configuration properties loaded:
  - Enabled: true
  - Endpoint: http://api.example.com
  - Timeout: 5000 ms
  - Retry Count: 3
Service execution result: Operation completed successfully with endpoint: http://api.example.com
```

### Part 3: Configuration Properties

```
--- Part 3: Configuration Properties ---

Properties bound from application.properties:
  myservice.enabled = true
  myservice.endpoint = http://api.example.com
  myservice.timeout = 5000
  myservice.retry-count = 3

These properties control the auto-configuration behavior.
```

### Part 4: Conditional Beans

```
--- Part 4: Conditional Beans ---

Checking EmailService (@ConditionalOnProperty):
✓ EmailService is registered (feature.email.enabled=true)
  EmailService instance: com.masterclass.autoconfigure.service.EmailService

Checking NotificationService (@ConditionalOnProperty):
✗ NotificationService is NOT registered (feature.notification.enabled=false)

Checking DatabaseService (@ConditionalOnClass):
✓ DatabaseService is registered (H2 on classpath)
```

### Part 5: Bean Overriding

```
--- Part 5: Bean Overriding ---

Checking if default service bean was overridden:
✓ Found defaultService bean
  Type: com.masterclass.autoconfigure.service.DefaultService
  This demonstrates @ConditionalOnMissingBean behavior
```

## Experiments to Try

### 1. Enable/Disable Features

Modify `application.properties`:

```properties
# Disable email service
feature.email.enabled=false

# Enable notification service
feature.notification.enabled=true
```

**Result**: EmailService won't be created, NotificationService will be created.

### 2. Remove H2 Dependency

Comment out H2 dependency in `pom.xml`:

```xml
<!--
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
</dependency>
-->
```

**Result**: DatabaseService won't be created (no H2 class on classpath).

### 3. Override Auto-Configuration

Add your own bean to override the default:

```java
@Configuration
public class CustomConfig {
    
    @Bean
    public MyCustomService myCustomService(MyServiceProperties props) {
        // Your custom implementation
        return new MyCustomService(props) {
            @Override
            public String doSomething() {
                return "Custom implementation!";
            }
        };
    }
}
```

**Result**: Your bean will be used instead of auto-configured one.

### 4. Change Configuration Properties

Modify `application.properties`:

```properties
myservice.endpoint=http://production-api.example.com
myservice.timeout=10000
myservice.retry-count=5
```

**Result**: MyCustomService will use new configuration values.

### 5. Disable Auto-Configuration

In `AutoConfigurationDemoApp.java`:

```java
@SpringBootApplication(exclude = {
    MyServiceAutoConfiguration.class
})
```

**Result**: MyCustomService won't be auto-configured.

## Debug Mode Analysis

When running with `--debug` or `debug=true`, you'll see:

### Positive Matches (Conditions that passed)

```
MyServiceAutoConfiguration matched:
   - @ConditionalOnClass found required class 'MyCustomService'
   - @ConditionalOnProperty (myservice.enabled) matched

ConditionalBeansConfig#emailService matched:
   - @ConditionalOnProperty (feature.email.enabled=true) matched
```

### Negative Matches (Conditions that failed)

```
ConditionalBeansConfig#notificationService:
   Did not match:
      - @ConditionalOnProperty (feature.notification.enabled) did not match;
        found different value in property 'feature.notification.enabled'
```

## Key Concepts Demonstrated

| Concept | Annotation | Example |
|---------|------------|---------|
| **Auto-Configuration** | `@AutoConfiguration` | `MyServiceAutoConfiguration` |
| **Property Binding** | `@ConfigurationProperties` | `MyServiceProperties` |
| **Class Conditional** | `@ConditionalOnClass` | `DatabaseService` (H2 required) |
| **Property Conditional** | `@ConditionalOnProperty` | `EmailService` (enabled flag) |
| **Bean Conditional** | `@ConditionalOnMissingBean` | `DefaultService` (fallback) |
| **Enable Properties** | `@EnableConfigurationProperties` | Enable `MyServiceProperties` |

## Real-World Applications

1. **Feature Toggles**
   - Use `@ConditionalOnProperty` to enable/disable features without code changes
   - Example: Enable email notifications in production but not in development

2. **Multi-Database Support**
   - Use `@ConditionalOnClass` to configure different databases
   - Example: Configure H2 for development, PostgreSQL for production

3. **Default Implementations**
   - Use `@ConditionalOnMissingBean` to provide fallbacks
   - Example: Default cache implementation that can be overridden

4. **Environment-Specific Configuration**
   - Combine conditionals with Spring profiles
   - Example: Different configurations for dev, test, prod

## Common Pitfalls

1. **Circular Dependencies**
   - Be careful when using `@ConditionalOnBean` with beans that depend on each other

2. **Property Naming**
   - Use kebab-case in properties files: `my-service.enabled`
   - Use camelCase in Java: `myService.enabled`

3. **matchIfMissing**
   - `true`: Enable by default (opt-out)
   - `false`: Disable by default (opt-in)

4. **Bean Order**
   - Use `@AutoConfigureAfter` and `@AutoConfigureBefore` for ordering
   - Example: Configure DataSource before JPA

## Interview Questions

**Q: What is the difference between @Configuration and @AutoConfiguration?**

A: `@AutoConfiguration` is specifically for auto-configuration classes that are loaded by Spring Boot's auto-configuration mechanism. It provides ordering capabilities and is typically registered in `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`.

**Q: How does Spring Boot decide which auto-configurations to apply?**

A: Spring Boot evaluates conditional annotations:
- `@ConditionalOnClass`: Check if class exists on classpath
- `@ConditionalOnMissingBean`: Check if bean is already defined
- `@ConditionalOnProperty`: Check property values
- If all conditions pass, the configuration is applied

**Q: What is matchIfMissing in @ConditionalOnProperty?**

A: It determines behavior when the property is not defined:
- `matchIfMissing = true`: Condition passes (feature enabled by default)
- `matchIfMissing = false`: Condition fails (feature disabled by default)

**Q: How can you override an auto-configured bean?**

A: Define your own bean of the same type. Most auto-configurations use `@ConditionalOnMissingBean`, so your bean takes precedence.

---

**Related Demos:**
- [Configuration Management](../../02-configuration-management/) - Learn about properties and profiles
- [Starter Dependencies](../../03-starter-dependencies/) - Create custom starters
