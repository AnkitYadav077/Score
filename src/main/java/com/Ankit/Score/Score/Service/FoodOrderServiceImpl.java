package com.Ankit.Score.Score.Service;

import com.Ankit.Score.Score.Entity.Cart;
import com.Ankit.Score.Score.Entity.FoodOrder;
import com.Ankit.Score.Score.Payloads.FoodOrderDto;
import com.Ankit.Score.Score.Repo.CartRepo;
import com.Ankit.Score.Score.Repo.FoodOrderRepo;
import com.Ankit.Score.Score.Repo.PaymentRepo;
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
            var user = cart.getUser();
            return FoodOrder.builder()
                    .user(user)
                    .userName(user != null ? user.getName() : null)
                    .userMobileNo(user != null ? user.getMobileNo() : null)
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

        return orders.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public List<FoodOrderDto> getAllOrders() {
        List<FoodOrder> orders = foodOrderRepo.findAll();
        return orders.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public List<FoodOrderDto> getOrdersForUser(Long userId) {
        List<FoodOrder> orders = foodOrderRepo.findByUser_UserId(userId);
        if (orders.isEmpty()) {
            throw new ResourceNotFoundException("No orders found for user", "id", userId);
        }
        return orders.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    private FoodOrderDto mapToDto(FoodOrder order) {
        return FoodOrderDto.builder()
                .orderId(order.getOrderId())
                .quantity(order.getQuantity())
                .paymentStatus(order.getPaymentStatus())
                .status(order.getStatus())
                .orderDateTime(order.getOrderDateTime())
                .userId(order.getUser() != null ? order.getUser().getUserId() : null)
                .userName(order.getUserName())
                .userMobileNo(order.getUserMobileNo())
                .foodId(order.getFoodItem() != null ? order.getFoodItem().getFoodId() : null)
                .foodName(order.getFoodItem() != null ? order.getFoodItem().getName() : null)
                .build();
    }
}
