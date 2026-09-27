package com.ecommerce.sb_ecom.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Used for both the order history list and the order detail page. */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderSummaryResponse {
    private String orderId;
    private LocalDate orderDate;
    private String orderStatus;
    private Double orderTotal;
    private String paymentMethod;
    private String paymentId;

    // Only present while an online (UPI/CARD) order is still PENDING_PAYMENT, so the
    // storefront can reopen Razorpay Checkout for an abandoned payment. Null otherwise.
    private String razorpayOrderId;
    private String razorpayKeyId;

    private OrderAddressResponse address;
    private List<OrderItemResponse> items = new ArrayList<>();
}
