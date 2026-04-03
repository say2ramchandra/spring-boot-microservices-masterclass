# Java Serialization

> **Data persistence and transfer mechanisms in Java**

## 📚 Table of Contents

1. [Introduction](#introduction)
2. [Java Serialization](#java-serialization)
3. [Serializable Interface](#serializable-interface)
4. [transient Keyword](#transient-keyword)
5. [Custom Serialization](#custom-serialization)
6. [Externalizable](#externalizable)
7. [serialVersionUID](#serialversionuid)
8. [JSON Serialization](#json-serialization)
9. [Records Serialization](#records-serialization)
10. [Best Practices](#best-practices)
11. [Spring Boot Integration](#spring-boot-integration)

---

## Introduction

### What is Serialization?

**Serialization** is the process of converting an object's state into a byte stream. **Deserialization** is the reverse - reconstructing the object from bytes.

```
Object → Byte Stream → File/Network/Database → Byte Stream → Object
```

### Use Cases

| Use Case | Description |
|----------|-------------|
| **Persistence** | Save objects to files or databases |
| **Network transfer** | Send objects over network (RMI, messaging) |
| **Caching** | Store objects in distributed caches (Redis) |
| **Deep cloning** | Create deep copies of objects |
| **Session management** | Store HTTP session data |

---

## Java Serialization

### Basic Example

```java
import java.io.*;

// 1. Implement Serializable (marker interface)
public class User implements Serializable {
    private String name;
    private int age;
    
    public User(String name, int age) {
        this.name = name;
        this.age = age;
    }
    // getters, setters, toString...
}

// 2. Serialize to file
User user = new User("Alice", 30);
try (ObjectOutputStream oos = new ObjectOutputStream(
        new FileOutputStream("user.ser"))) {
    oos.writeObject(user);
}

// 3. Deserialize from file
try (ObjectInputStream ois = new ObjectInputStream(
        new FileInputStream("user.ser"))) {
    User loaded = (User) ois.readObject();
    System.out.println(loaded);  // User{name='Alice', age=30}
}
```

### What Gets Serialized?

- ✅ Instance fields (non-transient, non-static)
- ✅ Objects referenced by the instance
- ❌ Static fields (belong to class, not instance)
- ❌ transient fields (explicitly excluded)
- ❌ Method code (class must be available)

---

## Serializable Interface

### Marker Interface

`Serializable` has no methods - it's a **marker interface** that signals to the JVM that objects of this class can be serialized.

```java
public class User implements Serializable {
    // No methods to implement!
}
```

### Serialization Inheritance

```java
// Parent must be Serializable OR have no-arg constructor
public class Person implements Serializable {
    private String name;
}

// Child automatically serializable
public class Employee extends Person {
    private String department;  // Serialized
}
```

### Non-Serializable Parent

```java
// Non-serializable parent
public class Entity {
    private Long id;
    
    public Entity() { }  // REQUIRED no-arg constructor!
    public Entity(Long id) { this.id = id; }
}

// Serializable child
public class User extends Entity implements Serializable {
    private String name;
}
// Warning: Entity.id will NOT be serialized
// Parent's no-arg constructor called during deserialization
```

---

## transient Keyword

### Excluding Fields

Use `transient` to exclude sensitive or non-serializable fields.

```java
public class User implements Serializable {
    private String username;
    private transient String password;  // Not serialized!
    private transient Connection dbConn; // Non-serializable, must exclude
    
    // After deserialization:
    // username = "alice"
    // password = null (default for String)
    // dbConn = null
}
```

### Common Uses

```java
public class UserSession implements Serializable {
    private String sessionId;
    private User user;
    
    // Exclude sensitive data
    private transient String authToken;
    
    // Exclude non-serializable objects
    private transient Logger logger;
    private transient Connection connection;
    
    // Exclude computed/cached values (recalculate after load)
    private transient int cachedHashCode;
}
```

---

## Custom Serialization

### writeObject / readObject

Override serialization behavior with private methods.

```java
public class User implements Serializable {
    private String name;
    private transient String password;  // Want to encrypt this
    
    // Custom serialization
    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();  // Serialize non-transient fields
        
        // Manually write transient field (encrypted)
        String encrypted = encrypt(password);
        oos.writeObject(encrypted);
    }
    
    // Custom deserialization
    private void readObject(ObjectInputStream ois) 
            throws IOException, ClassNotFoundException {
        ois.defaultReadObject();  // Deserialize non-transient fields
        
        // Manually read and decrypt
        String encrypted = (String) ois.readObject();
        this.password = decrypt(encrypted);
    }
    
    private String encrypt(String value) { 
        return Base64.getEncoder().encodeToString(value.getBytes()); 
    }
    
    private String decrypt(String value) { 
        return new String(Base64.getDecoder().decode(value)); 
    }
}
```

### Validation During Deserialization

```java
private void readObject(ObjectInputStream ois) 
        throws IOException, ClassNotFoundException {
    ois.defaultReadObject();
    
    // Validate deserialized data
    if (age < 0 || age > 150) {
        throw new InvalidObjectException("Invalid age: " + age);
    }
    if (name == null || name.isBlank()) {
        throw new InvalidObjectException("Name cannot be blank");
    }
}
```

### writeReplace / readResolve

Control object substitution during serialization.

```java
// Singleton pattern with serialization
public class Singleton implements Serializable {
    private static final Singleton INSTANCE = new Singleton();
    
    private Singleton() {}
    
    public static Singleton getInstance() {
        return INSTANCE;
    }
    
    // Ensure singleton on deserialization
    private Object readResolve() {
        return INSTANCE;  // Return existing instance, not deserialized one
    }
}
```

---

## Externalizable

### Full Control Over Serialization

`Externalizable` gives complete control but requires implementing all logic.

```java
public class User implements Externalizable {
    private String name;
    private int age;
    private transient String computed;
    
    // REQUIRED: public no-arg constructor for Externalizable
    public User() {}
    
    public User(String name, int age) {
        this.name = name;
        this.age = age;
    }
    
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        // Manually write each field
        out.writeUTF(name);
        out.writeInt(age);
        // Don't write computed - it's transient
    }
    
    @Override
    public void readExternal(ObjectInput in) 
            throws IOException, ClassNotFoundException {
        // Manually read in SAME ORDER
        this.name = in.readUTF();
        this.age = in.readInt();
        // Recalculate computed value
        this.computed = name + "-" + age;
    }
}
```

### Serializable vs Externalizable

| Aspect | Serializable | Externalizable |
|--------|--------------|----------------|
| Control | Automatic (can customize) | Full manual control |
| Performance | Slower (uses reflection) | Faster |
| Constructor | Any constructor works | Needs public no-arg |
| Default behavior | Serializes all non-transient | Must implement all |
| Inheritance | Automatic | Must handle manually |

---

## serialVersionUID

### Version Control for Serialization

```java
public class User implements Serializable {
    // Explicit version - RECOMMENDED
    private static final long serialVersionUID = 1L;
    
    private String name;
    private int age;
}
```

### Why It Matters

```java
// Version 1
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
}

// Version 2 - added field
public class User implements Serializable {
    private static final long serialVersionUID = 1L;  // Same UID = compatible
    private String name;
    private int age;  // New field - will be 0 when deserializing old data
}

// Version 3 - incompatible change
public class User implements Serializable {
    private static final long serialVersionUID = 2L;  // Changed UID = incompatible
    private String fullName;  // Renamed field
}
// Deserializing V1/V2 data with V3 class throws InvalidClassException
```

### When to Change serialVersionUID

| Change | Action |
|--------|--------|
| Add field | Keep same UID (backward compatible) |
| Remove field | Keep same UID (forward compatible) |
| Change field type | **Change UID** (incompatible) |
| Rename field | **Change UID** (incompatible) |
| Change class hierarchy | **Change UID** (incompatible) |

---

## JSON Serialization

### Jackson (Standard for Spring Boot)

```java
import com.fasterxml.jackson.databind.ObjectMapper;

public class User {
    private String name;
    private int age;
    
    // Jackson needs: either no-arg constructor, or @JsonCreator
    public User() {}
    
    public User(String name, int age) {
        this.name = name;
        this.age = age;
    }
    
    // Getters required for serialization
    public String getName() { return name; }
    public int getAge() { return age; }
    
    // Setters required for deserialization
    public void setName(String name) { this.name = name; }
    public void setAge(int age) { this.age = age; }
}

// Serialize to JSON
ObjectMapper mapper = new ObjectMapper();
User user = new User("Alice", 30);
String json = mapper.writeValueAsString(user);
// {"name":"Alice","age":30}

// Deserialize from JSON
User loaded = mapper.readValue(json, User.class);
```

### Jackson Annotations

```java
public class User {
    @JsonProperty("user_name")  // Rename in JSON
    private String name;
    
    @JsonIgnore  // Exclude from JSON
    private String password;
    
    @JsonFormat(pattern = "yyyy-MM-dd")  // Date format
    private LocalDate birthDate;
    
    @JsonInclude(JsonInclude.Include.NON_NULL)  // Exclude if null
    private String nickname;
}
```

### Immutable Objects with Jackson

```java
public class User {
    private final String name;
    private final int age;
    
    @JsonCreator
    public User(
        @JsonProperty("name") String name,
        @JsonProperty("age") int age
    ) {
        this.name = name;
        this.age = age;
    }
    
    public String getName() { return name; }
    public int getAge() { return age; }
}
```

---

## Records Serialization

### Records with Java Serialization

```java
// Records are automatically serializable if declared
public record User(String name, int age) implements Serializable {
    // serialVersionUID for records is computed from components
}

// Serialize/deserialize like any Serializable
try (ObjectOutputStream oos = new ObjectOutputStream(
        new FileOutputStream("user.ser"))) {
    oos.writeObject(new User("Alice", 30));
}
```

### Records with Jackson

```java
// Records work out-of-box with Jackson 2.12+
public record User(String name, int age) {}

ObjectMapper mapper = new ObjectMapper();
User user = new User("Alice", 30);

String json = mapper.writeValueAsString(user);
// {"name":"Alice","age":30}

User loaded = mapper.readValue(json, User.class);
```

### Records with Annotations

```java
public record User(
    @JsonProperty("user_name") String name,
    @JsonProperty("user_age") int age,
    @JsonIgnore String internalId
) {}
```

---

## Best Practices

### ✅ DO

```java
// 1. Always define serialVersionUID
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
}

// 2. Use transient for sensitive/non-serializable data
private transient String password;
private transient Logger logger;

// 3. Validate during deserialization
private void readObject(ObjectInputStream ois) 
        throws IOException, ClassNotFoundException {
    ois.defaultReadObject();
    if (age < 0) throw new InvalidObjectException("Invalid age");
}

// 4. Prefer JSON (Jackson) for APIs and storage
ObjectMapper mapper = new ObjectMapper();
String json = mapper.writeValueAsString(user);

// 5. Use records for DTOs (immutable, auto-serializable)
public record UserDTO(String name, int age) {}
```

### ❌ DON'T

```java
// 1. Don't serialize sensitive data without encryption
public class User implements Serializable {
    private String creditCardNumber;  // BAD!
}

// 2. Don't ignore serialVersionUID warnings
// SonarQube/IDE warnings exist for a reason!

// 3. Don't use Java serialization for external APIs
// Security risk + not portable

// 4. Don't serialize non-serializable fields
public class User implements Serializable {
    private Connection connection;  // BREAKS serialization!
}

// 5. Don't trust deserialized data
// Always validate!
```

### Security Concerns

```java
// Java serialization is a known security risk!
// https://owasp.org/www-community/vulnerabilities/Deserialization_of_untrusted_data

// Use ObjectInputFilter (Java 9+) to restrict classes
ObjectInputStream ois = new ObjectInputStream(inputStream);
ois.setObjectInputFilter(filterInfo -> {
    if (filterInfo.serialClass() != null) {
        String className = filterInfo.serialClass().getName();
        if (className.startsWith("com.example.")) {
            return ObjectInputFilter.Status.ALLOWED;
        }
        return ObjectInputFilter.Status.REJECTED;
    }
    return ObjectInputFilter.Status.UNDECIDED;
});
```

---

## Spring Boot Integration

### Jackson Auto-Configuration

Spring Boot auto-configures Jackson for REST endpoints:

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @PostMapping
    public User create(@RequestBody User user) {
        // Jackson deserializes JSON to User
        return userService.save(user);
        // Jackson serializes User to JSON
    }
}
```

### Customizing ObjectMapper

```java
@Configuration
public class JacksonConfig {
    
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }
}
```

### Application Properties

```yaml
spring:
  jackson:
    serialization:
      write-dates-as-timestamps: false
      indent-output: true
    deserialization:
      fail-on-unknown-properties: false
    default-property-inclusion: non_null
    date-format: yyyy-MM-dd HH:mm:ss
```

### HTTP Session Serialization

```java
// Session attributes must be Serializable for distributed sessions
@Component
@SessionScope
public class ShoppingCart implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private List<CartItem> items = new ArrayList<>();
    // ...
}
```

---

## Demo: Run the Example

```bash
cd demo-serialization
mvn compile exec:java -Dexec.mainClass="com.example.SerializationDemo"
```

## Key Takeaways

1. **Use JSON (Jackson)** for APIs and modern applications
2. **Use Records** for DTOs - clean and auto-serializable
3. **Always define serialVersionUID** for Serializable classes
4. **Mark sensitive fields transient**
5. **Validate data on deserialization**
6. **Avoid Java serialization for external input** (security risk)
