# CQRS Pattern Demo

This demo showcases **CQRS (Command Query Responsibility Segregation)** - an advanced pattern that separates read and write operations into different models, allowing each to be optimized independently.

## Overview

Traditional CRUD applications use the same data model for both reading and writing. CQRS splits this into:
- **Commands** - Operations that change state (Create, Update, Delete)
- **Queries** - Operations that read state without modifying it

## Architecture

```
┌──────────────────────────────────────────────────────────────────────────────┐
│                              CLIENT REQUEST                                   │
└──────────────────────────────┬───────────────────────────────────────────────┘
                               │
            ┌──────────────────┴──────────────────┐
            │                                      │
            ▼                                      ▼
┌───────────────────────┐              ┌───────────────────────┐
│   COMMAND CONTROLLER  │              │   QUERY CONTROLLER    │
│  POST /api/products   │              │  GET /api/products    │
│  PUT /api/products    │              │  GET /api/products/   │
│  POST /api/orders     │              │      search           │
└───────────┬───────────┘              └───────────┬───────────┘
            │                                      │
            ▼                                      ▼
┌───────────────────────┐              ┌───────────────────────┐
│   COMMAND HANDLER     │              │    QUERY HANDLER      │
│  - Validation         │              │  - No business logic  │
│  - Business Logic     │              │  - Just data retrieval│
│  - State Changes      │              │                       │
└───────────┬───────────┘              └───────────┬───────────┘
            │                                      │
            ▼                                      ▼
┌───────────────────────┐              ┌───────────────────────┐
│     WRITE MODEL       │───SYNC──────▶│     READ MODEL        │
│  (Normalized)         │   (Event)    │  (Denormalized)       │
│  - ProductWriteModel  │              │  - ProductReadModel   │
│  - Version control    │              │  - Pre-computed fields│
│  - Business rules     │              │  - Indexed for search │
└───────────────────────┘              └───────────────────────┘
```

## Running the Demo

```bash
# Navigate to the demo directory
cd 11-advanced-patterns/02-advanced-patterns/demo-cqrs-pattern

# Build and run
./mvnw spring-boot:run
```

The application starts on **port 8201**.

## API Endpoints

### Setup: Create Sample Data
```bash
# Create sample products with different stock levels
curl -X POST http://localhost:8201/api/products/sample-data
```

### COMMAND Operations (Write)

```bash
# Create a new product
curl -X POST http://localhost:8201/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Gaming Mouse",
    "description": "High-precision gaming mouse",
    "price": 79.99,
    "stockQuantity": 100,
    "category": "GAMING",
    "sku": "GM-001"
  }'

# Update a product
curl -X PUT http://localhost:8201/api/products/{id} \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Gaming Mouse Pro",
    "price": 89.99,
    "stockQuantity": 150
  }'

# Place an order (reduces stock)
curl -X POST http://localhost:8201/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "CUST-001",
    "customerEmail": "customer@example.com",
    "items": [
      {"productId": "{productId}", "quantity": 2}
    ],
    "shippingAddress": "123 Main St"
  }'
```

### QUERY Operations (Read)

```bash
# Get all products
curl http://localhost:8201/api/products

# Get product by ID
curl http://localhost:8201/api/products/{id}

# Search products
curl "http://localhost:8201/api/products/search?q=wireless"

# Get products by category
curl http://localhost:8201/api/products/category/ELECTRONICS

# Get products by stock status
curl http://localhost:8201/api/products/stock/LOW_STOCK
curl http://localhost:8201/api/products/stock/IN_STOCK
curl http://localhost:8201/api/products/stock/OUT_OF_STOCK

# Get low stock alerts
curl http://localhost:8201/api/products/alerts/low-stock

# Get dashboard summary
curl http://localhost:8201/api/products/dashboard

# Get top selling products
curl http://localhost:8201/api/products/top-selling

# Get CQRS pattern info
curl http://localhost:8201/api/cqrs/info
```

## Write Model vs Read Model

### Write Model (ProductWriteModel)

```java
@Entity
@Table(name = "products")
public class ProductWriteModel {
    private String id;
    private String name;
    private BigDecimal price;
    private Integer stockQuantity;
    private String category;
    private String sku;
    private Boolean active;
    
    @Version
    private Long version;  // Optimistic locking
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Business logic methods
    public void reserveStock(int quantity) { ... }
    public void addStock(int quantity) { ... }
}
```

**Optimized for:**
- Data integrity
- Business rule enforcement
- Concurrency control (optimistic locking)

### Read Model (ProductReadModel)

```java
@Entity
@Table(name = "product_read_view")
public class ProductReadModel {
    private String id;
    private String name;
    private BigDecimal price;
    private String formattedPrice;      // Pre-computed: "$29.99"
    private Integer stockQuantity;
    private StockStatus stockStatus;    // Pre-computed: IN_STOCK/LOW_STOCK/OUT_OF_STOCK
    private String category;
    private String categoryDisplayName; // Pre-computed: "Electronics"
    private Integer totalSold;          // Aggregated
    private Double averageRating;       // Aggregated from reviews
    private Integer reviewCount;        // Aggregated
}
```

**Optimized for:**
- Fast queries with indexes
- Pre-computed values (no runtime calculations)
- Denormalized data (no JOINs needed)

## Key Concepts

### Commands
- Named as imperatives: `CreateProduct`, `UpdateProduct`, `PlaceOrder`
- Contain all data needed to perform the operation
- Validated before execution
- May be rejected if validation fails

### Queries
- Named as questions: `GetProduct`, `SearchProducts`, `GetDashboard`
- Never modify state
- Can be cached aggressively
- Optimized for specific use cases

### Synchronization
In this demo, write model changes sync to read model synchronously. In production:
- Use events (ProductCreatedEvent, ProductUpdatedEvent)
- Process asynchronously via message queue
- Accept eventual consistency

## When to Use CQRS

✅ **Good fit when:**
- Read and write patterns are very different
- Complex queries that benefit from denormalization
- Need to scale reads and writes independently
- Domain has complex business rules on write side
- Building event-sourced systems

❌ **Avoid when:**
- Simple CRUD application
- Read/write patterns are similar
- Strong consistency is required
- Small application that doesn't need the complexity

## Test Your Knowledge

### Questions

1. **What problem does CQRS solve that traditional CRUD doesn't?**

2. **Why does the read model have pre-computed fields like `formattedPrice` and `stockStatus`?**

3. **What is eventual consistency and why is it acceptable in CQRS?**

4. **How would you handle a query that needs data from multiple aggregates?**

5. **Why is optimistic locking (`@Version`) on the write model but not the read model?**

### Answers

<details>
<summary>Click to reveal answers</summary>

1. **CQRS solves the read/write optimization dilemma.** Traditional CRUD uses one model for both operations, requiring trade-offs. CQRS lets you optimize reads (denormalization, caching, indexes) and writes (validation, business rules, transactions) independently.

2. **Pre-computed fields eliminate runtime calculations.** Instead of computing `$29.99` or `LOW_STOCK` on every request, we calculate once during write and store it. This makes queries faster, especially under high load.

3. **Eventual consistency means the read model may lag slightly behind the write model** (milliseconds to seconds). This is acceptable because:
   - Most UIs don't need real-time data
   - Users can tolerate slight delays for dashboard views
   - It enables massive scaling of the read side
   - Critical operations still use the write model

4. **Create a dedicated read model (view) that spans aggregates.** For example, an `OrderSummaryView` could denormalize customer name, product names, and shipping status into one table. Update it when any referenced aggregate changes.

5. **Optimistic locking prevents lost updates on concurrent writes.** The read model doesn't need it because:
   - It's never updated by users directly
   - It's only updated by the system during synchronization
   - Concurrent sync events would be serialized anyway

</details>

## Production Considerations

### Scaling
- Read side can scale horizontally with replicas
- Write side may need sharding for high write volumes
- Read model can use different database technology (e.g., Elasticsearch for search)

### Event-Driven Sync
```java
// Instead of synchronous sync:
@EventListener
public void on(ProductCreatedEvent event) {
    // Update read model asynchronously
    readModelProjector.project(event);
}
```

### Multiple Read Models
A single write model may project to multiple read views:
- ProductCatalogView (for browsing)
- ProductInventoryView (for warehouse)
- ProductAnalyticsView (for reporting)

## Related Patterns

- **Event Sourcing**: Store state changes as events
- **Saga Pattern**: Manage distributed transactions
- **Domain-Driven Design**: Aggregate roots, bounded contexts
- **Materialized View**: Database-level read optimization
