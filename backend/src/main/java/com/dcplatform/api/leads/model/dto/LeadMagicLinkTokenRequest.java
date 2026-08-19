package com.dcplatform.api.leads.model.dto;

import jakarta.validation.constraints.NotBlank;

public record LeadMagicLinkTokenRequest(
		@NotBlank(message = "El token de verificación es obligatorio " +
				"para realizar el intercambio por un token de acceso.")
		String tokenFromMagicLink
) {
}
