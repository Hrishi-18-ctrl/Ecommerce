package com.ecommerce.sb_ecom.repository;

import com.ecommerce.sb_ecom.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem , String> {
    List<OrderItem> findByOrderId(String orderId);
}
