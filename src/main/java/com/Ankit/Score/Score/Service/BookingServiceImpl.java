package com.Ankit.Score.Score.Service;

import com.Ankit.Score.Score.Entity.Booking;
import com.Ankit.Score.Score.Entity.SportSlot;
import com.Ankit.Score.Score.Entity.User;
import com.Ankit.Score.Score.Payloads.BookingDto;
import com.Ankit.Score.Score.Repo.BookingRepo;
import com.Ankit.Score.Score.Repo.SportSlotRepo;
import com.Ankit.Score.Score.Repo.UserRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepo bookingRepo;
    private final UserRepo userRepo;
    private final SportSlotRepo slotRepo;
    private final PaymentService paymentService;
    private final ModelMapper modelMapper;

    @Autowired
    public BookingServiceImpl(BookingRepo bookingRepo, UserRepo userRepo, SportSlotRepo slotRepo, PaymentService paymentService, ModelMapper modelMapper) {
        this.bookingRepo = bookingRepo;
        this.userRepo = userRepo;
        this.slotRepo = slotRepo;
        this.paymentService = paymentService;
        this.modelMapper = modelMapper;
    }

    @Override
    public BookingDto createBooking(BookingDto dto) {
        User user = userRepo.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        SportSlot slot = slotRepo.findById(dto.getSlotId())
                .orElseThrow(() -> new RuntimeException("Slot not found"));

        if (!"PAID".equalsIgnoreCase(dto.getPaymentStatus())) {
            throw new RuntimeException("Payment required before booking.");
        }

        validateSlotAvailability(slot);
        validateSlotTime(slot);

        synchronized (this) {
            if (slot.isBooked()) {
                throw new RuntimeException("Slot already booked.");
            }
            slot.setBooked(true);
            slotRepo.save(slot);
        }

        Booking booking = Booking.builder()
                .user(user)
                .sportSlot(slot)
                .paymentStatus("PAID")
                .status("CONFIRMED")
                .bookingTime(LocalDateTime.of(slot.getDate(), slot.getStartTime()))
                .bookingDate(slot.getDate())
                .build();

        Booking saved = bookingRepo.save(booking);
        return mapToDto(saved);
    }

    @Override
    public BookingDto getBookingById(Long bookingId) {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        return mapToDto(booking);
    }

    @Override
    public List<BookingDto> getAllBookings() {
        return bookingRepo.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public BookingDto createBookingWithPayment(Long userId, Long slotId, String razorpayPaymentId) throws Exception {
        boolean isPaymentValid = paymentService.verifyPayment(razorpayPaymentId);
        if (!isPaymentValid) {
            throw new RuntimeException("Payment verification failed. Please try again.");
        }

        BookingDto dto = BookingDto.builder()
                .userId(userId)
                .slotId(slotId)
                .paymentStatus("PAID")
                .build();

        return createBooking(dto);
    }

    private void validateSlotAvailability(SportSlot slot) {
        boolean alreadyBooked = bookingRepo.existsBySportSlotAndBookingDate(slot, slot.getDate());
        if (alreadyBooked) {
            throw new RuntimeException("Slot already booked for this date.");
        }
    }

    private void validateSlotTime(SportSlot slot) {
        int startMin = slot.getStartTime().getMinute();
        int endMin = slot.getEndTime().getMinute();
        long durationMinutes = Duration.between(slot.getStartTime(), slot.getEndTime()).toMinutes();

        if (!isValidTimeFormat(startMin) || !isValidTimeFormat(endMin) || durationMinutes < 60) {
            throw new RuntimeException("Invalid slot time. Start/End must be on :00 or :30, min 1 hour.");
        }
    }

    private boolean isValidTimeFormat(int minutes) {
        return minutes == 0 || minutes == 30;
    }

    private BookingDto mapToDto(Booking entity) {
        BookingDto dto = modelMapper.map(entity, BookingDto.class);
        dto.setUserId(entity.getUser().getUserId());
        dto.setSlotId(entity.getSportSlot().getSlotId());
        dto.setUserName(entity.getUser().getName());
        dto.setUserMobileNo(entity.getUser().getMobileNo());
        dto.setBookingEndTime(LocalDateTime.of(entity.getSportSlot().getDate(), entity.getSportSlot().getEndTime()));
        return dto;
    }
}
