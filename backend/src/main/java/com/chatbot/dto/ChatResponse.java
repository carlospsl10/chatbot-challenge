package com.chatbot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Chat response DTO")
public class ChatResponse {
    
    @Schema(description = "AI-generated response message", example = "I can help you check your order status. Could you please provide your order number?", required = true)
    private String message;
    
    @Schema(description = "Detected intent from customer message", example = "ORDER_STATUS_INQUIRY", allowableValues = {"ORDER_STATUS_INQUIRY", "ORDER_HISTORY", "TRACKING_INFO", "GENERAL_INQUIRY", "UNKNOWN"})
    private String intent;
    
    @Schema(description = "Confidence score of the AI response (0.0 to 1.0)", example = "0.95", minimum = "0.0", maximum = "1.0")
    private Double confidence;
    
    @Schema(description = "Timestamp when the response was generated", example = "2024-01-20T10:30:00")
    private LocalDateTime timestamp;
    
    // Default constructor
    public ChatResponse() {}
    
    // Constructor with all fields
    public ChatResponse(String message, String intent, Double confidence) {
        this.message = message;
        this.intent = intent;
        this.confidence = confidence;
        this.timestamp = LocalDateTime.now();
    }
    
    // Constructor with message only
    public ChatResponse(String message) {
        this.message = message;
        this.intent = "GENERAL_INQUIRY";
        this.confidence = 0.8;
        this.timestamp = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getIntent() {
        return intent;
    }
    
    public void setIntent(String intent) {
        this.intent = intent;
    }
    
    public Double getConfidence() {
        return confidence;
    }
    
    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
} 