package com.dcplatform.api.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Objects;

import static org.assertj.core.api.Assertions.*;

class JwtServiceImplTest {

	private JwtServiceImpl jwtService;

	@BeforeEach
	void setUp() {
		jwtService = new JwtServiceImpl();
		ReflectionTestUtils.setField(jwtService, "secret", "0123456789abcdef0123456789abcdef");
		ReflectionTestUtils.setField(jwtService, "magicLinkTtlMinutes", 15);
	}

	@Test
	void generateAccessToken_shouldIncludeEmailRoleAndAccessType() {
		String token = jwtService.generateAccessToken("user@example.com", "ADMIN");

		Claims claims = extractClaims(token);

		assertThat(claims.getSubject()).isEqualTo("user@example.com");
		assertThat(claims.get("role", String.class)).isEqualTo("ADMIN");
		assertThat(claims.get("type", String.class)).isEqualTo(TokenType.ACCESS.name());
		assertThat(jwtService.isAccessToken(token)).isTrue();
		assertThat(jwtService.isMagicLinkToken(token)).isFalse();
		assertThat(jwtService.isTokenValid(token)).isTrue();
	}

	@Test
	void generateMagicLinkToken_shouldIncludeEmailAndMagicLinkType() {
		String token = jwtService.generateMagicLinkToken("guest@example.com");

		Claims claims = extractClaims(token);

		assertThat(claims.getSubject()).isEqualTo("guest@example.com");
		assertThat(claims.get("type", String.class)).isEqualTo(TokenType.MAGIC_LINK.name());
		assertThat(jwtService.isMagicLinkToken(token)).isTrue();
		assertThat(jwtService.isAccessToken(token)).isFalse();
		assertThat(jwtService.extractEmail(token)).isEqualTo("guest@example.com");
		assertThat(jwtService.extractRole(token)).isNull();
	}

	@Test
	void extractEmailAndRole_shouldReturnValuesFromValidToken() {
		String token = jwtService.generateAccessToken("analyst@example.com", "VIEWER");

		assertThat(jwtService.extractEmail(token)).isEqualTo("analyst@example.com");
		assertThat(jwtService.extractRole(token)).isEqualTo("VIEWER");
	}

	@Test
	void isTokenValid_shouldReturnFalseWhenTokenExpiredForMagicLinkToken() {
		ReflectionTestUtils.setField(jwtService, "magicLinkTtlMinutes", 0);

		String token = jwtService.generateMagicLinkToken("expired@example.com");

		assertThat(jwtService.isTokenValid(token)).isFalse();
	}

	@Test
	void validateSecret_shouldRejectBlankOrShortSecret() {
		// blank
		ReflectionTestUtils.setField(jwtService, "secret", " ");

		assertThatThrownBy(jwtService::validateSecret)
				.isInstanceOf(IllegalStateException.class)
				.hasMessageContaining("vacío");

		// short
		ReflectionTestUtils.setField(jwtService, "secret", "short");

		assertThatThrownBy(jwtService::validateSecret)
				.isInstanceOf(IllegalStateException.class)
				.hasMessageContaining("secreto JWT");
	}

	@Test
	void validateSecret_shouldRejectNullSecret() {
		ReflectionTestUtils.setField(jwtService, "secret", null);

		assertThatThrownBy(jwtService::validateSecret)
				.isInstanceOf(IllegalStateException.class)
				.hasMessageContaining("vacío");
	}

	@Test
	void validateSecret_shouldNotThrowWhenSecretIsValid() {
		assertThatNoException().isThrownBy(() -> jwtService.validateSecret());
	}

	@Test
	void generateAccessToken_shouldNotIncludeRoleClaimWhenRoleIsBlank() {
		String tokenWithNullRole = jwtService.generateAccessToken("user@example.com", null);
		String tokenWithBlankRole = jwtService.generateAccessToken("user@example.com", "   ");

		assertThat(extractClaims(tokenWithNullRole).get("role")).isNull();
		assertThat(extractClaims(tokenWithBlankRole).get("role")).isNull();
	}

	@Test
	void isTokenValid_shouldReturnFalseForMalformedOrNullToken() {
		assertThat(jwtService.isTokenValid("not.a.valid.token")).isFalse();

		assertThat(jwtService.isTokenValid(null)).isFalse();
	}

	@Test
	void extractionMethods_shouldThrowExceptionsOnMalformedOrNullTokens() {
		String malformedToken = "invalid.token.here";

		// Para tokens malformados, jjwt lanza una subclase de JwtException (MalformedJwtException)
		assertThatThrownBy(() -> jwtService.extractEmail(malformedToken))
				.isInstanceOf(io.jsonwebtoken.JwtException.class);

		// Para parámetros nulos, jjwt lanza directamente IllegalArgumentException
		assertThatThrownBy(() -> jwtService.extractRole(null))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void extractionMethods_shouldThrowExpiredJwtExceptionOnExpiredToken() {
		ReflectionTestUtils.setField(jwtService, "magicLinkTtlMinutes", 0);
		String expiredToken = jwtService.generateMagicLinkToken("expired@example.com");

		assertThatThrownBy(() -> jwtService.extractEmail(expiredToken))
				.isInstanceOf(io.jsonwebtoken.ExpiredJwtException.class);
	}

	@Test
	void isTokenValid_shouldReturnFalseWhenSignatureIsInvalid() {
		String attackerSecret = "9999999999abcdef0123456789abcdef";
		SecretKey attackerKey = Keys.hmacShaKeyFor(attackerSecret.getBytes(StandardCharsets.UTF_8));

		String tamperedToken = Jwts.builder()
				.subject("hacked@example.com")
				.claim("type", TokenType.ACCESS.name())
				.expiration(new Date(System.currentTimeMillis() + 100000))
				.signWith(attackerKey)
				.compact();

		assertThat(jwtService.isTokenValid(tamperedToken)).isFalse();
	}

	@Test
	void booleanExtractionMethods_shouldThrowExceptionsOnInvalidTokens() {
		String malformedToken = "invalid.token.here";

		assertThatThrownBy(() -> jwtService.isAccessToken(malformedToken))
				.isInstanceOf(io.jsonwebtoken.JwtException.class);

		assertThatThrownBy(() -> jwtService.isMagicLinkToken(null))
				.isInstanceOf(IllegalArgumentException.class);
	}

	private Claims extractClaims(String token) {
		SecretKey signingKey = Keys.hmacShaKeyFor(
				((String) Objects.requireNonNull(ReflectionTestUtils.getField(jwtService, "secret")))
						.getBytes(StandardCharsets.UTF_8)
		);

		return Jwts.parser()
				.verifyWith(signingKey)
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}
}
