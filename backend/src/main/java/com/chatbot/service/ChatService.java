package com.chatbot.service;

import com.chatbot.dto.ChatResponse;
import com.chatbot.model.Conversation;
import com.chatbot.model.Customer;
import com.chatbot.repository.ConversationRepository;
import com.chatbot.repository.CustomerRepository;
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
    
    @Value("${openai.model:gpt-3.5-turbo}")
    private String model;
    
    @Value("${openai.max-tokens:150}")
    private Integer maxTokens;
    
    @Value("${openai.temperature:0.7}")
    private Double temperature;
    
    /**
     * Process customer message and generate AI response
     * @param message Customer message
     * @param customerEmail Customer email for context
     * @return ChatResponse with AI-generated reply
     */
    public ChatResponse processMessage(String message, String customerEmail) {
        try {
            logger.info("Processing message for customer: {}", customerEmail);
            
            // Create system prompt for chatbot persona
            String systemPrompt = createSystemPrompt();
            
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
            
            logger.info("Successfully processed message. Intent: {}, Confidence: {}", intent, confidence);
            
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
     * Create system prompt for chatbot persona
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
} 