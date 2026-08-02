package com.dcplatform.api.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtServiceImpl implements JwtService {

	@Value("${app.security.jwt-secret}")
	private String secret;

	@Value("${app.security.magic-link-ttl-minutes}")
	private int magicLinkTtlMinutes;

	@Override
	public String generateMagicLinkToken(String email) {
		long ttlMillis = magicLinkTtlMinutes * 60L * 1000L;
		return buildToken(email, "MAGIC_LINK", null, ttlMillis); // entrada por magic link no requiere rol
	}

	@Override
	public String generateAccessToken(String email, String role) {
		long ttlMillis = 24 * 60L * 60L * 1000L; // token de 24 hs
		return buildToken(email, "ACCESS", role, ttlMillis);
	}

	@Override
	public String extractEmail(String token) {
		return extractAllClaims(token).getSubject();
	}

	public String extractRole(String token) {
		return extractAllClaims(token).get("role", String.class);
	}

	@Override
	public boolean isMagicLinkToken(String token) {
		return "MAGIC_LINK".equals(extractAllClaims(token).get("type"));
	}

	@Override
	public boolean isTokenValid(String token) {
		return extractAllClaims(token).getExpiration().after(new Date());
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parser()
				.verifyWith(getSigningKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}

	private SecretKey getSigningKey() {
		return Keys.hmacShaKeyFor(secret.getBytes());
	}

	private String buildToken(String email, String type, String role, long expirationMillis) {
		long currentTime = System.currentTimeMillis();

		var jwtBuilder = Jwts.builder()
				.subject(email)
				.claim("type", type)
				.issuedAt(new Date(currentTime))
				.expiration(new Date(currentTime + expirationMillis))
				.signWith(getSigningKey());

		if (role != null && !role.isBlank()) {
			jwtBuilder.claim("role", role);
		}

		return jwtBuilder.compact();
	}
}
