package com.Ankit.Score.Score.Service;

import com.Ankit.Score.Score.Entity.Category;
import com.Ankit.Score.Score.Entity.CategoryType;
import com.Ankit.Score.Score.Payloads.CategoryDto;
import com.Ankit.Score.Score.Repo.CategoryRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepo categoryRepository;
    private final ModelMapper modelMapper;

    @Override
    public CategoryDto addCategory(CategoryDto categoryDto) {
        if (categoryRepository.existsByNameAndType(categoryDto.getName(), categoryDto.getType())) {
            throw new RuntimeException("Category with this name and type already exists");
        }

        Category category = modelMapper.map(categoryDto, Category.class);
        Category savedCategory = categoryRepository.save(category);
        return modelMapper.map(savedCategory, CategoryDto.class);
    }

    @Override
    public List<CategoryDto> getCategoriesByType(CategoryType type) {
        return categoryRepository.findByType(type).stream()
                .map(category -> modelMapper.map(category, CategoryDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto updateCategoryPrice(Long id, Integer basePrice, Integer eveningPrice) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (category.getType() != CategoryType.SPORT) {
            throw new RuntimeException("Prices can only be set for SPORT categories");
        }

        category.setBasePrice(basePrice);
        category.setEveningPrice(eveningPrice);
        Category saved = categoryRepository.save(category);

        return modelMapper.map(saved, CategoryDto.class);
    }

}
