package com.Ankit.Score.Score.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    private int quantity;

    private String paymentStatus;  // PENDING, PAID, REFUNDED

    private String status;         // PENDING, CONFIRMED, CANCELLED, etc.

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String userName;
    private String userMobileNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id")
    private FoodItem foodItem;

    private LocalDateTime orderDateTime;
}