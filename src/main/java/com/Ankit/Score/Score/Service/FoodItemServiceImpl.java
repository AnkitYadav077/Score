package com.Ankit.Score.Score.Service;

import com.Ankit.Score.Score.Entity.Category;
import com.Ankit.Score.Score.Entity.FoodItem;
import com.Ankit.Score.Score.Exceptions.ResourceNotFoundException;
import com.Ankit.Score.Score.Payloads.FoodItemDto;
import com.Ankit.Score.Score.Repo.CategoryRepo;
import com.Ankit.Score.Score.Repo.FoodItemRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FoodItemServiceImpl implements FoodItemService {

    private final FoodItemRepo foodItemRepo;
    private final CategoryRepo categoryRepo;
    private final ModelMapper modelMapper;


    @Override
    public FoodItemDto createFoodItem(FoodItemDto foodItemDto) {
        FoodItem foodItem = convertToEntity(foodItemDto);
        FoodItem saved = foodItemRepo.save(foodItem);
        return convertToDto(saved);
    }

    @Override
    public FoodItemDto getFoodItemById(Long foodId) {
        FoodItem foodItem = foodItemRepo.findById(foodId)
                .orElseThrow(() -> new ResourceNotFoundException("FoodItem", "id", foodId));
        return convertToDto(foodItem);
    }

    @Override
    public List<FoodItemDto> getAllFoodItems() {
        return foodItemRepo.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public FoodItemDto updateFoodItem(FoodItemDto foodItemDto, Long foodId) {
        FoodItem existing = foodItemRepo.findById(foodId)
                .orElseThrow(() -> new ResourceNotFoundException("FoodItem", "id", foodId));

        existing.setName(foodItemDto.getName());
        existing.setPrice(foodItemDto.getPrice());
        existing.setDescription(foodItemDto.getDescription());

        if (foodItemDto.getCategory() != null && foodItemDto.getCategory().getId() != null) {
            Category category = categoryRepo.findById(foodItemDto.getCategory().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", foodItemDto.getCategory().getId()));
            existing.setCategory(category);
        }

        FoodItem updated = foodItemRepo.save(existing);
        return convertToDto(updated);
    }

    @Override
    public void deleteFoodItem(Long foodId) {
        FoodItem existing = foodItemRepo.findById(foodId)
                .orElseThrow(() -> new ResourceNotFoundException("FoodItem", "id", foodId));
        foodItemRepo.delete(existing);
    }

    @Override
    public List<FoodItemDto> getFoodItemsByCategoryNameOrId(String categoryIdentifier) {
        List<FoodItem> foodItems;
        if (categoryIdentifier == null || categoryIdentifier.isBlank()) {
            throw new IllegalArgumentException("Category identifier must not be null or blank");
        }

        try {
            Long categoryId = Long.parseLong(categoryIdentifier);
            foodItems = foodItemRepo.findByCategory_Id(categoryId);
        } catch (NumberFormatException e) {
            foodItems = foodItemRepo.findByCategory_NameIgnoreCase(categoryIdentifier.trim());
        }

        return foodItems.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Override
    public List<FoodItemDto> searchFood(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            throw new IllegalArgumentException("Search keyword must not be null or blank");
        }
        List<FoodItem> foodItems = foodItemRepo.findByNameContainingIgnoreCase(keyword.trim());
        return foodItems.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // DTO to Entity conversion with category fetch
    private FoodItem convertToEntity(FoodItemDto dto) {
        FoodItem foodItem = modelMapper.map(dto, FoodItem.class);

        if (dto.getCategory() != null && dto.getCategory().getId() != null) {
            Category category = categoryRepo.findById(dto.getCategory().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", dto.getCategory().getId()));
            foodItem.setCategory(category);
        } else {
            throw new IllegalArgumentException("Category information is required for FoodItem");
        }
        return foodItem;
    }

    // Entity to DTO conversion
    private FoodItemDto convertToDto(FoodItem entity) {
        return modelMapper.map(entity, FoodItemDto.class);
    }
}
