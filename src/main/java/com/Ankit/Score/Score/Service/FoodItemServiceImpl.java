package com.Ankit.Score.Score.Service;

import com.Ankit.Score.Score.Entity.Category;
import com.Ankit.Score.Score.Entity.FoodItem;
import com.Ankit.Score.Score.Exceptions.ResourceNotFoundException;
import com.Ankit.Score.Score.Payloads.FoodItemDto;
import com.Ankit.Score.Score.Repo.CategoryRepo;
import com.Ankit.Score.Score.Repo.FoodItemRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FoodItemServiceImpl implements FoodItemService {

    @Autowired
    private FoodItemRepo foodItemRepo;

    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public FoodItemDto createFoodItem(FoodItemDto foodItemDto) {
        FoodItem foodItem = dtoToEntity(foodItemDto);
        FoodItem saved = foodItemRepo.save(foodItem);
        return entityToDto(saved);
    }

    @Override
    public FoodItemDto getFoodItemById(Long foodId) {
        FoodItem foodItem = foodItemRepo.findById(foodId)
                .orElseThrow(() -> new ResourceNotFoundException("FoodItem", "id", foodId));
        return entityToDto(foodItem);
    }

    @Override
    public List<FoodItemDto> getAllFoodItems() {
        return foodItemRepo.findAll().stream().map(this::entityToDto).collect(Collectors.toList());
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
        return entityToDto(updated);
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
        try {
            Long categoryId = Long.parseLong(categoryIdentifier);
            foodItems = foodItemRepo.findByCategory_Id(categoryId);
        } catch (NumberFormatException e) {
            foodItems = foodItemRepo.findByCategory_Name(categoryIdentifier);
        }
        return foodItems.stream().map(this::entityToDto).collect(Collectors.toList());
    }

    // Converts DTO to Entity
    private FoodItem dtoToEntity(FoodItemDto dto) {
        FoodItem foodItem = modelMapper.map(dto, FoodItem.class);
        if (dto.getCategory() != null && dto.getCategory().getId() != null) {
            Category category = categoryRepo.findById(dto.getCategory().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", dto.getCategory().getId()));
            foodItem.setCategory(category);
        }
        return foodItem;
    }

    // Converts Entity to DTO
    private FoodItemDto entityToDto(FoodItem entity) {
        return modelMapper.map(entity, FoodItemDto.class);
    }
}
