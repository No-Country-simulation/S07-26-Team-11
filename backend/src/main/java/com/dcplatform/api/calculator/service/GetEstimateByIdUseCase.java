package com.dcplatform.api.calculator.service;

import com.dcplatform.api.calculator.model.CalculatorEstimateEntity;
import com.dcplatform.api.calculator.model.dto.CalculatorEstimateResponse;
import com.dcplatform.api.shared.UuidValidator;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class GetEstimateByIdUseCase {

	private final Logger logger = org.slf4j.LoggerFactory.getLogger(GetEstimateByIdUseCase.class);
	private final CalculatorPersistenceService persistenceService;

	public GetEstimateByIdUseCase(CalculatorPersistenceService persistenceService) {
		this.persistenceService = persistenceService;
	}

	public CalculatorEstimateResponse execute(String leadEmail, String id) {
		logger.info("Getting estimate for lead {} with id {}", leadEmail, id);

		CalculatorEstimateEntity entity = persistenceService.findById(UuidValidator.safeParse(id));

		return new CalculatorEstimateResponse(
				entity.getId().toString(),
				entity.getCalculationVersion(),
				entity.getCreatedAt().toString(),
				true, // is always unlocked
				entity.getOutputsJson().kpis(),
				entity.getOutputsJson().lockedKpiCount()
		);
	}
}
