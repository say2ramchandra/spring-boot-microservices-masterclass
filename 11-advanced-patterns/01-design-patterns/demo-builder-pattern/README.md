# Builder Pattern Demo - Product & Email System

> **Demonstrates Builder Pattern with both Lombok and custom implementations**

## 🎯 What is Builder Pattern?

**Problem:** You have a class with many constructor parameters (especially optional ones), making it hard to create objects.

**Solution:** Provide a step-by-step approach to construct complex objects with a fluent API.

---

## 🏗️ How It Works Here

### Without Builder Pattern (Bad):
```java
// Constructor with many parameters - hard to read!
Product product = new Product(
    null,                    // id - what is this?
    "Laptop",                // name
    "Gaming laptop",         // description
    1299.99,                 // price
    "Electronics",           // category
    50,                      // stock
    "Asus",                  // manufacturer
    "SKU-001",               // sku
    true,                    // active
    null,                    // createdAt
    null                     // updatedAt
);
// 😱 Hard to understand! What does each parameter mean?
```

### With Builder Pattern (Good):
```java
// Using Lombok's @Builder - readable and flexible!
Product product = Product.builder()
    .name("Laptop")
    .description("Gaming laptop")
    .price(1299.99)
    .category("Electronics")
    .stockQuantity(50)
    .manufacturer("Asus")
    .sku("SKU-001")
    .active(true)
    .build();
// ✅ Clear, readable, self-documenting!
```

---

## 🚀 Running the Demo

### Prerequisites
- Java 17+
- Maven 3.8+

### Start the Application
```bash
cd 11-advanced-patterns/01-design-patterns/demo-builder-pattern
mvn spring-boot:run
```

Application runs on: **http://localhost:8091**

---

## 🧪 Testing the Builder Pattern

### Part 1: Lombok Builder (@Builder)

#### 1. Create Product (Lombok Builder)
```bash
curl -X POST http://localhost:8091/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Gaming Laptop",
    "description": "High-performance gaming laptop with RTX 4090",
    "price": 2499.99,
    "category": "Electronics",
    "stockQuantity": 25,
    "manufacturer": "Asus ROG",
    "sku": "LAPTOP-001",
    "active": true
  }'
```

**Response:**
```json
{
  "id": 1,
  "name": "Gaming Laptop",
  "description": "High-performance gaming laptop with RTX 4090",
  "price": 2499.99,
  "category": "Electronics",
  "stockQuantity": 25,
  "manufacturer": "Asus ROG",
  "sku": "LAPTOP-001",
  "active": true,
  "createdAt": "2024-12-10T11:00:00",
  "updatedAt": "2024-12-10T11:00:00"
}
```

#### 2. Create Another Product (Minimal Fields)
```bash
curl -X POST http://localhost:8091/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Wireless Mouse",
    "price": 29.99,
    "category": "Accessories",
    "sku": "MOUSE-001"
  }'
```

#### 3. Get All Products
```bash
curl http://localhost:8091/api/products
```

#### 4. Get Product by ID
```bash
curl http://localhost:8091/api/products/1
```

#### 5. Update Product (Using toBuilder)
```bash
curl -X PUT http://localhost:8091/api/products/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Gaming Laptop - UPDATED",
    "description": "Now with extended warranty",
    "price": 2299.99,
    "category": "Electronics",
    "stockQuantity": 30,
    "manufacturer": "Asus ROG",
    "sku": "LAPTOP-001",
    "active": true
  }'
```

---

### Part 2: Custom Builder (Email)

#### 1. Send Simple Email
```bash
curl -X POST http://localhost:8091/api/emails/send \
  -H "Content-Type: application/json" \
  -d '{
    "to": "customer@example.com",
    "subject": "Welcome!",
    "body": "Thank you for joining us."
  }'
```

**Response:**
```json
{
  "to": "customer@example.com",
  "subject": "Welcome!",
  "body": "Thank you for joining us.",
  "from": "noreply@example.com",
  "cc": null,
  "bcc": null,
  "replyTo": null,
  "priority": "NORMAL",
  "htmlFormat": false,
  "attachmentPath": null
}
```

#### 2. Send Email with All Options
```bash
curl -X POST http://localhost:8091/api/emails/send \
  -H "Content-Type: application/json" \
  -d '{
    "to": "vip@example.com",
    "subject": "Important: Action Required",
    "body": "<h1>Please review the attached document</h1>",
    "from": "admin@company.com",
    "cc": "manager@company.com",
    "bcc": "archive@company.com",
    "replyTo": "support@company.com",
    "priority": "URGENT",
    "htmlFormat": true,
    "attachmentPath": "/documents/report.pdf"
  }'
```

#### 3. Run Builder Demonstration
```bash
curl http://localhost:8091/api/emails/demo
```

This will print 3 different email configurations in the console logs.

---

## 📁 Project Structure

```
demo-builder-pattern/
├── src/main/java/com/masterclass/patterns/builder/
│   ├── BuilderPatternApplication.java         # Main application
│   ├── controller/
│   │   ├── ProductController.java             # Product REST API
│   │   └── EmailController.java               # Email REST API
│   ├── service/
│   │   ├── ProductService.java                # Lombok Builder demo
│   │   └── EmailService.java                  # Custom Builder demo
│   ├── model/
│   │   ├── Product.java                       # ⭐ Lombok @Builder
│   │   └── Email.java                         # ⭐ Custom Builder
│   ├── dto/
│   │   ├── ProductRequest.java                # Product DTO
│   │   └── EmailRequest.java                  # Email DTO
│   └── repository/
│       └── ProductRepository.java             # JPA repository
└── pom.xml
```

---

## 🎓 Key Components

### 1. Lombok Builder (Product.java)
```java
@Entity
@Data
@Builder  // ⭐ Lombok generates Builder automatically!
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private Long id;
    private String name;
    private String description;
    private Double price;
    // ... more fields
}

// Usage:
Product product = Product.builder()
    .name("Laptop")
    .price(1299.99)
    .category("Electronics")
    .build();
```

**Lombok generates:**
- `builder()` - Creates a new builder
- `toBuilder()` - Creates builder from existing object
- All setter methods with fluent API
- `build()` - Creates the final object

### 2. Custom Builder (Email.java)
```java
public class Email {
    // Fields
    private final String to;
    private final String subject;
    // ...
    
    // Private constructor
    private Email(Builder builder) {
        this.to = builder.to;
        this.subject = builder.subject;
        // ...
    }
    
    // Static nested Builder class
    public static class Builder {
        private final String to;  // Required
        private final String subject;  // Required
        private String from = "default@example.com";  // Optional with default
        
        public Builder(String to, String subject) {
            this.to = to;
            this.subject = subject;
        }
        
        public Builder from(String from) {
            this.from = from;
            return this;  // Fluent API
        }
        
        public Email build() {
            // Validation
            if (to == null || to.isBlank()) {
                throw new IllegalArgumentException("'to' required");
            }
            return new Email(this);
        }
    }
}

// Usage:
Email email = new Email.Builder("user@example.com", "Hello")
    .from("admin@example.com")
    .priority(Priority.HIGH)
    .build();
```

---

## ✨ Benefits of Builder Pattern

### 1. **Readability**
```java
// Before: What do these parameters mean?
new Product(null, "Laptop", "Gaming", 1299.99, "Electronics", 50, "Asus", "SKU", true, null, null);

// After: Crystal clear!
Product.builder()
    .name("Laptop")
    .description("Gaming")
    .price(1299.99)
    .build();
```

### 2. **Flexibility with Optional Parameters**
```java
// Minimal email
Email simple = new Email.Builder("user@example.com", "Hi", "Welcome").build();

// Email with all options
Email complex = new Email.Builder("user@example.com", "Hi", "Welcome")
    .cc("manager@example.com")
    .priority(Priority.URGENT)
    .htmlFormat(true)
    .build();
```

### 3. **Immutability**
```java
// All fields are final - object is immutable after creation
private final String to;
private final String subject;
```

### 4. **Validation at Build Time**
```java
public Email build() {
    if (to == null || to.isBlank()) {
        throw new IllegalArgumentException("'to' required");
    }
    return new Email(this);
}
```

### 5. **Easy Modification with toBuilder()**
```java
Product original = Product.builder()
    .name("Laptop")
    .price(1299.99)
    .build();

// Create modified copy
Product updated = original.toBuilder()
    .price(1199.99)  // Only change price
    .build();
```

---

## 🔍 Lombok vs Custom Builder

| Feature | Lombok @Builder | Custom Builder |
|---------|----------------|----------------|
| **Code Lines** | 1 annotation | 50+ lines |
| **Customization** | Limited | Full control |
| **Validation** | Manual | In `build()` method |
| **Immutability** | Optional | Enforced |
| **Required Fields** | No distinction | Constructor parameters |
| **Default Values** | Field initialization | Builder field initialization |
| **When to Use** | Simple POJOs/DTOs | Complex validation, custom logic |

---

## 🎯 Real-World Use Cases

1. **Product Catalogs** (This demo - Lombok)
   - Many optional attributes (description, manufacturer, etc.)

2. **Email Systems** (This demo - Custom)
   - Required: to, subject, body
   - Optional: cc, bcc, priority, attachments

3. **HTTP Client Requests**
   - URL (required)
   - Headers, timeout, retries (optional)

4. **Database Query Builders**
   - SQL query construction
   - Fluent API for WHERE, ORDER BY, LIMIT

5. **Configuration Objects**
   - Application settings with many optional parameters

6. **Report Generators**
   - Title, data (required)
   - Format, filters, sorting (optional)

---

## 🎓 Key Takeaways

1. **Use Lombok @Builder** for simple POJOs and DTOs
2. **Use Custom Builder** when you need:
   - Complex validation logic
   - Required vs optional field distinction
   - Immutable objects
   - Custom build logic

3. **Builder solves "Telescoping Constructor" problem**
4. **Makes code self-documenting**
5. **Enables fluent, readable API**

---

## 📚 Further Reading

- [Builder Pattern - Refactoring Guru](https://refactoring.guru/design-patterns/builder)
- [Lombok @Builder Documentation](https://projectlombok.org/features/Builder)
- [Effective Java Item 2: Builder Pattern](https://www.oreilly.com/library/view/effective-java/9780134686097/)

---

**Happy Building!** 🏗️✨
