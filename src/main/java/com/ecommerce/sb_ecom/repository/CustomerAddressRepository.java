package com.ecommerce.sb_ecom.repository;

import com.ecommerce.sb_ecom.model.CustomerAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerAddressRepository extends JpaRepository<CustomerAddress , String> {
    List<CustomerAddress> findByCustomerId(String id);
    Optional<CustomerAddress> findByIdAndCustomerId(String id , String customerId);
}
