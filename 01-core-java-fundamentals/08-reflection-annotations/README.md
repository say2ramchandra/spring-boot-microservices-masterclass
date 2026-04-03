# Reflection & Annotations in Java

> **Understand how Spring Boot works under the hood**

## 📚 Table of Contents

1. [Introduction](#introduction)
2. [Reflection Basics](#reflection-basics)
3. [Inspecting Classes](#inspecting-classes)
4. [Creating Instances](#creating-instances)
5. [Accessing Fields](#accessing-fields)
6. [Invoking Methods](#invoking-methods)
7. [Annotations](#annotations)
8. [Custom Annotations](#custom-annotations)
9. [Runtime Annotation Processing](#runtime-annotation-processing)
10. [How Spring Uses Reflection](#how-spring-uses-reflection)
11. [Best Practices](#best-practices)
12. [Key Concepts](#key-concepts)

---

## Introduction

### What is Reflection?

**Reflection** is the ability of a program to examine and modify its own structure and behavior at runtime. It allows you to:

- Inspect classes, fields, methods at runtime
- Create objects dynamically
- Invoke methods by name
- Access private members
- Read annotations

### Why Learn Reflection?

Understanding reflection helps you understand how frameworks like Spring work:

| Spring Feature | Uses Reflection For |
|----------------|---------------------|
| `@Autowired` | Field/constructor injection |
| `@Controller` | Finding and registering handlers |
| `@Service`, `@Repository` | Component scanning |
| `@RequestMapping` | URL to method mapping |
| `@Transactional` | Proxy creation |

---

## Reflection Basics

### The Class Object

Every type in Java has a corresponding `Class` object.

```java
// Three ways to get a Class object

// 1. Using .class syntax
Class<String> stringClass = String.class;

// 2. Using getClass() on an instance
String str = "Hello";
Class<?> clazz = str.getClass();

// 3. Using Class.forName() - loads class dynamically
Class<?> dynamicClass = Class.forName("java.lang.String");
```

### Class Information

```java
Class<String> clazz = String.class;

// Basic info
System.out.println("Name: " + clazz.getName());           // java.lang.String
System.out.println("Simple name: " + clazz.getSimpleName()); // String
System.out.println("Package: " + clazz.getPackageName()); // java.lang

// Type checks
System.out.println("Is interface: " + clazz.isInterface());     // false
System.out.println("Is array: " + clazz.isArray());             // false
System.out.println("Is primitive: " + clazz.isPrimitive());     // false
System.out.println("Is enum: " + clazz.isEnum());               // false

// Hierarchy
System.out.println("Superclass: " + clazz.getSuperclass());     // class java.lang.Object
System.out.println("Interfaces: " + Arrays.toString(clazz.getInterfaces()));
```

---

## Inspecting Classes

### Getting Fields

```java
public class User {
    public String name;
    private int age;
    protected String email;
}

Class<User> clazz = User.class;

// Get all public fields (including inherited)
Field[] publicFields = clazz.getFields();

// Get all declared fields (including private, but not inherited)
Field[] allFields = clazz.getDeclaredFields();

for (Field field : allFields) {
    System.out.println("Field: " + field.getName());
    System.out.println("  Type: " + field.getType());
    System.out.println("  Modifiers: " + Modifier.toString(field.getModifiers()));
}
```

### Getting Methods

```java
Class<String> clazz = String.class;

// Get all public methods (including inherited)
Method[] publicMethods = clazz.getMethods();

// Get all declared methods (including private, but not inherited)
Method[] declaredMethods = clazz.getDeclaredMethods();

// Get specific method by name and parameter types
Method lengthMethod = clazz.getMethod("length");
Method substringMethod = clazz.getMethod("substring", int.class, int.class);

for (Method method : declaredMethods) {
    System.out.println("Method: " + method.getName());
    System.out.println("  Return type: " + method.getReturnType());
    System.out.println("  Parameters: " + Arrays.toString(method.getParameterTypes()));
}
```

### Getting Constructors

```java
public class User {
    public User() {}
    public User(String name) {}
    private User(String name, int age) {}
}

Class<User> clazz = User.class;

// All public constructors
Constructor<?>[] publicConstructors = clazz.getConstructors();

// All declared constructors (including private)
Constructor<?>[] allConstructors = clazz.getDeclaredConstructors();

// Specific constructor by parameter types
Constructor<User> noArgConstructor = clazz.getConstructor();
Constructor<User> stringConstructor = clazz.getConstructor(String.class);
```

---

## Creating Instances

### Using Constructor

```java
Class<User> clazz = User.class;

// No-arg constructor
Constructor<User> constructor = clazz.getConstructor();
User user1 = constructor.newInstance();

// Constructor with parameters
Constructor<User> paramConstructor = clazz.getConstructor(String.class, int.class);
User user2 = paramConstructor.newInstance("Alice", 30);

// Private constructor (requires setAccessible)
Constructor<User> privateConstructor = clazz.getDeclaredConstructor(String.class);
privateConstructor.setAccessible(true);  // Bypass access check
User user3 = privateConstructor.newInstance("Bob");
```

### Dynamic Class Loading

```java
// Load class by name (useful for plugins, configurations)
String className = "com.example.UserService";
Class<?> clazz = Class.forName(className);

// Create instance
Object instance = clazz.getDeclaredConstructor().newInstance();
```

---

## Accessing Fields

### Reading Field Values

```java
public class User {
    public String name = "John";
    private int age = 25;
}

User user = new User();
Class<?> clazz = user.getClass();

// Public field
Field nameField = clazz.getField("name");
String name = (String) nameField.get(user);
System.out.println("Name: " + name);  // John

// Private field (requires setAccessible)
Field ageField = clazz.getDeclaredField("age");
ageField.setAccessible(true);
int age = (int) ageField.get(user);
System.out.println("Age: " + age);  // 25
```

### Modifying Field Values

```java
User user = new User();
Class<?> clazz = user.getClass();

// Modify public field
Field nameField = clazz.getField("name");
nameField.set(user, "Alice");

// Modify private field
Field ageField = clazz.getDeclaredField("age");
ageField.setAccessible(true);
ageField.set(user, 30);

System.out.println(user.name);  // Alice
System.out.println(user.age);   // 30 (if we had getter)
```

### Static Fields

```java
public class Config {
    public static String ENV = "development";
    private static int maxConnections = 10;
}

Field envField = Config.class.getField("ENV");
String env = (String) envField.get(null);  // null for static fields

Field maxField = Config.class.getDeclaredField("maxConnections");
maxField.setAccessible(true);
maxField.set(null, 20);  // Modify static field
```

---

## Invoking Methods

### Basic Method Invocation

```java
public class Calculator {
    public int add(int a, int b) {
        return a + b;
    }
    
    private int multiply(int a, int b) {
        return a * b;
    }
}

Calculator calc = new Calculator();
Class<?> clazz = calc.getClass();

// Public method
Method addMethod = clazz.getMethod("add", int.class, int.class);
int sum = (int) addMethod.invoke(calc, 5, 3);
System.out.println("Sum: " + sum);  // 8

// Private method
Method multiplyMethod = clazz.getDeclaredMethod("multiply", int.class, int.class);
multiplyMethod.setAccessible(true);
int product = (int) multiplyMethod.invoke(calc, 5, 3);
System.out.println("Product: " + product);  // 15
```

### Static Methods

```java
public class StringUtils {
    public static String reverse(String str) {
        return new StringBuilder(str).reverse().toString();
    }
}

Method reverseMethod = StringUtils.class.getMethod("reverse", String.class);
String result = (String) reverseMethod.invoke(null, "Hello");  // null for static
System.out.println(result);  // olleH
```

### Methods with Variable Arguments

```java
public class Utils {
    public static void printAll(String... items) {
        for (String item : items) {
            System.out.print(item + " ");
        }
    }
}

Method printMethod = Utils.class.getMethod("printAll", String[].class);
printMethod.invoke(null, (Object) new String[]{"A", "B", "C"});  // A B C
```

---

## Annotations

### What are Annotations?

Annotations are metadata that provide information about the program. They don't directly affect program execution but can be read by the compiler or at runtime.

### Built-in Annotations

```java
// Compile-time annotations
@Override           // Method overrides superclass method
@Deprecated        // Element is deprecated
@SuppressWarnings  // Suppress compiler warnings
@FunctionalInterface // Interface is functional

// Runtime annotations
@Retention(RetentionPolicy.RUNTIME)  // Available at runtime
@Target(ElementType.METHOD)           // Can only apply to methods
```

### Annotation Retention

| Retention | Available At |
|-----------|--------------|
| `SOURCE` | Compile time only (discarded by compiler) |
| `CLASS` | In .class file but not at runtime (default) |
| `RUNTIME` | Available at runtime via reflection |

### Annotation Targets

```java
@Target({
    ElementType.TYPE,        // Class, interface, enum
    ElementType.FIELD,       // Field (including enum constant)
    ElementType.METHOD,      // Method
    ElementType.PARAMETER,   // Method parameter
    ElementType.CONSTRUCTOR, // Constructor
    ElementType.LOCAL_VARIABLE,
    ElementType.ANNOTATION_TYPE,  // Another annotation
    ElementType.PACKAGE
})
```

---

## Custom Annotations

### Creating Annotations

```java
import java.lang.annotation.*;

// Simple marker annotation
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Entity {
}

// Annotation with elements
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Column {
    String name() default "";
    boolean nullable() default true;
    int length() default 255;
}

// Annotation with single value (use 'value')
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Route {
    String value();  // Can use @Route("/users") instead of @Route(value="/users")
}
```

### Using Custom Annotations

```java
@Entity
public class User {
    
    @Column(name = "user_name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "user_email")
    private String email;
    
    @Column  // Uses defaults
    private int age;
    
    @Route("/users")
    public List<User> getAll() {
        return List.of();
    }
}
```

---

## Runtime Annotation Processing

### Reading Class Annotations

```java
Class<User> clazz = User.class;

// Check if annotation is present
if (clazz.isAnnotationPresent(Entity.class)) {
    Entity entity = clazz.getAnnotation(Entity.class);
    System.out.println("This is an entity class");
}

// Get all annotations
Annotation[] annotations = clazz.getAnnotations();
for (Annotation ann : annotations) {
    System.out.println("Annotation: " + ann.annotationType().getSimpleName());
}
```

### Reading Field Annotations

```java
for (Field field : clazz.getDeclaredFields()) {
    if (field.isAnnotationPresent(Column.class)) {
        Column column = field.getAnnotation(Column.class);
        
        String columnName = column.name().isEmpty() ? field.getName() : column.name();
        System.out.println("Field: " + field.getName());
        System.out.println("  Column: " + columnName);
        System.out.println("  Nullable: " + column.nullable());
        System.out.println("  Length: " + column.length());
    }
}
```

### Reading Method Annotations

```java
for (Method method : clazz.getDeclaredMethods()) {
    if (method.isAnnotationPresent(Route.class)) {
        Route route = method.getAnnotation(Route.class);
        System.out.println("Route: " + route.value() + " -> " + method.getName());
    }
}
```

### Building an Annotation Processor

```java
/**
 * Simple dependency injection framework using annotations
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Inject {
}

public class DIContainer {
    private Map<Class<?>, Object> instances = new HashMap<>();
    
    public <T> T getInstance(Class<T> clazz) {
        // Check if already instantiated
        if (instances.containsKey(clazz)) {
            return clazz.cast(instances.get(clazz));
        }
        
        try {
            // Create new instance
            T instance = clazz.getDeclaredConstructor().newInstance();
            instances.put(clazz, instance);
            
            // Inject dependencies
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Inject.class)) {
                    Object dependency = getInstance(field.getType());
                    field.setAccessible(true);
                    field.set(instance, dependency);
                }
            }
            
            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create instance", e);
        }
    }
}

// Usage
public class UserRepository {
    public List<String> findAll() {
        return List.of("Alice", "Bob");
    }
}

public class UserService {
    @Inject
    private UserRepository userRepository;
    
    public void printUsers() {
        userRepository.findAll().forEach(System.out::println);
    }
}

// Bootstrap
DIContainer container = new DIContainer();
UserService service = container.getInstance(UserService.class);
service.printUsers();  // Works! UserRepository was injected
```

---

## How Spring Uses Reflection

### Component Scanning

Spring scans packages to find classes with `@Component`, `@Service`, `@Repository`, etc.

```java
// Simplified component scanner
public class ComponentScanner {
    public List<Class<?>> scan(String packageName) {
        List<Class<?>> components = new ArrayList<>();
        
        // Get all classes in package (simplified)
        for (Class<?> clazz : getClassesInPackage(packageName)) {
            if (clazz.isAnnotationPresent(Component.class) ||
                clazz.isAnnotationPresent(Service.class)) {
                components.add(clazz);
            }
        }
        
        return components;
    }
}
```

### Dependency Injection

```java
// Spring's @Autowired field injection
public class AutowiredProcessor {
    public void processBean(Object bean) {
        for (Field field : bean.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Autowired.class)) {
                Object dependency = findBean(field.getType());
                field.setAccessible(true);
                field.set(bean, dependency);
            }
        }
    }
}
```

### Request Mapping

```java
// Spring MVC request mapping
public class RequestMappingHandler {
    private Map<String, Method> routes = new HashMap<>();
    
    public void registerController(Object controller) {
        Class<?> clazz = controller.getClass();
        
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(GetMapping.class)) {
                GetMapping mapping = method.getAnnotation(GetMapping.class);
                routes.put(mapping.value(), method);
            }
        }
    }
    
    public Object handleRequest(String path, Object controller) {
        Method method = routes.get(path);
        return method.invoke(controller);
    }
}
```

---

## Best Practices

### ✅ DO

```java
// 1. Cache reflection results
private static final Map<Class<?>, Field[]> fieldCache = new ConcurrentHashMap<>();

public Field[] getFields(Class<?> clazz) {
    return fieldCache.computeIfAbsent(clazz, Class::getDeclaredFields);
}

// 2. Handle exceptions properly
try {
    Method method = clazz.getMethod("doSomething");
    method.invoke(instance);
} catch (NoSuchMethodException e) {
    throw new IllegalStateException("Method not found", e);
} catch (IllegalAccessException e) {
    throw new SecurityException("Cannot access method", e);
} catch (InvocationTargetException e) {
    throw new RuntimeException("Method threw exception", e.getCause());
}

// 3. Use setAccessible only when necessary
field.setAccessible(true);  // Only when accessing private members
```

### ❌ DON'T

```java
// 1. Don't overuse reflection - it's slower than direct calls
// Use when dynamic behavior is truly needed

// 2. Don't ignore security implications
// setAccessible bypasses access control

// 3. Don't use reflection for simple cases
// If you know the type, use it directly
```

### Performance Considerations

```java
// Direct call: ~1 nanosecond
user.getName();

// Reflection: ~100 nanoseconds (first call)
// Subsequent cached calls: ~10 nanoseconds

// For hot paths, cache Method/Field objects
private static final Method getName;
static {
    getName = User.class.getMethod("getName");
}
```

---

## Key Concepts

### Q1: What is the difference between `getFields()` and `getDeclaredFields()`?

| getFields() | getDeclaredFields() |
|-------------|---------------------|
| Only public fields | All fields (public, private, protected) |
| Includes inherited | Only declared in this class |

### Q2: How do you invoke a private method using reflection?

```java
Method method = clazz.getDeclaredMethod("privateMethod");
method.setAccessible(true);  // Bypass access check
method.invoke(instance);
```

### Q3: How does Spring's `@Autowired` work?

1. Spring scans for `@Autowired` fields/constructors using reflection
2. Looks up matching beans in the ApplicationContext
3. Uses `Field.set()` or constructor injection to inject dependencies

### Q4: What are the performance implications of reflection?

- **Slower than direct calls** (10-100x for uncached reflection)
- **No compile-time type checking**
- **Can bypass access modifiers** (security concern)
- **Cache Method/Field objects** to improve performance

---

## Demo: Run the Example

```bash
cd demo-reflection-annotations
mvn compile exec:java -Dexec.mainClass="com.example.ReflectionDemo"
```

## Key Takeaways

1. **Reflection allows runtime inspection** of classes, fields, methods
2. **Annotations provide metadata** that can be read at runtime
3. **Spring relies heavily on reflection** for DI, AOP, component scanning
4. **Cache reflection results** for better performance
5. **Use sparingly** - direct code is faster and safer
