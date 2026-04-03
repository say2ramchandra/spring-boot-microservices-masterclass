# Singleton Pattern Demo - Configuration & Resource Management

> **Demonstrates various Singleton Pattern implementations with Spring Boot**

## 🎯 What is Singleton Pattern?

**Problem:** You need to ensure a class has only one instance and provide a global point of access to it.

**Solution:** Control object creation by making the constructor private and providing a static method to get the single instance.

---

## 🏗️ Implementations Covered

| Type | Thread-Safe | Lazy | Serializable | Reflection-Safe |
|------|-------------|------|--------------|-----------------|
| **Classic (Eager)** | ✅ | ❌ | ❌ | ❌ |
| **Lazy (DCL)** | ✅ | ✅ | ❌ | ❌ |
| **Enum** | ✅ | ❌ | ✅ | ✅ |
| **Spring Bean** | ✅ | ✅ | N/A | N/A |

---

## 🚀 Running the Demo

### Prerequisites
- Java 17+
- Maven 3.8+

### Start the Application
```bash
cd 11-advanced-patterns/01-design-patterns/demo-singleton-pattern
mvn spring-boot:run
```

Application runs on: **http://localhost:8094**

---

## 🧪 Testing the Singleton Pattern

### 1. Classic Singleton (Eager Initialization)
```bash
curl http://localhost:8094/api/singleton/classic
```

**Response:**
```json
{
  "type": "Classic Singleton (Eager)",
  "instanceId": "a1b2c3d4",
  "createdAt": "2024-12-10T10:00:00",
  "accessCount": 2,
  "hashCode": "5f1a3c2e",
  "description": "Same instance: true | Eager initialization at class loading"
}
```

### 2. Lazy Singleton (Double-Checked Locking)
```bash
curl http://localhost:8094/api/singleton/lazy
```

**Response:**
```json
{
  "type": "Lazy Singleton (Double-Checked Locking)",
  "instanceId": "e5f6g7h8",
  "createdAt": "2024-12-10T10:01:00",
  "accessCount": 2,
  "description": "Same instance: true | Created only when first accessed"
}
```

### 3. Enum Singleton (Recommended)
```bash
curl http://localhost:8094/api/singleton/enum
```

**Response:**
```json
{
  "type": "Enum Singleton (Effective Java recommended)",
  "instanceId": "i9j0k1l2",
  "accessCount": 2,
  "description": "Same instance: true | Thread-safe, serialization-safe, reflection-safe | Sample code: ORDER-1702200060000-i9j0k1l2"
}
```

### 4. Spring Singleton Bean
```bash
curl http://localhost:8094/api/singleton/spring
```

**Response:**
```json
{
  "type": "Spring Singleton Bean (@Service)",
  "instanceId": "m3n4o5p6",
  "accessCount": 2,
  "description": "Spring-managed singleton | Processed by Spring Singleton [m3n4o5p6]: test-data"
}
```

### 5. Configuration Holder
```bash
curl http://localhost:8094/api/singleton/config
```

**Response:**
```json
{
  "type": "Configuration Holder Singleton",
  "appName": "Singleton Pattern Demo",
  "version": "1.0.0",
  "maxConnections": 10,
  "timeoutSeconds": 30,
  "cacheEnabled": true
}
```

### 6. Connection Pool (Practical Use Case)
```bash
# Check pool status
curl http://localhost:8094/api/singleton/connection

# Execute a query
curl -X POST "http://localhost:8094/api/singleton/connection/execute?sql=SELECT%20*%20FROM%20products"
```

### 7. Compare All Implementations
```bash
curl http://localhost:8094/api/singleton/compare
```

---

## 📚 Implementation Details

### Classic Singleton (Eager)
```java
public class ClassicSingleton {
    private static final ClassicSingleton INSTANCE = new ClassicSingleton();
    
    private ClassicSingleton() {}  // Private constructor
    
    public static ClassicSingleton getInstance() {
        return INSTANCE;
    }
}
```

### Lazy Singleton (Double-Checked Locking)
```java
public class LazySingleton {
    private static volatile LazySingleton instance;
    
    private LazySingleton() {}
    
    public static LazySingleton getInstance() {
        if (instance == null) {                    // First check
            synchronized (LazySingleton.class) {
                if (instance == null) {            // Second check
                    instance = new LazySingleton();
                }
            }
        }
        return instance;
    }
}
```

### Enum Singleton (Recommended)
```java
public enum EnumSingleton {
    INSTANCE;
    
    public void doSomething() {
        // Business logic
    }
}
```

### Spring Singleton Bean
```java
@Service  // Default scope is singleton
public class MyService {
    // Spring ensures only one instance per container
}
```

---

## 🎯 When to Use Singleton

### ✅ Good Use Cases:
- **Configuration holders** - Application-wide settings
- **Connection pools** - Database/HTTP connections
- **Caches** - Application-level caching
- **Loggers** - Centralized logging
- **Thread pools** - Executor services

### ❌ Avoid When:
- Object state varies per request
- Testing requires different instances
- Multiple instances needed for scaling
- Object holds request-specific data

---

## ⚠️ Common Pitfalls

| Pitfall | Solution |
|---------|----------|
| Not thread-safe | Use DCL or Enum singleton |
| Serialization creates new instance | Use Enum or implement `readResolve()` |
| Reflection creates new instance | Use Enum singleton |
| Hidden dependencies | Use Spring DI instead |
| Hard to test | Use Spring beans for mockability |

---

## 💡 Test Your Knowledge

1. **Why is Enum singleton recommended by Joshua Bloch?**
   - A) It's faster
   - B) It's thread-safe, serialization-safe, and reflection-safe
   - C) It uses less memory
   - D) It's easier to write

2. **What keyword is required for thread-safe lazy singleton?**
   - A) static
   - B) final
   - C) volatile
   - D) synchronized only

3. **What is Spring's default bean scope?**
   - A) Prototype
   - B) Singleton
   - C) Request
   - D) Session

4. **Why use double-checked locking?**
   - A) To check twice for errors
   - B) To minimize synchronization overhead after instance creation
   - C) To create two instances
   - D) To improve memory usage

5. **Which singleton approach is best for Spring applications?**
   - A) Classic singleton
   - B) Lazy singleton
   - C) Spring-managed @Service/@Component beans
   - D) Static utility classes

<details>
<summary>📝 Answers</summary>

1. **B** - Enum is thread-safe, serialization-safe, and reflection-safe by JVM guarantee
2. **C** - volatile ensures visibility across threads
3. **B** - Spring beans are singleton by default
4. **B** - After creation, the check bypasses synchronization for performance
5. **C** - Spring beans are testable, injectable, and lifecycle-managed

</details>

---

## 📚 Further Reading

- [Singleton Pattern - Refactoring Guru](https://refactoring.guru/design-patterns/singleton)
- [Effective Java Item 3: Enum Singleton](https://www.oreilly.com/library/view/effective-java/9780134686097/)
- [Spring Bean Scopes](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-factory-scopes)

---

**Happy Learning!** 🎯✨
