package com.Ankit.Score.Score.Controller;

import com.Ankit.Score.Score.Entity.Cart;
import com.Ankit.Score.Score.Payloads.PaymentVerificationRequest;
import com.Ankit.Score.Score.Service.CartService;
import com.Ankit.Score.Score.Service.PaymentService;
import com.Ankit.Score.Score.Service.FoodOrderService;
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
    private FoodOrderService foodOrderService;

    // Create Razorpay Payment Order
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createPaymentOrder(@RequestParam Long cartId,
                                                                  @RequestParam String currency) throws Exception {
        // Get Cart from DB
        Cart cart = cartService.getCartById(cartId);
        if (cart == null || cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Cart is empty or invalid"));
        }

        int amount = (int) Math.round(cart.getTotalAmount()); // Amount in rupees (int)
        String receipt = "cart_receipt_" + cartId;

        Map<String, Object> response = paymentService.createPaymentOrder(amount, currency, receipt);
        return ResponseEntity.ok(response);
    }


    // Verify Payment & Place Order for Cart
    @PostMapping("/verify")
    public ResponseEntity<?> verifyPaymentAndPlaceOrder(@RequestBody PaymentVerificationRequest request,
                                                        @RequestParam Long cartId) {  // cartId param here
        try {
            paymentService.verifyAndSavePayment(request);
            var orders = foodOrderService.placeOrderFromCart(cartId, request.getRazorpayPaymentId());
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Payment verification failed: " + e.getMessage());
        }
    }
}
