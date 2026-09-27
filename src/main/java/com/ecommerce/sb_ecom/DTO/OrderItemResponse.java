package com.ecommerce.sb_ecom.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** One line of an order. price is the unit price the customer was charged. */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemResponse {
    private String id;
    private String productId;
    private String name;
    private String imageUrl;
    private Integer quantity;
    private Double price;
}
