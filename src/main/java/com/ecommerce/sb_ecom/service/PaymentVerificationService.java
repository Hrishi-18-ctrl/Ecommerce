package com.ecommerce.sb_ecom.service;

import com.ecommerce.sb_ecom.DTO.RazorpayOrderResult;
import com.ecommerce.sb_ecom.enums.PaymentMethod;
import com.ecommerce.sb_ecom.enums.PaymentStatus;
import com.ecommerce.sb_ecom.exceptions.PaymentVerificationException;
import com.ecommerce.sb_ecom.model.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentVerificationService {

    private final RazorpayService razorpayService;

    public Payment initiatePayment(PaymentMethod paymentMethod, long amountInSmallestUnit, String currency,
                                   String internalReferenceId) {
        Payment payment = new Payment();
        payment.setPaymentMethod(paymentMethod);
        payment.setAmount(amountInSmallestUnit);
        payment.setCurrency(currency);

        switch (paymentMethod) {
            case CASH -> {
                payment.setPaymentStatus(PaymentStatus.SUCCEEDED);
                payment.setPgStatus("PENDING_COLLECTION");
                payment.setPgResponseMessage("Cash on delivery - to be collected on delivery");
                payment.setPgName("COD");
            }
            case UPI, CARD -> {
                RazorpayOrderResult result = razorpayService.createOrder(
                        amountInSmallestUnit, currency, internalReferenceId);

                payment.setPgOrderId(result.orderId());
                payment.setPaymentStatus(PaymentStatus.PENDING);
                payment.setPgStatus("created");
                payment.setPgResponseMessage("Razorpay order created, awaiting payment");
                payment.setPgName("RAZORPAY");
            }
            default -> throw new PaymentVerificationException("Unsupported payment method: " + paymentMethod);
        }

        return payment;
    }

    public boolean confirmPayment(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) {
        if (razorpayOrderId == null || razorpayPaymentId == null || razorpaySignature == null) {
            throw new PaymentVerificationException(
                    "razorpayOrderId, razorpayPaymentId and razorpaySignature are all required to confirm a payment");
        }

        return razorpayService.verifyPaymentSignature(razorpayOrderId, razorpayPaymentId, razorpaySignature);
    }
}