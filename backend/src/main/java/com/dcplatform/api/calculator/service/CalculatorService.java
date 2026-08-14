package com.dcplatform.api.calculator.service;

import com.dcplatform.api.calculator.model.dto.CalculatorEstimateRequest;
import com.dcplatform.api.calculator.model.dto.CalculatorEstimateResponse;

public interface CalculatorService {
	CalculatorEstimateResponse calculate(CalculatorEstimateRequest request, boolean isUnlocked);
}