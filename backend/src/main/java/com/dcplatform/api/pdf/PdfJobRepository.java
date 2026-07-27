package com.dcplatform.api.pdf;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PdfJobRepository extends JpaRepository<PdfJob, Long> {

    // Consulta con bloqueo optimizado para el Worker descrita en la arquitectura
    @Query(value = "SELECT * FROM pdf_jobs WHERE status = 'PENDING' ORDER BY created_at LIMIT 1 FOR UPDATE SKIP LOCKED", nativeQuery = true)
    Optional<PdfJob> findNextPendingJobForProcessing();

    Optional<PdfJob> findByResponseId(Long responseId);
}