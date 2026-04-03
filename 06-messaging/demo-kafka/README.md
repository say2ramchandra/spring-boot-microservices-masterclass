# Apache Kafka Demo

Interactive demonstration of Apache Kafka integration with Spring Boot including Producer, Consumer, and multiple topics.

## Prerequisites

- Java 17+
- Maven 3.6+
- Docker (for running Kafka) OR local Kafka installation

## Quick Start

### 1. Start Kafka with Docker Compose

```bash
# From this directory
docker-compose up -d

# Verify containers are running
docker-compose ps
```

### 2. Run the Application

```bash
mvn spring-boot:run
```

The application starts on port **8085**.

### 3. Test the APIs

```bash
# Create an order (sends to order-events topic)
curl -X POST http://localhost:8085/api/orders

# Process a payment (sends to payment-events topic)
curl -X POST http://localhost:8085/api/payments

# Send a notification (sends to notification-events topic)
curl -X POST http://localhost:8085/api/notifications

# View message statistics
curl http://localhost:8085/api/messages/stats
```

## Project Structure

```
demo-kafka/
├── pom.xml                          # Maven dependencies
├── docker-compose.yml               # Kafka + Zookeeper setup
├── README.md                        # This file
└── src/main/java/com/masterclass/messaging/kafka/
    ├── KafkaDemoApplication.java    # Main application
    ├── config/
    │   └── KafkaTopicConfig.java    # Topic creation
    ├── model/
    │   ├── OrderEvent.java          # Order event model
    │   ├── PaymentEvent.java        # Payment event model
    │   └── NotificationEvent.java   # Notification event model
    ├── producer/
    │   └── KafkaProducerService.java # Message producer
    ├── consumer/
    │   └── KafkaConsumerService.java # Message consumer
    └── controller/
        └── KafkaController.java      # REST API
```

## Kafka Concepts Demonstrated

### 1. Topics
Three topics with 3 partitions each:
- `order-events` - Order lifecycle events
- `payment-events` - Payment processing events
- `notification-events` - Notification dispatch events

### 2. Producer Features
- **Async sending** with CompletableFuture callbacks
- **Message keys** for partition affinity (same order → same partition)
- **JSON serialization** with Jackson
- **Idempotent producer** for exactly-once semantics

### 3. Consumer Features
- **@KafkaListener** annotation-based consumption
- **Consumer groups** for load balancing
- **Manual acknowledgment** for at-least-once delivery
- **Concurrent consumers** (3 threads per topic)
- **Error handling** patterns

### 4. Configuration
- `application.yml` - All Kafka settings
- Auto topic creation on startup
- JSON serializer/deserializer configuration

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/orders` | Create order event |
| POST | `/api/payments` | Create payment event |
| POST | `/api/notifications` | Create notification event |
| GET | `/api/messages/stats` | View processing statistics |

## Sample Requests

### Create Order with Custom Data

```bash
curl -X POST http://localhost:8085/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "CUST-12345",
    "items": [
      {"productId": "PROD-001", "productName": "Laptop", "quantity": 1, "price": 999.99},
      {"productId": "PROD-002", "productName": "Mouse", "quantity": 2, "price": 29.99}
    ]
  }'
```

### Process Payment

```bash
curl -X POST http://localhost:8085/api/payments \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": "ORD-12345",
    "customerId": "CUST-12345",
    "amount": 1059.97,
    "paymentMethod": "CREDIT_CARD"
  }'
```

### Send Notification

```bash
curl -X POST http://localhost:8085/api/notifications \
  -H "Content-Type: application/json" \
  -d '{
    "recipientId": "USER-12345",
    "recipientEmail": "user@example.com",
    "type": "EMAIL",
    "subject": "Order Shipped",
    "message": "Your order is on the way!"
  }'
```

## Kafka Commands

```bash
# List topics
docker exec -it kafka kafka-topics --list --bootstrap-server localhost:9092

# Describe a topic
docker exec -it kafka kafka-topics --describe --topic order-events --bootstrap-server localhost:9092

# Consume messages from terminal
docker exec -it kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic order-events \
  --from-beginning

# View consumer groups
docker exec -it kafka kafka-consumer-groups --list --bootstrap-server localhost:9092

# Describe consumer group
docker exec -it kafka kafka-consumer-groups \
  --describe \
  --group order-consumer-group \
  --bootstrap-server localhost:9092
```

## Key Learning Points

1. **Producer Best Practices**:
   - Use message keys for ordering guarantees
   - Enable idempotence for exactly-once semantics
   - Handle send failures with callbacks

2. **Consumer Best Practices**:
   - Use consumer groups for horizontal scaling
   - Manual acknowledgment for reliability
   - Implement proper error handling

3. **Topic Design**:
   - Partition count affects parallelism
   - Replication factor for fault tolerance
   - Key-based partitioning for ordering

## Cleanup

```bash
# Stop containers
docker-compose down

# Remove volumes (deletes all Kafka data)
docker-compose down -v
```

## Troubleshooting

### Connection Refused
Ensure Kafka is running:
```bash
docker-compose ps
docker-compose logs kafka
```

### Messages Not Being Consumed
Check consumer group lag:
```bash
docker exec -it kafka kafka-consumer-groups \
  --describe \
  --group order-consumer-group \
  --bootstrap-server localhost:9092
```

## Further Reading

- [Spring Kafka Documentation](https://docs.spring.io/spring-kafka/docs/current/reference/html/)
- [Apache Kafka Documentation](https://kafka.apache.org/documentation/)
- [Kafka Best Practices](https://docs.confluent.io/platform/current/kafka/post-deployment.html)
