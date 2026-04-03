# Field Injection Demo (Anti-Pattern)

> **Understand why field injection is discouraged and what to use instead**

## 📚 What This Demo Demonstrates

- ✅ How field injection works with `@Autowired` on fields
- ✅ Why it's considered an anti-pattern
- ✅ Testing difficulties with field-injected beans
- ✅ Side-by-side: field injection vs constructor injection
- ✅ Live NullPointerException when testing without Spring

## 🚀 How to Run

```bash
cd demo-field-injection
mvn clean compile exec:java
```

## 🏗️ Project Structure

```
demo-field-injection/
├── pom.xml
├── README.md
└── src/main/java/com/masterclass/spring/
    ├── FieldInjectionDemo.java           (main entry point)
    ├── config/AppConfig.java
    ├── repository/UserRepository.java
    └── service/
        ├── UserService.java              (field injection — anti-pattern)
        ├── BetterUserService.java        (constructor injection — recommended)
        └── EmailService.java
```

## 🔑 Key Takeaway

**Never use field injection in production code.**  
It hides dependencies, breaks testability, and prevents immutability.  
Always prefer constructor injection.

## Learning Objectives

- Identify why field injection is considered an anti-pattern.
- Recognize testability and maintainability risks.
- Apply constructor injection as the preferred replacement.

## Theory Checkpoints

1. Field injection hides required dependencies from class contract.
2. Reflection-based wiring complicates isolated unit tests.
3. Constructor injection improves explicitness and immutability.

## Run Steps

```bash
cd demo-field-injection
mvn clean compile exec:java
```

## Verification Steps

```bash
mvn -q clean compile exec:java
```

Ensure output includes the NPE demonstration and the constructor-injection alternative.

## Expected Outcome

- Field injection path works inside Spring but demonstrates testing pain.
- Constructor-based alternative remains clear and test-friendly.
- Anti-pattern summary maps directly to observed behavior.

## Hands-on Lab

1. Convert `UserService` from field to constructor injection.
2. Add one focused unit test that uses constructor-based mocks.
3. Add static analysis rule/documentation to prevent future field injection.
