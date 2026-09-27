package com.ecommerce.sb_ecom.exceptions;


public class InsufficientStockException extends RuntimeException{
    public InsufficientStockException(String message){
        super(message);
    }
}
