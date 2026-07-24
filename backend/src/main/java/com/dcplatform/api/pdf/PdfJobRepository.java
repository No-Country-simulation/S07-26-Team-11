package com.dcplatform.api.pdf;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PdfJobRepository extends JpaRepository<PdfJob, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT j FROM PdfJob j WHERE j.status = 'PENDING' ORDER BY j.createdAt ASC LIMIT 1")
    Optional<PdfJob> findTopPendingJobForProcessing();
}