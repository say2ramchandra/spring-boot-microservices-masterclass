# Concurrency & Multithreading

## Overview
Master Java concurrency, multithreading, thread safety, and asynchronous programming with CompletableFuture.

## Duration: 1-1.5 Days

---

## Table of Contents
1. [Thread Basics](#thread-basics)
2. [Thread Lifecycle](#thread-lifecycle)
3. [Synchronization](#synchronization)
4. [Java Memory Model (JMM)](#java-memory-model-jmm)
5. [Locks and Conditions](#locks-and-conditions)
6. [Thread Pools (ExecutorService)](#thread-pools)
7. [Concurrent Collections](#concurrent-collections)
8. [CompletableFuture](#completablefuture)
9. [Fork/Join Framework](#forkjoin-framework)
10. [Best Practices](#best-practices)
11. [Spring Boot Integration](#spring-boot-integration)

---

## Thread Basics

### Creating Threads

#### 1. Extending Thread Class
```java
class MyThread extends Thread {
    @Override
    public void run() {
        System.out.println("Thread running: " + Thread.currentThread().getName());
    }
}

// Usage
MyThread thread = new MyThread();
thread.start(); // Starts the thread
```

#### 2. Implementing Runnable Interface (Preferred)
```java
class MyRunnable implements Runnable {
    @Override
    public void run() {
        System.out.println("Runnable running: " + Thread.currentThread().getName());
    }
}

// Usage
Thread thread = new Thread(new MyRunnable());
thread.start();
```

#### 3. Using Lambda (Modern Approach)
```java
Thread thread = new Thread(() -> {
    System.out.println("Lambda thread: " + Thread.currentThread().getName());
});
thread.start();
```

### Thread Methods

```java
// Get current thread
Thread currentThread = Thread.currentThread();

// Thread name
thread.setName("WorkerThread-1");
String name = thread.getName();

// Thread priority (1-10)
thread.setPriority(Thread.MAX_PRIORITY);

// Check if alive
boolean isAlive = thread.isAlive();

// Daemon threads (JVM exits when only daemon threads remain)
thread.setDaemon(true);

// Sleep (pause execution)
Thread.sleep(1000); // milliseconds

// Wait for thread to finish
thread.join();
thread.join(2000); // wait max 2 seconds
```

---

## Thread Lifecycle

```
NEW → RUNNABLE → RUNNING → BLOCKED/WAITING/TIMED_WAITING → TERMINATED
```

### States Explained

1. **NEW**: Thread created but not started
2. **RUNNABLE**: Thread ready to run, waiting for CPU
3. **RUNNING**: Thread executing
4. **BLOCKED**: Thread waiting for monitor lock
5. **WAITING**: Thread waiting indefinitely (wait(), join())
6. **TIMED_WAITING**: Thread waiting for specified time (sleep(), wait(timeout))
7. **TERMINATED**: Thread execution completed

```java
Thread thread = new Thread(() -> {
    try {
        Thread.sleep(1000);
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
});

System.out.println(thread.getState()); // NEW
thread.start();
System.out.println(thread.getState()); // RUNNABLE
thread.join();
System.out.println(thread.getState()); // TERMINATED
```

---

## Synchronization

### Problem: Race Condition

```java
class Counter {
    private int count = 0;
    
    // NOT thread-safe
    public void increment() {
        count++; // Read-Modify-Write (3 operations)
    }
    
    public int getCount() {
        return count;
    }
}

// Multiple threads calling increment() can cause lost updates!
```

### Solution 1: Synchronized Method

```java
class Counter {
    private int count = 0;
    
    // Thread-safe
    public synchronized void increment() {
        count++;
    }
    
    public synchronized int getCount() {
        return count;
    }
}
```

### Solution 2: Synchronized Block

```java
class Counter {
    private int count = 0;
    private final Object lock = new Object();
    
    public void increment() {
        synchronized(lock) {
            count++;
        }
    }
    
    public int getCount() {
        synchronized(lock) {
            return count;
        }
    }
}
```

### Solution 3: Atomic Classes

```java
import java.util.concurrent.atomic.AtomicInteger;

class Counter {
    private AtomicInteger count = new AtomicInteger(0);
    
    // Lock-free, thread-safe
    public void increment() {
        count.incrementAndGet();
    }
    
    public int getCount() {
        return count.get();
    }
}
```

### Atomic Operations

```java
AtomicInteger atomicInt = new AtomicInteger(0);
atomicInt.incrementAndGet(); // ++i
atomicInt.getAndIncrement(); // i++
atomicInt.addAndGet(5);      // i += 5
atomicInt.compareAndSet(10, 20); // CAS operation

AtomicLong atomicLong = new AtomicLong(0);
AtomicBoolean atomicBoolean = new AtomicBoolean(false);

// Atomic references
AtomicReference<String> atomicRef = new AtomicReference<>("initial");
atomicRef.set("updated");
```

---

## Java Memory Model (JMM)

The Java Memory Model defines how threads interact through memory and establishes rules for visibility and ordering of shared variables.

### Why JMM Matters

Without proper understanding of JMM, you may encounter:
- **Visibility problems**: Thread changes not seen by other threads
- **Reordering issues**: Code executes in unexpected order
- **Data races**: Unpredictable program behavior

### Memory Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                        MAIN MEMORY                               │
│    ┌─────────┐    ┌─────────┐    ┌─────────┐    ┌─────────┐    │
│    │ Var A   │    │ Var B   │    │ Var C   │    │ Object  │    │
│    └─────────┘    └─────────┘    └─────────┘    └─────────┘    │
└─────────────────────────────────────────────────────────────────┘
         ↑↓                ↑↓                 ↑↓
    ┌─────────────┐   ┌─────────────┐   ┌─────────────┐
    │  Thread 1   │   │  Thread 2   │   │  Thread 3   │
    │ ┌─────────┐ │   │ ┌─────────┐ │   │ ┌─────────┐ │
    │ │ CPU     │ │   │ │ CPU     │ │   │ │ CPU     │ │
    │ │ Cache   │ │   │ │ Cache   │ │   │ │ Cache   │ │
    │ │(local)  │ │   │ │(local)  │ │   │ │(local)  │ │
    │ └─────────┘ │   │ └─────────┘ │   │ └─────────┘ │
    └─────────────┘   └─────────────┘   └─────────────┘
```

### Visibility Problem

```java
// BROKEN: Visibility problem
class StopThread {
    private boolean stopRequested = false;  // Not volatile!
    
    public void requestStop() {
        stopRequested = true;  // Thread 1 writes
    }
    
    public void run() {
        while (!stopRequested) {  // Thread 2 may NEVER see the change!
            // do work
        }
    }
}
```

**Why does this break?**
- Thread 1 updates `stopRequested` in its CPU cache
- Thread 2 reads from its own CPU cache
- Without synchronization, caches may never sync

### The `volatile` Keyword

`volatile` guarantees visibility and prevents reordering for a variable.

```java
// FIXED: Using volatile
class StopThread {
    private volatile boolean stopRequested = false;
    
    public void requestStop() {
        stopRequested = true;  // Write to main memory
    }
    
    public void run() {
        while (!stopRequested) {  // Always read from main memory
            // do work
        }
    }
}
```

**What `volatile` guarantees:**
1. **Visibility**: Writes are immediately visible to other threads
2. **Ordering**: No reordering across volatile read/write

**What `volatile` does NOT guarantee:**
- Atomicity of compound operations (count++ is NOT atomic even with volatile)

```java
// STILL BROKEN: volatile doesn't make count++ atomic
private volatile int count = 0;

public void increment() {
    count++;  // Read, increment, write - NOT atomic!
}
```

### Happens-Before Relationship

The happens-before relationship guarantees that memory writes by one specific statement are visible to another specific statement.

```
If Action A happens-before Action B, then:
  - Memory effects of A are visible to B
  - A is ordered before B
```

#### Rules That Establish Happens-Before

**1. Program Order Rule**
Within a single thread, each action happens-before every action that comes later.

```java
// Thread 1
x = 1;     // Action A
y = 2;     // Action B - A happens-before B
```

**2. Monitor Lock Rule**
An unlock on a monitor happens-before every subsequent lock on that same monitor.

```java
synchronized (lock) {
    x = 10;        // Write inside synchronized
}
// Thread 2
synchronized (lock) {
    int y = x;     // Guaranteed to see x = 10
}
```

**3. Volatile Variable Rule**
A write to a volatile field happens-before every subsequent read of that volatile.

```java
volatile boolean ready = false;
int data = 0;

// Thread 1
data = 42;        // (1) Write data
ready = true;     // (2) Write volatile - (1) happens-before (2)

// Thread 2
if (ready) {      // (3) Read volatile - (2) happens-before (3)
    use(data);    // (4) Guaranteed to see data = 42!
}
```

**4. Thread Start Rule**
A call to `Thread.start()` happens-before any action in the started thread.

```java
data = 42;
thread.start();    // happens-before
// In new thread:  // data = 42 is visible
```

**5. Thread Join Rule**
All actions in a thread happen-before any action following `thread.join()`.

```java
thread.join();     // Wait for thread
// All writes by thread are now visible here
```

**6. Transitivity**
If A happens-before B, and B happens-before C, then A happens-before C.

### Safe Publication

Safe publication ensures that an object reference and its state are visible to other threads correctly.

#### Unsafe Publication (BROKEN)

```java
// BROKEN: Unsafe publication
public class Holder {
    private int value;
    
    public Holder(int n) {
        this.value = n;  // May not complete before reference escapes!
    }
    
    public void assertValue() {
        if (value != value) {  // Could actually fail!
            throw new AssertionError("Value inconsistent");
        }
    }
}

// Thread 1
holder = new Holder(42);  // Reference may be visible before value is set!

// Thread 2
holder.assertValue();  // May see value = 0 (default) or value = 42
```

#### Safe Publication Idioms

```java
// 1. Initialize in static initializer
static Holder holder = new Holder(42);

// 2. Store reference in volatile field
volatile Holder holder = new Holder(42);

// 3. Store reference in properly synchronized field
synchronized(lock) {
    holder = new Holder(42);
}

// 4. Store reference in final field (for immutable objects)
final Holder holder = new Holder(42);

// 5. Store in concurrent collection
ConcurrentMap<String, Holder> map = new ConcurrentHashMap<>();
map.put("key", new Holder(42));
```

### Double-Checked Locking

A common pattern that MUST use volatile to be correct.

```java
// BROKEN without volatile (before Java 5)
class Singleton {
    private static Singleton instance;
    
    public static Singleton getInstance() {
        if (instance == null) {
            synchronized (Singleton.class) {
                if (instance == null) {
                    instance = new Singleton();  // Can partially construct!
                }
            }
        }
        return instance;
    }
}

// CORRECT with volatile
class Singleton {
    private static volatile Singleton instance;  // Must be volatile!
    
    public static Singleton getInstance() {
        if (instance == null) {                   // First check (no lock)
            synchronized (Singleton.class) {
                if (instance == null) {           // Second check (with lock)
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }
}

// EVEN BETTER: Use enum or holder pattern
enum SingletonEnum {
    INSTANCE;
    // Thread-safe by JVM guarantee
}

// Or initialization-on-demand holder
class SingletonHolder {
    private static class Holder {
        static final SingletonHolder INSTANCE = new SingletonHolder();
    }
    
    public static SingletonHolder getInstance() {
        return Holder.INSTANCE;  // Thread-safe, lazy initialization
    }
}
```

### Memory Barriers (Fences)

Memory barriers prevent certain reorderings of instructions.

```java
// VarHandle provides explicit fences (Java 9+)
import java.lang.invoke.VarHandle;

VarHandle.fullFence();       // Both load and store barrier
VarHandle.loadLoadFence();   // Load-load barrier
VarHandle.storeStoreFence(); // Store-store barrier
VarHandle.acquireFence();    // Like volatile read
VarHandle.releaseFence();    // Like volatile write
```

### Common JMM Pitfalls

#### 1. Non-Volatile Flag
```java
// BROKEN
boolean running = true;  // Not volatile - may loop forever
while (running) { /* work */ }
```

#### 2. Inconsistent Synchronization
```java
// BROKEN: All accesses must be synchronized
synchronized (lock) { count++; }  // Write synchronized
int x = count;                      // Read NOT synchronized - bug!
```

#### 3. Publishing via Constructor
```java
// BROKEN: Don't let 'this' escape during construction
class BadExample {
    public BadExample() {
        EventBus.register(this);  // 'this' escapes before construction completes!
    }
}
```

### JMM Summary Table

| Operation | Visibility | Ordering | Atomicity |
|-----------|------------|----------|-----------|
| Normal variable | ❌ | ❌ | ❌ |
| `volatile` variable | ✅ | ✅ | ❌ (compound) |
| `synchronized` | ✅ | ✅ | ✅ |
| `Atomic*` classes | ✅ | ✅ | ✅ |
| `final` field | ✅ (after construction) | ✅ | N/A |

---

## Locks and Conditions

### ReentrantLock

```java
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class BankAccount {
    private double balance;
    private final Lock lock = new ReentrantLock();
    
    public void withdraw(double amount) {
        lock.lock();
        try {
            if (balance >= amount) {
                balance -= amount;
            }
        } finally {
            lock.unlock(); // Always unlock in finally!
        }
    }
    
    public boolean tryWithdraw(double amount) {
        if (lock.tryLock()) { // Non-blocking
            try {
                if (balance >= amount) {
                    balance -= amount;
                    return true;
                }
                return false;
            } finally {
                lock.unlock();
            }
        }
        return false; // Couldn't acquire lock
    }
}
```

### ReadWriteLock

```java
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class SharedResource {
    private String data;
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    
    // Multiple threads can read simultaneously
    public String read() {
        rwLock.readLock().lock();
        try {
            return data;
        } finally {
            rwLock.readLock().unlock();
        }
    }
    
    // Only one thread can write
    public void write(String newData) {
        rwLock.writeLock().lock();
        try {
            data = newData;
        } finally {
            rwLock.writeLock().unlock();
        }
    }
}
```

### Condition Variables

```java
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class BoundedBuffer<T> {
    private final Queue<T> queue = new LinkedList<>();
    private final int capacity;
    private final Lock lock = new ReentrantLock();
    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();
    
    public BoundedBuffer(int capacity) {
        this.capacity = capacity;
    }
    
    public void put(T item) throws InterruptedException {
        lock.lock();
        try {
            while (queue.size() == capacity) {
                notFull.await(); // Wait until not full
            }
            queue.offer(item);
            notEmpty.signal(); // Signal waiting consumers
        } finally {
            lock.unlock();
        }
    }
    
    public T take() throws InterruptedException {
        lock.lock();
        try {
            while (queue.isEmpty()) {
                notEmpty.await(); // Wait until not empty
            }
            T item = queue.poll();
            notFull.signal(); // Signal waiting producers
            return item;
        } finally {
            lock.unlock();
        }
    }
}
```

---

## Thread Pools

### Why Thread Pools?
- Creating threads is expensive
- Too many threads waste resources
- Thread pools reuse threads efficiently

### ExecutorService

```java
import java.util.concurrent.*;

// 1. Fixed Thread Pool (recommended for bounded tasks)
ExecutorService executor = Executors.newFixedThreadPool(10);

// 2. Cached Thread Pool (creates threads as needed)
ExecutorService cachedExecutor = Executors.newCachedThreadPool();

// 3. Single Thread Executor (sequential execution)
ExecutorService singleExecutor = Executors.newSingleThreadExecutor();

// 4. Scheduled Thread Pool (for periodic tasks)
ScheduledExecutorService scheduledExecutor = 
    Executors.newScheduledThreadPool(5);
```

### Using ExecutorService

```java
ExecutorService executor = Executors.newFixedThreadPool(5);

// Submit Runnable (no return value)
executor.submit(() -> {
    System.out.println("Task executed by: " + 
        Thread.currentThread().getName());
});

// Submit Callable (returns value)
Future<Integer> future = executor.submit(() -> {
    Thread.sleep(1000);
    return 42;
});

// Get result (blocks until complete)
try {
    Integer result = future.get(); // Blocks
    System.out.println("Result: " + result);
    
    // Or with timeout
    Integer result2 = future.get(2, TimeUnit.SECONDS);
} catch (InterruptedException | ExecutionException | TimeoutException e) {
    e.printStackTrace();
}

// Shutdown (important!)
executor.shutdown(); // Graceful shutdown
executor.shutdownNow(); // Force shutdown

// Wait for termination
executor.awaitTermination(60, TimeUnit.SECONDS);
```

### Scheduled Tasks

```java
ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

// Execute after delay
scheduler.schedule(() -> {
    System.out.println("Executed after 5 seconds");
}, 5, TimeUnit.SECONDS);

// Execute periodically (fixed rate)
scheduler.scheduleAtFixedRate(() -> {
    System.out.println("Executed every 10 seconds");
}, 0, 10, TimeUnit.SECONDS); // initial delay, period

// Execute periodically (fixed delay)
scheduler.scheduleWithFixedDelay(() -> {
    System.out.println("Executed 10 seconds after previous completion");
}, 0, 10, TimeUnit.SECONDS);

// Shutdown when done
scheduler.shutdown();
```

### ThreadPoolExecutor (Advanced)

```java
int corePoolSize = 5;
int maximumPoolSize = 10;
long keepAliveTime = 60L;
BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(100);

ThreadPoolExecutor executor = new ThreadPoolExecutor(
    corePoolSize,
    maximumPoolSize,
    keepAliveTime,
    TimeUnit.SECONDS,
    workQueue,
    new ThreadPoolExecutor.CallerRunsPolicy() // Rejection policy
);

// Monitor pool
System.out.println("Active threads: " + executor.getActiveCount());
System.out.println("Pool size: " + executor.getPoolSize());
System.out.println("Queue size: " + executor.getQueue().size());
```

---

## Concurrent Collections

### ConcurrentHashMap

```java
import java.util.concurrent.ConcurrentHashMap;

ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();

// Thread-safe operations
map.put("key", 1);
map.putIfAbsent("key", 2); // Only if absent
map.computeIfAbsent("key", k -> 10);
map.computeIfPresent("key", (k, v) -> v + 1);
map.merge("key", 1, Integer::sum); // Atomic increment

// Bulk operations
map.forEach((k, v) -> System.out.println(k + ": " + v));
```

### CopyOnWriteArrayList

```java
import java.util.concurrent.CopyOnWriteArrayList;

// Good for read-heavy scenarios
CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>();
list.add("item1");

// Thread-safe iteration (no ConcurrentModificationException)
for (String item : list) {
    System.out.println(item);
    list.add("item2"); // Safe!
}
```

### BlockingQueue

```java
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ArrayBlockingQueue;

// Producer-Consumer pattern
BlockingQueue<String> queue = new ArrayBlockingQueue<>(10);

// Producer
new Thread(() -> {
    try {
        queue.put("item"); // Blocks if full
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
}).start();

// Consumer
new Thread(() -> {
    try {
        String item = queue.take(); // Blocks if empty
        System.out.println("Consumed: " + item);
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
}).start();
```

### Other Concurrent Collections

```java
// Thread-safe queue
ConcurrentLinkedQueue<String> concurrentQueue = new ConcurrentLinkedQueue<>();

// Thread-safe set
ConcurrentSkipListSet<String> concurrentSet = new ConcurrentSkipListSet<>();

// Thread-safe sorted map
ConcurrentSkipListMap<String, Integer> sortedMap = new ConcurrentSkipListMap<>();
```

---

## CompletableFuture

### Async Execution

```java
import java.util.concurrent.CompletableFuture;

// Run async (no return value)
CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> {
    System.out.println("Running async task");
});

// Supply async (returns value)
CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
    return "Result from async task";
});

// Get result (blocks)
String result = future2.get();
```

### Chaining Operations

```java
CompletableFuture.supplyAsync(() -> "Hello")
    .thenApply(s -> s + " World")           // Transform
    .thenApply(String::toUpperCase)         // Transform again
    .thenAccept(System.out::println)        // Consume
    .thenRun(() -> System.out.println("Done")); // Side effect
```

### Combining Futures

```java
// Combine two independent futures
CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> "Hello");
CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> "World");

CompletableFuture<String> combined = future1.thenCombine(future2, (s1, s2) -> s1 + " " + s2);
System.out.println(combined.get()); // "Hello World"

// Compose (chain dependent futures)
CompletableFuture<String> composed = CompletableFuture
    .supplyAsync(() -> "User123")
    .thenCompose(userId -> getUserDetails(userId)); // Returns CompletableFuture
```

### Handling Multiple Futures

```java
CompletableFuture<String> f1 = CompletableFuture.supplyAsync(() -> "Task1");
CompletableFuture<String> f2 = CompletableFuture.supplyAsync(() -> "Task2");
CompletableFuture<String> f3 = CompletableFuture.supplyAsync(() -> "Task3");

// Wait for all to complete
CompletableFuture<Void> allOf = CompletableFuture.allOf(f1, f2, f3);
allOf.join(); // Wait for all

// Get first completed
CompletableFuture<Object> anyOf = CompletableFuture.anyOf(f1, f2, f3);
Object firstResult = anyOf.get();
```

### Error Handling

```java
CompletableFuture<String> future = CompletableFuture
    .supplyAsync(() -> {
        if (Math.random() > 0.5) {
            throw new RuntimeException("Error!");
        }
        return "Success";
    })
    .exceptionally(ex -> {
        System.out.println("Error: " + ex.getMessage());
        return "Default value";
    })
    .handle((result, ex) -> {
        if (ex != null) {
            return "Handled: " + ex.getMessage();
        }
        return result;
    });
```

### Real-World Example

```java
// Fetch user, orders, and recommendations in parallel
CompletableFuture<User> userFuture = 
    CompletableFuture.supplyAsync(() -> userService.getUser(userId));

CompletableFuture<List<Order>> ordersFuture = 
    CompletableFuture.supplyAsync(() -> orderService.getOrders(userId));

CompletableFuture<List<Product>> recommendationsFuture = 
    CompletableFuture.supplyAsync(() -> recommendationService.getRecommendations(userId));

// Combine all results
CompletableFuture<UserDashboard> dashboardFuture = userFuture
    .thenCombine(ordersFuture, (user, orders) -> new Pair<>(user, orders))
    .thenCombine(recommendationsFuture, (pair, recommendations) -> 
        new UserDashboard(pair.getKey(), pair.getValue(), recommendations));

UserDashboard dashboard = dashboardFuture.get(5, TimeUnit.SECONDS);
```

---

## Fork/Join Framework

### RecursiveTask (Returns Result)

```java
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.ForkJoinPool;

class SumTask extends RecursiveTask<Long> {
    private static final int THRESHOLD = 10_000;
    private final long[] array;
    private final int start, end;
    
    public SumTask(long[] array, int start, int end) {
        this.array = array;
        this.start = start;
        this.end = end;
    }
    
    @Override
    protected Long compute() {
        int length = end - start;
        
        if (length <= THRESHOLD) {
            // Compute directly
            long sum = 0;
            for (int i = start; i < end; i++) {
                sum += array[i];
            }
            return sum;
        } else {
            // Split task
            int mid = start + length / 2;
            SumTask leftTask = new SumTask(array, start, mid);
            SumTask rightTask = new SumTask(array, mid, end);
            
            leftTask.fork(); // Execute in parallel
            long rightResult = rightTask.compute();
            long leftResult = leftTask.join();
            
            return leftResult + rightResult;
        }
    }
}

// Usage
ForkJoinPool pool = new ForkJoinPool();
long[] array = new long[1_000_000];
// ... populate array
Long sum = pool.invoke(new SumTask(array, 0, array.length));
```

---

## Best Practices

### 1. Prefer High-Level Concurrency Utilities
```java
// ❌ Don't use raw threads
new Thread(() -> task()).start();

// ✅ Use ExecutorService
executor.submit(() -> task());

// ✅ Use CompletableFuture
CompletableFuture.supplyAsync(() -> task());
```

### 2. Always Shutdown Executors
```java
ExecutorService executor = Executors.newFixedThreadPool(10);
try {
    // Submit tasks
} finally {
    executor.shutdown();
    executor.awaitTermination(60, TimeUnit.SECONDS);
}
```

### 3. Handle InterruptedException Properly
```java
// ❌ Don't swallow interruption
try {
    Thread.sleep(1000);
} catch (InterruptedException e) {
    e.printStackTrace(); // Wrong!
}

// ✅ Restore interrupt status
try {
    Thread.sleep(1000);
} catch (InterruptedException e) {
    Thread.currentThread().interrupt(); // Restore flag
    throw new RuntimeException(e);
}
```

### 4. Use Immutable Objects
```java
// Thread-safe by design
public final class ImmutableUser {
    private final String name;
    private final int age;
    
    public ImmutableUser(String name, int age) {
        this.name = name;
        this.age = age;
    }
    
    // Only getters, no setters
    public String getName() { return name; }
    public int getAge() { return age; }
}
```

### 5. Minimize Synchronization Scope
```java
// ❌ Too broad
public synchronized void processLargeData() {
    // Lots of non-critical code
    criticalSection();
    // More non-critical code
}

// ✅ Minimal scope
public void processLargeData() {
    // Non-critical code
    synchronized(this) {
        criticalSection(); // Only synchronize what's needed
    }
    // More non-critical code
}
```

### 6. Use Concurrent Collections
```java
// ❌ Synchronized wrapper (performance overhead)
Map<String, String> map = Collections.synchronizedMap(new HashMap<>());

// ✅ Concurrent collection
Map<String, String> map = new ConcurrentHashMap<>();
```

### 7. Prefer AtomicInteger over synchronized
```java
// ❌ Synchronized
private int counter = 0;
public synchronized void increment() {
    counter++;
}

// ✅ Atomic
private AtomicInteger counter = new AtomicInteger(0);
public void increment() {
    counter.incrementAndGet();
}
```

---

## Spring Boot Integration

### @Async Annotation

```java
@Configuration
@EnableAsync
public class AsyncConfig {
    
    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("async-");
        executor.initialize();
        return executor;
    }
}

@Service
public class UserService {
    
    @Async
    public CompletableFuture<User> findUser(Long id) {
        User user = userRepository.findById(id).orElse(null);
        return CompletableFuture.completedFuture(user);
    }
    
    @Async
    public void sendEmail(String to, String subject, String body) {
        // Email sending logic (runs asynchronously)
    }
}
```

### @Scheduled Tasks

```java
@Configuration
@EnableScheduling
public class SchedulingConfig {
}

@Component
public class ScheduledTasks {
    
    @Scheduled(fixedRate = 5000) // Every 5 seconds
    public void reportCurrentTime() {
        System.out.println("Current time: " + LocalDateTime.now());
    }
    
    @Scheduled(cron = "0 0 1 * * ?") // Every day at 1 AM
    public void dailyCleanup() {
        // Cleanup logic
    }
    
    @Scheduled(initialDelay = 10000, fixedDelay = 5000)
    public void delayedTask() {
        // Runs 10 seconds after start, then every 5 seconds after completion
    }
}
```

---

## Common Interview Questions

**Q1: What's the difference between `start()` and `run()`?**
A: `start()` creates a new thread and calls `run()`. Calling `run()` directly executes in the current thread.

**Q2: Explain happens-before relationship**
A: Ensures that memory writes in one thread are visible to reads in another thread. Established by synchronized blocks, volatile fields, thread start/join.

**Q3: What's the difference between `sleep()` and `wait()`?**
A: `sleep()` doesn't release locks, `wait()` does. `wait()` must be called inside synchronized block.

**Q4: What is thread starvation?**
A: When a thread is unable to gain regular access to shared resources and is unable to make progress.

**Q5: Explain volatile keyword**
A: Ensures visibility of changes across threads. Prevents compiler optimizations that cache values.

```java
private volatile boolean running = true; // Always read from main memory
```

**Q6: What are deadlocks and how to prevent them?**
A: Deadlock occurs when two threads wait for each other's locks forever.
Prevention: Always acquire locks in the same order, use timeouts, use tryLock().

---

## See Demo

Check out the [demo project](demo-concurrency-multithreading/) for working examples of all concepts!

---

## Additional Resources

- **Java Concurrency in Practice** by Brian Goetz
- **Java Documentation**: java.util.concurrent package
- **Spring Documentation**: Async and Scheduled tasks

---

**Next**: Move to [Module 02: Spring Boot Fundamentals](../../02-spring-boot-fundamentals/) 🚀
