# Spring Cloud Config Client Demo

This demo demonstrates a **Spring Cloud Config Client** that fetches configuration from Config Server.

## Overview

The Config Client connects to **Config Server** on startup, fetches configuration based on application name and profile, and can refresh configuration dynamically without restart.

## Architecture

```
┌─────────────────────────────────┐
│   Config Server (8888)           │
│                                  │
│  Configuration Source:           │
│  - product-service.yml           │
│  - product-service-dev.yml       │
│  - application.yml               │
└─────────────────────────────────┘
            ↓
     Fetches config
            ↓
┌─────────────────────────────────┐
│   Config Client (8081)           │
│   (product-service)              │
│                                  │
│  REST Endpoints:                 │
│   GET /api/config                │
│   POST /actuator/refresh         │
└─────────────────────────────────┘
```

## Features

### 1. Fetch Configuration on Startup
- Connects to Config Server
- Retrieves environment-specific config
- Falls back to local config if server unavailable

### 2. Dynamic Refresh
- Refresh configuration without restart
- Use `@RefreshScope` annotation
- Call `/actuator/refresh` endpoint

### 3. Type-Safe Configuration
- `@ConfigurationProperties` for structured config
- `@Value` for simple property injection
- Validation and documentation

### 4. Multiple Configuration Sources
- Config Server (primary)
- Local application.yml (fallback)
- Environment variables
- Command line arguments

## Prerequisites

1. **Config Server running** on port 8888
2. **Configuration repository** set up (see Config Server README)
3. **Java 17+**
4. **Maven 3.6+**

## Setup Instructions

### Step 1: Ensure Config Server is Running

Start Config Server first:

```bash
cd ../demo-config-server
mvn spring-boot:run
```

Verify Config Server is accessible:
```bash
curl -u configuser:configpass http://localhost:8888/product-service/dev
```

### Step 2: Build Config Client

```bash
# Navigate to project directory
cd demo-config-client

# Build the project
mvn clean package

# Run Config Client
java -jar target/demo-config-client-1.0.0.jar

# Or use Maven
mvn spring-boot:run
```

**Config Client will start on port 8081**

### Step 3: Verify Configuration

**View all configuration:**
```bash
curl http://localhost:8081/api/config
```

**View message:**
```bash
curl http://localhost:8081/api/config/message
```

**View features:**
```bash
curl http://localhost:8081/api/config/features
```

## Configuration Sources

### Resolution Order (Highest to Lowest Priority)

```
1. Command Line Arguments
   java -jar app.jar --app.timeout=10000

2. System Environment Variables
   export APP_TIMEOUT=10000

3. Config Server - Profile-Specific
   product-service-dev.yml

4. Config Server - Application-Specific
   product-service.yml

5. Config Server - Shared
   application.yml

6. Bundled Configuration
   src/main/resources/application.yml

7. Default Values in @Value
   @Value("${app.timeout:5000}")
```

### Example

Given these configuration files:

**application.yml (Config Server):**
```yaml
logging:
  level:
    root: INFO
```

**product-service.yml (Config Server):**
```yaml
app:
  timeout: 5000
  message: "Product Service"
```

**product-service-dev.yml (Config Server):**
```yaml
app:
  message: "Product Service - Development"
  feature-x-enabled: true
```

**Resolved Configuration in dev profile:**
```yaml
logging:
  level:
    root: INFO                                    # from application.yml
app:
  timeout: 5000                                   # from product-service.yml
  message: "Product Service - Development"        # from product-service-dev.yml (overrides)
  feature-x-enabled: true                         # from product-service-dev.yml
```

## Dynamic Refresh

### Scenario: Update Timeout Without Restart

**Step 1: View current configuration**
```bash
curl http://localhost:8081/api/config/timeout
```

Output:
```json
{
  "timeout": 5000,
  "timeoutFromConfig": 5000,
  "unit": "milliseconds"
}
```

**Step 2: Update configuration in Git**
```bash
cd ~/config-repo
cat > product-service-dev.yml << 'EOF'
app:
  message: "Product Service - Development"
  timeout: 10000
  max-retries: 5
  feature-x-enabled: true
EOF

git add product-service-dev.yml
git commit -m "Increase timeout to 10s"
```

**Step 3: Refresh configuration**
```bash
curl -X POST http://localhost:8081/actuator/refresh
```

Output:
```json
[
  "app.timeout",
  "app.max-retries"
]
```

**Step 4: Verify updated configuration**
```bash
curl http://localhost:8081/api/config/timeout
```

Output:
```json
{
  "timeout": 10000,
  "timeoutFromConfig": 10000,
  "unit": "milliseconds"
}
```

**Configuration updated without restart!**

## Using @RefreshScope

### @RefreshScope on Bean

Beans annotated with `@RefreshScope` are recreated when configuration is refreshed:

```java
@Component
@RefreshScope  // Bean will be refreshed
public class ProductService {
    
    @Value("${app.timeout}")
    private int timeout;  // Updated on refresh
    
    public void processProduct() {
        // Uses latest timeout value
    }
}
```

### @RefreshScope on Controller

```java
@RestController
@RefreshScope  // Controller will be refreshed
public class ProductController {
    
    @Value("${app.message}")
    private String message;  // Updated on refresh
    
    @GetMapping("/message")
    public String getMessage() {
        return message;  // Returns latest value
    }
}
```

### @ConfigurationProperties (Automatic Refresh)

`@ConfigurationProperties` beans are automatically refreshed without `@RefreshScope`:

```java
@Component
@ConfigurationProperties(prefix = "app")
public class AppConfig {
    private String message;
    private int timeout;
    
    // Automatically refreshed when POST /actuator/refresh
    // No need for @RefreshScope
}
```

## API Endpoints

### Configuration Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/config` | GET | Get all configuration values |
| `/api/config/message` | GET | Get application message |
| `/api/config/timeout` | GET | Get timeout configuration |
| `/api/config/features` | GET | Get feature flags |
| `/api/config/health` | GET | Health check |

### Actuator Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/actuator/health` | GET | Application health |
| `/actuator/env` | GET | Environment properties |
| `/actuator/configprops` | GET | Configuration properties |
| `/actuator/refresh` | POST | Refresh configuration |

## Testing Scenarios

### Scenario 1: View Configuration

```bash
# View all configuration
curl http://localhost:8081/api/config | jq

# View specific property
curl http://localhost:8081/api/config/message | jq
```

### Scenario 2: Change Environment

**Start with production profile:**
```bash
java -jar target/demo-config-client-1.0.0.jar --spring.profiles.active=prod
```

Configuration will be loaded from:
- `application.yml`
- `product-service.yml`
- `product-service-prod.yml`

### Scenario 3: Dynamic Refresh

1. **Update config in Git**
2. **Refresh without restart:**
```bash
curl -X POST http://localhost:8081/actuator/refresh
```
3. **Verify changes:**
```bash
curl http://localhost:8081/api/config
```

### Scenario 4: Config Server Unavailable

**With fail-fast: true (default):**
```bash
# Stop Config Server
# Try to start Config Client
java -jar target/demo-config-client-1.0.0.jar
# ❌ Startup fails
```

**With fail-fast: false:**
```yaml
spring:
  cloud:
    config:
      fail-fast: false
```

```bash
# Stop Config Server
# Start Config Client
java -jar target/demo-config-client-1.0.0.jar
# ✅ Startup succeeds with bundled application.yml
```

## Configuration Properties

### AppConfig Class

```java
@ConfigurationProperties(prefix = "app")
public class AppConfig {
    private String message;      // app.message
    private int timeout;         // app.timeout
    private int maxRetries;      // app.max-retries
    private Map<String, Object> features;  // app.features.*
}
```

**Configuration Example:**
```yaml
app:
  message: "Product Service"
  timeout: 5000
  max-retries: 3
  features:
    feature-x-enabled: true
    feature-y-enabled: false
    beta-features: true
```

**Access in Code:**
```java
@Autowired
private AppConfig appConfig;

public void doSomething() {
    String msg = appConfig.getMessage();
    int timeout = appConfig.getTimeout();
    boolean featureX = (Boolean) appConfig.getFeatures().get("feature-x-enabled");
}
```

## Troubleshooting

### Issue: Config Client can't connect to Config Server

**Error:**
```
Could not locate PropertySource: I/O error on GET request for "http://localhost:8888/product-service/dev"
```

**Solutions:**

1. **Verify Config Server is running:**
```bash
curl -u configuser:configpass http://localhost:8888/actuator/health
```

2. **Check Config Server URL in application.yml:**
```yaml
spring:
  cloud:
    config:
      uri: http://localhost:8888  # Correct URL?
      username: configuser        # Correct credentials?
      password: configpass
```

3. **Use fail-fast: false for development:**
```yaml
spring:
  cloud:
    config:
      fail-fast: false
```

### Issue: Configuration not refreshing

**Problem:** Called `/actuator/refresh` but configuration not updated

**Solutions:**

1. **Verify @RefreshScope annotation:**
```java
@Component
@RefreshScope  // Must have this annotation
public class MyService {
    @Value("${app.timeout}")
    private int timeout;
}
```

2. **Check refresh endpoint is enabled:**
```yaml
management:
  endpoints:
    web:
      exposure:
        include: refresh
```

3. **Verify changes in Config Server:**
```bash
curl -u configuser:configpass http://localhost:8888/product-service/dev | jq '.propertySources[0].source."app.timeout"'
```

### Issue: Using wrong profile

**Problem:** Loading wrong configuration file

**Solution:** Check active profile:
```bash
curl http://localhost:8081/actuator/env | jq '.activeProfiles'
```

Set profile explicitly:
```bash
java -jar app.jar --spring.profiles.active=dev
```

## Production Considerations

### 1. Secure Communication

```yaml
spring:
  cloud:
    config:
      uri: https://config-server.example.com  # HTTPS
      username: ${CONFIG_USER}
      password: ${CONFIG_PASSWORD}
```

### 2. Retry Configuration

```yaml
spring:
  cloud:
    config:
      fail-fast: true  # Fail if Config Server unavailable
      retry:
        initial-interval: 1000
        max-attempts: 6
        max-interval: 2000
        multiplier: 1.1
```

### 3. Service Discovery Integration

```yaml
spring:
  cloud:
    config:
      discovery:
        enabled: true
        service-id: config-server

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

### 4. Monitor Configuration Changes

```java
@Component
public class ConfigMonitor implements ApplicationListener<EnvironmentChangeEvent> {
    
    @Override
    public void onApplicationEvent(EnvironmentChangeEvent event) {
        // Log to monitoring system
        metrics.counter("config.refresh").increment();
        
        // Alert if sensitive properties changed
        if (event.getKeys().contains("db.password")) {
            alertingService.alert("Database password changed!");
        }
    }
}
```

## Next Steps

1. **Integrate with Eureka** for Config Server discovery
2. **Add Spring Cloud Bus** for broadcast refresh
3. **Set up Git webhooks** for automatic refresh
4. **Implement encryption** for sensitive values
5. **Add monitoring** for configuration changes

## References

- [Spring Cloud Config Client Documentation](https://cloud.spring.io/spring-cloud-config/reference/html/#_spring_cloud_config_client)
- [RefreshScope Documentation](https://cloud.spring.io/spring-cloud-commons/reference/html/#refresh-scope)
- [ConfigurationProperties Guide](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config.typesafe-configuration-properties)
