package com.dcplatform.api.security.jwt;

import com.dcplatform.api.shared.ApiException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

class JwtServiceImplTest {

	private JwtServiceImpl jwtService;

	public static final List<String> MALFORMED_EMAILS = List.of(
			"",
			" ",
			"abc@email.x",
			"abc@ abc",
			"abc@abc",
			"abc",
			"@",
			"abc @ abc . abc",
			"abc@abc-abc",
			"juan@",
			"correo-sin-dominio",
			"\"esto es un correo\"@ejemplo.com",
			"admin@[192.168.0.1]"
	);

	static Stream<String> provideMalformedEmails() {
		return MALFORMED_EMAILS.stream();
	}

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
	void extractEmailAndRole_shouldReturnValuesFromValidAccessToken() {
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
	void validateSecret_shouldRejectBlankShortOrNullSecret() {
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

		// null
		ReflectionTestUtils.setField(jwtService, "secret", null);

		assertThatThrownBy(jwtService::validateSecret)
				.isInstanceOf(IllegalStateException.class)
				.hasMessageContaining("secreto JWT");
	}


	@Test
	void validateSecret_shouldNotThrowWhenSecretIsValid() {
		assertThatNoException().isThrownBy(() -> jwtService.validateSecret());
	}

	@Test
	void validateSecret_shouldAcceptExactly32BytesAndReject31Bytes() {
		ReflectionTestUtils.setField(jwtService, "secret", "a".repeat(32));
		assertThatNoException().isThrownBy(() -> jwtService.validateSecret());

		ReflectionTestUtils.setField(jwtService, "secret", "a".repeat(31));
		assertThatThrownBy(jwtService::validateSecret)
				.isInstanceOf(IllegalStateException.class)
				.hasMessageContaining("32 bytes");
	}

	@Test
	void generateAccessToken_shouldRejectNullEmptyBlankOrMalformedEmail() {
		// null email
		assertThatThrownBy(() -> jwtService.generateAccessToken(null, "ADMIN"))
				.isInstanceOf(ApiException.class)
				.hasMessageContaining("email");

		// empty email
		assertThatThrownBy(() -> jwtService.generateAccessToken("", "ADMIN"))
				.isInstanceOf(ApiException.class)
				.hasMessageContaining("email");

		// blank email
		assertThatThrownBy(() -> jwtService.generateAccessToken("   ", "ADMIN"))
				.isInstanceOf(ApiException.class)
				.hasMessageContaining("email");
	}

	@Test
	void generateMagicLinkToken_shouldRejectNullEmptyOrBlankEmail() {
		// null email
		assertThatThrownBy(() -> jwtService.generateMagicLinkToken(null))
				.isInstanceOf(ApiException.class)
				.hasMessageContaining("email");

		// empty email
		assertThatThrownBy(() -> jwtService.generateMagicLinkToken(""))
				.isInstanceOf(ApiException.class)
				.hasMessageContaining("email");

		// blank email
		assertThatThrownBy(() -> jwtService.generateMagicLinkToken("   "))
				.isInstanceOf(ApiException.class)
				.hasMessageContaining("email");
	}

	@ParameterizedTest
	@MethodSource("provideMalformedEmails")
	void generateAccessToken_shouldRejectMalformedEmail(String invalidEmail) {
		assertThatThrownBy(() -> jwtService.generateMagicLinkToken(invalidEmail))
				.isInstanceOf(ApiException.class)
				.hasMessageContaining("El formato del email es inválido");
	}

	@ParameterizedTest
	@MethodSource("provideMalformedEmails")
	void generateMagicLinkToken_shouldRejectMalformedEmail(String invalidEmail) {
		assertThatThrownBy(() -> jwtService.generateMagicLinkToken(invalidEmail))
				.isInstanceOf(ApiException.class)
				.hasMessageContaining("El formato del email es inválido");
	}

	@Test
	void generateAccessToken_shouldNotIncludeRoleClaimWhenRoleIsNullBlankOrEmpty() {
		String tokenWithNullRole = jwtService.generateAccessToken("user@example.com", null);
		String tokenWithBlankRole = jwtService.generateAccessToken("user@example.com", "   ");
		String tokenWithEmptyRole = jwtService.generateAccessToken("user@example.com", "");

		assertThat(extractClaims(tokenWithNullRole).get("role")).isNull();
		assertThat(extractClaims(tokenWithBlankRole).get("role")).isNull();
		assertThat(extractClaims(tokenWithEmptyRole).get("role")).isNull();
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
	void extractionMethods_shouldThrowExpiredJwtExceptionOnExpiredMagicLinkToken() {
		ReflectionTestUtils.setField(jwtService, "magicLinkTtlMinutes", 0);
		String expiredToken = jwtService.generateMagicLinkToken("expired@example.com");

		assertThatThrownBy(() -> jwtService.extractEmail(expiredToken))
				.isInstanceOf(io.jsonwebtoken.ExpiredJwtException.class);
	}

	@Test
	void extractionMethods_shouldThrowExpiredJwtExceptionOnExpiredAccessToken() {
		String expiredAccessToken = Jwts.builder()
				.subject("user@example.com")
				.claim("type", TokenType.ACCESS.name())
				.expiration(new Date(System.currentTimeMillis() - 1000)) // Expiró hace 1 segundo
				.signWith(Keys.hmacShaKeyFor("0123456789abcdef0123456789abcdef".getBytes(StandardCharsets.UTF_8)))
				.compact();

		assertThatThrownBy(() -> jwtService.extractEmail(expiredAccessToken))
				.isInstanceOf(io.jsonwebtoken.ExpiredJwtException.class);

		assertThatThrownBy(() -> jwtService.extractRole(expiredAccessToken))
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
	void extractEmail_shouldThrowJwtExceptionWhenSignatureIsInvalid() {
		String attackerSecret = "9999999999abcdef0123456789abcdef";
		SecretKey attackerKey = Keys.hmacShaKeyFor(attackerSecret.getBytes(StandardCharsets.UTF_8));

		String tamperedToken = Jwts.builder()
				.subject("hacked@example.com")
				.claim("type", TokenType.ACCESS.name())
				.expiration(new Date(System.currentTimeMillis() + 100000))
				.signWith(attackerKey)
				.compact();

		assertThatThrownBy(() -> jwtService.extractEmail(tamperedToken))
				.isInstanceOf(io.jsonwebtoken.JwtException.class);
	}

	@Test
	void isAccessTokenAndIsMagicLinkToken_shouldReturnFalseWhenTypeClaimIsMissingOrUnexpected() {
		String tokenWithoutType = Jwts.builder()
				.subject("user@example.com")
				.expiration(new Date(System.currentTimeMillis() + 100000))
				.signWith(Keys.hmacShaKeyFor("0123456789abcdef0123456789abcdef".getBytes(StandardCharsets.UTF_8)))
				.compact();

		String tokenWithUnexpectedType = Jwts.builder()
				.subject("user@example.com")
				.claim("type", "OTHER")
				.expiration(new Date(System.currentTimeMillis() + 100000))
				.signWith(Keys.hmacShaKeyFor("0123456789abcdef0123456789abcdef".getBytes(StandardCharsets.UTF_8)))
				.compact();

		assertThat(jwtService.isAccessToken(tokenWithoutType)).isFalse();
		assertThat(jwtService.isAccessToken(tokenWithUnexpectedType)).isFalse();
		assertThat(jwtService.isMagicLinkToken(tokenWithoutType)).isFalse();
		assertThat(jwtService.isMagicLinkToken(tokenWithUnexpectedType)).isFalse();
	}

	@Test
	void isTokenValid_shouldReturnFalseWhenAccessTokenExpired() {
		String expiredAccessToken = Jwts.builder()
				.subject("user@example.com")
				.claim("type", TokenType.ACCESS.name())
				.expiration(new Date(System.currentTimeMillis() - 1000))
				.signWith(Keys.hmacShaKeyFor("0123456789abcdef0123456789abcdef".getBytes(StandardCharsets.UTF_8)))
				.compact();

		assertThat(jwtService.isTokenValid(expiredAccessToken)).isFalse();
	}

	@Test
	void booleanExtractionMethods_shouldThrowExceptionsOnInvalidTokens() {
		String malformedToken = "invalid.token.here";

		assertThatThrownBy(() -> jwtService.isAccessToken(malformedToken))
				.isInstanceOf(io.jsonwebtoken.JwtException.class);

		assertThatThrownBy(() -> jwtService.isMagicLinkToken(null))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void extractExpiration_shouldReturnFutureDateForValidAccessToken() {
		String token = jwtService.generateAccessToken("admin@example.com", "ADMIN");

		Date expirationDate = jwtService.extractExpiration(token);

		assertThat(expirationDate).isNotNull();
		assertThat(expirationDate).isAfter(new Date());
	}

	@Test
	void extractExpiration_shouldReturnFutureDateForValidMagicLinkToken() {
		String token = jwtService.generateMagicLinkToken("lead@example.com");

		Date expirationDate = jwtService.extractExpiration(token);

		assertThat(expirationDate).isNotNull();
		assertThat(expirationDate).isAfter(new Date());
	}

	@Test
	void extractExpiration_shouldThrowIllegalArgumentExceptionOnNullToken() {
		assertThatThrownBy(() -> jwtService.extractExpiration(null))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void extractExpiration_shouldThrowJwtExceptionOnMalformedToken() {
		String malformedToken = "not.a.valid.jwt";

		assertThatThrownBy(() -> jwtService.extractExpiration(malformedToken))
				.isInstanceOf(io.jsonwebtoken.JwtException.class);
	}

	@Test
	void extractExpiration_shouldThrowExpiredJwtExceptionOnExpiredToken() {
		// 1. Configuramos el tiempo de vida en 0 para forzar la expiración inmediata
		ReflectionTestUtils.setField(jwtService, "magicLinkTtlMinutes", 0);
		String expiredToken = jwtService.generateMagicLinkToken("expired@example.com");

		// 2. Al intentar extraer los claims (y por ende la expiración), la librería debe fallar
		assertThatThrownBy(() -> jwtService.extractExpiration(expiredToken))
				.isInstanceOf(io.jsonwebtoken.ExpiredJwtException.class);
	}

	@Test
	void extractExpiration_shouldThrowJwtExceptionWhenSignatureIsInvalid() {
		// 1. Simulamos un token generado por un atacante con un secreto diferente
		String attackerSecret = "9999999999abcdef0123456789abcdef";
		SecretKey attackerKey = Keys.hmacShaKeyFor(attackerSecret.getBytes(StandardCharsets.UTF_8));

		String tamperedToken = Jwts.builder()
				.subject("hacked@example.com")
				.expiration(new Date(System.currentTimeMillis() + 100000))
				.signWith(attackerKey)
				.compact();

		// 2. Intentamos extraer la fecha, lo que desencadenará una falla de firma criptográfica
		assertThatThrownBy(() -> jwtService.extractExpiration(tamperedToken))
				.isInstanceOf(io.jsonwebtoken.JwtException.class);
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
