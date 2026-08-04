package com.dcplatform.api.leads;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class LeadAccessTokens {
    private UUID id ;
    private UUID lead_id;
    private String token_hash;
    private LocalDateTime expires_at;
    private LocalDateTime used_at;
    private  LocalDateTime created_at;
}
