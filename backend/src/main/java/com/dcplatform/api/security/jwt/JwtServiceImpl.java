package com.dcplatform.api.security.jwt;

import com.dcplatform.api.shared.ApiException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.regex.Pattern;

@Service
public class JwtServiceImpl implements JwtService {

	@Value("${app.security.jwt-secret}")
	private String secret;

	@Value("${app.security.magic-link-ttl-minutes}")
	private int magicLinkTtlMinutes;

	private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

	@PostConstruct
	public void validateSecret() {
		if (secret == null || secret.isBlank()) {
			throw new IllegalStateException("FATAL: El secreto JWT (app.security.jwt-secret) no puede estar vacío.");
		}
		if (secret.getBytes(StandardCharsets.UTF_8).length < 32) {
			throw new IllegalStateException("FATAL: El secreto JWT debe tener al menos 32 bytes (256 bits) " +
					"para garantizar firmas seguras HMAC-SHA.");
		}
	}

	@Override
	public String generateMagicLinkToken(String email) {
		validateEmail(email);
		long ttlMillis = magicLinkTtlMinutes * 60L * 1000L;
		return buildToken(email, TokenType.MAGIC_LINK, null, ttlMillis); // entrada por magic link no requiere rol
	}

	@Override
	public String generateAccessToken(String email, String role) {
		validateEmail(email);
		long ttlMillis = 24 * 60L * 60L * 1000L; // token de 24 hs
		return buildToken(email, TokenType.ACCESS, role, ttlMillis);
	}

	@Override
	public String extractEmail(String token) {
		return extractAllClaims(token).getSubject();
	}

	public String extractRole(String token) {
		return extractAllClaims(token).get("role", String.class);
	}

	@Override
	public Date extractExpiration(String token) {
		return extractAllClaims(token).getExpiration();
	}

	@Override
	public boolean isMagicLinkToken(String token) {
		return TokenType.MAGIC_LINK.name().equals(extractAllClaims(token).get("type"));
	}

	@Override
	public boolean isAccessToken(String token) {
		return TokenType.ACCESS.name().equals(extractAllClaims(token).get("type"));
	}

	@Override
public boolean isTokenValid(String token) {
    try {
        Claims claims = extractAllClaims(token);
        boolean isValid = claims.getExpiration().after(new Date());
        System.out.println("🔍 [DEBUG JWT] ¿Fecha de expiración válida?: " + isValid);
        return isValid;

    } catch (ExpiredJwtException e) {
        System.err.println("❌ [DEBUG JWT] Token EXPIRADO: " + e.getMessage());
        return false;
    } catch (SignatureException e) {
        System.err.println("❌ [DEBUG JWT] FIRMA INVÁLIDA (Revisá JWT_SECRET): " + e.getMessage());
        return false;
    } catch (JwtException | IllegalArgumentException e) {
        System.err.println("❌ [DEBUG JWT] Error de firma o estructura JWT: " + e.getMessage());
        return false;
    } catch (Exception e) {
        System.err.println("❌ [DEBUG JWT] Error inesperado al parsear JWT: " + e.getMessage());
        return false;
    }
}
	

	private Claims extractAllClaims(String token) {
		return Jwts.parser()
				.verifyWith(getSigningKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}

	private SecretKey getSigningKey() {
		return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
	}

	private void validateEmail(String email) {
		if (email == null || email.isBlank() || !EMAIL_PATTERN.matcher(email).matches()) {
			throw new ApiException(
					HttpStatus.BAD_REQUEST,
					"validation-error",
					"El formato del email es inválido");
		}
	}

	private String buildToken(String email, TokenType type, String role, long expirationMillis) {
		long currentTime = System.currentTimeMillis();

		var jwtBuilder = Jwts.builder()
				.subject(email)
				.claim("type", type.name())
				.issuedAt(new Date(currentTime))
				.expiration(new Date(currentTime + expirationMillis))
				.signWith(getSigningKey());

		if (role != null && !role.isBlank()) {
			jwtBuilder.claim("role", role);
		}

		return jwtBuilder.compact();
	}
}
