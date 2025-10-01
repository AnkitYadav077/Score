package com.Ankit.Score.Score.Controller;

import com.Ankit.Score.Score.Entity.Cart;
import com.Ankit.Score.Score.Payloads.PaymentVerificationRequest;
import com.Ankit.Score.Score.Service.BookingService;
import com.Ankit.Score.Score.Service.CartServiceImpl;
import com.Ankit.Score.Score.Service.FoodOrderService;
import com.Ankit.Score.Score.Service.PaymentServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentServiceImpl paymentService;
    private final CartServiceImpl cartService;
    private final BookingService bookingService;
    private final FoodOrderService foodOrderService;

    // Create Payment Order for Food Cart - Only Users can create payment orders
    @PostMapping("/create")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<String, Object>> createPaymentOrder(
            @RequestParam Long cartId,
            @RequestParam(defaultValue = "INR") String currency) throws Exception {

        Cart cart = cartService.getCartById(cartId);
        if (cart == null || cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Cart is empty or invalid"));
        }

        int amount = (int) Math.round(cart.getTotalAmount());
        String receipt = "cart_receipt_" + cartId;

        Map<String, Object> response = paymentService.createPaymentOrder(amount, currency, receipt);
        return ResponseEntity.ok(response);
    }

    // Verify Payment and Place Food Order - Only Users can verify payments
    @PostMapping("/verify")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> verifyPaymentAndPlaceOrder(
            @RequestBody PaymentVerificationRequest request,
            @RequestParam Long cartId) {

        try {
            paymentService.verifyAndSavePayment(request);
            var orders = foodOrderService.placeOrderFromCart(cartId, request.getRazorpayPaymentId());
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "Payment verification failed: " + e.getMessage())
            );
        }
    }

    // Create Payment Order for Slot Booking - Only Users can create slot payments
    @PostMapping("/create/slot")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<String, Object>> createSlotPaymentOrder(
            @RequestParam Long slotId,
            @RequestParam(defaultValue = "INR") String currency) throws Exception {

        Map<String, Object> orderData = paymentService.createOrderForSlot(slotId, currency);
        return ResponseEntity.ok(orderData);
    }

    // Verify Payment and Create Booking - Only Users can verify slot payments
    @PostMapping("/verify/slot")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> verifyPaymentForSlot(
            @RequestParam Long userId,
            @RequestParam Long slotId,
            @RequestParam String orderId,
            @RequestParam String paymentId,
            @RequestParam String signature) {

        try {
            paymentService.verifyAndSavePaymentForSlot(userId, slotId, orderId, paymentId, signature);
            var bookingDto = bookingService.createBookingWithPayment(userId, slotId, orderId, paymentId, signature);

            return ResponseEntity.ok(Map.of(
                    "message", "Slot booking successful",
                    "booking", bookingDto
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "Payment verification failed: " + e.getMessage())
            );
        }
    }
}