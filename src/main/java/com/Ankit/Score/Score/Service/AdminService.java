package com.Ankit.Score.Score.Service;

import com.Ankit.Score.Score.Payloads.AdminDto;

public interface AdminService {
    AdminDto createAdmin(AdminDto adminDto);
    AdminDto updateAdmin(AdminDto adminDto, Long id);
    AdminDto getAdminById(Long id);
}
