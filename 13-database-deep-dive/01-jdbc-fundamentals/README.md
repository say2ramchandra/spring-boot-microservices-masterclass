# JDBC Fundamentals

> **Master low-level database access with Java Database Connectivity**

## 📚 Table of Contents

- [What is JDBC?](#what-is-jdbc)
- [JDBC Architecture](#jdbc-architecture)
- [Core Components](#core-components)
- [Connection Management](#connection-management)
- [Statement Types](#statement-types)
- [ResultSet Operations](#resultset-operations)
- [Transaction Management](#transaction-management)
- [Batch Processing](#batch-processing)
- [Connection Pooling](#connection-pooling)
- [Best Practices](#best-practices)
- [Interview Questions](#interview-questions)

---

## What is JDBC?

**JDBC (Java Database Connectivity)** is a Java API that enables Java applications to interact with relational databases. It provides a standard interface for connecting to databases, executing SQL queries, and processing results.

### Why Learn JDBC?

Even with modern ORMs like Hibernate, understanding JDBC is essential:

| Reason | Explanation |
|--------|-------------|
| **Foundation** | JPA/Hibernate are built on top of JDBC |
| **Control** | Fine-grained control over SQL execution |
| **Performance** | Direct SQL can be faster for complex queries |
| **Legacy** | Many enterprise systems still use raw JDBC |
| **Debugging** | Understanding JDBC helps troubleshoot ORM issues |
| **Interviews** | Common interview topic for Java developers |

---

## JDBC Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                     Java Application                         │
└─────────────────────────────┬───────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                        JDBC API                              │
│  (java.sql.*, javax.sql.*)                                  │
│  Connection, Statement, ResultSet, PreparedStatement        │
└─────────────────────────────┬───────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    JDBC Driver Manager                       │
│  (Manages database drivers)                                  │
└─────────────────────────────┬───────────────────────────────┘
                              │
        ┌─────────────────────┼─────────────────────┐
        ▼                     ▼                     ▼
┌───────────────┐   ┌───────────────┐   ┌───────────────┐
│ MySQL Driver  │   │ PostgreSQL    │   │  Oracle       │
│               │   │ Driver        │   │  Driver       │
└───────┬───────┘   └───────┬───────┘   └───────┬───────┘
        │                   │                   │
        ▼                   ▼                   ▼
┌───────────────┐   ┌───────────────┐   ┌───────────────┐
│    MySQL      │   │  PostgreSQL   │   │   Oracle      │
│   Database    │   │   Database    │   │   Database    │
└───────────────┘   └───────────────┘   └───────────────┘
```

### JDBC Driver Types

| Type | Name | Description |
|------|------|-------------|
| Type 1 | JDBC-ODBC Bridge | Deprecated, uses ODBC driver |
| Type 2 | Native API | Uses database-specific native libraries |
| Type 3 | Network Protocol | Middleware converts JDBC to DB protocol |
| **Type 4** | **Thin Driver** | **Pure Java, direct to database (Most Common)** |

---

## Core Components

### 1. DriverManager

Manages JDBC drivers and establishes connections.

```java
// Register driver (automatic in JDBC 4.0+)
Class.forName("com.mysql.cj.jdbc.Driver");

// Get connection
Connection conn = DriverManager.getConnection(
    "jdbc:mysql://localhost:3306/mydb",
    "username",
    "password"
);
```

### 2. Connection

Represents a session with the database.

```java
// Connection properties
conn.setAutoCommit(false);        // Manual transaction control
conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
conn.setReadOnly(true);           // Optimization hint

// Check connection state
boolean isValid = conn.isValid(5);  // 5 second timeout
boolean isClosed = conn.isClosed();
```

### 3. Statement

Executes SQL queries.

```java
Statement stmt = conn.createStatement();

// Execute query (SELECT)
ResultSet rs = stmt.executeQuery("SELECT * FROM users");

// Execute update (INSERT, UPDATE, DELETE)
int rowsAffected = stmt.executeUpdate("DELETE FROM users WHERE id = 1");

// Execute any SQL
boolean hasResultSet = stmt.execute("SELECT * FROM users");
```

### 4. PreparedStatement

Pre-compiled SQL statement with parameters (prevents SQL injection).

```java
String sql = "INSERT INTO users (name, email, age) VALUES (?, ?, ?)";
PreparedStatement pstmt = conn.prepareStatement(sql);

pstmt.setString(1, "John Doe");
pstmt.setString(2, "john@example.com");
pstmt.setInt(3, 30);

int rowsInserted = pstmt.executeUpdate();
```

### 5. CallableStatement

Executes stored procedures.

```java
// {call procedure_name(?, ?, ?)}
CallableStatement cstmt = conn.prepareCall("{call getUserById(?, ?)}");

cstmt.setInt(1, 100);                          // IN parameter
cstmt.registerOutParameter(2, Types.VARCHAR);  // OUT parameter

cstmt.execute();

String userName = cstmt.getString(2);  // Get OUT value
```

### 6. ResultSet

Holds query results.

```java
ResultSet rs = stmt.executeQuery("SELECT id, name, email FROM users");

while (rs.next()) {
    int id = rs.getInt("id");           // By column name
    String name = rs.getString(2);       // By column index (1-based)
    String email = rs.getString("email");
    
    System.out.printf("User: %d - %s (%s)%n", id, name, email);
}
```

---

## Connection Management

### Basic Connection

```java
public class JdbcConnectionExample {
    
    private static final String URL = "jdbc:mysql://localhost:3306/mydb";
    private static final String USER = "root";
    private static final String PASSWORD = "password";
    
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
```

### Using Properties

```java
public static Connection getConnection() throws SQLException {
    Properties props = new Properties();
    props.setProperty("user", "root");
    props.setProperty("password", "password");
    props.setProperty("useSSL", "false");
    props.setProperty("serverTimezone", "UTC");
    props.setProperty("allowPublicKeyRetrieval", "true");
    
    return DriverManager.getConnection(URL, props);
}
```

### Try-with-Resources (Recommended)

```java
public List<User> getAllUsers() {
    String sql = "SELECT id, name, email FROM users";
    List<User> users = new ArrayList<>();
    
    try (Connection conn = getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {
        
        while (rs.next()) {
            users.add(new User(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("email")
            ));
        }
    } catch (SQLException e) {
        throw new RuntimeException("Database error", e);
    }
    
    return users;
}
```

---

## Statement Types

### Statement vs PreparedStatement vs CallableStatement

| Feature | Statement | PreparedStatement | CallableStatement |
|---------|-----------|-------------------|-------------------|
| **SQL Injection** | Vulnerable | Safe | Safe |
| **Performance** | Slow | Fast (cached) | Fast |
| **Parameters** | String concat | Placeholders (?) | IN/OUT/INOUT |
| **Use Case** | Static SQL | Dynamic SQL | Stored Procedures |

### Preventing SQL Injection

```java
// ❌ WRONG - SQL Injection vulnerable
String sql = "SELECT * FROM users WHERE name = '" + userInput + "'";
Statement stmt = conn.createStatement();
ResultSet rs = stmt.executeQuery(sql);  // userInput: "'; DROP TABLE users; --"

// ✅ CORRECT - Parameterized query
String sql = "SELECT * FROM users WHERE name = ?";
PreparedStatement pstmt = conn.prepareStatement(sql);
pstmt.setString(1, userInput);  // Safely escaped
ResultSet rs = pstmt.executeQuery();
```

### PreparedStatement Data Types

```java
PreparedStatement pstmt = conn.prepareStatement(
    "INSERT INTO products (name, price, quantity, created_at, image) VALUES (?, ?, ?, ?, ?)"
);

pstmt.setString(1, "Laptop");                    // VARCHAR
pstmt.setBigDecimal(2, new BigDecimal("999.99")); // DECIMAL
pstmt.setInt(3, 100);                            // INTEGER
pstmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now())); // TIMESTAMP
pstmt.setBlob(5, new FileInputStream("image.jpg")); // BLOB

pstmt.executeUpdate();
```

---

## ResultSet Operations

### ResultSet Types

```java
Statement stmt = conn.createStatement(
    ResultSet.TYPE_SCROLL_INSENSITIVE,  // Can scroll forward/backward
    ResultSet.CONCUR_READ_ONLY          // Read-only cursor
);
```

| Type | Scrollable | Sensitive to Changes |
|------|------------|---------------------|
| TYPE_FORWARD_ONLY | No | N/A |
| TYPE_SCROLL_INSENSITIVE | Yes | No |
| TYPE_SCROLL_SENSITIVE | Yes | Yes |

### Navigation Methods

```java
ResultSet rs = stmt.executeQuery("SELECT * FROM products");

rs.next();          // Move to next row
rs.previous();      // Move to previous row
rs.first();         // Move to first row
rs.last();          // Move to last row
rs.absolute(5);     // Move to row 5
rs.relative(-2);    // Move 2 rows back
rs.beforeFirst();   // Before first row
rs.afterLast();     // After last row

// Check position
boolean isFirst = rs.isFirst();
boolean isLast = rs.isLast();
int currentRow = rs.getRow();
```

### Handling NULL Values

```java
ResultSet rs = stmt.executeQuery("SELECT name, age, salary FROM employees");

while (rs.next()) {
    String name = rs.getString("name");
    
    int age = rs.getInt("age");
    if (rs.wasNull()) {
        System.out.println("Age is NULL");
    }
    
    // Alternative: Use wrapper classes
    Integer ageObj = rs.getObject("age", Integer.class);  // Returns null if NULL
    BigDecimal salary = rs.getBigDecimal("salary");       // Returns null if NULL
}
```

---

## Transaction Management

### Auto-Commit Mode

```java
// Default: auto-commit is ON (each statement is a transaction)
conn.setAutoCommit(true);   // Default
conn.setAutoCommit(false);  // Manual transaction control
```

### Manual Transaction Control

```java
Connection conn = null;
try {
    conn = getConnection();
    conn.setAutoCommit(false);  // Start transaction
    
    // Execute multiple operations
    PreparedStatement pstmt1 = conn.prepareStatement(
        "UPDATE accounts SET balance = balance - ? WHERE id = ?"
    );
    pstmt1.setBigDecimal(1, new BigDecimal("100.00"));
    pstmt1.setInt(2, 1);
    pstmt1.executeUpdate();
    
    PreparedStatement pstmt2 = conn.prepareStatement(
        "UPDATE accounts SET balance = balance + ? WHERE id = ?"
    );
    pstmt2.setBigDecimal(1, new BigDecimal("100.00"));
    pstmt2.setInt(2, 2);
    pstmt2.executeUpdate();
    
    conn.commit();  // Commit transaction
    
} catch (SQLException e) {
    if (conn != null) {
        try {
            conn.rollback();  // Rollback on error
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    throw new RuntimeException("Transaction failed", e);
} finally {
    if (conn != null) {
        try {
            conn.setAutoCommit(true);  // Restore default
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
```

### Savepoints

```java
conn.setAutoCommit(false);

Savepoint savepoint1 = conn.setSavepoint("SAVEPOINT_1");

try {
    // Some operations
    stmt.executeUpdate("INSERT INTO orders VALUES (1, 'Order 1')");
    
    Savepoint savepoint2 = conn.setSavepoint("SAVEPOINT_2");
    
    // More operations that might fail
    stmt.executeUpdate("INSERT INTO order_items VALUES (1, 1, 100)");
    
    conn.commit();
    
} catch (SQLException e) {
    conn.rollback(savepoint1);  // Rollback to savepoint
    // Or rollback everything: conn.rollback();
}
```

### Transaction Isolation Levels

```java
// Set isolation level
conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
```

| Level | Dirty Read | Non-Repeatable Read | Phantom Read |
|-------|------------|---------------------|--------------|
| READ_UNCOMMITTED | Yes | Yes | Yes |
| READ_COMMITTED | No | Yes | Yes |
| REPEATABLE_READ | No | No | Yes |
| SERIALIZABLE | No | No | No |

---

## Batch Processing

### Statement Batch

```java
Statement stmt = conn.createStatement();

stmt.addBatch("INSERT INTO products VALUES (1, 'Product A', 10.00)");
stmt.addBatch("INSERT INTO products VALUES (2, 'Product B', 20.00)");
stmt.addBatch("INSERT INTO products VALUES (3, 'Product C', 30.00)");

int[] results = stmt.executeBatch();  // Returns affected rows per statement
```

### PreparedStatement Batch (Recommended)

```java
String sql = "INSERT INTO users (name, email) VALUES (?, ?)";
PreparedStatement pstmt = conn.prepareStatement(sql);

conn.setAutoCommit(false);

for (User user : users) {
    pstmt.setString(1, user.getName());
    pstmt.setString(2, user.getEmail());
    pstmt.addBatch();
    
    // Execute in chunks to avoid memory issues
    if (++count % BATCH_SIZE == 0) {
        pstmt.executeBatch();
        conn.commit();
    }
}

pstmt.executeBatch();  // Execute remaining
conn.commit();
```

### Batch Performance Tuning

```java
// MySQL: Add rewriteBatchedStatements for better performance
String URL = "jdbc:mysql://localhost:3306/mydb?rewriteBatchedStatements=true";

// PostgreSQL: Use COPY for bulk inserts
// Oracle: Use INSERT ALL or bulk collect
```

---

## Connection Pooling

### Why Connection Pooling?

Creating database connections is expensive:
- TCP handshake
- SSL negotiation
- Authentication
- Memory allocation

Connection pools maintain reusable connections.

### HikariCP (Fastest Pool)

```xml
<dependency>
    <groupId>com.zaxxer</groupId>
    <artifactId>HikariCP</artifactId>
    <version>5.1.0</version>
</dependency>
```

```java
public class HikariCPExample {
    
    private static HikariDataSource dataSource;
    
    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3306/mydb");
        config.setUsername("root");
        config.setPassword("password");
        
        // Pool configuration
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(5);
        config.setIdleTimeout(300000);        // 5 minutes
        config.setConnectionTimeout(20000);   // 20 seconds
        config.setMaxLifetime(1200000);       // 20 minutes
        
        // Performance settings
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        
        dataSource = new HikariDataSource(config);
    }
    
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
```

### Spring Boot with HikariCP

```yaml
# application.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mydb
    username: root
    password: password
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      idle-timeout: 300000
      connection-timeout: 20000
      max-lifetime: 1200000
      pool-name: MyAppPool
```

### Connection Pool Sizing

**Formula:** `connections = (core_count * 2) + effective_spindle_count`

For SSDs: `connections = core_count * 2`

Example: 4-core server with SSD = 8 connections

---

## Best Practices

### 1. Always Use Try-with-Resources

```java
// ✅ Resources automatically closed
try (Connection conn = dataSource.getConnection();
     PreparedStatement pstmt = conn.prepareStatement(sql);
     ResultSet rs = pstmt.executeQuery()) {
    
    // Process results
}
```

### 2. Always Use PreparedStatement

```java
// ✅ Prevents SQL injection, improves performance
PreparedStatement pstmt = conn.prepareStatement(
    "SELECT * FROM users WHERE email = ?"
);
pstmt.setString(1, email);
```

### 3. Close Resources in Reverse Order

```java
// If not using try-with-resources
finally {
    if (rs != null) rs.close();      // First
    if (stmt != null) stmt.close();  // Second
    if (conn != null) conn.close();  // Last
}
```

### 4. Use Connection Pooling in Production

```java
// ✅ Never use DriverManager.getConnection() in production
DataSource dataSource = new HikariDataSource(config);
Connection conn = dataSource.getConnection();
```

### 5. Limit Result Sets

```java
// ✅ Use LIMIT/OFFSET for large tables
PreparedStatement pstmt = conn.prepareStatement(
    "SELECT * FROM products LIMIT ? OFFSET ?"
);
pstmt.setInt(1, pageSize);
pstmt.setInt(2, offset);
```

### 6. Use Appropriate Fetch Size

```java
// For large result sets
stmt.setFetchSize(100);  // Fetch 100 rows at a time
```

---

## Common Exceptions

| Exception | Cause | Solution |
|-----------|-------|----------|
| `SQLSyntaxErrorException` | Invalid SQL | Check SQL syntax |
| `SQLIntegrityConstraintViolationException` | Constraint violation | Check unique/foreign keys |
| `SQLTimeoutException` | Query timeout | Optimize query, increase timeout |
| `SQLTransientConnectionException` | Connection lost | Retry with backoff |
| `SQLException: Connection refused` | Database not running | Start database server |

---

## Interview Questions

### Q1: What is the difference between Statement and PreparedStatement?

**Answer:**
- **Statement**: Executes static SQL, vulnerable to SQL injection, compiled each time
- **PreparedStatement**: Pre-compiled, parameterized, SQL injection safe, better performance for repeated queries

### Q2: How does JDBC prevent SQL injection?

**Answer:** PreparedStatement uses parameterized queries where user input is treated as data, not SQL code. The database driver escapes special characters.

### Q3: Explain JDBC transaction management.

**Answer:**
1. Disable auto-commit: `conn.setAutoCommit(false)`
2. Execute multiple statements
3. Commit on success: `conn.commit()`
4. Rollback on failure: `conn.rollback()`
5. Re-enable auto-commit

### Q4: What is connection pooling and why is it important?

**Answer:** Connection pooling maintains a cache of database connections for reuse. It's important because:
- Creating connections is expensive (TCP, SSL, auth)
- Pools reduce latency
- Controls resource usage
- Prevents connection exhaustion

### Q5: What are the different ResultSet types?

**Answer:**
- **TYPE_FORWARD_ONLY**: Can only move forward (default, most efficient)
- **TYPE_SCROLL_INSENSITIVE**: Can scroll, doesn't see DB changes
- **TYPE_SCROLL_SENSITIVE**: Can scroll, sees DB changes

### Q6: How do you handle database NULL values in JDBC?

**Answer:**
```java
int value = rs.getInt("column");
if (rs.wasNull()) {
    // Handle NULL
}
// Or use wrapper classes:
Integer value = rs.getObject("column", Integer.class);
```

---

## Demo Projects

### demo-jdbc-basics
Basic JDBC operations: connections, CRUD, transactions

### demo-jdbc-connection-pool
HikariCP configuration and performance comparison

---

## Next Steps

After mastering JDBC fundamentals:
1. → [Advanced Hibernate](../02-hibernate-advanced/) - Caching, performance
2. → [NoSQL with MongoDB](../03-nosql-mongodb/) - Document databases
3. → [Redis Caching](../04-nosql-redis/) - In-memory data store
