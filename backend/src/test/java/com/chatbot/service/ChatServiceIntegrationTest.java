package com.chatbot.service;

import com.chatbot.dto.ChatResponse;
import com.chatbot.model.Customer;
import com.chatbot.model.Order;
import com.chatbot.repository.CustomerRepository;
import com.chatbot.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
} 