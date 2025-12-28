# Section 02: Service Communication

## Table of Contents
1. [Introduction](#introduction)
2. [Types of Communication](#types-of-communication)
3. [Synchronous Communication](#synchronous-communication)
4. [Asynchronous Communication](#asynchronous-communication)
5. [Communication Patterns](#communication-patterns)
6. [REST Communication](#rest-communication)
7. [Message-Based Communication](#message-based-communication)
8. [Choosing Communication Style](#choosing-communication-style)
9. [Best Practices](#best-practices)
10. [Interview Questions](#interview-questions)

---

## Introduction

Service communication is a fundamental aspect of microservices architecture. Since microservices are distributed by nature, they need reliable mechanisms to communicate with each other to fulfill business requirements.

### Why Service Communication Matters

- **Distributed Nature**: Microservices run in separate processes/containers
- **Data Consistency**: Services need to share and synchronize data
- **Business Workflows**: Complex operations span multiple services
- **Performance**: Communication overhead impacts system performance
- **Reliability**: Network calls can fail, requiring proper error handling

---

## Types of Communication

### 1. Synchronous Communication

**Definition**: The caller waits for a response from the callee before proceeding.

**Characteristics**:
- Blocking operation
- Immediate response expected
- Direct coupling between services
- Request-response pattern

**Use Cases**:
- Real-time data retrieval
- User-initiated operations
- Transactions requiring immediate feedback

**Example**: REST API calls, gRPC

### 2. Asynchronous Communication

**Definition**: The caller sends a request and continues processing without waiting for a response.

**Characteristics**:
- Non-blocking operation
- Eventual consistency
- Loose coupling between services
- Event-driven architecture

**Use Cases**:
- Background processing
- Event notifications
- Long-running operations
- High-volume data processing

**Example**: Message queues (RabbitMQ, Kafka), Events

---

## Synchronous Communication

### REST (Representational State Transfer)

**Overview**: HTTP-based communication using standard methods (GET, POST, PUT, DELETE).

**Advantages**:
- ✅ Simple and widely understood
- ✅ Standard HTTP methods and status codes
- ✅ Easy to test and debug
- ✅ Human-readable (JSON/XML)
- ✅ Browser-friendly

**Disadvantages**:
- ❌ Tight coupling between services
- ❌ Blocking calls impact performance
- ❌ Cascading failures
- ❌ Not suitable for high-throughput scenarios

### Spring Boot REST Communication Tools

#### 1. RestTemplate (Legacy)

```java
@Configuration
public class RestConfig {
    
    @Bean
    @LoadBalanced  // Enable service discovery
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

// Using RestTemplate
@Service
public class UserService {
    
    @Autowired
    private RestTemplate restTemplate;
    
    public User getUser(Long id) {
        String url = "http://USER-SERVICE/api/users/" + id;
        return restTemplate.getForObject(url, User.class);
    }
    
    public User createUser(User user) {
        String url = "http://USER-SERVICE/api/users";
        return restTemplate.postForObject(url, user, User.class);
    }
}
```

#### 2. WebClient (Modern - Recommended)

**Non-blocking, reactive approach**

```java
@Configuration
public class WebClientConfig {
    
    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}

// Using WebClient
@Service
public class UserService {
    
    private final WebClient webClient;
    
    public UserService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://USER-SERVICE").build();
    }
    
    // Blocking call
    public User getUser(Long id) {
        return webClient.get()
            .uri("/api/users/{id}", id)
            .retrieve()
            .bodyToMono(User.class)
            .block();
    }
    
    // Non-blocking call
    public Mono<User> getUserAsync(Long id) {
        return webClient.get()
            .uri("/api/users/{id}", id)
            .retrieve()
            .bodyToMono(User.class);
    }
    
    // POST request
    public Mono<User> createUser(User user) {
        return webClient.post()
            .uri("/api/users")
            .bodyValue(user)
            .retrieve()
            .bodyToMono(User.class);
    }
    
    // With error handling
    public Mono<User> getUserWithErrorHandling(Long id) {
        return webClient.get()
            .uri("/api/users/{id}", id)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, 
                response -> Mono.error(new UserNotFoundException()))
            .onStatus(HttpStatusCode::is5xxServerError,
                response -> Mono.error(new ServiceUnavailableException()))
            .bodyToMono(User.class)
            .timeout(Duration.ofSeconds(5))
            .retry(3);
    }
}
```

#### 3. OpenFeign (Declarative REST Client)

**Simplifies REST calls with interface declarations**

```java
// Add dependency
// spring-cloud-starter-openfeign

@EnableFeignClients
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

// Feign Client Interface
@FeignClient(name = "USER-SERVICE")
public interface UserServiceClient {
    
    @GetMapping("/api/users/{id}")
    User getUserById(@PathVariable("id") Long id);
    
    @PostMapping("/api/users")
    User createUser(@RequestBody User user);
    
    @GetMapping("/api/users")
    List<User> getAllUsers();
}

// Using Feign Client
@Service
public class OrderService {
    
    @Autowired
    private UserServiceClient userServiceClient;
    
    public void createOrder(OrderRequest request) {
        // Simple method call - no RestTemplate boilerplate
        User user = userServiceClient.getUserById(request.getUserId());
        
        if (user == null) {
            throw new UserNotFoundException();
        }
        
        // Continue with order processing...
    }
}
```

### gRPC (Google Remote Procedure Call)

**High-performance, language-agnostic RPC framework**

**Advantages**:
- ✅ Very fast (uses Protocol Buffers)
- ✅ Strongly typed contracts
- ✅ Bi-directional streaming
- ✅ Built-in load balancing
- ✅ Multi-language support

**Disadvantages**:
- ❌ Not human-readable (binary protocol)
- ❌ Limited browser support
- ❌ More complex setup

**When to Use**:
- Microservice-to-microservice communication
- High-performance requirements
- Streaming data

---

## Asynchronous Communication

### Message Queues

**Overview**: Services communicate by sending messages through a message broker.

**Key Components**:
- **Producer**: Sends messages
- **Consumer**: Receives and processes messages
- **Message Broker**: Routes messages (RabbitMQ, Kafka, ActiveMQ)
- **Queue/Topic**: Holds messages

### RabbitMQ

**Overview**: Traditional message broker supporting multiple messaging patterns.

**Messaging Patterns**:

#### 1. Point-to-Point (Queue)
```
Producer → Queue → Consumer
```

#### 2. Publish-Subscribe (Exchange)
```
Producer → Exchange → Queue1 → Consumer1
                   → Queue2 → Consumer2
```

**Spring Boot with RabbitMQ**:

```java
// Add dependency
// spring-boot-starter-amqp

// Configuration
@Configuration
public class RabbitMQConfig {
    
    public static final String QUEUE_NAME = "order.queue";
    public static final String EXCHANGE_NAME = "order.exchange";
    public static final String ROUTING_KEY = "order.routing.key";
    
    @Bean
    public Queue queue() {
        return new Queue(QUEUE_NAME, true);
    }
    
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }
    
    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }
}

// Producer
@Service
public class OrderProducer {
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    public void sendOrder(Order order) {
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.EXCHANGE_NAME,
            RabbitMQConfig.ROUTING_KEY,
            order
        );
        
        System.out.println("Order sent: " + order.getId());
    }
}

// Consumer
@Component
public class OrderConsumer {
    
    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void receiveOrder(Order order) {
        System.out.println("Order received: " + order.getId());
        
        // Process order
        processOrder(order);
    }
    
    private void processOrder(Order order) {
        // Business logic here
    }
}
```

### Apache Kafka

**Overview**: Distributed streaming platform for high-throughput, fault-tolerant messaging.

**Key Concepts**:
- **Topic**: Category of messages
- **Partition**: Ordered, immutable sequence of records
- **Producer**: Publishes messages to topics
- **Consumer**: Subscribes to topics
- **Consumer Group**: Allows parallel processing

**Kafka vs RabbitMQ**:

| Feature | RabbitMQ | Kafka |
|---------|----------|-------|
| **Type** | Message Broker | Event Streaming Platform |
| **Message Ordering** | Per queue | Per partition |
| **Throughput** | Moderate | Very High |
| **Message Retention** | Until consumed | Configurable (days/weeks) |
| **Use Case** | Task queues, RPC | Event streaming, logs |
| **Complexity** | Lower | Higher |
| **Message Replay** | Limited | Yes |

**Spring Boot with Kafka**:

```java
// Add dependency
// spring-kafka

// Configuration
@Configuration
public class KafkaConfig {
    
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    
    // Producer Configuration
    @Bean
    public ProducerFactory<String, Order> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }
    
    @Bean
    public KafkaTemplate<String, Order> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
    
    // Consumer Configuration
    @Bean
    public ConsumerFactory<String, Order> consumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "order-group");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        return new DefaultKafkaConsumerFactory<>(config);
    }
    
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Order> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Order> factory = 
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}

// Producer
@Service
public class OrderProducer {
    
    private static final String TOPIC = "order-topic";
    
    @Autowired
    private KafkaTemplate<String, Order> kafkaTemplate;
    
    public void sendOrder(Order order) {
        kafkaTemplate.send(TOPIC, order.getId().toString(), order);
        System.out.println("Order published to Kafka: " + order.getId());
    }
}

// Consumer
@Component
public class OrderConsumer {
    
    @KafkaListener(topics = "order-topic", groupId = "order-group")
    public void consumeOrder(Order order) {
        System.out.println("Order consumed from Kafka: " + order.getId());
        
        // Process order
        processOrder(order);
    }
    
    private void processOrder(Order order) {
        // Business logic here
    }
}
```

---

## Communication Patterns

### 1. Request-Response Pattern

**Description**: Synchronous pattern where client waits for response.

**Use Case**: Real-time data retrieval

```
Client → [Request] → Service
Client ← [Response] ← Service
```

### 2. Fire-and-Forget Pattern

**Description**: Asynchronous pattern where client doesn't wait for response.

**Use Case**: Logging, notifications

```
Client → [Message] → Queue
                      ↓
                   Service
```

### 3. Request-Async Response Pattern

**Description**: Client sends request, service responds asynchronously via callback or message.

**Use Case**: Long-running operations

```
Client → [Request] → Service
         [Accepted]
         
Later:
Client ← [Callback] ← Service
```

### 4. Publish-Subscribe Pattern

**Description**: Multiple services subscribe to events from a publisher.

**Use Case**: Event-driven architectures

```
Publisher → Topic → Subscriber1
                 → Subscriber2
                 → Subscriber3
```

### 5. Saga Pattern

**Description**: Distributed transaction pattern using choreography or orchestration.

**Use Case**: Multi-service transactions

**Choreography**:
```
OrderService → [OrderCreated] → PaymentService
                              ↓ [PaymentProcessed]
                              InventoryService
                              ↓ [InventoryReserved]
                              ShippingService
```

**Orchestration**:
```
Orchestrator → OrderService
            → PaymentService
            → InventoryService
            → ShippingService
```

---

## REST Communication

### RestTemplate Example

```java
@Service
public class ProductService {
    
    @Autowired
    private RestTemplate restTemplate;
    
    private static final String INVENTORY_SERVICE_URL = "http://INVENTORY-SERVICE";
    
    public void updateProductStock(Long productId, int quantity) {
        String url = INVENTORY_SERVICE_URL + "/api/inventory/" + productId;
        
        // GET request
        Inventory inventory = restTemplate.getForObject(url, Inventory.class);
        
        // PUT request
        inventory.setQuantity(quantity);
        restTemplate.put(url, inventory);
        
        // POST request
        StockUpdate update = new StockUpdate(productId, quantity);
        restTemplate.postForObject(
            INVENTORY_SERVICE_URL + "/api/inventory/update",
            update,
            StockUpdate.class
        );
        
        // DELETE request
        restTemplate.delete(url);
    }
    
    // With error handling
    public Inventory getInventoryWithErrorHandling(Long productId) {
        String url = INVENTORY_SERVICE_URL + "/api/inventory/" + productId;
        
        try {
            return restTemplate.getForObject(url, Inventory.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new InventoryNotFoundException();
            }
            throw e;
        } catch (RestClientException e) {
            throw new ServiceCommunicationException("Failed to reach inventory service", e);
        }
    }
}
```

### WebClient Example (Reactive)

```java
@Service
public class ProductService {
    
    private final WebClient webClient;
    
    public ProductService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
            .baseUrl("http://INVENTORY-SERVICE")
            .build();
    }
    
    // Blocking approach
    public Inventory getInventory(Long productId) {
        return webClient.get()
            .uri("/api/inventory/{id}", productId)
            .retrieve()
            .bodyToMono(Inventory.class)
            .block();
    }
    
    // Reactive approach
    public Mono<Inventory> getInventoryAsync(Long productId) {
        return webClient.get()
            .uri("/api/inventory/{id}", productId)
            .retrieve()
            .bodyToMono(Inventory.class)
            .timeout(Duration.ofSeconds(5))
            .onErrorResume(e -> {
                log.error("Failed to fetch inventory", e);
                return Mono.empty();
            });
    }
    
    // Multiple parallel calls
    public Mono<ProductWithInventory> getProductWithInventory(Long productId) {
        Mono<Product> productMono = getProductAsync(productId);
        Mono<Inventory> inventoryMono = getInventoryAsync(productId);
        
        return Mono.zip(productMono, inventoryMono)
            .map(tuple -> new ProductWithInventory(tuple.getT1(), tuple.getT2()));
    }
}
```

---

## Message-Based Communication

### Event-Driven Architecture

**Overview**: Services communicate through events representing state changes.

**Benefits**:
- Loose coupling
- Scalability
- Resilience
- Temporal decoupling

**Example: Order Processing System**

```java
// Event
public class OrderCreatedEvent {
    private Long orderId;
    private Long userId;
    private BigDecimal amount;
    private LocalDateTime timestamp;
}

// Publisher (Order Service)
@Service
public class OrderService {
    
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    
    @Transactional
    public Order createOrder(OrderRequest request) {
        // Create order
        Order order = new Order(request);
        orderRepository.save(order);
        
        // Publish event
        OrderCreatedEvent event = new OrderCreatedEvent(
            order.getId(),
            order.getUserId(),
            order.getTotalAmount(),
            LocalDateTime.now()
        );
        
        eventPublisher.publishEvent(event);
        
        return order;
    }
}

// Listeners (Other Services)

// Payment Service
@Component
public class PaymentEventListener {
    
    @EventListener
    @Async
    public void handleOrderCreated(OrderCreatedEvent event) {
        // Process payment
        processPayment(event.getOrderId(), event.getAmount());
    }
}

// Inventory Service
@Component
public class InventoryEventListener {
    
    @EventListener
    @Async
    public void handleOrderCreated(OrderCreatedEvent event) {
        // Reserve inventory
        reserveInventory(event.getOrderId());
    }
}

// Notification Service
@Component
public class NotificationEventListener {
    
    @EventListener
    @Async
    public void handleOrderCreated(OrderCreatedEvent event) {
        // Send notification
        sendOrderConfirmation(event.getUserId(), event.getOrderId());
    }
}
```

---

## Choosing Communication Style

### Use Synchronous Communication When:

1. **Immediate Response Required**
   - User is waiting for result
   - Real-time data needed
   
2. **Simple Operations**
   - Quick database lookups
   - Simple calculations
   
3. **Strong Consistency Needed**
   - Financial transactions
   - Critical data updates

### Use Asynchronous Communication When:

1. **Long-Running Operations**
   - Video processing
   - Report generation
   - Batch operations

2. **Event Notifications**
   - User registered
   - Order placed
   - Payment received

3. **High Throughput Required**
   - Log aggregation
   - Metrics collection
   - Data streaming

4. **Decoupling Services**
   - Reduce dependencies
   - Improve resilience
   - Enable scalability

### Decision Matrix

| Criteria | Synchronous | Asynchronous |
|----------|-------------|--------------|
| **Response Time** | Immediate | Eventual |
| **Coupling** | Tight | Loose |
| **Consistency** | Strong | Eventual |
| **Complexity** | Lower | Higher |
| **Scalability** | Limited | High |
| **Failure Handling** | Immediate | Retry/DLQ |
| **User Experience** | Blocking | Non-blocking |

---

## Best Practices

### 1. Use Service Discovery

```java
// Instead of hardcoded URLs
String url = "http://192.168.1.100:8080/api/users";

// Use service names with discovery
String url = "http://USER-SERVICE/api/users";
```

### 2. Implement Timeouts

```java
@Bean
public RestTemplate restTemplate() {
    SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
    factory.setConnectTimeout(5000);  // 5 seconds
    factory.setReadTimeout(5000);     // 5 seconds
    return new RestTemplate(factory);
}
```

### 3. Handle Errors Gracefully

```java
public User getUser(Long id) {
    try {
        return restTemplate.getForObject(url, User.class);
    } catch (HttpClientErrorException e) {
        log.error("Client error: {}", e.getMessage());
        throw new UserNotFoundException();
    } catch (HttpServerErrorException e) {
        log.error("Server error: {}", e.getMessage());
        throw new ServiceUnavailableException();
    } catch (ResourceAccessException e) {
        log.error("Timeout or connection error: {}", e.getMessage());
        throw new ServiceCommunicationException();
    }
}
```

### 4. Use Circuit Breakers

```java
@CircuitBreaker(name = "userService", fallbackMethod = "getUserFallback")
public User getUser(Long id) {
    return restTemplate.getForObject(url, User.class);
}

public User getUserFallback(Long id, Exception e) {
    log.error("Circuit breaker activated for user: {}", id, e);
    return new User(id, "Unknown User");
}
```

### 5. Implement Retry Logic

```java
@Retry(name = "userService", fallbackMethod = "getUserFallback")
public User getUser(Long id) {
    return restTemplate.getForObject(url, User.class);
}
```

### 6. Use Connection Pooling

```java
@Bean
public RestTemplate restTemplate() {
    PoolingHttpClientConnectionManager connectionManager = 
        new PoolingHttpClientConnectionManager();
    connectionManager.setMaxTotal(100);
    connectionManager.setDefaultMaxPerRoute(20);
    
    CloseableHttpClient httpClient = HttpClients.custom()
        .setConnectionManager(connectionManager)
        .build();
    
    HttpComponentsClientHttpRequestFactory factory = 
        new HttpComponentsClientHttpRequestFactory(httpClient);
    
    return new RestTemplate(factory);
}
```

### 7. Implement Idempotency

```java
@PostMapping("/api/orders")
public ResponseEntity<Order> createOrder(
        @RequestBody OrderRequest request,
        @RequestHeader("Idempotency-Key") String idempotencyKey) {
    
    // Check if order already exists with this key
    Optional<Order> existing = orderRepository.findByIdempotencyKey(idempotencyKey);
    if (existing.isPresent()) {
        return ResponseEntity.ok(existing.get());
    }
    
    // Create new order
    Order order = orderService.createOrder(request);
    order.setIdempotencyKey(idempotencyKey);
    orderRepository.save(order);
    
    return ResponseEntity.status(HttpStatus.CREATED).body(order);
}
```

### 8. Use Dead Letter Queues

```java
@Configuration
public class RabbitMQConfig {
    
    @Bean
    public Queue mainQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", "dlx.exchange");
        args.put("x-dead-letter-routing-key", "dlq");
        return new Queue("main.queue", true, false, false, args);
    }
    
    @Bean
    public Queue deadLetterQueue() {
        return new Queue("dead.letter.queue", true);
    }
}
```

### 9. Monitor Communication

```java
@Component
public class RestTemplateMetrics {
    
    @Autowired
    private MeterRegistry registry;
    
    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        
        restTemplate.setInterceptors(Collections.singletonList(
            (request, body, execution) -> {
                Timer.Sample sample = Timer.start(registry);
                
                try {
                    ClientHttpResponse response = execution.execute(request, body);
                    
                    sample.stop(Timer.builder("http.client.requests")
                        .tag("uri", request.getURI().getPath())
                        .tag("status", String.valueOf(response.getStatusCode().value()))
                        .register(registry));
                    
                    return response;
                } catch (Exception e) {
                    sample.stop(Timer.builder("http.client.requests")
                        .tag("uri", request.getURI().getPath())
                        .tag("status", "error")
                        .register(registry));
                    throw e;
                }
            }
        ));
        
        return restTemplate;
    }
}
```

---

## Interview Questions

### Q1: What is the difference between synchronous and asynchronous communication in microservices?

**Answer**:

**Synchronous Communication**:
- Client waits for response from server
- Blocking operation
- Examples: REST API, gRPC
- Use case: When immediate response is needed
- Pros: Simple, immediate feedback
- Cons: Tight coupling, cascading failures

**Asynchronous Communication**:
- Client doesn't wait for response
- Non-blocking operation
- Examples: Message queues (RabbitMQ, Kafka)
- Use case: Long-running operations, event-driven systems
- Pros: Loose coupling, better scalability
- Cons: More complex, eventual consistency

**Example**:
```java
// Synchronous
User user = restTemplate.getForObject(url, User.class);  // Waits here

// Asynchronous
kafkaTemplate.send("user-topic", user);  // Returns immediately
```

---

### Q2: When would you use REST vs message queues for service communication?

**Answer**:

**Use REST When**:
1. Real-time data needed (user profile lookup)
2. Simple request-response pattern
3. Immediate error feedback required
4. Low to moderate traffic
5. Browser/mobile client communication

**Use Message Queues When**:
1. Long-running operations (video processing)
2. High throughput requirements (log aggregation)
3. Decoupling services for resilience
4. Event-driven architecture
5. Retry and DLQ requirements
6. Multiple consumers for same event

**Example Scenario**:
- Order Creation: Use REST for initial validation
- Order Processing: Use message queue for payment, inventory, shipping

---

### Q3: What is the difference between RabbitMQ and Kafka?

**Answer**:

| Aspect | RabbitMQ | Kafka |
|--------|----------|-------|
| **Type** | Message broker | Streaming platform |
| **Message Model** | Push-based | Pull-based |
| **Message Retention** | Until consumed | Configurable (days) |
| **Message Ordering** | Per queue | Per partition |
| **Use Case** | Task queues, RPC | Event streaming, logs |
| **Throughput** | 10K-100K msg/sec | 100K-1M+ msg/sec |
| **Message Replay** | No | Yes |
| **Protocols** | AMQP, MQTT, STOMP | Binary TCP |
| **Complexity** | Lower | Higher |

**Choose RabbitMQ for**:
- Traditional message queuing
- Complex routing requirements
- Lower message volumes
- Priority queues

**Choose Kafka for**:
- High-throughput event streaming
- Message replay requirements
- Log aggregation
- Real-time analytics

---

### Q4: How do you handle failures in service-to-service communication?

**Answer**:

**1. Timeout Configuration**:
```java
factory.setConnectTimeout(5000);
factory.setReadTimeout(5000);
```

**2. Retry Mechanism**:
```java
@Retry(name = "userService", 
       fallbackMethod = "getUserFallback",
       maxAttempts = 3,
       waitDuration = 1000)
public User getUser(Long id) {
    return restTemplate.getForObject(url, User.class);
}
```

**3. Circuit Breaker**:
```java
@CircuitBreaker(name = "userService",
                fallbackMethod = "getUserFallback")
public User getUser(Long id) {
    return restTemplate.getForObject(url, User.class);
}
```

**4. Fallback Methods**:
```java
public User getUserFallback(Long id, Exception e) {
    return new User(id, "Default User");
}
```

**5. Dead Letter Queue** (for async):
```java
args.put("x-dead-letter-exchange", "dlx.exchange");
```

**6. Graceful Degradation**:
Return cached data or partial results instead of failing completely.

---

### Q5: What is idempotency and why is it important in microservices?

**Answer**:

**Idempotency**: An operation that produces the same result no matter how many times it's executed.

**Importance**:
1. **Network Failures**: Requests may be retried
2. **Message Duplicates**: Queues may deliver same message twice
3. **Data Consistency**: Prevents duplicate records
4. **Safe Retries**: Can retry without side effects

**Implementation**:

```java
@PostMapping("/api/orders")
public ResponseEntity<Order> createOrder(
        @RequestBody OrderRequest request,
        @RequestHeader("Idempotency-Key") String key) {
    
    // Check if already processed
    Optional<Order> existing = orderRepository.findByIdempotencyKey(key);
    if (existing.isPresent()) {
        return ResponseEntity.ok(existing.get());  // Return existing
    }
    
    // Process new request
    Order order = orderService.createOrder(request);
    order.setIdempotencyKey(key);
    return ResponseEntity.created(uri).body(order);
}
```

**Idempotent Methods**:
- ✅ GET: Always idempotent
- ✅ PUT: Should be idempotent (replace resource)
- ✅ DELETE: Should be idempotent
- ❌ POST: Not idempotent by default (needs implementation)

---

### Q6: How do you implement request-response pattern with message queues?

**Answer**:

**Approach 1: Correlation ID**

```java
// Sender
@Service
public class RequestSender {
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    public String sendRequest(Request request) {
        String correlationId = UUID.randomUUID().toString();
        
        Message message = MessageBuilder
            .withBody(serialize(request))
            .setCorrelationId(correlationId)
            .setReplyTo("response.queue")
            .build();
        
        rabbitTemplate.send("request.queue", message);
        
        // Wait for response with same correlation ID
        return waitForResponse(correlationId);
    }
}

// Receiver
@Component
public class RequestReceiver {
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    @RabbitListener(queues = "request.queue")
    public void handleRequest(Request request, 
                             @Header("correlationId") String correlationId,
                             @Header("replyTo") String replyTo) {
        
        // Process request
        Response response = processRequest(request);
        
        // Send response with same correlation ID
        Message message = MessageBuilder
            .withBody(serialize(response))
            .setCorrelationId(correlationId)
            .build();
        
        rabbitTemplate.send(replyTo, message);
    }
}
```

**Approach 2: Spring AMQP RPC**

```java
@Bean
public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
    RabbitTemplate template = new RabbitTemplate(connectionFactory);
    template.setReplyTimeout(5000);
    return template;
}

// Simple RPC call
public Response makeRpcCall(Request request) {
    return (Response) rabbitTemplate.convertSendAndReceive(
        "request.exchange",
        "request.routing.key",
        request
    );
}
```

---

### Q7: What are the challenges of distributed transactions in microservices?

**Answer**:

**Challenges**:

1. **No ACID Transactions**: Cannot use traditional database transactions across services
2. **Partial Failures**: One service may succeed while another fails
3. **Data Consistency**: Hard to maintain consistency across services
4. **Rollback Complexity**: Difficult to undo completed operations

**Solutions**:

**1. Saga Pattern**:

**Choreography** (Event-driven):
```
OrderService → [OrderCreated]
PaymentService → [PaymentProcessed] / [PaymentFailed]
InventoryService → [InventoryReserved] / [InventoryFailed]
```

**Orchestration** (Centralized):
```java
@Service
public class OrderSaga {
    
    public void executeOrderSaga(Order order) {
        try {
            // Step 1: Create order
            orderService.createOrder(order);
            
            // Step 2: Process payment
            paymentService.processPayment(order);
            
            // Step 3: Reserve inventory
            inventoryService.reserveInventory(order);
            
            // Step 4: Confirm order
            orderService.confirmOrder(order);
            
        } catch (Exception e) {
            // Compensating transactions
            inventoryService.releaseInventory(order);
            paymentService.refundPayment(order);
            orderService.cancelOrder(order);
        }
    }
}
```

**2. Two-Phase Commit (2PC)**:
Not recommended for microservices due to blocking nature.

**3. Eventual Consistency**:
Accept that data will be consistent eventually, not immediately.

---

### Q8: How do you ensure message delivery in asynchronous communication?

**Answer**:

**1. Publisher Confirms (RabbitMQ)**:
```java
rabbitTemplate.setConfirmCallback((correlation, ack, reason) -> {
    if (ack) {
        log.info("Message delivered successfully");
    } else {
        log.error("Message delivery failed: " + reason);
    }
});
```

**2. Message Persistence**:
```java
@Bean
public Queue queue() {
    return new Queue("order.queue", true);  // durable = true
}
```

**3. Acknowledgment Modes**:
```java
// Manual acknowledgment
@RabbitListener(queues = "order.queue", ackMode = "MANUAL")
public void receiveOrder(Order order, Channel channel, 
                        @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
    try {
        processOrder(order);
        channel.basicAck(tag, false);  // Success
    } catch (Exception e) {
        channel.basicNack(tag, false, true);  // Requeue
    }
}
```

**4. Dead Letter Queue**:
```java
args.put("x-dead-letter-exchange", "dlx.exchange");
args.put("x-max-retries", "3");
```

**5. Kafka Acknowledgment**:
```java
@KafkaListener(topics = "order-topic")
public void consume(ConsumerRecord<String, Order> record, 
                   Acknowledgment ack) {
    try {
        processOrder(record.value());
        ack.acknowledge();  // Manual commit
    } catch (Exception e) {
        // Don't acknowledge - will be reprocessed
    }
}
```

**6. Idempotent Consumers**:
Handle duplicate messages gracefully.

**7. Transactional Outbox Pattern**:
```java
@Transactional
public void createOrder(Order order) {
    // Save order to database
    orderRepository.save(order);
    
    // Save event to outbox table
    OutboxEvent event = new OutboxEvent(
        "OrderCreated",
        serialize(order)
    );
    outboxRepository.save(event);
    
    // Background process publishes from outbox
}
```

---

## Summary

**Key Takeaways**:

1. **Choose the Right Pattern**: REST for synchronous, messages for asynchronous
2. **Handle Failures**: Timeouts, retries, circuit breakers, fallbacks
3. **Ensure Reliability**: Idempotency, acknowledgments, DLQ
4. **Monitor Everything**: Track latency, errors, throughput
5. **Loose Coupling**: Prefer async communication when possible
6. **Event-Driven**: Use events for notifications and state changes
7. **Saga for Transactions**: Choreography or orchestration
8. **Test Communication**: Integration tests, contract testing

**Communication Decision Tree**:
```
Need immediate response? 
  ├─ Yes → REST/gRPC
  └─ No → Message Queue
  
High throughput?
  ├─ Yes → Kafka
  └─ No → RabbitMQ/REST
  
Strong consistency?
  ├─ Yes → Synchronous (REST)
  └─ No → Asynchronous (Messages)
```

**Next Topics**:
- Resilience patterns (Circuit breaker, retry, timeout)
- API Gateway patterns
- Service mesh (Istio, Linkerd)
- GraphQL for flexible APIs
