$ErrorActionPreference = 'Stop'
# Theory: traffic switch is controlled by changing the Service selector.
kubectl patch service prod-app -n blue-green-demo --type='merge' -p '{"spec":{"selector":{"app":"prod-app","version":"green"}}}'
kubectl get service prod-app -n blue-green-demo -o yaml | Select-String "version:"
