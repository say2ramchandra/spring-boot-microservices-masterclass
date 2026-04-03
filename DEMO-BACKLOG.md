# Demo Backlog (Post README Audit)

Date: 2026-04-03

## Summary

- README `demo-*` folder reference audit: **0 missing**.
- Self-referencing placeholder audit: **8 items** found across 3 modules (README links pointing back to themselves instead of actual demo folders).
- Implementation progress: **13 of 13** backlog demos are now completed.

## Tracked Runnable Demos

These items were tracked from documentation gaps and are now implemented with dedicated demo folders.

### Self-Referencing Placeholder Demos

Items listed in "Demo Projects" sections that link to `README.md` instead of an actual demo folder.

| Module | Missing Demo | Current State |
|--------|-------------|---------------|
| 01-core-java-fundamentals/02-streams-and-lambdas | Lambda basics | Completed: `demo-lambda-basics` created |
| 02-spring-core/01-dependency-injection | Setter injection | Completed: `demo-setter-injection` created |
| 02-spring-core/01-dependency-injection | Field injection (anti-pattern) | Completed: `demo-field-injection` created |
| 02-spring-core/01-dependency-injection | Qualifier and Primary | Completed: `demo-qualifier-primary` created |
| 02-spring-core/01-dependency-injection | Complete DI guide | Completed: `demo-complete-di-guide` created |
| 02-spring-core/02-bean-lifecycle | Bean scopes | Completed: `demo-bean-scopes` created |
| 02-spring-core/02-bean-lifecycle | Lifecycle callbacks | Completed: `demo-lifecycle-callbacks` created |
| 02-spring-core/02-bean-lifecycle | BeanPostProcessor | Completed: `demo-bean-post-processor` created |

### Previously Planned Demos (Now Completed)

| Module | Topic | Current State |
|--------|-------|---------------|
| 08-testing | Contract Testing runnable sample | Completed: `03-contract-testing/demo-contract-testing` created |
| 10-devops-deployment/03-ci-cd | GitOps with ArgoCD | Completed: `03-ci-cd/demo-gitops-argocd` created |
| 10-devops-deployment/04-production-deployment | Blue-Green deployment | Completed: `04-production-deployment/demo-blue-green` created |
| 10-devops-deployment/04-production-deployment | Canary release | Completed: `04-production-deployment/demo-canary-release` created |
| 10-devops-deployment/04-production-deployment | Production monitoring | Completed: `04-production-deployment/demo-production-monitoring` created |

## Analysis Gap (Fixed)

The original audit only scanned for `demo-*` folder name patterns via regex. Items 2–5 in the
DI module (and similar entries in other modules) were missed because they use descriptive names
linking back to `README.md` rather than `demo-*` directory names. This second pass used a
section-aware scan to catch all self-referencing placeholder entries.

## Notes

- `03-spring-boot-fundamentals` quick start now points to `demo-rest-api`.
- Placeholder wording like "Demo Project" in topic READMEs was normalized to "Hands-on Demo" to prevent false-positive demo-name scans.
- Security docs now reference `demo-oauth2` (existing) instead of outdated naming.
- Implemented in this pass: `demo-lambda-basics`, `demo-setter-injection`, `demo-field-injection`.
