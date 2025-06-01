package com.Ankit.Score.Score.Payloads;

import lombok.Data;

@Data
public class BookingPaymentRequest {
    private Long userId;
    private Long slotId;
    private String orderId;
    private String paymentId;
    private String signature;
}
