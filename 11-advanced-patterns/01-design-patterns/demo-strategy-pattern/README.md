# Strategy Pattern Demo - Discount Pricing System

> **Demonstrates Strategy Pattern with multiple discount calculation strategies**

## 🎯 What is Strategy Pattern?

**Problem:** You have multiple algorithms for doing the same task, and you want to switch between them at runtime without using complex conditionals.

**Solution:** Define a family of algorithms, encapsulate each one, and make them interchangeable.

---

## 🏗️ How It Works Here

### Without Strategy Pattern (Bad):
```java
public class OrderService {
    public double calculateDiscount(Order order) {
        if (order.getDiscountType() == DiscountType.PERCENTAGE) {
            return order.getAmount() * 0.10;
        } else if (order.getDiscountType() == DiscountType.FIXED_AMOUNT) {
            return 20.0;
        } else if (order.getDiscountType() == DiscountType.BOGO) {
            // complex BOGO logic...
        } else if (order.getDiscountType() == DiscountType.SEASONAL) {
            // complex seasonal logic...
        }
        // 😱 Massive if-else chain! Hard to maintain!
    }
}
```

### With Strategy Pattern (Good):
```java
public class OrderService {
    private final DiscountContext context;
    
    public double calculateDiscount(Order order) {
        // Context selects appropriate strategy
        return context.applyDiscount(
            order.getDiscountType(),
            order.getAmount()
        );
        // ✅ Clean! Each strategy in separate class!
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
cd 11-advanced-patterns/01-design-patterns/demo-strategy-pattern
mvn spring-boot:run
```

Application runs on: **http://localhost:8092**

---

## 🧪 Testing the Strategy Pattern

### 1. No Discount
```bash
curl -X POST http://localhost:8092/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerEmail": "user@example.com",
    "amount": 100.00,
    "discountType": "NONE",
    "itemQuantity": 1
  }'
```

**Response:**
```json
{
  "id": 1,
  "customerEmail": "user@example.com",
  "originalAmount": 100.00,
  "discountAmount": 0.00,
  "finalAmount": 100.00,
  "discountType": "NONE",
  "discountDetails": "No discount applied",
  "isLoyaltyMember": false,
  "itemQuantity": 1
}
```

### 2. Percentage Discount (10% off)
```bash
curl -X POST http://localhost:8092/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerEmail": "user@example.com",
    "amount": 100.00,
    "discountType": "PERCENTAGE",
    "itemQuantity": 1
  }'
```

**Result:** $90.00 (saved $10.00)

### 3. Percentage Discount for Loyalty Member (15% off)
```bash
curl -X POST http://localhost:8092/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerEmail": "vip@example.com",
    "amount": 100.00,
    "discountType": "PERCENTAGE",
    "isLoyaltyMember": true,
    "itemQuantity": 1
  }'
```

**Result:** $85.00 (saved $15.00 - includes 5% loyalty bonus)

### 4. Fixed Amount Discount ($20 off)
```bash
curl -X POST http://localhost:8092/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerEmail": "user@example.com",
    "amount": 150.00,
    "discountType": "FIXED_AMOUNT",
    "itemQuantity": 1
  }'
```

**Result:** $130.00 (saved $20.00)

### 5. Buy One Get One (BOGO - 50% off)
```bash
curl -X POST http://localhost:8092/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerEmail": "user@example.com",
    "amount": 80.00,
    "discountType": "BUY_ONE_GET_ONE",
    "itemQuantity": 4
  }'
```

**Result:** $40.00 (saved $40.00 - buy 4, get 2 free)

### 6. Seasonal Discount (Changes by day of week)
```bash
curl -X POST http://localhost:8092/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerEmail": "user@example.com",
    "amount": 200.00,
    "discountType": "SEASONAL",
    "itemQuantity": 1
  }'
```

**Result:** 
- Weekday: $190.00 (5% off)
- Weekend: $170.00 (15% off)

### 7. Loyalty Member Tiered Discount
```bash
# Silver Tier ($50-$99)
curl -X POST http://localhost:8092/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerEmail": "vip@example.com",
    "amount": 75.00,
    "discountType": "LOYALTY_MEMBER",
    "isLoyaltyMember": true,
    "itemQuantity": 1
  }'
```
**Result:** $67.50 (10% off)

```bash
# Gold Tier ($100-$199)
curl -X POST http://localhost:8092/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerEmail": "vip@example.com",
    "amount": 150.00,
    "discountType": "LOYALTY_MEMBER",
    "isLoyaltyMember": true,
    "itemQuantity": 1
  }'
```
**Result:** $127.50 (15% off)

```bash
# Platinum Tier ($200+)
curl -X POST http://localhost:8092/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerEmail": "vip@example.com",
    "amount": 250.00,
    "discountType": "LOYALTY_MEMBER",
    "isLoyaltyMember": true,
    "itemQuantity": 1
  }'
```
**Result:** $187.50 (25% off)

### 8. Get All Orders
```bash
curl http://localhost:8092/api/orders
```

### 9. Get Available Discount Types
```bash
curl http://localhost:8092/api/orders/discount-types
```

**Response:**
```json
[
  "NONE",
  "PERCENTAGE",
  "FIXED_AMOUNT",
  "BUY_ONE_GET_ONE",
  "SEASONAL",
  "LOYALTY_MEMBER"
]
```

---

## 📁 Project Structure

```
demo-strategy-pattern/
├── src/main/java/com/masterclass/patterns/strategy/
│   ├── StrategyPatternApplication.java        # Main application
│   ├── controller/
│   │   └── OrderController.java               # REST API
│   ├── service/
│   │   └── OrderService.java                  # Business logic
│   ├── context/
│   │   └── DiscountContext.java               # ⭐ STRATEGY CONTEXT
│   ├── strategy/
│   │   ├── DiscountStrategy.java              # ⭐ STRATEGY INTERFACE
│   │   ├── NoDiscountStrategy.java            # Concrete strategy 1
│   │   ├── PercentageDiscountStrategy.java    # Concrete strategy 2
│   │   ├── FixedAmountDiscountStrategy.java   # Concrete strategy 3
│   │   ├── BuyOneGetOneStrategy.java          # Concrete strategy 4
│   │   ├── SeasonalDiscountStrategy.java      # Concrete strategy 5
│   │   └── LoyaltyMemberDiscountStrategy.java # Concrete strategy 6
│   ├── model/
│   │   ├── Order.java                         # Entity
│   │   └── DiscountType.java                  # Enum
│   ├── dto/
│   │   ├── OrderRequest.java                  # Request DTO
│   │   └── DiscountResult.java                # Result DTO
│   └── repository/
│       └── OrderRepository.java               # JPA repository
└── pom.xml
```

---

## 🎓 Key Components

### 1. Strategy Interface
```java
public interface DiscountStrategy {
    DiscountResult calculateDiscount(
        Double amount,
        Boolean isLoyaltyMember,
        Integer itemQuantity
    );
    
    DiscountType getDiscountType();
}
```

### 2. Concrete Strategies (6 implementations)

**PercentageDiscountStrategy** - 10% off (15% for loyalty members)
```java
@Component
public class PercentageDiscountStrategy implements DiscountStrategy {
    @Override
    public DiscountResult calculateDiscount(...) {
        double discount = amount * 0.10;
        // Loyalty members get extra 5%
        if (isLoyaltyMember) discount = amount * 0.15;
        return new DiscountResult(amount, discount, amount - discount);
    }
}
```

**FixedAmountDiscountStrategy** - $20 off ($25 for loyalty members)

**BuyOneGetOneStrategy** - Buy X, get X/2 free (50% off)

**SeasonalDiscountStrategy** - 5% weekday, 15% weekend

**LoyaltyMemberDiscountStrategy** - Tiered: 10%, 15%, 25%

**NoDiscountStrategy** - Full price

### 3. Context Class
```java
@Component
public class DiscountContext {
    private final Map<DiscountType, DiscountStrategy> strategies;
    
    // Spring injects all DiscountStrategy beans
    public DiscountContext(List<DiscountStrategy> discountStrategies) {
        this.strategies = discountStrategies.stream()
            .collect(Collectors.toMap(
                DiscountStrategy::getDiscountType,
                Function.identity()
            ));
    }
    
    // Context delegates to selected strategy
    public DiscountResult applyDiscount(DiscountType type, ...) {
        DiscountStrategy strategy = strategies.get(type);
        return strategy.calculateDiscount(amount, isLoyaltyMember, itemQuantity);
    }
}
```

### 4. Client Code (Service)
```java
@Service
public class OrderService {
    private final DiscountContext context;
    
    public Order processOrder(OrderRequest request) {
        // Context selects and executes strategy
        DiscountResult result = context.applyDiscount(
            request.getDiscountType(),
            request.getAmount(),
            request.getIsLoyaltyMember(),
            request.getItemQuantity()
        );
        
        return saveOrder(result);
    }
}
```

---

## ✨ Benefits of Strategy Pattern

### 1. **Eliminates Conditional Logic**
```java
// Before: Complex if-else
if (type == PERCENTAGE) { ... }
else if (type == FIXED) { ... }
else if (type == BOGO) { ... }

// After: Simple delegation
strategy.calculateDiscount(amount);
```

### 2. **Easy to Add New Strategies**
```java
// Just create a new class!
@Component
public class BlackFridayDiscountStrategy implements DiscountStrategy {
    public DiscountResult calculateDiscount(...) {
        return new DiscountResult(...); // 50% off everything!
    }
}
// Spring automatically registers it - no changes to existing code!
```

### 3. **Runtime Strategy Selection**
```java
// Strategy selected at runtime based on discount type
DiscountType type = order.getDiscountType(); // From user input
context.applyDiscount(type, amount);
```

### 4. **Testability**
```java
@Test
public void testPercentageDiscount() {
    DiscountStrategy strategy = new PercentageDiscountStrategy();
    DiscountResult result = strategy.calculateDiscount(100.0, false, 1);
    assertEquals(90.0, result.getFinalAmount());
}
```

### 5. **Single Responsibility**
Each strategy class handles ONE discount algorithm.

---

## 🔍 Strategy vs Factory Pattern

| Aspect | Strategy | Factory |
|--------|----------|---------|
| **Purpose** | Select algorithm at runtime | Create objects |
| **Focus** | **How** to do something | **What** to create |
| **Interface** | Common algorithm interface | Common product interface |
| **Context** | Delegates to strategy | Returns created object |
| **Example** | Discount calculation | Payment processor creation |

**Both patterns work together:**
- Factory creates the right object
- Strategy executes the right algorithm

---

## 🎯 Real-World Use Cases

1. **Pricing/Discount Systems** (This demo)
   - Multiple discount algorithms

2. **Payment Processing**
   - Different payment gateways (Stripe, PayPal, etc.)

3. **Compression Algorithms**
   - ZIP, GZIP, RAR compression strategies

4. **Sorting Algorithms**
   - QuickSort, MergeSort, BubbleSort

5. **Validation Strategies**
   - Email validation, phone validation

6. **Export Formats**
   - PDF, Excel, CSV export strategies

7. **Shipping Calculation**
   - Standard, Express, Overnight rates

---

## 🧩 Pattern Variations

### Classic Strategy
Define interface, implement strategies, context delegates.

### Strategy with Spring DI (This Demo)
Spring automatically discovers and injects all strategies.

### Functional Strategy (Java 8+)
Use lambdas instead of classes:
```java
Function<Double, Double> percentageDiscount = amount -> amount * 0.90;
Function<Double, Double> fixedDiscount = amount -> amount - 20.0;
```

---

## 💡 When to Use Strategy Pattern

### Use When:
- ✅ Multiple algorithms for same task
- ✅ Want to switch algorithms at runtime
- ✅ Have complex conditional logic (if-else chains)
- ✅ Algorithms have different implementations but same interface

### Don't Use When:
- ❌ Only one algorithm
- ❌ Algorithm never changes
- ❌ Adds unnecessary complexity for simple logic

---

## 🎓 Key Takeaways

1. **Eliminates complex if-else chains**
2. **Makes code Open/Closed** - open for extension, closed for modification
3. **Spring DI** simplifies strategy registration
4. **Each strategy is independent** - easy to test and maintain
5. **Runtime flexibility** - switch strategies dynamically

---

## 📊 Discount Summary Table

| Strategy | Regular | Loyalty Member | Special Condition |
|----------|---------|----------------|-------------------|
| **None** | $100 → $100 | - | - |
| **Percentage** | $100 → $90 (10% off) | $100 → $85 (15% off) | - |
| **Fixed Amount** | $100 → $80 ($20 off) | $100 → $75 ($25 off) | Min $50 order |
| **BOGO** | 4 items → Pay for 2 | - | Min 2 items |
| **Seasonal** | Weekday: 5% off<br>Weekend: 15% off | - | Day-dependent |
| **Loyalty Tiers** | - | Silver: 10%<br>Gold: 15%<br>Platinum: 25% | Amount-based tiers |

---

## 📚 Further Reading

- [Strategy Pattern - Refactoring Guru](https://refactoring.guru/design-patterns/strategy)
- [Spring Strategy Pattern](https://www.baeldung.com/spring-strategy-pattern)
- [Head First Design Patterns - Strategy Chapter](https://www.oreilly.com/library/view/head-first-design/0596007124/)

---

**Happy Strategizing!** 🎯✨
