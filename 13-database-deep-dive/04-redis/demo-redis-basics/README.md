# Redis Basics Demo

This demo showcases Redis integration with Spring Boot, demonstrating all major Redis data structures, Spring Cache abstraction, and RedisTemplate operations.

## Features Demonstrated

- **String Operations** - Basic key-value storage with TTL
- **Hash Operations** - Storing complex objects as hashes
- **List Operations** - Queues and stacks
- **Set Operations** - Unique collections with set operations
- **Sorted Set Operations** - Leaderboards and rankings
- **Spring Cache** - `@Cacheable`, `@CachePut`, `@CacheEvict` annotations
- **Embedded Redis** - No external Redis needed for development

## Project Structure

```
demo-redis-basics/
├── pom.xml
└── src/main/
    └── java/com/masterclass/redis/basics/
        ├── RedisBasicsDemoApplication.java
        ├── config/
        │   ├── RedisConfig.java           # RedisTemplate & CacheManager
        │   ├── EmbeddedRedisConfig.java   # Embedded Redis server
        │   └── DataInitializer.java       # Sample data
        ├── controller/
        │   └── RedisController.java       # REST endpoints
        ├── entity/
        │   └── Product.java               # Domain entity
        └── service/
            ├── RedisService.java          # RedisTemplate operations
            └── ProductCacheService.java   # Spring Cache demo
```

## Running the Demo

```bash
cd 13-database-deep-dive/04-redis/demo-redis-basics
mvn spring-boot:run
```

- Application: http://localhost:8086
- Embedded Redis: localhost:6370

## API Endpoints

### String Operations

```bash
# Set a string
curl -X POST "http://localhost:8086/api/string/mykey?value=Hello%20Redis"

# Set with TTL (expires in 60 seconds)
curl -X POST "http://localhost:8086/api/string/tempkey?value=Temporary&ttlSeconds=60"

# Get a string
curl http://localhost:8086/api/string/mykey

# Increment counter
curl -X POST http://localhost:8086/api/counter/page:views/increment

# Increment by delta
curl -X POST "http://localhost:8086/api/counter/page:views/increment?delta=10"
```

### Hash Operations

```bash
# Set hash fields
curl -X POST http://localhost:8086/api/hash/user:1002 \
  -H "Content-Type: application/json" \
  -d '{"name":"John","email":"john@example.com","age":"30"}'

# Set single field
curl -X POST "http://localhost:8086/api/hash/user:1002/status?value=active"

# Get all hash fields
curl http://localhost:8086/api/hash/user:1002

# Get single field
curl http://localhost:8086/api/hash/user:1002/email
```

### List Operations (Queue/Stack)

```bash
# Push to list (queue: push right)
curl -X POST "http://localhost:8086/api/list/tasks/push?value=Task1&direction=right"
curl -X POST "http://localhost:8086/api/list/tasks/push?value=Task2&direction=right"
curl -X POST "http://localhost:8086/api/list/tasks/push?value=Task3&direction=right"

# Get list contents
curl http://localhost:8086/api/list/tasks

# Pop from list (queue: pop left)
curl -X POST "http://localhost:8086/api/list/tasks/pop?direction=left"

# Stack: push left, pop left
curl -X POST "http://localhost:8086/api/list/stack/push?value=Item1&direction=left"
```

### Set Operations

```bash
# Add to set
curl -X POST "http://localhost:8086/api/set/skills/add?values=java,python,redis"

# Get set members
curl http://localhost:8086/api/set/skills

# Check membership
curl "http://localhost:8086/api/set/skills/ismember?value=java"

# Set intersection
curl -X POST "http://localhost:8086/api/set/intersection?key1=tags:programming&key2=tags:database"
```

### Sorted Set Operations (Leaderboard)

```bash
# Add player score
curl -X POST "http://localhost:8086/api/leaderboard/game/add?member=Player1&score=1500"

# Update score (increment)
curl -X POST "http://localhost:8086/api/leaderboard/game/score?member=Player1&delta=100"

# Get top 10 leaderboard
curl http://localhost:8086/api/leaderboard/game

# Get specific range
curl "http://localhost:8086/api/leaderboard/game?start=0&end=4"

# Get player rank
curl "http://localhost:8086/api/leaderboard/game/rank?member=Diana"
```

### Product Cache Operations

```bash
# Get all products (first call hits "database", subsequent calls use cache)
curl http://localhost:8086/api/products

# Get by ID (cached after first call)
curl http://localhost:8086/api/products/1

# Get by SKU
curl http://localhost:8086/api/products/sku/SKU-001

# Get by category
curl http://localhost:8086/api/products/category/Electronics

# Create product (updates cache)
curl -X POST http://localhost:8086/api/products \
  -H "Content-Type: application/json" \
  -d '{"id":7,"sku":"SKU-007","name":"Wireless Mouse","category":"Accessories","price":29.99,"stock":150}'

# Update product (evicts and updates cache)
curl -X PUT http://localhost:8086/api/products/1 \
  -H "Content-Type: application/json" \
  -d '{"id":1,"sku":"SKU-001","name":"MacBook Pro 16 M3","category":"Electronics","price":2599.99,"stock":45}'

# Update price only
curl -X PATCH "http://localhost:8086/api/products/1/price?price=2399.99"

# Delete product (evicts from cache)
curl -X DELETE http://localhost:8086/api/products/7

# Clear all product cache
curl -X POST http://localhost:8086/api/products/cache/clear
```

### Key Operations

```bash
# List all keys
curl http://localhost:8086/api/keys

# List keys matching pattern
curl "http://localhost:8086/api/keys?pattern=user:*"

# Check if key exists
curl http://localhost:8086/api/key/greeting/exists

# Get TTL
curl http://localhost:8086/api/key/greeting/ttl

# Set expiry
curl -X POST "http://localhost:8086/api/key/greeting/expire?seconds=300"

# Delete key
curl -X DELETE http://localhost:8086/api/key/tempkey
```

## Observing Cache Behavior

Watch application logs to see cache behavior:

```
>>> Cache HIT for product ID: 1
>>> Cache MISS for product ID: 1
>>> EVICTING product from cache: 1
>>> Fetching ALL products from database
>>> Cache updated for product: SKU-007
```

### Cache Test Sequence

```bash
# 1. First call - cache miss (observe log: "Cache MISS...")
curl http://localhost:8086/api/products/1

# 2. Second call - cache hit (observe log: "Cache HIT...")
curl http://localhost:8086/api/products/1

# 3. Update - evicts and updates cache
curl -X PATCH "http://localhost:8086/api/products/1/price?price=2299.99"

# 4. Next get - cache hit with new data
curl http://localhost:8086/api/products/1
```

## Pre-loaded Sample Data

On startup, the demo initializes:

### Products (for cache demo)
- MacBook Pro 16" (Electronics)
- iPhone 15 Pro (Electronics)
- AirPods Pro (Electronics)
- Java Programming Book (Books)
- Spring Boot in Action (Books)
- Coffee Mug - Developer (Accessories)

### Redis Data Structures
- `greeting` - String: "Hello, Redis!"
- `visitor:count` - Counter: 100
- `user:1001` - Hash with user profile
- `activities:recent` - List with 4 activities
- `tags:programming` - Set: java, spring, redis, microservices
- `tags:database` - Set: redis, mongodb, postgresql, mysql
- `leaderboard:game` - Sorted set with 5 players
- `session:abc123` - Hash with session data

## Configuration

### application.properties

```properties
server.port=8086
spring.data.redis.host=localhost
spring.data.redis.port=6370
spring.cache.type=redis
spring.cache.redis.time-to-live=600000  # 10 minutes
```

### Cache Configuration

Two caches configured with different TTLs:
- `products` - 10 minute TTL
- `products-list` - 5 minute TTL

## Key Concepts Demonstrated

### 1. RedisTemplate vs StringRedisTemplate

```java
// For complex objects (JSON serialization)
@Bean
public RedisTemplate<String, Object> redisTemplate() {
    template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
}

// For simple strings
@Bean
public StringRedisTemplate stringRedisTemplate() {
    return new StringRedisTemplate(connectionFactory);
}
```

### 2. Spring Cache Annotations

```java
@Cacheable(value = "products", key = "#id")
public Product findById(Long id) { ... }

@CachePut(value = "products", key = "#product.id")
public Product save(Product product) { ... }

@CacheEvict(value = "products", key = "#id")
public void deleteById(Long id) { ... }

@CacheEvict(value = "products", allEntries = true)
public void clearCache() { ... }
```

### 3. Redis Data Structures Usage

| Structure | Use Case |
|-----------|----------|
| String | Simple values, counters, flags |
| Hash | Object storage, user profiles |
| List | Queues, recent items, activity logs |
| Set | Tags, unique values, memberships |
| Sorted Set | Leaderboards, rankings, time-series |

## Technologies

- Spring Boot 3.2.0
- Spring Data Redis
- Lettuce (Redis client)
- Embedded Redis (ozimov)
- Jackson JSON serialization
