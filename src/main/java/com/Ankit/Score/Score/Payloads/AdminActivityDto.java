package com.Ankit.Score.Score.Payloads;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminActivityDto {
    private Long id;
    private String action;
    private String description;
    private LocalDateTime timestamp;
    private Long performedById;
    private String performedByName;
    private Long rootAdminId;
}
