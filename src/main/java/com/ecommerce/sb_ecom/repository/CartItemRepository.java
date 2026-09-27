package com.ecommerce.sb_ecom.repository;

import com.ecommerce.sb_ecom.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem , String> {
    Optional<CartItem> findByCartIdAndProductId(String cartId, String productId);
    List<CartItem> findByCartId(String id);
    void deleteByCartId(String cartId);

}
