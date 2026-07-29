package com.dcplatform.api.pdf;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * El documentId de la API es la clave primaria de pdf_documents, asi que la
 * busqueda por documento es findById heredado de JpaRepository.
 */
@Repository
public interface PdfDocumentRepository extends JpaRepository<PdfDocument, UUID> {

    Optional<PdfDocument> findByResponseId(UUID responseId);

    Optional<PdfDocument> findByStorageKey(String storageKey);
}
