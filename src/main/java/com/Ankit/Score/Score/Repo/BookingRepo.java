package com.Ankit.Score.Score.Repo;

import com.Ankit.Score.Score.Entity.Booking;
import com.Ankit.Score.Score.Entity.Category;
import com.Ankit.Score.Score.Entity.SportSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepo extends JpaRepository<Booking, Long> {
    boolean existsBySportSlotAndBookingDate(SportSlot sportSlot, LocalDate bookingDate);
    List<Booking> findByUser_UserId(Long userId);
    List<Booking> findByUser_UserIdAndPaymentStatus(Long userId, String paymentStatus);
    List<Booking> findBySportSlotCategoryAndBookingDate(Category category, LocalDate bookingDate);
}
