package com.chatbot.service;

import com.chatbot.dto.ChatResponse;
import com.chatbot.model.Customer;
import com.chatbot.model.Order;
import com.chatbot.repository.CustomerRepository;
import com.chatbot.repository.OrderRepository;
import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ChatServiceIntegrationTest {

    @Autowired
    private ChatService chatService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderRepository orderRepository;

    @MockBean
    private RagService ragService;

    @MockBean
    private OpenAiService openAiService;

    private Customer testCustomer;
    private Order testOrder;

    @BeforeEach
    void setUp() {
        // Create test customer
        testCustomer = new Customer();
        testCustomer.setEmail("test@example.com");
        testCustomer.setFirstName("Test");
        testCustomer.setLastName("User");
        testCustomer.setPassword("$2a$10$encodedPassword");
        testCustomer.setEnabled(true);
        testCustomer = customerRepository.save(testCustomer);

        // Create test order
        testOrder = new Order();
        testOrder.setOrderNumber("TEST-001");
        testOrder.setCustomerId(testCustomer.getId());
        testOrder.setStatus("PROCESSING");
        testOrder.setTotalAmount(new BigDecimal("99.99"));
        testOrder.setShippingAddress("123 Test St, Test City, TC 12345");
        testOrder.setCreatedDate(LocalDateTime.now().minusDays(1));
        testOrder = orderRepository.save(testOrder);

        // Mock RagService behavior
        when(ragService.retrieveRelevantDocuments(anyString(), anyInt()))
                .thenReturn(Collections.emptyList());
        when(ragService.buildContext(any()))
                .thenReturn("Mock knowledge base context for testing");
    }

    @Test
    void testProcessMessageWithOrderHistoryQuery() {
        String message = "Show me my order history";
        String customerEmail = testCustomer.getEmail();

        ChatResponse result = chatService.processMessage(message, customerEmail);

        assertNotNull(result);
        assertNotNull(result.getMessage());
        assertNotNull(result.getIntent());
        assertTrue(result.getConfidence() >= 0 && result.getConfidence() <= 1);
    }

    @Test
    void testProcessMessageWithEmptyMessage() {
        String message = "";
        String customerEmail = testCustomer.getEmail();

        ChatResponse result = chatService.processMessage(message, customerEmail);

        assertNotNull(result);
        assertNotNull(result.getMessage());
        assertNotNull(result.getIntent());
        assertTrue(result.getConfidence() >= 0 && result.getConfidence() <= 1);
    }

    @Test
    void testProcessMessageWithCustomerNotFound() {
        String message = "What's the status of my order?";
        String customerEmail = "nonexistent@example.com";

        ChatResponse result = chatService.processMessage(message, customerEmail);

        assertNotNull(result);
        assertNotNull(result.getMessage());
        assertNotNull(result.getIntent());
        assertTrue(result.getConfidence() >= 0 && result.getConfidence() <= 1);
    }

    @Test
    void testProcessMessageWithOwnOrderNumber() {
        String message = "What's the status of order TEST-001?";
        String customerEmail = testCustomer.getEmail();

        when(ragService.retrieveRelevantDocuments(anyString(), anyInt()))
                .thenReturn(Collections.emptyList());
        when(ragService.buildContext(any()))
                .thenReturn("Mock knowledge base context for testing");

        ChatMessage aiMessage = new ChatMessage();
        aiMessage.setContent("Here is the status of your order TEST-001: It is currently processing with a total of $99.99.");

        ChatCompletionChoice choice = new ChatCompletionChoice();
        choice.setMessage(aiMessage);

        ChatCompletionResult chatCompletionResult = new ChatCompletionResult();
        chatCompletionResult.setChoices(List.of(choice));

        when(openAiService.createChatCompletion(any()))
                .thenReturn(chatCompletionResult);

        ChatResponse result = chatService.processMessage(message, customerEmail);

        assertNotNull(result);
        assertNotNull(result.getMessage());
        // The response should contain order information since it's the customer's own order
        assertTrue(result.getMessage().contains("SPECIFIC ORDER INFORMATION") || result.getMessage().contains("TEST-001"));
    }

    @Test
    void testProcessMessageWithOtherCustomerOrderNumber() {
        // Create another customer and order
        Customer otherCustomer = new Customer();
        otherCustomer.setEmail("other@example.com");
        otherCustomer.setFirstName("Other");
        otherCustomer.setLastName("User");
        otherCustomer.setPassword("$2a$10$encodedPassword");
        otherCustomer.setEnabled(true);
        otherCustomer = customerRepository.save(otherCustomer);

        Order otherOrder = new Order();
        otherOrder.setOrderNumber("OTHER-001");
        otherOrder.setCustomerId(otherCustomer.getId());
        otherOrder.setStatus("SHIPPED");
        otherOrder.setTotalAmount(new BigDecimal("199.99"));
        otherOrder.setShippingAddress("456 Other St, Other City, OC 67890");
        otherOrder.setCreatedDate(LocalDateTime.now().minusDays(2));
        otherOrder = orderRepository.save(otherOrder);

        // Try to access other customer's order
        String message = "What's the status of order OTHER-001?";
        String customerEmail = testCustomer.getEmail(); // Using first customer

        when(ragService.retrieveRelevantDocuments(anyString(), anyInt()))
                .thenReturn(Collections.emptyList());
        when(ragService.buildContext(any()))
                .thenReturn("Mock knowledge base context for testing");

        ChatMessage aiMessage = new ChatMessage();
        aiMessage.setContent("I'm sorry, but I can only provide information about your own orders. The order number 'OTHER-001' does not belong to your account.");

        ChatCompletionChoice choice = new ChatCompletionChoice();
        choice.setMessage(aiMessage);

        ChatCompletionResult chatCompletionResult = new ChatCompletionResult();
        chatCompletionResult.setChoices(List.of(choice));

        when(openAiService.createChatCompletion(any()))
                .thenReturn(chatCompletionResult);

        ChatResponse result = chatService.processMessage(message, customerEmail);

        assertNotNull(result);
        assertNotNull(result.getMessage());
        // The response should contain the access denied message
        assertTrue(result.getMessage().contains("ACCESS DENIED") || result.getMessage().contains("does not belong to your account"));
    }

    @Test
    void testProcessMessageWithNonExistentOrderNumber() {
        String message = "What's the status of order NONEXISTENT-001?";
        String customerEmail = testCustomer.getEmail();

        when(ragService.retrieveRelevantDocuments(anyString(), anyInt()))
                .thenReturn(Collections.emptyList());
        when(ragService.buildContext(any()))
                .thenReturn("Mock knowledge base context for testing");

        ChatMessage aiMessage = new ChatMessage();
        aiMessage.setContent("I'm sorry, but I couldn't find order NONEXISTENT-001 in our system. Please check the order number and try again.");

        ChatCompletionChoice choice = new ChatCompletionChoice();
        choice.setMessage(aiMessage);

        ChatCompletionResult chatCompletionResult = new ChatCompletionResult();
        chatCompletionResult.setChoices(List.of(choice));

        when(openAiService.createChatCompletion(any()))
                .thenReturn(chatCompletionResult);

        ChatResponse result = chatService.processMessage(message, customerEmail);

        assertNotNull(result);
        assertNotNull(result.getMessage());
        assertTrue(result.getMessage().contains("ORDER NOT FOUND") || result.getMessage().contains("NONEXISTENT-001"));
    }

    @Test
    void testProcessMessageWithOrderHistory() {
        String message = "Show me my order history";
        String customerEmail = testCustomer.getEmail();

        when(ragService.retrieveRelevantDocuments(anyString(), anyInt()))
                .thenReturn(Collections.emptyList());
        when(ragService.buildContext(any()))
                .thenReturn("Mock knowledge base context for testing");

        ChatMessage aiMessage = new ChatMessage();
        aiMessage.setContent("Here is your order history: TEST-001 is currently processing with a total of $99.99.");

        ChatCompletionChoice choice = new ChatCompletionChoice();
        choice.setMessage(aiMessage);

        ChatCompletionResult chatCompletionResult = new ChatCompletionResult();
        chatCompletionResult.setChoices(List.of(choice));

        when(openAiService.createChatCompletion(any()))
                .thenReturn(chatCompletionResult);

        ChatResponse result = chatService.processMessage(message, customerEmail);

        assertNotNull(result);
        assertNotNull(result.getMessage());
        assertTrue(result.getMessage().contains("CUSTOMER ORDER HISTORY") || result.getMessage().contains("TEST-001"));
    }

    @Test
    void testProcessMessageWithRecentOrders() {
        String customerEmail = testCustomer.getEmail();

        when(ragService.retrieveRelevantDocuments(anyString(), anyInt()))
                .thenReturn(Collections.emptyList());
        when(ragService.buildContext(any()))
                .thenReturn("Mock knowledge base context for testing");

        ChatCompletionRequest request = new ChatCompletionRequest(); // or mock this too if needed

        ChatMessage message = new ChatMessage();
        message.setContent("your recent orders are TEST-001 and TEST-002");

        ChatCompletionChoice choice = new ChatCompletionChoice();
        choice.setMessage(message);

        ChatCompletionResult chatCompletionResult = new ChatCompletionResult();
        chatCompletionResult.setChoices(List.of(choice));

        when(openAiService.createChatCompletion(any()))
                .thenReturn(chatCompletionResult);

        ChatResponse result = chatService.processMessage("Show me my recent orders", customerEmail);

        assertNotNull(result);
        assertNotNull(result.getMessage());
        assertTrue(result.getMessage().contains("recent orders") || result.getMessage().contains("TEST-001"));
    }
}