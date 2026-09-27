package com.ecommerce.sb_ecom.repository;

import com.ecommerce.sb_ecom.model.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SellerRepository extends JpaRepository<Seller, String> {
    Optional<Seller> findByUserId(String userId);
}
