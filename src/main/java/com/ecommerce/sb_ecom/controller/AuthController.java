package com.ecommerce.sb_ecom.controller;

import com.ecommerce.sb_ecom.DTO.AuthResponse;
import com.ecommerce.sb_ecom.DTO.LoginRequest;
import com.ecommerce.sb_ecom.DTO.RegisterRequest;
import com.ecommerce.sb_ecom.DTO.SellerRegisterRequest;
import com.ecommerce.sb_ecom.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return new ResponseEntity<>(authService.register(request), HttpStatus.CREATED);
    }

    // Public self-serve "become a seller" registration - see AuthService.registerSeller.
    // Under /api/auth/**, which SecurityConfig already leaves public, so no security
    // config change is needed for this endpoint to work.
    @PostMapping("/register/seller")
    public ResponseEntity<AuthResponse> registerSeller(@Valid @RequestBody SellerRegisterRequest request) {
        return new ResponseEntity<>(authService.registerSeller(request), HttpStatus.CREATED);
    }

    // Shared by customers, sellers and admins - AuthResponse carries whichever of
    // customerId/sellerId applies (or neither, for admin).
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return new ResponseEntity<>(authService.login(request), HttpStatus.OK);
    }
}
