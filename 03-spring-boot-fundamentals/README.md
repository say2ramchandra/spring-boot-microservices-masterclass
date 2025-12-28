# Module 03: Spring Boot Fundamentals

> **Learn to build production-ready applications with Spring Boot**

## 📚 Module Overview

Spring Boot takes the complexity out of Spring Framework development with auto-configuration, embedded servers, and production-ready features. This module teaches you everything you need to build modern REST APIs and data-driven applications.

## 🎯 Learning Objectives

By the end of this module, you will:

- ✅ Understand Spring Boot auto-configuration
- ✅ Manage configuration with properties/YAML files
- ✅ Build RESTful APIs with proper architecture
- ✅ Implement data persistence with Spring Data JPA
- ✅ Handle exceptions globally
- ✅ Implement validation and error handling
- ✅ Add logging and monitoring

## 📖 Module Contents

### 1. [Auto-Configuration](01-auto-configuration/)
- What is auto-configuration?
- @SpringBootApplication explained
- Creating custom auto-configuration
- Conditional beans
- Excluding auto-configuration

### 2. [Configuration Management](02-configuration-management/)
- application.properties vs application.yml
- Profile-specific configurations
- @Value and @ConfigurationProperties
- Externalized configuration
- Configuration precedence

### 3. [Starter Dependencies](03-starter-dependencies/)
- What are starters?
- Common starters (web, data-jpa, security)
- Creating custom starters
- Dependency management

### 4. [REST API Development](04-rest-api-development/)
- REST principles
- @RestController and @RequestMapping
- HTTP methods (GET, POST, PUT, DELETE, PATCH)
- Request/Response handling
- Content negotiation
- HATEOAS

### 5. [Data Access with JPA](05-data-access-jpa/)
- JPA vs JDBC
- Entity relationships
- Spring Data repositories
- Query methods
- Transactions
- Database initialization

### 6. [Exception Handling](06-exception-handling/)
- @ControllerAdvice
- @ExceptionHandler
- Custom exceptions
- Error response structure
- Global error handling

### 7. [Logging Strategies](07-logging-strategies/)
- SLF4J + Logback
- Log levels
- Structured logging
- Log aggregation
- Best practices

## ⏱️ Estimated Time

**Total: 5-7 days** (with hands-on practice)

- Auto-Configuration: 1 day
- Configuration Management: 1 day
- Starter Dependencies: 0.5 day
- REST API Development: 2 days
- Data Access with JPA: 1.5 days
- Exception Handling: 0.5 day
- Logging Strategies: 0.5 day

## 🚀 Getting Started

### Prerequisites
- Completed Module 02 (Spring Core)
- Java 17+
- Maven 3.8+
- Postman or cURL for testing APIs

### Quick Start
```bash
cd 03-spring-boot-fundamentals
cd 04-rest-api-development
cd demo-complete-rest-api
mvn spring-boot:run
```

## 🎓 Learning Path

```
Start Here
    ↓
Auto-Configuration (Understand the magic)
    ↓
Configuration Management (External config)
    ↓
Starter Dependencies (Quick project setup)
    ↓
REST API Development (Build APIs)
    ↓
Data Access with JPA (Persist data)
    ↓
Exception Handling (Error management)
    ↓
Logging (Observability)
    ↓
Ready for Microservices! →
```

## 💡 Why These Topics?

### Auto-Configuration
**Spring Boot's killer feature!** It:
- Automatically configures beans based on classpath
- Eliminates 90% of configuration boilerplate
- Provides sensible defaults
- Can be customized when needed

### Configuration Management
**Externalize settings** for:
- Different environments (dev, staging, prod)
- Feature flags
- Database connections
- API keys
- Without rebuilding the application!

### REST API Development
**The foundation of microservices:**
- Services communicate via REST APIs
- Frontend applications consume REST APIs
- Mobile apps integrate via REST
- Third-party integrations use REST

### Data Access with JPA
**Simplified database access:**
- No boilerplate JDBC code
- Object-relational mapping
- Query generation from method names
- Transaction management

### Exception Handling
**Professional error management:**
- Consistent error responses
- Client-friendly error messages
- Proper HTTP status codes
- Centralized exception handling

## 📊 Spring Boot vs Spring Framework

| Aspect | Spring Framework | Spring Boot |
|--------|------------------|-------------|
| **Configuration** | Extensive XML/Java config | Auto-configuration |
| **Dependency Management** | Manual versions | Starter dependencies |
| **Server** | External (Tomcat, Jetty) | Embedded server |
| **Production Features** | Add manually | Built-in (Actuator) |
| **Setup Time** | Hours | Minutes |
| **Boilerplate** | Significant | Minimal |

**Key Insight**: Spring Boot doesn't replace Spring - it simplifies it!

## 🏗️ Typical Spring Boot Application Structure

```
my-spring-boot-app/
├── src/main/java/com/example/app/
│   ├── MyApplication.java           # Main class
│   ├── config/                      # Configuration classes
│   ├── controller/                  # REST controllers
│   ├── service/                     # Business logic
│   ├── repository/                  # Data access
│   ├── model/                       # Domain entities
│   ├── dto/                         # Data transfer objects
│   └── exception/                   # Custom exceptions
├── src/main/resources/
│   ├── application.yml              # Configuration
│   ├── application-dev.yml          # Dev config
│   ├── application-prod.yml         # Prod config
│   └── data.sql                     # Initial data
├── src/test/java/                   # Tests
└── pom.xml                          # Dependencies
```

## 📝 Practice Tips

1. **Build incrementally** - Start with a simple API, add features one by one
2. **Test with Postman** - Create a collection of API requests
3. **Read auto-configuration reports** - Run with `--debug` to see what Spring Boot configures
4. **Experiment with properties** - Change configurations and observe behavior
5. **Use Spring Initializr** - Generate projects quickly at start.spring.io

## ✅ Self-Assessment

After completing this module, you should be able to:

- [ ] Create a Spring Boot project from scratch
- [ ] Build a complete CRUD REST API
- [ ] Configure application for different environments
- [ ] Implement data persistence with JPA
- [ ] Handle exceptions gracefully
- [ ] Add comprehensive logging
- [ ] Explain Spring Boot auto-configuration
- [ ] Deploy a Spring Boot application

## 🎯 Sample Project: Task Management API

By the end of this module, you'll build a complete Task Management REST API:

**Features**:
- ✅ CRUD operations for tasks
- ✅ Task categories
- ✅ User authentication
- ✅ Data validation
- ✅ Exception handling
- ✅ Database persistence
- ✅ Logging
- ✅ API documentation (Swagger)

**Tech Stack**:
- Spring Boot 3.2
- Spring Data JPA
- H2 Database (in-memory)
- Lombok
- Validation API

## 🔗 Next Steps

Once you complete this module, proceed to:
- **[Module 04: Microservices Architecture](../04-microservices-architecture/)** - Build distributed systems

---

**Ready to begin? Start with [Auto-Configuration →](01-auto-configuration/)**

_Spring Boot: From zero to hero in minutes! ⚡_
