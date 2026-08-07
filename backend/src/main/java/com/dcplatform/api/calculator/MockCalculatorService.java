package com.dcplatform.api.calculator;

import com.dcplatform.api.shared.annotations.MockIntegration;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@MockIntegration
@Service
public class MockCalculatorService implements CalculatorService {

	@Override
	public CalculatorEstimateResponse calculate(CalculatorEstimateRequest request) {
		double contracted = request.contractedCapacityMw();
		double utilized = request.utilizedCapacityMw();
		double energyCost = request.energyCostPerKwh();
		String country = request.country();
		double pue = request.pue();

		// calcular cpacidad ociosa
		double idleRatio = 0.0;
		double idleMw = 0.0;

		if (contracted > 0 && contracted > utilized) {
			idleRatio = 1.0 - (utilized / contracted);
			idleMw = contracted - utilized;
		}

		// calcular costo anual
		double annualHours = 8760.0;
		double idleAnnualCost = idleMw * 1000.0 * annualHours * energyCost;

		// lógica de diversidad: benchmarks dinámicos
		double ratioBenchmark = 0.25; // default
		List<String> latamCountries = List.of("CO", "AR", "BR", "MX", "CL");

		if (latamCountries.contains(country)) {
			ratioBenchmark = 0.28;
		} else if ("US".equals(country) || "CA".equals(country)) {
			ratioBenchmark = 0.15;
		}

		// penalización por PUE ineficiente
		if (pue > 1.8) {
			ratioBenchmark += 0.05;
		}

		// redondeo de valores
		double finalIdleRatio = Math.round(idleRatio * 1000.0) / 1000.0;       // 3 decimales
		double finalIdleCost = Math.round(idleAnnualCost * 10.0) / 10.0;       // 1 decimal
		double finalBenchmark = Math.round(ratioBenchmark * 100.0) / 100.0;    // 2 decimales

		// generar fecha en formato ISO 8601
		String createdAt = Instant.now().atOffset(ZoneOffset.UTC)
				.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));

		// construir los KPIs
		CalculatorEstimateResponse.KpiResult ratioKpi = new CalculatorEstimateResponse.KpiResult(
				"IDLE_CAPACITY_RATIO",
				"Capacidad ociosa pagada",
				finalIdleRatio,
				"RATIO",
				finalBenchmark
		);

		CalculatorEstimateResponse.KpiResult costKpi = new CalculatorEstimateResponse.KpiResult(
				"IDLE_CAPACITY_ANNUAL_COST",
				"Costo anual de capacidad ociosa",
				finalIdleCost,
				"CURRENCY",
				null // null porque este no lleva benchmark en tu ejemplo
		);

		// retornar la respuesta final
		return new CalculatorEstimateResponse(
				UUID.randomUUID().toString(),
				"1.0.0",
				createdAt,
				false,
				List.of(ratioKpi, costKpi),
				4 // cantidad de KPIs ocultos
		);
	}
}
