package com.foodsave.platform.iam.domain.model;

import java.time.Instant;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "accounts")
public class Account {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "full_name", nullable = false, length = 160)
    private String fullName;
    @Column(nullable = false, unique = true, length = 254)
    private String email;
    @Column(nullable = false, length = 30)
    private String phone = "";
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 30)
    private AccountRole role;
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;
    @Column(nullable = false)
    private boolean active = true;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    protected Account() {}

    public Account(String fullName, String email, String phone, AccountRole role, String passwordHash) {
        this.fullName = fullName.trim();
        this.email = email.trim().toLowerCase();
        this.phone = phone == null ? "" : phone.trim();
        this.role = role;
        this.passwordHash = passwordHash;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public void updateProfile(String fullName, String phone) {
        if (fullName == null || fullName.isBlank()) throw new IllegalArgumentException("account.name.required");
        this.fullName = fullName.trim();
        this.phone = phone == null ? "" : phone.trim();
        this.updatedAt = Instant.now();
    }

    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public AccountRole getRole() { return role; }
    public String getPasswordHash() { return passwordHash; }
    public boolean isActive() { return active; }
}
