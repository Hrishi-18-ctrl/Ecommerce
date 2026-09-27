package com.ecommerce.sb_ecom.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "orderItems")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id" , foreignKey = @ForeignKey(name = "fk_orderItem_order") , nullable = false)
    @JsonIgnore
    private Order order;


    @ManyToOne
    @JoinColumn(name = "product_id" , foreignKey = @ForeignKey(name = "fk_orderItem_product") , nullable = false)
    @JsonIgnore
    private Product product;

    private Integer quantity;
    private Double discount;
    private Double finalPrice;


}
