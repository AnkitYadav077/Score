package com.Ankit.Score.Score.Service;

import com.Ankit.Score.Score.Entity.Cart;
import com.Ankit.Score.Score.Entity.CartItem;
import com.Ankit.Score.Score.Entity.FoodItem;
import com.Ankit.Score.Score.Entity.User;
import com.Ankit.Score.Score.Exceptions.ResourceNotFoundException;
import com.Ankit.Score.Score.Payloads.CartItemDTO;
import com.Ankit.Score.Score.Payloads.CartSummaryDTO;
import com.Ankit.Score.Score.Repo.CartItemRepo;
import com.Ankit.Score.Score.Repo.CartRepo;
import com.Ankit.Score.Score.Repo.FoodItemRepo;
import com.Ankit.Score.Score.Repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired
    private CartRepo cartRepo;

    @Autowired
    private CartItemRepo cartItemRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private FoodItemRepo foodItemRepo;

    // Get or create active unpaid cart for user
    public Cart getOrCreateActiveCart(Long userId) {
        return cartRepo.findByUser_UserIdAndIsPaidFalse(userId)
                .orElseGet(() -> {
                    User user = userRepo.findById(userId)
                            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
                    Cart cart = new Cart();
                    cart.setUser(user);
                    cart.setCreatedAt(LocalDateTime.now());
                    cart.setIsPaid(false);
                    cart.setTotalAmount(0);
                    return cartRepo.save(cart);
                });
    }

    @Transactional
    public CartItemDTO addToCart(Long userId, Long foodId, int quantity) {
        Cart cart = getOrCreateActiveCart(userId);

        FoodItem foodItem = foodItemRepo.findById(foodId)
                .orElseThrow(() -> new ResourceNotFoundException("FoodItem", "id", foodId));

        // Check if item already exists in cart
        Optional<CartItem> existingItem = cart.getCartItems() != null ?
                cart.getCartItems().stream()
                        .filter(item -> item.getFoodItem().getFoodId().equals(foodId))
                        .findFirst() : Optional.empty();

        CartItem cartItem;
        if (existingItem.isPresent()) {
            // Update quantity if item already exists
            cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        } else {
            // Create new cart item
            cartItem = new CartItem();
            cartItem.setUser(cart.getUser());
            cartItem.setFoodItem(foodItem);
            cartItem.setQuantity(quantity);
            cartItem.setPrice(foodItem.getPrice());
            cartItem.setCart(cart);

            // Add to cart's item collection
            if (cart.getCartItems() == null) {
                cart.setCartItems(new ArrayList<>());
            }
            cart.getCartItems().add(cartItem);
        }

        cartItem = cartItemRepo.save(cartItem);

        // Calculate total from ALL cart items including the new/updated one
        double total = cart.getCartItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        cart.setTotalAmount(total);
        cartRepo.save(cart);

        CartItemDTO dto = new CartItemDTO();
        dto.setId(cartItem.getCartItemId());
        dto.setFoodId(foodItem.getFoodId());
        dto.setFoodName(foodItem.getName());
        dto.setQuantity(cartItem.getQuantity());
        dto.setPrice(cartItem.getPrice());
        dto.setCategoryName(foodItem.getCategory().getName());
        return dto;
    }

    public CartSummaryDTO getCartSummary(Long userId) {
        Cart cart = getOrCreateActiveCart(userId);

        List<CartItemDTO> items = cart.getCartItems() != null ? cart.getCartItems().stream()
                .map(item -> {
                    CartItemDTO dto = new CartItemDTO();
                    dto.setId(item.getCartItemId());
                    dto.setFoodId(item.getFoodItem().getFoodId());
                    dto.setFoodName(item.getFoodItem().getName());
                    dto.setQuantity(item.getQuantity());
                    dto.setPrice(item.getPrice());
                    dto.setCategoryName(item.getFoodItem().getCategory().getName());
                    return dto;
                }).collect(Collectors.toList()) : List.of();

        CartSummaryDTO summary = new CartSummaryDTO();
        summary.setCartId(cart.getCartId());
        summary.setCartItems(items);
        summary.setTotalAmount(cart.getTotalAmount());
        return summary;
    }

    @Transactional
    public void removeCartItem(Long cartItemId) {
        CartItem cartItem = cartItemRepo.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", cartItemId));

        Cart cart = cartItem.getCart();

        if (cart != null && cart.getCartItems() != null) {
            cart.getCartItems().remove(cartItem);
        }

        cartItemRepo.delete(cartItem);

        if (cart != null) {
            double total = cart.getCartItems().stream()
                    .mapToDouble(item -> item.getPrice() * item.getQuantity())
                    .sum();
            cart.setTotalAmount(total);
            cartRepo.save(cart);
        }
    }

    @Transactional
    public void checkoutCart(Long userId) {
        Cart cart = cartRepo.findByUser_UserIdAndIsPaidFalse(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "userId", userId));

        cart.setIsPaid(true);
        cartRepo.save(cart);

        // TODO: Integrate payment logic here (e.g. Razorpay)
    }

    public Cart getCartById(Long cartId) {
        return cartRepo.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "id", cartId));
    }
}
