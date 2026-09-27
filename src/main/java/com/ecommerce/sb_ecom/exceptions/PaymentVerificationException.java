package com.ecommerce.sb_ecom.exceptions;


public class PaymentVerificationException extends RuntimeException{
    public PaymentVerificationException(String message){
        super(message);
    }
}
