package com.Ankit.Score.Score.Repo;

import com.Ankit.Score.Score.Entity.FoodOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodOrderRepo extends JpaRepository<FoodOrder,Long> {
}
