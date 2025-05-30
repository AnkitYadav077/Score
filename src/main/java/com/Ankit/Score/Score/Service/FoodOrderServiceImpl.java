package com.Ankit.Score.Score.Service;

import com.Ankit.Score.Score.Entity.Cart;
import com.Ankit.Score.Score.Entity.FoodOrder;
import com.Ankit.Score.Score.Repo.CartRepo;
import com.Ankit.Score.Score.Repo.FoodOrderRepo;
import com.Ankit.Score.Score.Repo.PaymentRepo;
import com.Ankit.Score.Score.Payloads.FoodOrderDto;
import com.Ankit.Score.Score.Exceptions.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FoodOrderServiceImpl implements FoodOrderService {

    @Autowired
    private CartRepo cartRepo;

    @Autowired
    private FoodOrderRepo foodOrderRepo;

    @Autowired
    private PaymentRepo paymentRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    @Transactional
    public List<FoodOrderDto> placeOrderFromCart(Long cartId, String razorpayPaymentId) throws Exception {
        Cart cart = cartRepo.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found with", "id: ", cartId));

        var payment = paymentRepo.findByTransactionId(razorpayPaymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for", "id: ", razorpayPaymentId));

        if (!payment.getPaymentStatus().equalsIgnoreCase("SUCCESS")) {
            throw new IllegalStateException("Payment not successful yet");
        }

        var orders = cart.getCartItems().stream().map(item -> {
            return FoodOrder.builder()
                    .user(cart.getUser())
                    .foodItem(item.getFoodItem())
                    .quantity(item.getQuantity())
                    .paymentStatus("PAID")
                    .status("CONFIRMED")
                    .orderDateTime(LocalDateTime.now())
                    .build();
        }).collect(Collectors.toList());

        foodOrderRepo.saveAll(orders);

        cart.getCartItems().clear();
        cart.setTotalAmount(0.0);
        cartRepo.save(cart);

        return orders.stream().map(order -> {
            FoodOrderDto dto = new FoodOrderDto();

            dto.setOrderId(order.getOrderId());
            dto.setQuantity(order.getQuantity());
            dto.setOrderAt(order.getOrderAt());
            dto.setPaymentStatus(order.getPaymentStatus());
            dto.setStatus(order.getStatus());

            // Set User Info
            if (order.getUser() != null) {
                dto.setUserId(order.getUser().getUserId());
                dto.setUserName(order.getUser().getName());
                dto.setUserMobileNo(order.getUser().getMobileNo());
            }

            // Set Food Info
            if (order.getFoodItem() != null) {
                dto.setFoodId(order.getFoodItem().getFoodId());
                dto.setFoodName(order.getFoodItem().getName());
            }

            return dto;
        }).collect(Collectors.toList());

    }

}
