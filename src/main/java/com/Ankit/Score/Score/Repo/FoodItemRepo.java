package com.Ankit.Score.Score.Repo;

import com.Ankit.Score.Score.Entity.FoodItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodItemRepo extends JpaRepository<FoodItem, Long> {
    List<FoodItem> findByCategory_NameIgnoreCase(String categoryName);
    List<FoodItem> findByCategory_Id(Long categoryId);
    List<FoodItem> findByNameContainingIgnoreCase(String keyword);
}
