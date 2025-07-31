package com.chatbot.controller;

import com.chatbot.dto.LoginRequest;
import com.chatbot.dto.LoginResponse;
import com.chatbot.dto.RegisterRequest;
import com.chatbot.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "APIs for customer authentication and session management")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @PostMapping("/register")
    @Operation(
        summary = "Customer Registration",
        description = "Register a new customer account with email, password, and personal information"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Registration successful",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = LoginResponse.class),
                examples = @ExampleObject(
                    name = "Successful Registration",
                    value = """
                    {
                      "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                      "tokenType": "Bearer",
                      "customerId": 1,
                      "email": "john.doe@example.com",
                      "firstName": "John",
                      "lastName": "Doe",
                      "expiresIn": 86400000
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad request - Validation error or duplicate email",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Map.class),
                examples = @ExampleObject(
                    name = "Validation Error",
                    value = """
                    {
                      "error": "Email already exists",
                      "timestamp": "2024-01-20T10:30:00",
                      "status": 400
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "422",
            description = "Unprocessable Entity - Invalid input data"
        )
    })
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            LoginResponse response = authService.registerCustomer(registerRequest);
            return ResponseEntity.status(201).body(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            errorResponse.put("timestamp", java.time.LocalDateTime.now().toString());
            errorResponse.put("status", 400);
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Registration failed");
            errorResponse.put("timestamp", java.time.LocalDateTime.now().toString());
            errorResponse.put("status", 400);
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @PostMapping("/login")
    @Operation(
        summary = "Customer Login",
        description = "Authenticate customer with email and password, returns JWT token"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Login successful",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = LoginResponse.class),
                examples = @ExampleObject(
                    name = "Successful Login",
                    value = """
                    {
                      "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                      "tokenType": "Bearer",
                      "customerId": 1,
                      "email": "customer@example.com",
                      "firstName": "John",
                      "lastName": "Doe",
                      "expiresIn": 86400000
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad request - Invalid credentials or validation error",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Map.class),
                examples = @ExampleObject(
                    name = "Invalid Credentials",
                    value = """
                    {
                      "error": "Invalid email or password",
                      "timestamp": "2024-01-20T10:30:00",
                      "status": 400
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Invalid credentials"
        )
    })
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse response = authService.authenticateCustomer(loginRequest);
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Invalid email or password");
            errorResponse.put("timestamp", java.time.LocalDateTime.now().toString());
            errorResponse.put("status", 401);
            return ResponseEntity.status(401).body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Authentication failed");
            errorResponse.put("timestamp", java.time.LocalDateTime.now().toString());
            errorResponse.put("status", 400);
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @PostMapping("/logout")
    @Operation(
        summary = "Customer Logout",
        description = "Logout customer and invalidate session (client-side token removal)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Logout successful",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Map.class),
                examples = @ExampleObject(
                    name = "Successful Logout",
                    value = """
                    {
                      "message": "Logout successful",
                      "timestamp": "2024-01-20T10:30:00"
                    }
                    """
                )
            )
        )
    })
    public ResponseEntity<Map<String, Object>> logout() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Logout successful");
        response.put("timestamp", java.time.LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }
} 