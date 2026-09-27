package com.ecommerce.sb_ecom.controller;

import com.ecommerce.sb_ecom.DTO.AddressRequest;
import com.ecommerce.sb_ecom.DTO.AddressResponse;
import com.ecommerce.sb_ecom.DTO.SellerAddressRequest;
import com.ecommerce.sb_ecom.exceptions.DuplicateAddressTypeException;
import com.ecommerce.sb_ecom.service.SellerAddressService;
import com.ecommerce.sb_ecom.service.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
public class SellerAddressController {

    private final SellerAddressService sellerAddressService;

    //    GET ALL ADDRESSES
    @GetMapping("/api/users/sellers/addresses")
    public ResponseEntity<List<AddressResponse>> getAllAddresses(){
        return new ResponseEntity<>(sellerAddressService.getAllAddress() , HttpStatus.OK);
    }


    //    GET ADDRESSES BY SELLER ID
    @GetMapping("/api/users/sellers/{id}")
    public ResponseEntity<AddressResponse> getAddressBySellerId(@PathVariable String id){
        return new ResponseEntity<>(sellerAddressService.getAddressesBySellerId(id) , HttpStatus.OK);
    }


    //    CREATE ADDRESS
    @PostMapping("/api/users/sellers/addresses")
    public ResponseEntity<AddressResponse> createAddress(@RequestBody SellerAddressRequest addressRequest) throws RuntimeException {
        return new ResponseEntity<>(sellerAddressService.createAddress(addressRequest) , HttpStatus.CREATED);
    }

    //    UPDATE ADDRESS
    @PatchMapping("/api/users/sellers/addresses/{id}")
    public ResponseEntity<AddressResponse> updateAddress(@PathVariable String id , @RequestBody AddressRequest addressRequest){
        return new ResponseEntity<>(sellerAddressService.updateAddress(id , addressRequest) , HttpStatus.OK);
    }
}
