package com.ecommerce.sb_ecom.repository;

import com.ecommerce.sb_ecom.model.Seller;
import com.ecommerce.sb_ecom.model.SellerAddress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerAddressRepository extends JpaRepository<SellerAddress, String> {
}
