package com.ecommerce.sb_ecom.DTO;

import com.ecommerce.sb_ecom.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerRequest {
    // NOTE: this DTO is shared by both POST (create) and PATCH (partial update).
    // Validation annotations are intentionally left off firstName/lastName/gender
    // because null means "leave unchanged" on update. userId is only used on create;
    // CustomerService.createCustomer already null-checks/looks it up and throws
    // UserNotFoundException if missing/invalid.
    private String userId;
    private String firstName;
    private String lastName;
    private Gender gender;
}
