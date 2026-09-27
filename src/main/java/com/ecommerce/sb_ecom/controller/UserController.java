package com.ecommerce.sb_ecom.controller;

import com.ecommerce.sb_ecom.DTO.UserPageResponse;
import com.ecommerce.sb_ecom.DTO.UserRequest;
import com.ecommerce.sb_ecom.DTO.UserResponse;
import com.ecommerce.sb_ecom.repository.UserRepository;
import com.ecommerce.sb_ecom.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

//    GET ALL USERS
    @GetMapping("/api/users")
    public ResponseEntity<UserPageResponse> getAllUsers(@RequestParam(defaultValue = "0") Integer pageNumber , @RequestParam(defaultValue = "5") Integer pageSize){
        return new ResponseEntity<>(userService.getAllUsers(pageNumber , pageSize) , HttpStatus.OK);
    }
//    CREATE USER
    @PostMapping("/api/users")
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest userRequest){
        return new ResponseEntity<>(userService.createUser(userRequest) , HttpStatus.CREATED);
    }


//    UPDATE USER
    @PatchMapping("api/users/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable String id , @RequestBody UserRequest userRequest){
        return new ResponseEntity<>(userService.updateUser(id , userRequest) , HttpStatus.OK);
    }


//    DELETE USER
    @DeleteMapping("api/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id){
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
