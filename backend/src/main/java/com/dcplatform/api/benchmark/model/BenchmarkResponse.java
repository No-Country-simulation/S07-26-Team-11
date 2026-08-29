package com.dcplatform.api.benchmark.model;

import com.dcplatform.api.benchmark.agent.dto.AiReportResult;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "benchmark_responses")
public class BenchmarkResponse {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(name = "lead_id")
	private UUID leadId;

	@Column(name = "instrument_id")
	private UUID instrumentId;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private BenchmarkStatus status = BenchmarkStatus.IN_PROGRESS;

	@Column(name = "global_score")
	private BigDecimal globalScore;

	@Column(name = "started_at")
	private OffsetDateTime startedAt;

	@Column(name = "completed_at")
	private OffsetDateTime completedAt;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "ai_report_result", columnDefinition = "jsonb", nullable = false)
	private AiReportResult aiReportResult;

	public BenchmarkResponse() {
	}

	@PrePersist
	public void prePersist() {
		if (this.startedAt == null) {
			this.startedAt = OffsetDateTime.now();
		}
	}

	public enum BenchmarkStatus {
		// respetando el constraint: 'IN_PROGRESS', 'COMPLETED', 'ABANDONED'
		IN_PROGRESS,
		COMPLETED,
		ABANDONED
	}

	public void markAsInProgress() {
		this.status = BenchmarkStatus.IN_PROGRESS;
		this.startedAt = OffsetDateTime.now();
	}

	public void markAsCompleted() {
		this.status = BenchmarkStatus.COMPLETED;
		this.completedAt = OffsetDateTime.now();
	}

	public void markAsAbandoned() {
		this.status = BenchmarkStatus.ABANDONED;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public UUID getLeadId() {
		return leadId;
	}

	public void setLeadId(UUID leadId) {
		this.leadId = leadId;
	}

	public UUID getInstrumentId() {
		return instrumentId;
	}

	public void setInstrumentId(UUID instrumentId) {
		this.instrumentId = instrumentId;
	}

	public BenchmarkStatus getStatus() {
		return status;
	}

	public void setStatus(BenchmarkStatus status) {
		this.status = status;
	}

	public BigDecimal getGlobalScore() {
		return globalScore;
	}

	public void setGlobalScore(BigDecimal globalScore) {
		this.globalScore = globalScore;
	}

	public OffsetDateTime getStartedAt() {
		return startedAt;
	}

	public void setStartedAt(OffsetDateTime startedAt) {
		this.startedAt = startedAt;
	}

	public OffsetDateTime getCompletedAt() {
		return completedAt;
	}

	public void setCompletedAt(OffsetDateTime completedAt) {
		this.completedAt = completedAt;
	}

	public AiReportResult getAiReportResult() {
		return aiReportResult;
	}

	public void setAiReportResult(AiReportResult aiReportResult) {
		this.aiReportResult = aiReportResult;
	}
}
