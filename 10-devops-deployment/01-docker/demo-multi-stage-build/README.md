# Multi-Stage Docker Build Demo

> **Build optimized Docker images with multi-stage builds - 60% size reduction!**

## 📚 Overview

This demo shows how to use **multi-stage builds** to create smaller, more secure Docker images. By separating the build environment from the runtime environment, we achieve significant improvements in image size, security, and deployment speed.

---

## 🎯 Learning Objectives

- ✅ Understand multi-stage Docker builds
- ✅ Optimize Docker image sizes
- ✅ Separate build and runtime dependencies
- ✅ Leverage Docker layer caching
- ✅ Build images without pre-building JARs

---

## 📊 Single-Stage vs Multi-Stage

### Single-Stage Build

```dockerfile
FROM maven:3.9-jdk-17
WORKDIR /app
COPY . .
RUN mvn package
CMD ["java", "-jar", "target/app.jar"]
```

**Problems:**
- ❌ Image size: ~500MB
- ❌ Contains Maven (not needed at runtime)
- ❌ Contains all source code
- ❌ More security vulnerabilities
- ❌ Slower to transfer

### Multi-Stage Build

```dockerfile
# Stage 1: Build
FROM maven:3.9-jdk-17 AS builder
WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /build/target/*.jar app.jar
CMD ["java", "-jar", "app.jar"]
```

**Benefits:**
- ✅ Image size: ~200MB (60% reduction!)
- ✅ No Maven in final image
- ✅ No source code in final image
- ✅ Fewer vulnerabilities
- ✅ Faster deployment

---

## 📦 Image Size Comparison

```
┌────────────────────────────────┬──────────┬───────────┐
│ Image Type                     │ Size     │ Reduction │
├────────────────────────────────┼──────────┼───────────┤
│ Single-stage (Maven + JDK)    │ 500 MB   │ -         │
│ Single-stage (JDK only)        │ 300 MB   │ 40%       │
│ Multi-stage (JRE)              │ 250 MB   │ 50%       │
│ Multi-stage (JRE Alpine)       │ 200 MB   │ 60%       │
│ Multi-stage with layers        │ 180 MB   │ 64%       │
└────────────────────────────────┴──────────┴───────────┘
```

---

## 🚀 Quick Start

### Method 1: Build with Docker (No Maven Needed!)

```bash
# Navigate to demo directory
cd 10-devops-deployment/01-docker/demo-multi-stage-build

# Build image directly (Maven runs inside Docker)
docker build -t multi-stage-demo:1.0 .

# Run container
docker run -d -p 8080:8080 --name multi-stage multi-stage-demo:1.0

# View logs
docker logs -f multi-stage
```

### Method 2: Build with Layered Dockerfile

```bash
# Build with advanced layering
docker build -f Dockerfile.layered -t multi-stage-demo:layered .

# Run
docker run -d -p 8080:8080 --name multi-stage-layered multi-stage-demo:layered
```

### Test the Application

```bash
# Health check
curl http://localhost:8080/actuator/health

# Get all products
curl http://localhost:8080/api/product

# Get product by ID
curl http://localhost:8080/api/product/1

# Create new product
curl -X POST http://localhost:8080/api/product \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Docker Book",
    "description": "Learn Docker multi-stage builds",
    "price": 49.99,
    "category": "Books",
    "stock": 100,
    "available": true
  }'

# Get container information
curl http://localhost:8080/api/product/container/info
```

---

## 📝 Understanding the Dockerfile

### Stage 1: Builder

```dockerfile
# ============================================
# Stage 1: Build Stage
# ============================================
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /build

# Copy pom.xml first (for layer caching)
COPY pom.xml .

# Download dependencies (cached layer)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build application
RUN mvn clean package -DskipTests -B
```

**Key Points:**
1. **AS builder** - Names this stage for reference
2. **pom.xml first** - Dependencies cached unless pom.xml changes
3. **Source code last** - Changes don't invalidate dependency cache
4. **Maven included** - But only in this stage

### Stage 2: Runtime

```dockerfile
# ============================================
# Stage 2: Runtime Stage
# ============================================
FROM eclipse-temurin:17-jre-alpine

RUN apk add --no-cache curl

# Create non-root user
RUN addgroup -S spring && adduser -S spring -G spring

WORKDIR /app

# Copy JAR from builder stage
COPY --from=builder --chown=spring:spring /build/target/*.jar app.jar

USER spring:spring

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=60s \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-jar", "app.jar"]
```

**Key Points:**
1. **--from=builder** - Copy from previous stage
2. **JRE only** - No JDK or Maven
3. **Alpine Linux** - Minimal base image
4. **Non-root user** - Security best practice
5. **Health check** - Container health monitoring

---

## 🔄 Build Process Flow

```
┌─────────────────────────────────────────────┐
│ Stage 1: Builder (maven:3.9-jdk-17)        │
│                                             │
│  ┌────────────────────────────────┐        │
│  │ 1. Copy pom.xml                │        │
│  │ 2. Download dependencies       │        │
│  │ 3. Copy source code            │        │
│  │ 4. Build JAR                   │        │
│  └────────────────┬───────────────┘        │
│                   │                         │
│                   │ /build/target/app.jar  │
└───────────────────┼─────────────────────────┘
                    │
                    ▼
┌─────────────────────────────────────────────┐
│ Stage 2: Runtime (temurin:17-jre-alpine)   │
│                                             │
│  ┌────────────────────────────────┐        │
│  │ 1. COPY --from=builder         │        │
│  │ 2. Only get app.jar            │        │
│  │ 3. Set up user & health check  │        │
│  │ 4. Configure entrypoint        │        │
│  └────────────────────────────────┘        │
│                                             │
│  Final image: ~200MB                       │
└─────────────────────────────────────────────┘

Builder stage DISCARDED (not in final image)
```

---

## 🎯 Layer Caching Optimization

### Why Copy pom.xml First?

```dockerfile
# GOOD: Dependencies cached
COPY pom.xml .
RUN mvn dependency:go-offline  # ← Cached unless pom.xml changes
COPY src ./src                 # ← Source changes don't affect cache
RUN mvn package

# BAD: Cache invalidated on every source change
COPY . .                       # ← Everything copied at once
RUN mvn package                # ← Redownloads all dependencies
```

### Cache Behavior

```
Build 1:
  COPY pom.xml        [CACHE MISS] 2s
  RUN mvn dependency  [CACHE MISS] 60s
  COPY src            [CACHE MISS] 1s
  RUN mvn package     [CACHE MISS] 30s
  Total: 93s

Build 2 (source change only):
  COPY pom.xml        [CACHE HIT]  0s
  RUN mvn dependency  [CACHE HIT]  0s
  COPY src            [CACHE MISS] 1s
  RUN mvn package     [CACHE MISS] 30s
  Total: 31s (66% faster!)

Build 3 (pom.xml change):
  COPY pom.xml        [CACHE MISS] 2s
  RUN mvn dependency  [CACHE MISS] 60s  ← Invalidated
  COPY src            [CACHE MISS] 1s
  RUN mvn package     [CACHE MISS] 30s
  Total: 93s
```

---

## 🔒 Security Improvements

### Running as Non-Root

```dockerfile
# Create user
RUN addgroup -S spring && adduser -S spring -G spring

# Set ownership when copying
COPY --chown=spring:spring /build/target/*.jar app.jar

# Switch user
USER spring:spring
```

**Why?**
- Container compromises have limited impact
- Cannot modify system files
- Industry best practice

### Minimal Attack Surface

```
Builder image contains:
  - Maven
  - JDK
  - Build tools
  - Source code
  ❌ NOT in final image!

Runtime image contains:
  - JRE only
  - Application JAR
  - Minimal OS (Alpine)
  ✅ Much smaller attack surface
```

---

## 📊 Inspect Image Layers

```bash
# View image history
docker history multi-stage-demo:1.0

# Compare sizes
docker images | grep multi-stage-demo

# Analyze image
docker inspect multi-stage-demo:1.0

# Use dive tool for detailed analysis
dive multi-stage-demo:1.0
```

---

## 🎛️ Advanced: Layered JAR Optimization

Spring Boot supports extracting JAR into layers for even better caching.

### Dockerfile.layered

```dockerfile
# In builder stage:
RUN java -Djarmode=layertools -jar target/*.jar extract

# In runtime stage:
COPY --from=builder /build/dependencies/ ./
COPY --from=builder /build/spring-boot-loader/ ./
COPY --from=builder /build/snapshot-dependencies/ ./
COPY --from=builder /build/application/ ./
```

### Layer Structure

```
┌─────────────────────────────┐
│ Application code (changes)  │  ← Top layer (changes frequently)
├─────────────────────────────┤
│ Snapshot dependencies       │
├─────────────────────────────┤
│ Spring Boot loader          │
├─────────────────────────────┤
│ Dependencies (stable)       │  ← Bottom layer (rarely changes)
└─────────────────────────────┘
```

**Benefits:**
- Only changed layers are pushed/pulled
- Faster deployments
- Better cache utilization

---

## 🧪 Testing Scenarios

### 1. Verify Image Size

```bash
# Check image size
docker images multi-stage-demo:1.0

# Should be ~200MB or less
```

### 2. Test Container Health

```bash
# View health status
docker ps

# Test health endpoint
curl http://localhost:8080/actuator/health
```

### 3. Verify No Build Tools

```bash
# Enter container
docker exec -it multi-stage sh

# Try to run Maven (should fail)
mvn --version  # Command not found ✓

# Try to find source code (should fail)
find / -name "*.java" 2>/dev/null  # No results ✓
```

### 4. Test Memory Usage

```bash
# Check container stats
docker stats multi-stage --no-stream

# Test with memory limit
docker run -d -p 8080:8080 --memory="256m" multi-stage-demo:1.0
```

---

## 🐛 Troubleshooting

### Build Fails: Dependencies Download

```bash
# Issue: Network timeout during dependency download
# Solution: Increase build timeout
docker build --network=host -t multi-stage-demo:1.0 .
```

### Build Fails: No JAR Found

```bash
# Issue: COPY --from=builder finds no JAR
# Solution: Verify JAR is built
docker build --target builder -t test-builder .
docker run --rm test-builder ls -la /build/target/
```

### Container Starts but App Fails

```bash
# View full logs
docker logs multi-stage

# Check if running as correct user
docker exec multi-stage whoami  # Should be 'spring'

# Check memory
docker stats multi-stage
```

---

## 🔄 Rebuild Strategies

### Full Rebuild

```bash
# Ignore all cache
docker build --no-cache -t multi-stage-demo:1.0 .
```

### Rebuild from Specific Stage

```bash
# Build only builder stage (for testing)
docker build --target builder -t multi-stage-builder .

# Verify builder output
docker run --rm multi-stage-builder ls -la /build/target/
```

### Rebuild with Different Source

```bash
# Make code changes
# Rebuild (dependencies cached!)
docker build -t multi-stage-demo:1.1 .
```

---

## 📈 Performance Metrics

### Build Times

```
First build (no cache):     ~90 seconds
Rebuild (source change):    ~30 seconds
Rebuild (dependency add):   ~90 seconds
Rebuild (config change):    ~30 seconds
```

### Deployment Times

```
Single-stage image (500MB):   ~60 seconds
Multi-stage image (200MB):    ~25 seconds

Improvement: 58% faster! 🚀
```

---

## 🎓 Key Takeaways

1. ✅ **Multi-stage builds separate build and runtime**
2. ✅ **Final image 60% smaller**
3. ✅ **No build tools in production image**
4. ✅ **Better security (minimal attack surface)**
5. ✅ **Layer caching optimizes rebuild times**
6. ✅ **No need to pre-build JAR locally**
7. ✅ **Consistent builds across environments**

---

## 🔄 Cleanup

```bash
# Stop and remove container
docker stop multi-stage
docker rm multi-stage

# Remove image
docker rmi multi-stage-demo:1.0

# Clean up build cache
docker builder prune
```

---

## 🎯 Next Steps

- ✅ Try **Docker Compose** for multi-container apps
- ✅ Push to **Docker Hub** or private registry
- ✅ Deploy to **Kubernetes**
- ✅ Implement **CI/CD pipelines**

---

## 🔗 References

- [Multi-stage builds documentation](https://docs.docker.com/build/building/multi-stage/)
- [Spring Boot Docker layers](https://spring.io/guides/topicals/spring-boot-docker/)
- [Docker best practices](https://docs.docker.com/develop/dev-best-practices/)

---

**Multi-stage builds are the standard for production Docker images!** 🎉
