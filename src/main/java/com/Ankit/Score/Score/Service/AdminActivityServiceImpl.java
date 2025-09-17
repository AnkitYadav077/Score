package com.Ankit.Score.Score.Service;

import com.Ankit.Score.Score.Entity.AdminActivity;
import com.Ankit.Score.Score.Payloads.AdminActivityDto;
import com.Ankit.Score.Score.Repo.AdminActivityRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminActivityServiceImpl implements AdminActivityService {

    private final AdminActivityRepo adminActivityRepo;
    private final ModelMapper modelMapper;

    @Autowired
    public AdminActivityServiceImpl(AdminActivityRepo adminActivityRepo, ModelMapper modelMapper) {
        this.adminActivityRepo = adminActivityRepo;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<AdminActivityDto> getActivitiesByRootAdmin(Long rootAdminId) {
        List<AdminActivity> activities = adminActivityRepo.findByRootAdminIdOrderByTimestampDesc(rootAdminId);
        return activities.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AdminActivityDto> getActivitiesForAdminDashboard(Long adminId) {
        List<AdminActivity> activities = adminActivityRepo.findByRootAdminAndSubAdmins(adminId, adminId);
        return activities.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private AdminActivityDto convertToDto(AdminActivity activity) {
        AdminActivityDto dto = modelMapper.map(activity, AdminActivityDto.class);
        dto.setPerformedById(activity.getPerformedBy().getId());
        dto.setPerformedByName(activity.getPerformedBy().getName());
        dto.setRootAdminId(activity.getRootAdmin().getId());
        return dto;
    }
}