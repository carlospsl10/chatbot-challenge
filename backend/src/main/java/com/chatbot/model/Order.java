package com.chatbot.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Schema(description = "Order entity representing customer orders")
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier for the order", example = "1")
    private Long id;
    
    @Column(name = "order_number", unique = true, nullable = false)
    @Schema(description = "Unique order number", example = "ORD-001", required = true)
    private String orderNumber;
    
    @Column(name = "customer_id", nullable = false)
    @Schema(description = "Customer ID associated with the order", example = "1", required = true)
    private Long customerId;
    
    @Column(name = "status", nullable = false)
    @Schema(description = "Current status of the order", example = "SHIPPED", allowableValues = {"PROCESSING", "SHIPPED", "DELIVERED", "CANCELLED"}, required = true)
    private String status;
    
    @Column(name = "total_amount", precision = 10, scale = 2)
    @Schema(description = "Total amount of the order", example = "299.99")
    private BigDecimal totalAmount;
    
    @Column(name = "shipping_address", columnDefinition = "TEXT")
    @Schema(description = "Shipping address for the order", example = "123 Main St, New York, NY 10001")
    private String shippingAddress;
    
    @Column(name = "created_date", nullable = false)
    @Schema(description = "Date when the order was created", example = "2024-01-15T10:30:00")
    private LocalDateTime createdDate;
    
    @Column(name = "updated_date")
    @Schema(description = "Date when the order was last updated", example = "2024-01-19T14:45:00")
    private LocalDateTime updatedDate;
    
    // Default constructor
    public Order() {}
    
    // Constructor with all fields
    public Order(Long id, String orderNumber, Long customerId, String status, 
                BigDecimal totalAmount, String shippingAddress, 
                LocalDateTime createdDate, LocalDateTime updatedDate) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.customerId = customerId;
        this.status = status;
        this.totalAmount = totalAmount;
        this.shippingAddress = shippingAddress;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
    
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    
    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
    
    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }
    
    public LocalDateTime getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(LocalDateTime updatedDate) { this.updatedDate = updatedDate; }
    
    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
        updatedDate = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedDate = LocalDateTime.now();
    }
} 