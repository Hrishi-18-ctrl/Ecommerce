package com.ecommerce.sb_ecom.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** The delivery address snapshot stored on an order. */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderAddressResponse {
    private String line1;
    private String line2;
    private String city;
    private String state;
    private String country;
    private Integer pinCode;
}
