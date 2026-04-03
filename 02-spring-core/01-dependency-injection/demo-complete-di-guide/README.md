# Complete Dependency Injection Guide Demo

This demo is an end-to-end DI practices guide in one runnable project.

## Covers

- Constructor injection (recommended default)
- Setter injection (optional dependency case)
- Field injection anti-pattern (why to avoid)
- Multi-bean resolution using `@Primary` and `@Qualifier`

## Run

```bash
cd demo-complete-di-guide
mvn clean compile exec:java
```

## Expected Output (high level)

- Constructor section uses default `@Primary` client
- Setter section runs with mutable wiring model
- Field section prints anti-pattern warning
- Qualifier section routes via explicit SMS client

## Learning Objectives

- Compare all major DI styles in one end-to-end flow.
- Understand recommended defaults vs conditional alternatives.
- Map DI theory to concrete service wiring decisions.

## Theory Checkpoints

1. Constructor injection is default for required dependencies.
2. Setter injection fits optional/reconfigurable dependencies.
3. Field injection is discouraged due to hidden coupling and test cost.
4. `@Primary` + `@Qualifier` solve multi-implementation conflicts.

## Run Steps

```bash
cd demo-complete-di-guide
mvn clean compile exec:java
```

## Verification Steps

```bash
mvn -q clean compile exec:java
```

Verify all four sections execute in sequence and print DI best-practice summary.

## Expected Outcome

- Each DI pattern is demonstrated with clear output behavior.
- Warnings and recommendations align with Spring best practices.
- Learner can contrast patterns in one runnable reference.

## Hands-on Lab

1. Add one service that mixes constructor + qualifier selection.
2. Convert warning section to fully constructor-based implementation.
3. Add tests covering one required and one optional dependency path.

## Hands-on Tasks

1. Add a third notification client and wire it with `@Qualifier`.
2. Refactor one setter-based dependency to constructor injection.
3. Write a small unit test that manually instantiates `ConstructorInjectionService`.
