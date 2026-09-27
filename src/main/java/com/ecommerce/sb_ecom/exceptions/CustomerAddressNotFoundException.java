package com.ecommerce.sb_ecom.exceptions;

public class CustomerAddressNotFoundException extends RuntimeException {
    public CustomerAddressNotFoundException(String message) {
        super(message);
    }
}
