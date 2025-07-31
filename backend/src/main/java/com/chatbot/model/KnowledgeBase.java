package com.chatbot.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "knowledge_base")
public class KnowledgeBase {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "document_id", nullable = false, unique = true)
    private String documentId;
    
    @Column(name = "title", nullable = false)
    private String title;
    
    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;
    
    @Column(name = "category", nullable = false)
    private String category;
    
    @Column(name = "tags", columnDefinition = "TEXT")
    private String tags; // JSON array as string
    
    @Column(name = "embedding", columnDefinition = "TEXT")
    private String embedding; // Store as JSON string
    
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;
    
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;
    
    // Default constructor
    public KnowledgeBase() {}
    
    // Constructor with fields
    public KnowledgeBase(String documentId, String title, String content, String category, String tags) {
        this.documentId = documentId;
        this.title = title;
        this.content = content;
        this.category = category;
        this.tags = tags;
        this.createdDate = LocalDateTime.now();
        this.updatedDate = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getDocumentId() {
        return documentId;
    }
    
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getTags() {
        return tags;
    }
    
    public void setTags(String tags) {
        this.tags = tags;
    }
    
    public String getEmbedding() {
        return embedding;
    }
    
    public void setEmbedding(String embedding) {
        this.embedding = embedding;
    }
    
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }
    
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
    
    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }
    
    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }
    
    @PrePersist
    protected void onCreate() {
        if (createdDate == null) {
            createdDate = LocalDateTime.now();
        }
        if (updatedDate == null) {
            updatedDate = LocalDateTime.now();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedDate = LocalDateTime.now();
    }
} 