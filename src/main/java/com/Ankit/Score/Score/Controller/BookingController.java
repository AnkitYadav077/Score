package com.Ankit.Score.Score.Controller;

import com.Ankit.Score.Score.Payloads.BookingDto;
import com.Ankit.Score.Score.Payloads.BookingPaymentRequest;
import com.Ankit.Score.Score.Service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingDto> getBooking(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.getBookingById(id));
    }

    @GetMapping
    public ResponseEntity<List<BookingDto>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @PostMapping("/with-payment")
    public ResponseEntity<BookingDto> createBookingWithPayment(@RequestBody BookingPaymentRequest request) throws Exception {
        BookingDto booking = bookingService.createBookingWithPayment(
                request.getUserId(), request.getSlotId(), request.getOrderId(), request.getPaymentId(), request.getSignature());
        return ResponseEntity.ok(booking);
    }

    @GetMapping("/by-category-date")
    public ResponseEntity<List<BookingDto>> getBookingsByCategoryAndDate(
            @RequestParam Long categoryId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate bookingDate) {

        List<BookingDto> bookings = bookingService.getBookingsByCategoryAndDate(categoryId, bookingDate);
        return ResponseEntity.ok(bookings);
    }
}
