# REST Communication Demo

Demonstrates synchronous communication between microservices using **RestTemplate** and **WebClient**.

## Architecture

```
┌─────────────────┐         ┌─────────────────┐
│  Order Service  │ ──REST─→ │  Payment Service│
│   Port: 8091    │ ←─────   │   Port: 8092    │
└─────────────────┘          └─────────────────┘
```

## Services

### 1. Payment Service (Port 8092)
- Processes payment requests
- Validates payment details
- Returns payment status

### 2. Order Service (Port 8091)
- Creates orders
- Calls Payment Service to process payment
- Demonstrates both RestTemplate and WebClient

## Running the Demo

### Start Payment Service
```bash
cd payment-service
mvn spring-boot:run
```

### Start Order Service
```bash
cd order-service
mvn spring-boot:run
```

## Testing

### Create Order (uses RestTemplate)
```bash
curl -X POST http://localhost:8091/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "productId": 100,
    "amount": 250.00
  }'
```

### Create Order with WebClient
```bash
curl -X POST http://localhost:8091/api/orders/webclient \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "productId": 100,
    "amount": 250.00
  }'
```

## Key Concepts

1. **RestTemplate**: Traditional blocking approach
2. **WebClient**: Modern reactive approach
3. **Error Handling**: Timeout and exception handling
4. **Load Balancing**: Service discovery integration
