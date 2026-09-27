package com.ecommerce.sb_ecom.model;


import com.ecommerce.sb_ecom.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "orders")
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id" , foreignKey = @ForeignKey(name = "fk_order_customer") , nullable = false)
    @JsonIgnore
    private Customer customer;


    @OneToMany(mappedBy = "order" , orphanRemoval = true , cascade = CascadeType.ALL , fetch = FetchType.LAZY)
    @JsonIgnore
    private List<OrderItem> orderItems = new ArrayList<>();

    private LocalDate orderDate;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "payment_id" , foreignKey = @ForeignKey(name = "fk_order_payment"))
    @JsonIgnore
    private Payment payment;

    private Double totalAmount;


    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @ManyToOne(fetch = FetchType.LAZY , cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id" , foreignKey = @ForeignKey(name = "fk_order_orderAddress") , nullable = false)
    @JsonIgnore
    private OrderAddress address;


}