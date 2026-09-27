package com.ecommerce.sb_ecom.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orderAddresses")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;


    @OneToMany(mappedBy = "address" , fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Order> orders = new ArrayList<>();

    private String line1;
    private String line2;
    private String city;
    private String state;
    private String country;
    private Integer pinCode;
}
