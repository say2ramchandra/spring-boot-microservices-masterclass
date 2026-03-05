# Demo: E2E Testing with REST Assured

Complete end-to-end API testing demonstration using REST Assured with fluent, BDD-style syntax.

## 🎯 What's Covered

- ✅ Complete CRUD workflows
- ✅ REST Assured fluent API
- ✅ JSON path validation
- ✅ Multi-step scenarios
- ✅ Error handling tests
- ✅ Data extraction
- ✅ Response validation

## 🏗️ Project Structure

```
src/
├── main/java/com/masterclass/testing/
│   ├── TestingApplication.java
│   ├── controller/ProductController.java
│   ├── service/ProductService.java
│   ├── repository/ProductRepository.java
│   ├── model/Product.java
│   └── dto/ProductDTO.java
│
└── test/java/com/masterclass/testing/e2e/
    └── ProductE2ETest.java      # ⭐ REST Assured E2E tests
```

## 🚀 Running Tests

```bash
# Start the application first
mvn spring-boot:run

# Then in another terminal, run tests
mvn test

# Or run application and tests together
mvn clean spring-boot:test-run
```

## 📊 What Gets Tested

### Complete Lifecycle Test
1. **Create** product via POST
2. **Read** product via GET
3. **Update** product via PUT
4. **Delete** product via DELETE
5. **Verify** deletion (404)

### Search Operations
- Search by name (case-insensitive)
- Filter by price range
- Find low stock products

### Error Scenarios
- 404 for non-existent products
- 400 for validation errors
- Duplicate name handling

### Data Validation
- JSON structure
- Field types
- Value ranges
- Required fields

## 💡 REST Assured Examples

### Basic Test
```java
given()
    .contentType(ContentType.JSON)
when()
    .get("/api/products")
then()
    .statusCode(200)
    .body("size()", greaterThan(0));
```

### With Request Body
```java
ProductDTO product = new ProductDTO(...);

given()
    .contentType(ContentType.JSON)
    .body(product)
when()
    .post("/api/products")
then()
    .statusCode(201)
    .body("id", notNullValue())
    .body("name", equalTo(product.getName()));
```

### Extract Response
```java
String productId = given()
    .contentType(ContentType.JSON)
    .body(product)
when()
    .post("/api/products")
then()
    .statusCode(201)
.extract()
    .path("id").toString();
```

## 📈 Expected Results

```
[INFO] Tests run: 10, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] Test Scenarios Covered:
[INFO] ✅ Complete product lifecycle (CRUD)
[INFO] ✅ Search products by name
[INFO] ✅ Filter by price range
[INFO] ✅ Find low stock products
[INFO] ✅ Handle 404 errors
[INFO] ✅ Validate input  
[INFO] ✅ Extract and reuse data
[INFO] ✅ Multi-step workflows
[INFO]
[INFO] BUILD SUCCESS
```

## 🎓 Key Learnings

### 1. Fluent API Style
REST Assured uses BDD-style given-when-then:
- **given()** - Setup (headers, body, params)
- **when()** - Action (HTTP call)
- **then()** - Assertions

### 2. JSON Path
```java
.body("products[0].name", equalTo("Laptop"))
.body("products.size()", equalTo(5))
.body("products.find { it.price > 100 }.name", equalTo("Laptop"))
```

### 3. Hamcrest Matchers
```java
equalTo(), notNullValue(), greaterThan(),
hasSize(), containsString(), startsWith()
```

### 4. Response Extraction
```java
String value = extract().path("field");
List<String> values = extract().path("items.name");
```

## 🐛 Troubleshooting

### "Connection refused"
Make sure the application is running:
```bash
mvn spring-boot:run
```

### "Port 8080 already in use"
Change port in application.properties:
```properties
server.port=8081
```

Then update REST Assured base URI in tests.

## 🎯 Next Steps

1. ✅ Review ProductE2ETest.java
2. ✅ Run tests and verify they pass
3. ✅ Try modifying test scenarios
4. ✅ Add your own E2E tests
5. ✅ Explore REST Assured documentation

## 🔗 Resources

- [REST Assured Docs](https://rest-assured.io/)
- [Hamcrest Matchers](http://hamcrest.org/JavaHamcrest/javadoc/2.2/)
- [JSON Path Syntax](https://goessner.net/articles/JsonPath/)

---

_"E2E tests verify the system works as users expect." - Software Testing Principle_
