# Kubernetes Deployment

> **Deploy and orchestrate containerized microservices at scale with Kubernetes**

## 📚 Overview

Kubernetes (K8s) is an open-source container orchestration platform that automates deployment, scaling, and management of containerized applications. It's the industry standard for running microservices in production.

---

## 🎯 Learning Objectives

- ✅ Understand Kubernetes architecture and components
- ✅ Deploy Spring Boot applications to Kubernetes
- ✅ Configure service discovery and load balancing
- ✅ Manage configuration with ConfigMaps and Secrets
- ✅ Implement health checks (liveness/readiness probes)
- ✅ Scale applications horizontally
- ✅ Perform rolling updates and rollbacks
- ✅ Use Helm for package management

---

## ☸️ Kubernetes Architecture

```
┌─────────────────────────────────────────────────────────┐
│                 Kubernetes Cluster                       │
│                                                          │
│  ┌────────────────────────────────────────────────┐    │
│  │              Control Plane (Master)            │    │
│  │                                                │    │
│  │  ┌──────────────┐  ┌──────────────────────┐  │    │
│  │  │  API Server  │  │  Scheduler           │  │    │
│  │  │  (kubectl)   │  │  (Pod placement)     │  │    │
│  │  └──────────────┘  └──────────────────────┘  │    │
│  │                                                │    │
│  │  ┌──────────────┐  ┌──────────────────────┐  │    │
│  │  │ Controller   │  │  etcd                │  │    │
│  │  │ Manager      │  │  (Cluster state)     │  │    │
│  │  └──────────────┘  └──────────────────────┘  │    │
│  └────────────────────────────────────────────────┘    │
│                                                          │
│  ┌────────────────────────────────────────────────┐    │
│  │              Worker Nodes                      │    │
│  │                                                │    │
│  │  ┌────────────────────────────────────────┐   │    │
│  │  │  Node 1                                │   │    │
│  │  │  ┌─────────┐  ┌─────────┐            │   │    │
│  │  │  │  Pod 1  │  │  Pod 2  │            │   │    │
│  │  │  │ ┌─────┐ │  │ ┌─────┐ │            │   │    │
│  │  │  │ │ App │ │  │ │ App │ │            │   │    │
│  │  │  │ └─────┘ │  │ └─────┘ │            │   │    │
│  │  │  └─────────┘  └─────────┘            │   │    │
│  │  │  ┌─────────────────┐                 │   │    │
│  │  │  │ Kubelet         │                 │   │    │
│  │  │  │ kube-proxy      │                 │   │    │
│  │  │  │ Container       │                 │   │    │
│  │  │  │ Runtime         │                 │   │    │
│  │  │  └─────────────────┘                 │   │    │
│  │  └────────────────────────────────────────┘   │    │
│  │                                                │    │
│  │  ┌────────────────────────────────────────┐   │    │
│  │  │  Node 2, Node 3, ...                   │   │    │
│  │  └────────────────────────────────────────┘   │    │
│  └────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────┘
```

---

## 🧩 Core Kubernetes Objects

### 1. Pod

**Smallest deployable unit** - one or more containers

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: product-service
spec:
  containers:
  - name: app
    image: product-service:1.0
    ports:
    - containerPort: 8080
```

### 2. Deployment

**Manages desired state** of Pods (replicas, updates, rollbacks)

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: product-service
spec:
  replicas: 3
  selector:
    matchLabels:
      app: product-service
  template:
    metadata:
      labels:
        app: product-service
    spec:
      containers:
      - name: app
        image: product-service:1.0
```

### 3. Service

**Stable network endpoint** for Pods (load balancing, service discovery)

```yaml
apiVersion: v1
kind: Service
metadata:
  name: product-service
spec:
  type: LoadBalancer
  selector:
    app: product-service
  ports:
  - port: 80
    targetPort: 8080
```

### 4. ConfigMap

**Configuration data** (non-sensitive)

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: app-config
data:
  application.yml: |
    server:
      port: 8080
```

### 5. Secret

**Sensitive data** (passwords, tokens)

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: db-secret
type: Opaque
data:
  username: YWRtaW4=  # base64 encoded
  password: c2VjcmV0  # base64 encoded
```

### 6. Ingress

**HTTP(S) routing** to services

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: app-ingress
spec:
  rules:
  - host: myapp.example.com
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

---

## 🛠️ Kubernetes vs Docker Compose

```
┌────────────────────────────┬─────────────────┬──────────────┐
│ Feature                    │ Docker Compose  │ Kubernetes   │
├────────────────────────────┼─────────────────┼──────────────┤
│ Scope                      │ Single host     │ Multi-host   │
│ Orchestration              │ Basic           │ Advanced     │
│ Auto-scaling               │ ❌              │ ✅           │
│ Self-healing               │ ❌              │ ✅           │
│ Load balancing             │ Manual          │ Automatic    │
│ Rolling updates            │ Manual          │ Automatic    │
│ Service discovery          │ DNS             │ DNS + labels │
│ Health checks              │ Basic           │ Advanced     │
│ Production ready           │ Development     │ Production   │
│ Complexity                 │ Low             │ High         │
└────────────────────────────┴─────────────────┴──────────────┘
```

---

## 🚀 Setup Local Kubernetes

### Option 1: Minikube (Recommended)

```bash
# Install Minikube (Windows)
choco install minikube

# Start cluster
minikube start

# Verify
kubectl get nodes

# Enable ingress
minikube addons enable ingress

# Dashboard
minikube dashboard
```

### Option 2: Docker Desktop Kubernetes

```bash
# Enable in Docker Desktop settings
# Settings → Kubernetes → Enable Kubernetes

# Verify
kubectl get nodes
```

### Option 3: Kind (Kubernetes in Docker)

```bash
# Install kind
choco install kind

# Create cluster
kind create cluster --name dev

# Verify
kubectl cluster-info
```

---

## 📝 Essential kubectl Commands

### Cluster Information

```bash
# Get cluster info
kubectl cluster-info

# Get nodes
kubectl get nodes

# Describe node
kubectl describe node <node-name>
```

### Working with Pods

```bash
# List pods
kubectl get pods

# List pods (all namespaces)
kubectl get pods --all-namespaces

# Describe pod
kubectl describe pod <pod-name>

# View logs
kubectl logs <pod-name>

# Follow logs
kubectl logs -f <pod-name>

# Execute command
kubectl exec -it <pod-name> -- sh

# Port forward
kubectl port-forward <pod-name> 8080:8080
```

### Working with Deployments

```bash
# Create deployment
kubectl create deployment product-service --image=product-service:1.0

# List deployments
kubectl get deployments

# Scale deployment
kubectl scale deployment product-service --replicas=3

# Update image
kubectl set image deployment/product-service app=product-service:2.0

# Rollout status
kubectl rollout status deployment/product-service

# Rollout history
kubectl rollout history deployment/product-service

# Rollback
kubectl rollout undo deployment/product-service
```

### Working with Services

```bash
# List services
kubectl get services

# Expose deployment
kubectl expose deployment product-service --type=LoadBalancer --port=80 --target-port=8080

# Describe service
kubectl describe service product-service
```

### Apply YAML Files

```bash
# Apply single file
kubectl apply -f deployment.yaml

# Apply directory
kubectl apply -f k8s/

# Delete resources
kubectl delete -f deployment.yaml

# Dry run
kubectl apply -f deployment.yaml --dry-run=client
```

### Namespaces

```bash
# List namespaces
kubectl get namespaces

# Create namespace
kubectl create namespace dev

# Set default namespace
kubectl config set-context --current --namespace=dev

# Get resources in namespace
kubectl get pods -n dev
```

---

## 🎯 Deployment Strategies

### 1. Recreate

```yaml
strategy:
  type: Recreate
```

- Terminates all old pods
- Creates new pods
- Downtime during update

### 2. Rolling Update (Default)

```yaml
strategy:
  type: RollingUpdate
  rollingUpdate:
    maxSurge: 1        # Max new pods above replicas
    maxUnavailable: 1  # Max unavailable pods
```

- Gradual replacement
- Zero downtime
- Can rollback

### 3. Blue-Green

- Deploy new version (green)
- Switch traffic from old (blue) to new
- Quick rollback by switching back

### 4. Canary

- Deploy new version to small subset
- Gradually increase traffic
- Monitor metrics
- Rollout or rollback based on results

---

## 🔍 Health Checks

### Liveness Probe

**Checks if container is alive** (restart if fails)

```yaml
livenessProbe:
  httpGet:
    path: /actuator/health/liveness
    port: 8080
  initialDelaySeconds: 60
  periodSeconds: 10
  timeoutSeconds: 5
  failureThreshold: 3
```

### Readiness Probe

**Checks if container is ready** to receive traffic (remove from load balancer if fails)

```yaml
readinessProbe:
  httpGet:
    path: /actuator/health/readiness
    port: 8080
  initialDelaySeconds: 30
  periodSeconds: 10
  timeoutSeconds: 5
  failureThreshold: 3
```

### Startup Probe

**Checks if application has started** (for slow-starting apps)

```yaml
startupProbe:
  httpGet:
    path: /actuator/health
    port: 8080
  initialDelaySeconds: 0
  periodSeconds: 10
  timeoutSeconds: 5
  failureThreshold: 30  # 5 minutes total
```

---

## ⚙️ Resource Management

### Resource Requests and Limits

```yaml
resources:
  requests:
    memory: "256Mi"
    cpu: "500m"     # 0.5 CPU core
  limits:
    memory: "512Mi"
    cpu: "1000m"    # 1 CPU core
```

**requests**: Guaranteed resources
**limits**: Maximum allowed resources

### Horizontal Pod Autoscaler (HPA)

```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: product-service-hpa
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
```

---

## 🔐 Configuration Management

### ConfigMaps

```yaml
# configmap.yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: app-config
data:
  DATABASE_HOST: postgres
  DATABASE_PORT: "5432"
  LOG_LEVEL: INFO
```

**Use in Pod:**

```yaml
env:
  - name: DATABASE_HOST
    valueFrom:
      configMapKeyRef:
        name: app-config
        key: DATABASE_HOST
```

### Secrets

```yaml
# secret.yaml
apiVersion: v1
kind: Secret
metadata:
  name: db-secret
type: Opaque
stringData:  # Will be base64 encoded
  username: admin
  password: secret123
```

**Use in Pod:**

```yaml
env:
  - name: DB_USERNAME
    valueFrom:
      secretKeyRef:
        name: db-secret
        key: username
  - name: DB_PASSWORD
    valueFrom:
      secretKeyRef:
        name: db-secret
        key: password
```

---

## 📊 Service Types

### 1. ClusterIP (Default)

```yaml
type: ClusterIP
```

- Internal cluster access only
- Used for internal microservices

### 2. NodePort

```yaml
type: NodePort
ports:
- port: 80
  targetPort: 8080
  nodePort: 30080  # 30000-32767
```

- Exposes on each node's IP at static port
- Accessible from outside cluster

### 3. LoadBalancer

```yaml
type: LoadBalancer
```

- Creates external load balancer (cloud providers)
- Assigns external IP

### 4. ExternalName

```yaml
type: ExternalName
externalName: my.database.example.com
```

- Maps service to DNS name
- For external services

---

## 🎓 Demo Projects

### 1. Basic Deployment (`demo-basic-deployment/`)
- Simple Spring Boot deployment
- Service and Ingress configuration
- ConfigMaps and Secrets
- Health checks

### 2. Service Discovery (`demo-service-discovery/`)
- Multiple microservices
- Inter-service communication
- DNS-based discovery
- Load balancing

### 3. ConfigMaps & Secrets (`demo-configmaps-secrets/`)
- External configuration
- Database credentials
- Environment-specific configs
- Configuration hot-reload

---

## 🔗 Kubernetes Networking

```
┌──────────────────────────────────────────────┐
│           External Traffic                   │
│                  ↓                           │
│         ┌────────────────┐                   │
│         │    Ingress     │                   │
│         │   Controller   │                   │
│         └────────┬───────┘                   │
│                  ↓                           │
│         ┌────────────────┐                   │
│         │    Service     │                   │
│         │  (Load Bal)    │                   │
│         └────────┬───────┘                   │
│            ┌─────┼─────┐                     │
│            ↓     ↓     ↓                     │
│         ┌────┐┌────┐┌────┐                  │
│         │Pod ││Pod ││Pod │                  │
│         │ 1  ││ 2  ││ 3  │                  │
│         └────┘└────┘└────┘                  │
└──────────────────────────────────────────────┘
```

---

## 🎯 Best Practices

### 1. Always Use Deployments (Not Bare Pods)

```yaml
# Good ✓
kind: Deployment

# Bad ✗
kind: Pod
```

### 2. Set Resource Limits

```yaml
resources:
  requests:
    memory: "256Mi"
    cpu: "500m"
  limits:
    memory: "512Mi"
    cpu: "1000m"
```

### 3. Use Health Checks

```yaml
livenessProbe:
  httpGet:
    path: /actuator/health/liveness
readinessProbe:
  httpGet:
    path: /actuator/health/readiness
```

### 4. Use Labels and Selectors

```yaml
metadata:
  labels:
    app: product-service
    version: "1.0"
    environment: production
```

### 5. Use Namespaces for Isolation

```bash
kubectl create namespace dev
kubectl create namespace staging
kubectl create namespace production
```

### 6. Store Secrets Securely

```yaml
# Use Kubernetes Secrets
# Or external secret managers (Vault, AWS Secrets Manager)
```

### 7. Monitor and Log

- Use Prometheus for metrics
- Use Grafana for visualization
- Use ELK/Loki for logging

### 8. Implement Network Policies

```yaml
kind: NetworkPolicy
# Restrict pod-to-pod communication
```

---

## 🔄 CI/CD Integration

```
┌──────────────────────────────────────────────┐
│  Developer pushes code                        │
│           ↓                                   │
│  ┌──────────────────┐                        │
│  │   CI Pipeline    │                        │
│  │   (GitHub        │                        │
│  │    Actions)      │                        │
│  └────────┬─────────┘                        │
│           ↓                                   │
│  ┌──────────────────┐                        │
│  │ Build & Test     │                        │
│  │ Build Docker Img │                        │
│  │ Push to Registry │                        │
│  └────────┬─────────┘                        │
│           ↓                                   │
│  ┌──────────────────┐                        │
│  │   CD Pipeline    │                        │
│  │   kubectl apply  │                        │
│  └────────┬─────────┘                        │
│           ↓                                   │
│  ┌──────────────────┐                        │
│  │  Kubernetes      │                        │
│  │  Cluster         │                        │
│  └──────────────────┘                        │
└──────────────────────────────────────────────┘
```

---

## 📚 Key Takeaways

1. ✅ **Kubernetes orchestrates containers at scale**
2. ✅ **Deployments manage Pod lifecycle**
3. ✅ **Services provide stable networking**
4. ✅ **ConfigMaps & Secrets manage configuration**
5. ✅ **Health checks enable self-healing**
6. ✅ **HPA enables auto-scaling**
7. ✅ **Ingress manages external access**

---

## 🎯 Next Steps

After mastering Kubernetes basics:
1. ✅ Learn Helm for package management
2. ✅ Implement CI/CD pipelines
3. ✅ Add monitoring with Prometheus/Grafana
4. ✅ Implement GitOps with ArgoCD
5. ✅ Study Kubernetes operators

---

## 🔗 Resources

- [Kubernetes Documentation](https://kubernetes.io/docs/)
- [kubectl Cheat Sheet](https://kubernetes.io/docs/reference/kubectl/cheatsheet/)
- [Spring Boot on Kubernetes](https://spring.io/guides/gs/spring-boot-kubernetes/)
- [Kubernetes Patterns](https://k8spatterns.io/)

---

_"Kubernetes is the operating system of the cloud." - Kelsey Hightower_
