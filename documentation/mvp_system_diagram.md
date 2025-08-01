# MVP System Architecture Diagrams

## 1. High-Level MVP Architecture

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           CLIENT LAYER                                    │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐     │
│  │                    React Web Application                           │     │
│  │                                                                   │     │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────┐ │     │
│  │  │   Login     │  │    Chat     │  │   Order     │  │ Session │ │     │
│  │  │   Form      │  │  Interface  │  │   Status    │  │  Info   │ │     │
│  │  └─────────────┘  └─────────────┘  └─────────────┘  └─────────┘ │     │
│  └─────────────────────────────────────────────────────────────────────┘     │
└─────────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                        SPRING BOOT BACKEND                               │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐     │
│  │   REST      │  │  WebSocket  │  │  Security   │  │  Chatbot    │     │
│  │   APIs      │  │   Server    │  │   Service   │  │   Service   │     │
│  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘     │
│                                                                             │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐     │
│  │   Order     │  │  Customer   │  │  Session    │  │   OpenAI    │     │
│  │  Service    │  │   Service   │  │  Manager    │  │   Service   │     │
│  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘     │
└─────────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                            DATA LAYER                                     │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐     │
│  │ PostgreSQL  │  │    Redis    │  │   Static    │  │   Logs      │     │
│  │  Database   │  │   Cache     │  │   Assets    │  │   Files     │     │
│  │             │  │             │  │             │  │             │     │
│  │ • Orders    │  │ • Sessions  │  │ • Images    │  │ • Chat      │     │
│  │ • Customers │  │ • Cache     │  │ • CSS/JS    │  │   History   │     │
│  │ • Conversations│ │ • Tokens   │  │ • Config    │  │ • Errors    │     │
│  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘     │
└─────────────────────────────────────────────────────────────────────────────┘
```

## 2. Customer Interaction Flow

```
Customer Opens Web App
         │
         ▼
┌─────────────────┐
│   Login Form    │ ← Email/Password
└─────────────────┘
         │
         ▼
┌─────────────────┐
│  Spring Boot    │ ← JWT Authentication
│   Auth API      │
└─────────────────┘
         │
         ▼
┌─────────────────┐
│   JWT Token     │ ← Secure Session Token
└─────────────────┘
         │
         ▼
┌─────────────────┐
│   Chat Widget   │ ← Customer Interface
└─────────────────┘
         │
         ▼
┌─────────────────┐
│  WebSocket      │ ← Real-time Connection
│   Connection    │
└─────────────────┘
         │
         ▼
┌─────────────────┐
│   Chat Message  │ ← "Where is my order #12345?"
└─────────────────┘
         │
         ▼
┌─────────────────┐
│  Intent         │ ← Recognize Order Status Query
│  Recognition    │
└─────────────────┘
         │
         ▼
┌─────────────────┐
│   Order         │ ← Extract Order Number
│   Service       │
└─────────────────┘
         │
         ▼
┌─────────────────┐
│  PostgreSQL     │ ← Query Order Database
│   Database      │
└─────────────────┘
         │
         ▼
┌─────────────────┐
│   OpenAI        │ ← Generate Natural Response
│   Service       │
└─────────────────┘
         │
         ▼
┌─────────────────┐
│   Response      │ ← "Your order #12345 is currently..."
└─────────────────┘
         │
         ▼
Customer Sees Response
```

## 3. Authentication Flow

```
Customer Login Request
         │
         ▼
┌─────────────────┐
│   React App     │ ← Login Form Submission
└─────────────────┘
         │
         ▼
┌─────────────────┐
│  Spring Boot    │ ← POST /api/auth/login
│   Auth API      │
└─────────────────┘
         │
         ▼
┌─────────────────┐
│  Spring Security│ ← Validate Credentials
└─────────────────┘
         │
         ▼
┌─────────────────┐
│  PostgreSQL     │ ← Check Customer Database
│   Database      │
└─────────────────┘
         │
         ▼
┌─────────────────┐
│   JWT Token     │ ← Generate Secure Token
│   Generation     │
└─────────────────┘
         │
         ▼
┌─────────────────┐
│    Redis        │ ← Store Session Data
│   Session       │
└─────────────────┘
         │
         ▼
┌─────────────────┐
│   React App     │ ← Store Token in Browser
└─────────────────┘
         │
         ▼
┌─────────────────┐
│   Chat Widget   │ ← Enable Chat Interface
└─────────────────┘
```

## 4. Data Flow Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   React         │    │   Spring Boot   │    │   External      │
│   Frontend      │◄──►│   Backend       │◄──►│   Services      │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   WebSocket     │    │   REST APIs     │    │   OpenAI API    │
│   Connection    │    │   & Services    │    │   (GPT)         │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Browser       │    │   Business      │    │   AI/ML         │
│   Storage       │    │   Logic         │    │   Processing    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Session       │    │   PostgreSQL    │    │   Response      │
│   Data          │    │   Database      │    │   Generation    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## 5. Database Schema Relationships

```
┌─────────────────┐         ┌─────────────────┐
│   Customers     │         │     Orders      │
│                 │         │                 │
│ • id (PK)       │◄────────┤ • customer_id   │
│ • email         │         │ • order_number  │
│ • first_name    │         │ • status        │
│ • last_name     │         │ • total_amount  │
│ • phone         │         │ • created_date  │
│ • created_date  │         │ • updated_date  │
└─────────────────┘         └─────────────────┘
         │                           │
         │                           │
         ▼                           ▼
┌─────────────────┐         ┌─────────────────┐
│   Sessions      │         │  Conversations  │
│                 │         │                 │
│ • id (PK)       │         │ • id (PK)       │
│ • customer_id   │◄────────┤ • customer_id   │
│ • created_date  │         │ • session_id    │
│ • last_activity │         │ • message       │
│ • is_active     │         │ • response      │
└─────────────────┘         │ • intent        │
                            │ • timestamp     │
                            └─────────────────┘
```

## 6. Docker Deployment Architecture

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           DOCKER COMPOSE                                  │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐            │
│  │   Frontend      │  │   Backend       │  │   Database      │            │
│  │   Container     │  │   Container     │  │   Container     │            │
│  │                 │  │                 │  │                 │            │
│  │ • React App     │  │ • Spring Boot   │  │ • PostgreSQL    │            │
│  │ • Nginx         │  │ • JVM           │  │ • Data Volume   │            │
│  │ • Port 3000     │  │ • Port 8080     │  │ • Port 5432     │            │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘            │
│                                                                             │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐            │
│  │   Redis         │  │   Network       │  │   Volumes       │            │
│  │   Container     │  │   Bridge        │  │                 │            │
│  │                 │  │                 │  │ • postgres_data  │            │
│  │ • Cache         │  │ • Internal      │  │ • app_logs      │            │
│  │ • Sessions      │  │   Communication │  │ • static_files  │            │
│  │ • Port 6379     │  │ • Service       │  │                 │            │
│  └─────────────────┘  │   Discovery     │  └─────────────────┘            │
│                       └─────────────────┘                                  │
└─────────────────────────────────────────────────────────────────────────────┘
```

## 7. API Endpoints Overview

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           API ENDPOINTS                                   │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  Authentication Endpoints:                                                  │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐            │
│  │ POST /auth/login│  │POST /auth/logout│  │ GET /auth/me    │            │
│  │                 │  │                 │  │                 │            │
│  │ • Email/Password│  │ • Invalidate    │  │ • Get Customer  │            │
│  │ • Return JWT    │  │   Token         │  │   Info          │            │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘            │
│                                                                             │
│  Chat Endpoints:                                                           │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐            │
│  │POST /chat/message│  │GET /chat/history│  │  WebSocket      │            │
│  │                 │  │                 │  │  /ws/chat       │            │
│  │ • Send Message  │  │ • Get Previous  │  │                 │            │
│  │ • Get Response  │  │   Messages      │  │ • Real-time     │            │
│  └─────────────────┘  └─────────────────┘  │   Chat          │            │
│                                            └─────────────────┘            │
│                                                                             │
│  Order Endpoints:                                                          │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐            │
│  │GET /orders/{id} │  │GET /orders/cust │  │GET /orders/track│            │
│  │                 │  │  /{customerId}  │  │  /{orderNumber} │            │
│  │ • Order Details │  │ • Customer      │  │ • Tracking      │            │
│  │ • Status Info   │  │   Orders        │  │   Information   │            │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘            │
└─────────────────────────────────────────────────────────────────────────────┘
```

## 8. Security Implementation

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           SECURITY LAYERS                                 │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐     │
│  │   Frontend  │  │   Transport │  │ Application │  │     Data     │     │
│  │   Security  │  │   Security  │  │   Security  │  │   Security   │     │
│  │             │  │             │  │             │  │             │     │
│  │ • HTTPS     │  │ • TLS/SSL   │  │ • JWT Auth  │  │ • Encryption│     │
│  │ • CORS      │  │ • HTTPS     │  │ • Session   │  │ • Validation│     │
│  │ • XSS       │  │ • Headers   │  │   Mgmt      │  │ • Sanitize  │     │
│  │   Protection│  │             │  │ • RBAC      │  │   Input     │     │
│  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘     │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

## 9. Error Handling Flow

```
Customer Request
         │
         ▼
┌─────────────────┐
│   Input         │ ← Validate Request
│   Validation    │
└─────────────────┘
         │
         ├─ VALID ──────────────────┐
         │                          │
         ▼                          │
┌─────────────────┐                 │
│   Process       │                 │
│   Request       │                 │
└─────────────────┘                 │
         │                          │
         ▼                          │
┌─────────────────┐                 │
│   Success       │                 │
│   Response      │                 │
└─────────────────┘                 │
         │                          │
         ▼                          │
┌─────────────────┐                 │
│   Return        │                 │
│   Result        │                 │
└─────────────────┘                 │
                                    │
                                    ▼
┌─────────────────┐
│   Error         │ ← Handle Errors
│   Handler       │
└─────────────────┘
         │
         ▼
┌─────────────────┐
│   Log Error     │ ← Log for Debugging
└─────────────────┘
         │
         ▼
┌─────────────────┐
│   Return        │ ← Graceful Error
│   Error         │   Response
│   Response      │
└─────────────────┘
```

These diagrams provide a clear view of the simplified MVP architecture, showing the essential components and data flows for the AI-powered order status chatbot. 