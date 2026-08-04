package com.dcplatform.api.auth;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

/**
 * Cuenta basica de email + password. Independiente de admin_users (dashboard interno)
 * y de leads (captura de marketing): ver database/migrations/V2__auth_users.sql.
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "email", nullable = false, length = 320)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 200)
    private String passwordHash;

    /**
     * USER o ADMIN (CHECK en la tabla). Viaja como claim "role" en el token de acceso y es lo
     * que SecurityConfig traduce a ROLE_USER / ROLE_ADMIN para proteger /api/v1/admin/**.
     */
    @Column(name = "role", nullable = false, length = 20)
    private String role = "USER";

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    protected User() {
        // requerido por JPA
    }

    public User(String email, String passwordHash) {
        this.email = email;
        this.passwordHash = passwordHash;
    }

    @PrePersist
    void onInsert() {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = OffsetDateTime.now(ZoneOffset.UTC);
    }

    public UUID getId() { return id; }

    public String getEmail() { return email; }

    public String getPasswordHash() { return passwordHash; }

    public String getRole() { return role; }

    public boolean isActive() { return active; }

    public OffsetDateTime getCreatedAt() { return createdAt; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
}
