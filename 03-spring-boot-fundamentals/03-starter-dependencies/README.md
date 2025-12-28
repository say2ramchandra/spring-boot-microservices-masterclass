# Spring Boot Starter Dependencies

> **Understanding and using Spring Boot starters for rapid application development**

## 📚 Table of Contents

- [What are Spring Boot Starters?](#what-are-spring-boot-starters)
- [Why Use Starters?](#why-use-starters)
- [Common Spring Boot Starters](#common-spring-boot-starters)
- [How Starters Work](#how-starters-work)
- [Using Starters](#using-starters)
- [Creating Custom Starters](#creating-custom-starters)
- [Dependency Management](#dependency-management)
- [Best Practices](#best-practices)
- [Demo Project](#demo-project)
- [Interview Questions](#interview-questions)

---

## What are Spring Boot Starters?

Spring Boot starters are **opinionated dependency descriptors** that bundle together all the dependencies needed for a specific functionality. Instead of manually adding multiple dependencies, you add one starter and get everything you need.

### The Problem Without Starters

Building a web application traditionally:

```xml
<!-- Before Spring Boot Starters -->
<dependencies>
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-webmvc</artifactId>
        <version>6.1.0</version>
    </dependency>
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-web</artifactId>
        <version>6.1.0</version>
    </dependency>
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
        <version>2.15.3</version>
    </dependency>
    <dependency>
        <groupId>org.hibernate.validator</groupId>
        <artifactId>hibernate-validator</artifactId>
        <version>8.0.1.Final</version>
    </dependency>
    <dependency>
        <groupId>org.apache.tomcat.embed</groupId>
        <artifactId>tomcat-embed-core</artifactId>
        <version>10.1.15</version>
    </dependency>
    <!-- ... 10+ more dependencies -->
</dependencies>
```

### The Solution: Spring Boot Starters

With Spring Boot starters:

```xml
<!-- With Spring Boot Starter -->
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
</dependencies>
```

**That's it!** One dependency gives you:
- Spring MVC
- Embedded Tomcat
- JSON processing (Jackson)
- Validation
- All compatible versions managed automatically

---

## Why Use Starters?

### 1. **Simplified Dependency Management**

No need to research which dependencies work together.

```xml
<!-- Without starter - manual management -->
<dependency>
    <groupId>org.springframework.data</groupId>
    <artifactId>spring-data-jpa</artifactId>
    <version>3.2.0</version>
</dependency>
<dependency>
    <groupId>org.hibernate.orm</groupId>
    <artifactId>hibernate-core</artifactId>
    <version>6.4.0.Final</version>
</dependency>
<dependency>
    <groupId>jakarta.persistence</groupId>
    <artifactId>jakarta.persistence-api</artifactId>
    <version>3.1.0</version>
</dependency>

<!-- With starter - automatic management -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

### 2. **Version Compatibility**

All dependencies in a starter are tested together and guaranteed to work.

### 3. **Reduced Configuration**

Starters trigger auto-configuration, so you get sensible defaults.

### 4. **Faster Development**

Focus on business logic, not dependency management.

### 5. **Best Practices Built-in**

Starters follow Spring Boot's opinionated approach with production-ready defaults.

---

## Common Spring Boot Starters

### Web Development

| Starter | Purpose | Key Dependencies |
|---------|---------|------------------|
| `spring-boot-starter-web` | RESTful web applications | Spring MVC, Tomcat, Jackson |
| `spring-boot-starter-webflux` | Reactive web applications | Spring WebFlux, Netty, Reactor |
| `spring-boot-starter-thymeleaf` | Server-side templates | Thymeleaf, Spring MVC |
| `spring-boot-starter-websocket` | WebSocket applications | Spring WebSocket, SockJS |

### Data Access

| Starter | Purpose | Key Dependencies |
|---------|---------|------------------|
| `spring-boot-starter-data-jpa` | JPA with Hibernate | Spring Data JPA, Hibernate |
| `spring-boot-starter-data-mongodb` | MongoDB database | Spring Data MongoDB |
| `spring-boot-starter-data-redis` | Redis database | Spring Data Redis, Lettuce |
| `spring-boot-starter-jdbc` | JDBC with connection pool | Spring JDBC, HikariCP |

### Messaging

| Starter | Purpose | Key Dependencies |
|---------|---------|------------------|
| `spring-boot-starter-amqp` | RabbitMQ messaging | Spring AMQP, RabbitMQ client |
| `spring-boot-starter-kafka` | Apache Kafka | Spring Kafka |
| `spring-boot-starter-artemis` | ActiveMQ Artemis | Spring JMS, Artemis |

### Security

| Starter | Purpose | Key Dependencies |
|---------|---------|------------------|
| `spring-boot-starter-security` | Spring Security | Spring Security Core/Web |
| `spring-boot-starter-oauth2-client` | OAuth2 client | Spring Security OAuth2 |
| `spring-boot-starter-oauth2-resource-server` | OAuth2 resource server | Spring Security OAuth2 |

### Monitoring & Operations

| Starter | Purpose | Key Dependencies |
|---------|---------|------------------|
| `spring-boot-starter-actuator` | Production-ready features | Micrometer, endpoints |
| `spring-boot-starter-validation` | Bean validation | Hibernate Validator |

### Testing

| Starter | Purpose | Key Dependencies |
|---------|---------|------------------|
| `spring-boot-starter-test` | Testing framework | JUnit, Mockito, AssertJ, Spring Test |

### Cloud & Microservices

| Starter | Purpose | Key Dependencies |
|---------|---------|------------------|
| `spring-cloud-starter-netflix-eureka-client` | Service discovery | Eureka client |
| `spring-cloud-starter-openfeign` | Declarative REST client | Feign |
| `spring-cloud-starter-config` | Externalized config | Spring Cloud Config |

---

## How Starters Work

### Anatomy of a Starter

A starter is essentially a Maven POM file that declares dependencies:

**Example: spring-boot-starter-web**

```xml
<project>
    <artifactId>spring-boot-starter-web</artifactId>
    
    <dependencies>
        <!-- Core Spring Boot -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>
        
        <!-- JSON support -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-json</artifactId>
        </dependency>
        
        <!-- Embedded Tomcat -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-tomcat</artifactId>
        </dependency>
        
        <!-- Spring MVC -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-webmvc</artifactId>
        </dependency>
    </dependencies>
</project>
```

### Starter Naming Convention

**Official starters**: `spring-boot-starter-*`
- `spring-boot-starter-web`
- `spring-boot-starter-data-jpa`

**Third-party starters**: `*-spring-boot-starter`
- `mybatis-spring-boot-starter`
- `camel-spring-boot-starter`

### Parent POM Management

Spring Boot Parent manages versions:

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.0</version>
</parent>
```

Now you don't need to specify versions:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <!-- No version needed! -->
</dependency>
```

---

## Using Starters

### Example 1: Web Application

```xml
<dependencies>
    <!-- Web support -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <!-- Template engine -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-thymeleaf</artifactId>
    </dependency>
    
    <!-- Development tools -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-devtools</artifactId>
        <optional>true</optional>
    </dependency>
</dependencies>
```

### Example 2: REST API with Database

```xml
<dependencies>
    <!-- REST API -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <!-- Database -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    
    <!-- Database driver -->
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>runtime</scope>
    </dependency>
    
    <!-- Validation -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
</dependencies>
```

### Example 3: Microservice with Security

```xml
<dependencies>
    <!-- Web -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <!-- Security -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    
    <!-- Service Discovery -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
    
    <!-- Circuit Breaker -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-circuitbreaker-resilience4j</artifactId>
    </dependency>
    
    <!-- Monitoring -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
</dependencies>
```

---

## Creating Custom Starters

### When to Create Custom Starters?

1. **Shared libraries** across multiple projects
2. **Company-wide configurations** (logging, monitoring)
3. **Reusable integrations** with external systems
4. **Standard setups** for teams

### Custom Starter Structure

```
my-custom-spring-boot-starter/
├── my-custom-spring-boot-autoconfigure/
│   ├── src/main/java/
│   │   └── com/company/autoconfigure/
│   │       ├── CustomAutoConfiguration.java
│   │       └── CustomProperties.java
│   └── src/main/resources/
│       └── META-INF/spring/
│           └── org.springframework.boot.autoconfigure.AutoConfiguration.imports
└── my-custom-spring-boot-starter/
    └── pom.xml (depends on autoconfigure)
```

### Step-by-Step Example

**1. Create Auto-Configuration Module**

```java
@AutoConfiguration
@ConditionalOnClass(CustomService.class)
@EnableConfigurationProperties(CustomProperties.class)
public class CustomAutoConfiguration {
    
    @Bean
    @ConditionalOnMissingBean
    public CustomService customService(CustomProperties properties) {
        return new CustomService(properties);
    }
}
```

**2. Create Properties Class**

```java
@ConfigurationProperties(prefix = "custom")
public class CustomProperties {
    private String apiKey;
    private String endpoint = "http://localhost:8080";
    // getters/setters
}
```

**3. Register Auto-Configuration**

Create `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`:

```
com.company.autoconfigure.CustomAutoConfiguration
```

**4. Create Starter POM**

```xml
<project>
    <artifactId>my-custom-spring-boot-starter</artifactId>
    
    <dependencies>
        <dependency>
            <groupId>com.company</groupId>
            <artifactId>my-custom-spring-boot-autoconfigure</artifactId>
        </dependency>
        
        <!-- Other dependencies your starter needs -->
    </dependencies>
</project>
```

**5. Use Your Starter**

```xml
<dependency>
    <groupId>com.company</groupId>
    <artifactId>my-custom-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

```properties
# application.properties
custom.api-key=secret123
custom.endpoint=https://api.company.com
```

---

## Dependency Management

### Viewing Dependencies

```bash
# Maven
mvn dependency:tree

# Gradle
gradle dependencies
```

### Excluding Dependencies

Sometimes you need to exclude transitive dependencies:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-tomcat</artifactId>
        </exclusion>
    </exclusions>
</dependency>

<!-- Use Jetty instead -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jetty</artifactId>
</dependency>
```

### Overriding Versions

```xml
<properties>
    <!-- Override default version -->
    <jackson.version>2.16.0</jackson.version>
</properties>
```

### BOM (Bill of Materials)

For projects not using spring-boot-starter-parent:

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-dependencies</artifactId>
            <version>3.2.0</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

---

## Best Practices

### 1. Use Starters Instead of Individual Dependencies

```xml
<!-- ❌ Don't -->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-webmvc</artifactId>
</dependency>
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
</dependency>

<!-- ✅ Do -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

### 2. Don't Specify Versions for Managed Dependencies

```xml
<!-- ❌ Don't -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <version>3.2.0</version>
</dependency>

<!-- ✅ Do -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

### 3. Use Appropriate Scopes

```xml
<!-- Development only -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <optional>true</optional>
</dependency>

<!-- Runtime only (database drivers) -->
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>

<!-- Test only -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

### 4. Minimize Dependencies

Only add what you need:

```xml
<!-- ❌ Don't add unnecessary starters -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>
<!-- If you're not using MongoDB -->

<!-- ✅ Add only what you use -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

### 5. Keep Dependencies Up-to-Date

Regularly update Spring Boot version for security patches:

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.0</version> <!-- Keep updated -->
</parent>
```

---

## Demo Project

See [demo-starter-usage](demo-starter-usage/) for a complete example demonstrating:

1. **Common starters in action**
2. **Dependency analysis**
3. **Custom starter creation**
4. **Exclusions and overrides**

---

## Interview Questions

### Q1: What is a Spring Boot Starter?

**Answer**: A Spring Boot Starter is a dependency descriptor that bundles together all the required dependencies for a specific functionality. It simplifies dependency management by providing:
- Pre-configured, compatible dependency versions
- Auto-configuration triggers
- Best practice defaults

Example: `spring-boot-starter-web` includes Spring MVC, embedded Tomcat, JSON processing, and validation.

### Q2: What's the difference between spring-boot-starter-web and spring-boot-starter-webflux?

**Answer**:

| Feature | starter-web | starter-webflux |
|---------|------------|-----------------|
| **Programming Model** | Imperative (blocking) | Reactive (non-blocking) |
| **Server** | Tomcat (Servlet) | Netty (Reactive Streams) |
| **Concurrency** | Thread-per-request | Event loop |
| **Use Case** | Traditional web apps | High-concurrency, streaming |
| **Dependencies** | Spring MVC | Spring WebFlux, Project Reactor |

### Q3: How do you exclude a transitive dependency from a starter?

**Answer**:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-tomcat</artifactId>
        </exclusion>
    </exclusions>
</dependency>

<!-- Add alternative -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jetty</artifactId>
</dependency>
```

### Q4: What is the naming convention for Spring Boot starters?

**Answer**:
- **Official starters**: `spring-boot-starter-*` (e.g., `spring-boot-starter-web`)
- **Third-party starters**: `*-spring-boot-starter` (e.g., `mybatis-spring-boot-starter`)

This helps identify official vs. third-party starters.

### Q5: How do you create a custom Spring Boot starter?

**Answer**: Three main steps:

1. **Create auto-configuration**:
```java
@AutoConfiguration
@ConditionalOnClass(MyService.class)
@EnableConfigurationProperties(MyProperties.class)
public class MyAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public MyService myService(MyProperties props) {
        return new MyService(props);
    }
}
```

2. **Register in `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`**:
```
com.company.MyAutoConfiguration
```

3. **Create starter POM** that depends on autoconfigure module.

### Q6: What is spring-boot-starter-parent and why use it?

**Answer**: `spring-boot-starter-parent` is a parent POM that provides:
- **Dependency management**: Pre-defined versions for all Spring Boot dependencies
- **Plugin configuration**: Maven plugins with sensible defaults
- **Resource filtering**: Automatic property expansion
- **Java version**: Default Java version configuration

Alternative: Use `spring-boot-dependencies` BOM if you can't use the parent POM.

### Q7: How do you override a dependency version managed by Spring Boot?

**Answer**:

```xml
<properties>
    <!-- Override managed version -->
    <jackson.version>2.16.0</jackson.version>
    <hibernate.version>6.4.1.Final</hibernate.version>
</properties>
```

Spring Boot uses these property names internally, so overriding them changes all related dependencies.

---

## Summary

| Concept | Key Points |
|---------|------------|
| **What** | Opinionated dependency descriptors |
| **Why** | Simplified dependency management, version compatibility |
| **Common Starters** | web, data-jpa, security, actuator, test |
| **Naming** | `spring-boot-starter-*` (official), `*-spring-boot-starter` (3rd party) |
| **Custom Starters** | Auto-configuration + starter POM |
| **Best Practices** | Use starters, appropriate scopes, minimal dependencies |

Spring Boot starters are the foundation of rapid application development in Spring Boot, eliminating dependency hell and configuration complexity.

---

**Next**: [REST API Development](../04-rest-api-development/)
