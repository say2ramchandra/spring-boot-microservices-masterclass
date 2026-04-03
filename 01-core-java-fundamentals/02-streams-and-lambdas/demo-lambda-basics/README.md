# Lambda Basics Demo

> **Master Java Lambda expressions — the foundation of functional programming in Java**

## 📚 What This Demo Demonstrates

- ✅ Lambda syntax variations (no-param, single-param, multi-param, multi-line)
- ✅ Common functional interfaces (Predicate, Function, Consumer, Supplier)
- ✅ Method references (static, bound instance, unbound instance, constructor)
- ✅ Lambdas with Collections (sort, forEach, removeIf, replaceAll)
- ✅ Closures and effectively final variables
- ✅ Custom functional interfaces
- ✅ Real-world patterns (Strategy, Callback, Lazy Evaluation)

## 🎯 Learning Objectives

After running this demo, you will understand:
- How lambda syntax works and when to use each form
- The key functional interfaces in `java.util.function`
- How method references simplify common lambdas
- How lambdas interact with local variables (closures)
- Practical patterns for using lambdas in real applications

## 📋 Prerequisites

- Java 17+
- Maven 3.8+

## 🚀 How to Run

```bash
cd demo-lambda-basics
mvn clean compile exec:java
```

## 🏗️ Project Structure

```
demo-lambda-basics/
├── pom.xml
├── README.md
└── src/main/java/com/masterclass/lambdas/
    └── LambdaBasicsDemo.java
```

## 🔑 Key Concepts Covered

| Section | Concept | Example |
|---------|---------|---------|
| 1 | Lambda syntax | `(a, b) -> a + b` |
| 2 | Predicate | `n -> n % 2 == 0` |
| 3 | Method reference | `String::toUpperCase` |
| 4 | Collection lambdas | `list.sort(...)`, `list.removeIf(...)` |
| 5 | Effectively final | Captured variables must not be reassigned |
| 6 | Custom interfaces | `@FunctionalInterface` |
| 7 | Strategy pattern | Swap behavior via lambdas |

## Learning Objectives

- Write and read lambda expressions confidently.
- Select the right functional interface for each use case.
- Apply method references and lambda composition in practical code.

## Theory Checkpoints

1. Lambda is an implementation of a functional interface.
2. Method references are syntax sugar for simple lambdas.
3. Closures capture effectively-final variables from outer scope.

## Run Steps

```bash
cd demo-lambda-basics
mvn clean compile exec:java
```

## Verification Steps

```bash
mvn -q clean compile exec:java
```

Check that output includes all 7 sections and ends with `Demo Completed Successfully!`.

## Expected Outcome

- All lambda syntax examples execute correctly.
- Functional interface and collection examples produce expected transformations.
- Real-world strategy/callback examples run without errors.

## Hands-on Lab

1. Add a custom `TriFunction` and use it in one section.
2. Add one comparator chain with three sort criteria.
3. Add a mini benchmark comparing loop vs stream for a simple operation.
