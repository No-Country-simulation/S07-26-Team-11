package com.dcplatform.api.leads.model.dto;

import java.util.Date;
import java.util.UUID;

public record LeadAuthenticationResponse(
		String accessToken,
		Date expiresAt,
		LeadRegistrationResponse lead
) {

	public record LeadRegistrationResponse(
			UUID id,
			String email,
			String companyName
	) {
	}
}
