# Factory Pattern Demo - Payment Processing System

> **Demonstrates Factory Pattern with Spring Boot dependency injection**

## 🎯 What is Factory Pattern?

**Problem:** You need to create objects without exposing the creation logic to the client.

**Solution:** Define an interface for creating objects, but let subclasses decide which class to instantiate.

---

## 🏗️ How It Works Here

### Without Factory Pattern (Bad):
```java
public class PaymentService {
    public void processPayment(PaymentType type) {
        if (type == PaymentType.CREDIT_CARD) {
            CreditCardProcessor processor = new CreditCardProcessor();
            processor.process();
        } else if (type == PaymentType.PAYPAL) {
            PayPalProcessor processor = new PayPalProcessor();
            processor.process();
        } else if (type == PaymentType.CRYPTO) {
            CryptoProcessor processor = new CryptoProcessor();
            processor.process();
        }
        // 😱 Need to modify code for every new payment type!
    }
}
```

### With Factory Pattern (Good):
```java
public class PaymentService {
    private final PaymentProcessorFactory factory;
    
    public void processPayment(PaymentType type) {
        PaymentProcessor processor = factory.getProcessor(type);
        processor.process();
        // ✅ No modification needed for new payment types!
    }
}
```

---

## 🚀 Running the Demo

### Prerequisites
- Java 17+
- Maven 3.8+

### Start the Application
```bash
cd 11-advanced-patterns/01-design-patterns/demo-factory-pattern
mvn spring-boot:run
```

Application runs on: **http://localhost:8090**

---

## 🧪 Testing the Factory Pattern

### 1. Process Credit Card Payment
```bash
curl -X POST http://localhost:8090/api/payments \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 100.50,
    "paymentType": "CREDIT_CARD",
    "customerEmail": "john@example.com"
  }'
```

**Response:**
```json
{
  "id": 1,
  "amount": 100.50,
  "paymentType": "CREDIT_CARD",
  "status": "COMPLETED",
  "transactionId": "CC-a1b2c3d4",
  "customerEmail": "john@example.com",
  "processorResponse": "Approved by Credit Card Gateway",
  "createdAt": "2024-12-10T10:30:00",
  "completedAt": "2024-12-10T10:30:01"
}
```

### 2. Process PayPal Payment
```bash
curl -X POST http://localhost:8090/api/payments \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 250.00,
    "paymentType": "PAYPAL",
    "customerEmail": "jane@example.com"
  }'
```

**Response:**
```json
{
  "id": 2,
  "amount": 250.00,
  "paymentType": "PAYPAL",
  "status": "COMPLETED",
  "transactionId": "PP-x9y8z7w6",
  "customerEmail": "jane@example.com",
  "processorResponse": "COMPLETED - PayPal Transaction",
  "createdAt": "2024-12-10T10:31:00",
  "completedAt": "2024-12-10T10:31:01"
}
```

### 3. Process Cryptocurrency Payment
```bash
curl -X POST http://localhost:8090/api/payments \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 500.00,
    "paymentType": "CRYPTOCURRENCY",
    "customerEmail": "crypto@example.com"
  }'
```

### 4. Get All Payments
```bash
curl http://localhost:8090/api/payments
```

### 5. Get Supported Payment Types
```bash
curl http://localhost:8090/api/payments/types
```

**Response:**
```json
["CREDIT_CARD", "PAYPAL", "CRYPTOCURRENCY"]
```

### 6. Get Payment by ID
```bash
curl http://localhost:8090/api/payments/1
```

---

## 📁 Project Structure

```
demo-factory-pattern/
├── src/main/java/com/masterclass/patterns/factory/
│   ├── FactoryPatternApplication.java         # Main application
│   ├── controller/
│   │   └── PaymentController.java             # REST endpoints
│   ├── service/
│   │   ├── PaymentProcessor.java              # Product interface
│   │   ├── CreditCardPaymentProcessor.java    # Concrete product 1
│   │   ├── PayPalPaymentProcessor.java        # Concrete product 2
│   │   ├── CryptoPaymentProcessor.java        # Concrete product 3
│   │   ├── PaymentProcessorFactory.java       # ⭐ FACTORY CLASS
│   │   └── PaymentService.java                # Service layer
│   ├── model/
│   │   ├── Payment.java                       # Entity
│   │   ├── PaymentType.java                   # Enum
│   │   └── PaymentStatus.java                 # Enum
│   ├── dto/
│   │   ├── PaymentRequest.java                # Request DTO
│   │   └── PaymentResult.java                 # Result DTO
│   └── repository/
│       └── PaymentRepository.java             # JPA repository
└── pom.xml
```

---

## 🎓 Key Components

### 1. Product Interface
```java
public interface PaymentProcessor {
    PaymentResult processPayment(BigDecimal amount, String customerEmail);
    PaymentType getPaymentType();
}
```

### 2. Concrete Products
- `CreditCardPaymentProcessor` - Processes credit card payments
- `PayPalPaymentProcessor` - Processes PayPal payments
- `CryptoPaymentProcessor` - Processes cryptocurrency payments

Each implements `PaymentProcessor` interface.

### 3. Factory Class (⭐ Core of the Pattern)
```java
@Component
public class PaymentProcessorFactory {
    
    private final Map<PaymentType, PaymentProcessor> processors;
    
    // Spring injects all PaymentProcessor implementations
    public PaymentProcessorFactory(List<PaymentProcessor> paymentProcessors) {
        this.processors = paymentProcessors.stream()
            .collect(Collectors.toMap(
                PaymentProcessor::getPaymentType,
                Function.identity()
            ));
    }
    
    // Factory method - returns appropriate processor
    public PaymentProcessor getProcessor(PaymentType paymentType) {
        return processors.get(paymentType);
    }
}
```

### 4. Client Code (Service)
```java
@Service
public class PaymentService {
    
    private final PaymentProcessorFactory factory;
    
    public Payment processPayment(PaymentRequest request) {
        // Get processor from factory
        PaymentProcessor processor = factory.getProcessor(request.getPaymentType());
        
        // Process payment
        PaymentResult result = processor.processPayment(
            request.getAmount(),
            request.getCustomerEmail()
        );
        
        return savePayment(result);
    }
}
```

---

## ✨ Benefits of Factory Pattern

### 1. **Loose Coupling**
Client code doesn't know about concrete classes.
```java
// Client only knows about PaymentProcessor interface
PaymentProcessor processor = factory.getProcessor(type);
// Doesn't care if it's Credit Card, PayPal, or Crypto
```

### 2. **Easy to Extend**
Add new payment type without modifying existing code:
```java
@Component
public class BankTransferPaymentProcessor implements PaymentProcessor {
    @Override
    public PaymentType getPaymentType() {
        return PaymentType.BANK_TRANSFER;
    }
    // ... implementation
}
// That's it! Factory automatically picks it up via Spring DI
```

### 3. **Single Responsibility**
Each processor handles only one payment type.

### 4. **Testable**
Easy to mock specific processors:
```java
@Mock
private CreditCardPaymentProcessor creditCardProcessor;

@Test
public void testPaymentProcessing() {
    when(creditCardProcessor.processPayment(any(), any()))
        .thenReturn(successResult());
    // Test in isolation
}
```

---

## 🔍 How Spring Helps

### Automatic Dependency Injection
```java
// Spring automatically finds all PaymentProcessor beans
public PaymentProcessorFactory(List<PaymentProcessor> paymentProcessors) {
    // Spring injects:
    // - CreditCardPaymentProcessor
    // - PayPalPaymentProcessor
    // - CryptoPaymentProcessor
    // No manual registration needed!
}
```

### Component Scanning
```java
@Component  // Spring automatically registers as bean
public class CreditCardPaymentProcessor implements PaymentProcessor {
    // ...
}
```

---

## 🎯 Real-World Use Cases

1. **Payment Processing** (This demo)
   - Credit Card, PayPal, Crypto, Bank Transfer

2. **Notification Services**
   - Email, SMS, Push Notifications

3. **File Parsers**
   - CSV, JSON, XML, Excel parsers

4. **Database Connections**
   - MySQL, PostgreSQL, MongoDB drivers

5. **Report Generators**
   - PDF, Excel, CSV reports

6. **Authentication Providers**
   - OAuth2, LDAP, Database, JWT

---

## 🧩 Pattern Variations

### Simple Factory (This Demo)
Factory method creates objects based on input.

### Factory Method
Abstract factory class, subclasses decide what to create.

### Abstract Factory
Factory of factories - create families of related objects.

---

## 💡 When to Use Factory Pattern

### Use When:
- ✅ Object creation logic is complex
- ✅ Need to hide creation details from client
- ✅ Want to add new types without changing client code
- ✅ Multiple implementations of same interface

### Don't Use When:
- ❌ Only one implementation
- ❌ Object creation is simple (use `new`)
- ❌ Adds unnecessary complexity

---

## 🎓 Key Takeaways

1. **Separation**: Create objects through factory, not `new`
2. **Flexibility**: Easy to add new types
3. **Spring Integration**: Automatic dependency injection
4. **Testability**: Mock specific implementations
5. **Maintainability**: Change implementations without affecting client

---

## 📚 Further Reading

- [Factory Pattern - Refactoring Guru](https://refactoring.guru/design-patterns/factory-method)
- [Spring Bean Factory](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans)
- [Dependency Injection in Spring](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-dependencies)

---

**Happy Learning!** 🏭✨
