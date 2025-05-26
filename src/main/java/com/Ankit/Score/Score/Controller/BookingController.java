package com.Ankit.Score.Score.Controller;

import com.Ankit.Score.Score.Payloads.BookingDto;
import com.Ankit.Score.Score.Service.BookingService;
import com.Ankit.Score.Score.Service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private PaymentService paymentService;


    // Create a new booking
    @PostMapping
    public ResponseEntity<BookingDto> createBooking(@RequestBody BookingDto dto) {
        return ResponseEntity.ok(bookingService.createBooking(dto));
    }

    // Get booking by ID
    @GetMapping("/{id}")
    public ResponseEntity<BookingDto> getBooking(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.getBookingById(id));
    }

    // Get all bookings
    @GetMapping
    public ResponseEntity<List<BookingDto>> getAll() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @PostMapping("/createPaymentOrder")
    public ResponseEntity<?> createBookingPaymentOrder(@RequestParam int amount) throws Exception {
        String order = paymentService.createPaymentOrder(amount, "INR", "booking_receipt_" + System.currentTimeMillis());
        return ResponseEntity.ok(order);
    }

//    @PostMapping("/confirmBooking")
//    public ResponseEntity<BookingDto> confirmBooking(
//            @RequestParam Long userId,
//            @RequestParam Long slotId,
//            @RequestParam String paymentId) throws Exception {
//        BookingDto bookingDto = bookingService.createBookingWithPayment(userId, slotId, paymentId);
//        return ResponseEntity.ok(bookingDto);
//    }


}