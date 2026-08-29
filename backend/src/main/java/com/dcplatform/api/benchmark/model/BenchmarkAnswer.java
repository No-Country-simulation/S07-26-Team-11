package com.dcplatform.api.benchmark.model;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "benchmark_answers")
public class BenchmarkAnswer {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(name = "response_id")
	private UUID responseId;

	@Column(name = "question_id")
	private UUID questionId;

	@Column(name = "option_id")
	private UUID optionId;

	@Column(name = "answered_at")
	private OffsetDateTime answeredAt;

	public BenchmarkAnswer() {
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public UUID getResponseId() {
		return responseId;
	}

	public void setResponseId(UUID responseId) {
		this.responseId = responseId;
	}

	public UUID getQuestionId() {
		return questionId;
	}

	public void setQuestionId(UUID questionId) {
		this.questionId = questionId;
	}

	public UUID getOptionId() {
		return optionId;
	}

	public void setOptionId(UUID optionId) {
		this.optionId = optionId;
	}

	public OffsetDateTime getAnsweredAt() {
		return answeredAt;
	}

	public void setAnsweredAt(OffsetDateTime answeredAt) {
		this.answeredAt = answeredAt;
	}
}
