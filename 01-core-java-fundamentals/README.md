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
- ✅ Apply these concepts in Spring Boot context

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

## ⏱️ Estimated Time

**Total: 4-5 days** (with hands-on practice)

- Collections Framework: 1 day
- Streams and Lambdas: 1 day
- Functional Interfaces: 0.5 day
- Concurrency & Multithreading: 1-1.5 days
- JVM Internals: 0.5-1 day

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
Concurrency (Handle parallel processing)
    ↓
JVM Internals (Understand what runs your code)
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

## 🔗 Next Steps

Once you complete this module, proceed to:
- **[Module 02: Spring Core](../02-spring-core/)** - Learn Dependency Injection and Spring fundamentals

---

**Ready to begin? Start with [Collections Framework →](01-collections-framework/)**
