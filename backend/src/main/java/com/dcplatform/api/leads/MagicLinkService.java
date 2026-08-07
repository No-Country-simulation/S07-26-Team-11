package com.dcplatform.api.leads;

import java.time.LocalDateTime;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.dcplatform.api.leads.DTO.LeadResponse;
import com.dcplatform.api.leads.DTO.MagicLinkRequest;
import com.dcplatform.api.leads.DTO.MagicLinkResponse;
import com.dcplatform.api.leads.DTO.TokenResponse;
import com.dcplatform.api.security.jwt.JwtService;

@Service
public class MagicLinkService {

    private final MagicLinkNotifer magicLinkNotifier;
    private final JwtService jwtService;
    private final TokenHasher tokenHasher;
    private final LeandAccessTokenRepository leandAccessTokenRepository;

	public MagicLinkService(MagicLinkNotifer magicLinkNotifier, JwtService jwtService, TokenHasher tokenHasher,
	                        LeandAccessTokenRepository leandAccessTokenRepository) {
		this.magicLinkNotifier = magicLinkNotifier;
		this.jwtService = jwtService;
		this.tokenHasher = tokenHasher;
		this.leandAccessTokenRepository = leandAccessTokenRepository;
	}

	public MagicLinkResponse generateMagicLink(MagicLinkRequest magicLinkRequest) {

        var magick_link = jwtService.generateMagicLinkToken(magicLinkRequest.email());

        

        magicLinkNotifier.sendNotificacion(magicLinkRequest.email(), magick_link);

        return new MagicLinkResponse("Si el correo es válido, recibirás un enlace de acceso en unos segundos.");
    }

    public TokenResponse verifyAndExchange(String rawToken) {

        if (!jwtService.isTokenValid(rawToken) || !jwtService.isMagicLinkToken(rawToken)) {
            // throw new badRequest("El token de acceso es inválido o ha expirado");
        }

        String email = jwtService.extractEmail(rawToken);

        String hash = tokenHasher.hash(rawToken);

        LeadAccessTokens accessToken = leandAccessTokenRepository.findByTokenHash(hash);

        if (accessToken.getUsedAt() != null) {
            // throw ApiException.badRequest("El enlace mágico ya fue utilizado");
        }

        accessToken.setUsedAt(LocalDateTime.now());
        leandAccessTokenRepository.save(accessToken);

        String newAccessToken = jwtService.generateAccessToken(email, "LEAD");

        Date expiration = jwtService.extractExpiration(newAccessToken);

        var lead = new LeadResponse(null, email, newAccessToken);

        return new TokenResponse(newAccessToken, expiration, lead);

    }

}
