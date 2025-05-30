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

    private final UserRepo userRepo;
    private final BookingRepo bookingRepo;
    private final FoodOrderRepo foodOrderRepo;
    private final ModelMapper modelMapper;

    @Autowired
    public UserServiceImpl(UserRepo userRepo,
                           BookingRepo bookingRepo,
                           FoodOrderRepo foodOrderRepo,
                           ModelMapper modelMapper) {
        this.userRepo = userRepo;
        this.bookingRepo = bookingRepo;
        this.foodOrderRepo = foodOrderRepo;
        this.modelMapper = modelMapper;
    }

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

//    @Override
//    public List<OrderHistoryDto> getUserOrderHistory(Long userId) {
//        // Fetch Slot Bookings
//        List<OrderHistoryDto> slotOrders = bookingRepo.findByUser_UserId(userId).stream()
//                .map(booking -> new OrderHistoryDto(
//                        "SLOT",
//                        booking.getBookingId(),
//                        LocalDateTime.of(booking.getBookingDate(), booking.getSportSlot().getStartTime()),
//                        "Slot on " + booking.getBookingDate() + " at " + booking.getSportSlot().getStartTime()
//                ))
//                .collect(Collectors.toList());
//
//        // Fetch Food Orders
//        List<OrderHistoryDto> foodOrders = foodOrderRepo.findByUser_UserId(userId).stream()
//                .map(order -> new OrderHistoryDto(
//                        "FOOD",
//                        order.getOrderId(),
//                        LocalDateTime.of(LocalDateTime.now(), order.getOrderAt()),
//                        "Ordered " + order.getQuantity() + " x " + order.getFoodItem().getName()
//                                + ", Total: ₹" + (order.getQuantity() * order.getFoodItem().getPrice())
//                ))
//                .collect(Collectors.toList());
//
//        // Merge all orders
//        List<OrderHistoryDto> allOrders = new ArrayList<>();
//        allOrders.addAll(slotOrders);
//        allOrders.addAll(foodOrders);
//
//        // Sort descending by order datetime (future first)
//        return allOrders.stream()
//                .sorted((o1, o2) -> o2.getOrderDateTime().compareTo(o1.getOrderDateTime()))
//                .collect(Collectors.toList());
//    }
}