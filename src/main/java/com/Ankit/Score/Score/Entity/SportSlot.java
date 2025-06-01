package com.Ankit.Score.Score.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

// package: com.Ankit.Score.Score.Entity

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SportSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long slotId;

    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean booked;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    private Integer totalPrice;

    // ... other fields
}

