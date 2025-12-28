# Module 06: Messaging & Event-Driven Architecture - Demos

## 🎯 Overview

This module demonstrates asynchronous messaging patterns using RabbitMQ and Apache Kafka.

## 📦 Demo Projects

| Project | Port | Technology | Description |
|---------|------|------------|-------------|
| **demo-rabbitmq** | 8084 | RabbitMQ | Exchange types & messaging patterns |
| **demo-kafka** | 8085 | Apache Kafka | Event streaming & partitioning |

## ⚠️ Important Note

These demos require external message brokers to be running:
- **RabbitMQ**: Port 5672 (AMQP), 15672 (Management UI)
- **Apache Kafka**: Port 9092

## 🐰 RabbitMQ Demo

### Setup RabbitMQ

**Using Docker** (Recommended):
```bash
docker run -d --name rabbitmq \
  -p 5672:5672 \
  -p 15672:15672 \
  rabbitmq:3-management
```

**Verify**:
- Management UI: http://localhost:15672
- Login: guest/guest

### Run RabbitMQ Demo

```bash
cd 06-messaging/demo-rabbitmq
mvn spring-boot:run
```

### Features Demonstrated
- ✅ **Direct Exchange** - One-to-one message routing
- ✅ **Fanout Exchange** - Broadcast to all queues
- ✅ **Topic Exchange** - Pattern-based routing
- ✅ Multiple consumers
- ✅ Message acknowledgment

### Quick Test

**Direct Exchange** (Order processing):
```bash
curl -X POST http://localhost:8084/api/messages/order \
  -H "Content-Type: application/json" \
  -d '{"customerId":"CUST123","amount":99.99}'
```

**Fanout Exchange** (Broadcast):
```bash
curl -X POST http://localhost:8084/api/messages/broadcast \
  -H "Content-Type: application/json" \
  -d '{"message":"System maintenance at 2 AM"}'
```

**Topic Exchange** (Pattern routing):
```bash
curl -X POST http://localhost:8084/api/messages/notification/email \
  -H "Content-Type: application/json" \
  -d '{"message":"Welcome email"}'
```

---

## 🔥 Kafka Demo (Coming Soon)

### Setup Kafka

**Using Docker Compose**:
```yaml
version: '3'
services:
  zookeeper:
    image: confluentinc/cp-zookeeper:latest
    ports:
      - "2181:2181"
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      
  kafka:
    image: confluentinc/cp-kafka:latest
    ports:
      - "9092:9092"
    environment:
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
```

```bash
docker-compose up -d
```

### Run Kafka Demo

```bash
cd 06-messaging/demo-kafka
mvn spring-boot:run
```

### Features (Planned)
- ✅ Producer sending events
- ✅ Consumer with manual offset management
- ✅ Partitioning strategy
- ✅ Consumer groups
- ✅ Event sourcing pattern

---

## 📊 RabbitMQ vs Kafka Comparison

| Feature | RabbitMQ | Kafka |
|---------|----------|-------|
| **Type** | Message Broker | Event Streaming Platform |
| **Pattern** | Publisher-Subscriber | Publish-Subscribe with Log |
| **Message Retention** | Until consumed | Configurable (days/size) |
| **Ordering** | Per queue | Per partition |
| **Use Case** | Task queues, RPC | Event sourcing, log aggregation |
| **Protocol** | AMQP | Custom binary protocol |
| **Complexity** | Easier to start | Steeper learning curve |

## 🎯 When to Use What?

### Use RabbitMQ When:
- ✅ You need task distribution (work queues)
- ✅ Request-reply pattern is required
- ✅ Complex routing logic needed
- ✅ Guaranteed delivery is critical
- ✅ Smaller message volumes

### Use Kafka When:
- ✅ High throughput required (millions of messages/sec)
- ✅ Event sourcing architecture
- ✅ Stream processing needed
- ✅ Long message retention required
- ✅ Multiple consumers need same data

## 🧪 Testing Both Demos

### Scenario 1: Order Processing Flow

**RabbitMQ Approach**:
```bash
# Send order
curl -X POST http://localhost:8084/api/messages/order \
  -H "Content-Type: application/json" \
  -d '{"customerId":"C123","amount":50}'

# Observe: Single consumer processes immediately
```

**Kafka Approach** (when available):
```bash
# Publish order event
curl -X POST http://localhost:8085/api/events/order \
  -H "Content-Type: application/json" \
  -d '{"customerId":"C123","amount":50}'

# Observe: Event persisted, multiple consumers can replay
```

### Scenario 2: Broadcast Notifications

**RabbitMQ - Fanout Exchange**:
```bash
curl -X POST http://localhost:8084/api/messages/broadcast \
  -H "Content-Type: application/json" \
  -d '{"message":"Server restart in 5 min"}'

# Both email and SMS consumers receive simultaneously
```

**Kafka - Topic Subscription**:
```bash
# Multiple consumer groups can independently consume same event
# Each consumer group tracks its own offset
```

## 📚 Learning Path

1. **Start with RabbitMQ Demo**
   - Understand message queues
   - Learn exchange types
   - Practice routing patterns
   - See consumer acknowledgment

2. **Progress to Kafka** (when ready)
   - Understand event logs
   - Learn partitioning
   - Practice consumer groups
   - Explore offset management

3. **Compare Architectures**
   - When to use each
   - Trade-offs
   - Integration patterns

## 🛠️ Troubleshooting

### RabbitMQ Issues

**Can't connect**:
```bash
# Check if RabbitMQ is running
docker ps | grep rabbitmq

# Check logs
docker logs rabbitmq
```

**Messages not consumed**:
- Verify consumer is running
- Check queue bindings in Management UI
- Review consumer logs

### Kafka Issues

**Broker not available**:
```bash
# Check Kafka and Zookeeper
docker-compose ps

# Test connection
kafka-topics.sh --list --bootstrap-server localhost:9092
```

**Consumer not receiving**:
- Verify topic exists
- Check consumer group membership
- Review offset position

## 🔗 Integration with Previous Modules

### With Spring Cloud (Module 05)
- Use messaging for async communication between microservices
- Replace synchronous Feign calls with event-driven patterns
- Decouple services using message brokers

### Example Flow:
```
Order Service → Publishes OrderCreated Event → Message Broker
                                              ↓
                         ┌────────────────────┼─────────────────┐
                         ↓                    ↓                 ↓
                    Email Service    Payment Service    Inventory Service
```

## 📖 Resources

- [RabbitMQ Tutorials](https://www.rabbitmq.com/getstarted.html)
- [Kafka Documentation](https://kafka.apache.org/documentation/)
- [Spring AMQP](https://spring.io/projects/spring-amqp)
- [Spring for Apache Kafka](https://spring.io/projects/spring-kafka)

## ✅ Success Criteria

You've mastered this module when you can:
- ✅ Set up RabbitMQ with Docker
- ✅ Create exchanges, queues, and bindings
- ✅ Send messages through different exchange types
- ✅ Implement consumers with proper acknowledgment
- ✅ Understand when to use RabbitMQ vs Kafka
- ✅ Design event-driven architectures

## 🚀 Next Module

**Module 07: Security**
- Secure your microservices and messaging
- JWT authentication
- OAuth2 authorization
- API Gateway security

---

**💡 Tip**: Run the RabbitMQ Management UI alongside the demo to see messages flow in real-time!
