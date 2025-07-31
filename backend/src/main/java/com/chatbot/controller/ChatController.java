package com.chatbot.controller;

import com.chatbot.dto.ChatRequest;
import com.chatbot.dto.ChatResponse;
import com.chatbot.service.ChatService;
import io.github.bucket4j.Bucket;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@Tag(name = "Chat", description = "APIs for AI-powered chat interactions")
public class ChatController {

    @Autowired
    private ChatService chatService;
    
    @Autowired
    @Qualifier("chatRateLimiter")
    private Bucket rateLimiter;

    @PostMapping("/message")
    @Operation(
        summary = "Send Chat Message",
        description = "Send a message to the AI chatbot and receive a response"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Message processed successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ChatResponse.class),
                examples = @ExampleObject(
                    name = "Successful Chat Response",
                    value = """
                    {
                      "message": "I can help you check your order status. Could you please provide your order number?",
                      "intent": "ORDER_STATUS_INQUIRY",
                      "confidence": 0.95,
                      "timestamp": "2024-01-20T10:30:00"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad request - Invalid message format or content",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Map.class),
                examples = @ExampleObject(
                    name = "Invalid Message",
                    value = """
                    {
                      "error": "Message cannot be empty",
                      "timestamp": "2024-01-20T10:30:00",
                      "status": 400
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Invalid or missing authentication token"
        ),
        @ApiResponse(
            responseCode = "429",
            description = "Too Many Requests - Rate limit exceeded",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Map.class),
                examples = @ExampleObject(
                    name = "Rate Limit Exceeded",
                    value = """
                    {
                      "error": "Rate limit exceeded. Please wait a moment before sending another message.",
                      "timestamp": "2024-01-20T10:30:00",
                      "status": 429
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal Server Error - AI service unavailable",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Map.class),
                examples = @ExampleObject(
                    name = "Service Error",
                    value = """
                    {
                      "error": "I'm having trouble processing your request right now. Please try again in a moment.",
                      "timestamp": "2024-01-20T10:30:00",
                      "status": 500
                    }
                    """
                )
            )
        )
    })
    public ResponseEntity<?> sendMessage(
        @RequestBody ChatRequest chatRequest,
        Authentication authentication
    ) {
        // Check rate limit
        if (!rateLimiter.tryConsume(1)) {
            Map<String, Object> errorResponse = Map.of(
                "error", "Rate limit exceeded. Please wait a moment before sending another message.",
                "timestamp", java.time.LocalDateTime.now().toString(),
                "status", 429
            );
            return ResponseEntity.status(429).body(errorResponse);
        }
        
        try {
            // Validate request
            if (chatRequest.getMessage() == null || chatRequest.getMessage().trim().isEmpty()) {
                Map<String, Object> errorResponse = Map.of(
                    "error", "Message cannot be empty",
                    "timestamp", java.time.LocalDateTime.now().toString(),
                    "status", 400
                );
                return ResponseEntity.badRequest().body(errorResponse);
            }

            // Get customer ID from authentication
            String customerEmail = authentication.getName();
            
            // Process message through chat service
            ChatResponse response = chatService.processMessage(chatRequest.getMessage(), customerEmail);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = Map.of(
                "error", "I'm having trouble processing your request right now. Please try again in a moment.",
                "timestamp", java.time.LocalDateTime.now().toString(),
                "status", 500
            );
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/health")
    @Operation(
        summary = "Chat Service Health Check",
        description = "Check if the chat service is available"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Chat service is healthy",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Map.class),
                examples = @ExampleObject(
                    name = "Service Healthy",
                    value = """
                    {
                      "status": "healthy",
                      "timestamp": "2024-01-20T10:30:00"
                    }
                    """
                )
            )
        )
    })
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = Map.of(
            "status", "healthy",
            "timestamp", java.time.LocalDateTime.now().toString()
        );
        return ResponseEntity.ok(response);
    }
} 