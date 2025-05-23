package com.Ankit.Score.Score.Service;

import com.Ankit.Score.Score.Entity.*;
import com.Ankit.Score.Score.Exceptions.ResourceNotFoundException;
import com.Ankit.Score.Score.Payloads.FoodOrderDto;
import com.Ankit.Score.Score.Repo.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FoodOrderServiceImpl implements FoodOrderService {

    @Autowired private FoodOrderRepo foodOrderRepo;
    @Autowired private UserRepo userRepo;
    @Autowired private FoodItemRepo foodItemRepo;
    @Autowired private ModelMapper modelMapper;

    private static final String DEFAULT_STATUS = "PENDING";
    private static final String DEFAULT_PAYMENT_STATUS = "PENDING";

    @Override
    @Transactional
    public FoodOrderDto createOrder(FoodOrderDto dto) {
        User user = userRepo.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", dto.getUserId()));

        FoodItem foodItem = foodItemRepo.findById(dto.getFoodId())
                .orElseThrow(() -> new ResourceNotFoundException("FoodItem", "id", dto.getFoodId()));

        if (dto.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        FoodOrder order = new FoodOrder();
        order.setUser(user);
        order.setFoodItem(foodItem);
        order.setQuantity(dto.getQuantity());
        order.setOrderAt(LocalTime.now());

        FoodOrder savedOrder = foodOrderRepo.save(order);
        return enrichOrderDto(entityToDto(savedOrder));
    }

    @Override
    public FoodOrderDto getOrderById(Long orderId) {
        FoodOrder order = foodOrderRepo.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("FoodOrder", "id", orderId));
        return enrichOrderDto(entityToDto(order));
    }

    @Override
    public List<FoodOrderDto> getAllOrders() {
        return foodOrderRepo.findAll().stream()
                .map(this::entityToDto)
                .map(this::enrichOrderDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<FoodOrderDto> getOrdersByUser(Long userId) {
        userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        return foodOrderRepo.findAll().stream()
                .filter(order -> order.getUser().getUserId().equals(userId))
                .map(this::entityToDto)
                .map(this::enrichOrderDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public FoodOrderDto updateOrder(Long orderId, FoodOrderDto dto) {
        try {
            FoodOrder order = foodOrderRepo.findById(orderId)
                    .orElseThrow(() -> new ResourceNotFoundException("FoodOrder", "id", orderId));

            if (dto.getQuantity() > 0) {
                order.setQuantity(dto.getQuantity());
            }

            FoodOrder updatedOrder = foodOrderRepo.save(order);
            return enrichOrderDto(entityToDto(updatedOrder));

        } catch (OptimisticLockingFailureException e) {
            throw new RuntimeException("Order was modified by another process. Try again.", e);
        }
    }

    // Convert entity to DTO with userId and foodId
    private FoodOrderDto entityToDto(FoodOrder entity) {
        FoodOrderDto dto = modelMapper.map(entity, FoodOrderDto.class);

        if (entity.getUser() != null) {
            dto.setUserId(entity.getUser().getUserId());
        }

        if (entity.getFoodItem() != null) {
            dto.setFoodId(entity.getFoodItem().getFoodId());
        }

        return dto;
    }

    // Add extra fields from related entities to the DTO
    private FoodOrderDto enrichOrderDto(FoodOrderDto dto) {
        if (dto.getFoodId() != null) {
            FoodItem food = foodItemRepo.findById(dto.getFoodId())
                    .orElseThrow(() -> new ResourceNotFoundException("FoodItem", "id", dto.getFoodId()));
            dto.setFoodName(food.getName());
            dto.setPrice(food.getPrice());
            dto.setTotalAmount(food.getPrice() * dto.getQuantity());
        }

        if (dto.getUserId() != null) {
            User user = userRepo.findById(dto.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", dto.getUserId()));
            dto.setName(user.getName());
            dto.setMobileNo(user.getMobileNo());
        }

        if (dto.getStatus() == null) {
            dto.setStatus(DEFAULT_STATUS);
        }

        if (dto.getPaymentStatus() == null) {
            dto.setPaymentStatus(DEFAULT_PAYMENT_STATUS);
        }

        return dto;
    }
}
