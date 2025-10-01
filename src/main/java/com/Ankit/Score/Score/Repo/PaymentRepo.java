package com.Ankit.Score.Score.Repo;

import com.Ankit.Score.Score.Entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepo extends JpaRepository<Payment, Long> {

    List<Payment> findByUserId(Long userId);

    // 🔥 Add this method for Razorpay payment lookup
    Optional<Payment> findByTransactionId(String transactionId);
}