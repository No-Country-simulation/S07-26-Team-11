package com.dcplatform.api.leads;

import com.dcplatform.api.ApiApplication;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import com.dcplatform.api.leads.DTO.LeadResponse;
import com.dcplatform.api.leads.DTO.MagicLinkRequest;
import com.dcplatform.api.leads.DTO.MagicLinkResponse;
import com.dcplatform.api.leads.DTO.TokenRequest;
import com.dcplatform.api.leads.DTO.TokenResponse;
import com.dcplatform.api.auth.UserAuthService;
import com.dcplatform.api.security.jwt.JwtService;
import com.dcplatform.api.shared.ApiException;

@Service
public class MagicLinkService {

    private final MagicLinkNotifer magicLinkNotifier;
    private final JwtService jwtService;
    private final UserAuthService userAuthService;
    private final TokenHasher tokenHasher;
    private final LeandAccessTokenRepository leandAccessTokenRepository;
    private final LeadRepository leadRepository;
    private final RateLimiterService rateLimiterService;

    @Value("${app.cors.frontend-url}")
    private String frontendUrl;

    public MagicLinkService(MagicLinkNotifer magicLinkNotifier, JwtService jwtService,
            UserAuthService userAuthService, TokenHasher tokenHasher,
            LeandAccessTokenRepository leandAccessTokenRepository, LeadRepository leadRepository,
            ApiApplication apiApplication) {
        this.magicLinkNotifier = magicLinkNotifier;
        this.jwtService = jwtService;
        this.userAuthService = userAuthService;
        this.tokenHasher = tokenHasher;
        this.leandAccessTokenRepository = leandAccessTokenRepository;
        this.leadRepository = leadRepository;
        this.rateLimiterService = new RateLimiterService();

    }

    public MagicLinkResponse generateMagicLink(MagicLinkRequest magicLinkRequest, String clientIp) {

        if (!rateLimiterService.tryConsumeIp(clientIp)) {
            throw ApiException.ratLimited("Has superado el límite de solicitudes por IP. Intenta más tarde.");
        }

        
        if (!rateLimiterService.tryConsumeEmail(magicLinkRequest.email())) {
            throw ApiException.ratLimited("Has superado el límite de solicitudes por este correo. Intenta más tarde.");
        }
    

    Lead lead = leadRepository.findByEmail(magicLinkRequest.email())
            .orElseGet(() -> leadRepository.save(new Lead(
                    magicLinkRequest.email(),
                    magicLinkRequest.companyName(),
                    magicLinkRequest.role(),
                    magicLinkRequest.source(),
                    LocalDateTime.now(),
                    clientIp,
                    magicLinkRequest.privacyPolicyVersion())));

    String rawToken = jwtService.generateMagicLinkToken(magicLinkRequest.email());
    String tokenHash = tokenHasher.hash(rawToken);

    LeadAccessTokens accessToken = new LeadAccessTokens();accessToken.setTokenHash(tokenHash);accessToken.setLead(lead);accessToken.setCreatedAt(LocalDateTime.now());accessToken.setExpiresAt(LocalDateTime.now().plusMinutes(15));leandAccessTokenRepository.save(accessToken);

    String magicLinkUrl = UriComponentsBuilder.fromUriString(frontendUrl)
            .path("/auth/verify")
            .queryParam("token", rawToken)
            .toUriString();

    magicLinkNotifier.sendNotificacion(magicLinkRequest.email(),magicLinkUrl);

    return new MagicLinkResponse("Si el correo es válido, recibirás un enlace de acceso en unos segundos.");
    }

    public TokenResponse verifyAndExchange(TokenRequest tokenRequest) {

        String token = tokenRequest.token();

        if (!jwtService.isTokenValid(token)) {
            throw ApiException.badRequest("El token de acceso es inválido o ha expirado");
        }

        if (!jwtService.isMagicLinkToken(token)) {
            throw ApiException.badRequest("El token proporcionado no es un token de acceso de tipo Magic Link");
        }

        String emailJwt = jwtService.extractEmail(token);
        String hash = tokenHasher.hash(token);

        Lead leadBd = leadRepository.findByEmail(emailJwt)
                .orElseThrow(() -> ApiException.notFound("El lead especificado no existe"));

        LeadAccessTokens accessToken = leandAccessTokenRepository.findByTokenHash(hash)
                .orElseThrow(() -> ApiException.badRequest("El token de acceso no se encuentra registrado"));

        if (accessToken.getUsedAt() != null) {
            throw ApiException.badRequest("El token de acceso ya ha sido utilizado");
        }

        accessToken.setUsedAt(LocalDateTime.now());
        leandAccessTokenRepository.save(accessToken);

        // El token se emite contra la cuenta de `users`, no contra la fila de `lead`: es la
        // identidad que el resto de la API sabe resolver (ver UserAuthService). `leads` sigue
        // siendo el registro del funnel; quien opera en la API es la cuenta.
        String newAccessToken = userAuthService.issueAccessTokenForVerifiedEmail(leadBd.getEmail());
        /// # String newAccessToken = jwtService.generateAccessToken(leadBd.getEmail(), "LEAD");
        
        Date expiration = jwtService.extractExpiration(newAccessToken);

        var leadDtoResponse = new LeadResponse(leadBd.getId(), leadBd.getEmail(), leadBd.getCompanyName());

        return new TokenResponse(newAccessToken, expiration, leadDtoResponse);

    }

}
