package com.Ankit.Score.Score.Controller;

import com.Ankit.Score.Score.Payloads.CartItemDTO;
import com.Ankit.Score.Score.Payloads.CartSummaryDTO;
import com.Ankit.Score.Score.Service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    // Add an item to the cart
    @PostMapping("/add")
    public ResponseEntity<CartItemDTO> addToCart(
            @RequestParam Long userId,
            @RequestParam Long foodId,
            @RequestParam int quantity
    ) {
        CartItemDTO dto = cartService.addToCart(userId, foodId, quantity);
        return ResponseEntity.ok(dto);
    }

    // Get cart summary (items + total amount)
    @GetMapping("/{userId}")
    public ResponseEntity<CartSummaryDTO> getCart(@PathVariable Long userId) {
        CartSummaryDTO cartSummary = cartService.getCartSummary(userId);
        return ResponseEntity.ok(cartSummary);
    }

    // Remove an item from cart
    @DeleteMapping("/remove/{cartItemId}")
    public ResponseEntity<String> removeCartItem(@PathVariable Long cartItemId) {
        cartService.removeCartItem(cartItemId);
        return ResponseEntity.ok("Cart item removed successfully!");
    }
}
