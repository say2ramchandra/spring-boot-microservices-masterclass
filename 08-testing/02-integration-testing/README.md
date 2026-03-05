# Integration Testing with TestContainers

> **Test with real dependencies using Docker containers**

## 📚 Overview

Integration testing verifies that different components work together correctly. TestContainers provides lightweight, disposable Docker containers for integration tests.

---

## 🎯 Learning Objectives

- Write integration tests with TestContainers
- Test with real databases (PostgreSQL, MySQL)
- Test with message brokers (RabbitMQ, Kafka)
- Test with caching layers (Redis)
- Test REST APIs end-to-end
- Manage test data and cleanup

---

## 🧪 Testing Pyramid - Integration Tests

```
           /\
          /E2E\              
         /──────\
        /  API   \           ← YOU ARE HERE (15-20% of tests)
       /Integration\
      /──────────────\
     /   Unit Tests   \      
    /──────────────────\
```

**Why Integration Tests?**
- 🔗 Test component interactions
- 🐘 Use real databases
- 📦 Test full request/response cycle
- 🎯 Catch integration issues early
- 🛡️ More confidence than unit tests

---

## 🐳 TestContainers Benefits

### Instead of Mocks, Use Real Services

❌ **Without TestContainers:**
```java
@Mock
private Database database; // Mocked, not real behavior
```

✅ **With TestContainers:**
```java
@Container
PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:15");
// Real PostgreSQL running in Docker!
```

### Key Benefits

1. **Real Dependencies** - Test against actual PostgreSQL, Redis, RabbitMQ
2. **Isolated Tests** - Each test run gets fresh containers
3. **CI/CD Ready** - Works anywhere Docker runs
4. **No Manual Setup** - Containers start automatically
5. **Clean State** - Containers destroyed after tests

---

## 📦 Demo Project Structure

```
demo-testcontainers/
├── pom.xml
├── docker-compose.yml                      # Optional: for manual testing
├── src/
│   ├── main/
│   │   └── java/com/masterclass/testing/
│   │       ├── TestingApplication.java
│   │       ├── controller/
│   │       │   └── ProductController.java
│   │       ├── service/
│   │       │   ├── ProductService.java
│   │       │   └── CacheService.java
│   │       ├── repository/
│   │       │   └── ProductRepository.java
│   │       ├── model/
│   │       │   └── Product.java
│   │       ├── dto/
│   │       │   └── ProductDTO.java
│   │       └── config/
│   │           └── CacheConfig.java
│   └── test/
│       └── java/com/masterclass/testing/
│           ├── integration/
│           │   ├── ProductIntegrationTest.java     # ⭐ Full stack test
│           │   ├── ProductRepositoryIT.java        # ⭐ PostgreSQL test
│           │   ├── CacheIntegrationTest.java       # ⭐ Redis test
│           │   └── MultiServiceIT.java             # ⭐ Multiple containers
│           └── config/
│               └── TestContainersConfiguration.java
```

---

## 🔧 TestContainer Types

### 1. Database Containers

```java
@Container
static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
    .withDatabaseName("testdb")
    .withUsername("test")
    .withPassword("test");
```

**Supported Databases:**
- PostgreSQL
- MySQL / MariaDB
- MongoDB
- Oracle
- MSSQL
- And more...

### 2. Message Broker Containers

```java
@Container
static GenericContainer<?> rabbitmq = new GenericContainer<>("rabbitmq:3-management")
    .withExposedPorts(5672, 15672);
```

### 3. Cache Containers

```java
@Container
static GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
    .withExposedPorts(6379);
```

### 4. Generic Containers

```java
@Container
static GenericContainer<?> myService = new GenericContainer<>("my-service:latest")
    .withExposedPorts(8080)
    .withEnv("ENV_VAR", "value");
```

---

## 🎯 Testing Patterns

### 1. Single Container Pattern

Test one component with one external dependency:

```java
@SpringBootTest
@Testcontainers
class ProductRepositoryIT {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");
    
    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
    
    @Test
    void testWithRealDatabase() {
        // Test with actual PostgreSQL
    }
}
```

### 2. Multiple Container Pattern

Test with several dependencies:

```java
@SpringBootTest
@Testcontainers
class MultiServiceIT {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");
    
    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7")
        .withExposedPorts(6379);
    
    @Container
    static GenericContainer<?> rabbitmq = new GenericContainer<>("rabbitmq:3")
        .withExposedPorts(5672);
    
    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        // Configure all services
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.redis.host", redis::getHost);
        registry.add("spring.rabbitmq.host", rabbitmq::getHost);
    }
}
```

### 3. Shared Container Pattern

Share containers across test classes for faster execution:

```java
public abstract class BaseIntegrationTest {
    
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
        .withReuse(true); // ⚠️ Requires testcontainers.reuse.enable=true
    
    static {
        postgres.start();
    }
    
    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
    }
}

class ProductIT extends BaseIntegrationTest {
    // Reuses postgres container
}
```

---

## 🚀 Quick Start

### 1. Navigate to Demo
```bash
cd 08-testing/02-integration-testing/demo-testcontainers
```

### 2. Ensure Docker is Running
```bash
docker --version
```

### 3. Run Integration Tests
```bash
# Run all integration tests
mvn verify

# Run specific integration test
mvn verify -Dit.test=ProductIntegrationTest

# Run with verbose output
mvn verify -Dit.test=ProductIntegrationTest -X
```

### 4. View Test Output
```bash
# Integration test reports
target/failsafe-reports/
```

---

## 📊 Test Execution Flow

```
1. Test starts
   ↓
2. TestContainers downloads Docker image (first time only)
   ↓
3. Container starts (PostgreSQL, Redis, etc.)
   ↓
4. Application connects to container
   ↓
5. Tests execute
   ↓
6. Container stops and removes
   ↓
7. Test completes
```

**First Run:** ~30 seconds (downloads images)  
**Subsequent Runs:** ~5-10 seconds (images cached)

---

## 💡 Best Practices

### 1. Use Alpine Images
```java
// ✅ GOOD - Smaller, faster
new PostgreSQLContainer<>("postgres:15-alpine")

// ❌ AVOID - Larger image
new PostgreSQLContainer<>("postgres:15")
```

### 2. Minimize Container Starts
```java
// ✅ GOOD - Static container shared across tests
static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(...);

// ❌ BAD - New container per test (slow!)
@Container
PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(...);
```

### 3. Clean Data Between Tests
```java
@BeforeEach
void setUp() {
    productRepository.deleteAll(); // Clean slate
}
```

### 4. Use Specific Tags
```java
// ✅ GOOD - Specific version
new PostgreSQLContainer<>("postgres:15.2-alpine")

// ⚠️ AVOID - Latest may break tests
new PostgreSQLContainer<>("postgres:latest")
```

### 5. Configure Timeouts
```java
@Test
@Timeout(value = 30, unit = TimeUnit.SECONDS)
void testWithTimeout() {
    // Fails if takes > 30 seconds
}
```

---

## 🐛 Troubleshooting

### Issue: "Docker not found"
```bash
# Ensure Docker is running
docker ps

# On Windows, check Docker Desktop is started
```

### Issue: "Port already in use"
```java
// Solution: Use random ports
@Container
static GenericContainer<?> redis = new GenericContainer<>("redis:7")
    .withExposedPorts(6379); // TestContainers maps to random host port
```

### Issue: Tests are slow
```bash
# Enable container reuse (in ~/.testcontainers.properties):
testcontainers.reuse.enable=true

# Then in code:
postgres.withReuse(true);
```

### Issue: Out of memory
```java
// Solution: Limit container resources
@Container
static GenericContainer<?> container = new GenericContainer<>("image")
    .withCreateContainerCmdModifier(cmd -> 
        cmd.getHostConfig().withMemory(512 * 1024 * 1024L)); // 512MB
```

---

## 📚 Comparison: Unit vs Integration Tests

| Aspect | Unit Tests | Integration Tests |
|--------|-----------|-------------------|
| **Speed** | Very fast (ms) | Slower (seconds) |
| **Scope** | Single component | Multiple components |
| **Dependencies** | Mocked | Real |
| **Database** | Mocked/H2 | Real PostgreSQL |
| **Isolation** | Complete | Partial |
| **Confidence** | Medium | High |
| **Count** | Many (100s) | Some (10s) |

---

## 🎓 When to Use Integration Tests

### ✅ Do Use Integration Tests For:
- Database query correctness
- Transaction management
- API endpoint contracts
- Service-to-service communication
- Cache behavior
- Message broker interactions

### ❌ Don't Use Integration Tests For:
- Simple business logic (use unit tests)
- All possible scenarios (use unit tests)
- UI testing (use E2E tests)
- Performance testing (use dedicated tools)

---

## 📈 Expected Results

```
[INFO] Integration Tests Summary:
[INFO] Tests run: 15, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] Container startup times:
[INFO] - PostgreSQL: 4.2s
[INFO] - Redis: 2.1s
[INFO] - RabbitMQ: 5.3s
[INFO] 
[INFO] Total time: 18.5s
```

---

## 🔗 Additional Resources

- [TestContainers Documentation](https://www.testcontainers.org/)
- [TestContainers Spring Boot](https://www.testcontainers.org/modules/databases/postgres/)
- [Integration Testing Best Practices](https://martinfowler.com/bliki/IntegrationTest.html)

---

_Test with real dependencies, deploy with confidence! 🐳_
