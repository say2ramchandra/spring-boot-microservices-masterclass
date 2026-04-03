# BeanPostProcessor Demo

This demo shows how `BeanPostProcessor` intercepts and decorates Spring beans.

## Covers

- `postProcessBeforeInitialization`
- `postProcessAfterInitialization`
- Wrapping target beans with a dynamic proxy
- Cross-cutting behavior without changing service code

## Run

```bash
cd demo-bean-post-processor
mvn clean compile exec:java
```

## Expected Output (high level)

- BPP logs before and after bean initialization
- Services are wrapped with proxy
- Method call logs appear around service logic

## Hands-on Tasks

1. Add a third service and verify auto-interception.
2. Add execution-time logging inside proxy wrapper.
3. Filter by package or annotation before wrapping.

## Learning Objectives

- Understand where BeanPostProcessor fits in bean lifecycle.
- Observe before/after initialization interception hooks.
- Apply proxy wrapping for cross-cutting concerns.

## Theory Checkpoints

1. BeanPostProcessor can inspect/modify every bean.
2. `postProcessAfterInitialization` can replace bean instances.
3. Framework features like AOP build on similar interception ideas.

## Run Steps

```bash
cd demo-bean-post-processor
mvn clean compile exec:java
```

## Verification Steps

```bash
mvn -q clean compile exec:java
```

Confirm before/after init logs and proxy method wrapper logs appear for tracked services.

## Expected Outcome

- Target beans are detected and wrapped automatically.
- Service method execution shows pre/post proxy logging.
- Learner understands lifecycle interception as reusable pattern.

## Hands-on Lab

1. Add annotation-based filtering before wrapping beans.
2. Add latency measurement in proxy invocation handler.
3. Compare this approach with Spring AOP on same services.
