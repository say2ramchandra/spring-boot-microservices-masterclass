# Contract Testing

> **Ensure API compatibility between services**

## 📚 Overview

Contract testing verifies that services can communicate correctly by testing against agreed-upon contracts. This prevents breaking changes between microservices.

---

## 🎯 Learning Objectives

- Understand consumer-driven contracts
- Test API contracts
- Prevent breaking changes
- Enable independent service deployment
- Validate request/response formats

---

## 🧪 Testing Pyramid - Contract Tests

```
           /\
          /E2E\              
         /──────\
        /  API   \           ← YOU ARE HERE (5-10% of tests)
       /Integration\
      /──────────────\
     /   Unit Tests   \      
    /──────────────────\
```

**Why Contract Testing?**
- 🤝 Verify service compatibility
- 🚫 Prevent breaking changes
- 🔄 Enable independent deployment
- 📄 Living API documentation
- ⚡ Faster than E2E tests

---

## 🎭 Contract Testing Approaches

### 1. Provider Contract Testing
Provider defines and verifies the contract they provide.

### 2. Consumer-Driven Contracts (CDC)
Consumer defines expectations, provider verifies they meet them.

**Example Scenario:**
```
Order Service (Consumer) → Product Service (Provider)
```

Order Service expects:
```json
GET /api/products/1
Response: {
  "id": 1,
  "name": "Laptop",
  "price": 999.99
}
```

Product Service must honor this contract!

---

## 📦 Spring Cloud Contract

Spring Cloud Contract is a popular tool for contract testing in Spring ecosystems.

### Key Concepts

1. **Contract Definition** - YAML or Groovy DSL
2. **Provider Side** - Generates tests from contracts
3. **Consumer Side** - Generates stubs from contracts
4. **Maven/Gradle** - Automates stub sharing

---

## 📝 Contract Example

### Contract Definition (YAML)
```yaml
# src/test/resources/contracts/shouldReturnProductById.yml
request:
  method: GET
  url: /api/products/1
  headers:
    Content-Type: application/json
    
response:
  status: 200
  headers:
    Content-Type: application/json
  body:
    id: 1
    name: "Test Product"
    price: 99.99
    quantity: 10
```

### Provider Test (Auto-generated)
```java
@SpringBootTest
@AutoConfigureRestDocs(outputDir = "target/snippets")
public class ContractVerifierTest extends ContractBase {
    
    @Test
    public void validate_shouldReturnProductById() throws Exception {
        // Auto-generated test that verifies contract
        mockMvc.perform(get("/api/products/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Test Product"));
    }
}
```

### Consumer Stub Usage
```java
@SpringBootTest
@AutoConfigureStubRunner(
    ids = "com.masterclass:product-service:+:stubs:8081",
    stubsMode = StubRunnerProperties.StubsMode.LOCAL
)
class OrderServiceTest {
    
    @Test
    void createOrder_shouldCallProductService() {
        // Stub automatically available at localhost:8081
        // Order service can call it safely!
    }
}
```

---

## 🔄 Contract Testing Workflow

```
1. Consumer writes contract
   ↓
2. Consumer commits contract to provider repo
   ↓
3. Provider runs tests against contract
   ↓
4. If tests pass → Provider publishes stubs
   ↓
5. Consumer uses stubs for testing
   ↓
6. Both services deploy independently! ✅
```

---

## 💡 Benefits

### vs Integration Tests
- ⚡ **Faster** - No need to start all services
- 🔒 **Isolated** - Test one service at a time
- 🎯 **Focused** - Test only the contract

### vs E2E Tests
- 💰 **Cheaper** - No full environment needed
- 🐛 **Easier debugging** - Failures are clear
- 🔄 **Parallel development** - Teams work independently

---

## 📚 Tools for Contract Testing

### 1. Spring Cloud Contract
- Best for Spring/Java ecosystem
- Maven/Gradle integration
- Groovy or YAML contracts

### 2. Pact
- Language agnostic
- Consumer-driven
- Pact Broker for sharing contracts
- Supports multiple languages

### 3. OpenAPI/Swagger
- Can be used for contract testing
- Generate tests from specs
- Widely adopted standard

---

## 🎯 When to Use Contract Tests

### ✅ Use Contract Tests For:
- Microservices communication
- API versioning
- Team coordination
- CI/CD pipeline
- Preventing breaking changes

### ❌ Don't Use Contract Tests For:
- Internal class methods
- UI testing
- Performance testing
- Security testing

---

## 🚀 Quick Example

### Simple Contract Test
```java
@Test
@DisplayName("Product API should match contract")
void testProductContract() throws Exception {
    // Arrange - Expected contract
    String expectedResponse = """
        {
            "id": 1,
            "name": "Test Product",
            "price": 99.99
        }
        """;
    
    // Act - Call actual API
    String actualResponse = mockMvc.perform(get("/api/products/1"))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();
    
    // Assert - Verify contract is honored
    JSONAssert.assertEquals(expectedResponse, actualResponse, false);
}
```

---

## 📖 Contract Testing Best Practices

### 1. **Consumer-Driven**
Let consumers define what they need, not what providers can give.

### 2. **Version Contracts**
```
contracts/
  v1/
    get-product.yml
  v2/
    get-product.yml
```

### 3. **Keep Contracts Simple**
Test the contract, not business logic.

### 4. **Automate Everything**
```bash
# Provider CI/CD
mvn clean install
# ↓ Auto-generates tests
# ↓ Publishes stubs
```

### 5. **Share Stubs**
- Use Maven/Gradle artifact repositories
- Or Pact Broker
- Or Git submodules

---

## 🔗 Resources

- [Spring Cloud Contract Docs](https://spring.io/projects/spring-cloud-contract)
- [Pact.io](https://pact.io/)
- [Consumer-Driven Contracts](https://martinfowler.com/articles/consumerDrivenContracts.html)

---

## 📝 Note on Demo

Contract testing is most valuable in multi-service architectures. This module provides the concepts and structure. For a full demo:

1. You'd need 2+ services (e.g., order-service + product-service)
2. Contract definitions shared between them
3. Provider tests auto-generated from contracts
4. Consumer tests using stubs

For the scope of this masterclass, we've covered the concepts and approaches. Implementing full contract testing is demonstrated in **Module 05: Spring Cloud** where multiple services interact.

---

_"Contracts prevent surprises between teams." - Martin Fowler_
