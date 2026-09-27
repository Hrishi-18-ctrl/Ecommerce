package com.ecommerce.sb_ecom.DTO;

// Internal result of creating a Razorpay order — deliberately not Razorpay's
// own SDK Order type, so only RazorpayService ever imports com.razorpay.*.
// If we ever swap gateways again, this is the only shape other services
// depend on.
public record RazorpayOrderResult(
        String orderId,
        long amount,
        String currency
) {
}