package com.Ankit.Score.Score.Service;

import com.Ankit.Score.Score.Payloads.FoodOrderDto;

import java.util.List;

public interface FoodOrderService {

    FoodOrderDto createOrder(FoodOrderDto foodOrderDto);

    FoodOrderDto getOrderById(Long orderId);

    List<FoodOrderDto> getAllOrders();

    List<FoodOrderDto> getOrdersByUser(Long userId);

    FoodOrderDto updateOrder(Long orderId, FoodOrderDto foodOrderDto);
}
