# Production Deployment Patterns

This section contains practical deployment strategy demos for safe production rollouts.

## Demos

1. `demo-blue-green` - switch traffic between blue and green versions
2. `demo-canary-release` - gradually shift traffic to canary version
3. `demo-production-monitoring` - baseline monitoring stack with Prometheus and Grafana

## Suggested Learning Order

1. Blue-Green
2. Canary
3. Monitoring

## Prerequisites

- Kubernetes cluster (local or cloud)
- kubectl access
- NGINX Ingress controller (for canary demo)
- Docker (for monitoring demo with compose)
