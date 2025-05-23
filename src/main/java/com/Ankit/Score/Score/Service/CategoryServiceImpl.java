package com.Ankit.Score.Score.Service;

import com.Ankit.Score.Score.Entity.Category;
import com.Ankit.Score.Score.Entity.CategoryType;
import com.Ankit.Score.Score.Payloads.CategoryDto;
import com.Ankit.Score.Score.Repo.CategoryRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepo categoryRepository;
    private final ModelMapper modelMapper;

    @Override
    public CategoryDto addCategory(CategoryDto categoryDto) {
        Category category = toEntity(categoryDto);
        Category savedCategory = categoryRepository.save(category);
        return toDto(savedCategory);
    }

    @Override
    public List<CategoryDto> getCategoriesByType(CategoryType type) {
        return categoryRepository.findByType(type)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // Mapping methods
    private CategoryDto toDto(Category category) {
        return modelMapper.map(category, CategoryDto.class);
    }

    private Category toEntity(CategoryDto categoryDto) {
        return modelMapper.map(categoryDto, Category.class);
    }
}
