# Java Quick Revision Guide

> **All essential Java concepts in one place - Perfect for quick revision!**

---

## 1. OOP Fundamentals

### Four Pillars

| Pillar | Definition | Java Implementation |
|--------|------------|---------------------|
| **Encapsulation** | Hide internal state | `private` fields + getters/setters |
| **Inheritance** | IS-A relationship | `extends` (class), `implements` (interface) |
| **Polymorphism** | One interface, many forms | Method overloading & overriding |
| **Abstraction** | Hide complexity | Abstract classes & interfaces |

### Abstract Class vs Interface

| Feature | Abstract Class | Interface |
|---------|---------------|-----------|
| Methods | Abstract + concrete | Abstract + default (Java 8+) |
| Variables | Any type | `public static final` only |
| Constructor | ✅ Yes | ❌ No |
| Inheritance | Single (`extends`) | Multiple (`implements`) |
| Access modifiers | Any | `public` only (methods) |
| **Use when** | Shared code + state | Contract/capability |

```java
abstract class Animal {
    protected String name;
    abstract void sound();           // Must implement
    void breathe() { /* shared */ }  // Inherited
}

interface Flyable {
    void fly();                      // Abstract
    default void land() { /* default impl */ }
}
```

### Overloading vs Overriding

| Aspect | Overloading | Overriding |
|--------|-------------|------------|
| Where | Same class | Subclass |
| Signature | Different params | Same signature |
| Return type | Can differ | Same or covariant |
| Access | Can differ | Same or wider |
| Binding | Compile-time | Runtime |
| `static` | Can overload | Cannot override |

---

## 2. Keywords & Modifiers

### final vs finally vs finalize

| Keyword | Used With | Purpose |
|---------|-----------|---------|
| `final` | Variable | Cannot reassign (constant) |
| `final` | Method | Cannot override |
| `final` | Class | Cannot extend |
| `finally` | try-catch | Always executes (cleanup) |
| `finalize()` | Object method | Called by GC before collection (deprecated!) |

```java
final int MAX = 100;              // Constant
final List<String> list = new ArrayList<>();  // Reference is final, content isn't!
list.add("OK");                   // ✅ Allowed
list = new ArrayList<>();         // ❌ Compile error
```

### static Keyword

| Usage | Meaning |
|-------|---------|
| `static` variable | Shared by all instances (class level) |
| `static` method | Belongs to class, no `this` access |
| `static` block | Runs once when class loads |
| `static` class | Only inner classes (no outer instance needed) |

```java
class Counter {
    static int count = 0;                // Shared
    static { System.out.println("Loaded"); }  // Runs once
    static void increment() { count++; }      // Class method
}
```

### Access Modifiers

| Modifier | Class | Package | Subclass | World |
|----------|-------|---------|----------|-------|
| `public` | ✅ | ✅ | ✅ | ✅ |
| `protected` | ✅ | ✅ | ✅ | ❌ |
| *default* | ✅ | ✅ | ❌ | ❌ |
| `private` | ✅ | ❌ | ❌ | ❌ |

### Other Important Keywords

| Keyword | Purpose |
|---------|---------|
| `this` | Current instance reference |
| `super` | Parent class reference |
| `transient` | Skip during serialization |
| `volatile` | Read from main memory (thread visibility) |
| `synchronized` | Thread-safe block/method |
| `native` | Method implemented in C/C++ |
| `strictfp` | Strict floating-point calculations |

---

## 3. String

### String Immutability & Pool

```java
String s1 = "hello";        // String pool
String s2 = "hello";        // Same reference from pool
String s3 = new String("hello");  // New object on heap

s1 == s2;      // true (same pool reference)
s1 == s3;      // false (different objects)
s1.equals(s3); // true (same content)

s3.intern();   // Add to pool, return pool reference
```

**Why Strings are immutable?**
- Security (passwords, class names)
- Thread-safe
- String pool efficiency
- Caching hashCode

### String vs StringBuilder vs StringBuffer

| Feature | String | StringBuilder | StringBuffer |
|---------|--------|---------------|--------------|
| Mutability | Immutable | Mutable | Mutable |
| Thread-safe | Yes (immutable) | ❌ No | ✅ Yes (synchronized) |
| Performance | Slow (concat) | Fast | Slower than SB |
| **Use when** | Few operations | Single thread, many ops | Multi-thread, many ops |

```java
// Bad - creates many objects
String s = "";
for (int i = 0; i < 1000; i++) s += i;

// Good - single object
StringBuilder sb = new StringBuilder();
for (int i = 0; i < 1000; i++) sb.append(i);
```

### Common String Methods

```java
str.length()                    // Length
str.charAt(0)                   // Character at index
str.substring(1, 4)             // Substring [1, 4)
str.indexOf("x")                // First occurrence (-1 if not found)
str.contains("x")               // Contains check
str.startsWith("x")             // Prefix check
str.split(",")                  // Split to array
str.trim() / str.strip()        // Remove whitespace
str.toUpperCase()               // Case conversion
str.replace("a", "b")           // Replace all
str.isEmpty() / str.isBlank()   // Empty check (isBlank: whitespace only)
String.join(",", list)          // Join collection
```

---

## 4. Object Class Methods

Every class inherits from `Object`:

```java
public class Object {
    public boolean equals(Object obj)   // Content equality
    public int hashCode()               // Hash for HashMap
    public String toString()            // String representation
    protected Object clone()            // Copy object
    public Class<?> getClass()          // Runtime class
    protected void finalize()           // GC callback (deprecated)
    
    // Thread coordination
    public void wait()
    public void notify()
    public void notifyAll()
}
```

### equals() and hashCode() Contract

**Rules:**
1. If `a.equals(b)` → `a.hashCode() == b.hashCode()` (MUST)
2. If `hashCode` differs → `equals` must be false
3. Same `hashCode` doesn't mean equal (collision)

```java
@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Person p = (Person) o;
    return age == p.age && Objects.equals(name, p.name);
}

@Override
public int hashCode() {
    return Objects.hash(name, age);
}
```

**Why override both?** HashMap uses `hashCode()` to find bucket, then `equals()` to find exact match.

---

## 5. Exception Handling

### Hierarchy

```
Throwable
├── Error (Don't catch - JVM issues)
│   ├── OutOfMemoryError
│   ├── StackOverflowError
│   └── ...
└── Exception
    ├── RuntimeException (Unchecked)
    │   ├── NullPointerException
    │   ├── ArrayIndexOutOfBoundsException
    │   ├── IllegalArgumentException
    │   └── ...
    └── Checked Exceptions
        ├── IOException
        ├── SQLException
        └── ...
```

### Checked vs Unchecked

| Type | Must Handle? | Examples |
|------|--------------|----------|
| **Checked** | Yes (compile-time) | IOException, SQLException |
| **Unchecked** | No (runtime) | NullPointerException, IllegalArgumentException |

### throw vs throws

```java
// throws - declares exception (method signature)
public void read() throws IOException {
    // throw - actually throws exception
    throw new IOException("File not found");
}
```

### try-catch-finally vs try-with-resources

```java
// Traditional
try {
    // risky code
} catch (IOException e) {
    // handle
} finally {
    // ALWAYS runs (cleanup)
}

// Modern (Java 7+) - AutoCloseable resources
try (FileReader fr = new FileReader("file.txt");
     BufferedReader br = new BufferedReader(fr)) {
    // resources auto-closed
} catch (IOException e) {
    // handle
}
```

---

## 6. Collections Quick Reference

### Hierarchy

```
Collection                      Map
├── List (ordered, duplicates)  ├── HashMap (O(1), unordered)
│   ├── ArrayList               ├── LinkedHashMap (insertion order)
│   └── LinkedList              ├── TreeMap (sorted, O(log n))
├── Set (unique)                └── Hashtable (legacy, synchronized)
│   ├── HashSet
│   ├── LinkedHashSet
│   └── TreeSet (sorted)
└── Queue
    ├── LinkedList
    ├── PriorityQueue
    └── ArrayDeque
```

### When to Use What

| Need | Use |
|------|-----|
| Fast random access | ArrayList |
| Frequent insert/delete | LinkedList |
| Unique, unordered | HashSet |
| Unique, sorted | TreeSet |
| Key-value, fast | HashMap |
| Key-value, sorted | TreeMap |
| FIFO queue | LinkedList/ArrayDeque |
| Priority-based | PriorityQueue |
| Thread-safe | ConcurrentHashMap, CopyOnWriteArrayList |

### Comparable vs Comparator

| Feature | Comparable | Comparator |
|---------|------------|------------|
| Package | `java.lang` | `java.util` |
| Method | `compareTo(T o)` | `compare(T o1, T o2)` |
| Modifies class | Yes (implements) | No (external) |
| Sorting | Natural order (single) | Custom order (multiple) |

```java
// Comparable - natural ordering
class Person implements Comparable<Person> {
    public int compareTo(Person p) {
        return this.name.compareTo(p.name);
    }
}
Collections.sort(persons);  // Uses compareTo

// Comparator - custom ordering
Comparator<Person> byAge = (p1, p2) -> p1.age - p2.age;
Comparator<Person> byAge = Comparator.comparingInt(Person::getAge);
Collections.sort(persons, byAge);
```

---

## 7. Generics

### Basics

```java
// Generic class
class Box<T> {
    private T item;
    public void set(T item) { this.item = item; }
    public T get() { return item; }
}

// Generic method
public <T> void print(T item) {
    System.out.println(item);
}

// Multiple type parameters
class Pair<K, V> { }
```

### Bounded Types

```java
// Upper bound - T must extend Number
<T extends Number>              // T is Number or subclass

// Multiple bounds
<T extends Number & Comparable<T>>

// Lower bound (wildcards only)
<? super Integer>               // Integer or superclass
```

### Wildcards & PECS

| Wildcard | Meaning | Use For |
|----------|---------|---------|
| `<?>` | Unknown type | Read-only |
| `<? extends T>` | T or subtype | **P**roducer (read) |
| `<? super T>` | T or supertype | **C**onsumer (write) |

```java
// PECS: Producer Extends, Consumer Super
void copy(List<? extends Number> src,   // READ from (producer)
          List<? super Number> dest) {   // WRITE to (consumer)
    for (Number n : src) {
        dest.add(n);
    }
}
```

---

## 8. Memory & GC

### Stack vs Heap

| Stack | Heap |
|-------|------|
| Method calls, local variables | Objects and instance variables |
| Primitive values | Reference types |
| LIFO (Last In First Out) | GC managed |
| Thread-specific | Shared across threads |
| Fast allocation | Slower allocation |
| `StackOverflowError` | `OutOfMemoryError` |

```java
void method() {
    int x = 10;              // Stack (primitive)
    String s = "hello";      // Stack (reference) → Heap (String object)
    Person p = new Person(); // Stack (reference) → Heap (Person object)
}
```

### Java is ALWAYS Pass-by-Value

```java
void modify(int x) { x = 100; }        // Copy of value
void modify(Person p) { p.name = "X"; } // Copy of REFERENCE (same object!)
void reassign(Person p) { p = new Person(); } // Changes local copy only
```

### Garbage Collection

**Eligibility:** Object has no reachable references

```java
Person p = new Person();  // Object created
p = null;                 // Now eligible for GC
// OR
p = new Person();         // Old object eligible
```

**GC Algorithms:**
- Serial GC (single-threaded, small heaps)
- Parallel GC (multi-threaded, throughput)
- G1 GC (default, balanced)
- ZGC (low latency, large heaps)

---

## 9. Serialization

### Basics

```java
class Person implements Serializable {
    private static final long serialVersionUID = 1L;  // Version control
    
    private String name;
    private transient String password;  // NOT serialized
    private static int count;           // NOT serialized (class-level)
}

// Serialize
ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("data.ser"));
oos.writeObject(person);

// Deserialize
ObjectInputStream ois = new ObjectInputStream(new FileInputStream("data.ser"));
Person p = (Person) ois.readObject();
```

### Key Points

- `serialVersionUID`: Version check (change = incompatible)
- `transient`: Skip field during serialization
- `static` fields: Not serialized (belong to class)
- Custom: Override `writeObject()` / `readObject()`
- `Externalizable`: Full control (must implement all)

---

## 10. Cloning & Immutability

### Shallow vs Deep Copy

```java
// Shallow copy - references shared
class Person implements Cloneable {
    String name;
    Address address;  // Reference copied, same object!
    
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();  // Shallow
    }
}

// Deep copy - create new objects
@Override
protected Object clone() throws CloneNotSupportedException {
    Person copy = (Person) super.clone();
    copy.address = new Address(this.address);  // Deep
    return copy;
}
```

### Creating Immutable Class

```java
public final class ImmutablePerson {              // 1. final class
    private final String name;                    // 2. final fields
    private final List<String> hobbies;
    
    public ImmutablePerson(String name, List<String> hobbies) {
        this.name = name;
        this.hobbies = new ArrayList<>(hobbies);  // 3. Defensive copy
    }
    
    public String getName() { return name; }
    
    public List<String> getHobbies() {
        return Collections.unmodifiableList(hobbies);  // 4. Return copy/unmodifiable
    }
    // 5. No setters!
}

// Java 16+ - Records are immutable by default
public record Person(String name, int age) {}
```

---

## 11. Inner Classes

| Type | Declaration | Access to Outer |
|------|-------------|-----------------|
| **Static Nested** | `static class Inner` | Static members only |
| **Inner (Member)** | `class Inner` | All members |
| **Local** | Inside method | Final/effectively final |
| **Anonymous** | Inline definition | Final/effectively final |

```java
class Outer {
    private int x = 10;
    
    // Static nested - no outer instance needed
    static class StaticNested {
        void method() { /* can't access x */ }
    }
    
    // Inner class - needs outer instance
    class Inner {
        void method() { System.out.println(x); }  // Can access
    }
    
    void method() {
        int y = 20;  // Effectively final
        
        // Local class
        class Local { void m() { System.out.println(y); } }
        
        // Anonymous class
        Runnable r = new Runnable() {
            @Override
            public void run() { System.out.println(y); }
        };
        
        // Lambda (Java 8+)
        Runnable r2 = () -> System.out.println(y);
    }
}

// Usage
Outer.StaticNested sn = new Outer.StaticNested();
Outer.Inner inner = new Outer().new Inner();
```

---

## 12. Enums

```java
public enum Status {
    PENDING(0),
    ACTIVE(1),
    COMPLETED(2);
    
    private final int code;
    
    Status(int code) { this.code = code; }  // Constructor (private)
    
    public int getCode() { return code; }
    
    // Override per constant
    public boolean isActive() {
        return this == ACTIVE;
    }
}

// Usage
Status s = Status.ACTIVE;
Status s2 = Status.valueOf("ACTIVE");  // From string
Status[] all = Status.values();        // All values
int ordinal = s.ordinal();             // Index (0, 1, 2)
String name = s.name();                // "ACTIVE"

// Switch
switch (status) {
    case PENDING -> System.out.println("Waiting");
    case ACTIVE -> System.out.println("Running");
    case COMPLETED -> System.out.println("Done");
}
```

---

## 13. Multithreading Essentials

### Thread States

```
NEW → RUNNABLE ←→ BLOCKED/WAITING/TIMED_WAITING → TERMINATED
```

### Creating Threads

```java
// 1. Extend Thread
class MyThread extends Thread {
    public void run() { /* work */ }
}
new MyThread().start();

// 2. Implement Runnable (preferred)
Runnable r = () -> { /* work */ };
new Thread(r).start();

// 3. ExecutorService (best)
ExecutorService executor = Executors.newFixedThreadPool(4);
executor.submit(() -> { /* work */ });
executor.shutdown();
```

### synchronized & volatile

```java
// synchronized - mutual exclusion (only one thread)
public synchronized void method() { /* thread-safe */ }

synchronized (lockObject) {
    // Thread-safe block
}

// volatile - visibility (read from main memory)
private volatile boolean running = true;
// Changes visible to all threads immediately
```

### wait() / notify()

```java
synchronized (lock) {
    while (!condition) {
        lock.wait();      // Release lock, wait for notify
    }
    // Proceed when condition is true
}

synchronized (lock) {
    condition = true;
    lock.notify();        // Wake one waiting thread
    // lock.notifyAll(); // Wake all waiting threads
}
```

### Deadlock Conditions (All 4 needed)

1. **Mutual Exclusion** - Resource not shareable
2. **Hold and Wait** - Hold one, wait for another
3. **No Preemption** - Can't force release
4. **Circular Wait** - A waits for B, B waits for A

**Prevention:** Lock ordering, timeout, tryLock()

---

## 14. Java 8+ Features Quick Reference

### Lambda Expressions

```java
// Before
Comparator<String> c = new Comparator<String>() {
    public int compare(String a, String b) { return a.compareTo(b); }
};

// Lambda
Comparator<String> c = (a, b) -> a.compareTo(b);

// Method reference
Comparator<String> c = String::compareTo;
```

### Functional Interfaces

| Interface | Method | Use Case |
|-----------|--------|----------|
| `Predicate<T>` | `boolean test(T)` | Filter, condition |
| `Function<T,R>` | `R apply(T)` | Transform |
| `Consumer<T>` | `void accept(T)` | Process (no return) |
| `Supplier<T>` | `T get()` | Factory, lazy |
| `UnaryOperator<T>` | `T apply(T)` | Same type transform |
| `BinaryOperator<T>` | `T apply(T,T)` | Combine two |

### Streams

```java
list.stream()
    .filter(x -> x > 10)           // Predicate
    .map(x -> x * 2)               // Function
    .sorted()                       // Comparable
    .distinct()                     // Remove duplicates
    .limit(5)                       // First 5
    .skip(2)                        // Skip first 2
    .forEach(System.out::println);  // Terminal

// Collectors
.collect(Collectors.toList())
.collect(Collectors.toSet())
.collect(Collectors.toMap(k -> k, v -> v))
.collect(Collectors.groupingBy(Person::getCity))
.collect(Collectors.joining(", "))

// Reduce
int sum = list.stream().reduce(0, Integer::sum);
```

### Optional

```java
Optional<String> opt = Optional.ofNullable(value);

opt.isPresent()                    // Check
opt.orElse("default")              // Default value
opt.orElseGet(() -> compute())     // Lazy default
opt.orElseThrow()                  // Throw if empty
opt.map(String::toUpperCase)       // Transform
opt.filter(s -> s.length() > 3)    // Filter
opt.ifPresent(System.out::println) // If exists
```

### Other Java 8+ Features

```java
// Default methods in interfaces
interface MyInterface {
    default void method() { /* implementation */ }
}

// Static methods in interfaces
interface MyInterface {
    static void utility() { /* implementation */ }
}

// Method references
list.forEach(System.out::println);    // Instance method
list.stream().map(String::length);    // Instance method (on element)
list.stream().map(String::valueOf);   // Static method
list.stream().map(Person::new);       // Constructor
```

---

## 15. Common Design Patterns

### Singleton Pattern

```java
// Eager initialization
public class Singleton {
    private static final Singleton INSTANCE = new Singleton();
    private Singleton() {}
    public static Singleton getInstance() { return INSTANCE; }
}

// Lazy with double-checked locking
public class Singleton {
    private static volatile Singleton instance;
    private Singleton() {}
    public static Singleton getInstance() {
        if (instance == null) {
            synchronized (Singleton.class) {
                if (instance == null) {
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }
}

// Best: Enum singleton
public enum Singleton {
    INSTANCE;
    public void doSomething() { }
}
```

### Builder Pattern

```java
public class Person {
    private final String name;
    private final int age;
    
    private Person(Builder builder) {
        this.name = builder.name;
        this.age = builder.age;
    }
    
    public static class Builder {
        private String name;
        private int age;
        
        public Builder name(String name) { this.name = name; return this; }
        public Builder age(int age) { this.age = age; return this; }
        public Person build() { return new Person(this); }
    }
}

Person p = new Person.Builder().name("John").age(30).build();
```

### Factory Pattern

```java
interface Shape { void draw(); }
class Circle implements Shape { public void draw() { } }
class Square implements Shape { public void draw() { } }

class ShapeFactory {
    public static Shape create(String type) {
        return switch (type) {
            case "circle" -> new Circle();
            case "square" -> new Square();
            default -> throw new IllegalArgumentException();
        };
    }
}
```

---

## Quick Reference Tables

### Important Interfaces

| Interface | Purpose | Key Method |
|-----------|---------|------------|
| `Comparable<T>` | Natural ordering | `compareTo(T)` |
| `Comparator<T>` | Custom ordering | `compare(T, T)` |
| `Iterable<T>` | For-each support | `iterator()` |
| `Serializable` | Serialization marker | (none) |
| `Cloneable` | Clone support marker | (none) |
| `AutoCloseable` | Try-with-resources | `close()` |
| `Runnable` | Thread task | `run()` |
| `Callable<V>` | Thread task with result | `call()` |

### Null-Safe Operations

```java
// Objects utility
Objects.equals(a, b)           // Null-safe equals
Objects.hashCode(obj)          // Null-safe hashCode
Objects.requireNonNull(obj)    // Throw if null
Objects.isNull(obj)            // Check null
Objects.nonNull(obj)           // Check not null

// String
String.valueOf(obj)            // "null" if null
Optional.ofNullable(obj)       // Wrap possibly null
```

### Common Exceptions

| Exception | Cause |
|-----------|-------|
| `NullPointerException` | Calling method on null |
| `ArrayIndexOutOfBoundsException` | Invalid array index |
| `ClassCastException` | Invalid cast |
| `IllegalArgumentException` | Invalid method argument |
| `IllegalStateException` | Method called at wrong time |
| `ConcurrentModificationException` | Collection modified during iteration |
| `UnsupportedOperationException` | Operation not supported |

---

## 🎯 Revision Tips

1. **Always mention trade-offs** (ArrayList vs LinkedList)
2. **Know Big-O** for common operations
3. **Explain WHY** not just WHAT
4. **Use concrete examples** when explaining concepts
5. **Connect to real-world** Spring Boot usage

---

_This guide covers all essential Java core topics. For deep dives, refer to the individual modules._
