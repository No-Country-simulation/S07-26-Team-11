package com.dcplatform.api.benchmark.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "benchmark_dimensions", uniqueConstraints = {
		@UniqueConstraint(name = "benchmark_dimensions_uk", columnNames = {"instrument_id", "code"})
})
public class BenchmarkDimension {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "instrument_id", nullable = false)
	private BenchmarkInstrument instrument;

	@Column(nullable = false, length = 60)
	private String code;

	@Column(nullable = false, length = 200)
	private String label;

	@Column(nullable = false, precision = 5, scale = 4)
	private BigDecimal weight;

	@Column(name = "display_order", nullable = false)
	private Integer displayOrder;

	@OneToMany(mappedBy = "dimension", fetch = FetchType.EAGER)
	@OrderBy("displayOrder ASC")
	private List<BenchmarkQuestion> questions;

	public BenchmarkDimension() {
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public BenchmarkInstrument getInstrument() {
		return instrument;
	}

	public void setInstrument(BenchmarkInstrument instrument) {
		this.instrument = instrument;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Integer getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}

	public List<BenchmarkQuestion> getQuestions() {
		return questions;
	}

	public void setQuestions(List<BenchmarkQuestion> questions) {
		this.questions = questions;
	}

	public BigDecimal getWeight() {
		return weight;
	}

	public void setWeight(BigDecimal weight) {
		this.weight = weight;
	}
}
