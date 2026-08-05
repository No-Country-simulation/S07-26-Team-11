package com.dcplatform.api.auth;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * Lista de revocacion para JWT stateless. La clave es el hash SHA-256 del token
 * crudo (nunca se guarda el token en si). Sirve tanto para logout de esta sesion
 * basica como, mas adelante, para revocar tokens de magic link.
 */
@Entity
@Table(name = "revoked_tokens")
public class RevokedToken {

    @Id
    @Column(name = "token_hash", nullable = false, updatable = false, length = 64)
    private String tokenHash;

    @Column(name = "expires_at", nullable = false, updatable = false)
    private OffsetDateTime expiresAt;

    @Column(name = "revoked_at", nullable = false, updatable = false)
    private OffsetDateTime revokedAt;

    protected RevokedToken() {
        // requerido por JPA
    }

    public RevokedToken(String tokenHash, OffsetDateTime expiresAt) {
        this.tokenHash = tokenHash;
        this.expiresAt = expiresAt;
        this.revokedAt = OffsetDateTime.now(ZoneOffset.UTC);
    }

    public String getTokenHash() { return tokenHash; }

    public OffsetDateTime getExpiresAt() { return expiresAt; }

    public OffsetDateTime getRevokedAt() { return revokedAt; }
}
