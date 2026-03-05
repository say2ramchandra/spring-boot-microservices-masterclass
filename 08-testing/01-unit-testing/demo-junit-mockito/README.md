# Demo: Unit Testing with JUnit 5 and Mockito

This demo showcases comprehensive unit testing strategies using JUnit 5, Mockito, and AssertJ.

## 🎯 What's Covered

- ✅ Service layer testing with Mockito
- ✅ REST controller testing with MockMvc
- ✅ Repository testing with @DataJpaTest
- ✅ Parameterized tests
- ✅ Nested test classes
- ✅ Exception testing
- ✅ Argument capture
- ✅ Code coverage with JaCoCo

## 🏗️ Project Structure

```
src/
├── main/java/com/masterclass/testing/
│   ├── controller/
│   │   ├── ProductController.java          # REST endpoints
│   │   └── GlobalExceptionHandler.java     # Exception handling
│   ├── service/
│   │   ├── ProductService.java             # Business logic
│   │   └── DiscountService.java            # Discount calculations
│   ├── repository/
│   │   └── ProductRepository.java          # Data access
│   ├── model/
│   │   ├── Product.java                    # Entity
│   │   ├── Customer.java                   # Domain model
│   │   └── CustomerType.java               # Enum
│   ├── dto/
│   │   └── ProductDTO.java                 # Data transfer object
│   └── exception/
│       ├── ResourceNotFoundException.java
│       └── ValidationException.java
│
└── test/java/com/masterclass/testing/
    ├── service/
    │   ├── ProductServiceTest.java         # ⭐ Service unit tests
    │   └── DiscountServiceTest.java        # ⭐ TDD examples
    ├── controller/
    │   └── ProductControllerTest.java      # ⭐ MockMvc tests
    └── repository/
        └── ProductRepositoryTest.java      # ⭐ Repository tests
```

## 🚀 Running Tests

### Run All Tests
```bash
mvn test
```

### Run Specific Test Class
```bash
mvn test -Dtest=ProductServiceTest
mvn test -Dtest=DiscountServiceTest
```

### Run Specific Test Method
```bash
mvn test -Dtest=ProductServiceTest#createProduct_withValidInput_shouldReturnCreatedProduct
```

### Run Tests with Coverage
```bash
mvn clean test jacoco:report
```

## 📊 View Coverage Report

After running tests with coverage:

```bash
# Windows
start target/site/jacoco/index.html

# macOS/Linux
open target/site/jacoco/index.html
```

## 🧪 Test Classes Overview

### 1. ProductServiceTest
Tests the business logic layer with comprehensive Mockito examples:
- Creating, reading, updating, deleting products
- Validation logic
- Exception scenarios
- Search operations
- Argument capture

**Key Patterns:**
- AAA (Arrange-Act-Assert)
- Mocking with `@Mock` and `@InjectMocks`
- Verification with `verify()`
- Parameterized tests with `@ValueSource`

### 2. DiscountServiceTest
Demonstrates TDD approach with nested test classes:
- Discount calculations for different customer types
- Edge cases (zero price, negative values)
- Parameterized tests with `@CsvSource` and `@MethodSource`
- Nested test organization

**Key Patterns:**
- Nested test classes (`@Nested`)
- Multiple parameterized test approaches
- Edge case testing
- Business rule validation

### 3. ProductControllerTest
Tests REST endpoints using MockMvc:
- HTTP methods (GET, POST, PUT, DELETE)
- Request/response validation
- Status code verification
- JSON path assertions
- Validation error handling

**Key Patterns:**
- `@WebMvcTest` for controller testing
- MockMvc for HTTP testing
- JSON path assertions
- Testing validation errors

### 4. ProductRepositoryTest
Tests data access layer with H2:
- CRUD operations
- Custom query methods
- JPA features (timestamps, etc.)
- Edge cases

**Key Patterns:**
- `@DataJpaTest` for repository testing
- TestEntityManager for setup
- Testing custom queries
- Database assertions

## 📈 Expected Test Results

```
[INFO] Tests run: 50, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] Classes: 7
[INFO] Line Coverage: 85%
[INFO] Branch Coverage: 80%
[INFO] Method Coverage: 90%
```

## 🎓 Key Learnings

### 1. Unit Test Characteristics
- Fast execution (< 1 second per test)
- Isolated (no external dependencies)
- Repeatable (same result every time)
- Independent (tests don't affect each other)

### 2. Mockito Best Practices
```java
// ✅ DO: Mock external dependencies
@Mock
private ProductRepository productRepository;

// ✅ DO: Verify important interactions
verify(productRepository, times(1)).save(any(Product.class));

// ❌ DON'T: Mock everything
// ❌ DON'T: Test framework code
```

### 3. Test Naming
```java
// Pattern: methodName_scenario_expectedBehavior
void createProduct_validInput_shouldReturnProduct()
void getProduct_nonExistentId_shouldThrowException()
void calculateDiscount_premiumCustomer_shouldApply20Percent()
```

### 4. AssertJ Fluent Assertions
```java
assertThat(result)
    .isNotNull()
    .satisfies(dto -> {
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Laptop");
    });
```

## 🐛 Common Issues

### Issue: Tests fail with NullPointerException
**Solution:** Ensure `@ExtendWith(MockitoExtension.class)` is present

### Issue: Repository tests fail
**Solution:** Check `@DataJpaTest` annotation and H2 dependency

### Issue: MockMvc returns 404
**Solution:** Verify `@WebMvcTest(ControllerClass.class)` includes correct controller

## 📚 Additional Examples

### Parameterized Test Example
```java
@ParameterizedTest
@CsvSource({
    "PREMIUM, 100.00, 80.00",
    "VIP, 100.00, 70.00",
    "REGULAR, 100.00, 95.00"
})
void testDiscount(CustomerType type, BigDecimal price, BigDecimal expected) {
    Customer customer = new Customer("Test", type);
    BigDecimal result = discountService.calculateDiscount(customer, price);
    assertThat(result).isEqualByComparingTo(expected);
}
```

### Exception Testing Example
```java
@Test
void deleteProduct_nonExistent_shouldThrowException() {
    when(productRepository.existsById(999L)).thenReturn(false);
    
    assertThatThrownBy(() -> productService.deleteProduct(999L))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("Product not found");
}
```

## 🎯 Next Steps

1. ✅ Run all tests and verify they pass
2. ✅ Review test coverage report
3. ✅ Study different testing patterns used
4. ✅ Try adding your own test cases
5. ➡️ Move to Integration Testing (next module)

---

_"Testing leads to failure, and failure leads to understanding." - Burt Rutan_
