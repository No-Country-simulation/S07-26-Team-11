package com.dcplatform.api.leads.DTO;

import java.util.Date;

public record TokenResponse(
                String accessToken,
                Date expiresAt,
                LeadResponse lead

) {

}
