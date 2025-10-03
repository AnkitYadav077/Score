package com.Ankit.Score.Score.Payloads;

import lombok.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
public class OrderHistoryDto {
    private String orderType; // "FOOD" or "SLOT"
    private Long orderId;
    private String orderDateTime; // Changed to String
    private String details;
    private String description;
    private Double amount;

    // Constructor that accepts LocalDateTime and converts to String
    public OrderHistoryDto(String orderType, Long orderId, LocalDateTime orderDateTime,
                           String details, String description, Double amount) {
        this.orderType = orderType;
        this.orderId = orderId;
        this.orderDateTime = formatDateTime(orderDateTime);
        this.details = details;
        this.description = description;
        this.amount = amount;
    }

    // All-args constructor for String date time
    public OrderHistoryDto(String orderType, Long orderId, String orderDateTime,
                           String details, String description, Double amount) {
        this.orderType = orderType;
        this.orderId = orderId;
        this.orderDateTime = orderDateTime;
        this.details = details;
        this.description = description;
        this.amount = amount;
    }

    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}