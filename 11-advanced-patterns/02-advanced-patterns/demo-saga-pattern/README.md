# Saga Pattern Demo

This demo showcases the **Saga Pattern** - an advanced pattern for managing distributed transactions across multiple microservices without using traditional two-phase commit.

## Overview

When a business operation spans multiple services (inventory, payment, shipping), we can't use traditional ACID transactions. The Saga pattern breaks the transaction into a series of local transactions, each with a compensating action that can undo its effects if a later step fails.

## How It Works

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        SAGA ORCHESTRATOR                                     │
│                                                                             │
│   ┌──────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐            │
│   │ STEP 1   │───▶│ STEP 2   │───▶│ STEP 3   │───▶│ STEP 4   │            │
│   │Inventory │    │ Payment  │    │ Shipping │    │ Notify   │            │
│   │ Reserve  │    │ Process  │    │ Create   │    │ Customer │            │
│   └────┬─────┘    └────┬─────┘    └────┬─────┘    └──────────┘            │
│        │               │               │                                   │
│   ┌────▼─────┐    ┌────▼─────┐    ┌────▼─────┐                            │
│   │COMPENSATE│◀───│COMPENSATE│◀───│COMPENSATE│     (Execute in reverse    │
│   │ Release  │    │ Refund   │    │ Cancel   │      if any step fails)    │
│   └──────────┘    └──────────┘    └──────────┘                            │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

## Running the Demo

```bash
# Navigate to the demo directory
cd 11-advanced-patterns/02-advanced-patterns/demo-saga-pattern

# Build and run
./mvnw spring-boot:run
```

The application starts on **port 8200**.

## API Endpoints

### Successful Order Flow
```bash
# Place an order (all steps succeed)
curl -X POST http://localhost:8200/api/saga/orders
```

### Failure Scenarios (with Compensation)

```bash
# Payment failure - see inventory compensation happen
curl -X POST http://localhost:8200/api/saga/orders/fail-payment

# Shipping failure - see payment refund and inventory release
curl -X POST http://localhost:8200/api/saga/orders/fail-shipping

# Inventory failure - no compensation needed (early failure)
curl -X POST http://localhost:8200/api/saga/orders/fail-inventory
```

### Query Saga State
```bash
# Get specific saga details
curl http://localhost:8200/api/saga/orders/{sagaId}

# List all sagas
curl http://localhost:8200/api/saga/orders

# Get pattern info
curl http://localhost:8200/api/saga/info
```

## Example Output (Successful)

```
═══════════════════════════════════════════════════════════════
🎭 SAGA STARTED: SAGA-A1B2C3D4 for Order: ORD-E5F6G7H8
═══════════════════════════════════════════════════════════════

📦 STEP 1: INVENTORY RESERVATION
─────────────────────────────────────────────────────────────────
📦 [Inventory Step] Reserving inventory for order ORD-E5F6G7H8
📦 [Inventory Step] ✅ Inventory reserved: RES-X1Y2Z3

💳 STEP 2: PAYMENT PROCESSING
─────────────────────────────────────────────────────────────────
💳 [Payment Step] Processing payment of $99.99 for order ORD-E5F6G7H8
💳 [Payment Step] ✅ Payment successful: TXN-A1B2C3

🚚 STEP 3: SHIPMENT CREATION
─────────────────────────────────────────────────────────────────
🚚 [Shipping Step] Creating shipment for order ORD-E5F6G7H8
🚚 [Shipping Step] ✅ Shipment created: SHIP-D4E5F6 (tracking: TRK-G7H8I9J0K1L2)

📧 STEP 4: SEND NOTIFICATION
─────────────────────────────────────────────────────────────────
📧 [Notification Step] Sending confirmation to customer@example.com
📧 [Notification Step] ✅ Confirmation sent

═══════════════════════════════════════════════════════════════
🎭 SAGA COMPLETED SUCCESSFULLY: SAGA-A1B2C3D4
═══════════════════════════════════════════════════════════════
```

## Example Output (With Compensation)

```
═══════════════════════════════════════════════════════════════
🎭 SAGA STARTED: SAGA-M1N2O3P4 for Order: ORD-Q5R6S7T8
═══════════════════════════════════════════════════════════════

📦 STEP 1: INVENTORY RESERVATION
─────────────────────────────────────────────────────────────────
📦 [Inventory Step] ✅ Inventory reserved: RES-U1V2W3

💳 STEP 2: PAYMENT PROCESSING
─────────────────────────────────────────────────────────────────
💳 [Payment Step] ❌ Payment declined (simulated failure)

═══════════════════════════════════════════════════════════════
🎭 SAGA FAILED at step: PAYMENT - Starting COMPENSATION
═══════════════════════════════════════════════════════════════

🔄 STARTING COMPENSATION (Reverse Order)
═══════════════════════════════════════════════════════════════

🔄 COMPENSATING: Inventory
📦 [Inventory Compensation] ✅ Inventory released for RES-U1V2W3

📧 [Notification] Sending failure notification to customer@example.com
📧 [Notification] ✅ Failure notification sent

═══════════════════════════════════════════════════════════════
🔄 COMPENSATION COMPLETED - Saga rolled back successfully
═══════════════════════════════════════════════════════════════
```

## Key Components

### 1. Saga Orchestrator
Central coordinator that:
- Executes saga steps in sequence
- Tracks state of each step
- Triggers compensation on failure
- Manages reverse-order rollback

### 2. Service Steps
Each service implements:
- **Execute**: The forward action
- **Compensate**: The rollback action

### 3. Saga State
Tracks:
- Current status (STARTED, COMPLETED, COMPENSATING, COMPENSATED)
- IDs from each completed step
- Execution log with timestamps
- Error information if failed

## Saga Pattern Types

| Type | Description | This Demo |
|------|-------------|-----------|
| **Orchestration** | Central coordinator controls flow | ✅ Implemented |
| **Choreography** | Services emit events, others react | Alternative approach |

## When to Use

✅ **Use Saga When:**
- Transactions span multiple microservices
- You need eventual consistency
- Long-running transactions
- Services have different databases

❌ **Don't Use When:**
- Single database operations
- Need strict ACID consistency
- Simple request-response patterns

## Best Practices

1. **Idempotent Operations**: Each step should be safely retryable
2. **Compensatable Actions**: Design every action with its undo in mind
3. **Timeout Handling**: Set appropriate timeouts for each step
4. **Saga State Persistence**: Store state durably (database, not memory)
5. **Logging**: Comprehensive logging for debugging distributed flows

## Test Your Knowledge

### Questions

1. **Why can't we use traditional database transactions in a microservices architecture?**

2. **What happens if Step 3 (Shipping) fails after Steps 1 (Inventory) and 2 (Payment) succeed?**

3. **Why does compensation execute in reverse order?**

4. **What's the difference between Orchestration and Choreography saga patterns?**

5. **Why is idempotency important for saga steps?**

### Answers

<details>
<summary>Click to reveal answers</summary>

1. **Traditional transactions don't span services** because each microservice has its own database. Two-phase commit (2PC) is slow, doesn't scale well, and creates tight coupling. Sagas provide eventual consistency through local transactions and compensating actions.

2. **When Shipping fails**, the orchestrator triggers compensation:
   - First: Release inventory (compensate Step 1)
   - Then: Refund payment (compensate Step 2)
   - Notification service sends failure email
   This ensures the system returns to a consistent state.

3. **Reverse order compensation** undoes changes in the opposite order they were made, similar to unwinding a stack. The most recent change is undone first because later steps may depend on earlier ones.

4. **Orchestration vs Choreography**:
   - Orchestration: Central coordinator tells each service what to do (like a conductor)
   - Choreography: Services react to events from each other (like dancers following cues)
   Orchestration is easier to understand and debug; choreography is more decoupled but harder to trace.

5. **Idempotency ensures safety** when steps are retried due to network issues or timeouts. If a payment step is accidentally called twice, an idempotent implementation ensures the customer is only charged once.

</details>

## Production Considerations

For production use, consider:
- **State Persistence**: Use a database instead of in-memory storage
- **Retry Logic**: Implement exponential backoff for transient failures
- **Dead Letter Queue**: Handle permanently failed sagas
- **Monitoring**: Track saga success/failure rates
- **Distributed Tracing**: Correlate logs across services

## Related Patterns

- **Event Sourcing**: Store state changes as events
- **CQRS**: Separate read and write models
- **Outbox Pattern**: Reliable event publishing
- **Compensating Transaction**: The core concept behind saga compensation
