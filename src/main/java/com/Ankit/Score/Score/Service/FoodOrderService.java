package com.Ankit.Score.Score.Service;

import com.Ankit.Score.Score.Payloads.FoodOrderDto;

import java.util.List;

public interface FoodOrderService {
    List<FoodOrderDto> placeOrderFromCart(Long cartId, String razorpayPaymentId) throws Exception;
}