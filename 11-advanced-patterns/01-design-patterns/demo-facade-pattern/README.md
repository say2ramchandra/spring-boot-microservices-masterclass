# Facade Pattern Demo - E-Commerce Order Processing

> **Demonstrates Facade Pattern with a complex order processing system**

## 🎯 What is Facade Pattern?

**Problem:** A subsystem contains many classes with complex relationships. Clients need to interact with multiple classes to perform tasks.

**Solution:** Provide a unified interface (Facade) that simplifies client interaction with the subsystem.

---

## 🏗️ How It Works

### Without Facade (Bad):
```java
// Client must know about ALL subsystems and their methods
public void placeOrder(Order order) {
    // 11+ method calls across 4 services!
    inventoryService.checkStock(order.getProductId(), qty);
    inventoryService.reserveInventory(orderId, productId, qty);
    paymentService.validatePaymentMethod(method, card);
    shippingService.calculateShippingRate(address, weight, method);
    // ... calculate total ...
    paymentService.authorizePayment(orderId, total, method);
    paymentService.capturePayment(authId);
    inventoryService.confirmReservation(reservationId);
    shippingService.createShippingLabel(orderId, address, method);
    notificationService.sendOrderConfirmationEmail(email, orderId, total);
    notificationService.sendPaymentReceipt(email, txnId, total);
    // ... plus error handling and rollback for each step!
}
// 😱 Client is tightly coupled to 4 subsystems!
```

### With Facade (Good):
```java
// Client calls ONE method on the Facade
public void placeOrder(Order order) {
    OrderResult result = orderFacade.placeOrder(order);
}
// ✅ Simple! Facade handles all complexity internally!
```

---

## 🚀 Running the Demo

### Prerequisites
- Java 17+
- Maven 3.8+

### Start the Application
```bash
cd 11-advanced-patterns/01-design-patterns/demo-facade-pattern
mvn spring-boot:run
```

Application runs on: **http://localhost:8097**

---

## 🧪 Testing the Facade Pattern

### 1. Place Order WITH Facade (Simple!)
```bash
curl http://localhost:8097/api/orders/sample
```

**Response:**
```json
{
  "success": true,
  "orderId": "ORD-A1B2C3D4",
  "transactionId": "TXN-X9Y8Z7W6",
  "trackingNumber": "TRK-123ABC456DEF",
  "subtotal": 999.99,
  "shippingCost": 16.49,
  "total": 1016.48,
  "estimatedDelivery": "2024-12-12",
  "message": "Order placed successfully!",
  "steps": [
    "1. Checking inventory...",
    "   ✅ Inventory reserved: RES-1702200000000",
    "2. Validating payment method...",
    "   ✅ Payment method validated",
    "3. Calculating shipping...",
    "   ✅ Shipping cost: $16.49",
    "4. Order total: $1016.48",
    "5. Authorizing payment...",
    "   ✅ Payment authorized: AUTH-A1B2C3D4",
    "6. Capturing payment...",
    "   ✅ Payment captured: TXN-X9Y8Z7W6",
    "7. Confirming inventory...",
    "   ✅ Inventory confirmed",
    "8. Creating shipping label...",
    "   ✅ Tracking number: TRK-123ABC456DEF",
    "9. Estimated delivery: 2024-12-12",
    "10. Sending notifications...",
    "   ✅ Notifications sent"
  ]
}
```

### 2. Place Order WITHOUT Facade (See the complexity!)
```bash
curl -X POST http://localhost:8097/api/orders/place-manual \
  -H "Content-Type: application/json" \
  -d '{
    "customerEmail": "test@example.com",
    "productId": "LAPTOP-001",
    "quantity": 1,
    "price": 999.99,
    "paymentMethod": "CREDIT_CARD",
    "cardNumber": "4111111111111111",
    "shippingAddress": "123 Main Street, New York, NY 10001",
    "shippingMethod": "EXPRESS"
  }'
```

### 3. List Subsystems
```bash
curl http://localhost:8097/api/orders/subsystems
```

### 4. Custom Order
```bash
curl -X POST http://localhost:8097/api/orders/place \
  -H "Content-Type: application/json" \
  -d '{
    "customerEmail": "john@example.com",
    "productId": "MOUSE-001",
    "quantity": 2,
    "price": 29.99,
    "paymentMethod": "PAYPAL",
    "shippingAddress": "456 Oak Avenue, Los Angeles, CA 90001",
    "shippingMethod": "STANDARD"
  }'
```

---

## 📚 Pattern Structure

```
┌────────────────────────────────────────────────────────────┐
│                      Client Code                           │
│                   (OrderController)                        │
│                                                            │
│          orderFacade.placeOrder(request)                   │
│                      ↓ ONE call                            │
└────────────────────────────────────────────────────────────┘
                           │
                           ▼
┌────────────────────────────────────────────────────────────┐
│                       FACADE                               │
│                    OrderFacade                             │
├────────────────────────────────────────────────────────────┤
│  + placeOrder(OrderRequest): OrderResult                   │
│                                                            │
│  Internally coordinates:                                   │
│  1. Inventory → 2. Payment → 3. Shipping → 4. Notify      │
└────────────────────────────────────────────────────────────┘
                           │
        ┌──────────────────┼──────────────────┐
        │                  │                  │
        ▼                  ▼                  ▼
┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│  Inventory   │  │   Payment    │  │  Shipping    │
│   Service    │  │   Service    │  │   Service    │
├──────────────┤  ├──────────────┤  ├──────────────┤
│checkStock()  │  │validate()    │  │calcRate()    │
│reserve()     │  │authorize()   │  │createLabel() │
│confirm()     │  │capture()     │  │schedule()    │
│release()     │  │void()        │  │getDelivery() │
└──────────────┘  └──────────────┘  └──────────────┘
                           │
                           ▼
                  ┌──────────────┐
                  │ Notification │
                  │   Service    │
                  ├──────────────┤
                  │sendEmail()   │
                  │sendSMS()     │
                  │sendPush()    │
                  └──────────────┘
```

---

## 🔑 Key Benefits

| Without Facade | With Facade |
|----------------|-------------|
| 11+ method calls | 1 method call |
| Know 4 subsystems | Know 1 facade |
| Handle errors per service | Facade handles errors |
| Write rollback logic | Facade handles rollback |
| Tight coupling | Loose coupling |
| Hard to test | Easy to test |

---

## 🎯 When to Use Facade Pattern

### ✅ Good Use Cases:
- **E-commerce checkout** (inventory + payment + shipping + notification)
- **User registration** (create account + send email + setup profile)
- **Report generation** (query data + format + export + email)
- **API gateway** (authentication + routing + rate limiting + logging)
- **System initialization** (config + database + cache + services)

### ❌ Avoid When:
- Subsystem is simple enough
- Client needs fine-grained control over subsystems
- Adding facade creates unnecessary abstraction

---

## 💡 Test Your Knowledge

1. **What problem does the Facade Pattern solve?**
   - A) Object creation
   - B) Simplifying complex subsystem interfaces
   - C) Algorithm selection
   - D) Object composition

2. **How many subsystems does OrderFacade coordinate?**
   - A) 1
   - B) 2
   - C) 4 (Inventory, Payment, Shipping, Notification)
   - D) 6

3. **What happens if payment fails in the Facade?**
   - A) The order continues
   - B) Client must handle rollback
   - C) Facade automatically rolls back inventory reservation
   - D) Nothing

4. **Does Facade replace subsystems?**
   - A) Yes, subsystems are removed
   - B) No, it provides a simplified interface to existing subsystems
   - C) Yes, it duplicates their functionality
   - D) Only for external clients

5. **What's the key benefit of using a Facade?**
   - A) Better performance
   - B) Reduced coupling between client and subsystems
   - C) More features
   - D) Less code in subsystems

<details>
<summary>📝 Answers</summary>

1. **B** - Facade simplifies complex subsystem interfaces
2. **C** - 4 subsystems: Inventory, Payment, Shipping, Notification
3. **C** - Facade automatically handles rollback (releases reservation, voids authorization)
4. **B** - Facade doesn't replace subsystems, it provides a simplified interface
5. **B** - Key benefit is reduced coupling between client and subsystems

</details>

---

## 📚 Further Reading

- [Facade Pattern - Refactoring Guru](https://refactoring.guru/design-patterns/facade)
- [Facade vs Adapter](https://stackoverflow.com/questions/1428556/)
- [Spring's JdbcTemplate as Facade](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/jdbc/core/JdbcTemplate.html)

---

**Happy Simplifying!** 🏢✨
