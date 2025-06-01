package com.Ankit.Score.Score.Controller;

import com.Ankit.Score.Score.Entity.Cart;
import com.Ankit.Score.Score.Payloads.PaymentVerificationRequest;
import com.Ankit.Score.Score.Service.BookingService;
import com.Ankit.Score.Score.Service.CartService;
import com.Ankit.Score.Score.Service.FoodOrderService;
import com.Ankit.Score.Score.Service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private CartService cartService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private FoodOrderService foodOrderService;

    // 1️⃣ Create Payment Order for Food Cart
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createPaymentOrder(@RequestParam Long cartId,
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

    // 2️⃣ Verify Payment and Place Food Order
    @PostMapping("/verify")
    public ResponseEntity<?> verifyPaymentAndPlaceOrder(@RequestBody PaymentVerificationRequest request,
                                                        @RequestParam Long cartId) {
        try {
            paymentService.verifyAndSavePayment(request);
            var orders = foodOrderService.placeOrderFromCart(cartId, request.getRazorpayPaymentId());
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Payment verification failed: " + e.getMessage()));
        }
    }

    // 3️⃣ Create Payment Order for Slot Booking
    @PostMapping("/create/slot")
    public ResponseEntity<Map<String, Object>> createSlotPaymentOrder(@RequestParam Long slotId,
                                                                      @RequestParam(defaultValue = "INR") String currency) throws Exception {
        Map<String, Object> orderData = paymentService.createOrderForSlot(slotId, currency);
        return ResponseEntity.ok(orderData);
    }

    // 4️⃣ Verify Payment and Create Booking
    @PostMapping("/verify/slot")
    public ResponseEntity<?> verifyPaymentForSlot(@RequestParam Long userId,
                                                  @RequestParam Long slotId,
                                                  @RequestParam String orderId,
                                                  @RequestParam String paymentId,
                                                  @RequestParam String signature) {
        try {
            // Verify and Save Payment
            paymentService.verifyAndSavePaymentForSlot(userId, slotId, orderId, paymentId, signature);

            // Create Booking (combine)
            var bookingDto = bookingService.createBookingWithPayment(userId, slotId, orderId, paymentId, signature);

            return ResponseEntity.ok(Map.of(
                    "message", "Slot booking successful",
                    "booking", bookingDto
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Payment verification failed: " + e.getMessage()));
        }
    }


}
