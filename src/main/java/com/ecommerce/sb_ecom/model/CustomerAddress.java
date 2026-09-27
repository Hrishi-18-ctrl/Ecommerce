package com.ecommerce.sb_ecom.model;


import com.ecommerce.sb_ecom.enums.AddressType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "customerAddresses",
        uniqueConstraints = {
        @UniqueConstraint(name = "uk_customer_address_type",
        columnNames = {
                    "customer_id",
                    "type"})}
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;



    @Enumerated(EnumType.STRING)
    private AddressType type;

    private String line1;
    private String line2;
    private String city;
    private String state;
    private String country;
    private Integer pinCode;


    @ManyToOne
    @JoinColumn(name = "customer_id" , nullable = false , foreignKey = @ForeignKey(name = "fk_address_customer"))
    @JsonIgnore
    private Customer customer;


    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
