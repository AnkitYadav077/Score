package com.Ankit.Score.Score.Service;

import com.Ankit.Score.Score.Payloads.AdminDto;
import com.Ankit.Score.Score.Payloads.CreateSubAdminRequest;
import java.util.List;

public interface AdminService {
    AdminDto createAdmin(AdminDto adminDto);
    AdminDto updateAdmin(AdminDto adminDto, Long id);
    AdminDto getAdminById(Long id);
    AdminDto createSubAdmin(CreateSubAdminRequest request, Long parentAdminId);
    List<AdminDto> getSubAdmins(Long adminId);
    void logActivity(Long adminId, String action, String description);
    List<AdminDto> getAllAdmins();
}
