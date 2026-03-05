# Kubernetes Basic Deployment Demo

> **Deploy Spring Boot microservice to Kubernetes with full production setup**

## 📚 Overview

This demo shows a complete Kubernetes deployment of a Spring Boot application with PostgreSQL, including Deployments, Services, ConfigMaps, Secrets, Ingress, and health checks.

---

## 🏗️ Architecture

```
External Traffic
       ↓
   [Ingress]
       ↓
[Product Service] ←→ [PostgreSQL]
    (3 replicas)        (1 replica)
       ↓                     ↓
 [ClusterIP]          [ClusterIP]
       ↓                     ↓
[ConfigMap/Secret]    [PersistentVolume]
```

---

## 📁 Project Structure

```
demo-basic-deployment/
├── k8s/
│   ├── namespace.yaml              # Namespace isolation
│   ├── configmap.yaml              # Application configuration
│   ├── secret.yaml                 # Database credentials
│   ├── postgres-deployment.yaml    # PostgreSQL deployment
│   ├── postgres-service.yaml       # PostgreSQL service
│   ├── app-deployment.yaml         # Spring Boot deployment
│   ├── app-service.yaml            # Spring Boot service
│   ├── ingress.yaml                # External access
│   └── hpa.yaml                    # Horizontal Pod Autoscaler
├── src/                            # Spring Boot application
├── Dockerfile
├── pom.xml
└── README.md
```

---

## 🚀 Quick Start

### Prerequisites

```bash
# Verify kubectl installed
kubectl version --client

# Verify cluster access
kubectl cluster-info

# If using Minikube
minikube start
minikube addons enable ingress
minikube addons enable metrics-server
```

### Step 1: Build Docker Image

```bash
# Build application
mvn clean package

# Build Docker image
docker build -t product-service:1.0 .

# For Minikube (load image into Minikube)
minikube image load product-service:1.0

# For other clusters, push to registry
docker tag product-service:1.0 yourregistry/product-service:1.0
docker push yourregistry/product-service:1.0
```

### Step 2: Deploy to Kubernetes

```bash
# Create namespace
kubectl apply -f k8s/namespace.yaml

# Deploy all resources
kubectl apply -f k8s/

# Or deploy in order:
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/secret.yaml
kubectl apply -f k8s/postgres-deployment.yaml
kubectl apply -f k8s/postgres-service.yaml
kubectl apply -f k8s/app-deployment.yaml
kubectl apply -f k8s/app-service.yaml
kubectl apply -f k8s/ingress.yaml
kubectl apply -f k8s/hpa.yaml
```

### Step 3: Verify Deployment

```bash
# Check all resources
kubectl get all -n product

# Check pods
kubectl get pods -n product

# Check services
kubectl get services -n product

# Check ingress
kubectl get ingress -n product

# View logs
kubectl logs -n product -l app=product-service -f
```

### Step 4: Access the Application

```bash
# For Minikube
minikube service product-service -n product

# Or get Minikube IP and NodePort
minikube ip
kubectl get service product-service -n product

# Access API
curl http://<MINIKUBE-IP>:<NODE-PORT>/api/products

# With Ingress (add to /etc/hosts)
echo "$(minikube ip) product.local" | sudo tee -a /etc/hosts
curl http://product.local/api/products
```

---

## 📝 Kubernetes Manifests Explained

### 1. Namespace

**File: k8s/namespace.yaml**

```yaml
apiVersion: v1
kind: Namespace
metadata:
  name: product
  labels:
    name: product
    environment: development
```

**Purpose:**
- Isolates resources
- Organizes cluster resources
- Enables resource quotas

### 2. ConfigMap

**File: k8s/configmap.yaml**

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: app-config
  namespace: product
data:
  DATABASE_HOST: postgres-service
  DATABASE_PORT: "5432"
  DATABASE_NAME: productdb
  SPRING_PROFILES_ACTIVE: kubernetes
  LOG_LEVEL: INFO
```

**Purpose:**
- Non-sensitive configuration
- Environment-specific settings
- Can be updated without rebuilding image

### 3. Secret

**File: k8s/secret.yaml**

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: db-secret
  namespace: product
type: Opaque
stringData:
  username: admin
  password: secret123
```

**Purpose:**
- Stores sensitive data (passwords, tokens)
- Base64 encoded (use external secret managers in production)
- Mounted as environment variables or files

### 4. PostgreSQL Deployment

**File: k8s/postgres-deployment.yaml**

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: postgres
  namespace: product
spec:
  replicas: 1
  selector:
    matchLabels:
      app: postgres
  template:
    metadata:
      labels:
        app: postgres
    spec:
      containers:
      - name: postgres
        image: postgres:15-alpine
        ports:
        - containerPort: 5432
        env:
        - name: POSTGRES_DB
          valueFrom:
            configMapKeyRef:
              name: app-config
              key: DATABASE_NAME
        - name: POSTGRES_USER
          valueFrom:
            secretKeyRef:
              name: db-secret
              key: username
        - name: POSTGRES_PASSWORD
          valueFrom:
            secretKeyRef:
              name: db-secret
              key: password
        resources:
          requests:
            memory: "256Mi"
            cpu: "250m"
          limits:
            memory: "512Mi"
            cpu: "500m"
        volumeMounts:
        - name: postgres-storage
          mountPath: /var/lib/postgresql/data
        livenessProbe:
          exec:
            command:
            - pg_isready
            - -U
            - admin
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          exec:
            command:
            - pg_isready
            - -U
            - admin
          initialDelaySeconds: 5
          periodSeconds: 5
      volumes:
      - name: postgres-storage
        emptyDir: {}  # For demo; use PersistentVolume in production
```

**Key Points:**
- **replicas: 1** - Single database instance
- **env** - Uses ConfigMap and Secret
- **resources** - Resource requests and limits
- **livenessProbe** - Checks if container is alive
- **readinessProbe** - Checks if ready to accept traffic
- **volumes** - Persistent storage (emptyDir for demo)

### 5. Application Deployment

**File: k8s/app-deployment.yaml**

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: product-service
  namespace: product
spec:
  replicas: 3
  selector:
    matchLabels:
      app: product-service
  template:
    metadata:
      labels:
        app: product-service
        version: "1.0"
    spec:
      containers:
      - name: product-service
        image: product-service:1.0
        imagePullPolicy: IfNotPresent
        ports:
        - containerPort: 8080
          name: http
        env:
        - name: SPRING_PROFILES_ACTIVE
          valueFrom:
            configMapKeyRef:
              name: app-config
              key: SPRING_PROFILES_ACTIVE
        - name: SPRING_DATASOURCE_URL
          value: "jdbc:postgresql://$(DATABASE_HOST):$(DATABASE_PORT)/$(DATABASE_NAME)"
        - name: DATABASE_HOST
          valueFrom:
            configMapKeyRef:
              name: app-config
              key: DATABASE_HOST
        - name: DATABASE_PORT
          valueFrom:
            configMapKeyRef:
              name: app-config
              key: DATABASE_PORT
        - name: DATABASE_NAME
          valueFrom:
            configMapKeyRef:
              name: app-config
              key: DATABASE_NAME
        - name: SPRING_DATASOURCE_USERNAME
          valueFrom:
            secretKeyRef:
              name: db-secret
              key: username
        - name: SPRING_DATASOURCE_PASSWORD
          valueFrom:
            secretKeyRef:
              name: db-secret
              key: password
        resources:
          requests:
            memory: "512Mi"
            cpu: "500m"
          limits:
            memory: "1Gi"
            cpu: "1000m"
        livenessProbe:
          httpGet:
            path: /actuator/health/liveness
            port: 8080
          initialDelaySeconds: 60
          periodSeconds: 10
          timeoutSeconds: 5
          failureThreshold: 3
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
          timeoutSeconds: 5
          failureThreshold: 3
        startupProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 0
          periodSeconds: 10
          failureThreshold: 30
```

**Key Points:**
- **replicas: 3** - Three instances for high availability
- **env** - Configuration from ConfigMap and Secret
- **resources** - JVM requires more memory
- **livenessProbe** - Restart if unhealthy
- **readinessProbe** - Remove from load balancer if not ready
- **startupProbe** - Allows slow startup (5 minutes)

### 6. Services

**File: k8s/postgres-service.yaml**

```yaml
apiVersion: v1
kind: Service
metadata:
  name: postgres-service
  namespace: product
spec:
  type: ClusterIP
  selector:
    app: postgres
  ports:
  - port: 5432
    targetPort: 5432
    protocol: TCP
```

**File: k8s/app-service.yaml**

```yaml
apiVersion: v1
kind: Service
metadata:
  name: product-service
  namespace: product
  labels:
    app: product-service
spec:
  type: LoadBalancer  # Or NodePort for Minikube
  selector:
    app: product-service
  ports:
  - port: 80
    targetPort: 8080
    protocol: TCP
    name: http
```

**Key Points:**
- **ClusterIP** (postgres) - Internal only
- **LoadBalancer** (app) - External access
- **selector** - Matches pod labels
- **port** - External port, **targetPort** - Container port

### 7. Ingress

**File: k8s/ingress.yaml**

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: product-ingress
  namespace: product
  annotations:
    nginx.ingress.kubernetes.io/rewrite-target: /
spec:
  ingressClassName: nginx
  rules:
  - host: product.local
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: product-service
            port:
              number: 80
```

**Purpose:**
- HTTP(S) routing
- SSL termination
- Path-based routing
- Virtual hosting

### 8. Horizontal Pod Autoscaler

**File: k8s/hpa.yaml**

```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: product-service-hpa
  namespace: product
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: product-service
  minReplicas: 2
  maxReplicas: 10
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 70
  - type: Resource
    resource:
      name: memory
      target:
        type: Utilization
        averageUtilization: 80
```

**Purpose:**
- Automatic scaling based on CPU/memory
- Maintains performance under load
- Cost optimization (scale down when idle)

---

## 🔄 Common Operations

### View Resources

```bash
# Get all resources in namespace
kubectl get all -n product

# Get pods with details
kubectl get pods -n product -o wide

# Get services
kubectl get services -n product

# Get deployments
kubectl get deployments -n product

# Get configmaps
kubectl get configmaps -n product

# Get secrets
kubectl get secrets -n product
```

### Logs and Debugging

```bash
# View pod logs
kubectl logs -n product <pod-name>

# Follow logs
kubectl logs -n product -f <pod-name>

# Logs from all pods with label
kubectl logs -n product -l app=product-service -f

# Describe pod (events, status)
kubectl describe pod -n product <pod-name>

# Exec into pod
kubectl exec -it -n product <pod-name> -- sh

# Port forward
kubectl port-forward -n product <pod-name> 8080:8080
```

### Scaling

```bash
# Manual scale
kubectl scale deployment product-service --replicas=5 -n product

# Check HPA status
kubectl get hpa -n product

# Describe HPA
kubectl describe hpa product-service-hpa -n product
```

### Updates

```bash
# Update image
kubectl set image deployment/product-service product-service=product-service:2.0 -n product

# Check rollout status
kubectl rollout status deployment/product-service -n product

# View rollout history
kubectl rollout history deployment/product-service -n product

# Rollback to previous version
kubectl rollout undo deployment/product-service -n product

# Rollback to specific revision
kubectl rollout undo deployment/product-service --to-revision=2 -n product
```

### Configuration Updates

```bash
# Edit ConfigMap
kubectl edit configmap app-config -n product

# Restart pods to pick up changes
kubectl rollout restart deployment/product-service -n product
```

---

## 🧪 Testing

### Test Endpoints

```bash
# Get service URL (Minikube)
minikube service product-service -n product --url

# Set variable
export API_URL=$(minikube service product-service -n product --url)

# Test health
curl $API_URL/actuator/health

# Get all products
curl $API_URL/api/products

# Create product
curl -X POST $API_URL/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Kubernetes Book",
    "description": "Learn Kubernetes",
    "price": 59.99,
    "category": "Books",
    "stock": 100,
    "available": true
  }'
```

### Load Testing

```bash
# Install hey (load testing tool)
go install github.com/rakyll/hey@latest

# Run load test
hey -n 10000 -c 100 $API_URL/api/products

# Watch HPA scale
kubectl get hpa -n product --watch
```

---

## 🐛 Troubleshooting

### Pod Not Starting

```bash
# Check pod status
kubectl get pods -n product

# Describe pod for events
kubectl describe pod -n product <pod-name>

# View logs
kubectl logs -n product <pod-name>

# Common issues:
# - Image pull error: Check image name/tag
# - CrashLoopBackOff: Check logs for application errors
# - Pending: Check resource availability
```

### Database Connection Issues

```bash
# Test database connectivity from app pod
kubectl exec -it -n product <app-pod-name> -- sh

# Inside pod:
nc -zv postgres-service 5432
env | grep DATABASE

# Check database pod
kubectl logs -n product -l app=postgres
```

### Service Not Accessible

```bash
# Check service exists
kubectl get service product-service -n product

# Check endpoints
kubectl get endpoints product-service -n product

# If empty, check pod labels match service selector
kubectl get pods -n product --show-labels
```

---

## 🔄 Cleanup

```bash
# Delete all resources
kubectl delete -f k8s/

# Or delete namespace (removes everything)
kubectl delete namespace product

# Verify deletion
kubectl get all -n product
```

---

## 📚 Key Takeaways

1. ✅ **Deployments manage Pod lifecycle**
2. ✅ **Services provide stable networking**
3. ✅ **ConfigMaps store configuration**
4. ✅ **Secrets store credentials**
5. ✅ **Health probes enable self-healing**
6. ✅ **HPA enables auto-scaling**
7. ✅ **Ingress manages external access**

---

## 🎯 Next Steps

- ✅ Add monitoring (Prometheus/Grafana)
- ✅ Use PersistentVolumes for database
- ✅ Implement Network Policies
- ✅ Add resource quotas
- ✅ Use Helm charts
- ✅ Implement GitOps with ArgoCD

---

**You've successfully deployed a Spring Boot microservice to Kubernetes!** 🎉
