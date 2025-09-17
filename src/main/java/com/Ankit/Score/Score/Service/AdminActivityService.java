package com.Ankit.Score.Score.Service;

import com.Ankit.Score.Score.Payloads.AdminActivityDto;
import java.util.List;

public interface AdminActivityService {
    List<AdminActivityDto> getActivitiesByRootAdmin(Long rootAdminId);
    List<AdminActivityDto> getActivitiesForAdminDashboard(Long adminId);
}