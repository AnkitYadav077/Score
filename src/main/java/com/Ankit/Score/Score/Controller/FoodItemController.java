package com.Ankit.Score.Score.Controller;

import com.Ankit.Score.Score.Payloads.FoodItemDto;
import com.Ankit.Score.Score.Service.FoodItemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/menu")
public class FoodItemController {

    private final FoodItemService foodItemService;

    public FoodItemController(FoodItemService foodItemService) {
        this.foodItemService = foodItemService;
    }

    // Create food item - Only Admin can create food items
    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SUB_ADMIN')")
    public ResponseEntity<FoodItemDto> createFoodItem(@Valid @RequestBody FoodItemDto foodItemDto) {
        FoodItemDto created = foodItemService.createFoodItem(foodItemDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // Get food item by ID - Public access
    @GetMapping("/{id}")
    public ResponseEntity<FoodItemDto> getFoodItemById(@PathVariable("id") Long foodId) {
        FoodItemDto dto = foodItemService.getFoodItemById(foodId);
        return ResponseEntity.ok(dto);
    }

    // Get all food items - Public access
    @GetMapping
    public ResponseEntity<List<FoodItemDto>> getAllFoodItems() {
        List<FoodItemDto> list = foodItemService.getAllFoodItems();
        return ResponseEntity.ok(list);
    }

    // Update food item - Only Admin can update food items
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SUB_ADMIN')")
    public ResponseEntity<FoodItemDto> updateFoodItem(@Valid @RequestBody FoodItemDto foodItemDto, @PathVariable("id") Long foodId) {
        FoodItemDto updated = foodItemService.updateFoodItem(foodItemDto, foodId);
        return ResponseEntity.ok(updated);
    }

    // Delete food item - Only Admin can delete food items
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SUB_ADMIN')")
    public ResponseEntity<Void> deleteFoodItem(@PathVariable("id") Long foodId) {
        foodItemService.deleteFoodItem(foodId);
        return ResponseEntity.noContent().build();
    }

    // Get by category - Public access
    @GetMapping("/category/{identifier}")
    public ResponseEntity<List<FoodItemDto>> getByCategoryNameOrId(@PathVariable String identifier) {
        List<FoodItemDto> items = foodItemService.getFoodItemsByCategoryNameOrId(identifier);
        return ResponseEntity.ok(items);
    }

    // Search food - Public access
    @GetMapping("/search")
    public ResponseEntity<List<FoodItemDto>> searchFood(@RequestParam("q") String keyword) {
        List<FoodItemDto> results = foodItemService.searchFood(keyword);
        return ResponseEntity.ok(results);
    }
}