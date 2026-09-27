package com.ecommerce.sb_ecom.exceptions;


public class InvalidRequestException extends RuntimeException{
    public InvalidRequestException(String message){
        super(message);
    }
}
