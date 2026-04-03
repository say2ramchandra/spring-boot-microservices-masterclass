$ErrorActionPreference = 'Stop'
# Rollback path: point service selector back to blue deployment.
kubectl patch service prod-app -n blue-green-demo --type='merge' -p '{"spec":{"selector":{"app":"prod-app","version":"blue"}}}'
kubectl get service prod-app -n blue-green-demo -o yaml | Select-String "version:"
