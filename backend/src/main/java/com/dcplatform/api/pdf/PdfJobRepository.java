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
     *
     * El bloqueo solo vive dentro de una transaccion: quien llame a este metodo
     * tiene que ser @Transactional, o la fila queda liberada de inmediato.
     */
    @Query(value = """
            SELECT * FROM pdf_jobs
            WHERE status = 'PENDING'
            ORDER BY created_at
            LIMIT 1
            FOR UPDATE SKIP LOCKED
            """, nativeQuery = true)
    Optional<PdfJob> findNextPendingJobForProcessing();

    Optional<PdfJob> findByResponseId(UUID responseId);
}
