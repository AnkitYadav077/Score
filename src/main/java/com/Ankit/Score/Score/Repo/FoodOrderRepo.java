package com.Ankit.Score.Score.Repo;

import com.Ankit.Score.Score.Entity.FoodOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodOrderRepo extends JpaRepository<FoodOrder, Long> {
    List<FoodOrder> findByUser_UserId(Long userId);
    List<FoodOrder> findByUser_UserIdAndPaymentStatus(Long userId, String paymentStatus);
}
