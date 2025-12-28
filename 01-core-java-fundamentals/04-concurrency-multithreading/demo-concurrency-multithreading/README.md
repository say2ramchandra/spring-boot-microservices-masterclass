# Concurrency & Multithreading Demo

## Overview
Comprehensive demonstration of Java concurrency, multithreading, synchronization, and asynchronous programming.

## Running the Demo

```bash
cd 04-concurrency-multithreading/demo-concurrency-multithreading
mvn clean compile exec:java
```

## What You'll Learn

### Part 1: Thread Basics
- ✅ Three ways to create threads (extends Thread, implements Runnable, Lambda)
- ✅ Thread methods (start, join, sleep, getName, setPriority)
- ✅ Thread states (NEW, RUNNABLE, RUNNING, TERMINATED)

### Part 2: Race Condition Problem
- ⚠️ Demonstrates why unsynchronized access fails
- ⚠️ Shows lost updates in counter++ operation
- ⚠️ Explains the read-modify-write problem

### Part 3: Synchronization Solutions
- ✅ **Solution 1**: Synchronized methods
- ✅ **Solution 2**: Synchronized blocks
- ✅ **Solution 3**: ReentrantLock
- ✅ **Solution 4**: AtomicInteger (recommended for counters)

### Part 4: Thread Pools
- ✅ Fixed thread pool for bounded tasks
- ✅ Callable and Future for returning results
- ✅ Scheduled tasks (delays and periodic execution)
- ✅ Batch execution with invokeAll()

### Part 5: CompletableFuture
- ✅ Async execution (supplyAsync, runAsync)
- ✅ Chaining operations (thenApply, thenCompose)
- ✅ Combining futures (thenCombine, allOf, anyOf)
- ✅ Error handling (exceptionally, handle)
- ✅ Real-world parallel API calls

### Part 6: Concurrent Collections
- ✅ ConcurrentHashMap - Thread-safe map with atomic operations
- ✅ CopyOnWriteArrayList - Safe iteration during modification
- ✅ ConcurrentLinkedQueue - Lock-free queue

### Part 7: Producer-Consumer Pattern
- ✅ BlockingQueue for thread communication
- ✅ Producer thread generating items
- ✅ Consumer thread processing items
- ✅ Graceful termination

### Part 8: Real-World Scenarios
- ✅ Parallel data processing with thread pools
- ✅ Parallel web downloads (simulated)
- ✅ Rate limiting with Semaphore

## Key Concepts Demonstrated

### 1. Why Synchronization is Needed

```java
// Problem: Race Condition
counter++; // Actually 3 operations!
// 1. Read from memory
// 2. Increment
// 3. Write back

// Multiple threads can interleave these operations
// Result: Lost updates
```

### 2. Synchronization Solutions Comparison

| Solution | Performance | Use Case |
|----------|------------|----------|
| synchronized | Moderate | General purpose |
| ReentrantLock | Moderate | Need tryLock, fairness |
| AtomicInteger | Best | Simple counters |
| ConcurrentHashMap | Good | Concurrent map access |

### 3. Thread Pool Benefits

```
Creating threads is expensive:
- OS resources
- Memory overhead
- Context switching

Thread pools:
✅ Reuse threads
✅ Control resource usage
✅ Better performance
```

### 4. CompletableFuture vs Thread Pools

```java
// Traditional approach
ExecutorService executor = Executors.newFixedThreadPool(3);
Future<String> future = executor.submit(() -> "result");
String result = future.get(); // Blocking

// Modern approach with CompletableFuture
CompletableFuture.supplyAsync(() -> "result")
    .thenApply(String::toUpperCase)
    .thenAccept(System.out::println); // Non-blocking, chainable
```

## Expected Output

The demo prints detailed output for each part:

```
╔══════════════════════════════════════════════╗
║  Concurrency & Multithreading Demo          ║
╚══════════════════════════════════════════════╝

==================================================
PART 1: Thread Basics
==================================================

1. Creating Threads - Three Methods:

   [Extends Thread] Running in: Thread-1
   [Implements Runnable] Running in: Thread-2 (task: Thread-2)
   [Lambda] Running in: Thread-3

2. Thread Methods:

   Thread created: WorkerThread
   Is alive before start: false
   Name: WorkerThread
   Priority: 10
   Is daemon: false
   Is alive after start: true
   Is alive after join: false
...
```

## Performance Comparison

### Sequential vs Parallel
```
Sequential execution: ~1500ms
Parallel with CompletableFuture: ~700ms

Speedup: ~2.14x
```

### Synchronization Overhead
```
No synchronization: Race conditions
synchronized: Thread-safe, moderate overhead
AtomicInteger: Thread-safe, minimal overhead
```

## Best Practices Shown

1. ✅ **Always shut down ExecutorService**
   ```java
   executor.shutdown();
   executor.awaitTermination(60, TimeUnit.SECONDS);
   ```

2. ✅ **Handle InterruptedException properly**
   ```java
   catch (InterruptedException e) {
       Thread.currentThread().interrupt();
   }
   ```

3. ✅ **Use appropriate synchronization**
   - Simple counter → AtomicInteger
   - Complex state → synchronized or Lock
   - Read-heavy → ConcurrentHashMap

4. ✅ **Prefer high-level utilities**
   - Use ExecutorService over raw threads
   - Use CompletableFuture for async
   - Use concurrent collections

5. ✅ **Avoid deadlocks**
   - Acquire locks in consistent order
   - Use tryLock with timeout
   - Keep synchronized blocks short

## Common Pitfalls Avoided

❌ **Don't:**
- Use raw threads
- Forget to shut down executors
- Swallow InterruptedException
- Synchronize too broadly
- Use synchronized collections

✅ **Do:**
- Use thread pools
- Always shutdown and await termination
- Restore interrupt status
- Minimize synchronization scope
- Use concurrent collections

## Real-World Applications

### 1. Web API Parallel Calls
```java
CompletableFuture<User> userFuture = fetchUser(userId);
CompletableFuture<Orders> ordersFuture = fetchOrders(userId);
CompletableFuture<Recommendations> recsFuture = fetchRecs(userId);

// Combine all results
Dashboard dashboard = userFuture
    .thenCombine(ordersFuture, Pair::of)
    .thenCombine(recsFuture, (pair, recs) -> 
        new Dashboard(pair.getLeft(), pair.getRight(), recs))
    .get(5, TimeUnit.SECONDS);
```

### 2. Batch Processing
```java
ExecutorService executor = Executors.newFixedThreadPool(10);
List<CompletableFuture<Void>> futures = items.stream()
    .map(item -> CompletableFuture.runAsync(() -> 
        process(item), executor))
    .toList();

CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
    .join();
```

### 3. Rate Limiting
```java
Semaphore rateLimiter = new Semaphore(10); // Max 10 concurrent

rateLimiter.acquire();
try {
    // Perform rate-limited operation
} finally {
    rateLimiter.release();
}
```

## Integration with Spring Boot

These concepts are used extensively in Spring:
- `@Async` for async method execution
- `@Scheduled` for periodic tasks
- WebFlux for reactive programming
- `ThreadPoolTaskExecutor` for custom pools

See Module 02 for Spring Boot integration examples.

## Common Interview Questions

**Q: What's the difference between synchronized and volatile?**
A: `synchronized` ensures mutual exclusion and visibility. `volatile` only ensures visibility.

**Q: When to use ExecutorService vs CompletableFuture?**
A: Use ExecutorService for simple parallel execution. Use CompletableFuture for complex async workflows with chaining.

**Q: What's thread starvation?**
A: When a thread cannot gain regular access to shared resources due to other threads monopolizing access.

**Q: Explain happens-before relationship**
A: Guarantees that memory writes in one thread are visible to reads in another thread.

**Q: How to avoid deadlocks?**
A: 1) Lock ordering, 2) Lock timeout, 3) Deadlock detection

## Next Steps

1. Run the demo and study each part
2. Modify examples to experiment
3. Try creating your own concurrent programs
4. Move to [Module 02: Spring Boot Fundamentals](../../02-spring-boot-fundamentals/)

---

**💡 Tip**: Concurrency is one of the hardest topics in programming. Understanding these patterns will make you a better developer and prepare you for production systems!
