# REST API Development with Spring Boot

> **Building production-ready RESTful web services**

## 📚 Table of Contents

- [What is REST?](#what-is-rest)
- [REST Principles](#rest-principles)
- [HTTP Methods](#http-methods)
- [Spring REST Annotations](#spring-rest-annotations)
- [Request and Response Handling](#request-and-response-handling)
- [Status Codes](#status-codes)
- [CRUD Operations](#crud-operations)
- [Best Practices](#best-practices)
- [Demo Project](#demo-project)
- [Interview Questions](#interview-questions)

---

## What is REST?

**REST** (Representational State Transfer) is an architectural style for designing networked applications. It uses HTTP protocol methods to manipulate resources.

### Key Concepts

- **Resource**: Any information that can be named (user, product, order)
- **URI**: Unique identifier for a resource (`/api/products/123`)
- **Representation**: Format of resource (JSON, XML)
- **Stateless**: Each request contains all information needed

---

## REST Principles

### 1. Client-Server Architecture
Client and server are independent. Server exposes API, client consumes it.

### 2. Stateless
Each request is independent. No session state on server.

### 3. Cacheable
Responses should indicate if they're cacheable.

### 4. Uniform Interface
Standardized way to interact with resources using HTTP methods.

### 5. Layered System
Client doesn't know if connected directly to server or through intermediary.

### 6. Resource-Based
Everything is a resource with a unique URI.

---

## HTTP Methods

| Method | Purpose | Idempotent | Safe | Request Body | Response Body |
|--------|---------|------------|------|--------------|---------------|
| **GET** | Retrieve resource(s) | Yes | Yes | No | Yes |
| **POST** | Create new resource | No | No | Yes | Yes |
| **PUT** | Update/Replace resource | Yes | No | Yes | Yes |
| **PATCH** | Partial update | No | No | Yes | Yes |
| **DELETE** | Remove resource | Yes | No | No | Optional |

### Examples

```
GET    /api/products        - Get all products
GET    /api/products/1      - Get product by ID
POST   /api/products        - Create new product
PUT    /api/products/1      - Update product (full)
PATCH  /api/products/1      - Update product (partial)
DELETE /api/products/1      - Delete product
```

---

## Spring REST Annotations

### @RestController

Combines `@Controller` and `@ResponseBody`:

```java
@RestController
@RequestMapping("/api/products")
public class ProductController {
    // Methods automatically return JSON
}
```

### @RequestMapping

Maps HTTP requests to methods:

```java
@RequestMapping(value = "/products", method = RequestMethod.GET)
public List<Product> getAllProducts() {
    return productService.findAll();
}

// Shorthand versions:
@GetMapping("/products")
@PostMapping("/products")
@PutMapping("/products/{id}")
@DeleteMapping("/products/{id}")
```

### @PathVariable

Extract values from URI:

```java
@GetMapping("/products/{id}")
public Product getProduct(@PathVariable Long id) {
    return productService.findById(id);
}
```

### @RequestParam

Extract query parameters:

```java
@GetMapping("/products")
public List<Product> searchProducts(
    @RequestParam(required = false) String name,
    @RequestParam(defaultValue = "0") int page
) {
    return productService.search(name, page);
}
```

### @RequestBody

Bind request body to object:

```java
@PostMapping("/products")
public Product createProduct(@RequestBody @Valid ProductRequest request) {
    return productService.create(request);
}
```

---

## Request and Response Handling

### Request Bodies

```java
@PostMapping("/products")
public ResponseEntity<Product> createProduct(@RequestBody Product product) {
    Product saved = productService.save(product);
    return ResponseEntity.status(HttpStatus.CREATED).body(saved);
}
```

### Path Variables

```java
@GetMapping("/products/{id}/reviews/{reviewId}")
public Review getReview(
    @PathVariable Long id,
    @PathVariable Long reviewId
) {
    return reviewService.findByProductAndId(id, reviewId);
}
```

### Query Parameters

```java
// GET /api/products?category=electronics&sort=price&page=0
@GetMapping("/products")
public List<Product> getProducts(
    @RequestParam String category,
    @RequestParam(defaultValue = "name") String sort,
    @RequestParam(defaultValue = "0") int page
) {
    return productService.findByCategory(category, sort, page);
}
```

### Response Entity

Full control over response:

```java
@GetMapping("/products/{id}")
public ResponseEntity<Product> getProduct(@PathVariable Long id) {
    return productService.findById(id)
        .map(product -> ResponseEntity.ok(product))
        .orElse(ResponseEntity.notFound().build());
}
```

---

## Status Codes

### Success Codes (2xx)

| Code | Meaning | Usage |
|------|---------|-------|
| **200 OK** | Success | GET, PUT successful |
| **201 Created** | Resource created | POST successful |
| **204 No Content** | Success, no body | DELETE successful |

### Client Error Codes (4xx)

| Code | Meaning | Usage |
|------|---------|-------|
| **400 Bad Request** | Invalid input | Validation failed |
| **404 Not Found** | Resource not found | GET by ID failed |
| **409 Conflict** | Resource conflict | Duplicate resource |

### Server Error Codes (5xx)

| Code | Meaning | Usage |
|------|---------|-------|
| **500 Internal Server Error** | Server error | Unexpected exception |

### Examples

```java
// 200 OK
return ResponseEntity.ok(product);

// 201 Created
return ResponseEntity.status(HttpStatus.CREATED).body(product);

// 204 No Content
return ResponseEntity.noContent().build();

// 404 Not Found
return ResponseEntity.notFound().build();

// 400 Bad Request
return ResponseEntity.badRequest().body(errorDetails);
```

---

## CRUD Operations

### Complete CRUD Example

```java
@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    // CREATE
    @PostMapping
    public ResponseEntity<Product> create(@RequestBody @Valid Product product) {
        Product saved = productService.save(product);
        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(saved.getId())
            .toUri();
        return ResponseEntity.created(location).body(saved);
    }

    // READ ALL
    @GetMapping
    public List<Product> getAll() {
        return productService.findAll();
    }

    // READ ONE
    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(@PathVariable Long id) {
        return productService.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Product> update(
        @PathVariable Long id,
        @RequestBody @Valid Product product
    ) {
        return productService.update(id, product)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (productService.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
```

---

## Best Practices

### 1. Use Proper HTTP Methods

```java
// ✅ Good - RESTful
POST   /api/products          // Create
GET    /api/products          // List all
GET    /api/products/1        // Get one
PUT    /api/products/1        // Update
DELETE /api/products/1        // Delete

// ❌ Bad - Non-RESTful
POST   /api/createProduct
GET    /api/getProduct?id=1
POST   /api/updateProduct
POST   /api/deleteProduct
```

### 2. Use Nouns, Not Verbs

```java
// ✅ Good
GET /api/products
POST /api/orders

// ❌ Bad
GET /api/getProducts
POST /api/createOrder
```

### 3. Use Plural Nouns

```java
// ✅ Good
/api/products
/api/users

// ❌ Bad
/api/product
/api/user
```

### 4. Use Resource Nesting

```java
// ✅ Good - Clear hierarchy
GET /api/products/1/reviews
GET /api/users/123/orders

// ❌ Bad - Unclear relationship
GET /api/reviews?productId=1
```

### 5. Version Your API

```java
@RequestMapping("/api/v1/products")
public class ProductControllerV1 { }

@RequestMapping("/api/v2/products")
public class ProductControllerV2 { }
```

### 6. Use DTOs for Request/Response

```java
// Don't expose entities directly
public class ProductRequest {
    private String name;
    private Double price;
    // No id, timestamps, etc.
}

public class ProductResponse {
    private Long id;
    private String name;
    private Double price;
    private LocalDateTime createdAt;
}
```

### 7. Return Proper Status Codes

```java
@PostMapping
public ResponseEntity<Product> create(@RequestBody Product product) {
    Product saved = service.save(product);
    return ResponseEntity.status(HttpStatus.CREATED).body(saved);
}

@DeleteMapping("/{id}")
public ResponseEntity<Void> delete(@PathVariable Long id) {
    service.delete(id);
    return ResponseEntity.noContent().build();
}
```

### 8. Validate Input

```java
@PostMapping
public ResponseEntity<Product> create(@RequestBody @Valid ProductRequest request) {
    // @Valid triggers validation
    Product saved = service.save(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(saved);
}
```

---

## Demo Project

See [demo-rest-api](demo-rest-api/) for a complete REST API with:
- Full CRUD operations
- Proper layered architecture
- Validation
- Exception handling
- DTO pattern

---

## Interview Questions

### Q1: What's the difference between @Controller and @RestController?

**Answer:**
- `@Controller`: Returns views (HTML templates)
- `@RestController`: Returns data (JSON/XML). It's `@Controller` + `@ResponseBody`

```java
@RestController  // Automatically converts return to JSON
public class ProductController {
    @GetMapping("/products")
    public List<Product> getAll() {
        return service.findAll();  // Returns JSON automatically
    }
}
```

### Q2: What's the difference between @PathVariable and @RequestParam?

**Answer:**

**@PathVariable**: Part of URL path
```java
GET /api/products/123
@GetMapping("/products/{id}")
public Product get(@PathVariable Long id) { }
```

**@RequestParam**: Query parameter
```java
GET /api/products?category=electronics
@GetMapping("/products")
public List<Product> get(@RequestParam String category) { }
```

### Q3: When to use PUT vs PATCH?

**Answer:**
- **PUT**: Replace entire resource (send all fields)
- **PATCH**: Update specific fields (send only changed fields)

```java
// PUT - Must send complete object
@PutMapping("/{id}")
public Product update(@PathVariable Long id, @RequestBody Product product) {
    // Replace all fields
}

// PATCH - Send only changed fields
@PatchMapping("/{id}")
public Product partialUpdate(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
    // Update only provided fields
}
```

### Q4: How do you handle 404 Not Found?

**Answer:**

```java
@GetMapping("/{id}")
public ResponseEntity<Product> getById(@PathVariable Long id) {
    return service.findById(id)
        .map(ResponseEntity::ok)              // 200 if found
        .orElse(ResponseEntity.notFound().build());  // 404 if not found
}
```

### Q5: What status code for creating a resource?

**Answer:** **201 Created** with `Location` header:

```java
@PostMapping
public ResponseEntity<Product> create(@RequestBody Product product) {
    Product saved = service.save(product);
    URI location = ServletUriComponentsBuilder
        .fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(saved.getId())
        .toUri();
    return ResponseEntity.created(location).body(saved);
}
```

Response:
```
HTTP/1.1 201 Created
Location: /api/products/123
Content-Type: application/json

{
  "id": 123,
  "name": "Product"
}
```

---

## Summary

| Aspect | Best Practice |
|--------|---------------|
| **URLs** | Use nouns, plural, resource-based |
| **Methods** | GET, POST, PUT, DELETE appropriately |
| **Status Codes** | 200 OK, 201 Created, 204 No Content, 404 Not Found |
| **Validation** | Use @Valid with @RequestBody |
| **Responses** | Use ResponseEntity for full control |
| **Architecture** | Controller → Service → Repository layers |

REST APIs are the backbone of modern web applications. Following these conventions ensures your APIs are intuitive and maintainable.

---

**Next**: [Data Access with JPA](../05-data-access-jpa/)
