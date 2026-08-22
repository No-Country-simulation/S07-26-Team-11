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
import org.springframework.http.MediaType;
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

	@PostMapping(value = "/estimate", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(
			summary = "Calcula una estimación",
			description = "Devuelve los KPI públicos y mantiene bloqueados los resultados de costo mensual y proyección a 3 anos."
	)
	@ApiJsonExample(
			summary = "Estimación con KPIs públicos",
			description = "Estimación calculada con KPIs como teasers",
			path = "/static/swagger/examples/calculator/estimation-200.json"
	)
	@ApiJsonExample(
			status = "400",
			summary = "Valores de entrada inválidos",
			description = "Datos de entrada inválidos",
			path = "/static/swagger/examples/calculator/estimation-400-invalid-values.json"
	)
	@ApiJsonExample(
			status = "400",
			summary = "Capacidad utilizada superior a la instalada",
			description = "La capacidad utilizada es mayor que la capacidad instalada",
			path = "/static/swagger/examples/calculator/estimation-400-capacity-exceeded.json"
	)
	@ApiJsonExample(
			status = "400",
			summary = "Campos obligatorios faltantes",
			description = "Campos o datos faltantes",
			path = "/static/swagger/examples/calculator/estimation-400-missing-fields.json"
	)
	public ResponseEntity<CalculatorEstimateResponse> estimate(@Valid @RequestBody CalculatorEstimateRequest request) {
		CalculatorEstimateResponse response = calculatorService.calculate(request, false);
		return ResponseEntity.ok(response);
	}

	@PostMapping(value = "/estimate/unlock-results", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(
			summary = "Desbloquea los resultados de costo",
			description = "Calcula y guarda una estimación completa para el lead autenticado."
	)
	@ApiJsonExample(
			summary = "Estimación con resultados desbloqueados",
			description = "Estimación calculada con todos los KPIs desbloqueados",
			path = "/static/swagger/examples/calculator/estimation-200-unlocked-results.json"
	)
	@ApiJsonExample(
			status = "400",
			summary = "Valores de entrada inválidos",
			description = "Datos de entrada inválidos",
			path = "/static/swagger/examples/calculator/estimation-400-invalid-values.json"
	)
	@ApiJsonExample(
			status = "400",
			summary = "Capacidad utilizada superior a la instalada",
			description = "La capacidad utilizada es mayor que la capacidad instalada",
			path = "/static/swagger/examples/calculator/estimation-400-capacity-exceeded.json"
	)
	@ApiJsonExample(
			status = "400",
			summary = "Campos obligatorios faltantes",
			description = "Campos o datos faltantes",
			path = "/static/swagger/examples/calculator/estimation-400-missing-fields.json"
	)
	@ApiJsonExample(
			status = "401",
			summary = "Autenticación requerida",
			description = "Lead o usuario no autenticado",
			path = "/static/swagger/examples/auth/authentication-401.json"
	)
	public ResponseEntity<CalculatorEstimateResponse> unlockEstimate(
			@Valid @RequestBody CalculatorEstimateRequest request, @AuthenticationPrincipal String leadEmail) {
		CalculatorEstimateResponse response = unlockEstimateUseCase.execute(leadEmail, request);
		return ResponseEntity.ok(response);
	}

	@GetMapping(value = "/defaults", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(
			summary = "Obtiene valores de ejemplo",
			description = "Devuelve valores iniciales que pueden enviarse directamente a POST /estimate.")
	@ApiJsonExample(
			summary = "Valores predeterminados de la calculadora",
			description = "Valores para ser usados como ejemplo o para rellenar campos",
			path = "/static/swagger/examples/calculator/default-200.json"
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
				rackCount);

		return ResponseEntity.ok(defaultsValues);
	}

	@GetMapping(value = "/estimates/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(
			summary = "Buscar una estimación guardada",
			description = "Consulta una estimación guardada mediante el ID. Requiere autenticación."
	)
	@ApiJsonExample(
			summary = "Estimación guardada con resultados desbloqueados",
			description = "Estimación calculada con todos los KPIs desbloqueados",
			path = "/static/swagger/examples/calculator/estimation-200-unlocked-results.json"
	)
	@ApiJsonExample(
			status = "404",
			summary = "Estimación no encontrada",
			description = "No se logra encontrar un cálculo con el ID proporcionado",
			path = "/static/swagger/examples/calculator/estimation-404-not-found.json"
	)
	@ApiJsonExample(
			status = "400",
			summary = "ID con formato inválido",
			description = "El ID no contiene un formato válido UUIDv4",
			path = "/static/swagger/examples/calculator/estimation-400-invalid-id.json"
	)
	@ApiJsonExample(
			status = "401",
			summary = "Autenticación requerida",
			description = "Lead o usuario no autenticado",
			path = "/static/swagger/examples/auth/authentication-401.json"
	)
	public ResponseEntity<CalculatorEstimateResponse> getEstimate(@PathVariable String id,
	                                                              @AuthenticationPrincipal String leadEmail) {
		return ResponseEntity.ok(getEstimateByIdUseCase.execute(leadEmail, id));
	}
}
