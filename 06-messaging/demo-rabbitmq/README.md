# RabbitMQ Demo

## Overview
Complete demonstration of RabbitMQ messaging patterns with Producer and Consumer.

## Features Demonstrated
- ✅ **Direct Exchange** (One-to-One routing)
- ✅ **Fanout Exchange** (Broadcast to all queues)
- ✅ **Topic Exchange** (Pattern-based routing)
- ✅ Producer sending messages
- ✅ Multiple consumers
- ✅ JSON message serialization

## Prerequisites

### 1. Install RabbitMQ

**Using Docker** (Recommended):
```bash
docker run -d --name rabbitmq \
  -p 5672:5672 \
  -p 15672:15672 \
  rabbitmq:3-management
```

**Or install locally**:
- Download from: https://www.rabbitmq.com/download.html
- Default credentials: `guest/guest`

### 2. Verify RabbitMQ is Running
- Management UI: http://localhost:15672
- Login: guest/guest

## Running the Application

```bash
cd 06-messaging/demo-rabbitmq
mvn spring-boot:run
```

## Testing the Exchanges

### 1. Direct Exchange (One-to-One)

**Send Order Event**:
```bash
curl -X POST http://localhost:8084/api/messages/order \
  -H "Content-Type: application/json" \
  -d '{"customerId":"CUST123","amount":99.99}'
```

**What Happens**:
- Message sent to `direct.exchange` with routing key `order.created`
- Only `order.queue` receives the message
- Order consumer processes it

**Expected Logs**:
```
📤 [DIRECT] Sent Order Created: <uuid>
📥 [ORDER CONSUMER] Received: {...}
✅ [ORDER CONSUMER] Order processed successfully
```

---

### 2. Fanout Exchange (Broadcast)

**Send Broadcast**:
```bash
curl -X POST http://localhost:8084/api/messages/broadcast \
  -H "Content-Type: application/json" \
  -d '{"message":"System maintenance at 2 AM"}'
```

**What Happens**:
- Message sent to `fanout.exchange`
- **ALL** bound queues receive the message:
  - `email.queue` → Email consumer
  - `sms.queue` → SMS consumer

**Expected Logs**:
```
📤 [FANOUT] Broadcast: System maintenance at 2 AM
📧 [EMAIL CONSUMER] Received: System maintenance at 2 AM
✅ [EMAIL CONSUMER] Email sent
📱 [SMS CONSUMER] Received: System maintenance at 2 AM
✅ [SMS CONSUMER] SMS sent
```

---

### 3. Topic Exchange (Pattern Matching)

**Send Email Notification**:
```bash
curl -X POST http://localhost:8084/api/messages/notification/email \
  -H "Content-Type: application/json" \
  -d '{"message":"Welcome to our service!"}'
```

**Send SMS Notification**:
```bash
curl -X POST http://localhost:8084/api/messages/notification/sms \
  -H "Content-Type: application/json" \
  -d '{"message":"Your verification code is 123456"}'
```

**What Happens**:
- Message sent with routing key `notification.email` or `notification.sms`
- `notification.queue` bound with pattern `notification.*` receives both
- Notification consumer processes them

**Expected Logs**:
```
📤 [TOPIC] Sent to notification.email: Welcome to our service!
🔔 [NOTIFICATION CONSUMER] Received: Welcome to our service!
✅ [NOTIFICATION CONSUMER] Notification processed
```

## RabbitMQ Management UI

Access: http://localhost:15672 (guest/guest)

**What to Explore**:
1. **Queues Tab**: See all queues and message counts
2. **Exchanges Tab**: See configured exchanges
3. **Connections**: Active connections from your app
4. **Channels**: Communication channels

## Exchange Types Comparison

| Exchange Type | Routing | Use Case |
|--------------|---------|----------|
| **Direct** | Exact routing key match | Task distribution, specific routing |
| **Fanout** | Broadcast to all | Notifications, cache invalidation |
| **Topic** | Pattern matching | Flexible routing, log aggregation |
| **Headers** | Header-based routing | Complex routing logic |

## Message Flow

### Direct Exchange Flow
```
Producer → direct.exchange → [routing key: order.created] → order.queue → Consumer
```

### Fanout Exchange Flow
```
Producer → fanout.exchange → ├─ email.queue → Email Consumer
                              └─ sms.queue → SMS Consumer
```

### Topic Exchange Flow
```
Producer → topic.exchange → [pattern: notification.*] → notification.queue → Consumer
```

## Testing Scenarios

### Scenario 1: Multiple Orders
```bash
# Send 5 orders
for i in {1..5}; do
  curl -X POST http://localhost:8084/api/messages/order \
    -H "Content-Type: application/json" \
    -d "{\"customerId\":\"CUST$i\",\"amount\":$((i * 10))}"
done
```

Watch logs to see 5 messages processed!

### Scenario 2: Broadcast to Multiple Consumers
```bash
curl -X POST http://localhost:8084/api/messages/broadcast \
  -H "Content-Type: application/json" \
  -d '{"message":"Flash sale starting now!"}'
```

Both email AND SMS consumers receive the same message!

### Scenario 3: Topic Patterns
```bash
# These will all match notification.*
curl -X POST http://localhost:8084/api/messages/notification/email -H "Content-Type: application/json" -d '{"message":"Email msg"}'
curl -X POST http://localhost:8084/api/messages/notification/sms -H "Content-Type: application/json" -d '{"message":"SMS msg"}'
curl -X POST http://localhost:8084/api/messages/notification/push -H "Content-Type: application/json" -d '{"message":"Push msg"}'
```

## Key Concepts

### Durability
- **Durable Queues**: Survive broker restarts
- Configured in: `RabbitMQConfig.java` with `true` parameter

### Acknowledgment
- **Auto Ack**: Message acknowledged automatically after delivery
- Configured in: `application.yml`

### Prefetch
- **Prefetch Count**: Number of unacknowledged messages a consumer can have
- Set to `1` for fair distribution

## Production Considerations

1. **Message Persistence**: Make messages durable
   ```java
   rabbitTemplate.convertAndSend(exchange, routingKey, message,
       msg -> {
           msg.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
           return msg;
       });
   ```

2. **Dead Letter Queue**: Handle failed messages
   ```java
   @Bean
   public Queue queueWithDLQ() {
       return QueueBuilder.durable("main.queue")
           .withArgument("x-dead-letter-exchange", "dlx.exchange")
           .build();
   }
   ```

3. **Retry Logic**: Already configured in application.yml

4. **Monitoring**: Use RabbitMQ management plugins

5. **Connection Pooling**: Configure connection factory

## Troubleshooting

**Can't connect to RabbitMQ**:
```bash
# Check if RabbitMQ is running
docker ps | grep rabbitmq

# Or check service status
sudo systemctl status rabbitmq-server
```

**Messages not being consumed**:
- Check consumer is annotated with `@RabbitListener`
- Verify queue name matches configuration
- Check logs for errors

**Queue not created**:
- Ensure Spring Boot has started fully
- Check `RabbitMQConfig` is being loaded
- Verify RabbitMQ management UI

## Next Steps
1. Implement Dead Letter Queue
2. Add message TTL (Time To Live)
3. Implement priority queues
4. Add request-reply pattern
5. Test with Kafka demo for comparison
