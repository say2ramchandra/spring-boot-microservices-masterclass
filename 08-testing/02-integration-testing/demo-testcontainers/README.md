# Demo: Integration Testing with TestContainers

Complete integration testing demonstration using TestContainers, PostgreSQL, and Redis.

## 🎯 What's Covered

- ✅ TestContainers with PostgreSQL
- ✅ Full-stack integration tests
- ✅ Database persistence verification
- ✅ REST API end-to-end testing
- ✅ Transaction management testing
- ✅ Search and query operations

## 🐳 Prerequisites

**Docker must be installed and running!**

```bash
# Verify Docker is running
docker --version
docker ps
```

## 🏗️ Project Structure

```
src/
├── main/java/com/masterclass/testing/
│   ├── controller/ProductController.java
│   ├── service/ProductService.java
│   ├── repository/ProductRepository.java
│   ├── model/Product.java
│   └── dto/ProductDTO.java
│
└── test/java/com/masterclass/testing/integration/
    └── ProductIntegrationTest.java      # ⭐ 15+ integration tests
```

## 🚀 Running Tests

### Run Integration Tests
```bash
# Run all integration tests
mvn verify

# Or use failsafe plugin directly
mvn failsafe:integration-test

# Run specific test
mvn verify -Dit.test=ProductIntegrationTest

# Skip unit tests, run only integration tests
mvn verify -DskipUnitTests
```

### Run Unit Tests Only
```bash
mvn test
```

## 📊 What Gets Tested

### 1. **Create Operations** (POST)
- Product creation with database persistence
- Validation of created IDs
- Timestamp verification
- Duplicate name handling

### 2. **Read Operations** (GET)
- Get all products
- Get product by ID
- 404 handling for non-existent products
- Database query verification

### 3. **Update Operations** (PUT)
- Product updates
- Timestamp updates (updated_at)
- Database persistence of changes

### 4. **Delete Operations** (DELETE)
- Product deletion
- Database cleanup verification
- 404 on deleted products

### 5. **Search Operations**
- Search by name (case-insensitive)
- Price range filtering
- Low stock detection
- Custom query methods

### 6. **Complete Workflows**
- Full product lifecycle (Create → Read → Update → Delete)
- Multi-step scenarios
- Transaction verification

## 🐳 TestContainers in Action

### Container Startup
```
🐳 PostgreSQL Container Started:
   JDBC URL: jdbc:postgresql://localhost:32768/testdb
   Username: test
   Image: postgres:15-alpine
```

### What Happens:
1. Test starts
2. Docker downloads `postgres:15-alpine` (first time only)
3. Container starts (~4 seconds)
4. Application connects to container
5. Tests execute
6. Container stops and removes automatically

## 📈 Expected Test Results

```
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.masterclass.testing.integration.ProductIntegrationTest
🐳 PostgreSQL Container Started:
   JDBC URL: jdbc:postgresql://localhost:32769/testdb
   Username: test

[INFO] Tests run: 15, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 18.5 s

🐳 PostgreSQL Container Stopped

[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 15, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] BUILD SUCCESS
```

## 🎓 Key Integration Test Features

### 1. Real Database Testing
```java
@Container
static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");
```
- Real PostgreSQL instance
- Actual SQL queries
- True transaction behavior
- Database constraints enforced

### 2. Dynamic Configuration
```java
@DynamicPropertySource
static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
}
```
- Automatically configures Spring Boot
- Uses container's random port
- No manual configuration needed

### 3. Database Verification
```java
// Test via API
mockMvc.perform(post("/api/products")...)
    .andExpect(status().isCreated());

// Verify in database
Product saved = productRepository.findById(id).orElseThrow();
assertThat(saved.getName()).isEqualTo("Expected Name");
```
- Tests HTTP layer AND database
- Verifies complete data flow
- Ensures persistence works correctly

### 4. Clean State Management
```java
@BeforeEach
void setUp() {
    productRepository.deleteAll(); // Fresh state per test
}
```
- Each test starts with clean database
- Tests are independent
- No test pollution

## 💡 Integration Testing Best Practices

### ✅ DO:
- Test complete user workflows
- Verify database state after operations
- Use real databases (PostgreSQL, not H2)
- Clean data between tests
- Test error scenarios (404, validation)

### ❌ DON'T:
- Test every edge case (use unit tests)
- Mock the database
- Share state between tests
- Forget to clean up test data
- Use `latest` tags for containers

## 🐛 Troubleshooting

### "Docker not running"
```bash
# Start Docker Desktop (Windows/Mac)
# Or start Docker daemon (Linux)
sudo systemctl start docker
```

### "Port already in use"
TestContainers automatically finds free ports. If this happens, you may have manually run containers. Clean them up:
```bash
docker ps -a
docker rm -f $(docker ps -aq)
```

### "Tests are slow"
First run downloads images (~30s). Subsequent runs are faster (~5-10s).

To speed up further:
```properties
# ~/.testcontainers.properties
testcontainers.reuse.enable=true
```

```java
postgres.withReuse(true); // In test code
```

### "OutOfMemory errors"
Increase Docker memory allocation:
- Docker Desktop → Settings → Resources → Memory → Increase to 4GB+

## 📚 Compared to Unit Tests

| Aspect | Unit Tests | Integration Tests (This Demo) |
|--------|-----------|-------------------------------|
| Database | H2 (in-memory) | PostgreSQL (real) |
| Speed | Very fast (ms) | Slower (seconds) |
| Scope | Single method | Full request cycle |
| Confidence | Medium | High |
| Mocking | Heavy | Minimal |
| Setup | Simple | Docker required |

## 🎯 Next Steps

After mastering this demo:

1. ✅ Run `mvn verify` and see all tests pass
2. ✅ Review `ProductIntegrationTest.java` carefully
3. ✅ Try adding your own integration test
4. ✅ Experiment with other containers (Redis, MongoDB)
5. ➡️ Move to Contract Testing (next module)

## 🔗 Additional Resources

- [TestContainers Official Docs](https://www.testcontainers.org/)
- [PostgreSQL Container Module](https://www.testcontainers.org/modules/databases/postgres/)
- [Spring Boot TestContainers Support](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing.testcontainers)

---

_"Integration tests give confidence that your components work together." - Martin Fowler_
