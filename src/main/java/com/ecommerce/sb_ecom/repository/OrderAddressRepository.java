package com.ecommerce.sb_ecom.repository;

import com.ecommerce.sb_ecom.model.OrderAddress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderAddressRepository extends JpaRepository<OrderAddress , String> {

}
