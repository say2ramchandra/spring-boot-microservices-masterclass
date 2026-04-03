# Module 01: Core Java Fundamentals

> **Master modern Java features essential for Spring Boot development**

## 📚 Module Overview

Before diving into Spring Boot and Microservices, it's crucial to have a solid understanding of modern Java features. This module covers the Java fundamentals that you'll use daily in Spring Boot development.

## 🎯 Learning Objectives

By the end of this module, you will:

- ✅ Master Java Collections Framework for data management
- ✅ Write functional code using Streams and Lambdas
- ✅ Understand and implement Functional Interfaces
- ✅ Handle concurrency and multithreading scenarios
- ✅ Understand JVM internals (GC, ClassLoaders, Memory, JIT)
- ✅ Use Generics for type-safe code
- ✅ Handle exceptions properly with try-with-resources
- ✅ Understand Reflection and Annotations (how Spring works)
- ✅ Master Optional API for null safety
- ✅ Use modern Date/Time API (java.time)
- ✅ Apply modern Java features (Records, Sealed Classes, Pattern Matching)
- ✅ Use Virtual Threads for high-throughput applications (Java 21)
- ✅ Serialize data with Java and Jackson JSON
- ✅ Handle file I/O with modern NIO APIs
- ✅ Quick revision of all core concepts

## 📖 Module Contents

### 1. [Collections Framework](01-collections-framework/) ✅
- List, Set, Map interfaces and implementations
- When to use which collection
- Performance characteristics
- Common operations and best practices
- **Demos:** ArrayList basics, HashMap cache implementation

### 2. [Streams and Lambdas](02-streams-and-lambdas/) ✅
- Lambda expressions syntax
- Stream API operations (filter, map, reduce)
- Parallel streams
- Real-world data processing
- **Demo:** Comprehensive stream operations

### 3. [Functional Interfaces](03-functional-interfaces/) ✅
- Predicate, Function, Consumer, Supplier
- BiFunction, BiPredicate, BiConsumer
- UnaryOperator, BinaryOperator
- Custom functional interfaces
- Method references (4 types)
- Function composition and chaining
- **Demo:** All functional interfaces with real-world examples

### 4. [Concurrency & Multithreading](04-concurrency-multithreading/) ✅
- Thread basics and lifecycle
- Synchronization and race conditions
- **Java Memory Model (JMM)** - visibility, happens-before, volatile
- Locks and atomic operations
- ExecutorService and Thread Pools
- CompletableFuture async programming
- Concurrent collections
- Producer-Consumer pattern
- Real-world parallel processing
- **Demo:** Complete concurrency patterns

### 5. [JVM Internals](05-jvm-internals/) ✅
- JVM Architecture overview
- Memory Areas: Heap, Stack, Metaspace
- Garbage Collection: Serial, Parallel, G1, ZGC
- ClassLoader subsystem and delegation
- JIT Compilation and optimizations
- JVM Tuning and monitoring
- **Demo:** GC behavior, ClassLoader hierarchy, Memory analysis, JIT effects

### 6. [Generics](06-generics/) ✅ 🆕
- Generic classes and methods
- Bounded type parameters (`extends`, `super`)
- Wildcards and PECS principle
- Type erasure
- Generic Repository pattern (like Spring Data)
- **Demo:** Comprehensive generics with real-world patterns

### 7. [Exception Handling](07-exception-handling/) ✅ 🆕
- Checked vs unchecked exceptions
- Try-with-resources (AutoCloseable)
- Custom exception hierarchies
- Exception chaining
- Spring Boot @ExceptionHandler integration
- **Demo:** Complete exception handling patterns

### 8. [Reflection & Annotations](08-reflection-annotations/) ✅ 🆕
- Reflection API basics
- Inspecting classes, fields, methods
- Creating instances dynamically
- Custom annotations
- Runtime annotation processing
- How Spring uses reflection (mini DI framework)
- **Demo:** Building a simple dependency injection container

### 9. [Optional API](09-optional-handling/) ✅ 🆕
- Creating and using Optional
- map, flatMap, filter operations
- orElse vs orElseGet vs orElseThrow
- Combining Optionals
- Spring Data/JPA integration
- **Demo:** Null-safe programming patterns

### 10. [Date/Time API](10-datetime-api/) ✅ 🆕
- LocalDate, LocalTime, LocalDateTime
- ZonedDateTime and time zones
- Instant for machine timestamps
- Duration and Period
- Formatting and parsing
- **Demo:** Complete date/time handling

### 11. [Modern Java Features](11-modern-java-features/) ✅ 🆕
- **Records** - Immutable data classes (DTOs)
- **Sealed Classes** - Controlled inheritance
- **Pattern Matching** - instanceof and switch
- **Switch Expressions** - Cleaner switch syntax
- **Text Blocks** - Multi-line strings
- **var** - Local variable type inference
- **Demo:** All modern features with Spring Boot patterns

### 12. [Virtual Threads](12-virtual-threads/) ✅ 🆕
- **Project Loom** - Lightweight threads (Java 21)
- Platform vs Virtual threads
- Executors for virtual threads
- Structured concurrency
- Spring Boot 3.2+ integration
- **Demo:** Performance comparison, high-throughput I/O

### 13. [Serialization](13-serialization/) ✅ 🆕
- Java Serialization (Serializable interface)
- `transient` keyword and custom serialization
- `serialVersionUID` - version control
- **Jackson JSON serialization** (Spring Boot standard)
- Records serialization
- **Demo:** Complete serialization patterns

### 14. [I/O and NIO](14-io-nio/) ✅ 🆕
- **java.nio.file** - Modern file operations
- Path API and Files utility class
- NIO Channels and Buffers
- Memory-mapped files, File watching
- Spring Resource abstraction
- **Demo:** File operations, performance comparison

### 15. [Quick Revision Guide](15-quick-revision/) ✅ 🆕
- **All-in-one reference** for quick revision
- OOP, Keywords, String, Object methods
- Collections, Generics, Memory & GC
- Serialization, Cloning, Immutability
- Inner classes, Enums, Multithreading
- Java 8+ features, Design patterns
- **Format:** Concise tables and code snippets

## ⏱️ Estimated Time

**Total: 9-10 days** (with hands-on practice)

| Topic | Duration |
|-------|----------|
| Collections Framework | 1 day |
| Streams and Lambdas | 1 day |
| Functional Interfaces | 0.5 day |
| Concurrency & Multithreading | 1-1.5 days |
| JVM Internals | 0.5-1 day |
| Generics | 0.5 day |
| Exception Handling | 0.5 day |
| Reflection & Annotations | 0.5 day |
| Optional API | 0.25 day |
| Date/Time API | 0.5 day |
| Modern Java Features | 0.5 day |
| Virtual Threads | 0.5 day |
| Serialization | 0.5 day |
| I/O and NIO | 0.5 day |
| Quick Revision | 0.5 day (review) |

## 🚀 Getting Started

### Prerequisites
- Java 17+ installed
- Maven 3.8+ installed
- Your favorite IDE

### Quick Start
```bash
cd 01-core-java-fundamentals
cd 01-collections-framework
# Read the README.md, then run demos
```

## 🎓 Learning Path

```
Start Here
    ↓
Collections Framework (Understand data structures)
    ↓
Streams & Lambdas (Functional programming)
    ↓
Functional Interfaces (Deep dive into functional paradigm)
    ↓
Generics (Type-safe programming)
    ↓
Exception Handling (Robust error handling)
    ↓
Optional API (Null safety)
    ↓
Date/Time API (Modern date handling)
    ↓
Reflection & Annotations (How Spring works)
    ↓
Serialization & I/O (Data persistence)
    ↓
Concurrency (Handle parallel processing)
    ↓
Virtual Threads (Java 21 lightweight concurrency)
    ↓
JVM Internals (Understand what runs your code)
    ↓
Modern Java Features (Records, Sealed Classes, etc.)
    ↓
📋 Quick Revision (Review all concepts)
    ↓
Ready for Spring Core! →
```

## 💡 Why These Topics?

### Collections Framework
**In Spring Boot, you'll use:**
- `List` for method parameters and return types
- `Set` for unique constraint handling
- `Map` for configuration properties and caching

### Streams & Lambdas
**Spring Boot heavily uses:**
- Lambda expressions in configuration
- Stream processing for data transformation
- Functional programming patterns

### Functional Interfaces
**You'll see them in:**
- Repository method signatures
- Event handling
- Async processing

### Generics
**Essential for:**
- Spring Data repositories (`JpaRepository<T, ID>`)
- Generic service layers
- Type-safe REST controllers

### Exception Handling
**Critical for:**
- `@ExceptionHandler` and `@ControllerAdvice`
- Resource management with try-with-resources
- Custom application exceptions

### Reflection & Annotations
**How Spring works:**
- `@Autowired` dependency injection
- `@Component` scanning
- `@RequestMapping` URL resolution
- Understanding Spring's "magic"

### Optional API
**Used everywhere in:**
- Spring Data `findById()` returns Optional
- Null-safe coding practices
- Fluent API design

### Date/Time API
**You'll need for:**
- Entity timestamps
- Scheduling (`@Scheduled`)
- API date formatting
- Time zone handling

### Modern Java Features
**Spring Boot 3.x benefits:**
- Records as DTOs
- Sealed classes for domain modeling
- Pattern matching for cleaner code
- Text blocks for SQL/JSON

### Concurrency
**Essential for:**
- Async REST APIs (`@Async`)
- Scheduled tasks (`@Scheduled`)
- Reactive programming
- Performance optimization

### JVM Internals
**Critical for:**
- Production troubleshooting (GC pauses, OOM)
- Performance tuning (heap sizing, GC selection)
- Understanding Spring Boot's startup and classloading
- Memory leak diagnosis

## 📝 Practice Tips

1. **Type the code yourself** - Don't copy-paste
2. **Experiment** - Change values, break things, fix them
3. **Time yourself** - Can you solve problems without hints?
4. **Relate to Spring** - Think how each concept applies to Spring Boot

## ✅ Self-Assessment

After completing this module, you should be able to:

- [ ] Choose the right collection for any scenario
- [ ] Transform data using Stream API fluently
- [ ] Write clean functional code with lambdas
- [ ] Implement thread-safe code
- [ ] Explain JMM visibility and happens-before rules
- [ ] Tune JVM for different workloads
- [ ] Diagnose memory and GC issues
- [ ] Explain performance implications of your choices

---

## Test Your Knowledge

### Q1: What's the difference between ArrayList and LinkedList?
**A:** ArrayList uses dynamic array (fast random access O(1), slow insert/delete O(n)). LinkedList uses doubly-linked list (slow random access O(n), fast insert/delete O(1) at ends).

### Q2: When should you use Stream's parallel() method?
**A:** For CPU-intensive operations on large datasets (>10,000 elements) without shared mutable state. Avoid for I/O operations or small collections.

### Q3: What is the difference between Runnable and Callable?
**A:** Runnable's run() returns void and can't throw checked exceptions. Callable's call() returns a value and can throw exceptions.

### Q4: Explain the Java Memory Model's "happens-before" relationship.
**A:** Guarantees that memory writes by one thread are visible to reads in another thread. Established by synchronized blocks, volatile variables, and thread start/join.

### Q5: What triggers a Full GC vs Minor GC?
**A:** Minor GC: Eden space full (cleans Young Gen). Full GC: Old Gen full, Metaspace full, or explicit System.gc() call.

---

## 🔗 Next Steps

Once you complete this module, proceed to:
- **[Module 02: Spring Core](../02-spring-core/)** - Learn Dependency Injection and Spring fundamentals

---

**Ready to begin? Start with [Collections Framework →](01-collections-framework/)**
