package com.Ankit.Score.Score.Controller;

import com.Ankit.Score.Score.Payloads.AdminDto;
import com.Ankit.Score.Score.Payloads.AdminActivityDto;
import com.Ankit.Score.Score.Payloads.CreateSubAdminRequest;
import com.Ankit.Score.Score.Service.AdminService;
import com.Ankit.Score.Score.Service.AdminActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final AdminActivityService adminActivityService;

    @Autowired
    public AdminController(AdminService adminService, AdminActivityService adminActivityService) {
        this.adminService = adminService;
        this.adminActivityService = adminActivityService;
    }

    // Create new admin (POST /admin)
    @PostMapping
    public ResponseEntity<AdminDto> createAdmin(@RequestBody AdminDto adminDto) {
        AdminDto savedAdmin = adminService.createAdmin(adminDto);
        return ResponseEntity.status(201).body(savedAdmin);
    }

    // Create sub-admin (POST /admin/{id}/subadmin)
    @PostMapping("/{id}/subadmin")
    public ResponseEntity<AdminDto> createSubAdmin(
            @RequestBody CreateSubAdminRequest request,
            @PathVariable Long id) {
        AdminDto subAdmin = adminService.createSubAdmin(request, id);
        return ResponseEntity.status(201).body(subAdmin);
    }

    // Update existing admin (PUT /admin/{id})
    @PutMapping("/{id}")
    public ResponseEntity<AdminDto> updateAdmin(@RequestBody AdminDto adminDto, @PathVariable Long id) {
        AdminDto updatedAdmin = adminService.updateAdmin(adminDto, id);
        return ResponseEntity.ok(updatedAdmin);
    }

    // Get admin by ID (GET /admin/{id})
    @GetMapping("/{id}")
    public ResponseEntity<AdminDto> getAdminById(@PathVariable Long id) {
        AdminDto admin = adminService.getAdminById(id);
        return ResponseEntity.ok(admin);
    }

    // Get sub-admins for an admin (GET /admin/{id}/subadmins)
    @GetMapping("/{id}/subadmins")
    public ResponseEntity<List<AdminDto>> getSubAdmins(@PathVariable Long id) {
        List<AdminDto> subAdmins = adminService.getSubAdmins(id);
        return ResponseEntity.ok(subAdmins);
    }

    // Get activities for admin dashboard (GET /admin/{id}/activities)
    @GetMapping("/{id}/activities")
    public ResponseEntity<List<AdminActivityDto>> getAdminActivities(@PathVariable Long id) {
        List<AdminActivityDto> activities = adminActivityService.getActivitiesForAdminDashboard(id);
        return ResponseEntity.ok(activities);
    }
}