package com.Ankit.Score.Score.Service;

import com.Ankit.Score.Score.Entity.User;
import com.Ankit.Score.Score.Exceptions.ResourceNotFoundException;
import com.Ankit.Score.Score.Payloads.OrderHistoryDto;
import com.Ankit.Score.Score.Payloads.UserDto;
import com.Ankit.Score.Score.Repo.BookingRepo;
import com.Ankit.Score.Score.Repo.FoodOrderRepo;
import com.Ankit.Score.Score.Repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final BookingRepo bookingRepo;
    private final FoodOrderRepo foodOrderRepo;
    private final ModelMapper modelMapper;


    @Override
    public UserDto createUser(UserDto userDto) {
        User user = modelMapper.map(userDto, User.class);
        User savedUser = userRepo.save(user);
        return modelMapper.map(savedUser, UserDto.class);
    }

    @Override
    public UserDto updateUser(UserDto userDto, Long userId) {
        User existingUser = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        existingUser.setName(userDto.getName());
        existingUser.setEmail(userDto.getEmail());
        existingUser.setMobileNo(userDto.getMobileNo());

        User updatedUser = userRepo.save(existingUser);
        return modelMapper.map(updatedUser, UserDto.class);
    }

    @Override
    public UserDto getUserById(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public List<UserDto> getAllUser() {
        return userRepo.findAll().stream()
                .map(user -> modelMapper.map(user, UserDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderHistoryDto> getUserOrderHistory(Long userId) {
        // Verify user exists
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // Fetch Slot Bookings
        List<OrderHistoryDto> slotOrders = bookingRepo.findByUser_UserId(userId).stream()
                .map(booking -> {
                    String details = "Sports Slot Booking";
                    String description = "Sports Facility Booking";
                    Double amount = booking.getPrice() != null ? booking.getPrice() : 0.0;
                    LocalDateTime orderDateTime = booking.getBookingStartTime() != null ?
                            booking.getBookingStartTime() : LocalDateTime.now();

                    // Build description with available information
                    if (booking.getBookingDate() != null) {
                        description = "Slot booked for " + booking.getBookingDate();
                    }
                    if (booking.getSportSlot() != null && booking.getSportSlot().getStartTime() != null) {
                        description += " at " + booking.getSportSlot().getStartTime();
                    }

                    // Add category name if available
                    if (booking.getSportSlot() != null && booking.getSportSlot().getCategory() != null) {
                        details = booking.getSportSlot().getCategory().getName() + " Booking";
                    }

                    return new OrderHistoryDto(
                            "SLOT",
                            booking.getBookingId(),
                            orderDateTime, // This will be converted to String in constructor
                            details,
                            description,
                            amount
                    );
                })
                .collect(Collectors.toList());

        // Fetch Food Orders
        List<OrderHistoryDto> foodOrders = foodOrderRepo.findByUser_UserId(userId).stream()
                .map(order -> {
                    String details = "Food Order";
                    String description = "Food order";
                    Double amount = 0.0;
                    LocalDateTime orderDateTime = order.getOrderDateTime() != null ?
                            order.getOrderDateTime() : LocalDateTime.now();

                    // Build description with food item details
                    if (order.getFoodItem() != null) {
                        String foodName = order.getFoodItem().getName();
                        double foodPrice = order.getFoodItem().getPrice();
                        int quantity = order.getQuantity();
                        amount = quantity * foodPrice;

                        description = "Ordered " + quantity + " x " + foodName + ", Total: ₹" + amount;
                        details = foodName + " (Qty: " + quantity + ")";
                    }

                    return new OrderHistoryDto(
                            "FOOD",
                            order.getOrderId(),
                            orderDateTime, // This will be converted to String in constructor
                            details,
                            description,
                            amount
                    );
                })
                .collect(Collectors.toList());

        // Merge all orders
        List<OrderHistoryDto> allOrders = new ArrayList<>();
        allOrders.addAll(slotOrders);
        allOrders.addAll(foodOrders);

        // Sort descending by order datetime (most recent first)
        // Since orderDateTime is now String, we need to parse it for sorting
        return allOrders.stream()
                .sorted((o1, o2) -> {
                    try {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        LocalDateTime date1 = o1.getOrderDateTime() != null ?
                                LocalDateTime.parse(o1.getOrderDateTime(), formatter) : LocalDateTime.MIN;
                        LocalDateTime date2 = o2.getOrderDateTime() != null ?
                                LocalDateTime.parse(o2.getOrderDateTime(), formatter) : LocalDateTime.MIN;
                        return date2.compareTo(date1);
                    } catch (Exception e) {
                        return 0;
                    }
                })
                .collect(Collectors.toList());
    }
}
