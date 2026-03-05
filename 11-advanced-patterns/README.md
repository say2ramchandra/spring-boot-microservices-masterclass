# Module 11: Advanced Patterns & Best Practices

> **Master advanced microservices patterns and classic design patterns for production-ready systems**

## 📚 Module Overview

This module covers advanced architectural patterns, classic design patterns in the context of Spring Boot, and best practices for building scalable, maintainable microservices. You'll learn how to apply proven patterns to solve common challenges in distributed systems.

---

## 🎯 Learning Objectives

By the end of this module, you will be able to:

- ✅ Implement classic design patterns (Factory, Builder, Strategy, Observer, etc.)
- ✅ Apply Backend for Frontend (BFF) pattern
- ✅ Use Strangler Fig pattern for legacy migration
- ✅ Implement API versioning strategies
- ✅ Build reactive microservices with Spring WebFlux
- ✅ Create GraphQL APIs for flexible queries
- ✅ Use gRPC for high-performance internal communication
- ✅ Apply advanced database patterns
- ✅ Follow microservices best practices

---

## 🗺️ Module Structure

```
11-advanced-patterns/
├── README.md                                    # This file
├── 01-design-patterns/
│   ├── README.md                               # Design patterns guide
│   ├── demo-factory-pattern/                   # Factory pattern
│   ├── demo-builder-pattern/                   # Builder pattern
│   ├── demo-strategy-pattern/                  # Strategy pattern
│   └── demo-observer-pattern/                  # Observer pattern
├── 02-bff-pattern/
│   ├── README.md                               # BFF pattern guide
│   └── demo-mobile-web-bff/                    # Mobile + Web BFF demo
├── 03-reactive-microservices/
│   ├── README.md                               # Reactive programming guide
│   └── demo-webflux/                           # Spring WebFlux demo
├── 04-api-versioning/
│   ├── README.md                               # Versioning strategies
│   └── demo-version-strategies/                # Versioning demo
├── 05-graphql/
│   ├── README.md                               # GraphQL guide
│   └── demo-graphql-api/                       # GraphQL demo
└── 06-grpc/
    ├── README.md                               # gRPC guide
    └── demo-grpc-service/                      # gRPC demo
```

---

## 📖 Table of Contents

1. [Classic Design Patterns](#1-classic-design-patterns)
2. [Backend for Frontend (BFF)](#2-backend-for-frontend-bff)
3. [Strangler Fig Pattern](#3-strangler-fig-pattern)
4. [API Versioning Strategies](#4-api-versioning-strategies)
5. [Reactive Microservices](#5-reactive-microservices)
6. [GraphQL APIs](#6-graphql-apis)
7. [gRPC Communication](#7-grpc-communication)
8. [Advanced Database Patterns](#8-advanced-database-patterns)
9. [Best Practices](#9-best-practices)

---

## 1. Classic Design Patterns

### Why Design Patterns?

Design patterns are proven solutions to common software design problems. In Spring Boot, many patterns are built-in, but understanding them helps you:
- Write cleaner, more maintainable code
- Communicate design decisions effectively
- Solve problems faster with proven solutions
- Build flexible, extensible systems

---

### 1.1 Creational Patterns

#### **Factory Pattern** 🏭

**Problem:** Creating objects without exposing creation logic. Need flexibility in object creation.

**Solution:** Use a factory class/method to create objects based on input.

**Spring Boot Context:**
```java
// Payment service factory
@Component
public class PaymentServiceFactory {
    
    private final Map<PaymentType, PaymentService> services;
    
    public PaymentServiceFactory(List<PaymentService> paymentServices) {
        this.services = paymentServices.stream()
            .collect(Collectors.toMap(
                PaymentService::getPaymentType,
                Function.identity()
            ));
    }
    
    public PaymentService getPaymentService(PaymentType type) {
        PaymentService service = services.get(type);
        if (service == null) {
            throw new IllegalArgumentException("Unknown payment type: " + type);
        }
        return service;
    }
}

// Usage
@Service
public class OrderService {
    
    private final PaymentServiceFactory paymentFactory;
    
    public void processPayment(Order order, PaymentType type) {
        PaymentService paymentService = paymentFactory.getPaymentService(type);
        paymentService.processPayment(order.getTotalAmount());
    }
}
```

**Key Benefits:**
- ✅ Loose coupling - client doesn't know concrete classes
- ✅ Easy to add new payment types
- ✅ Centralized creation logic
- ✅ Testable - mock specific implementations

**Real-World Use Cases:**
- Payment processing (Credit Card, PayPal, Crypto)
- Notification services (Email, SMS, Push)
- File parsers (CSV, JSON, XML)
- Database connections (MySQL, PostgreSQL, MongoDB)

---

#### **Builder Pattern** 🔨

**Problem:** Creating complex objects with many optional parameters. Constructor with many parameters is hard to read.

**Solution:** Use a builder class to construct objects step-by-step.

**Lombok Integration:**
```java
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Order {
    private Long id;
    private String customerName;
    private String email;
    
    // Optional fields
    private String shippingAddress;
    private String billingAddress;
    private String couponCode;
    private String specialInstructions;
    private Boolean giftWrap;
    private String giftMessage;
    
    private BigDecimal totalAmount;
    private OrderStatus status;
    
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}

// Usage - clean and readable
Order order = Order.builder()
    .customerName("John Doe")
    .email("john@example.com")
    .shippingAddress("123 Main St")
    .totalAmount(new BigDecimal("99.99"))
    .giftWrap(true)
    .giftMessage("Happy Birthday!")
    .status(OrderStatus.PENDING)
    .build();
```

**Custom Builder for Complex Logic:**
```java
public class ReportBuilder {
    
    private Report report = new Report();
    
    public ReportBuilder withTitle(String title) {
        report.setTitle(title);
        return this;
    }
    
    public ReportBuilder withDateRange(LocalDate start, LocalDate end) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }
        report.setStartDate(start);
        report.setEndDate(end);
        return this;
    }
    
    public ReportBuilder includeCharts(boolean includeCharts) {
        report.setIncludeCharts(includeCharts);
        if (includeCharts) {
            report.setChartType(ChartType.DEFAULT);
        }
        return this;
    }
    
    public Report build() {
        // Validation before creating
        if (report.getTitle() == null) {
            throw new IllegalStateException("Title is required");
        }
        return report;
    }
}

// Usage
Report report = new ReportBuilder()
    .withTitle("Monthly Sales Report")
    .withDateRange(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 31))
    .includeCharts(true)
    .build();
```

**Key Benefits:**
- ✅ Readable code with named parameters
- ✅ Immutable objects (when built)
- ✅ Validation in builder
- ✅ Optional parameters without multiple constructors

**Real-World Use Cases:**
- Complex DTOs/POJOs
- Query builders (JPA Criteria API)
- Configuration objects
- Test data builders

---

#### **Singleton Pattern** 🎯

**Spring Boot Implementation:**

Spring manages singleton beans by default - you don't need to implement singleton pattern manually!

```java
@Service  // Singleton by default
public class ApplicationCache {
    
    private final Map<String, Object> cache = new ConcurrentHashMap<>();
    
    public void put(String key, Object value) {
        cache.put(key, value);
    }
    
    public Object get(String key) {
        return cache.get(key);
    }
}

// Spring ensures only ONE instance exists
// Injected everywhere as the same instance
```

**Manual Singleton (when needed outside Spring):**
```java
public class DatabaseConnectionPool {
    
    private static volatile DatabaseConnectionPool instance;
    private final HikariDataSource dataSource;
    
    private DatabaseConnectionPool() {
        // Initialize connection pool
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3306/mydb");
        config.setMaximumPoolSize(20);
        this.dataSource = new HikariDataSource(config);
    }
    
    public static DatabaseConnectionPool getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnectionPool.class) {
                if (instance == null) {
                    instance = new DatabaseConnectionPool();
                }
            }
        }
        return instance;
    }
    
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
```

**Key Points:**
- ✅ Spring @Component, @Service, @Repository are singletons by default
- ✅ Use @Scope("prototype") for multiple instances
- ✅ Thread-safe in Spring container
- ⚠️ Avoid for stateful objects

---

### 1.2 Behavioral Patterns

#### **Strategy Pattern** 🎲

**Problem:** Different algorithms for the same task. Need to switch algorithms at runtime.

**Solution:** Define family of algorithms, encapsulate each one, and make them interchangeable.

**Spring Boot Implementation:**
```java
// Strategy interface
public interface DiscountStrategy {
    BigDecimal applyDiscount(BigDecimal originalPrice);
    String getStrategyName();
}

// Concrete strategies
@Component("noDiscount")
public class NoDiscountStrategy implements DiscountStrategy {
    @Override
    public BigDecimal applyDiscount(BigDecimal originalPrice) {
        return originalPrice;
    }
    
    @Override
    public String getStrategyName() {
        return "NO_DISCOUNT";
    }
}

@Component("percentageDiscount")
public class PercentageDiscountStrategy implements DiscountStrategy {
    
    @Value("${discount.percentage:10}")
    private int percentage;
    
    @Override
    public BigDecimal applyDiscount(BigDecimal originalPrice) {
        BigDecimal discount = originalPrice
            .multiply(BigDecimal.valueOf(percentage))
            .divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);
        return originalPrice.subtract(discount);
    }
    
    @Override
    public String getStrategyName() {
        return "PERCENTAGE_DISCOUNT";
    }
}

@Component("fixedDiscount")
public class FixedAmountDiscountStrategy implements DiscountStrategy {
    
    @Value("${discount.fixed-amount:5.00}")
    private BigDecimal fixedAmount;
    
    @Override
    public BigDecimal applyDiscount(BigDecimal originalPrice) {
        BigDecimal result = originalPrice.subtract(fixedAmount);
        return result.compareTo(BigDecimal.ZERO) < 0 
            ? BigDecimal.ZERO 
            : result;
    }
    
    @Override
    public String getStrategyName() {
        return "FIXED_DISCOUNT";
    }
}

// Context class
@Service
public class PricingService {
    
    private final Map<String, DiscountStrategy> strategies;
    
    public PricingService(List<DiscountStrategy> strategyList) {
        this.strategies = strategyList.stream()
            .collect(Collectors.toMap(
                DiscountStrategy::getStrategyName,
                Function.identity()
            ));
    }
    
    public BigDecimal calculatePrice(BigDecimal originalPrice, String strategyName) {
        DiscountStrategy strategy = strategies.getOrDefault(
            strategyName, 
            strategies.get("NO_DISCOUNT")
        );
        return strategy.applyDiscount(originalPrice);
    }
}

// Usage in controller
@RestController
@RequestMapping("/api/pricing")
public class PricingController {
    
    private final PricingService pricingService;
    
    @GetMapping("/calculate")
    public BigDecimal calculatePrice(
            @RequestParam BigDecimal price,
            @RequestParam(defaultValue = "NO_DISCOUNT") String strategy) {
        return pricingService.calculatePrice(price, strategy);
    }
}
```

**Key Benefits:**
- ✅ Open/Closed Principle - add strategies without changing client
- ✅ Runtime algorithm selection
- ✅ Eliminates conditional statements
- ✅ Easy to test each strategy independently

**Real-World Use Cases:**
- Pricing strategies (discounts, promotions)
- Sorting algorithms
- Compression algorithms
- Encryption methods
- Search strategies
- Payment processing

---

#### **Observer Pattern** 👁️

**Problem:** Multiple objects need to be notified when an object's state changes.

**Solution:** Define one-to-many dependency - when one object changes, notify all dependents.

**Spring Events Implementation:**
```java
// Event class
public class OrderCreatedEvent extends ApplicationEvent {
    
    private final Order order;
    
    public OrderCreatedEvent(Object source, Order order) {
        super(source);
        this.order = order;
    }
    
    public Order getOrder() {
        return order;
    }
}

// Publisher (Subject)
@Service
public class OrderService {
    
    private final ApplicationEventPublisher eventPublisher;
    private final OrderRepository orderRepository;
    
    public OrderService(ApplicationEventPublisher eventPublisher,
                       OrderRepository orderRepository) {
        this.eventPublisher = eventPublisher;
        this.orderRepository = orderRepository;
    }
    
    public Order createOrder(OrderRequest request) {
        Order order = new Order();
        order.setCustomerName(request.getCustomerName());
        order.setTotalAmount(request.getTotalAmount());
        order.setStatus(OrderStatus.CREATED);
        
        Order savedOrder = orderRepository.save(order);
        
        // Publish event - notify all observers
        eventPublisher.publishEvent(new OrderCreatedEvent(this, savedOrder));
        
        return savedOrder;
    }
}

// Observer 1: Send email notification
@Component
public class EmailNotificationListener {
    
    private static final Logger log = LoggerFactory.getLogger(EmailNotificationListener.class);
    
    @EventListener
    @Async
    public void handleOrderCreated(OrderCreatedEvent event) {
        Order order = event.getOrder();
        log.info("Sending email notification for order: {}", order.getId());
        
        // Send email logic
        sendOrderConfirmationEmail(order);
    }
    
    private void sendOrderConfirmationEmail(Order order) {
        // Email sending implementation
        log.info("Email sent to: {}", order.getCustomerName());
    }
}

// Observer 2: Update inventory
@Component
public class InventoryUpdateListener {
    
    private static final Logger log = LoggerFactory.getLogger(InventoryUpdateListener.class);
    
    @EventListener
    @Async
    public void handleOrderCreated(OrderCreatedEvent event) {
        Order order = event.getOrder();
        log.info("Updating inventory for order: {}", order.getId());
        
        // Update inventory logic
        reserveInventory(order);
    }
    
    private void reserveInventory(Order order) {
        // Inventory update implementation
        log.info("Inventory reserved for order: {}", order.getId());
    }
}

// Observer 3: Analytics tracking
@Component
public class AnalyticsListener {
    
    private static final Logger log = LoggerFactory.getLogger(AnalyticsListener.class);
    
    @EventListener
    @Async
    public void handleOrderCreated(OrderCreatedEvent event) {
        Order order = event.getOrder();
        log.info("Recording analytics for order: {}", order.getId());
        
        // Track analytics
        trackOrderCreation(order);
    }
    
    private void trackOrderCreation(Order order) {
        // Analytics tracking implementation
        log.info("Analytics recorded for order: {}", order.getId());
    }
}
```

**Advanced: Conditional Listening**
```java
@Component
public class HighValueOrderListener {
    
    private static final Logger log = LoggerFactory.getLogger(HighValueOrderListener.class);
    
    @EventListener(condition = "#event.order.totalAmount > 1000")
    @Async
    public void handleHighValueOrder(OrderCreatedEvent event) {
        Order order = event.getOrder();
        log.info("High-value order detected: {} - Amount: {}", 
                 order.getId(), order.getTotalAmount());
        
        // Special handling for high-value orders
        notifyManagement(order);
        applyFraudCheck(order);
    }
}
```

**Key Benefits:**
- ✅ Loose coupling between publisher and subscribers
- ✅ Easy to add new observers
- ✅ Asynchronous processing
- ✅ Conditional event handling

**Real-World Use Cases:**
- Event-driven microservices
- Notification systems
- Audit logging
- Analytics tracking
- Cache invalidation
- Workflow orchestration

---

#### **Template Method Pattern** 📝

**Problem:** Multiple classes with similar algorithms but different steps.

**Solution:** Define algorithm skeleton in base class, let subclasses override specific steps.

**Spring Boot Implementation:**
```java
// Abstract template class
public abstract class ReportGenerator {
    
    private static final Logger log = LoggerFactory.getLogger(ReportGenerator.class);
    
    // Template method - defines algorithm structure
    public final Report generateReport(String reportId) {
        log.info("Starting report generation: {}", reportId);
        
        // Step 1: Fetch data
        List<DataPoint> data = fetchData(reportId);
        
        // Step 2: Process data (subclass implements)
        ProcessedData processed = processData(data);
        
        // Step 3: Format report (subclass implements)
        String formatted = formatReport(processed);
        
        // Step 4: Add metadata
        Report report = new Report();
        report.setId(reportId);
        report.setContent(formatted);
        report.setGeneratedAt(LocalDateTime.now());
        report.setType(getReportType());
        
        // Step 5: Optional hook - save to database
        saveReport(report);
        
        log.info("Report generation completed: {}", reportId);
        return report;
    }
    
    // Common implementation
    private List<DataPoint> fetchData(String reportId) {
        log.info("Fetching data for report: {}", reportId);
        // Common data fetching logic
        return Arrays.asList(/* data */);
    }
    
    // Abstract methods - must be implemented by subclasses
    protected abstract ProcessedData processData(List<DataPoint> data);
    protected abstract String formatReport(ProcessedData processed);
    protected abstract String getReportType();
    
    // Hook method - optional override
    protected void saveReport(Report report) {
        log.info("Saving report: {}", report.getId());
        // Default implementation (can be overridden)
    }
}

// Concrete implementation 1: Sales Report
@Component
public class SalesReportGenerator extends ReportGenerator {
    
    @Override
    protected ProcessedData processData(List<DataPoint> data) {
        // Sales-specific processing
        BigDecimal totalSales = data.stream()
            .map(DataPoint::getValue)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        ProcessedData result = new ProcessedData();
        result.put("totalSales", totalSales);
        result.put("count", data.size());
        return result;
    }
    
    @Override
    protected String formatReport(ProcessedData processed) {
        return String.format("Sales Report\nTotal: $%s\nOrders: %d",
            processed.get("totalSales"),
            processed.get("count"));
    }
    
    @Override
    protected String getReportType() {
        return "SALES";
    }
}

// Concrete implementation 2: Inventory Report
@Component
public class InventoryReportGenerator extends ReportGenerator {
    
    @Override
    protected ProcessedData processData(List<DataPoint> data) {
        // Inventory-specific processing
        long lowStockCount = data.stream()
            .filter(dp -> dp.getValue().compareTo(BigDecimal.TEN) < 0)
            .count();
        
        ProcessedData result = new ProcessedData();
        result.put("lowStockCount", lowStockCount);
        result.put("totalItems", data.size());
        return result;
    }
    
    @Override
    protected String formatReport(ProcessedData processed) {
        return String.format("Inventory Report\nLow Stock Items: %d\nTotal Items: %d",
            processed.get("lowStockCount"),
            processed.get("totalItems"));
    }
    
    @Override
    protected String getReportType() {
        return "INVENTORY";
    }
    
    @Override
    protected void saveReport(Report report) {
        // Custom save logic for inventory reports
        super.saveReport(report);
        // Additional inventory-specific saving
    }
}
```

**Key Benefits:**
- ✅ Code reuse - common algorithm in base class
- ✅ Consistent structure across implementations
- ✅ Control over algorithm flow
- ✅ Easy to add new report types

**Real-World Use Cases:**
- Report generation
- Data import/export pipelines
- Workflow execution
- Testing frameworks (setup, execute, teardown)
- Authentication flows

---

### 1.3 Structural Patterns

#### **Adapter Pattern** 🔌

**Problem:** Integrate incompatible interfaces. Third-party APIs don't match your interface.

**Solution:** Create adapter to convert one interface to another.

**Spring Boot Implementation:**
```java
// Target interface - what your application expects
public interface PaymentProcessor {
    PaymentResult processPayment(String orderId, BigDecimal amount);
}

// Adaptee - Third-party Stripe API (incompatible interface)
public class StripePaymentGateway {
    public StripeResponse charge(StripeRequest request) {
        // Stripe-specific implementation
        return new StripeResponse(/* ... */);
    }
}

// Adapter - makes Stripe compatible with PaymentProcessor
@Component("stripeAdapter")
public class StripePaymentAdapter implements PaymentProcessor {
    
    private final StripePaymentGateway stripeGateway;
    
    public StripePaymentAdapter(StripePaymentGateway stripeGateway) {
        this.stripeGateway = stripeGateway;
    }
    
    @Override
    public PaymentResult processPayment(String orderId, BigDecimal amount) {
        // Adapt application interface to Stripe interface
        StripeRequest stripeRequest = new StripeRequest();
        stripeRequest.setAmount(amount.multiply(BigDecimal.valueOf(100))); // Convert to cents
        stripeRequest.setCurrency("USD");
        stripeRequest.setDescription("Order: " + orderId);
        
        // Call Stripe API
        StripeResponse response = stripeGateway.charge(stripeRequest);
        
        // Convert Stripe response to application response
        return PaymentResult.builder()
            .orderId(orderId)
            .transactionId(response.getId())
            .success(response.isSuccessful())
            .message(response.getMessage())
            .build();
    }
}

// Another adaptee - PayPal API
public class PayPalPaymentService {
    public PayPalResult executePayment(PayPalPaymentRequest request) {
        // PayPal-specific implementation
        return new PayPalResult(/* ... */);
    }
}

// PayPal Adapter
@Component("paypalAdapter")
public class PayPalPaymentAdapter implements PaymentProcessor {
    
    private final PayPalPaymentService paypalService;
    
    public PayPalPaymentAdapter(PayPalPaymentService paypalService) {
        this.paypalService = paypalService;
    }
    
    @Override
    public PaymentResult processPayment(String orderId, BigDecimal amount) {
        // Adapt to PayPal interface
        PayPalPaymentRequest paypalRequest = new PayPalPaymentRequest();
        paypalRequest.setOrderId(orderId);
        paypalRequest.setTotal(amount);
        
        PayPalResult result = paypalService.executePayment(paypalRequest);
        
        // Convert PayPal response
        return PaymentResult.builder()
            .orderId(orderId)
            .transactionId(result.getTransactionId())
            .success("COMPLETED".equals(result.getStatus()))
            .message(result.getStatusMessage())
            .build();
    }
}

// Client code - works with any adapter
@Service
public class OrderPaymentService {
    
    private final Map<String, PaymentProcessor> processors;
    
    public OrderPaymentService(List<PaymentProcessor> processorList) {
        this.processors = processorList.stream()
            .collect(Collectors.toMap(
                processor -> processor.getClass().getSimpleName(),
                Function.identity()
            ));
    }
    
    public PaymentResult pay(String orderId, BigDecimal amount, String provider) {
        PaymentProcessor processor = processors.get(provider + "Adapter");
        return processor.processPayment(orderId, amount);
    }
}
```

**Key Benefits:**
- ✅ Integrate third-party libraries without modifying them
- ✅ Single Responsibility - adapter handles conversion
- ✅ Easy to swap implementations
- ✅ Testable - mock adapters

**Real-World Use Cases:**
- Payment gateway integration (Stripe, PayPal, Square)
- Cloud storage adapters (AWS S3, Azure Blob, GCP Storage)
- Message queue adapters (RabbitMQ, Kafka, SQS)
- Database drivers

---

#### **Facade Pattern** 🏛️

**Problem:** Complex subsystem with many classes and interfaces. Difficult to use.

**Solution:** Provide simple unified interface to complex subsystem.

**Spring Boot Implementation:**
```java
// Complex subsystems
@Component
class InventoryService {
    public boolean checkStock(Long productId, int quantity) {
        // Complex inventory checking logic
        return true;
    }
    
    public void reserveStock(Long productId, int quantity) {
        // Reserve inventory
    }
}

@Component
class PaymentService {
    public PaymentResult processPayment(PaymentDetails details) {
        // Complex payment processing
        return new PaymentResult(true, "txn_123");
    }
}

@Component
class ShippingService {
    public ShippingInfo calculateShipping(Address address, double weight) {
        // Complex shipping calculation
        return new ShippingInfo("FedEx", new BigDecimal("9.99"));
    }
    
    public void createShipment(Order order) {
        // Create shipment
    }
}

@Component
class NotificationService {
    public void sendOrderConfirmation(String email, Order order) {
        // Send email
    }
    
    public void sendShippingNotification(String email, ShippingInfo info) {
        // Send shipping update
    }
}

// Facade - simplified interface
@Service
public class OrderFacade {
    
    private static final Logger log = LoggerFactory.getLogger(OrderFacade.class);
    
    private final InventoryService inventoryService;
    private final PaymentService paymentService;
    private final ShippingService shippingService;
    private final NotificationService notificationService;
    private final OrderRepository orderRepository;
    
    public OrderFacade(InventoryService inventoryService,
                      PaymentService paymentService,
                      ShippingService shippingService,
                      NotificationService notificationService,
                      OrderRepository orderRepository) {
        this.inventoryService = inventoryService;
        this.paymentService = paymentService;
        this.shippingService = shippingService;
        this.notificationService = notificationService;
        this.orderRepository = orderRepository;
    }
    
    // Simple method that orchestrates complex operations
    public OrderResult placeOrder(OrderRequest request) {
        try {
            // Step 1: Check inventory
            log.info("Checking inventory for product: {}", request.getProductId());
            boolean inStock = inventoryService.checkStock(
                request.getProductId(), 
                request.getQuantity()
            );
            
            if (!inStock) {
                return OrderResult.failure("Product out of stock");
            }
            
            // Step 2: Calculate shipping
            log.info("Calculating shipping");
            ShippingInfo shipping = shippingService.calculateShipping(
                request.getShippingAddress(),
                request.getWeight()
            );
            
            // Step 3: Process payment
            log.info("Processing payment");
            PaymentDetails paymentDetails = PaymentDetails.builder()
                .amount(request.getAmount().add(shipping.getCost()))
                .cardNumber(request.getCardNumber())
                .build();
            
            PaymentResult paymentResult = paymentService.processPayment(paymentDetails);
            
            if (!paymentResult.isSuccess()) {
                return OrderResult.failure("Payment failed");
            }
            
            // Step 4: Reserve inventory
            log.info("Reserving inventory");
            inventoryService.reserveStock(request.getProductId(), request.getQuantity());
            
            // Step 5: Create order
            log.info("Creating order");
            Order order = Order.builder()
                .customerEmail(request.getEmail())
                .productId(request.getProductId())
                .quantity(request.getQuantity())
                .totalAmount(request.getAmount().add(shipping.getCost()))
                .transactionId(paymentResult.getTransactionId())
                .status(OrderStatus.CONFIRMED)
                .build();
            
            Order savedOrder = orderRepository.save(order);
            
            // Step 6: Create shipment
            log.info("Creating shipment");
            shippingService.createShipment(savedOrder);
            
            // Step 7: Send notifications
            log.info("Sending notifications");
            notificationService.sendOrderConfirmation(request.getEmail(), savedOrder);
            notificationService.sendShippingNotification(request.getEmail(), shipping);
            
            return OrderResult.success(savedOrder, "Order placed successfully");
            
        } catch (Exception e) {
            log.error("Order placement failed", e);
            return OrderResult.failure("Order placement failed: " + e.getMessage());
        }
    }
    
    // Another simple method for order tracking
    public OrderTrackingInfo trackOrder(String orderId) {
        Order order = orderRepository.findById(Long.parseLong(orderId))
            .orElseThrow(() -> new OrderNotFoundException(orderId));
        
        // Complex tracking logic hidden behind simple interface
        return OrderTrackingInfo.builder()
            .orderId(orderId)
            .status(order.getStatus())
            .estimatedDelivery(calculateEstimatedDelivery(order))
            .trackingNumber(getTrackingNumber(order))
            .build();
    }
}

// Controller uses facade - simple and clean
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    private final OrderFacade orderFacade;
    
    @PostMapping
    public ResponseEntity<OrderResult> placeOrder(@RequestBody OrderRequest request) {
        OrderResult result = orderFacade.placeOrder(request);
        return result.isSuccess() 
            ? ResponseEntity.ok(result)
            : ResponseEntity.badRequest().body(result);
    }
    
    @GetMapping("/{id}/track")
    public OrderTrackingInfo trackOrder(@PathVariable String id) {
        return orderFacade.trackOrder(id);
    }
}
```

**Key Benefits:**
- ✅ Simplified interface to complex system
- ✅ Loose coupling - clients don't depend on subsystems
- ✅ Easy to use and understand
- ✅ Changes to subsystems don't affect clients

**Real-World Use Cases:**
- Order processing systems
- Account management (create account, set preferences, configure services)
- Booking systems (flights, hotels, car rental)
- Report generation (fetch data, process, format, deliver)

---

#### **Decorator Pattern** 🎨

**Problem:** Add functionality to objects dynamically without modifying their structure.

**Solution:** Wrap objects with decorator objects that add behavior.

**Spring AOP as Decorator:**
```java
// Base service
@Service
public class ProductService {
    
    public Product getProduct(Long id) {
        // Fetch product
        return new Product(id, "Laptop", new BigDecimal("999.99"));
    }
    
    public Product saveProduct(Product product) {
        // Save product
        return product;
    }
}

// Decorator 1: Logging
@Aspect
@Component
public class LoggingDecorator {
    
    private static final Logger log = LoggerFactory.getLogger(LoggingDecorator.class);
    
    @Around("execution(* com.example.service.ProductService.*(..))")
    public Object logMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        log.info("Executing method: {}", methodName);
        
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long duration = System.currentTimeMillis() - start;
        
        log.info("Method {} completed in {}ms", methodName, duration);
        return result;
    }
}

// Decorator 2: Caching
@Aspect
@Component
public class CachingDecorator {
    
    private final Map<String, Object> cache = new ConcurrentHashMap<>();
    
    @Around("@annotation(Cacheable)")
    public Object cacheResult(ProceedingJoinPoint joinPoint) throws Throwable {
        String key = generateKey(joinPoint);
        
        Object cached = cache.get(key);
        if (cached != null) {
            return cached;
        }
        
        Object result = joinPoint.proceed();
        cache.put(key, result);
        return result;
    }
}

// Decorator 3: Performance monitoring
@Aspect
@Component
public class PerformanceMonitoringDecorator {
    
    private final MeterRegistry meterRegistry;
    
    @Around("execution(* com.example.service.*.*(..))")
    public Object monitorPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        Timer.Sample sample = Timer.start(meterRegistry);
        
        try {
            return joinPoint.proceed();
        } finally {
            sample.stop(Timer.builder("method.execution")
                .tag("class", joinPoint.getTarget().getClass().getSimpleName())
                .tag("method", joinPoint.getSignature().getName())
                .register(meterRegistry));
        }
    }
}
```

**Manual Decorator Pattern:**
```java
// Component interface
public interface Coffee {
    String getDescription();
    BigDecimal getCost();
}

// Concrete component
public class SimpleCoffee implements Coffee {
    @Override
    public String getDescription() {
        return "Simple Coffee";
    }
    
    @Override
    public BigDecimal getCost() {
        return new BigDecimal("2.00");
    }
}

// Decorator abstract class
public abstract class CoffeeDecorator implements Coffee {
    protected final Coffee decoratedCoffee;
    
    public CoffeeDecorator(Coffee coffee) {
        this.decoratedCoffee = coffee;
    }
    
    @Override
    public String getDescription() {
        return decoratedCoffee.getDescription();
    }
    
    @Override
    public BigDecimal getCost() {
        return decoratedCoffee.getCost();
    }
}

// Concrete decorators
public class MilkDecorator extends CoffeeDecorator {
    public MilkDecorator(Coffee coffee) {
        super(coffee);
    }
    
    @Override
    public String getDescription() {
        return decoratedCoffee.getDescription() + ", Milk";
    }
    
    @Override
    public BigDecimal getCost() {
        return decoratedCoffee.getCost().add(new BigDecimal("0.50"));
    }
}

public class SugarDecorator extends CoffeeDecorator {
    public SugarDecorator(Coffee coffee) {
        super(coffee);
    }
    
    @Override
    public String getDescription() {
        return decoratedCoffee.getDescription() + ", Sugar";
    }
    
    @Override
    public BigDecimal getCost() {
        return decoratedCoffee.getCost().add(new BigDecimal("0.25"));
    }
}

// Usage
Coffee coffee = new SimpleCoffee();
coffee = new MilkDecorator(coffee);
coffee = new SugarDecorator(coffee);

System.out.println(coffee.getDescription()); // "Simple Coffee, Milk, Sugar"
System.out.println(coffee.getCost());        // 2.75
```

**Key Benefits:**
- ✅ Add behavior at runtime
- ✅ More flexible than inheritance
- ✅ Follow Open/Closed Principle
- ✅ Combine multiple decorators

**Real-World Use Cases:**
- Logging, monitoring, caching (Spring AOP)
- Stream processing (InputStream decorators)
- UI components (borders, scrollbars)
- Security (authentication, authorization)

---

## 2. Backend for Frontend (BFF)

### What is BFF Pattern?

**Problem:** 
- Mobile apps need different data than web apps
- Web needs full details, mobile needs minimal data
- Different screen sizes, different capabilities
- One API trying to serve all clients becomes bloated

**Solution:**
Create separate backend for each frontend type.

### Architecture

```
┌──────────────┐     ┌──────────────────┐
│  Web Client  │────▶│   Web BFF (8081) │
└──────────────┘     └────────┬─────────┘
                              │
┌──────────────┐     ┌────────▼─────────┐     ┌──────────────────┐
│Mobile Client │────▶│ Mobile BFF (8082)│────▶│ Product Service  │
└──────────────┘     └────────┬─────────┘     │ (8083)           │
                              │               ├──────────────────┤
┌──────────────┐     ┌────────▼─────────┐     │ Order Service    │
│  IoT Device  │────▶│  IoT BFF (8084)  │────▶│ (8084)           │
└──────────────┘     └──────────────────┘     └──────────────────┘
```

### Implementation Example

**Common Backend Service:**
```java
// Product Service - provides full product data
@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    @GetMapping("/{id}")
    public ProductDetails getProduct(@PathVariable Long id) {
        return ProductDetails.builder()
            .id(id)
            .name("4K Ultra HD TV")
            .description("55-inch 4K Smart TV with HDR...")
            .price(new BigDecimal("599.99"))
            .specifications(getDetailedSpecs())
            .reviews(getAllReviews())
            .relatedProducts(getRelatedProducts())
            .warranty("2 years")
            .shippingInfo(getShippingDetails())
            .build();
    }
}
```

**Web BFF - Full Details:**
```java
@RestController
@RequestMapping("/web/api/products")
public class WebBFFController {
    
    private final ProductServiceClient productServiceClient;
    private final ReviewServiceClient reviewServiceClient;
    private final RecommendationEngine recommendationEngine;
    
    @GetMapping("/{id}")
    public WebProductResponse getProduct(@PathVariable Long id) {
        // Fetch full data for web
        ProductDetails product = productServiceClient.getProduct(id);
        List<Review> reviews = reviewServiceClient.getReviews(id);
        List<Product> recommended = recommendationEngine.getRecommendations(id);
        
        // Return comprehensive data for web
        return WebProductResponse.builder()
            .product(product)
            .reviews(reviews)
            .averageRating(calculateRating(reviews))
            .totalReviews(reviews.size())
            .recommendedProducts(recommended)
            .breadcrumbs(generateBreadcrumbs(product))
            .seoMetadata(generateSEO(product))
            .build();
    }
}
```

**Mobile BFF - Minimal Data:**
```java
@RestController
@RequestMapping("/mobile/api/products")
public class MobileBFFController {
    
    private final ProductServiceClient productServiceClient;
    
    @GetMapping("/{id}")
    public MobileProductResponse getProduct(@PathVariable Long id) {
        ProductDetails product = productServiceClient.getProduct(id);
        
        // Return minimal data for mobile
        return MobileProductResponse.builder()
            .id(product.getId())
            .name(product.getName())
            .price(product.getPrice())
            .thumbnailUrl(product.getImageUrl())
            .rating(product.getAverageRating())
            .inStock(product.isInStock())
            // Only essential fields for mobile
            .build();
    }
    
    // Mobile-specific endpoints
    @GetMapping("/{id}/quick-view")
    public MobileQuickView getQuickView(@PathVariable Long id) {
        // Even lighter response for quick view
        return MobileQuickView.builder()
            .id(id)
            .name(getProductName(id))
            .price(getPrice(id))
            .image(getThumbnail(id))
            .build();
    }
}
```

### Key Benefits

- ✅ **Optimized for each client** - different data shapes
- ✅ **Better performance** - mobile gets less data
- ✅ **Independent scaling** - scale mobile BFF separately
- ✅ **Team autonomy** - mobile team owns mobile BFF
- ✅ **Easier to change** - modify mobile API without affecting web

### When to Use BFF

**Use BFF when:**
- ✅ Multiple client types (web, mobile, IoT)
- ✅ Different data requirements per client
- ✅ Need client-specific logic
- ✅ Want independent deployment cycles

**Don't use BFF when:**
- ❌ Only one client type
- ❌ All clients need same data
- ❌ Small team (overhead not worth it)

---

## 3. Strangler Fig Pattern

### What is Strangler Fig?

**Problem:** Need to migrate from legacy monolith to microservices without big bang rewrite.

**Solution:** Gradually replace parts of monolith with microservices, running both in parallel.

### How It Works

```
Phase 1: All traffic to monolith
┌────────────┐
│   Client   │──────▶ ┌─────────────┐
└────────────┘        │  Monolith   │
                      └─────────────┘

Phase 2: Route some traffic to microservice
┌────────────┐        ┌─────────────────┐
│   Client   │──────▶ │  API Gateway    │
└────────────┘        └────────┬────────┘
                               │
                      ┌────────┴────────┐
                      │                 │
                 (new) │            (old) │
            ┌──────────▼──┐       ┌─────▼─────┐
            │ User Service│       │ Monolith  │
            └─────────────┘       └───────────┘

Phase 3: Route more traffic
┌────────────┐        ┌─────────────────┐
│   Client   │──────▶ │  API Gateway    │
└────────────┘        └────────┬────────┘
                               │
                      ┌────────┴────────┐
                      │                 │
            ┌─────────▼──────┐  ┌──────▼────────┐
            │ User Service   │  │Product Service│
            └────────────────┘  └───────┬───────┘
                                        │
                                  ┌─────▼──────┐
                                  │  Monolith  │
                                  │ (shrinking)│
                                  └────────────┘

Phase 4: Monolith decommissioned
┌────────────┐        ┌─────────────────┐
│   Client   │──────▶ │  API Gateway    │
└────────────┘        └────────┬────────┘
                               │
                  ┌────────────┼────────────┐
                  │            │            │
        ┌─────────▼──┐  ┌──────▼─────┐  ┌──▼──────────┐
        │    User    │  │  Product   │  │   Order     │
        │  Service   │  │  Service   │  │  Service    │
        └────────────┘  └────────────┘  └─────────────┘
```

### Implementation Strategy

**Step 1: Add API Gateway**
```java
@Configuration
public class StranglerGatewayConfig {
    
    @Bean
    public RouteLocator stranglerRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
            // New microservice - route to new service
            .route("user-service", r -> r
                .path("/api/users/**")
                .uri("http://user-service:8081"))
            
            // Everything else - route to monolith
            .route("legacy-monolith", r -> r
                .path("/api/**")
                .uri("http://legacy-monolith:8080"))
            
            .build();
    }
}
```

**Step 2: Extract First Service**
```java
// New User Microservice
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        // New implementation
        return userService.findById(id);
    }
    
    // Gradually migrate endpoints
}
```

**Step 3: Synchronize Data**
```java
// Dual-write during migration
@Service
public class DualWriteUserService {
    
    private final NewUserRepository newRepo;
    private final LegacyMonolithClient legacyClient;
    
    @Transactional
    public User createUser(UserRequest request) {
        // Write to new database
        User newUser = newRepo.save(toEntity(request));
        
        // Also write to legacy system
        try {
            legacyClient.createUser(toLegacyFormat(request));
        } catch (Exception e) {
            // Log but don't fail - eventual consistency
            log.warn("Failed to sync to legacy system", e);
        }
        
        return newUser;
    }
}
```

**Step 4: Phase Out Gradually**
```java
// Feature flag to control rollout
@Service
public class UserServiceRouter {
    
    @Value("${feature.new-user-service.enabled:false}")
    private boolean useNewService;
    
    @Value("${feature.new-user-service.percentage:0}")
    private int rolloutPercentage;
    
    public User getUser(Long userId) {
        if (shouldUseNewService(userId)) {
            return newUserService.getUser(userId);
        } else {
            return legacyMonolithClient.getUser(userId);
        }
    }
    
    private boolean shouldUseNewService(Long userId) {
        if (!useNewService) {
            return false;
        }
        
        // Percentage rollout
        return (userId % 100) < rolloutPercentage;
    }
}
```

### Best Practices

1. **Start small** - extract least risky service first
2. **Feature flags** - control rollout percentage
3. **Dual-write** - keep legacy and new in sync
4. **Monitor closely** - watch for errors and performance
5. **Gradual rollout** - 1% → 5% → 25% → 50% → 100%
6. **Rollback plan** - be ready to route back to monolith
7. **Decommission carefully** - only after confident

---

## 4. API Versioning Strategies

### Why Version APIs?

- Breaking changes without breaking clients
- Support multiple client versions
- Gradual migration
- Backward compatibility

### Strategy 1: URI Versioning

**Most common and explicit approach.**

```java
// Version 1
@RestController
@RequestMapping("/api/v1/products")
public class ProductV1Controller {
    
    @GetMapping("/{id}")
    public ProductV1 getProduct(@PathVariable Long id) {
        return ProductV1.builder()
            .id(id)
            .name("Laptop")
            .price(999.99)
            .build();
    }
}

// Version 2 - added new fields
@RestController
@RequestMapping("/api/v2/products")
public class ProductV2Controller {
    
    @GetMapping("/{id}")
    public ProductV2 getProduct(@PathVariable Long id) {
        return ProductV2.builder()
            .id(id)
            .name("Laptop")
            .price(new BigDecimal("999.99"))  // Changed to BigDecimal
            .currency("USD")                   // New field
            .taxRate(0.08)                    // New field
            .priceWithTax(calculateTax())     // New field
            .build();
    }
}
```

**Pros:** ✅ Clear, explicit, easy to route  
**Cons:** ❌ URL pollution, version in domain model

---

### Strategy 2: Header Versioning

**Version in HTTP header.**

```java
@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    @GetMapping(value = "/{id}", headers = "API-Version=1")
    public ProductV1 getProductV1(@PathVariable Long id) {
        return productService.getV1(id);
    }
    
    @GetMapping(value = "/{id}", headers = "API-Version=2")
    public ProductV2 getProductV2(@PathVariable Long id) {
        return productService.getV2(id);
    }
}

// Client usage:
// curl -H "API-Version: 2" http://api.example.com/products/1
```

**Pros:** ✅ Clean URLs, RESTful  
**Cons:** ❌ Not visible in URL, harder to test

---

### Strategy 3: Content Negotiation (Accept Header)

**Use Accept header mime-types.**

```java
@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    @GetMapping(value = "/{id}", produces = "application/vnd.company.v1+json")
    public ProductV1 getProductV1(@PathVariable Long id) {
        return productService.getV1(id);
    }
    
    @GetMapping(value = "/{id}", produces = "application/vnd.company.v2+json")
    public ProductV2 getProductV2(@PathVariable Long id) {
        return productService.getV2(id);
    }
}

// Client usage:
// curl -H "Accept: application/vnd.company.v2+json" http://api.example.com/products/1
```

**Pros:** ✅ True REST, clean URLs  
**Cons:** ❌ Complex, harder to understand

---

### Strategy 4: Query Parameter

**Version as query parameter.**

```java
@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    @GetMapping("/{id}")
    public Object getProduct(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") int version) {
        
        return switch (version) {
            case 1 -> productService.getV1(id);
            case 2 -> productService.getV2(id);
            default -> throw new UnsupportedVersionException(version);
        };
    }
}

// Client usage:
// curl http://api.example.com/products/1?version=2
```

**Pros:** ✅ Simple, flexible  
**Cons:** ❌ Pollutes query params, not RESTful

---

### Recommended: URI Versioning with Deprecation

**Best practice approach:**

```java
// V1 - deprecated but still supported
@RestController
@RequestMapping("/api/v1/products")
@Deprecated
public class ProductV1Controller {
    
    @GetMapping("/{id}")
    public ResponseEntity<ProductV1> getProduct(@PathVariable Long id) {
        ProductV1 product = productService.getV1(id);
        
        // Add deprecation header
        return ResponseEntity.ok()
            .header("X-API-Deprecated", "true")
            .header("X-API-Deprecation-Date", "2025-12-31")
            .header("X-API-Migration-Guide", "https://api.example.com/docs/migration/v1-to-v2")
            .body(product);
    }
}

// V2 - current version
@RestController
@RequestMapping("/api/v2/products")
public class ProductV2Controller {
    
    @GetMapping("/{id}")
    public ProductV2 getProduct(@PathVariable Long id) {
        return productService.getV2(id);
    }
}

// V3 - latest (if needed)
@RestController
@RequestMapping("/api/v3/products")
public class ProductV3Controller {
    
    @GetMapping("/{id}")
    public ProductV3 getProduct(@PathVariable Long id) {
        return productService.getV3(id);
    }
}
```

### Version Management Service

```java
@Component
public class APIVersionManager {
    
    private final Map<Integer, LocalDate> deprecationDates = Map.of(
        1, LocalDate.of(2025, 12, 31),  // V1 will be removed
        2, LocalDate.of(2026, 6, 30)    // V2 will be removed
    );
    
    private final int CURRENT_VERSION = 3;
    private final int MINIMUM_SUPPORTED_VERSION = 1;
    
    public boolean isVersionSupported(int version) {
        return version >= MINIMUM_SUPPORTED_VERSION && version <= CURRENT_VERSION;
    }
    
    public boolean isVersionDeprecated(int version) {
        return deprecationDates.containsKey(version);
    }
    
    public LocalDate getDeprecationDate(int version) {
        return deprecationDates.get(version);
    }
    
    public int getCurrentVersion() {
        return CURRENT_VERSION;
    }
}
```

### Best Practices

1. **Use URI versioning** - clearest and most common
2. **Version major changes only** - minor changes don't need versions
3. **Deprecate gracefully** - give 6-12 months notice
4. **Document migration** - provide clear upgrade path
5. **Monitor usage** - track which versions are still used
6. **Limit versions** - support 2-3 versions max
7. **Add headers** - deprecation warnings in response

---

## 5. Reactive Microservices

### What is Reactive Programming?

**Traditional (Blocking):**
```java
// Each request blocks a thread
@GetMapping("/users/{id}")
public User getUser(@PathVariable Long id) {
    User user = userService.findById(id);        // Blocks thread
    Orders orders = orderService.getOrders(id);  // Blocks thread
    return user;
}
// Thread waits while database/network calls complete
```

**Reactive (Non-Blocking):**
```java
// Thread doesn't wait - handles other requests
@GetMapping("/users/{id}")
public Mono<User> getUser(@PathVariable Long id) {
    return userService.findById(id)              // Returns immediately
        .flatMap(user -> 
            orderService.getOrders(id)           // Asynchronous
                .map(orders -> user.setOrders(orders))
        );
}
// Thread is free to handle other requests while waiting
```

### Key Concepts

**Mono** - 0 or 1 element
**Flux** - 0 to N elements

```java
Mono<User> user = Mono.just(new User("John"));   // Single item
Flux<User> users = Flux.just(user1, user2, user3); // Multiple items
```

### Spring WebFlux Implementation

**Dependencies:**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-r2dbc</artifactId>
</dependency>
<dependency>
    <groupId>io.r2dbc</groupId>
    <artifactId>r2dbc-postgresql</artifactId>
</dependency>
```

**Reactive Controller:**
```java
@RestController
@RequestMapping("/api/products")
public class ReactiveProductController {
    
    private final ReactiveProductService productService;
    
    // Return Mono for single item
    @GetMapping("/{id}")
    public Mono<Product> getProduct(@PathVariable String id) {
        return productService.findById(id);
    }
    
    // Return Flux for multiple items
    @GetMapping
    public Flux<Product> getAllProducts() {
        return productService.findAll();
    }
    
    // Return Flux with filtering
    @GetMapping("/search")
    public Flux<Product> searchProducts(@RequestParam String keyword) {
        return productService.findAll()
            .filter(product -> product.getName().contains(keyword));
    }
    
    // Server-Sent Events - real-time updates
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Product> streamProducts() {
        return productService.findAll()
            .delayElements(Duration.ofSeconds(1)); // Emit every second
    }
    
    // Create with reactive
    @PostMapping
    public Mono<Product> createProduct(@RequestBody Product product) {
        return productService.save(product);
    }
}
```

**Reactive Service:**
```java
@Service
public class ReactiveProductService {
    
    private final ReactiveProductRepository repository;
    private final WebClient webClient;
    
    public Mono<Product> findById(String id) {
        return repository.findById(id);
    }
    
    public Flux<Product> findAll() {
        return repository.findAll();
    }
    
    public Mono<Product> save(Product product) {
        return repository.save(product);
    }
    
    // Combining multiple async calls
    public Mono<ProductDetails> getProductWithDetails(String id) {
        Mono<Product> productMono = repository.findById(id);
        Mono<List<Review>> reviewsMono = getReviews(id);
        Mono<Inventory> inventoryMono = getInventory(id);
        
        // Combine results
        return Mono.zip(productMono, reviewsMono, inventoryMono)
            .map(tuple -> ProductDetails.builder()
                .product(tuple.getT1())
                .reviews(tuple.getT2())
                .inventory(tuple.getT3())
                .build());
    }
    
    // Call external service reactively
    private Mono<List<Review>> getReviews(String productId) {
        return webClient.get()
            .uri("/reviews/{productId}", productId)
            .retrieve()
            .bodyToFlux(Review.class)
            .collectList();
    }
}
```

**Reactive Repository:**
```java
public interface ReactiveProductRepository extends ReactiveCrudRepository<Product, String> {
    
    Flux<Product> findByCategory(String category);
    
    Flux<Product> findByPriceLessThan(BigDecimal price);
    
    @Query("SELECT * FROM products WHERE name LIKE :name")
    Flux<Product> searchByName(String name);
}
```

**WebClient for Reactive HTTP Calls:**
```java
@Configuration
public class WebClientConfig {
    
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
            .baseUrl("http://api.example.com")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
    }
}

@Service
public class ExternalAPIService {
    
    private final WebClient webClient;
    
    public Mono<ExternalData> fetchData(String id) {
        return webClient.get()
            .uri("/data/{id}", id)
            .retrieve()
            .bodyToMono(ExternalData.class)
            .timeout(Duration.ofSeconds(5))
            .retry(3);
    }
    
    public Flux<ExternalData> fetchAllData() {
        return webClient.get()
            .uri("/data")
            .retrieve()
            .bodyToFlux(ExternalData.class);
    }
}
```

### When to Use Reactive?

**Use Reactive When:**
- ✅ High-concurrency applications
- ✅ Real-time updates (SSE, WebSocket)
- ✅ Streaming data
- ✅ Many I/O operations
- ✅ Need to scale to many concurrent users

**Don't Use Reactive When:**
- ❌ Simple CRUD applications
- ❌ Low traffic
- ❌ Team unfamiliar with reactive
- ❌ Blocking dependencies (JDBC, many libraries)

### Benefits

- ⚡ **Better throughput** - handle more requests with fewer threads
- 💪 **Scalability** - scales to high concurrency
- 📊 **Backpressure** - handle slow consumers
- 🔄 **Non-blocking** - threads don't wait for I/O

### Challenges

- 📚 **Learning curve** - harder to understand
- 🐛 **Debugging** - stack traces are complex
- 🧪 **Testing** - requires different approach
- 📦 **Ecosystem** - fewer reactive libraries

---

## 6. GraphQL APIs

### Why GraphQL?

**REST Problems:**
- Over-fetching - get more data than needed
- Under-fetching - need multiple requests
- Versioning headaches
- Documentation drift

**GraphQL Benefits:**
- ✅ Request exactly what you need
- ✅ Single request for multiple resources
- ✅ Strong typing
- ✅ Self-documenting

### GraphQL vs REST

**REST:**
```
GET /api/users/1
GET /api/users/1/orders
GET /api/users/1/profile
// 3 separate requests
```

**GraphQL:**
```graphql
query {
  user(id: 1) {
    name
    email
    orders {
      id
      total
    }
    profile {
      avatar
    }
  }
}
// 1 request, get exactly what you need
```

### Implementation with Spring Boot

**Dependencies:**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-graphql</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

**Schema Definition (schema.graphqls):**
```graphql
type Query {
    product(id: ID!): Product
    products(category: String, limit: Int): [Product]
    searchProducts(keyword: String!): [Product]
}

type Mutation {
    createProduct(input: ProductInput!): Product
    updateProduct(id: ID!, input: ProductInput!): Product
    deleteProduct(id: ID!): Boolean
}

type Product {
    id: ID!
    name: String!
    description: String
    price: Float!
    category: String
    inStock: Boolean!
    reviews: [Review]
}

type Review {
    id: ID!
    rating: Int!
    comment: String
    author: String
}

input ProductInput {
    name: String!
    description: String
    price: Float!
    category: String
    inStock: Boolean
}
```

**Query Resolver:**
```java
@Controller
public class ProductQueryResolver {
    
    private final ProductService productService;
    
    @QueryMapping
    public Product product(@Argument String id) {
        return productService.findById(id);
    }
    
    @QueryMapping
    public List<Product> products(
            @Argument String category,
            @Argument Integer limit) {
        
        if (category != null) {
            return productService.findByCategory(category, limit);
        }
        return productService.findAll(limit);
    }
    
    @QueryMapping
    public List<Product> searchProducts(@Argument String keyword) {
        return productService.search(keyword);
    }
}
```

**Mutation Resolver:**
```java
@Controller
public class ProductMutationResolver {
    
    private final ProductService productService;
    
    @MutationMapping
    public Product createProduct(@Argument ProductInput input) {
        Product product = Product.builder()
            .name(input.getName())
            .description(input.getDescription())
            .price(input.getPrice())
            .category(input.getCategory())
            .inStock(input.getInStock())
            .build();
        
        return productService.create(product);
    }
    
    @MutationMapping
    public Product updateProduct(@Argument String id, @Argument ProductInput input) {
        return productService.update(id, input);
    }
    
    @MutationMapping
    public Boolean deleteProduct(@Argument String id) {
        productService.delete(id);
        return true;
    }
}
```

**Nested Field Resolver:**
```java
@Controller
public class ProductFieldResolver {
    
    private final ReviewService reviewService;
    
    // Resolver for Product.reviews field
    @SchemaMapping
    public List<Review> reviews(Product product) {
        return reviewService.findByProductId(product.getId());
    }
}
```

**Client Usage:**
```graphql
# Query - get specific fields
query GetProduct {
  product(id: "123") {
    name
    price
    inStock
    reviews {
      rating
      comment
    }
  }
}

# Mutation - create product
mutation CreateProduct {
  createProduct(input: {
    name: "New Laptop"
    price: 1299.99
    category: "Electronics"
    inStock: true
  }) {
    id
    name
    price
  }
}

# Query with variables
query SearchProducts($keyword: String!) {
  searchProducts(keyword: $keyword) {
    id
    name
    price
  }
}
```

### Benefits

- ✅ **Flexible queries** - clients control response shape
- ✅ **Single endpoint** - no URL management
- ✅ **Strongly typed** - schema defines API
- ✅ **Self-documenting** - GraphiQL playground
- ✅ **No over-fetching** - get only what you need

### When to Use

**Use GraphQL when:**
- ✅ Multiple client types (web, mobile, desktop)
- ✅ Complex data relationships
- ✅ Frequent UI changes
- ✅ Need flexible querying

**Stick with REST when:**
- ❌ Simple CRUD operations
- ❌ File uploads/downloads
- ❌ Caching is important
- ❌ Team unfamiliar with GraphQL

---

## 7. gRPC Communication

### What is gRPC?

**gRPC** = Google Remote Procedure Call

- High-performance RPC framework
- Uses Protocol Buffers (binary format)
- HTTP/2 based
- Strongly typed
- Multiple language support

### gRPC vs REST

| Feature | REST | gRPC |
|---------|------|------|
| Format | JSON (text) | Protobuf (binary) |
| Speed | Slower | Faster (5-10x) |
| Size | Larger | Smaller (30-50%) |
| Streaming | Limited | Built-in |
| Browser | Full support | Limited |
| Human-readable | Yes | No |

### When to Use gRPC

**Use gRPC for:**
- ✅ Internal microservice communication
- ✅ High-performance requirements
- ✅ Streaming data
- ✅ Polyglot systems
- ✅ Real-time communication

**Use REST for:**
- ✅ Public APIs
- ✅ Browser clients
- ✅ Simple CRUD
- ✅ Human-readable APIs

### Implementation

**1. Define Protocol Buffer:**
```protobuf
syntax = "proto3";

package com.example.grpc;

service ProductService {
  rpc GetProduct (ProductRequest) returns (ProductResponse);
  rpc ListProducts (ListProductsRequest) returns (stream ProductResponse);
  rpc CreateProduct (CreateProductRequest) returns (ProductResponse);
  rpc UpdateProduct (UpdateProductRequest) returns (ProductResponse);
}

message ProductRequest {
  string id = 1;
}

message ProductResponse {
  string id = 1;
  string name = 2;
  string description = 3;
  double price = 4;
  bool in_stock = 5;
}

message ListProductsRequest {
  string category = 1;
  int32 limit = 2;
}

message CreateProductRequest {
  string name = 1;
  string description = 2;
  double price = 3;
  string category = 4;
}

message UpdateProductRequest {
  string id = 1;
  string name = 2;
  double price = 3;
  bool in_stock = 4;
}
```

**2. gRPC Server:**
```java
@GrpcService
public class ProductGrpcService extends ProductServiceGrpc.ProductServiceImplBase {
    
    private final ProductService productService;
    
    @Override
    public void getProduct(ProductRequest request, StreamObserver<ProductResponse> responseObserver) {
        Product product = productService.findById(request.getId());
        
        ProductResponse response = ProductResponse.newBuilder()
            .setId(product.getId())
            .setName(product.getName())
            .setDescription(product.getDescription())
            .setPrice(product.getPrice().doubleValue())
            .setInStock(product.isInStock())
            .build();
        
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
    
    @Override
    public void listProducts(ListProductsRequest request, StreamObserver<ProductResponse> responseObserver) {
        List<Product> products = productService.findByCategory(
            request.getCategory(),
            request.getLimit()
        );
        
        products.forEach(product -> {
            ProductResponse response = toResponse(product);
            responseObserver.onNext(response);
        });
        
        responseObserver.onCompleted();
    }
    
    @Override
    public void createProduct(CreateProductRequest request, StreamObserver<ProductResponse> responseObserver) {
        Product product = Product.builder()
            .name(request.getName())
            .description(request.getDescription())
            .price(BigDecimal.valueOf(request.getPrice()))
            .category(request.getCategory())
            .build();
        
        Product created = productService.create(product);
        
        responseObserver.onNext(toResponse(created));
        responseObserver.onCompleted();
    }
}
```

**3. gRPC Client:**
```java
@Service
public class ProductGrpcClient {
    
    private final ProductServiceGrpc.ProductServiceBlockingStub blockingStub;
    
    public ProductGrpcClient(ManagedChannel channel) {
        this.blockingStub = ProductServiceGrpc.newBlockingStub(channel);
    }
    
    public ProductResponse getProduct(String id) {
        ProductRequest request = ProductRequest.newBuilder()
            .setId(id)
            .build();
        
        return blockingStub.getProduct(request);
    }
    
    public List<ProductResponse> listProducts(String category, int limit) {
        ListProductsRequest request = ListProductsRequest.newBuilder()
            .setCategory(category)
            .setLimit(limit)
            .build();
        
        List<ProductResponse> products = new ArrayList<>();
        Iterator<ProductResponse> iterator = blockingStub.listProducts(request);
        
        iterator.forEachRemaining(products::add);
        
        return products;
    }
}
```

### Key Benefits

- ⚡ **High Performance** - binary format, HTTP/2
- 📦 **Small Payload** - 30-50% smaller than JSON
- 🔄 **Bi-directional Streaming** - both sides can stream
- 🎯 **Strongly Typed** - compile-time type safety
- 🌐 **Multi-language** - generate clients in any language

---

## 8. Advanced Database Patterns

### Database per Service

**Problem:** Microservices sharing database creates tight coupling.

**Solution:** Each microservice has its own database.

```
┌──────────────┐    ┌──────────────┐    ┌──────────────┐
│    User      │    │   Product    │    │    Order     │
│   Service    │    │   Service    │    │   Service    │
└──────┬───────┘    └──────┬───────┘    └──────┬───────┘
       │                   │                   │
       ▼                   ▼                   ▼
  ┌─────────┐        ┌─────────┐        ┌─────────┐
  │  User   │        │ Product │        │  Order  │
  │   DB    │        │   DB    │        │   DB    │
  └─────────┘        └─────────┘        └─────────┘
```

**Benefits:**
- ✅ Loose coupling
- ✅ Independent scaling
- ✅ Technology diversity

**Challenges:**
- ❌ Data consistency
- ❌ Joins across services
- ❌ Transaction management

---

### Saga Pattern

**Problem:** Distributed transactions across multiple services.

**Solution:** Chain of local transactions with compensation.

```java
// Order Saga Orchestrator
@Service
public class OrderSagaOrchestrator {
    
    private final OrderService orderService;
    private final PaymentService paymentService;
    private final InventoryService inventoryService;
    private final ShippingService shippingService;
    
    @Transactional
    public OrderResult processOrder(OrderRequest request) {
        String sagaId = UUID.randomUUID().toString();
        
        try {
            // Step 1: Create order
            Order order = orderService.createOrder(request, sagaId);
            
            // Step 2: Reserve inventory
            inventoryService.reserveStock(order.getProductId(), order.getQuantity(), sagaId);
            
            // Step 3: Process payment
            PaymentResult payment = paymentService.charge(order.getTotalAmount(), sagaId);
            
            // Step 4: Create shipment
            shippingService.createShipment(order, sagaId);
            
            // Success - complete saga
            orderService.completeOrder(order.getId());
            return OrderResult.success(order);
            
        } catch (InventoryException e) {
            // Compensation: Cancel order
            orderService.cancelOrder(sagaId);
            return OrderResult.failure("Out of stock");
            
        } catch (PaymentException e) {
            // Compensation: Release inventory, cancel order
            inventoryService.releaseStock(sagaId);
            orderService.cancelOrder(sagaId);
            return OrderResult.failure("Payment failed");
            
        } catch (ShippingException e) {
            // Compensation: Refund payment, release inventory, cancel order
            paymentService.refund(sagaId);
            inventoryService.releaseStock(sagaId);
            orderService.cancelOrder(sagaId);
            return OrderResult.failure("Shipping unavailable");
        }
    }
}
```

---

### CQRS (Command Query Responsibility Segregation)

**Problem:** Same model for reads and writes is inefficient.

**Solution:** Separate models for commands (writes) and queries (reads).

```
┌──────────────┐                ┌──────────────┐
│   Commands   │                │   Queries    │
│   (Write)    │                │   (Read)     │
└──────┬───────┘                └──────▲───────┘
       │                               │
       ▼                               │
┌─────────────┐                        │
│  Write DB   │───────Events──────────▶│
│ (Normalized)│                   ┌────┴─────┐
└─────────────┘                   │  Read DB │
                                  │(Denormal)│
                                  └──────────┘
```

**Implementation:**
```java
// Command side
@Service
public class ProductCommandService {
    
    private final ProductRepository repository;
    private final ApplicationEventPublisher eventPublisher;
    
    @Transactional
    public Product createProduct(CreateProductCommand command) {
        Product product = Product.builder()
            .name(command.getName())
            .price(command.getPrice())
            .build();
        
        Product saved = repository.save(product);
        
        // Publish event for read side
        eventPublisher.publishEvent(new ProductCreatedEvent(saved));
        
        return saved;
    }
}

// Query side
@Service
public class ProductQueryService {
    
    private final ProductReadRepository readRepository;
    
    public ProductView getProduct(String id) {
        return readRepository.findById(id);
    }
    
    public List<ProductView> searchProducts(String keyword) {
        return readRepository.searchByKeyword(keyword);
    }
}

// Event handler - sync read model
@Component
public class ProductReadModelUpdater {
    
    private final ProductReadRepository readRepository;
    
    @EventListener
    public void handleProductCreated(ProductCreatedEvent event) {
        ProductView view = ProductView.builder()
            .id(event.getProduct().getId())
            .name(event.getProduct().getName())
            .price(event.getProduct().getPrice())
            // Denormalized data
            .categoryName(event.getProduct().getCategory().getName())
            .supplierName(event.getProduct().getSupplier().getName())
            .build();
        
        readRepository.save(view);
    }
}
```

---

## 9. Best Practices

### 1. **Single Responsibility**
Each microservice does one thing well.

### 2. **API Design**
- Consistent naming conventions
- Versioning strategy
- Pagination for lists
- Proper HTTP status codes

### 3. **Error Handling**
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(404)
                .error("Not Found")
                .message(ex.getMessage())
                .build());
    }
}
```

### 4. **Logging and Monitoring**
- Structured logging
- Correlation IDs
- Metrics collection
- Distributed tracing

### 5. **Security**
- Authentication/Authorization
- API key management
- Rate limiting
- Input validation

### 6. **Testing**
- Unit tests
- Integration tests
- Contract tests
- End-to-end tests

### 7. **Documentation**
- OpenAPI/Swagger
- README files
- Architecture diagrams
- API examples

### 8. **Configuration Management**
- Externalized configuration
- Environment-specific settings
- Feature flags
- Secrets management

### 9. **Deployment**
- Container images
- Health checks
- Graceful shutdown
- Rolling updates

### 10. **Performance**
- Caching strategies
- Connection pooling
- Async processing
- Load balancing

---

## 📚 Summary

This module covered:

✅ **Design Patterns**: Factory, Builder, Strategy, Observer, Adapter, Facade, Decorator  
✅ **BFF Pattern**: Backend for Frontend  
✅ **Strangler Fig**: Legacy migration strategy  
✅ **API Versioning**: Multiple versioning strategies  
✅ **Reactive**: Spring WebFlux, non-blocking I/O  
✅ **GraphQL**: Flexible query API  
✅ **gRPC**: High-performance RPC  
✅ **Database Patterns**: Database per service, Saga, CQRS  
✅ **Best Practices**: Production-ready microservices  

---

## 🎯 Next Steps

1. **Practice patterns** - Implement in small projects
2. **Read documentation** - Spring, GraphQL, gRPC docs
3. **Build demos** - Create sample services
4. **Move to Module 12** - Apply patterns in capstone project

---

**Duration:** 5-7 days  
**Difficulty:** Advanced  
**Prerequisites:** Modules 01-10  

🎉 **You're almost done! One more module - the Capstone Project!** 🚀
