# CI/CD Pipelines

> **Automate build, test, and deployment with continuous integration and continuous delivery**

## 📚 Overview

CI/CD (Continuous Integration / Continuous Delivery) automates the software delivery process. It enables rapid, reliable, and repeatable deployments to production environments.

---

## 🎯 Learning Objectives

- ✅ Understand CI/CD principles and benefits
- ✅ Build CI/CD pipelines with GitHub Actions
- ✅ Automate testing in pipelines
- ✅ Build and push Docker images automatically
- ✅ Deploy to Kubernetes from CI/CD
- ✅ Implement GitOps workflows
- ✅ Add security scanning to pipelines

---

## 🔄 CI/CD Pipeline Flow

```
┌──────────────────────────────────────────────────────────────┐
│                     CI/CD Pipeline                            │
│                                                               │
│  Developer          Git Commit                                │
│     │                   ↓                                     │
│     └──────────→  GitHub/GitLab                              │
│                        ↓                                      │
│              ┌─────────────────────┐                         │
│              │   Trigger Pipeline  │                         │
│              └──────────┬──────────┘                         │
│                         │                                     │
│         ┌───────────────┴───────────────┐                   │
│         ↓                               ↓                    │
│   ┌──────────┐                   ┌──────────┐               │
│   │  Build   │                   │   Test   │               │
│   │          │                   │          │               │
│   │ - Compile│                   │ - Unit   │               │
│   │ - Maven  │                   │ - Integ  │               │
│   └─────┬────┘                   │ - E2E    │               │
│         │                        └─────┬────┘               │
│         └──────────┬───────────────────┘                    │
│                    ↓                                         │
│          ┌──────────────────┐                               │
│          │  Security Scan   │                               │
│          │  - SAST          │                               │
│          │  - Dependency    │                               │
│          └────────┬─────────┘                               │
│                   ↓                                          │
│          ┌──────────────────┐                               │
│          │  Build Image     │                               │
│          │  - Docker build  │                               │
│          │  - Tag version   │                               │
│          └────────┬─────────┘                               │
│                   ↓                                          │
│          ┌──────────────────┐                               │
│          │  Push to         │                               │
│          │  Registry        │                               │
│          │  - Docker Hub    │                               │
│          │  - ECR/GCR/ACR   │                               │
│          └────────┬─────────┘                               │
│                   ↓                                          │
│          ┌──────────────────┐                               │
│          │  Deploy          │                               │
│          │  - Update K8s    │                               │
│          │  - kubectl apply │                               │
│          │  - Helm upgrade  │                               │
│          └────────┬─────────┘                               │
│                   ↓                                          │
│          ┌──────────────────┐                               │
│          │  Verify          │                               │
│          │  - Health check  │                               │
│          │  - Smoke tests   │                               │
│          └──────────────────┘                               │
└──────────────────────────────────────────────────────────────┘
```

---

## 🚀 CI vs CD

### Continuous Integration (CI)

```
Code → Build → Test → Merge
```

**Goals:**
- Detect integration issues early
- Automate testing
- Maintain code quality
- Fast feedback to developers

**Key Practices:**
- Commit frequently
- Automated builds
- Automated testing
- Fix broken builds immediately

### Continuous Delivery (CD)

```
Build → Package → Deploy to Staging → Manual Approval → Production
```

**Goals:**
- Always production-ready
- Automated deployments
- Reduce time to market

### Continuous Deployment

```
Build → Test → Deploy → Production (Automatic)
```

**Goals:**
- Fully automated pipeline
- No manual approval
- Multiple deployments per day

---

## 🛠️ GitHub Actions

### Workflow Structure

```yaml
name: CI/CD Pipeline

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
      - name: Build with Maven
        run: mvn clean package
```

### Complete Pipeline Example

See `demo-github-actions/.github/workflows/ci-cd.yml` for full implementation.

---

## 📊 Pipeline Stages

### 1. Source

```yaml
- name: Checkout code
  uses: actions/checkout@v3
  with:
    fetch-depth: 0  # Full history for better analysis
```

### 2. Build

```yaml
- name: Set up JDK 17
  uses: actions/setup-java@v3
  with:
    java-version: '17'
    distribution: 'temurin'
    cache: maven

- name: Build with Maven
  run: mvn clean package -DskipTests
```

### 3. Test

```yaml
- name: Run Unit Tests
  run: mvn test

- name: Run Integration Tests
  run: mvn verify -P integration-tests

- name: Generate Test Report
  uses: dorny/test-reporter@v1
  if: always()
  with:
    name: Test Results
    path: target/surefire-reports/*.xml
    reporter: java-junit
```

### 4. Code Quality

```yaml
- name: SonarQube Scan
  uses: sonarsource/sonarqube-scan-action@master
  env:
    SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
    SONAR_HOST_URL: ${{ secrets.SONAR_HOST_URL }}
```

### 5. Security Scan

```yaml
- name: Run Trivy vulnerability scanner
  uses: aquasecurity/trivy-action@master
  with:
    scan-type: 'fs'
    scan-ref: '.'
    format: 'sarif'
    output: 'trivy-results.sarif'
```

### 6. Build Docker Image

```yaml
- name: Set up Docker Buildx
  uses: docker/setup-buildx-action@v2

- name: Login to Docker Hub
  uses: docker/login-action@v2
  with:
    username: ${{ secrets.DOCKER_USERNAME }}
    password: ${{ secrets.DOCKER_PASSWORD }}

- name: Build and push
  uses: docker/build-push-action@v4
  with:
    context: .
    push: true
    tags: |
      ${{ secrets.DOCKER_USERNAME }}/product-service:latest
      ${{ secrets.DOCKER_USERNAME }}/product-service:${{ github.sha }}
    cache-from: type=gha
    cache-to: type=gha,mode=max
```

### 7. Deploy to Kubernetes

```yaml
- name: Deploy to Kubernetes
  uses: azure/k8s-deploy@v4
  with:
    manifests: |
      k8s/deployment.yaml
      k8s/service.yaml
    images: |
      ${{ secrets.DOCKER_USERNAME }}/product-service:${{ github.sha }}
    kubectl-version: 'latest'
```

---

## 🔐 Secrets Management

### GitHub Secrets

```bash
# Add secrets via GitHub UI:
# Settings → Secrets → Actions → New repository secret

# Required secrets:
DOCKER_USERNAME
DOCKER_PASSWORD
KUBECONFIG  # Base64 encoded kubeconfig
SONAR_TOKEN
```

### Using Secrets in Workflow

```yaml
env:
  DOCKER_USERNAME: ${{ secrets.DOCKER_USERNAME }}
  DOCKER_PASSWORD: ${{ secrets.DOCKER_PASSWORD }}
```

---

## 🎯 Best Practices

### 1. Keep Pipelines Fast

```yaml
# Use caching
- uses: actions/cache@v3
  with:
    path: ~/.m2/repository
    key: ${{ runner.os }}-maven-${{ hashFiles('**/pom.xml') }}

# Run tests in parallel
jobs:
  test:
    strategy:
      matrix:
        test-suite: [unit, integration, e2e]
```

### 2. Fail Fast

```yaml
# Stop on first failure
jobs:
  build:
    steps:
      - name: Build
        run: mvn clean package || exit 1
```

### 3. Use Matrix Builds

```yaml
jobs:
  test:
    strategy:
      matrix:
        java-version: [11, 17, 21]
        os: [ubuntu-latest, windows-latest]
```

### 4. Add Status Badges

```markdown
![CI/CD](https://github.com/username/repo/workflows/CI%2FCD/badge.svg)
```

### 5. Implement Approval Gates

```yaml
environment:
  name: production
  # Requires manual approval in GitHub
```

---

## 📚 GitOps

### What is GitOps?

```
Git as Single Source of Truth
     ↓
Declarative Infrastructure
     ↓
Automated Deployment
     ↓
Self-Healing Systems
```

### GitOps Tools

- **ArgoCD** - Kubernetes native
- **Flux** - CNCF project
- **Jenkins X** - Cloud native CI/CD

### ArgoCD Example

```yaml
apiVersion: argoproj.io/v1alpha1
kind: Application
metadata:
  name: product-service
spec:
  project: default
  source:
    repoURL: https://github.com/username/repo
    targetRevision: HEAD
    path: k8s
  destination:
    server: https://kubernetes.default.svc
    namespace: product
  syncPolicy:
    automated:
      prune: true
      selfHeal: true
```

---

## 🧪 Testing in CI/CD

### Test Pyramid

```
        ┌──────────┐
       /    E2E     \
      /   (Slow)     \
     ┌────────────────┐
    /   Integration   \
   /    (Medium)       \
  ┌──────────────────────┐
 /       Unit Tests       \
/      (Fast, Many)        \
────────────────────────────
```

### Test Stages

```yaml
jobs:
  unit-tests:
    runs-on: ubuntu-latest
    steps:
      - run: mvn test
  
  integration-tests:
    needs: unit-tests
    runs-on: ubuntu-latest
    services:
      postgres:
        image: postgres:15
    steps:
      - run: mvn verify -P integration
  
  e2e-tests:
    needs: [unit-tests, integration-tests]
    runs-on: ubuntu-latest
    steps:
      - run: mvn verify -P e2e
```

---

## 📊 Monitoring Pipelines

### Metrics to Track

- **Build time** - Target: < 10 minutes
- **Test pass rate** - Target: > 95%
- **Deployment frequency** - How often?
- **Mean time to recovery** - How fast to fix?
- **Change failure rate** - % of failed deployments

### Notifications

```yaml
- name: Slack Notification
  uses: 8398a7/action-slack@v3
  with:
    status: ${{ job.status }}
    text: 'Build ${{ job.status }}'
    webhook_url: ${{ secrets.SLACK_WEBHOOK }}
  if: always()
```

---

## 🎓 Demo Projects

### 1. GitHub Actions Pipeline (`demo-github-actions/`)
- Complete CI/CD workflow
- Build, test, security scan
- Docker build and push
- Kubernetes deployment

### 2. GitOps with ArgoCD (`demo-gitops/`)
- Git-based deployments
- Automated sync
- Self-healing applications

---

## 📚 Key Takeaways

1. ✅ **Automate everything** - Build, test, deploy
2. ✅ **Test in pipeline** - Catch issues early
3. ✅ **Security scanning** - Vulnerabilities and secrets
4. ✅ **Fast feedback** - Keep pipelines < 10 min
5. ✅ **Deployment automation** - Reduce manual errors
6. ✅ **Monitor pipelines** - Track metrics
7. ✅ **GitOps for production** - Declarative deployments

---

## 🔗 Resources

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Jenkins Documentation](https://www.jenkins.io/doc/)
- [Argo CD Documentation](https://argo-cd.readthedocs.io/)
- [GitOps Principles](https://www.gitops.tech/)

---

_"The key is to automate the boring stuff." - DevOps Principle_
