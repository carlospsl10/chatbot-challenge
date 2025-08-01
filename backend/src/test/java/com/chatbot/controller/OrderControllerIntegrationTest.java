package com.chatbot.controller;

import com.chatbot.model.Customer;
import com.chatbot.model.Order;
import com.chatbot.repository.CustomerRepository;
import com.chatbot.repository.OrderRepository;
import com.chatbot.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class OrderControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private MockMvc mockMvc;
    private Customer testCustomer;
    private Order testOrder;
    private String validToken;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // Create test customer
        testCustomer = new Customer();
        testCustomer.setEmail("test@example.com");
        testCustomer.setFirstName("Test");
        testCustomer.setLastName("User");
        testCustomer.setPassword(passwordEncoder.encode("password123"));
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

        // Generate valid token
        validToken = jwtService.generateToken(testCustomer.getId(), testCustomer.getEmail());
    }

    @Test
    void testGetOrderByNumberSuccess() throws Exception {
        mockMvc.perform(get("/api/orders/TEST-001")
                        .header("Authorization", "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderNumber").value("TEST-001"))
                .andExpect(jsonPath("$.status").value("PROCESSING"))
                .andExpect(jsonPath("$.totalAmount").value(99.99))
                .andExpect(jsonPath("$.customerId").value(testCustomer.getId()));
    }

    @Test
    void testGetOrderByNumberNotFound() throws Exception {
        mockMvc.perform(get("/api/orders/NONEXISTENT-001")
                        .header("Authorization", "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetMyOrdersSuccess() throws Exception {
        mockMvc.perform(get("/api/orders/my-orders")
                        .header("Authorization", "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderNumber").value("TEST-001"))
                .andExpect(jsonPath("$[0].status").value("PROCESSING"));
    }

    @Test
    void testGetMyOrdersWithLimit() throws Exception {
        mockMvc.perform(get("/api/orders/my-orders")
                        .param("limit", "5")
                        .header("Authorization", "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderNumber").value("TEST-001"));
    }

    @Test
    void testGetMyRecentOrdersSuccess() throws Exception {
        mockMvc.perform(get("/api/orders/my-orders/recent")
                        .header("Authorization", "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderNumber").value("TEST-001"))
                .andExpect(jsonPath("$[0].status").value("PROCESSING"));
    }

    @Test
    void testGetMyOrdersByStatusSuccess() throws Exception {
        mockMvc.perform(get("/api/orders/my-orders/status/PROCESSING")
                        .header("Authorization", "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderNumber").value("TEST-001"))
                .andExpect(jsonPath("$[0].status").value("PROCESSING"));
    }

    @Test
    void testGetMyOrdersByInvalidStatus() throws Exception {
        mockMvc.perform(get("/api/orders/my-orders/status/INVALID_STATUS")
                        .header("Authorization", "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetOrderTrackingNotFound() throws Exception {
        mockMvc.perform(get("/api/orders/NONEXISTENT-001/tracking")
                        .header("Authorization", "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetOrderByNumberWithInvalidToken() throws Exception {
        mockMvc.perform(get("/api/orders/TEST-001")
                        .header("Authorization", "Bearer invalid-token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetMyOrdersWithInvalidToken() throws Exception {
        mockMvc.perform(get("/api/orders/my-orders")
                        .header("Authorization", "Bearer invalid-token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
} 