package com.ecommerce.sb_ecom.repository;

import com.ecommerce.sb_ecom.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category , String> {

    Optional<Category> findByName(String name);

}
