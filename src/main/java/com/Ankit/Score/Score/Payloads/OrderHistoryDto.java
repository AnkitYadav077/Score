package com.Ankit.Score.Score.Payloads;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderHistoryDto {
    private String orderType; // "FOOD" or "SLOT"
    private Long orderId;
    private LocalDateTime orderDateTime;
    private String details;
}