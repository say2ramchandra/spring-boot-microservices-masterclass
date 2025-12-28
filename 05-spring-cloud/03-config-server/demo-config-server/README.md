# Spring Cloud Config Server Demo

This demo demonstrates a **Spring Cloud Config Server** that provides centralized external configuration management for microservices.

## Overview

**Config Server** serves configuration from a Git repository (or other backends) via REST API. Services fetch their configuration on startup and can refresh dynamically.

## Architecture

```
┌─────────────────────────────────────┐
│        Git Repository                │
│     (Configuration Store)            │
│                                      │
│  config-repo/                        │
│    ├── product-service.yml           │
│    ├── product-service-dev.yml       │
│    ├── order-service.yml             │
│    └── application.yml (shared)      │
└─────────────────────────────────────┘
                ↓
         Reads from
                ↓
┌─────────────────────────────────────┐
│      Config Server (8888)            │
│                                      │
│  REST Endpoints:                     │
│    GET /{app}/{profile}              │
│    GET /{app}-{profile}.yml          │
└─────────────────────────────────────┘
                ↓
         Fetches config
                ↓
┌─────────────────────────────────────┐
│      Microservices (Clients)         │
│                                      │
│  Product Service  Order Service      │
│     (8081)          (8082)           │
└─────────────────────────────────────┘
```

## Features

### 1. Git-Backed Configuration
- Store configurations in Git repository
- Version control for config changes
- Easy rollback to previous versions

### 2. Environment-Specific Configs
- Separate configs for dev/test/prod
- Profile-based configuration resolution
- Hierarchical property resolution

### 3. Dynamic Refresh
- Update configuration without restart
- `/actuator/refresh` endpoint
- Spring Cloud Bus for broadcast refresh

### 4. Security
- Basic authentication for Config Server
- Encryption for sensitive values
- HTTPS support

## Prerequisites

1. **Java 17+**
2. **Maven 3.6+**
3. **Git** (for local repository)

## Setup Instructions

### Step 1: Create Configuration Repository

Create a local Git repository to store configurations:

```bash
# Create config repository directory
mkdir ~/config-repo
cd ~/config-repo

# Initialize Git repository
git init

# Create shared configuration
cat > application.yml << 'EOF'
# Shared configuration for all services
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
EOF

# Create product-service configuration
cat > product-service.yml << 'EOF'
server:
  port: 8081

spring:
  application:
    name: product-service

app:
  message: "Product Service - Common Config"
  timeout: 5000
  max-retries: 3
EOF

# Create product-service development config
cat > product-service-dev.yml << 'EOF'
spring:
  datasource:
    url: jdbc:h2:mem:product_dev
    username: sa
    password:
  
  jpa:
    show-sql: true
    hibernate:
      ddl-auto: update

logging:
  level:
    com.example: DEBUG

app:
  message: "Product Service - Development Environment"
  feature-x-enabled: true
EOF

# Create product-service production config
cat > product-service-prod.yml << 'EOF'
spring:
  datasource:
    url: jdbc:mysql://prod-db.example.com:3306/product_prod
    username: prod_user
    password: '{cipher}ENCRYPTED_PASSWORD'
  
  jpa:
    show-sql: false
    hibernate:
      ddl-auto: validate

logging:
  level:
    com.example: INFO

app:
  message: "Product Service - Production Environment"
  feature-x-enabled: false
EOF

# Commit configurations
git add .
git commit -m "Initial configuration"
```

### Step 2: Build and Run Config Server

```bash
# Navigate to project directory
cd demo-config-server

# Build the project
mvn clean package

# Run Config Server
java -jar target/demo-config-server-1.0.0.jar

# Or use Maven
mvn spring-boot:run
```

**Config Server will start on port 8888**

### Step 3: Test Config Server

**Test Endpoints:**

```bash
# Get configuration for product-service in dev profile
curl -u configuser:configpass http://localhost:8888/product-service/dev

# Get as YAML
curl -u configuser:configpass http://localhost:8888/product-service/dev/main

# Get as properties
curl -u configuser:configpass http://localhost:8888/product-service-dev.properties

# Get production config
curl -u configuser:configpass http://localhost:8888/product-service/prod
```

**Health Check:**

```bash
curl http://localhost:8888/actuator/health
```

## API Endpoints

### Configuration Endpoints

| Endpoint | Description | Example |
|----------|-------------|---------|
| `/{application}/{profile}` | Get config as JSON | `/product-service/dev` |
| `/{application}/{profile}/{label}` | Get config from specific branch | `/product-service/dev/main` |
| `/{application}-{profile}.yml` | Get config as YAML | `/product-service-dev.yml` |
| `/{application}-{profile}.properties` | Get config as properties | `/product-service-dev.properties` |
| `/{label}/{application}-{profile}.yml` | Get config from branch as YAML | `/main/product-service-dev.yml` |

### Response Format

**JSON Response:**
```json
{
  "name": "product-service",
  "profiles": ["dev"],
  "label": "main",
  "version": "abc123...",
  "state": null,
  "propertySources": [
    {
      "name": "file:///home/user/config-repo/product-service-dev.yml",
      "source": {
        "spring.datasource.url": "jdbc:h2:mem:product_dev",
        "app.message": "Product Service - Development Environment"
      }
    },
    {
      "name": "file:///home/user/config-repo/product-service.yml",
      "source": {
        "server.port": 8081,
        "app.timeout": 5000
      }
    },
    {
      "name": "file:///home/user/config-repo/application.yml",
      "source": {
        "logging.level.root": "INFO"
      }
    }
  ]
}
```

## Configuration Resolution

Config Server resolves configuration in this order (highest priority first):

```
1. {application}-{profile}.yml
   Example: product-service-dev.yml

2. {application}.yml
   Example: product-service.yml

3. application.yml
   (Shared across all services)
```

**Example:**

For `product-service` in `dev` profile:

```yaml
# application.yml
logging:
  level:
    root: INFO

# product-service.yml  
app:
  timeout: 5000
  message: "Default Message"

# product-service-dev.yml
app:
  message: "Dev Message"
  feature-x: true
```

**Resolved Configuration:**
```yaml
logging:
  level:
    root: INFO         # from application.yml
app:
  timeout: 5000        # from product-service.yml
  message: "Dev Message"  # from product-service-dev.yml (overrides)
  feature-x: true      # from product-service-dev.yml
```

## Security

### Authentication

Config Server is secured with Spring Security:

**Default Credentials:**
- Username: `configuser`
- Password: `configpass`

**Admin Credentials:**
- Username: `admin`
- Password: `admin123`

### Encryption

**Encrypt a value:**
```bash
curl -u admin:admin123 http://localhost:8888/encrypt -d "MySecretPassword"
```

**Use encrypted value in config:**
```yaml
db.password: '{cipher}AQB3jJ7L8+9K3mN2pQ5rS6tU7vW8xY9zA0bC1dE2fF3gH4iJ5k'
```

Config Server automatically decrypts when serving configuration.

## Testing Scenarios

### Scenario 1: Different Environments

**Development:**
```bash
curl -u configuser:configpass http://localhost:8888/product-service/dev | jq
```

**Production:**
```bash
curl -u configuser:configpass http://localhost:8888/product-service/prod | jq
```

### Scenario 2: Configuration Update

1. **Update configuration in Git:**
```bash
cd ~/config-repo
echo "app.timeout: 10000" >> product-service-dev.yml
git add product-service-dev.yml
git commit -m "Increase timeout to 10s"
```

2. **Verify changes:**
```bash
curl -u configuser:configpass http://localhost:8888/product-service/dev | jq '.propertySources[0].source."app.timeout"'
```

### Scenario 3: Shared Configuration

All services inherit from `application.yml`:

```bash
# Product service inherits logging config
curl -u configuser:configpass http://localhost:8888/product-service/dev | jq '.propertySources[2].source."logging.level.root"'

# Order service also inherits same config
curl -u configuser:configpass http://localhost:8888/order-service/dev | jq '.propertySources[2].source."logging.level.root"'
```

## Troubleshooting

### Issue: Cannot find configuration

**Error:**
```
404 Not Found - Cannot find property source for application: product-service, profile: dev
```

**Solution:**
- Verify Git repository exists: `ls ~/config-repo`
- Check Git repository is initialized: `cd ~/config-repo && git status`
- Ensure configuration files are committed: `git log`

### Issue: Authentication failure

**Error:**
```
401 Unauthorized
```

**Solution:**
- Verify credentials: `configuser:configpass`
- Include authentication in request:
```bash
curl -u configuser:configpass http://localhost:8888/product-service/dev
```

### Issue: Configuration not updating

**Problem:** Changed config in Git but server returns old values

**Solution:**
- Ensure `force-pull: true` is set in `application.yml`
- Restart Config Server to pull latest changes
- Check Git commit: `cd ~/config-repo && git log`

## Production Considerations

### 1. Use Remote Git Repository

```yaml
spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/your-org/config-repo
          username: ${GIT_USERNAME}
          password: ${GIT_PAT}
```

### 2. Enable HTTPS

```yaml
server:
  port: 8888
  ssl:
    enabled: true
    key-store: classpath:keystore.p12
    key-store-password: ${KEYSTORE_PASSWORD}
    key-store-type: PKCS12
```

### 3. High Availability

Run multiple Config Server instances behind a load balancer:

```yaml
# Client configuration
spring:
  cloud:
    config:
      uri: http://loadbalancer:8888
      fail-fast: true
      retry:
        max-attempts: 6
```

### 4. Encrypt Sensitive Values

```bash
# Generate encryption key
keytool -genkeypair -alias config-server-key -keyalg RSA \
        -keysize 2048 -keystore config-server.jks -storepass changeme
```

```yaml
encrypt:
  key-store:
    location: classpath:config-server.jks
    password: changeme
    alias: config-server-key
```

## Next Steps

1. **Create Config Client** (see `demo-config-client`)
2. **Implement Dynamic Refresh** with `@RefreshScope`
3. **Add Spring Cloud Bus** for broadcast refresh
4. **Integrate with Eureka** for service discovery
5. **Set up Git webhooks** for automatic refresh

## References

- [Spring Cloud Config Documentation](https://spring.io/projects/spring-cloud-config)
- [Config Server Setup Guide](https://cloud.spring.io/spring-cloud-config/reference/html/)
- [Encryption and Decryption](https://cloud.spring.io/spring-cloud-config/reference/html/#_encryption_and_decryption)
