# Data Access with Spring Data JPA

> **Mastering database operations with JPA and Hibernate**

## 📚 Table of Contents

- [What is JPA?](#what-is-jpa)
- [Spring Data JPA](#spring-data-jpa)
- [Entity Relationships](#entity-relationships)
- [Repository Interfaces](#repository-interfaces)
- [Query Methods](#query-methods)
- [Custom Queries](#custom-queries)
- [Transactions](#transactions)
- [Best Practices](#best-practices)
- [Demo Project](#demo-project)
- [Interview Questions](#interview-questions)

---

## What is JPA?

**JPA** (Java Persistence API) is a specification for object-relational mapping (ORM) in Java. It allows you to map Java objects to database tables.

### JPA vs JDBC

| Feature | JDBC | JPA/Hibernate |
|---------|------|---------------|
| **Code Volume** | High (boilerplate) | Low (annotations) |
| **SQL** | Manual | Auto-generated |
| **Object Mapping** | Manual | Automatic |
| **Caching** | Manual | Built-in |
| **Relationships** | Manual joins | Automatic |

### JPA Providers

- **Hibernate** (most popular, default in Spring Boot)
- EclipseLink
- OpenJPA

---

## Spring Data JPA

Spring Data JPA reduces boilerplate code by providing repository interfaces with built-in CRUD operations.

### Key Features

1. **No implementation needed** - just interfaces
2. **Query methods** - derived from method names
3. **Custom queries** - @Query annotation
4. **Pagination and sorting**
5. **Auditing** - automatic timestamps

### Dependencies

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```

---

## Entity Relationships

### @OneToOne

One entity relates to exactly one other entity.

```java
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String username;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_id")
    private UserProfile profile;
}

@Entity
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String bio;
    
    @OneToOne(mappedBy = "profile")
    private User user;
}
```

### @OneToMany / @ManyToOne

One entity relates to many others (most common relationship).

```java
@Entity
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    private List<Book> books = new ArrayList<>();
}

@Entity
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    
    @ManyToOne
    @JoinColumn(name = "author_id")
    private Author author;
}
```

### @ManyToMany

Many entities relate to many others.

```java
@Entity
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    @ManyToMany
    @JoinTable(
        name = "student_course",
        joinColumns = @JoinColumn(name = "student_id"),
        inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private Set<Course> courses = new HashSet<>();
}

@Entity
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    @ManyToMany(mappedBy = "courses")
    private Set<Student> students = new HashSet<>();
}
```

### Cascade Types

```java
@OneToMany(cascade = CascadeType.ALL)  // All operations
@OneToMany(cascade = CascadeType.PERSIST)  // Only save
@OneToMany(cascade = CascadeType.MERGE)    // Only update
@OneToMany(cascade = CascadeType.REMOVE)   // Only delete
```

### Fetch Types

```java
@OneToMany(fetch = FetchType.LAZY)   // Load on demand (default)
@OneToMany(fetch = FetchType.EAGER)  // Load immediately
```

---

## Repository Interfaces

### JpaRepository

Provides all CRUD operations out of the box.

```java
public interface BookRepository extends JpaRepository<Book, Long> {
    // Inherits:
    // save(entity)
    // findById(id)
    // findAll()
    // delete(entity)
    // count()
    // existsById(id)
}
```

### Usage

```java
@Service
public class BookService {
    
    @Autowired
    private BookRepository bookRepository;
    
    public Book save(Book book) {
        return bookRepository.save(book);
    }
    
    public List<Book> findAll() {
        return bookRepository.findAll();
    }
    
    public Optional<Book> findById(Long id) {
        return bookRepository.findById(id);
    }
    
    public void delete(Long id) {
        bookRepository.deleteById(id);
    }
}
```

---

## Query Methods

Spring Data JPA generates queries from method names.

### Simple Queries

```java
public interface BookRepository extends JpaRepository<Book, Long> {
    
    // SELECT * FROM book WHERE title = ?
    Book findByTitle(String title);
    
    // SELECT * FROM book WHERE author_id = ?
    List<Book> findByAuthorId(Long authorId);
    
    // SELECT * FROM book WHERE price > ?
    List<Book> findByPriceGreaterThan(Double price);
    
    // SELECT * FROM book WHERE published_year BETWEEN ? AND ?
    List<Book> findByPublishedYearBetween(Integer start, Integer end);
}
```

### Keywords

| Keyword | Example | SQL |
|---------|---------|-----|
| `findBy` | findByTitle | WHERE title = ? |
| `And` | findByTitleAndAuthor | WHERE title = ? AND author = ? |
| `Or` | findByTitleOrAuthor | WHERE title = ? OR author = ? |
| `Between` | findByPriceBetween | WHERE price BETWEEN ? AND ? |
| `LessThan` | findByPriceLessThan | WHERE price < ? |
| `GreaterThan` | findByPriceGreaterThan | WHERE price > ? |
| `Like` | findByTitleLike | WHERE title LIKE ? |
| `StartingWith` | findByTitleStartingWith | WHERE title LIKE '?%' |
| `EndingWith` | findByTitleEndingWith | WHERE title LIKE '%?' |
| `Containing` | findByTitleContaining | WHERE title LIKE '%?%' |
| `OrderBy` | findByAuthorOrderByTitle | ORDER BY title |
| `Not` | findByTitleNot | WHERE title <> ? |
| `In` | findByIdIn | WHERE id IN (?) |
| `IsNull` | findByDescriptionIsNull | WHERE description IS NULL |

### Complex Queries

```java
public interface BookRepository extends JpaRepository<Book, Long> {
    
    // Multiple conditions
    List<Book> findByTitleAndPriceGreaterThan(String title, Double price);
    
    // Ordering
    List<Book> findByAuthorOrderByPublishedYearDesc(String author);
    
    // Limiting results
    List<Book> findTop10ByOrderByPriceDesc();
    
    // Distinct
    List<Book> findDistinctByAuthor(String author);
    
    // Ignore case
    Book findByTitleIgnoreCase(String title);
    
    // Contains (for collections)
    List<Book> findByTitleContaining(String keyword);
}
```

---

## Custom Queries

### @Query with JPQL

```java
public interface BookRepository extends JpaRepository<Book, Long> {
    
    // JPQL (Java Persistence Query Language)
    @Query("SELECT b FROM Book b WHERE b.price > :price")
    List<Book> findExpensiveBooks(@Param("price") Double price);
    
    // Join query
    @Query("SELECT b FROM Book b JOIN b.author a WHERE a.name = :authorName")
    List<Book> findByAuthorName(@Param("authorName") String authorName);
    
    // Custom projection
    @Query("SELECT b.title, b.price FROM Book b WHERE b.author.id = :authorId")
    List<Object[]> findTitleAndPriceByAuthorId(@Param("authorId") Long authorId);
}
```

### @Query with Native SQL

```java
@Query(value = "SELECT * FROM book WHERE price > ?1", nativeQuery = true)
List<Book> findExpensiveBooksNative(Double price);

@Query(value = "SELECT b.* FROM book b " +
               "JOIN author a ON b.author_id = a.id " +
               "WHERE a.country = :country", 
       nativeQuery = true)
List<Book> findBooksByAuthorCountry(@Param("country") String country);
```

### @Modifying for Updates/Deletes

```java
@Modifying
@Transactional
@Query("UPDATE Book b SET b.price = b.price * 1.1 WHERE b.author.id = :authorId")
int increasePriceByAuthor(@Param("authorId") Long authorId);

@Modifying
@Transactional
@Query("DELETE FROM Book b WHERE b.publishedYear < :year")
int deleteOldBooks(@Param("year") Integer year);
```

---

## Transactions

### @Transactional

Ensures operations are atomic (all or nothing).

```java
@Service
@Transactional
public class BookService {
    
    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private AuthorRepository authorRepository;
    
    // Entire method in transaction
    public void transferBook(Long bookId, Long newAuthorId) {
        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new RuntimeException("Book not found"));
        
        Author newAuthor = authorRepository.findById(newAuthorId)
            .orElseThrow(() -> new RuntimeException("Author not found"));
        
        book.setAuthor(newAuthor);
        bookRepository.save(book);
        
        // If exception occurs here, everything rolls back
    }
    
    // Read-only transaction (optimization)
    @Transactional(readOnly = true)
    public List<Book> findAll() {
        return bookRepository.findAll();
    }
}
```

### Transaction Propagation

```java
@Transactional(propagation = Propagation.REQUIRED)      // Join existing or create new
@Transactional(propagation = Propagation.REQUIRES_NEW)  // Always create new
@Transactional(propagation = Propagation.MANDATORY)     // Must be in existing
@Transactional(propagation = Propagation.NEVER)         // Must not be in transaction
```

### Isolation Levels

```java
@Transactional(isolation = Isolation.READ_UNCOMMITTED)  // Lowest isolation
@Transactional(isolation = Isolation.READ_COMMITTED)    // Default for most DBs
@Transactional(isolation = Isolation.REPEATABLE_READ)   // Higher isolation
@Transactional(isolation = Isolation.SERIALIZABLE)      // Highest isolation
```

---

## Best Practices

### 1. Use @Transactional Appropriately

```java
// ✅ Good - Service layer
@Service
@Transactional
public class BookService {
    public void updateBook(Book book) { }
}

// ❌ Bad - Repository layer
@Repository
@Transactional
public interface BookRepository extends JpaRepository<Book, Long> { }
```

### 2. Prefer Lazy Loading

```java
// ✅ Good - Load on demand
@OneToMany(fetch = FetchType.LAZY)
private List<Book> books;

// ❌ Bad - N+1 query problem
@OneToMany(fetch = FetchType.EAGER)
private List<Book> books;
```

### 3. Use DTOs for API Responses

```java
// ✅ Good - Don't expose entities
public class BookDTO {
    private Long id;
    private String title;
    // No lazy collections, no circular references
}

// ❌ Bad - Exposing entity directly
@GetMapping("/books")
public List<Book> getBooks() {
    return bookRepository.findAll();  // May cause JSON serialization issues
}
```

### 4. Handle Bidirectional Relationships

```java
public class Author {
    @OneToMany(mappedBy = "author")
    private List<Book> books;
    
    // Helper method
    public void addBook(Book book) {
        books.add(book);
        book.setAuthor(this);  // Keep both sides in sync
    }
}
```

### 5. Use Proper Cascade Types

```java
// ✅ Good - Carefully chosen cascade
@OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
private List<Book> books;

// ❌ Bad - Dangerous with bidirectional
@ManyToOne(cascade = CascadeType.ALL)  // Don't cascade to parent!
private Author author;
```

### 6. Add Indexes for Query Performance

```java
@Entity
@Table(name = "book", indexes = {
    @Index(name = "idx_title", columnList = "title"),
    @Index(name = "idx_author_id", columnList = "author_id")
})
public class Book { }
```

### 7. Use Query Methods Over Native Queries

```java
// ✅ Good - Type-safe, database-independent
List<Book> findByTitleContaining(String keyword);

// ⚠️ Use only when necessary - Database-specific
@Query(value = "SELECT * FROM book WHERE ...", nativeQuery = true)
List<Book> complexQuery();
```

---

## Demo Project

See [demo-data-jpa](demo-data-jpa/) for a complete example with:
- Entity relationships (OneToMany, ManyToOne, ManyToMany)
- Repository methods
- Custom queries
- Transactions
- Complete CRUD operations

---

## Interview Questions

### Q1: What's the difference between JPA and Hibernate?

**Answer:**
- **JPA** is a **specification** (interface)
- **Hibernate** is an **implementation** (concrete class)

JPA defines the rules, Hibernate implements them. Spring Data JPA builds on top of JPA.

### Q2: What's the difference between save() and saveAndFlush()?

**Answer:**
- `save()`: Persists to context, writes to DB at transaction end
- `saveAndFlush()`: Immediately flushes to database

```java
Book book = new Book();
repository.save(book);           // May not be in DB yet
repository.saveAndFlush(book);   // Guaranteed in DB now
```

### Q3: Explain N+1 problem and how to solve it?

**Answer:**

**Problem:**
```java
List<Author> authors = authorRepository.findAll();  // 1 query
for (Author author : authors) {
    author.getBooks().size();  // N queries (one per author)
}
```

**Solutions:**

1. **Join Fetch:**
```java
@Query("SELECT a FROM Author a JOIN FETCH a.books")
List<Author> findAllWithBooks();
```

2. **EntityGraph:**
```java
@EntityGraph(attributePaths = {"books"})
List<Author> findAll();
```

3. **Batch Fetching:**
```java
@BatchSize(size = 10)
@OneToMany(mappedBy = "author")
private List<Book> books;
```

### Q4: What's the difference between @Id and @GeneratedValue?

**Answer:**
- `@Id`: Marks field as primary key
- `@GeneratedValue`: Defines how ID is generated

```java
@Id  // This is the primary key
@GeneratedValue(strategy = GenerationType.IDENTITY)  // Auto-increment
private Long id;
```

**Strategies:**
- `IDENTITY`: Database auto-increment
- `SEQUENCE`: Database sequence
- `TABLE`: Separate table for IDs
- `AUTO`: Provider chooses

### Q5: When to use @Transactional?

**Answer:**

**Use @Transactional when:**
- Multiple database operations must succeed/fail together
- Modifying data (@Modifying queries)
- Lazy loading outside repository

```java
@Service
@Transactional
public class OrderService {
    
    public void createOrder(Order order) {
        orderRepository.save(order);        // Operation 1
        inventoryService.reduceStock();     // Operation 2
        paymentService.charge();            // Operation 3
        // All succeed or all rollback
    }
}
```

**Don't use on:**
- Read-only queries (use `readOnly = true`)
- Repository interfaces (Spring manages transactions)

### Q6: What's the difference between JPQL and Native SQL?

**Answer:**

**JPQL:**
```java
@Query("SELECT b FROM Book b WHERE b.price > :price")
List<Book> findExpensive(@Param("price") Double price);
```
- Works with entities (Book, not table name)
- Database-independent
- Type-safe

**Native SQL:**
```java
@Query(value = "SELECT * FROM book WHERE price > :price", nativeQuery = true)
List<Book> findExpensive(@Param("price") Double price);
```
- Works with tables and columns
- Database-specific
- More powerful (DB-specific features)

### Q7: How to handle bidirectional relationships in JSON?

**Answer:**

**Problem:** Infinite recursion with Jackson

```java
// Author → Books → Author → Books → ...
```

**Solutions:**

1. **@JsonManagedReference / @JsonBackReference:**
```java
public class Author {
    @JsonManagedReference
    @OneToMany(mappedBy = "author")
    private List<Book> books;
}

public class Book {
    @JsonBackReference
    @ManyToOne
    private Author author;
}
```

2. **@JsonIgnore:**
```java
public class Book {
    @JsonIgnore
    @ManyToOne
    private Author author;
}
```

3. **Use DTOs (Best Practice):**
```java
public class AuthorDTO {
    private Long id;
    private String name;
    // No books collection
}
```

---

## Summary

| Concept | Key Points |
|---------|------------|
| **JPA** | ORM specification for Java |
| **Spring Data JPA** | Reduces boilerplate, provides repositories |
| **Relationships** | @OneToOne, @OneToMany, @ManyToOne, @ManyToMany |
| **Repositories** | JpaRepository with built-in CRUD |
| **Query Methods** | Derived from method names |
| **Custom Queries** | @Query with JPQL or native SQL |
| **Transactions** | @Transactional for atomic operations |

Spring Data JPA dramatically simplifies database access, turning hours of JDBC code into simple interface declarations.

---

**Next**: [Exception Handling](../06-exception-handling/)
