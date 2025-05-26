package com.Ankit.Score.Score.Payloads;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentVerificationRequest {
    private String orderId;
    private String paymentId;
    private String signature;

    private Long foodOrderId; // Optional, if payment is for FoodOrder
    private Long bookingId;   // Optional, if payment is for Booking
}
