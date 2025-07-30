package com.chatbot.service;

import com.chatbot.dto.LoginRequest;
import com.chatbot.dto.LoginResponse;
import com.chatbot.model.Customer;
import com.chatbot.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
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