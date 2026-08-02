package com.dcplatform.api.security.jwt;

public interface JwtService {
	String generateMagicLinkToken(String email);

	String generateAccessToken(String email, String role);

	String extractEmail(String token);

	String extractRole(String token);

	boolean isMagicLinkToken(String token);

	boolean isAccessToken(String token);

	boolean isTokenValid(String token);
}
