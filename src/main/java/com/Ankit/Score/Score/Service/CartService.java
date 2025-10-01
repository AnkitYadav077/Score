package com.Ankit.Score.Score.Service;

import com.Ankit.Score.Score.Entity.Cart;
import com.Ankit.Score.Score.Payloads.CartItemDTO;
import com.Ankit.Score.Score.Payloads.CartSummaryDTO;

public interface CartService {
    CartItemDTO addToCart(Long userId, Long foodId, int quantity);
    CartSummaryDTO getCartSummary(Long userId);
    void removeCartItem(Long cartItemId);
    void checkoutCart(Long userId);
    Cart getCartById(Long cartId);
    Cart getOrCreateActiveCart(Long userId);
}