package com.chatbot.controller;

import com.chatbot.model.Order;
import com.chatbot.repository.OrderRepository;
import com.chatbot.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Order Management", description = "APIs for retrieving order information and tracking")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private JwtService jwtService;

    @GetMapping("/{orderNumber}")
    @Operation(
        summary = "Get Order by Number",
        description = "Retrieves detailed information about a specific order using the order number. Only returns orders belonging to the authenticated customer."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Order found successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Order.class),
                examples = @ExampleObject(
                    name = "Sample Order",
                    value = """
                    {
                      "id": 1,
                      "orderNumber": "ORD-001",
                      "customerId": 1,
                      "status": "SHIPPED",
                      "totalAmount": 299.99,
                      "shippingAddress": "123 Main St, New York, NY 10001",
                      "createdDate": "2024-01-15T10:30:00",
                      "updatedDate": "2024-01-19T14:45:00"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad request - Invalid order number format"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Order not found"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - Order does not belong to authenticated customer"
        )
    })
    public ResponseEntity<Order> getOrderByNumber(
        @Parameter(description = "Order number to retrieve", example = "ORD-001", required = true)
        @PathVariable String orderNumber,
        @RequestHeader("Authorization") String authorization
    ) {
        try {
            // Extract customer ID from JWT token
            String token = authorization.replace("Bearer ", "");
            Long customerId = jwtService.extractCustomerId(token);
            
            // Find order by order number
            Optional<Order> orderOpt = orderRepository.findByOrderNumber(orderNumber);
            
            if (orderOpt.isPresent()) {
                Order order = orderOpt.get();
                
                // Security check: ensure order belongs to authenticated customer
                if (!order.getCustomerId().equals(customerId)) {
                    return ResponseEntity.status(403).build();
                }
                
                return ResponseEntity.ok(order);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/my-orders")
    @Operation(
        summary = "Get My Orders",
        description = "Retrieves all orders for the authenticated customer. Orders are returned in chronological order (most recent first)."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Customer orders retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Order.class),
                examples = @ExampleObject(
                    name = "Sample Customer Orders",
                    value = """
                    [
                      {
                        "id": 1,
                        "orderNumber": "ORD-001",
                        "customerId": 1,
                        "status": "DELIVERED",
                        "totalAmount": 199.99,
                        "shippingAddress": "123 Main St, New York, NY 10001",
                        "createdDate": "2024-01-10T09:15:00",
                        "updatedDate": "2024-01-12T16:30:00"
                      },
                      {
                        "id": 2,
                        "orderNumber": "ORD-002",
                        "customerId": 1,
                        "status": "SHIPPED",
                        "totalAmount": 149.50,
                        "shippingAddress": "456 Oak Ave, Los Angeles, CA 90210",
                        "createdDate": "2024-01-17T11:20:00",
                        "updatedDate": "2024-01-19T13:45:00"
                      }
                    ]
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Invalid or missing authentication token"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad request - Invalid token format"
        )
    })
    public ResponseEntity<List<Order>> getMyOrders(
        @RequestHeader("Authorization") String authorization,
        @Parameter(description = "Maximum number of orders to return (default: 10)", example = "10")
        @RequestParam(defaultValue = "10") int limit
    ) {
        try {
            // Extract customer ID from JWT token
            String token = authorization.replace("Bearer ", "");
            Long customerId = jwtService.extractCustomerId(token);
            
            // Get customer orders from database
            List<Order> orders = orderRepository.findByCustomerId(customerId);
            
            // Sort by created date (most recent first) and limit results
            orders.sort((o1, o2) -> o2.getCreatedDate().compareTo(o1.getCreatedDate()));
            
            if (orders.size() > limit) {
                orders = orders.subList(0, limit);
            }
            
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/my-orders/recent")
    @Operation(
        summary = "Get My Recent Orders",
        description = "Retrieves recent orders (last 30 days) for the authenticated customer."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Recent orders retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Order.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Invalid or missing authentication token"
        )
    })
    public ResponseEntity<List<Order>> getMyRecentOrders(
        @RequestHeader("Authorization") String authorization
    ) {
        try {
            // Extract customer ID from JWT token
            String token = authorization.replace("Bearer ", "");
            Long customerId = jwtService.extractCustomerId(token);
            
            // Get recent orders from database
            List<Order> recentOrders = orderRepository.findRecentOrdersByCustomerId(customerId);
            
            return ResponseEntity.ok(recentOrders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/my-orders/status/{status}")
    @Operation(
        summary = "Get My Orders by Status",
        description = "Retrieves orders with specific status for the authenticated customer."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Orders retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Order.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Invalid or missing authentication token"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad request - Invalid status"
        )
    })
    public ResponseEntity<List<Order>> getMyOrdersByStatus(
        @RequestHeader("Authorization") String authorization,
        @Parameter(description = "Order status to filter by", example = "SHIPPED", required = true)
        @PathVariable String status
    ) {
        try {
            // Extract customer ID from JWT token
            String token = authorization.replace("Bearer ", "");
            Long customerId = jwtService.extractCustomerId(token);
            
            // Validate status
            String validStatus = status.toUpperCase();
            if (!isValidStatus(validStatus)) {
                return ResponseEntity.badRequest().build();
            }
            
            // Get orders by status from database
            List<Order> orders = orderRepository.findByCustomerIdAndStatus(customerId, validStatus);
            
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/track/{orderNumber}")
    @Operation(
        summary = "Get Order Tracking Information",
        description = "Retrieves real-time tracking information for a specific order. Only accessible for orders belonging to the authenticated customer."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Tracking information retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Map.class),
                examples = @ExampleObject(
                    name = "Sample Tracking Info",
                    value = """
                    {
                      "orderNumber": "ORD-001",
                      "status": "IN_TRANSIT",
                      "estimatedDelivery": "2024-01-22T14:30:00",
                      "currentLocation": "Distribution Center - Memphis, TN",
                      "trackingNumber": "1Z999AA1234567890",
                      "carrier": "FedEx",
                      "lastUpdate": "2024-01-20T08:15:00",
                      "message": "Package is in transit to final destination"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad request - Invalid order number format"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Order tracking not found"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - Order does not belong to authenticated customer"
        )
    })
    public ResponseEntity<Map<String, Object>> getOrderTracking(
        @Parameter(description = "Order number to track", example = "ORD-001", required = true)
        @PathVariable String orderNumber,
        @RequestHeader("Authorization") String authorization
    ) {
        try {
            // Extract customer ID from JWT token
            String token = authorization.replace("Bearer ", "");
            Long customerId = jwtService.extractCustomerId(token);
            
            // Find order and verify ownership
            Optional<Order> orderOpt = orderRepository.findByOrderNumber(orderNumber);
            
            if (orderOpt.isPresent()) {
                Order order = orderOpt.get();
                
                // Security check: ensure order belongs to authenticated customer
                if (!order.getCustomerId().equals(customerId)) {
                    return ResponseEntity.status(403).build();
                }
                
                // Dummy tracking data (in real implementation, this would come from tracking service)
                Map<String, Object> trackingInfo = Map.of(
                    "orderNumber", orderNumber,
                    "status", order.getStatus(),
                    "estimatedDelivery", LocalDateTime.now().plusDays(2).toString(),
                    "currentLocation", "Distribution Center - Memphis, TN",
                    "trackingNumber", "1Z999AA1234567890",
                    "carrier", "FedEx",
                    "lastUpdate", LocalDateTime.now().minusHours(6).toString(),
                    "message", "Package is in transit to final destination"
                );
                
                return ResponseEntity.ok(trackingInfo);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Validate order status
     * @param status Status to validate
     * @return true if valid, false otherwise
     */
    private boolean isValidStatus(String status) {
        return status.equals("PROCESSING") || 
               status.equals("SHIPPED") || 
               status.equals("DELIVERED") || 
               status.equals("CANCELLED");
    }
} 