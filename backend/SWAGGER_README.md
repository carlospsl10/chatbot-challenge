# Swagger UI Documentation

## Overview
This Spring Boot application includes comprehensive API documentation using SpringDoc OpenAPI and Swagger UI.

## Accessing Swagger UI

### Development Environment
Once the application is running, you can access the Swagger UI at:

**Swagger UI:** http://localhost:8080/swagger-ui.html

**OpenAPI JSON:** http://localhost:8080/api-docs

### Production Environment
Replace `localhost:8080` with your production server URL.

## Features

### 📋 **API Documentation**
- **Interactive Documentation**: Test APIs directly from the browser
- **Request/Response Examples**: Pre-filled examples for all endpoints
- **Schema Definitions**: Detailed model schemas with examples
- **Response Codes**: Complete list of possible response codes

### 🔍 **Available Endpoints**

#### Order Management
- **GET** `/api/orders/{orderNumber}` - Get order by number
- **GET** `/api/orders/customer/{customerId}` - Get orders by customer
- **GET** `/api/orders/track/{orderNumber}` - Get order tracking information

### 📊 **API Information**
- **Title**: Order Status Chatbot API
- **Version**: 1.0.0
- **Description**: AI-Powered Order Status Chatbot Backend API
- **Contact**: Chatbot Development Team
- **License**: MIT License

## Configuration

### SpringDoc Configuration (application.yml)
```yaml
springdoc:
  api-docs:
    path: /api-docs
  swagger-ui:
    path: /swagger-ui.html
    operations-sorter: method
    tags-sorter: alpha
  packages-to-scan: com.chatbot.controller
  paths-to-match: /api/**
```

### Custom OpenAPI Configuration
The application includes a custom OpenAPI configuration in `OpenApiConfig.java` that provides:
- API metadata (title, description, version)
- Contact information
- License details
- Server configurations for development and production

## Usage

1. **Start the Application**
   ```bash
   mvn spring-boot:run
   ```

2. **Open Swagger UI**
   - Navigate to http://localhost:8080/swagger-ui.html
   - Browse available endpoints
   - Test APIs with sample data

3. **API Testing**
   - Click on any endpoint to expand
   - Click "Try it out" to test the API
   - Modify parameters as needed
   - Click "Execute" to send the request
   - View the response in the UI

## Benefits

### 🎯 **Developer Experience**
- **Interactive Testing**: No need for Postman or curl
- **Auto-generated Documentation**: Always up-to-date
- **Example Data**: Pre-filled with realistic examples
- **Schema Validation**: Visual representation of data models

### 📈 **API Quality**
- **Consistent Documentation**: Standardized across all endpoints
- **Error Handling**: Clear error response documentation
- **Parameter Validation**: Detailed parameter descriptions
- **Response Examples**: Real-world usage examples

### 🔧 **Maintenance**
- **Auto-updating**: Documentation updates with code changes
- **Version Control**: Track API changes over time
- **Team Collaboration**: Shared understanding of APIs

## Customization

### Adding New Endpoints
1. Add `@Tag` annotation to controller class
2. Add `@Operation` annotation to methods
3. Add `@Parameter` annotations to method parameters
4. Add `@ApiResponses` for response documentation
5. Add `@Schema` annotations to model classes

### Example
```java
@GetMapping("/{id}")
@Operation(summary = "Get item by ID", description = "Retrieves an item by its unique identifier")
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Item found"),
    @ApiResponse(responseCode = "404", description = "Item not found")
})
public ResponseEntity<Item> getItem(
    @Parameter(description = "Item ID", example = "1", required = true)
    @PathVariable Long id
) {
    // Implementation
}
```

## Troubleshooting

### Common Issues

1. **Swagger UI not accessible**
   - Ensure application is running on port 8080
   - Check if SpringDoc dependency is included in pom.xml
   - Verify no security configuration blocking access

2. **Missing endpoints**
   - Ensure controller is in `com.chatbot.controller` package
   - Check if endpoints use `/api/**` path pattern
   - Verify `@RestController` annotation is present

3. **Schema not showing**
   - Add `@Schema` annotations to model classes
   - Ensure proper imports for OpenAPI annotations
   - Check if model classes are properly annotated

## Security Note
In production, consider securing the Swagger UI endpoints or disabling them entirely for security reasons. 