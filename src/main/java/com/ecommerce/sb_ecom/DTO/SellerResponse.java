package com.ecommerce.sb_ecom.DTO;

import com.ecommerce.sb_ecom.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class SellerResponse {
    private String id;
    private String email;
    private String firstName;
    private String lastName;

    private String user_id;
    private String addressId;


}
