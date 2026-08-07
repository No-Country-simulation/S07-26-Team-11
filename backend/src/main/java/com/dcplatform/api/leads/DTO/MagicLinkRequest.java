package com.dcplatform.api.leads.DTO;

import com.dcplatform.api.leads.Source;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record MagicLinkRequest(
                @NotBlank(message = "El email es obligatorio") @Email(message = "El formato del email no es válido") String email,

                String companyName,

                String role,

                @NotNull(message = "El origen (source) es obligatorio") Source source,

                UUID estimateId,

                @NotNull(message = "El consentimiento es obligatorio") @AssertTrue(message = "Debes aceptar los términos y condiciones para continuar") String consent,

                @NotBlank(message = "La versión de la política de privacidad es obligatoria") String privacyPolicyVersion) {
}
