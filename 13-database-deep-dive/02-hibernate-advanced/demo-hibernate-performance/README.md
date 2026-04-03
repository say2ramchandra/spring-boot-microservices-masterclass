# Demo: Hibernate Performance

This demo project demonstrates the N+1 query problem and various solutions, along with batch processing optimizations.

## Overview

### Topics Covered

| Topic | Description |
|-------|-------------|
| N+1 Problem | Understanding lazy loading performance issues |
| JOIN FETCH | Eager loading with JPQL |
| Entity Graph | Declarative fetch graphs |
| Batch Size | Batch loading collections |
| Batch Processing | Efficient bulk operations |

## Quick Start

```bash
# Navigate to demo directory
cd 13-database-deep-dive/02-hibernate-advanced/demo-hibernate-performance

# Run the application
mvn spring-boot:run
```

**Access Points:**
- Application: http://localhost:8083
- H2 Console: http://localhost:8083/h2-console
- Compare Solutions: http://localhost:8083/api/demo/compare

## Project Structure

```
demo-hibernate-performance/
├── src/main/java/com/masterclass/hibernate/performance/
│   ├── HibernatePerformanceDemoApplication.java
│   ├── config/
│   │   └── DataInitializer.java
│   ├── controller/
│   │   └── PerformanceController.java
│   ├── entity/
│   │   ├── Author.java          # @BatchSize annotation
│   │   └── Book.java
│   ├── repository/
│   │   ├── AuthorRepository.java # Various fetch strategies
│   │   └── BookRepository.java
│   └── service/
│       └── PerformanceDemoService.java
└── src/main/resources/
    └── application.properties
```

## The N+1 Problem

### What is it?

When you load a list of entities and then access their lazy-loaded associations, Hibernate executes:
- 1 query to load the parent entities
- N additional queries to load each parent's associations

```java
// 1 query: SELECT * FROM authors
List<Author> authors = authorRepository.findAll();

// N queries: SELECT * FROM books WHERE author_id = ?
for (Author author : authors) {
    author.getBooks().size();  // Triggers lazy load!
}
```

### Visual Representation

```
Without Optimization (N+1):
┌─────────────────────────────────────────────┐
│ Query 1: SELECT * FROM authors              │  → Returns 10 authors
├─────────────────────────────────────────────┤
│ Query 2: SELECT * FROM books WHERE aid = 1  │  ← Triggered by getBooks()
│ Query 3: SELECT * FROM books WHERE aid = 2  │
│ Query 4: SELECT * FROM books WHERE aid = 3  │
│ ...                                         │
│ Query 11: SELECT * FROM books WHERE aid = 10│
└─────────────────────────────────────────────┘
Total: 11 queries for 10 authors!

With JOIN FETCH (1 query):
┌─────────────────────────────────────────────────────────────────┐
│ SELECT a.*, b.* FROM authors a LEFT JOIN books b ON a.id = b.aid│
└─────────────────────────────────────────────────────────────────┘
Total: 1 query with all data!
```

## Demo Endpoints

### Compare All Solutions
```bash
# Best endpoint - compares all approaches
curl http://localhost:8083/api/demo/compare | jq

# Sample output:
[
  {
    "method": "N+1 Problem (no optimization)",
    "queryCount": 11,
    "executionTimeMs": 45
  },
  {
    "method": "JOIN FETCH",
    "queryCount": 1,
    "executionTimeMs": 12
  },
  {
    "method": "Entity Graph",
    "queryCount": 1,
    "executionTimeMs": 10
  },
  {
    "method": "Batch Size",
    "queryCount": 2,
    "executionTimeMs": 15
  }
]
```

### Individual Solutions
```bash
# N+1 Problem (watch the logs!)
curl http://localhost:8083/api/demo/n1-problem

# Solution 1: JOIN FETCH
curl http://localhost:8083/api/demo/join-fetch

# Solution 2: Entity Graph
curl http://localhost:8083/api/demo/entity-graph

# Solution 3: Batch Size
curl http://localhost:8083/api/demo/batch-size
```

### Batch Processing
```bash
# Batch insert (creates authors with books)
curl -X POST "http://localhost:8083/api/demo/batch-insert?authors=100&booksPerAuthor=5"

# Batch update (10% price increase on all books)
curl -X POST http://localhost:8083/api/demo/batch-update
```

## N+1 Solutions Explained

### Solution 1: JOIN FETCH

```java
@Query("SELECT DISTINCT a FROM Author a LEFT JOIN FETCH a.books")
List<Author> findAllWithBooksJoinFetch();
```

**Pros:**
- Single query
- Simple to implement
- Works with JPQL and native queries

**Cons:**
- Can cause Cartesian product with multiple collections
- May fetch more data than needed
- No pagination support with fetch joins on collections

### Solution 2: Entity Graph

```java
@EntityGraph(attributePaths = {"books"})
@Query("SELECT a FROM Author a")
List<Author> findAllWithBooksEntityGraph();
```

**Pros:**
- Declarative and reusable
- Can define complex graphs
- Works with Spring Data method naming

**Cons:**
- Can become complex with deep graphs
- Same Cartesian product issue

### Solution 3: Batch Size

```java
@Entity
@BatchSize(size = 10)
public class Author {
    
    @OneToMany(mappedBy = "author")
    @BatchSize(size = 10)
    private List<Book> books;
}
```

**Pros:**
- Automatic batch loading
- Works with existing queries
- Good for unpredictable access patterns

**Cons:**
- Not as efficient as single query
- Requires careful size tuning

### Solution 4: Subselect

```java
@OneToMany(mappedBy = "author")
@Fetch(FetchMode.SUBSELECT)
private List<Book> books;
```

**Pros:**
- Loads all collections in one query
- Works automatically

**Cons:**
- Always loads ALL parent's collections
- Can be memory intensive

## Batch Processing Configuration

### application.properties
```properties
# JDBC batch size
spring.jpa.properties.hibernate.jdbc.batch_size=50

# Order inserts/updates for batching
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true

# Enable batch for versioned entities
spring.jpa.properties.hibernate.jdbc.batch_versioned_data=true
```

### Best Practices for Batch Processing

```java
@Transactional
public void batchInsert(List<Entity> entities) {
    int batchSize = 50;
    
    for (int i = 0; i < entities.size(); i++) {
        entityManager.persist(entities.get(i));
        
        // Flush and clear periodically
        if (i > 0 && i % batchSize == 0) {
            entityManager.flush();
            entityManager.clear();
        }
    }
    
    entityManager.flush();
}
```

## Choosing the Right Solution

| Scenario | Recommended Solution |
|----------|---------------------|
| Always need associations | JOIN FETCH |
| Sometimes need associations | Entity Graph |
| Large collection of parents | Batch Size |
| Read-only reporting | Projections/DTOs |
| Complex object graphs | Named Entity Graph |
| Bulk data operations | Batch Processing |

## Statistics Endpoints

```bash
# Get detailed statistics
curl http://localhost:8083/api/stats

# Reset statistics
curl -X POST http://localhost:8083/api/stats/reset
```

## Related Topics
- [Hibernate Caching Demo](../demo-hibernate-caching/)
- [Hibernate Advanced README](../README.md)
- [JDBC Fundamentals](../../01-jdbc-fundamentals/)
