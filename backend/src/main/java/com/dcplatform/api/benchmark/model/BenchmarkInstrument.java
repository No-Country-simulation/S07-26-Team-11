package com.dcplatform.api.benchmark.model;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "benchmark_instruments")
public class BenchmarkInstrument {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(name = "version", nullable = false, unique = true, length = 20)
	private String version;

	@Column(name = "is_active", nullable = false)
	private boolean isActive = false;

	@Column(name = "published_at")
	private OffsetDateTime publishedAt;

	@Column(name = "created_at", nullable = false, updatable = false)
	private OffsetDateTime createdAt = OffsetDateTime.now();

	@OneToMany(mappedBy = "instrument", fetch = FetchType.EAGER)
	@OrderBy("displayOrder ASC")
	private List<BenchmarkDimension> dimensions;

	public BenchmarkInstrument() {
	}

	// activa y publica el instrumento
	public void activateAndPublish() {
		this.isActive = true;
		this.publishedAt = OffsetDateTime.now();
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

	public OffsetDateTime getPublishedAt() {
		return publishedAt;
	}

	public void setPublishedAt(OffsetDateTime publishedAt) {
		this.publishedAt = publishedAt;
	}

	public OffsetDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(OffsetDateTime createdAt) {
		this.createdAt = createdAt;
	}
}
