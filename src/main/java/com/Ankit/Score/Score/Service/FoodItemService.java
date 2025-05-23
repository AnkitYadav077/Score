package com.Ankit.Score.Score.Service;

import com.Ankit.Score.Score.Payloads.FoodItemDto;
import java.util.List;

public interface FoodItemService {

    FoodItemDto createFoodItem(FoodItemDto foodItemDto);
    FoodItemDto getFoodItemById(Long foodId);
    List<FoodItemDto> getAllFoodItems();
    FoodItemDto updateFoodItem(FoodItemDto foodItemDto, Long foodId);
    void deleteFoodItem(Long foodId);
    List<FoodItemDto> getFoodItemsByCategoryNameOrId(String categoryIdentifier);
}
