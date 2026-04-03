# JVM Internals

## Overview
Deep dive into Java Virtual Machine architecture, memory management, garbage collection, and class loading mechanisms.

## Duration: 1 Day

---

## Table of Contents
1. [JVM Architecture](#jvm-architecture)
2. [Memory Areas](#memory-areas)
3. [Garbage Collection](#garbage-collection)
4. [ClassLoader Subsystem](#classloader-subsystem)
5. [JIT Compilation](#jit-compilation)
6. [JVM Tuning](#jvm-tuning)
7. [Monitoring and Profiling](#monitoring-and-profiling)
8. [Interview Questions](#interview-questions)

---

## JVM Architecture

### High-Level Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                           JVM                                    │
├─────────────────────────────────────────────────────────────────┤
│  ┌──────────────┐   ┌──────────────┐   ┌──────────────────────┐ │
│  │  ClassLoader │   │   Runtime    │   │    Execution Engine  │ │
│  │   Subsystem  │   │  Data Areas  │   │                      │ │
│  │              │   │              │   │  ┌────────────────┐  │ │
│  │ ┌──────────┐ │   │ ┌──────────┐ │   │  │   Interpreter  │  │ │
│  │ │Bootstrap │ │   │ │   Heap   │ │   │  └────────────────┘  │ │
│  │ └──────────┘ │   │ └──────────┘ │   │  ┌────────────────┐  │ │
│  │ ┌──────────┐ │   │ ┌──────────┐ │   │  │ JIT Compiler   │  │ │
│  │ │Extension │ │   │ │  Stack   │ │   │  └────────────────┘  │ │
│  │ └──────────┘ │   │ └──────────┘ │   │  ┌────────────────┐  │ │
│  │ ┌──────────┐ │   │ ┌──────────┐ │   │  │ Garbage        │  │ │
│  │ │Application│   │ │Metaspace │ │   │  │ Collector      │  │ │
│  │ └──────────┘ │   │ └──────────┘ │   │  └────────────────┘  │ │
│  └──────────────┘   └──────────────┘   └──────────────────────┘ │
│                                                                  │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │                Native Method Interface (JNI)               │  │
│  └───────────────────────────────────────────────────────────┘  │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │                  Native Method Libraries                   │  │
│  └───────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

### Components

| Component | Description |
|-----------|-------------|
| **ClassLoader** | Loads, links, and initializes classes |
| **Runtime Data Areas** | Memory regions used during execution |
| **Execution Engine** | Executes bytecode (interpreter + JIT) |
| **JNI** | Interface for native C/C++ code |
| **Native Libraries** | Platform-specific native code |

---

## Memory Areas

### Runtime Data Areas

```
┌─────────────────────────────────────────────────────────────┐
│                        JVM Memory                            │
├───────────────────────────┬─────────────────────────────────┤
│      SHARED (All Threads) │       PER THREAD                │
├───────────────────────────┼─────────────────────────────────┤
│                           │  ┌───────────────────────────┐  │
│  ┌─────────────────────┐  │  │      JVM Stack            │  │
│  │        HEAP         │  │  │  ┌─────────────────────┐  │  │
│  │  ┌───────────────┐  │  │  │  │   Stack Frame 1     │  │  │
│  │  │  Young Gen    │  │  │  │  │  - Local Variables  │  │  │
│  │  │ ┌───────────┐ │  │  │  │  │  - Operand Stack    │  │  │
│  │  │ │   Eden    │ │  │  │  │  │  - Frame Data       │  │  │
│  │  │ ├───────────┤ │  │  │  │  └─────────────────────┘  │  │
│  │  │ │Survivor S0│ │  │  │  │  ┌─────────────────────┐  │  │
│  │  │ ├───────────┤ │  │  │  │  │   Stack Frame 2     │  │  │
│  │  │ │Survivor S1│ │  │  │  │  └─────────────────────┘  │  │
│  │  │ └───────────┘ │  │  │  └───────────────────────────┘  │
│  │  └───────────────┘  │  │                                 │
│  │  ┌───────────────┐  │  │  ┌───────────────────────────┐  │
│  │  │   Old Gen     │  │  │  │      PC Register          │  │
│  │  │ (Tenured)     │  │  │  │  (Current instruction)    │  │
│  │  └───────────────┘  │  │  └───────────────────────────┘  │
│  └─────────────────────┘  │                                 │
│                           │  ┌───────────────────────────┐  │
│  ┌─────────────────────┐  │  │   Native Method Stack     │  │
│  │     METASPACE       │  │  │   (For JNI calls)         │  │
│  │  - Class metadata   │  │  └───────────────────────────┘  │
│  │  - Method bytecode  │  │                                 │
│  │  - Constant pool    │  │                                 │
│  └─────────────────────┘  │                                 │
└───────────────────────────┴─────────────────────────────────┘
```

### 1. Heap Memory

The largest memory area where all objects are allocated.

```java
// All objects are created on the heap
Object obj = new Object();        // Allocated in Eden
String str = new String("Hello"); // Allocated in Eden
int[] arr = new int[1000];        // Allocated in Eden
```

**Heap Structure:**

| Region | Purpose | Size Ratio |
|--------|---------|------------|
| **Eden** | New object allocation | ~80% of Young Gen |
| **Survivor S0** | Surviving objects from Eden | ~10% of Young Gen |
| **Survivor S1** | Surviving objects from S0 | ~10% of Young Gen |
| **Old Gen** | Long-lived objects | ~2/3 of total heap |

### 2. Stack Memory

Per-thread memory for method execution.

```java
public void methodA() {
    int x = 10;           // x stored in stack frame of methodA
    methodB(x);           // New stack frame for methodB
}

public void methodB(int param) {
    double y = 20.5;      // param and y in methodB's stack frame
    Object obj = new Object(); // reference 'obj' on stack, 
                               // actual Object on heap
}
```

**Stack Frame Contents:**

```
┌─────────────────────────┐
│     Stack Frame         │
├─────────────────────────┤
│  Local Variables Array  │  ← this, method params, local vars
├─────────────────────────┤
│     Operand Stack       │  ← Intermediate computation values
├─────────────────────────┤
│      Frame Data         │  ← Return address, exception handler
└─────────────────────────┘
```

### 3. Metaspace (Java 8+)

Replaced PermGen. Stores class metadata in native memory.

```java
// Class metadata stored in Metaspace
public class MyClass {
    private static final String CONSTANT = "value"; // In Metaspace constant pool
    
    public void method() {  // Method bytecode in Metaspace
        // ...
    }
}
```

**Key Differences from PermGen:**

| Aspect | PermGen (≤Java 7) | Metaspace (≥Java 8) |
|--------|-------------------|---------------------|
| Location | JVM Heap | Native Memory |
| Default Size | Fixed (64-256MB) | Unlimited (auto-grows) |
| GC | Full GC required | Can be collected independently |
| OOM Error | `PermGen space` | `Metaspace` |

### Memory Configuration

```bash
# Heap settings
-Xms512m          # Initial heap size
-Xmx2g            # Maximum heap size
-Xmn256m          # Young generation size

# Metaspace settings
-XX:MetaspaceSize=128m        # Initial metaspace
-XX:MaxMetaspaceSize=512m     # Maximum metaspace

# Stack size
-Xss1m            # Thread stack size

# Direct memory
-XX:MaxDirectMemorySize=256m  # For NIO buffers
```

---

## Garbage Collection

### Why Garbage Collection?

```java
public void processData() {
    // Memory allocated
    List<String> data = new ArrayList<>();
    for (int i = 0; i < 10000; i++) {
        data.add("Item " + i);
    }
    // Method ends, 'data' becomes unreachable
    // GC will reclaim this memory automatically
}
```

### GC Roots and Reachability

Objects are "alive" if reachable from GC Roots:

```
GC Roots (Starting Points):
├── Local variables in stack frames
├── Active threads
├── Static fields of loaded classes
├── JNI references
└── System class loader

         GC Root
            │
            ▼
        ┌───────┐    Reference    ┌───────┐
        │ Obj A │ ─────────────► │ Obj B │ ◄── Reachable (alive)
        └───────┘                └───────┘
            │
            ▼
        ┌───────┐
        │ Obj C │ ◄── Reachable (alive)
        └───────┘

        ┌───────┐
        │ Obj D │ ◄── NOT reachable (garbage)
        └───────┘
```

### Generational Hypothesis

Most objects die young ("infant mortality"), so GC is optimized for this:

```
Object Allocation Flow:
                                              
    NEW                MINOR GC              MAJOR GC
  OBJECTS               (Fast)               (Slow)
     │                    │                     │
     ▼                    ▼                     ▼
┌─────────┐     ┌──────────────┐      ┌─────────────┐
│  EDEN   │ ──► │  SURVIVOR    │ ───► │   OLD GEN   │
│(allocate)│    │(survive 1-15x)│     │(long-lived) │
└─────────┘     └──────────────┘      └─────────────┘
     │                │                      │
     ▼                ▼                      ▼
  ~98% die        ~90% die             Collected in
  quickly        after a few           Major/Full GC
                   cycles
```

### GC Algorithms

#### 1. Mark-Sweep

```
Phase 1: MARK              Phase 2: SWEEP
                           
 ┌─┐  ┌─┐  ┌─┐  ┌─┐        ┌─┐      ┌─┐
 │●│  │ │  │●│  │ │   →    │●│      │●│
 └─┘  └─┘  └─┘  └─┘        └─┘      └─┘
  ▲         ▲               ▲        ▲
  │         │               │        │
 Used     Used             Used    Used
 Keep     Keep             
          
● = Marked (reachable)
  = Unmarked (garbage) - reclaimed
```

#### 2. Mark-Compact

```
Before:  [A][  ][B][  ][  ][C][  ][D]
          ↓
Mark:    [●][  ][●][  ][  ][●][  ][●]
          ↓
Compact: [A][B][C][D][          free          ]

Advantage: No fragmentation
Disadvantage: Moving objects is expensive
```

#### 3. Copying (Used in Young Gen)

```
EDEN        S0 (from)    S1 (to)
┌────────┐  ┌────┐       ┌────┐
│A B C D │  │E F │       │    │
└────────┘  └────┘       └────┘
     │          │
     └────┬─────┘
          │ Copy surviving objects
          ▼
┌────────┐  ┌────┐       ┌────┐
│        │  │    │       │A E │  ← Survivors compacted
└────────┘  └────┘       └────┘
   Empty     Empty      to-space

Next GC: S0 and S1 swap roles
```

### GC Types in HotSpot JVM

#### Serial GC

```bash
-XX:+UseSerialGC
```

```
Single-threaded GC - Simple but causes long pauses

Application Threads: ────────┬─────────────────┬────────
                             │                 │
                        STOP │   GC Running    │ RESUME
                             │  (Single core)  │
                             └─────────────────┘
                                   pause
```

**Best for:** Small apps, single CPU, < 100MB heap

#### Parallel GC (Throughput Collector)

```bash
-XX:+UseParallelGC
-XX:ParallelGCThreads=4
```

```
Multi-threaded for maximum throughput

Application Threads: ────────┬─────────────────┬────────
                             │                 │
                        STOP │   GC Running    │ RESUME
                             │   (4 threads)   │
                             │   ┌─┬─┬─┬─┐     │
                             │   │ │ │ │ │     │
                             │   └─┴─┴─┴─┘     │
                             └─────────────────┘
                               shorter pause
```

**Best for:** Batch processing, scientific computing, throughput-focused apps

#### G1 GC (Garbage First) - Default since Java 9

```bash
-XX:+UseG1GC
-XX:MaxGCPauseMillis=200
```

```
Region-based, predictable pause times

Heap divided into regions:
┌─────┬─────┬─────┬─────┬─────┬─────┬─────┬─────┐
│Eden │Eden │Surv │ Old │ Old │Free │Eden │Hum* │
├─────┼─────┼─────┼─────┼─────┼─────┼─────┼─────┤
│ Old │Free │Free │ Old │Eden │ Old │Surv │ Old │
└─────┴─────┴─────┴─────┴─────┴─────┴─────┴─────┘
                                          *Humongous (large objects)

G1 collects regions with most garbage first (hence "Garbage First")
```

**G1 Phases:**

1. **Young GC** - Collect Eden + Survivor regions
2. **Concurrent Mark** - Mark live objects (concurrent with app)
3. **Mixed GC** - Collect Young + some Old regions
4. **Full GC** - Fallback (avoid this!)

#### ZGC (Low Latency) - Production ready since Java 15

```bash
-XX:+UseZGC
```

```
Sub-millisecond pauses, terabyte heaps

Features:
- Pause times < 1ms (even for TB heaps)
- Concurrent compaction
- Colored pointers (metadata in pointer)
- Load barriers

Application: ────────────────────────────────────────
                  │  │                  │  │
                  └──┘                  └──┘
              <1ms pause            <1ms pause
                      
           GC: ══════════════════════════════════════
                    runs concurrently with app
```

**Best for:** Ultra-low latency requirements, large heaps (up to 16TB)

#### Shenandoah GC

```bash
-XX:+UseShenandoahGC
```

Similar to ZGC - ultra-low pause times. Available in OpenJDK.

### GC Comparison Table

| GC | Pause Time | Throughput | Heap Size | Use Case |
|----|------------|------------|-----------|----------|
| Serial | Long | Low | < 100MB | Single CPU, small apps |
| Parallel | Medium | **High** | Medium | Batch jobs, throughput |
| G1 | **Predictable** | Good | Large | General purpose (default) |
| ZGC | **Ultra-low** | Good | **Huge** | Low latency, large heap |
| Shenandoah | **Ultra-low** | Good | Large | Low latency |

### GC Tuning Parameters

```bash
# Common tuning options
java \
  -Xms4g -Xmx4g \                    # Fixed heap (no resizing)
  -XX:+UseG1GC \                      # Use G1 GC
  -XX:MaxGCPauseMillis=200 \          # Target max pause
  -XX:G1HeapRegionSize=16m \          # Region size
  -XX:InitiatingHeapOccupancyPercent=45 \  # When to start marking
  -XX:+PrintGCDetails \               # GC logging
  -XX:+PrintGCDateStamps \
  -Xlog:gc*:file=gc.log:time \        # GC log file (Java 9+)
  -jar myapp.jar
```

---

## ClassLoader Subsystem

### ClassLoader Hierarchy

```
                    ┌─────────────────────┐
                    │  Bootstrap Loader   │ ← Loads core Java (rt.jar)
                    │  (Native code)      │   java.lang.*, java.util.*
                    └──────────┬──────────┘
                               │ parent
                               ▼
                    ┌─────────────────────┐
                    │  Platform Loader    │ ← Loads platform classes
                    │  (Java 9+ modules)  │   java.sql.*, javax.*
                    └──────────┬──────────┘
                               │ parent
                               ▼
                    ┌─────────────────────┐
                    │  Application Loader │ ← Loads classpath classes
                    │  (System Loader)    │   Your application code
                    └──────────┬──────────┘
                               │ parent
                               ▼
                    ┌─────────────────────┐
                    │  Custom ClassLoader │ ← Plugin systems, hot reload
                    └─────────────────────┘
```

### Class Loading Process

```
┌─────────┐    ┌─────────┐    ┌─────────────┐    ┌─────────────┐
│ LOADING │ ─► │ LINKING │ ─► │ INITIALIZING│ ─► │   READY     │
└─────────┘    └─────────┘    └─────────────┘    └─────────────┘
     │              │               │
     ▼              ▼               ▼
 Read .class    Verify +        Run static
 from disk      Prepare +       initializers
               Resolve          <clinit>
```

**1. Loading:**
```java
// ClassLoader reads .class file into memory
Class<?> clazz = ClassLoader.getSystemClassLoader()
    .loadClass("com.example.MyClass");
```

**2. Linking:**
- **Verify**: Check bytecode validity
- **Prepare**: Allocate memory for static fields (default values)
- **Resolve**: Convert symbolic references to direct references

**3. Initialization:**
```java
public class MyClass {
    static int x = 10;           // Initialized here
    static {
        System.out.println("Static block"); // Runs during init
    }
}
```

### Delegation Model

Classes are loaded using **parent-delegation**:

```java
// When you request MyClass:
AppClassLoader.loadClass("MyClass")
    │
    ├─ 1. Check if already loaded (cache)
    │
    ├─ 2. Delegate to parent: PlatformClassLoader.loadClass("MyClass")
    │      │
    │      ├─ Delegate to parent: BootstrapClassLoader.loadClass("MyClass")
    │      │      │
    │      │      └─ Cannot find → return to child
    │      │
    │      └─ Cannot find → return to child
    │
    └─ 3. Try to load myself (from classpath)
           │
           └─ Found! Return the class
```

**Why delegation?**
- **Security**: Prevents malicious code from replacing core classes
- **Uniqueness**: Same class loaded once per classloader
- **Visibility**: Child can see parent's classes, not vice versa

### Custom ClassLoader

```java
public class HotReloadClassLoader extends ClassLoader {
    
    private final String classPath;
    
    public HotReloadClassLoader(String classPath) {
        super(HotReloadClassLoader.class.getClassLoader());
        this.classPath = classPath;
    }
    
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        try {
            // Convert class name to file path
            String fileName = classPath + "/" + 
                name.replace('.', '/') + ".class";
            
            // Read bytecode from file
            byte[] classData = Files.readAllBytes(Paths.get(fileName));
            
            // Define the class
            return defineClass(name, classData, 0, classData.length);
            
        } catch (IOException e) {
            throw new ClassNotFoundException(name, e);
        }
    }
}

// Usage - Hot reload plugins
public class PluginManager {
    
    public Object loadPlugin(String pluginClass) throws Exception {
        // Create new classloader each time for hot reload
        ClassLoader loader = new HotReloadClassLoader("/plugins");
        Class<?> clazz = loader.loadClass(pluginClass);
        return clazz.getDeclaredConstructor().newInstance();
    }
    
    public void reloadPlugin(String pluginClass) throws Exception {
        // New classloader = new version of class
        Object newPlugin = loadPlugin(pluginClass);
        // Replace old plugin with new one
    }
}
```

### Common ClassLoader Issues

#### 1. ClassNotFoundException

```java
// Class not on classpath
try {
    Class.forName("com.unknown.Missing");
} catch (ClassNotFoundException e) {
    // Check: Is the JAR in classpath?
    // Check: Is the class name spelled correctly?
}
```

#### 2. NoClassDefFoundError

```java
// Class was found at compile time but not at runtime
public class MyApp {
    // Uses SomeLibrary class
    SomeLibrary lib = new SomeLibrary(); // NoClassDefFoundError if JAR missing
}
// Solution: Add missing JAR to runtime classpath
```

#### 3. ClassCastException (Same class, different loaders)

```java
// Each classloader creates a separate "namespace"
ClassLoader loader1 = new CustomLoader();
ClassLoader loader2 = new CustomLoader();

Class<?> class1 = loader1.loadClass("MyClass");
Class<?> class2 = loader2.loadClass("MyClass");

class1 == class2; // FALSE! Different classes

Object obj = class1.newInstance();
MyClass myClass = (MyClass) obj; // ClassCastException!
// because MyClass is loaded by AppClassLoader, obj by loader1
```

---

## JIT Compilation

### Interpretation vs JIT

```
Source Code → Bytecode → Execution

                       ┌──────────────────────┐
                       │     INTERPRETER      │
                       │  (Execute bytecode   │
                       │   line by line)      │
                       └──────────────────────┘
                                 │
              "This code is hot" │ (executed many times)
                                 ▼
                       ┌──────────────────────┐
                       │    JIT COMPILER      │
                       │  (Compile to native  │
                       │   machine code)      │
                       └──────────────────────┘
                                 │
                                 ▼
                       ┌──────────────────────┐
                       │    NATIVE CODE       │
                       │  (Fast execution)    │
                       └──────────────────────┘
```

### Tiered Compilation (Default)

```
Level 0: Interpreter
    │
    │ Method called few times
    ▼
Level 1-3: C1 Compiler (Client)
    │       - Fast compilation
    │       - Basic optimizations
    │
    │ "Hot" method (10,000+ calls)
    ▼
Level 4: C2 Compiler (Server)
            - Aggressive optimizations
            - Inlining, loop unrolling
            - Escape analysis
```

### JIT Optimizations

#### 1. Method Inlining

```java
// Before inlining
public int calculate(int x) {
    return square(x) + 1;
}

private int square(int x) {
    return x * x;
}

// After inlining (JIT eliminates method call)
public int calculate(int x) {
    return (x * x) + 1;  // No method call overhead
}
```

#### 2. Escape Analysis

```java
public int sum() {
    // JIT detects Point doesn't "escape" this method
    Point p = new Point(3, 4);  // Can be stack-allocated!
    return p.x + p.y;
}

// After optimization (no heap allocation!)
public int sum() {
    int p_x = 3;
    int p_y = 4;
    return p_x + p_y;
}
```

#### 3. Loop Unrolling

```java
// Before
for (int i = 0; i < 4; i++) {
    sum += array[i];
}

// After unrolling
sum += array[0];
sum += array[1];
sum += array[2];
sum += array[3];
// No loop overhead
```

### JIT Monitoring

```bash
# Print compilation events
-XX:+PrintCompilation

# Example output:
#  123   1       3       java.lang.String::hashCode (55 bytes)
#  ^     ^       ^                ^                  ^
#  time  compile tier         method              size
#  (ms)   id
```

---

## JVM Tuning

### Memory Sizing Guidelines

```bash
# Heap sizing
-Xms = -Xmx                 # Avoid resizing
-Xmx = 50-70% of available RAM  # Leave room for OS, Metaspace

# Young Gen sizing
-Xmn = 1/3 to 1/2 of heap   # For high allocation rate apps

# Stack sizing
-Xss = 256k to 1m           # Default 1m, reduce for many threads
```

### Common Tuning Scenarios

#### High Throughput (Batch Jobs)

```bash
java \
  -Xms4g -Xmx4g \
  -XX:+UseParallelGC \
  -XX:ParallelGCThreads=8 \
  -jar batch-job.jar
```

#### Low Latency (Web Services)

```bash
java \
  -Xms4g -Xmx4g \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=50 \
  -XX:+UseStringDeduplication \
  -jar web-service.jar
```

#### Ultra-Low Latency (Trading Systems)

```bash
java \
  -Xms8g -Xmx8g \
  -XX:+UseZGC \
  -XX:+AlwaysPreTouch \
  -jar trading-system.jar
```

#### Container/Kubernetes

```bash
java \
  -XX:+UseContainerSupport \           # Respect container limits
  -XX:MaxRAMPercentage=75.0 \          # Use 75% of container memory
  -XX:InitialRAMPercentage=75.0 \
  -jar containerized-app.jar
```

---

## Monitoring and Profiling

### Built-in Tools

#### jps - List Java Processes

```bash
$ jps -lv
12345 com.example.MyApp -Xmx2g
67890 org.apache.maven.Main
```

#### jstat - GC Statistics

```bash
# GC statistics every 1 second
$ jstat -gc 12345 1000

 S0C    S1C    S0U    S1U   EC       EU       OC        OU      MC     MU
10240  10240  0.0    5120  81920    40960    175104    50000   34816  33792

# S0C/S1C = Survivor capacity
# S0U/S1U = Survivor used
# EC/EU = Eden capacity/used
# OC/OU = Old capacity/used
# MC/MU = Metaspace capacity/used
```

#### jmap - Memory Map

```bash
# Heap summary
$ jmap -heap 12345

# Heap dump
$ jmap -dump:format=b,file=heap.hprof 12345
```

#### jstack - Thread Dump

```bash
# Thread dump
$ jstack 12345 > thread_dump.txt

# Useful for finding deadlocks, blocked threads
```

### GC Logging (Java 11+)

```bash
java \
  -Xlog:gc*:file=gc.log:time,tags:filecount=5,filesize=10M \
  -jar myapp.jar

# Unified logging format:
[2024-01-15T10:30:00.000+0000][gc,start] GC(123) Pause Young
[2024-01-15T10:30:00.015+0000][gc] GC(123) Pause Young 100M->50M(200M) 15ms
```

### Analyzing with VisualVM / JConsole

```bash
# Enable JMX for remote monitoring
java \
  -Dcom.sun.management.jmxremote \
  -Dcom.sun.management.jmxremote.port=9010 \
  -Dcom.sun.management.jmxremote.authenticate=false \
  -Dcom.sun.management.jmxremote.ssl=false \
  -jar myapp.jar
```

---

## Interview Questions

### Basic Level

**Q1: What are the main components of JVM?**
A: ClassLoader (loads classes), Runtime Data Areas (Heap, Stack, Metaspace), Execution Engine (Interpreter, JIT, GC), JNI (native interface).

**Q2: What's the difference between Heap and Stack?**
A: 
- Heap: Shared, stores objects, managed by GC, larger, slower
- Stack: Per-thread, stores method frames/local variables, LIFO, faster

**Q3: What happens when you create a new object?**
A: Memory allocated in Eden (heap), constructor called, reference stored in stack (if local variable).

**Q4: What is garbage collection?**
A: Automatic memory management that reclaims memory from unreachable objects.

### Intermediate Level

**Q5: Explain the generational hypothesis.**
A: Most objects die young. JVM optimizes for this by separating Young Gen (frequent, fast GC) from Old Gen (infrequent, slower GC).

**Q6: What's the difference between Minor GC and Major GC?**
A:
- Minor GC: Collects Young Gen only, fast (~10-50ms)
- Major GC: Collects Old Gen, slower (~100-1000ms)
- Full GC: Collects entire heap + Metaspace

**Q7: Explain the ClassLoader delegation model.**
A: Child classloader delegates to parent first before trying to load itself. Ensures core classes (java.lang.*) always loaded by Bootstrap ClassLoader.

**Q8: What is Metaspace? How is it different from PermGen?**
A: Metaspace stores class metadata in native memory (not heap). Unlike PermGen, it auto-grows, avoiding `OutOfMemoryError: PermGen space`.

### Advanced Level

**Q9: When would you use ZGC over G1?**
A: ZGC for ultra-low latency (<1ms pauses), large heaps (>8GB), or when predictable latency is critical (trading, gaming). G1 is generally sufficient otherwise.

**Q10: How does JIT improve performance?**
A: JIT compiles frequently-executed bytecode to native machine code, applies optimizations (inlining, escape analysis, loop unrolling) for faster execution.

**Q11: How would you diagnose a memory leak?**
A:
1. Enable GC logging, look for growing heap usage
2. Take heap dumps at intervals with `jmap`
3. Analyze with Eclipse MAT or VisualVM
4. Look for objects with growing instance counts
5. Check for unclosed resources, static collections, listeners

**Q12: What causes `OutOfMemoryError: Metaspace`?**
A: Too many classes loaded (common with dynamic classloaders, frameworks with bytecode generation). Solutions: Increase `-XX:MaxMetaspaceSize` or fix classloader leaks.

---

## Next Steps

- Practice with the demo application
- Experiment with different GC settings
- Profile a real application with VisualVM
- Move to: [04-concurrency-multithreading](../04-concurrency-multithreading/) for Java Memory Model coverage
