package com.chatbot.security;

import com.chatbot.model.Customer;
import com.chatbot.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Find customer by email
        Customer customer = customerRepository.findByEmailAndEnabled(email, true)
                .orElseThrow(() -> new UsernameNotFoundException("Customer not found with email: " + email));
        
        // Create UserDetails with customer information
        return new User(
            customer.getEmail(),
            customer.getPassword(),
            customer.getEnabled(),
            true, // accountNonExpired
            true, // credentialsNonExpired
            true, // accountNonLocked
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_CUSTOMER"))
        );
    }
} 