package com.ecommerce.sb_ecom.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartResponse {
    private String cartId;
    private List<CartItemResponse> cartItems = new ArrayList<>();

    private Integer totalItems;
    private Double totalPrice;
    private Double discountAmount;
    private Double finalPrice;
}
