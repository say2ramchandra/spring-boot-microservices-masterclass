# Module 10: DevOps & Deployment

> **Deploy microservices to production using Docker, Kubernetes, and CI/CD pipelines**

## 📚 Module Overview

Modern microservices applications require containerization and orchestration for scalability, reliability, and efficient deployment. This module covers the complete DevOps lifecycle from containerizing applications with Docker to deploying them on Kubernetes and automating the process with CI/CD pipelines.

---

## 🎯 Learning Objectives

By the end of this module, you will be able to:

- ✅ Containerize Spring Boot microservices with Docker
- ✅ Build optimized multi-stage Docker images
- ✅ Deploy applications to Kubernetes clusters
- ✅ Manage Kubernetes resources (Pods, Services, Deployments, ConfigMaps)
- ✅ Implement service discovery and load balancing in Kubernetes
- ✅ Configure health checks and auto-scaling
- ✅ Build CI/CD pipelines with GitHub Actions
- ✅ Deploy to production environments safely
- ✅ Monitor and troubleshoot containerized applications

---

## 🗺️ Module Structure

```
10-devops-deployment/
├── README.md                                # This file
├── 01-docker/
│   ├── README.md                           # Docker fundamentals
│   ├── demo-simple-dockerfile/             # Basic containerization
│   ├── demo-multi-stage-build/             # Optimized builds
│   └── demo-docker-compose/                # Multi-container apps
├── 02-kubernetes/
│   ├── README.md                           # Kubernetes fundamentals
│   ├── demo-basic-deployment/              # K8s deployment
│   ├── demo-service-discovery/             # Service mesh
│   └── demo-configmaps-secrets/            # Configuration management
├── 03-ci-cd/
│   ├── README.md                           # CI/CD fundamentals
│   ├── demo-github-actions/                # GitHub Actions pipeline
│   ├── demo-jenkins/                       # Jenkins pipeline
│   └── demo-gitops/                        # GitOps with ArgoCD
└── 04-production-deployment/
    ├── README.md                           # Production best practices
    ├── demo-blue-green/                    # Blue-green deployment
    ├── demo-canary/                        # Canary releases
    └── demo-monitoring/                    # Production monitoring
```

---

## 🐳 Docker Fundamentals

### Why Docker?

**Problems Docker Solves:**
- "Works on my machine" syndrome
- Environment inconsistencies
- Dependency conflicts
- Slow deployment processes
- Resource inefficiency

**Benefits:**
- ⚡ Consistent environments (dev, test, prod)
- 📦 Package app with all dependencies
- 🚀 Fast startup times
- 💰 Efficient resource utilization
- 🔄 Easy rollback and updates
- 📊 Better scaling capabilities

### Docker Architecture

```
┌─────────────────────────────────────────────┐
│           Docker Client (CLI)               │
│        docker build | run | push            │
└────────────────┬────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────┐
│           Docker Daemon                     │
│   - Builds images                           │
│   - Runs containers                         │
│   - Manages networks & volumes              │
└────────────────┬────────────────────────────┘
                 │
        ┌────────┴─────────┐
        ▼                  ▼
┌──────────────┐    ┌──────────────┐
│   Images     │    │  Containers  │
│  (Template)  │    │   (Running)  │
└──────────────┘    └──────────────┘
```

---

## ☸️ Kubernetes Fundamentals

### Why Kubernetes?

**Problems Kubernetes Solves:**
- Container orchestration at scale
- Service discovery and load balancing
- Self-healing and auto-scaling
- Rolling updates and rollbacks
- Secret and configuration management

**Key Components:**

```
┌─────────────────────────────────────────────┐
│            Kubernetes Cluster               │
│                                             │
│  ┌──────────────────────────────────────┐  │
│  │         Control Plane                │  │
│  │  - API Server                        │  │
│  │  - Scheduler                         │  │
│  │  - Controller Manager                │  │
│  │  - etcd (Cluster state)              │  │
│  └──────────────────────────────────────┘  │
│                                             │
│  ┌──────────────────────────────────────┐  │
│  │         Worker Nodes                 │  │
│  │                                      │  │
│  │  ┌──────────────┐  ┌──────────────┐ │  │
│  │  │   Pod 1      │  │   Pod 2      │ │  │
│  │  │ ┌──────────┐ │  │ ┌──────────┐ │ │  │
│  │  │ │Container │ │  │ │Container │ │ │  │
│  │  │ └──────────┘ │  │ └──────────┘ │ │  │
│  │  └──────────────┘  └──────────────┘ │  │
│  │                                      │  │
│  │  - Kubelet (Node agent)              │  │
│  │  - Kube-proxy (Networking)           │  │
│  │  - Container Runtime (Docker)        │  │
│  └──────────────────────────────────────┘  │
└─────────────────────────────────────────────┘
```

---

## 🔄 CI/CD Pipeline

### Complete DevOps Workflow

```
Developer → Git Push → CI/CD Pipeline → Production
                           │
                           ├─ Build
                           ├─ Test
                           ├─ Security Scan
                           ├─ Build Image
                           ├─ Push to Registry
                           └─ Deploy

┌─────────────────────────────────────────────────────┐
│                   CI/CD Pipeline                    │
│                                                     │
│  ┌──────┐   ┌──────┐   ┌──────┐   ┌──────┐       │
│  │ Code │ → │Build │ → │ Test │ → │Deploy│       │
│  │      │   │      │   │      │   │      │       │
│  └──────┘   └──────┘   └──────┘   └──────┘       │
│      │          │          │          │           │
│      ▼          ▼          ▼          ▼           │
│   GitHub    Maven/    Unit Tests  Kubernetes     │
│             Gradle    Integration                 │
│                       Tests                       │
└─────────────────────────────────────────────────────┘
```

---

## 🚀 Quick Start

### Prerequisites

1. **Docker Desktop** (includes Kubernetes)
   ```bash
   docker --version
   docker-compose --version
   ```

2. **kubectl** (Kubernetes CLI)
   ```bash
   kubectl version --client
   ```

3. **Minikube** (optional, for local K8s cluster)
   ```bash
   minikube version
   ```

### Module Learning Path

Follow this order for best results:

1. **Week 1: Docker** (Days 1-3)
   - Docker basics and Dockerfile
   - Multi-stage builds for Spring Boot
   - Docker Compose for multi-container apps
   
2. **Week 2: Kubernetes** (Days 4-6)
   - Kubernetes fundamentals
   - Deployments, Services, ConfigMaps
   - Service discovery and scaling
   
3. **Week 3: CI/CD** (Days 7-8)
   - GitHub Actions pipelines
   - Automated testing and deployment
   - GitOps principles
   
4. **Week 4: Production** (Days 9-10)
   - Blue-green deployments
   - Canary releases
   - Production monitoring and troubleshooting

---

## 📊 Learning Outcomes

After completing this module, you'll be able to:

### Docker Competencies
- ✅ Write production-ready Dockerfiles
- ✅ Optimize image sizes with multi-stage builds
- ✅ Use Docker Compose for local development
- ✅ Manage Docker networks and volumes
- ✅ Push images to Docker Hub/Registry

### Kubernetes Competencies
- ✅ Deploy microservices to Kubernetes
- ✅ Configure service discovery and load balancing
- ✅ Manage application configuration with ConfigMaps/Secrets
- ✅ Implement health checks (liveness/readiness probes)
- ✅ Scale applications horizontally
- ✅ Perform rolling updates and rollbacks

### CI/CD Competencies
- ✅ Build automated CI/CD pipelines
- ✅ Implement automated testing in pipelines
- ✅ Deploy to Kubernetes automatically
- ✅ Implement GitOps workflows
- ✅ Manage environment-specific configurations

### Production Competencies
- ✅ Deploy safely with blue-green deployments
- ✅ Implement canary releases
- ✅ Monitor containerized applications
- ✅ Troubleshoot production issues
- ✅ Implement disaster recovery

---

## 🛠️ Tools & Technologies

### Core Tools
- **Docker** - Containerization
- **Kubernetes** - Container orchestration
- **kubectl** - Kubernetes CLI
- **Helm** - Package manager for Kubernetes
- **Docker Compose** - Multi-container applications

### CI/CD Tools
- **GitHub Actions** - CI/CD automation
- **Jenkins** - Alternative CI/CD platform
- **ArgoCD** - GitOps continuous delivery

### Supporting Tools
- **Prometheus** - Monitoring
- **Grafana** - Visualization
- **Lens** - Kubernetes IDE
- **k9s** - Terminal UI for Kubernetes

---

## 📚 Key Concepts

### Docker Concepts
- **Image** - Read-only template with application code
- **Container** - Running instance of an image
- **Dockerfile** - Instructions to build an image
- **Layer** - Cached instruction result in image
- **Volume** - Persistent data storage
- **Network** - Communication between containers

### Kubernetes Concepts
- **Pod** - Smallest deployable unit (one or more containers)
- **Deployment** - Manages desired state of Pods
- **Service** - Stable network endpoint for Pods
- **Namespace** - Virtual cluster for isolation
- **ConfigMap** - Non-sensitive configuration data
- **Secret** - Sensitive data (passwords, tokens)
- **Ingress** - HTTP(S) routing to services
- **HPA** - Horizontal Pod Autoscaler

### CI/CD Concepts
- **Continuous Integration** - Automated build + test
- **Continuous Deployment** - Automated deployment
- **Pipeline** - Automated workflow
- **Stage** - Step in pipeline (build, test, deploy)
- **Artifact** - Build output (JAR, Docker image)
- **GitOps** - Git as single source of truth

---

## 🎯 Best Practices

### Docker Best Practices
1. ✅ Use official base images
2. ✅ Minimize image layers
3. ✅ Use multi-stage builds
4. ✅ Don't run as root user
5. ✅ Use .dockerignore file
6. ✅ Version your images with tags
7. ✅ Scan images for vulnerabilities
8. ✅ Keep images small (<500MB ideally)

### Kubernetes Best Practices
1. ✅ Use resource limits (CPU/Memory)
2. ✅ Implement health checks
3. ✅ Use namespaces for isolation
4. ✅ Store sensitive data in Secrets
5. ✅ Label resources consistently
6. ✅ Use rolling updates
7. ✅ Enable RBAC for security
8. ✅ Monitor resource usage

### CI/CD Best Practices
1. ✅ Automate everything
2. ✅ Test early and often
3. ✅ Keep pipelines fast (<10 min)
4. ✅ Use environment-specific configs
5. ✅ Implement approval gates for prod
6. ✅ Version control everything
7. ✅ Monitor pipeline health
8. ✅ Implement rollback strategies

---

## 📈 Module Progression

```
Basic → Intermediate → Advanced → Expert

Day 1-2:  Docker basics, Dockerfile fundamentals
Day 3-4:  Multi-stage builds, Docker Compose
Day 5-6:  Kubernetes basics, Deployments, Services
Day 7-8:  ConfigMaps, Secrets, Ingress
Day 9:    CI/CD pipelines, GitHub Actions
Day 10:   Production deployments, monitoring
```

---

## 🔗 Additional Resources

### Documentation
- [Docker Documentation](https://docs.docker.com/)
- [Kubernetes Documentation](https://kubernetes.io/docs/)
- [GitHub Actions Docs](https://docs.github.com/en/actions)
- [Helm Documentation](https://helm.sh/docs/)

### Tutorials
- [Play with Docker](https://labs.play-with-docker.com/)
- [Play with Kubernetes](https://labs.play-with-k8s.com/)
- [Katacoda Kubernetes Scenarios](https://www.katacoda.com/courses/kubernetes)

### Books
- "Docker Deep Dive" by Nigel Poulton
- "Kubernetes in Action" by Marko Lukša
- "Continuous Delivery" by Jez Humble

---

## 🎓 Certification Paths

After mastering this module, consider:
- **Docker Certified Associate (DCA)**
- **Certified Kubernetes Administrator (CKA)**
- **Certified Kubernetes Application Developer (CKAD)**

---

## 🚀 Let's Get Started!

Begin with **01-docker/** to learn containerization fundamentals, then progress through Kubernetes and CI/CD.

Each section includes:
- 📖 Comprehensive theory
- 💻 Hands-on demos
- 🎯 Real-world examples
- ✅ Best practices
- 🐛 Troubleshooting guides

---

_"Containers are the future of application deployment." - Industry Standard_
