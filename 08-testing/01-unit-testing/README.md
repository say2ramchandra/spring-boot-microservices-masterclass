# Unit Testing with JUnit 5 and Mockito

> **Master unit testing fundamentals for microservices**

## 📚 Overview

Unit testing focuses on testing individual components in isolation. This section covers JUnit 5, Mockito, and best practices for writing effective unit tests.

---

## 🎯 Learning Objectives

- Write unit tests with JUnit 5
- Mock dependencies with Mockito
- Test REST controllers with MockMvc
- Use parameterized and nested tests
- Apply TDD principles
- Achieve high test coverage

---

## 🧪 Key Concepts

### Testing Pyramid - Unit Tests

```
           /\
          /E2E\              
         /──────\
        /  API   \           
       /Integration\
      /──────────────\
     /   Unit Tests   \      ← YOU ARE HERE (70-80% of tests)
    /──────────────────\
```

**Why Unit Tests?**
- ⚡ Fast execution
- 💰 Low cost
- 🎯 Pinpoint failures
- 🔄 Quick feedback
- 📝 Living documentation

---

## 🔧 Testing Tools

### 1. JUnit 5 (Jupiter)

The foundation for unit testing in Java:

```java
@Test                           // Basic test
@DisplayName("Custom name")     // Readable test name
@ParameterizedTest             // Data-driven tests
@RepeatedTest(5)               // Repeat tests
@Timeout(100)                  // Performance tests
@Disabled("Not ready")         // Skip tests
```

### 2. Mockito

Mock framework for isolating dependencies:

```java
@Mock                           // Create mock
@InjectMocks                    // Inject mocks
@Spy                            // Partial mock
@Captor                         // Argument capture

when().thenReturn()            // Stub behavior
verify()                       // Verify interactions
```

### 3. AssertJ

Fluent assertions for readable tests:

```java
assertThat(actual)
    .isNotNull()
    .isEqualTo(expected)
    .hasSize(5)
    .contains("item");
```

---

## 📦 Demo Project Structure

```
demo-junit-mockito/
├── pom.xml
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/masterclass/testing/
│   │           ├── TestingApplication.java
│   │           ├── controller/
│   │           │   └── ProductController.java
│   │           ├── service/
│   │           │   ├── ProductService.java
│   │           │   └── DiscountService.java
│   │           ├── repository/
│   │           │   └── ProductRepository.java
│   │           ├── model/
│   │           │   ├── Product.java
│   │           │   └── Customer.java
│   │           ├── dto/
│   │           │   └── ProductDTO.java
│   │           └── exception/
│   │               ├── ResourceNotFoundException.java
│   │               └── ValidationException.java
│   └── test/
│       └── java/
│           └── com/masterclass/testing/
│               ├── service/
│               │   ├── ProductServiceTest.java
│               │   └── DiscountServiceTest.java
│               ├── controller/
│               │   └── ProductControllerTest.java
│               └── repository/
│                   └── ProductRepositoryTest.java
```

---

## 🧪 Testing Patterns

### 1. AAA Pattern (Arrange-Act-Assert)

```java
@Test
void createProduct_validInput_shouldReturnProduct() {
    // ARRANGE (Given) - Setup test data and mocks
    ProductDTO input = new ProductDTO(null, "Laptop", "Gaming", 
        new BigDecimal("999.99"), 10);
    Product savedProduct = new Product(1L, "Laptop", "Gaming", 
        new BigDecimal("999.99"), 10);
    when(productRepository.save(any(Product.class)))
        .thenReturn(savedProduct);
    
    // ACT (When) - Execute the method under test
    ProductDTO result = productService.createProduct(input);
    
    // ASSERT (Then) - Verify the results
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(1L);
    assertThat(result.getName()).isEqualTo("Laptop");
    
    // Verify mock interactions
    verify(productRepository, times(1)).save(any(Product.class));
}
```

### 2. Test Naming Convention

```
methodName_scenario_expectedBehavior

Examples:
- createProduct_validInput_shouldReturnProduct
- getProduct_nonExistentId_shouldThrowException
- calculateDiscount_premiumCustomer_shouldApply20Percent
```

### 3. Given-When-Then (BDD Style)

```java
@Test
@DisplayName("Given valid product, When creating, Then should return saved product")
void testCreateProduct() {
    // Given
    ProductDTO input = createValidProductDTO();
    Product savedProduct = createValidProduct();
    given(productRepository.save(any())).willReturn(savedProduct);
    
    // When
    ProductDTO result = productService.createProduct(input);
    
    // Then
    then(result).isNotNull();
    then(productRepository).should(times(1)).save(any());
}
```

---

## 🎯 What to Test

### ✅ DO Test

1. **Business Logic**
   ```java
   @Test
   void calculateDiscount_premiumCustomer_shouldApply20Percent() {
       // Test your discount calculation logic
   }
   ```

2. **Validation Logic**
   ```java
   @Test
   void createProduct_invalidName_shouldThrowException() {
       // Test input validation
   }
   ```

3. **Edge Cases**
   ```java
   @Test
   void calculatePrice_zeroQuantity_shouldReturnZero() {
       // Test boundary conditions
   }
   ```

4. **Error Handling**
   ```java
   @Test
   void getProduct_nonExistent_shouldThrowNotFoundException() {
       // Test exception scenarios
   }
   ```

### ❌ DON'T Test

1. **Framework Code** - Don't test Spring, JPA, etc.
2. **Getters/Setters** - Unless they contain logic
3. **Private Methods** - Test through public API
4. **External APIs** - Use integration tests instead

---

## 🚀 Quick Start

### 1. Navigate to Demo
```bash
cd 08-testing/01-unit-testing/demo-junit-mockito
```

### 2. Run Tests
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=ProductServiceTest

# Run with coverage
mvn clean test jacoco:report
```

### 3. View Coverage Report
```bash
# Open in browser
start target/site/jacoco/index.html  # Windows
open target/site/jacoco/index.html   # macOS
```

---

## 📊 Example Test Results

```
[INFO] Tests run: 15, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] Coverage Summary:
[INFO] - Line Coverage: 87%
[INFO] - Branch Coverage: 82%
[INFO] - Method Coverage: 90%
```

---

## 💡 Best Practices

### 1. Test Independence
```java
// ✅ GOOD - Each test is independent
@BeforeEach
void setUp() {
    productService = new ProductService(productRepository);
}

// ❌ BAD - Tests sharing state
static Product sharedProduct; // Don't do this!
```

### 2. Meaningful Assertions
```java
// ✅ GOOD - Clear intent
assertThat(result.getPrice())
    .as("Price should include tax")
    .isEqualByComparingTo(new BigDecimal("119.99"));

// ❌ BAD - Unclear
assertTrue(result.getPrice().compareTo(new BigDecimal("100")) > 0);
```

### 3. One Assertion Per Test (Generally)
```java
// ✅ GOOD - Focused test
@Test
void createProduct_shouldSetCorrectPrice() {
    ProductDTO result = productService.createProduct(input);
    assertThat(result.getPrice()).isEqualByComparingTo(expectedPrice);
}

// ⚠️ OK - Related assertions on same object
@Test
void createProduct_shouldSetAllFields() {
    ProductDTO result = productService.createProduct(input);
    assertThat(result)
        .hasFieldOrPropertyWithValue("name", "Laptop")
        .hasFieldOrPropertyWithValue("price", new BigDecimal("999.99"));
}
```

### 4. Mock Only Direct Dependencies
```java
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock
    private ProductRepository productRepository; // ✅ Direct dependency
    
    @InjectMocks
    private ProductService productService;
    
    // Don't mock database, HTTP client, etc.
}
```

---

## 📚 Additional Resources

- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [AssertJ Documentation](https://assertj.github.io/doc/)
- [Test-Driven Development](https://martinfowler.com/bliki/TestDrivenDevelopment.html)

---

_Write tests. Not too many. Mostly integration. But definitely unit tests! ✅_
