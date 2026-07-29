package com.dcplatform.api.pdf;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PdfJobRepository extends JpaRepository<PdfJob, Long> {

    @Query(value = "SELECT * FROM pdf_jobs WHERE status = 'PENDING' ORDER BY created_at LIMIT 1 FOR UPDATE SKIP LOCKED", nativeQuery = true)
    Optional<PdfJob> findNextPendingJobForProcessing();

    Optional<PdfJob> findByResponseId(Long responseId);
}