package com.dcplatform.api.security.jwt;

import java.util.Date;

public interface JwtService {
	String generateMagicLinkToken(String email);

	String generateAccessToken(String email, String role);

	String extractEmail(String token);

	String extractRole(String token);

	Date extractExpiration(String token);

	boolean isMagicLinkToken(String token);

	boolean isAccessToken(String token);

	boolean isTokenValid(String token);
}
