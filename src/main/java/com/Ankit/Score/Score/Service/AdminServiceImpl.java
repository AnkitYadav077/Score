package com.Ankit.Score.Score.Service;

import com.Ankit.Score.Score.Entity.Admin;
import com.Ankit.Score.Score.Entity.AdminActivity;
import com.Ankit.Score.Score.Exceptions.ResourceNotFoundException;
import com.Ankit.Score.Score.Payloads.AdminDto;
import com.Ankit.Score.Score.Payloads.CreateSubAdminRequest;
import com.Ankit.Score.Score.Repo.AdminActivityRepo;
import com.Ankit.Score.Score.Repo.AdminRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    private final AdminRepo adminRepo;
    private final AdminActivityRepo adminActivityRepo;
    private final ModelMapper modelMapper;

    @Autowired
    public AdminServiceImpl(AdminRepo adminRepo, AdminActivityRepo adminActivityRepo, ModelMapper modelMapper) {
        this.adminRepo = adminRepo;
        this.adminActivityRepo = adminActivityRepo;
        this.modelMapper = modelMapper;
    }

    @Override
    public AdminDto createAdmin(AdminDto adminDto) {
        Admin admin = modelMapper.map(adminDto, Admin.class);
        Admin savedAdmin = adminRepo.save(admin);

        logActivity(savedAdmin.getId(), "CREATE_ADMIN", "Admin account created");

        return modelMapper.map(savedAdmin, AdminDto.class);
    }

    @Override
    @Transactional
    public AdminDto createSubAdmin(CreateSubAdminRequest request, Long parentAdminId) {
        Admin parentAdmin = adminRepo.findById(parentAdminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin", "id", parentAdminId));

        if (adminRepo.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        Admin subAdmin = new Admin();
        subAdmin.setName(request.getName());
        subAdmin.setEmail(request.getEmail());
        subAdmin.setPassword(request.getPassword());
        subAdmin.setParentAdmin(parentAdmin);

        Admin savedSubAdmin = adminRepo.save(subAdmin);

        logActivity(parentAdminId, "CREATE_SUB_ADMIN",
                "Created sub-admin: " + request.getName() + " (" + request.getEmail() + ")");

        return modelMapper.map(savedSubAdmin, AdminDto.class);
    }

    @Override
    public AdminDto updateAdmin(AdminDto adminDto, Long id) {
        Admin admin = adminRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin", "id", id));

        admin.setName(adminDto.getName());
        admin.setEmail(adminDto.getEmail());

        Admin updatedAdmin = adminRepo.save(admin);

        logActivity(id, "UPDATE_ADMIN", "Admin details updated");

        return modelMapper.map(updatedAdmin, AdminDto.class);
    }

    @Override
    public AdminDto getAdminById(Long id) {
        Admin admin = adminRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin", "id", id));
        return modelMapper.map(admin, AdminDto.class);
    }

    @Override
    public List<AdminDto> getSubAdmins(Long adminId) {
        List<Admin> subAdmins = adminRepo.findByParentAdminId(adminId);
        return subAdmins.stream()
                .map(admin -> modelMapper.map(admin, AdminDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void logActivity(Long adminId, String action, String description) {
        Admin admin = adminRepo.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin", "id", adminId));

        Admin rootAdmin = findRootAdmin(admin);

        AdminActivity activity = new AdminActivity();
        activity.setAction(action);
        activity.setDescription(description);
        activity.setTimestamp(LocalDateTime.now());
        activity.setPerformedBy(admin);
        activity.setRootAdmin(rootAdmin);

        adminActivityRepo.save(activity);
    }

    private Admin findRootAdmin(Admin admin) {
        if (admin.getParentAdmin() == null) {
            return admin;
        }
        return findRootAdmin(admin.getParentAdmin());
    }
}