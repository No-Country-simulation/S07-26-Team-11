package com.dcplatform.api.leads.DTO;

import java.util.UUID;

public record LeadResponse(
        UUID id,
        String email,
        String companyName

) {

}
