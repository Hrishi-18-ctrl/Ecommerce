package com.ecommerce.sb_ecom.model;

import com.ecommerce.sb_ecom.enums.Gender;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customers")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;


    private String firstName;
    private String lastName;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @OneToOne
    @JoinColumn(name = "user_id" , foreignKey = @ForeignKey(name = "fk_customer_user") , nullable = false)
    @JsonIgnore
    private User user;


    @OneToMany(mappedBy = "customer" , cascade = CascadeType.ALL , fetch = FetchType.LAZY , orphanRemoval = true)
    @JsonIgnore
    private List<CustomerAddress> addressList = new ArrayList<>();


    @OneToOne(mappedBy = "customer" , orphanRemoval = true , cascade = CascadeType.ALL , fetch = FetchType.LAZY)
    @JsonIgnore
    private Cart cart;


    @OneToMany(mappedBy = "customer" , orphanRemoval = true , cascade = CascadeType.ALL , fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Order> orders = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
