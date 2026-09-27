package com.ecommerce.sb_ecom.controller;


import com.ecommerce.sb_ecom.DTO.OrderPageResponse;
import com.ecommerce.sb_ecom.DTO.OrderRequest;
import com.ecommerce.sb_ecom.DTO.OrderResponse;
import com.ecommerce.sb_ecom.DTO.OrderSummaryResponse;
import com.ecommerce.sb_ecom.DTO.PaymentConfirmationRequest;
import com.ecommerce.sb_ecom.enums.PaymentMethod;
import com.ecommerce.sb_ecom.security.CurrentUser;
import com.ecommerce.sb_ecom.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final CurrentUser currentUser;

    @PostMapping("/api/users/customers/orders/{paymentMethod}")
    public ResponseEntity<OrderResponse> placeOrder(@PathVariable PaymentMethod paymentMethod , @Valid @RequestBody OrderRequest orderRequest){
        currentUser.assertOwnsCart(orderRequest.getCartId());
        return new ResponseEntity<>(orderService.placeOrder(paymentMethod , orderRequest) , HttpStatus.CREATED);
    }

    // Called by the frontend right after Razorpay Checkout completes, with
    // the razorpay_order_id / razorpay_payment_id / razorpay_signature it
    // received back from Razorpay's widget. Only relevant for CARD/UPI
    // orders - CASH orders are already PLACED by the time this could be called.
    @PostMapping("/api/users/customers/orders/{orderId}/confirm-payment")
    public ResponseEntity<OrderResponse> confirmPayment(@PathVariable String orderId,
                                                        @Valid @RequestBody PaymentConfirmationRequest confirmationRequest) {
        currentUser.assertOwnsOrder(orderId);
        return new ResponseEntity<>(orderService.confirmPayment(orderId, confirmationRequest), HttpStatus.OK);
    }

    // GET the signed-in customer's own order history. Must be mapped before
    // /api/users/customers/orders/{paymentMethod} - it's a literal segment, so Spring
    // matches it first regardless of declaration order, but keeping it here too for
    // readability.
    @GetMapping("/api/users/customers/orders")
    public ResponseEntity<OrderPageResponse> listOrders(@RequestParam(defaultValue = "0") Integer pageNumber,
                                                        @RequestParam(defaultValue = "10") Integer pageSize) {
        return new ResponseEntity<>(orderService.listOrdersForCustomer(currentUser.customerId(), pageNumber, pageSize), HttpStatus.OK);
    }

    @GetMapping("/api/users/customers/orders/{orderId}")
    public ResponseEntity<OrderSummaryResponse> getOrder(@PathVariable String orderId) {
        currentUser.assertOwnsOrder(orderId);
        return new ResponseEntity<>(orderService.getOrderDetail(orderId), HttpStatus.OK);
    }
}
