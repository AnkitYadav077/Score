package com.Ankit.Score.Score.Repo;

import com.Ankit.Score.Score.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    // Add custom queries if needed
}
