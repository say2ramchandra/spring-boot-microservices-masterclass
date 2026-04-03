# ==================================================
# Multi-Stage Dockerfile for Spring Boot
# ==================================================
# This Dockerfile demonstrates multi-stage builds which:
# 1. Build the application in one stage (with build tools)
# 2. Copy only the JAR to runtime stage (without build tools)
#
# Benefits:
# - Smaller final image (~50% size reduction)
# - No build tools in production image
# - Better security (fewer vulnerabilities)
# - Faster deployment (smaller image)
#
# Build: docker build -t multi-stage-demo:1.0 .
# Run:   docker run -p 8080:8080 multi-stage-demo:1.0
# ==================================================

# ============================================
# Stage 1: Build Stage
# ============================================
FROM maven:3.9-eclipse-temurin-17 AS builder

# Set working directory for build
WORKDIR /build

# Copy Maven files first for better layer caching
# Dependencies change less frequently than source code
COPY pom.xml .

# Download dependencies (cached if pom.xml unchanged)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
# -DskipTests: Skip tests for faster builds (run tests in CI/CD)
# -B: Batch mode (non-interactive)
RUN mvn clean package -DskipTests -B

# Optional: List build artifacts for verification
RUN ls -lh /build/target/

# ============================================
# Stage 2: Runtime Stage
# ============================================
FROM eclipse-temurin:17-jre-alpine

# Metadata labels
LABEL maintainer="masterclass@example.com"
LABEL version="1.0"
LABEL description="Multi-stage build optimized Spring Boot application"

# Install curl for health checks
RUN apk add --no-cache curl

# Create non-root user and group
RUN addgroup -S spring && adduser -S spring -G spring

# Set working directory
WORKDIR /app

# Copy JAR from builder stage
# IMPORTANT: Use --from=builder to reference the build stage
COPY --from=builder --chown=spring:spring /build/target/*.jar app.jar

# Switch to non-root user
USER spring:spring

# Expose application port
EXPOSE 8080

# Health check configuration
# Checks if the application is healthy every 30 seconds
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# JVM Options optimized for containers
ENV JAVA_OPTS="-XX:+UseContainerSupport \
               -XX:MaxRAMPercentage=75.0 \
               -XX:InitialRAMPercentage=50.0 \
               -Djava.security.egd=file:/dev/./urandom"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]

# ==================================================
# Image Size Comparison:
# - Single-stage with Maven: ~500MB
# - Multi-stage with JRE only: ~200MB
# - Size reduction: ~60%
# ==================================================
