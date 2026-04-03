# Hibernate Advanced

> **Master ORM internals, caching strategies, and performance optimization**

## 📚 Table of Contents

- [Hibernate Architecture](#hibernate-architecture)
- [Session and SessionFactory](#session-and-sessionfactory)
- [Entity Mapping](#entity-mapping)
- [First-Level Cache (L1)](#first-level-cache-l1)
- [Second-Level Cache (L2)](#second-level-cache-l2)
- [Query Cache](#query-cache)
- [N+1 Problem](#n1-problem)
- [Batch Processing](#batch-processing)
- [Lazy vs Eager Loading](#lazy-vs-eager-loading)
- [Dirty Checking](#dirty-checking)
- [Best Practices](#best-practices)
- [Interview Questions](#interview-questions)

---

## Hibernate Architecture

### What is Hibernate?

Hibernate is an **Object-Relational Mapping (ORM)** framework that maps Java objects to database tables, eliminating the need for manual SQL in most cases.

```
┌─────────────────────────────────────────────────────────────────┐
│                     Java Application                             │
│              (Entities, Services, Repositories)                  │
└─────────────────────────────┬───────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                      Hibernate ORM                               │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────────┐  │
│  │   Session   │  │   Query     │  │   Transaction           │  │
│  │   Factory   │  │   Language  │  │   Management            │  │
│  └─────────────┘  │   (HQL)     │  └─────────────────────────┘  │
│                   └─────────────┘                                │
│  ┌─────────────────────────────────────────────────────────────┐│
│  │                    Caching Layer                             ││
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐       ││
│  │  │  L1 Cache    │  │  L2 Cache    │  │ Query Cache  │       ││
│  │  │  (Session)   │  │  (Factory)   │  │              │       ││
│  │  └──────────────┘  └──────────────┘  └──────────────┘       ││
│  └─────────────────────────────────────────────────────────────┘│
└─────────────────────────────┬───────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                        JDBC Layer                                │
└─────────────────────────────┬───────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                        Database                                  │
│              (PostgreSQL, MySQL, Oracle, etc.)                   │
└─────────────────────────────────────────────────────────────────┘
```

### Hibernate vs JPA

| Aspect | JPA | Hibernate |
|--------|-----|-----------|
| **Type** | Specification | Implementation |
| **Package** | `javax.persistence` / `jakarta.persistence` | `org.hibernate` |
| **Features** | Core ORM features | JPA + Hibernate-specific extensions |
| **Caching** | Defines interfaces | Provides implementations |
| **Query** | JPQL | HQL (superset of JPQL) |

---

## Session and SessionFactory

### SessionFactory

**SessionFactory** is a thread-safe, heavyweight object created once per application. It holds:
- Configuration settings
- Second-level cache
- Mapping metadata
- Connection pool

```java
// Traditional Hibernate Configuration
Configuration configuration = new Configuration();
configuration.configure("hibernate.cfg.xml");
SessionFactory sessionFactory = configuration.buildSessionFactory();

// Spring Boot - Auto-configured!
@Autowired
private EntityManagerFactory entityManagerFactory;

// Get Hibernate SessionFactory from JPA
SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
```

### Session

**Session** is a lightweight, short-lived object representing a unit of work. It:
- Wraps a JDBC connection
- Maintains first-level cache
- Tracks entity changes (dirty checking)
- Is NOT thread-safe

```java
// Traditional Hibernate
Session session = sessionFactory.openSession();
Transaction tx = session.beginTransaction();

try {
    // Operations
    Product product = session.get(Product.class, 1L);
    product.setPrice(new BigDecimal("99.99"));
    
    tx.commit();
} catch (Exception e) {
    tx.rollback();
    throw e;
} finally {
    session.close();
}

// Spring Boot with JPA EntityManager
@PersistenceContext
private EntityManager entityManager;

// Get Hibernate Session from EntityManager
Session session = entityManager.unwrap(Session.class);
```

### Session States (Entity Lifecycle)

```
                    ┌─────────────────┐
                    │    Transient    │  (new object, not in DB)
                    └────────┬────────┘
                             │ persist() / save()
                             ▼
┌─────────────────┐    ┌─────────────────┐
│    Removed      │◄───│   Persistent    │  (in session & DB)
│  (marked for    │    │   (Managed)     │
│   deletion)     │    └────────┬────────┘
└─────────────────┘             │ evict() / clear() / close()
                                ▼
                    ┌─────────────────┐
                    │    Detached     │  (was persistent, session closed)
                    └─────────────────┘
```

### Entity States Explained

| State | In Session | In Database | Description |
|-------|------------|-------------|-------------|
| **Transient** | No | No | New object, not yet saved |
| **Persistent** | Yes | Yes | Managed by session, changes auto-sync |
| **Detached** | No | Yes | Was managed, session closed |
| **Removed** | Yes | Pending delete | Marked for deletion |

```java
// Transient
Product product = new Product("Laptop", 999.99); // Transient

// Persistent
session.persist(product); // Now Persistent
product.setPrice(1099.99); // Change auto-detected (dirty checking)

// Detached
session.evict(product); // Now Detached
product.setPrice(899.99); // Change NOT tracked!

// Re-attach
session.merge(product); // Back to Persistent

// Removed
session.remove(product); // Marked for deletion
```

---

## Entity Mapping

### Basic Entity

```java
@Entity
@Table(name = "products")
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "product_name", nullable = false, length = 200)
    private String name;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    @Version  // Optimistic locking
    private Integer version;
}
```

### ID Generation Strategies

```java
// AUTO - Hibernate chooses based on database
@GeneratedValue(strategy = GenerationType.AUTO)

// IDENTITY - Auto-increment (MySQL, PostgreSQL SERIAL)
@GeneratedValue(strategy = GenerationType.IDENTITY)

// SEQUENCE - Database sequence (PostgreSQL, Oracle)
@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_seq")
@SequenceGenerator(name = "product_seq", sequenceName = "product_sequence", allocationSize = 50)

// TABLE - Simulated sequence using a table (portable but slow)
@GeneratedValue(strategy = GenerationType.TABLE)

// UUID - For distributed systems
@Id
@GeneratedValue(generator = "UUID")
@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
private UUID id;
```

### Relationship Mappings

```java
// One-to-Many (Parent side)
@Entity
public class Order {
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();
    
    // Helper method for bidirectional sync
    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }
}

// Many-to-One (Child side - owns the relationship)
@Entity
public class OrderItem {
    @ManyToOne(fetch = FetchType.LAZY)  // LAZY is important!
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
}
```

---

## First-Level Cache (L1)

### What is L1 Cache?

The **First-Level Cache** is:
- **Session-scoped**: Each Session has its own L1 cache
- **Enabled by default**: Cannot be disabled
- **Automatic**: No configuration needed
- **Identity Map**: Ensures only one instance per entity ID per session

```
┌─────────────────────────────────────────────┐
│                  Session 1                   │
│  ┌─────────────────────────────────────────┐│
│  │           L1 Cache (Identity Map)       ││
│  │  ┌─────────────────────────────────┐    ││
│  │  │ Product#1 → Product@abc123      │    ││
│  │  │ Product#2 → Product@def456      │    ││
│  │  │ User#5    → User@ghi789         │    ││
│  │  └─────────────────────────────────┘    ││
│  └─────────────────────────────────────────┘│
└─────────────────────────────────────────────┘
```

### L1 Cache in Action

```java
// L1 Cache Demo
Session session = sessionFactory.openSession();

// First fetch - hits database
Product p1 = session.get(Product.class, 1L);  // SQL: SELECT * FROM products WHERE id=1
System.out.println("First fetch: " + p1.getName());

// Second fetch - returns cached instance (NO SQL!)
Product p2 = session.get(Product.class, 1L);  // No SQL executed
System.out.println("Second fetch: " + p2.getName());

// Same object reference!
System.out.println("Same object? " + (p1 == p2));  // true

// Query also populates L1 cache
List<Product> products = session.createQuery("FROM Product", Product.class).list();

// This uses L1 cache (if product with ID 1 was in query results)
Product p3 = session.get(Product.class, 1L);  // No SQL if ID 1 was in list

session.close();
```

### L1 Cache Management

```java
// Clear entire L1 cache
session.clear();

// Remove specific entity from L1 cache
session.evict(product);

// Check if entity is in L1 cache
boolean isCached = session.contains(product);

// Flush pending changes to database (but keep in cache)
session.flush();
```

### L1 Cache Pitfalls

```java
// ❌ Problem: Memory leak with large result sets
Session session = sessionFactory.openSession();
for (int i = 0; i < 100000; i++) {
    Product product = new Product("Product " + i, 9.99);
    session.persist(product);
    // L1 cache keeps growing!
}

// ✅ Solution: Periodic flush and clear
Session session = sessionFactory.openSession();
Transaction tx = session.beginTransaction();

for (int i = 0; i < 100000; i++) {
    Product product = new Product("Product " + i, 9.99);
    session.persist(product);
    
    if (i % 50 == 0) {
        session.flush();  // Write to DB
        session.clear();  // Clear L1 cache
    }
}

tx.commit();
session.close();
```

---

## Second-Level Cache (L2)

### What is L2 Cache?

The **Second-Level Cache** is:
- **SessionFactory-scoped**: Shared across all sessions
- **Optional**: Must be explicitly enabled
- **Configurable**: Choose cache provider and strategies
- **Region-based**: Different entities can have different cache settings

```
┌──────────────────────────────────────────────────────────────────────┐
│                         SessionFactory                                │
│  ┌────────────────────────────────────────────────────────────────┐  │
│  │                    Second-Level Cache                          │  │
│  │  ┌─────────────────┐  ┌─────────────────┐  ┌────────────────┐ │  │
│  │  │ Product Region  │  │  Order Region   │  │  User Region   │ │  │
│  │  │ ID:1 → data     │  │ ID:100 → data   │  │ ID:5 → data    │ │  │
│  │  │ ID:2 → data     │  │ ID:101 → data   │  │ ID:6 → data    │ │  │
│  │  └─────────────────┘  └─────────────────┘  └────────────────┘ │  │
│  └────────────────────────────────────────────────────────────────┘  │
│                                                                       │
│  ┌──────────────┐    ┌──────────────┐    ┌──────────────┐            │
│  │   Session 1  │    │   Session 2  │    │   Session 3  │            │
│  │  ┌────────┐  │    │  ┌────────┐  │    │  ┌────────┐  │            │
│  │  │L1 Cache│  │    │  │L1 Cache│  │    │  │L1 Cache│  │            │
│  │  └────────┘  │    │  └────────┘  │    │  └────────┘  │            │
│  └──────────────┘    └──────────────┘    └──────────────┘            │
└──────────────────────────────────────────────────────────────────────┘
```

### Enabling L2 Cache

#### 1. Add Dependencies

```xml
<!-- Ehcache 3 (JCache/JSR-107) -->
<dependency>
    <groupId>org.hibernate.orm</groupId>
    <artifactId>hibernate-jcache</artifactId>
</dependency>
<dependency>
    <groupId>org.ehcache</groupId>
    <artifactId>ehcache</artifactId>
    <classifier>jakarta</classifier>
</dependency>
```

#### 2. Configure Hibernate

```yaml
# application.yml
spring:
  jpa:
    properties:
      hibernate:
        # Enable L2 cache
        cache:
          use_second_level_cache: true
          region:
            factory_class: org.hibernate.cache.jcache.JCacheRegionFactory
          # Enable query cache (optional)
          use_query_cache: true
        # JCache provider
        javax:
          cache:
            provider: org.ehcache.jsr107.EhcacheCachingProvider
        # Generate statistics (for monitoring)
        generate_statistics: true
```

#### 3. Configure Ehcache

```xml
<!-- ehcache.xml -->
<config xmlns="http://www.ehcache.org/v3">
    
    <!-- Default cache template -->
    <cache-template name="default">
        <expiry>
            <ttl unit="minutes">60</ttl>
        </expiry>
        <heap unit="entries">1000</heap>
    </cache-template>
    
    <!-- Product entity cache -->
    <cache alias="com.example.Product" uses-template="default">
        <heap unit="entries">5000</heap>
        <expiry>
            <ttl unit="hours">2</ttl>
        </expiry>
    </cache>
    
    <!-- Query cache region -->
    <cache alias="default-query-results-region" uses-template="default">
        <heap unit="entries">500</heap>
    </cache>
    
</config>
```

#### 4. Enable Caching on Entities

```java
@Entity
@Table(name = "products")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)  // Enable L2 cache
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private BigDecimal price;
    
    // Collections can be cached too
    @OneToMany(mappedBy = "product")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private List<Review> reviews;
}
```

### Cache Concurrency Strategies

| Strategy | Description | Use Case |
|----------|-------------|----------|
| `READ_ONLY` | Never modified after creation | Reference data, lookup tables |
| `NONSTRICT_READ_WRITE` | Occasional updates, eventual consistency OK | Rarely updated data |
| `READ_WRITE` | Frequent updates, uses soft locks | Most entities |
| `TRANSACTIONAL` | Full JTA transaction support | Distributed transactions |

```java
// Read-only entity (lookup table)
@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class Country {
    @Id
    private String code;
    private String name;
}

// Frequently updated entity
@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Product {
    // ...
}
```

### L2 Cache Lookup Flow

```
Session.get(Product.class, 1L)
           │
           ▼
    ┌──────────────┐
    │  L1 Cache?   │──Yes──▶ Return entity
    └──────┬───────┘
           │ No
           ▼
    ┌──────────────┐
    │  L2 Cache?   │──Yes──▶ Hydrate & return (add to L1)
    └──────┬───────┘
           │ No
           ▼
    ┌──────────────┐
    │   Database   │──────▶ Fetch, add to L1 & L2, return
    └──────────────┘
```

---

## Query Cache

### What is Query Cache?

The **Query Cache** stores:
- Query string + parameters → List of entity IDs
- Useful for repeated identical queries
- Must be used WITH L2 cache

```
Query Cache Entry:
─────────────────
Key:   "SELECT p FROM Product p WHERE p.category = 'Electronics'" + params
Value: [1, 5, 12, 23, 45]  (Just IDs!)

When query cache hit:
1. Get IDs from query cache
2. Look up each entity from L2 cache (or DB)
```

### Enabling Query Cache

```java
// Enable on specific query
List<Product> products = entityManager
    .createQuery("SELECT p FROM Product p WHERE p.category = :cat", Product.class)
    .setParameter("cat", "Electronics")
    .setHint("org.hibernate.cacheable", true)  // Enable query cache
    .getResultList();

// Spring Data JPA
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
    List<Product> findByCategory(String category);
}
```

### Query Cache Invalidation

⚠️ **The query cache is invalidated whenever ANY entity in the related table is modified!**

```java
// This query result is cached
List<Product> cached = repo.findByCategory("Electronics");

// ANY product update invalidates the query cache!
Product unrelated = repo.findById(999L);
unrelated.setName("Changed");
repo.save(unrelated);  // Entire query cache for Product invalidated!

// Next call hits database again
List<Product> notCached = repo.findByCategory("Electronics");
```

### When to Use Query Cache

| Use Case | Query Cache? |
|----------|--------------|
| Rarely changing reference data | ✅ Yes |
| Frequently executed identical queries | ✅ Yes |
| Frequently updated tables | ❌ No |
| Queries with many results | ❌ No (memory) |
| Queries with unique parameters | ❌ No (low hit rate) |

---

## N+1 Problem

### What is N+1 Problem?

The **N+1 problem** occurs when fetching a collection results in:
- **1 query** for the parent entities
- **N queries** for each parent's children

```java
// ❌ N+1 Problem Example
@Entity
public class Author {
    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    private List<Book> books;  // LAZY by default
}

// Code that triggers N+1
List<Author> authors = authorRepo.findAll();  // 1 query for authors

for (Author author : authors) {
    System.out.println(author.getBooks().size());  // N queries! (one per author)
}

// SQL Generated:
// SELECT * FROM authors                    -- 1 query
// SELECT * FROM books WHERE author_id = 1  -- +1
// SELECT * FROM books WHERE author_id = 2  -- +1
// SELECT * FROM books WHERE author_id = 3  -- +1
// ... (N more queries)
```

### Solution 1: JOIN FETCH

```java
// ✅ Solution: Fetch all data in one query
@Query("SELECT a FROM Author a JOIN FETCH a.books")
List<Author> findAllWithBooks();

// SQL: SELECT * FROM authors a JOIN books b ON a.id = b.author_id
// Just 1 query!
```

### Solution 2: @EntityGraph

```java
// Define named entity graph on entity
@Entity
@NamedEntityGraph(
    name = "Author.withBooks",
    attributeNodes = @NamedAttributeNode("books")
)
public class Author { ... }

// Use in repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    
    @EntityGraph(value = "Author.withBooks", type = EntityGraph.EntityGraphType.FETCH)
    List<Author> findAll();
    
    // Or define inline
    @EntityGraph(attributePaths = {"books"})
    List<Author> findAllWithBooks();
}
```

### Solution 3: @BatchSize

```java
// Fetch in batches instead of one-by-one
@Entity
public class Author {
    @OneToMany(mappedBy = "author")
    @BatchSize(size = 25)  // Load 25 authors' books per query
    private List<Book> books;
}

// SQL Generated:
// SELECT * FROM authors                          -- 1 query
// SELECT * FROM books WHERE author_id IN (1,2,3,...,25)  -- batch 1
// SELECT * FROM books WHERE author_id IN (26,27,...,50)  -- batch 2
// Much better than N queries!
```

### Solution 4: @Fetch(FetchMode.SUBSELECT)

```java
@Entity
public class Author {
    @OneToMany(mappedBy = "author")
    @Fetch(FetchMode.SUBSELECT)  // Use subselect for entire parent query
    private List<Book> books;
}

// SQL Generated:
// SELECT * FROM authors WHERE ...
// SELECT * FROM books WHERE author_id IN (SELECT id FROM authors WHERE ...)
// Just 2 queries total!
```

### Comparison of N+1 Solutions

| Solution | Queries | Memory | Best For |
|----------|---------|--------|----------|
| JOIN FETCH | 1 | High (Cartesian) | Small collections |
| EntityGraph | 1 | High | Flexible, per-query control |
| @BatchSize | 1 + N/batch | Medium | Large collections |
| SUBSELECT | 2 | Medium | When reusing parent query |

---

## Batch Processing

### Batch Inserts

```java
@Service
@Transactional
public class ProductBatchService {
    
    @PersistenceContext
    private EntityManager em;
    
    public void batchInsert(List<Product> products) {
        int batchSize = 50;
        
        for (int i = 0; i < products.size(); i++) {
            em.persist(products.get(i));
            
            if (i > 0 && i % batchSize == 0) {
                em.flush();   // Write to database
                em.clear();   // Clear L1 cache
            }
        }
        
        em.flush();  // Final flush
    }
}
```

### Batch Configuration

```yaml
spring:
  jpa:
    properties:
      hibernate:
        jdbc:
          batch_size: 50
          batch_versioned_data: true
        order_inserts: true
        order_updates: true
```

### Batch Updates

```java
// ❌ Slow: Loading all entities
List<Product> products = productRepo.findByCategoryId(categoryId);
for (Product p : products) {
    p.setDiscounted(true);
}
productRepo.saveAll(products);

// ✅ Fast: Bulk update with JPQL
@Modifying
@Query("UPDATE Product p SET p.discounted = true WHERE p.category.id = :categoryId")
int bulkUpdateDiscount(@Param("categoryId") Long categoryId);

// ⚠️ Note: Bulk updates bypass L1 cache and Hibernate events!
```

---

## Lazy vs Eager Loading

### Fetch Types

```java
@Entity
public class Order {
    // EAGER: Load immediately with parent
    @ManyToOne(fetch = FetchType.EAGER)  // Default for @ManyToOne
    private Customer customer;
    
    // LAZY: Load on first access
    @OneToMany(fetch = FetchType.LAZY)   // Default for @OneToMany
    private List<OrderItem> items;
}
```

### Default Fetch Types

| Annotation | Default FetchType |
|------------|-------------------|
| `@ManyToOne` | EAGER |
| `@OneToOne` | EAGER |
| `@OneToMany` | LAZY |
| `@ManyToMany` | LAZY |

### Best Practice: Always Use LAZY

```java
// ✅ Recommended: LAZY everything
@Entity
public class Order {
    @ManyToOne(fetch = FetchType.LAZY)  // Override default EAGER
    private Customer customer;
    
    @OneToMany(fetch = FetchType.LAZY)
    private List<OrderItem> items;
}

// Then fetch what you need using JOIN FETCH or EntityGraph
```

### LazyInitializationException

```java
// ❌ This will fail!
@GetMapping("/orders/{id}")
public OrderDTO getOrder(@PathVariable Long id) {
    Order order = orderRepo.findById(id).orElseThrow();
    // Transaction ends here (session closed)
    
    return new OrderDTO(
        order.getId(),
        order.getItems().size()  // LazyInitializationException!
    );
}

// ✅ Solution 1: Initialize in service layer
@Service
public class OrderService {
    @Transactional(readOnly = true)
    public Order getOrderWithItems(Long id) {
        Order order = orderRepo.findById(id).orElseThrow();
        order.getItems().size();  // Force initialization
        return order;
    }
}

// ✅ Solution 2: Use JOIN FETCH
@Query("SELECT o FROM Order o JOIN FETCH o.items WHERE o.id = :id")
Optional<Order> findByIdWithItems(@Param("id") Long id);

// ✅ Solution 3: Use EntityGraph
@EntityGraph(attributePaths = {"items"})
Optional<Order> findById(Long id);

// ✅ Solution 4: Use DTO projection (best for API responses)
@Query("SELECT new com.example.OrderDTO(o.id, o.status, SIZE(o.items)) FROM Order o WHERE o.id = :id")
Optional<OrderDTO> findOrderDTO(@Param("id") Long id);
```

---

## Dirty Checking

### How Dirty Checking Works

Hibernate automatically detects changes to managed entities:

```java
@Transactional
public void updateProductPrice(Long productId, BigDecimal newPrice) {
    Product product = productRepo.findById(productId).orElseThrow();
    
    product.setPrice(newPrice);  // Just set the value
    
    // NO save() needed! Hibernate detects the change at flush time
}
// Transaction commits → Hibernate compares snapshot → Generates UPDATE
```

### Dirty Checking Internals

```
┌──────────────────────────────────────────────────────────────────┐
│                         Session                                   │
│  ┌────────────────────────────────────────────────────────────┐  │
│  │                    Persistence Context                      │  │
│  │                                                             │  │
│  │  Entity Instance          Original Snapshot                 │  │
│  │  ┌────────────────┐       ┌────────────────┐               │  │
│  │  │ id: 1          │       │ id: 1          │               │  │
│  │  │ name: "Laptop" │       │ name: "Laptop" │               │  │
│  │  │ price: 1099.99 │  !=   │ price: 999.99  │  → DIRTY!     │  │
│  │  └────────────────┘       └────────────────┘               │  │
│  └────────────────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼ flush()
                    UPDATE products SET price = 1099.99 WHERE id = 1
```

### Disabling Dirty Checking

```java
// Read-only transaction (no dirty checking)
@Transactional(readOnly = true)
public Product getProduct(Long id) {
    return productRepo.findById(id).orElseThrow();
}

// Detach entity to prevent updates
entityManager.detach(product);
product.setPrice(newPrice);  // Change NOT persisted

// Read-only entity hint
@QueryHints(@QueryHint(name = "org.hibernate.readOnly", value = "true"))
List<Product> findAllReadOnly();
```

---

## Best Practices

### 1. Always Use LAZY Fetching

```java
@ManyToOne(fetch = FetchType.LAZY)  // Override default EAGER
@OneToOne(fetch = FetchType.LAZY)   // Override default EAGER
```

### 2. Prefer JOIN FETCH for Known Associations

```java
@Query("SELECT o FROM Order o JOIN FETCH o.items JOIN FETCH o.customer WHERE o.id = :id")
Optional<Order> findByIdWithDetails(@Param("id") Long id);
```

### 3. Use DTOs for API Responses

```java
// Project only needed fields
@Query("SELECT new com.example.ProductDTO(p.id, p.name, p.price) FROM Product p")
List<ProductDTO> findAllAsDTO();
```

### 4. Enable Batch Processing

```yaml
spring.jpa.properties.hibernate.jdbc.batch_size: 50
spring.jpa.properties.hibernate.order_inserts: true
spring.jpa.properties.hibernate.order_updates: true
```

### 5. Use Read-Only Transactions for Queries

```java
@Transactional(readOnly = true)
public List<Product> searchProducts(String keyword) {
    return productRepo.findByNameContaining(keyword);
}
```

### 6. Monitor with Statistics

```yaml
spring.jpa.properties.hibernate.generate_statistics: true
logging.level.org.hibernate.stat: DEBUG
```

---

## Interview Questions

### Q1: What is the difference between L1 and L2 cache?

**Answer:**
- **L1 (First-Level)**: Session-scoped, automatic, one entity instance per ID per session
- **L2 (Second-Level)**: SessionFactory-scoped, optional, shared across sessions, requires configuration

### Q2: How do you solve the N+1 problem?

**Answer:**
1. JOIN FETCH in JPQL/HQL
2. @EntityGraph annotation
3. @BatchSize for collection fetching
4. @Fetch(FetchMode.SUBSELECT)

### Q3: What is dirty checking?

**Answer:** Hibernate automatically detects changes to managed entities by comparing current state with original snapshot at flush time, generating UPDATE statements without explicit save() calls.

### Q4: When would you use the Query Cache?

**Answer:** For frequently executed identical queries on rarely-changing data. Avoid for frequently updated tables as any modification invalidates the entire query cache for that entity.

### Q5: Explain entity states in Hibernate.

**Answer:**
- **Transient**: New object, not in database
- **Persistent**: Managed by session, changes auto-synced
- **Detached**: Was managed, session closed
- **Removed**: Marked for deletion

### Q6: What is the default fetch type for @ManyToOne?

**Answer:** EAGER (which is often problematic). Best practice is to explicitly set LAZY.

---

## Demo Projects

### demo-hibernate-caching
L1/L2 cache configuration and behavior demonstration

### demo-hibernate-performance
N+1 problem, batch processing, and optimization techniques

---

## Next Steps

After mastering Hibernate:
1. → [NoSQL with MongoDB](../03-nosql-mongodb/) - Document databases
2. → [Redis Caching](../04-nosql-redis/) - Application-level caching
3. → [Database Patterns](../05-database-patterns/) - Polyglot persistence
