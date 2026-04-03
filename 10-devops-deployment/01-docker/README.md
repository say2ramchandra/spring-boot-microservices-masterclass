# Docker Containerization

> **Package Spring Boot microservices into portable, self-contained Docker images**

## 📚 Overview

Docker is a platform for developing, shipping, and running applications in containers. Containers package your application with all its dependencies, ensuring it runs consistently across different environments.

---

## 🎯 Learning Objectives

- ✅ Understand Docker architecture and concepts
- ✅ Write Dockerfiles for Spring Boot applications
- ✅ Build optimized multi-stage Docker images
- ✅ Use Docker Compose for multi-container applications
- ✅ Manage Docker volumes and networks
- ✅ Push images to Docker Hub/Registry

---

## 🐳 What is Docker?

### The Problem

**Before Docker:**
```
Developer: "It works on my machine!"
Ops Team: "But it doesn't work in production!"

Issues:
- Different Java versions
- Missing dependencies
- Environment variables
- Configuration files
- Port conflicts
```

**After Docker:**
```
Developer: "Here's the Docker image"
Ops Team: "Perfect, deploying now!"

Benefits:
- Consistent environment
- All dependencies included
- Portable across platforms
- Quick deployment
```

---

## 📦 Docker Components

### 1. Docker Image

A **read-only template** with instructions for creating a container.

```
┌──────────────────────┐
│  JDK 17              │  ← Base Layer
├──────────────────────┤
│  Maven Dependencies  │  ← Dependencies Layer
├──────────────────────┤
│  Application JAR     │  ← Application Layer
├──────────────────────┤
│  Startup Script      │  ← Runtime Layer
└──────────────────────┘
        ↓
    Docker Image
```

### 2. Docker Container

A **running instance** of a Docker image.

```
Image (Template)  →  Container (Running Process)
    JAR              →  Java Application
    
One Image  →  Multiple Containers
product-service:1.0  →  container1 (port 8081)
                     →  container2 (port 8082)
                     →  container3 (port 8083)
```

### 3. Dockerfile

Instructions to build a Docker image.

```dockerfile
FROM openjdk:17-jdk-slim      # Base image
WORKDIR /app                  # Working directory
COPY target/*.jar app.jar     # Copy JAR file
EXPOSE 8080                   # Expose port
ENTRYPOINT ["java", "-jar"]   # Run command
CMD ["app.jar"]               # Default args
```

---

## 🏗️ Docker Architecture

```
┌─────────────────────────────────────────────────────┐
│                  Docker Client                      │
│            (docker build, run, push)                │
└───────────────────┬─────────────────────────────────┘
                    │ CLI Commands
                    ▼
┌─────────────────────────────────────────────────────┐
│                  Docker Daemon                      │
│                  (dockerd)                          │
│                                                     │
│  ┌──────────────────────────────────────────────┐  │
│  │         Container Lifecycle                  │  │
│  │  - Create  - Start  - Stop  - Remove        │  │
│  └──────────────────────────────────────────────┘  │
│                                                     │
│  ┌──────────────────────────────────────────────┐  │
│  │         Image Management                     │  │
│  │  - Build  - Pull  - Push  - Tag             │  │
│  └──────────────────────────────────────────────┘  │
│                                                     │
│  ┌──────────────────────────────────────────────┐  │
│  │         Network & Volume                     │  │
│  │  - Networks  - Volumes  - Port Mapping      │  │
│  └──────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────┘
                    │
        ┌───────────┴───────────┐
        ▼                       ▼
┌──────────────┐        ┌──────────────┐
│   Images     │        │  Containers  │
│ (Templates)  │        │  (Running)   │
└──────────────┘        └──────────────┘
```

---

## 📝 Dockerfile for Spring Boot

### Basic Dockerfile

```dockerfile
# Use official OpenJDK image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy JAR file
COPY target/product-service-1.0.0.jar app.jar

# Expose port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Build and Run

```bash
# Build the Docker image
docker build -t product-service:1.0 .

# Run the container
docker run -p 8080:8080 product-service:1.0

# Run with environment variables
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DATABASE_URL=jdbc:postgresql://db:5432/products \
  product-service:1.0
```

---

## 🚀 Multi-Stage Builds

Multi-stage builds create **smaller, more secure images** by separating build and runtime environments.

### Why Multi-Stage?

**Single-Stage (Bad):**
- Image size: ~500MB
- Contains Maven, build tools
- Security vulnerabilities
- Slow to transfer

**Multi-Stage (Good):**
- Image size: ~200MB
- Only runtime dependencies
- Fewer security issues
- Fast to transfer

### Multi-Stage Dockerfile

```dockerfile
# ================================
# Stage 1: Build
# ================================
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /build

# Copy dependency files first (for layer caching)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# ================================
# Stage 2: Runtime
# ================================
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Create non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy JAR from builder stage
COPY --from=builder /build/target/*.jar app.jar

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
```

### Benefits Explained

1. **Layer Caching:**
   ```dockerfile
   COPY pom.xml .                # Changes rarely → cached
   RUN mvn dependency:go-offline # Reuse cached layers
   COPY src ./src                # Changes often → rebuild
   ```

2. **Smaller Image:**
   ```
   Builder stage:  maven:3.9 (500MB) → Discarded
   Runtime stage:  jre-alpine (200MB) → Final image
   ```

3. **Security:**
   ```dockerfile
   USER spring:spring  # Non-root user
   JRE only           # No build tools
   ```

---

## 🎛️ Docker Compose

**Docker Compose** manages multi-container applications with a single YAML file.

### Use Cases
- Local development with database
- Microservices with dependencies
- Integration testing
- Complete application stack

### docker-compose.yml Example

```yaml
version: '3.8'

services:
  # PostgreSQL Database
  postgres:
    image: postgres:15-alpine
    container_name: product-db
    environment:
      POSTGRES_DB: productdb
      POSTGRES_USER: admin
      POSTGRES_PASSWORD: secret
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
    networks:
      - app-network
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U admin"]
      interval: 10s
      timeout: 5s
      retries: 5

  # Product Service
  product-service:
    build:
      context: .
      dockerfile: Dockerfile
    container_name: product-service
    environment:
      SPRING_PROFILES_ACTIVE: docker
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/productdb
      SPRING_DATASOURCE_USERNAME: admin
      SPRING_DATASOURCE_PASSWORD: secret
    ports:
      - "8080:8080"
    depends_on:
      postgres:
        condition: service_healthy
    networks:
      - app-network
    restart: unless-stopped

volumes:
  postgres_data:
    driver: local

networks:
  app-network:
    driver: bridge
```

### Docker Compose Commands

```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f product-service

# Stop all services
docker-compose down

# Rebuild and restart
docker-compose up -d --build

# Scale services
docker-compose up -d --scale product-service=3
```

---

## 🔧 Docker Commands Cheat Sheet

### Image Management

```bash
# Build image
docker build -t myapp:1.0 .

# List images
docker images

# Remove image
docker rmi myapp:1.0

# Tag image
docker tag myapp:1.0 username/myapp:1.0

# Push to Docker Hub
docker push username/myapp:1.0

# Pull from Docker Hub
docker pull username/myapp:1.0
```

### Container Management

```bash
# Run container
docker run -d -p 8080:8080 --name myapp myapp:1.0

# List running containers
docker ps

# List all containers
docker ps -a

# Stop container
docker stop myapp

# Start container
docker start myapp

# Remove container
docker rm myapp

# View logs
docker logs -f myapp

# Execute command in container
docker exec -it myapp /bin/sh

# View container stats
docker stats myapp
```

### Cleanup Commands

```bash
# Remove stopped containers
docker container prune

# Remove unused images
docker image prune

# Remove unused volumes
docker volume prune

# Remove everything unused
docker system prune -a
```

---

## 🎯 Best Practices

### 1. Use Official Base Images

❌ **Bad:**
```dockerfile
FROM ubuntu:latest
RUN apt-get update && apt-get install -y openjdk-17-jdk
```

✅ **Good:**
```dockerfile
FROM eclipse-temurin:17-jre-alpine
```

### 2. Minimize Layers

❌ **Bad:**
```dockerfile
RUN apt-get update
RUN apt-get install -y curl
RUN apt-get install -y wget
```

✅ **Good:**
```dockerfile
RUN apt-get update && apt-get install -y \
    curl \
    wget \
 && rm -rf /var/lib/apt/lists/*
```

### 3. Use .dockerignore

```
# .dockerignore
target/
.git/
.idea/
*.md
.env
node_modules/
```

### 4. Don't Run as Root

❌ **Bad:**
```dockerfile
FROM openjdk:17-jre
COPY app.jar /app.jar
CMD ["java", "-jar", "/app.jar"]
```

✅ **Good:**
```dockerfile
FROM openjdk:17-jre
RUN useradd -m -u 1000 appuser
USER appuser
COPY --chown=appuser:appuser app.jar /app.jar
CMD ["java", "-jar", "/app.jar"]
```

### 5. Use Specific Tags

❌ **Bad:**
```dockerfile
FROM openjdk:latest
```

✅ **Good:**
```dockerfile
FROM eclipse-temurin:17.0.9_9-jre-alpine
```

### 6. Leverage Build Cache

```dockerfile
# Copy dependency files first
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source later (changes more often)
COPY src ./src
RUN mvn package
```

### 7. Add Health Checks

```dockerfile
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s \
  CMD curl -f http://localhost:8080/actuator/health || exit 1
```

### 8. Use Multi-Stage Builds

```dockerfile
FROM maven:3.9 AS builder
# Build steps...

FROM openjdk:17-jre-alpine
COPY --from=builder /build/target/*.jar app.jar
```

---

## 📊 Image Optimization

### Size Comparison

```
┌────────────────────────┬──────────┐
│ Image Type             │ Size     │
├────────────────────────┼──────────┤
│ openjdk:17             │ 471 MB   │
│ openjdk:17-slim        │ 227 MB   │
│ openjdk:17-jre         │ 289 MB   │
│ eclipse-temurin:17-jre │ 199 MB   │
│ eclipse-temurin:17-jre-alpine │ 167 MB │
└────────────────────────┴──────────┘
```

### Optimization Techniques

1. **Choose Smaller Base**: Use Alpine Linux
2. **Multi-Stage Build**: Remove build tools
3. **Minimize Layers**: Combine RUN commands
4. **Remove Cache**: Clean up after installs
5. **Use .dockerignore**: Exclude unnecessary files

---

## 🐛 Common Issues & Solutions

### Issue 1: Port Already in Use

```bash
Error: Bind for 0.0.0.0:8080 failed: port already allocated

Solution:
docker ps  # Find container using port
docker stop <container-id>
# Or use different port
docker run -p 8081:8080 myapp
```

### Issue 2: Out of Memory

```bash
Error: Java heap space

Solution:
docker run -m 512m -e JAVA_OPTS="-Xmx256m" myapp
```

### Issue 3: Cannot Connect to Database

```bash
Error: Connection refused to localhost:5432

Solution:
# Use service name in docker-compose
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/db
# NOT localhost!
```

### Issue 4: Image Build Fails

```bash
Error: COPY failed: no source files

Solution:
# Build JAR first
mvn clean package
# Then build Docker image
docker build -t myapp .
```

---

## 📚 Demo Projects

### 1. Simple Dockerfile (`demo-simple-dockerfile/`)
- Basic Spring Boot containerization
- Single-stage Dockerfile
- Manual JAR build and Docker build

### 2. Multi-Stage Build (`demo-multi-stage-build/`)
- Optimized multi-stage Dockerfile
- Automated build inside Docker
- Smaller, more secure images

### 3. Docker Compose (`demo-docker-compose/`)
- Spring Boot + PostgreSQL
- Service dependencies
- Volume management
- Network configuration

---

## 🎓 Next Steps

After mastering Docker:
1. ✅ Deploy containers to Kubernetes (Module 02)
2. ✅ Build CI/CD pipelines (Module 03)
3. ✅ Implement production deployments (Module 04)

---

## 🔗 Resources

- [Docker Documentation](https://docs.docker.com/)
- [Docker Hub](https://hub.docker.com/)
- [Spring Boot with Docker](https://spring.io/guides/topicals/spring-boot-docker/)
- [Docker Best Practices](https://docs.docker.com/develop/dev-best-practices/)

---

_"Containers are a way to package software with everything it needs to run." - Docker_
