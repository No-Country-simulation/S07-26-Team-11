package com.dcplatform.api.calculator.service;

import com.dcplatform.api.calculator.model.KpiCode;
import com.dcplatform.api.calculator.model.dto.CalculatorEstimateRequest;
import com.dcplatform.api.calculator.model.dto.CalculatorEstimateResponse;
import com.dcplatform.api.shared.annotations.MockIntegration;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

@MockIntegration
@Service
public class MockCalculatorService implements CalculatorService {

	private final Logger logger = org.slf4j.LoggerFactory.getLogger(MockCalculatorService.class);

	@Override
	public CalculatorEstimateResponse calculate(CalculatorEstimateRequest request, boolean isUnlocked) {
		logger.info("Using mock calculator service");

		final String CALCULATOR_VERSION = "1.0.0";

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
						KpiCode.IDLE_CAPACITY_KW,
						"Capacidad subutilizada estimada",
						finalIdleKw,
						KpiCode.UnitType.KW
				),
				new CalculatorEstimateResponse.KpiResult(
						KpiCode.IDLE_CAPACITY_RATIO,
						"Porcentaje de subutilización",
						finalIdleRatio,
						KpiCode.UnitType.RATIO
				),
				new CalculatorEstimateResponse.KpiResult(
						KpiCode.IDLE_CAPACITY_ANNUAL_COST,
						"Costo anual desperdiciado",
						finalAnnualCost,
						KpiCode.UnitType.CURRENCY
				),
				new CalculatorEstimateResponse.KpiResult(
						KpiCode.IDLE_COST_MONTHLY,
						"Costo mensual estimado",
						finalCostMonthly,
						KpiCode.UnitType.CURRENCY
				),
				new CalculatorEstimateResponse.KpiResult(
						KpiCode.IDLE_COST_3Y_PROJECTION,
						"Proyección a 3 años",
						finalCost3y,
						KpiCode.UnitType.CURRENCY
				),
				new CalculatorEstimateResponse.KpiResult(
						KpiCode.IDLE_COST_PER_RACK_ANNUAL,
						"Costo por rack / año",
						finalCostPerRack,
						KpiCode.UnitType.CURRENCY
				)
		);

		int lockedKpiCount = 0;

		if (!isUnlocked) {
			listOfKpi = listOfKpi.stream()
					.filter(kpi -> !kpi.code().equals(KpiCode.IDLE_COST_MONTHLY) &&
							!kpi.code().equals(KpiCode.IDLE_COST_3Y_PROJECTION))
					.toList();
			lockedKpiCount = 2;
		}

		return new CalculatorEstimateResponse(
				null,
				CALCULATOR_VERSION,
				createdAt,
				isUnlocked,
				listOfKpi,
				lockedKpiCount
		);
	}
}
