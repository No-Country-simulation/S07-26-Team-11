package com.dcplatform.api.leads;

import com.dcplatform.api.leads.model.dto.LeadAuthenticationResponse;
import com.dcplatform.api.leads.model.dto.LeadMagicLinkTokenRequest;
import com.dcplatform.api.leads.model.dto.LeadRegistrationRequest;
import com.dcplatform.api.leads.model.dto.LeadRegistrationResponse;
import com.dcplatform.api.leads.service.LeadService;
import com.dcplatform.api.leads.utils.GetClientIp;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/public/leads")
public class LeadController {

	private final LeadService leadService;
	private final GetClientIp getClientIp = new GetClientIp();

	public LeadController(LeadService leadService) {
		this.leadService = leadService;
	}

	@PostMapping()
	public ResponseEntity<LeadRegistrationResponse> register(@Valid @RequestBody LeadRegistrationRequest request,
	                                                         HttpServletRequest servletRequest) {
		String clientIp = getClientIp.getClienwtIp(servletRequest);
		var response = leadService.createLead(clientIp, request);
		return ResponseEntity.status(202).body(response);
	}

	@PostMapping("/verify")
	public ResponseEntity<LeadAuthenticationResponse> verifyMagicLinkToken(
			@Valid @RequestBody LeadMagicLinkTokenRequest request) {
		LeadAuthenticationResponse response = leadService.emailTokenVerification(request);
		return ResponseEntity.ok(response);
	}
}
