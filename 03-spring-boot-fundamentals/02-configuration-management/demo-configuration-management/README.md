# Demo: Configuration Management

This demo demonstrates Spring Boot's powerful configuration management features including property binding, profiles, and validation.

## What This Demo Shows

1. **Property Binding**
   - `@Value` for simple property injection
   - `@ConfigurationProperties` for type-safe bulk binding

2. **Profile-Specific Configuration**
   - Different configurations for dev, test, prod environments
   - Profile activation and switching

3. **Nested Properties**
   - Complex hierarchical configuration
   - Nested classes for organized properties

4. **Property Validation**
   - JSR-303 validation annotations
   - Startup validation with meaningful error messages

5. **Property Precedence**
   - Understanding configuration priority
   - Overriding properties from different sources

## Project Structure

```
demo-configuration-management/
├── src/main/java/com/masterclass/config/
│   ├── ConfigurationDemoApp.java         # Main application with demonstrations
│   ├── AppProperties.java                # @ConfigurationProperties with validation
│   ├── DatabaseProperties.java           # Nested database configuration
│   └── SimplePropertiesExample.java      # @Value examples
└── src/main/resources/
    ├── application.yml                    # Common configuration
    ├── application-dev.yml                # Development profile
    ├── application-test.yml               # Test profile
    └── application-prod.yml               # Production profile
```

## Running the Demo

### Prerequisites
- Java 17 or higher
- Maven 3.6+

### Default Profile (dev)

```bash
cd demo-configuration-management
mvn clean install
mvn spring-boot:run
```

### With Specific Profile

```bash
# Development profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Test profile
mvn spring-boot:run -Dspring-boot.run.profiles=test

# Production profile
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

### With Property Overrides

```bash
# Override server port
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=9090"

# Override multiple properties
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=9090,--app.debug-enabled=true"
```

### Using JAR

```bash
mvn clean package
java -jar target/demo-configuration-management-1.0.0.jar --spring.profiles.active=prod
```

## Expected Output

### Part 1: Environment Information

```
--- Part 1: Environment Information ---

Active Profiles: dev
Default Profiles: default

Environment Properties:
  java.version: 17.0.5
  os.name: Windows 11
  user.name: yourname
```

### Part 2: Simple @Value Properties

```
--- Part 2: Simple @Value Properties ---

Application Name: Configuration Management Demo
Application Version: 1.0.0
Server Port: 8080
Environment: development
Debug Enabled: true
```

### Part 3: @ConfigurationProperties - AppProperties

```
--- Part 3: @ConfigurationProperties - AppProperties ---

Name: Configuration Management Demo
Version: 1.0.0
Description: Demonstrates Spring Boot configuration features
Environment: development
Debug Enabled: true

Features Enabled:
  - authentication
  - caching
  - monitoring

Connection Settings:
  Max Pool Size: 20
  Min Pool Size: 5
  Timeout: 30000 ms

API Configuration:
  Endpoint: http://localhost:8081/api
  Timeout: 5000 ms
  Retry Count: 3
```

### Part 4: DatabaseProperties

```
--- Part 4: @ConfigurationProperties - DatabaseProperties ---

Database Configuration:
  Host: localhost
  Port: 3306
  Database Name: devdb
  Username: devuser
  Password: *******
  Connection URL: jdbc:mysql://localhost:3306/devdb
```

### Part 5: Property Precedence

```
--- Part 5: Property Precedence ---

Property precedence (highest to lowest):
1. Command line arguments
2. Environment variables
3. Profile-specific files (application-{profile}.yml)
4. Default application.yml

Example: server.port property
  Current value: 8080
  Source: Profile-specific configuration
  Can be overridden with: --server.port=9090
```

## Experiments to Try

### 1. Switch Between Profiles

```bash
# Development (port 8080, debug enabled, local database)
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Test (port 8081, limited debug, test database)
mvn spring-boot:run -Dspring-boot.run.profiles=test

# Production (port 80, minimal logging, requires env vars)
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

Notice how different values are loaded from each profile file.

### 2. Override with Command Line

```bash
# Override port
mvn spring-boot:run -Dspring-boot.run.profiles=dev -Dspring-boot.run.arguments="--server.port=9090"

# Override API endpoint
mvn spring-boot:run -Dspring-boot.run.arguments="--app.api.endpoint=http://custom-api.com"
```

Command-line arguments have highest precedence!

### 3. Use Environment Variables

```bash
# Windows PowerShell
$env:DB_HOST="remote-server"
$env:DB_USERNAME="admin"
$env:DB_PASSWORD="secret123"
mvn spring-boot:run -Dspring-boot.run.profiles=prod

# Linux/Mac
export DB_HOST=remote-server
export DB_USERNAME=admin
export DB_PASSWORD=secret123
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

Environment variables override profile files!

### 4. Test Property Validation

Modify `application-dev.yml` with invalid values:

```yaml
app:
  version: invalid-version  # Must match x.y.z pattern
  api:
    timeout: 100  # Must be >= 1000ms
```

**Result**: Application fails to start with validation error:

```
***************************
APPLICATION FAILED TO START
***************************

Description:

Binding to target org.springframework.boot.context.properties.bind.BindException:
Failed to bind properties under 'app' to com.masterclass.config.AppProperties:

    Property: app.version
    Value: invalid-version
    Reason: Version must follow semantic versioning (x.y.z)

    Property: app.api.timeout
    Value: 100
    Reason: must be greater than or equal to 1000
```

### 5. Multiple Profiles

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev,debug
```

Create `application-debug.yml`:

```yaml
logging:
  level:
    root: TRACE
    com.masterclass: TRACE
```

Both `application-dev.yml` and `application-debug.yml` are loaded!

### 6. External Configuration

Create external config file:

```bash
# Create external-config.yml
mkdir config
echo "server.port: 7777" > config/application.yml

# Run - automatically picks up config/application.yml
java -jar target/demo-configuration-management-1.0.0.jar
```

External files override internal ones!

## Configuration Precedence Demo

Try this sequence to understand precedence:

```bash
# 1. Default (from application.yml)
mvn spring-boot:run
# server.port = (no default in common config)

# 2. Profile-specific (from application-dev.yml)
mvn spring-boot:run -Dspring-boot.run.profiles=dev
# server.port = 8080

# 3. Environment variable
$env:SERVER_PORT="8888"
mvn spring-boot:run -Dspring-boot.run.profiles=dev
# server.port = 8888 (env var overrides profile)

# 4. Command-line (highest priority)
mvn spring-boot:run -Dspring-boot.run.profiles=dev -Dspring-boot.run.arguments="--server.port=9999"
# server.port = 9999 (command-line overrides everything)
```

## Key Concepts Demonstrated

| Concept | Demonstration | File/Class |
|---------|---------------|------------|
| **@Value** | Simple property injection | `SimplePropertiesExample.java` |
| **@ConfigurationProperties** | Type-safe bulk binding | `AppProperties.java` |
| **Nested Properties** | Hierarchical configuration | `AppProperties.Connection`, `Api` |
| **Validation** | JSR-303 constraints | `@NotBlank`, `@Min`, `@Max` |
| **Profiles** | Environment-specific config | `application-{profile}.yml` |
| **Relaxed Binding** | Multiple naming styles | `debug-enabled` → `debugEnabled` |
| **Property Precedence** | Override hierarchy | Command line > Env > Profile |

## Common Pitfalls Demonstrated

### 1. Missing Required Properties

If you run with prod profile without environment variables:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

**Error**: Missing required properties (DB_USERNAME, DB_PASSWORD)

### 2. Invalid Property Values

Modify `application-dev.yml`:

```yaml
app:
  api:
    retry-count: 20  # Max is 10
```

**Error**: Validation fails - "must be less than or equal to 10"

### 3. Type Mismatch

```yaml
server:
  port: "not-a-number"
```

**Error**: Failed to convert String to int

## Real-World Applications

1. **Multi-Environment Deployment**
   - Same JAR for dev/test/prod
   - Different configurations per environment
   - Secrets from environment variables

2. **Feature Toggles**
   - Enable/disable features via config
   - No code deployment needed
   - A/B testing support

3. **Database Configuration**
   - Development: H2 in-memory
   - Testing: Testcontainers
   - Production: External MySQL/PostgreSQL

4. **API Integration**
   - Different endpoints per environment
   - Timeout and retry configuration
   - Circuit breaker settings

## Interview Questions & Answers

**Q: What's the difference between @Value and @ConfigurationProperties?**

A: See demonstration in Parts 2 and 3:
- `@Value`: Simple, scattered properties. Used in `SimplePropertiesExample`
- `@ConfigurationProperties`: Type-safe, grouped, validated. Used in `AppProperties`

**Q: How do you activate a Spring profile?**

A: Multiple ways demonstrated:
```bash
# application.yml
spring.profiles.active=dev

# Command line
--spring.profiles.active=prod

# Environment variable
SPRING_PROFILES_ACTIVE=prod
```

**Q: What is property precedence in Spring Boot?**

A: See Part 5 output - from highest to lowest:
1. Command-line arguments
2. Environment variables
3. Profile-specific files
4. Default application files

**Q: How do you validate configuration properties?**

A: Use `@Validated` and JSR-303 annotations (see `AppProperties.java`):
```java
@ConfigurationProperties(prefix = "app")
@Validated
public class AppProperties {
    @NotBlank
    @Pattern(regexp = "\\d+\\.\\d+\\.\\d+")
    private String version;
}
```

**Q: How would you externalize sensitive data like passwords?**

A: See `application-prod.yml`:
```yaml
database:
  username: ${DB_USERNAME}
  password: ${DB_PASSWORD}
```

Set via environment variables - never commit to source control!

---

**Related Demos:**
- [Auto-Configuration](../../01-auto-configuration/) - Understanding conditional configuration
- [Starter Dependencies](../../03-starter-dependencies/) - Creating custom starters with config
