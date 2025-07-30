# MVP Architecture: AI-Powered Order Status Chatbot

## Executive Summary

This document outlines a simplified MVP architecture for an AI-powered chatbot focused on order status inquiries. The system consists of a web application frontend and a Spring Boot backend, with authentication and session management for secure customer interactions.

## System Overview

The MVP is designed as a simple two-tier architecture:
- **Frontend**: React web application for customer interactions
- **Backend**: Spring Boot application with REST APIs and chatbot logic
- **Database**: PostgreSQL for storing orders, customers, and conversation data
- **Authentication**: JWT-based authentication with session management

## Core Architecture Components

### 1. Frontend Layer (React Web Application)
- **React Application**: Single-page application for customer chat interface
- **WebSocket Connection**: Real-time communication with backend
- **Authentication UI**: Login/logout functionality
- **Chat Interface**: Customer-friendly chat widget
- **Responsive Design**: Works on desktop and mobile browsers

### 2. Backend Layer (Spring Boot Application)
- **REST APIs**: Order status, authentication, and chat endpoints
- **WebSocket Server**: Real-time chat communication
- **Authentication Service**: JWT token management and session handling
- **Chatbot Service**: Natural language processing and response generation
- **Order Service**: Business logic for order information retrieval
- **Customer Service**: Customer profile and session management

### 3. Data Layer
- **PostgreSQL Database**: Stores orders, customers, and conversation logs
- **Redis Cache**: Session storage and response caching
- **File Storage**: Static assets and chat logs

## Technology Stack

### Frontend
- **React**: UI framework with TypeScript
- **Socket.io-client**: Real-time communication
- **Axios**: HTTP client for API calls
- **Material-UI**: Component library for rapid development
- **React Router**: Client-side routing

### Backend
- **Spring Boot**: Main application framework
- **Spring Security**: Authentication and authorization
- **Spring WebSocket**: Real-time communication
- **Spring Data JPA**: Database access layer
- **Spring Boot Starter Web**: REST API support
- **JWT**: Token-based authentication
- **Redis**: Session and cache management

### Database
- **PostgreSQL**: Primary database for orders, customers, conversations
- **Redis**: Session storage and caching

### AI/ML (Simple Approach)
- **OpenAI API**: GPT integration for natural language processing
- **Simple Intent Recognition**: Pattern matching for common queries
- **Response Templates**: Pre-defined responses for order status

## System Interactions and Data Flow

### 1. Customer Authentication Flow
```
Customer → React App → Login Form → Spring Security → JWT Token → Session Storage
```

### 2. Order Status Query Flow
```
Customer → Chat Interface → WebSocket → Spring Boot → Intent Recognition → Order Service → Database → Response Generation → Customer
```

### 3. Session Management Flow
```
Customer Request → JWT Validation → Session Check → Redis → Continue/Re-authenticate
```

## Database Schema (Simplified)

### Orders Table
```sql
CREATE TABLE orders (
    id BIGINT PRIMARY KEY,
    order_number VARCHAR(50) UNIQUE,
    customer_id BIGINT,
    status VARCHAR(50),
    created_date TIMESTAMP,
    updated_date TIMESTAMP,
    total_amount DECIMAL(10,2),
    shipping_address TEXT
);
```

### Customers Table
```sql
CREATE TABLE customers (
    id BIGINT PRIMARY KEY,
    email VARCHAR(255) UNIQUE,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    phone VARCHAR(20),
    created_date TIMESTAMP
);
```

### Conversations Table
```sql
CREATE TABLE conversations (
    id BIGINT PRIMARY KEY,
    customer_id BIGINT,
    session_id VARCHAR(255),
    message TEXT,
    response TEXT,
    timestamp TIMESTAMP,
    intent VARCHAR(100),
    confidence DECIMAL(3,2)
);
```

### Sessions Table
```sql
CREATE TABLE sessions (
    id VARCHAR(255) PRIMARY KEY,
    customer_id BIGINT,
    created_date TIMESTAMP,
    last_activity TIMESTAMP,
    is_active BOOLEAN
);
```

## Spring Boot Project Structure

### Dependencies (pom.xml)
```xml
<dependencies>
    <!-- Spring Boot Starters -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-websocket</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>
    
    <!-- Database -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
    </dependency>
    
    <!-- JWT -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.11.5</version>
    </dependency>
    
    <!-- OpenAI Client -->
    <dependency>
        <groupId>com.theokanning.openai-gpt3-java</groupId>
        <artifactId>service</artifactId>
        <version>0.12.0</version>
    </dependency>
    
    <!-- Utilities -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
</dependencies>
```

### Package Structure
```
src/main/java/com/chatbot/
├── ChatbotApplication.java
├── config/
│   ├── SecurityConfig.java
│   ├── WebSocketConfig.java
│   └── RedisConfig.java
├── controller/
│   ├── AuthController.java
│   ├── ChatController.java
│   ├── OrderController.java
│   └── WebSocketController.java
├── service/
│   ├── AuthService.java
│   ├── ChatbotService.java
│   ├── OrderService.java
│   ├── CustomerService.java
│   └── OpenAIService.java
├── model/
│   ├── Order.java
│   ├── Customer.java
│   ├── Conversation.java
│   ├── Session.java
│   └── ChatMessage.java
├── repository/
│   ├── OrderRepository.java
│   ├── CustomerRepository.java
│   ├── ConversationRepository.java
│   └── SessionRepository.java
└── dto/
    ├── LoginRequest.java
    ├── LoginResponse.java
    ├── ChatRequest.java
    └── ChatResponse.java
```

## API Endpoints

### Authentication
- `POST /api/auth/login` - Customer login
- `POST /api/auth/logout` - Customer logout
- `GET /api/auth/me` - Get current customer info

### Chat
- `POST /api/chat/message` - Send chat message
- `GET /api/chat/history` - Get conversation history

### Orders
- `GET /api/orders/{orderNumber}` - Get order status
- `GET /api/orders/customer/{customerId}` - Get customer orders

### WebSocket
- `/ws/chat` - WebSocket endpoint for real-time chat

## Security Implementation

### Authentication Flow
1. **Customer Login**: Email/password authentication
2. **JWT Generation**: Secure token with customer information
3. **Session Management**: Redis-based session storage
4. **Token Validation**: Middleware for protected endpoints
5. **Session Expiry**: Automatic session cleanup

### Security Features
- **Password Encryption**: BCrypt password hashing
- **JWT Tokens**: Stateless authentication
- **CORS Configuration**: Cross-origin resource sharing
- **Input Validation**: Request parameter validation
- **SQL Injection Prevention**: JPA parameterized queries

## Chatbot Logic (Simplified)

### Intent Recognition
```java
// Simple pattern matching for common queries
public class IntentRecognition {
    public String recognizeIntent(String message) {
        String lowerMessage = message.toLowerCase();
        
        if (lowerMessage.contains("order status") || lowerMessage.contains("where is my order")) {
            return "ORDER_STATUS";
        }
        if (lowerMessage.contains("track") || lowerMessage.contains("shipping")) {
            return "TRACKING";
        }
        if (lowerMessage.contains("cancel") || lowerMessage.contains("refund")) {
            return "CANCELLATION";
        }
        
        return "GENERAL_INQUIRY";
    }
}
```

### Response Generation
```java
// Template-based responses with OpenAI enhancement
public class ResponseGenerator {
    public String generateResponse(String intent, String orderNumber, Order order) {
        switch (intent) {
            case "ORDER_STATUS":
                return generateOrderStatusResponse(order);
            case "TRACKING":
                return generateTrackingResponse(order);
            default:
                return generateGeneralResponse();
        }
    }
}
```

## Docker Configuration

### Backend Dockerfile
```dockerfile
FROM openjdk:11-jre-slim
COPY target/chatbot-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Frontend Dockerfile
```dockerfile
FROM node:16-alpine
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build
FROM nginx:alpine
COPY --from=0 /app/build /usr/share/nginx/html
EXPOSE 80
```

### Docker Compose
```yaml
version: '3.8'
services:
  backend:
    build: ./backend
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
      - DATABASE_URL=jdbc:postgresql://postgres:5432/chatbot
      - REDIS_URL=redis://redis:6379
    depends_on:
      - postgres
      - redis

  frontend:
    build: ./frontend
    ports:
      - "3000:80"
    depends_on:
      - backend

  postgres:
    image: postgres:13
    environment:
      POSTGRES_DB: chatbot
      POSTGRES_USER: chatbot
      POSTGRES_PASSWORD: password
    volumes:
      - postgres_data:/var/lib/postgresql/data
    ports:
      - "5432:5432"

  redis:
    image: redis:6-alpine
    ports:
      - "6379:6379"

volumes:
  postgres_data:
```

## MVP Features

### Phase 1: Basic Functionality (2-3 weeks)
- [ ] Customer authentication and session management
- [ ] Basic chat interface with WebSocket communication
- [ ] Simple order status queries
- [ ] Pre-defined response templates
- [ ] Basic conversation logging

### Phase 2: Enhanced Chatbot (2-3 weeks)
- [ ] OpenAI GPT integration for natural responses
- [ ] Intent recognition for common queries
- [ ] Order tracking and shipping information
- [ ] Conversation history and context
- [ ] Basic error handling and fallbacks

### Phase 3: Polish and Testing (1-2 weeks)
- [ ] UI/UX improvements
- [ ] Performance optimization
- [ ] Comprehensive testing
- [ ] Documentation and deployment

## Success Criteria

### Technical Metrics
- **Response Time**: < 3 seconds for 90% of chat messages
- **Uptime**: 95% availability during development
- **Authentication**: Secure login/logout functionality
- **WebSocket**: Stable real-time communication

### Business Metrics
- **Order Status Accuracy**: 95% correct order information
- **Customer Satisfaction**: Basic usability testing
- **Conversation Flow**: Smooth chat experience
- **Error Handling**: Graceful fallbacks for unknown queries

## Development Timeline

### Week 1-2: Foundation
- Set up Spring Boot project with dependencies
- Implement authentication and session management
- Create basic database schema
- Set up Docker environment

### Week 3-4: Core Features
- Implement chat WebSocket functionality
- Create order status service
- Build basic chatbot logic
- Develop React frontend

### Week 5-6: Integration
- Connect frontend and backend
- Implement OpenAI integration
- Add conversation logging
- Basic testing and debugging

### Week 7-8: Polish
- UI/UX improvements
- Performance optimization
- Comprehensive testing
- Documentation and deployment

## Risk Mitigation

### Technical Risks
- **WebSocket Stability**: Implement reconnection logic
- **Database Performance**: Add proper indexing
- **Authentication Security**: Regular security reviews
- **API Rate Limits**: Implement rate limiting for OpenAI

### Business Risks
- **User Experience**: Regular usability testing
- **Data Accuracy**: Validate order information
- **Response Quality**: Monitor chatbot responses
- **Scalability**: Plan for future growth

This MVP architecture provides a solid foundation for demonstrating the chatbot functionality while keeping the implementation simple and focused on core features. 