# GitHub Actions CI/CD Demo

> **Complete CI/CD pipeline with GitHub Actions**

## 📚 Overview

This demo provides a production-ready GitHub Actions workflow that automates the entire software delivery lifecycle from code commit to production deployment.

---

## 🔄 Pipeline Stages

```
1. Build & Test       →  Compile, unit tests, integration tests
2. Code Quality       →  SonarQube analysis
3. Security Scan      →  Trivy, OWASP dependency check
4. Build Docker       →  Multi-stage Docker build + push
5. Deploy Staging     →  Auto-deploy to staging environment
6. Deploy Production  →  Manual approval + deploy to production
7. Notifications      →  Slack/Email notifications
```

---

## 🚀 Quick Setup

### Step 1: Create Secrets

Add these secrets in GitHub repository settings:

```bash
# Docker Registry
DOCKER_USERNAME=your-docker-username
DOCKER_PASSWORD=your-docker-password

# Kubernetes
KUBE_CONFIG_STAGING=<base64-encoded-kubeconfig>
KUBE_CONFIG_PRODUCTION=<base64-encoded-kubeconfig>

# Code Quality (Optional)
SONAR_TOKEN=your-sonar-token
SONAR_HOST_URL=https://sonarcloud.io

# Notifications (Optional)
SLACK_WEBHOOK=https://hooks.slack.com/services/xxx
```

### Step 2: Copy Workflow File

```bash
# Copy workflow to your repository
mkdir -p .github/workflows
cp ci-cd.yml .github/workflows/
```

### Step 3: Configure Environments

1. Go to Settings → Environments
2. Create environments: `staging` and `production`
3. Add protection rules for production (require approval)
4. Add environment-specific URLs

### Step 4: Push and Watch

```bash
git add .github/workflows/ci-cd.yml
git commit -m "Add CI/CD pipeline"
git push origin main
```

Visit Actions tab to see pipeline in action!

---

## 📝 Workflow Breakdown

### Trigger Events

```yaml
on:
  push:
    branches: [ main, develop ]  # Auto-trigger on push
  pull_request:
    branches: [ main ]           # Run on PRs
  workflow_dispatch:             # Manual trigger
```

### Job Dependencies

```
build-and-test
    ├─→ code-quality
    ├─→ security-scan
    └─→ build-docker
            ├─→ deploy-staging (develop branch)
            └─→ deploy-production (main branch)
                    └─→ notify
```

---

## 🎯 Key Features

### 1. Parallel Execution

```yaml
jobs:
  code-quality:
    needs: build-and-test  # Runs after build
  security-scan:
    needs: build-and-test  # Runs in parallel with code-quality
```

### 2. Conditional Deployment

```yaml
if: github.ref == 'refs/heads/main'  # Only on main branch
```

### 3. Manual Approval

```yaml
environment:
  name: production  # Requires approval in GitHub settings
```

### 4. Automatic Rollback

```yaml
- name: Rollback on Failure
  if: failure()
  run: kubectl rollout undo deployment/product-service
```

### 5. Artifact Caching

```yaml
- uses: actions/cache@v3
  with:
    path: ~/.m2/repository
    key: ${{ runner.os }}-maven-${{ hashFiles('**/pom.xml') }}
```

---

## 🔐 Security Features

### 1. Vulnerability Scanning

```yaml
- name: Run Trivy vulnerability scanner
  uses: aquasecurity/trivy-action@master
  with:
    scan-type: 'fs'
    severity: 'CRITICAL,HIGH'
```

### 2. Dependency Checking

```yaml
- name: OWASP Dependency Check
  uses: dependency-check/Dependency-Check_Action@main
```

### 3. Secret Scanning

```yaml
# GitHub automatically scans for exposed secrets
# Configure in Settings → Security → Code security and analysis
```

### 4. SARIF Upload

```yaml
- name: Upload to GitHub Security
  uses: github/codeql-action/upload-sarif@v2
  with:
    sarif_file: 'trivy-results.sarif'
```

---

## 📊 Monitoring & Notifications

### Status Badge

Add to README.md:

```markdown
![CI/CD](https://github.com/username/repo/workflows/Spring%20Boot%20CI%2FCD%20Pipeline/badge.svg)
```

### Slack Notifications

```yaml
- name: Slack Notification
  uses: 8398a7/action-slack@v3
  with:
    status: ${{ job.status }}
    webhook_url: ${{ secrets.SLACK_WEBHOOK }}
```

---

## 🧪 Testing the Pipeline

### Test on Feature Branch

```bash
git checkout -b feature/new-feature
# Make changes
git add .
git commit -m "Add new feature"
git push origin feature/new-feature
```

**Result:** Runs build and test, but no deployment

### Test on Develop Branch

```bash
git checkout develop
git merge feature/new-feature
git push origin develop
```

**Result:** Deploys to staging automatically

### Test on Main Branch

```bash
git checkout main
git merge develop
git push origin main
```

**Result:** Requires approval, then deploys to production

---

##Configure Kubeconfig Secret

```bash
# Get your kubeconfig
cat ~/.kube/config | base64

# Add as secret in GitHub
# Settings → Secrets → Actions → New secret
# Name: KUBE_CONFIG_PRODUCTION
# Value: <base64-encoded-kubeconfig>
```

---

## 🎯 Best Practices Implemented

1. ✅ **Fail Fast** - Build before expensive operations
2. ✅ **Parallel Jobs** - Security scan while doing code quality
3. ✅ **Caching** - Maven dependencies cached
4. ✅ **Artifacts** - JAR uploaded for reuse
5. ✅ **Security** - Multiple security scans
6. ✅ **Approval Gates** - Production requires approval
7. ✅ **Automatic Rollback** - Rollback on deployment failure
8. ✅ **Notifications** - Slack alerts
9. ✅ **Branch Strategy** - Different behavior per branch

---

## 📚 Additional Resources

### GitHub Actions Marketplace

- [Setup Java](https://github.com/marketplace/actions/setup-java-jdk)
- [Docker Build/Push](https://github.com/marketplace/actions/build-and-push-docker-images)
- [Kubectl](https://github.com/marketplace/actions/kubernetes-set-context)
- [Slack Notify](https://github.com/marketplace/actions/action-slack)

### Documentation

- [GitHub Actions Docs](https://docs.github.com/en/actions)
- [Workflow Syntax](https://docs.github.com/en/actions/reference/workflow-syntax-for-github-actions)
- [Security Hardening](https://docs.github.com/en/actions/security-guides/security-hardening-for-github-actions)

---

## 🐛 Troubleshooting

### Pipeline Fails at Build

- Check Java version matches
- Verify pom.xml syntax
- Check Maven repository access

### Docker Push Fails

- Verify Docker credentials
- Check image name format
- Ensure registry is accessible

### Kubernetes Deployment Fails

- Verify kubeconfig is valid
- Check namespace exists
- Verify image pull policy
- Check resource limits

### Approval Not Working

- Verify environment created
- Check protection rules set
- Ensure user has approval rights

---

## 🎓 Key Takeaways

1. ✅ **Automate everything** - From commit to production
2. ✅ **Test thoroughly** - Multiple testing stages
3. ✅ **Security first** - Multiple security scans
4. ✅ **Fast feedback** - Parallel execution
5. ✅ **Safe deployments** - Approval gates + rollback
6. ✅ **Visibility** - Notifications and badges

---

**This pipeline is production-ready and follows industry best practices!** 🎉
