package com.ecommerce.sb_ecom.controller;


import com.ecommerce.sb_ecom.DTO.*;
import com.ecommerce.sb_ecom.exceptions.CustomerAlreadyExistsException;
import com.ecommerce.sb_ecom.service.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class SellerController {


    private final SellerService sellerService;


    //    GET ALL SELLERS
    @GetMapping("/api/users/sellers")
    public ResponseEntity<SellerPageResponse> getAllSellers(@RequestParam(defaultValue = "0") Integer pageNumber ,
                                                              @RequestParam(defaultValue = "5") Integer pageSize)
    {
        return new ResponseEntity<>(sellerService.getAllSellers(pageNumber , pageSize) , HttpStatus.OK);

    }


    //    CREATE SELLER
    @PostMapping("/api/users/sellers")
    public ResponseEntity<SellerResponse> createSeller(@RequestBody SellerRequest sellerRequest) throws RuntimeException {
        return new ResponseEntity<>(sellerService.createSeller(sellerRequest) , HttpStatus.CREATED);
    }


    //    UPDATE SELLERS
    @PatchMapping("/api/users/sellers/{id}")
    public ResponseEntity<SellerResponse> updateCustomer(@RequestBody SellerRequest sellerRequest , @PathVariable String id){
        return new ResponseEntity<>(sellerService.updateSeller(sellerRequest , id) , HttpStatus.OK);
    }
}
