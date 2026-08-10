package com.dcplatform.api.leads;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LeandAccessTokenRepository extends JpaRepository<LeadAccessTokens,UUID>{
    Optional<LeadAccessTokens> findByTokenHash(String hash);
    
}
