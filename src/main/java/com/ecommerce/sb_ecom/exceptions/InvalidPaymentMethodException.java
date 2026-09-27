package com.ecommerce.sb_ecom.exceptions;


public class InvalidPaymentMethodException extends RuntimeException{
    public InvalidPaymentMethodException(String message){
        super(message);
    }
}
