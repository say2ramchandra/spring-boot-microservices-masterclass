# Section 03: Centralized Configuration with Spring Cloud Config

## Table of Contents
1. [Introduction](#introduction)
2. [The Configuration Problem](#the-configuration-problem)
3. [Spring Cloud Config Architecture](#spring-cloud-config-architecture)
4. [Setting Up Config Server](#setting-up-config-server)
5. [Config Client Setup](#config-client-setup)
6. [Configuration Strategies](#configuration-strategies)
7. [Refreshing Configuration](#refreshing-configuration)
8. [Encryption and Security](#encryption-and-security)
9. [Best Practices](#best-practices)
10. [Interview Questions](#interview-questions)

---

## Introduction

In a microservices architecture, managing configuration across dozens or hundreds of services becomes a nightmare. Spring Cloud Config provides a centralized external configuration management backed by Git.

### Why Centralized Configuration?

**Traditional Approach (Bad):**
```
Each service has its own application.properties
- Product Service (application.properties)
- Order Service (application.properties)
- Payment Service (application.properties)
- User Service (application.properties)

Problems:
❌ Configuration scattered across services
❌ Need to rebuild/redeploy for config changes
❌ No version control for configs
❌ Environment-specific configs mixed in code
❌ Security nightmare (passwords in code)
```

**Centralized Approach (Good):**
```
All configurations in one Git repository
- Versioned (Git history)
- Centralized (One place to manage)
- Dynamic (Refresh without restart)
- Secure (Encrypted values)
- Environment-specific (dev/test/prod)
```

---

## The Configuration Problem

### Problem 1: Scattered Configuration

```
product-service/
  src/main/resources/
    application.properties  ← DB config, API keys, timeouts

order-service/
  src/main/resources/
    application.properties  ← DB config, API keys, timeouts

payment-service/
  src/main/resources/
    application.properties  ← DB config, API keys, timeouts
```

**Issues:**
- Change database password → Update 50+ services
- Add new environment → Copy-paste configs
- No audit trail of who changed what

### Problem 2: Environment Management

```java
// ❌ Bad: Hard-coded environment logic
if (env.equals("prod")) {
    dbUrl = "prod-db.example.com";
} else if (env.equals("test")) {
    dbUrl = "test-db.example.com";
} else {
    dbUrl = "localhost";
}
```

### Problem 3: Secrets Management

```properties
# ❌ Bad: Passwords in source code
db.password=SuperSecretPassword123
api.key=sk_live_1234567890abcdef
```

### Problem 4: Dynamic Updates

```
Want to change timeout from 5s to 10s
→ Update application.properties
→ Rebuild application
→ Redeploy
→ Restart pod
→ 5 minutes of downtime
```

---

## Spring Cloud Config Architecture

### Components

```
┌─────────────────────────────────────────────────────────┐
│                     Git Repository                       │
│                  (Configuration Store)                   │
│                                                          │
│  config-repo/                                           │
│    ├── product-service.yml                              │
│    ├── product-service-dev.yml                          │
│    ├── product-service-prod.yml                         │
│    ├── order-service.yml                                │
│    └── application.yml (shared)                         │
└─────────────────────────────────────────────────────────┘
                           ↓
                      Reads from
                           ↓
┌─────────────────────────────────────────────────────────┐
│               Spring Cloud Config Server                 │
│                  (Port 8888)                            │
│                                                          │
│  REST API:                                              │
│    GET /{application}/{profile}/{label}                │
│    Example: /product-service/dev/master                │
└─────────────────────────────────────────────────────────┘
                           ↓
                    Fetches config
                           ↓
┌─────────────────────────────────────────────────────────┐
│              Microservices (Config Clients)             │
│                                                          │
│  Product Service  Order Service  Payment Service        │
│     (8081)          (8082)          (8083)              │
│                                                          │
│  On Startup:                                            │
│  1. Call Config Server                                  │
│  2. Fetch configuration                                 │
│  3. Start with fetched config                           │
└─────────────────────────────────────────────────────────┘
```

### Configuration Resolution Order

```
Spring resolves properties in this order (higher priority first):

1. Command line arguments
   java -jar app.jar --server.port=9090

2. System environment variables
   export SERVER_PORT=9090

3. Config Server (from Git)
   {application}-{profile}.yml

4. Bundled application.properties
   src/main/resources/application.properties

5. Default values in @Value annotations
   @Value("${timeout:5000}")
```

---

## Setting Up Config Server

### Step 1: Create Config Server Application

**pom.xml:**
```xml
<dependencies>
    <!-- Config Server -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-config-server</artifactId>
    </dependency>
    
    <!-- For Git support -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
</dependencies>

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>2023.0.0</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### Step 2: Enable Config Server

```java
package com.example.configserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer  // This enables Config Server
public class ConfigServerApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}
```

### Step 3: Configure Config Server

**application.yml:**
```yaml
server:
  port: 8888

spring:
  application:
    name: config-server
  
  cloud:
    config:
      server:
        git:
          # Git repository containing configurations
          uri: https://github.com/your-org/config-repo
          
          # Clone repository on startup
          clone-on-start: true
          
          # Default branch
          default-label: main
          
          # Search paths (subdirectories)
          search-paths:
            - services
            - shared
          
          # Authentication (if private repo)
          username: ${GIT_USERNAME}
          password: ${GIT_PASSWORD}
          
          # Timeout
          timeout: 5

# Optional: Eureka registration
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

### Step 4: Create Git Configuration Repository

**Git Repository Structure:**
```
config-repo/
├── application.yml                    # Shared across all services
├── product-service.yml                # Product service (all envs)
├── product-service-dev.yml            # Product service (dev only)
├── product-service-prod.yml           # Product service (prod only)
├── order-service.yml
├── order-service-dev.yml
├── order-service-prod.yml
└── README.md
```

**application.yml (Shared):**
```yaml
# Common configuration for all services
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics

logging:
  level:
    root: INFO

spring:
  jackson:
    serialization:
      indent-output: true
```

**product-service.yml (All Environments):**
```yaml
server:
  port: 8081

spring:
  application:
    name: product-service

# Common config
app:
  message: "Product Service - Common Config"
  timeout: 5000
```

**product-service-dev.yml (Development):**
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/product_dev
    username: dev_user
    password: dev_pass
  
  jpa:
    show-sql: true
    hibernate:
      ddl-auto: update

logging:
  level:
    com.example: DEBUG

app:
  message: "Product Service - Development Environment"
```

**product-service-prod.yml (Production):**
```yaml
spring:
  datasource:
    url: jdbc:mysql://prod-db.example.com:3306/product_prod
    username: prod_user
    password: '{cipher}AQB3jJ7...'  # Encrypted
  
  jpa:
    show-sql: false
    hibernate:
      ddl-auto: validate

logging:
  level:
    com.example: INFO

app:
  message: "Product Service - Production Environment"
```

### Step 5: Test Config Server

**Endpoints:**
```
# Get configuration for product-service in dev profile
GET http://localhost:8888/product-service/dev

# Response:
{
  "name": "product-service",
  "profiles": ["dev"],
  "label": "main",
  "version": "abc123",
  "propertySources": [
    {
      "name": "https://github.com/.../product-service-dev.yml",
      "source": {
        "spring.datasource.url": "jdbc:mysql://localhost:3306/product_dev",
        "app.message": "Product Service - Development Environment"
      }
    },
    {
      "name": "https://github.com/.../product-service.yml",
      "source": {
        "server.port": 8081,
        "app.timeout": 5000
      }
    },
    {
      "name": "https://github.com/.../application.yml",
      "source": {
        "logging.level.root": "INFO"
      }
    }
  ]
}
```

**URL Pattern:**
```
/{application}/{profile}[/{label}]
/{application}-{profile}.yml
/{label}/{application}-{profile}.yml
/{application}-{profile}.properties
/{label}/{application}-{profile}.properties

Examples:
/product-service/dev
/product-service/dev/main
/order-service/prod
```

---

## Config Client Setup

### Step 1: Add Config Client Dependency

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
</dependency>

<!-- For refresh capability -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

### Step 2: Configure Config Client

**Important:** Config client properties must be in `application.yml` (or `bootstrap.yml` for older versions), not `application.properties`.

**application.yml:**
```yaml
spring:
  application:
    name: product-service  # Must match config file name
  
  profiles:
    active: dev  # Environment: dev/test/prod
  
  config:
    import: optional:configserver:http://localhost:8888
  
  cloud:
    config:
      # Config Server URL
      uri: http://localhost:8888
      
      # Fail fast if Config Server unavailable
      fail-fast: true
      
      # Retry configuration
      retry:
        initial-interval: 1000
        max-attempts: 6
        max-interval: 2000
        multiplier: 1.1

# Enable refresh endpoint
management:
  endpoints:
    web:
      exposure:
        include: refresh
```

### Step 3: Use Configuration Properties

**Method 1: @Value Annotation**

```java
@RestController
@RequestMapping("/api/config")
public class ConfigController {
    
    @Value("${app.message}")
    private String message;
    
    @Value("${app.timeout}")
    private int timeout;
    
    @GetMapping
    public Map<String, Object> getConfig() {
        return Map.of(
            "message", message,
            "timeout", timeout
        );
    }
}
```

**Method 2: @ConfigurationProperties**

```java
@Configuration
@ConfigurationProperties(prefix = "app")
public class AppConfig {
    
    private String message;
    private int timeout;
    private Map<String, String> features;
    
    // Getters and setters
}

@RestController
@RequestMapping("/api/config")
public class ConfigController {
    
    @Autowired
    private AppConfig appConfig;
    
    @GetMapping
    public AppConfig getConfig() {
        return appConfig;
    }
}
```

---

## Configuration Strategies

### Strategy 1: Single File Per Service

```
config-repo/
├── product-service.yml
├── order-service.yml
└── payment-service.yml
```

**Pros:** Simple
**Cons:** No environment separation

### Strategy 2: Profile-Specific Files

```
config-repo/
├── product-service-dev.yml
├── product-service-test.yml
├── product-service-prod.yml
├── order-service-dev.yml
├── order-service-test.yml
└── order-service-prod.yml
```

**Pros:** Clear separation
**Cons:** Duplication

### Strategy 3: Hierarchical (Best)

```
config-repo/
├── application.yml                  # Shared
├── product-service.yml              # Service defaults
├── product-service-dev.yml          # Dev overrides
├── product-service-prod.yml         # Prod overrides
└── shared/
    ├── database-dev.yml
    └── database-prod.yml
```

**Pros:** DRY, flexible
**Cons:** More complex

### Strategy 4: Multi-Folder Structure

```
config-repo/
├── services/
│   ├── product-service/
│   │   ├── application.yml
│   │   ├── application-dev.yml
│   │   └── application-prod.yml
│   └── order-service/
│       ├── application.yml
│       ├── application-dev.yml
│       └── application-prod.yml
└── shared/
    ├── logging.yml
    ├── monitoring.yml
    └── security.yml
```

**Config Server setup:**
```yaml
spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/your-org/config-repo
          search-paths:
            - services/{application}
            - shared
```

---

## Refreshing Configuration

### Problem: Configuration Changes Without Restart

```
Change timeout from 5s to 10s
❌ Traditional: Restart service (downtime)
✅ Config Server: Refresh without restart
```

### Solution 1: @RefreshScope

```java
@RestController
@RequestMapping("/api/products")
@RefreshScope  // Enable refresh
public class ProductController {
    
    @Value("${app.timeout}")
    private int timeout;  // Will be updated on refresh
    
    @Value("${app.message}")
    private String message;
    
    @GetMapping("/config")
    public Map<String, Object> getConfig() {
        return Map.of(
            "timeout", timeout,
            "message", message
        );
    }
}
```

**Steps to Refresh:**

1. Update configuration in Git:
```bash
cd config-repo
echo "app.timeout: 10000" > product-service-dev.yml
git add .
git commit -m "Increase timeout to 10s"
git push
```

2. Call refresh endpoint:
```bash
curl -X POST http://localhost:8081/actuator/refresh
```

3. Configuration updated without restart!

### Solution 2: Spring Cloud Bus (Broadcast Refresh)

**Problem with @RefreshScope:**
```
Have 10 instances of product-service
→ Need to call /refresh on each instance
→ 10 HTTP calls
```

**Solution: Spring Cloud Bus**

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-bus-amqp</artifactId>
</dependency>
```

```yaml
spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest

management:
  endpoints:
    web:
      exposure:
        include: busrefresh
```

**Architecture:**
```
┌─────────────┐
│ Git Webhook │  Config changed
└──────┬──────┘
       ↓
┌──────────────────┐
│  Config Server   │
│   POST /monitor  │
└──────┬───────────┘
       ↓ Broadcast via RabbitMQ
    ┌──┴──┬──────┬──────┐
    ↓     ↓      ↓      ↓
  [Svc1][Svc2][Svc3][Svc4]
    All instances refreshed!
```

**Refresh all instances:**
```bash
curl -X POST http://localhost:8888/actuator/busrefresh
```

### Solution 3: Webhook Automation

**GitHub Webhook:**
```
Repository Settings → Webhooks → Add webhook
Payload URL: http://your-server:8888/monitor
Content type: application/json
Events: Just the push event
```

**Config Server:**
```yaml
spring:
  cloud:
    config:
      server:
        monitor:
          github:
            enabled: true
```

**Flow:**
```
Git push → GitHub webhook → Config Server /monitor
       → Broadcast refresh via Bus
       → All services updated!
```

---

## Encryption and Security

### Problem: Secrets in Git

```yaml
# ❌ Bad: Plain text password in Git
spring:
  datasource:
    password: SuperSecretPassword123
```

### Solution 1: Symmetric Encryption

**Step 1: Generate Key**

```bash
keytool -genkeypair -alias config-server-key \
        -keyalg RSA -keysize 2048 -keystore config-server.jks \
        -storepass changeme
```

**Step 2: Configure Config Server**

```yaml
encrypt:
  key-store:
    location: classpath:config-server.jks
    password: changeme
    alias: config-server-key
```

**Step 3: Encrypt Values**

```bash
curl http://localhost:8888/encrypt -d "SuperSecretPassword123"

# Returns:
AQB3jJ7L8+9K3mN2pQ5rS6tU7vW8xY9zA0bC1dE2fF3gH4iJ5k
```

**Step 4: Use Encrypted Values**

```yaml
spring:
  datasource:
    password: '{cipher}AQB3jJ7L8+9K3mN2pQ5rS6tU7vW8xY9zA0bC1dE2fF3gH4iJ5k'
```

**Decryption happens automatically** when client fetches config!

### Solution 2: Asymmetric Encryption (RSA)

```yaml
encrypt:
  key-store:
    location: classpath:server.jks
    password: ${KEYSTORE_PASSWORD}
    alias: config-server-key
    secret: ${KEY_PASSWORD}
```

**More secure:** Even if someone gets encrypted value, they can't decrypt without private key.

### Solution 3: External Secret Management

**Integrate with Vault:**

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-vault-config</artifactId>
</dependency>
```

```yaml
spring:
  cloud:
    vault:
      uri: http://localhost:8200
      token: ${VAULT_TOKEN}
      kv:
        enabled: true
        backend: secret
```

---

## Best Practices

### 1. Use Hierarchical Configuration

```
application.yml           ← Shared by all services
product-service.yml       ← Product service (all envs)
product-service-dev.yml   ← Product service (dev overrides)
product-service-prod.yml  ← Product service (prod overrides)
```

### 2. Encrypt Sensitive Data

```yaml
# ✅ Good
db.password: '{cipher}AQB3jJ7L8+9K3mN2pQ5rS6tU7vW8xY9zA0bC1dE2fF3gH4iJ5k'

# ❌ Bad
db.password: SuperSecret123
```

### 3. Use Profiles Wisely

```java
@Profile("dev")
@Configuration
public class DevConfig {
    // Dev-specific beans
}

@Profile("prod")
@Configuration
public class ProdConfig {
    // Prod-specific beans
}
```

### 4. Version Your Configurations

```bash
git log product-service-prod.yml

commit abc123 - Increased timeout to 10s
commit def456 - Updated database URL
commit ghi789 - Added new API key
```

**Rollback easily:**
```yaml
spring:
  cloud:
    config:
      label: def456  # Use specific commit
```

### 5. Monitor Config Changes

```java
@Component
public class ConfigChangeListener implements ApplicationListener<EnvironmentChangeEvent> {
    
    private static final Logger log = LoggerFactory.getLogger(ConfigChangeListener.class);
    
    @Override
    public void onApplicationEvent(EnvironmentChangeEvent event) {
        log.info("Configuration changed: {}", event.getKeys());
    }
}
```

### 6. Fail Fast

```yaml
spring:
  cloud:
    config:
      fail-fast: true  # Fail startup if Config Server unavailable
```

### 7. Cache Configuration Locally

```yaml
spring:
  cloud:
    config:
      fail-fast: false
      # Fallback to local file if Config Server down
```

---

## Interview Questions

### Q1: What is Spring Cloud Config and why do we need it?

**Answer:**

**Spring Cloud Config** provides server-side and client-side support for externalized configuration in a distributed system.

**Why We Need It:**

**Problems It Solves:**
1. **Scattered Configuration**: Each service has its own config files
2. **No Version Control**: No history of configuration changes
3. **Environment Management**: Hard to manage dev/test/prod configs
4. **Security**: Passwords stored in source code
5. **Dynamic Updates**: Need to rebuild/redeploy for config changes

**Benefits:**
```
✅ Centralized: All configs in one place (Git)
✅ Versioned: Git provides history and rollback
✅ Environment-Specific: Separate configs for dev/test/prod
✅ Secure: Encrypt sensitive values
✅ Dynamic: Refresh without restart
✅ Auditable: Track who changed what
```

**Architecture:**
```
Git Repo → Config Server → Microservices
(Store)    (Serve)         (Consume)
```

---

### Q2: How does Config Server resolve configuration priority?

**Answer:**

**Resolution Order (Highest to Lowest Priority):**

```
1. Command Line Arguments
   java -jar app.jar --server.port=9090

2. System Environment Variables
   SERVER_PORT=9090 java -jar app.jar

3. Profile-Specific Config from Config Server
   product-service-prod.yml

4. Application-Specific Config from Config Server
   product-service.yml

5. Shared Config from Config Server
   application.yml

6. Bundled application.yml/properties
   src/main/resources/application.yml

7. Default values in @Value
   @Value("${timeout:5000}")
```

**Example:**

**application.yml (Config Server):**
```yaml
app:
  timeout: 3000
  message: "Default Message"
```

**product-service.yml (Config Server):**
```yaml
app:
  timeout: 5000
```

**product-service-prod.yml (Config Server):**
```yaml
app:
  timeout: 10000
```

**Result in prod profile:**
```
app.timeout = 10000  (from product-service-prod.yml)
app.message = "Default Message"  (from application.yml)
```

---

### Q3: How do you refresh configuration without restarting the service?

**Answer:**

**Method 1: Using @RefreshScope**

```java
@RestController
@RefreshScope  // Enable refresh
public class ProductController {
    
    @Value("${app.timeout}")
    private int timeout;  // Will be updated
    
    @GetMapping("/config")
    public int getTimeout() {
        return timeout;
    }
}
```

**Steps:**
1. Update config in Git and push
2. Call refresh endpoint:
```bash
curl -X POST http://localhost:8081/actuator/refresh
```
3. Configuration updated!

**Method 2: Using @ConfigurationProperties**

```java
@Configuration
@ConfigurationProperties(prefix = "app")
public class AppConfig {
    private int timeout;
    // getter/setter
}
```

`@ConfigurationProperties` beans are refreshed automatically without `@RefreshScope`.

**Method 3: Spring Cloud Bus (All Instances)**

```
POST /actuator/busrefresh
  ↓
RabbitMQ broadcasts to all instances
  ↓
All services refresh simultaneously
```

**What Gets Refreshed:**
- ✅ @RefreshScope beans
- ✅ @ConfigurationProperties
- ✅ Logging levels
- ❌ @Value in non-@RefreshScope beans
- ❌ DataSource configurations (need restart)

---

### Q4: How do you encrypt sensitive configuration values?

**Answer:**

**Step 1: Enable Encryption in Config Server**

```yaml
encrypt:
  key: mySecretKey  # Symmetric encryption
```

Or use keystore:
```yaml
encrypt:
  key-store:
    location: classpath:server.jks
    password: changeme
    alias: config-server-key
```

**Step 2: Encrypt Value**

```bash
curl http://localhost:8888/encrypt -d "MyPassword123"

# Returns encrypted value:
AQB3jJ7L8+9K3mN2pQ5rS6tU7vW8xY9zA0bC1dE2fF3gH4iJ5k
```

**Step 3: Use in Configuration**

```yaml
spring:
  datasource:
    password: '{cipher}AQB3jJ7L8+9K3mN2pQ5rS6tU7vW8xY9zA0bC1dE2fF3gH4iJ5k'
```

**Step 4: Decryption**

Config Server automatically decrypts when serving configuration.

**Security Note:**
```
✅ Git stores encrypted value
✅ Config Server decrypts
✅ Client receives plain text (over HTTPS!)
❌ Never commit plain text passwords
```

---

### Q5: What happens if Config Server is down?

**Answer:**

**Scenario: Config Server Unavailable**

**On Service Startup:**

With `fail-fast: true` (default):
```
Service starts → Calls Config Server → Fails → Service startup fails
```

With `fail-fast: false`:
```
Service starts → Calls Config Server → Fails → Uses bundled application.yml
```

**Configuration:**
```yaml
spring:
  cloud:
    config:
      fail-fast: false  # Don't fail if Config Server down
      retry:
        initial-interval: 1000
        max-attempts: 6
```

**For Running Services:**

Services already running continue to work because:
1. Configuration is fetched only on startup
2. Configuration is cached locally
3. Services don't continuously poll Config Server

**Best Practices:**

**1. High Availability:**
```
Run multiple Config Server instances behind load balancer
```

**2. Local Fallback:**
```yaml
spring:
  config:
    import: optional:configserver:http://localhost:8888
```
`optional:` means continue if Config Server unavailable

**3. Bundle Critical Configs:**
```
Include basic configs in application.yml
Fetch optional configs from Config Server
```

---

### Q6: How do you implement Config Server with Git backend?

**Answer:**

**Step 1: Configure Config Server**

```yaml
spring:
  cloud:
    config:
      server:
        git:
          # Repository URL
          uri: https://github.com/myorg/config-repo
          
          # Default branch
          default-label: main
          
          # Search subdirectories
          search-paths:
            - services
            - shared
          
          # Clone on startup
          clone-on-start: true
          
          # Authentication (if private)
          username: ${GIT_USERNAME}
          password: ${GIT_PAT}  # Personal Access Token
          
          # Timeout
          timeout: 5
          
          # Force pull
          force-pull: true
```

**Step 2: Git Repository Structure**

```
config-repo/
├── application.yml                    # Shared
├── services/
│   ├── product-service.yml
│   ├── product-service-dev.yml
│   ├── product-service-prod.yml
│   ├── order-service.yml
│   └── order-service-prod.yml
└── shared/
    ├── logging.yml
    └── security.yml
```

**Step 3: Using Specific Branch/Tag**

```yaml
# Client configuration
spring:
  cloud:
    config:
      label: release-1.0  # Use specific branch/tag
```

**Step 4: Multiple Repositories**

```yaml
spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/myorg/config-repo
          repos:
            # Special repo for production
            production:
              pattern: '*/prod'
              uri: https://github.com/myorg/prod-config-repo
              username: ${PROD_GIT_USER}
              password: ${PROD_GIT_PASS}
```

**Benefits of Git Backend:**
- ✅ Version control (Git history)
- ✅ Rollback capability
- ✅ Audit trail
- ✅ Pull requests for config changes
- ✅ Multiple environments (branches)

---

### Q7: What are the alternatives to Git backend for Config Server?

**Answer:**

| Backend | Use Case | Pros | Cons |
|---------|----------|------|------|
| **Git** | Default choice | Version control, audit trail | Requires Git server |
| **Filesystem** | Local development | Simple, fast | No version control |
| **Vault** | Secrets management | Encryption, dynamic secrets | Complex setup |
| **JDBC** | Database storage | Centralized, dynamic | No version control |
| **Consul** | Service mesh | KV store, service discovery | Limited features |
| **AWS S3** | Cloud storage | Scalable, durable | AWS-specific |

**Configuration Examples:**

**Filesystem:**
```yaml
spring:
  profiles:
    active: native
  cloud:
    config:
      server:
        native:
          search-locations: file:///config-repo
```

**Vault:**
```yaml
spring:
  cloud:
    config:
      server:
        vault:
          host: localhost
          port: 8200
          scheme: http
          backend: secret
          kv-version: 2
```

**JDBC:**
```yaml
spring:
  cloud:
    config:
      server:
        jdbc:
          sql: SELECT key, value from PROPERTIES where APPLICATION=? and PROFILE=? and LABEL=?
  datasource:
    url: jdbc:mysql://localhost:3306/config
    username: root
    password: secret
```

**Recommendation:**
- **Dev**: Filesystem (fast)
- **Prod**: Git (version control) + Vault (secrets)

---

### Q8: How do you secure Config Server?

**Answer:**

**1. Enable Spring Security**

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

```yaml
spring:
  security:
    user:
      name: configuser
      password: ${CONFIG_PASSWORD}
```

**2. Update Client Configuration**

```yaml
spring:
  cloud:
    config:
      uri: http://localhost:8888
      username: configuser
      password: ${CONFIG_PASSWORD}
```

**3. Enable HTTPS**

```yaml
server:
  port: 8888
  ssl:
    enabled: true
    key-store: classpath:keystore.p12
    key-store-password: changeme
    key-store-type: PKCS12
```

**4. Encrypt Sensitive Values**

```yaml
encrypt:
  key-store:
    location: classpath:server.jks
    password: changeme
    alias: config-server-key
```

**5. Network Security**

```
Config Server should NOT be publicly accessible
Put behind API Gateway or VPN
```

**6. Audit Logging**

```java
@Component
public class ConfigAuditListener implements ApplicationListener<EnvironmentChangeEvent> {
    
    @Override
    public void onApplicationEvent(EnvironmentChangeEvent event) {
        log.info("Config accessed by: {}", SecurityContextHolder.getContext().getAuthentication().getName());
    }
}
```

---

## Summary

**Key Takeaways:**

1. ✅ **Centralized Configuration**: One place to manage all configs
2. ✅ **Git Backend**: Version control and audit trail
3. ✅ **Environment Profiles**: Separate configs for dev/test/prod
4. ✅ **Encryption**: Protect sensitive values
5. ✅ **Dynamic Refresh**: Update configs without restart
6. ✅ **Hierarchical Resolution**: application.yml → service.yml → service-profile.yml
7. ✅ **High Availability**: Multiple Config Server instances

**Production Checklist:**
- [ ] Use Git backend with version control
- [ ] Encrypt all sensitive values
- [ ] Enable HTTPS for Config Server
- [ ] Implement Spring Security
- [ ] Run Config Server in HA mode
- [ ] Set up Spring Cloud Bus for broadcast refresh
- [ ] Configure fail-fast appropriately
- [ ] Monitor configuration access

**Next Steps:**
- Explore Circuit Breaker patterns (Section 04)
- Implement Feign clients (Section 05)
