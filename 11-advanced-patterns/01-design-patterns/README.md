# Design Patterns in Spring Boot

> **Classic design patterns implemented with Spring Boot context**

## 📚 Overview

This section demonstrates classic GoF (Gang of Four) design patterns in the context of Spring Boot applications. Each pattern includes real-world examples with complete runnable code.

---

## 🗺️ Patterns Covered

### Creational Patterns
1. **Factory Pattern** - Create objects without exposing creation logic
2. **Builder Pattern** - Construct complex objects step-by-step
3. **Singleton Pattern** - Ensure a class has only one instance (Spring manages this)

### Behavioral Patterns
4. **Strategy Pattern** - Define family of algorithms, encapsulate each one
5. **Observer Pattern** - Define one-to-many dependency between objects
6. **Template Method Pattern** - Define algorithm skeleton, let subclasses override specific steps

### Structural Patterns
7. **Adapter Pattern** - Convert interface of a class into another interface
8. **Facade Pattern** - Provide unified interface to a set of interfaces
9. **Decorator Pattern** - Add behavior to objects dynamically

---

## 🚀 Quick Reference

### When to Use Each Pattern

| Pattern | Use When | Example |
|---------|----------|---------|
| **Factory** | Need to create objects based on conditions | Payment processors (Credit, PayPal, Crypto) |
| **Builder** | Object has many optional parameters | Complex DTOs, configuration objects |
| **Strategy** | Multiple algorithms for same task | Discount strategies, sorting algorithms |
| **Observer** | Multiple objects need updates when state changes | Event-driven systems, notifications |
| **Adapter** | Integrate incompatible interfaces | Third-party API integration |
| **Facade** | Simplify complex subsystem | Order processing with multiple services |
| **Decorator** | Add behavior without modifying class | Logging, caching, validation |

---

## 📁 Demos

### Demo 1: Factory Pattern
**Location:** `demo-factory-pattern/`  
**Description:** Payment processing system with multiple payment providers  
**Demonstrates:**
- Factory method pattern
- Spring dependency injection with factories
- Strategy selection at runtime

**Run:**
```bash
cd demo-factory-pattern
mvn spring-boot:run
```

**Endpoints:**
- `POST /api/payments` - Process payment with different providers
- `GET /api/payments/{id}` - Get payment status

---

### Demo 2: Builder Pattern
**Location:** `demo-builder-pattern/`  
**Description:** Order management with complex object construction  
**Demonstrates:**
- Lombok @Builder integration
- Custom builder with validation
- Fluent API design

**Run:**
```bash
cd demo-builder-pattern
mvn spring-boot:run
```

**Endpoints:**
- `POST /api/orders` - Create order with builder
- `GET /api/orders/{id}` - Get order details

---

### Demo 3: Strategy Pattern
**Location:** `demo-strategy-pattern/`  
**Description:** E-commerce pricing with different discount strategies  
**Demonstrates:**
- Strategy interface with multiple implementations
- Runtime strategy selection
- Spring automatic dependency injection

**Run:**
```bash
cd demo-strategy-pattern
mvn spring-boot:run
```

**Endpoints:**
- `GET /api/pricing/calculate?price=100&strategy=PERCENTAGE` - Calculate with discount
- `GET /api/pricing/strategies` - List available strategies

---

### Demo 4: Observer Pattern
**Location:** `demo-observer-pattern/`  
**Description:** Order processing with event-driven notifications  
**Demonstrates:**
- Spring Application Events
- @EventListener annotation
- Asynchronous event processing
- Multiple observers for same event

**Run:**
```bash
cd demo-observer-pattern
mvn spring-boot:run
```

**Endpoints:**
- `POST /api/orders` - Create order (triggers multiple observers)
- `GET /api/orders/{id}` - Get order with event history

---

## 🎓 Learning Path

1. **Start with Factory Pattern** - Understand object creation
2. **Move to Builder** - See how to create complex objects
3. **Try Strategy** - Learn algorithm encapsulation
4. **Explore Observer** - Understand event-driven design
5. **Review all patterns** - See how they work together

---

## 💡 Key Takeaways

### Factory Pattern
- ✅ Loose coupling between client and concrete classes
- ✅ Easy to add new product types
- ✅ Centralized object creation
- ⚠️ Can become complex with many product types

### Builder Pattern
- ✅ Readable code with named parameters
- ✅ Immutable objects
- ✅ Validation in one place
- ⚠️ More code to write (use Lombok to reduce)

### Strategy Pattern
- ✅ Open/Closed Principle
- ✅ Runtime algorithm selection
- ✅ No conditional statements
- ⚠️ Client must know about different strategies

### Observer Pattern
- ✅ Loose coupling between publisher and subscribers
- ✅ Easy to add new observers
- ✅ Asynchronous processing
- ⚠️ Potential memory leaks if not careful

---

## 🔍 Pattern Comparison

### Factory vs Builder
- **Factory**: Create objects based on type/condition
- **Builder**: Create complex objects with many parameters

### Strategy vs Template Method
- **Strategy**: Change entire algorithm
- **Template Method**: Change specific steps

### Observer vs Mediator
- **Observer**: One-to-many (publisher to subscribers)
- **Mediator**: Many-to-many (controlled central point)

---

## 📚 Additional Resources

- [Design Patterns: Elements of Reusable Object-Oriented Software](https://en.wikipedia.org/wiki/Design_Patterns) (GoF)
- [Refactoring Guru - Design Patterns](https://refactoring.guru/design-patterns)
- [Spring Design Patterns](https://spring.io/blog/2013/10/08/spring-framework-patterns)

---

## �� Next Steps

1. Run each demo
2. Modify the code
3. Create your own examples
4. Combine patterns in real projects

---

**Happy Coding!** 🎨
