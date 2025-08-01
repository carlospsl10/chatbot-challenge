package com.chatbot.service;

import com.chatbot.dto.ChatResponse;
import com.chatbot.model.Conversation;
import com.chatbot.model.Customer;
import com.chatbot.model.Order;
import com.chatbot.repository.ConversationRepository;
import com.chatbot.repository.CustomerRepository;
import com.chatbot.repository.OrderRepository;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ChatService {
    
    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);
    
    @Autowired
    private OpenAiService openAiService;
    
    @Autowired
    private ConversationRepository conversationRepository;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private RagService ragService;
    
    @Value("${openai.model:gpt-3.5-turbo}")
    private String model;
    
    @Value("${openai.max-tokens:150}")
    private Integer maxTokens;
    
    @Value("${openai.temperature:0.7}")
    private Double temperature;
    
    /**
     * Process customer message and generate AI response with RAG
     * @param message Customer message
     * @param customerEmail Customer email for context
     * @return ChatResponse with AI-generated reply
     */
    public ChatResponse processMessage(String message, String customerEmail) {
        try {
            logger.info("Processing message for customer: {}", customerEmail);
            
            // Get customer ID
            Long customerId = getCustomerIdFromEmail(customerEmail);
            
            // Retrieve relevant knowledge base documents
            List<com.chatbot.model.KnowledgeBase> relevantDocs = ragService.retrieveRelevantDocuments(message, 3);
            String knowledgeContext = ragService.buildContext(relevantDocs);
            
            // Retrieve actual order data based on message content
            String orderContext = retrieveOrderContext(message, customerId);
            
            // Combine knowledge base and order data context
            String combinedContext = knowledgeContext + "\n\n" + orderContext;
            
            // Create system prompt with combined context
            String systemPrompt = createSystemPromptWithRAG(combinedContext);
            
            // Create user message
            ChatMessage userMessage = new ChatMessage("user", message);
            
            // Create chat completion request
            ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(model)
                .messages(Arrays.asList(
                    new ChatMessage("system", systemPrompt),
                    userMessage
                ))
                .maxTokens(maxTokens)
                .temperature(temperature)
                .build();
            
            // Get AI response
            String aiResponse = openAiService.createChatCompletion(request)
                .getChoices().get(0).getMessage().getContent();
            
            // Analyze intent and confidence
            String intent = analyzeIntent(message);
            Double confidence = calculateConfidence(message, aiResponse);
            
            // Create response
            ChatResponse response = new ChatResponse(aiResponse, intent, confidence);
            
            // Save conversation to database
            saveConversation(customerEmail, message, aiResponse, intent, confidence);
            
            logger.info("Successfully processed message with RAG and order data. Intent: {}, Confidence: {}", intent, confidence);
            
            return response;
            
        } catch (Exception e) {
            logger.error("Error processing message: {}", e.getMessage(), e);
            
            // Return fallback response
            return new ChatResponse(
                "I apologize, but I'm having trouble processing your request right now. Please try again in a moment.",
                "UNKNOWN",
                0.0
            );
        }
    }
    
    /**
     * Create system prompt with RAG context for order status service
     * @param context Combined knowledge base and order data context
     * @return System prompt string
     */
    private String createSystemPromptWithRAG(String context) {
        return """
            You are a helpful and polite customer service AI assistant for an online order status service. 
            Your role is to help customers with their order-related inquiries.
            
            Key guidelines:
            1. Always be polite, professional, and customer-focused
            2. Help customers check order status, order history, and tracking information
            3. Ask for order numbers when needed to provide specific information
            4. Provide clear, concise, and helpful responses
            5. If you don't have specific order information, guide customers to provide their order number
            6. Be empathetic and understanding of customer concerns
            7. Keep responses conversational but professional
            8. If you can't help with a specific request, politely redirect to human support
            9. Use the provided knowledge base information to give accurate and detailed responses
            10. When order data is available, provide specific details about order status, amounts, and dates
            11. If an order is not found, politely inform the customer and ask them to verify the order number
            
            Common customer inquiries you can help with:
            - Order status checks (provide specific order number like ORD-001)
            - Order history requests
            - Tracking information
            - General order-related questions
            
            Available order statuses:
            - PROCESSING: Order is being prepared
            - SHIPPED: Order has been shipped
            - DELIVERED: Order has been delivered
            - CANCELLED: Order has been cancelled
            
            Knowledge Base Context and Order Data:
            %s
            
            Remember: You're here to make the customer experience smooth and helpful! 
            Use the knowledge base information and order data to provide accurate and helpful responses.
            If specific order information is provided, use it to give detailed, personalized responses.
            """.formatted(context);
    }
    
    /**
     * Create system prompt for chatbot persona (fallback)
     * @return System prompt string
     */
    private String createSystemPrompt() {
        return """
            You are a helpful and polite customer service AI assistant for an online order status service. 
            Your role is to help customers with their order-related inquiries.
            
            Key guidelines:
            1. Always be polite, professional, and customer-focused
            2. Help customers check order status, order history, and tracking information
            3. Ask for order numbers when needed to provide specific information
            4. Provide clear, concise, and helpful responses
            5. If you don't have specific order information, guide customers to provide their order number
            6. Be empathetic and understanding of customer concerns
            7. Keep responses conversational but professional
            8. If you can't help with a specific request, politely redirect to human support
            
            Common customer inquiries you can help with:
            - Order status checks
            - Order history requests
            - Tracking information
            - General order-related questions
            
            Remember: You're here to make the customer experience smooth and helpful!
            """;
    }
    
    /**
     * Analyze the intent of the customer message
     * @param message Customer message
     * @return Detected intent
     */
    private String analyzeIntent(String message) {
        String lowerMessage = message.toLowerCase();
        
        if (lowerMessage.contains("order") && (lowerMessage.contains("status") || lowerMessage.contains("where") || lowerMessage.contains("track"))) {
            return "ORDER_STATUS_INQUIRY";
        } else if (lowerMessage.contains("order") && (lowerMessage.contains("history") || lowerMessage.contains("past") || lowerMessage.contains("previous"))) {
            return "ORDER_HISTORY";
        } else if (lowerMessage.contains("track") || lowerMessage.contains("shipping") || lowerMessage.contains("delivery")) {
            return "TRACKING_INFO";
        } else if (lowerMessage.contains("help") || lowerMessage.contains("support")) {
            return "GENERAL_INQUIRY";
        } else {
            return "GENERAL_INQUIRY";
        }
    }
    
    /**
     * Calculate confidence score based on message analysis
     * @param userMessage Customer message
     * @param aiResponse AI response
     * @return Confidence score (0.0 to 1.0)
     */
    private Double calculateConfidence(String userMessage, String aiResponse) {
        // Simple confidence calculation based on response quality
        double baseConfidence = 0.8;
        
        // Increase confidence if response is substantial
        if (aiResponse.length() > 20) {
            baseConfidence += 0.1;
        }
        
        // Increase confidence if response contains helpful keywords
        String lowerResponse = aiResponse.toLowerCase();
        if (lowerResponse.contains("order") || lowerResponse.contains("help") || lowerResponse.contains("can")) {
            baseConfidence += 0.05;
        }
        
        // Cap at 1.0
        return Math.min(baseConfidence, 1.0);
    }
    
    /**
     * Save conversation to database
     * @param customerEmail Customer email
     * @param userMessage User message
     * @param aiResponse AI response
     * @param intent Detected intent
     * @param confidence Confidence score
     */
    private void saveConversation(String customerEmail, String userMessage, String aiResponse, String intent, Double confidence) {
        try {
            // Get customer ID from email
            Long customerId = getCustomerIdFromEmail(customerEmail);
            
            // Generate session ID (in a real app, you'd manage sessions)
            String sessionId = UUID.randomUUID().toString();
            
            // Save user message
            Conversation userConversation = new Conversation();
            userConversation.setCustomerId(customerId);
            userConversation.setSessionId(sessionId);
            userConversation.setMessage(userMessage);
            userConversation.setIsBotMessage(false);
            userConversation.setCreatedDate(LocalDateTime.now());
            conversationRepository.save(userConversation);
            
            // Save AI response
            Conversation aiConversation = new Conversation();
            aiConversation.setCustomerId(customerId);
            aiConversation.setSessionId(sessionId);
            aiConversation.setMessage(aiResponse);
            aiConversation.setIsBotMessage(true);
            aiConversation.setCreatedDate(LocalDateTime.now());
            conversationRepository.save(aiConversation);
            
            logger.info("Saved conversation for customer: {}", customerEmail);
            
        } catch (Exception e) {
            logger.error("Error saving conversation: {}", e.getMessage(), e);
            // Don't throw exception - conversation saving is not critical
        }
    }
    
    /**
     * Get customer ID from email
     * @param email Customer email
     * @return Customer ID
     */
    private Long getCustomerIdFromEmail(String email) {
        try {
            Customer customer = customerRepository.findByEmailAndEnabled(email, true)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + email));
            return customer.getId();
        } catch (Exception e) {
            logger.error("Error finding customer by email: {}", email, e);
            // Return default customer ID as fallback
            return 1L;
        }
    }
    
    /**
     * Retrieve order context based on message content
     * @param message Customer message
     * @param customerId Customer ID
     * @return Order context string
     */
    private String retrieveOrderContext(String message, Long customerId) {
        StringBuilder context = new StringBuilder();
        String lowerMessage = message.toLowerCase();
        
        try {
            // Check if message contains specific order number
            String orderNumber = extractOrderNumber(message);
            if (orderNumber != null) {
                Optional<Order> order = orderRepository.findByOrderNumber(orderNumber);
                if (order.isPresent()) {
                    Order o = order.get();
                    
                    // Security check: ensure order belongs to authenticated customer
                    if (!o.getCustomerId().equals(customerId)) {
                        context.append("ACCESS DENIED: I'm sorry, but I can only provide information about your own orders. The order number '").append(orderNumber).append("' does not belong to your account.\n\n");
                    } else {
                        context.append("SPECIFIC ORDER INFORMATION:\n");
                        context.append(String.format("- Order Number: %s\n", o.getOrderNumber()));
                        context.append(String.format("- Status: %s\n", o.getStatus()));
                        context.append(String.format("- Total Amount: $%.2f\n", o.getTotalAmount()));
                        context.append(String.format("- Shipping Address: %s\n", o.getShippingAddress()));
                        context.append(String.format("- Created Date: %s\n", o.getCreatedDate()));
                        if (o.getUpdatedDate() != null) {
                            context.append(String.format("- Last Updated: %s\n", o.getUpdatedDate()));
                        }
                        context.append("\n");
                    }
                } else {
                    context.append("ORDER NOT FOUND: The order number '").append(orderNumber).append("' was not found in our system.\n\n");
                }
            }
            
            // If asking for order history or all orders
            if (lowerMessage.contains("order") && (lowerMessage.contains("history") || lowerMessage.contains("all") || lowerMessage.contains("my orders"))) {
                List<Order> customerOrders = orderRepository.findByCustomerId(customerId);
                if (!customerOrders.isEmpty()) {
                    context.append("CUSTOMER ORDER HISTORY:\n");
                    for (Order o : customerOrders) {
                        context.append(String.format("- Order %s: %s, $%.2f, %s\n", 
                            o.getOrderNumber(), o.getStatus(), o.getTotalAmount(), o.getCreatedDate()));
                    }
                    context.append("\n");
                } else {
                    context.append("ORDER HISTORY: No orders found for this customer.\n\n");
                }
            }
            
            // If asking for recent orders
            if (lowerMessage.contains("recent") || lowerMessage.contains("latest")) {
                List<Order> recentOrders = orderRepository.findRecentOrdersByCustomerId(customerId);
                if (!recentOrders.isEmpty()) {
                    context.append("RECENT ORDERS (Last 30 days):\n");
                    for (Order o : recentOrders) {
                        context.append(String.format("- Order %s: %s, $%.2f, %s\n", 
                            o.getOrderNumber(), o.getStatus(), o.getTotalAmount(), o.getCreatedDate()));
                    }
                    context.append("\n");
                } else {
                    context.append("RECENT ORDERS: No recent orders found.\n\n");
                }
            }
            
            // If asking for orders by status
            if (lowerMessage.contains("shipped") || lowerMessage.contains("processing") || lowerMessage.contains("delivered")) {
                String status = extractStatus(lowerMessage);
                if (status != null) {
                    List<Order> statusOrders = orderRepository.findByCustomerIdAndStatus(customerId, status.toUpperCase());
                    if (!statusOrders.isEmpty()) {
                        context.append(String.format("ORDERS WITH STATUS '%s':\n", status.toUpperCase()));
                        for (Order o : statusOrders) {
                            context.append(String.format("- Order %s: $%.2f, %s\n", 
                                o.getOrderNumber(), o.getTotalAmount(), o.getCreatedDate()));
                        }
                        context.append("\n");
                    } else {
                        context.append(String.format("No orders found with status '%s'.\n\n", status.toUpperCase()));
                    }
                }
            }
            
        } catch (Exception e) {
            logger.error("Error retrieving order context: {}", e.getMessage(), e);
            context.append("ERROR: Unable to retrieve order information at this time.\n\n");
        }
        
        return context.toString();
    }
    
    /**
     * Extract order number from message
     * @param message Customer message
     * @return Order number if found, null otherwise
     */
    private String extractOrderNumber(String message) {
        // Look for patterns like "ORD-001", "order ORD-001", "check ORD-001"
        String[] words = message.split("\\s+");
        for (String word : words) {
            if (word.matches("ORD-\\d+")) {
                return word;
            }
        }
        return null;
    }
    
    /**
     * Extract status from message
     * @param message Customer message
     * @return Status if found, null otherwise
     */
    private String extractStatus(String message) {
        if (message.contains("shipped")) return "SHIPPED";
        if (message.contains("processing")) return "PROCESSING";
        if (message.contains("delivered")) return "DELIVERED";
        if (message.contains("cancelled")) return "CANCELLED";
        return null;
    }
} 