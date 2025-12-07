# Kubernetes Deployment Guide

This directory contains Kubernetes manifests for deploying the Todo Application to a Kubernetes cluster.

## Prerequisites

- Kubernetes cluster (minikube, kind, or cloud provider)
- kubectl configured to access your cluster
- Docker for building the image

## Quick Start

### 1. Build the Docker Image

```bash
docker build -t todo-app:latest .
```

If using minikube, load the image:
```bash
minikube image load todo-app:latest
```

### 2. Deploy to Kubernetes

Deploy all resources:
```bash
kubectl apply -f k8s/
```

Or deploy individually:
```bash
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
kubectl apply -f k8s/ingress.yaml  # Optional
```

### 3. Verify Deployment

Check pods:
```bash
kubectl get pods -l app=todo-app
```

Check services:
```bash
kubectl get svc
```

View logs:
```bash
kubectl logs -l app=todo-app -f
```

## Accessing the Application

### Option 1: NodePort (Default)

Get the NodePort:
```bash
kubectl get svc todo-app-nodeport
```

If using minikube:
```bash
minikube service todo-app-nodeport --url
```

Access the application at the provided URL.

### Option 2: Port Forward

Forward local port to the service:
```bash
kubectl port-forward svc/todo-app-service 8080:80
```

Access at: http://localhost:8080

### Option 3: Ingress

If you have an Ingress controller (like nginx-ingress) installed:

```bash
kubectl apply -f k8s/ingress.yaml
```

Add to `/etc/hosts`:
```
<ingress-ip> todo-app.local
```

Access at: http://todo-app.local

## Manifest Files

### deployment.yaml
- **Deployment**: Runs 2 replicas of the Todo app
- **PersistentVolumeClaim**: 1Gi storage for SQLite database
- Resource limits and requests configured
- Health checks (liveness and readiness probes)
- Volume mounts for logs and data

### service.yaml
- **ClusterIP Service**: Internal cluster access on port 80
- **NodePort Service**: External access on port 30080
- Load balancing across pod replicas

### configmap.yaml
- Application configuration (application.yaml)
- Logging configuration
- Database path configuration
- Spring Boot settings

### ingress.yaml
- Nginx Ingress configuration
- Host-based routing (todo-app.local)
- Path-based routing for API and Swagger UI

## Configuration

### Environment Variables

The deployment uses these environment variables:
- `SPRING_PROFILES_ACTIVE`: Set to "production"
- `JAVA_OPTS`: JVM tuning for containers

### Resource Limits

Default resource configuration:
- **Requests**: 512Mi memory, 250m CPU
- **Limits**: 1Gi memory, 1000m CPU

Adjust in `deployment.yaml` based on your needs.

### Scaling

Scale the deployment:
```bash
kubectl scale deployment todo-app --replicas=3
```

Or use Horizontal Pod Autoscaler:
```bash
kubectl autoscale deployment todo-app --min=2 --max=10 --cpu-percent=80
```

## Persistence

The application uses a PersistentVolumeClaim for the SQLite database:
- **Name**: todo-app-pvc
- **Size**: 1Gi
- **Access Mode**: ReadWriteOnce
- **Storage Class**: standard (adjust for your cluster)

**Note**: With multiple replicas and ReadWriteOnce, only one pod can write to the database. For production, consider using a shared database like PostgreSQL or MySQL.

## Monitoring

### View Logs

All pods:
```bash
kubectl logs -l app=todo-app --tail=100 -f
```

Specific pod:
```bash
kubectl logs <pod-name> -f
```

### Health Checks

The application exposes health endpoints:
- Liveness probe: `/api/todos/health`
- Readiness probe: `/api/todos/health`

### Exec into Pod

```bash
kubectl exec -it <pod-name> -- /bin/bash
```

## Troubleshooting

### Pods not starting

Check pod status:
```bash
kubectl describe pod <pod-name>
```

Check events:
```bash
kubectl get events --sort-by='.lastTimestamp'
```

### Image pull errors

If using local images with minikube:
```bash
minikube image load todo-app:latest
```

Set `imagePullPolicy: IfNotPresent` in deployment.yaml

### Database issues

Check PVC status:
```bash
kubectl get pvc
```

Ensure storage class exists:
```bash
kubectl get storageclass
```

### Service not accessible

Check service endpoints:
```bash
kubectl get endpoints todo-app-service
```

Verify pod labels match service selector:
```bash
kubectl get pods --show-labels
```

## Cleanup

Delete all resources:
```bash
kubectl delete -f k8s/
```

Or delete individually:
```bash
kubectl delete deployment todo-app
kubectl delete service todo-app-service todo-app-nodeport
kubectl delete configmap todo-app-config
kubectl delete pvc todo-app-pvc
kubectl delete ingress todo-app-ingress
```

## Production Considerations

1. **Database**: Replace SQLite with a production database (PostgreSQL, MySQL)
2. **Secrets**: Use Kubernetes Secrets for sensitive configuration
3. **Ingress**: Configure TLS/SSL certificates
4. **Monitoring**: Add Prometheus metrics and Grafana dashboards
5. **Logging**: Configure log aggregation (ELK stack, Fluentd)
6. **Backup**: Implement database backup strategy
7. **Security**: Run as non-root user (already configured)
8. **Network Policies**: Add network policies for pod-to-pod communication
9. **Resource Limits**: Tune based on actual usage
10. **High Availability**: Use pod disruption budgets and affinity rules

## Advanced Configuration

### Using Different Storage Classes

Edit `deployment.yaml`:
```yaml
storageClassName: fast-ssd  # or your storage class
```

### Adding Annotations for Monitoring

Add to deployment metadata:
```yaml
annotations:
  prometheus.io/scrape: "true"
  prometheus.io/port: "8080"
  prometheus.io/path: "/actuator/prometheus"
```

### Setting Pod Affinity

Add to deployment spec.template.spec:
```yaml
affinity:
  podAntiAffinity:
    preferredDuringSchedulingIgnoredDuringExecution:
    - weight: 100
      podAffinityTerm:
        labelSelector:
          matchExpressions:
          - key: app
            operator: In
            values:
            - todo-app
        topologyKey: kubernetes.io/hostname
```

## CI/CD Integration

### GitHub Actions Example

```yaml
- name: Build and push Docker image
  run: |
    docker build -t todo-app:${{ github.sha }} .
    docker push your-registry/todo-app:${{ github.sha }}

- name: Deploy to Kubernetes
  run: |
    kubectl set image deployment/todo-app \
      todo-app=your-registry/todo-app:${{ github.sha }}
```

## Support

For issues or questions, refer to the main README.md or open an issue.
