package com.dcplatform.api.calculator;

import com.dcplatform.api.calculator.model.dto.CalculatorEstimateRequest;
import com.dcplatform.api.calculator.model.dto.CalculatorEstimateResponse;
import com.dcplatform.api.calculator.service.CalculatorService;
import com.dcplatform.api.calculator.service.GetEstimateByIdUseCase;
import com.dcplatform.api.calculator.service.UnlockEstimateUseCase;
import com.dcplatform.api.shared.annotations.ApiJsonExample;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/public/calculator")
@Tag(name = "Calculator", description = "Calcula costos y capacidad subutilizada de un data center")
public class CalculatorController {

	private final CalculatorService calculatorService;
	private final UnlockEstimateUseCase unlockEstimateUseCase;
	private final GetEstimateByIdUseCase getEstimateByIdUseCase;

	public CalculatorController(CalculatorService calculatorService,
	                            UnlockEstimateUseCase unlockEstimateUseCase,
	                            GetEstimateByIdUseCase getEstimateByIdUseCase) {
		this.calculatorService = calculatorService;
		this.unlockEstimateUseCase = unlockEstimateUseCase;
		this.getEstimateByIdUseCase = getEstimateByIdUseCase;
	}

	@PostMapping("/estimate")
	@Operation(summary = "Calcula una estimación", description = "Devuelve los KPI públicos y mantiene bloqueados los resultados de costo mensual y proyección a 3 anos.")
	@ApiJsonExample(
			status = HttpStatus.OK,
			description = "Estimación calculada con KPIs como teasers",
			path = "/swagger/examples/calculator/estimation-200.json"
	)
	@ApiJsonExample(
			status = HttpStatus.BAD_REQUEST,
			description = "Datos de entrada inválidos",
			path = "/swagger/examples/calculator/estimation-400-invalid-values.json"
	)
	@ApiJsonExample(
			status = HttpStatus.BAD_REQUEST,
			description = "La capacidad utilizada es mayor que la capacidad instalada",
			path = "/swagger/examples/calculator/estimation-400-capacity-exceeded.json"
	)
	@ApiJsonExample(
			status = HttpStatus.BAD_REQUEST,
			description = "Campos o datos faltantes",
			path = "/swagger/examples/calculator/estimation-400-missing-fields.json"
	)
	public ResponseEntity<CalculatorEstimateResponse> estimate(@Valid @RequestBody CalculatorEstimateRequest request) {
		CalculatorEstimateResponse response = calculatorService.calculate(request, false);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/estimate/unlock-results")
	@Operation(summary = "Desbloquea los resultados de costo", description = "Calcula y guarda una estimación completa para el lead autenticado.")
	@ApiJsonExample(
			status = HttpStatus.OK,
			path = "/swagger/examples/calculator/estimation-200-unlocked-results.json",
			description = "Estimación calculada con todos los KPIs desbloqueados"
	)
	@ApiJsonExample(
			status = HttpStatus.BAD_REQUEST,
			description = "Datos de entrada inválidos",
			path = "/swagger/examples/calculator/estimation-400-invalid-values.json"
	)
	@ApiJsonExample(
			status = HttpStatus.BAD_REQUEST,
			description = "La capacidad utilizada es mayor que la capacidad instalada",
			path = "/swagger/examples/calculator/estimation-400-capacity-exceeded.json"
	)
	@ApiJsonExample(
			status = HttpStatus.BAD_REQUEST,
			description = "Campos o datos faltantes",
			path = "/swagger/examples/calculator/estimation-400-missing-fields.json"
	)
	@ApiJsonExample(
			status = HttpStatus.UNAUTHORIZED,
			description = "Lead/usuario no autenticado",
			path = "/swagger/examples/auth/authentication-401.json"
	)
	public ResponseEntity<CalculatorEstimateResponse> unlockEstimate(
			@Valid @RequestBody CalculatorEstimateRequest request, @AuthenticationPrincipal String leadEmail) {
		CalculatorEstimateResponse response = unlockEstimateUseCase.execute(leadEmail, request);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/defaults")
	@Operation(summary = "Obtiene valores de ejemplo", description = "Devuelve valores iniciales que pueden enviarse directamente a POST /estimate.")
	@ApiJsonExample(
			status = HttpStatus.OK,
			description = "Valores para ser usados como ejemplo/rellenar campos",
			path = "/swagger/examples/calculator/default-200.json"
	)
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
	@Operation(summary = "Consulta una estimación guardada")
	@ApiJsonExample(
			status = HttpStatus.OK,
			description = "Estimación calculada con todos los KPIs desbloqueados",
			path = "/swagger/examples/calculator/estimation-200-unlocked-results.json"
	)
	@ApiJsonExample(
			status = HttpStatus.NOT_FOUND,
			description = "No se logra encontrar un cálculo con el ID proporcionado",
			path = "/swagger/examples/calculator/estimation-200-unlocked-results.json"
	)
	@ApiJsonExample(
			status = HttpStatus.BAD_REQUEST,
			description = "El ID no contiene un formato válido UUIDv4",
			path = "/swagger/examples/calculator/estimation-400-invalid-id.json"
	)
	@ApiJsonExample(
			status = HttpStatus.UNAUTHORIZED,
			description = "Lead/usuario no autenticado",
			path = "/swagger/examples/auth/authentication-401.json"
	)
	public ResponseEntity<CalculatorEstimateResponse> getEstimate(@PathVariable String id,
	                                                              @AuthenticationPrincipal String leadEmail) {
		return ResponseEntity.ok(getEstimateByIdUseCase.execute(leadEmail, id));
	}
}
