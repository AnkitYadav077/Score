package com.Ankit.Score.Score.Payloads;

import lombok.Data;
import java.util.List;

@Data
public class CartSummaryDTO {
    private Long cartId;             // Add this field
    private List<CartItemDTO> cartItems;
    private double totalAmount;
}