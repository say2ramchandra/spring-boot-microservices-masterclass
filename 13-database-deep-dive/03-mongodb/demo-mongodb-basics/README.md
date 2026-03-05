# Demo: MongoDB Basics

This demo project demonstrates Spring Data MongoDB operations including CRUD, queries, and MongoTemplate usage.

## Overview

### Features Demonstrated

| Feature | Description |
|---------|-------------|
| Repository | Spring Data MongoDB interface |
| MongoTemplate | Programmatic query building |
| CRUD Operations | Create, Read, Update, Delete |
| Queries | Derived, @Query, Criteria API |
| Updates | Set, Inc, Push, Pull |
| Embedded Documents | Nested objects |

## Quick Start

```bash
# Navigate to demo directory
cd 13-database-deep-dive/03-mongodb/demo-mongodb-basics

# Run the application (uses embedded MongoDB)
mvn spring-boot:run
```

**Application starts on port 8084**

> **Note:** This demo uses embedded MongoDB (Flapdoodle) - no MongoDB installation required!

## Project Structure

```
demo-mongodb-basics/
├── src/main/java/com/masterclass/mongodb/basics/
│   ├── MongoDbBasicsDemoApplication.java
│   ├── config/
│   │   └── DataInitializer.java
│   ├── controller/
│   │   └── ProductController.java
│   ├── entity/
│   │   ├── Product.java
│   │   └── Specifications.java
│   ├── repository/
│   │   └── ProductRepository.java
│   └── service/
│       └── ProductService.java
└── src/main/resources/
    └── application.properties
```

## API Endpoints

### Create Operations

```bash
# Create single product
curl -X POST http://localhost:8084/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "New Product",
    "sku": "NEW-001",
    "price": 99.99,
    "category": "Electronics",
    "stockQuantity": 100,
    "tags": ["new", "sale"]
  }'

# Create multiple products
curl -X POST http://localhost:8084/api/products/batch \
  -H "Content-Type: application/json" \
  -d '[
    {"name": "Product 1", "sku": "P1", "price": 10, "category": "Cat1"},
    {"name": "Product 2", "sku": "P2", "price": 20, "category": "Cat2"}
  ]'
```

### Read Operations

```bash
# Get all products
curl http://localhost:8084/api/products

# Get by ID
curl http://localhost:8084/api/products/{id}

# Get by SKU
curl http://localhost:8084/api/products/sku/ELEC-001

# Get by category
curl http://localhost:8084/api/products/category/Electronics

# Search by name
curl "http://localhost:8084/api/products/search?name=book"

# Get by price range
curl "http://localhost:8084/api/products/price-range?min=10&max=100"

# Get available products (in stock)
curl http://localhost:8084/api/products/available/Electronics

# Paginated results
curl "http://localhost:8084/api/products/category/Electronics/paged?page=0&size=5"
```

### Advanced Queries

```bash
# Filter with multiple criteria
curl "http://localhost:8084/api/products/filter?category=Electronics&minPrice=100&minStock=10"

# Regex search
curl "http://localhost:8084/api/products/regex-search?field=name&pattern=pro"

# Search by tags
curl "http://localhost:8084/api/products/by-tags?tags=apple,premium&matchAll=true"

# Paginated with sorting
curl "http://localhost:8084/api/products/paginated?page=0&size=5&sortBy=price&ascending=false"
```

### Update Operations

```bash
# Update entire product
curl -X PUT http://localhost:8084/api/products/{id} \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Updated Product",
    "sku": "ELEC-001",
    "price": 1899.99,
    "category": "Electronics",
    "stockQuantity": 45
  }'

# Update price only
curl -X PATCH "http://localhost:8084/api/products/{id}/price?price=1799.99"

# Increment stock
curl -X PATCH "http://localhost:8084/api/products/{id}/stock/increment?quantity=10"

# Decrement stock
curl -X PATCH "http://localhost:8084/api/products/{id}/stock/decrement?quantity=5"

# Add tag
curl -X POST "http://localhost:8084/api/products/{id}/tags?tag=sale"

# Remove tag
curl -X DELETE "http://localhost:8084/api/products/{id}/tags?tag=sale"

# Apply discount to category
curl -X POST "http://localhost:8084/api/products/category/Electronics/discount?percent=10"
```

### Delete Operations

```bash
# Delete by ID
curl -X DELETE http://localhost:8084/api/products/{id}

# Delete by category
curl -X DELETE http://localhost:8084/api/products/category/Clothing
```

### Utility Endpoints

```bash
# Get total count
curl http://localhost:8084/api/products/count

# Count by category
curl http://localhost:8084/api/products/count/category/Electronics

# Check if SKU exists
curl http://localhost:8084/api/products/exists/sku/ELEC-001
```

## Document Structure

### Sample Product Document

```json
{
  "_id": ObjectId("..."),
  "name": "MacBook Pro 14",
  "sku": "ELEC-001",
  "price": NumberDecimal("1999.99"),
  "category": "Electronics",
  "stock_qty": 50,
  "tags": ["apple", "laptop", "premium"],
  "attributes": {
    "brand": "Apple",
    "processor": "M2 Pro",
    "ram": "16GB"
  },
  "specifications": {
    "weight": 1.6,
    "weightUnit": "kg",
    "dimensions": {
      "length": 31.26,
      "width": 22.12,
      "height": 1.55,
      "unit": "cm"
    }
  },
  "createdAt": ISODate("2024-01-15T10:30:00Z"),
  "updatedAt": ISODate("2024-01-15T10:30:00Z"),
  "version": 0
}
```

## Key Concepts

### Repository vs MongoTemplate

| Repository | MongoTemplate |
|------------|---------------|
| Declarative | Programmatic |
| Method naming convention | Full control |
| Simple queries | Complex queries |
| Less code | More flexibility |

### Query Examples

```java
// Repository - derived query
List<Product> findByCategory(String category);

// Repository - @Query
@Query("{ 'price': { $gte: ?0, $lte: ?1 } }")
List<Product> findByPriceRange(BigDecimal min, BigDecimal max);

// MongoTemplate - Criteria
Query query = new Query(
    new Criteria().andOperator(
        Criteria.where("category").is("Electronics"),
        Criteria.where("price").gte(100),
        Criteria.where("stockQuantity").gt(0)
    )
);
mongoTemplate.find(query, Product.class);
```

### Update Examples

```java
// Atomic field update
Update update = new Update().set("price", newPrice);

// Increment field
Update update = new Update().inc("stockQuantity", 10);

// Add to array (with duplicate prevention)
Update update = new Update().addToSet("tags", "sale");

// Remove from array
Update update = new Update().pull("tags", "sale");

// Update nested field
Update update = new Update().set("attributes.brand", "NewBrand");
```

## Sample Data

The application initializes with:

| Category | Products |
|----------|----------|
| Electronics | MacBook Pro, iPhone, Sony Headphones, iPad |
| Books | Effective Java, Spring in Action, MongoDB Guide |
| Clothing | T-Shirt, Jeans, Limited Sneakers (out of stock) |

## Related Topics

- [MongoDB README](../README.md)
- [MongoDB Aggregation Demo](../demo-mongodb-aggregation/)
- [Hibernate Caching](../../02-hibernate-advanced/demo-hibernate-caching/)
