package com.ecommerce.sb_ecom.DTO;

import com.ecommerce.sb_ecom.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private String id;
    private UserRole role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

