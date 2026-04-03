# Bean Scopes Demo

This demo explains Spring bean scopes with runnable output.

## Covers

- Singleton scope
- Prototype scope
- Request and session scope simulation in non-web Spring (thread scopes)

## Run

```bash
cd demo-bean-scopes
mvn clean compile exec:java
```

## What to Observe

- Singleton bean ID remains the same everywhere.
- Prototype bean ID changes on each retrieval.
- Request/session (simulated) stay same within one thread, change across threads.

## Note

In real Spring MVC applications, request/session scopes are tied to HTTP lifecycle.
This demo uses `SimpleThreadScope` to show behavior in a plain Spring Core app.

## Learning Objectives

- Distinguish singleton, prototype, request, and session semantics.
- Observe scope behavior across repeated lookups and threads.
- Understand non-web simulation limits for request/session scope.

## Theory Checkpoints

1. Singleton is one instance per container.
2. Prototype creates a new instance for each lookup.
3. Request/session are context-bound; simulated here with thread scope.

## Run Steps

```bash
cd demo-bean-scopes
mvn clean compile exec:java
```

## Verification Steps

```bash
mvn -q clean compile exec:java
```

Check IDs: singleton remains stable, prototype changes each retrieval, thread-bound scope changes across threads.

## Expected Outcome

- Scope differences are observable directly from printed bean IDs.
- Thread switch demonstrates context-bound behavior for simulated request/session.
- Output aligns with Spring scope theory.

## Hands-on Lab

1. Add application scope simulation and compare with singleton.
2. Inject prototype into singleton using provider and verify behavior.
3. Build a tiny web endpoint version to observe real request/session semantics.
