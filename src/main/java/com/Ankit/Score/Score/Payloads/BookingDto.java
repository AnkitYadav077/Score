package com.Ankit.Score.Score.Payloads;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
    private Long bookingId;
    private Long userId;
    private Long slotId;
    private String userName;
    private String userMobileNo;
    private String paymentStatus;
    private String status;
    private LocalDateTime bookingStartTime;  // <== ADD THIS
    private LocalDateTime bookingEndTime;
    private LocalDate bookingDate;
    private Integer totalPrice;
}
