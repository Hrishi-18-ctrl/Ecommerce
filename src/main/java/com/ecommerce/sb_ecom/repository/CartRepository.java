package com.ecommerce.sb_ecom.repository;

import com.ecommerce.sb_ecom.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, String> {

    Optional<Cart> findByCustomerId(String customerId);

    // Used by CurrentUser.assertOwnsCart to check a cart id belongs to the caller
    // without loading the whole entity.
    boolean existsByIdAndCustomerId(String id, String customerId);
}
