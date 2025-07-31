package com.chatbot.service;

import com.chatbot.dto.LoginRequest;
import com.chatbot.dto.LoginResponse;
import com.chatbot.dto.RegisterRequest;
import com.chatbot.model.Customer;
import com.chatbot.model.Order;
import com.chatbot.repository.CustomerRepository;
import com.chatbot.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * Register a new customer and create dummy orders
     * @param registerRequest the registration request
     * @return LoginResponse with JWT token and customer information
     */
    @Transactional
    public LoginResponse registerCustomer(RegisterRequest registerRequest) {
        // Check if email already exists
        if (customerRepository.existsByEmail(registerRequest.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        
        // Create new customer
        Customer customer = new Customer();
        customer.setEmail(registerRequest.getEmail());
        customer.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        customer.setFirstName(registerRequest.getFirstName());
        customer.setLastName(registerRequest.getLastName());
        customer.setEnabled(true);
        customer.setCreatedDate(LocalDateTime.now());
        customer.setUpdatedDate(LocalDateTime.now());
        
        // Save customer
        Customer savedCustomer = customerRepository.save(customer);
        
        // Create dummy orders for the new customer
        createDummyOrders(savedCustomer.getId());
        
        // Generate JWT token
        String token = jwtService.generateToken(savedCustomer.getId(), savedCustomer.getEmail());
        
        // Create login response
        return new LoginResponse(
            token,
            savedCustomer.getId(),
            savedCustomer.getEmail(),
            savedCustomer.getFirstName(),
            savedCustomer.getLastName(),
            jwtService.getExpirationTime()
        );
    }
    
    /**
     * Create dummy orders for a new customer
     * @param customerId the customer ID
     */
    private void createDummyOrders(Long customerId) {
        // Order 1: Processing
        Order order1 = new Order();
        order1.setOrderNumber("ORD-" + String.format("%03d", customerId) + "-001");
        order1.setCustomerId(customerId);
        order1.setStatus("PROCESSING");
        order1.setTotalAmount(new BigDecimal("199.99"));
        order1.setShippingAddress("123 Main St, New York, NY 10001");
        order1.setCreatedDate(LocalDateTime.now().minusDays(2));
        order1.setUpdatedDate(LocalDateTime.now().minusDays(1));
        orderRepository.save(order1);
        
        // Order 2: Shipped
        Order order2 = new Order();
        order2.setOrderNumber("ORD-" + String.format("%03d", customerId) + "-002");
        order2.setCustomerId(customerId);
        order2.setStatus("SHIPPED");
        order2.setTotalAmount(new BigDecimal("149.50"));
        order2.setShippingAddress("456 Oak Ave, Los Angeles, CA 90210");
        order2.setCreatedDate(LocalDateTime.now().minusDays(5));
        order2.setUpdatedDate(LocalDateTime.now().minusDays(3));
        orderRepository.save(order2);
        
        // Order 3: Delivered
        Order order3 = new Order();
        order3.setOrderNumber("ORD-" + String.format("%03d", customerId) + "-003");
        order3.setCustomerId(customerId);
        order3.setStatus("DELIVERED");
        order3.setTotalAmount(new BigDecimal("89.99"));
        order3.setShippingAddress("789 Pine St, Chicago, IL 60601");
        order3.setCreatedDate(LocalDateTime.now().minusDays(10));
        order3.setUpdatedDate(LocalDateTime.now().minusDays(8));
        orderRepository.save(order3);
    }
    
    /**
     * Authenticate customer and generate JWT token
     * @param loginRequest the login request containing email and password
     * @return LoginResponse with JWT token and customer information
     * @throws BadCredentialsException if authentication fails
     */
    public LoginResponse authenticateCustomer(LoginRequest loginRequest) {
        // Find customer by email
        Optional<Customer> customerOpt = customerRepository.findByEmailAndEnabled(loginRequest.getEmail(), true);
        
        if (customerOpt.isEmpty()) {
            throw new BadCredentialsException("Invalid email or password");
        }
        
        Customer customer = customerOpt.get();
        
        // Verify password
        if (!passwordEncoder.matches(loginRequest.getPassword(), customer.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }
        
        // Generate JWT token
        String token = jwtService.generateToken(customer.getId(), customer.getEmail());
        
        // Create login response
        return new LoginResponse(
            token,
            customer.getId(),
            customer.getEmail(),
            customer.getFirstName(),
            customer.getLastName(),
            jwtService.getExpirationTime()
        );
    }
    
    /**
     * Create a new customer account with encrypted password
     * @param customer the customer to create
     * @return the created customer
     */
    public Customer createCustomer(Customer customer) {
        // Check if email already exists
        if (customerRepository.existsByEmail(customer.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        
        // Encrypt password
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        
        // Set enabled to true by default
        customer.setEnabled(true);
        
        return customerRepository.save(customer);
    }
    
    /**
     * Find customer by email
     * @param email the email to search for
     * @return Optional containing the customer if found
     */
    public Optional<Customer> findByEmail(String email) {
        return customerRepository.findByEmail(email);
    }
    
    /**
     * Validate JWT token and return customer information
     * @param token the JWT token
     * @return Optional containing the customer if token is valid
     */
    public Optional<Customer> validateTokenAndGetCustomer(String token) {
        try {
            String email = jwtService.extractEmail(token);
            return customerRepository.findByEmailAndEnabled(email, true);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
} 