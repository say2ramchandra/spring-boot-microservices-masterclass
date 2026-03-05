# Docker Compose Demo

> **Orchestrate multi-container applications with Spring Boot and PostgreSQL**

## 📚 Overview

Docker Compose simplifies running multiple containers as a single application. This demo shows how to run a Spring Boot application with PostgreSQL database, complete with volumes, networks, health checks, and dependencies.

---

## 🎯 Learning Objectives

- ✅ Understand Docker Compose fundamentals
- ✅ Define multi-container applications in YAML
- ✅ Configure service dependencies
- ✅ Manage volumes and networks
- ✅ Implement health checks
- ✅ Use environment variables
- ✅ Scale services

---

## 🏗️ Application Architecture

```
┌─────────────────────────────────────────────────────┐
│                  Docker Compose                      │
│                                                      │
│  ┌────────────────┐         ┌──────────────────┐   │
│  │  Spring Boot   │────────>│   PostgreSQL     │   │
│  │  Application   │  JDBC   │    Database      │   │
│  │  (Port 8080)   │         │   (Port 5432)    │   │
│  └────────────────┘         └──────────────────┘   │
│         │                            │              │
│         │                            │              │
│  ┌────────────────┐         ┌──────────────────┐   │
│  │   app-network  │<────────│  postgres_data   │   │
│  │   (Bridge)     │         │     (Volume)     │   │
│  └────────────────┘         └──────────────────┘   │
└─────────────────────────────────────────────────────┘
```

---

## 📁 Project Structure

```
demo-docker-compose/
├── src/
│   └── main/
│       ├── java/com/masterclass/compose/
│       │   ├── DockerComposeApplication.java
│       │   ├── controller/
│       │   │   └── ProductController.java
│       │   ├── entity/
│       │   │   └── Product.java
│       │   ├── repository/
│       │   │   └── ProductRepository.java
│       │   ├── service/
│       │   │   └── ProductService.java
│       │   └── config/
│       │       └── DataInitializer.java
│       └── resources/
│           ├── application.yml
│           └── application-docker.yml
├── init-db/
│   └── 01-init.sql              # Database initialization
├── docker-compose.yml            # Service orchestration
├── Dockerfile                    # Application image
├── .dockerignore
├── pom.xml
└── README.md
```

---

## 🚀 Quick Start

### Step 1: Build and Start All Services

```bash
# Navigate to demo directory
cd 10-devops-deployment/01-docker/demo-docker-compose

# Start all services (builds images if needed)
docker-compose up -d

# View logs
docker-compose logs -f
```

### Step 2: Verify Services are Running

```bash
# Check running containers
docker-compose ps

# Should see:
# - product-postgres  (PostgreSQL)
# - product-service   (Spring Boot)
```

### Step 3: Test the Application

```bash
# Health check
curl http://localhost:8080/actuator/health

# Get all products
curl http://localhost:8080/api/products

# Get product by ID
curl http://localhost:8080/api/products/1

# Create new product
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Docker Course",
    "description": "Complete Docker for beginners",
    "price": 99.99,
    "category": "Courses",
    "stock": 50,
    "available": true
  }'

# Search products
curl "http://localhost:8080/api/products/search?keyword=laptop"

# Get by category
curl http://localhost:8080/api/products/category/Electronics
```

---

## 📝 docker-compose.yml Explained

### Service: PostgreSQL Database

```yaml
postgres:
  image: postgres:15-alpine
  container_name: product-postgres
  environment:
    POSTGRES_DB: productdb
    POSTGRES_USER: admin
    POSTGRES_PASSWORD: secret123
  ports:
    - "5432:5432"
  volumes:
    - postgres_data:/var/lib/postgresql/data
    - ./init-db:/docker-entrypoint-initdb.d
  networks:
    - app-network
  healthcheck:
    test: ["CMD-SHELL", "pg_isready -U admin -d productdb"]
    interval: 10s
    timeout: 5s
    retries: 5
```

**Key Points:**
- **image**: Uses official PostgreSQL 15 Alpine image
- **environment**: Database credentials and name
- **ports**: Exposes 5432 for local access
- **volumes**: Persists data + init scripts
- **healthcheck**: Verifies database is ready

### Service: Spring Boot Application

```yaml
app:
  build:
    context: .
    dockerfile: Dockerfile
  container_name: product-service
  environment:
    SPRING_PROFILES_ACTIVE: docker
    SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/productdb
    SPRING_DATASOURCE_USERNAME: admin
    SPRING_DATASOURCE_PASSWORD: secret123
  ports:
    - "8080:8080"
  depends_on:
    postgres:
      condition: service_healthy
  networks:
    - app-network
```

**Key Points:**
- **build**: Builds image from Dockerfile
- **environment**: Overrides Spring Boot config
- **depends_on**: Waits for PostgreSQL health check
- **postgres**: Uses service name (not localhost!)

### Volumes (Persistent Storage)

```yaml
volumes:
  postgres_data:
    driver: local
```

**Benefits:**
- Data persists across container restarts
- Can be backed up
- Shared between containers

### Networks

```yaml
networks:
  app-network:
    driver: bridge
```

**Benefits:**
- Isolated network for services
- Services communicate by name
- Better security

---

## 🎛️ Docker Compose Commands

### Basic Operations

```bash
# Start services
docker-compose up -d

# View logs
docker-compose logs -f

# View logs for specific service
docker-compose logs -f app

# Stop services
docker-compose stop

# Start stopped services
docker-compose start

# Stop and remove containers
docker-compose down

# Remove containers + volumes
docker-compose down -v

# Remove containers + images
docker-compose down --rmi all
```

### Build and Rebuild

```bash
# Build images
docker-compose build

# Build without cache
docker-compose build --no-cache

# Rebuild and restart
docker-compose up -d --build

# Pull latest base images
docker-compose pull
```

### Scaling Services

```bash
# Scale application to 3 instances
docker-compose up -d --scale app=3

# Note: Remove port mapping or use different ports
```

### Inspection

```bash
# List services
docker-compose ps

# View service configuration
docker-compose config

# Execute command in service
docker-compose exec app sh

# Execute command in database
docker-compose exec postgres psql -U admin -d productdb
```

---

## 🔧 Environment Configuration

### Using .env File

Create `.env` file:

```bash
# Database
POSTGRES_DB=productdb
POSTGRES_USER=admin
POSTGRES_PASSWORD=secret123

# Application
SPRING_PROFILES_ACTIVE=docker
JAVA_OPTS=-Xmx512m
```

Update docker-compose.yml:

```yaml
environment:
  POSTGRES_DB: ${POSTGRES_DB}
  POSTGRES_USER: ${POSTGRES_USER}
  POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}
```

### Override Configuration

Create `docker-compose.override.yml` for local development:

```yaml
version: '3.8'

services:
  app:
    environment:
      SPRING_JPA_SHOW_SQL: "true"
      LOGGING_LEVEL_COM_MASTERCLASS: DEBUG
```

---

## 🗄️ Database Management

### Connect to PostgreSQL

```bash
# Using psql inside container
docker-compose exec postgres psql -U admin -d productdb

# From host (requires psql installed)
psql -h localhost -p 5432 -U admin -d productdb
```

### Common SQL Commands

```sql
-- List tables
\dt

-- Describe products table
\d products

-- View all products
SELECT * FROM products;

-- Count products
SELECT COUNT(*) FROM products;

-- Products by category
SELECT category, COUNT(*) FROM products GROUP BY category;

-- Exit psql
\q
```

### Backup and Restore

```bash
# Backup database
docker-compose exec -T postgres pg_dump -U admin productdb > backup.sql

# Restore database
docker-compose exec -T postgres psql -U admin productdb < backup.sql
```

---

## 🎭 Optional: pgAdmin (Database UI)

### Start with pgAdmin

```bash
# Start with pgAdmin profile
docker-compose --profile with-admin up -d

# Access pgAdmin
# URL: http://localhost:5050
# Email: admin@example.com
# Password: admin123
```

### Configure Connection in pgAdmin

1. Right-click "Servers" → "Create" → "Server"
2. General Tab:
   - Name: Product Database
3. Connection Tab:
   - Host: postgres (service name)
   - Port: 5432
   - Database: productdb
   - Username: admin
   - Password: secret123

---

## 🔍 Health Checks

### PostgreSQL Health Check

```yaml
healthcheck:
  test: ["CMD-SHELL", "pg_isready -U admin -d productdb"]
  interval: 10s
  timeout: 5s
  retries: 5
```

### Application Health Check

```yaml
healthcheck:
  test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
  interval: 30s
  timeout: 10s
  retries: 3
  start_period: 60s
```

### Check Health Status

```bash
# View health status
docker-compose ps

# Detailed health check output
docker inspect product-service | grep -A 10 Health

# Test endpoints
curl http://localhost:8080/actuator/health
```

---

## 🎯 Service Dependencies

### depends_on with Health Check

```yaml
depends_on:
  postgres:
    condition: service_healthy
```

**Ensures:**
- PostgreSQL starts first
- Application waits for database health check
- Prevents connection errors

### Startup Order

```
1. Start postgres container
2. Run health check (pg_isready)
3. Health check passes (healthy)
4. Start app container
5. App connects to database
```

---

## 📊 Volume Management

### List Volumes

```bash
# List volumes
docker volume ls

# Inspect volume
docker volume inspect demo-docker-compose_postgres_data
```

### Backup Volume

```bash
# Backup PostgreSQL data
docker run --rm \
  -v demo-docker-compose_postgres_data:/data \
  -v $(pwd):/backup \
  alpine tar czf /backup/postgres-backup.tar.gz -C /data .
```

### Restore Volume

```bash
# Restore PostgreSQL data
docker run --rm \
  -v demo-docker-compose_postgres_data:/data \
  -v $(pwd):/backup \
  alpine sh -c "cd /data && tar xzf /backup/postgres-backup.tar.gz"
```

---

## 🐛 Troubleshooting

### Issue 1: Container Can't Connect to Database

```bash
# Check if database is healthy
docker-compose ps

# Check network
docker network inspect demo-docker-compose_app-network

# Verify environment variables
docker-compose exec app env | grep DATABASE

# Common mistake: Using 'localhost' instead of 'postgres'
# ✗ jdbc:postgresql://localhost:5432/productdb
# ✓ jdbc:postgresql://postgres:5432/productdb
```

### Issue 2: Port Already in Use

```bash
# Error: Bind for 0.0.0.0:8080 failed

# Solution 1: Stop conflicting service
docker ps
docker stop <container-id>

# Solution 2: Change port in docker-compose.yml
ports:
  - "8081:8080"  # External:Internal
```

### Issue 3: Database Connection Refused

```bash
# Wait for PostgreSQL to be ready
docker-compose logs postgres

# Manually test connection
docker-compose exec postgres pg_isready -U admin -d productdb
```

### Issue 4: Volume Data Loss

```bash
# Never use: docker-compose down -v
# This deletes volumes!

# Safe stop:
docker-compose stop

# Safe remove (keeps volumes):
docker-compose down
```

---

## 🧪 Testing Scenarios

### 1. Test Data Persistence

```bash
# Create a product
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Test Product","price":99.99,"category":"Test","stock":10,"available":true}'

# Stop containers
docker-compose stop

# Start containers
docker-compose start

# Verify product still exists
curl http://localhost:8080/api/products
# Data persisted! ✓
```

### 2. Test Service Restart

```bash
# Restart only app service
docker-compose restart app

# Application reconnects to database automatically
```

### 3. Test Database Failure Recovery

```bash
# Stop database
docker-compose stop postgres

# Try to access API (should fail gracefully)
curl http://localhost:8080/api/products

# Restart database
docker-compose start postgres

# Application reconnects automatically
curl http://localhost:8080/api/products
```

---

## 📈 Performance Tuning

### JVM Options

```yaml
environment:
  JAVA_OPTS: >-
    -XX:+UseContainerSupport
    -XX:MaxRAMPercentage=75.0
    -XX:InitialRAMPercentage=50.0
    -Xlog:gc*:file=/tmp/gc.log
```

### PostgreSQL Tuning

```yaml
environment:
  POSTGRES_INITDB_ARGS: "--encoding=UTF8"
command: >
  postgres
  -c max_connections=200
  -c shared_buffers=256MB
  -c effective_cache_size=1GB
```

### Resource Limits

```yaml
deploy:
  resources:
    limits:
      cpus: '1.0'
      memory: 512M
    reservations:
      cpus: '0.5'
      memory: 256M
```

---

## 🔄 Different Environments

### Development (docker-compose.override.yml)

```yaml
services:
  app:
    environment:
      SPRING_PROFILES_ACTIVE: dev
      SPRING_JPA_SHOW_SQL: "true"
    volumes:
      - ./src:/app/src  # Hot reload
```

### Production (docker-compose.prod.yml)

```yaml
services:
  app:
    environment:
      SPRING_PROFILES_ACTIVE: prod
      SPRING_JPA_SHOW_SQL: "false"
    restart: always
    deploy:
      replicas: 3
```

Run with:

```bash
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d
```

---

## 🎓 Key Takeaways

1. ✅ **Docker Compose orchestrates multiple containers**
2. ✅ **Services communicate by name (not localhost)**
3. ✅ **Volumes persist data across restarts**
4. ✅ **Health checks ensure proper startup order**
5. ✅ **Networks isolate container communication**
6. ✅ **Environment variables configure services**
7. ✅ **One command (`up`) starts entire stack**

---

## 🔄 Cleanup

```bash
# Stop and remove containers
docker-compose down

# Remove containers + volumes (DELETES DATA!)
docker-compose down -v

# Remove containers + volumes + images
docker-compose down -v --rmi all

# Clean up everything
docker-compose down -v --rmi all --remove-orphans
```

---

## 🎯 Next Steps

- ✅ Deploy to **Kubernetes** for production orchestration
- ✅ Add more services (Redis, RabbitMQ, etc.)
- ✅ Implement **CI/CD** pipelines
- ✅ Use **secrets** for sensitive data
- ✅ Add **monitoring** (Prometheus, Grafana)

---

## 🔗 References

- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [Compose File Reference](https://docs.docker.com/compose/compose-file/)
- [Spring Boot with Docker](https://spring.io/guides/topicals/spring-boot-docker/)

---

**Docker Compose simplifies multi-container applications!** 🎉
