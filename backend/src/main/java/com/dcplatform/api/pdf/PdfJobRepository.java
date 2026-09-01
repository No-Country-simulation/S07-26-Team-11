package com.dcplatform.api.pdf;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PdfJobRepository extends JpaRepository<PdfJob, UUID> {

    /**
     * Toma el siguiente trabajo pendiente saltando los que ya bloqueo otro worker.
     * SKIP LOCKED es lo que permite correr varios workers sin cola externa
     * (ver PdfGeneratorArchitecture.md).
     * <p>
     * El bloqueo solo vive dentro de una transacción: quien llame a este método
     * tiene que ser @Transactional, o la fila queda liberada de inmediato.
     */
    @Query(value = """
            SELECT p.* FROM pdf_jobs p
            INNER JOIN benchmark_responses r ON p.response_id = r.id
            WHERE p.status = 'PENDING'
              AND r.status = 'COMPLETED'
            ORDER BY p.created_at ASC
            LIMIT 1
            FOR UPDATE OF p SKIP LOCKED
            """, nativeQuery = true)
    Optional<PdfJob> findNextPendingJobForProcessing();

    Optional<PdfJob> findByResponseId(UUID responseId);
}