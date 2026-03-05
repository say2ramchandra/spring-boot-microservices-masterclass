# Shared Service (Backend Service)

> **Central backend service that provides data to both Mobile and Web BFFs**

## 📚 Overview

The Shared Service is a backend microservice that acts as the single source of truth for product data. Both Mobile BFF and Web BFF call this service to retrieve product information, which they then transform and optimize for their respective client types.

---

## 🎯 Purpose

### Why Shared Service?

**Centralized Data Management:**
- Single source of truth for product data
- Consistent data across all BFFs
- Reusable business logic
- Independent of client-specific concerns
- Easier to maintain and test

**Benefits:**
- ✅ Data consistency across platforms
- ✅ Reduced code duplication
- ✅ Easier to scale backend independently
- ✅ Simplified data management
- ✅ Backend can evolve independently

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────┐
│                                                 │
│  ┌──────────────┐         ┌──────────────┐     │
│  │  Mobile BFF  │         │   Web BFF    │     │
│  │  (Port 8081) │         │  (Port 8082) │     │
│  └──────┬───────┘         └───────┬──────┘     │
│         │                         │             │
│         └────────┬─────────┬──────┘             │
│                  ▼         ▼                     │
│         ┌────────────────────────┐              │
│         │   Shared Service       │              │
│         │    (Port 8083)         │              │
│         │                        │              │
│         │  - Product data        │              │
│         │  - Category data       │              │
│         │  - Business logic      │              │
│         │  - Data validation     │              │
│         └───────────┬────────────┘              │
│                     ▼                            │
│         ┌────────────────────┐                  │
│         │  In-Memory Storage │                  │
│         │    (Demo Data)     │                  │
│         └────────────────────┘                  │
└─────────────────────────────────────────────────┘
```

---

## 🚀 Getting Started

### Prerequisites

```bash
# Java 17
java -version

# Maven
mvn -version
```

### Run Shared Service

```bash
# Navigate to shared-service directory
cd shared-service

# Build and run
mvn clean spring-boot:run

# Server starts on: http://localhost:8083
```

**Important:** This service must be running before starting Mobile BFF or Web BFF!

---

## 📡 API Endpoints

### 1. Get All Products

**GET** `/api/products`

**Response:**
```json
[
  {
    "id": 1,
    "name": "MacBook Pro 16\"",
    "description": "Apple MacBook Pro 16-inch with M3 Pro chip",
    "price": 2499.00,
    "category": "Electronics",
    "stock": 25,
    "available": true,
    "imageUrl": "/images/macbook.jpg",
    "createdAt": "2024-01-15T10:00:00Z",
    "updatedAt": "2026-02-15T14:30:00Z"
  },
  {
    "id": 2,
    "name": "Dell UltraSharp Monitor",
    "description": "27-inch 4K USB-C monitor",
    "price": 599.99,
    "category": "Electronics",
    "stock": 40,
    "available": true,
    "imageUrl": "/images/dell_monitor.jpg",
    "createdAt": "2024-01-20T11:00:00Z",
    "updatedAt": "2026-02-14T09:15:00Z"
  }
]
```

**Usage:**
```bash
curl http://localhost:8083/api/products | jq
```

---

### 2. Get Product by ID

**GET** `/api/products/{id}`

**Response:**
```json
{
  "id": 1,
  "name": "MacBook Pro 16\"",
  "description": "Apple MacBook Pro 16-inch with M3 Pro chip",
  "price": 2499.00,
  "category": "Electronics",
  "stock": 25,
  "available": true,
  "imageUrl": "/images/macbook.jpg",
  "createdAt": "2024-01-15T10:00:00Z",
  "updatedAt": "2026-02-15T14:30:00Z"
}
```

**Error Response (404):**
```json
{
  "error": "Product not found",
  "productId": 999
}
```

**Usage:**
```bash
# Valid product
curl http://localhost:8083/api/products/1 | jq

# Non-existent product
curl http://localhost:8083/api/products/999 | jq
```

---

### 3. Get Products by Category

**GET** `/api/products/category/{category}`

**Response:**
```json
[
  {
    "id": 1,
    "name": "MacBook Pro 16\"",
    "description": "Apple MacBook Pro 16-inch with M3 Pro chip",
    "price": 2499.00,
    "category": "Electronics",
    "stock": 25,
    "available": true,
    "imageUrl": "/images/macbook.jpg",
    "createdAt": "2024-01-15T10:00:00Z",
    "updatedAt": "2026-02-15T14:30:00Z"
  },
  {
    "id": 2,
    "name": "Dell UltraSharp Monitor",
    "description": "27-inch 4K USB-C monitor",
    "price": 599.99,
    "category": "Electronics",
    "stock": 40,
    "available": true,
    "imageUrl": "/images/dell_monitor.jpg",
    "createdAt": "2024-01-20T11:00:00Z",
    "updatedAt": "2026-02-14T09:15:00Z"
  }
]
```

**Usage:**
```bash
# Get Electronics products
curl http://localhost:8083/api/products/category/Electronics | jq

# Get Books
curl http://localhost:8083/api/products/category/Books | jq

# Get Accessories
curl http://localhost:8083/api/products/category/Accessories | jq
```

---

### 4. Get All Categories

**GET** `/api/categories`

**Response:**
```json
[
  "Electronics",
  "Books",
  "Accessories"
]
```

**Usage:**
```bash
curl http://localhost:8083/api/categories | jq
```

---

### 5. Search Products

**GET** `/api/products/search?keyword={keyword}`

**Response:**
```json
[
  {
    "id": 1,
    "name": "MacBook Pro 16\"",
    "description": "Apple MacBook Pro 16-inch with M3 Pro chip",
    "price": 2499.00,
    "category": "Electronics",
    "stock": 25,
    "available": true,
    "imageUrl": "/images/macbook.jpg",
    "createdAt": "2024-01-15T10:00:00Z",
    "updatedAt": "2026-02-15T14:30:00Z"
  }
]
```

**Search Logic:**
- Case-insensitive
- Searches in product name and description
- Returns empty array if no matches

**Usage:**
```bash
# Search for "laptop"
curl "http://localhost:8083/api/products/search?keyword=laptop" | jq

# Search for "monitor"
curl "http://localhost:8083/api/products/search?keyword=monitor" | jq

# No results
curl "http://localhost:8083/api/products/search?keyword=nonexistent" | jq
```

---

### 6. Create Product

**POST** `/api/products`

**Request Body:**
```json
{
  "name": "iPhone 15 Pro",
  "description": "Latest iPhone with A17 Pro chip",
  "price": 999.99,
  "category": "Electronics",
  "stock": 100,
  "available": true,
  "imageUrl": "/images/iphone15.jpg"
}
```

**Response (201 Created):**
```json
{
  "id": 9,
  "name": "iPhone 15 Pro",
  "description": "Latest iPhone with A17 Pro chip",
  "price": 999.99,
  "category": "Electronics",
  "stock": 100,
  "available": true,
  "imageUrl": "/images/iphone15.jpg",
  "createdAt": "2026-02-16T10:45:00Z",
  "updatedAt": "2026-02-16T10:45:00Z"
}
```

**Validation Rules:**
- `name`: Required, 2-100 characters
- `price`: Required, must be > 0
- `category`: Required
- `stock`: Optional, default 0

**Usage:**
```bash
curl -X POST http://localhost:8083/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "iPhone 15 Pro",
    "description": "Latest iPhone",
    "price": 999.99,
    "category": "Electronics",
    "stock": 100,
    "available": true,
    "imageUrl": "/images/iphone15.jpg"
  }' | jq
```

---

### 7. Update Product

**PUT** `/api/products/{id}`

**Request Body:**
```json
{
  "name": "MacBook Pro 16\" (Updated)",
  "description": "Updated description",
  "price": 2399.00,
  "category": "Electronics",
  "stock": 30,
  "available": true,
  "imageUrl": "/images/macbook_updated.jpg"
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "MacBook Pro 16\" (Updated)",
  "description": "Updated description",
  "price": 2399.00,
  "category": "Electronics",
  "stock": 30,
  "available": true,
  "imageUrl": "/images/macbook_updated.jpg",
  "createdAt": "2024-01-15T10:00:00Z",
  "updatedAt": "2026-02-16T10:50:00Z"
}
```

**Usage:**
```bash
curl -X PUT http://localhost:8083/api/products/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "MacBook Pro 16\" (Updated)",
    "description": "Updated description",
    "price": 2399.00,
    "category": "Electronics",
    "stock": 30,
    "available": true,
    "imageUrl": "/images/macbook_updated.jpg"
  }' | jq
```

---

### 8. Delete Product

**DELETE** `/api/products/{id}`

**Response (204 No Content)**

**Usage:**
```bash
curl -X DELETE http://localhost:8083/api/products/1

# Verify deletion
curl http://localhost:8083/api/products/1
# Returns 404 Not Found
```

---

## 📊 Initial Demo Data

The service initializes with 8 sample products:

### Electronics (4 products)
1. **MacBook Pro 16"** - $2,499.00
2. **Dell UltraSharp Monitor** - $599.99
3. **Mechanical Keyboard** - $149.99
4. **Webcam HD** - $79.99

### Books (2 products)
5. **Spring Boot in Action** - $49.99
6. **Microservices Patterns** - $59.99

### Accessories (2 products)
7. **Wireless Mouse** - $29.99
8. **USB-C Cable** - $12.99

---

## ⚙️ Configuration

**application.yml**

```yaml
server:
  port: 8083

spring:
  application:
    name: shared-service

# Logging
logging:
  level:
    com.masterclass: INFO
  pattern:
    console: "%d{HH:mm:ss.SSS} - %msg%n"
```

---

## 🏗️ Architecture Decisions

### In-Memory Storage

```java
// Uses ConcurrentHashMap for thread-safe operations
private final Map<Long, Product> products = new ConcurrentHashMap<>();
```

**Why In-Memory?**
- ✅ Demo simplicity
- ✅ No database setup required
- ✅ Fast development
- ✅ Easy to reset

**Production Alternative:**
- Use PostgreSQL/MySQL
- Add Spring Data JPA
- Implement proper transactions
- Add database migrations

### Data Model

```java
public class Product {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private Integer stock;
    private boolean available;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

---

## 🔄 How BFFs Use This Service

### Mobile BFF Flow

```
Mobile App → Mobile BFF → Shared Service
         ← (Optimized)  ← (Raw Data)
```

**Example:**
```java
// Shared Service returns:
{
  "id": 1,
  "name": "MacBook Pro 16\"",
  "description": "Apple MacBook Pro 16-inch...",
  "price": 2499.00,
  "category": "Electronics",
  "imageUrl": "/images/macbook.jpg"
}

// Mobile BFF transforms to:
{
  "id": 1,
  "name": "MacBook Pro 16\"",
  "desc": "Apple MacBook Pro...",  // Shortened
  "price": 2499.00,
  "img": "/thumb_macbook.jpg",     // Thumbnail only
  "cat": "Electronics"             // Abbreviated
}
```

### Web BFF Flow

```
Web App → Web BFF → Shared Service
      ← (Enriched) ← (Raw Data)
```

**Example:**
```java
// Shared Service returns:
{
  "id": 1,
  "name": "MacBook Pro 16\"",
  "price": 2499.00
}

// Web BFF enriches to:
{
  "id": 1,
  "name": "MacBook Pro 16\"",
  "price": 2499.00,
  "category": {                    // Nested object
    "name": "Electronics",
    "description": "..."
  },
  "images": {                      // Multiple sizes
    "thumbnail": "...",
    "medium": "...",
    "large": "..."
  },
  "relatedProducts": [...]         // Additional data
}
```

---

## 🧪 Testing

### Unit Tests

```bash
# Run unit tests
mvn test

# Expected tests:
# - ProductServiceTest
# - ProductControllerTest
# - DataInitializerTest
```

### Manual Testing

```bash
# Start service
mvn spring-boot:run

# Test all endpoints
./test-endpoints.sh

# Or test individually:
curl http://localhost:8083/api/products
curl http://localhost:8083/api/products/1
curl http://localhost:8083/api/products/category/Electronics
curl http://localhost:8083/api/categories
curl "http://localhost:8083/api/products/search?keyword=laptop"
```

### Integration Testing with BFFs

```bash
# Terminal 1: Start Shared Service
cd shared-service && mvn spring-boot:run

# Terminal 2: Start Mobile BFF
cd mobile-bff && mvn spring-boot:run

# Terminal 3: Start Web BFF
cd web-bff && mvn spring-boot:run

# Terminal 4: Test complete flow
curl http://localhost:8081/api/mobile/dashboard  # Mobile
curl http://localhost:8082/api/web/dashboard     # Web
```

---

## 📊 Performance Considerations

### Response Times

```
GET /api/products          < 50ms
GET /api/products/{id}     < 10ms
GET /api/products/category < 30ms
POST /api/products         < 20ms
```

### Scalability

**Current (Demo):**
- Single instance
- In-memory storage
- No caching

**Production Recommendations:**
- Add Redis caching
- Database with indexes
- Load balancing
- Connection pooling

---

## 🐛 Troubleshooting

### Issue: Port 8083 Already in Use

```bash
# Find process using port 8083
netstat -ano | findstr :8083

# Kill the process
taskkill /PID <pid> /F

# Or change port in application.yml
server.port: 8084
```

### Issue: BFFs Can't Connect

```bash
# Verify shared service is running
curl http://localhost:8083/actuator/health

# Check logs for errors
# Look for: "Started SharedServiceApplication"

# Verify firewall allows localhost connections
```

### Issue: No Data Returned

```bash
# Check if DataInitializer ran
# Logs should show: "Initialized 8 sample products"

# If not, restart service
```

---

## 🎓 Key Takeaways

1. ✅ **Single source of truth** - Centralized data management
2. ✅ **Platform-agnostic** - Doesn't know about mobile/web differences
3. ✅ **Reusable** - Multiple BFFs can consume
4. ✅ **Simple interface** - Standard REST API
5. ✅ **Easy to test** - No client-specific logic
6. ✅ **Independent lifecycle** - Can be developed separately

---

## 🔗 Related Services

- **Mobile BFF** - [../mobile-bff/](../mobile-bff/) - Consumes and optimizes for mobile
- **Web BFF** - [../web-bff/](../web-bff/) - Consumes and enriches for web

---

## 📈 Production Enhancements

### Database Integration

```xml
<!-- Add to pom.xml -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
</dependency>
```

### Caching

```java
@EnableCaching
public class SharedServiceApplication {
    // Add Redis cache
}

@Cacheable("products")
public Product getProductById(Long id) {
    // Method will be cached
}
```

### API Versioning

```java
@RequestMapping("/api/v1/products")
public class ProductController {
    // Version 1 API
}

@RequestMapping("/api/v2/products")
public class ProductControllerV2 {
    // Version 2 API with breaking changes
}
```

---

**Shared Service is the foundation of the BFF pattern!** 🏗️✨
