package com.Ankit.Score.Score.Service;

import com.Ankit.Score.Score.Entity.Admin;
import com.Ankit.Score.Score.Repo.AdminRepo;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AdminRepo adminRepo;

    public CustomUserDetailsService(AdminRepo adminRepo) {
        this.adminRepo = adminRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Admin admin = adminRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found with email: " + email));

        return new User(
                admin.getEmail(),
                admin.getPassword(),
                getAuthorities(admin)
        );
    }

    public Admin loadAdminByEmail(String email) {
        return adminRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found with email: " + email));
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Admin admin) {
        // Determine role based on hierarchy
        String role = admin.getParentAdmin() == null ? "ROLE_SUPER_ADMIN" : "ROLE_SUB_ADMIN";
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }
}