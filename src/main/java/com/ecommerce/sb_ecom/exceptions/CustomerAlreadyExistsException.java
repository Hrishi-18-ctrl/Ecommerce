package com.ecommerce.sb_ecom.exceptions;

import java.sql.SQLIntegrityConstraintViolationException;

public class CustomerAlreadyExistsException extends SQLIntegrityConstraintViolationException {
    public CustomerAlreadyExistsException(String message){
        super(message);
    }
}
