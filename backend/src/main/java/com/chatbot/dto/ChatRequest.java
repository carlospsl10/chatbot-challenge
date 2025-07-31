package com.chatbot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Chat request DTO")
public class ChatRequest {
    
    @NotBlank(message = "Message cannot be empty")
    @Size(min = 1, max = 1000, message = "Message must be between 1 and 1000 characters")
    @Schema(description = "Customer message to be processed by AI", example = "What's the status of my order?", required = true)
    private String message;
    
    // Default constructor
    public ChatRequest() {}
    
    // Constructor with message
    public ChatRequest(String message) {
        this.message = message;
    }
    
    // Getters and Setters
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
} 