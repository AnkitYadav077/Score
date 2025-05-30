package com.Ankit.Score.Score.Controller;

import com.Ankit.Score.Score.Payloads.FoodItemDto;
import com.Ankit.Score.Score.Payloads.FoodOrderDto;
import com.Ankit.Score.Score.Service.FoodItemService;
import com.Ankit.Score.Score.Service.FoodOrderService;
import com.Ankit.Score.Score.Service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class FoodOrderController {

    @Autowired
    private FoodOrderService foodOrderService;

    @Autowired
    private FoodItemService foodItemService;

    @Autowired
    private PaymentService paymentService;

    // Place order from Cart with correct parameter (cartId)
    @PostMapping("/placeCartOrder")
    public ResponseEntity<List<FoodOrderDto>> placeCartOrder(
            @RequestParam Long cartId,                    // <-- Changed from userId to cartId
            @RequestParam String razorpayPaymentId
    ) throws Exception {
        List<FoodOrderDto> orders = foodOrderService.placeOrderFromCart(cartId, razorpayPaymentId);
        return ResponseEntity.status(201).body(orders);
    }

    @GetMapping("/search")
    public ResponseEntity<List<FoodItemDto>> searchFood(@RequestParam String keyword) {
        return ResponseEntity.ok(foodItemService.searchFood(keyword));
    }

    // Get all orders
    @GetMapping("/admin")
    public List<FoodOrderDto> getAllOrders() {
        return foodOrderService.getAllOrders();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<FoodOrderDto>> getOrdersForUser(@PathVariable Long userId) {
        List<FoodOrderDto> orders = foodOrderService.getOrdersForUser(userId);
        return ResponseEntity.ok(orders);
    }
}
