# CI/CD Pipeline Documentation

## Overview

This GitHub Actions workflow provides a comprehensive CI/CD pipeline for the AI-Powered Order Status Chatbot project.

## Pipeline Stages

### 1. Setup Environment
- **Purpose**: Initialize the build environment
- **Actions**:
  - Checkout code from repository
  - Setup Java 17 for backend
  - Setup Node.js 18 for frontend
  - Cache Maven packages and Node modules

### 2. Backend Build
- **Purpose**: Build and test the Spring Boot backend
- **Actions**:
  - Compile Java code with Maven
  - Run backend unit and integration tests
  - Cache Maven dependencies

### 3. Frontend Build
- **Purpose**: Build and test the React frontend
- **Actions**:
  - Install Node.js dependencies
  - Build React application
  - Run frontend tests with coverage

### 4. Docker Build & Push
- **Purpose**: Create and push Docker images to Docker Hub
- **Actions**:
  - Build backend Docker image
  - Build frontend Docker image
  - Push images to Docker Hub registry
  - Tag images with commit SHA and latest

### 5. Security Scan
- **Purpose**: Scan for security vulnerabilities
- **Actions**:
  - Run Trivy vulnerability scanner
  - Upload results to GitHub Security tab

### 6. Notification
- **Purpose**: Provide pipeline status feedback
- **Actions**:
  - Display success/failure messages
  - Show Docker image tags

## Configuration

### Environment Variables

The pipeline uses the following environment variables:

- `DOCKER_HUB_USERNAME`: Docker Hub username (carloselpapa10)
- `DOCKER_HUB_TOKEN`: Docker Hub access token (stored as GitHub secret)
- `BACKEND_IMAGE_NAME`: Backend Docker image name (chatbot-backend)
- `FRONTEND_IMAGE_NAME`: Frontend Docker image name (chatbot-frontend)
- `IMAGE_TAG`: Docker image tag (GitHub commit SHA)

### Required GitHub Secrets

1. **DOCKER_HUB_TOKEN**: Docker Hub access token
   - Value: `[YOUR_DOCKER_HUB_TOKEN]`
   - Purpose: Authenticate with Docker Hub for pushing images

### Setup Instructions

1. **Add Docker Hub Token Secret**:
   - Go to your GitHub repository
   - Navigate to Settings > Secrets and variables > Actions
   - Click "New repository secret"
   - Name: `DOCKER_HUB_TOKEN`
   - Value: `[YOUR_DOCKER_HUB_TOKEN]`

2. **Verify Dockerfiles**:
   - Ensure `backend/Dockerfile` exists and is properly configured
   - Ensure `frontend/Dockerfile` exists and is properly configured

3. **Test the Pipeline**:
   - Push changes to the main branch
   - Monitor the Actions tab in GitHub

## Docker Images

The pipeline creates and pushes the following Docker images:

- **Backend**: `carloselpapa10/chatbot-backend:latest` and `carloselpapa10/chatbot-backend:{commit-sha}`
- **Frontend**: `carloselpapa10/chatbot-frontend:latest` and `carloselpapa10/chatbot-frontend:{commit-sha}`

## Triggers

The pipeline is triggered on:
- Push to `main` branch
- Pull requests to `main` branch

## Cache Strategy

The pipeline implements caching for:
- Maven packages (`~/.m2`)
- Node.js modules (`frontend/node_modules`)
- Docker layers (using GitHub Actions cache)

## Security Features

- **Vulnerability Scanning**: Trivy scanner checks for security vulnerabilities
- **Results Upload**: Scan results are uploaded to GitHub Security tab
- **Dependency Caching**: Reduces build time and improves reliability

## Monitoring

- **Success**: Pipeline completes all stages successfully
- **Failure**: Any stage fails, pipeline stops and reports error
- **Notifications**: Status messages displayed in workflow logs

## Troubleshooting

### Common Issues

1. **Docker Hub Authentication Failed**:
   - Verify `DOCKER_HUB_TOKEN` secret is set correctly
   - Check token permissions in Docker Hub

2. **Build Failures**:
   - Check Maven/Node.js dependency issues
   - Verify Dockerfile configurations
   - Review test failures

3. **Cache Issues**:
   - Clear GitHub Actions cache if needed
   - Check cache key configurations

### Debug Steps

1. Check workflow logs in GitHub Actions tab
2. Verify all required secrets are configured
3. Test Docker builds locally
4. Review security scan results

## Performance Optimizations

- **Parallel Jobs**: Backend and frontend builds run in parallel
- **Caching**: Maven, Node.js, and Docker layer caching
- **Buildx**: Uses Docker Buildx for better build performance
- **Conditional Steps**: Security scan and notifications run conditionally 