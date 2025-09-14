package com.Ankit.Score.Score.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "admins")
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_admin_id")
    private Admin parentAdmin;

    @OneToMany(mappedBy = "parentAdmin", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Admin> subAdmins = new ArrayList<>();

    @OneToMany(mappedBy = "performedBy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AdminActivity> activities = new ArrayList<>();
}