package com.dcplatform.api.benchmark.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "benchmark_options")
public class BenchmarkOption {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "question_id")
	private BenchmarkQuestion question;

	private String label;

	private BigDecimal score;

	@Column(name = "display_order")
	private Integer displayOrder;

	public BenchmarkOption() {
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public BenchmarkQuestion getQuestion() {
		return question;
	}

	public void setQuestion(BenchmarkQuestion question) {
		this.question = question;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public BigDecimal getScore() {
		return score;
	}

	public void setScore(BigDecimal score) {
		this.score = score;
	}

	public Integer getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}
}
