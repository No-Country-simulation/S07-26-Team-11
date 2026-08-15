package com.dcplatform.api.leads.model.dto;

import jakarta.validation.constraints.NotBlank;

public record LeadMagicLinkTokenRequest(
		@NotBlank(message = "Campo tokenFromMagicLink no proporcionado.")
		String tokenFromMagicLink
) {
}
