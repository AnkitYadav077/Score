package com.Ankit.Score.Score.Service;

import com.Ankit.Score.Score.Entity.Admin;
import com.Ankit.Score.Score.Exceptions.ResourceNotFoundException;
import com.Ankit.Score.Score.Payloads.AdminDto;
import com.Ankit.Score.Score.Repo.AdminRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {

    private final AdminRepo adminRepo;
    private final ModelMapper modelMapper;

    @Autowired
    public AdminServiceImpl(AdminRepo adminRepo, ModelMapper modelMapper) {
        this.adminRepo = adminRepo;
        this.modelMapper = modelMapper;
    }

    @Override
    public AdminDto createAdmin(AdminDto adminDto) {
        Admin admin = modelMapper.map(adminDto, Admin.class);
        Admin savedAdmin = adminRepo.save(admin);
        return modelMapper.map(savedAdmin, AdminDto.class);
    }

    @Override
    public AdminDto updateAdmin(AdminDto adminDto, Long id) {
        Admin admin = adminRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin", "id", id));

        admin.setName(adminDto.getName());
        admin.setEmail(adminDto.getEmail());

        Admin updatedAdmin = adminRepo.save(admin);
        return modelMapper.map(updatedAdmin, AdminDto.class);
    }

    @Override
    public AdminDto getAdminById(Long id) {
        Admin admin = adminRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin", "id", id));
        return modelMapper.map(admin, AdminDto.class);
    }
}
