package com.ecommerce.sb_ecom.DTO;

import com.ecommerce.sb_ecom.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String token;
    private String userId;
    private String email;
    private UserRole role;

    // Added so a storefront can act straight after sign-in. customerId/cartId are null
    // for SELLER/ADMIN accounts (no customer profile or cart); sellerId is null for
    // CUSTOMER/ADMIN accounts (no seller profile).
    private String customerId;
    private String cartId;
    private String sellerId;
    private String firstName;
    private String lastName;
}
