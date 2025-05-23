package com.Ankit.Score.Score.Repo;

import com.Ankit.Score.Score.Entity.SportSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SportSlotRepo extends JpaRepository<SportSlot,Long> {
    List<SportSlot> findByCategory_Id(Long categoryId);
    List<SportSlot> findByCategory_Name(String categoryName);
}
