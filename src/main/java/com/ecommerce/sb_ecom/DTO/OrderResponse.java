package com.ecommerce.sb_ecom.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/** Returned when an order is placed or a payment is confirmed. */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
    // Was "OrderId" (capital O). Lombok's getter/setter names are unchanged
    // (getOrderId/setOrderId), so no calling code needs to change.
    private String orderId;
    private List<OrderItemResponse> orderItems = new ArrayList<>();
    private String orderAddressId;
    private Double orderTotal;
    private String paymentMethod;
    private String paymentId;
    private String orderStatus;

    // Populated only for CARD/UPI orders - null for CASH.
    private String razorpayOrderId;
    private String razorpayKeyId;
}
