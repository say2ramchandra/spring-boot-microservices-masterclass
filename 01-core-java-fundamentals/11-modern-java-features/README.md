# Modern Java Features (17+)

> **Leverage the latest Java features for cleaner, more expressive code**

## 📚 Table of Contents

1. [Introduction](#introduction)
2. [Records](#records)
3. [Sealed Classes](#sealed-classes)
4. [Pattern Matching](#pattern-matching)
5. [Switch Expressions](#switch-expressions)
6. [Text Blocks](#text-blocks)
7. [Local Variable Type Inference](#local-variable-type-inference)
8. [Helpful NullPointerExceptions](#helpful-nullpointerexceptions)
9. [Spring Boot Integration](#spring-boot-integration)
10. [Migration Tips](#migration-tips)

---

## Introduction

### Java Evolution Timeline

| Version | Year | Key Features |
|---------|------|--------------|
| Java 8 | 2014 | Lambdas, Streams, Optional |
| Java 11 | 2018 | var, HTTP Client, String methods |
| Java 14 | 2020 | Switch expressions (final) |
| Java 15 | 2020 | Text blocks (final) |
| Java 16 | 2021 | Records (final), Pattern matching for instanceof |
| Java 17 | 2021 | Sealed classes (final) - **LTS** |
| Java 21 | 2023 | Virtual threads, Pattern matching - **LTS** |

### Using Modern Features in Spring Boot

```xml
<!-- Spring Boot 3.x requires Java 17+ -->
<properties>
    <java.version>17</java.version>
</properties>
```

---

## Records

Records are immutable data carriers - perfect for DTOs, value objects, and API responses.

### Basic Syntax

```java
// Traditional class: 50+ lines
public class User {
    private final String name;
    private final String email;
    
    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }
    
    public String getName() { return name; }
    public String getEmail() { return email; }
    
    @Override public boolean equals(Object o) { /* ... */ }
    @Override public int hashCode() { /* ... */ }
    @Override public String toString() { /* ... */ }
}

// Record: 1 line!
public record User(String name, String email) {}
```

### What You Get Automatically

```java
public record User(String name, String email) {}

// Automatically generated:
// - Private final fields
// - Canonical constructor
// - Accessor methods: name(), email()  (not getName()!)
// - equals() based on all fields
// - hashCode() based on all fields
// - toString() showing all fields

User user = new User("Alice", "alice@example.com");
System.out.println(user.name());     // "Alice"
System.out.println(user.email());    // "alice@example.com"
System.out.println(user);            // User[name=Alice, email=alice@example.com]
```

### Custom Constructors

```java
public record User(String name, String email) {
    
    // Compact constructor - validation without explicit assignment
    public User {
        Objects.requireNonNull(name, "Name cannot be null");
        Objects.requireNonNull(email, "Email cannot be null");
        name = name.trim();
        email = email.toLowerCase();
    }
}

// Alternative: Explicit canonical constructor
public record Point(int x, int y) {
    public Point(int x, int y) {
        this.x = Math.max(0, x);  // Ensure non-negative
        this.y = Math.max(0, y);
    }
    
    // Additional constructor
    public Point(int value) {
        this(value, value);  // Must call canonical constructor
    }
}
```

### Adding Methods

```java
public record Rectangle(int width, int height) {
    
    // Instance methods
    public int area() {
        return width * height;
    }
    
    public int perimeter() {
        return 2 * (width + height);
    }
    
    // Static methods and fields allowed
    public static Rectangle square(int size) {
        return new Rectangle(size, size);
    }
}

Rectangle rect = new Rectangle(5, 3);
System.out.println(rect.area());       // 15
System.out.println(rect.perimeter());  // 16
Rectangle sq = Rectangle.square(4);    // 4x4 square
```

### Records as DTOs

```java
// Request DTO
public record CreateUserRequest(
    @NotBlank String name,
    @Email String email,
    @Min(18) int age
) {}

// Response DTO
public record UserResponse(
    Long id,
    String name,
    String email,
    LocalDateTime createdAt
) {
    // Factory method from entity
    public static UserResponse from(User user) {
        return new UserResponse(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getCreatedAt()
        );
    }
}
```

### Limitations

```java
// Records CANNOT:
// - Extend other classes (implicitly extend Record)
// - Be abstract
// - Have mutable fields (always final)
// - Declare instance fields (only in header)

// Records CAN:
// - Implement interfaces
// - Be generic
// - Be nested in other classes
// - Have static fields and methods

public record Pair<T, U>(T first, U second) implements Comparable<Pair<T, U>> {
    // ...
}
```

---

## Sealed Classes

Control which classes can extend your class - perfect for domain modeling.

### Basic Syntax

```java
// Sealed class specifies permitted subclasses
public sealed class Shape 
    permits Circle, Rectangle, Triangle {
    // ...
}

// Subclasses must be final, sealed, or non-sealed
public final class Circle extends Shape {
    private final double radius;
    // ...
}

public final class Rectangle extends Shape {
    private final double width, height;
    // ...
}

public final class Triangle extends Shape {
    private final double a, b, c;
    // ...
}

// No other class can extend Shape!
public class Hexagon extends Shape {}  // COMPILE ERROR!
```

### Subclass Modifiers

```java
public sealed class Vehicle permits Car, Truck, Motorcycle {
}

// final - no further subclasses
public final class Motorcycle extends Vehicle { }

// sealed - controlled subclasses
public sealed class Car extends Vehicle permits Sedan, SUV { }
public final class Sedan extends Car { }
public final class SUV extends Car { }

// non-sealed - open for extension
public non-sealed class Truck extends Vehicle { }
public class DeliveryTruck extends Truck { }  // Allowed!
```

### With Records

```java
// Sealed interface with record implementations
public sealed interface Result<T> 
    permits Success, Failure {
}

public record Success<T>(T value) implements Result<T> { }
public record Failure<T>(String error) implements Result<T> { }

// Usage
public Result<User> findUser(Long id) {
    try {
        User user = repository.findById(id);
        return new Success<>(user);
    } catch (Exception e) {
        return new Failure<>(e.getMessage());
    }
}
```

### Benefits with Pattern Matching

```java
public sealed interface Shape permits Circle, Rectangle, Triangle { }

// Compiler knows all possible types - no default needed!
public double area(Shape shape) {
    return switch (shape) {
        case Circle c -> Math.PI * c.radius() * c.radius();
        case Rectangle r -> r.width() * r.height();
        case Triangle t -> calculateTriangleArea(t);
        // No default needed - compiler knows all cases covered!
    };
}
```

---

## Pattern Matching

### Pattern Matching for instanceof (Java 16)

```java
// Old way
if (obj instanceof String) {
    String s = (String) obj;
    System.out.println(s.length());
}

// New way - binding variable
if (obj instanceof String s) {
    System.out.println(s.length());  // s is already a String!
}

// With negation
if (!(obj instanceof String s)) {
    return;
}
// s is available here due to flow scoping
System.out.println(s.length());
```

### Pattern Matching in Switch (Java 21)

```java
// Type patterns
public String describe(Object obj) {
    return switch (obj) {
        case Integer i -> "Integer: " + i;
        case Long l -> "Long: " + l;
        case Double d -> "Double: " + d;
        case String s -> "String of length " + s.length();
        case List<?> list -> "List with " + list.size() + " elements";
        case null -> "null value";
        default -> "Unknown type";
    };
}

// With guards (when clause)
public String categorize(Object obj) {
    return switch (obj) {
        case Integer i when i < 0 -> "Negative integer";
        case Integer i when i == 0 -> "Zero";
        case Integer i -> "Positive integer: " + i;
        case String s when s.isEmpty() -> "Empty string";
        case String s -> "String: " + s;
        default -> "Other";
    };
}
```

### Record Patterns (Java 21)

```java
record Point(int x, int y) { }
record Rectangle(Point topLeft, Point bottomRight) { }

// Deconstruct records in patterns
public void process(Object obj) {
    if (obj instanceof Point(int x, int y)) {
        System.out.println("Point at (" + x + ", " + y + ")");
    }
}

// Nested deconstruction
public int calculateArea(Rectangle rect) {
    if (rect instanceof Rectangle(Point(int x1, int y1), Point(int x2, int y2))) {
        return Math.abs((x2 - x1) * (y2 - y1));
    }
    return 0;
}

// In switch
public String describeShape(Object shape) {
    return switch (shape) {
        case Point(int x, int y) -> "Point at " + x + "," + y;
        case Rectangle(Point p1, Point p2) -> "Rectangle from " + p1 + " to " + p2;
        default -> "Unknown shape";
    };
}
```

---

## Switch Expressions

### Basic Switch Expression

```java
// Old switch statement
String dayType;
switch (day) {
    case MONDAY:
    case TUESDAY:
    case WEDNESDAY:
    case THURSDAY:
    case FRIDAY:
        dayType = "Weekday";
        break;
    case SATURDAY:
    case SUNDAY:
        dayType = "Weekend";
        break;
    default:
        dayType = "Unknown";
}

// New switch expression
String dayType = switch (day) {
    case MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY -> "Weekday";
    case SATURDAY, SUNDAY -> "Weekend";
};
```

### Arrow Syntax vs Colon

```java
// Arrow syntax (no fall-through, no break needed)
int numLetters = switch (day) {
    case MONDAY, FRIDAY, SUNDAY -> 6;
    case TUESDAY -> 7;
    case THURSDAY, SATURDAY -> 8;
    case WEDNESDAY -> 9;
};

// Colon syntax with yield (when you need multiple statements)
int numLetters = switch (day) {
    case MONDAY, FRIDAY, SUNDAY:
        System.out.println("Six letters");
        yield 6;
    case TUESDAY:
        yield 7;
    default:
        yield -1;
};
```

### Exhaustiveness

```java
// Enum switch must be exhaustive
enum Status { ACTIVE, INACTIVE, PENDING }

// Must cover all cases (or use default)
String message = switch (status) {
    case ACTIVE -> "User is active";
    case INACTIVE -> "User is inactive";
    case PENDING -> "User is pending";
    // No default needed - all enum values covered
};

// With sealed classes - compiler knows all subtypes
sealed interface Animal permits Dog, Cat { }
final class Dog implements Animal { }
final class Cat implements Animal { }

String sound = switch (animal) {
    case Dog d -> "Woof";
    case Cat c -> "Meow";
    // No default needed!
};
```

---

## Text Blocks

Multi-line string literals with preserved formatting.

### Basic Syntax

```java
// Old way - messy escaping
String json = "{\n" +
    "  \"name\": \"Alice\",\n" +
    "  \"age\": 30\n" +
    "}";

// Text block - clean and readable
String json = """
    {
      "name": "Alice",
      "age": 30
    }
    """;
```

### SQL Queries

```java
String query = """
    SELECT u.id, u.name, u.email
    FROM users u
    JOIN orders o ON u.id = o.user_id
    WHERE o.status = 'ACTIVE'
      AND o.created_at > ?
    ORDER BY o.created_at DESC
    """;
```

### HTML Templates

```java
String html = """
    <html>
        <head>
            <title>%s</title>
        </head>
        <body>
            <h1>Welcome, %s!</h1>
        </body>
    </html>
    """.formatted(title, username);
```

### Indentation Control

```java
// Incidental whitespace is removed
// Content starts from the leftmost character

String s1 = """
    Hello
    World
    """;
// Result: "Hello\nWorld\n"

String s2 = """
        Hello
        World
        """;
// Result: "Hello\nWorld\n" (same - extra indent is incidental)

// Trailing """ controls final newline
String withNewline = """
    text
    """;   // Ends with newline

String noNewline = """
    text""";  // No trailing newline
```

### Escape Sequences

```java
// New escape: \s (single space that prevents stripping)
// New escape: \ at end of line (no newline)

String longLine = """
    This is a very long line that \
    continues on the next line \
    but renders as one line.""";
// Result: "This is a very long line that continues on the next line but renders as one line."

String preserveSpaces = """
    trailing spaces   \s
    are preserved here\s
    """;
```

---

## Local Variable Type Inference

### var Keyword (Java 10)

```java
// Type inferred from right side
var name = "Alice";           // String
var age = 30;                 // int
var users = new ArrayList<User>();  // ArrayList<User>
var stream = list.stream();   // Stream<T>

// Great for complex types
var entries = map.entrySet();  // Set<Map.Entry<K,V>>
var iterator = list.iterator();  // Iterator<T>
```

### When to Use var

```java
// ✅ Good uses
var users = userRepository.findAll();  // Clear from method name
var response = restTemplate.getForObject(url, User.class);
var builder = new StringBuilder();

// ❌ Avoid when type isn't obvious
var result = getData();  // What type is this?
var x = 0;               // int or long? Prefer explicit
var items = new ArrayList<>();  // ArrayList<Object> - lost type info!
```

### Limitations

```java
// Cannot use var with:
var x;                    // No initializer
var x = null;             // Cannot infer type
var lambda = () -> {};    // Lambda needs target type
var methodRef = String::length;  // Method reference needs target type

// Cannot use for:
// - Fields
// - Method parameters
// - Return types
```

---

## Helpful NullPointerExceptions

### Before Java 14

```java
user.getAddress().getCity().toUpperCase();
// Exception: NullPointerException
// Which one was null? No idea!
```

### After Java 14

```java
user.getAddress().getCity().toUpperCase();
// Exception: NullPointerException: Cannot invoke "Address.getCity()" 
//            because the return value of "User.getAddress()" is null
```

### Enable in JVM (default in Java 17+)

```bash
java -XX:+ShowCodeDetailsInExceptionMessages MyApp
```

---

## Spring Boot Integration

### Records as DTOs

```java
// Request/Response records work great with Spring
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest request) {
        User user = userService.create(request);
        return ResponseEntity.ok(UserResponse.from(user));
    }
}

public record CreateUserRequest(
    @NotBlank String name,
    @Email String email
) {}

public record UserResponse(Long id, String name, String email) {
    public static UserResponse from(User entity) {
        return new UserResponse(entity.getId(), entity.getName(), entity.getEmail());
    }
}
```

### Sealed Classes for Domain Modeling

```java
public sealed interface PaymentMethod 
    permits CreditCard, BankTransfer, PayPal {
}

public record CreditCard(String number, YearMonth expiry) implements PaymentMethod {}
public record BankTransfer(String iban, String bic) implements PaymentMethod {}
public record PayPal(String email) implements PaymentMethod {}

// Service with exhaustive handling
@Service
public class PaymentService {
    public PaymentResult process(PaymentMethod method, Money amount) {
        return switch (method) {
            case CreditCard cc -> processCreditCard(cc, amount);
            case BankTransfer bt -> processBankTransfer(bt, amount);
            case PayPal pp -> processPayPal(pp, amount);
        };
    }
}
```

### Text Blocks for Native Queries

```java
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    @Query(value = """
        SELECT o.* FROM orders o
        JOIN users u ON o.user_id = u.id
        WHERE u.status = 'ACTIVE'
          AND o.created_at > :since
        ORDER BY o.created_at DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<Order> findRecentActiveOrders(
        @Param("since") LocalDateTime since,
        @Param("limit") int limit
    );
}
```

---

## Migration Tips

### Gradual Adoption

1. **Start with Records** - Convert DTOs first
2. **Use var** - In new code where type is clear
3. **Adopt Text Blocks** - For SQL, JSON, HTML
4. **Add Sealed Classes** - For new domain models

### IDE Support

- IntelliJ IDEA: Excellent support for all features
- VS Code: Java Extension Pack supports modern features
- Eclipse: Recent versions support Java 17+

### Common Mistakes

```java
// Don't mix old and new patterns unnecessarily
record User(String name) {
    public String getName() { return name; }  // Redundant! Use name()
}

// Don't overuse var
var x = someMethod();  // What type? Be explicit when unclear

// Don't forget record immutability
User user = new User("Alice");
user.setName("Bob");  // COMPILE ERROR - no setters!
```

---

## Demo: Run the Example

```bash
cd demo-modern-java-features
mvn compile exec:java -Dexec.mainClass="com.example.ModernJavaDemo"
```

## Key Takeaways

1. **Records** - Perfect for DTOs and value objects
2. **Sealed Classes** - Control inheritance hierarchy
3. **Pattern Matching** - Cleaner type checks and decomposition
4. **Switch Expressions** - Return values, no fall-through
5. **Text Blocks** - Clean multi-line strings
6. **var** - Type inference for cleaner code
