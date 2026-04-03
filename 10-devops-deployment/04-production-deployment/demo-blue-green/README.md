# Blue-Green Deployment Demo

This demo shows zero-downtime rollout using two live environments (blue and green) and a controlled traffic switch via Service selector.

## Learning Objectives

- Understand blue-green release and rollback flow
- Perform explicit traffic cutover with minimal downtime
- Validate rollback safety with one command

## Theory Checkpoints

1. Blue serves current stable traffic.
2. Green is pre-warmed and validated before cutover.
3. Service selector flip is the release and rollback switch.

## Project Structure

```text
demo-blue-green/
  k8s/
    namespace.yaml
    blue-deployment.yaml
    green-deployment.yaml
    service.yaml
  scripts/
    switch-to-blue.ps1
    switch-to-green.ps1
```

## Run Steps

```bash
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/blue-deployment.yaml
kubectl apply -f k8s/green-deployment.yaml
kubectl apply -f k8s/service.yaml
```

Switch to green:

```powershell
./scripts/switch-to-green.ps1
```

Rollback to blue:

```powershell
./scripts/switch-to-blue.ps1
```

## Verification Steps

```bash
kubectl get deploy -n blue-green-demo
kubectl get svc prod-app -n blue-green-demo -o yaml
kubectl get endpoints prod-app -n blue-green-demo
```

## Expected Outcome

- Both blue and green deployments remain healthy
- Service selector determines which version receives traffic
- Rollback is immediate by selector switch

## Hands-on Lab

1. Scale green replicas and measure readiness time before switch.
2. Add readiness/liveness probes and block switch until healthy.
3. Add CI pipeline gate that requires approval before running switch script.
