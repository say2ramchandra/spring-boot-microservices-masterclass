# Product REST API Demo

> **Complete CRUD REST API with Spring Boot, JPA, validation, and exception handling**

## 🎯 What You'll Learn

- Building RESTful APIs with Spring Boot
- CRUD operations with Spring Data JPA
- Request/Response handling with DTOs
- Input validation with Bean Validation
- Global exception handling
- Database integration (H2)
- Repository pattern
- Service layer design

---

## 🏗️ Architecture

```
┌────────────────────────────────────────────────────┐
│              REST API Architecture                  │
├────────────────────────────────────────────────────┤
│                                                     │
│  Controller (HTTP Layer)                           │
│  ├─ Handle HTTP requests/responses                 │
│  ├─ Validate input (@Valid)                        │
│  └─ Return appropriate status codes                │
│          ↓                                          │
│  Service (Business Layer)                          │
│  ├─ Business logic                                 │
│  ├─ Entity ↔ DTO conversion                       │
│  └─ Transaction management                         │
│          ↓                                          │
│  Repository (Data Layer)                           │
│  ├─ Database operations                            │
│  ├─ Query methods                                  │
│  └─ Custom queries                                 │
│          ↓                                          │
│  Database (H2)                                     │
│                                                     │
└────────────────────────────────────────────────────┘
```

---

## 📂 Project Structure

```
demo-rest-api/
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   ├── java/com/masterclass/
    │   │   ├── ProductRestApiApplication.java    # Main class
    │   │   ├── controller/
    │   │   │   └── ProductController.java         # REST endpoints
    │   │   ├── service/
    │   │   │   └── ProductService.java            # Business logic
    │   │   ├── repository/
    │   │   │   └── ProductRepository.java         # Data access
    │   │   ├── model/
    │   │   │   └── Product.java                   # JPA entity
    │   │   ├── dto/
    │   │   │   └── ProductDTO.java                # Data Transfer Object
    │   │   └── exception/
    │   │       ├── ResourceNotFoundException.java
    │   │       └── GlobalExceptionHandler.java    # Global error handler
    │   └── resources/
    │       ├── application.properties              # Configuration
    │       └── data.sql                           # Sample data
    └── test/
        └── java/
```

---

## 🚀 How to Run

### Option 1: Using Maven

```bash
cd demo-rest-api
mvn spring-boot:run
```

### Option 2: Using IDE

1. Open project in your IDE (IntelliJ IDEA, Eclipse, VS Code)
2. Run `ProductRestApiApplication.java`

### Option 3: Build and Run JAR

```bash
mvn clean package
java -jar target/demo-rest-api-1.0.0.jar
```

---

## 📡 API Endpoints

### Base URL
```
http://localhost:8080/api/products
```

### 1. Get All Products

```bash
GET /api/products

curl http://localhost:8080/api/products
```

**Response** (200 OK):
```json
[
  {
    "id": 1,
    "name": "Laptop",
    "description": "High-performance laptop",
    "price": 999.99,
    "quantity": 10
  },
  ...
]
```

---

### 2. Get Product by ID

```bash
GET /api/products/{id}

curl http://localhost:8080/api/products/1
```

**Response** (200 OK):
```json
{
  "id": 1,
  "name": "Laptop",
  "description": "High-performance laptop",
  "price": 999.99,
  "quantity": 10
}
```

**Error Response** (404 NOT FOUND):
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Product not found with id: 999",
  "path": "/api/products/999"
}
```

---

### 3. Create Product

```bash
POST /api/products

curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Wireless Keyboard",
    "description": "Ergonomic wireless keyboard",
    "price": 49.99,
    "quantity": 25
  }'
```

**Response** (201 CREATED):
```json
{
  "id": 6,
  "name": "Wireless Keyboard",
  "description": "Ergonomic wireless keyboard",
  "price": 49.99,
  "quantity": 25
}
```

**Validation Error** (400 BAD REQUEST):
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Validation Failed",
  "errors": {
    "name": "Product name is required",
    "price": "Price must be at least 0.01"
  },
  "path": "/api/products"
}
```

---

### 4. Update Product

```bash
PUT /api/products/{id}

curl -X PUT http://localhost:8080/api/products/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Updated Laptop",
    "description": "Updated description",
    "price": 899.99,
    "quantity": 8
  }'
```

**Response** (200 OK):
```json
{
  "id": 1,
  "name": "Updated Laptop",
  "description": "Updated description",
  "price": 899.99,
  "quantity": 8
}
```

---

### 5. Delete Product

```bash
DELETE /api/products/{id}

curl -X DELETE http://localhost:8080/api/products/1
```

**Response** (204 NO CONTENT):
```
(empty response body)
```

---

### 6. Search Products by Name

```bash
GET /api/products/search?name={keyword}

curl "http://localhost:8080/api/products/search?name=laptop"
```

**Response** (200 OK):
```json
[
  {
    "id": 1,
    "name": "Laptop",
    "description": "High-performance laptop",
    "price": 999.99,
    "quantity": 10
  }
]
```

---

### 7. Get Products by Price Range

```bash
GET /api/products/price-range?min={min}&max={max}

curl "http://localhost:8080/api/products/price-range?min=20&max=100"
```

---

### 8. Get Low Stock Products

```bash
GET /api/products/low-stock?threshold={number}

curl "http://localhost:8080/api/products/low-stock?threshold=15"
```

---

## 🗄️ Database Access

### H2 Console

Access the H2 database console at: **http://localhost:8080/h2-console**

**Connection Details**:
- **JDBC URL**: `jdbc:h2:mem:productdb`
- **Username**: `sa`
- **Password**: (leave empty)

---

## 🧪 Testing with Postman

### Import Collection

1. Open Postman
2. Create a new collection: "Product API"
3. Add requests for each endpoint
4. Set environment variables:
   - `baseUrl`: `http://localhost:8080`

### Sample Requests

**Create Product**:
- Method: POST
- URL: `{{baseUrl}}/api/products`
- Body (JSON):
  ```json
  {
    "name": "Test Product",
    "description": "Test Description",
    "price": 99.99,
    "quantity": 10
  }
  ```

---

## 💡 Key Concepts

### 1. REST Principles

- **Resource-based**: URLs represent resources (`/products`)
- **HTTP Methods**: Use appropriate verbs (GET, POST, PUT, DELETE)
- **Stateless**: Each request contains all needed information
- **Status Codes**: Use proper HTTP status codes

### 2. DTO Pattern

DTOs (Data Transfer Objects) decouple API from internal entities:
- **ProductDTO**: What API exposes
- **Product**: Internal JPA entity

### 3. Service Layer

Business logic separation:
- Controllers handle HTTP
- Services handle business logic
- Repositories handle data access

### 4. Repository Pattern

Spring Data JPA auto-implements repositories:
```java
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByNameContaining(String name);  // Auto-implemented!
}
```

### 5. Validation

Bean Validation API validates input:
```java
@NotBlank(message = "Name is required")
@Size(min = 2, max = 100)
private String name;
```

### 6. Exception Handling

`@RestControllerAdvice` handles exceptions globally:
- Consistent error responses
- Proper HTTP status codes
- Field-level validation errors

---

## 📊 Sample Data

The application comes with 5 pre-loaded products:

1. **Laptop** - $999.99 (Qty: 10)
2. **Mouse** - $29.99 (Qty: 50)
3. **Keyboard** - $79.99 (Qty: 30)
4. **Monitor** - $399.99 (Qty: 15)
5. **Headphones** - $199.99 (Qty: 25)

---

## 🎓 Practice Exercises

1. **Add a new endpoint**: `GET /api/products/expensive?price=X` to find products above a price
2. **Implement PATCH**: Update only specific fields instead of entire product
3. **Add categories**: Extend Product entity with category field
4. **Implement pagination**: Use `Pageable` for paginated results
5. **Add sorting**: Support sorting by price, name, etc.

---

## 🐛 Common Issues

### Port 8080 Already in Use

Change port in `application.properties`:
```properties
server.port=8081
```

### Database Connection Error

Verify H2 configuration in `application.properties`.

---

## 📚 Related Topics

- [Spring Data JPA](../../02-data-jpa/)
- [Bean Validation](../../03-validation/)
- [Exception Handling](../../04-exception-handling/)

---

**Requirements**: Java 17+, Maven 3.8+

_Build production-ready REST APIs! 🚀_
