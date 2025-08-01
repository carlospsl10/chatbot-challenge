# Frontend Environment Configuration

This document explains how to configure the frontend application for different environments (development and production).

## Environment Files

### Development Environment (`.env.development`)
```bash
REACT_APP_API_URL=http://localhost:8080
REACT_APP_ENV=development
REACT_APP_USE_PROXY=true
REACT_APP_DEBUG=true
REACT_APP_LOG_LEVEL=debug
REACT_APP_ENABLE_HOT_RELOAD=true
REACT_APP_BACKEND_URL=http://backend:8080
REACT_APP_NGINX_CONFIG=dev
```

### Production Environment (`.env.production`)
```bash
REACT_APP_API_URL=https://chatbot-challenge.railway.internal
REACT_APP_ENV=production
REACT_APP_USE_PROXY=false
REACT_APP_DEBUG=false
REACT_APP_LOG_LEVEL=error
REACT_APP_ENABLE_HOT_RELOAD=false
REACT_APP_BACKEND_URL=https://chatbot-challenge.railway.internal
REACT_APP_NGINX_CONFIG=prod
```

## Nginx Configurations

### Development (`nginx.dev.conf`)
- Proxies API requests to `http://backend:8080` (Docker service)
- No caching for easier debugging
- Less restrictive security headers
- Development mode headers

### Production (`nginx.prod.conf`)
- Proxies API requests to `https://chatbot-challenge.railway.internal`
- Aggressive caching for static assets
- Strict security headers
- Production mode headers

## Available Scripts

### Development
```bash
# Start development server
npm start

# Build for development
npm run build:dev

# Build Docker for development
npm run docker:dev
```

### Production
```bash
# Build for production
npm run build:prod

# Build Docker for production
npm run docker:prod
```

## Environment Configuration Service

The application uses a centralized configuration service (`src/config/environment.ts`) that:

- Reads environment variables
- Provides type-safe access to configuration
- Logs configuration in debug mode
- Handles fallbacks for missing variables

### Usage Example
```typescript
import environment from '../config/environment';

// Get API URL
const apiUrl = environment.getApiUrl();

// Check environment
if (environment.isDevelopment()) {
  console.log('Running in development mode');
}

// Log configuration (only in debug mode)
environment.logConfig();
```

## Docker Build

The Dockerfile supports environment-specific builds:

```bash
# Development build
docker build --build-arg REACT_APP_ENV=development -t chatbot-frontend:dev .

# Production build
docker build --build-arg REACT_APP_ENV=production -t chatbot-frontend:prod .
```

## Environment Variables Reference

| Variable | Description | Development | Production |
|----------|-------------|-------------|------------|
| `REACT_APP_API_URL` | Backend API URL | `http://localhost:8080` | `https://chatbot-challenge.railway.internal` |
| `REACT_APP_ENV` | Environment name | `development` | `production` |
| `REACT_APP_USE_PROXY` | Use nginx proxy | `true` | `false` |
| `REACT_APP_DEBUG` | Enable debug mode | `true` | `false` |
| `REACT_APP_LOG_LEVEL` | Logging level | `debug` | `error` |
| `REACT_APP_ENABLE_HOT_RELOAD` | Enable hot reload | `true` | `false` |
| `REACT_APP_BACKEND_URL` | Backend service URL | `http://backend:8080` | `https://chatbot-challenge.railway.internal` |
| `REACT_APP_NGINX_CONFIG` | Nginx config type | `dev` | `prod` |

## Deployment

### Local Development
1. Copy `.env.development` to `.env.local` (if needed)
2. Run `npm start`
3. Frontend will proxy API requests to local backend

### Production Deployment
1. Set environment variables in your deployment platform
2. Build with `npm run build:prod`
3. Deploy with Docker using `npm run docker:prod`
4. Frontend will connect directly to deployed backend

## Troubleshooting

### Common Issues

1. **API Connection Errors**
   - Check `REACT_APP_API_URL` is correct
   - Verify backend is running and accessible
   - Check CORS settings on backend

2. **Build Errors**
   - Ensure all environment variables are set
   - Check for missing dependencies
   - Verify Node.js version compatibility

3. **Docker Build Failures**
   - Ensure nginx config files exist
   - Check Dockerfile syntax
   - Verify build arguments are passed correctly

### Debug Mode
Enable debug mode to see configuration details:
```bash
REACT_APP_DEBUG=true npm start
```

This will log the current environment configuration to the console. 