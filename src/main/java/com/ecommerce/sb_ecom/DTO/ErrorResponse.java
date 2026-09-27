package com.ecommerce.sb_ecom.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
public class ErrorResponse {
    private boolean success;
    private int status;
    private String message;
    private LocalDateTime timestamp;
}
