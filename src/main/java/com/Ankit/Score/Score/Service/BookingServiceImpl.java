package com.Ankit.Score.Score.Service;

import com.Ankit.Score.Score.Entity.Booking;
import com.Ankit.Score.Score.Entity.Category;
import com.Ankit.Score.Score.Entity.SportSlot;
import com.Ankit.Score.Score.Entity.User;
import com.Ankit.Score.Score.Payloads.BookingDto;
import com.Ankit.Score.Score.Repo.BookingRepo;
import com.Ankit.Score.Score.Repo.CategoryRepo;
import com.Ankit.Score.Score.Repo.SportSlotRepo;
import com.Ankit.Score.Score.Repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepo bookingRepo;
    private final UserRepo userRepo;
    private final SportSlotRepo slotRepo;
    private final PaymentServiceImpl paymentService;
    private final ModelMapper modelMapper;
    private final CategoryRepo categoryRepository;
    private final SportSlotServiceImpl sportSlotService; // Add this dependency

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

        // Calculate price using the SAME method as everywhere else
        Integer totalPrice = sportSlotService.calculateTotalPrice(slot);
        if (totalPrice == null || totalPrice <= 0) {
            throw new RuntimeException("Invalid slot price calculation");
        }

        // Mark slot as booked
        slot.setBooked(true);
        slotRepo.save(slot);

        // Create Booking
        Booking booking = Booking.builder()
                .user(user)
                .sportSlot(slot)
                .userName(user.getName())
                .userMobileNo(user.getMobileNo())
                .paymentStatus("PAID")
                .status("CONFIRMED")
                .bookingStartTime(LocalDateTime.of(slot.getDate(), slot.getStartTime()))
                .bookingEndTime(LocalDateTime.of(slot.getDate(), slot.getEndTime()))
                .bookingDate(slot.getDate())
                .price((double) totalPrice)
                .build();

        Booking savedBooking = bookingRepo.save(booking);

        // Prepare DTO
        return mapToDto(savedBooking);
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

    @Override
    public List<BookingDto> getBookingsByCategoryAndDate(Long categoryId, LocalDate bookingDate) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        List<Booking> bookings = bookingRepo.findBySportSlotCategoryAndBookingDate(category, bookingDate);

        return bookings.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private BookingDto mapToDto(Booking entity) {
        return BookingDto.builder()
                .bookingId(entity.getBookingId())
                .userId(entity.getUser().getUserId())
                .slotId(entity.getSportSlot().getSlotId())
                .userName(entity.getUserName())
                .userMobileNo(entity.getUserMobileNo())
                .paymentStatus(entity.getPaymentStatus())
                .status(entity.getStatus())
                .bookingStartTime(entity.getBookingStartTime())
                .bookingEndTime(entity.getBookingEndTime())
                .bookingDate(entity.getBookingDate())
                .totalPrice(entity.getPrice())
                .build();
    }
}