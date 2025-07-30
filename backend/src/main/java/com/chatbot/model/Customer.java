package com.chatbot.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "customers")
@Schema(description = "Customer entity representing registered users")
public class Customer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier for the customer", example = "1")
    private Long id;
    
    @Column(name = "email", unique = true, nullable = false)
    @Schema(description = "Customer email address (used for login)", example = "customer@example.com", required = true)
    private String email;
    
    @Column(name = "password", nullable = false)
    @Schema(description = "Encrypted password", required = true)
    private String password;
    
    @Column(name = "first_name", nullable = false)
    @Schema(description = "Customer first name", example = "John", required = true)
    private String firstName;
    
    @Column(name = "last_name", nullable = false)
    @Schema(description = "Customer last name", example = "Doe", required = true)
    private String lastName;
    
    @Column(name = "phone")
    @Schema(description = "Customer phone number", example = "+1-555-123-4567")
    private String phone;
    
    @Column(name = "address", columnDefinition = "TEXT")
    @Schema(description = "Customer address", example = "123 Main St, New York, NY 10001")
    private String address;
    
    @Column(name = "created_date", nullable = false)
    @Schema(description = "Date when the customer account was created", example = "2024-01-15T10:30:00")
    private LocalDateTime createdDate;
    
    @Column(name = "updated_date")
    @Schema(description = "Date when the customer account was last updated", example = "2024-01-19T14:45:00")
    private LocalDateTime updatedDate;
    
    @Column(name = "enabled", nullable = false)
    @Schema(description = "Whether the customer account is enabled", example = "true", defaultValue = "true")
    private Boolean enabled = true;
    
    // Default constructor
    public Customer() {}
    
    // Constructor with all fields
    public Customer(Long id, String email, String password, String firstName, String lastName, 
                   String phone, String address, LocalDateTime createdDate, LocalDateTime updatedDate, Boolean enabled) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.address = address;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.enabled = enabled;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }
    
    public LocalDateTime getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(LocalDateTime updatedDate) { this.updatedDate = updatedDate; }
    
    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }
    
    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
        updatedDate = LocalDateTime.now();
        if (enabled == null) {
            enabled = true;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedDate = LocalDateTime.now();
    }
} 