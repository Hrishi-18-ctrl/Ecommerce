package com.ecommerce.sb_ecom.exceptions;


public class InvalidCredentialsException extends RuntimeException{
    public InvalidCredentialsException(String message){
        super(message);
    }
}
