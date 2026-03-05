# End-to-End Testing with REST Assured

> **Test complete user journeys and API workflows**

## 📚 Overview

End-to-end (E2E) testing validates complete application flows from start to finish, testing the system as a whole from the user's perspective.

---

## 🎯 Learning Objectives

- Write E2E tests with REST Assured
- Test complete API workflows
- Validate multi-step processes
- Test real user scenarios
- Perform API integration testing

---

## 🧪 Testing Pyramid - E2E Tests

```
           /\
          /E2E\              ← YOU ARE HERE (5% of tests)
         /──────\
        /  API   \           
       /Integration\
      /──────────────\
     /   Unit Tests   \      
    /──────────────────\
```

**Why E2E Tests?**
- 🎭 Test real user workflows
- 🔗 Verify system integration
- 💼 Business scenario validation
- 🎯 Highest confidence level
- 📊 Complete data flow testing

---

## 🚀 REST Assured

REST Assured is a Java library that simplifies testing REST APIs with a fluent, readable syntax.

### Why REST Assured?

- ✅ **Fluent API** - Readable BDD-style syntax
- ✅ **JSON/XML Support** - Built-in parsers
- ✅ **Authentication** - OAuth, Basic, Bearer tokens
- ✅ **Validation** - JSON path, XML path
- ✅ **Integration** - Works with JUnit/TestNG

---

## 📝 REST Assured Syntax

### Basic Structure
```java
given()
    .contentType(ContentType.JSON)
    .body(requestBody)
when()
    .post("/api/products")
then()
    .statusCode(201)
    .body("id", notNullValue())
    .body("name", equalTo("Laptop"));
```

### Given-When-Then Pattern
```java
// GIVEN - Setup (headers, body, params)
given()
    .header("Authorization", "Bearer " + token)
    .queryParam("page", 1)
    .body(product)

// WHEN - Action (HTTP method + endpoint)
when()
    .post("/api/products")

// THEN - Assertions (status, body, headers)
then()
    .statusCode(201)
    .body("name", equalTo("Product"))
```

---

## 📦 Demo Project

### Simple REST Assured Test
```java
@Test
@DisplayName("Complete product lifecycle E2E test")
void productLifecycleE2E() {
    // 1. CREATE Product
    String productId = given()
        .contentType(ContentType.JSON)
        .body("""
            {
                "name": "E2E Test Laptop",
                "description": "For testing",
                "price": 999.99,
                "quantity": 10
            }
            """)
    .when()
        .post("/api/products")
    .then()
        .statusCode(201)
        .body("name", equalTo("E2E Test Laptop"))
        .body("price", equalTo(999.99f))
    .extract()
        .path("id").toString();

    // 2. READ Product
    given()
    .when()
        .get("/api/products/" + productId)
    .then()
        .statusCode(200)
        .body("id", equalTo(Integer.parseInt(productId)))
        .body("name", equalTo("E2E Test Laptop"));

    // 3. UPDATE Product
    given()
        .contentType(ContentType.JSON)
        .body("""
            {
                "id": %s,
                "name": "Updated Laptop",
                "description": "Updated",
                "price": 1099.99,
                "quantity": 5
            }
            """.formatted(productId))
    .when()
        .put("/api/products/" + productId)
    .then()
        .statusCode(200)
        .body("name", equalTo("Updated Laptop"))
        .body("price", equalTo(1099.99f));

    // 4. DELETE Product
    given()
    .when()
        .delete("/api/products/" + productId)
    .then()
        .statusCode(204);

    // 5. VERIFY Deletion
    given()
    .when()
        .get("/api/products/" + productId)
    .then()
        .statusCode(404);
}
```

---

## 🎯 E2E Testing Patterns

### 1. Happy Path Testing
```java
@Test
void happyPath_createOrderWithProduct() {
    // Create product
    String productId = createProduct("Laptop", 999.99);
    
    // Create order with product
    String orderId = createOrder(productId, 2);
    
    // Verify order created
    verifyOrder(orderId, "PENDING");
    
    // Process payment
    processPayment(orderId);
    
    // Verify order completed
    verifyOrder(orderId, "COMPLETED");
}
```

### 2. Error Scenario Testing
```java
@Test
void errorPath_createOrderWithInsufficientStock() {
    String productId = createProduct("Mouse", 29.99, 1);
    
    // Try to order more than available
    given()
        .body(orderRequest(productId, quantity = 10))
    .when()
        .post("/api/orders")
    .then()
        .statusCode(400)
        .body("message", containsString("Insufficient stock"));
}
```

### 3. Multi-Step Workflow
```java
@Test
void workflow_completeShoppingExperience() {
    // 1. Browse products
    List<String> products = browseProducts("laptop");
    assertThat(products).isNotEmpty();
    
    // 2. Add to cart
    String cartId = addToCart(products.get(0), 1);
    
    // 3. Proceed to checkout
    String orderId = checkout(cartId);
    
    // 4. Complete payment
    completePayment(orderId, paymentDetails);
    
    // 5. Verify order status
    verifyOrderStatus(orderId, "COMPLETED");
    
    // 6. Check inventory reduced
    verifyInventoryReduced(products.get(0));
}
```

---

## 💡 REST Assured Features

### JSON Path Validation
```java
.then()
    .body("products.size()", equalTo(5))
    .body("products[0].name", equalTo("Laptop"))
    .body("products.findAll { it.price > 100 }.size()", greaterThan(2));
```

### Response Extraction
```java
String name = given()
    .when().get("/api/products/1")
    .then().extract().path("name");

List<String> names = given()
    .when().get("/api/products")
    .then().extract().path("products.name");
```

### Request Specifications
```java
RequestSpecification requestSpec = new RequestSpecBuilder()
    .setBaseUri("http://localhost:8080")
    .setContentType(ContentType.JSON)
    .addHeader("Authorization", "Bearer " + token)
    .build();

given()
    .spec(requestSpec)
.when()
    .get("/api/products");
```

### Response Specifications
```java
ResponseSpecification responseSpec = new ResponseSpecBuilder()
    .expectStatusCode(200)
    .expectContentType(ContentType.JSON)
    .build();

given()
    .when().get("/api/products")
    .then().spec(responseSpec);
```

---

## 📊 E2E vs Other Tests

| Aspect | Unit | Integration | E2E |
|--------|------|-------------|-----|
| **Scope** | Method | Module | Full system |
| **Speed** | Fast (ms) | Medium (s) | Slow (s-min) |
| **Dependencies** | Mocked | Some real | All real |
| **Environment** | None | Docker | Full stack |
| **Confidence** | Low | Medium | High |
| **Debugging** | Easy | Medium | Hard |
| **Maintenance** | Low | Medium | High |

---

## 🎓 E2E Testing Best Practices

### ✅ DO:
- Test critical user journeys
- Use realistic data
- Test error scenarios
- Clean up test data
- Run in CI/CD pipeline
- Keep tests independent

### ❌ DON'T:
- Test every edge case
- Create too many E2E tests
- Let tests depend on each other
- Ignore flaky tests
- Skip cleanup
- Test unit-level logic

---

## 🔧 Advanced REST Assured

### File Upload
```java
given()
    .multiPart("file", new File("product-image.jpg"))
    .multiPart("name", "Product Name")
.when()
    .post("/api/products/upload")
.then()
    .statusCode(200);
```

### Authentication
```java
// Basic Auth
given()
    .auth().basic("username", "password")
.when()
    .get("/api/products");

// OAuth2
given()
    .auth().oauth2(accessToken)
.when()
    .get("/api/products");
```

### Custom Matchers
```java
.then()
    .body("price", new CustomMatcher<Float>("price validator") {
        @Override
        public boolean matches(Object item) {
            float price = (Float) item;
            return price > 0 && price < 10000;
        }
    });
```

---

## 🐛 Handling Flaky Tests

### Problem: Tests pass/fail randomly
```java
// ❌ BAD - Race condition
@Test
void test() {
    createProduct();
    // Product might not be ready yet!
    assertThat(getProducts()).hasSize(1);
}

// ✅ GOOD - Wait for condition
@Test
void test() {
    createProduct();
    await().atMost(5, SECONDS)
        .until(() -> getProducts().size() == 1);
}
```

### Use Awaitility
```java
await()
    .atMost(10, SECONDS)
    .pollInterval(500, MILLISECONDS)
    .until(() -> getOrderStatus(orderId).equals("COMPLETED"));
```

---

## 📈 Expected Results

```
[INFO] E2E Tests Summary:
[INFO] Tests run: 8, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] Test Scenarios:
[INFO] ✅ Complete product lifecycle
[INFO] ✅ Search and filter products
[INFO] ✅ Price range queries
[INFO] ✅ Low stock detection
[INFO] ✅ Error handling (404, 400)
[INFO] ✅ Data validation
[INFO] ✅ Multi-step workflows
[INFO] ✅ Concurrent operations
[INFO]
[INFO] Total time: 12.3s
```

---

## 🎯 When to Use E2E Tests

### Critical Workflows
- User registration → Login → Purchase
- Create → Update → Delete operations
- Multi-step processes
- Payment flows
- Authentication/Authorization

### **How Many E2E Tests?**
```
Total Tests: 100%
├── Unit: 70%
├── Integration: 20%
├── E2E: 5-10%
└── Manual: remaining
```

---

## 🔗 Resources

- [REST Assured Documentation](https://rest-assured.io/)
- [JSON Path Syntax](https://goessner.net/articles/JsonPath/)
- [E2E Testing Best Practices](https://martinfowler.com/articles/practical-test-pyramid.html)

---

_"E2E tests are expensive but invaluable for critical paths." - Test Pyramid Principle_
