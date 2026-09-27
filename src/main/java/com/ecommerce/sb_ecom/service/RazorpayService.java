package com.ecommerce.sb_ecom.service;

import com.ecommerce.sb_ecom.DTO.RazorpayOrderResult;
import com.ecommerce.sb_ecom.config.RazorpayConfig;
import com.ecommerce.sb_ecom.exceptions.PaymentVerificationException;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

/**
 * The only class in this codebase that imports com.razorpay.*.
 * Every other class talks to RazorpayOrderResult / plain booleans, never
 * to Razorpay's own SDK types — keeps a future gateway swap contained here.
 */
@Service
@RequiredArgsConstructor
public class RazorpayService {

    private final RazorpayClient razorpayClient;

    // Injected separately rather than pulled off razorpayClient - the SDK
    // does not expose the secret back out of an instantiated client.
    private final RazorpayConfig razorpayConfig;

    public RazorpayOrderResult createOrder(long amountInSmallestUnit, String currency, String internalReferenceId) {
        try {
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amountInSmallestUnit);
            orderRequest.put("currency", currency);
            orderRequest.put("receipt", internalReferenceId);
            orderRequest.put("payment_capture", true);

            Order order = razorpayClient.orders.create(orderRequest);

            return new RazorpayOrderResult(
                    order.get("id"),
                    amountInSmallestUnit,
                    currency
            );
        } catch (RazorpayException e) {
            throw new PaymentVerificationException("Failed to create Razorpay order: " + e.getMessage());
        }
    }

    public boolean verifyPaymentSignature(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) {
        try {
            JSONObject attributes = new JSONObject();
            attributes.put("razorpay_order_id", razorpayOrderId);
            attributes.put("razorpay_payment_id", razorpayPaymentId);
            attributes.put("razorpay_signature", razorpaySignature);

            return Utils.verifyPaymentSignature(attributes, razorpayConfig.getKeySecret());
        } catch (RazorpayException e) {
            return false;
        }
    }
}