package com.Ankit.Score.Score.Controller;

import com.Ankit.Score.Score.Payloads.AdminDto;
import com.Ankit.Score.Score.Service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    // Create new admin
    @PostMapping
    public ResponseEntity<AdminDto> createAdmin(@RequestBody AdminDto adminDto) {
        AdminDto savedAdmin = adminService.createAdmin(adminDto);
        return ResponseEntity.status(201).body(savedAdmin);
    }

    // Update existing admin
    @PutMapping("/{id}")
    public ResponseEntity<AdminDto> updateAdmin(@RequestBody AdminDto adminDto, @PathVariable Long id) {
        AdminDto updatedAdmin = adminService.updateAdmin(adminDto, id);
        return ResponseEntity.ok(updatedAdmin);
    }

    // Get admin by ID
    @GetMapping("/{id}")
    public ResponseEntity<AdminDto> getAdminById(@PathVariable Long id) {
        AdminDto admin = adminService.getAdminById(id);
        return ResponseEntity.ok(admin);
    }
}
