package com.Ankit.Score.Score.Service;

import com.Ankit.Score.Score.Entity.User;
import com.Ankit.Score.Score.Exceptions.ResourceNotFoundException;
import com.Ankit.Score.Score.Payloads.OrderHistoryDto;
import com.Ankit.Score.Score.Payloads.UserDto;
import com.Ankit.Score.Score.Repo.BookingRepo;
import com.Ankit.Score.Score.Repo.FoodOrderRepo;
import com.Ankit.Score.Score.Repo.UserRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private BookingRepo bookingRepo;

    @Autowired
    private FoodOrderRepo foodOrderRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = dtoToUser(userDto);
        User savedUser = userRepo.save(user);
        return userToDto(savedUser);
    }

    @Override
    public UserDto updateUser(UserDto userDto, Long userId) {
        User existingUser = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        existingUser.setName(userDto.getName());
        existingUser.setEmail(userDto.getEmail());
        existingUser.setMobileNo(userDto.getMobileNo());

        User updatedUser = userRepo.save(existingUser);
        return userToDto(updatedUser);
    }

    @Override
    public UserDto getUserById(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        return userToDto(user);
    }

    @Override
    public List<UserDto> getAllUser() {
        return userRepo.findAll().stream()
                .map(this::userToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderHistoryDto> getUserOrderHistory(Long userId) {
        // Slot Bookings
        List<OrderHistoryDto> slotOrders = bookingRepo.findByUser_UserId(userId).stream()
                .map(b -> new OrderHistoryDto(
                        "SLOT",
                        b.getBookingId(),
                        LocalDateTime.of(b.getBookingDate(), b.getSportSlot().getStartTime()),  // ✅ Fixed here
                        "Slot on " + b.getBookingDate() + " at " + b.getSportSlot().getStartTime()
                )).collect(Collectors.toList());

        // Food Orders
        List<OrderHistoryDto> foodOrders = foodOrderRepo.findByUser_UserId(userId).stream()
                .map(f -> new OrderHistoryDto(
                        "FOOD",
                        f.getOrderId(),
                        LocalDateTime.of(LocalDate.now(), f.getOrderAt()),  // ✅ Fixed here
                        "Ordered " + f.getQuantity() + " x " + f.getFoodItem().getName()
                                + ", Total: ₹" + (f.getQuantity() * f.getFoodItem().getPrice())
                )).collect(Collectors.toList());

        // Merge and sort (future date first)
        List<OrderHistoryDto> allOrders = new ArrayList<>();
        allOrders.addAll(slotOrders);
        allOrders.addAll(foodOrders);

        return allOrders.stream()
                .sorted((a, b) -> b.getOrderDateTime().compareTo(a.getOrderDateTime()))
                .collect(Collectors.toList());
    }


    // Helper: DTO -> Entity
    private User dtoToUser(UserDto userDto) {
        return modelMapper.map(userDto, User.class);
    }

    // Helper: Entity -> DTO
    private UserDto userToDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }
}
