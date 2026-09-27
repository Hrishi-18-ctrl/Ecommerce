package com.ecommerce.sb_ecom.exceptions;


public class CategoryNotFoundException extends RuntimeException{
    public CategoryNotFoundException(String message){
        super(message);
    }
}
