# JDBC Basics Demo

> **Hands-on demonstration of pure JDBC operations**

## 📖 Overview

This demo project demonstrates core JDBC concepts including:
- Raw JDBC with `Connection`, `PreparedStatement`, `ResultSet`
- Spring `JdbcTemplate` as a cleaner alternative
- Transaction management
- Batch processing
- Connection pooling with HikariCP

## 🏗️ Project Structure

```
demo-jdbc-basics/
├── src/main/java/com/masterclass/jdbc/
│   ├── JdbcBasicsApplication.java     # Main application
│   ├── model/
│   │   ├── User.java                  # User record
│   │   └── Product.java               # Product record
│   ├── dao/
│   │   ├── UserDao.java               # Raw JDBC implementation
│   │   └── ProductDao.java            # JdbcTemplate implementation
│   ├── service/
│   │   └── TransactionDemoService.java # Transaction examples
│   └── runner/
│       └── JdbcDemoRunner.java        # Demo execution
├── src/main/resources/
│   ├── application.properties         # Configuration
│   ├── schema.sql                     # Table definitions
│   └── data.sql                       # Initial data
└── src/test/java/                     # Integration tests
```

## 🚀 Running the Demo

### 1. Run the Application

```bash
# From the demo-jdbc-basics directory
mvn spring-boot:run
```

Watch the console output to see:
- Raw JDBC CRUD operations (UserDao)
- JdbcTemplate operations (ProductDao)
- Transaction isolation level demos
- Batch insert operations

### 2. Access H2 Console

Open browser: http://localhost:8080/h2-console

Connection settings:
- JDBC URL: `jdbc:h2:mem:jdbcdemodb`
- Username: `sa`
- Password: (leave empty)

### 3. Run Tests

```bash
mvn test
```

## 🔍 Key Concepts Demonstrated

### Raw JDBC (UserDao)

```java
// Connection management with try-with-resources
try (Connection conn = dataSource.getConnection();
     PreparedStatement pstmt = conn.prepareStatement(sql)) {
    pstmt.setString(1, user.name());
    pstmt.executeUpdate();
}

// Handling NULL values
if (rs.wasNull()) {
    // Column was NULL
}
```

### JdbcTemplate (ProductDao)

```java
// Much cleaner!
jdbcTemplate.query(sql, productRowMapper, id);
jdbcTemplate.update(sql, param1, param2);
```

### Transactions

```java
conn.setAutoCommit(false);    // Start transaction
// ... operations ...
conn.commit();                 // Commit
// or conn.rollback();        // Rollback on error
```

### Batch Processing

```java
pstmt.addBatch();
// ... add more ...
pstmt.executeBatch();
```

## 📊 Comparing Approaches

| Feature | Raw JDBC | JdbcTemplate |
|---------|----------|--------------|
| **Boilerplate** | High | Low |
| **Exception Handling** | Manual | Automatic |
| **Resource Cleanup** | Manual | Automatic |
| **Control** | Full | Good |
| **Learning Curve** | Steeper | Easier |

## 🎯 Exercises

1. **Add a new method** to `UserDao` that finds users with NULL age
2. **Implement pagination** in `ProductDao.findAll()` using LIMIT/OFFSET
3. **Create a `CallableStatement` example** by adding a stored procedure
4. **Add a scrollable ResultSet** demo

## 📚 Next Steps

- [Connection Pooling Demo](../demo-jdbc-connection-pool/) - Deep dive into HikariCP
- [Hibernate Advanced](../../02-hibernate-advanced/) - ORM caching and optimization
