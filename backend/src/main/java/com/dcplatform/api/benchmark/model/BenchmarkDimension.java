package com.dcplatform.api.benchmark.model;

import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "benchmark_dimensions")
public class BenchmarkDimension {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "instrument_id")
	private BenchmarkInstrument instrument;

	private String code;
	private String label;

	@Column(name = "display_order")
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
}
