package com.chatbot.service;

import com.chatbot.dto.LoginRequest;
import com.chatbot.dto.LoginResponse;
import com.chatbot.dto.RegisterRequest;
import com.chatbot.model.Customer;
import com.chatbot.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AuthServiceIntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        // Create test customer
        testCustomer = new Customer();
        testCustomer.setEmail("test@example.com");
        testCustomer.setFirstName("Test");
        testCustomer.setLastName("User");
        testCustomer.setPassword(passwordEncoder.encode("password123"));
        testCustomer.setEnabled(true);
        testCustomer = customerRepository.save(testCustomer);
    }

    @Test
    void testAuthenticateCustomerSuccess() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        LoginResponse response = authService.authenticateCustomer(loginRequest);

        assertNotNull(response);
        assertNotNull(response.getToken());
        assertEquals("test@example.com", response.getEmail());
        assertEquals("Test", response.getFirstName());
        assertEquals("User", response.getLastName());
        assertEquals(testCustomer.getId(), response.getCustomerId());
    }

    @Test
    void testRegisterCustomerSuccess() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("newuser@example.com");
        registerRequest.setPassword("SecurePass123!");
        registerRequest.setFirstName("Jane");
        registerRequest.setLastName("Smith");

        LoginResponse response = authService.registerCustomer(registerRequest);

        assertNotNull(response);
        assertNotNull(response.getToken());
        assertEquals("newuser@example.com", response.getEmail());
        assertEquals("Jane", response.getFirstName());
        assertEquals("Smith", response.getLastName());
    }

    @Test
    void testFindByEmail() {
        var result = authService.findByEmail("test@example.com");

        assertTrue(result.isPresent());
        assertEquals("test@example.com", result.get().getEmail());
    }

    @Test
    void testFindByEmailNotFound() {
        var result = authService.findByEmail("nonexistent@example.com");

        assertFalse(result.isPresent());
    }
} 