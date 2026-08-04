package com.dcplatform.api.auth;

import com.dcplatform.api.security.jwt.JwtService;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HexFormat;

/**
 * Lista de revocacion para JWT stateless (ver revoked_tokens en
 * database/migrations/V2__auth_users.sql). Nunca se guarda el token en si, solo
 * su hash SHA-256, para que una fuga de la tabla no equivalga a robar tokens.
 */
@Service
public class TokenRevocationService {

    private final RevokedTokenRepository repository;
    private final JwtService jwtService;

    public TokenRevocationService(RevokedTokenRepository repository, JwtService jwtService) {
        this.repository = repository;
        this.jwtService = jwtService;
    }

    public void revoke(String rawToken) {
        String hash = sha256(rawToken);
        if (repository.existsById(hash)) {
            return;
        }
        OffsetDateTime expiresAt = jwtService.extractExpiration(rawToken)
                .toInstant()
                .atOffset(ZoneOffset.UTC);
        repository.save(new RevokedToken(hash, expiresAt));
    }

    public boolean isRevoked(String rawToken) {
        return repository.existsById(sha256(rawToken));
    }

    private static String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 siempre esta disponible en la JVM estandar.
            throw new IllegalStateException(e);
        }
    }
}
