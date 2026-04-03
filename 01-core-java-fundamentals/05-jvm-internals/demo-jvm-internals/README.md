# JVM Internals Demo

Interactive demonstrations of JVM internals including Garbage Collection, ClassLoaders, Memory Areas, and JIT Compilation.

## Prerequisites

- Java 17+ (tested with Java 21)
- Maven 3.6+

## Quick Start

```bash
# Navigate to demo directory
cd 01-core-java-fundamentals/05-jvm-internals/demo-jvm-internals

# Compile
mvn compile

# Run any demo
mvn exec:java -Dexec.mainClass="com.masterclass.jvm.GarbageCollectionDemo"
mvn exec:java -Dexec.mainClass="com.masterclass.jvm.ClassLoaderDemo"
mvn exec:java -Dexec.mainClass="com.masterclass.jvm.MemoryAreasDemo"
mvn exec:java -Dexec.mainClass="com.masterclass.jvm.JITCompilationDemo"
```

## Demo Classes

### 1. GarbageCollectionDemo

Demonstrates GC behavior with different allocation patterns.

**What You'll Learn:**
- How to monitor GC activity
- Difference between Minor and Major GC
- Effect of object lifecycle on GC

**Run with GC options:**
```bash
# With G1 GC (default) and logging
mvn exec:java -Dexec.mainClass="com.masterclass.jvm.GarbageCollectionDemo" \
  -Dexec.vmArgs="-Xms128m -Xmx256m -Xlog:gc*:stdout:time"

# With Parallel GC
mvn exec:java -Dexec.mainClass="com.masterclass.jvm.GarbageCollectionDemo" \
  -Dexec.vmArgs="-XX:+UseParallelGC -Xms128m -Xmx256m -Xlog:gc*:stdout:time"

# With ZGC (if available)
mvn exec:java -Dexec.mainClass="com.masterclass.jvm.GarbageCollectionDemo" \
  -Dexec.vmArgs="-XX:+UseZGC -Xms128m -Xmx256m -Xlog:gc*:stdout:time"
```

### 2. ClassLoaderDemo

Demonstrates the ClassLoader hierarchy and delegation model.

**What You'll Learn:**
- Bootstrap, Platform, and Application ClassLoaders
- Parent delegation model
- Dynamic class loading techniques
- Custom ClassLoader implementation

**Run:**
```bash
mvn exec:java -Dexec.mainClass="com.masterclass.jvm.ClassLoaderDemo"
```

### 3. MemoryAreasDemo

Demonstrates JVM memory areas: Heap, Stack, and Metaspace.

**What You'll Learn:**
- What data goes in each memory area
- How to monitor memory usage programmatically
- Memory pool structure in modern JVMs

**Run with memory options:**
```bash
# Default settings
mvn exec:java -Dexec.mainClass="com.masterclass.jvm.MemoryAreasDemo"

# Custom heap and stack sizes
mvn exec:java -Dexec.mainClass="com.masterclass.jvm.MemoryAreasDemo" \
  -Dexec.vmArgs="-Xms128m -Xmx512m -Xss512k"

# With native memory tracking
mvn exec:java -Dexec.mainClass="com.masterclass.jvm.MemoryAreasDemo" \
  -Dexec.vmArgs="-XX:NativeMemoryTracking=summary"
```

### 4. JITCompilationDemo

Demonstrates JIT compilation effects and optimizations.

**What You'll Learn:**
- Tiered compilation levels
- Method inlining
- Loop optimizations
- Escape analysis
- Warm-up effects

**Run with JIT options:**
```bash
# Default (tiered compilation)
mvn exec:java -Dexec.mainClass="com.masterclass.jvm.JITCompilationDemo"

# Show compiled methods
mvn exec:java -Dexec.mainClass="com.masterclass.jvm.JITCompilationDemo" \
  -Dexec.vmArgs="-XX:+PrintCompilation"

# Interpreter only (no JIT) - see how slow it is
mvn exec:java -Dexec.mainClass="com.masterclass.jvm.JITCompilationDemo" \
  -Dexec.vmArgs="-Xint"
```

## Useful JVM Monitoring Commands

While running demos, you can use these commands in another terminal:

```bash
# Find Java process ID
jps -l

# Real-time GC statistics (run every 1 second)
jstat -gc <pid> 1000

# Heap histogram (shows object counts)
jmap -histo <pid>

# Thread dump
jstack <pid>

# Flight recorder (collect for 60 seconds)
jcmd <pid> JFR.start duration=60s filename=recording.jfr

# Native memory summary (if NMT enabled)
jcmd <pid> VM.native_memory summary
```

## Key JVM Options Reference

| Option | Description |
|--------|-------------|
| `-Xms<size>` | Initial heap size (e.g., -Xms256m) |
| `-Xmx<size>` | Maximum heap size (e.g., -Xmx1g) |
| `-Xss<size>` | Thread stack size (e.g., -Xss512k) |
| `-XX:+UseG1GC` | Use G1 garbage collector (default) |
| `-XX:+UseZGC` | Use ZGC (low latency) |
| `-XX:+UseParallelGC` | Use Parallel GC (throughput) |
| `-Xlog:gc*:stdout:time` | GC logging to stdout with timestamps |
| `-XX:+PrintCompilation` | Show JIT compilation activity |
| `-XX:NativeMemoryTracking=summary` | Enable native memory tracking |

## Learning Exercises

1. **GC Tuning**: Modify heap sizes and observe GC frequency changes
2. **Memory Leak**: Comment out `longLived.clear()` in GC demo and watch memory grow
3. **ClassLoader**: Try loading your own class dynamically
4. **JIT Warm-up**: Compare execution times with `-Xint` vs normal mode

## Further Reading

- [JVM Specification](https://docs.oracle.com/javase/specs/jvms/se21/html/)
- [GC Tuning Guide](https://docs.oracle.com/en/java/javase/21/gctuning/)
- [JIT Compilation](https://www.oracle.com/technical-resources/articles/java/architect-evans-pt1.html)
