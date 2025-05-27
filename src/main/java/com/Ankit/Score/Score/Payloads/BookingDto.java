package com.Ankit.Score.Score.Payloads;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDto {

    private Long bookingId;
    private Long userId;
    private Long slotId;

    private String userName;
    private String userMobileNo;

    private LocalDateTime bookingTime;
    private LocalDateTime bookingEndTime;

    private String paymentStatus;
    private String status;


}
