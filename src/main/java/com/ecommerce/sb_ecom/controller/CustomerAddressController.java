package com.ecommerce.sb_ecom.controller;

import com.ecommerce.sb_ecom.DTO.AddressRequest;
import com.ecommerce.sb_ecom.DTO.AddressResponse;
import com.ecommerce.sb_ecom.exceptions.DuplicateAddressTypeException;
import com.ecommerce.sb_ecom.security.CurrentUser;
import com.ecommerce.sb_ecom.service.CustomerAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CustomerAddressController {

    private final CustomerAddressService customerAddressService;
    private final CurrentUser currentUser;

//    GET ALL ADDRESSES (admin only - see CartController.getAllCarts for the same pattern)
    @GetMapping("/api/users/customers/addresses")
    public ResponseEntity<List<AddressResponse>> getAllAddresses(){
        currentUser.requireAdmin();
        return new ResponseEntity<>(customerAddressService.getAllAddress() , HttpStatus.OK);
    }


//    GET ADDRESSES BY CUSTOMER ID
    @GetMapping("/api/users/customers/{id}")
    public ResponseEntity<List<AddressResponse>> getAddressesByCustomerId(@PathVariable String id){
        currentUser.assertOwnsCustomer(id);
        return new ResponseEntity<>(customerAddressService.getAddressesByCustomerId(id) , HttpStatus.OK);
    }


//    CREATE ADDRESS
    @PostMapping("/api/users/customers/addresses")
    public ResponseEntity<AddressResponse> createAddress(@RequestBody AddressRequest addressRequest) throws DuplicateAddressTypeException {
        currentUser.assertOwnsCustomer(addressRequest.getCustomerId());
        return new ResponseEntity<>(customerAddressService.createAddress(addressRequest) , HttpStatus.CREATED);
    }

//    UPDATE ADDRESS
    @PatchMapping("/api/users/customers/addresses/{id}")
    public ResponseEntity<AddressResponse> updateAddress(@PathVariable String id , @RequestBody AddressRequest addressRequest){
        currentUser.assertOwnsAddress(id);
        return new ResponseEntity<>(customerAddressService.updateAddress(id , addressRequest) , HttpStatus.OK);
    }

//    DELETE ADDRESS
    @DeleteMapping("/api/users/customers/addresses/{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable String id){
        currentUser.assertOwnsAddress(id);
        customerAddressService.deleteAddress(id);
        return ResponseEntity.noContent().build();
    }
}
