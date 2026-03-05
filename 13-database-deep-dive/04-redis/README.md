# Redis with Spring Boot

## Overview

Redis is an in-memory data structure store used as a database, cache, message broker, and streaming engine. This module covers Redis fundamentals, Spring Data Redis integration, caching, sessions, and pub/sub patterns.

## Table of Contents

1. [Redis Fundamentals](#redis-fundamentals)
2. [Data Structures](#data-structures)
3. [Spring Data Redis](#spring-data-redis)
4. [Caching with Redis](#caching-with-redis)
5. [Session Management](#session-management)
6. [Pub/Sub Messaging](#pubsub-messaging)
7. [Transactions & Pipelining](#transactions--pipelining)
8. [Best Practices](#best-practices)

---

## Redis Fundamentals

### What is Redis?

- **In-Memory**: Data stored in RAM for ultra-fast access
- **Persistent**: Optional disk persistence (RDB, AOF)
- **Versatile**: Multiple data structures (strings, hashes, lists, sets, sorted sets)
- **Distributed**: Supports clustering and replication

### Use Cases

| Use Case | Description |
|----------|-------------|
| **Caching** | Store frequently accessed data |
| **Sessions** | User session storage |
| **Rate Limiting** | API request throttling |
| **Leaderboards** | Sorted sets for rankings |
| **Real-time Analytics** | Counters, HyperLogLog |
| **Message Queues** | Lists, Streams, Pub/Sub |
| **Geospatial** | Location-based queries |

### Performance Comparison

| Operation | Redis | RDBMS |
|-----------|-------|-------|
| Read | ~100μs | ~1-10ms |
| Write | ~100μs | ~5-20ms |
| Throughput | 100K+ ops/sec | 1K-10K ops/sec |

---

## Data Structures

### String

```bash
# Basic operations
SET user:1:name "John Doe"
GET user:1:name

# With expiration
SET session:abc123 "user_data" EX 3600  # 1 hour

# Increment/Decrement
INCR page:views
INCRBY product:1:stock 10
DECR api:rate:user:1

# Append
APPEND log:today "New entry\n"
```

### Hash

```bash
# Store object fields
HSET user:1 name "John" email "john@example.com" age 30
HGET user:1 name
HGETALL user:1
HINCRBY user:1 age 1

# Multiple fields at once
HMSET product:1 name "Laptop" price 999.99 stock 50
HMGET product:1 name price
```

### List

```bash
# Queue (FIFO)
RPUSH queue:orders "order:1" "order:2"
LPOP queue:orders

# Stack (LIFO)
LPUSH stack:tasks "task:1"
LPOP stack:tasks

# Range
LRANGE recent:products 0 9  # Last 10 items
LLEN queue:orders
```

### Set

```bash
# Unique values
SADD tags:product:1 "electronics" "laptop" "premium"
SMEMBERS tags:product:1
SISMEMBER tags:product:1 "laptop"

# Set operations
SINTER tags:product:1 tags:product:2  # Intersection
SUNION tags:product:1 tags:product:2  # Union
SDIFF tags:product:1 tags:product:2   # Difference
```

### Sorted Set

```bash
# Leaderboard
ZADD leaderboard 1000 "player:1" 950 "player:2" 900 "player:3"
ZREVRANK leaderboard "player:1"  # Rank (0-indexed)
ZREVRANGE leaderboard 0 9 WITHSCORES  # Top 10
ZINCRBY leaderboard 50 "player:2"  # Add score
```

---

## Spring Data Redis

### Dependencies

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>

<!-- Connection pool -->
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-pool2</artifactId>
</dependency>
```

### Configuration

```yaml
# application.yml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      password: ${REDIS_PASSWORD:}
      database: 0
      timeout: 2000ms
      lettuce:
        pool:
          max-active: 8
          max-idle: 8
          min-idle: 0
          max-wait: -1ms
```

### RedisTemplate Configuration

```java
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        
        // Key serializer
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        
        // Value serializer (JSON)
        Jackson2JsonRedisSerializer<Object> serializer = 
            new Jackson2JsonRedisSerializer<>(Object.class);
        
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.activateDefaultTyping(
            LaissezFaireSubTypeValidator.instance,
            ObjectMapper.DefaultTyping.NON_FINAL
        );
        serializer.setObjectMapper(mapper);
        
        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);
        
        template.afterPropertiesSet();
        return template;
    }
    
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
        return new StringRedisTemplate(factory);
    }
}
```

### Basic Operations with RedisTemplate

```java
@Service
public class RedisService {
    
    private final RedisTemplate<String, Object> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;
    
    // ==========================================
    // STRING Operations
    // ==========================================
    
    public void setString(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }
    
    public void setStringWithExpiry(String key, String value, Duration ttl) {
        stringRedisTemplate.opsForValue().set(key, value, ttl);
    }
    
    public String getString(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }
    
    public Long increment(String key) {
        return stringRedisTemplate.opsForValue().increment(key);
    }
    
    public Long incrementBy(String key, long delta) {
        return stringRedisTemplate.opsForValue().increment(key, delta);
    }
    
    // ==========================================
    // OBJECT Operations (JSON serialized)
    // ==========================================
    
    public void setObject(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }
    
    public void setObjectWithExpiry(String key, Object value, Duration ttl) {
        redisTemplate.opsForValue().set(key, value, ttl);
    }
    
    public Object getObject(String key) {
        return redisTemplate.opsForValue().get(key);
    }
    
    @SuppressWarnings("unchecked")
    public <T> T getObject(String key, Class<T> type) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value != null && type.isInstance(value)) {
            return (T) value;
        }
        return null;
    }
    
    // ==========================================
    // HASH Operations
    // ==========================================
    
    public void setHash(String key, String field, Object value) {
        redisTemplate.opsForHash().put(key, field, value);
    }
    
    public void setHashAll(String key, Map<String, Object> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }
    
    public Object getHashField(String key, String field) {
        return redisTemplate.opsForHash().get(key, field);
    }
    
    public Map<Object, Object> getHashAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }
    
    public Long incrementHashField(String key, String field, long delta) {
        return redisTemplate.opsForHash().increment(key, field, delta);
    }
    
    // ==========================================
    // LIST Operations
    // ==========================================
    
    public Long pushToList(String key, Object value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }
    
    public Object popFromList(String key) {
        return redisTemplate.opsForList().leftPop(key);
    }
    
    public List<Object> getListRange(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }
    
    public Long getListSize(String key) {
        return redisTemplate.opsForList().size(key);
    }
    
    // ==========================================
    // SET Operations
    // ==========================================
    
    public Long addToSet(String key, Object... values) {
        return redisTemplate.opsForSet().add(key, values);
    }
    
    public Set<Object> getSetMembers(String key) {
        return redisTemplate.opsForSet().members(key);
    }
    
    public Boolean isSetMember(String key, Object value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }
    
    public Long removeFromSet(String key, Object... values) {
        return redisTemplate.opsForSet().remove(key, values);
    }
    
    // ==========================================
    // SORTED SET Operations
    // ==========================================
    
    public Boolean addToSortedSet(String key, Object value, double score) {
        return redisTemplate.opsForZSet().add(key, value, score);
    }
    
    public Double incrementScore(String key, Object value, double delta) {
        return redisTemplate.opsForZSet().incrementScore(key, value, delta);
    }
    
    public Set<Object> getSortedSetRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().range(key, start, end);
    }
    
    public Set<Object> getSortedSetReverseRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().reverseRange(key, start, end);
    }
    
    public Long getSortedSetRank(String key, Object value) {
        return redisTemplate.opsForZSet().reverseRank(key, value);
    }
    
    // ==========================================
    // KEY Operations
    // ==========================================
    
    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }
    
    public Boolean exists(String key) {
        return redisTemplate.hasKey(key);
    }
    
    public Boolean expire(String key, Duration ttl) {
        return redisTemplate.expire(key, ttl);
    }
    
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key);
    }
    
    public Set<String> getKeys(String pattern) {
        return stringRedisTemplate.keys(pattern);
    }
}
```

---

## Caching with Redis

### Enable Caching

```java
@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory factory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofHours(1))
            .serializeKeysWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                    new StringRedisSerializer()))
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                    new GenericJackson2JsonRedisSerializer()))
            .disableCachingNullValues();
        
        // Custom configurations per cache
        Map<String, RedisCacheConfiguration> cacheConfigs = new HashMap<>();
        cacheConfigs.put("products", config.entryTtl(Duration.ofMinutes(30)));
        cacheConfigs.put("users", config.entryTtl(Duration.ofHours(2)));
        cacheConfigs.put("sessions", config.entryTtl(Duration.ofMinutes(30)));
        
        return RedisCacheManager.builder(factory)
            .cacheDefaults(config)
            .withInitialCacheConfigurations(cacheConfigs)
            .build();
    }
}
```

### Using Cache Annotations

```java
@Service
public class ProductService {
    
    private final ProductRepository productRepository;
    
    // ==========================================
    // @Cacheable - Read-through cache
    // ==========================================
    
    @Cacheable(value = "products", key = "#id")
    public Product findById(Long id) {
        // Only called if not in cache
        return productRepository.findById(id).orElse(null);
    }
    
    @Cacheable(value = "products", key = "#sku")
    public Product findBySku(String sku) {
        return productRepository.findBySku(sku).orElse(null);
    }
    
    // Conditional caching
    @Cacheable(value = "products", key = "#category", 
               condition = "#category != null",
               unless = "#result.isEmpty()")
    public List<Product> findByCategory(String category) {
        return productRepository.findByCategory(category);
    }
    
    // ==========================================
    // @CachePut - Always update cache
    // ==========================================
    
    @CachePut(value = "products", key = "#result.id")
    public Product save(Product product) {
        return productRepository.save(product);
    }
    
    // ==========================================
    // @CacheEvict - Remove from cache
    // ==========================================
    
    @CacheEvict(value = "products", key = "#id")
    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }
    
    // Evict all entries in cache
    @CacheEvict(value = "products", allEntries = true)
    public void clearProductCache() {
        // Cache cleared
    }
    
    // ==========================================
    // @Caching - Multiple cache operations
    // ==========================================
    
    @Caching(
        put = @CachePut(value = "products", key = "#result.id"),
        evict = @CacheEvict(value = "productsByCategory", key = "#product.category")
    )
    public Product update(Product product) {
        return productRepository.save(product);
    }
}
```

### Cache-Aside Pattern (Manual)

```java
@Service
public class ManualCacheService {
    
    private final RedisTemplate<String, Object> redisTemplate;
    private final ProductRepository productRepository;
    
    private static final String PRODUCT_KEY_PREFIX = "product:";
    private static final Duration DEFAULT_TTL = Duration.ofHours(1);
    
    public Product getProduct(Long id) {
        String key = PRODUCT_KEY_PREFIX + id;
        
        // Check cache first
        Product cached = (Product) redisTemplate.opsForValue().get(key);
        if (cached != null) {
            return cached;
        }
        
        // Cache miss - load from DB
        Product product = productRepository.findById(id).orElse(null);
        
        // Store in cache
        if (product != null) {
            redisTemplate.opsForValue().set(key, product, DEFAULT_TTL);
        }
        
        return product;
    }
    
    public void invalidateProduct(Long id) {
        String key = PRODUCT_KEY_PREFIX + id;
        redisTemplate.delete(key);
    }
}
```

---

## Session Management

### Dependencies

```xml
<dependency>
    <groupId>org.springframework.session</groupId>
    <artifactId>spring-session-data-redis</artifactId>
</dependency>
```

### Configuration

```java
@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 1800) // 30 minutes
public class SessionConfig {
    
    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setCookieName("SESSION");
        serializer.setCookiePath("/");
        serializer.setDomainNamePattern("^.+?\\.(\\w+\\.[a-z]+)$");
        serializer.setUseSecureCookie(true);
        serializer.setUseHttpOnlyCookie(true);
        serializer.setSameSite("Strict");
        return serializer;
    }
    
    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        return new GenericJackson2JsonRedisSerializer();
    }
}
```

### Using Sessions

```java
@RestController
@RequestMapping("/api")
public class SessionController {
    
    @GetMapping("/session/set")
    public ResponseEntity<String> setSessionAttribute(HttpSession session,
                                                       @RequestParam String key,
                                                       @RequestParam String value) {
        session.setAttribute(key, value);
        return ResponseEntity.ok("Session attribute set");
    }
    
    @GetMapping("/session/get")
    public ResponseEntity<Object> getSessionAttribute(HttpSession session,
                                                       @RequestParam String key) {
        Object value = session.getAttribute(key);
        if (value == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(value);
    }
    
    @GetMapping("/session/info")
    public ResponseEntity<Map<String, Object>> getSessionInfo(HttpSession session) {
        Map<String, Object> info = new HashMap<>();
        info.put("sessionId", session.getId());
        info.put("creationTime", new Date(session.getCreationTime()));
        info.put("lastAccessedTime", new Date(session.getLastAccessedTime()));
        info.put("maxInactiveInterval", session.getMaxInactiveInterval());
        return ResponseEntity.ok(info);
    }
    
    @PostMapping("/session/invalidate")
    public ResponseEntity<String> invalidateSession(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("Session invalidated");
    }
}
```

---

## Pub/Sub Messaging

### Message Listener Configuration

```java
@Configuration
public class RedisPubSubConfig {
    
    @Bean
    public RedisMessageListenerContainer messageListenerContainer(
            RedisConnectionFactory connectionFactory,
            MessageListenerAdapter orderListener,
            MessageListenerAdapter notificationListener) {
        
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        
        // Subscribe to channels
        container.addMessageListener(orderListener, new ChannelTopic("orders"));
        container.addMessageListener(notificationListener, new PatternTopic("notifications.*"));
        
        return container;
    }
    
    @Bean
    public MessageListenerAdapter orderListener(OrderMessageHandler handler) {
        return new MessageListenerAdapter(handler, "handleMessage");
    }
    
    @Bean
    public MessageListenerAdapter notificationListener(NotificationHandler handler) {
        return new MessageListenerAdapter(handler, "handleMessage");
    }
}
```

### Message Handlers

```java
@Component
public class OrderMessageHandler {
    
    private static final Logger log = LoggerFactory.getLogger(OrderMessageHandler.class);
    
    public void handleMessage(String message) {
        log.info("Received order message: {}", message);
        // Process order
    }
}

@Component
public class NotificationHandler {
    
    private static final Logger log = LoggerFactory.getLogger(NotificationHandler.class);
    
    public void handleMessage(String message, String channel) {
        log.info("Received notification on {}: {}", channel, message);
        // Process notification
    }
}
```

### Publishing Messages

```java
@Service
public class MessagePublisher {
    
    private final StringRedisTemplate redisTemplate;
    
    public void publishOrder(String orderId, String orderData) {
        redisTemplate.convertAndSend("orders", orderData);
    }
    
    public void publishNotification(String type, String message) {
        redisTemplate.convertAndSend("notifications." + type, message);
    }
}
```

---

## Transactions & Pipelining

### Transactions (MULTI/EXEC)

```java
@Service
public class TransactionalRedisService {
    
    private final StringRedisTemplate redisTemplate;
    
    public void transferPoints(String fromUser, String toUser, long points) {
        redisTemplate.execute(new SessionCallback<List<Object>>() {
            @Override
            public List<Object> execute(RedisOperations operations) {
                operations.watch(fromUser);  // Optimistic locking
                
                String currentBalance = (String) operations.opsForValue().get(fromUser);
                if (Long.parseLong(currentBalance) < points) {
                    throw new InsufficientBalanceException();
                }
                
                operations.multi();  // Start transaction
                operations.opsForValue().increment(fromUser, -points);
                operations.opsForValue().increment(toUser, points);
                return operations.exec();  // Commit
            }
        });
    }
}
```

### Pipelining (Batch Operations)

```java
@Service
public class PipelinedRedisService {
    
    private final StringRedisTemplate redisTemplate;
    
    public void batchSet(Map<String, String> keyValues) {
        redisTemplate.executePipelined(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) {
                StringRedisConnection stringConn = (StringRedisConnection) connection;
                
                keyValues.forEach((key, value) -> {
                    stringConn.set(key, value);
                });
                
                return null;  // Pipeline doesn't return results
            }
        });
    }
    
    public List<Object> batchGet(List<String> keys) {
        return redisTemplate.executePipelined(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) {
                StringRedisConnection stringConn = (StringRedisConnection) connection;
                
                keys.forEach(stringConn::get);
                
                return null;
            }
        });
    }
}
```

---

## Best Practices

### Key Naming Convention

```
# Format: namespace:entity:id:field
user:1001:profile
user:1001:sessions
product:electronics:laptop:stock
cache:products:category:electronics
session:abc123
rate_limit:api:user:1001
```

### TTL Strategy

| Data Type | TTL |
|-----------|-----|
| Cache | 5 min - 1 hour |
| Session | 30 min |
| Rate limit | 1 min - 1 hour |
| Temporary data | As needed |
| Reference data | 24 hours |

### Memory Management

```java
// SET with expiry
redisTemplate.opsForValue().set(key, value, Duration.ofHours(1));

// Check and set expiry
if (redisTemplate.getExpire(key) == -1) {
    redisTemplate.expire(key, Duration.ofHours(1));
}

// Use appropriate serialization
// StringRedisSerializer for simple keys
// Jackson2JsonRedisSerializer for objects
```

### Error Handling

```java
@Service
public class ResilientRedisService {
    
    private final RedisTemplate<String, Object> redisTemplate;
    private final ProductRepository productRepository;
    
    public Product getProduct(Long id) {
        try {
            Object cached = redisTemplate.opsForValue().get("product:" + id);
            if (cached != null) {
                return (Product) cached;
            }
        } catch (RedisConnectionFailureException e) {
            log.warn("Redis unavailable, falling back to database");
        }
        
        // Fallback to database
        return productRepository.findById(id).orElse(null);
    }
}
```

---

## Demo Projects

### Available Demos

| Demo | Description | Port |
|------|-------------|------|
| [demo-redis-basics](./demo-redis-basics/) | RedisTemplate, caching | 8086 |

### Running Demos

```bash
# Start Redis (Docker)
docker run -d -p 6379:6379 --name redis redis:latest

# Run demo
cd demo-redis-basics
mvn spring-boot:run
```

---

## Related Topics

- [JDBC Fundamentals](../01-jdbc-fundamentals/)
- [Hibernate Advanced](../02-hibernate-advanced/)
- [MongoDB](../03-mongodb/)
