package com.chatbot.controller;

import com.chatbot.model.Order;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Order Management", description = "APIs for retrieving order information and tracking")
public class OrderController {

    @GetMapping("/{orderNumber}")
    @Operation(
        summary = "Get Order by Number",
        description = "Retrieves detailed information about a specific order using the order number"
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
        )
    })
    public ResponseEntity<Order> getOrderByNumber(
        @Parameter(description = "Order number to retrieve", example = "ORD-001", required = true)
        @PathVariable String orderNumber
    ) {
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
    @Operation(
        summary = "Get Orders by Customer",
        description = "Retrieves all orders for a specific customer"
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
            responseCode = "400",
            description = "Bad request - Invalid customer ID"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Customer not found"
        )
    })
    public ResponseEntity<List<Order>> getOrdersByCustomer(
        @Parameter(description = "Customer ID to retrieve orders for", example = "1", required = true)
        @PathVariable Long customerId
    ) {
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
    @Operation(
        summary = "Get Order Tracking Information",
        description = "Retrieves real-time tracking information for a specific order"
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
        )
    })
    public ResponseEntity<Map<String, Object>> getOrderTracking(
        @Parameter(description = "Order number to track", example = "ORD-001", required = true)
        @PathVariable String orderNumber
    ) {
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