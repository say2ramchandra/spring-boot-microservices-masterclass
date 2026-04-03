# Qualifier and Primary Demo

This demo shows how Spring resolves multiple beans of the same interface using `@Primary` and `@Qualifier`.

## Covers

- Default bean selection using `@Primary`
- Explicit bean selection using `@Qualifier`
- Injecting multiple named implementations in one service

## Run

```bash
cd demo-qualifier-primary
mvn clean compile exec:java
```

## Expected Output (high level)

- `CheckoutService` uses Stripe as default (`@Primary`)
- `SubscriptionService` uses PayPal (`@Qualifier("paypalGateway")`)
- `MarketplaceService` uses Stripe + Razorpay explicitly

## Learning Objectives

- Resolve multi-bean ambiguity using `@Primary` and `@Qualifier`.
- Select default and explicit implementations safely.
- Apply multi-implementation routing patterns.

## Theory Checkpoints

1. `@Primary` provides default bean resolution.
2. `@Qualifier` provides explicit bean selection.
3. Both can be combined for flexibility and clarity.

## Run Steps

```bash
cd demo-qualifier-primary
mvn clean compile exec:java
```

## Verification Steps

```bash
mvn -q clean compile exec:java
```

Confirm output shows Stripe as default, PayPal via qualifier, and split routing.

## Expected Outcome

- Default injection uses `@Primary` implementation.
- Qualified injections select exact targeted implementations.
- Multi-bean orchestration behaves deterministically.

## Hands-on Lab

1. Add a fourth gateway and wire it to one service with qualifier.
2. Remove `@Primary` and observe failure mode, then fix it.
3. Add integration test to assert active provider per service.

## Project Structure

```text
demo-qualifier-primary/
  src/main/java/com/masterclass/spring/
    QualifierPrimaryDemo.java
    config/AppConfig.java
    payment/
      PaymentGateway.java
      StripePaymentGateway.java
      PaypalPaymentGateway.java
      RazorpayPaymentGateway.java
    service/
      CheckoutService.java
      SubscriptionService.java
      MarketplaceService.java
```
