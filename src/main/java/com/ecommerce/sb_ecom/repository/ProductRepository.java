package com.ecommerce.sb_ecom.repository;

import com.ecommerce.sb_ecom.model.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, String> {

    // Locks the row until the enclosing transaction commits, so two concurrent
    // orders for the last unit of stock cannot both read "1 left" and both succeed.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdForUpdate(@Param("id") String id);

    // Was findByName (exact match only, case-sensitive) - a search for "earbud" or
    // "EARBUDS" found nothing. Containing + IgnoreCase makes /products/search behave
    // like the search box shoppers expect.
    Page<Product> findByNameContainingIgnoreCase(Pageable pageable, String name);

    Page<Product> findByCategoryId(Pageable pageable, String id);

    // Backs GET /api/admin/products/mine - a seller's own product listing.
    Page<Product> findBySellerId(Pageable pageable, String sellerId);
}
