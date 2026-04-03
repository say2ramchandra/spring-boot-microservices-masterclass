# Setter Injection Demo

> **Understand setter-based dependency injection and when to use it**

## 📚 What This Demo Demonstrates

- ✅ Basic setter injection with `@Autowired`
- ✅ Multiple setter methods on a single bean
- ✅ Optional dependencies with `@Autowired(required = false)`
- ✅ Runtime reconfiguration (mutable dependencies)
- ✅ Side-by-side comparison: Setter vs Constructor injection

## 🚀 How to Run

```bash
cd demo-setter-injection
mvn clean compile exec:java
```

## 🏗️ Project Structure

```
demo-setter-injection/
├── pom.xml
├── README.md
└── src/main/java/com/masterclass/spring/
    ├── SetterInjectionDemo.java          (main entry point)
    ├── config/AppConfig.java
    ├── repository/
    │   ├── UserRepository.java
    │   └── OrderRepository.java
    └── service/
        ├── UserService.java              (basic setter injection)
        ├── OrderService.java             (multiple setters)
        ├── EmailService.java
        ├── ReportService.java            (optional dependency)
        └── ConfigurableService.java      (mutable reconfiguration)
```

## 🔑 Key Takeaway

Use setter injection only for **optional** dependencies.  
For everything else, prefer **constructor injection**.

## Learning Objectives

- Explain where setter injection fits in DI strategy.
- Implement optional dependencies safely.
- Compare setter and constructor trade-offs in production code.

## Theory Checkpoints

1. Setter injection wires dependencies after object construction.
2. Mutable dependencies reduce immutability guarantees.
3. `@Autowired(required = false)` is a valid optional dependency pattern.

## Run Steps

```bash
cd demo-setter-injection
mvn clean compile exec:java
```

## Verification Steps

```bash
mvn -q clean compile exec:java
```

Verify logs show setter callback invocations and optional dependency behavior.

## Expected Outcome

- Services initialize successfully through setter wiring.
- Optional dependency path executes without container failure.
- Comparison summary clarifies when to avoid setter injection.

## Hands-on Lab

1. Add one more optional dependency and null-safe handling.
2. Refactor one setter-wired service to constructor injection.
3. Add a small unit test to contrast test setup complexity.
