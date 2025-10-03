package com.Ankit.Score.Score.Controller;

import com.Ankit.Score.Score.Payloads.FoodItemDto;
import com.Ankit.Score.Score.Payloads.FoodOrderDto;
import com.Ankit.Score.Score.Security.JwtHelper;
import com.Ankit.Score.Score.Service.FoodItemService;
import com.Ankit.Score.Score.Service.FoodOrderService;
import com.Ankit.Score.Score.Service.PaymentServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class FoodOrderController {

    private final FoodOrderService foodOrderService;
    private final FoodItemService foodItemService;
    private final PaymentServiceImpl paymentService;
    private final JwtHelper jwtHelper;

    private Long getAuthenticatedUserId(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return jwtHelper.getUserIdFromToken(token);
        }
        throw new RuntimeException("Invalid token");
    }

    // Place order from Cart - Only Users can place orders
    @PostMapping("/placeCartOrder")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<FoodOrderDto>> placeCartOrder(
            @RequestParam Long cartId,
            @RequestParam String razorpayPaymentId
    ) throws Exception {
        List<FoodOrderDto> orders = foodOrderService.placeOrderFromCart(cartId, razorpayPaymentId);
        return ResponseEntity.status(201).body(orders);
    }

    // Search food - Public access
    @GetMapping("/search")
    public ResponseEntity<List<FoodItemDto>> searchFood(@RequestParam String keyword) {
        return ResponseEntity.ok(foodItemService.searchFood(keyword));
    }

    // Get all orders - Only Admin can view all orders
    @GetMapping("/admin")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SUB_ADMIN')")
    public List<FoodOrderDto> getAllOrders() {
        return foodOrderService.getAllOrders();
    }

    // Get my orders - User can view their own orders using token
    @GetMapping("/my-orders")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<FoodOrderDto>> getMyOrders(HttpServletRequest request) {
        Long userId = getAuthenticatedUserId(request);
        List<FoodOrderDto> orders = foodOrderService.getOrdersForUser(userId);
        return ResponseEntity.ok(orders);
    }
}