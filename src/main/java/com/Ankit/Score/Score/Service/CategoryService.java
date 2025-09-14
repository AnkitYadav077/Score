package com.Ankit.Score.Score.Service;

import com.Ankit.Score.Score.Entity.CategoryType;
import com.Ankit.Score.Score.Payloads.CategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto addCategory(CategoryDto categoryDto);
    List<CategoryDto> getCategoriesByType(CategoryType type);
    CategoryDto updateCategoryPrice(Long id, Integer basePrice, Integer eveningPrice);
}
