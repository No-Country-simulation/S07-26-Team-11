package com.dcplatform.api.leads;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class Lead {
    private UUID id;
    private String email;
    private String companyName;
    private String role;
    private Source source; 
    private LocalDateTime consentAt;
    private String consentIp; 
    private String privacyPolicyVersion;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    
}
