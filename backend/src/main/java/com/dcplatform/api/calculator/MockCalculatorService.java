package com.dcplatform.api.calculator;

import com.dcplatform.api.shared.annotations.MockIntegration;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@MockIntegration
@Service
public class MockCalculatorService implements CalculatorService {

	private final Logger logger = org.slf4j.LoggerFactory.getLogger(MockCalculatorService.class);

	@Override
	public CalculatorEstimateResponse calculate(CalculatorEstimateRequest request) {
		logger.info("Using mock calculator service");

		// 1. Extraer los nuevos campos de la request
		double installedKw = request.installedCapacityKw();
		double usedKw = request.usedCapacityKw();
		double energyCost = request.electricityRatePerKwh();
		int rackCount = request.rackCount();

		// 2. Calcular capacidad ociosa (ahora en kW)
		double idleRatio = 0.0;
		double idleKw = 0.0;

		if (installedKw > 0 && installedKw > usedKw) {
			idleRatio = 1.0 - (usedKw / installedKw);
			idleKw = installedKw - usedKw;
		}

		// 3. Calcular costo anual
		double annualHours = 8760.0;
		double idleAnnualCost = idleKw * annualHours * energyCost;

		// 4. Lógica de diversidad: benchmarks dinámicos basados en infraestructura
		double ratioBenchmark = 0.20; // default para un DC mediano

		if (rackCount < 50) {
			ratioBenchmark = 0.28; // Centros pequeños suelen tener más ociosidad relativa
		} else if (rackCount > 200) {
			ratioBenchmark = 0.15; // Hiperescala u operaciones grandes optimizan mejor
		}

		// 5. Redondeo de valores
		double finalIdleRatio = Math.round(idleRatio * 1000.0) / 1000.0;       // 3 decimales
		double finalIdleCost = Math.round(idleAnnualCost * 10.0) / 10.0;       // 1 decimal
		double finalBenchmark = Math.round(ratioBenchmark * 100.0) / 100.0;    // 2 decimales

		// 6. Generar fecha en formato ISO 8601
		String createdAt = Instant.now().atOffset(ZoneOffset.UTC)
				.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));

		// 7. Construir los KPI alineados al CalculatorEstimateResponse
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
				null // null porque este no lleva benchmark en tu modelo
		);

		// 8. Retornar la respuesta final
		return new CalculatorEstimateResponse(
				UUID.randomUUID().toString(),
				"1.0.0",
				createdAt,
				false,
				List.of(ratioKpi, costKpi),
				4 // cantidad de KPI ocultos
		);
	}
}