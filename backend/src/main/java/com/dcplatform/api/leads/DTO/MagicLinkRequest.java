package com.dcplatform.api.leads.DTO;

import com.dcplatform.api.leads.Source;
import java.util.UUID;

public record MagicLinkRequest(
        String email,
        String companyName,
        String role,
        Source source,
        UUID estimateId,
        String consent,
        String privacyPolicyVersion
    ) {
}
