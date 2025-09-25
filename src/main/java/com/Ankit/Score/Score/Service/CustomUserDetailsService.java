package com.Ankit.Score.Score.Service;

import com.Ankit.Score.Score.Entity.Admin;
import com.Ankit.Score.Score.Entity.User;
import com.Ankit.Score.Score.Repo.AdminRepo;
import com.Ankit.Score.Score.Repo.UserRepo;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AdminRepo adminRepo;
    private final UserRepo userRepo;

    public CustomUserDetailsService(AdminRepo adminRepo, UserRepo userRepo) {
        this.adminRepo = adminRepo;
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // First try to find as Admin
        Optional<Admin> adminOptional = adminRepo.findByEmail(email);
        if (adminOptional.isPresent()) {
            Admin admin = adminOptional.get();
            return new CustomUserDetails(
                    admin.getEmail(),
                    admin.getPassword(),
                    getAdminAuthorities(admin),
                    "ADMIN",
                    admin.getId()
            );
        }

        // Then try to find as User
        Optional<User> userOptional = userRepo.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return new CustomUserDetails(
                    user.getEmail(),
                    "", // OAuth users don't have passwords
                    getUserAuthorities(user),
                    "USER",
                    user.getUserId()
            );
        }

        throw new UsernameNotFoundException("User not found with email: " + email);
    }

    public Admin loadAdminByEmail(String email) {
        return adminRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found with email: " + email));
    }

    public User loadUserByEmail(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    private Collection<? extends GrantedAuthority> getAdminAuthorities(Admin admin) {
        String role = admin.getParentAdmin() == null ? "ROLE_SUPER_ADMIN" : "ROLE_SUB_ADMIN";
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }

    private Collection<? extends GrantedAuthority> getUserAuthorities(User user) {
        return user.getRoles().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}