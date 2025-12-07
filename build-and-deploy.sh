#!/bin/bash

# Build and Deploy Script for Todo Application
set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}Todo Application - Build & Deploy${NC}"
echo -e "${GREEN}========================================${NC}"

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo -e "${RED}Error: Docker is not running${NC}"
    exit 1
fi

# Build Docker image
echo -e "\n${YELLOW}Building Docker image...${NC}"
docker build -t todo-app:latest .

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Docker image built successfully${NC}"
else
    echo -e "${RED}✗ Docker build failed${NC}"
    exit 1
fi

# Tag image with version
VERSION=$(date +%Y%m%d-%H%M%S)
docker tag todo-app:latest todo-app:${VERSION}
echo -e "${GREEN}✓ Tagged image as todo-app:${VERSION}${NC}"

# Ask deployment target
echo -e "\n${YELLOW}Select deployment target:${NC}"
echo "1) Docker Compose (local)"
echo "2) Kubernetes (minikube/local)"
echo "3) Build only (skip deployment)"
read -p "Enter choice [1-3]: " choice

case $choice in
    1)
        echo -e "\n${YELLOW}Deploying with Docker Compose...${NC}"
        docker-compose down
        docker-compose up -d
        echo -e "${GREEN}✓ Application deployed with Docker Compose${NC}"
        echo -e "${GREEN}Access at: http://localhost:8080${NC}"
        echo -e "${GREEN}Swagger UI: http://localhost:8080/swagger-ui.html${NC}"
        ;;
    2)
        echo -e "\n${YELLOW}Deploying to Kubernetes...${NC}"

        # Check if kubectl is available
        if ! command -v kubectl &> /dev/null; then
            echo -e "${RED}Error: kubectl not found${NC}"
            exit 1
        fi

        # Load image to minikube if using minikube
        if command -v minikube &> /dev/null; then
            echo -e "${YELLOW}Loading image to minikube...${NC}"
            minikube image load todo-app:latest
        fi

        # Apply Kubernetes manifests
        kubectl apply -f k8s/configmap.yaml
        kubectl apply -f k8s/deployment.yaml
        kubectl apply -f k8s/service.yaml

        # Optionally apply ingress
        read -p "Deploy Ingress? [y/N]: " deploy_ingress
        if [[ $deploy_ingress =~ ^[Yy]$ ]]; then
            kubectl apply -f k8s/ingress.yaml
        fi

        echo -e "${GREEN}✓ Application deployed to Kubernetes${NC}"
        echo -e "${YELLOW}Check status with: kubectl get pods -l app=todo-app${NC}"
        echo -e "${YELLOW}Access via NodePort: kubectl get svc todo-app-nodeport${NC}"
        ;;
    3)
        echo -e "${GREEN}✓ Build complete, skipping deployment${NC}"
        ;;
    *)
        echo -e "${RED}Invalid choice${NC}"
        exit 1
        ;;
esac

echo -e "\n${GREEN}========================================${NC}"
echo -e "${GREEN}Deployment Complete!${NC}"
echo -e "${GREEN}========================================${NC}"
