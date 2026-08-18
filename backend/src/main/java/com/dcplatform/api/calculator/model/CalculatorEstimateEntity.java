package com.dcplatform.api.calculator.model;

import com.dcplatform.api.calculator.model.dto.CalculatorEstimateRequest;
import com.dcplatform.api.calculator.model.dto.CalculatorEstimateResponse;
import com.dcplatform.api.leads.model.LeadEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "calculator_estimates")
public class CalculatorEstimateEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "lead_id")
	private LeadEntity lead;

	@Column(name = "calculation_version", length = 20, nullable = false)
	private String calculationVersion;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "inputs_json", columnDefinition = "jsonb", nullable = false)
	private CalculatorEstimateRequest inputsJson;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "outputs_json", columnDefinition = "jsonb", nullable = false)
	private CalculatorEstimateResponse outputsJson;

	@Column(name = "idle_capacity_ratio", precision = 7, scale = 4)
	private BigDecimal idleCapacityRatio;

	@Column(name = "idle_capacity_cost", precision = 19, scale = 4)
	private BigDecimal idleCapacityCost;

	@JdbcTypeCode(SqlTypes.CHAR)
	@Column(length = 3)
	private String currency;

	@Column(name = "created_at", nullable = false, updatable = false)
	private OffsetDateTime createdAt;

	// Constructor vacío exigido por la especificación JPA
	public CalculatorEstimateEntity() {
	}

	// --- Getters y Setters ---

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public LeadEntity getLead() {
		return lead;
	}

	public void setLead(LeadEntity lead) {
		this.lead = lead;
	}

	public String getCalculationVersion() {
		return calculationVersion;
	}

	public void setCalculationVersion(String calculationVersion) {
		this.calculationVersion = calculationVersion;
	}

	public CalculatorEstimateRequest getInputsJson() {
		return inputsJson;
	}

	public void setInputsJson(CalculatorEstimateRequest inputsJson) {
		this.inputsJson = inputsJson;
	}

	public CalculatorEstimateResponse getOutputsJson() {
		return outputsJson;
	}

	public void setOutputsJson(CalculatorEstimateResponse outputsJson) {
		this.outputsJson = outputsJson;
	}

	public BigDecimal getIdleCapacityRatio() {
		return idleCapacityRatio;
	}

	public void setIdleCapacityRatio(BigDecimal idleCapacityRatio) {
		this.idleCapacityRatio = idleCapacityRatio;
	}

	public BigDecimal getIdleCapacityCost() {
		return idleCapacityCost;
	}

	public void setIdleCapacityCost(BigDecimal idleCapacityCost) {
		this.idleCapacityCost = idleCapacityCost;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public OffsetDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(OffsetDateTime createdAt) {
		this.createdAt = createdAt;
	}
}
