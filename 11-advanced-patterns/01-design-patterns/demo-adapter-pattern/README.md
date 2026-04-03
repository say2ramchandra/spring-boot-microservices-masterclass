# Adapter Pattern Demo - Payment Gateway Integration

> **Demonstrates Adapter Pattern with multiple payment gateway integrations**

## 🎯 What is Adapter Pattern?

**Problem:** You need to use a class with an incompatible interface (like a third-party library).

**Solution:** Create an adapter that converts the interface of the class into an interface clients expect.

---

## 🏗️ How It Works

### Without Adapter (Bad):
```java
// Code directly depends on each SDK's specific interface
public class PaymentService {
    public void pay(String gateway, double amount) {
        if (gateway.equals("paypal")) {
            PayPalSdk sdk = new PayPalSdk();
            sdk.makePayment(new PayPalPaymentRequest(...));  // PayPal's interface
        } else if (gateway.equals("stripe")) {
            StripeSdk sdk = new StripeSdk();
            sdk.createCharge(ChargeCreateParams.builder()...);  // Stripe's interface
        } else if (gateway.equals("square")) {
            SquareSdk sdk = new SquareSdk();
            sdk.createPayment(new CreatePaymentRequest(...));  // Square's interface
        }
    }
}
// 😱 Tightly coupled to each SDK's specific interface!
```

### With Adapter (Good):
```java
// Client only knows about PaymentGateway interface
public class PaymentService {
    private final PaymentGateway gateway;  // Could be any adapter
    
    public void pay(double amount) {
        gateway.processPayment(amount, "USD", email, desc);
    }
}
// ✅ Decoupled! Can switch gateways without changing client code!
```

---

## 🚀 Running the Demo

### Prerequisites
- Java 17+
- Maven 3.8+

### Start the Application
```bash
cd 11-advanced-patterns/01-design-patterns/demo-adapter-pattern
mvn spring-boot:run
```

Application runs on: **http://localhost:8096**

---

## 🧪 Testing the Adapter Pattern

### 1. List Available Gateways
```bash
curl http://localhost:8096/api/payments/gateways
```

**Response:**
```json
{
  "message": "Available payment gateways via Adapter Pattern",
  "gateways": [
    {
      "name": "PayPal",
      "endpoint": "/api/payments/paypal",
      "adaptedFrom": "PayPalSdk (legacy REST API)"
    },
    {
      "name": "Stripe",
      "endpoint": "/api/payments/stripe",
      "adaptedFrom": "StripeSdk (uses cents, builder pattern)"
    },
    {
      "name": "Square",
      "endpoint": "/api/payments/square",
      "adaptedFrom": "SquareSdk (nested objects, idempotency keys)"
    }
  ],
  "unifiedInterface": "PaymentGateway"
}
```

### 2. Process Payment via PayPal
```bash
curl -X POST http://localhost:8096/api/payments/paypal \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 99.99,
    "currency": "USD",
    "customerEmail": "john@example.com",
    "description": "Premium subscription"
  }'
```

### 3. Process Payment via Stripe
```bash
curl -X POST http://localhost:8096/api/payments/stripe \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 49.99,
    "currency": "USD",
    "customerEmail": "jane@example.com",
    "description": "Basic plan"
  }'
```

### 4. Process Payment via Square
```bash
curl -X POST http://localhost:8096/api/payments/square \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 149.99,
    "currency": "USD",
    "customerEmail": "bob@example.com",
    "description": "Enterprise plan"
  }'
```

### 5. Sample Payments (Quick Test)
```bash
curl http://localhost:8096/api/payments/sample/paypal
curl http://localhost:8096/api/payments/sample/stripe
curl http://localhost:8096/api/payments/sample/square
```

---

## 📚 Pattern Structure

```
┌─────────────────────────────────────────────────────────────────┐
│                       Client Code                               │
│                  (PaymentController)                            │
│                                                                 │
│                Uses only: PaymentGateway                        │
└─────────────────────────┬───────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────────┐
│                   TARGET INTERFACE                              │
│                    PaymentGateway                               │
├─────────────────────────────────────────────────────────────────┤
│ + processPayment(amount, currency, email, desc): PaymentResult  │
│ + refundPayment(transactionId, amount): PaymentResult           │
│ + getTransactionStatus(transactionId): PaymentResult            │
└─────────────────────────┬───────────────────────────────────────┘
                          │
        ┌─────────────────┼─────────────────┐
        ▼                 ▼                 ▼
┌───────────────┐ ┌───────────────┐ ┌───────────────┐
│ PayPalAdapter │ │ StripeAdapter │ │ SquareAdapter │
│   (Adapter)   │ │   (Adapter)   │ │   (Adapter)   │
├───────────────┤ ├───────────────┤ ├───────────────┤
│ Converts our  │ │ Converts our  │ │ Converts our  │
│ interface to  │ │ interface to  │ │ interface to  │
│ PayPal's SDK  │ │ Stripe's SDK  │ │ Square's SDK  │
└───────┬───────┘ └───────┬───────┘ └───────┬───────┘
        │                 │                 │
        ▼                 ▼                 ▼
┌───────────────┐ ┌───────────────┐ ┌───────────────┐
│  PayPalSdk    │ │  StripeSdk    │ │  SquareSdk    │
│  (Adaptee)    │ │  (Adaptee)    │ │  (Adaptee)    │
├───────────────┤ ├───────────────┤ ├───────────────┤
│ makePayment() │ │createCharge() │ │createPayment()│
│ Uses dollars  │ │ Uses cents    │ │ Uses Money obj│
│ REST-style    │ │ Builder style │ │ Nested objects│
└───────────────┘ └───────────────┘ └───────────────┘
```

---

## 🔑 Key Adaptations Made

| SDK | Their Interface | Our Interface | Adaptation |
|-----|-----------------|---------------|------------|
| **PayPal** | `makePayment(PayPalPaymentRequest)` | `processPayment(amount, currency, email, desc)` | Convert parameters to PayPalPaymentRequest |
| **Stripe** | `createCharge(ChargeCreateParams)` | `processPayment(amount, currency, email, desc)` | Convert dollars to cents, use builder |
| **Square** | `createPayment(CreatePaymentRequest)` | `processPayment(amount, currency, email, desc)` | Create Money object, add idempotency key |

---

## 🎯 When to Use Adapter Pattern

### ✅ Good Use Cases:
- **Third-party API integration** (payment gateways, shipping providers)
- **Legacy system integration** (wrapping old APIs)
- **Database driver abstraction** (different DB vendors)
- **Logging framework switching** (SLF4J over Log4j, Logback)
- **External service clients** (REST, SOAP, gRPC unification)

### ❌ Avoid When:
- You control both interfaces (just change them directly)
- Adapter adds significant overhead
- Simple interface differences (use method overloading)

---

## 💡 Test Your Knowledge

1. **What problem does the Adapter Pattern solve?**
   - A) Creating multiple objects efficiently
   - B) Converting incompatible interfaces to work together
   - C) Reducing memory usage
   - D) Improving algorithm performance

2. **What is the "Adaptee" in this pattern?**
   - A) The interface clients expect
   - B) The class that does the conversion
   - C) The existing class with incompatible interface (SDK)
   - D) The client code

3. **What is the "Target" interface?**
   - A) The third-party SDK
   - B) The interface our application expects (PaymentGateway)
   - C) The adapter class
   - D) The database interface

4. **Why does StripeAdapter convert dollars to cents?**
   - A) For security
   - B) Because Stripe SDK expects amounts in cents
   - C) To reduce precision errors
   - D) For international support

5. **What's the benefit of using adapters for payment gateways?**
   - A) Faster payment processing
   - B) Switch gateways without changing client code
   - C) Lower transaction fees
   - D) Better error handling

<details>
<summary>📝 Answers</summary>

1. **B** - Adapter converts incompatible interfaces to work together
2. **C** - Adaptee is the existing class with incompatible interface (the SDKs)
3. **B** - Target is the interface our application expects (PaymentGateway)
4. **B** - Stripe SDK requires amounts in cents, not dollars
5. **B** - Adapters let you switch implementations without changing client code

</details>

---

## 📚 Further Reading

- [Adapter Pattern - Refactoring Guru](https://refactoring.guru/design-patterns/adapter)
- [Object Adapter vs Class Adapter](https://stackoverflow.com/questions/9978477/)
- [SLF4J - Real World Adapter Example](http://www.slf4j.org/)

---

**Happy Adapting!** 🔌✨
