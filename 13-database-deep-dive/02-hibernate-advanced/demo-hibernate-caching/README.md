# Demo: Hibernate Caching

This demo project demonstrates Hibernate's caching mechanisms including L1 cache, L2 cache (with Ehcache), query cache, and collection caching.

## Overview

### Cache Types Demonstrated

| Cache Type | Scope | Provider | Configuration |
|------------|-------|----------|---------------|
| L1 Cache | Session | Built-in | Automatic |
| L2 Cache | SessionFactory | Ehcache 3 | ehcache.xml |
| Query Cache | SessionFactory | Built-in | @QueryHints |
| Collection Cache | SessionFactory | Ehcache 3 | @Cache on collection |

## Quick Start

```bash
# Navigate to demo directory
cd 13-database-deep-dive/02-hibernate-advanced/demo-hibernate-caching

# Run the application
mvn spring-boot:run

# Or using wrapper
./mvnw spring-boot:run
```

**Access Points:**
- Application: http://localhost:8082
- H2 Console: http://localhost:8082/h2-console
- Cache Stats: http://localhost:8082/api/cache/stats

## Project Structure

```
demo-hibernate-caching/
├── src/main/java/com/masterclass/hibernate/caching/
│   ├── HibernateCachingDemoApplication.java
│   ├── config/
│   │   └── DataInitializer.java          # Sample data
│   ├── controller/
│   │   └── CachingDemoController.java    # REST endpoints
│   ├── entity/
│   │   ├── Category.java                 # @Cache annotation
│   │   └── Product.java                  # @Cache annotation
│   ├── listener/
│   │   └── CacheEventLogger.java         # Cache events
│   ├── repository/
│   │   ├── CategoryRepository.java       # Query cache
│   │   └── ProductRepository.java        # Query cache
│   └── service/
│       └── CachingDemoService.java       # Demo logic
└── src/main/resources/
    ├── application.properties            # Hibernate config
    └── ehcache.xml                       # Ehcache config
```

## Demo Endpoints

### L1 Cache Demo
```bash
# L1 cache is session-scoped - same entity loaded once per session
curl http://localhost:8082/api/demo/l1-cache/1
```
Check logs to see that only ONE SQL query is executed despite three `findById()` calls.

### L2 Cache Demo
```bash
# Reset stats first
curl -X POST http://localhost:8082/api/cache/stats/reset

# First call - cache miss, loads from DB
curl http://localhost:8082/api/demo/l2-cache/1

# Check stats - should show 1 miss, 1 put
curl http://localhost:8082/api/cache/stats

# Second call - cache hit, no DB query
curl http://localhost:8082/api/demo/l2-cache/1

# Check stats - should show 1 hit
curl http://localhost:8082/api/cache/stats
```

### Query Cache Demo
```bash
# Cached query for products by category
curl http://localhost:8082/api/demo/query-cache/1

# Call again - results come from query cache
curl http://localhost:8082/api/demo/query-cache/1

# Check query cache stats
curl http://localhost:8082/api/cache/stats
```

### Collection Cache Demo
```bash
# Load category with cached products collection
curl http://localhost:8082/api/demo/collection-cache/1
```

## Cache Statistics

```bash
# Get all cache statistics
curl http://localhost:8082/api/cache/stats | jq

# Sample output:
{
  "secondLevelCacheHitCount": 5,
  "secondLevelCacheMissCount": 2,
  "secondLevelCachePutCount": 7,
  "queryCacheHitCount": 3,
  "queryCacheMissCount": 1,
  "queryExecutionCount": 10
}
```

## Cache Management

```bash
# Reset statistics
curl -X POST http://localhost:8082/api/cache/stats/reset

# Clear L1 cache
curl -X POST http://localhost:8082/api/cache/l1/clear

# Evict all products from L2 cache
curl -X POST http://localhost:8082/api/cache/l2/evict/products

# Evict specific product
curl -X POST http://localhost:8082/api/cache/l2/evict/product/1

# Evict all categories
curl -X POST http://localhost:8082/api/cache/l2/evict/categories
```

## CRUD Operations

```bash
# List all products
curl http://localhost:8082/api/products

# Get product by ID
curl http://localhost:8082/api/products/1

# List all categories
curl http://localhost:8082/api/categories

# Get category by ID
curl http://localhost:8082/api/categories/1

# Search by price range (uses query cache)
curl "http://localhost:8082/api/products/price-range?min=40&max=100"

# Create category
curl -X POST http://localhost:8082/api/categories \
  -H "Content-Type: application/json" \
  -d '{"name":"Sports","description":"Sports equipment"}'

# Create product
curl -X POST http://localhost:8082/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name":"Basketball",
    "description":"Official size basketball",
    "price":29.99,
    "stockQuantity":100,
    "sku":"SPORT-001",
    "categoryId":4
  }'
```

## Key Concepts

### L1 Cache (First-Level Cache)
- **Automatic** - enabled by default
- **Session-scoped** - cached entities only valid within same Hibernate Session
- **Same instance** - multiple loads return same object reference
- **Cleared** - when session closes or `session.clear()` called

### L2 Cache (Second-Level Cache)
- **Requires configuration** - enable in properties
- **SessionFactory-scoped** - shared across sessions
- **By entity ID** - caches entities by primary key
- **Concurrency strategies**: READ_ONLY, NONSTRICT_READ_WRITE, READ_WRITE, TRANSACTIONAL

### Query Cache
- **Caches query results** - parameter values → entity IDs
- **Requires L2 cache** - entities still loaded from L2 cache
- **Invalidation** - invalidated when underlying tables modified
- **Best for** - frequently executed queries with same parameters

### Collection Cache
- **Caches collection IDs** - not the actual entities
- **Works with L2 cache** - entities loaded from L2 cache
- **Invalidation** - invalidated when collection modified

## Configuration Files

### application.properties
```properties
# Enable L2 cache
spring.jpa.properties.hibernate.cache.use_second_level_cache=true

# Cache region factory (JCache/Ehcache)
spring.jpa.properties.hibernate.cache.region.factory_class=org.hibernate.cache.jcache.JCacheRegionFactory

# Enable query cache
spring.jpa.properties.hibernate.cache.use_query_cache=true

# Enable statistics
spring.jpa.properties.hibernate.generate_statistics=true
```

### ehcache.xml
```xml
<!-- Entity cache -->
<cache alias="com.masterclass.hibernate.caching.entity.Product">
    <expiry><ttl unit="minutes">30</ttl></expiry>
    <heap unit="entries">500</heap>
</cache>

<!-- Collection cache -->
<cache alias="com.masterclass.hibernate.caching.entity.Category.products">
    <heap unit="entries">200</heap>
</cache>
```

## Entity Cache Annotations

```java
@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, 
       region = "com.masterclass.hibernate.caching.entity.Product")
public class Product {
    // ...
}
```

## Troubleshooting

### Cache not working?
1. Check `hibernate.cache.use_second_level_cache=true`
2. Verify `@Cache` annotation on entity
3. Ensure ehcache.xml has matching region alias
4. Check logs for cache events

### Query cache not hitting?
1. Ensure `hibernate.cache.use_query_cache=true`
2. Add `@QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))`
3. Query must use exact same parameters

### Statistics showing all zeros?
1. Enable `hibernate.generate_statistics=true`
2. Call `/api/cache/stats/reset` to start fresh
3. Statistics accumulate from application start

## Sample Data

The application initializes with:
- 3 Categories: Electronics, Books, Clothing
- 7 Products distributed across categories

## Related Topics
- [N+1 Problem Solutions](../demo-hibernate-performance/)
- [Hibernate Advanced README](../README.md)
- [JDBC Fundamentals](../../01-jdbc-fundamentals/)
