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
    private SportSlotRepo slotRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public BookingDto createBooking(BookingDto bookingDto) {
        User user = userRepo.findById(bookingDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        SportSlot slot = slotRepo.findById(bookingDto.getSlotId())
                .orElseThrow(() -> new RuntimeException("Slot not found"));

        if (!isValidSlotTime(slot)) {
            throw new RuntimeException("❌ Invalid Slot: Must start/end on :00 or :30 and last at least 1 hour.");
        }

        boolean alreadyBooked = bookingRepo.existsBySportSlotAndBookingDate(slot, slot.getDate());
        if (alreadyBooked || slot.isBooked()) {
            throw new RuntimeException("❌ Slot already booked for this date");
        }

        // Lock to prevent race condition
        synchronized (this) {
            slot.setBooked(true);
            slotRepo.save(slot);
        }

        Booking booking = modelMapper.map(bookingDto, Booking.class);
        booking.setUser(user);
        booking.setSportSlot(slot);
        booking.setBookingTime(LocalDateTime.of(slot.getDate(), slot.getStartTime()));
        booking.setBookingDate(slot.getDate());

        Booking saved = bookingRepo.save(booking);
        return convertToDto(saved);
    }

    @Override
    public BookingDto getBookingById(Long bookingId) {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        return convertToDto(booking);
    }

    @Override
    public List<BookingDto> getAllBookings() {
        return bookingRepo.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private boolean isValidSlotTime(SportSlot slot) {
        boolean validStart = slot.getStartTime().getMinute() % 30 == 0
                && slot.getStartTime().getSecond() == 0
                && slot.getStartTime().getNano() == 0;

        boolean validEnd = slot.getEndTime().getMinute() % 30 == 0
                && slot.getEndTime().getSecond() == 0
                && slot.getEndTime().getNano() == 0;

        long durationMinutes = Duration.between(slot.getStartTime(), slot.getEndTime()).toMinutes();
        return validStart && validEnd && durationMinutes >= 60;
    }

    private BookingDto convertToDto(Booking entity) {
        BookingDto dto = modelMapper.map(entity, BookingDto.class);
        dto.setUserId(entity.getUser().getUserId());
        dto.setSlotId(entity.getSportSlot().getSlotId());
        dto.setUserName(entity.getUser().getName());
        dto.setUserMobileNo(entity.getUser().getMobileNo());

        dto.setBookingTime(entity.getBookingTime());
        dto.setBookingEndTime(LocalDateTime.of(entity.getSportSlot().getDate(), entity.getSportSlot().getEndTime()));
        return dto;
    }
}
