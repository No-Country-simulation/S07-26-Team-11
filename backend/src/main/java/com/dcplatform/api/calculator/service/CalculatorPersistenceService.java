package com.dcplatform.api.calculator.service;

import com.dcplatform.api.calculator.model.CalculatorEstimateEntity;
import com.dcplatform.api.calculator.model.KpiCode;
import com.dcplatform.api.calculator.model.dto.CalculatorEstimateRequest;
import com.dcplatform.api.calculator.model.dto.CalculatorEstimateResponse;
import com.dcplatform.api.calculator.repository.CalculatorRepository;
import com.dcplatform.api.shared.ApiException;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Service
public class CalculatorPersistenceService {

	private final Logger logger = org.slf4j.LoggerFactory.getLogger(CalculatorPersistenceService.class);
	private final CalculatorRepository repository;

	public CalculatorPersistenceService(CalculatorRepository repository) {
		this.repository = repository;
	}

	public void save(String leadEmail, CalculatorEstimateRequest request, CalculatorEstimateResponse response) {
		logger.info("Saving estimate for lead {}", leadEmail);
		CalculatorEstimateEntity entity = new CalculatorEstimateEntity();

		// TODO: insert lead entity here with LeadRepository.findByEmail(leadEmail)
		entity.setCalculationVersion(response.calculationVersion());
		entity.setInputsJson(request);
		entity.setOutputsJson(response);
		BigDecimal idleCapacityRatio = BigDecimal.valueOf(
				getIdleCapacityVariantFromResponse(KpiCode.IDLE_CAPACITY_RATIO, response)
		);
		BigDecimal idleCapacityCost = BigDecimal.valueOf(
				getIdleCapacityVariantFromResponse(KpiCode.IDLE_CAPACITY_ANNUAL_COST, response)
		);
		entity.setIdleCapacityRatio(idleCapacityRatio);
		entity.setIdleCapacityCost(idleCapacityCost);
		final String currency = "USD";
		entity.setCurrency(currency);
		entity.setCreatedAt(OffsetDateTime.parse(response.createdAt()));

		repository.save(entity);
	}

	public CalculatorEstimateEntity findById(String id) {
		return repository.findById(id)
				.orElseThrow(() -> ApiException.notFound("Cálculo de estimación no encontrado para ID " + id));
	}

	private Double getIdleCapacityVariantFromResponse(KpiCode variantCode, CalculatorEstimateResponse response) {
		return response.kpis().stream()
				.filter(kpi -> kpi.code().equals(variantCode))
				.findFirst()
				.map(CalculatorEstimateResponse.KpiResult::value)
				.orElse(0.0);
	}
}
