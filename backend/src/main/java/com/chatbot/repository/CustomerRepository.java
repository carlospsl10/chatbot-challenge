package com.chatbot.repository;

import com.chatbot.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    
    /**
     * Find customer by email address
     * @param email the email address to search for
     * @return Optional containing the customer if found
     */
    Optional<Customer> findByEmail(String email);
    
    /**
     * Check if a customer exists with the given email
     * @param email the email address to check
     * @return true if customer exists, false otherwise
     */
    boolean existsByEmail(String email);
    
    /**
     * Find customer by email and enabled status
     * @param email the email address to search for
     * @param enabled whether the account is enabled
     * @return Optional containing the customer if found
     */
    Optional<Customer> findByEmailAndEnabled(String email, Boolean enabled);
} 