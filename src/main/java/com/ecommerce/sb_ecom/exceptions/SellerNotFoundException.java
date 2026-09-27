package com.ecommerce.sb_ecom.exceptions;

public class SellerNotFoundException extends RuntimeException{
    public SellerNotFoundException(String message){
        super(message);
    }
}
