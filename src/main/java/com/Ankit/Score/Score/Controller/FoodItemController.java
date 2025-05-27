package com.Ankit.Score.Score.Controller;

import com.Ankit.Score.Score.Payloads.FoodItemDto;
import com.Ankit.Score.Score.Service.FoodItemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/menu")
public class FoodItemController {

    private final FoodItemService foodItemService;

    public FoodItemController(FoodItemService foodItemService) {
        this.foodItemService = foodItemService;
    }

    @PostMapping
    public ResponseEntity<FoodItemDto> createFoodItem(@Valid @RequestBody FoodItemDto foodItemDto) {
        FoodItemDto created = foodItemService.createFoodItem(foodItemDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FoodItemDto> getFoodItemById(@PathVariable("id") Long foodId) {
        FoodItemDto dto = foodItemService.getFoodItemById(foodId);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<List<FoodItemDto>> getAllFoodItems() {
        List<FoodItemDto> list = foodItemService.getAllFoodItems();
        return ResponseEntity.ok(list);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FoodItemDto> updateFoodItem(@Valid @RequestBody FoodItemDto foodItemDto, @PathVariable("id") Long foodId) {
        FoodItemDto updated = foodItemService.updateFoodItem(foodItemDto, foodId);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFoodItem(@PathVariable("id") Long foodId) {
        foodItemService.deleteFoodItem(foodId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/category/{identifier}")
    public ResponseEntity<List<FoodItemDto>> getByCategoryNameOrId(@PathVariable String identifier) {
        List<FoodItemDto> items = foodItemService.getFoodItemsByCategoryNameOrId(identifier);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/search")
    public ResponseEntity<List<FoodItemDto>> searchFood(@RequestParam("q") String keyword) {
        List<FoodItemDto> results = foodItemService.searchFood(keyword);
        return ResponseEntity.ok(results);
    }
}
