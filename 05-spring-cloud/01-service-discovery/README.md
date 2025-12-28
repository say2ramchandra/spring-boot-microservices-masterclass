# Section 01: Service Discovery with Eureka

## Table of Contents
1. [Introduction](#introduction)
2. [What is Service Discovery](#what-is-service-discovery)
3. [Eureka Architecture](#eureka-architecture)
4. [Setting Up Eureka Server](#setting-up-eureka-server)
5. [Registering Services](#registering-services)
6. [Service Discovery Patterns](#service-discovery-patterns)
7. [High Availability](#high-availability)
8. [Best Practices](#best-practices)
9. [Interview Questions](#interview-questions)

---

## Introduction

In a microservices architecture, services need to communicate with each other. But how does Service A know where Service B is running? Service Discovery solves this problem by maintaining a registry of all available services.

### The Problem

**Without Service Discovery:**
```
Order Service needs to call Payment Service
↓
Where is Payment Service?
- Is it at 192.168.1.10:8080?
- Or 192.168.1.11:8080?
- What if it moves?
- What if there are multiple instances?
```

**Challenges:**
- Hard-coded URLs break when services move
- Load balancing becomes manual
- Service instances scale dynamically
- No automatic health checks

---

## What is Service Discovery

**Service Discovery** is a mechanism that allows services to:
1. Register themselves with a central registry
2. Discover other services dynamically
3. Get notified when services come up/down
4. Load balance across multiple instances

### Two Types

#### 1. Client-Side Discovery (Eureka)

```
┌─────────────────────────────────────┐
│     Eureka Server (Registry)        │
│      http://localhost:8761          │
└─────────────────────────────────────┘
     ↑                          ↑
Register                   Register
     │                          │
┌─────────┐                ┌─────────┐
│Service A│                │Service B│
│ (8081)  │                │ (8082)  │
└─────────┘                └─────────┘
     │
     │ 1. Query registry for Service B
     │ 2. Get Service B address
     │ 3. Call Service B directly
     │
     └──────────────────→ Service B
```

**Pros:**
- Client controls load balancing
- No single point of failure for routing
- Better performance (direct calls)

**Cons:**
- Clients need discovery logic
- Language-specific clients

#### 2. Server-Side Discovery

```
┌─────────────────────────────────────┐
│      Service Registry               │
└─────────────────────────────────────┘
              ↑
              │
┌─────────────┴─────────────┐
│      Load Balancer         │
└─────────────┬─────────────┘
              ↓
      ┌───────┴───────┐
      ↓               ↓
┌─────────┐      ┌─────────┐
│Service A│      │Service B│
└─────────┘      └─────────┘
```

**Pros:**
- Centralized routing logic
- Language agnostic
- Simpler clients

**Cons:**
- Load balancer is single point of failure
- Extra network hop

---

## Eureka Architecture

### Components

#### 1. Eureka Server (Service Registry)

- Maintains registry of all service instances
- Provides REST API for registration/discovery
- Sends heartbeats to check service health
- Removes dead instances automatically

#### 2. Eureka Client (Service)

- Registers itself on startup
- Sends heartbeat every 30 seconds
- Fetches registry from server
- De-registers on shutdown

### Communication Flow

```
Service Startup:
1. Service starts
2. Registers with Eureka (POST)
3. Sends instance metadata (host, port, health URL)

Heartbeat:
4. Every 30s: Service sends heartbeat
5. Eureka marks service as UP

Service Discovery:
6. Service A queries Eureka for Service B
7. Eureka returns list of Service B instances
8. Service A picks one instance (load balancing)
9. Service A calls Service B directly

Service Shutdown:
10. Service sends de-register request
11. Eureka removes service from registry
```

### Registry Cache

```
┌──────────────────────────┐
│    Eureka Server         │
│  (Master Registry)       │
└──────────────────────────┘
            ↓
     Replicate every 30s
            ↓
┌──────────────────────────┐
│   Client-Side Cache      │
│   (Local Registry Copy)  │
└──────────────────────────┘
```

**Benefits:**
- Faster lookups (no network call)
- Resilience (works if Eureka is down)
- Reduced load on Eureka server

---

## Setting Up Eureka Server

### Step 1: Add Dependencies

```xml
<dependencies>
    <!-- Eureka Server -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
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

### Step 2: Enable Eureka Server

```java
package com.example.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer  // This annotation enables Eureka Server
public class EurekaServerApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
```

### Step 3: Configure Properties

```properties
# Application Name
spring.application.name=eureka-server

# Server Port
server.port=8761

# Eureka Server Configuration
# Don't register itself as a client
eureka.client.register-with-eureka=false
eureka.client.fetch-registry=false

# Service URL
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/

# Disable self-preservation mode (for development)
eureka.server.enable-self-preservation=false

# Eviction interval
eureka.server.eviction-interval-timer-in-ms=3000

# Disable delta updates
eureka.server.disable-delta=true

# Logging
logging.level.com.netflix.eureka=OFF
logging.level.com.netflix.discovery=OFF
```

### Step 4: Access Dashboard

```
http://localhost:8761
```

**Dashboard shows:**
- Registered services
- Instance count per service
- Status (UP/DOWN)
- Renewal thresholds
- General info

---

## Registering Services

### Step 1: Add Eureka Client Dependency

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

### Step 2: Enable Discovery Client

```java
package com.example.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient  // Enable Eureka client
public class ProductServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }
}
```

### Step 3: Configure Client

```properties
# Service Name (IMPORTANT - used for discovery)
spring.application.name=product-service

# Server Port
server.port=8081

# Eureka Client Configuration
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/

# Prefer IP address over hostname
eureka.instance.prefer-ip-address=true

# Instance ID (useful for multiple instances)
eureka.instance.instance-id=${spring.application.name}:${random.value}

# Heartbeat interval (default: 30s)
eureka.instance.lease-renewal-interval-in-seconds=30

# Time after which Eureka removes instance if no heartbeat (default: 90s)
eureka.instance.lease-expiration-duration-in-seconds=90

# Health check URL
eureka.instance.health-check-url-path=/actuator/health
```

### Step 4: Verify Registration

Check Eureka dashboard at `http://localhost:8761`. You should see:

```
Application         AMIs        Availability Zones    Status
PRODUCT-SERVICE     n/a (1)     (1)                  UP (1) - 192.168.1.10:product-service:8081
```

---

## Service Discovery Patterns

### Pattern 1: Using RestTemplate with @LoadBalanced

```java
@Configuration
public class RestConfig {
    
    @Bean
    @LoadBalanced  // Enable service discovery
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

@Service
public class OrderService {
    
    @Autowired
    private RestTemplate restTemplate;
    
    public Product getProduct(Long productId) {
        // Use service name instead of URL
        String url = "http://PRODUCT-SERVICE/api/products/" + productId;
        return restTemplate.getForObject(url, Product.class);
    }
}
```

**How it works:**
1. RestTemplate intercepts the call
2. Resolves `PRODUCT-SERVICE` using Eureka
3. Gets list of instances
4. Picks one using load balancing algorithm
5. Replaces service name with actual URL
6. Makes the HTTP call

### Pattern 2: Using WebClient

```java
@Configuration
public class WebClientConfig {
    
    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}

@Service
public class OrderService {
    
    private final WebClient webClient;
    
    public OrderService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }
    
    public Mono<Product> getProduct(Long productId) {
        return webClient.get()
            .uri("http://PRODUCT-SERVICE/api/products/{id}", productId)
            .retrieve()
            .bodyToMono(Product.class);
    }
}
```

### Pattern 3: Using DiscoveryClient (Manual)

```java
@Service
public class OrderService {
    
    @Autowired
    private DiscoveryClient discoveryClient;
    
    @Autowired
    private RestTemplate restTemplate;
    
    public Product getProduct(Long productId) {
        // Get all instances of PRODUCT-SERVICE
        List<ServiceInstance> instances = 
            discoveryClient.getInstances("PRODUCT-SERVICE");
        
        if (instances.isEmpty()) {
            throw new ServiceUnavailableException("Product service not available");
        }
        
        // Pick first instance (or implement your own load balancing)
        ServiceInstance instance = instances.get(0);
        
        // Build URL
        String url = instance.getUri() + "/api/products/" + productId;
        
        return restTemplate.getForObject(url, Product.class);
    }
}
```

### Pattern 4: Using Feign Client

```java
@FeignClient(name = "PRODUCT-SERVICE")
public interface ProductClient {
    
    @GetMapping("/api/products/{id}")
    Product getProductById(@PathVariable Long id);
}

@Service
public class OrderService {
    
    @Autowired
    private ProductClient productClient;
    
    public Product getProduct(Long productId) {
        // Very simple!
        return productClient.getProductById(productId);
    }
}
```

---

## High Availability

### Problem: Single Eureka Server

```
If Eureka Server goes down:
- New services can't register
- Existing services can't discover new instances
- But existing services continue working (cached registry)
```

### Solution: Eureka Server Cluster

```
┌──────────────┐         ┌──────────────┐
│  Eureka 1    │◄───────►│  Eureka 2    │
│  (8761)      │  Peer   │  (8762)      │
└──────────────┘         └──────────────┘
       ↑                        ↑
       │                        │
   Register                 Register
       │                        │
   ┌───────┐              ┌───────┐
   │Service│              │Service│
   │   A   │              │   B   │
   └───────┘              └───────┘
```

### Eureka Server 1 Configuration

```properties
# Peer Configuration
spring.application.name=eureka-server
server.port=8761

# Register with peer
eureka.client.register-with-eureka=true
eureka.client.fetch-registry=true
eureka.client.service-url.defaultZone=http://localhost:8762/eureka/

# Instance
eureka.instance.hostname=eureka-server-1
```

### Eureka Server 2 Configuration

```properties
spring.application.name=eureka-server
server.port=8762

eureka.client.register-with-eureka=true
eureka.client.fetch-registry=true
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/

eureka.instance.hostname=eureka-server-2
```

### Client Configuration (Multiple Eureka Servers)

```properties
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/,http://localhost:8762/eureka/
```

**Benefits:**
- No single point of failure
- Automatic failover
- Registry replication

---

## Best Practices

### 1. Use Meaningful Service Names

```properties
# ❌ Bad
spring.application.name=service1

# ✅ Good
spring.application.name=product-service
```

### 2. Enable Health Checks

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

```properties
management.endpoints.web.exposure.include=health,info
management.endpoint.health.show-details=always
eureka.instance.health-check-url-path=/actuator/health
```

### 3. Configure Proper Timeouts

```properties
# How often to send heartbeat (default: 30s)
eureka.instance.lease-renewal-interval-in-seconds=10

# Time before removing instance if no heartbeat (default: 90s)
eureka.instance.lease-expiration-duration-in-seconds=30

# How often to fetch registry (default: 30s)
eureka.client.registry-fetch-interval-seconds=10
```

### 4. Use Instance ID for Multiple Instances

```properties
eureka.instance.instance-id=${spring.application.name}:${spring.application.instance_id:${random.value}}
```

### 5. Secure Eureka Server

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
            .httpBasic();
        return http.build();
    }
}
```

```properties
spring.security.user.name=admin
spring.security.user.password=admin123

# Client configuration
eureka.client.service-url.defaultZone=http://admin:admin123@localhost:8761/eureka/
```

### 6. Monitor Eureka Metrics

```properties
management.endpoints.web.exposure.include=*
management.metrics.export.prometheus.enabled=true
```

Access metrics:
```
http://localhost:8761/actuator/metrics
http://localhost:8761/actuator/prometheus
```

### 7. Graceful Shutdown

```properties
server.shutdown=graceful
spring.lifecycle.timeout-per-shutdown-phase=30s
```

```java
@PreDestroy
public void onShutdown() {
    log.info("De-registering from Eureka...");
    // Spring Boot handles this automatically
}
```

---

## Interview Questions

### Q1: What is Service Discovery and why do we need it?

**Answer:**

Service Discovery is a mechanism for services to find and communicate with each other dynamically.

**Why We Need It:**
1. **Dynamic Environments**: Services start/stop dynamically
2. **Scaling**: Multiple instances of same service
3. **Load Balancing**: Distribute calls across instances
4. **Resilience**: Automatic removal of failed instances
5. **No Hard-Coding**: No need for static IP addresses

**Example Without Service Discovery:**
```java
// Hard-coded URL - breaks if service moves
String url = "http://192.168.1.10:8080/api/products/1";
```

**Example With Service Discovery:**
```java
// Dynamic discovery - works regardless of location
String url = "http://PRODUCT-SERVICE/api/products/1";
```

---

### Q2: Explain Eureka architecture and its components

**Answer:**

**Components:**

1. **Eureka Server (Registry)**:
   - Central registry storing all service instances
   - Provides REST API for registration/discovery
   - Sends heartbeat checks
   - Port: 8761 (default)

2. **Eureka Client (Service)**:
   - Registers with Eureka on startup
   - Sends heartbeat every 30 seconds
   - Fetches registry and caches locally
   - De-registers on shutdown

**Communication Flow:**
```
1. Service starts → Registers with Eureka
2. Every 30s → Sends heartbeat
3. Service A needs Service B → Queries Eureka
4. Eureka returns Service B instances
5. Service A picks instance (load balancing)
6. Service A calls Service B directly
```

**Key Features:**
- **Self-Preservation Mode**: Prevents mass deregistration during network partitions
- **Registry Replication**: Supports clustering for HA
- **Client-Side Caching**: Services work even if Eureka is temporarily down

---

### Q3: What is the difference between @EnableEurekaServer and @EnableDiscoveryClient?

**Answer:**

| Annotation | Purpose | Usage |
|------------|---------|-------|
| **@EnableEurekaServer** | Creates Eureka Server | Registry application only |
| **@EnableDiscoveryClient** | Makes service a Eureka client | Microservices that register |

**@EnableEurekaServer:**
```java
@SpringBootApplication
@EnableEurekaServer  // This is the registry
public class EurekaServerApplication {
    // Eureka dashboard at http://localhost:8761
}
```

**@EnableDiscoveryClient:**
```java
@SpringBootApplication
@EnableDiscoveryClient  // This service registers with Eureka
public class ProductServiceApplication {
    // Registers as "PRODUCT-SERVICE"
}
```

**Note:** `@EnableDiscoveryClient` is optional in Spring Cloud versions 2020+. Services auto-register if Eureka client dependency is present.

---

### Q4: How does client-side load balancing work with Eureka?

**Answer:**

**Client-Side Load Balancing:**
- Client (not server) decides which instance to call
- Uses `@LoadBalanced` RestTemplate/WebClient
- Default algorithm: Round Robin

**How It Works:**

```java
@Bean
@LoadBalanced
public RestTemplate restTemplate() {
    return new RestTemplate();
}

// Usage
restTemplate.getForObject("http://PRODUCT-SERVICE/api/products/1", Product.class);
```

**Behind the Scenes:**
1. Intercepts call to "PRODUCT-SERVICE"
2. Queries Eureka for all PRODUCT-SERVICE instances
3. Gets: [instance1:8081, instance2:8082, instance3:8083]
4. Applies load balancing algorithm (Round Robin)
5. Selects instance2:8082
6. Replaces URL: "http://192.168.1.10:8082/api/products/1"
7. Makes HTTP call

**Load Balancing Algorithms:**
- **Round Robin** (default): Rotates through instances
- **Random**: Picks random instance
- **Weighted**: Based on instance metrics

**Configuration:**
```yaml
spring:
  cloud:
    loadbalancer:
      ribbon:
        enabled: false  # Use Spring Cloud LoadBalancer
```

---

### Q5: What happens if Eureka Server goes down?

**Answer:**

**Scenario: Eureka Server Fails**

**Short-Term (Services Continue Working):**
1. Services have **cached registry** locally
2. Services can still discover and call each other
3. Existing connections work normally
4. Cache refreshes every 30 seconds (from cache itself)

**Long-Term Issues:**
1. **New services can't register** → Not discoverable
2. **Service state changes not updated** → Calling dead instances
3. **Scaling events missed** → New instances not discovered
4. **Health updates not propagated** → Unhealthy services still called

**Solutions:**

**1. Self-Preservation Mode** (Enabled by default):
```properties
eureka.server.enable-self-preservation=true
```
- Prevents mass deregistration during network partition
- Eureka assumes network issue, not service failures
- Keeps services in registry even without heartbeats

**2. Eureka Server Cluster** (HA):
```properties
# Multiple Eureka servers
eureka.client.service-url.defaultZone=http://eureka1:8761/eureka/,http://eureka2:8762/eureka/
```

**3. Client Retries:**
```properties
eureka.client.registry-fetch-interval-seconds=5  # Fetch more frequently
```

**Best Practice**: Always run Eureka in cluster for production.

---

### Q6: What is Eureka self-preservation mode?

**Answer:**

**Self-Preservation Mode** protects the registry from mass deregistration during network partitions.

**Problem:**
```
Network Partition → Services can't send heartbeat
                  → Eureka thinks all services are down
                  → Removes all services from registry
                  → Disaster!
```

**Solution (Self-Preservation):**
```
If renewal rate < threshold (85% by default)
  → Eureka suspects network issue
  → Stops removing services
  → Shows warning: "EMERGENCY! EUREKA MAY BE INCORRECTLY CLAIMING INSTANCES ARE UP WHEN THEY'RE NOT"
  → Waits for network to recover
```

**Configuration:**

Enable (Production):
```properties
eureka.server.enable-self-preservation=true
eureka.server.renewal-percent-threshold=0.85  # 85% threshold
```

Disable (Development):
```properties
eureka.server.enable-self-preservation=false
eureka.server.eviction-interval-timer-in-ms=3000  # Check every 3s
```

**When to Disable:**
- Local development (faster instance removal)
- Testing (want immediate deregistration)

**When to Enable:**
- Production (protect against network issues)
- Large-scale deployments

---

### Q7: How do you secure Eureka Server?

**Answer:**

**1. Add Spring Security:**

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

**2. Configure Security:**

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()  // Disable CSRF for REST endpoints
            .authorizeHttpRequests(auth -> auth
                .anyRequest().authenticated()
            )
            .httpBasic();  // Basic authentication
        
        return http.build();
    }
    
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.builder()
            .username("eureka")
            .password(passwordEncoder().encode("secret"))
            .roles("USER")
            .build();
        
        return new InMemoryUserDetailsManager(user);
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

**3. Update Client Configuration:**

```properties
# Include credentials in URL
eureka.client.service-url.defaultZone=http://eureka:secret@localhost:8761/eureka/
```

**4. Enable HTTPS:**

```properties
server.port=8761
server.ssl.enabled=true
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=password
server.ssl.key-store-type=PKCS12
```

**5. Restrict Dashboard Access:**

```java
http.authorizeHttpRequests(auth -> auth
    .requestMatchers("/").hasRole("ADMIN")  // Dashboard
    .requestMatchers("/eureka/**").permitAll()  // API
    .anyRequest().authenticated()
)
```

---

### Q8: What are the alternatives to Eureka?

**Answer:**

| Solution | Type | Best For |
|----------|------|----------|
| **Consul** | Service Discovery + KV Store | Multi-datacenter, health checks |
| **Zookeeper** | Distributed Coordination | Strong consistency needs |
| **Kubernetes** | Container Orchestration | Cloud-native apps |
| **Istio** | Service Mesh | Advanced routing, security |
| **AWS Cloud Map** | AWS Service Discovery | AWS-native applications |

**Comparison:**

**Eureka:**
```
+ Easy to set up
+ Spring Cloud integration
+ AP (Available, Partition-tolerant)
- Netflix no longer actively developing
- Limited features
```

**Consul:**
```
+ Health checks (HTTP, TCP, Script)
+ Key-value store
+ Multi-datacenter support
+ DNS interface
- More complex setup
```

**Kubernetes:**
```
+ Built-in service discovery
+ No separate component
+ Production-grade
- Requires Kubernetes cluster
- Steeper learning curve
```

**When to Choose:**
- **Eureka**: Spring Boot apps, easy start
- **Consul**: Multi-DC, advanced health checks
- **Kubernetes**: Cloud-native, containerized apps
- **Istio**: Need service mesh features

---

## Summary

**Key Takeaways:**

1. ✅ **Service Discovery** eliminates hard-coded URLs
2. ✅ **Eureka** provides client-side service discovery
3. ✅ **@EnableEurekaServer** creates registry
4. ✅ **@EnableDiscoveryClient** registers service
5. ✅ **@LoadBalanced** enables client-side load balancing
6. ✅ **Self-Preservation** protects against network partitions
7. ✅ **Clustering** provides high availability
8. ✅ **Health Checks** ensure only healthy instances are called

**Production Checklist:**
- [ ] Run Eureka in cluster (2+ instances)
- [ ] Enable security (Spring Security + HTTPS)
- [ ] Configure proper timeouts
- [ ] Enable health checks
- [ ] Monitor Eureka metrics
- [ ] Enable self-preservation mode
- [ ] Configure graceful shutdown

**Next Steps:**
- Implement API Gateway (Section 02)
- Add Config Server (Section 03)
- Integrate Circuit Breaker (Section 04)
