package com.ecommerce.sb_ecom.DTO;


import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateItemQuantityRequest {

    @Positive(message = "addQuantity must be greater than 0")
    private Integer addQuantity;

    @Positive(message = "removeQuantity must be greater than 0")
    private Integer removeQuantity;
}
