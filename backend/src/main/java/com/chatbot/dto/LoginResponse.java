package com.chatbot.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Login response DTO")
public class LoginResponse {
    
    @Schema(description = "JWT authentication token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...", required = true)
    private String token;
    
    @Schema(description = "Token type", example = "Bearer", defaultValue = "Bearer")
    private String tokenType = "Bearer";
    
    @Schema(description = "Customer ID", example = "1", required = true)
    private Long customerId;
    
    @Schema(description = "Customer email", example = "customer@example.com", required = true)
    private String email;
    
    @Schema(description = "Customer first name", example = "John", required = true)
    private String firstName;
    
    @Schema(description = "Customer last name", example = "Doe", required = true)
    private String lastName;
    
    @Schema(description = "Token expiration time in milliseconds", example = "86400000")
    private Long expiresIn;
    
    // Default constructor
    public LoginResponse() {}
    
    // Constructor with fields
    public LoginResponse(String token, Long customerId, String email, String firstName, String lastName, Long expiresIn) {
        this.token = token;
        this.customerId = customerId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.expiresIn = expiresIn;
    }
    
    // Getters and Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    
    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }
    
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public Long getExpiresIn() { return expiresIn; }
    public void setExpiresIn(Long expiresIn) { this.expiresIn = expiresIn; }
} 