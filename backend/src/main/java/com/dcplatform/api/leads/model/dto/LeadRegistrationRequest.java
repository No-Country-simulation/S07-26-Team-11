package com.dcplatform.api.leads.model.dto;

import com.dcplatform.api.leads.Source;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record LeadRegistrationRequest(
		@NotBlank(message = "El email es obligatorio")
		@Email(message = "El formato del email no es válido")
		String email,

		@NotBlank(message = "El nombre de la empresa es obligatorio")
		String companyName,

		@NotBlank(message = "Debe especificar el rol que cumple en su empresa")
		String role,

		@NotNull(message = "El origen (source) es obligatorio")
		Source source,

		UUID estimateId,

		@NotNull(message = "El consentimiento es obligatorio")
		@AssertTrue(message = "Debes aceptar los términos para continuar")
		Boolean consent,

		@NotBlank(message = "La versión de la política de privacidad es obligatoria")
		String privacyPolicyVersion
) {
}
