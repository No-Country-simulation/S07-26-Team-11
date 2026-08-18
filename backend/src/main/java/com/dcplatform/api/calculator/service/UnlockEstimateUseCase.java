package com.dcplatform.api.calculator.service;

import com.dcplatform.api.calculator.model.CalculatorEstimateEntity;
import com.dcplatform.api.calculator.model.dto.CalculatorEstimateRequest;
import com.dcplatform.api.calculator.model.dto.CalculatorEstimateResponse;
import org.springframework.stereotype.Service;

@Service
public class UnlockEstimateUseCase {

	private final CalculatorService calculatorService;
	private final CalculatorPersistenceService persistenceService;

	public UnlockEstimateUseCase(CalculatorService calculatorService, CalculatorPersistenceService persistenceService) {
		this.calculatorService = calculatorService;
		this.persistenceService = persistenceService;
	}

	public CalculatorEstimateResponse execute(String leadEmail, CalculatorEstimateRequest request) {
		CalculatorEstimateResponse response = calculatorService.calculate(request, true);
		CalculatorEstimateEntity entity = persistenceService.save(leadEmail, request, response);

		response = new CalculatorEstimateResponse(
				entity.getId().toString(),
				entity.getCalculationVersion(),
				entity.getCreatedAt().toString(),
				response.unlocked(),
				response.kpis(),
				response.lockedKpiCount()
		);

		return response;
	}
}
