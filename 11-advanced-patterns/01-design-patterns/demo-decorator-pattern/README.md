# Decorator Pattern Demo - Coffee Shop & Data Processing

> **Demonstrates Decorator Pattern with two practical examples**

## 🎯 What is Decorator Pattern?

**Problem:** You want to add responsibilities to objects dynamically without affecting other objects, and inheritance would lead to an explosion of subclasses.

**Solution:** Wrap an object with decorator objects that add new behavior while keeping the same interface.

---

## 🏗️ How It Works

### Without Decorator (Bad):
```java
// Subclass explosion!
class Espresso {}
class EspressoWithMilk {}
class EspressoWithMilkAndMocha {}
class EspressoWithMilkAndMochaAndWhip {}
class EspressoWithMilkAndDoubleMochaAndWhip {}
class LatteWithMilk {}
class LatteWithMilkAndVanilla {}
// ... 100+ classes for all combinations! 😱
```

### With Decorator (Good):
```java
// Compose behaviors dynamically!
Coffee coffee = new Espresso();           // $2.50
coffee = new MilkDecorator(coffee);       // +$0.50
coffee = new MochaDecorator(coffee);      // +$0.75
coffee = new MochaDecorator(coffee);      // +$0.75 (double mocha!)
coffee = new WhipDecorator(coffee);       // +$0.60
// Total: $5.10 ✅
```

---

## 🚀 Running the Demo

### Prerequisites
- Java 17+
- Maven 3.8+

### Start the Application
```bash
cd 11-advanced-patterns/01-design-patterns/demo-decorator-pattern
mvn spring-boot:run
```

Application runs on: **http://localhost:8098**

---

## 🧪 Example 1: Coffee Shop

### View Menu
```bash
curl http://localhost:8098/api/coffee/menu
```

### Order Custom Coffee
```bash
curl -X POST http://localhost:8098/api/coffee/order \
  -H "Content-Type: application/json" \
  -d '{
    "baseCoffee": "Espresso",
    "addOns": ["milk", "mocha", "whip"]
  }'
```

**Response:**
```json
{
  "description": "Espresso, Milk, Mocha, Whip",
  "cost": 4.35,
  "formattedCost": "$4.35",
  "decoratorsApplied": 3
}
```

### Sample Coffee (Double Mocha)
```bash
curl http://localhost:8098/api/coffee/sample
```

**Response:**
```json
{
  "order": "Double Mocha Espresso with Milk and Whip",
  "description": "Espresso, Milk, Mocha, Mocha, Whip",
  "cost": 5.10,
  "breakdown": [
    "Espresso: $2.50",
    "Milk: +$0.50",
    "Mocha: +$0.75",
    "Mocha: +$0.75",
    "Whip: +$0.60",
    "─────────────",
    "Total: $5.10"
  ],
  "decoratorChain": "Espresso → MilkDecorator → MochaDecorator → MochaDecorator → WhipDecorator"
}
```

---

## 🧪 Example 2: Data Processing

### Process Data with Custom Pipeline
```bash
curl -X POST http://localhost:8098/api/data/process \
  -H "Content-Type: application/json" \
  -d '{
    "data": "Sensitive user information",
    "options": ["validate", "log", "compress", "encrypt"]
  }'
```

**Response:**
```json
{
  "inputData": "Sensitive user information",
  "outputData": "W1ZBTElEQVRFRF1bQ09NUFJFU1NFRDoyN11TZW5zaXRpdmUgdXNlciBpbmZvcm1hdGlvbg==",
  "processingPipeline": "Basic Processing → Validated → Logged → Compressed → Encrypted",
  "inputLength": 28,
  "outputLength": 64
}
```

### Sample Data Processing
```bash
curl http://localhost:8098/api/data/sample
```

---

## 📚 Pattern Structure

```
┌─────────────────────────────────────────────────────────────┐
│                    <<interface>>                            │
│                        Coffee                               │
├─────────────────────────────────────────────────────────────┤
│ + getDescription(): String                                  │
│ + getCost(): double                                         │
└─────────────────────────────────────────────────────────────┘
                          △
                          │
        ┌─────────────────┼─────────────────┐
        │                 │                 │
        ▼                 ▼                 ▼
┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│   Espresso   │  │  HouseBlend  │  │CoffeeDecorator│
│  (Concrete)  │  │  (Concrete)  │  │  (Abstract)  │
├──────────────┤  ├──────────────┤  ├──────────────┤
│cost = $2.50  │  │cost = $1.99  │  │-wrappedCoffee│
└──────────────┘  └──────────────┘  └──────────────┘
                                            △
                                            │
                    ┌───────────────────────┼───────────────────────┐
                    │                       │                       │
                    ▼                       ▼                       ▼
            ┌──────────────┐        ┌──────────────┐        ┌──────────────┐
            │MilkDecorator │        │MochaDecorator│        │WhipDecorator │
            ├──────────────┤        ├──────────────┤        ├──────────────┤
            │  +$0.50      │        │  +$0.75      │        │  +$0.60      │
            └──────────────┘        └──────────────┘        └──────────────┘
```

---

## 🎯 When to Use Decorator Pattern

### ✅ Good Use Cases:
- **Adding features dynamically** (coffee add-ons, text formatting)
- **I/O Streams** (Java's BufferedReader, InputStream decorators)
- **Data processing pipelines** (validation, encryption, compression)
- **Middleware** (logging, authentication, caching)
- **UI components** (adding borders, scrollbars, shadows)

### ❌ Avoid When:
- Few fixed combinations (use subclasses)
- Performance is critical (many layers = overhead)
- Order of decorators matters significantly
- Debugging deep chains is a concern

---

## 🔑 Key Benefits

| Benefit | Description |
|---------|-------------|
| **Open/Closed** | Open for extension, closed for modification |
| **Single Responsibility** | Each decorator has one job |
| **Flexible Composition** | Mix and match at runtime |
| **No Class Explosion** | Avoid subclass for every combination |

---

## 💡 Test Your Knowledge

1. **What problem does the Decorator Pattern solve?**
   - A) Creating objects efficiently
   - B) Avoiding subclass explosion for feature combinations
   - C) Managing object lifecycles
   - D) Database optimization

2. **How do decorators add behavior?**
   - A) By modifying the original class
   - B) By wrapping the original object and delegating
   - C) By using inheritance only
   - D) By copying the object

3. **Can you apply the same decorator multiple times?**
   - A) No, each decorator can only be used once
   - B) Yes, like adding double mocha to coffee
   - C) Only for certain decorators
   - D) Only if explicitly allowed

4. **What must decorators and components share?**
   - A) The same database
   - B) The same interface
   - C) The same constructor
   - D) Nothing

5. **What's a real-world example of Decorator in Java?**
   - A) ArrayList
   - B) BufferedInputStream wrapping FileInputStream
   - C) HashMap
   - D) Thread

<details>
<summary>📝 Answers</summary>

1. **B** - Decorator avoids subclass explosion for feature combinations
2. **B** - Decorators wrap the original and delegate, adding behavior
3. **B** - Yes! You can add multiple of the same decorator (double mocha)
4. **B** - Decorators and components must share the same interface
5. **B** - Java I/O uses decorator: `new BufferedInputStream(new FileInputStream(file))`

</details>

---

## 📚 Further Reading

- [Decorator Pattern - Refactoring Guru](https://refactoring.guru/design-patterns/decorator)
- [Java I/O Decorators](https://docs.oracle.com/javase/tutorial/essential/io/)
- [Head First Design Patterns - Decorator Chapter](https://www.oreilly.com/library/view/head-first-design/0596007124/)

---

**Happy Decorating!** 🎨✨
