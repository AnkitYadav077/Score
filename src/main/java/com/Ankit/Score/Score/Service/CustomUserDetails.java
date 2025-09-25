package com.Ankit.Score.Score.Service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class CustomUserDetails extends User {
    private final String userType;
    private final Long userId;

    public CustomUserDetails(String username, String password,
                             Collection<? extends GrantedAuthority> authorities,
                             String userType, Long userId) {
        super(username, password, authorities);
        this.userType = userType;
        this.userId = userId;
    }

    public String getUserType() {
        return userType;
    }

    public Long getUserId() {
        return userId;
    }

    public boolean isAdmin() {
        return "ADMIN".equals(userType);
    }

    public boolean isUser() {
        return "USER".equals(userType);
    }
}