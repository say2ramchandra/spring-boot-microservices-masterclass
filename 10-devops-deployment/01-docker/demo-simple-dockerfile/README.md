# Simple Dockerfile Demo

> **Basic Docker containerization for Spring Boot applications**

## 📚 Overview

This demo shows the fundamental approach to containerizing a Spring Boot application using a simple, single-stage Dockerfile.

---

## 🎯 What You'll Learn

- ✅ Write a basic Dockerfile
- ✅ Build Docker images
- ✅ Run containers
- ✅ Understand Docker layers
- ✅ Use environment variables
- ✅ Implement health checks

---

## 🏗️ Project Structure

```
demo-simple-dockerfile/
├── src/
│   └── main/
│       ├── java/com/masterclass/docker/
│       │   ├── SimpleDockerApplication.java
│       │   ├── controller/
│       │   │   └── HelloController.java
│       │   └── dto/
│       │       ├── HelloResponse.java
│       │       └── AppInfoResponse.java
│       └── resources/
│           └── application.yml
├── Dockerfile                   # Basic Dockerfile
├── Dockerfile.optimized         # Improved with security
├── .dockerignore               # Exclude files from image
├── pom.xml
└── README.md
```

---

## 🚀 Quick Start

### Step 1: Build the Application

```bash
# Navigate to demo directory
cd 10-devops-deployment/01-docker/demo-simple-dockerfile

# Build the JAR file
mvn clean package

# Verify JAR created
ls target/demo-simple-dockerfile.jar
```

### Step 2: Build Docker Image

```bash
# Build image with tag
docker build -t simple-docker-demo:1.0 .

# List images to verify
docker images | grep simple-docker-demo
```

### Step 3: Run Container

```bash
# Run container (foreground)
docker run -p 8080:8080 simple-docker-demo:1.0

# Or run in background (detached)
docker run -d -p 8080:8080 --name simple-demo simple-docker-demo:1.0
```

### Step 4: Test the Application

```bash
# Health check
curl http://localhost:8080/actuator/health

# Hello endpoint
curl http://localhost:8080/api/hello?name=Docker

# App info
curl http://localhost:8080/api/info

# Echo test
curl -X POST http://localhost:8080/api/echo \
  -H "Content-Type: application/json" \
  -d '{"message": "Hello from Docker!"}'
```

---

## 📝 Dockerfile Explained

### Basic Dockerfile

```dockerfile
# Base image - Java 17 runtime on Alpine Linux
FROM eclipse-temurin:17-jre-alpine

# Working directory inside container
WORKDIR /app

# Copy JAR from host to container
COPY target/demo-simple-dockerfile.jar app.jar

# Expose port 8080
EXPOSE 8080

# Command to run when container starts
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
```

### Image Layers

Each instruction creates a layer:

```
┌─────────────────────────────┐
│ ENTRYPOINT (Run command)    │  ← Layer 4
├─────────────────────────────┤
│ COPY app.jar                │  ← Layer 3
├─────────────────────────────┤
│ WORKDIR /app                │  ← Layer 2
├─────────────────────────────┤
│ FROM eclipse-temurin:17     │  ← Layer 1 (Base)
└─────────────────────────────┘
```

---

## 🔧 Docker Commands

### Build Commands

```bash
# Basic build
docker build -t simple-docker-demo:1.0 .

# Build with different Dockerfile
docker build -f Dockerfile.optimized -t simple-docker-demo:optimized .

# Build with no cache
docker build --no-cache -t simple-docker-demo:1.0 .

# Build with build arguments
docker build --build-arg JAR_FILE=target/*.jar -t simple-docker-demo:1.0 .
```

### Run Commands

```bash
# Run with port mapping
docker run -p 8080:8080 simple-docker-demo:1.0

# Run with custom name
docker run -d --name my-app -p 8080:8080 simple-docker-demo:1.0

# Run with environment variables
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e JAVA_OPTS="-Xmx256m" \
  simple-docker-demo:1.0

# Run with resource limits
docker run -p 8080:8080 \
  --memory="512m" \
  --cpus="1.0" \
  simple-docker-demo:1.0
```

### Management Commands

```bash
# List running containers
docker ps

# List all containers
docker ps -a

# View logs
docker logs simple-demo
docker logs -f simple-demo  # Follow logs

# Execute command in running container
docker exec -it simple-demo sh

# Inspect container
docker inspect simple-demo

# View container stats
docker stats simple-demo

# Stop container
docker stop simple-demo

# Start stopped container
docker start simple-demo

# Remove container
docker rm simple-demo

# Remove image
docker rmi simple-docker-demo:1.0
```

---

## 🎛️ Environment Variables

### Runtime Configuration

```bash
# Set Spring profile
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=production \
  simple-docker-demo:1.0

# Set JVM options
docker run -p 8080:8080 \
  -e JAVA_OPTS="-Xmx512m -Xms256m" \
  simple-docker-demo:1.0

# Multiple environment variables
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e JAVA_OPTS="-Xmx256m" \
  -e SERVER_PORT=8080 \
  simple-docker-demo:1.0
```

### Using .env File

```bash
# Create .env file
cat > .env << EOF
SPRING_PROFILES_ACTIVE=production
JAVA_OPTS=-Xmx512m
EOF

# Run with env file
docker run -p 8080:8080 --env-file .env simple-docker-demo:1.0
```

---

## 🔍 Inspecting the Container

### View Container Details

```bash
# Get container IP
docker inspect -f '{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}' simple-demo

# Get all environment variables
docker inspect -f '{{range .Config.Env}}{{println .}}{{end}}' simple-demo

# View port mappings
docker port simple-demo

# View resource usage
docker stats simple-demo --no-stream
```

### Access Container Shell

```bash
# Enter running container
docker exec -it simple-demo sh

# Inside container:
# - View files:  ls -la
# - Check Java:  java -version
# - Check JAR:   ls -lh /app/app.jar
# - Exit:        exit
```

---

## 🎯 Best Practices Applied

### 1. Use .dockerignore

```
# Exclude unnecessary files
target/
.git/
.idea/
*.md
```

### 2. Specific Base Image Tag

```dockerfile
# Good: Specific version
FROM eclipse-temurin:17-jre-alpine

# Bad: Latest tag (unpredictable)
FROM openjdk:latest
```

### 3. Non-Root User (Dockerfile.optimized)

```dockerfile
# Create and use non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
```

### 4. Health Check

```dockerfile
HEALTHCHECK --interval=30s --timeout=3s \
  CMD curl -f http://localhost:8080/actuator/health || exit 1
```

### 5. JVM Optimization

```dockerfile
# Container-aware JVM settings
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-jar", "app.jar"]
```

---

## 📊 Image Size Comparison

```bash
# Check image size
docker images simple-docker-demo

# Expected size: ~200MB
# - Base JRE: ~175MB
# - Spring Boot JAR: ~20-30MB
```

### Size Optimization Tips

1. Use Alpine base images (smaller)
2. Remove unnecessary dependencies
3. Use multi-stage builds (see demo-multi-stage-build)
4. Clean up in same layer

---

## 🐛 Troubleshooting

### Container Won't Start

```bash
# View startup logs
docker logs simple-demo

# Common issues:
# - Port already in use: Change port mapping
# - JAR not found: Check COPY path
# - Permission denied: Check file permissions
```

### Application Not Accessible

```bash
# Check if container is running
docker ps

# Check port mapping
docker port simple-demo

# Check from inside container
docker exec simple-demo curl http://localhost:8080/actuator/health

# Check logs
docker logs -f simple-demo
```

### Build Failures

```bash
# Common issues:
# 1. JAR not found
# Solution: Run 'mvn clean package' first

# 2. Base image pull fails
# Solution: Check Docker daemon and internet connection

# 3. Copy fails
# Solution: Check file paths and .dockerignore
```

---

## 🧪 Testing Scenarios

### 1. Verify Container Info

```bash
curl http://localhost:8080/api/info
# Should show container hostname and Java version
```

### 2. Test with Multiple Containers

```bash
# Run multiple instances on different ports
docker run -d -p 8081:8080 --name demo1 simple-docker-demo:1.0
docker run -d -p 8082:8080 --name demo2 simple-docker-demo:1.0
docker run -d -p 8083:8080 --name demo3 simple-docker-demo:1.0

# Test each
curl http://localhost:8081/api/hello
curl http://localhost:8082/api/hello
curl http://localhost:8083/api/hello

# Each shows different container hostname!
```

### 3. Test Resource Limits

```bash
# Run with memory limit
docker run -d -p 8080:8080 --memory="256m" --name demo-limited simple-docker-demo:1.0

# Check actual usage
docker stats demo-limited --no-stream
```

---

## 🔄 Cleanup

```bash
# Stop and remove container
docker stop simple-demo
docker rm simple-demo

# Remove all stopped containers
docker container prune

# Remove image
docker rmi simple-docker-demo:1.0

# Remove all unused images
docker image prune -a
```

---

## 📚 Key Takeaways

1. ✅ **Dockerfile** = Recipe for building images
2. ✅ **Image** = Template (like a class)
3. ✅ **Container** = Running instance (like an object)
4. ✅ **Layer** = Cacheable instruction result
5. ✅ **Tag** = Version identifier for images
6. ✅ **.dockerignore** = Exclude files from build context

---

## 🎓 Next Steps

- ✅ Try **Dockerfile.optimized** with security improvements
- ✅ Learn multi-stage builds (demo-multi-stage-build)
- ✅ Use Docker Compose for multi-container apps (demo-docker-compose)
- ✅ Push images to Docker Hub
- ✅ Deploy to Kubernetes

---

## 🔗 References

- [Spring Boot with Docker](https://spring.io/guides/topicals/spring-boot-docker/)
- [Dockerfile Reference](https://docs.docker.com/engine/reference/builder/)
- [Eclipse Temurin Images](https://hub.docker.com/_/eclipse-temurin)

---

**Congratulations!** You've containerized your first Spring Boot application! 🎉
