package com.dcplatform.api.calculator;

import com.dcplatform.api.calculator.model.dto.CalculatorEstimateRequest;
import com.dcplatform.api.calculator.model.dto.CalculatorEstimateResponse;
import com.dcplatform.api.calculator.service.CalculatorService;
import com.dcplatform.api.calculator.service.GetEstimateById;
import com.dcplatform.api.calculator.service.UnlockEstimateUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/public/calculator")
public class CalculatorController {

	private final CalculatorService calculatorService;
	private final UnlockEstimateUseCase unlockEstimateUseCase;
	private final GetEstimateById getEstimateById;

	public CalculatorController(CalculatorService calculatorService,
	                            UnlockEstimateUseCase unlockEstimateUseCase,
	                            GetEstimateById getEstimateById) {
		this.calculatorService = calculatorService;
		this.unlockEstimateUseCase = unlockEstimateUseCase;
		this.getEstimateById = getEstimateById;
	}

	@PostMapping("/estimate")
	public ResponseEntity<CalculatorEstimateResponse> estimate(@Valid @RequestBody CalculatorEstimateRequest request) {
		CalculatorEstimateResponse response = calculatorService.calculate(request, false);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/estimate/unlock-results")
	public ResponseEntity<CalculatorEstimateResponse> unlockEstimate(
			@Valid @RequestBody CalculatorEstimateRequest request, @AuthenticationPrincipal String leadEmail) {
		CalculatorEstimateResponse response = unlockEstimateUseCase.execute(leadEmail, request);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/defaults")
	public ResponseEntity<CalculatorEstimateRequest> getDefaults() {
		final double installedCapacityKw = 2000.0;
		final double usedCapacityKw = 760.0;
		final double electricityRatePerKwh = 0.12;
		final int rackCount = 48;

		final CalculatorEstimateRequest defaultsValues = new CalculatorEstimateRequest(
				installedCapacityKw,
				usedCapacityKw,
				electricityRatePerKwh,
				rackCount
		);

		return ResponseEntity.ok(defaultsValues);
	}

	@GetMapping("/estimates/{id}")
	public ResponseEntity<CalculatorEstimateResponse> getEstimate(@PathVariable String id,
	                                                              @AuthenticationPrincipal String leadEmail) {
		return ResponseEntity.ok(getEstimateById.execute(leadEmail, id));
	}
}
