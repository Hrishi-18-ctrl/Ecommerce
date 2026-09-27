package com.ecommerce.sb_ecom.controller;

import com.ecommerce.sb_ecom.DTO.CartItemResponse;
import com.ecommerce.sb_ecom.DTO.CartPageResponse;
import com.ecommerce.sb_ecom.DTO.CartRequest;
import com.ecommerce.sb_ecom.DTO.CartResponse;
import com.ecommerce.sb_ecom.DTO.UpdateItemQuantityRequest;
import com.ecommerce.sb_ecom.security.CurrentUser;
import com.ecommerce.sb_ecom.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final CurrentUser currentUser;

//    ADD ITEM TO CART
    @PostMapping("/api/customers/carts")
    public ResponseEntity<CartItemResponse> addItemToCart(@Valid @RequestBody CartRequest cartRequest){
        currentUser.assertOwnsCart(cartRequest.getCartId());
        return new ResponseEntity<>(cartService.addItemToCart(cartRequest) , HttpStatus.CREATED);
    }


//    DELETE ITEM FROM A CART
    @DeleteMapping("/api/customers/carts/{cartId}/{cartItemId}")
    public ResponseEntity<Void> deleteItemFromCart(@PathVariable String cartId , @PathVariable String cartItemId){
        currentUser.assertOwnsCart(cartId);
        cartService.deleteItemFromCart(cartId , cartItemId);
        return ResponseEntity.noContent().build();
    }


//    GET CART BY CUSTOMER ID
    @GetMapping("/api/customers/carts/{customerId}")
    public ResponseEntity<CartResponse> getCartByCustomerId(@PathVariable String customerId){
        currentUser.assertOwnsCustomer(customerId);
        return new ResponseEntity<>(cartService.getCartByCustomerId(customerId) , HttpStatus.OK);
    }


//    GET ALL CARTS (admin only - enforced by SecurityConfig's /api/customers/carts/** rule
//    plus the explicit check below, since a non-admin having gotten this far would
//    otherwise see every customer's cart)
    @GetMapping("/api/customers/carts/")
    public ResponseEntity<CartPageResponse> getAllCarts(@RequestParam(defaultValue = "0") Integer pageNumber ,
                                                        @RequestParam(defaultValue = "5") Integer pageSize)
    {
        currentUser.requireAdmin();
        return new ResponseEntity<>(cartService.getAllCarts(pageNumber , pageSize) , HttpStatus.OK);
    }


//    UPDATE QUANTITY OF ITEM IN A CART
    @PatchMapping("/api/customers/carts/{cartId}/{cartItemId}")
    public ResponseEntity<CartItemResponse> updateQuantityOfItem(@Valid @RequestBody UpdateItemQuantityRequest updateItemQuantityRequest ,
                                                         @PathVariable String cartId ,
                                                         @PathVariable String cartItemId)
    {
        currentUser.assertOwnsCart(cartId);
        return new ResponseEntity<>(cartService.updateQuantityOfItem(updateItemQuantityRequest , cartId , cartItemId) , HttpStatus.OK);
    }


}
