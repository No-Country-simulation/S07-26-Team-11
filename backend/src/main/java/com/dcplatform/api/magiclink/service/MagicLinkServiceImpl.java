package com.dcplatform.api.magiclink.service;

import com.dcplatform.api.magiclink.model.MagicLinkAccessToken;
import com.dcplatform.api.magiclink.repository.MagicLinkAccessTokenRepository;
import com.dcplatform.api.magiclink.utils.TokenHasher;
import com.dcplatform.api.security.jwt.JwtService;
import com.dcplatform.api.shared.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class MagicLinkServiceImpl implements MagicLinkService {

	private final MagicLinkNotifier magicLinkNotifier;
	private final JwtService jwtService;
	private final TokenHasher tokenHasher;
	private final MagicLinkAccessTokenRepository magicLinkRepository;
	private final RateLimiterService rateLimiterService;

	@Value("${app.cors.frontend-url}")
	private String frontendUrl;

	public MagicLinkServiceImpl(MagicLinkNotifier magicLinkNotifier,
	                            JwtService jwtService,
	                            TokenHasher tokenHasher,
	                            MagicLinkAccessTokenRepository magicLinkRepository,
	                            RateLimiterService rateLimiterService) {
		this.magicLinkNotifier = magicLinkNotifier;
		this.jwtService = jwtService;
		this.tokenHasher = tokenHasher;
		this.magicLinkRepository = magicLinkRepository;
		this.rateLimiterService = rateLimiterService;
	}

	@Override
	public String generateMagicLinkAndInform(String email, UUID subjectId, String clientIp) {
		validateInputs(email, subjectId);

		if (!rateLimiterService.tryConsumeIp(clientIp)) {
			throw ApiException.ratLimited("Has superado el límite de solicitudes por IP. Intenta más tarde.");
		}

		if (!rateLimiterService.tryConsumeEmail(email)) {
			throw ApiException.ratLimited("Has superado el límite de solicitudes por este correo. Intenta más tarde.");
		}

		String rawToken = jwtService.generateMagicLinkToken(email);
		String tokenHash = tokenHasher.hash(rawToken);

		MagicLinkAccessToken accessToken = new MagicLinkAccessToken();
		accessToken.setTokenHash(tokenHash);
		accessToken.setSubjectId(subjectId);
		accessToken.setCreatedAt(LocalDateTime.now());
		accessToken.setExpiresAt(LocalDateTime.now().plusMinutes(15));
		magicLinkRepository.save(accessToken);

		String magicLinkUrl = UriComponentsBuilder.fromUriString(frontendUrl)
				.path("/auth/verify")
				.queryParam("token", rawToken)
				.toUriString();

		magicLinkNotifier.sendNotificacion(email, magicLinkUrl);

		return "Si el correo es válido, recibirás un enlace de acceso en unos segundos.";
	}

	@Override
	public String verifyAndExchange(String tokenFromMagicLink) {
		if (tokenFromMagicLink == null || tokenFromMagicLink.isBlank() || tokenFromMagicLink.trim().isEmpty()) {
			throw ApiException.badRequest("Debe proporcionarse el token obtenido desde el correo");
		}

		if (!jwtService.isTokenValid(tokenFromMagicLink)) {
			throw ApiException.badRequest("El token de acceso es inválido o ha expirado");
		}

		if (!jwtService.isMagicLinkToken(tokenFromMagicLink)) {
			throw ApiException.badRequest("El token proporcionado no es un token de acceso de tipo Magic Link");
		}

		String emailJwt = jwtService.extractEmail(tokenFromMagicLink);
		String hash = tokenHasher.hash(tokenFromMagicLink);

		MagicLinkAccessToken accessToken = magicLinkRepository.findByTokenHash(hash)
				.orElseThrow(() -> ApiException.badRequest("El token de acceso no se encuentra registrado"));

		if (accessToken.getUsedAt() != null) {
			throw ApiException.badRequest("El token de acceso ya ha sido utilizado");
		}

		accessToken.setUsedAt(LocalDateTime.now());
		magicLinkRepository.save(accessToken);

		return jwtService.generateAccessToken(emailJwt, "LEAD");
	}

	private void validateInputs(String email, UUID subjectId) {
		if (email == null || email.isBlank() || email.trim().isEmpty()) {
			throw ApiException.badRequest("Proporcione un email válido para recibir el enlace de acceso.");
		}

		if (subjectId == null) {
			throw ApiException.badRequest("Campo SubjectID faltante en la solicitud.");
		}
	}
}
