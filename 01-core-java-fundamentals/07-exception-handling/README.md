# Exception Handling in Java

> **Master exception handling for robust Spring Boot applications**

## 📚 Table of Contents

1. [Introduction](#introduction)
2. [Exception Hierarchy](#exception-hierarchy)
3. [Checked vs Unchecked](#checked-vs-unchecked)
4. [Handling Exceptions](#handling-exceptions)
5. [Try-with-Resources](#try-with-resources)
6. [Custom Exceptions](#custom-exceptions)
7. [Exception Chaining](#exception-chaining)
8. [Best Practices](#best-practices)
9. [Spring Boot Integration](#spring-boot-integration)
10. [Key Concepts](#key-concepts)

---

## Introduction

### What is Exception Handling?

Exception handling is a mechanism to handle runtime errors, maintaining the normal flow of the application. It allows you to separate error-handling code from regular code.

### Why is it Important?

- **Graceful degradation** - Application doesn't crash unexpectedly
- **User-friendly errors** - Meaningful messages instead of stack traces
- **Resource management** - Ensures resources are properly cleaned up
- **Debugging** - Provides valuable information for troubleshooting

---

## Exception Hierarchy

```
                        Throwable
                            │
            ┌───────────────┴───────────────┐
            │                               │
         Error                          Exception
            │                               │
    ┌───────┴───────┐               ┌───────┴───────────┐
    │               │               │                   │
OutOfMemoryError  StackOverflow  RuntimeException    IOException
                   Error              │              SQLException
                              ┌───────┴───────┐     FileNotFoundException
                              │               │
                      NullPointerException  IllegalArgumentException
                      ArrayIndexOutOfBounds  NumberFormatException
```

### Key Classes

| Class | Description | Should Catch? |
|-------|-------------|---------------|
| `Throwable` | Root of all errors and exceptions | Rarely |
| `Error` | Serious problems (JVM issues) | No |
| `Exception` | Recoverable problems | Yes |
| `RuntimeException` | Programming errors | Depends |

---

## Checked vs Unchecked

### Checked Exceptions

Must be either caught or declared in method signature with `throws`.

```java
// Checked exception - MUST handle
public void readFile(String path) throws IOException {
    FileReader reader = new FileReader(path);  // throws FileNotFoundException
    // ...
}

// Or catch it
public void readFile(String path) {
    try {
        FileReader reader = new FileReader(path);
    } catch (FileNotFoundException e) {
        System.err.println("File not found: " + path);
    }
}
```

**Common Checked Exceptions:**
- `IOException`, `FileNotFoundException`
- `SQLException`
- `ClassNotFoundException`
- `InterruptedException`

### Unchecked Exceptions (Runtime)

Don't need to be declared or caught (but often should be).

```java
// Unchecked - no need to declare
public int divide(int a, int b) {
    return a / b;  // ArithmeticException if b == 0
}

// But you CAN catch them
public int safeDivide(int a, int b) {
    try {
        return a / b;
    } catch (ArithmeticException e) {
        return 0;  // Default value
    }
}
```

**Common Unchecked Exceptions:**
- `NullPointerException`
- `IllegalArgumentException`
- `IllegalStateException`
- `ArrayIndexOutOfBoundsException`
- `NumberFormatException`

### When to Use Which?

| Use Checked When... | Use Unchecked When... |
|---------------------|----------------------|
| Caller can reasonably recover | Programming error occurred |
| External resource may fail | Precondition violated |
| Recovery action is possible | Bug in the code |

---

## Handling Exceptions

### Basic Try-Catch

```java
try {
    // Code that may throw exception
    int result = 10 / 0;
} catch (ArithmeticException e) {
    // Handle the exception
    System.err.println("Cannot divide by zero!");
}
```

### Multiple Catch Blocks

```java
try {
    String text = readFile("data.txt");
    int number = Integer.parseInt(text);
} catch (FileNotFoundException e) {
    System.err.println("File not found: " + e.getMessage());
} catch (NumberFormatException e) {
    System.err.println("Invalid number format: " + e.getMessage());
} catch (IOException e) {
    System.err.println("IO error: " + e.getMessage());
}
```

### Multi-Catch (Java 7+)

```java
try {
    // risky operations
} catch (FileNotFoundException | NumberFormatException e) {
    // Handle both exceptions the same way
    System.err.println("Error: " + e.getMessage());
}
```

### Finally Block

Always executes, regardless of exception.

```java
FileReader reader = null;
try {
    reader = new FileReader("file.txt");
    // read file
} catch (IOException e) {
    System.err.println("Error reading file");
} finally {
    // ALWAYS executes - perfect for cleanup
    if (reader != null) {
        try {
            reader.close();
        } catch (IOException e) {
            // ignore
        }
    }
}
```

### Throwing Exceptions

```java
public void setAge(int age) {
    if (age < 0) {
        throw new IllegalArgumentException("Age cannot be negative: " + age);
    }
    if (age > 150) {
        throw new IllegalArgumentException("Age too large: " + age);
    }
    this.age = age;
}
```

### Re-throwing Exceptions

```java
public void processData() throws DataProcessingException {
    try {
        // business logic
    } catch (SQLException e) {
        // Log and re-throw with more context
        logger.error("Database error during processing", e);
        throw new DataProcessingException("Failed to process data", e);
    }
}
```

---

## Try-with-Resources

### The Problem

```java
// Old way - verbose and error-prone
BufferedReader reader = null;
try {
    reader = new BufferedReader(new FileReader("file.txt"));
    String line = reader.readLine();
} catch (IOException e) {
    e.printStackTrace();
} finally {
    if (reader != null) {
        try {
            reader.close();
        } catch (IOException e) {
            // Ignored
        }
    }
}
```

### The Solution (Java 7+)

```java
// Try-with-resources - automatic cleanup!
try (BufferedReader reader = new BufferedReader(new FileReader("file.txt"))) {
    String line = reader.readLine();
    System.out.println(line);
} catch (IOException e) {
    e.printStackTrace();
}
// reader.close() is called automatically!
```

### Multiple Resources

```java
try (FileInputStream fis = new FileInputStream("input.txt");
     FileOutputStream fos = new FileOutputStream("output.txt");
     BufferedInputStream bis = new BufferedInputStream(fis)) {
    
    byte[] buffer = new byte[1024];
    int bytesRead;
    while ((bytesRead = bis.read(buffer)) != -1) {
        fos.write(buffer, 0, bytesRead);
    }
} // All resources closed automatically in reverse order!
```

### AutoCloseable Interface

Any class implementing `AutoCloseable` can be used with try-with-resources.

```java
public class DatabaseConnection implements AutoCloseable {
    private Connection connection;
    
    public DatabaseConnection(String url) throws SQLException {
        this.connection = DriverManager.getConnection(url);
    }
    
    public void executeQuery(String sql) throws SQLException {
        // Execute query
    }
    
    @Override
    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            System.out.println("Connection closed");
        }
    }
}

// Usage
try (DatabaseConnection db = new DatabaseConnection("jdbc:mysql://localhost/test")) {
    db.executeQuery("SELECT * FROM users");
} // Automatically closed!
```

### Suppressed Exceptions

When both try block and close() throw exceptions:

```java
public class Resource implements AutoCloseable {
    public void doSomething() {
        throw new RuntimeException("Error in doSomething");
    }
    
    @Override
    public void close() {
        throw new RuntimeException("Error in close");
    }
}

try (Resource r = new Resource()) {
    r.doSomething();
} catch (RuntimeException e) {
    System.out.println("Primary: " + e.getMessage());
    
    // Access suppressed exceptions
    for (Throwable suppressed : e.getSuppressed()) {
        System.out.println("Suppressed: " + suppressed.getMessage());
    }
}
// Output:
// Primary: Error in doSomething
// Suppressed: Error in close
```

---

## Custom Exceptions

### Creating Custom Exceptions

```java
// Unchecked (extends RuntimeException)
public class UserNotFoundException extends RuntimeException {
    
    private final Long userId;
    
    public UserNotFoundException(Long userId) {
        super("User not found with ID: " + userId);
        this.userId = userId;
    }
    
    public UserNotFoundException(Long userId, Throwable cause) {
        super("User not found with ID: " + userId, cause);
        this.userId = userId;
    }
    
    public Long getUserId() {
        return userId;
    }
}

// Checked (extends Exception)
public class InsufficientFundsException extends Exception {
    
    private final double balance;
    private final double amount;
    
    public InsufficientFundsException(double balance, double amount) {
        super(String.format("Insufficient funds. Balance: %.2f, Requested: %.2f", 
                            balance, amount));
        this.balance = balance;
        this.amount = amount;
    }
    
    public double getBalance() { return balance; }
    public double getAmount() { return amount; }
}
```

### Using Custom Exceptions

```java
public class UserService {
    
    public User findById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));
    }
}

public class BankAccount {
    private double balance;
    
    public void withdraw(double amount) throws InsufficientFundsException {
        if (amount > balance) {
            throw new InsufficientFundsException(balance, amount);
        }
        balance -= amount;
    }
}
```

### Exception Hierarchy for Application

```java
// Base exception for your application
public class ApplicationException extends RuntimeException {
    private final String errorCode;
    
    public ApplicationException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public ApplicationException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() { return errorCode; }
}

// Specific exceptions
public class ResourceNotFoundException extends ApplicationException {
    public ResourceNotFoundException(String resource, Object id) {
        super("NOT_FOUND", String.format("%s not found with id: %s", resource, id));
    }
}

public class ValidationException extends ApplicationException {
    public ValidationException(String field, String message) {
        super("VALIDATION_ERROR", String.format("Validation failed for '%s': %s", field, message));
    }
}

public class BusinessRuleException extends ApplicationException {
    public BusinessRuleException(String message) {
        super("BUSINESS_RULE", message);
    }
}
```

---

## Exception Chaining

### Why Chain Exceptions?

- Preserve the original cause
- Add context at each layer
- Enable root cause analysis

### Chaining Pattern

```java
public class DataService {
    
    public void saveData(Data data) {
        try {
            repository.save(data);
        } catch (SQLException e) {
            // Chain: wrap low-level exception with high-level context
            throw new DataPersistenceException("Failed to save data: " + data.getId(), e);
        }
    }
}

// Accessing the chain
try {
    dataService.saveData(data);
} catch (DataPersistenceException e) {
    System.out.println("Error: " + e.getMessage());
    System.out.println("Cause: " + e.getCause().getMessage());  // Original SQLException
    
    // Print full stack trace (shows chain)
    e.printStackTrace();
}
```

### Finding Root Cause

```java
public static Throwable getRootCause(Throwable throwable) {
    Throwable cause = throwable;
    while (cause.getCause() != null) {
        cause = cause.getCause();
    }
    return cause;
}

// Usage
try {
    // ...
} catch (Exception e) {
    Throwable root = getRootCause(e);
    System.out.println("Root cause: " + root.getClass().getSimpleName() + 
                       " - " + root.getMessage());
}
```

---

## Best Practices

### ✅ DO

```java
// 1. Be specific with exception types
try {
    // ...
} catch (FileNotFoundException e) {
    // Handle missing file specifically
} catch (IOException e) {
    // Handle other IO errors
}

// 2. Include useful information in messages
throw new IllegalArgumentException(
    "Age must be between 0 and 150, but was: " + age);

// 3. Use try-with-resources
try (var stream = Files.newInputStream(path)) {
    // ...
}

// 4. Log exceptions with context
catch (Exception e) {
    logger.error("Failed to process order {} for user {}", orderId, userId, e);
    throw new OrderProcessingException("Order processing failed", e);
}

// 5. Validate early, fail fast
public void processUser(User user) {
    Objects.requireNonNull(user, "User cannot be null");
    if (user.getEmail() == null || user.getEmail().isBlank()) {
        throw new IllegalArgumentException("Email is required");
    }
    // Now safe to process
}
```

### ❌ DON'T

```java
// 1. Don't catch generic Exception (usually)
catch (Exception e) {  // Too broad!
    // ...
}

// 2. Don't swallow exceptions
catch (Exception e) {
    // Empty catch block - BAD!
}

// 3. Don't use exceptions for flow control
try {
    while (true) {
        array[i++] = value;  // Using exception to detect end - BAD!
    }
} catch (ArrayIndexOutOfBoundsException e) {
    // Loop ended
}

// 4. Don't throw Exception or Throwable
public void doSomething() throws Exception {  // Too generic!
    // ...
}

// 5. Don't log and rethrow the same exception
catch (Exception e) {
    logger.error("Error", e);
    throw e;  // Causes duplicate log entries!
}
```

---

## Spring Boot Integration

### @ExceptionHandler

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.findById(id);  // May throw UserNotFoundException
    }
    
    // Handle exception locally in this controller
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException e) {
        ErrorResponse error = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            e.getMessage(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}
```

### @ControllerAdvice (Global Handler)

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException e) {
        logger.warn("Resource not found: {}", e.getMessage());
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse("NOT_FOUND", e.getMessage()));
    }
    
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(ValidationException e) {
        logger.warn("Validation error: {}", e.getMessage());
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse("VALIDATION_ERROR", e.getMessage()));
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException e) {
        List<String> errors = e.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .toList();
        
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse("VALIDATION_ERROR", String.join(", ", errors)));
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception e) {
        logger.error("Unexpected error", e);
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"));
    }
}

// Error response DTO
public record ErrorResponse(
    String code,
    String message,
    LocalDateTime timestamp
) {
    public ErrorResponse(String code, String message) {
        this(code, message, LocalDateTime.now());
    }
}
```

### @ResponseStatus

```java
@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long id) {
        super("User not found: " + id);
    }
}
// Spring will automatically return 404 when this exception is thrown
```

---

## Key Concepts

### Q1: Difference between `throw` and `throws`?

| `throw` | `throws` |
|---------|----------|
| Used to actually throw an exception | Used to declare exceptions |
| Followed by exception instance | Followed by exception class |
| Used inside method body | Used in method signature |

```java
// throws - declaration
public void read() throws IOException {
    // throw - actual throwing
    throw new IOException("File error");
}
```

### Q2: Can we have try without catch?

Yes, with finally or try-with-resources:

```java
// try-finally (no catch)
try {
    // ...
} finally {
    // cleanup
}

// try-with-resources (no catch)
try (var stream = Files.newInputStream(path)) {
    // ...
}
```

### Q3: What happens if exception occurs in finally block?

It suppresses the original exception (unless using try-with-resources which handles this properly).

### Q4: When to use checked vs unchecked exceptions?

- **Checked**: When caller can reasonably recover (file not found, network error)
- **Unchecked**: For programming errors (null pointer, illegal argument)

---

## Demo: Run the Example

```bash
cd demo-exception-handling
mvn compile exec:java -Dexec.mainClass="com.example.ExceptionHandlingDemo"
```

## Key Takeaways

1. **Use try-with-resources** for automatic resource cleanup
2. **Create custom exceptions** with meaningful messages
3. **Chain exceptions** to preserve root cause
4. **Use @ControllerAdvice** for global exception handling in Spring Boot
5. **Prefer unchecked exceptions** for most application-level errors
