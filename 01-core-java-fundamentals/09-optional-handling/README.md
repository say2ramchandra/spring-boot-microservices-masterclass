# Optional API in Java

> **Eliminate NullPointerException with proper null handling**

## 📚 Table of Contents

1. [Introduction](#introduction)
2. [Why Optional?](#why-optional)
3. [Creating Optional](#creating-optional)
4. [Checking Values](#checking-values)
5. [Getting Values](#getting-values)
6. [Transforming Values](#transforming-values)
7. [Combining Optionals](#combining-optionals)
8. [Optional with Streams](#optional-with-streams)
9. [Spring Boot Integration](#spring-boot-integration)
10. [Best Practices](#best-practices)
11. [Key Concepts](#key-concepts)

---

## Introduction

### What is Optional?

`Optional<T>` is a container object that may or may not contain a non-null value. It was introduced in **Java 8** to provide a better alternative to null references.

```java
Optional<String> name = Optional.of("Alice");  // Contains "Alice"
Optional<String> empty = Optional.empty();      // Contains nothing
```

### Why Was it Introduced?

**Problem**: Null references cause `NullPointerException` - the "billion dollar mistake"

```java
// Dangerous - NPE waiting to happen
String name = user.getAddress().getCity().toUpperCase();
```

**Solution**: Optional provides explicit handling of missing values

```java
// Safe - forces you to handle missing values
Optional<String> city = Optional.ofNullable(user)
    .map(User::getAddress)
    .map(Address::getCity)
    .map(String::toUpperCase);
```

---

## Why Optional?

### The Problem with Null

```java
// Classic NPE scenario
public String getUserCity(Long userId) {
    User user = userRepository.findById(userId);
    // What if user is null?
    Address address = user.getAddress();
    // What if address is null?
    return address.getCity();
    // What if city is null?
}
```

### Traditional Null Checks

```java
// Defensive null checking - verbose and error-prone
public String getUserCity(Long userId) {
    User user = userRepository.findById(userId);
    if (user != null) {
        Address address = user.getAddress();
        if (address != null) {
            String city = address.getCity();
            if (city != null) {
                return city;
            }
        }
    }
    return "Unknown";
}
```

### With Optional

```java
// Clean, fluent, and explicit about possible absence
public String getUserCity(Long userId) {
    return Optional.ofNullable(userRepository.findById(userId))
        .map(User::getAddress)
        .map(Address::getCity)
        .orElse("Unknown");
}
```

### Benefits

| Benefit | Description |
|---------|-------------|
| **Explicit** | Method signature shows value might be absent |
| **Compile-time safety** | Forces handling of missing values |
| **Fluent API** | Chain operations cleanly |
| **Self-documenting** | Code expresses intent |

---

## Creating Optional

### Optional.of() - Non-null Value

```java
// Use when you're SURE the value is not null
String name = "Alice";
Optional<String> opt = Optional.of(name);

// Throws NullPointerException if null!
Optional<String> bad = Optional.of(null); // NPE!
```

### Optional.ofNullable() - Possibly Null Value

```java
// Use when value might be null
String name = getUserName(); // might return null
Optional<String> opt = Optional.ofNullable(name);

// Safe even if null
Optional<String> opt2 = Optional.ofNullable(null); // Returns empty Optional
```

### Optional.empty() - No Value

```java
// Represents absence of value
Optional<String> empty = Optional.empty();

// Common pattern for conditional returns
public Optional<User> findUser(Long id) {
    User user = database.get(id);
    return user != null ? Optional.of(user) : Optional.empty();
}
```

---

## Checking Values

### isPresent() and isEmpty()

```java
Optional<String> name = Optional.of("Alice");

// Check if value exists
if (name.isPresent()) {
    System.out.println("Name: " + name.get());
}

// Java 11+: Check if empty
if (name.isEmpty()) {
    System.out.println("No name available");
}
```

### ifPresent() - Execute if Present

```java
Optional<String> name = Optional.of("Alice");

// Execute action only if value present
name.ifPresent(n -> System.out.println("Hello, " + n));

// Method reference
name.ifPresent(System.out::println);
```

### ifPresentOrElse() - Java 9+

```java
Optional<String> name = Optional.ofNullable(getName());

// Execute one action if present, another if absent
name.ifPresentOrElse(
    n -> System.out.println("Hello, " + n),    // if present
    () -> System.out.println("Hello, stranger") // if absent
);
```

---

## Getting Values

### get() - Unsafe (Avoid!)

```java
Optional<String> name = Optional.of("Alice");
String value = name.get(); // "Alice"

// DANGEROUS - throws NoSuchElementException if empty!
Optional<String> empty = Optional.empty();
String bad = empty.get(); // NoSuchElementException!
```

### orElse() - Default Value

```java
Optional<String> name = Optional.ofNullable(getName());

// Always provide default
String result = name.orElse("Unknown");

// Note: default is always evaluated (even if not needed)
String result = name.orElse(expensiveOperation()); // expensiveOperation() always runs!
```

### orElseGet() - Lazy Default (Preferred)

```java
Optional<String> name = Optional.ofNullable(getName());

// Supplier only called if Optional is empty
String result = name.orElseGet(() -> expensiveOperation());

// With method reference
String result = name.orElseGet(this::computeDefault);
```

### orElseThrow() - Throw if Absent

```java
Optional<User> user = userRepository.findById(userId);

// Throw custom exception if absent
User found = user.orElseThrow(() -> 
    new UserNotFoundException("User not found: " + userId));

// Java 10+: No-arg version throws NoSuchElementException
User found = user.orElseThrow(); // NoSuchElementException if empty
```

### or() - Alternative Optional (Java 9+)

```java
Optional<String> name = Optional.empty();

// Return another Optional if empty
Optional<String> result = name.or(() -> Optional.of("Default"));
```

---

## Transforming Values

### map() - Transform Value

```java
Optional<String> name = Optional.of("alice");

// Transform the value inside Optional
Optional<String> upper = name.map(String::toUpperCase);
// Optional["ALICE"]

// Chain transformations
Optional<Integer> length = name
    .map(String::toUpperCase)
    .map(String::length);
// Optional[5]

// If empty, map returns empty
Optional<String> empty = Optional.empty();
Optional<String> result = empty.map(String::toUpperCase);
// Optional.empty
```

### flatMap() - Unwrap Nested Optionals

```java
// When transformation returns Optional, use flatMap
public Optional<Address> getAddress(User user) {
    return Optional.ofNullable(user.getAddress());
}

Optional<User> user = findUser(1L);

// map would give Optional<Optional<Address>>
Optional<Optional<Address>> nested = user.map(this::getAddress);

// flatMap unwraps it
Optional<Address> address = user.flatMap(this::getAddress);
```

### filter() - Conditional Selection

```java
Optional<Integer> age = Optional.of(25);

// Keep only if predicate matches
Optional<Integer> adult = age.filter(a -> a >= 18);
// Optional[25]

Optional<Integer> minor = age.filter(a -> a < 18);
// Optional.empty

// Chain filter with map
Optional<String> result = Optional.of("  hello  ")
    .map(String::trim)
    .filter(s -> !s.isEmpty())
    .map(String::toUpperCase);
// Optional["HELLO"]
```

---

## Combining Optionals

### Chaining Multiple Optionals

```java
public Optional<String> getUserCity(Long userId) {
    return userRepository.findById(userId)
        .flatMap(user -> Optional.ofNullable(user.getAddress()))
        .flatMap(address -> Optional.ofNullable(address.getCity()));
}
```

### Combining Two Optionals

```java
Optional<String> firstName = Optional.of("John");
Optional<String> lastName = Optional.of("Doe");

// Combine using flatMap and map
Optional<String> fullName = firstName
    .flatMap(first -> lastName
        .map(last -> first + " " + last));
// Optional["John Doe"]
```

### Multiple Optionals with Stream

```java
Optional<String> opt1 = Optional.of("A");
Optional<String> opt2 = Optional.empty();
Optional<String> opt3 = Optional.of("C");

// Get first present value
Optional<String> first = Stream.of(opt1, opt2, opt3)
    .filter(Optional::isPresent)
    .map(Optional::get)
    .findFirst();
// Optional["A"]

// Java 9+: stream() on Optional
List<String> values = Stream.of(opt1, opt2, opt3)
    .flatMap(Optional::stream)  // Filters out empty ones
    .toList();
// ["A", "C"]
```

---

## Optional with Streams

### stream() Method (Java 9+)

```java
Optional<String> opt = Optional.of("Hello");

// Convert Optional to Stream (0 or 1 element)
Stream<String> stream = opt.stream();

// Useful in flatMap
List<Optional<String>> optionals = List.of(
    Optional.of("A"),
    Optional.empty(),
    Optional.of("C")
);

List<String> values = optionals.stream()
    .flatMap(Optional::stream)  // Filters out empties
    .toList();
// ["A", "C"]
```

### Finding Optional in Stream

```java
List<User> users = getUsers();

// findFirst returns Optional
Optional<User> firstAdult = users.stream()
    .filter(u -> u.getAge() >= 18)
    .findFirst();

// findAny for parallel streams
Optional<User> anyAdult = users.parallelStream()
    .filter(u -> u.getAge() >= 18)
    .findAny();
```

### reduce() Returns Optional

```java
List<Integer> numbers = List.of(1, 2, 3, 4, 5);

// reduce with no identity returns Optional
Optional<Integer> sum = numbers.stream()
    .reduce((a, b) -> a + b);

Optional<Integer> max = numbers.stream()
    .reduce(Integer::max);
```

---

## Spring Boot Integration

### JPA Repository Methods

```java
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Returns Optional by default
    Optional<User> findById(Long id);
    
    // Custom query methods can return Optional
    Optional<User> findByEmail(String email);
    
    Optional<User> findByUsernameAndActive(String username, boolean active);
}
```

### Service Layer Pattern

```java
@Service
public class UserService {
    
    private final UserRepository userRepository;
    
    public UserDTO getUser(Long id) {
        return userRepository.findById(id)
            .map(this::toDTO)
            .orElseThrow(() -> new UserNotFoundException(id));
    }
    
    public Optional<UserDTO> findByEmail(String email) {
        return userRepository.findByEmail(email)
            .map(this::toDTO);
    }
    
    public UserDTO updateUser(Long id, UpdateRequest request) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));
        
        Optional.ofNullable(request.getName())
            .ifPresent(user::setName);
        Optional.ofNullable(request.getEmail())
            .filter(this::isValidEmail)
            .ifPresent(user::setEmail);
        
        return toDTO(userRepository.save(user));
    }
}
```

### Controller Layer

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
        return userService.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/email/{email}")
    public ResponseEntity<UserDTO> getByEmail(@PathVariable String email) {
        return userService.findByEmail(email)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
```

---

## Best Practices

### ✅ DO

```java
// 1. Use Optional as return type for methods that might not return value
public Optional<User> findById(Long id) {
    return Optional.ofNullable(repository.get(id));
}

// 2. Use orElseGet for expensive defaults
user.orElseGet(() -> createDefaultUser());

// 3. Use orElseThrow for required values
User user = findById(id).orElseThrow(() -> new NotFoundException(id));

// 4. Chain transformations
Optional.ofNullable(user)
    .map(User::getAddress)
    .map(Address::getCity)
    .orElse("Unknown");

// 5. Use ifPresentOrElse for side effects (Java 9+)
optionalUser.ifPresentOrElse(
    this::processUser,
    () -> log.warn("User not found")
);
```

### ❌ DON'T

```java
// 1. Don't use Optional for fields
public class User {
    private Optional<String> nickname; // BAD!
}

// 2. Don't use Optional in method parameters
public void process(Optional<String> name) { // BAD!
    // Use overloading or @Nullable instead
}

// 3. Don't use get() without checking
optional.get(); // BAD - use orElse/orElseThrow

// 4. Don't use Optional for collections
Optional<List<User>> users; // BAD - use empty list instead

// 5. Don't use isPresent() + get() pattern
if (optional.isPresent()) {
    String value = optional.get(); // BAD - use map/orElse
}

// 6. Don't return null from Optional methods
public Optional<User> findUser() {
    return null; // VERY BAD - return Optional.empty()
}
```

### When NOT to Use Optional

```java
// 1. Fields - use null or Nullable annotation
class User {
    @Nullable
    private String middleName;
}

// 2. Method parameters - use overloading
void process(String required);
void process(String required, String optional);

// 3. Collections - use empty collection
List<User> findAll() {
    return new ArrayList<>(); // Not Optional<List<User>>
}

// 4. In performance-critical code (Optional has overhead)
```

---

## Key Concepts

### Q1: Why use orElseGet() instead of orElse()?

`orElse()` always evaluates the default, while `orElseGet()` only evaluates when needed.

```java
// orElse: expensiveOperation() ALWAYS runs
String result = opt.orElse(expensiveOperation());

// orElseGet: expensiveOperation() runs only if opt is empty
String result = opt.orElseGet(() -> expensiveOperation());
```

### Q2: Difference between map() and flatMap()?

- `map()`: Transforms value, wraps result in Optional
- `flatMap()`: Transforms value to Optional, doesn't double-wrap

```java
// map() for regular transformations
Optional<Integer> length = opt.map(String::length);

// flatMap() when transformation returns Optional
Optional<Address> address = user.flatMap(User::getOptionalAddress);
```

### Q3: Can Optional contain null?

`Optional.of(null)` throws NPE. Use `Optional.ofNullable(null)` which returns `Optional.empty()`.

### Q4: Why not use Optional for class fields?

- Optional is not Serializable
- Adds memory overhead
- Fields should use `@Nullable` annotation instead
- Optional is designed for return types

---

## Demo: Run the Example

```bash
cd demo-optional-api
mvn compile exec:java -Dexec.mainClass="com.example.OptionalDemo"
```

## Key Takeaways

1. **Use Optional for return types** that might not have a value
2. **Prefer orElseGet()** over orElse() for expensive operations
3. **Use orElseThrow()** when value is required
4. **Chain with map/flatMap/filter** for clean transformations
5. **Don't use for fields, parameters, or collections**
