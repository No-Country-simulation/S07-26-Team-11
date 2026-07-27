package com.dcplatform.api.pdf;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PdfDocumentRepository extends JpaRepository<PdfDocument, Long> {
    Optional<PdfDocument> findByDocumentId(UUID documentId);
    Optional<PdfDocument> findByResponseId(Long responseId);
}