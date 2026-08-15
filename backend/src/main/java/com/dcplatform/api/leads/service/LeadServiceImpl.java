package com.dcplatform.api.leads.service;

import com.dcplatform.api.leads.repository.LeadRepository;
import com.dcplatform.api.leads.model.LeadEntity;
import com.dcplatform.api.leads.model.dto.LeadAuthenticationResponse;
import com.dcplatform.api.leads.model.dto.LeadMagicLinkTokenRequest;
import com.dcplatform.api.leads.model.dto.LeadRegistrationRequest;
import com.dcplatform.api.leads.model.dto.LeadRegistrationResponse;
import com.dcplatform.api.magiclink.service.MagicLinkService;
import com.dcplatform.api.security.jwt.JwtService;
import com.dcplatform.api.shared.ApiException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;

@Service
public class LeadServiceImpl implements LeadService {

	private final LeadRepository leadRepository;
	private final MagicLinkService magicLinkService;
	private final JwtService jwtService;

	public LeadServiceImpl(LeadRepository leadRepository, MagicLinkService magicLinkService, JwtService jwtService) {
		this.leadRepository = leadRepository;
		this.magicLinkService = magicLinkService;
		this.jwtService = jwtService;
	}

	@Override
	public LeadRegistrationResponse createLead(String clientIp, LeadRegistrationRequest request) {
		LeadEntity lead = new LeadEntity();
		lead.setCompanyName(request.companyName());
		lead.setSource(request.source());
		lead.setConsentAt(LocalDateTime.now());
		lead.setConsentIp(clientIp);
		lead.setPrivacyPolicyVersion(request.privacyPolicyVersion());
		lead.setCreatedAt(LocalDateTime.now());

		lead = leadRepository.save(lead);

		String message = magicLinkService.generateMagicLinkAndInform(lead.getEmail(), lead.getId(), lead.getConsentIp());
		return new LeadRegistrationResponse(message);
	}

	@Override
	public LeadAuthenticationResponse emailTokenVerification(LeadMagicLinkTokenRequest request) {
		String accessToken = magicLinkService.verifyAndExchange(request.tokenFromMagicLink());
		String email = jwtService.extractEmail(accessToken);

		LeadEntity lead = leadRepository.findByEmail(email)
				.orElseThrow(() -> ApiException.badRequest("Error al intentar verificar el token."));

		Date tokenExpiration = jwtService.extractExpiration(accessToken);

		return new LeadAuthenticationResponse(
				accessToken,
				tokenExpiration,
				new LeadAuthenticationResponse.LeadRegistrationResponse(
						lead.getId(),
						lead.getEmail(),
						lead.getCompanyName()
				)
		);
	}

	@Override
	public LeadEntity getLeadEntityByEmail(String email) {
		if (email == null || email.trim().isEmpty() || email.isBlank()) {
			throw ApiException.badRequest("Campo email no proporcionado.");
		}

		return leadRepository.findByEmail(email)
				.orElseThrow(() -> ApiException.notFound("Recurso no encontrado: entidad Lead."));
	}
}
