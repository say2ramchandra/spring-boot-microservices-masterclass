# Messaging Demo with RabbitMQ

Demonstrates asynchronous communication between microservices using **RabbitMQ**.

## Architecture

```
┌──────────────┐        ┌──────────┐        ┌─────────────────┐
│Order Producer│ ─────→ │ RabbitMQ │ ─────→ │ Order Consumer  │
│  Port: 8093  │        │  Queue   │        │   Port: 8094    │
└──────────────┘        └──────────┘        └─────────────────┘
```

## Prerequisites

### Install RabbitMQ with Docker
```bash
docker run -d --name rabbitmq \
  -p 5672:5672 \
  -p 15672:15672 \
  rabbitmq:3-management
```

**Access RabbitMQ Management UI**:
- URL: http://localhost:15672
- Username: `guest`
- Password: `guest`

## Services

### 1. Order Producer (Port 8093)
- Publishes order messages to RabbitMQ
- REST API for creating orders

### 2. Order Consumer (Port 8094)
- Listens to order messages from RabbitMQ
- Processes orders asynchronously

## Running the Demo

### 1. Start RabbitMQ
```bash
docker start rabbitmq
```

### 2. Start Order Consumer
```bash
cd order-consumer
mvn spring-boot:run
```

### 3. Start Order Producer
```bash
cd order-producer
mvn spring-boot:run
```

## Testing

### Create Order (Async)
```bash
curl -X POST http://localhost:8093/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "productId": 100,
    "quantity": 5,
    "amount": 500.00
  }'
```

Response (immediate):
```json
{
  "message": "Order submitted successfully",
  "orderId": 1234
}
```

Check consumer logs to see async processing!

### Send Bulk Orders
```bash
curl -X POST http://localhost:8093/api/orders/bulk \
  -H "Content-Type: application/json" \
  -d '{
    "count": 10,
    "userId": 1
  }'
```

## RabbitMQ Management

Visit http://localhost:15672 to see:
- Queues and message counts
- Exchanges and bindings
- Consumer connections
- Message rates

## Key Concepts

1. **Fire-and-Forget**: Producer doesn't wait for processing
2. **Decoupling**: Services don't depend on each other's availability
3. **Scalability**: Multiple consumers can process messages in parallel
4. **Reliability**: Messages persisted until acknowledged
