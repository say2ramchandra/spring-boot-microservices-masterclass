# Exception Handling in Spring Boot

> **Building robust applications with global exception handling**

## 📚 Table of Contents

- [Why Exception Handling?](#why-exception-handling)
- [@ControllerAdvice](#controlleradvice)
- [@ExceptionHandler](#exceptionhandler)
- [Custom Exceptions](#custom-exceptions)
- [Error Response Structure](#error-response-structure)
- [Common Exceptions](#common-exceptions)
- [Best Practices](#best-practices)
- [Demo Project](#demo-project)
- [Interview Questions](#interview-questions)

---

## Why Exception Handling?

Without proper exception handling:
```json
{
  "timestamp": "2024-01-20T10:15:30.123+00:00",
  "status": 500,
  "error": "Internal Server Error",
  "path": "/api/users/999"
}
```

With proper exception handling:
```json
{
  "timestamp": "2024-01-20T10:15:30.123+00:00",
  "status": 404,
  "error": "NOT_FOUND",
  "message": "User not found with ID: 999",
  "path": "/api/users/999",
  "details": ["Please check the user ID and try again"]
}
```

### Benefits

1. **Consistent error responses** across all endpoints
2. **Better debugging** with clear error messages
3. **Proper HTTP status codes** (404, 400, 500, etc.)
4. **Security** - hide internal stack traces from clients
5. **Maintainability** - centralized error handling logic

---

## @ControllerAdvice

`@ControllerAdvice` allows you to handle exceptions globally across all controllers.

### Basic Example

```java
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex,
            WebRequest request) {
        
        ErrorResponse error = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            ex.getMessage(),
            request.getDescription(false)
        );
        
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
}
```

### How It Works

1. Exception occurs in any controller
2. Spring looks for matching `@ExceptionHandler`
3. Handler method executes
4. Returns custom error response

### Scope Control

```java
// Handle exceptions from specific controllers
@ControllerAdvice(assignableTypes = {UserController.class, OrderController.class})
public class SpecificExceptionHandler { }

// Handle exceptions from specific packages
@ControllerAdvice(basePackages = "com.example.controllers")
public class PackageExceptionHandler { }

// Handle exceptions with specific annotations
@ControllerAdvice(annotations = RestController.class)
public class RestExceptionHandler { }
```

---

## @ExceptionHandler

`@ExceptionHandler` defines a method to handle specific exception types.

### Single Exception

```java
@ExceptionHandler(ResourceNotFoundException.class)
public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
    ErrorResponse error = new ErrorResponse(
        HttpStatus.NOT_FOUND.value(),
        ex.getMessage()
    );
    return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
}
```

### Multiple Exceptions

```java
@ExceptionHandler({
    ResourceNotFoundException.class,
    UserNotFoundException.class,
    ProductNotFoundException.class
})
public ResponseEntity<ErrorResponse> handleNotFoundExceptions(Exception ex) {
    ErrorResponse error = new ErrorResponse(
        HttpStatus.NOT_FOUND.value(),
        ex.getMessage()
    );
    return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
}
```

### Method Signatures

```java
// With exception only
@ExceptionHandler(Exception.class)
public ResponseEntity<ErrorResponse> handle(Exception ex) { }

// With WebRequest for context
@ExceptionHandler(Exception.class)
public ResponseEntity<ErrorResponse> handle(Exception ex, WebRequest request) { }

// With HttpServletRequest
@ExceptionHandler(Exception.class)
public ResponseEntity<ErrorResponse> handle(Exception ex, HttpServletRequest request) { }
```

---

## Custom Exceptions

Create custom exceptions for specific business scenarios.

### Base Exception

```java
public class ResourceNotFoundException extends RuntimeException {
    
    private String resourceName;
    private String fieldName;
    private Object fieldValue;
    
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s: '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
```

### Specific Exceptions

```java
public class UserNotFoundException extends ResourceNotFoundException {
    public UserNotFoundException(Long id) {
        super("User", "id", id);
    }
}

public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String email) {
        super("User already exists with email: " + email);
    }
}

public class InvalidInputException extends RuntimeException {
    private List<String> errors;
    
    public InvalidInputException(List<String> errors) {
        super("Invalid input data");
        this.errors = errors;
    }
}
```

### Usage in Service

```java
@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    public User getUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));
    }
    
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicateEmailException(user.getEmail());
        }
        return userRepository.save(user);
    }
}
```

---

## Error Response Structure

### Standard Error Response

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private List<String> details;
    
    public ErrorResponse(int status, String message, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = HttpStatus.valueOf(status).name();
        this.message = message;
        this.path = path;
        this.details = new ArrayList<>();
    }
}
```

### Validation Error Response

```java
@Data
@AllArgsConstructor
public class ValidationErrorResponse {
    
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private Map<String, String> fieldErrors;
    
    public ValidationErrorResponse(String message, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = HttpStatus.BAD_REQUEST.value();
        this.error = "VALIDATION_ERROR";
        this.message = message;
        this.path = path;
        this.fieldErrors = new HashMap<>();
    }
}
```

---

## Common Exceptions

### 1. Validation Exceptions (400 Bad Request)

```java
@ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<ValidationErrorResponse> handleValidation(
        MethodArgumentNotValidException ex,
        WebRequest request) {
    
    ValidationErrorResponse response = new ValidationErrorResponse(
        "Validation failed",
        request.getDescription(false).replace("uri=", "")
    );
    
    // Extract field errors
    ex.getBindingResult().getFieldErrors().forEach(error -> 
        response.getFieldErrors().put(
            error.getField(), 
            error.getDefaultMessage()
        )
    );
    
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
}
```

### 2. Resource Not Found (404 Not Found)

```java
@ExceptionHandler(ResourceNotFoundException.class)
public ResponseEntity<ErrorResponse> handleNotFound(
        ResourceNotFoundException ex,
        WebRequest request) {
    
    ErrorResponse error = new ErrorResponse(
        HttpStatus.NOT_FOUND.value(),
        ex.getMessage(),
        request.getDescription(false).replace("uri=", "")
    );
    
    return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
}
```

### 3. Duplicate Resource (409 Conflict)

```java
@ExceptionHandler(DuplicateResourceException.class)
public ResponseEntity<ErrorResponse> handleDuplicate(
        DuplicateResourceException ex,
        WebRequest request) {
    
    ErrorResponse error = new ErrorResponse(
        HttpStatus.CONFLICT.value(),
        ex.getMessage(),
        request.getDescription(false).replace("uri=", "")
    );
    
    return new ResponseEntity<>(error, HttpStatus.CONFLICT);
}
```

### 4. Bad Request (400 Bad Request)

```java
@ExceptionHandler(IllegalArgumentException.class)
public ResponseEntity<ErrorResponse> handleBadRequest(
        IllegalArgumentException ex,
        WebRequest request) {
    
    ErrorResponse error = new ErrorResponse(
        HttpStatus.BAD_REQUEST.value(),
        ex.getMessage(),
        request.getDescription(false).replace("uri=", "")
    );
    
    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
}
```

### 5. Generic Exception (500 Internal Server Error)

```java
@ExceptionHandler(Exception.class)
public ResponseEntity<ErrorResponse> handleGlobalException(
        Exception ex,
        WebRequest request) {
    
    ErrorResponse error = new ErrorResponse(
        HttpStatus.INTERNAL_SERVER_ERROR.value(),
        "An unexpected error occurred",
        request.getDescription(false).replace("uri=", "")
    );
    
    // Log the full exception for debugging
    log.error("Unexpected error:", ex);
    
    return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
}
```

### 6. Data Integrity Violation (409 Conflict)

```java
@ExceptionHandler(DataIntegrityViolationException.class)
public ResponseEntity<ErrorResponse> handleDataIntegrity(
        DataIntegrityViolationException ex,
        WebRequest request) {
    
    String message = "Database constraint violation";
    
    if (ex.getMessage().contains("unique")) {
        message = "A record with this value already exists";
    } else if (ex.getMessage().contains("foreign key")) {
        message = "Cannot delete - record is referenced by other records";
    }
    
    ErrorResponse error = new ErrorResponse(
        HttpStatus.CONFLICT.value(),
        message,
        request.getDescription(false).replace("uri=", "")
    );
    
    return new ResponseEntity<>(error, HttpStatus.CONFLICT);
}
```

---

## Best Practices

### 1. Use Specific Exceptions

```java
// ✅ Good - Specific exception
throw new UserNotFoundException(userId);

// ❌ Bad - Generic exception
throw new RuntimeException("User not found");
```

### 2. Centralize Exception Handling

```java
// ✅ Good - Single @ControllerAdvice
@ControllerAdvice
public class GlobalExceptionHandler {
    // All exception handlers here
}

// ❌ Bad - Exception handling in each controller
@RestController
public class UserController {
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handleNotFound() { }
}
```

### 3. Don't Expose Sensitive Information

```java
// ✅ Good - Generic message for client
ErrorResponse error = new ErrorResponse(
    500,
    "An unexpected error occurred",
    path
);
log.error("Database connection failed", ex);  // Detailed log

// ❌ Bad - Exposing stack trace
ErrorResponse error = new ErrorResponse(
    500,
    ex.getMessage() + "\n" + Arrays.toString(ex.getStackTrace()),
    path
);
```

### 4. Return Appropriate HTTP Status Codes

```java
// ✅ Good - Correct status codes
404 - Resource not found
400 - Invalid input
409 - Duplicate resource
500 - Server error

// ❌ Bad - Wrong status codes
200 - For errors
500 - For not found
```

### 5. Include Timestamp and Request Path

```java
// ✅ Good - Complete error context
{
  "timestamp": "2024-01-20T10:15:30",
  "status": 404,
  "message": "User not found",
  "path": "/api/users/999"
}

// ❌ Bad - Minimal information
{
  "message": "User not found"
}
```

### 6. Validate Input Early

```java
// ✅ Good - Validate at controller level
@PostMapping("/users")
public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
    // Validation handled by @Valid
}

// ❌ Bad - Manual validation in service
@PostMapping("/users")
public ResponseEntity<User> createUser(@RequestBody User user) {
    if (user.getName() == null) throw new Exception("Name required");
}
```

### 7. Log Exceptions Appropriately

```java
// ✅ Good - Log with appropriate levels
log.error("Database error", ex);          // For server errors
log.warn("Invalid request: {}", message);  // For client errors
log.info("User not found: {}", userId);    // For expected cases

// ❌ Bad - Same level for everything
log.error("Everything");
```

### 8. Use @ResponseStatus for Simple Cases

```java
// ✅ Good - Simple exception with status
@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long id) {
        super("User not found: " + id);
    }
}

// No need for @ExceptionHandler if you just need the status
```

---

## Demo Project

See [demo-exception-handling](demo-exception-handling/) for a complete example with:
- Global exception handler
- Custom exceptions
- Validation error handling
- Complete error responses
- Multiple HTTP status codes

---

## Interview Questions

### Q1: What is @ControllerAdvice and how does it work?

**Answer:**
`@ControllerAdvice` is a specialization of `@Component` that allows you to handle exceptions globally across all controllers in your application.

**How it works:**
1. Spring scans for `@ControllerAdvice` beans at startup
2. When an exception occurs in any controller
3. Spring searches for matching `@ExceptionHandler` in all `@ControllerAdvice` classes
4. Executes the handler and returns the response

**Example:**
```java
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handle(UserNotFoundException ex) {
        // Handle exception
    }
}
```

### Q2: What's the difference between @ControllerAdvice and @RestControllerAdvice?

**Answer:**

**@ControllerAdvice:**
- For traditional MVC controllers
- Returns view names
- Needs `@ResponseBody` on handler methods

**@RestControllerAdvice:**
- For REST controllers
- Returns JSON/XML directly
- Combines `@ControllerAdvice` + `@ResponseBody`

```java
// Equivalent declarations
@RestControllerAdvice
public class RestExceptionHandler { }

@ControllerAdvice
@ResponseBody
public class RestExceptionHandler { }
```

### Q3: How do you handle validation errors?

**Answer:**

Use `@Valid` with `@RequestBody` and handle `MethodArgumentNotValidException`:

```java
// Controller
@PostMapping("/users")
public User create(@Valid @RequestBody User user) {
    return userService.save(user);
}

// Entity
public class User {
    @NotBlank(message = "Name is required")
    private String name;
    
    @Email(message = "Invalid email format")
    private String email;
}

// Exception Handler
@ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<ValidationErrorResponse> handleValidation(
        MethodArgumentNotValidException ex) {
    
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getFieldErrors().forEach(error ->
        errors.put(error.getField(), error.getDefaultMessage())
    );
    
    return new ResponseEntity<>(new ValidationErrorResponse(errors), 
                                HttpStatus.BAD_REQUEST);
}
```

### Q4: What HTTP status codes should you return for different exceptions?

**Answer:**

| Exception Type | Status Code | Description |
|---------------|-------------|-------------|
| ResourceNotFoundException | 404 | Resource not found |
| ValidationException | 400 | Invalid input data |
| DuplicateResourceException | 409 | Resource already exists |
| UnauthorizedException | 401 | Authentication required |
| ForbiddenException | 403 | Insufficient permissions |
| MethodNotAllowedException | 405 | HTTP method not allowed |
| Generic Exception | 500 | Server error |

### Q5: How to handle multiple exception types with one handler?

**Answer:**

**Method 1: Multiple exception classes**
```java
@ExceptionHandler({
    UserNotFoundException.class,
    ProductNotFoundException.class,
    OrderNotFoundException.class
})
public ResponseEntity<ErrorResponse> handleNotFound(Exception ex) {
    // Handle all not found exceptions
}
```

**Method 2: Common base exception**
```java
// Base exception
public abstract class NotFoundException extends RuntimeException { }

// Specific exceptions
public class UserNotFoundException extends NotFoundException { }
public class ProductNotFoundException extends NotFoundException { }

// Single handler
@ExceptionHandler(NotFoundException.class)
public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException ex) {
    // Handle all subclasses
}
```

### Q6: Should you catch Exception.class in @ExceptionHandler?

**Answer:**

**Yes, but as a fallback:**

```java
// Specific handlers first
@ExceptionHandler(UserNotFoundException.class)
public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
    // Specific handling
}

@ExceptionHandler(ValidationException.class)
public ResponseEntity<ErrorResponse> handleValidation(ValidationException ex) {
    // Specific handling
}

// Generic handler as fallback
@ExceptionHandler(Exception.class)
public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
    log.error("Unexpected error", ex);  // Log for debugging
    return new ResponseEntity<>(
        new ErrorResponse(500, "An unexpected error occurred"),
        HttpStatus.INTERNAL_SERVER_ERROR
    );
}
```

**Why:** Catches any unexpected exceptions and prevents exposing internal errors to clients.

### Q7: How to test exception handling?

**Answer:**

```java
@WebMvcTest(UserController.class)
class UserControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private UserService userService;
    
    @Test
    void getUserNotFound_ReturnsNotFound() throws Exception {
        when(userService.getUser(999L))
            .thenThrow(new UserNotFoundException(999L));
        
        mockMvc.perform(get("/api/users/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("User not found with id: 999"))
            .andExpect(jsonPath("$.status").value(404));
    }
    
    @Test
    void createUserInvalidData_ReturnsBadRequest() throws Exception {
        String invalidUser = "{\"name\":\"\",\"email\":\"invalid\"}";
        
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidUser))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.fieldErrors.name").exists())
            .andExpect(jsonPath("$.fieldErrors.email").exists());
    }
}
```

---

## Summary

| Concept | Purpose |
|---------|---------|
| **@ControllerAdvice** | Handle exceptions globally |
| **@ExceptionHandler** | Define exception handler methods |
| **Custom Exceptions** | Specific business scenarios |
| **ErrorResponse** | Standardized error structure |
| **HTTP Status Codes** | Proper status for each error type |
| **Validation** | Handle @Valid errors |
| **Logging** | Debug without exposing to client |

Proper exception handling makes your REST API professional, user-friendly, and easier to debug.

---

**Next**: [Logging Strategies](../07-logging-strategies/)
