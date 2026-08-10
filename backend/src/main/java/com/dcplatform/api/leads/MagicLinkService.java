package com.dcplatform.api.leads;

import com.dcplatform.api.ApiApplication;
import java.time.LocalDateTime;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.dcplatform.api.leads.DTO.LeadResponse;
import com.dcplatform.api.leads.DTO.MagicLinkRequest;
import com.dcplatform.api.leads.DTO.MagicLinkResponse;
import com.dcplatform.api.leads.DTO.TokenResponse;
import com.dcplatform.api.security.jwt.JwtService;
import com.dcplatform.api.shared.ApiException;

@Service
public class MagicLinkService {

    private final ApiApplication apiApplication;
    private final MagicLinkNotifer magicLinkNotifier;
    private final JwtService jwtService;
    private final TokenHasher tokenHasher;
    private final LeandAccessTokenRepository leandAccessTokenRepository;
    private final LeadRepository leadRepository;

    public MagicLinkService(MagicLinkNotifer magicLinkNotifier, JwtService jwtService, TokenHasher tokenHasher,
            LeandAccessTokenRepository leandAccessTokenRepository, LeadRepository leadRepository,
            ApiApplication apiApplication) {
        this.magicLinkNotifier = magicLinkNotifier;
        this.jwtService = jwtService;
        this.tokenHasher = tokenHasher;
        this.leandAccessTokenRepository = leandAccessTokenRepository;
        this.leadRepository = leadRepository;
        this.apiApplication = apiApplication;
    }

    public MagicLinkResponse generateMagicLink(MagicLinkRequest magicLinkRequest) {

        var leadDb = leadRepository.findByEmail(magicLinkRequest.email());
        Lead lead = leadDb;
        if (lead == null) {
            lead = new Lead(
                magicLinkRequest.email(),
                magicLinkRequest.companyName(),
                magicLinkRequest.role(),
                magicLinkRequest.source(),
                LocalDateTime.now(),
                magicLinkRequest.consent(),
                magicLinkRequest.privacyPolicyVersion());

            leadRepository.save(lead);
        }

        String rawToken = jwtService.generateMagicLinkToken(magicLinkRequest.email());
        String tokenHash = tokenHasher.hash(rawToken);

        LeadAccessTokens accessToken = new LeadAccessTokens();
        accessToken.setTokenHash(tokenHash);
        accessToken.setLead(lead);
        accessToken.setCreatedAt(LocalDateTime.now());
        accessToken.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        leandAccessTokenRepository.save(accessToken);

        magicLinkNotifier.sendNotificacion(magicLinkRequest.email(), rawToken);

        return new MagicLinkResponse("Si el correo es válido, recibirás un enlace de acceso en unos segundos.");
    }

    public TokenResponse verifyAndExchange(String rawToken) {

        if (!jwtService.isTokenValid(rawToken) || !jwtService.isMagicLinkToken(rawToken)) {
            throw ApiException.badRequest("El token de acceso es inválido o ha expirado");
        }

        String email = jwtService.extractEmail(rawToken);
        String hash = tokenHasher.hash(rawToken);

        LeadAccessTokens accessToken = leandAccessTokenRepository.findByTokenHash(hash);

        if (accessToken == null) {
            ApiException.badRequest("El enlace de acceso no es válido o no existe");
        }

        if (accessToken.getUsedAt() != null) {
            throw ApiException.conflict("El enlace mágico ya fue utilizado");
        }

        accessToken.setUsedAt(LocalDateTime.now());
        leandAccessTokenRepository.save(accessToken);

        var leadBd = leadRepository.findByEmail(email);
        if (leadBd == null) {
            throw ApiException.notFound("Correo de usuario no encontrado");
        }

        String newAccessToken = jwtService.generateAccessToken(email, "LEAD");
        Date expiration = jwtService.extractExpiration(newAccessToken);

        var leadDtoResponse = new LeadResponse(leadBd.getId(), email, newAccessToken);

        return new TokenResponse(newAccessToken, expiration, leadDtoResponse);

    }

}
