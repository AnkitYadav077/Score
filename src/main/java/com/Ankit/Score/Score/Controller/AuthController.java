package com.Ankit.Score.Score.Controller;

import com.Ankit.Score.Score.Entity.Admin;
import com.Ankit.Score.Score.Payloads.AdminRegisterRequest;
import com.Ankit.Score.Score.Payloads.JwtAuthRequest;
import com.Ankit.Score.Score.Repo.AdminRepo;
import com.Ankit.Score.Score.Security.JwtHelper;
import com.Ankit.Score.Score.Service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtHelper jwtHelper;
    private final AdminRepo adminRepo;
    private final PasswordEncoder passwordEncoder;



    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> createToken(@RequestBody JwtAuthRequest request) throws Exception {
        this.authenticate(request.getEmail(), request.getPassword());

        UserDetails userDetails = this.userDetailsService.loadUserByUsername(request.getEmail());
        String token = this.jwtHelper.generateToken(userDetails);

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("message", "Login successful");
        response.put("userType", ((com.Ankit.Score.Score.Service.CustomUserDetails) userDetails).getUserType());

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
        if (adminRepo.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        Admin admin = new Admin();
        admin.setName(request.getName());
        admin.setEmail(request.getEmail());

        // Encode password before saving
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        admin.setPassword(encodedPassword);
        admin.setParentAdmin(null);

        Admin savedAdmin = adminRepo.save(admin);

        UserDetails userDetails = userDetailsService.loadUserByUsername(savedAdmin.getEmail());
        String token = jwtHelper.generateToken(userDetails);

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("message", "Admin registered successfully");
        response.put("adminId", savedAdmin.getId().toString());

        return ResponseEntity.status(201).body(response);
    }
}