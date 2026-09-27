package com.ecommerce.sb_ecom.exceptions;

import java.sql.SQLIntegrityConstraintViolationException;

public class DuplicateAddressTypeException extends SQLIntegrityConstraintViolationException {
    public DuplicateAddressTypeException(String message) {
        super(message);
    }
}
