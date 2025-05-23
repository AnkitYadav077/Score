package com.Ankit.Score.Score.Repo;

import com.Ankit.Score.Score.Entity.Booking;
import com.Ankit.Score.Score.Entity.SportSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface BookingRepo extends JpaRepository<Booking, Long> {
    boolean existsBySportSlotAndBookingDate(SportSlot slot, LocalDate bookingDate);
}
