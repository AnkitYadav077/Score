package com.Ankit.Score.Score.Controller;

import com.Ankit.Score.Score.Payloads.BookingDto;
import com.Ankit.Score.Score.Payloads.BookingPaymentRequest;
import com.Ankit.Score.Score.Security.JwtHelper;
import com.Ankit.Score.Score.Service.BookingService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final JwtHelper jwtHelper;

    private Long getAuthenticatedUserId(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return jwtHelper.getUserIdFromToken(token);
        }
        throw new RuntimeException("Invalid token");
    }

    // Get booking by ID - User can view their own booking, Admin can view any
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'SUPER_ADMIN', 'SUB_ADMIN')")
    public ResponseEntity<BookingDto> getBooking(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.getBookingById(id));
    }

    // Get all bookings - Only Admin can view all bookings
    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SUB_ADMIN')")
    public ResponseEntity<List<BookingDto>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    // Create booking with payment - Only Users can create bookings
    @PostMapping("/with-payment")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BookingDto> createBookingWithPayment(
            @RequestBody BookingPaymentRequest request,
            HttpServletRequest httpRequest) throws Exception {
        Long userId = getAuthenticatedUserId(httpRequest);
        BookingDto booking = bookingService.createBookingWithPayment(
                userId,
                request.getSlotId(),
                request.getOrderId(),
                request.getPaymentId(),
                request.getSignature()
        );
        return ResponseEntity.ok(booking);
    }

    // Get bookings by category and date - Admin can view for management
    @GetMapping("/by-category-date")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SUB_ADMIN')")
    public ResponseEntity<List<BookingDto>> getBookingsByCategoryAndDate(
            @RequestParam Long categoryId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate bookingDate) {

        List<BookingDto> bookings = bookingService.getBookingsByCategoryAndDate(categoryId, bookingDate);
        return ResponseEntity.ok(bookings);
    }

    // Get my bookings - User can view their own bookings
    @GetMapping("/my-bookings")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<BookingDto>> getMyBookings(HttpServletRequest request) {
        Long userId = getAuthenticatedUserId(request);
        List<BookingDto> allBookings = bookingService.getAllBookings();
        List<BookingDto> myBookings = allBookings.stream()
                .filter(booking -> booking.getUserId().equals(userId))
                .toList();
        return ResponseEntity.ok(myBookings);
    }
}