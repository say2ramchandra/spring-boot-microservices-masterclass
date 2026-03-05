# E-Commerce Microservices - Setup Guide

> **Complete setup instructions to get the system running**

## 📋 Table of Contents

1. [Prerequisites](#prerequisites)
2. [Environment Setup](#environment-setup)
3. [Running Options](#running-options)
4. [Verification](#verification)
5. [Troubleshooting](#troubleshooting)

---

## 🔧 Prerequisites

### Required Software

#### 1. Java Development Kit (JDK) 17+
```bash
# Check Java version
java -version

# Should show: java version "17.0.x" or higher
```

**Installation**:
- **Windows**: Download from [Oracle](https://www.oracle.com/java/technologies/downloads/) or use [Adoptium](https://adoptium.net/)
- **macOS**: `brew install openjdk@17`
- **Linux**: `sudo apt install openjdk-17-jdk`

#### 2. Maven 3.8+
```bash
# Check Maven version
mvn -version

# Should show: Apache Maven 3.8.x or higher
```

**Installation**:
- **Windows**: Download from [Apache Maven](https://maven.apache.org/download.cgi)
- **macOS**: `brew install maven`
- **Linux**: `sudo apt install maven`

#### 3. Docker Desktop
```bash
# Check Docker version
docker --version
docker-compose --version

# Should show: Docker version 20.x or higher
```

**Installation**:
- Download from [Docker Desktop](https://www.docker.com/products/docker-desktop/)
- Ensure Docker daemon is running

**Docker Resources** (Recommended):
- Memory: 8GB minimum, 12GB recommended
- CPUs: 4 minimum, 6 recommended
- Disk: 50GB available space

#### 4. Git
```bash
# Check Git version
git --version
```

**Installation**:
- **Windows**: [Git for Windows](https://git-scm.com/download/win)
- **macOS**: `brew install git`
- **Linux**: `sudo apt install git`

### Optional but Recommended

#### Kubernetes (Choose One)

**Option 1: Docker Desktop Kubernetes**
- Enable in Docker Desktop Settings → Kubernetes
- Simplest option for local development

**Option 2: Minikube**
```bash
# Install Minikube
# macOS
brew install minikube

# Start cluster
minikube start --memory=8192 --cpus=4
```

#### Development Tools

- **IDE**: IntelliJ IDEA (recommended) or VS Code with Java extensions
- **API Testing**: Postman or Insomnia
- **Database Client**: DBeaver or pgAdmin for PostgreSQL

---

## 🚀 Environment Setup

### 1. Clone the Repository

```bash
git clone https://github.com/your-username/spring-boot-microservices-masterclass.git
cd spring-boot-microservices-masterclass/12-capstone-project
```

### 2. Verify Project Structure

```bash
ls -la

# You should see:
# - infrastructure/
# - services/
# - docker/
# - kubernetes/
# - config-repo/
```

### 3. Build All Services

```bash
# Build all services at once (from root directory)
mvn clean install -DskipTests

# This will:
# - Download all dependencies
# - Compile all services
# - Create JAR files
# - Take 3-5 minutes on first run
```

**Expected Output**:
```
[INFO] ------------------------------------------------------------------------
[INFO] Reactor Summary:
[INFO] ------------------------------------------------------------------------
[INFO] eureka-server ...................................... SUCCESS
[INFO] config-server ...................................... SUCCESS
[INFO] api-gateway ........................................ SUCCESS
[INFO] product-service .................................... SUCCESS
[INFO] user-service ....................................... SUCCESS
[INFO] order-service ...................................... SUCCESS
[INFO] inventory-service .................................. SUCCESS
[INFO] payment-service .................................... SUCCESS
[INFO] notification-service ............................... SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

### 4. Setup Configuration Repository

The Config Server needs a Git repository for configurations.

```bash
# Navigate to config-repo
cd config-repo

# Initialize git repository
git init
git add .
git commit -m "Initial configuration"

# Return to project root
cd ..
```

---

## 🏃 Running Options

### Option 1: Docker Compose (Recommended for First Time)

This is the easiest way to run the complete system.

#### Step 1: Start Infrastructure Services

```bash
cd docker

# Start Eureka, Config Server, API Gateway, Databases, Kafka
docker-compose -f docker-compose-infra.yml up -d

# View logs to ensure all started successfully
docker-compose -f docker-compose-infra.yml logs -f
```

**Wait for services to be ready** (about 60 seconds). Look for:
```
eureka-server    | Started EurekaServerApplication
config-server    | Started ConfigServerApplication
postgres         | database system is ready to accept connections
kafka            | [KafkaServer id=1] started
```

Press `Ctrl+C` to stop following logs.

#### Step 2: Verify Infrastructure

```bash
# Check Eureka Dashboard
curl http://localhost:8761
# Or open in browser: http://localhost:8761

# Check Config Server
curl http://localhost:8888/product-service/default

# All containers should be healthy
docker-compose -f docker-compose-infra.yml ps
```

#### Step 3: Start Business Services

```bash
# Start all microservices
docker-compose -f docker-compose-services.yml up -d

# View logs
docker-compose -f docker-compose-services.yml logs -f
```

Wait for all services to register with Eureka (about 90 seconds).

#### Step 4: Start Monitoring Stack (Optional)

```bash
# Start Prometheus, Grafana, Zipkin
docker-compose -f docker-compose-monitoring.yml up -d
```

#### All-in-One Command

```bash
# Start everything at once
docker-compose up -d

# View all logs
docker-compose logs -f

# View specific service logs
docker-compose logs -f product-service
```

#### Stopping Services

```bash
# Stop all services
docker-compose down

# Stop and remove volumes (clean slate)
docker-compose down -v
```

---

### Option 2: Kubernetes

This provides a production-like environment.

#### Prerequisites

```bash
# Ensure Kubernetes is running
kubectl cluster-info

# Create namespace
kubectl create namespace ecommerce
kubectl config set-context --current --namespace=ecommerce
```

#### Step 1: Deploy Infrastructure

```bash
cd kubernetes

# Apply ConfigMaps and Secrets
kubectl apply -f configmaps/
kubectl apply -f secrets/

# Deploy infrastructure services
kubectl apply -f deployments/eureka-server.yml
kubectl apply -f deployments/config-server.yml
kubectl apply -f deployments/api-gateway.yml

# Wait for infrastructure to be ready
kubectl wait --for=condition=ready pod -l app=eureka-server --timeout=120s
```

#### Step 2: Deploy Databases and Kafka

```bash
# Deploy PostgreSQL
kubectl apply -f deployments/postgresql.yml

# Deploy MongoDB
kubectl apply -f deployments/mongodb.yml

# Deploy Kafka
kubectl apply -f deployments/kafka.yml

# Wait for databases to be ready
kubectl wait --for=condition=ready pod -l app=postgresql --timeout=180s
```

#### Step 3: Deploy Business Services

```bash
# Deploy all microservices
kubectl apply -f deployments/product-service.yml
kubectl apply -f deployments/user-service.yml
kubectl apply -f deployments/order-service.yml
kubectl apply -f deployments/inventory-service.yml
kubectl apply -f deployments/payment-service.yml
kubectl apply -f deployments/notification-service.yml

# Check deployment status
kubectl get pods
kubectl get services
```

#### Step 4: Setup Ingress (Optional)

```bash
# Deploy ingress controller (if not already installed)
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.8.2/deploy/static/provider/cloud/deploy.yaml

# Apply ingress rules
kubectl apply -f ingress/

# Add to /etc/hosts (on your machine)
echo "127.0.0.1 ecommerce.local" | sudo tee -a /etc/hosts
```

#### Accessing Services in Kubernetes

```bash
# Port-forward API Gateway
kubectl port-forward service/api-gateway 8080:8080

# Access Eureka Dashboard
kubectl port-forward service/eureka-server 8761:8761

# Access Zipkin
kubectl port-forward service/zipkin 9411:9411
```

#### Kubernetes Cleanup

```bash
# Delete all resources
kubectl delete namespace ecommerce

# Or delete specific deployments
kubectl delete -f deployments/
```

---

### Option 3: Run Locally (Development)

This is best for developing or debugging individual services.

#### Prerequisites

You'll need to start backing services first:

```bash
cd docker

# Start only databases and Kafka
docker-compose -f docker-compose-backing.yml up -d
```

#### Terminal Layout

Open 9 terminal windows/tabs:

**Terminal 1: Eureka Server**
```bash
cd infrastructure/eureka-server
mvn spring-boot:run

# Wait for: "Started EurekaServerApplication"
# Access: http://localhost:8761
```

**Terminal 2: Config Server**
```bash
cd infrastructure/config-server
mvn spring-boot:run

# Wait for: "Started ConfigServerApplication"
```

**Terminal 3: API Gateway**
```bash
cd infrastructure/api-gateway
mvn spring-boot:run

# Wait for: "Started ApiGatewayApplication"
# Access: http://localhost:8080
```

**Terminal 4: Product Service**
```bash
cd services/product-service
mvn spring-boot:run

# Wait for service to register with Eureka
```

**Terminal 5: User Service**
```bash
cd services/user-service
mvn spring-boot:run
```

**Terminal 6: Order Service**
```bash
cd services/order-service
mvn spring-boot:run
```

**Terminal 7: Inventory Service**
```bash
cd services/inventory-service
mvn spring-boot:run
```

**Terminal 8: Payment Service**
```bash
cd services/payment-service
mvn spring-boot:run
```

**Terminal 9: Notification Service**
```bash
cd services/notification-service
mvn spring-boot:run
```

#### Tips for Local Development

1. **Start in order**: Infrastructure → Services
2. **Wait between starts**: 10-15 seconds for each service
3. **Check Eureka**: Verify registration at http://localhost:8761
4. **Use profiles**: `mvn spring-boot:run -Dspring-boot.run.profiles=dev`

---

## ✅ Verification

### 1. Check All Services Are Running

#### Docker Compose
```bash
docker-compose ps

# All services should show "Up" status
```

#### Kubernetes
```bash
kubectl get pods

# All pods should show "Running" status
# READY should show 1/1 or 2/2
```

### 2. Verify Eureka Registration

```bash
# Open Eureka Dashboard
open http://localhost:8761

# Or check via API
curl http://localhost:8761/eureka/apps | grep -o '<app>[^<]*</app>'
```

**Expected Output**: You should see all 6 services registered:
- PRODUCT-SERVICE
- USER-SERVICE
- ORDER-SERVICE
- INVENTORY-SERVICE
- PAYMENT-SERVICE
- NOTIFICATION-SERVICE

### 3. Check Service Health

```bash
# Check via API Gateway
curl http://localhost:8080/actuator/health

# Check individual services
curl http://localhost:8081/actuator/health  # Product Service
curl http://localhost:8082/actuator/health  # User Service
curl http://localhost:8083/actuator/health  # Order Service
curl http://localhost:8084/actuator/health  # Inventory Service
curl http://localhost:8085/actuator/health  # Payment Service
curl http://localhost:8086/actuator/health  # Notification Service
```

**Expected Output**:
```json
{
  "status": "UP",
  "components": {
    "db": {"status": "UP"},
    "diskSpace": {"status": "UP"},
    "ping": {"status": "UP"}
  }
}
```

### 4. Test Database Connectivity

```bash
# Connect to PostgreSQL
docker exec -it postgres psql -U postgres

# List databases
\l

# You should see:
# - product_db
# - user_db
# - order_db
# - inventory_db
# - payment_db

# Exit
\q
```

### 5. Test Kafka

```bash
# List Kafka topics
docker exec -it kafka kafka-topics.sh \
  --list \
  --bootstrap-server localhost:9092

# You should see topics like:
# - order.created
# - payment.completed
# - inventory.reserved
# - notification.sent
```

### 6. End-to-End Test

Run the complete workflow:

```bash
# 1. Register user
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "Test123!",
    "firstName": "Test",
    "lastName": "User"
  }'

# 2. Login
TOKEN=$(curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "Test123!"
  }' | jq -r '.token')

# 3. Get products
curl http://localhost:8080/api/products

# 4. Create order
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "items": [
      {"productId": 1, "quantity": 2}
    ],
    "shippingAddress": {
      "street": "123 Main St",
      "city": "San Francisco",
      "state": "CA",
      "zipCode": "94102",
      "country": "USA"
    }
  }'
```

---

## 🐛 Troubleshooting

### Services Not Starting

#### Check Docker Resources
```bash
# Docker Desktop: Settings → Resources
# Ensure you have at least:
# - Memory: 8GB
# - CPUs: 4
# - Disk: 20GB free
```

#### Check Port Conflicts
```bash
# Check if ports are already in use
# Windows
netstat -ano | findstr :8080
netstat -ano | findstr :8761

# macOS/Linux
lsof -i :8080
lsof -i :8761

# Kill process using port (replace PID)
kill -9 <PID>
```

### Services Not Registering with Eureka

**Symptoms**: Service starts but doesn't appear in Eureka dashboard

**Solutions**:

1. **Check Eureka Server is running first**
```bash
curl http://localhost:8761
```

2. **Verify network connectivity (Docker)**
```bash
# Services should be on same network
docker network ls
docker network inspect capstone-network
```

3. **Check service logs**
```bash
docker logs product-service | grep -i eureka

# Look for:
# "DiscoveryClient_PRODUCT-SERVICE - registration status: 204"
```

4. **Wait longer**: Services take 30-60 seconds to register

### Database Connection Issues

**Symptoms**: Service starts but fails with database errors

**Solutions**:

1. **Verify PostgreSQL is running**
```bash
docker ps | grep postgres

# Should show: Up (healthy)
```

2. **Check database exists**
```bash
docker exec -it postgres psql -U postgres -l
```

3. **Verify connection string**
```yaml
# In application.yml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/product_db
    username: postgres
    password: postgres
```

4. **Test connection manually**
```bash
docker exec -it postgres psql -U postgres -d product_db
```

### Kafka Connection Issues

**Symptoms**: Services can't connect to Kafka

**Solutions**:

1. **Check Kafka is running**
```bash
docker ps | grep kafka
```

2. **Verify Kafka advertised listeners**
```yaml
# In docker-compose.yml
KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
```

3. **Test Kafka connectivity**
```bash
docker exec -it kafka kafka-broker-api-versions.sh \
  --bootstrap-server localhost:9092
```

### Out of Memory Errors

**Symptoms**: Services crash with `OutOfMemoryError`

**Solutions**:

1. **Increase Docker memory**
   - Docker Desktop → Settings → Resources → Memory: 12GB

2. **Reduce service memory**
```yaml
# In docker-compose.yml
environment:
  JAVA_OPTS: "-Xmx512m -Xms256m"
```

3. **Run fewer services**
```bash
# Start only essential services
docker-compose up -d eureka-server config-server api-gateway product-service user-service
```

### Configuration Not Loading

**Symptoms**: Service uses default config instead of Config Server

**Solutions**:

1. **Check Config Server is running**
```bash
curl http://localhost:8888/product-service/default
```

2. **Verify bootstrap.yml**
```yaml
spring:
  cloud:
    config:
      uri: http://localhost:8888
      fail-fast: true
```

3. **Check config repo**
```bash
cd config-repo
git log  # Should have commits
```

### API Gateway Not Routing

**Symptoms**: 404 errors when calling through gateway

**Solutions**:

1. **Check Gateway logs**
```bash
docker logs api-gateway | grep -i route
```

2. **Verify service registration**
```bash
# Gateway should see services in Eureka
curl http://localhost:8761/eureka/apps
```

3. **Test direct service access**
```bash
# Bypass gateway to test service directly
curl http://localhost:8081/products
```

4. **Check route configuration**
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: product-service
          uri: lb://PRODUCT-SERVICE  # Must match Eureka registration
          predicates:
            - Path=/api/products/**
```

---

## 📊 Monitoring Access

Once everything is running, access these URLs:

| Service | URL | Credentials |
|---------|-----|-------------|
| **API Gateway** | http://localhost:8080 | - |
| **Eureka Dashboard** | http://localhost:8761 | - |
| **Config Server** | http://localhost:8888 | - |
| **Zipkin** | http://localhost:9411 | - |
| **Prometheus** | http://localhost:9090 | - |
| **Grafana** | http://localhost:3000 | admin/admin |
| **Kafka UI** | http://localhost:8090 | - |
| **pgAdmin** | http://localhost:5050 | admin@admin.com/admin |

---

## 🧹 Cleanup

### Docker Compose
```bash
# Stop all services
docker-compose down

# Remove volumes (databases will be reset)
docker-compose down -v

# Remove images
docker-compose down --rmi all
```

### Kubernetes
```bash
# Delete namespace (removes everything)
kubectl delete namespace ecommerce

# Or delete specific resources
kubectl delete -f kubernetes/deployments/
kubectl delete -f kubernetes/services/
```

### Local Development
```bash
# Stop all Spring Boot applications (Ctrl+C in each terminal)

# Stop backing services
docker-compose -f docker/docker-compose-backing.yml down -v
```

---

## 🚦 Next Steps

Once setup is complete:

1. ✅ Read [API-DOCUMENTATION.md](./API-DOCUMENTATION.md) for API references
2. ✅ Review [ARCHITECTURE.md](./ARCHITECTURE.md) for system design
3. ✅ Start testing workflows (user registration, orders, etc.)
4. ✅ Explore service code in each service folder
5. ✅ Run tests: `mvn test` in each service
6. ✅ Experiment with configurations
7. ✅ Try breaking things and fixing them!

---

## 💡 Tips

- **Start small**: Run infrastructure first, then add services one by one
- **Check logs frequently**: Use `docker-compose logs -f <service-name>`
- **Use Eureka dashboard**: Verify service registration
- **Monitor health endpoints**: Quick way to check if services are working
- **Be patient**: Services take time to start and register
- **Save your token**: Need it for authenticated requests

---

**Last Updated**: February 2026  
**Version**: 1.0.0
