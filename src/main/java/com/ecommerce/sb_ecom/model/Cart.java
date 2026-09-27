package com.ecommerce.sb_ecom.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carts")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;


    private Integer totalItems;
    private Double totalPrice;
    private Double discountAmount;
    private Double finalPrice;



    @OneToOne
    @JoinColumn(name = "customer_id" , foreignKey = @ForeignKey(name = "fk_cart_customer"))
    @JsonIgnore
    private Customer customer;

    @OneToMany(mappedBy = "cart" , cascade = CascadeType.ALL , orphanRemoval = true)
    @JsonIgnore
    private List<CartItem> cartItems;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
