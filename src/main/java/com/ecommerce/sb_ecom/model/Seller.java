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
@Table(name = "sellers")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Seller {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;


    private String firstName;
    private String lastName;



    @OneToOne
    @JoinColumn(name = "user_id" , foreignKey = @ForeignKey(name = "fk_seller_user") , nullable = false)
    @JsonIgnore
    private User user;



    @OneToOne(mappedBy = "seller" , cascade = CascadeType.ALL , fetch = FetchType.LAZY , orphanRemoval = true)
    @JsonIgnore
    private SellerAddress address;




    @OneToMany(mappedBy = "seller" , cascade = CascadeType.ALL , fetch = FetchType.LAZY , orphanRemoval = true)
    @JsonIgnore
    private List<Product> products  = new ArrayList<>();





    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
