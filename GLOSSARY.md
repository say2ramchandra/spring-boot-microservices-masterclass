# 📚 Glossary of Terms

> **A comprehensive reference of technical terms used throughout this masterclass**

## A

### **API (Application Programming Interface)**
A set of rules and protocols that allows different software applications to communicate with each other.

### **API Gateway**
A server that acts as an entry point for microservices, handling routing, composition, and protocol translation.

### **AOP (Aspect-Oriented Programming)**
A programming paradigm that increases modularity by allowing separation of cross-cutting concerns (logging, security, transactions).

### **Actuator**
A Spring Boot module that provides production-ready features like health checks, metrics, and monitoring endpoints.

### **Auto-Configuration**
Spring Boot's ability to automatically configure beans based on classpath dependencies and application properties.

### **@Autowired**
Spring annotation used to inject dependencies automatically by type.

## B

### **Bean**
An object that is instantiated, assembled, and managed by the Spring IoC container.

### **Bean Lifecycle**
The series of stages a Spring bean goes through from creation to destruction.

### **Bean Scope**
Defines the lifecycle and visibility of beans: singleton, prototype, request, session, application.

### **Bulkhead**
A resilience pattern that isolates elements of an application into pools to prevent cascading failures.

## C

### **Circuit Breaker**
A design pattern that prevents an application from repeatedly trying to execute an operation that's likely to fail.

### **Container**
A lightweight, standalone, executable package that includes everything needed to run software (code, runtime, libraries).

### **CQRS (Command Query Responsibility Segregation)**
A pattern that separates read and write operations into different models.

### **Configuration Server**
Centralized server that provides configuration properties to distributed systems.

## D

### **Dependency Injection (DI)**
A design pattern where dependencies are provided to a class rather than the class creating them.

### **DTO (Data Transfer Object)**
An object used to transfer data between different layers or services.

### **Docker**
A platform for developing, shipping, and running applications in containers.

### **Distributed Tracing**
The method of tracking requests as they flow through distributed systems.

## E

### **Eureka**
Netflix's service discovery server used in Spring Cloud for registering and locating microservices.

### **Event-Driven Architecture**
A software architecture pattern where events trigger and communicate between decoupled services.

### **Event Sourcing**
A pattern where state changes are stored as a sequence of events.

## F

### **Feign Client**
A declarative REST client that makes writing web service clients easier.

### **Fallback**
An alternative action taken when a primary operation fails.

## G

### **Grafana**
An open-source platform for monitoring and observability with beautiful dashboards.

### **gRPC**
A high-performance RPC (Remote Procedure Call) framework using Protocol Buffers.

## H

### **Health Check**
An endpoint that returns the operational status of an application.

### **Hystrix**
Netflix's circuit breaker library (now in maintenance mode, replaced by Resilience4j).

## I

### **IoC (Inversion of Control)**
A principle where the control of object creation and management is inverted to a framework.

### **Idempotency**
Property where an operation produces the same result regardless of how many times it's executed.

### **Ingress**
Kubernetes resource that manages external access to services in a cluster.

## J

### **JPA (Java Persistence API)**
Java specification for managing relational data in applications.

### **JWT (JSON Web Token)**
A compact, URL-safe means of representing claims between two parties for authentication.

## K

### **Kafka**
A distributed event streaming platform for high-performance data pipelines.

### **Kubernetes (K8s)**
An open-source container orchestration platform for automating deployment, scaling, and management.

## L

### **Load Balancing**
Distributing network traffic across multiple servers to ensure availability and reliability.

### **Logback**
A logging framework that's the successor to Log4j, used with SLF4J.

## M

### **Microservices**
An architectural style that structures an application as a collection of loosely coupled services.

### **Micrometer**
Application metrics facade that supports multiple monitoring systems.

### **Monolith**
A single-tiered software application where all components are combined into a single program.

### **MockMvc**
Spring testing framework for testing Spring MVC controllers.

## O

### **OAuth2**
An authorization framework that enables applications to obtain limited access to user accounts.

### **Observability**
The ability to measure the internal state of a system based on its external outputs.

## P

### **Pod**
The smallest deployable unit in Kubernetes, containing one or more containers.

### **Prometheus**
An open-source monitoring and alerting toolkit.

### **Profiles**
Spring feature that allows different configurations for different environments.

## R

### **REST (Representational State Transfer)**
An architectural style for designing networked applications using HTTP methods.

### **Resilience4j**
A lightweight fault tolerance library inspired by Hystrix.

### **Redis**
An in-memory data structure store used as a database, cache, and message broker.

### **Repository**
A Spring Data abstraction that provides CRUD operations for domain objects.

## S

### **Saga Pattern**
A pattern for managing distributed transactions across microservices.

### **Service Discovery**
The automatic detection of services on a network.

### **Service Mesh**
A dedicated infrastructure layer for handling service-to-service communication.

### **Sleuth**
Spring Cloud's distributed tracing solution that integrates with Zipkin.

### **SLF4J (Simple Logging Facade for Java)**
A logging abstraction that allows plugging in different logging frameworks.

### **Span**
A single operation within a trace in distributed tracing.

### **Spring Boot**
An opinionated framework that simplifies Spring application development.

### **Spring Cloud**
A suite of tools for building cloud-native applications with Spring Boot.

### **Spring Security**
A framework that provides authentication, authorization, and protection against common attacks.

### **Starter**
Pre-configured dependency descriptors in Spring Boot that simplify dependency management.

## T

### **TestContainers**
A Java library that provides lightweight, throwaway instances of databases, message brokers, etc., for testing.

### **Trace**
The complete path of a request through a distributed system.

### **Transaction**
A sequence of operations performed as a single logical unit of work.

## W

### **WebFlux**
Spring's reactive web framework for building non-blocking applications.

### **WebClient**
A non-blocking, reactive client for making HTTP requests.

## Z

### **Zipkin**
A distributed tracing system that helps gather timing data for troubleshooting latency problems.

### **Zuul**
Netflix's API Gateway (now in maintenance mode, replaced by Spring Cloud Gateway).

---

## Acronyms Quick Reference

| Acronym | Full Form | Category |
|---------|-----------|----------|
| AOP | Aspect-Oriented Programming | Design Pattern |
| API | Application Programming Interface | General |
| CQRS | Command Query Responsibility Segregation | Pattern |
| DI | Dependency Injection | Pattern |
| DTO | Data Transfer Object | Pattern |
| IoC | Inversion of Control | Principle |
| JPA | Java Persistence API | Specification |
| JWT | JSON Web Token | Security |
| K8s | Kubernetes | DevOps |
| ORM | Object-Relational Mapping | Technology |
| REST | Representational State Transfer | Architecture |
| SOLID | Single Responsibility, Open-Closed, Liskov Substitution, Interface Segregation, Dependency Inversion | Principles |
| SLF4J | Simple Logging Facade for Java | Logging |

---

_This glossary will be your companion throughout the masterclass. Bookmark this page!_
