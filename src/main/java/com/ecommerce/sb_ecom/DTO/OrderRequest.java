package com.ecommerce.sb_ecom.DTO;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {

    // Only a reference id to a payment the gateway already created/confirmed
    // (e.g. a Razorpay/Stripe payment intent id) is accepted from the client.
    // Whether that payment actually succeeded is verified server-side against
    // the gateway in PaymentVerificationService — never taken on the client's word.
    private String pgPaymentId;

    @NotBlank(message = "cartId is required")
    private String cartId;

    @NotBlank(message = "customerAddressId is required")
    private String customerAddressId;

}
