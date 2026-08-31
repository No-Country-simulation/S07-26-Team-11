package com.dcplatform.api.benchmark.model;

import com.dcplatform.api.benchmark.agent.dto.AiReportResult;
import com.dcplatform.api.leads.model.LeadEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "benchmark_responses",
		uniqueConstraints = @UniqueConstraint(
				name = "benchmark_responses_lead_instrument_uk",
				columnNames = {"lead_id", "instrument_id"}
		),
		indexes = {
				@Index(name = "benchmark_responses_lead_idx", columnList = "lead_id"),
				@Index(name = "benchmark_responses_completed_idx", columnList = "completed_at DESC")
		}
)
public class BenchmarkResponse {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Version
	private Long version;

	// Relación N:1 con Lead
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "lead_id", nullable = false, updatable = false)
	private LeadEntity lead;

	// Relación N:1 con Instrument
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "instrument_id", nullable = false, updatable = false)
	private BenchmarkInstrument instrument;

	// Relación 1:N con Answers
	@OneToMany(mappedBy = "response", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<BenchmarkAnswer> answers = new ArrayList<>();

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private BenchmarkStatus status = BenchmarkStatus.IN_PROGRESS;

	@Column(name = "global_score")
	private BigDecimal globalScore;

	@Column(name = "maturity_level")
	private Integer maturityLevel;

	@Column(name = "percentile")
	private BigDecimal percentile;

	@Column(name = "cohort_size")
	private Integer cohortSize;

	@Column(name = "started_at", nullable = false, updatable = false)
	private OffsetDateTime startedAt;

	@Column(name = "completed_at")
	private OffsetDateTime completedAt;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "ai_report_result", columnDefinition = "jsonb")
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
		IN_PROGRESS, COMPLETED, ABANDONED
	}

	// Helper method para sincronizar la relación bidireccional
	public void addAnswer(BenchmarkAnswer answer) {
		answers.add(answer);
		answer.setResponse(this);
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

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public LeadEntity getLead() {
		return lead;
	}

	public void setLead(LeadEntity lead) {
		this.lead = lead;
	}

	public BenchmarkInstrument getInstrument() {
		return instrument;
	}

	public void setInstrument(BenchmarkInstrument instrument) {
		this.instrument = instrument;
	}

	public List<BenchmarkAnswer> getAnswers() {
		return answers;
	}

	public void setAnswers(List<BenchmarkAnswer> answers) {
		this.answers = answers;
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

	public Integer getMaturityLevel() {
		return maturityLevel;
	}

	public void setMaturityLevel(Integer maturityLevel) {
		this.maturityLevel = maturityLevel;
	}

	public BigDecimal getPercentile() {
		return percentile;
	}

	public void setPercentile(BigDecimal percentile) {
		this.percentile = percentile;
	}

	public Integer getCohortSize() {
		return cohortSize;
	}

	public void setCohortSize(Integer cohortSize) {
		this.cohortSize = cohortSize;
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
