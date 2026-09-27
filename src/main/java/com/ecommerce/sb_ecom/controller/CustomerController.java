package com.ecommerce.sb_ecom.controller;

import com.ecommerce.sb_ecom.DTO.CustomerPageResponse;
import com.ecommerce.sb_ecom.DTO.CustomerRequest;
import com.ecommerce.sb_ecom.DTO.CustomerResponse;
import com.ecommerce.sb_ecom.exceptions.CustomerAlreadyExistsException;
import com.ecommerce.sb_ecom.security.CurrentUser;
import com.ecommerce.sb_ecom.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final CurrentUser currentUser;

//    GET ALL CUSTOMERS (admin only)
    @GetMapping("/api/users/customers")
    public ResponseEntity<CustomerPageResponse> getAllCustomers(@RequestParam(defaultValue = "0") Integer pageNumber ,
                                                                @RequestParam(defaultValue = "5") Integer pageSize)
    {
        currentUser.requireAdmin();
        return new ResponseEntity<>(customerService.getAllCustomers(pageNumber , pageSize) , HttpStatus.OK);

    }

//    CREATE CUSTOMER (admin only - self-registration now creates the Customer profile
//    automatically; see AuthService.register)
    @PostMapping("/api/users/customers")
    public ResponseEntity<CustomerResponse> createCustomer(@RequestBody CustomerRequest customerRequest) throws CustomerAlreadyExistsException {
        currentUser.requireAdmin();
        return new ResponseEntity<>(customerService.createCustomer(customerRequest) , HttpStatus.CREATED);
    }

//    UPDATE CUSTOMER (the customer themself, or an admin)
    @PatchMapping("/api/users/customers/{id}")
    public ResponseEntity<CustomerResponse> updateCustomer(@RequestBody CustomerRequest customerRequest , @PathVariable String id){
        currentUser.assertOwnsCustomer(id);
        return new ResponseEntity<>(customerService.updateCustomer(customerRequest , id) , HttpStatus.OK);
    }

}
