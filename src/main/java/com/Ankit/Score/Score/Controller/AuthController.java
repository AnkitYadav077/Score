package com.Ankit.Score.Score.Controller;

import com.Ankit.Score.Score.Entity.Admin;
import com.Ankit.Score.Score.Payloads.AdminRegisterRequest;
import com.Ankit.Score.Score.Payloads.JwtAuthRequest;
import com.Ankit.Score.Score.Repo.AdminRepo;
import com.Ankit.Score.Score.Security.JwtHelper;
import com.Ankit.Score.Score.Service.CustomUserDetailsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtHelper jwtHelper;
    private final AdminRepo adminRepo;

    public AuthController(AuthenticationManager authenticationManager,
                          CustomUserDetailsService userDetailsService,
                          JwtHelper jwtHelper, AdminRepo adminRepo) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtHelper = jwtHelper;
        this.adminRepo = adminRepo;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> createToken(@RequestBody JwtAuthRequest request) throws Exception {
        this.authenticate(request.getEmail(), request.getPassword());

        UserDetails userDetails = this.userDetailsService.loadUserByUsername(request.getEmail());
        Admin admin = userDetailsService.loadAdminByEmail(request.getEmail());

        String token = this.jwtHelper.generateToken(userDetails, admin.getId(), admin.getName());

        Map<String, String> response = new HashMap<>();
        response.put("token", token);

        return ResponseEntity.ok(response);
    }

    private void authenticate(String email, String password) throws Exception {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(email, password);

        try {
            this.authenticationManager.authenticate(authenticationToken);
        } catch (BadCredentialsException e) {
            throw new Exception("Invalid credentials !!");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerAdmin(@RequestBody AdminRegisterRequest request) {
        // Check if admin already exists
        if (adminRepo.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Create new admin
        Admin admin = new Admin();
        admin.setName(request.getName());
        admin.setEmail(request.getEmail());
        admin.setPassword(request.getPassword());
        admin.setParentAdmin(null); // Super admin has no parent

        Admin savedAdmin = adminRepo.save(admin);

        // Generate token
        UserDetails userDetails = userDetailsService.loadUserByUsername(savedAdmin.getEmail());
        String token = jwtHelper.generateToken(userDetails, savedAdmin.getId(), savedAdmin.getName());

        Map<String, String> response = new HashMap<>();
        response.put("token", token);

        return ResponseEntity.status(201).body(response);
    }
}