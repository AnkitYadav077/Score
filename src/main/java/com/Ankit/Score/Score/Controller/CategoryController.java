package com.Ankit.Score.Score.Controller;

import com.Ankit.Score.Score.Payloads.CategoryDto;
import com.Ankit.Score.Score.Entity.CategoryType;
import com.Ankit.Score.Score.Service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    // Add a new category - Only Admin can add categories
    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SUB_ADMIN')")
    public ResponseEntity<CategoryDto> addCategory(@RequestBody CategoryDto categoryDto) {
        CategoryDto createdCategory = categoryService.addCategory(categoryDto);
        return ResponseEntity.ok(createdCategory);
    }

    // Get categories by type - Public access (no authentication required)
    @GetMapping("/{type}")
    public ResponseEntity<List<CategoryDto>> getCategoriesByType(@PathVariable CategoryType type) {
        List<CategoryDto> categories = categoryService.getCategoriesByType(type);
        return ResponseEntity.ok(categories);
    }

    // Update category price - Only Admin can update prices
    @PutMapping("/{id}/update-price")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SUB_ADMIN')")
    public ResponseEntity<CategoryDto> updateCategoryPrice(
            @PathVariable Long id,
            @RequestParam Integer basePrice,
            @RequestParam Integer eveningPrice) {
        CategoryDto updated = categoryService.updateCategoryPrice(id, basePrice, eveningPrice);
        return ResponseEntity.ok(updated);
    }
}
