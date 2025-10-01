package com.Ankit.Score.Score.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @Column(nullable = false)
    private Long userId;

    private Long slotId;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false, length = 50)
    private String paymentMethod;  // e.g. "RAZORPAY"

    @Column(nullable = false, length = 50)
    private String paymentStatus;  // "SUCCESS", "FAILED", etc.

    @Column(nullable = true)
    private String transactionId; // Razorpay payment id

    private LocalDateTime paymentDateTime;
}