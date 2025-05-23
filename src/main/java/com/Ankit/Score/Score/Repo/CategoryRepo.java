package com.Ankit.Score.Score.Repo;

import com.Ankit.Score.Score.Entity.Category;
import com.Ankit.Score.Score.Entity.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepo extends JpaRepository<Category,Long> {
    List<Category> findByType(CategoryType type);
}
