package com.ecommerce.sb_ecom.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProductResponse {
    private String id;
    private String name;
    private Double price;
    private Integer stockQuantity;
    private String description;
    private String imageUrl;
    private String category_id;
    private String sellerId;


    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
