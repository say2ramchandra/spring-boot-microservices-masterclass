# Exception Handling Demo

This demo showcases comprehensive exception handling in Spring Boot with global error handling, custom exceptions, and proper HTTP status codes.

## 📋 Features Demonstrated

- **@RestControllerAdvice** - Global exception handling
- **@ExceptionHandler** - Handle specific exceptions
- **Custom Exceptions** - ResourceNotFoundException, DuplicateResourceException, BusinessValidationException
- **Validation Errors** - Handle @Valid validation with field-level errors
- **Error Response Structure** - Consistent error format across all endpoints
- **Proper HTTP Status Codes** - 400, 404, 409, 500
- **Security** - Hide internal stack traces from clients
- **Logging** - Debug-friendly error logging

## 🚀 Running the Demo

### Prerequisites
- Java 17 or higher
- Maven 3.6+

### Steps

1. **Navigate to demo directory:**
   ```bash
   cd 03-spring-boot-fundamentals/06-exception-handling/demo-exception-handling
   ```

2. **Run the application:**
   ```bash
   mvn spring-boot:run
   ```

3. **Application starts on port 8080**

## 🎯 Testing Exception Scenarios

### 1. Resource Not Found (404)

```bash
# Try to get non-existent user
curl http://localhost:8080/api/users/999

# Response:
{
  "timestamp": "2024-01-20T10:15:30",
  "status": 404,
  "error": "NOT_FOUND",
  "message": "User not found with id: '999'",
  "path": "/api/users/999",
  "details": []
}
```

### 2. Validation Error (400)

```bash
# Create user with invalid data (empty name, invalid email, age < 18)
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "",
    "email": "invalid-email",
    "age": 15,
    "phone": "123"
  }'

# Response:
{
  "timestamp": "2024-01-20T10:16:30",
  "status": 400,
  "error": "VALIDATION_ERROR",
  "message": "Validation failed. Please check the errors below.",
  "path": "/api/users",
  "fieldErrors": {
    "name": "Name is required",
    "email": "Email must be valid",
    "age": "Age must be at least 18",
    "phone": "Phone number must be valid"
  }
}
```

### 3. Duplicate Resource (409)

```bash
# Create user with existing email
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test User",
    "email": "john.doe@example.com",
    "age": 25,
    "phone": "+12025550199"
  }'

# Response:
{
  "timestamp": "2024-01-20T10:17:30",
  "status": 409,
  "error": "CONFLICT",
  "message": "User already exists with email: 'john.doe@example.com'",
  "path": "/api/users",
  "details": []
}
```

### 4. Malformed JSON (400)

```bash
# Send malformed JSON
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name": "Test", invalid json}'

# Response:
{
  "timestamp": "2024-01-20T10:18:30",
  "status": 400,
  "error": "BAD_REQUEST",
  "message": "Malformed JSON request. Please check your request body.",
  "path": "/api/users",
  "details": []
}
```

### 5. Type Mismatch (400)

```bash
# Send string for ID parameter (expects Long)
curl http://localhost:8080/api/users/abc

# Response:
{
  "timestamp": "2024-01-20T10:19:30",
  "status": 400,
  "error": "BAD_REQUEST",
  "message": "Invalid value 'abc' for parameter 'id'. Expected type: Long",
  "path": "/api/users/abc",
  "details": []
}
```

### 6. Generic Exception (500)

```bash
# Trigger test exception
curl http://localhost:8080/api/users/test/error

# Response:
{
  "timestamp": "2024-01-20T10:20:30",
  "status": 500,
  "error": "INTERNAL_SERVER_ERROR",
  "message": "An unexpected error occurred. Please try again later.",
  "path": "/api/users/test/error",
  "details": []
}
```

## 📡 Valid API Endpoints

### Get All Users

```bash
curl http://localhost:8080/api/users

# Response: Array of users
[
  {
    "id": 1,
    "name": "John Doe",
    "email": "john.doe@example.com",
    "age": 30,
    "phone": "+12025550101",
    "createdAt": "2024-01-20T10:00:00",
    "updatedAt": "2024-01-20T10:00:00"
  },
  ...
]
```

### Get User by ID

```bash
curl http://localhost:8080/api/users/1

# Response: Single user object
```

### Get User by Email

```bash
curl http://localhost:8080/api/users/email/john.doe@example.com

# Response: Single user object
```

### Create User

```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "New User",
    "email": "newuser@example.com",
    "age": 28,
    "phone": "+12025550199"
  }'

# Response: 201 Created with user object
```

### Update User

```bash
curl -X PUT http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Updated Name",
    "email": "john.doe@example.com",
    "age": 31,
    "phone": "+12025550101"
  }'

# Response: 200 OK with updated user
```

### Delete User

```bash
curl -X DELETE http://localhost:8080/api/users/1

# Response:
{
  "message": "User deleted successfully",
  "id": "1"
}
```

### Check if User Exists

```bash
curl http://localhost:8080/api/users/1/exists

# Response:
{
  "exists": true
}
```

### Get User Count

```bash
curl http://localhost:8080/api/users/count

# Response:
{
  "count": 5
}
```

## 🔍 Exception Handler Flow

```
1. Exception occurs in Controller/Service
          ↓
2. Spring looks for @ExceptionHandler
          ↓
3. GlobalExceptionHandler catches it
          ↓
4. Appropriate handler method executes
          ↓
5. Returns ErrorResponse with proper status
```

## 📋 Exception Types and Status Codes

| Exception | Status Code | Description |
|-----------|-------------|-------------|
| ResourceNotFoundException | 404 | Resource not found |
| DuplicateResourceException | 409 | Resource already exists |
| BusinessValidationException | 400 | Business rule violation |
| MethodArgumentNotValidException | 400 | @Valid validation failed |
| IllegalArgumentException | 400 | Invalid method argument |
| MethodArgumentTypeMismatchException | 400 | Type conversion failed |
| HttpMessageNotReadableException | 400 | Malformed JSON |
| DataIntegrityViolationException | 409 | Database constraint violation |
| Exception (generic) | 500 | Unexpected server error |

## 💡 Key Learnings

### 1. Global Exception Handling

**@RestControllerAdvice** catches exceptions from all controllers:
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handle(ResourceNotFoundException ex) {
        // Handle exception globally
    }
}
```

### 2. Custom Exceptions

Create specific exceptions for business scenarios:
```java
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resource, String field, Object value) {
        super(String.format("%s not found with %s: '%s'", resource, field, value));
    }
}

// Usage in service
throw new ResourceNotFoundException("User", "id", 999);
```

### 3. Validation Error Handling

Handle @Valid validation errors:
```java
@ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<ValidationErrorResponse> handle(MethodArgumentNotValidException ex) {
    // Extract field errors
    ex.getBindingResult().getFieldErrors().forEach(error ->
        response.addFieldError(error.getField(), error.getDefaultMessage())
    );
}
```

### 4. Error Response Structure

Consistent error format:
```java
@Data
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private List<String> details;
}
```

### 5. Security Best Practice

Never expose internal details:
```java
// ✅ Good - Generic message for client
"An unexpected error occurred"

// ❌ Bad - Exposing stack trace
ex.getMessage() + "\n" + Arrays.toString(ex.getStackTrace())
```

## 🎓 Interview Preparation

After running this demo, you should understand:

1. **@RestControllerAdvice** vs **@ControllerAdvice**
2. **@ExceptionHandler** method signatures
3. **Custom exception** creation and usage
4. **Validation error handling** with @Valid
5. **Proper HTTP status codes** for different scenarios
6. **Error response structure** standardization
7. **Security** - not exposing internal errors
8. **Logging** vs client response separation

## 🧪 Test Scenarios

Try these scenarios to understand exception handling:

1. **Create user with all fields valid** ✅
2. **Create user with blank name** → Validation error
3. **Create user with invalid email** → Validation error
4. **Create user with age < 18** → Validation error
5. **Create user with duplicate email** → Conflict
6. **Get user with valid ID** ✅
7. **Get user with non-existent ID** → Not found
8. **Update user with invalid ID** → Not found
9. **Update user with duplicate email** → Conflict
10. **Delete user with invalid ID** → Not found
11. **Send malformed JSON** → Bad request
12. **Send invalid parameter type** → Bad request

## 📚 References

- [Spring @ControllerAdvice](https://spring.io/blog/2013/11/01/exception-handling-in-spring-mvc)
- [Bean Validation](https://beanvalidation.org/)
- [HTTP Status Codes](https://httpstatuses.com/)

---

**Next Demo:** [Logging Strategies](../../07-logging-strategies/demo-logging/)
