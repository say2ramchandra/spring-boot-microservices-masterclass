# Observer Pattern Demo - Order Event System

> **Demonstrates Observer Pattern using Spring Events**

## 🎯 What is Observer Pattern?

**Problem:** You need to notify multiple objects when something changes, without tight coupling between them.

**Solution:** Define a one-to-many dependency where when one object changes state, all its dependents are notified automatically.

---

## 🏗️ How It Works Here

### Without Observer Pattern (Bad):
```java
public class OrderService {
    private EmailService emailService;
    private SmsService smsService;
    private InventoryService inventoryService;
    private AnalyticsService analyticsService;
    
    public void createOrder(Order order) {
        orderRepository.save(order);
        
        // Tight coupling! Must know about all services
        emailService.sendConfirmation(order);
        smsService.sendNotification(order);
        inventoryService.updateStock(order);
        analyticsService.track(order);
        
        // 😱 Need to modify this method for every new notification type!
    }
}
```

### With Observer Pattern (Good):
```java
public class OrderService {
    private ApplicationEventPublisher eventPublisher;
    
    public void createOrder(Order order) {
        orderRepository.save(order);
        
        // Just publish event - observers handle the rest!
        eventPublisher.publishEvent(new OrderCreatedEvent(this, order));
        
        // ✅ Add new observers without modifying this code!
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
cd 11-advanced-patterns/01-design-patterns/demo-observer-pattern
mvn spring-boot:run
```

Application runs on: **http://localhost:8093**

---

## 🧪 Testing the Observer Pattern

### 1. Create Order (Low Value - $50)
```bash
curl -X POST http://localhost:8093/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerEmail": "john@example.com",
    "productName": "Book",
    "quantity": 2,
    "amount": 50.00
  }'
```

**Observers Triggered:**
- ✅ 📧 Email sent
- ✅ 📦 Inventory updated
- ✅ 📊 Analytics tracked
- ❌ 📱 SMS skipped (below $100 threshold)

**Console Output:**
```
🛒 Creating new order for: john@example.com
✅ Order created with ID: 1
📣 Publishing OrderCreatedEvent to all observers...
📦 [INVENTORY] Reserving stock for order #1
✅ [INVENTORY] Stock reserved successfully
📊 [ANALYTICS] Tracking order metrics
   Order Value: $50.00
   Customer Segment: Entry-Level
✅ [ANALYTICS] Metrics recorded
📧 [EMAIL] Sending order confirmation email to: john@example.com
✅ [EMAIL] Order confirmation sent successfully
⏭️  [SMS] Skipping SMS for low-value order ($50.00)
```

### 2. Create High-Value Order ($250)
```bash
curl -X POST http://localhost:8093/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerEmail": "jane@example.com",
    "productName": "Laptop",
    "quantity": 1,
    "amount": 250.00
  }'
```

**Observers Triggered:**
- ✅ 📧 Email sent
- ✅ 📱 SMS sent (high-value order)
- ✅ 📦 Inventory updated
- ✅ 📊 Analytics tracked (Regular segment)

### 3. Update Order Status
```bash
curl -X PUT "http://localhost:8093/api/orders/1/status?status=SHIPPED"
```

**Observers Triggered:**
- ✅ 📧 Status update email sent
- ✅ 📱 SMS sent (critical status: SHIPPED)
- ✅ 📊 Status change tracked

**Console Output:**
```
🔄 Updating order #1 status to: SHIPPED
✅ Order status updated
📣 Publishing OrderStatusChangedEvent...
📧 [EMAIL] Sending status update email to: john@example.com
   Subject: Order #1 - Status Update
   Body: Your order status changed from CREATED to SHIPPED
✅ [EMAIL] Status update email sent successfully
📱 [SMS] Sending critical status update
   Message: Order #1 status: SHIPPED
✅ [SMS] SMS sent successfully
📊 [ANALYTICS] Tracking status change metrics
   Order #1: CREATED → SHIPPED
✅ [ANALYTICS] Status change tracked
```

### 4. Get All Orders
```bash
curl http://localhost:8093/api/orders
```

### 5. Get Orders by Customer
```bash
curl http://localhost:8093/api/orders/customer/john@example.com
```

---

## 📁 Project Structure

```
demo-observer-pattern/
├── src/main/java/com/masterclass/patterns/observer/
│   ├── ObserverPatternApplication.java        # Main application
│   ├── controller/
│   │   └── OrderController.java               # REST API
│   ├── service/
│   │   └── OrderService.java                  # ⭐ SUBJECT (Event Publisher)
│   ├── event/
│   │   ├── OrderCreatedEvent.java             # Event 1
│   │   └── OrderStatusChangedEvent.java       # Event 2
│   ├── listener/                              # ⭐ OBSERVERS
│   │   ├── EmailNotificationListener.java     # Observer 1
│   │   ├── SmsNotificationListener.java       # Observer 2
│   │   ├── InventoryListener.java             # Observer 3
│   │   └── AnalyticsListener.java             # Observer 4
│   ├── model/
│   │   ├── Order.java                         # Entity
│   │   └── OrderStatus.java                   # Enum
│   ├── dto/
│   │   └── OrderRequest.java                  # Request DTO
│   └── repository/
│       └── OrderRepository.java               # JPA repository
└── pom.xml
```

---

## 🎓 Key Components

### 1. Subject (Event Publisher)
```java
@Service
public class OrderService {
    private final ApplicationEventPublisher eventPublisher;
    
    public Order createOrder(OrderRequest request) {
        Order order = orderRepository.save(buildOrder(request));
        
        // Publish event - all observers will be notified
        eventPublisher.publishEvent(new OrderCreatedEvent(this, order));
        
        return order;
    }
}
```

### 2. Events
```java
public class OrderCreatedEvent extends ApplicationEvent {
    private final Order order;
    
    public OrderCreatedEvent(Object source, Order order) {
        super(source);
        this.order = order;
    }
}
```

### 3. Observers (Event Listeners)

**Email Observer:**
```java
@Component
public class EmailNotificationListener {
    
    @EventListener
    @Async  // Non-blocking!
    public void handleOrderCreated(OrderCreatedEvent event) {
        Order order = event.getOrder();
        sendEmail(order.getCustomerEmail(), "Order Confirmation");
    }
}
```

**SMS Observer (Conditional):**
```java
@Component
public class SmsNotificationListener {
    
    @EventListener
    @Async
    public void handleOrderCreated(OrderCreatedEvent event) {
        Order order = event.getOrder();
        
        // Only send SMS for high-value orders
        if (order.getAmount() > 100.0) {
            sendSms(order.getCustomerEmail(), "High-value order received");
        }
    }
}
```

**Inventory Observer (High Priority):**
```java
@Component
public class InventoryListener {
    
    @EventListener
    @Order(1)  // Execute first!
    public void handleOrderCreated(OrderCreatedEvent event) {
        reserveStock(event.getOrder());
    }
}
```

---

## ✨ Benefits of Observer Pattern

### 1. **Loose Coupling**
```java
// Subject doesn't know about observers
eventPublisher.publishEvent(event);  // Just publish!

// Observers don't know about each other
@EventListener
public void handleOrderCreated(OrderCreatedEvent event) { ... }
```

### 2. **Easy to Add New Observers**
```java
// Just create a new listener - no changes to existing code!
@Component
public class LoyaltyPointsListener {
    @EventListener
    public void handleOrderCreated(OrderCreatedEvent event) {
        awardLoyaltyPoints(event.getOrder());
    }
}
```

### 3. **Asynchronous Processing**
```java
@EventListener
@Async  // Runs in separate thread!
public void handleOrderCreated(OrderCreatedEvent event) {
    // Long-running task doesn't block order creation
    sendEmail(event.getOrder());
}
```

### 4. **Execution Order Control**
```java
@EventListener
@Order(1)  // Execute first
public void highPriorityTask() { ... }

@EventListener
@Order(10)  // Execute later
public void lowPriorityTask() { ... }
```

### 5. **Conditional Observers**
```java
@EventListener
public void handleOrderCreated(OrderCreatedEvent event) {
    // Only react to specific conditions
    if (event.getOrder().getAmount() > 100.0) {
        doSomething();
    }
}
```

---

## 🔍 Observer Behavior Matrix

| Observer | Low-Value Order | High-Value Order | Status Change | Priority |
|----------|----------------|------------------|---------------|----------|
| **Email** | ✅ Always | ✅ Always | ✅ Always | Normal |
| **SMS** | ❌ Skip | ✅ Send | ✅ Critical only | Normal |
| **Inventory** | ✅ Always | ✅ Always | ❌ Only on create | **High (1)** |
| **Analytics** | ✅ Always | ✅ Always | ✅ Always | Normal |

---

## 🎯 Real-World Use Cases

1. **E-Commerce Order Processing** (This demo)
   - Email, SMS, inventory, analytics, loyalty points

2. **Social Media Notifications**
   - Like → notify post author, update counter, track analytics

3. **Stock Trading Systems**
   - Price change → notify subscribers, update charts, trigger alerts

4. **GUI Event Handling**
   - Button click → update UI, log action, trigger validation

5. **Logging Systems**
   - Log event → console, file, database, monitoring service

6. **Messaging Systems**
   - Message received → notify user, mark as unread, update badge, send push

---

## 🧩 Pattern Variations

### Classic Observer (Manual)
Maintain list of observers, manually iterate and notify.

### Spring Events (This Demo)
Spring handles observer registration and notification automatically.

### Reactive Streams
RxJava, Project Reactor - advanced observable patterns.

---

## 💡 When to Use Observer Pattern

### Use When:
- ✅ One object changes and others need to be notified
- ✅ Don't want tight coupling between objects
- ✅ Number of observers can change at runtime
- ✅ Need asynchronous processing

### Don't Use When:
- ❌ Only one observer ever
- ❌ Need immediate synchronous response from observers
- ❌ Complex observer dependencies (use mediator pattern)

---

## 🚦 Execution Flow

```
1. User creates order via REST API
         ↓
2. OrderService saves order to database
         ↓
3. OrderService publishes OrderCreatedEvent
         ↓
4. Spring Event Dispatcher notifies ALL observers:
         ↓
    ┌────┴────┬────────┬────────┬────────┐
    ↓         ↓        ↓        ↓        ↓
  Email     SMS    Inventory Analytics  (future observers)
  
5. Each observer processes event independently
   - Async observers run in parallel
   - Sync observers run in order (priority)
```

---

## 🎓 Key Takeaways

1. **Subject publishes events** - doesn't know about observers
2. **Observers listen for events** - automatically notified
3. **Loose coupling** - add/remove observers without changing subject
4. **@Async** for non-blocking execution
5. **@Order** for execution priority
6. **Spring Events** simplify observer pattern dramatically

---

## 📊 Observer Comparison

| Aspect | Manual Observer | Spring Events |
|--------|----------------|---------------|
| **Registration** | Explicit add/remove | Automatic via @EventListener |
| **Decoupling** | Medium | High |
| **Async Support** | Manual | @Async annotation |
| **Priority** | Manual sorting | @Order annotation |
| **Testability** | Complex | Simple (mock events) |
| **Code Lines** | ~100 | ~10 |

---

## 📚 Further Reading

- [Observer Pattern - Refactoring Guru](https://refactoring.guru/design-patterns/observer)
- [Spring Events](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#context-functionality-events)
- [Spring @EventListener](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/event/EventListener.html)

---

**Happy Observing!** 👁️✨
