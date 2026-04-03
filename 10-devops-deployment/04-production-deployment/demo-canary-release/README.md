# Canary Release Demo

This demo shows gradual traffic shifting to a canary version using NGINX Ingress canary annotations.

## Learning Objectives

- Understand progressive delivery with risk control
- Configure and tune canary traffic weights
- Validate rollback strategy when canary behavior degrades

## Theory Checkpoints

1. Stable serves baseline traffic and protects reliability.
2. Canary gets limited traffic for real-world validation.
3. Weight-based ramp-up reduces blast radius.

## Project Structure

```text
demo-canary-release/
  k8s/
    namespace.yaml
    stable-deployment.yaml
    canary-deployment.yaml
    service.yaml
    ingress-base.yaml
    ingress-canary.yaml
```

## Run Steps

```bash
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/stable-deployment.yaml
kubectl apply -f k8s/canary-deployment.yaml
kubectl apply -f k8s/service.yaml
kubectl apply -f k8s/ingress-base.yaml
kubectl apply -f k8s/ingress-canary.yaml
```

## Verification Steps

```bash
kubectl get ingress -n canary-demo
kubectl describe ingress canary-app-canary -n canary-demo
kubectl get pods -n canary-demo
```

## Expected Outcome

- Stable and canary workloads run together
- Ingress canary annotation controls traffic percentage
- Canary weight can be increased or reduced to 0 quickly

## Hands-on Lab

1. Update canary weight from 20 to 50 and compare request distribution.
2. Simulate canary failure and set weight to 0 for immediate rollback.
3. Create staged rollout script for 10→25→50→100 progression.
