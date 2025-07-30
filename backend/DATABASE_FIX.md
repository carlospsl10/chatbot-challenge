# Database Setup and Fix Guide

## Overview
This guide provides instructions for setting up and fixing the database for the Order Status Chatbot application.

## 🚀 Quick Start (Recommended)

### Option 1: Fresh Database Setup (Recommended)
The application is configured to automatically create and populate the database on startup.

1. **Start the application:**
   ```bash
   cd backend
   mvn spring-boot:run
   ```

2. **The database will be automatically:**
   - Created with all required tables
   - Populated with sample customers and orders
   - Configured with proper indexes and permissions
   - Set up with working password hashes

## 🔧 Manual Database Setup

### Option 2: Manual Database Creation
If you prefer to create the database manually:

1. **Create the database:**
   ```bash
   createdb -U postgres chatbot
   ```

2. **Run the integrated initialization script:**
   ```bash
   psql -h localhost -U chatbot -d chatbot -f src/main/resources/init.sql
   ```

## 📋 Test Credentials

Once the database is set up, you can test authentication with these credentials:

| Email | Password | Name |
|-------|----------|------|
| `john.doe@example.com` | `password123` | John Doe |
| `sarah.smith@example.com` | `password123` | Sarah Smith |
| `mike.johnson@example.com` | `password123` | Mike Johnson |
| `lisa.wilson@example.com` | `password123` | Lisa Wilson |
| `david.brown@example.com` | `password123` | David Brown |

## 🧪 Testing Authentication

### Using curl:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com",
    "password": "password123"
  }'
```

### Expected Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "customerId": 1,
  "email": "john.doe@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "expiresIn": 86400000
}
```

## 🔍 Verification Steps

### 1. Check Database Connection
```bash
psql -h localhost -U chatbot -d chatbot -c "\dt"
```

### 2. Verify Sample Data
```bash
psql -h localhost -U chatbot -d chatbot -c "SELECT email, first_name, last_name FROM customers;"
```

### 3. Test API Endpoints
- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **Health Check:** http://localhost:8080/api/auth/login

## 🐛 Troubleshooting

### Common Issues:

1. **Database Connection Failed**
   - Ensure PostgreSQL is running
   - Check connection settings in `application.yml`
   - Verify database exists and user has permissions

2. **Authentication Fails**
   - Ensure the database was created with the integrated `init.sql`
   - Verify password hashes are correct (all customers use `password123`)
   - Check application logs for debug information

3. **Schema Errors**
   - Drop and recreate the database for a fresh start
   - The integrated script handles all schema creation and data insertion

## 📁 File Structure

- `src/main/resources/init.sql` - **Integrated database initialization script**
  - Creates all tables
  - Handles migrations for existing databases
  - Inserts sample data with correct password hashes
  - Sets up indexes and permissions
  - Provides completion logging

## 🔄 Database Reset

If you need to reset the database:

```bash
# Drop and recreate
dropdb -U postgres chatbot
createdb -U postgres chatbot

# Or let the application recreate it automatically
# Just restart the Spring Boot application
```

## 📝 Notes

- All customers use the password: `password123`
- Password hashes are properly encoded using Spring Security's BCryptPasswordEncoder
- The integrated script is idempotent and safe to run multiple times
- Sample orders are created for testing order-related endpoints
- All necessary indexes are created for optimal performance 