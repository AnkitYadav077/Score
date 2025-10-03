package com.Ankit.Score.Score.Controller;

import com.Ankit.Score.Score.Payloads.AdminDto;
import com.Ankit.Score.Score.Payloads.AdminActivityDto;
import com.Ankit.Score.Score.Payloads.CreateSubAdminRequest;
import com.Ankit.Score.Score.Security.JwtHelper;
import com.Ankit.Score.Score.Service.AdminService;
import com.Ankit.Score.Score.Service.AdminActivityService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final AdminActivityService adminActivityService;
    private final JwtHelper jwtHelper;

    private Long getAuthenticatedAdminId(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return jwtHelper.getAdminIdFromToken(token);
        }
        throw new RuntimeException("Invalid token");
    }

    // Create new admin - Only Super Admin can create other admins
    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<AdminDto> createAdmin(@RequestBody AdminDto adminDto) {
        AdminDto savedAdmin = adminService.createAdmin(adminDto);
        return ResponseEntity.status(201).body(savedAdmin);
    }

    // Create sub-admin - Only Super Admin can create sub-admins
    @PostMapping("/subadmin")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<AdminDto> createSubAdmin(
            @RequestBody CreateSubAdminRequest request,
            HttpServletRequest httpRequest) {
        Long superAdminId = getAuthenticatedAdminId(httpRequest);
        AdminDto subAdmin = adminService.createSubAdmin(request, superAdminId);
        return ResponseEntity.status(201).body(subAdmin);
    }

    // Update my profile - Admin can update their own profile
    @PutMapping("/my-profile")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SUB_ADMIN')")
    public ResponseEntity<AdminDto> updateMyProfile(
            @RequestBody AdminDto adminDto,
            HttpServletRequest request) {
        Long adminId = getAuthenticatedAdminId(request);
        AdminDto updatedAdmin = adminService.updateAdmin(adminDto, adminId);
        return ResponseEntity.ok(updatedAdmin);
    }

    // Get my profile - Admin can view their own profile
    @GetMapping("/my-profile")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SUB_ADMIN')")
    public ResponseEntity<AdminDto> getMyProfile(HttpServletRequest request) {
        Long adminId = getAuthenticatedAdminId(request);
        AdminDto admin = adminService.getAdminById(adminId);
        return ResponseEntity.ok(admin);
    }

    // Get sub-admins for current super admin - Only Super Admin can view their sub-admins
    @GetMapping("/subadmins")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<AdminDto>> getMySubAdmins(HttpServletRequest request) {
        Long superAdminId = getAuthenticatedAdminId(request);
        List<AdminDto> subAdmins = adminService.getSubAdmins(superAdminId);
        return ResponseEntity.ok(subAdmins);
    }

    // Get activities for admin dashboard - Admin can view their own activities
    @GetMapping("/activities")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SUB_ADMIN')")
    public ResponseEntity<List<AdminActivityDto>> getMyActivities(HttpServletRequest request) {
        Long adminId = getAuthenticatedAdminId(request);
        List<AdminActivityDto> activities = adminActivityService.getActivitiesForAdminDashboard(adminId);
        return ResponseEntity.ok(activities);
    }

    // Super Admin can get all admins (both super and sub)
    @GetMapping("/all")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<AdminDto>> getAllAdmins() {
        List<AdminDto> admins = adminService.getAllAdmins();
        return ResponseEntity.ok(admins);
    }

    // Super Admin can get any admin by ID (for management purposes)
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<AdminDto> getAdminById(@PathVariable Long id) {
        AdminDto admin = adminService.getAdminById(id);
        return ResponseEntity.ok(admin);
    }

    // Super Admin can update any admin by ID (for management purposes)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<AdminDto> updateAdmin(@RequestBody AdminDto adminDto, @PathVariable Long id) {
        AdminDto updatedAdmin = adminService.updateAdmin(adminDto, id);
        return ResponseEntity.ok(updatedAdmin);
    }
}