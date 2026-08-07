package com.dcplatform.api.calculator;

import com.dcplatform.api.shared.annotations.RealIntegration;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

@RealIntegration
@Service
public class ExternalApiCalculatorService implements CalculatorService {

	private final Logger logger = org.slf4j.LoggerFactory.getLogger(ExternalApiCalculatorService.class);

	// Aquí se inyectaría el RestClient, WebClient o RestTemplate

	@Override
	public CalculatorEstimateResponse calculate(CalculatorEstimateRequest request) {
		// Lógica para llamar al endpoint secreto del cliente,
		// mapear su respuesta y devolver el CalculatorEstimateResponse.
		logger.info("Calling external API");
		return null;
	}
}
