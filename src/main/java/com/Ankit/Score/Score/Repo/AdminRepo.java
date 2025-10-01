package com.Ankit.Score.Score.Repo;

import com.Ankit.Score.Score.Entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRepo extends JpaRepository<Admin, Long> {
    Optional<Admin> findByEmail(String email);
    List<Admin> findByParentAdminId(Long parentAdminId);
    boolean existsByEmail(String email);

    @Query("SELECT a FROM Admin a WHERE a.parentAdmin.id = :adminId OR a.id = :adminId")
    List<Admin> findAllByAdminHierarchy(@Param("adminId") Long adminId);
}