package com.dcplatform.api.pdf;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

/**
 * Trabajo de la cola de generacion de PDF.
 *
 * Refleja la tabla pdf_jobs de database/migrations/V1__initial_schema.sql.
 * El esquema lo gobierna Flyway y Hibernate solo lo valida (ddl-auto: validate):
 * cualquier campo que se agregue aca necesita antes su migracion.
 */
@Entity
@Table(name = "pdf_jobs")
public class PdfJob {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    /** FK a benchmark_responses.id. Unica: un job por respuesta (pdf_jobs_response_uk). */
    @Column(name = "response_id", nullable = false, updatable = false)
    private UUID responseId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private JobStatus status = JobStatus.PENDING;

    @Column(name = "attempts", nullable = false)
    private int attempts = 0;

    @Column(name = "failure_reason", columnDefinition = "text")
    private String failureReason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "started_at")
    private OffsetDateTime startedAt;

    @Column(name = "finished_at")
    private OffsetDateTime finishedAt;

    protected PdfJob() {
        // requerido por JPA
    }

    public PdfJob(UUID responseId) {
        this.responseId = responseId;
    }

    @PrePersist
    void onInsert() {
        // La columna tiene DEFAULT now(), pero Hibernate incluye created_at en el INSERT
        // y un null explicito pisaria el default y violaria el NOT NULL.
        if (this.createdAt == null) {
            this.createdAt = OffsetDateTime.now(ZoneOffset.UTC);
        }
    }

    /** Toma el trabajo: suma un intento y marca el inicio. */
    public void markProcessing() {
        this.status = JobStatus.PROCESSING;
        this.attempts += 1;
        this.startedAt = OffsetDateTime.now(ZoneOffset.UTC);
        this.failureReason = null;
    }

    public void markDone() {
        this.status = JobStatus.DONE;
        this.finishedAt = OffsetDateTime.now(ZoneOffset.UTC);
        this.failureReason = null;
    }

    /**
     * Devuelve el trabajo a PENDING si quedan reintentos; si no, lo cierra como FAILED.
     * El maximo de 3 intentos es la decision registrada en PdfGeneratorArchitecture.md.
     */
    public void markFailed(String reason, int maxAttempts) {
        this.failureReason = reason;
        if (this.attempts >= maxAttempts) {
            this.status = JobStatus.FAILED;
            this.finishedAt = OffsetDateTime.now(ZoneOffset.UTC);
        } else {
            this.status = JobStatus.PENDING;
            this.startedAt = null;
        }
    }

    public enum JobStatus {
        PENDING, PROCESSING, DONE, FAILED
    }

    public UUID getId() { return id; }

    public UUID getResponseId() { return responseId; }

    public JobStatus getStatus() { return status; }
    public void setStatus(JobStatus status) { this.status = status; }

    public int getAttempts() { return attempts; }

    public String getFailureReason() { return failureReason; }

    public OffsetDateTime getCreatedAt() { return createdAt; }

    public OffsetDateTime getStartedAt() { return startedAt; }

    public OffsetDateTime getFinishedAt() { return finishedAt; }
}
