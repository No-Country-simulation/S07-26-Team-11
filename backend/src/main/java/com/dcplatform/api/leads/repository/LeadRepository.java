package com.dcplatform.api.leads.repository;

import com.dcplatform.api.leads.model.LeadEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LeadRepository extends JpaRepository<LeadEntity, UUID> {
	Optional<LeadEntity> findByEmail(String email);
}
