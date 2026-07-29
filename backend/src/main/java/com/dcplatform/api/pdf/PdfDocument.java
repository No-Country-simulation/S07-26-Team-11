package com.dcplatform.api.pdf;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

/**
 * PDF ya generado y subido al object storage.
 *
 * Refleja la tabla pdf_documents de database/migrations/V1__initial_schema.sql.
 * El id de esta fila es el documentId que expone la API: no existe una columna
 * document_id aparte.
 */
@Entity
@Table(name = "pdf_documents")
public class PdfDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    /** FK a benchmark_responses.id. */
    @Column(name = "response_id", nullable = false, updatable = false)
    private UUID responseId;

    /**
     * Ruta del objeto dentro del bucket. Unica (pdf_documents_storage_key_uk):
     * se deriva del responseId para que reprocesar un job pise el mismo objeto
     * en vez de dejar huerfanos.
     */
    @Column(name = "storage_key", nullable = false, length = 500)
    private String storageKey;

    @Column(name = "template_version", nullable = false, length = 20)
    private String templateVersion;

    @Column(name = "size_bytes")
    private Long sizeBytes;

    @Column(name = "page_count")
    private Integer pageCount;

    @Column(name = "generated_at", nullable = false, updatable = false)
    private OffsetDateTime generatedAt;

    @Column(name = "download_count", nullable = false)
    private int downloadCount = 0;

    protected PdfDocument() {
        // requerido por JPA
    }

    public PdfDocument(UUID responseId, String storageKey, String templateVersion,
                       Long sizeBytes, Integer pageCount) {
        this.responseId = responseId;
        this.storageKey = storageKey;
        this.templateVersion = templateVersion;
        this.sizeBytes = sizeBytes;
        this.pageCount = pageCount;
    }

    @PrePersist
    void onInsert() {
        if (this.generatedAt == null) {
            this.generatedAt = OffsetDateTime.now(ZoneOffset.UTC);
        }
    }

    public void registerDownload() {
        this.downloadCount += 1;
    }

    public UUID getId() { return id; }

    public UUID getResponseId() { return responseId; }

    public String getStorageKey() { return storageKey; }

    public String getTemplateVersion() { return templateVersion; }

    public Long getSizeBytes() { return sizeBytes; }

    public Integer getPageCount() { return pageCount; }

    public OffsetDateTime getGeneratedAt() { return generatedAt; }

    public int getDownloadCount() { return downloadCount; }
}
