# MongoDB with Spring Boot

## Overview

MongoDB is a document-oriented NoSQL database that stores data in flexible, JSON-like documents. This module covers MongoDB fundamentals, Spring Data MongoDB integration, and advanced features like aggregation pipelines.

## Table of Contents

1. [NoSQL & Document Databases](#nosql--document-databases)
2. [CAP Theorem](#cap-theorem)
3. [MongoDB Architecture](#mongodb-architecture)
4. [Spring Data MongoDB](#spring-data-mongodb)
5. [MongoTemplate](#mongotemplate)
6. [Aggregation Pipeline](#aggregation-pipeline)
7. [Indexing & Performance](#indexing--performance)
8. [Transactions](#transactions)

---

## NoSQL & Document Databases

### SQL vs NoSQL

| Aspect | SQL (Relational) | NoSQL (Document) |
|--------|------------------|------------------|
| Data Model | Tables with rows | Documents (JSON/BSON) |
| Schema | Fixed schema | Flexible schema |
| Relationships | JOINs | Embedded/Referenced |
| Scaling | Vertical | Horizontal (Sharding) |
| ACID | Strong | Eventually consistent* |
| Use Case | Complex queries, transactions | High volume, flexible data |

*MongoDB supports ACID transactions since v4.0

### Document Model

```json
// SQL: Multiple tables with foreign keys
// users table, addresses table, orders table

// MongoDB: Single document with embedded data
{
  "_id": ObjectId("507f1f77bcf86cd799439011"),
  "name": "John Doe",
  "email": "john@example.com",
  "addresses": [
    {
      "type": "home",
      "street": "123 Main St",
      "city": "New York"
    },
    {
      "type": "work",
      "street": "456 Office Ave",
      "city": "New York"
    }
  ],
  "orders": [
    {
      "orderId": "ORD-001",
      "total": 99.99,
      "items": ["product1", "product2"]
    }
  ],
  "createdAt": ISODate("2024-01-15T10:30:00Z")
}
```

### When to Use MongoDB

✅ **Good For:**
- Rapidly changing schemas
- Large volumes of unstructured data
- Real-time analytics
- Content management
- IoT data
- Caching and sessions

❌ **Not Ideal For:**
- Complex multi-table joins
- Strong consistency requirements
- Financial transactions (unless using transactions)
- Small datasets with fixed schema

---

## CAP Theorem

The CAP theorem states that a distributed system can only provide two of these three guarantees simultaneously:

```
        Consistency
           /\
          /  \
         /    \
        /      \
       /   CA   \
      /          \
     /____________\
Availability ---- Partition Tolerance
     AP              CP
```

### The Three Guarantees

| Property | Description | MongoDB Behavior |
|----------|-------------|------------------|
| **Consistency** | All nodes see the same data at the same time | Configurable (readConcern) |
| **Availability** | Every request receives a response | High (replica sets) |
| **Partition Tolerance** | System works despite network failures | Yes (distributed) |

### MongoDB's Position

MongoDB is primarily a **CP system** with configurable consistency:

```java
// Strong consistency (CP)
mongoTemplate.setReadPreference(ReadPreference.primary());
mongoTemplate.setReadConcern(ReadConcern.MAJORITY);
mongoTemplate.setWriteConcern(WriteConcern.MAJORITY);

// Eventual consistency (AP-like)
mongoTemplate.setReadPreference(ReadPreference.secondaryPreferred());
mongoTemplate.setReadConcern(ReadConcern.LOCAL);
```

### Read/Write Concerns

```java
// Write Concern: How many nodes must acknowledge write
WriteConcern.W1;         // Primary only
WriteConcern.MAJORITY;   // Majority of replica set
WriteConcern.JOURNALED;  // Written to journal

// Read Concern: What data to return
ReadConcern.LOCAL;       // Latest data on node
ReadConcern.MAJORITY;    // Committed to majority
ReadConcern.LINEARIZABLE; // Most consistent
```

---

## MongoDB Architecture

### Components

```
┌─────────────────────────────────────────────────────────┐
│                    MongoDB Cluster                       │
├─────────────────────────────────────────────────────────┤
│                                                          │
│  ┌──────────────────┐  ┌──────────────────┐             │
│  │  Replica Set 1   │  │  Replica Set 2   │  ...        │
│  │  ┌────────────┐  │  │  ┌────────────┐  │             │
│  │  │  Primary   │  │  │  │  Primary   │  │             │
│  │  └────────────┘  │  │  └────────────┘  │             │
│  │  ┌────────────┐  │  │  ┌────────────┐  │             │
│  │  │ Secondary  │  │  │  │ Secondary  │  │             │
│  │  └────────────┘  │  │  └────────────┘  │             │
│  │  ┌────────────┐  │  │  ┌────────────┐  │             │
│  │  │ Secondary  │  │  │  │ Secondary  │  │             │
│  │  └────────────┘  │  │  └────────────┘  │             │
│  └──────────────────┘  └──────────────────┘             │
│                                                          │
│  ┌─────────────────────────────────────────────────┐    │
│  │              Config Servers                      │    │
│  │  (Metadata about shards and chunks)             │    │
│  └─────────────────────────────────────────────────┘    │
│                                                          │
│  ┌─────────────────────────────────────────────────┐    │
│  │              mongos (Query Routers)             │    │
│  └─────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────┘
```

### Key Concepts

| Concept | Description |
|---------|-------------|
| **Database** | Container for collections |
| **Collection** | Group of documents (like a table) |
| **Document** | JSON-like record (like a row) |
| **Field** | Key-value pair in a document |
| **_id** | Unique identifier (ObjectId by default) |
| **Replica Set** | Group of mongod instances for redundancy |
| **Shard** | Horizontal partition of data |

---

## Spring Data MongoDB

### Dependencies

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>
```

### Configuration

```yaml
# application.yml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/mydb
      # Or individual properties:
      # host: localhost
      # port: 27017
      # database: mydb
      # username: user
      # password: pass
      # authentication-database: admin
```

### Document Entity

```java
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.CompoundIndex;

@Document(collection = "products")
@CompoundIndex(name = "category_price", def = "{'category': 1, 'price': -1}")
public class Product {
    
    @Id
    private String id;  // Maps to _id
    
    @Field("product_name")  // Custom field name
    private String name;
    
    @Indexed(unique = true)
    private String sku;
    
    private BigDecimal price;
    
    private String category;
    
    @Field("stock_qty")
    private Integer stockQuantity;
    
    private List<String> tags;
    
    private Map<String, Object> attributes;
    
    @DBRef  // Reference to another collection
    private Category categoryRef;
    
    private Address shippingAddress;  // Embedded document
    
    private LocalDateTime createdAt;
    
    // Constructors, getters, setters
}
```

### Embedded Documents

```java
// Embedded document (stored within parent)
public class Address {
    private String street;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    // No @Document - it's embedded
}

// Parent document
@Document(collection = "customers")
public class Customer {
    @Id
    private String id;
    private String name;
    
    // Embedded single document
    private Address address;
    
    // Embedded list of documents
    private List<Address> addresses;
}
```

### Repository Interface

```java
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface ProductRepository extends MongoRepository<Product, String> {
    
    // Derived queries
    List<Product> findByCategory(String category);
    
    List<Product> findByPriceBetween(BigDecimal min, BigDecimal max);
    
    List<Product> findByTagsContaining(String tag);
    
    List<Product> findByNameContainingIgnoreCase(String name);
    
    // Custom JSON query
    @Query("{ 'price': { $gte: ?0, $lte: ?1 } }")
    List<Product> findByPriceRange(BigDecimal min, BigDecimal max);
    
    @Query("{ 'category': ?0, 'stockQuantity': { $gt: 0 } }")
    List<Product> findAvailableByCategory(String category);
    
    // Projection - return only specific fields
    @Query(value = "{ 'category': ?0 }", fields = "{ 'name': 1, 'price': 1 }")
    List<Product> findNameAndPriceByCategory(String category);
    
    // Sorting
    List<Product> findByCategoryOrderByPriceDesc(String category);
    
    // Pagination
    Page<Product> findByCategory(String category, Pageable pageable);
    
    // Exists
    boolean existsBySku(String sku);
    
    // Count
    long countByCategory(String category);
    
    // Delete
    void deleteByCategory(String category);
}
```

---

## MongoTemplate

### Basic Operations

```java
@Service
public class ProductService {
    
    private final MongoTemplate mongoTemplate;
    
    public ProductService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }
    
    // ==========================================
    // INSERT Operations
    // ==========================================
    
    public Product insert(Product product) {
        return mongoTemplate.insert(product);
    }
    
    public List<Product> insertAll(List<Product> products) {
        return (List<Product>) mongoTemplate.insertAll(products);
    }
    
    // ==========================================
    // SAVE Operations (Insert or Update)
    // ==========================================
    
    public Product save(Product product) {
        return mongoTemplate.save(product);
    }
    
    // ==========================================
    // FIND Operations
    // ==========================================
    
    public Product findById(String id) {
        return mongoTemplate.findById(id, Product.class);
    }
    
    public List<Product> findAll() {
        return mongoTemplate.findAll(Product.class);
    }
    
    public Product findOne(Query query) {
        return mongoTemplate.findOne(query, Product.class);
    }
    
    public List<Product> find(Query query) {
        return mongoTemplate.find(query, Product.class);
    }
    
    // ==========================================
    // UPDATE Operations
    // ==========================================
    
    public UpdateResult updateFirst(Query query, Update update) {
        return mongoTemplate.updateFirst(query, update, Product.class);
    }
    
    public UpdateResult updateMulti(Query query, Update update) {
        return mongoTemplate.updateMulti(query, update, Product.class);
    }
    
    public Product findAndModify(Query query, Update update) {
        return mongoTemplate.findAndModify(query, update, Product.class);
    }
    
    // ==========================================
    // DELETE Operations
    // ==========================================
    
    public DeleteResult remove(Query query) {
        return mongoTemplate.remove(query, Product.class);
    }
    
    public Product findAndRemove(Query query) {
        return mongoTemplate.findAndRemove(query, Product.class);
    }
}
```

### Query Building

```java
@Service
public class ProductQueryService {
    
    private final MongoTemplate mongoTemplate;
    
    // ==========================================
    // CRITERIA Queries
    // ==========================================
    
    public List<Product> findByCategory(String category) {
        Query query = new Query(Criteria.where("category").is(category));
        return mongoTemplate.find(query, Product.class);
    }
    
    public List<Product> findByPriceRange(BigDecimal min, BigDecimal max) {
        Query query = new Query(
            Criteria.where("price").gte(min).lte(max)
        );
        return mongoTemplate.find(query, Product.class);
    }
    
    public List<Product> findWithMultipleCriteria(String category, BigDecimal minPrice) {
        Query query = new Query(
            new Criteria().andOperator(
                Criteria.where("category").is(category),
                Criteria.where("price").gte(minPrice),
                Criteria.where("stockQuantity").gt(0)
            )
        );
        return mongoTemplate.find(query, Product.class);
    }
    
    public List<Product> findByCategoriesOrMinPrice(List<String> categories, BigDecimal minPrice) {
        Query query = new Query(
            new Criteria().orOperator(
                Criteria.where("category").in(categories),
                Criteria.where("price").gte(minPrice)
            )
        );
        return mongoTemplate.find(query, Product.class);
    }
    
    // ==========================================
    // REGEX Queries
    // ==========================================
    
    public List<Product> searchByName(String searchTerm) {
        Query query = new Query(
            Criteria.where("name").regex(searchTerm, "i")  // case-insensitive
        );
        return mongoTemplate.find(query, Product.class);
    }
    
    // ==========================================
    // ARRAY Queries
    // ==========================================
    
    public List<Product> findByTags(List<String> tags) {
        Query query = new Query(
            Criteria.where("tags").all(tags)  // must contain all tags
        );
        return mongoTemplate.find(query, Product.class);
    }
    
    public List<Product> findByAnyTag(List<String> tags) {
        Query query = new Query(
            Criteria.where("tags").in(tags)  // contain any of the tags
        );
        return mongoTemplate.find(query, Product.class);
    }
    
    // ==========================================
    // PAGINATION & SORTING
    // ==========================================
    
    public List<Product> findWithPaginationAndSort(int page, int size, String sortField) {
        Query query = new Query()
            .with(Sort.by(Sort.Direction.DESC, sortField))
            .skip((long) page * size)
            .limit(size);
        return mongoTemplate.find(query, Product.class);
    }
    
    // ==========================================
    // PROJECTION (Select specific fields)
    // ==========================================
    
    public List<Product> findNamesAndPrices() {
        Query query = new Query();
        query.fields().include("name").include("price").exclude("_id");
        return mongoTemplate.find(query, Product.class);
    }
}
```

### Update Operations

```java
@Service
public class ProductUpdateService {
    
    private final MongoTemplate mongoTemplate;
    
    // ==========================================
    // UPDATE Operations
    // ==========================================
    
    public UpdateResult updatePrice(String productId, BigDecimal newPrice) {
        Query query = new Query(Criteria.where("id").is(productId));
        Update update = new Update().set("price", newPrice);
        return mongoTemplate.updateFirst(query, update, Product.class);
    }
    
    public UpdateResult incrementStock(String productId, int quantity) {
        Query query = new Query(Criteria.where("id").is(productId));
        Update update = new Update().inc("stockQuantity", quantity);
        return mongoTemplate.updateFirst(query, update, Product.class);
    }
    
    public UpdateResult decrementStock(String productId, int quantity) {
        Query query = new Query(Criteria.where("id").is(productId));
        Update update = new Update().inc("stockQuantity", -quantity);
        return mongoTemplate.updateFirst(query, update, Product.class);
    }
    
    public UpdateResult addTag(String productId, String tag) {
        Query query = new Query(Criteria.where("id").is(productId));
        Update update = new Update().push("tags", tag);
        return mongoTemplate.updateFirst(query, update, Product.class);
    }
    
    public UpdateResult addTags(String productId, List<String> tags) {
        Query query = new Query(Criteria.where("id").is(productId));
        Update update = new Update().pushAll("tags", tags.toArray());
        return mongoTemplate.updateFirst(query, update, Product.class);
    }
    
    public UpdateResult removeTag(String productId, String tag) {
        Query query = new Query(Criteria.where("id").is(productId));
        Update update = new Update().pull("tags", tag);
        return mongoTemplate.updateFirst(query, update, Product.class);
    }
    
    public UpdateResult updateAttribute(String productId, String key, Object value) {
        Query query = new Query(Criteria.where("id").is(productId));
        Update update = new Update().set("attributes." + key, value);
        return mongoTemplate.updateFirst(query, update, Product.class);
    }
    
    public UpdateResult setUpdatedTimestamp(String productId) {
        Query query = new Query(Criteria.where("id").is(productId));
        Update update = new Update().currentDate("updatedAt");
        return mongoTemplate.updateFirst(query, update, Product.class);
    }
    
    // ==========================================
    // UPSERT (Update or Insert)
    // ==========================================
    
    public UpdateResult upsert(String sku, Product product) {
        Query query = new Query(Criteria.where("sku").is(sku));
        Update update = new Update()
            .set("name", product.getName())
            .set("price", product.getPrice())
            .set("category", product.getCategory())
            .setOnInsert("createdAt", LocalDateTime.now());
        return mongoTemplate.upsert(query, update, Product.class);
    }
    
    // ==========================================
    // BULK Updates
    // ==========================================
    
    public UpdateResult applyDiscountToCategory(String category, double discountPercent) {
        Query query = new Query(Criteria.where("category").is(category));
        Update update = new Update().mul("price", 1 - (discountPercent / 100));
        return mongoTemplate.updateMulti(query, update, Product.class);
    }
}
```

---

## Aggregation Pipeline

### Basic Aggregation

```java
@Service
public class ProductAggregationService {
    
    private final MongoTemplate mongoTemplate;
    
    // ==========================================
    // GROUP BY with COUNT
    // ==========================================
    
    public List<CategoryCount> countByCategory() {
        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.group("category").count().as("count"),
            Aggregation.project("count").and("_id").as("category")
        );
        
        AggregationResults<CategoryCount> results = 
            mongoTemplate.aggregate(aggregation, "products", CategoryCount.class);
        
        return results.getMappedResults();
    }
    
    // ==========================================
    // GROUP BY with SUM
    // ==========================================
    
    public List<CategoryRevenue> revenueByCategory() {
        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.group("category")
                .sum("price").as("totalRevenue")
                .avg("price").as("averagePrice")
                .count().as("productCount"),
            Aggregation.sort(Sort.Direction.DESC, "totalRevenue")
        );
        
        AggregationResults<CategoryRevenue> results = 
            mongoTemplate.aggregate(aggregation, "products", CategoryRevenue.class);
        
        return results.getMappedResults();
    }
    
    // ==========================================
    // MATCH + GROUP (Filter then aggregate)
    // ==========================================
    
    public List<CategoryStats> statsForAvailableProducts() {
        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("stockQuantity").gt(0)),
            Aggregation.group("category")
                .count().as("count")
                .sum("stockQuantity").as("totalStock")
                .avg("price").as("avgPrice")
                .min("price").as("minPrice")
                .max("price").as("maxPrice")
        );
        
        return mongoTemplate.aggregate(aggregation, "products", CategoryStats.class)
                           .getMappedResults();
    }
    
    // ==========================================
    // UNWIND (Flatten arrays)
    // ==========================================
    
    public List<TagCount> countByTag() {
        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.unwind("tags"),
            Aggregation.group("tags").count().as("count"),
            Aggregation.project("count").and("_id").as("tag"),
            Aggregation.sort(Sort.Direction.DESC, "count")
        );
        
        return mongoTemplate.aggregate(aggregation, "products", TagCount.class)
                           .getMappedResults();
    }
    
    // ==========================================
    // LOOKUP (Join collections)
    // ==========================================
    
    public List<ProductWithCategory> getProductsWithCategories() {
        LookupOperation lookup = LookupOperation.newLookup()
            .from("categories")
            .localField("categoryId")
            .foreignField("_id")
            .as("categoryDetails");
        
        Aggregation aggregation = Aggregation.newAggregation(
            lookup,
            Aggregation.unwind("categoryDetails", true)  // preserveNullAndEmptyArrays
        );
        
        return mongoTemplate.aggregate(aggregation, "products", ProductWithCategory.class)
                           .getMappedResults();
    }
    
    // ==========================================
    // BUCKET (Group into ranges)
    // ==========================================
    
    public List<PriceBucket> bucketByPrice() {
        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.bucket("price")
                .withBoundaries(0, 25, 50, 100, 200, 500)
                .withDefaultBucket("500+")
                .andOutput("name").push().as("products")
                .andOutput("price").count().as("count")
        );
        
        return mongoTemplate.aggregate(aggregation, "products", PriceBucket.class)
                           .getMappedResults();
    }
    
    // ==========================================
    // PROJECT (Transform/Compute fields)
    // ==========================================
    
    public List<ProductSummary> getProductSummaries() {
        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.project()
                .and("name").as("productName")
                .and("price").as("originalPrice")
                .andExpression("price * 0.9").as("discountedPrice")
                .andExpression("stockQuantity * price").as("inventoryValue")
        );
        
        return mongoTemplate.aggregate(aggregation, "products", ProductSummary.class)
                           .getMappedResults();
    }
}
```

### Result Classes

```java
public record CategoryCount(String category, long count) {}

public record CategoryRevenue(String category, BigDecimal totalRevenue, 
                               BigDecimal averagePrice, long productCount) {}

public record CategoryStats(String category, long count, long totalStock,
                            BigDecimal avgPrice, BigDecimal minPrice, BigDecimal maxPrice) {}

public record TagCount(String tag, long count) {}

public record PriceBucket(String id, List<String> products, long count) {}

public record ProductSummary(String productName, BigDecimal originalPrice,
                             BigDecimal discountedPrice, BigDecimal inventoryValue) {}
```

---

## Indexing & Performance

### Index Types

```java
@Document(collection = "products")
@CompoundIndexes({
    @CompoundIndex(name = "cat_price", def = "{'category': 1, 'price': -1}"),
    @CompoundIndex(name = "cat_stock", def = "{'category': 1, 'stockQuantity': 1}")
})
public class Product {
    
    @Id
    private String id;
    
    @Indexed  // Single field index
    private String name;
    
    @Indexed(unique = true)  // Unique index
    private String sku;
    
    @TextIndexed  // Full-text search index
    private String description;
    
    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonPoint location;
}
```

### Programmatic Index Creation

```java
@Configuration
public class MongoIndexConfig {
    
    @Bean
    public CommandLineRunner createIndexes(MongoTemplate mongoTemplate) {
        return args -> {
            // Single field index
            mongoTemplate.indexOps(Product.class)
                .ensureIndex(new Index().on("name", Sort.Direction.ASC));
            
            // Compound index
            mongoTemplate.indexOps(Product.class)
                .ensureIndex(new Index()
                    .on("category", Sort.Direction.ASC)
                    .on("price", Sort.Direction.DESC));
            
            // TTL index (auto-expire documents)
            mongoTemplate.indexOps(Session.class)
                .ensureIndex(new Index()
                    .on("createdAt", Sort.Direction.ASC)
                    .expire(30, TimeUnit.MINUTES));
            
            // Text index
            mongoTemplate.indexOps(Product.class)
                .ensureIndex(new TextIndexDefinitionBuilder()
                    .onField("name", 10F)
                    .onField("description", 5F)
                    .build());
        };
    }
}
```

### Performance Tips

```java
// 1. Use projections to limit returned fields
Query query = new Query(Criteria.where("category").is("Electronics"));
query.fields().include("name", "price");

// 2. Use covered queries (index only)
// If query and projection use only indexed fields, MongoDB returns from index

// 3. Use explain() for query analysis
Document explanation = mongoTemplate.executeCommand(
    "{ explain: { find: 'products', filter: { category: 'Electronics' } } }"
);

// 4. Limit result size
query.limit(100);

// 5. Use cursor for large results
try (CloseableIterator<Product> iterator = 
        mongoTemplate.stream(query, Product.class)) {
    while (iterator.hasNext()) {
        process(iterator.next());
    }
}
```

---

## Transactions

### Multi-Document Transactions (MongoDB 4.0+)

```java
@Service
public class OrderService {
    
    private final MongoTemplate mongoTemplate;
    private final MongoTransactionManager transactionManager;
    
    // ==========================================
    // DECLARATIVE Transactions (@Transactional)
    // ==========================================
    
    @Transactional
    public Order createOrder(Order order) {
        // Validate stock
        for (OrderItem item : order.getItems()) {
            Product product = mongoTemplate.findById(item.getProductId(), Product.class);
            if (product.getStockQuantity() < item.getQuantity()) {
                throw new InsufficientStockException(product.getName());
            }
        }
        
        // Decrement stock
        for (OrderItem item : order.getItems()) {
            Query query = new Query(Criteria.where("id").is(item.getProductId()));
            Update update = new Update().inc("stockQuantity", -item.getQuantity());
            mongoTemplate.updateFirst(query, update, Product.class);
        }
        
        // Save order
        order.setCreatedAt(LocalDateTime.now());
        return mongoTemplate.save(order);
    }
    
    // ==========================================
    // PROGRAMMATIC Transactions
    // ==========================================
    
    public Order createOrderProgrammatic(Order order) {
        TransactionTemplate transactionTemplate = 
            new TransactionTemplate(transactionManager);
        
        return transactionTemplate.execute(status -> {
            try {
                // Validate and decrement stock
                for (OrderItem item : order.getItems()) {
                    Query query = new Query(Criteria.where("id").is(item.getProductId()));
                    Update update = new Update().inc("stockQuantity", -item.getQuantity());
                    UpdateResult result = mongoTemplate.updateFirst(query, update, Product.class);
                    
                    if (result.getModifiedCount() == 0) {
                        throw new ProductNotFoundException(item.getProductId());
                    }
                }
                
                return mongoTemplate.save(order);
                
            } catch (Exception e) {
                status.setRollbackOnly();
                throw e;
            }
        });
    }
}
```

### Transaction Configuration

```java
@Configuration
public class MongoTransactionConfig {
    
    @Bean
    MongoTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }
}
```

---

## Demo Projects

### Available Demos

| Demo | Description | Port |
|------|-------------|------|
| [demo-mongodb-basics](./demo-mongodb-basics/) | CRUD, queries, MongoTemplate | 8084 |
| [demo-mongodb-aggregation](./demo-mongodb-aggregation/) | Aggregation pipelines | 8085 |

### Running Demos

```bash
# Start MongoDB (Docker)
docker run -d -p 27017:27017 --name mongodb mongo:latest

# Run basic demo
cd demo-mongodb-basics
mvn spring-boot:run

# Run aggregation demo
cd demo-mongodb-aggregation
mvn spring-boot:run
```

---

## Comparison: MongoDB vs Hibernate

| Aspect | MongoDB | Hibernate/JPA |
|--------|---------|--------------|
| **Data Model** | Documents | Tables |
| **Schema** | Flexible | Fixed |
| **Relationships** | Embedded/DBRef | Foreign Keys |
| **Query Language** | MongoDB Query | JPQL/Criteria |
| **Joins** | $lookup aggregation | Native JOINs |
| **Transactions** | Multi-doc (4.0+) | ACID native |
| **Caching** | Application level | L1/L2 cache |
| **Scaling** | Horizontal (sharding) | Vertical |

---

## Best Practices

### Document Design

1. **Embed when:**
   - Data is queried together
   - One-to-few relationship
   - Data doesn't change frequently

2. **Reference when:**
   - Data is queried independently
   - One-to-many (unbounded)
   - Many-to-many relationships

### Schema Versioning

```java
@Document(collection = "products")
public class Product {
    @Version
    private Long version;
    
    private Integer schemaVersion = 2;  // Track schema changes
}
```

### Validation

```java
@Document(collection = "products")
public class Product {
    
    @NotNull
    private String name;
    
    @Min(0)
    private BigDecimal price;
    
    @Size(min = 1, max = 10)
    private List<String> tags;
}
```

---

## Related Topics

- [JDBC Fundamentals](../01-jdbc-fundamentals/)
- [Hibernate Advanced](../02-hibernate-advanced/)
- [Redis Caching](../04-redis/)
