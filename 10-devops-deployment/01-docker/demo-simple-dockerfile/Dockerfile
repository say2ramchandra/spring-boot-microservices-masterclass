# ==================================================
# Simple Dockerfile for Spring Boot Application
# ==================================================
# This is a basic, single-stage Dockerfile that demonstrates
# the fundamental concepts of containerizing a Spring Boot app.
#
# Build JAR first:  mvn clean package
# Build image:      docker build -t simple-docker-demo:1.0 .
# Run container:    docker run -p 8080:8080 simple-docker-demo:1.0
# ==================================================

# Use official Eclipse Temurin JDK image (OpenJDK distribution)
FROM eclipse-temurin:17-jre-alpine

# Metadata
LABEL maintainer="masterclass@example.com"
LABEL version="1.0"
LABEL description="Simple Docker containerization demo"

# Set working directory
WORKDIR /app

# Copy the JAR file from target directory
# Note: You must build the JAR first using 'mvn clean package'
COPY target/demo-simple-dockerfile.jar app.jar

# Expose the application port
EXPOSE 8080

# Set environment variables (can be overridden at runtime)
ENV SPRING_PROFILES_ACTIVE=default
ENV JAVA_OPTS=""

# Run the application
# Using exec form for proper signal handling
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
