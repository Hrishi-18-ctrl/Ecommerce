package com.ecommerce.sb_ecom.model;

import com.ecommerce.sb_ecom.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role;


    @OneToOne(mappedBy = "user" , cascade = CascadeType.ALL , orphanRemoval = true , fetch = FetchType.LAZY)
    @JsonIgnore
    private Customer customer;


    @OneToOne(mappedBy = "user" , cascade = CascadeType.ALL , orphanRemoval = true , fetch = FetchType.LAZY)
    @JsonIgnore
    private Seller seller;


    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
