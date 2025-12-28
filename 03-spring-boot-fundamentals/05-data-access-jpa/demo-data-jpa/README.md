# Spring Data JPA Demo

This demo showcases comprehensive Spring Data JPA usage including entity relationships, repository methods, custom queries, and transaction management.

## 📋 Features Demonstrated

- **Entity Relationships**
  - @OneToMany (Author → Books)
  - @ManyToOne (Book → Author)
  - @ManyToMany (Book ↔ Category)
  - Bidirectional relationship management
  - Cascade operations and orphan removal

- **Repository Operations**
  - JpaRepository built-in methods
  - Query method derivation from method names
  - Custom JPQL queries with @Query
  - Native SQL queries
  - @Modifying queries for updates/deletes
  - Aggregate queries (COUNT, AVG)

- **Transaction Management**
  - @Transactional for atomic operations
  - Read-only transactions for optimization
  - Complex multi-entity transactions
  - Rollback on exceptions

- **Best Practices**
  - DTOs to avoid entity exposure
  - @JsonManagedReference/@JsonBackReference for circular refs
  - Lazy loading to prevent N+1 problems
  - Indexed columns for performance
  - Validation with Bean Validation

## 🚀 Running the Demo

### Prerequisites
- Java 17 or higher
- Maven 3.6+

### Steps

1. **Navigate to demo directory:**
   ```bash
   cd 03-spring-boot-fundamentals/05-data-access-jpa/demo-data-jpa
   ```

2. **Run the application:**
   ```bash
   mvn spring-boot:run
   ```

3. **Application will start on port 8080**

## 🗄️ Database Access

### H2 Console

Access the in-memory database:
- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: *(leave empty)*

### Sample Data

The application initializes with:
- **3 Authors**: J.K. Rowling, George Orwell, Isaac Asimov
- **6 Books**: Harry Potter series, 1984, Animal Farm, Foundation, I Robot
- **5 Categories**: Fiction, Non-Fiction, Science Fiction, Mystery, Technology

## 📡 API Endpoints

### Authors

```bash
# Get all authors
curl http://localhost:8080/api/library/authors

# Get author by ID
curl http://localhost:8080/api/library/authors/1

# Get author with all books (JOIN FETCH)
curl http://localhost:8080/api/library/authors/1/with-books

# Get authors by country
curl http://localhost:8080/api/library/authors/country/United%20Kingdom

# Create new author
curl -X POST http://localhost:8080/api/library/authors \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Stephen King",
    "country": "United States",
    "birthDate": "1947-09-21",
    "bio": "American author of horror, supernatural fiction, suspense, and fantasy novels"
  }'

# Delete author
curl -X DELETE http://localhost:8080/api/library/authors/1
```

### Books

```bash
# Get all books
curl http://localhost:8080/api/library/books

# Get book by ID
curl http://localhost:8080/api/library/books/1

# Get book with full details (author + categories)
curl http://localhost:8080/api/library/books/1/details

# Search books by title
curl "http://localhost:8080/api/library/books/search?keyword=harry"

# Get books by price range
curl "http://localhost:8080/api/library/books/price-range?minPrice=10&maxPrice=20"

# Get books by author
curl http://localhost:8080/api/library/books/by-author/1

# Create new book
curl -X POST http://localhost:8080/api/library/books \
  -H "Content-Type: application/json" \
  -d '{
    "title": "The Shining",
    "isbn": "978-0-385-12167-5",
    "description": "Horror novel about a haunted hotel",
    "price": 19.99,
    "publishedYear": 1977,
    "stock": 50,
    "author": {
      "id": 4
    }
  }'

# Delete book
curl -X DELETE http://localhost:8080/api/library/books/1
```

### Categories

```bash
# Get all categories
curl http://localhost:8080/api/library/categories

# Get category by ID
curl http://localhost:8080/api/library/categories/1

# Create new category
curl -X POST http://localhost:8080/api/library/categories \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Biography",
    "description": "Biographical books"
  }'

# Delete category
curl -X DELETE http://localhost:8080/api/library/categories/1
```

### Complex Operations

```bash
# Add stock to a book
curl -X PUT "http://localhost:8080/api/library/books/1/stock?quantity=50"

# Increase all book prices by 10% for an author
curl -X PUT "http://localhost:8080/api/library/authors/1/increase-prices?percentage=10"

# Transfer book to a different author
curl -X PUT http://localhost:8080/api/library/books/1/transfer-author/2

# Get library statistics
curl http://localhost:8080/api/library/statistics

# Get average book price for an author
curl http://localhost:8080/api/library/authors/1/average-price
```

## 🧪 Testing Relationships

### One-to-Many (Author → Books)

```bash
# Get an author with all their books (JOIN FETCH - single query)
curl http://localhost:8080/api/library/authors/1/with-books

# This demonstrates:
# - @OneToMany relationship
# - JOIN FETCH to avoid N+1 problem
# - @JsonManagedReference to prevent infinite recursion
```

### Many-to-One (Book → Author)

```bash
# Get a book with its author details
curl http://localhost:8080/api/library/books/1/details

# This demonstrates:
# - @ManyToOne relationship
# - @JsonBackReference on the inverse side
# - Lazy loading with JOIN FETCH
```

### Many-to-Many (Book ↔ Category)

```bash
# Get a book with all its categories
curl http://localhost:8080/api/library/books/1/details

# This demonstrates:
# - @ManyToMany relationship
# - @JoinTable for junction table
# - Multiple categories per book
```

## 🔍 Testing Custom Queries

### Query Method Examples

The application includes various query methods:

```java
// Simple query methods
findByTitleContainingIgnoreCase(String keyword)
findByPriceBetween(BigDecimal min, BigDecimal max)
findByPublishedYearGreaterThanEqual(Integer year)

// JPQL queries
@Query("SELECT b FROM Book b WHERE b.price > :price")
List<Book> findExpensiveBooks(@Param("price") Double price);

// Native SQL queries
@Query(value = "SELECT * FROM books WHERE ...", nativeQuery = true)
List<Book> complexQuery();

// Modifying queries
@Modifying
@Query("UPDATE Book b SET b.price = b.price * :factor")
int updatePrices(@Param("factor") BigDecimal factor);
```

## 🎯 Transaction Examples

### Example 1: Transfer Book to New Author

```bash
# This operation demonstrates transaction management:
# - Fetch book and authors
# - Remove from old author's collection
# - Add to new author's collection
# - Save changes (all or nothing)

curl -X PUT http://localhost:8080/api/library/books/1/transfer-author/2
```

If any step fails, the entire transaction rolls back.

### Example 2: Batch Price Update

```bash
# This demonstrates @Modifying query in transaction:
# - Updates multiple book prices in one query
# - Atomic operation (all prices updated or none)

curl -X PUT "http://localhost:8080/api/library/authors/1/increase-prices?percentage=15"
```

## 📊 Database Schema

The application creates the following tables:

```sql
-- Authors table
CREATE TABLE authors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    country VARCHAR(50),
    birth_date DATE,
    bio VARCHAR(500)
);

-- Books table
CREATE TABLE books (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    isbn VARCHAR(20) UNIQUE NOT NULL,
    description VARCHAR(1000),
    price DECIMAL(10,2),
    published_year INT,
    stock INT DEFAULT 0,
    author_id BIGINT NOT NULL,
    FOREIGN KEY (author_id) REFERENCES authors(id)
);

-- Categories table
CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(200)
);

-- Book-Category junction table
CREATE TABLE book_category (
    book_id BIGINT,
    category_id BIGINT,
    PRIMARY KEY (book_id, category_id),
    FOREIGN KEY (book_id) REFERENCES books(id),
    FOREIGN KEY (category_id) REFERENCES categories(id)
);
```

## 💡 Key Learnings

### 1. Entity Relationships

**OneToMany/ManyToOne:**
- Use `mappedBy` on the "one" side to specify the owner
- Use `@JoinColumn` on the "many" side to specify the foreign key
- Use helper methods to maintain bidirectional consistency

**ManyToMany:**
- Use `@JoinTable` on the owning side
- Use `mappedBy` on the inverse side
- Use `Set` instead of `List` for better performance

### 2. Avoiding N+1 Problem

**Problem:**
```java
List<Author> authors = authorRepository.findAll();  // 1 query
for (Author author : authors) {
    author.getBooks().size();  // N queries!
}
```

**Solution: JOIN FETCH**
```java
@Query("SELECT a FROM Author a JOIN FETCH a.books")
List<Author> findAllWithBooks();  // 1 query with join
```

### 3. JSON Serialization

**Problem:** Bidirectional relationships cause infinite recursion

**Solutions:**
- `@JsonManagedReference` / `@JsonBackReference`
- `@JsonIgnore`
- **Best: Use DTOs** (don't expose entities directly)

### 4. Transaction Management

**Use @Transactional when:**
- Multiple related database operations
- Need atomicity (all or nothing)
- Lazy loading outside repository

**Optimization:**
- Use `readOnly = true` for queries
- Keep transactions short
- Avoid external calls inside transactions

## 🎓 Interview Preparation

After running this demo, you should understand:

1. **Entity Relationships**
   - How to map OneToMany, ManyToOne, ManyToMany
   - Cascade types and when to use them
   - Fetch types (LAZY vs EAGER)

2. **Repository Methods**
   - Built-in CRUD operations
   - Query method derivation rules
   - JPQL vs Native SQL
   - @Modifying queries

3. **Transactions**
   - When to use @Transactional
   - Transaction propagation
   - Rollback behavior
   - Read-only optimization

4. **Best Practices**
   - Avoiding N+1 problems
   - Using DTOs for API responses
   - Handling bidirectional relationships
   - Database indexing

## 📚 References

- [Spring Data JPA Documentation](https://spring.io/projects/spring-data-jpa)
- [JPA Specification](https://jcp.org/en/jsr/detail?id=338)
- [Hibernate Documentation](https://hibernate.org/orm/documentation/)

---

**Next Demo:** [Exception Handling](../../06-exception-handling/demo-exception-handling/)
