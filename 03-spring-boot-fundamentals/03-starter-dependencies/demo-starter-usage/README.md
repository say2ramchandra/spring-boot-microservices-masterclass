# Demo: Spring Boot Starter Dependencies

This demo showcases how Spring Boot starters simplify dependency management and auto-configuration.

## What This Demo Shows

1. **Multiple Starters Working Together**
   - `spring-boot-starter-web`: REST API with JSON support
   - `spring-boot-starter-data-jpa`: Database access with Hibernate
   - `spring-boot-starter-validation`: Bean validation
   - `spring-boot-starter-actuator`: Production monitoring

2. **Auto-Configuration in Action**
   - Embedded Tomcat server
   - DataSource and connection pooling (HikariCP)
   - JPA and transaction management
   - JSON serialization (Jackson)

3. **Dependency Analysis**
   - What each starter provides
   - Transitive dependencies
   - Bean auto-configuration

## Project Structure

```
demo-starter-usage/
├── src/main/java/com/masterclass/starter/
│   ├── StarterDemoApp.java              # Main application
│   ├── controller/
│   │   └── DemoController.java          # REST controller (web starter)
│   ├── model/
│   │   └── Product.java                 # JPA entity (data-jpa starter)
│   └── repository/
│       └── ProductRepository.java       # JPA repository (data-jpa starter)
└── src/main/resources/
    └── application.properties            # Configuration
```

## Running the Demo

### Prerequisites
- Java 17 or higher
- Maven 3.6+

### Start the Application

```bash
cd demo-starter-usage
mvn clean install
mvn spring-boot:run
```

The application starts on **http://localhost:8080**

## Expected Output

```
================================================================================
SPRING BOOT STARTER DEPENDENCIES DEMO
================================================================================

--- Part 1: Starters in Use ---

This application uses the following Spring Boot starters:
  1. spring-boot-starter
     → Core Spring Boot features, logging, YAML support
  2. spring-boot-starter-web
     → Spring MVC, embedded Tomcat, JSON support (Jackson)
  3. spring-boot-starter-data-jpa
     → Hibernate, Spring Data JPA, transaction management
  4. spring-boot-starter-validation
     → Hibernate Validator for bean validation
  5. spring-boot-starter-actuator
     → Production-ready monitoring and management endpoints

--- Part 2: Auto-Configured Beans from Starters ---

From spring-boot-starter-web:
  ✓ dispatcherServlet: DispatcherServlet for handling HTTP requests
  ✓ requestMappingHandlerMapping: Maps @RequestMapping to handlers
  ✓ jacksonObjectMapper: JSON serialization/deserialization

From spring-boot-starter-data-jpa:
  ✓ dataSource: Database connection pool (HikariCP)
  ✓ entityManagerFactory: JPA EntityManager factory
  ✓ transactionManager: Transaction management

From spring-boot-starter-validation:
  ✓ validator: Bean validation (Hibernate Validator)

From spring-boot-starter-actuator:
  ✓ healthEndpoint: Health check endpoint
  ✓ metricsEndpoint: Metrics collection endpoint

--- Part 3: Actuator Endpoints ---

Available endpoints:
  GET  /actuator/health   - Application health status
  GET  /actuator/info     - Application information
  GET  /actuator/metrics  - Application metrics
  GET  /actuator/beans    - All Spring beans
  GET  /actuator/env      - Environment properties

--- Part 4: Database Auto-Configuration ---

spring-boot-starter-data-jpa automatically configured:
  ✓ DataSource (HikariCP connection pool)
  ✓ EntityManagerFactory (Hibernate)
  ✓ TransactionManager
  ✓ JPA Repositories

Database details:
  Type: H2 (in-memory)
  URL: jdbc:h2:mem:testdb
  Console: http://localhost:8080/h2-console

================================================================================
You can explore:
  - REST API: http://localhost:8080/api/demo
  - H2 Console: http://localhost:8080/h2-console
  - Actuator Health: http://localhost:8080/actuator/health
  - Actuator Beans: http://localhost:8080/actuator/beans
================================================================================
```

## Exploring the Application

### 1. REST API Endpoint

```bash
curl http://localhost:8080/api/demo
```

**Response:**
```json
{
  "message": "Spring Boot Starter Demo",
  "timestamp": "2025-12-17T10:30:00",
  "starters": [
    "spring-boot-starter-web",
    "spring-boot-starter-data-jpa",
    "spring-boot-starter-validation",
    "spring-boot-starter-actuator"
  ],
  "features": [
    "RESTful API (Spring MVC)",
    "JSON serialization (Jackson)",
    "JPA data access (Hibernate)",
    "Bean validation",
    "Production monitoring (Actuator)"
  ]
}
```

### 2. Actuator Endpoints

**Health Check:**
```bash
curl http://localhost:8080/actuator/health
```

**Response:**
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "H2",
        "validationQuery": "isValid()"
      }
    },
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 500107862016,
        "free": 100000000000,
        "threshold": 10485760
      }
    }
  }
}
```

**All Beans:**
```bash
curl http://localhost:8080/actuator/beans
```

Shows all auto-configured beans from starters!

### 3. H2 Database Console

Open in browser: **http://localhost:8080/h2-console**

**Connection details:**
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (leave empty)

Query the Product table:
```sql
SELECT * FROM products;
```

### 4. Metrics

```bash
curl http://localhost:8080/actuator/metrics
```

View specific metric:
```bash
curl http://localhost:8080/actuator/metrics/jvm.memory.used
```

## Analyzing Dependencies

### View Full Dependency Tree

```bash
mvn dependency:tree
```

**Output shows all transitive dependencies:**
```
[INFO] com.masterclass:demo-starter-usage:jar:1.0.0
[INFO] +- org.springframework.boot:spring-boot-starter-web:jar:3.2.0
[INFO] |  +- org.springframework.boot:spring-boot-starter:jar:3.2.0
[INFO] |  |  +- org.springframework.boot:spring-boot:jar:3.2.0
[INFO] |  |  +- org.springframework.boot:spring-boot-autoconfigure:jar:3.2.0
[INFO] |  |  +- org.springframework.boot:spring-boot-starter-logging:jar:3.2.0
[INFO] |  |  |  +- ch.qos.logback:logback-classic:jar:1.4.11
[INFO] |  |  |  +- org.apache.logging.log4j:log4j-to-slf4j:jar:2.21.0
[INFO] |  |  |  \- org.slf4j:jul-to-slf4j:jar:2.0.9
[INFO] |  |  +- jakarta.annotation:jakarta.annotation-api:jar:2.1.1
[INFO] |  |  \- org.yaml:snakeyaml:jar:2.2
[INFO] |  +- org.springframework.boot:spring-boot-starter-json:jar:3.2.0
[INFO] |  |  +- com.fasterxml.jackson.core:jackson-databind:jar:2.15.3
[INFO] |  |  +- com.fasterxml.jackson.datatype:jackson-datatype-jdk8:jar:2.15.3
[INFO] |  |  +- com.fasterxml.jackson.datatype:jackson-datatype-jsr310:jar:2.15.3
[INFO] |  |  \- com.fasterxml.jackson.module:jackson-module-parameter-names:jar:2.15.3
[INFO] |  +- org.springframework.boot:spring-boot-starter-tomcat:jar:3.2.0
[INFO] |  |  +- org.apache.tomcat.embed:tomcat-embed-core:jar:10.1.15
[INFO] |  |  +- org.apache.tomcat.embed:tomcat-embed-el:jar:10.1.15
[INFO] |  |  \- org.apache.tomcat.embed:tomcat-embed-websocket:jar:10.1.15
[INFO] |  +- org.springframework:spring-web:jar:6.1.0
[INFO] |  \- org.springframework:spring-webmvc:jar:6.1.0
[INFO] +- org.springframework.boot:spring-boot-starter-data-jpa:jar:3.2.0
... (and many more)
```

### Count Dependencies

```bash
mvn dependency:tree | grep "spring-boot-starter" | wc -l
```

See how one starter brings in many dependencies!

## What Each Starter Provides

### spring-boot-starter-web

**Provides:**
- ✅ Spring MVC framework
- ✅ Embedded Tomcat server
- ✅ Jackson for JSON
- ✅ HTTP message converters
- ✅ Default error handling

**Without it, you'd need ~15 individual dependencies!**

### spring-boot-starter-data-jpa

**Provides:**
- ✅ Hibernate ORM
- ✅ Spring Data JPA
- ✅ HikariCP connection pool
- ✅ Transaction management
- ✅ JPA implementation

**Saves you from configuring EntityManager, transactions, etc.**

### spring-boot-starter-validation

**Provides:**
- ✅ Hibernate Validator
- ✅ Jakarta Bean Validation API
- ✅ Expression Language support

**Enables @NotBlank, @Size, @Email, etc.**

### spring-boot-starter-actuator

**Provides:**
- ✅ Health checks
- ✅ Metrics collection
- ✅ Application info endpoints
- ✅ Environment details
- ✅ Bean inspection

**Essential for production monitoring!**

## Experiments to Try

### 1. Remove a Starter

Comment out `spring-boot-starter-web` in pom.xml:

```xml
<!--
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
-->
```

**Result**: Application starts but no web server, no REST endpoints!

### 2. Switch Embedded Server

Replace Tomcat with Jetty:

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
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jetty</artifactId>
</dependency>
```

**Result**: Application uses Jetty instead of Tomcat!

### 3. Add More Starters

Add caching:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>
```

Use `@EnableCaching` and `@Cacheable` - it just works!

## Key Takeaways

1. **One starter = Many dependencies**: Each starter bundles related libraries
2. **Version management**: No need to specify versions for managed dependencies
3. **Auto-configuration**: Starters trigger smart configuration based on classpath
4. **Production-ready**: Actuator provides monitoring out of the box
5. **Flexibility**: Easy to swap implementations (Tomcat → Jetty)

## Interview Questions

**Q: What does spring-boot-starter-web include?**
A: Spring MVC, embedded Tomcat, Jackson, validation, error handling

**Q: How do you exclude a transitive dependency?**
A: Use `<exclusions>` in the starter dependency

**Q: What's the difference between dependencies and starters?**
A: Starters are opinionated bundles of dependencies. Regular dependencies are individual libraries.

**Q: Why use spring-boot-starter-parent?**
A: Provides dependency management (versions) and plugin configuration

---

**Related Demos:**
- [Auto-Configuration](../../01-auto-configuration/) - How starters trigger auto-config
- [REST API Development](../../04-rest-api-development/) - Deep dive into web starter
