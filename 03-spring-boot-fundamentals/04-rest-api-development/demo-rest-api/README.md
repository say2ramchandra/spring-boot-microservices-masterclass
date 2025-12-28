# Demo: REST API Development

Complete RESTful API demonstrating CRUD operations with Spring Boot.

## Features

- ✅ Full CRUD operations (Create, Read, Update, Delete)
- ✅ Proper HTTP methods and status codes
- ✅ Request validation
- ✅ Path variables and query parameters
- ✅ 3-layer architecture (Controller → Service → Repository)
- ✅ Sample data initialization

## Running the Application

```bash
cd demo-rest-api
mvn clean install
mvn spring-boot:run
```

Server starts on **http://localhost:8080**

## API Endpoints

### CREATE Product

```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Tablet",
    "description": "10-inch tablet",
    "price": 299.99,
    "stock": 20
  }'
```

**Response: 201 Created**
```json
{
  "id": 6,
  "name": "Tablet",
  "description": "10-inch tablet",
  "price": 299.99,
  "stock": 20,
  "createdAt": "2025-12-17T10:30:00",
  "updatedAt": "2025-12-17T10:30:00"
}
```

### READ All Products

```bash
curl http://localhost:8080/api/products
```

**Response: 200 OK**
```json
[
  {
    "id": 1,
    "name": "Laptop",
    "description": "High-performance laptop",
    "price": 999.99,
    "stock": 10,
    "createdAt": "2025-12-17T10:00:00",
    "updatedAt": "2025-12-17T10:00:00"
  },
  ...
]
```

### READ One Product

```bash
curl http://localhost:8080/api/products/1
```

**Response: 200 OK** (or 404 if not found)

### SEARCH by Name

```bash
curl "http://localhost:8080/api/products?name=laptop"
```

### SEARCH by Price Range

```bash
curl "http://localhost:8080/api/products/search?minPrice=50&maxPrice=200"
```

### UPDATE Product

```bash
curl -X PUT http://localhost:8080/api/products/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Gaming Laptop",
    "description": "Updated description",
    "price": 1299.99,
    "stock": 8
  }'
```

**Response: 200 OK** (or 404 if not found)

### DELETE Product

```bash
curl -X DELETE http://localhost:8080/api/products/1
```

**Response: 204 No Content** (or 404 if not found)

## Project Structure

```
demo-rest-api/
├── controller/
│   └── ProductController.java       # REST endpoints
├── service/
│   └── ProductService.java          # Business logic
├── repository/
│   └── ProductRepository.java       # Data access
├── model/
│   └── Product.java                 # Entity
└── config/
    └── DataInitializer.java         # Sample data
```

## Testing with Postman

Import this collection or test manually:

1. **GET All**: `http://localhost:8080/api/products`
2. **GET One**: `http://localhost:8080/api/products/1`
3. **POST Create**: Body → raw → JSON
4. **PUT Update**: Body → raw → JSON
5. **DELETE**: `http://localhost:8080/api/products/1`

## Validation Examples

### Valid Request
```json
{
  "name": "Valid Product",
  "price": 99.99,
  "stock": 10
}
```
✅ **201 Created**

### Invalid Request (name too short)
```json
{
  "name": "AB",
  "price": 99.99
}
```
❌ **400 Bad Request**

### Invalid Request (negative price)
```json
{
  "name": "Invalid Product",
  "price": -10.00
}
```
❌ **400 Bad Request**

## Status Codes

| Operation | Success | Not Found | Validation Error |
|-----------|---------|-----------|------------------|
| GET | 200 OK | 404 Not Found | - |
| POST | 201 Created | - | 400 Bad Request |
| PUT | 200 OK | 404 Not Found | 400 Bad Request |
| DELETE | 204 No Content | 404 Not Found | - |

## Key Concepts Demonstrated

### 1. @RestController
Combines `@Controller` + `@ResponseBody`

### 2. HTTP Method Mapping
```java
@GetMapping     // GET
@PostMapping    // POST
@PutMapping     // PUT
@DeleteMapping  // DELETE
```

### 3. Path Variables
```java
@GetMapping("/{id}")
public Product get(@PathVariable Long id)
```

### 4. Query Parameters
```java
@GetMapping
public List<Product> search(@RequestParam String name)
```

### 5. Request Body
```java
@PostMapping
public Product create(@RequestBody @Valid Product product)
```

### 6. Response Entity
```java
return ResponseEntity.ok(product);           // 200
return ResponseEntity.notFound().build();    // 404
return ResponseEntity.created(uri).body(p);  // 201
return ResponseEntity.noContent().build();   // 204
```

### 7. Validation
```java
@NotBlank
@Size(min = 3, max = 100)
@DecimalMin("0.01")
```

## Best Practices Demonstrated

✅ RESTful URL design (`/api/products`, not `/getProducts`)  
✅ Proper HTTP methods (GET for read, POST for create, etc.)  
✅ Meaningful status codes (201 for created, 204 for deleted)  
✅ Request validation with `@Valid`  
✅ Layered architecture (Controller → Service → Repository)  
✅ Location header on resource creation  
✅ Query parameters for filtering/searching  
✅ Logging for debugging  

## Common Errors

### 404 Not Found
```bash
curl http://localhost:8080/api/products/999
```
Product doesn't exist

### 400 Bad Request
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{"name": "A"}'  # Too short
```
Validation failed

## H2 Console

Access database console: **http://localhost:8080/h2-console**

- JDBC URL: `jdbc:h2:mem:restdb`
- Username: `sa`
- Password: (empty)

Query products:
```sql
SELECT * FROM products;
```

## Interview Prep

**Q: Difference between @RestController and @Controller?**  
A: `@RestController` = `@Controller` + `@ResponseBody`. Returns data (JSON), not views (HTML).

**Q: When to use PUT vs POST?**  
A: POST for create (non-idempotent), PUT for update (idempotent).

**Q: What status code for successful DELETE?**  
A: 204 No Content (or 200 OK if returning deleted resource).

**Q: How to handle resource not found?**  
A: Return `ResponseEntity.notFound().build()` (404 Not Found).

---

**Related:**
- [Exception Handling](../../06-exception-handling/) - Global error handling
- [Data Access JPA](../../05-data-access-jpa/) - Advanced database operations
