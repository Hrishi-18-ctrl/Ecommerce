package com.ecommerce.sb_ecom.model;


import com.ecommerce.sb_ecom.enums.AddressType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "sellerAddresses")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SellerAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;



    private String line1;
    private String line2;
    private String city;
    private String state;
    private String country;
    private Integer pinCode;


    @OneToOne
    @JoinColumn(name = "seller_id" , nullable = false , foreignKey = @ForeignKey(name = "fk_address_seller"))
    @JsonIgnore
    private Seller seller;


    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
