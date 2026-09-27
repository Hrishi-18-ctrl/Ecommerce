package com.ecommerce.sb_ecom.model;

import com.ecommerce.sb_ecom.enums.PaymentMethod;
import com.ecommerce.sb_ecom.enums.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne(mappedBy = "payment")
    @JsonIgnore
    private Order order;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    // Amount actually charged, in the smallest currency unit (e.g. paise for INR).
    // Snapshotted here rather than read off Order, so this record stays accurate
    // even if Order's amount is edited later.
    private Long amount;

    private String currency;

    // Razorpay's order id (rzp_order_...), created before the customer pays.
    // Not a completed-payment id — that arrives separately as pgPaymentId below.
    private String pgOrderId;

    // Razorpay's payment id (rzp_payment_...), only populated once a payment
    // attempt against pgOrderId has actually been made (success or failure).
    private String pgPaymentId;

    // Raw status string as reported by the gateway - kept verbatim for audit
    // and debugging. Business logic should never branch on this directly.
    private String pgStatus;

    private String pgResponseMessage;

    private String pgName;

    // Normalized status your own code should branch on instead of pgStatus.
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Payment(String paymentId, String pgPaymentId, String pgStatus, String pgResponseMessage, String pgName) {
        this.id = paymentId;
        this.pgPaymentId = pgPaymentId;
        this.pgStatus = pgStatus;
        this.pgResponseMessage = pgResponseMessage;
        this.pgName = pgName;
    }
}