package com.Ankit.Score.Score.Payloads;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentVerificationRequest {
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String razorpaySignature;

    private Long userId;       // add this
    private Double amount;
}
