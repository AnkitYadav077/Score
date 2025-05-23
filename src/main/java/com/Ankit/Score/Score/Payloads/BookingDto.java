package com.Ankit.Score.Score.Payloads;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {

    private Long bookingId;
    private Long userId;
    private Long slotId;
    private LocalDateTime bookingTime;
    private LocalDateTime bookingEndTime;
    private String userName;
    private String userMobileNo;
}
