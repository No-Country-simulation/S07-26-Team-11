package com.dcplatform.api.benchmark.repository;

import com.dcplatform.api.benchmark.model.BenchmarkInstrument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BenchmarkInstrumentRepository extends JpaRepository<BenchmarkInstrument, UUID> {

	Optional<BenchmarkInstrument> findByIsActiveTrue();

	Optional<BenchmarkInstrument> findByIdAndIsActiveTrue(UUID id);
}
