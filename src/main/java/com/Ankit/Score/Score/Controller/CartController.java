package com.Ankit.Score.Score.Controller;

import com.Ankit.Score.Score.Payloads.CartItemDTO;
import com.Ankit.Score.Score.Payloads.CartSummaryDTO;
import com.Ankit.Score.Score.Security.JwtHelper;
import com.Ankit.Score.Score.Service.CartServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartServiceImpl cartService;
    private final JwtHelper jwtHelper;

    // Add an item to the cart
    @PostMapping("/add")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CartItemDTO> addToCart(
            HttpServletRequest request,
            @RequestParam Long foodId,
            @RequestParam int quantity
    ) {
        Long userId = getUserIdFromToken(request);
        CartItemDTO dto = cartService.addToCart(userId, foodId, quantity);
        return ResponseEntity.ok(dto);
    }

    // Get cart summary (items + total amount)
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CartSummaryDTO> getCart(HttpServletRequest request) {
        Long userId = getUserIdFromToken(request);
        CartSummaryDTO cartSummary = cartService.getCartSummary(userId);
        return ResponseEntity.ok(cartSummary);
    }

    // Remove an item from cart
    @DeleteMapping("/remove/{cartItemId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> removeCartItem(@PathVariable Long cartItemId) {
        cartService.removeCartItem(cartItemId);
        return ResponseEntity.ok("Cart item removed successfully!");
    }

    private Long getUserIdFromToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return jwtHelper.getUserIdFromToken(token);
        }
        throw new RuntimeException("Invalid token");
    }
}