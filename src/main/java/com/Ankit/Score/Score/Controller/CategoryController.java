package com.Ankit.Score.Score.Controller;

import com.Ankit.Score.Score.Entity.Category;
import com.Ankit.Score.Score.Payloads.CategoryDto;
import com.Ankit.Score.Score.Entity.CategoryType;
import com.Ankit.Score.Score.Service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/category")
@RequiredArgsConstructor
public class CategoryController {


    private final CategoryService categoryService;

    // Add a new category
    @PostMapping
    public ResponseEntity<CategoryDto> addCategory(@RequestBody CategoryDto categoryDto) {
        CategoryDto createdCategory = categoryService.addCategory(categoryDto);
        return ResponseEntity.ok(createdCategory);
    }

    // Get categories by type (e.g., SPORT, FOOD)
    @GetMapping("/{type}")
    public ResponseEntity<List<CategoryDto>> getCategoriesByType(@PathVariable CategoryType type) {
        List<CategoryDto> categories = categoryService.getCategoriesByType(type);
        return ResponseEntity.ok(categories);
    }


    @PutMapping("/{id}/update-price")
    public ResponseEntity<CategoryDto> updateCategoryPrice(
            @PathVariable Long id,
            @RequestParam Integer basePrice,
            @RequestParam Integer eveningPrice) {
        CategoryDto updated = categoryService.updateCategoryPrice(id, basePrice, eveningPrice);
        return ResponseEntity.ok(updated);
    }


}
