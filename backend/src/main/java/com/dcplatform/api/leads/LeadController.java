package com.dcplatform.api.leads;

import com.dcplatform.api.leads.model.dto.LeadAuthenticationResponse;
import com.dcplatform.api.leads.model.dto.LeadMagicLinkTokenRequest;
import com.dcplatform.api.leads.model.dto.LeadRegistrationRequest;
import com.dcplatform.api.leads.model.dto.LeadRegistrationResponse;
import com.dcplatform.api.leads.service.LeadService;
import com.dcplatform.api.leads.utils.GetClientIp;
import com.dcplatform.api.shared.annotations.ApiJsonExample;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/public/leads")
@Tag(name = "Leads", description = "Captura leads y verifica tokens de Magic Link para entregar un token de acceso.")
public class LeadController {

	private final LeadService leadService;
	private final GetClientIp getClientIp = new GetClientIp();

	public LeadController(LeadService leadService) {
		this.leadService = leadService;
	}

	@Operation(
			summary = "Registrar lead y enviar Magic Link",
			description = "Registra un lead, aplica rate limit por IP/correo y dispara el envío de un enlace mágico al email indicado."
	)
	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiJsonExample(
			status = "202",
			summary = "Lead registrado y correo de acceso enviado",
			description = "El lead fue procesado y se inició el envío del Magic Link",
			path = "/static/swagger/examples/leads/lead-registration-202.json"
	)
	@ApiJsonExample(
			status = "400",
			summary = "Datos de entrada inválidos",
			description = "Campos inválidos o faltantes en la solicitud",
			path = "/static/swagger/examples/leads/lead-registration-400-invalid-values.json"
	)
	@ApiJsonExample(
			status = "400",
			summary = "Faltan valores obligatorios",
			description = "Campos faltantes en la solicitud",
			path = "/static/swagger/examples/leads/lead-registration-400-missing-values.json"
	)
	@ApiJsonExample(
			status = "429",
			summary = "Límite de solicitudes excedido",
			description = "Se alcanzó el límite de solicitudes por IP o correo",
			path = "/static/swagger/examples/leads/lead-registration-429-rate-limited.json"
	)
	public ResponseEntity<LeadRegistrationResponse> register(@Valid @RequestBody LeadRegistrationRequest request,
	                                                         HttpServletRequest servletRequest) {
		String clientIp = getClientIp.getClientIp(servletRequest);
		var response = leadService.createLead(clientIp, request);
		return ResponseEntity.status(202).body(response);
	}

	@Operation(
			summary = "Verificar token de Magic Link",
			description = "Valida el token recibido por correo y lo intercambia por un token de acceso para el lead autenticado."
	)
	@PostMapping(value = "/verify", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiJsonExample(
			summary = "Token de Magic Link verificado",
			description = "Token validado e intercambiado por un token de acceso",
			path = "/static/swagger/examples/leads/lead-token-verification-200.json"
	)
	@ApiJsonExample(
			status = "400",
			summary = "Token de verificación faltante",
			description = "No se proporcionó tokenFromMagicLink en el cuerpo",
			path = "/static/swagger/examples/leads/lead-token-verification-400-token-required.json"
	)
	@ApiJsonExample(
			status = "400",
			summary = "Token de verificación inválido o expirado",
			description = "El token no es válido, expiró o no está registrado",
			path = "/static/swagger/examples/leads/lead-token-verification-400-token-invalid.json"
	)
	@ApiJsonExample(
			status = "400",
			summary = "Token de verificación no proporcionado",
			description = "No se proporcionó tokenFromMagicLink en el cuerpo",
			path = "/static/swagger/examples/leads/lead-token-verification-400-token-required.json"
	)
	public ResponseEntity<LeadAuthenticationResponse> verifyMagicLinkToken(
			@Valid @RequestBody LeadMagicLinkTokenRequest request) {
		LeadAuthenticationResponse response = leadService.emailTokenVerification(request);
		return ResponseEntity.ok(response);
	}
}
