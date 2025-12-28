# E-Commerce Microservices Demo

Complete e-commerce system demonstrating microservices architecture with Spring Cloud.

## Architecture Overview

```
                    ┌─────────────────┐
                    │  Eureka Server  │
                    │   Port: 8761    │
                    └────────┬────────┘
                             │
                  ┌──────────┴──────────┐
                  │  All services       │
                  │  register here      │
                  └──────────┬──────────┘
                             │
                    ┌────────┴────────┐
                    │   API Gateway   │
                    │   Port: 8080    │
                    └────────┬────────┘
                             │
        ┌────────────────────┼────────────────────┐
        │                    │                    │
   ┌────┴────┐         ┌─────┴──────┐      ┌─────┴──────┐
   │  User   │         │  Product   │      │   Order    │
   │ Service │         │  Service   │      │  Service   │
   │  :8081  │         │   :8082    │      │   :8083    │
   └─────────┘         └────────────┘      └─────┬──────┘
                                                  │
                                        ┌─────────┴─────────┐
                                        │  Calls User &     │
                                        │  Product Services │
                                        └───────────────────┘
```

## Services

### 1. Eureka Server (Port 8761)
- **Purpose**: Service Discovery
- **URL**: http://localhost:8761
- **Features**:
  - Service registration
  - Service discovery
  - Health monitoring
  - Dashboard for viewing registered services

### 2. API Gateway (Port 8080)
- **Purpose**: Single entry point for all microservices
- **URL**: http://localhost:8080
- **Features**:
  - Request routing
  - Load balancing
  - Service discovery integration
- **Routes**:
  - `/api/users/**` → USER-SERVICE
  - `/api/products/**` → PRODUCT-SERVICE
  - `/api/orders/**` → ORDER-SERVICE

### 3. User Service (Port 8081)
- **Purpose**: User management
- **Direct URL**: http://localhost:8081
- **Gateway URL**: http://localhost:8080/api/users
- **Database**: H2 (userdb)
- **H2 Console**: http://localhost:8081/h2-console
- **Features**:
  - CRUD operations for users
  - Email-based lookup
  - User validation

### 4. Product Service (Port 8082)
- **Purpose**: Product catalog management
- **Direct URL**: http://localhost:8082
- **Gateway URL**: http://localhost:8080/api/products
- **Database**: H2 (productdb)
- **H2 Console**: http://localhost:8082/h2-console
- **Features**:
  - CRUD operations for products
  - Category-based filtering
  - Product search
  - Stock management

### 5. Order Service (Port 8083)
- **Purpose**: Order processing with inter-service communication
- **Direct URL**: http://localhost:8083
- **Gateway URL**: http://localhost:8080/api/orders
- **Database**: H2 (orderdb)
- **H2 Console**: http://localhost:8083/h2-console
- **Features**:
  - Create orders
  - Validate users (calls User Service)
  - Check product availability (calls Product Service)
  - Reduce product stock automatically
  - Order status management
  - Enriched order details with user and product info

## Startup Order

**IMPORTANT**: Start services in this order:

1. **Eureka Server** (Port 8761)
   ```bash
   cd eureka-server
   mvn spring-boot:run
   ```
   Wait until Eureka dashboard is accessible

2. **API Gateway** (Port 8080)
   ```bash
   cd api-gateway
   mvn spring-boot:run
   ```

3. **User Service** (Port 8081)
   ```bash
   cd user-service
   mvn spring-boot:run
   ```

4. **Product Service** (Port 8082)
   ```bash
   cd product-service
   mvn spring-boot:run
   ```

5. **Order Service** (Port 8083)
   ```bash
   cd order-service
   mvn spring-boot:run
   ```

## Testing the System

### 1. Check Eureka Dashboard
Visit http://localhost:8761 to verify all services are registered:
- API-GATEWAY
- USER-SERVICE
- PRODUCT-SERVICE
- ORDER-SERVICE

### 2. Create Test Data

#### Create a User
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "phone": "1234567890",
    "address": "123 Main St"
  }'
```

Response:
```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john@example.com",
  "phone": "1234567890",
  "address": "123 Main St"
}
```

#### Create a Product
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop",
    "description": "High-performance laptop",
    "price": 1200.00,
    "stock": 50,
    "category": "Electronics"
  }'
```

Response:
```json
{
  "id": 1,
  "name": "Laptop",
  "description": "High-performance laptop",
  "price": 1200.00,
  "stock": 50,
  "category": "Electronics"
}
```

#### Create an Order
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "productId": 1,
    "quantity": 2
  }'
```

Response (enriched with user and product names):
```json
{
  "id": 1,
  "userId": 1,
  "productId": 1,
  "quantity": 2,
  "totalPrice": 2400.00,
  "status": "PENDING",
  "orderDate": "2024-01-15T10:30:00",
  "userName": "John Doe",
  "productName": "Laptop"
}
```

### 3. Verify Inter-Service Communication

Check product stock was reduced:
```bash
curl http://localhost:8080/api/products/1
```

The stock should now be 48 (reduced by 2 from the order).

### 4. Get User Orders
```bash
curl http://localhost:8080/api/orders/user/1
```

### 5. Update Order Status
```bash
curl -X PUT "http://localhost:8080/api/orders/1/status?status=CONFIRMED"
```

## API Endpoints

### User Service

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/users | Get all users |
| GET | /api/users/{id} | Get user by ID |
| GET | /api/users/email/{email} | Get user by email |
| POST | /api/users | Create new user |
| PUT | /api/users/{id} | Update user |
| DELETE | /api/users/{id} | Delete user |

### Product Service

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/products | Get all products |
| GET | /api/products/{id} | Get product by ID |
| GET | /api/products/category/{category} | Get products by category |
| GET | /api/products/search?name={name} | Search products |
| POST | /api/products | Create new product |
| PUT | /api/products/{id} | Update product |
| DELETE | /api/products/{id} | Delete product |
| GET | /api/products/{id}/available?quantity={qty} | Check availability |
| PUT | /api/products/{id}/reduce-stock?quantity={qty} | Reduce stock |

### Order Service

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/orders | Get all orders |
| GET | /api/orders/{id} | Get order by ID |
| GET | /api/orders/user/{userId} | Get orders by user |
| GET | /api/orders/status/{status} | Get orders by status |
| POST | /api/orders | Create new order |
| PUT | /api/orders/{id}/status?status={status} | Update order status |
| DELETE | /api/orders/{id} | Delete order |

## Key Microservices Patterns Demonstrated

### 1. Service Discovery
- Services register with Eureka Server automatically
- No need for hardcoded URLs
- Dynamic service location

### 2. API Gateway
- Single entry point (http://localhost:8080)
- Automatic route configuration
- Load balancing built-in

### 3. Database per Service
- Each service has its own H2 database
- Data isolation and independence
- userdb, productdb, orderdb

### 4. Inter-Service Communication
- Order Service calls User Service to validate users
- Order Service calls Product Service to check availability and reduce stock
- Uses `RestTemplate` with `@LoadBalanced` for service discovery

### 5. Service Independence
- Each service can be deployed independently
- Separate databases
- Different ports

## Common Issues & Solutions

### Issue 1: "Connection refused" when creating orders
**Cause**: Services not fully registered with Eureka yet  
**Solution**: Wait 30 seconds after starting all services before testing

### Issue 2: Gateway returns 404
**Cause**: Service not registered with Eureka  
**Solution**: Check Eureka dashboard (http://localhost:8761) to verify service registration

### Issue 3: Order creation fails with "User not found"
**Cause**: User Service not running or user doesn't exist  
**Solution**: 
1. Verify User Service is running
2. Create a user first using POST /api/users

### Issue 4: "Insufficient stock" error
**Cause**: Product has insufficient stock  
**Solution**: Check product stock and create more products or reduce order quantity

## H2 Database Access

Each service has its own H2 console:

- **User Service**: http://localhost:8081/h2-console
  - JDBC URL: `jdbc:h2:mem:userdb`
  - Username: `sa`
  - Password: (empty)

- **Product Service**: http://localhost:8082/h2-console
  - JDBC URL: `jdbc:h2:mem:productdb`
  - Username: `sa`
  - Password: (empty)

- **Order Service**: http://localhost:8083/h2-console
  - JDBC URL: `jdbc:h2:mem:orderdb`
  - Username: `sa`
  - Password: (empty)

## Technologies Used

- **Spring Boot 3.2.0**: Core framework
- **Spring Cloud 2023.0.0**: Microservices infrastructure
- **Spring Cloud Netflix Eureka**: Service discovery
- **Spring Cloud Gateway**: API gateway
- **Spring Data JPA**: Data persistence
- **H2 Database**: In-memory database
- **Lombok**: Reduce boilerplate code
- **Spring Cloud LoadBalancer**: Client-side load balancing

## Learning Objectives

After running this demo, you will understand:

1. ✅ How service discovery works with Eureka
2. ✅ How to configure and use API Gateway
3. ✅ How microservices communicate with each other
4. ✅ Database per service pattern
5. ✅ Service registration and discovery
6. ✅ Load balancing in microservices
7. ✅ RESTful inter-service communication
8. ✅ Transaction handling across services

## Next Steps

1. Add **Circuit Breaker** pattern with Resilience4j
2. Implement **Distributed Tracing** with Sleuth and Zipkin
3. Add **Centralized Configuration** with Spring Cloud Config
4. Implement **API Authentication** with Spring Security
5. Add **Message Queue** for asynchronous communication
6. Implement **Saga Pattern** for distributed transactions
