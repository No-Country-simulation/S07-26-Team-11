package com.dcplatform.api.leads;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface LeadRepository extends JpaRepository<Lead,UUID> {
    
    Lead findByEmail(String email);
}
