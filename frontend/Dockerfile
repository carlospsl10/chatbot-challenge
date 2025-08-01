# Multi-stage build for React application
FROM node:18-alpine AS build

# Set working directory
WORKDIR /app

# Copy package files
COPY package*.json ./

# Install dependencies
RUN npm ci --only=production

# Copy source code
COPY . .

# Build the app with environment-specific settings
ARG REACT_APP_ENV=production
ENV REACT_APP_ENV=$REACT_APP_ENV

RUN npm run build

# Production stage
FROM nginx:alpine

# Copy built application from build stage
COPY --from=build /app/build /usr/share/nginx/html

# Copy nginx config files
COPY nginx.dev.conf /tmp/nginx.dev.conf
COPY nginx.prod.conf /tmp/nginx.prod.conf

# Copy appropriate nginx config based on environment
ARG REACT_APP_ENV=production
# Map environment to nginx config file names
RUN if [ "$REACT_APP_ENV" = "development" ]; then \
        cp /tmp/nginx.dev.conf /etc/nginx/nginx.conf; \
    else \
        cp /tmp/nginx.prod.conf /etc/nginx/nginx.conf; \
    fi

# Expose port
EXPOSE 80

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=30s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:80/ || exit 1

# Start nginx
CMD ["nginx", "-g", "daemon off;"] 