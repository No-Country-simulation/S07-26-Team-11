package com.dcplatform.api.benchmark.model;

import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "benchmark_instruments")
public class BenchmarkInstrument {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	private String version;

	@Column(name = "is_active")
	private boolean isActive;

	// Relación con las etapas (dimensiones)
	@OneToMany(mappedBy = "instrument", fetch = FetchType.EAGER)
	@OrderBy("displayOrder ASC") // Respetamos el orden de la base de datos
	private List<BenchmarkDimension> dimensions;

	public BenchmarkInstrument() {
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean active) {
		isActive = active;
	}

	public List<BenchmarkDimension> getDimensions() {
		return dimensions;
	}

	public void setDimensions(List<BenchmarkDimension> dimensions) {
		this.dimensions = dimensions;
	}
}
