package com.chatbot.controller;

import com.chatbot.model.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:3000")
public class OrderController {

    @GetMapping("/{orderNumber}")
    public ResponseEntity<Order> getOrderByNumber(@PathVariable String orderNumber) {
        try {
            // Dummy data for order
            Order order = new Order();
            order.setId(1L);
            order.setOrderNumber(orderNumber);
            order.setCustomerId(1L);
            order.setStatus("SHIPPED");
            order.setTotalAmount(new BigDecimal("299.99"));
            order.setShippingAddress("123 Main St, New York, NY 10001");
            order.setCreatedDate(LocalDateTime.now().minusDays(5));
            order.setUpdatedDate(LocalDateTime.now().minusDays(1));
            
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Order>> getOrdersByCustomer(@PathVariable Long customerId) {
        try {
            // Dummy data for customer orders
            Order order1 = new Order();
            order1.setId(1L);
            order1.setOrderNumber("ORD-001");
            order1.setCustomerId(customerId);
            order1.setStatus("DELIVERED");
            order1.setTotalAmount(new BigDecimal("199.99"));
            order1.setShippingAddress("123 Main St, New York, NY 10001");
            order1.setCreatedDate(LocalDateTime.now().minusDays(10));
            order1.setUpdatedDate(LocalDateTime.now().minusDays(8));

            Order order2 = new Order();
            order2.setId(2L);
            order2.setOrderNumber("ORD-002");
            order2.setCustomerId(customerId);
            order2.setStatus("SHIPPED");
            order2.setTotalAmount(new BigDecimal("149.50"));
            order2.setShippingAddress("456 Oak Ave, Los Angeles, CA 90210");
            order2.setCreatedDate(LocalDateTime.now().minusDays(3));
            order2.setUpdatedDate(LocalDateTime.now().minusDays(1));

            Order order3 = new Order();
            order3.setId(3L);
            order3.setOrderNumber("ORD-003");
            order3.setCustomerId(customerId);
            order3.setStatus("PROCESSING");
            order3.setTotalAmount(new BigDecimal("89.99"));
            order3.setShippingAddress("789 Pine St, Chicago, IL 60601");
            order3.setCreatedDate(LocalDateTime.now().minusDays(1));
            order3.setUpdatedDate(LocalDateTime.now());

            List<Order> orders = Arrays.asList(order1, order2, order3);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/track/{orderNumber}")
    public ResponseEntity<Map<String, Object>> getOrderTracking(@PathVariable String orderNumber) {
        try {
            // Dummy tracking data
            Map<String, Object> trackingInfo = Map.of(
                "orderNumber", orderNumber,
                "status", "IN_TRANSIT",
                "estimatedDelivery", LocalDateTime.now().plusDays(2).toString(),
                "currentLocation", "Distribution Center - Memphis, TN",
                "trackingNumber", "1Z999AA1234567890",
                "carrier", "FedEx",
                "lastUpdate", LocalDateTime.now().minusHours(6).toString(),
                "message", "Package is in transit to final destination"
            );
            
            return ResponseEntity.ok(trackingInfo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 