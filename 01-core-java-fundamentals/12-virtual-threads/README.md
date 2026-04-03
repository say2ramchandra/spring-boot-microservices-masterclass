# Virtual Threads (Project Loom)

> **Revolutionary concurrency model for high-throughput applications**

## 📚 Table of Contents

1. [Introduction](#introduction)
2. [Platform vs Virtual Threads](#platform-vs-virtual-threads)
3. [Creating Virtual Threads](#creating-virtual-threads)
4. [Virtual Thread Executors](#virtual-thread-executors)
5. [Structured Concurrency](#structured-concurrency)
6. [Best Practices](#best-practices)
7. [Spring Boot Integration](#spring-boot-integration)
8. [When to Use](#when-to-use)
9. [Pitfalls to Avoid](#pitfalls-to-avoid)

---

## Introduction

### What are Virtual Threads?

**Virtual Threads** (introduced in Java 21 as part of Project Loom) are lightweight threads managed by the JVM rather than the operating system. They enable writing high-throughput concurrent applications using the simple "thread-per-request" model.

### The Problem with Platform Threads

```java
// Traditional approach - limited by OS threads
ExecutorService executor = Executors.newFixedThreadPool(200);
// Can only handle ~200 concurrent requests!
// Creating more threads is expensive (1MB stack each)
```

### The Virtual Thread Solution

```java
// Virtual threads - millions possible!
ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
// Each request gets its own virtual thread
// JVM manages scheduling onto platform threads
```

### Key Benefits

| Aspect | Platform Threads | Virtual Threads |
|--------|------------------|-----------------|
| **Memory** | ~1MB per thread | ~1KB per thread |
| **Count** | Thousands max | Millions possible |
| **Creation cost** | Expensive | Cheap |
| **Context switch** | OS-managed (slow) | JVM-managed (fast) |
| **Programming model** | Complex (async/reactive) | Simple (blocking) |

---

## Platform vs Virtual Threads

### Platform Threads (Traditional)

```java
// Platform thread - tied to OS thread
Thread platformThread = new Thread(() -> {
    System.out.println("Running on: " + Thread.currentThread());
});
platformThread.start();
// Output: Thread[Thread-0,5,main]
```

### Virtual Threads

```java
// Virtual thread - lightweight, JVM-managed
Thread virtualThread = Thread.ofVirtual().start(() -> {
    System.out.println("Running on: " + Thread.currentThread());
});
// Output: VirtualThread[#21]/runnable@ForkJoinPool-1-worker-1
```

### How Virtual Threads Work

```
┌─────────────────────────────────────────────────────────────┐
│                         JVM                                  │
│                                                              │
│   ┌─────────────────────────────────────────────────────┐   │
│   │            Virtual Thread Scheduler                  │   │
│   │                                                      │   │
│   │   VT1  VT2  VT3  VT4  VT5  VT6  ...  VT1000000     │   │
│   │    │    │    │    │    │    │           │          │   │
│   └────┼────┼────┼────┼────┼────┼───────────┼──────────┘   │
│        │    │    │    │    │    │           │              │
│        └────┴────┴────┴────┴────┴───────────┘              │
│                        │                                    │
│                        ▼                                    │
│   ┌─────────────────────────────────────────────────────┐   │
│   │         Carrier Threads (ForkJoinPool)              │   │
│   │                                                      │   │
│   │      PT1        PT2        PT3        PT4           │   │
│   │   (OS Thread) (OS Thread) (OS Thread) (OS Thread)   │   │
│   └─────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

When a virtual thread **blocks** (I/O, sleep, lock), it's **unmounted** from its carrier thread, allowing another virtual thread to run.

---

## Creating Virtual Threads

### Thread.ofVirtual()

```java
// Start immediately
Thread vt = Thread.ofVirtual().start(() -> {
    System.out.println("Hello from virtual thread!");
});

// Create without starting
Thread vt = Thread.ofVirtual().unstarted(() -> {
    System.out.println("Hello!");
});
vt.start();

// With name
Thread vt = Thread.ofVirtual()
    .name("my-virtual-thread")
    .start(() -> System.out.println("Named thread!"));
```

### Thread.startVirtualThread()

```java
// Convenience method - creates and starts
Thread vt = Thread.startVirtualThread(() -> {
    System.out.println("Quick start!");
});
```

### Checking Thread Type

```java
Thread current = Thread.currentThread();

if (current.isVirtual()) {
    System.out.println("Running on virtual thread");
} else {
    System.out.println("Running on platform thread");
}
```

---

## Virtual Thread Executors

### newVirtualThreadPerTaskExecutor()

```java
// Best for I/O-bound tasks - creates new virtual thread per task
try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
    
    // Submit 10,000 tasks - no problem!
    List<Future<String>> futures = new ArrayList<>();
    for (int i = 0; i < 10_000; i++) {
        int taskId = i;
        futures.add(executor.submit(() -> {
            Thread.sleep(1000);  // Simulate I/O
            return "Task " + taskId + " done";
        }));
    }
    
    // Collect results
    for (Future<String> future : futures) {
        System.out.println(future.get());
    }
}
// ExecutorService auto-closed (try-with-resources)
```

### Custom Virtual Thread Factory

```java
// Create a factory for named virtual threads
ThreadFactory factory = Thread.ofVirtual()
    .name("worker-", 0)  // worker-0, worker-1, worker-2, ...
    .factory();

try (ExecutorService executor = Executors.newThreadPerTaskExecutor(factory)) {
    executor.submit(() -> {
        System.out.println(Thread.currentThread().getName());
        // Output: worker-0
    });
}
```

### Comparing Throughput

```java
// Platform threads - limited scalability
void testPlatformThreads() throws Exception {
    long start = System.currentTimeMillis();
    
    try (ExecutorService executor = Executors.newFixedThreadPool(100)) {
        List<Future<?>> futures = new ArrayList<>();
        for (int i = 0; i < 10_000; i++) {
            futures.add(executor.submit(() -> {
                Thread.sleep(100);  // Simulate blocking I/O
                return null;
            }));
        }
        for (Future<?> f : futures) f.get();
    }
    
    System.out.println("Platform threads: " + (System.currentTimeMillis() - start) + "ms");
    // ~10,000ms (100 threads processing 10,000 tasks)
}

// Virtual threads - massive scalability
void testVirtualThreads() throws Exception {
    long start = System.currentTimeMillis();
    
    try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
        List<Future<?>> futures = new ArrayList<>();
        for (int i = 0; i < 10_000; i++) {
            futures.add(executor.submit(() -> {
                Thread.sleep(100);  // Simulate blocking I/O
                return null;
            }));
        }
        for (Future<?> f : futures) f.get();
    }
    
    System.out.println("Virtual threads: " + (System.currentTimeMillis() - start) + "ms");
    // ~200ms (all 10,000 tasks run concurrently!)
}
```

---

## Structured Concurrency

### Preview Feature (Java 21+)

Structured concurrency ensures that concurrent tasks have a clear lifecycle and proper error handling.

```java
// Note: --enable-preview required
import java.util.concurrent.StructuredTaskScope;

// Fetch data from multiple services concurrently
record UserData(User user, List<Order> orders) {}

UserData fetchUserData(long userId) throws Exception {
    try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
        
        // Fork concurrent tasks
        Subtask<User> userTask = scope.fork(() -> 
            userService.getUser(userId));
        Subtask<List<Order>> ordersTask = scope.fork(() -> 
            orderService.getOrders(userId));
        
        // Wait for all tasks
        scope.join();           // Wait for completion
        scope.throwIfFailed();  // Propagate exceptions
        
        // All succeeded - combine results
        return new UserData(userTask.get(), ordersTask.get());
    }
}
```

### ShutdownOnSuccess

```java
// Return first successful result, cancel others
try (var scope = new StructuredTaskScope.ShutdownOnSuccess<String>()) {
    
    // Race between multiple data sources
    scope.fork(() -> fetchFromPrimaryDB());
    scope.fork(() -> fetchFromReplicaDB());
    scope.fork(() -> fetchFromCache());
    
    scope.join();
    
    // Get first successful result
    String result = scope.result();  
}
```

---

## Best Practices

### ✅ DO

```java
// 1. Use for I/O-bound workloads
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    executor.submit(() -> {
        // Database call
        // HTTP request  
        // File I/O
    });
}

// 2. Use try-with-resources for ExecutorService
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    // Submit tasks
} // Auto-shutdown and wait for completion

// 3. Keep blocking operations simple
Thread.startVirtualThread(() -> {
    String result = httpClient.get(url);  // Blocking is OK!
    process(result);
});

// 4. Use thread-local carefully (prefer scoped values)
// Thread locals work but are copied on virtual thread creation
```

### ❌ DON'T

```java
// 1. Don't pool virtual threads - they're cheap!
ExecutorService pool = Executors.newFixedThreadPool(10);  // ❌ Don't pool
ExecutorService vt = Executors.newVirtualThreadPerTaskExecutor();  // ✅ Per-task

// 2. Don't use for CPU-bound work
Thread.startVirtualThread(() -> {
    // Heavy computation ❌ - use platform threads
    while (true) {
        compute();  
    }
});

// 3. Don't hold locks during blocking operations (pinning!)
synchronized (lock) {
    Thread.sleep(1000);  // ❌ Pins virtual thread to carrier
}

// 4. Don't use with native code that expects specific thread
Thread.startVirtualThread(() -> {
    nativeLibrary.threadSpecificCall();  // ❌ May not work
});
```

### Avoiding Thread Pinning

```java
// ❌ Bad - synchronized pins virtual thread
synchronized (lock) {
    blockingOperation();
}

// ✅ Good - ReentrantLock doesn't pin
private final ReentrantLock lock = new ReentrantLock();

lock.lock();
try {
    blockingOperation();
} finally {
    lock.unlock();
}
```

---

## Spring Boot Integration

### Enable Virtual Threads (Spring Boot 3.2+)

```yaml
# application.yml
spring:
  threads:
    virtual:
      enabled: true
```

Or in `application.properties`:
```properties
spring.threads.virtual.enabled=true
```

### What it Enables

- **Tomcat** uses virtual threads for request handling
- **@Async** methods run on virtual threads
- **Spring WebClient** blocking calls are efficient

### Controller Example

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        // With virtual threads enabled:
        // - This runs on a virtual thread
        // - Blocking calls are fine!
        
        User user = userRepository.findById(id);  // Blocking DB call
        enrichWithExternalData(user);              // Blocking HTTP call
        
        return user;
    }
    
    private void enrichWithExternalData(User user) {
        // These blocking calls are efficient with virtual threads
        String address = addressService.getAddress(user.getId());
        List<Order> orders = orderService.getOrders(user.getId());
        user.setAddress(address);
        user.setOrders(orders);
    }
}
```

### Custom Virtual Thread Executor

```java
@Configuration
public class AsyncConfig {
    
    @Bean
    public Executor taskExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}

@Service
public class UserService {
    
    @Async
    public CompletableFuture<User> findUserAsync(Long id) {
        // Runs on virtual thread
        User user = repository.findById(id);
        return CompletableFuture.completedFuture(user);
    }
}
```

### Comparison: Before and After

```java
// BEFORE: Reactive/WebFlux (complex)
@GetMapping("/{id}")
public Mono<User> getUser(@PathVariable Long id) {
    return userRepository.findById(id)
        .flatMap(user -> 
            Mono.zip(
                addressService.getAddress(user.getId()),
                orderService.getOrders(user.getId())
            ).map(tuple -> {
                user.setAddress(tuple.getT1());
                user.setOrders(tuple.getT2());
                return user;
            })
        );
}

// AFTER: Virtual threads (simple!)
@GetMapping("/{id}")
public User getUser(@PathVariable Long id) {
    User user = userRepository.findById(id);
    user.setAddress(addressService.getAddress(user.getId()));
    user.setOrders(orderService.getOrders(user.getId()));
    return user;
}
```

---

## When to Use

### ✅ Use Virtual Threads For

| Use Case | Why |
|----------|-----|
| **Web servers** | Handle millions of concurrent requests |
| **Database access** | Blocking JDBC calls become efficient |
| **HTTP clients** | Blocking calls without thread exhaustion |
| **File I/O** | Concurrent file operations |
| **Message processing** | High-throughput message consumers |

### ❌ Use Platform Threads For

| Use Case | Why |
|----------|-----|
| **CPU-intensive work** | Virtual threads don't help, may hurt |
| **Real-time systems** | Need predictable scheduling |
| **Native code integration** | Thread affinity requirements |
| **Synchronized blocks** | Risk of pinning |

---

## Pitfalls to Avoid

### 1. Thread Pinning

```java
// Monitor this with JFR events
// jdk.VirtualThreadPinned

// Check if thread is pinned
Thread.currentThread().isVirtual();  // true
// Pinning occurs when:
// - Inside synchronized block during blocking
// - Inside native method
```

### 2. ThreadLocal Overhead

```java
// Thread locals are inherited but copied
// Each virtual thread gets its own copy
ThreadLocal<ExpensiveObject> tl = ThreadLocal.withInitial(ExpensiveObject::new);

Thread.startVirtualThread(() -> {
    tl.get();  // Creates new ExpensiveObject for each virtual thread!
});

// Better: Use ScopedValue (preview)
static final ScopedValue<User> CURRENT_USER = ScopedValue.newInstance();

ScopedValue.where(CURRENT_USER, user).run(() -> {
    // CURRENT_USER available without copying
});
```

### 3. Pool Sizing Mistakes

```java
// ❌ Wrong - defeats purpose of virtual threads
ExecutorService wrong = Executors.newFixedThreadPool(100);

// ✅ Right - unlimited virtual threads
ExecutorService right = Executors.newVirtualThreadPerTaskExecutor();
```

---

## Demo: Run the Example

```bash
# Requires Java 21+
cd demo-virtual-threads
mvn compile exec:java -Dexec.mainClass="com.example.VirtualThreadsDemo"
```

## Key Takeaways

1. **Virtual threads are cheap** - Create millions, don't pool
2. **Write blocking code** - It's efficient now!
3. **I/O-bound workloads** - Perfect use case
4. **Avoid synchronization** - Use ReentrantLock instead
5. **Spring Boot 3.2+** - One property to enable
6. **Simplify code** - Replace reactive with blocking
