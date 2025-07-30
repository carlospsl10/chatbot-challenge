# Order Status Chatbot - Backend

A Spring Boot application that provides REST APIs and WebSocket support for the AI-powered Order Status Chatbot. This backend handles authentication, order management, and AI integration.

## Features

- 🔐 **JWT Authentication** - Secure token-based authentication
- 💬 **WebSocket Support** - Real-time chat communication
- 🗄️ **Database Integration** - PostgreSQL with JPA/Hibernate
- 🔄 **Redis Caching** - Session management and response caching
- 🤖 **AI Integration** - OpenAI GPT for natural language processing
- 📊 **Order Management** - CRUD operations for orders and customers
- 🔒 **Security** - Spring Security with CORS configuration

## Technology Stack

- **Spring Boot 3.1.0** - Main framework
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Database access layer
- **Spring WebSocket** - Real-time communication
- **PostgreSQL** - Primary database
- **Redis** - Session and cache management
- **JWT** - Token-based authentication
- **OpenAI API** - Natural language processing
- **Lombok** - Code generation utilities

## Project Structure

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/chatbot/
│   │   │   ├── config/
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   ├── WebSocketConfig.java
│   │   │   │   └── RedisConfig.java
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── ChatController.java
│   │   │   │   ├── OrderController.java
│   │   │   │   └── WebSocketController.java
│   │   │   ├── service/
│   │   │   │   ├── AuthService.java
│   │   │   │   ├── ChatbotService.java
│   │   │   │   ├── OrderService.java
│   │   │   │   ├── CustomerService.java
│   │   │   │   └── OpenAIService.java
│   │   │   ├── model/
│   │   │   │   ├── Order.java
│   │   │   │   ├── Customer.java
│   │   │   │   ├── Conversation.java
│   │   │   │   ├── Session.java
│   │   │   │   └── ChatMessage.java
│   │   │   ├── repository/
│   │   │   │   ├── OrderRepository.java
│   │   │   │   ├── CustomerRepository.java
│   │   │   │   ├── ConversationRepository.java
│   │   │   │   └── SessionRepository.java
│   │   │   ├── dto/
│   │   │   │   ├── LoginRequest.java
│   │   │   │   ├── LoginResponse.java
│   │   │   │   ├── ChatRequest.java
│   │   │   │   └── ChatResponse.java
│   │   │   └── ChatbotApplication.java
│   │   └── resources/
│   │       └── application.yml
│   └── test/
│       └── java/com/chatbot/
├── pom.xml
└── README.md
```

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL 13+
- Redis 6+

### Database Setup

1. Create PostgreSQL database:
   ```sql
   CREATE DATABASE chatbot;
   CREATE USER chatbot WITH PASSWORD 'password';
   GRANT ALL PRIVILEGES ON DATABASE chatbot TO chatbot;
   ```

2. Update `application.yml` with your database credentials.

### Installation

1. Navigate to the backend directory:
   ```bash
   cd backend
   ```

2. Build the project:
   ```bash
   mvn clean install
   ```

3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

4. The application will start on `http://localhost:8080`

## API Endpoints

### Authentication
- `POST /api/auth/login` - Customer login
- `POST /api/auth/logout` - Customer logout
- `GET /api/auth/me` - Get current customer info

### Orders
- `GET /api/orders/{orderNumber}` - Get order by number
- `GET /api/orders/customer/{customerId}` - Get customer orders
- `GET /api/orders/track/{orderNumber}` - Get order tracking

### Chat
- `POST /api/chat/message` - Send chat message
- `GET /api/chat/history` - Get conversation history
- WebSocket `/ws/chat` - Real-time chat

## Configuration

### Environment Variables

Create a `.env` file or set environment variables:

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/chatbot
SPRING_DATASOURCE_USERNAME=chatbot
SPRING_DATASOURCE_PASSWORD=password
JWT_SECRET=your-secret-key-here
OPENAI_API_KEY=your-openai-api-key
```

### Application Properties

Key configuration in `application.yml`:

- **Database**: PostgreSQL connection settings
- **Redis**: Session and cache configuration
- **JWT**: Token secret and expiration
- **OpenAI**: API key and model settings
- **CORS**: Cross-origin resource sharing

## Development

### Running Tests

```bash
mvn test
```

### Code Quality

```bash
mvn checkstyle:check
mvn spotbugs:check
```

### Database Migration

The application uses Hibernate's `ddl-auto: update` for automatic schema generation. For production, consider using Flyway or Liquibase.

## Deployment

### Docker

1. Build the Docker image:
   ```bash
   docker build -t chatbot-backend .
   ```

2. Run the container:
   ```bash
   docker run -p 8080:8080 chatbot-backend
   ```

### Production Considerations

- Use external PostgreSQL and Redis instances
- Configure proper JWT secrets
- Set up monitoring and logging
- Enable HTTPS
- Configure CORS for production domains

## Security

### JWT Authentication

- Tokens are signed with a secret key
- Tokens expire after 24 hours
- Invalid tokens are rejected

### CORS Configuration

- Configured for frontend origin
- Allows necessary HTTP methods
- Supports credentials

### Input Validation

- All inputs are validated
- SQL injection prevention through JPA
- XSS protection through proper encoding

## Monitoring

### Health Checks

- `/actuator/health` - Application health
- `/actuator/info` - Application information

### Logging

- Structured logging with timestamps
- Different log levels for different environments
- Error tracking and monitoring

## Troubleshooting

### Common Issues

1. **Database Connection**
   - Check PostgreSQL is running
   - Verify connection credentials
   - Ensure database exists

2. **Redis Connection**
   - Check Redis is running
   - Verify Redis configuration
   - Test Redis connectivity

3. **JWT Issues**
   - Verify JWT secret is set
   - Check token expiration
   - Validate token format

## Contributing

1. Follow Spring Boot conventions
2. Add proper error handling
3. Include unit tests for new features
4. Update documentation
5. Follow security best practices

## License

This project is part of the Order Status Chatbot MVP. 