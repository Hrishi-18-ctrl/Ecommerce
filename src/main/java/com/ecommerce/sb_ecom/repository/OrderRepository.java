package com.ecommerce.sb_ecom.repository;

import com.ecommerce.sb_ecom.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, String> {

    // Newest first, to match what a shopper expects from an order history list.
    Page<Order> findByCustomerIdOrderByOrderDateDesc(String customerId, Pageable pageable);

    boolean existsByIdAndCustomerId(String id, String customerId);
}
