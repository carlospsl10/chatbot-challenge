package com.chatbot.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "conversations")
public class Conversation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "customer_id", nullable = false)
    private Long customerId;
    
    @Column(name = "session_id", nullable = false)
    private String sessionId;
    
    @Column(name = "message", columnDefinition = "TEXT", nullable = false)
    private String message;
    
    @Column(name = "is_bot_message", nullable = false)
    private Boolean isBotMessage = false;
    
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;
    
    // Default constructor
    public Conversation() {}
    
    // Constructor with fields
    public Conversation(Long customerId, String sessionId, String message, Boolean isBotMessage) {
        this.customerId = customerId;
        this.sessionId = sessionId;
        this.message = message;
        this.isBotMessage = isBotMessage;
        this.createdDate = LocalDateTime.now();
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
    
    public Boolean getIsBotMessage() { return isBotMessage; }
    public void setIsBotMessage(Boolean isBotMessage) { this.isBotMessage = isBotMessage; }
    
    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }
    
    @PrePersist
    protected void onCreate() {
        if (createdDate == null) {
            createdDate = LocalDateTime.now();
        }
    }
} 