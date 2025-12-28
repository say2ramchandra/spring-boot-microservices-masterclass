# Eureka Server Demo

## Overview
This is a **Service Discovery Server** using Netflix Eureka. It allows microservices to register themselves and discover other services dynamically.

## What You'll Learn
- ✅ How to set up Eureka Server
- ✅ Service registration and discovery
- ✅ Monitoring registered services via dashboard
- ✅ Health check configuration

## Running the Application

### Prerequisites
- Java 17+
- Maven 3.8+

### Steps
```bash
# Navigate to the project directory
cd 05-spring-cloud/demo-eureka-server

# Run the application
mvn spring-boot:run
```

### Access Points
- **Eureka Dashboard**: http://localhost:8761
- **Port**: 8761

## What to Observe

### 1. Eureka Dashboard
Visit http://localhost:8761 to see:
- Registered instances
- Service health status
- Instance metadata
- Renew and cancellation statistics

### 2. No Services Initially
When you first start, you'll see "No instances available" - this is normal. Services will register when you start the client applications.

## Key Configuration

### application.yml Highlights
```yaml
eureka:
  client:
    register-with-eureka: false  # Server doesn't register itself
    fetch-registry: false         # Server doesn't fetch registry
  server:
    enable-self-preservation: false  # Disabled for dev
```

## Next Steps
1. Start this Eureka Server
2. Run the demo-product-service and demo-order-service
3. Watch them register on the Eureka Dashboard
4. See how API Gateway discovers them automatically

## Production Considerations
- Enable self-preservation mode
- Set up multiple Eureka servers for high availability
- Configure proper security (authentication)
- Use DNS-based peer awareness
- Monitor with Actuator endpoints

## Troubleshooting

**Problem**: Services not showing up
- **Solution**: Check if services have `eureka.client.service-url.defaultZone` configured

**Problem**: Dashboard not loading
- **Solution**: Ensure port 8761 is not in use

**Problem**: Services showing as DOWN
- **Solution**: Check network connectivity and health check endpoints
