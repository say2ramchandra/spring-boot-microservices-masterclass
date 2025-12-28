# Module 06: Messaging & Event-Driven Architecture

> **Build asynchronous, event-driven microservices**

## 📚 Module Overview

Learn to build scalable, loosely-coupled systems using message queues and event streaming. Master asynchronous communication patterns with RabbitMQ and Apache Kafka.

---

## 🎯 Learning Objectives

By the end of this module, you will:

- ✅ Understand messaging patterns (Queue, Pub/Sub, Topics)
- ✅ Implement messaging with RabbitMQ
- ✅ Build event streaming with Apache Kafka
- ✅ Design event-driven architectures
- ✅ Implement Saga pattern for distributed transactions
- ✅ Handle message reliability and ordering
- ✅ Implement event sourcing and CQRS

---

## 📂 Module Structure

```
06-messaging/
├── README.md
├── 01-rabbitmq-basics/
│   ├── README.md
│   ├── demo-rabbitmq-producer/       ← Message producer
│   └── demo-rabbitmq-consumer/       ← Message consumer
├── 02-kafka-basics/
│   ├── README.md
│   ├── demo-kafka-producer/          ← Event producer
│   └── demo-kafka-consumer/          ← Event consumer
├── 03-event-driven/
│   ├── README.md
│   └── demo-order-events/            ← Order processing with events
└── 04-saga-pattern/
    ├── README.md
    └── demo-distributed-transaction/ ← Saga orchestration
```

---

## 🔑 Key Concepts

### Synchronous vs Asynchronous Communication

```
SYNCHRONOUS (REST):
┌─────────┐   Request    ┌─────────┐
│Service A│ ───────────→ │Service B│
│         │ ←─────────── │         │
└─────────┘   Response   └─────────┘
              (Blocking)

Characteristics:
✅ Immediate response
✅ Simple to implement
❌ Tight coupling
❌ Service B must be available
❌ Can't handle high load bursts


ASYNCHRONOUS (Messaging):
┌─────────┐   Message   ┌─────────┐   Message   ┌─────────┐
│Service A│ ──────────→ │  Queue  │ ──────────→ │Service B│
│         │             │ Broker  │             │         │
└─────────┘             └─────────┘             └─────────┘
           (Non-blocking)

Characteristics:
✅ Loose coupling
✅ Service B can be offline
✅ Load leveling
✅ Scalability
❌ Eventual consistency
❌ More complex
```

---

## 🐰 RabbitMQ

### Architecture

```
Producer → Exchange → Queue → Consumer
             ↓
         Routing Key
```

### Exchange Types

#### 1. Direct Exchange
```
Producer → [Direct Exchange] → Queue (exact routing key match)

Example:
Message with routing key "order.created"
  → Routed to queue bound with "order.created"
```

#### 2. Fanout Exchange
```
Producer → [Fanout Exchange] → Queue 1
                            → Queue 2
                            → Queue 3
(Broadcast to all queues)
```

#### 3. Topic Exchange
```
Producer → [Topic Exchange] → Queue (pattern matching)

Patterns:
- "order.*" matches "order.created", "order.updated"
- "*.notification" matches "email.notification", "sms.notification"
- "#.error" matches "system.error", "app.db.error"
```

#### 4. Headers Exchange
```
Routes based on message headers instead of routing key
```

---

### RabbitMQ Implementation

**Producer**:
```java
@Service
public class OrderEventPublisher {
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    private static final String EXCHANGE = "order.exchange";
    
    public void publishOrderCreated(OrderCreatedEvent event) {
        rabbitTemplate.convertAndSend(
            EXCHANGE,
            "order.created",
            event
        );
        log.info("Published OrderCreatedEvent: {}", event);
    }
    
    public void publishOrderShipped(OrderShippedEvent event) {
        rabbitTemplate.convertAndSend(
            EXCHANGE,
            "order.shipped",
            event
        );
        log.info("Published OrderShippedEvent: {}", event);
    }
}
```

**Configuration**:
```java
@Configuration
public class RabbitMQConfig {
    
    @Bean
    public Exchange orderExchange() {
        return ExchangeBuilder
            .topicExchange("order.exchange")
            .durable(true)
            .build();
    }
    
    @Bean
    public Queue inventoryQueue() {
        return QueueBuilder
            .durable("inventory.queue")
            .build();
    }
    
    @Bean
    public Queue notificationQueue() {
        return QueueBuilder
            .durable("notification.queue")
            .build();
    }
    
    @Bean
    public Binding inventoryBinding(Queue inventoryQueue, Exchange orderExchange) {
        return BindingBuilder
            .bind(inventoryQueue)
            .to(orderExchange)
            .with("order.created")
            .noargs();
    }
    
    @Bean
    public Binding notificationBinding(Queue notificationQueue, Exchange orderExchange) {
        return BindingBuilder
            .bind(notificationQueue)
            .to(orderExchange)
            .with("order.*")  // All order events
            .noargs();
    }
}
```

**Consumer**:
```java
@Service
public class OrderEventListener {
    
    @RabbitListener(queues = "inventory.queue")
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("Received OrderCreatedEvent: {}", event);
        // Update inventory
        inventoryService.reserveStock(event.getProductId(), event.getQuantity());
    }
    
    @RabbitListener(queues = "notification.queue")
    public void handleOrderEvents(Message message) {
        String routingKey = message.getMessageProperties().getReceivedRoutingKey();
        log.info("Received message with routing key: {}", routingKey);
        
        if (routingKey.equals("order.created")) {
            // Send order confirmation email
        } else if (routingKey.equals("order.shipped")) {
            // Send shipping notification
        }
    }
}
```

---

## 🌊 Apache Kafka

### Architecture

```
Producer → Topic (Partition 0) → Consumer Group A
                                 → Consumer Group B
        → Topic (Partition 1) → Consumer Group A
                                 → Consumer Group B
        → Topic (Partition 2) → Consumer Group A
                                 → Consumer Group B

Key Concepts:
- Topic: Category of messages
- Partition: Ordered, immutable sequence of messages
- Consumer Group: Group of consumers sharing work
- Offset: Position in partition
```

### Kafka vs RabbitMQ

| Feature | RabbitMQ | Kafka |
|---------|----------|-------|
| **Use Case** | Task queues, RPC | Event streaming, logs |
| **Message Model** | Push to consumer | Consumer pulls |
| **Ordering** | Per queue | Per partition |
| **Retention** | Until consumed | Time-based (configurable) |
| **Replay** | ❌ No | ✅ Yes |
| **Throughput** | Moderate | Very high |
| **Best For** | Complex routing | High-volume events |

---

### Kafka Implementation

**Producer**:
```java
@Service
public class OrderEventProducer {
    
    @Autowired
    private KafkaTemplate<String, OrderEvent> kafkaTemplate;
    
    private static final String TOPIC = "order-events";
    
    public void sendOrderCreatedEvent(OrderCreatedEvent event) {
        // Key determines partition (same key → same partition)
        String key = event.getOrderId().toString();
        
        kafkaTemplate.send(TOPIC, key, event)
            .whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Sent event to partition {}: {}",
                        result.getRecordMetadata().partition(), event);
                } else {
                    log.error("Failed to send event", ex);
                }
            });
    }
}
```

**Configuration**:
```java
@Configuration
public class KafkaConfig {
    
    @Bean
    public ProducerFactory<String, OrderEvent> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        config.put(ProducerConfig.ACKS_CONFIG, "all");  // Reliability
        config.put(ProducerConfig.RETRIES_CONFIG, 3);
        return new DefaultKafkaProducerFactory<>(config);
    }
    
    @Bean
    public KafkaTemplate<String, OrderEvent> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
    
    @Bean
    public ConsumerFactory<String, OrderEvent> consumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "order-service-group");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return new DefaultKafkaConsumerFactory<>(config);
    }
}
```

**Consumer**:
```java
@Service
public class OrderEventConsumer {
    
    @KafkaListener(
        topics = "order-events",
        groupId = "inventory-service-group"
    )
    public void consumeOrderEvent(
            ConsumerRecord<String, OrderEvent> record) {
        
        log.info("Consumed event from partition {}, offset {}: {}",
            record.partition(), record.offset(), record.value());
        
        OrderEvent event = record.value();
        
        if (event instanceof OrderCreatedEvent) {
            handleOrderCreated((OrderCreatedEvent) event);
        } else if (event instanceof OrderCancelledEvent) {
            handleOrderCancelled((OrderCancelledEvent) event);
        }
    }
    
    private void handleOrderCreated(OrderCreatedEvent event) {
        // Reserve inventory
        inventoryService.reserve(event.getProductId(), event.getQuantity());
    }
    
    private void handleOrderCancelled(OrderCancelledEvent event) {
        // Release inventory
        inventoryService.release(event.getProductId(), event.getQuantity());
    }
}
```

---

## 🎭 Event-Driven Patterns

### 1. Event Notification

**Simple event notification between services**

```
Order Service          Event Bus          Email Service
     │                     │                    │
     │  OrderCreated       │                    │
     ├────────────────────→│                    │
     │                     ├───────────────────→│
     │                     │   Send Email       │
     │                     │                    ├─→ 📧
```

### 2. Event-Carried State Transfer

**Events contain all necessary data**

```java
public class OrderCreatedEvent {
    private Long orderId;
    private Long customerId;
    private String customerEmail;  // Carried state
    private String customerName;   // Carried state
    private List<OrderItem> items;
    private BigDecimal totalAmount;
    // ... all data needed by consumers
}

// Consumers don't need to call back to Order Service
```

### 3. Event Sourcing

**Store all state changes as events**

```
Traditional:
┌────────────────┐
│   Order Table  │
│ ID │ Status    │
│ 1  │ SHIPPED   │  ← Current state only
└────────────────┘

Event Sourcing:
┌─────────────────────────────┐
│      Event Store            │
│ OrderCreated (timestamp)    │
│ OrderPaid (timestamp)       │
│ OrderShipped (timestamp)    │  ← Full history
└─────────────────────────────┘
         ↓
   Replay events → Current state
```

**Benefits**:
- ✅ Complete audit trail
- ✅ Time travel (rebuild state at any point)
- ✅ Event replay
- ✅ Easy debugging

### 4. CQRS (Command Query Responsibility Segregation)

**Separate read and write models**

```
Commands (Write)                  Queries (Read)
      ↓                                 ↑
┌──────────────┐    Events      ┌──────────────┐
│ Write Model  │ ──────────────→│  Read Model  │
│ (Normalized) │                │ (Denormalized)│
└──────────────┘                └──────────────┘
      ↓                                 ↑
┌──────────────┐                ┌──────────────┐
│ Write DB     │                │  Read DB     │
│ (PostgreSQL) │                │  (MongoDB)   │
└──────────────┘                └──────────────┘
```

---

## 🔄 Saga Pattern

**Handle distributed transactions across microservices**

### Choreography-Based Saga

**Services react to events (decentralized)**

```
Order Service    Payment Service   Inventory Service   Shipping Service
     │                 │                  │                   │
     │ OrderCreated    │                  │                   │
     ├────────────────→│                  │                   │
     │                 │ PaymentProcessed │                   │
     │                 ├─────────────────→│                   │
     │                 │                  │ InventoryReserved │
     │                 │                  ├──────────────────→│
     │                 │                  │                   │ ShipOrder
     │                 │                  │                   ├────────→
     
     
If any step fails, publish compensating events:
     
     │                 │ PaymentFailed    │                   │
     │                 ├─────────────────→│                   │
     │                 │                  │ ReleaseInventory  │
     │ CancelOrder     │                  │                   │
     ←────────────────┤                  │                   │
```

**Implementation**:

```java
// Order Service
@Service
public class OrderService {
    
    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        Order order = new Order(request);
        order.setStatus(OrderStatus.PENDING);
        orderRepository.save(order);
        
        // Publish event
        eventPublisher.publishOrderCreated(new OrderCreatedEvent(order));
        
        return order;
    }
    
    @EventListener
    public void handlePaymentFailed(PaymentFailedEvent event) {
        Order order = orderRepository.findById(event.getOrderId());
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }
}

// Payment Service
@Service
public class PaymentService {
    
    @EventListener
    public void handleOrderCreated(OrderCreatedEvent event) {
        try {
            Payment payment = processPayment(event);
            payment.setStatus(PaymentStatus.COMPLETED);
            paymentRepository.save(payment);
            
            eventPublisher.publishPaymentProcessed(
                new PaymentProcessedEvent(payment));
                
        } catch (PaymentException ex) {
            eventPublisher.publishPaymentFailed(
                new PaymentFailedEvent(event.getOrderId()));
        }
    }
}
```

### Orchestration-Based Saga

**Central coordinator manages saga (centralized)**

```
                  Saga Orchestrator
                        │
        ┌───────────────┼───────────────┬───────────────┐
        ↓               ↓               ↓               ↓
   Order Service   Payment Service  Inventory     Shipping
        │               │               │               │
        │ CreateOrder   │               │               │
        ←───────────────┤               │               │
        │               │ ProcessPayment│               │
        │               ←───────────────┤               │
        │               │               │ ReserveStock  │
        │               │               ←───────────────┤
        │               │               │               │ Ship
        │               │               │               ←──────
```

**Implementation**:

```java
@Service
public class OrderSagaOrchestrator {
    
    public void executeOrderSaga(CreateOrderRequest request) {
        SagaContext context = new SagaContext(request);
        
        try {
            // Step 1: Create Order
            Order order = orderService.createOrder(request);
            context.setOrder(order);
            
            // Step 2: Process Payment
            Payment payment = paymentService.processPayment(order);
            context.setPayment(payment);
            
            // Step 3: Reserve Inventory
            Inventory inventory = inventoryService.reserve(order);
            context.setInventory(inventory);
            
            // Step 4: Ship Order
            Shipment shipment = shippingService.ship(order);
            context.setShipment(shipment);
            
            // Success!
            orderService.completeOrder(order.getId());
            
        } catch (Exception ex) {
            // Compensate (rollback)
            compensate(context, ex);
        }
    }
    
    private void compensate(SagaContext context, Exception ex) {
        log.error("Saga failed, compensating...", ex);
        
        if (context.getShipment() != null) {
            shippingService.cancelShipment(context.getShipment());
        }
        if (context.getInventory() != null) {
            inventoryService.release(context.getInventory());
        }
        if (context.getPayment() != null) {
            paymentService.refund(context.getPayment());
        }
        if (context.getOrder() != null) {
            orderService.cancelOrder(context.getOrder().getId());
        }
    }
}
```

---

## 💡 Best Practices

### 1. Idempotency

**Handle duplicate messages gracefully**

```java
@Service
public class OrderEventHandler {
    
    private final Set<String> processedEventIds = new ConcurrentHashSet<>();
    
    @RabbitListener(queues = "order.queue")
    public void handleOrderEvent(OrderEvent event) {
        String eventId = event.getEventId();
        
        // Check if already processed
        if (processedEventIds.contains(eventId)) {
            log.warn("Duplicate event ignored: {}", eventId);
            return;
        }
        
        // Process event
        processOrder(event);
        
        // Mark as processed
        processedEventIds.add(eventId);
    }
}
```

### 2. Dead Letter Queue

**Handle failed messages**

```java
@Configuration
public class RabbitMQConfig {
    
    @Bean
    public Queue mainQueue() {
        return QueueBuilder
            .durable("main.queue")
            .withArgument("x-dead-letter-exchange", "dlx.exchange")
            .withArgument("x-dead-letter-routing-key", "failed")
            .build();
    }
    
    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder
            .durable("dead-letter.queue")
            .build();
    }
}
```

### 3. Message Ordering

**Ensure order with partitioning**

```java
// Kafka: Use same key for related messages
kafkaTemplate.send(
    "order-events",
    orderId.toString(),  // Key → determines partition
    event
);

// All events for same orderId go to same partition
// → Guaranteed order within partition
```

---

## 📊 Monitoring & Observability

### RabbitMQ Management UI

```
http://localhost:15672
Username: guest
Password: guest

Features:
- Queue statistics
- Message rates
- Consumer connections
- Message browsing
```

### Kafka UI (Kafdrop)

```
http://localhost:9000

Features:
- Topic overview
- Consumer group lag
- Message browsing
- Partition details
```

---

## 🎓 When to Use What?

### Use RabbitMQ When:
- ✅ Complex routing needed
- ✅ Task queues and work distribution
- ✅ Request/reply patterns
- ✅ Priority queues
- ✅ Lower message volumes

### Use Kafka When:
- ✅ High throughput needed (millions of messages)
- ✅ Event streaming and logs
- ✅ Need to replay events
- ✅ Real-time processing
- ✅ Multiple consumer groups

---

## 📚 Next Steps

- Complete messaging demos
- Move to **[Module 07: Security](../07-security/)** for securing microservices
- Learn **[Module 08: Observability](../08-observability/)** for monitoring

---

_Build scalable event-driven systems! 🚀_
