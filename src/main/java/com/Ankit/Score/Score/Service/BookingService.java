package com.Ankit.Score.Score.Service;

import com.Ankit.Score.Score.Payloads.BookingDto;
import java.util.List;

public interface BookingService {
    BookingDto getBookingById(Long bookingId);
    List<BookingDto> getAllBookings();
    BookingDto createBookingWithPayment(Long userId, Long slotId, String orderId, String paymentId, String signature) throws Exception;
}
