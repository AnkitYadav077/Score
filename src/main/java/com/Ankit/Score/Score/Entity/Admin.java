package com.Ankit.Score.Score.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "admins")
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;

    private String role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_admin_id")
    private Admin parentAdmin;

    private LocalDateTime createdAt;

    // Default constructor
    public Admin() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() {
        if (this.role == null) {
            return this.parentAdmin == null ? "SUPER_ADMIN" : "SUB_ADMIN";
        }
        return role;
    }

    public void setRole(String role) { this.role = role; }

    public Admin getParentAdmin() { return parentAdmin; }
    public void setParentAdmin(Admin parentAdmin) { this.parentAdmin = parentAdmin; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}