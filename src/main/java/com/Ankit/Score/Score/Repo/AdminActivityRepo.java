package com.Ankit.Score.Score.Repo;

import com.Ankit.Score.Score.Entity.AdminActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface AdminActivityRepo extends JpaRepository<AdminActivity, Long> {

    List<AdminActivity> findByRootAdminIdOrderByTimestampDesc(Long rootAdminId);

    @Query("SELECT a FROM AdminActivity a WHERE a.rootAdmin.id = :rootAdminId " +
            "AND (a.performedBy.id = :adminId OR a.performedBy.parentAdmin.id = :adminId) " +
            "ORDER BY a.timestamp DESC")
    List<AdminActivity> findByRootAdminAndSubAdmins(@Param("rootAdminId") Long rootAdminId,
                                                    @Param("adminId") Long adminId);
}