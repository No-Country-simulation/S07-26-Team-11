package com.dcplatform.api.calculator.service;

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
		persistenceService.save(leadEmail, request, response);
		return response;
	}
}
