package com.ecommerce.sb_ecom.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "cartItems")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private Integer quantity;


    @ManyToOne
    @JoinColumn(name = "cart_id" , foreignKey = @ForeignKey(name = "fk_cartItem_cart") , nullable = false)
    @JsonIgnore
    private Cart cart;


    @ManyToOne
    @JoinColumn(name = "product_id" , foreignKey = @ForeignKey(name = "fk_cartItem_product") , nullable = false)
    @JsonIgnore
    private Product product;



    @CreationTimestamp
    private LocalDateTime createdAt;


    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
