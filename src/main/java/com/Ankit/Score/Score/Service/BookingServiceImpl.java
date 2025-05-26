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

    @Autowired
    private BookingRepo bookingRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PaymentService paymentService;


    @Autowired
    private SportSlotRepo slotRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public BookingDto createBooking(BookingDto bookingDto) {
        User user = userRepo.findById(bookingDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        SportSlot slot = slotRepo.findById(bookingDto.getSlotId())
                .orElseThrow(() -> new RuntimeException("Slot not found"));

        if (!"PAID".equalsIgnoreCase(bookingDto.getPaymentStatus())) {
            throw new RuntimeException("Payment is required before booking the slot.");
        }

        // Validate slot time
        if (!isValidSlotTime(slot)) {
            throw new RuntimeException("❌ Invalid Slot: Start time must be at :00 or :30 minutes, end time must be at :00 or :30 minutes, " +
                    "no seconds or nanos allowed, and duration must be at least 1 hour.");
        }

        boolean alreadyBooked = bookingRepo.existsBySportSlotAndBookingDate(slot, slot.getDate());
        if (alreadyBooked || slot.isBooked()) {
            throw new RuntimeException("❌ Slot already booked for this date");
        }

        synchronized (this) {
            slot.setBooked(true);
            slotRepo.save(slot);
        }

        Booking booking = dtoToEntity(bookingDto);
        booking.setUser(user);
        booking.setSportSlot(slot);
        booking.setPaymentStatus("PAID");
        booking.setBookingTime(LocalDateTime.of(slot.getDate(), slot.getStartTime()));
        booking.setBookingDate(slot.getDate());

        Booking saved = bookingRepo.save(booking);
        return entityToDto(saved);
    }


    @Override
    public BookingDto getBookingById(Long bookingId) {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        return entityToDto(booking);
    }

    @Override
    public List<BookingDto> getAllBookings() {
        return bookingRepo.findAll().stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    /**
     * Validate slot times:
     * - startTime minutes must be 0 or 30, seconds and nanos zero
     * - endTime minutes must be 0 or 30, seconds and nanos zero
     * - duration between start and end time at least 1 hour
     */
    private boolean isValidSlotTime(SportSlot slot) {
        int startMinute = slot.getStartTime().getMinute();
        int startSecond = slot.getStartTime().getSecond();
        int startNano = slot.getStartTime().getNano();

        int endMinute = slot.getEndTime().getMinute();
        int endSecond = slot.getEndTime().getSecond();
        int endNano = slot.getEndTime().getNano();

        // Check start time minute must be 0 or 30, seconds and nanos zero
        boolean validStart = (startMinute == 0 || startMinute == 30) && startSecond == 0 && startNano == 0;

        // Check end time minute must be 0 or 30, seconds and nanos zero
        boolean validEnd = (endMinute == 0 || endMinute == 30) && endSecond == 0 && endNano == 0;

        // Duration between start and end in minutes
        long durationMinutes = Duration.between(slot.getStartTime(), slot.getEndTime()).toMinutes();

        boolean validDuration = durationMinutes >= 60; // minimum 1 hour

        return validStart && validEnd && validDuration;
    }

    private Booking dtoToEntity(BookingDto dto) {
        return modelMapper.map(dto, Booking.class);
    }

    private BookingDto entityToDto(Booking entity) {
        BookingDto dto = modelMapper.map(entity, BookingDto.class);
        dto.setUserId(entity.getUser().getUserId());
        dto.setSlotId(entity.getSportSlot().getSlotId());
        dto.setUserName(entity.getUser().getName());
        dto.setUserMobileNo(entity.getUser().getMobileNo());

        // Combine date + endTime to get proper LocalDateTime
        LocalDateTime endDateTime = LocalDateTime.of(
                entity.getSportSlot().getDate(),
                entity.getSportSlot().getEndTime()
        );

        dto.setBookingTime(entity.getBookingTime()); // usually start time
        dto.setBookingEndTime(endDateTime); // now full datetime

        return dto;
    }

    public BookingDto createBookingWithPayment(Long userId, Long slotId, String razorpayPaymentId) throws Exception {
        boolean isPaymentCaptured = paymentService.verifyPayment(razorpayPaymentId);
        if (!isPaymentCaptured) {
            throw new RuntimeException("Payment not verified. Please complete payment.");
        }

        BookingDto dto = new BookingDto();
        dto.setUserId(userId);
        dto.setSlotId(slotId);

        return createBooking(dto);
    }

}