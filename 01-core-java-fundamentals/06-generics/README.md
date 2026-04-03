# Generics in Java

> **Master type-safe programming with Java Generics - essential for Spring Boot development**

## 📚 Table of Contents

1. [Introduction](#introduction)
2. [Why Generics?](#why-generics)
3. [Generic Classes](#generic-classes)
4. [Generic Methods](#generic-methods)
5. [Bounded Type Parameters](#bounded-type-parameters)
6. [Wildcards](#wildcards)
7. [Type Erasure](#type-erasure)
8. [Generics in Spring Boot](#generics-in-spring-boot)
9. [Best Practices](#best-practices)
10. [Key Concepts](#key-concepts)

---

## Introduction

### What are Generics?

**Generics** enable types (classes and interfaces) to be parameters when defining classes, interfaces, and methods. They provide compile-time type safety while allowing code reuse.

### When Were They Introduced?

Generics were introduced in **Java 5 (2004)** to provide compile-time type checking and eliminate the risk of `ClassCastException` at runtime.

---

## Why Generics?

### Problem: Without Generics (Pre-Java 5)

```java
// Without generics - NOT type-safe
List list = new ArrayList();
list.add("Hello");
list.add(123);           // Can add ANY type - dangerous!

// Must cast when retrieving - error-prone
String str = (String) list.get(0);  // OK
String num = (String) list.get(1);  // ClassCastException at runtime!
```

### Solution: With Generics

```java
// With generics - type-safe
List<String> list = new ArrayList<>();
list.add("Hello");
// list.add(123);        // COMPILE ERROR - caught early!

// No casting needed
String str = list.get(0);  // Clean and safe
```

### Benefits

| Benefit | Description |
|---------|-------------|
| **Type Safety** | Catches type errors at compile time |
| **No Casting** | Eliminates need for explicit casting |
| **Code Reuse** | Same code works with different types |
| **Self-Documenting** | Type info is visible in code |

---

## Generic Classes

### Basic Syntax

```java
public class Box<T> {
    private T item;
    
    public void set(T item) {
        this.item = item;
    }
    
    public T get() {
        return item;
    }
}
```

### Using Generic Classes

```java
// Box for String
Box<String> stringBox = new Box<>();
stringBox.set("Hello");
String str = stringBox.get();  // No casting!

// Box for Integer
Box<Integer> intBox = new Box<>();
intBox.set(42);
Integer num = intBox.get();

// Box for custom class
Box<User> userBox = new Box<>();
userBox.set(new User("John"));
User user = userBox.get();
```

### Multiple Type Parameters

```java
public class Pair<K, V> {
    private K key;
    private V value;
    
    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }
    
    public K getKey() { return key; }
    public V getValue() { return value; }
}

// Usage
Pair<String, Integer> pair = new Pair<>("age", 25);
String key = pair.getKey();      // "age"
Integer value = pair.getValue(); // 25
```

### Common Type Parameter Names

| Name | Convention |
|------|------------|
| `T` | Type |
| `E` | Element (collections) |
| `K` | Key (maps) |
| `V` | Value (maps) |
| `N` | Number |
| `R` | Return type |

---

## Generic Methods

### Syntax

```java
public class Utilities {
    
    // Generic method - type parameter declared before return type
    public static <T> T getFirst(List<T> list) {
        return list.isEmpty() ? null : list.get(0);
    }
    
    // Multiple type parameters
    public static <K, V> V getOrDefault(Map<K, V> map, K key, V defaultValue) {
        return map.containsKey(key) ? map.get(key) : defaultValue;
    }
}
```

### Usage

```java
// Type is inferred from arguments
List<String> names = Arrays.asList("Alice", "Bob");
String first = Utilities.getFirst(names);  // "Alice"

List<Integer> numbers = Arrays.asList(1, 2, 3);
Integer firstNum = Utilities.getFirst(numbers);  // 1

// Explicit type specification (rarely needed)
String result = Utilities.<String>getFirst(names);
```

### Generic Method in Non-Generic Class

```java
public class ArrayUtils {
    
    // Swap elements in array
    public static <T> void swap(T[] array, int i, int j) {
        T temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
    
    // Print any array
    public static <T> void printArray(T[] array) {
        for (T element : array) {
            System.out.print(element + " ");
        }
        System.out.println();
    }
}
```

---

## Bounded Type Parameters

### Upper Bounded (`extends`)

Restrict type to a class or its subclasses.

```java
// Only accepts Number or its subclasses (Integer, Double, etc.)
public class NumberBox<T extends Number> {
    private T number;
    
    public NumberBox(T number) {
        this.number = number;
    }
    
    // Can call Number methods
    public double getDoubleValue() {
        return number.doubleValue();
    }
}

// Usage
NumberBox<Integer> intBox = new NumberBox<>(42);
NumberBox<Double> doubleBox = new NumberBox<>(3.14);
// NumberBox<String> strBox = new NumberBox<>("Hi"); // COMPILE ERROR!
```

### Multiple Bounds

```java
// T must extend Number AND implement Comparable
public class ComparableBox<T extends Number & Comparable<T>> {
    private T value;
    
    public ComparableBox(T value) {
        this.value = value;
    }
    
    public boolean isGreaterThan(T other) {
        return value.compareTo(other) > 0;
    }
}

// Usage
ComparableBox<Integer> box = new ComparableBox<>(10);
System.out.println(box.isGreaterThan(5));  // true
```

### Bounded Generic Methods

```java
// Find max in list of comparable elements
public static <T extends Comparable<T>> T findMax(List<T> list) {
    if (list.isEmpty()) return null;
    
    T max = list.get(0);
    for (T item : list) {
        if (item.compareTo(max) > 0) {
            max = item;
        }
    }
    return max;
}

// Usage
List<Integer> numbers = Arrays.asList(3, 1, 4, 1, 5, 9);
Integer max = findMax(numbers);  // 9
```

---

## Wildcards

Wildcards (`?`) represent unknown types and are used for flexibility in method parameters.

### 1. Unbounded Wildcard (`?`)

```java
// Accepts List of any type
public static void printList(List<?> list) {
    for (Object item : list) {
        System.out.println(item);
    }
}

// Usage
printList(Arrays.asList("A", "B", "C"));
printList(Arrays.asList(1, 2, 3));
printList(Arrays.asList(new User("John")));
```

### 2. Upper Bounded Wildcard (`? extends Type`)

**"Producer Extends"** - Read from the collection

```java
// Accepts List of Number or any subtype (Integer, Double, etc.)
public static double sumOfNumbers(List<? extends Number> numbers) {
    double sum = 0;
    for (Number num : numbers) {
        sum += num.doubleValue();
    }
    return sum;
}

// Usage
List<Integer> ints = Arrays.asList(1, 2, 3);
List<Double> doubles = Arrays.asList(1.5, 2.5);

double sum1 = sumOfNumbers(ints);     // Works!
double sum2 = sumOfNumbers(doubles);  // Works!
```

### 3. Lower Bounded Wildcard (`? super Type`)

**"Consumer Super"** - Write to the collection

```java
// Accepts List of Integer or any supertype (Number, Object)
public static void addNumbers(List<? super Integer> list) {
    list.add(1);
    list.add(2);
    list.add(3);
}

// Usage
List<Integer> intList = new ArrayList<>();
List<Number> numList = new ArrayList<>();
List<Object> objList = new ArrayList<>();

addNumbers(intList);  // Works!
addNumbers(numList);  // Works!
addNumbers(objList);  // Works!
```

### PECS Principle

> **Producer Extends, Consumer Super**

| Scenario | Wildcard | Example |
|----------|----------|---------|
| **Read** from collection | `? extends T` | `List<? extends Number>` |
| **Write** to collection | `? super T` | `List<? super Integer>` |
| **Read & Write** | No wildcard | `List<T>` |

```java
// Copy elements from source (producer) to destination (consumer)
public static <T> void copy(List<? super T> dest, List<? extends T> src) {
    for (T item : src) {
        dest.add(item);
    }
}

// Usage
List<Number> numbers = new ArrayList<>();
List<Integer> integers = Arrays.asList(1, 2, 3);
copy(numbers, integers);  // Copies integers to numbers list
```

---

## Type Erasure

### What is Type Erasure?

Java generics use **type erasure** - generic type information is removed at compile time. The JVM sees raw types at runtime.

```java
// What you write:
List<String> strings = new ArrayList<>();
List<Integer> integers = new ArrayList<>();

// What JVM sees (after erasure):
List strings = new ArrayList();
List integers = new ArrayList();

// These are the SAME class at runtime!
System.out.println(strings.getClass() == integers.getClass()); // true
```

### Implications

```java
// Cannot do these at runtime:

// 1. Cannot create generic arrays
// T[] array = new T[10];  // COMPILE ERROR

// 2. Cannot use instanceof with generic types
// if (obj instanceof List<String>) { }  // COMPILE ERROR
if (obj instanceof List<?>) { }  // OK - unbounded wildcard

// 3. Cannot call constructor with type parameter
// T object = new T();  // COMPILE ERROR
```

### Bridge Methods

Compiler generates bridge methods to preserve polymorphism:

```java
public class Node<T> {
    public T data;
    public void setData(T data) { this.data = data; }
}

public class StringNode extends Node<String> {
    @Override
    public void setData(String data) { /* ... */ }
    
    // Compiler generates bridge method:
    // public void setData(Object data) { setData((String) data); }
}
```

---

## Generics in Spring Boot

### 1. JPA Repositories

```java
// Generic repository interface
public interface JpaRepository<T, ID> {
    T findById(ID id);
    List<T> findAll();
    T save(T entity);
    void deleteById(ID id);
}

// Concrete implementation
public interface UserRepository extends JpaRepository<User, Long> {
    // User is the entity, Long is the ID type
    List<User> findByEmail(String email);
}

public interface ProductRepository extends JpaRepository<Product, UUID> {
    // Product entity with UUID as ID
}
```

### 2. Generic Service Layer

```java
// Generic CRUD service
public abstract class GenericService<T, ID> {
    
    protected abstract JpaRepository<T, ID> getRepository();
    
    public T findById(ID id) {
        return getRepository().findById(id)
            .orElseThrow(() -> new EntityNotFoundException());
    }
    
    public List<T> findAll() {
        return getRepository().findAll();
    }
    
    public T save(T entity) {
        return getRepository().save(entity);
    }
    
    public void deleteById(ID id) {
        getRepository().deleteById(id);
    }
}

// Concrete service
@Service
public class UserService extends GenericService<User, Long> {
    
    private final UserRepository userRepository;
    
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    @Override
    protected JpaRepository<User, Long> getRepository() {
        return userRepository;
    }
}
```

### 3. Generic REST Controller

```java
// Generic controller for CRUD operations
public abstract class GenericController<T, ID> {
    
    protected abstract GenericService<T, ID> getService();
    
    @GetMapping("/{id}")
    public ResponseEntity<T> findById(@PathVariable ID id) {
        return ResponseEntity.ok(getService().findById(id));
    }
    
    @GetMapping
    public ResponseEntity<List<T>> findAll() {
        return ResponseEntity.ok(getService().findAll());
    }
    
    @PostMapping
    public ResponseEntity<T> create(@RequestBody T entity) {
        return ResponseEntity.ok(getService().save(entity));
    }
}

// Concrete controller
@RestController
@RequestMapping("/api/users")
public class UserController extends GenericController<User, Long> {
    
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @Override
    protected GenericService<User, Long> getService() {
        return userService;
    }
}
```

### 4. ResponseEntity Pattern

```java
@RestController
public class ApiController {
    
    // Generic response wrapper
    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        User user = userService.findById(id);
        return ResponseEntity.ok(user);
    }
    
    // Generic list response
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }
    
    // Generic Page response
    @GetMapping("/users/page")
    public ResponseEntity<Page<User>> getUsers(Pageable pageable) {
        return ResponseEntity.ok(userService.findAll(pageable));
    }
}
```

---

## Best Practices

### 1. Prefer Generic Methods to Wildcard Types

```java
// ❌ Less clear
public static void swap(List<?> list, int i, int j) {
    swapHelper(list, i, j);
}
private static <E> void swapHelper(List<E> list, int i, int j) {
    list.set(i, list.set(j, list.get(i)));
}

// ✅ Clearer - use generic method directly
public static <T> void swap(List<T> list, int i, int j) {
    T temp = list.get(i);
    list.set(i, list.get(j));
    list.set(j, temp);
}
```

### 2. Use Bounded Wildcards for API Flexibility

```java
// ✅ Flexible API using PECS
public void processItems(Collection<? extends Item> items) {
    for (Item item : items) {
        // process
    }
}
```

### 3. Avoid Raw Types

```java
// ❌ Raw type - loses type safety
List list = new ArrayList();

// ✅ Parameterized type
List<String> list = new ArrayList<>();

// ✅ If type is unknown, use wildcard
List<?> list = unknownList;
```

### 4. Use Diamond Operator

```java
// ❌ Redundant type specification
Map<String, List<Integer>> map = new HashMap<String, List<Integer>>();

// ✅ Diamond operator (Java 7+)
Map<String, List<Integer>> map = new HashMap<>();
```

---

## Key Concepts

### Q1: What is the difference between `List<Object>` and `List<?>`?

**Answer:**
- `List<Object>` - A list specifically of Objects. Can add any Object.
- `List<?>` - A list of unknown type. Cannot add anything (except null).

```java
List<Object> objects = new ArrayList<>();
objects.add("Hello");    // OK
objects.add(123);        // OK

List<?> unknown = new ArrayList<String>();
// unknown.add("Hello"); // COMPILE ERROR
unknown.add(null);       // Only null allowed
```

### Q2: Can you create a generic array?

**Answer:** No, due to type erasure. Arrays are covariant and retain type at runtime, but generics are erased.

```java
// ❌ Not allowed
// T[] array = new T[10];

// ✅ Workarounds
List<T> list = new ArrayList<>();
Object[] array = new Object[10];
T[] array = (T[]) Array.newInstance(clazz, size);
```

### Q3: What's the difference between `<T extends Number>` and `<? extends Number>`?

**Answer:**
- `<T extends Number>` - Used to declare a type parameter that can be referenced
- `<? extends Number>` - Used for flexibility when you don't need to reference the type

```java
// Can reference T
public <T extends Number> T process(T number) {
    return number;  // Can return same type
}

// Cannot reference the type
public void process(List<? extends Number> numbers) {
    // Can only read, cannot add (except null)
}
```

---

## Demo: Run the Example

```bash
cd demo-generics
mvn compile exec:java -Dexec.mainClass="com.example.GenericsDemo"
```

## Key Takeaways

1. **Generics provide compile-time type safety**
2. **Use bounded types (`extends`) for constraints**
3. **Apply PECS principle for wildcards**
4. **Type erasure happens at compile time**
5. **Essential for Spring Boot repositories and services**
