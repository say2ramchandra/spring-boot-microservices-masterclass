# Lifecycle Callbacks Demo

This demo shows the bean lifecycle hooks provided by Spring.

## Covers

- `@PostConstruct` and `@PreDestroy`
- `InitializingBean` and `DisposableBean`
- `@Bean(initMethod, destroyMethod)`
- Prototype destroy callback caveat

## Run

```bash
cd demo-lifecycle-callbacks
mvn clean compile exec:java
```

## Expected Output (high level)

- Init callbacks run during context startup
- Business methods run after initialization
- Destroy callbacks run when context closes (singleton beans)
- Prototype beans show init but no automatic destroy callback

## Hands-on Tasks

1. Add a `DatabaseWarmupBean` with `@PostConstruct` cache preload logic.
2. Convert one callback style to another and compare readability.
3. Add explicit manual cleanup handling for prototype beans.

## Learning Objectives

- Understand major initialization and destruction callback mechanisms.
- Compare annotation, interface, and bean-method lifecycle hooks.
- Recognize prototype destruction caveat in Spring container lifecycle.

## Theory Checkpoints

1. `@PostConstruct` runs after dependency injection.
2. `@PreDestroy` runs on context shutdown for managed singleton beans.
3. Prototype beans are not fully lifecycle-managed for destruction.

## Run Steps

```bash
cd demo-lifecycle-callbacks
mvn clean compile exec:java
```

## Verification Steps

```bash
mvn -q clean compile exec:java
```

Confirm startup prints init callbacks and shutdown prints singleton destroy callbacks.

## Expected Outcome

- Callback order is visible in console output.
- Multiple lifecycle styles are demonstrated in one run.
- Prototype destroy caveat is explicitly observed.

## Hands-on Lab

1. Add one bean with all three init styles and compare order.
2. Add custom cleanup manager for prototype instances.
3. Capture callback timestamps to analyze lifecycle ordering.
