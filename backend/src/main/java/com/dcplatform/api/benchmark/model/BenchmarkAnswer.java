package com.dcplatform.api.benchmark.model;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "benchmark_answers",
		uniqueConstraints = @UniqueConstraint(
				name = "benchmark_answers_uk",
				columnNames = {"response_id", "question_id"}
		),
		indexes = @Index(name = "benchmark_answers_response_idx", columnList = "response_id")
)
public class BenchmarkAnswer {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	// Relación N:1 con Response
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "response_id", nullable = false, updatable = false)
	private BenchmarkResponse response;

	// Relación N:1 con Question
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "question_id", nullable = false, updatable = false)
	private BenchmarkQuestion question;

	// Relación N:1 con Option
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "option_id", nullable = false)
	private BenchmarkOption option;

	@Column(name = "answered_at", nullable = false)
	private OffsetDateTime answeredAt;

	public BenchmarkAnswer() {
	}

	@PrePersist
	@PreUpdate
	public void preSave() {
		this.answeredAt = OffsetDateTime.now();
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public BenchmarkResponse getResponse() {
		return response;
	}

	public void setResponse(BenchmarkResponse response) {
		this.response = response;
	}

	public BenchmarkQuestion getQuestion() {
		return question;
	}

	public void setQuestion(BenchmarkQuestion question) {
		this.question = question;
	}

	public BenchmarkOption getOption() {
		return option;
	}

	public void setOption(BenchmarkOption option) {
		this.option = option;
	}

	public OffsetDateTime getAnsweredAt() {
		return answeredAt;
	}

	public void setAnsweredAt(OffsetDateTime answeredAt) {
		this.answeredAt = answeredAt;
	}
}
