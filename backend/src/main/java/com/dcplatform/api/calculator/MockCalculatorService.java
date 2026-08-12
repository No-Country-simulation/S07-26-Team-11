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

		double installedKw = request.installedCapacityKw();
		double usedKw = request.usedCapacityKw();
		double energyCost = request.electricityRatePerKwh();
		int rackCount = request.rackCount();

		double idleRatio = 0.0;
		double idleKw = 0.0;

		if (installedKw > 0 && installedKw > usedKw) {
			idleRatio = 1.0 - (usedKw / installedKw);
			idleKw = installedKw - usedKw;
		}

		double annualHours = 8760.0;
		double idleAnnualCost = idleKw * annualHours * energyCost;

		double idleCostMonthly = idleAnnualCost / 12.0;
		double idleCost3yProjection = idleAnnualCost * 3.0;

		double idleCostPerRackAnnual = rackCount > 0 ? (idleAnnualCost / rackCount) : 0.0;

		double finalIdleKw = Math.round(idleKw * 10.0) / 10.0;
		double finalIdleRatio = Math.round(idleRatio * 1000.0) / 1000.0;
		double finalAnnualCost = Math.round(idleAnnualCost * 10.0) / 10.0;
		double finalCostMonthly = Math.round(idleCostMonthly * 10.0) / 10.0;
		double finalCost3y = Math.round(idleCost3yProjection * 10.0) / 10.0;
		double finalCostPerRack = Math.round(idleCostPerRackAnnual * 10.0) / 10.0;

		String createdAt = Instant.now().atOffset(ZoneOffset.UTC)
				.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));

		List<CalculatorEstimateResponse.KpiResult> listOfKpi = List.of(
				new CalculatorEstimateResponse.KpiResult(
						"IDLE_CAPACITY_KW",
						"Capacidad subutilizada estimada",
						finalIdleKw,
						"KW",
						null
				),
				new CalculatorEstimateResponse.KpiResult(
						"IDLE_CAPACITY_RATIO",
						"Porcentaje de subutilización",
						finalIdleRatio,
						"RATIO",
						null
				),
				new CalculatorEstimateResponse.KpiResult(
						"IDLE_CAPACITY_ANNUAL_COST",
						"Costo anual desperdiciado",
						finalAnnualCost,
						"CURRENCY",
						null
				),
				new CalculatorEstimateResponse.KpiResult(
						"IDLE_COST_MONTHLY",
						"Costo mensual estimado",
						finalCostMonthly,
						"CURRENCY",
						null
				),
				new CalculatorEstimateResponse.KpiResult(
						"IDLE_COST_3Y_PROJECTION",
						"Proyección a 3 años",
						finalCost3y,
						"CURRENCY",
						null
				),
				new CalculatorEstimateResponse.KpiResult(
						"IDLE_COST_PER_RACK_ANNUAL",
						"Costo por rack / año",
						finalCostPerRack,
						"CURRENCY",
						null
				)
		);

		return new CalculatorEstimateResponse(
				UUID.randomUUID().toString(),
				"1.0.0",
				createdAt,
				true,
				listOfKpi,
				0
		);
	}
}