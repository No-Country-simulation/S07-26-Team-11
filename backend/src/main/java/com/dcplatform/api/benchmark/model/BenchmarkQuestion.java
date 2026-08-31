package com.dcplatform.api.benchmark.model;

import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "benchmark_questions",
		uniqueConstraints = @UniqueConstraint(name = "benchmark_questions_order_uk", columnNames = {"dimension_id", "display_order"}),
		indexes = @Index(name = "benchmark_questions_dimension_idx", columnList = "dimension_id")
)
public class BenchmarkQuestion {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "dimension_id")
	private BenchmarkDimension dimension;

	private String text;

	@Column(name = "help_text")
	private String helpText;

	@Column(name = "display_order")
	private Integer displayOrder;

	@OneToMany(mappedBy = "question", fetch = FetchType.EAGER)
	@OrderBy("displayOrder ASC")
	private List<BenchmarkOption> options;

	public BenchmarkQuestion() {
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public BenchmarkDimension getDimension() {
		return dimension;
	}

	public void setDimension(BenchmarkDimension dimension) {
		this.dimension = dimension;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getHelpText() {
		return helpText;
	}

	public void setHelpText(String helpText) {
		this.helpText = helpText;
	}

	public Integer getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}

	public List<BenchmarkOption> getOptions() {
		return options;
	}

	public void setOptions(List<BenchmarkOption> options) {
		this.options = options;
	}
}
