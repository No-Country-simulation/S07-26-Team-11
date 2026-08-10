package com.dcplatform.api.calculator;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/public/calculator")
public class CalculatorController {

	private final CalculatorService calculatorService;

	public CalculatorController(CalculatorService calculatorService) {
		this.calculatorService = calculatorService;
	}

	@PostMapping("/estimate")
	public ResponseEntity<CalculatorEstimateResponse> estimate(@Valid @RequestBody CalculatorEstimateRequest request) {
		CalculatorEstimateResponse response = calculatorService.calculate(request);
		return ResponseEntity.ok(response);
	}
}
