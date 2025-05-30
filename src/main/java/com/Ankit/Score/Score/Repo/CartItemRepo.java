package com.Ankit.Score.Score.Repo;

import com.Ankit.Score.Score.Entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepo extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUser_UserId(Long userId);
    void deleteByUser_UserId(Long userId);
}
