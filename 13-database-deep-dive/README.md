# Module 13: Database Deep Dive

> **Master SQL, NoSQL, and database patterns for enterprise applications**

## 📚 Module Overview

This module covers database fundamentals from raw JDBC to advanced NoSQL patterns. You'll learn how to work with relational databases at the lowest level, optimize Hibernate/JPA, and leverage NoSQL databases like MongoDB and Redis.

---

## 🎯 Learning Objectives

- ✅ Master JDBC fundamentals (connections, statements, transactions)
- ✅ Understand connection pooling with HikariCP
- ✅ Optimize Hibernate with caching and batch operations
- ✅ Work with MongoDB for document storage
- ✅ Implement Redis caching and session management
- ✅ Apply polyglot persistence patterns

---

## 📦 Module Structure

```
13-database-deep-dive/
├── README.md
├── 01-jdbc-fundamentals/           ✅ COMPLETE
│   ├── README.md                   # JDBC concepts & connection pooling guide
│   ├── demo-jdbc-basics/           # Pure JDBC operations (port 8080)
│   └── demo-jdbc-connection-pool/  # HikariCP & metrics (port 8081)
├── 02-hibernate-advanced/          ✅ COMPLETE
│   ├── README.md                   # L1/L2 caching, N+1 solutions
│   ├── demo-hibernate-caching/     # Ehcache L2 cache demo (port 8082)
│   └── demo-hibernate-performance/ # N+1, batch fetching (port 8083)
├── 03-mongodb/                     ✅ COMPLETE
│   ├── README.md                   # MongoDB & Spring Data guide
│   └── demo-mongodb-basics/        # CRUD, MongoTemplate (port 8084)
└── 04-redis/                       ✅ COMPLETE
    ├── README.md                   # Redis data structures & caching
    └── demo-redis-basics/          # All Redis operations (port 8086)
```

---

## 🔄 Technology Comparison

| Feature | JDBC | JPA/Hibernate | MongoDB | Redis |
|---------|------|---------------|---------|-------|
| **Type** | Relational | ORM | Document | Key-Value |
| **Query** | SQL | JPQL/HQL | JSON/Aggregation | Commands |
| **Schema** | Fixed | Mapped | Flexible | None |
| **ACID** | Full | Full | Per-Document | Partial |
| **Speed** | Fast | Medium | Fast | Very Fast |
| **Use Case** | Legacy/Control | Standard Apps | Flexible Data | Caching |

---

## 🏗️ When to Use What

### JDBC
- Legacy system integration
- Maximum performance control
- Complex stored procedures
- Batch processing with fine-tuned SQL

### JPA/Hibernate
- Standard CRUD applications
- Object-oriented domain model
- Automatic schema generation
- Built-in caching

### MongoDB
- Flexible/evolving schemas
- Document-centric data
- Horizontal scaling needs
- Geospatial queries

### Redis
- Caching
- Session storage
- Real-time analytics
- Pub/Sub messaging

---

## 📈 Learning Path

```
Week 1: JDBC & Connection Pooling
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
├─ Day 1-2: JDBC Basics
│   └─ Connection, Statement, ResultSet
├─ Day 3-4: Advanced JDBC
│   └─ PreparedStatement, Transactions, Batch
└─ Day 5: Connection Pooling
    └─ HikariCP configuration & tuning

Week 2: Hibernate Deep Dive
━━━━━━━━━━━━━━━━━━━━━━━━━━━
├─ Day 1-2: Caching Strategies
│   └─ First-level, Second-level, Query cache
├─ Day 3-4: Performance Optimization
│   └─ N+1 problem, Batch fetching, Lazy loading
└─ Day 5: Advanced Features
    └─ Native queries, Stored procedures

Week 3: NoSQL Databases
━━━━━━━━━━━━━━━━━━━━━━━
├─ Day 1-3: MongoDB
│   └─ Documents, Collections, Aggregation
└─ Day 4-5: Redis
    └─ Data structures, Caching, Sessions

Week 4: Patterns & Integration
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
└─ Day 1-5: Multi-Database Patterns
    └─ Polyglot persistence, CQRS basics
```

---

## 🛠️ Prerequisites

- ✅ Java 17+
- ✅ Maven 3.8+
- ✅ Basic SQL knowledge
- ✅ Completed Module 03 (Spring Boot Fundamentals)

> **Note**: All demos use embedded databases (H2, Flapdoodle MongoDB, ozimov Redis) - no Docker required!

---

## 🚀 Quick Start

```bash
# JDBC Basics (port 8080)
cd 01-jdbc-fundamentals/demo-jdbc-basics
mvn spring-boot:run

# JDBC Connection Pool (port 8081)
cd 01-jdbc-fundamentals/demo-jdbc-connection-pool
mvn spring-boot:run

# Hibernate Caching (port 8082)
cd 02-hibernate-advanced/demo-hibernate-caching
mvn spring-boot:run

# Hibernate Performance (port 8083)
cd 02-hibernate-advanced/demo-hibernate-performance
mvn spring-boot:run

# MongoDB Basics (port 8084) - uses embedded MongoDB
cd 03-mongodb/demo-mongodb-basics
mvn spring-boot:run

# Redis Basics (port 8086) - uses embedded Redis
cd 04-redis/demo-redis-basics
mvn spring-boot:run
```

### All Demos Use Embedded Databases
- **H2**: JDBC and Hibernate demos (zero setup)
- **Embedded MongoDB**: Flapdoodle (zero setup)
- **Embedded Redis**: ozimov (zero setup)

---

## 📚 Resources

### Official Documentation
- [JDBC Tutorial](https://docs.oracle.com/javase/tutorial/jdbc/)
- [HikariCP](https://github.com/brettwooldridge/HikariCP)
- [Hibernate ORM](https://hibernate.org/orm/documentation/)
- [MongoDB Manual](https://www.mongodb.com/docs/manual/)
- [Redis Documentation](https://redis.io/docs/)

### Books
- "High-Performance Java Persistence" by Vlad Mihalcea
- "MongoDB: The Definitive Guide" by Shannon Bradshaw
- "Redis in Action" by Josiah Carlson
