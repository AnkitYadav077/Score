package com.Ankit.Score.Score.Payloads;

import lombok.*;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FoodOrderDto {

    private Long orderId;

    private Long userId;
    private String name;
    private String mobileNo;

    private Long foodId;
    private String foodName;

    private int quantity;
    private LocalTime orderAt;

    private int price;
    private int totalAmount;

    private String status;             // PENDING, PREPARING, READY, DELIVERED, CANCELLED
    private String specialInstructions;

    private String paymentStatus;      // PENDING, PAID, REFUNDED
    private String paymentMethod;// CASH, CARD, UPI

}
