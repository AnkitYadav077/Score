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
    public BookingServiceImpl(BookingRepo bookingRepo, UserRepo userRepo, SportSlotRepo slotRepo,
                              PaymentService paymentService, ModelMapper modelMapper) {
        this.bookingRepo = bookingRepo;
        this.userRepo = userRepo;
        this.slotRepo = slotRepo;
        this.paymentService = paymentService;
        this.modelMapper = modelMapper;
    }

    @Override
    public BookingDto createBookingWithPayment(Long userId, Long slotId, String orderId, String paymentId, String signature) throws Exception {
        // Verify and Save Payment
        paymentService.verifyAndSavePaymentForSlot(userId, slotId, orderId, paymentId, signature);

        // Fetch User & Slot
        User user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        SportSlot slot = slotRepo.findById(slotId).orElseThrow(() -> new RuntimeException("Slot not found"));

        if (slot.isBooked()) {
            throw new RuntimeException("Slot already booked");
        }

        // Calculate price
        int hour = slot.getStartTime().getHour();
        int totalPrice = (hour < 19) ? slot.getCategory().getBasePrice() : slot.getCategory().getEveningPrice();

        // Mark slot as booked
        slot.setBooked(true);
        slotRepo.save(slot);

        // Create Booking
        Booking booking = Booking.builder()
                .user(user)
                .sportSlot(slot)
                .paymentStatus("PAID")
                .status("CONFIRMED")
                .bookingTime(LocalDateTime.of(slot.getDate(), slot.getStartTime()))
                .bookingDate(slot.getDate())
                .price((double) totalPrice)
                .build();

        Booking savedBooking = bookingRepo.save(booking);

        // Prepare DTO
        BookingDto dto = modelMapper.map(savedBooking, BookingDto.class);
        dto.setUserId(user.getUserId());
        dto.setUserName(user.getName());
        dto.setUserMobileNo(user.getMobileNo());
        dto.setSlotId(slot.getSlotId());
        dto.setTotalPrice(totalPrice);
        dto.setBookingStartTime(LocalDateTime.of(slot.getDate(), slot.getStartTime()));
        dto.setBookingEndTime(LocalDateTime.of(slot.getDate(), slot.getEndTime()));

        return dto;
    }

    @Override
    public BookingDto getBookingById(Long bookingId) {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        return mapToDto(booking);
    }

    @Override
    public List<BookingDto> getAllBookings() {
        return bookingRepo.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private BookingDto mapToDto(Booking entity) {
        BookingDto dto = modelMapper.map(entity, BookingDto.class);
        dto.setUserId(entity.getUser().getUserId());
        dto.setUserName(entity.getUser().getName());
        dto.setUserMobileNo(entity.getUser().getMobileNo());
        dto.setSlotId(entity.getSportSlot().getSlotId());
        dto.setBookingStartTime(LocalDateTime.of(entity.getSportSlot().getDate(), entity.getSportSlot().getStartTime()));
        dto.setBookingEndTime(LocalDateTime.of(entity.getSportSlot().getDate(), entity.getSportSlot().getEndTime()));
        return dto;
    }
}
