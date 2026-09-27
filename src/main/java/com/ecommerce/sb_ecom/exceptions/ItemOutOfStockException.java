package com.ecommerce.sb_ecom.exceptions;

public class ItemOutOfStockException extends RuntimeException{
    public ItemOutOfStockException(String message){
        super(message);
    }
}
