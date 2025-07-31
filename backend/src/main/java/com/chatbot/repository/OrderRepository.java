package com.chatbot.repository;

import com.chatbot.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    /**
     * Find order by order number
     * @param orderNumber Order number to search for
     * @return Optional containing the order if found
     */
    Optional<Order> findByOrderNumber(String orderNumber);
    
    /**
     * Find all orders for a specific customer
     * @param customerId Customer ID
     * @return List of orders for the customer
     */
    List<Order> findByCustomerId(Long customerId);
    
    /**
     * Find orders by status
     * @param status Order status to search for
     * @return List of orders with the specified status
     */
    List<Order> findByStatus(String status);
    
    /**
     * Find orders by customer ID and status
     * @param customerId Customer ID
     * @param status Order status
     * @return List of orders matching both criteria
     */
    List<Order> findByCustomerIdAndStatus(Long customerId, String status);
    
    /**
     * Search orders by order number containing the given text
     * @param orderNumber Partial order number
     * @return List of orders matching the pattern
     */
    @Query("SELECT o FROM Order o WHERE o.orderNumber LIKE %:orderNumber%")
    List<Order> findByOrderNumberContaining(@Param("orderNumber") String orderNumber);
    
    /**
     * Find recent orders for a customer (last 30 days)
     * @param customerId Customer ID
     * @return List of recent orders
     */
    @Query("SELECT o FROM Order o WHERE o.customerId = :customerId AND o.createdDate >= CURRENT_DATE - 30 ORDER BY o.createdDate DESC")
    List<Order> findRecentOrdersByCustomerId(@Param("customerId") Long customerId);
} 