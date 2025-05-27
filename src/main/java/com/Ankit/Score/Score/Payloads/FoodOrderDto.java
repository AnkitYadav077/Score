package com.Ankit.Score.Score.Payloads;

import lombok.*;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodOrderDto {

    private Long orderId;

    private Long userId;
    private String userName;
    private String userMobileNo;

    private Long foodId;
    private String foodName;

    private int quantity;
    private LocalTime orderAt;

    private String paymentStatus;
    private String status;
}
