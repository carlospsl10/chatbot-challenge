package com.chatbot.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "conversations")
public class Conversation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "customer_id", nullable = false)
    private Long customerId;
    
    @Column(name = "session_id")
    private String sessionId;
    
    @Column(name = "message", columnDefinition = "TEXT", nullable = false)
    private String message;
    
    @Column(name = "response", columnDefinition = "TEXT")
    private String response;
    
    @Column(name = "intent")
    private String intent;
    
    @Column(name = "confidence", precision = 3, scale = 2)
    private BigDecimal confidence;
    
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
    
    // Default constructor
    public Conversation() {}
    
    // Constructor with all fields
    public Conversation(Long id, Long customerId, String sessionId, String message,
                      String response, String intent, BigDecimal confidence, LocalDateTime timestamp) {
        this.id = id;
        this.customerId = customerId;
        this.sessionId = sessionId;
        this.message = message;
        this.response = response;
        this.intent = intent;
        this.confidence = confidence;
        this.timestamp = timestamp;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getResponse() { return response; }
    public void setResponse(String response) { this.response = response; }
    
    public String getIntent() { return intent; }
    public void setIntent(String intent) { this.intent = intent; }
    
    public BigDecimal getConfidence() { return confidence; }
    public void setConfidence(BigDecimal confidence) { this.confidence = confidence; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }
} 