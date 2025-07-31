package com.chatbot.repository;

import com.chatbot.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    
    /**
     * Find conversations by customer ID
     * @param customerId Customer ID
     * @return List of conversations
     */
    List<Conversation> findByCustomerIdOrderByCreatedDateDesc(Long customerId);
    
    /**
     * Find conversations by session ID
     * @param sessionId Session ID
     * @return List of conversations
     */
    List<Conversation> findBySessionIdOrderByCreatedDateAsc(String sessionId);
    
    /**
     * Find conversations by customer ID and session ID
     * @param customerId Customer ID
     * @param sessionId Session ID
     * @return List of conversations
     */
    List<Conversation> findByCustomerIdAndSessionIdOrderByCreatedDateAsc(Long customerId, String sessionId);
} 