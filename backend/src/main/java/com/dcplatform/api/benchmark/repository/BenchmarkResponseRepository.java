package com.dcplatform.api.benchmark.repository;

import com.dcplatform.api.benchmark.model.BenchmarkInstrument;
import com.dcplatform.api.benchmark.model.BenchmarkResponse;
import com.dcplatform.api.benchmark.model.BenchmarkResponse.BenchmarkStatus;
import com.dcplatform.api.leads.model.LeadEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BenchmarkResponseRepository extends JpaRepository<BenchmarkResponse, UUID> {

	Optional<BenchmarkResponse> findByLeadAndInstrumentAndStatusWithinIgnoreCase(LeadEntity lead,
	                                                                             BenchmarkInstrument instrument,
	                                                                             BenchmarkStatus status);

	Optional<BenchmarkResponse> findByIdAndLead(UUID responseId, LeadEntity lead);
}
