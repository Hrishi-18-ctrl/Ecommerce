package com.ecommerce.sb_ecom.DTO;

import com.ecommerce.sb_ecom.enums.AddressType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class SellerAddressRequest {
    private String sellerId;
    private AddressType addressType;
    private String line1;
    private String line2;
    private String city;
    private String state;
    private String country;
    private Integer pinCode;
}
