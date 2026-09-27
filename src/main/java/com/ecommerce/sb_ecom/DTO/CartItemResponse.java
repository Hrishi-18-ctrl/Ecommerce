package com.ecommerce.sb_ecom.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A cart line as the storefront needs it. Replaces returning the raw CartItem entity,
 * whose product field is @JsonIgnore'd (so clients never saw a name, price or image).
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemResponse {
    private String id;
    private Integer quantity;
    private String productId;
    private String name;
    private Double price;
    private String imageUrl;
    private Integer stockQuantity;
}
