# Order Status Chatbot MVP

A complete AI-powered chatbot system for order status inquiries, built with React frontend and Spring Boot backend.

## 🚀 Project Overview

This MVP demonstrates a customer service chatbot that allows users to:
- Authenticate securely with JWT tokens
- Chat with an AI assistant in real-time
- Query order status and history
- Get natural language responses powered by OpenAI

## 📁 Project Structure

```
chatbot-challenge/
├── frontend/                 # React TypeScript application
│   ├── src/
│   │   ├── components/
│   │   │   ├── auth/        # Authentication components
│   │   │   ├── chat/        # Chat interface components
│   │   │   └── layout/      # Layout and navigation
│   │   ├── App.tsx          # Main application component
│   │   └── index.tsx        # Application entry point
│   ├── package.json         # Frontend dependencies
│   └── README.md           # Frontend documentation
├── backend/                  # Spring Boot application
│   ├── src/main/java/com/chatbot/
│   │   ├── controller/      # REST API controllers
│   │   ├── service/         # Business logic services
│   │   ├── model/          # JPA entities
│   │   ├── repository/     # Data access layer
│   │   ├── dto/           # Data transfer objects
│   │   └── config/        # Configuration classes
│   ├── pom.xml            # Maven dependencies
│   └── README.md          # Backend documentation
├── architecture_design.md   # System architecture documentation
├── mvp_architecture.md     # MVP-specific architecture
├── mvp_system_diagram.md  # System diagrams
├── technology_recommendations.md # Technology stack details
├── implementation_strategy.md   # Development phases
├── user-stories.md        # User stories and requirements
└── README.md             # This file
```

## 🛠️ Technology Stack

### Frontend
- **React 18** with TypeScript
- **Material-UI** for components
- **React Router** for navigation
- **Socket.io-client** for real-time chat
- **Axios** for HTTP requests

### Backend
- **Spring Boot 3.1.0** with Java 17
- **Spring Security** for authentication
- **Spring Data JPA** for database access
- **Spring WebSocket** for real-time communication
- **PostgreSQL** for data storage
- **Redis** for session management
- **JWT** for token-based authentication
- **OpenAI API** for natural language processing

## 🚀 Quick Start

### Prerequisites
- Node.js 16+ and npm
- Java 17+ and Maven
- PostgreSQL 13+
- Redis 6+

### Frontend Setup
```bash
cd frontend
npm install
npm start
```
Frontend will be available at `http://localhost:3000`

### Backend Setup
```bash
cd backend
mvn clean install
mvn spring-boot:run
```
Backend will be available at `http://localhost:8080`

### Database Setup
```sql
CREATE DATABASE chatbot;
CREATE USER chatbot WITH PASSWORD 'password';
GRANT ALL PRIVILEGES ON DATABASE chatbot TO chatbot;
```

## 📋 Features

### ✅ Implemented
- User authentication with JWT
- Real-time chat interface
- Order status queries
- Responsive web design
- Basic AI integration structure

### 🔄 In Progress
- OpenAI GPT integration
- Advanced intent recognition
- Conversation history
- Order tracking details

### 📅 Planned
- Mobile SDKs
- Advanced analytics
- Multi-language support
- Performance optimization

## 🔗 API Endpoints

### Authentication
- `POST /api/auth/login` - Customer login
- `POST /api/auth/logout` - Customer logout
- `GET /api/auth/me` - Get current user

### Orders
- `GET /api/orders/{orderNumber}` - Get order status
- `GET /api/orders/customer/{customerId}` - Get customer orders
- `GET /api/orders/track/{orderNumber}` - Get tracking info

### Chat
- WebSocket `/ws/chat` - Real-time chat
- `POST /api/chat/message` - Send message
- `GET /api/chat/history` - Get conversation history

## 🎯 User Stories

The application implements these key user stories:

1. **Customer Authentication** - Secure login with JWT tokens
2. **Real-time Chat** - WebSocket-based AI chat interface
3. **Order Status Inquiry** - Natural language order queries
4. **Customer Order History** - View all customer orders
5. **Session Management** - Secure logout functionality

## 🏗️ Architecture

### System Design
- **Frontend**: React SPA with Material-UI
- **Backend**: Spring Boot microservice
- **Database**: PostgreSQL for data, Redis for sessions
- **AI**: OpenAI GPT for natural language processing
- **Security**: JWT authentication with CORS

### Data Flow
1. Customer logs in via React frontend
2. JWT token is generated and stored
3. Customer sends chat messages via WebSocket
4. Backend processes with OpenAI GPT
5. Order data is retrieved from PostgreSQL
6. Response is sent back via WebSocket

## 🔒 Security

- JWT token-based authentication
- Password encryption with BCrypt
- CORS configuration for frontend
- Input validation and sanitization
- SQL injection prevention via JPA

## 📊 Development Phases

### Phase 1: Foundation (Weeks 1-2)
- ✅ Authentication system
- ✅ Basic chat interface
- ✅ Database schema

### Phase 2: Core Features (Weeks 3-4)
- 🔄 OpenAI integration
- 🔄 Order status queries
- 🔄 Real-time communication

### Phase 3: Enhancement (Weeks 5-6)
- 📅 Advanced features
- 📅 Performance optimization
- 📅 Testing and documentation

## 🐳 Docker Deployment

### Frontend
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

### Backend
```dockerfile
FROM openjdk:17-jre-slim
COPY target/order-status-chatbot-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## 🤝 Contributing

1. Follow the existing code style
2. Add TypeScript types for frontend
3. Include unit tests for backend
4. Update documentation
5. Test thoroughly before submitting

## 📝 Documentation

- [Architecture Design](architecture_design.md) - Complete system architecture
- [MVP Architecture](mvp_architecture.md) - Simplified MVP design
- [Technology Recommendations](technology_recommendations.md) - Tech stack details
- [Implementation Strategy](implementation_strategy.md) - Development phases
- [User Stories](user-stories.md) - Requirements and acceptance criteria

## 📄 License

This project is part of the Order Status Chatbot MVP demonstration.

---

**Status**: MVP Development in Progress  
**Last Updated**: December 2024  
**Version**: 0.1.0 