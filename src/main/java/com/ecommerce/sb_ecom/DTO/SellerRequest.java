package com.ecommerce.sb_ecom.DTO;

import com.ecommerce.sb_ecom.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class SellerRequest {
    private String userId;
    private String firstName;
    private String lastName;
}
