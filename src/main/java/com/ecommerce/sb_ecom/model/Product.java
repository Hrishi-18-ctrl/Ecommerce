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
@Table(name = "products")
@Builder
@Getter
@Setter
@ToString(exclude = "category")
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;

    private Double price;


    private String description;

    private String imageUrl;

    private Integer stockQuantity;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id" ,nullable = false, foreignKey = @ForeignKey(name = "fk_product_category"))
    @JsonIgnore
    private Category category;  // yaha par tujhe category ka object daalna hoga


    @OneToMany(mappedBy = "product" , cascade = CascadeType.ALL , orphanRemoval = true)
    @JsonIgnore
    private List<CartItem> cartItems;

    @ManyToOne
    @JoinColumn(name = "seller_id" , nullable = false , foreignKey = @ForeignKey(name = "fk_product_seller"))
    @JsonIgnore
    private Seller seller;

    @OneToMany(mappedBy = "product" , fetch = FetchType.LAZY)
    @JsonIgnore
    private List<OrderItem> orderItem = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
