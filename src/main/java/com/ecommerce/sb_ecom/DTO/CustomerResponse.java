package com.ecommerce.sb_ecom.DTO;

import com.ecommerce.sb_ecom.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerResponse {
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private Gender gender;

    private String user_id;
    private String cart_id;

//    home address

}
