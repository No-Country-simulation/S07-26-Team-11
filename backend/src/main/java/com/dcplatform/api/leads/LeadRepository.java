package com.dcplatform.api.leads;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;


public interface LeadRepository extends JpaRepository<Lead,UUID> {
    
    Optional<Lead> findByEmail(String email);
}
