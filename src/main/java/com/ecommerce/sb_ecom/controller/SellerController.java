package com.ecommerce.sb_ecom.controller;

import com.ecommerce.sb_ecom.DTO.SellerPageResponse;
import com.ecommerce.sb_ecom.DTO.SellerRequest;
import com.ecommerce.sb_ecom.DTO.SellerResponse;
import com.ecommerce.sb_ecom.security.CurrentUser;
import com.ecommerce.sb_ecom.service.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class SellerController {


    private final SellerService sellerService;
    private final CurrentUser currentUser;


    //    GET ALL SELLERS (admin only)
    @GetMapping("/api/users/sellers")
    public ResponseEntity<SellerPageResponse> getAllSellers(@RequestParam(defaultValue = "0") Integer pageNumber ,
                                                              @RequestParam(defaultValue = "5") Integer pageSize)
    {
        currentUser.requireAdmin();
        return new ResponseEntity<>(sellerService.getAllSellers(pageNumber , pageSize) , HttpStatus.OK);

    }


    //    CREATE SELLER (admin only - self-registration via /api/auth/register/seller now
    //    creates the Seller profile automatically; see AuthService.registerSeller)
    @PostMapping("/api/users/sellers")
    public ResponseEntity<SellerResponse> createSeller(@RequestBody SellerRequest sellerRequest) throws RuntimeException {
        currentUser.requireAdmin();
        return new ResponseEntity<>(sellerService.createSeller(sellerRequest) , HttpStatus.CREATED);
    }


    //    UPDATE SELLER (the seller themself, or an admin)
    @PatchMapping("/api/users/sellers/{id}")
    public ResponseEntity<SellerResponse> updateCustomer(@RequestBody SellerRequest sellerRequest , @PathVariable String id){
        currentUser.assertOwnsSeller(id);
        return new ResponseEntity<>(sellerService.updateSeller(sellerRequest , id) , HttpStatus.OK);
    }
}
