package com.Ankit.Score.Score.Controller;

import com.Ankit.Score.Score.Payloads.FoodItemDto;
import com.Ankit.Score.Score.Service.FoodItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/menu")
public class FoodItemController {

    @Autowired
    private FoodItemService foodItemService;

    // Create a new FoodItem
    @PostMapping
    public ResponseEntity<FoodItemDto> createFoodItem(@RequestBody FoodItemDto foodItemDto) {
        FoodItemDto created = foodItemService.createFoodItem(foodItemDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // Retrieve a FoodItem by its ID
    @GetMapping("/{id}")
    public ResponseEntity<FoodItemDto> getFoodItemById(@PathVariable("id") Long foodId) {
        FoodItemDto dto = foodItemService.getFoodItemById(foodId);
        return ResponseEntity.ok(dto);
    }

    // Retrieve all FoodItems
    @GetMapping
    public ResponseEntity<List<FoodItemDto>> getAllFoodItems() {
        List<FoodItemDto> list = foodItemService.getAllFoodItems();
        return ResponseEntity.ok(list);
    }

    // Update an existing FoodItem
    @PutMapping("/{id}")
    public ResponseEntity<FoodItemDto> updateFoodItem(@RequestBody FoodItemDto foodItemDto, @PathVariable("id") Long foodId) {
        FoodItemDto updated = foodItemService.updateFoodItem(foodItemDto, foodId);
        return ResponseEntity.ok(updated);
    }

    // Delete a FoodItem
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFoodItem(@PathVariable("id") Long foodId) {
        foodItemService.deleteFoodItem(foodId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Get FoodItems by category name or ID
    @GetMapping("/category/{identifier}")
    public ResponseEntity<List<FoodItemDto>> getByCategoryNameOrId(@PathVariable String identifier) {
        List<FoodItemDto> items = foodItemService.getFoodItemsByCategoryNameOrId(identifier);
        return ResponseEntity.ok(items);
    }
}