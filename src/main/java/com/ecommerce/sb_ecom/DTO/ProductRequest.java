package com.ecommerce.sb_ecom.DTO;


import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {

    @NotBlank(message = "categoryId is required")
    private String categoryId;

    @NotBlank(message = "name is required")
    private String name;

    @NotNull(message = "price is required")
    @Positive(message = "price must be greater than 0")
    private Double price;

    @NotNull(message = "stockQuantity is required")
    @PositiveOrZero(message = "stockQuantity cannot be negative")
    private Integer stockQuantity;

    private String imageUrl;
    private String description;

    @NotBlank(message = "sellerId is required")
    private String sellerId;

}
