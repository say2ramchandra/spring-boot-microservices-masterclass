# Module 08: Testing Microservices

> **Master testing strategies from unit to end-to-end**

## 📚 Module Overview

Learn comprehensive testing strategies for microservices including unit tests, integration tests, contract testing, and end-to-end testing.

---

## 🎯 Learning Objectives

- ✅ Write effective unit tests with JUnit 5 and Mockito
- ✅ Implement integration tests with TestContainers
- ✅ Perform contract testing with Spring Cloud Contract
- ✅ Create end-to-end tests
- ✅ Achieve high test coverage
- ✅ Apply test-driven development (TDD)
- ✅ Test asynchronous and event-driven systems

---

## 🧪 Testing Pyramid

```
           /\
          /E2E\              ← Few (Slow, Expensive)
         /──────\
        /  API   \           ← Some (Medium speed)
       /Integration\
      /──────────────\
     /   Unit Tests   \      ← Many (Fast, Cheap)
    /──────────────────\
```

**Principle**: More unit tests, fewer integration tests, even fewer E2E tests

---

## 📦 Module Structure

```
08-testing/
├── README.md
├── 01-unit-testing/
│   ├── README.md
│   └── demo-junit-mockito/
├── 02-integration-testing/
│   ├── README.md
│   └── demo-testcontainers/
├── 03-contract-testing/
│   ├── README.md
│   └── demo-spring-cloud-contract/
└── 04-e2e-testing/
    ├── README.md
    └── demo-rest-assured/
```

---

## 🔬 Unit Testing

### JUnit 5 Basics

```java
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    
    @Mock
    private ProductRepository productRepository;
    
    @InjectMocks
    private ProductService productService;
    
    @Test
    @DisplayName("Should create product successfully")
    void createProduct_validInput_shouldReturnProduct() {
        // Arrange (Given)
        ProductDTO productDTO = new ProductDTO(
            null, "Laptop", "Gaming laptop", new BigDecimal("999.99"), 10
        );
        Product product = new Product(
            1L, "Laptop", "Gaming laptop", new BigDecimal("999.99"), 10
        );
        
        when(productRepository.save(any(Product.class)))
            .thenReturn(product);
        
        // Act (When)
        ProductDTO result = productService.createProduct(productDTO);
        
        // Assert (Then)
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Laptop");
        
        verify(productRepository, times(1)).save(any(Product.class));
    }
    
    @Test
    @DisplayName("Should throw exception when product not found")
    void getProduct_nonExistentId_shouldThrowException() {
        // Arrange
        Long productId = 999L;
        when(productRepository.findById(productId))
            .thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(
            ResourceNotFoundException.class,
            () -> productService.getProductById(productId)
        );
        
        verify(productRepository, times(1)).findById(productId);
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"", "  ", "a"})
    @DisplayName("Should reject invalid product names")
    void createProduct_invalidName_shouldThrowException(String invalidName) {
        ProductDTO productDTO = new ProductDTO(
            null, invalidName, "Description", new BigDecimal("99.99"), 10
        );
        
        assertThrows(
            ValidationException.class,
            () -> productService.createProduct(productDTO)
        );
    }
    
    @Test
    @Timeout(value = 100, unit = TimeUnit.MILLISECONDS)
    @DisplayName("Should complete within 100ms")
    void getAllProducts_shouldBeF() {
        // Test performance
        when(productRepository.findAll())
            .thenReturn(List.of(new Product()));
        
        List<ProductDTO> products = productService.getAllProducts();
        
        assertThat(products).isNotEmpty();
    }
}
```

### Testing REST Controllers

```java
@WebMvcTest(ProductController.class)
class ProductControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private ProductService productService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    void getAllProducts_shouldReturn200WithProductList() throws Exception {
        // Arrange
        List<ProductDTO> products = Arrays.asList(
            new ProductDTO(1L, "Product 1", "Desc 1", new BigDecimal("99.99"), 10),
            new ProductDTO(2L, "Product 2", "Desc 2", new BigDecimal("149.99"), 5)
        );
        when(productService.getAllProducts()).thenReturn(products);
        
        // Act & Assert
        mockMvc.perform(get("/api/products")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].name").value("Product 1"))
            .andExpect(jsonPath("$[1].price").value(149.99));
    }
    
    @Test
    void createProduct_validInput_shouldReturn201() throws Exception {
        // Arrange
        ProductDTO inputDTO = new ProductDTO(
            null, "New Product", "Description", new BigDecimal("99.99"), 10
        );
        ProductDTO outputDTO = new ProductDTO(
            1L, "New Product", "Description", new BigDecimal("99.99"), 10
        );
        
        when(productService.createProduct(any(ProductDTO.class)))
            .thenReturn(outputDTO);
        
        // Act & Assert
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDTO)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("New Product"));
    }
    
    @Test
    void createProduct_invalidInput_shouldReturn400() throws Exception {
        // Invalid product (no name)
        ProductDTO invalidDTO = new ProductDTO(
            null, "", "Description", new BigDecimal("99.99"), 10
        );
        
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors.name").exists());
    }
    
    @Test
    void getProduct_nonExistent_shouldReturn404() throws Exception {
        when(productService.getProductById(999L))
            .thenThrow(new ResourceNotFoundException("Product not found"));
        
        mockMvc.perform(get("/api/products/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Product not found"));
    }
}
```

---

## 🔄 Integration Testing

### TestContainers Setup

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class ProductIntegrationTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");
    
    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
        .withExposedPorts(6379);
    
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.redis.host", redis::getHost);
        registry.add("spring.redis.port", () -> redis.getMappedPort(6379));
    }
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private ProductRepository productRepository;
    
    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
    }
    
    @Test
    void createProduct_fullFlow_shouldPersistToDatabase() {
        // Arrange
        ProductDTO productDTO = new ProductDTO(
            null, "Integration Test Product", 
            "Testing with real database", 
            new BigDecimal("199.99"), 15
        );
        
        // Act
        ResponseEntity<ProductDTO> response = restTemplate.postForEntity(
            "/api/products",
            productDTO,
            ProductDTO.class
        );
        
        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
        
        // Verify in database
        Product savedProduct = productRepository.findById(response.getBody().getId())
            .orElseThrow();
        assertThat(savedProduct.getName()).isEqualTo("Integration Test Product");
    }
    
    @Test
    void getAllProducts_withMultipleProducts_shouldReturnAll() {
        // Arrange - Create test data
        Product product1 = new Product(null, "Product 1", "Desc 1", 
            new BigDecimal("99.99"), 10, null, null);
        Product product2 = new Product(null, "Product 2", "Desc 2", 
            new BigDecimal("149.99"), 5, null, null);
        productRepository.saveAll(Arrays.asList(product1, product2));
        
        // Act
        ResponseEntity<ProductDTO[]> response = restTemplate.getForEntity(
            "/api/products",
            ProductDTO[].class
        );
        
        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
    }
}
```

### Testing with Multiple Services

```java
@SpringBootTest
@Testcontainers
class OrderServiceIntegrationTest {
    
    @Container
    static Network network = Network.newNetwork();
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
        .withNetwork(network);
    
    @Container
    static GenericContainer<?> productService = new GenericContainer<>("product-service:latest")
        .withNetwork(network)
        .withExposedPorts(8080)
        .dependsOn(postgres);
    
    @Container
    static GenericContainer<?> rabbitMq = new GenericContainer<>("rabbitmq:3-management")
        .withNetwork(network)
        .withExposedPorts(5672, 15672);
    
    @Test
    void createOrder_shouldCallProductServiceAndPublishEvent() {
        // Test order creation flow across services
    }
}
```

---

## 📝 Contract Testing

### Provider Side (Product Service)

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductContractBase {
    
    @LocalServerPort
    private int port;
    
    @Autowired
    private ProductService productService;
    
    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
        
        // Mock data for contract tests
        ProductDTO mockProduct = new ProductDTO(
            1L, "Test Product", "Test Description",
            new BigDecimal("99.99"), 10
        );
        
        when(productService.getProductById(1L)).thenReturn(mockProduct);
    }
}
```

**Contract Definition** (YAML):
```yaml
# src/test/resources/contracts/shouldReturnProductById.yml
request:
  method: GET
  url: /api/products/1
  
response:
  status: 200
  headers:
    Content-Type: application/json
  body:
    id: 1
    name: "Test Product"
    description: "Test Description"
    price: 99.99
    quantity: 10
```

### Consumer Side (Order Service)

```java
@SpringBootTest
@AutoConfigureStubRunner(
    ids = "com.masterclass:product-service:+:stubs:8081",
    stubsMode = StubRunnerProperties.StubsMode.LOCAL
)
class OrderServiceContractTest {
    
    @Autowired
    private OrderService orderService;
    
    @Test
    void createOrder_shouldCallProductService() {
        // Product service stub is automatically configured
        // Test order creation that calls product service
        
        CreateOrderRequest request = new CreateOrderRequest(1L, 1L, 2);
        OrderDTO order = orderService.createOrder(request);
        
        assertThat(order).isNotNull();
        assertThat(order.getProductId()).isEqualTo(1L);
    }
}
```

---

## 🌐 End-to-End Testing

### REST Assured

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductE2ETest {
    
    private static Long createdProductId;
    
    @BeforeAll
    static void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
    }
    
    @Test
    @Order(1)
    void completeProductLifecycle_shouldWork() {
        // 1. Create product
        ProductDTO newProduct = new ProductDTO(
            null, "E2E Test Product", "E2E Testing",
            new BigDecimal("299.99"), 20
        );
        
        createdProductId = given()
            .contentType(ContentType.JSON)
            .body(newProduct)
        .when()
            .post("/api/products")
        .then()
            .statusCode(201)
            .body("name", equalTo("E2E Test Product"))
            .body("price", equalTo(299.99f))
        .extract()
            .path("id");
        
        // 2. Get product
        given()
        .when()
            .get("/api/products/" + createdProductId)
        .then()
            .statusCode(200)
            .body("name", equalTo("E2E Test Product"));
        
        // 3. Update product
        ProductDTO updatedProduct = new ProductDTO(
            createdProductId, "Updated Product", "Updated description",
            new BigDecimal("349.99"), 15
        );
        
        given()
            .contentType(ContentType.JSON)
            .body(updatedProduct)
        .when()
            .put("/api/products/" + createdProductId)
        .then()
            .statusCode(200)
            .body("name", equalTo("Updated Product"))
            .body("price", equalTo(349.99f));
        
        // 4. Delete product
        given()
        .when()
            .delete("/api/products/" + createdProductId)
        .then()
            .statusCode(204);
        
        // 5. Verify deletion
        given()
        .when()
            .get("/api/products/" + createdProductId)
        .then()
            .statusCode(404);
    }
    
    @Test
    void searchProducts_shouldReturnFilteredResults() {
        given()
            .queryParam("name", "laptop")
        .when()
            .get("/api/products/search")
        .then()
            .statusCode(200)
            .body("size()", greaterThan(0))
            .body("[0].name", containsStringIgnoringCase("laptop"));
    }
}
```

---

## 🧪 Test-Driven Development (TDD)

### TDD Cycle

```
1. 🔴 Red: Write failing test
       ↓
2. 🟢 Green: Make test pass (minimal code)
       ↓
3. 🔵 Refactor: Improve code
       ↓
   Repeat
```

### TDD Example

```java
// Step 1: Write test first (RED)
@Test
void calculateDiscount_premiumCustomer_shouldApply20PercentDiscount() {
    // This test will fail - method doesn't exist yet
    Customer customer = new Customer("John", CustomerType.PREMIUM);
    BigDecimal originalPrice = new BigDecimal("100.00");
    
    BigDecimal discountedPrice = discountService.calculateDiscount(
        customer, originalPrice);
    
    assertThat(discountedPrice).isEqualByComparingTo(new BigDecimal("80.00"));
}

// Step 2: Write minimal code to pass (GREEN)
@Service
public class DiscountService {
    public BigDecimal calculateDiscount(Customer customer, BigDecimal price) {
        if (customer.getType() == CustomerType.PREMIUM) {
            return price.multiply(new BigDecimal("0.80"));
        }
        return price;
    }
}

// Step 3: Refactor and add more tests
@Test
void calculateDiscount_regularCustomer_shouldApply5PercentDiscount() {
    Customer customer = new Customer("Jane", CustomerType.REGULAR);
    BigDecimal originalPrice = new BigDecimal("100.00");
    
    BigDecimal discountedPrice = discountService.calculateDiscount(
        customer, originalPrice);
    
    assertThat(discountedPrice).isEqualByComparingTo(new BigDecimal("95.00"));
}
```

---

## 📊 Test Coverage

### JaCoCo Configuration

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.10</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
        <execution>
            <id>check</id>
            <goals>
                <goal>check</goal>
            </goals>
            <configuration>
                <rules>
                    <rule>
                        <element>PACKAGE</element>
                        <limits>
                            <limit>
                                <counter>LINE</counter>
                                <value>COVEREDRATIO</value>
                                <minimum>0.80</minimum>
                            </limit>
                        </limits>
                    </rule>
                </rules>
            </configuration>
        </execution>
    </executions>
</plugin>
```

**Generate Report**:
```bash
mvn clean test jacoco:report

# View report at: target/site/jacoco/index.html
```

---

## 💡 Testing Best Practices

### 1. AAA Pattern

```java
@Test
void testExample() {
    // Arrange (Given) - Setup
    Customer customer = new Customer("John");
    
    // Act (When) - Execute
    String greeting = greetingService.greet(customer);
    
    // Assert (Then) - Verify
    assertThat(greeting).isEqualTo("Hello, John!");
}
```

### 2. Test Naming

```java
// ✅ GOOD - Descriptive names
@Test
void createOrder_withValidInput_shouldReturnCreatedOrder() { }

@Test
void createOrder_whenProductOutOfStock_shouldThrowException() { }

// ❌ BAD - Unclear names
@Test
void test1() { }

@Test
void testCreateOrder() { }
```

### 3. Don't Test Framework Code

```java
// ❌ BAD - Testing JPA
@Test
void saveProduct_shouldSaveToDatabase() {
    Product product = new Product();
    productRepository.save(product);
    
    assertThat(productRepository.findAll()).hasSize(1);
}

// ✅ GOOD - Test your business logic
@Test
void createProduct_shouldApplyDefaultDiscount() {
    ProductDTO dto = new ProductDTO(...);
    ProductDTO result = productService.createProduct(dto);
    
    assertThat(result.getDiscount()).isEqualTo(new BigDecimal("0.05"));
}
```

---

_Test early, test often! ✅_
