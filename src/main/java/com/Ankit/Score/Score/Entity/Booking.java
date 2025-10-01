package com.Ankit.Score.Score.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "slot_id", nullable = false)
    private SportSlot sportSlot;

    private String userName; // Store user's name at the time of booking
    private String userMobileNo; // Store user's mobile number at the time of booking

    private String paymentStatus;
    private String status;

    private LocalDateTime bookingStartTime;
    private LocalDateTime bookingEndTime;
    private LocalDate bookingDate;

    private Double price;
}