package com.dcplatform.api.leads.service;

import com.dcplatform.api.leads.model.LeadEntity;
import com.dcplatform.api.leads.model.dto.LeadAuthenticationResponse;
import com.dcplatform.api.leads.model.dto.LeadMagicLinkTokenRequest;
import com.dcplatform.api.leads.model.dto.LeadRegistrationRequest;
import com.dcplatform.api.leads.model.dto.LeadRegistrationResponse;

public interface LeadService {
	LeadRegistrationResponse createLead(String clientIp, LeadRegistrationRequest request);

	LeadAuthenticationResponse emailTokenVerification(LeadMagicLinkTokenRequest request);

	LeadEntity getLeadEntityByEmail(String email);
}
